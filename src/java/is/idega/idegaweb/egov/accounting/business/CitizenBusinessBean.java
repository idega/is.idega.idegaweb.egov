/*
 * $Id$ Created on Nov 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package is.idega.idegaweb.egov.accounting.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.idega.block.login.LoginConstants;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.accesscontrol.event.LoggedInUserCredentials;
import com.idega.core.accesscontrol.event.LoggedInUserCredentials.LoginType;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.contact.data.Phone;
import com.idega.core.idgenerator.business.UUIDBusiness;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.core.location.business.CommuneBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserBusinessBean;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.LocaleUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Name;
import com.idega.util.text.TextSoap;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoCustodianFound;
import is.idega.idegaweb.egov.EGOVConstants;

public class CitizenBusinessBean extends UserBusinessBean implements CitizenBusiness, UserBusiness {

	private final String ROOT_CITIZEN_GROUP_ID_PARAMETER_NAME = "commune_id";
	private final String ROOT_ACCEPTED_CITIZEN_GROUP_ID_PARAMETER_NAME = "accepted_citizen_group_id";
	private final String ROOT_OTHER_COMMUNE_CITIZEN_GROUP_ID_PARAMETER_NAME = "special_citizen_group_id";

	private Group rootCitizenGroup;
	private Group rootAcceptedCitizenGroup;
	private Collection rootOtherCommuneCitizenGroup;

	private CommuneBusiness getCommuneBusiness() throws IBOLookupException {
		return getServiceInstance(CommuneBusiness.class);
	}

	@Override
	public Commune getDefaultCommune() {
		try {
			return getCommuneBusiness().getDefaultCommune();
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	@Override
	public boolean isCitizenOfDefaultCommune(User user) {
		return isCitizenOfCommune(user, getDefaultCommune());
	}

	@Override
	public boolean isCitizenOfCommune(User user, Commune commune) {
		try {
			Address address = getUsersMainAddress(user);
			Commune userCommune = address != null ? address.getCommune() : null;
			if (userCommune != null) {
				return userCommune.equals(commune);
			}
			else if (address != null) {
				PostalCode code = address.getPostalCode();
				if (code != null) {
					Commune postalCommune = code.getCommune();
					if (postalCommune != null) {
						return postalCommune.equals(commune);
					}
				}
			}

			return false;
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	@Override
	public boolean hasCitizenAccount(User user) throws RemoteException {
		return hasUserLogin(user);
	}

	@Override
	public boolean hasGuestAccount(User user) {
		if (user == null) {
			log(Level.WARNING, "User is not provided!");
			return false;
		}

		try {
			return getRootOtherCommuneCitizensGroups().contains(user.getPrimaryGroup());
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public User getCustodianForChild(User child) throws RemoteException {
		User performer = null;
		Collection parents = getParentsForChild(child);
		if (parents != null) {
			Iterator iter = parents.iterator();
			while (iter.hasNext()) {
				User parent = (User) iter.next();
				if (hasCitizenAccount(parent)) {
					performer = parent;
					break;
				}
				if (!iter.hasNext()) {
					performer = parent;
				}
			}
		}

		return performer;
	}

	@Override
	public Collection getParentsForChild(User child) throws RemoteException {
		try {
			return getMemberFamilyLogic().getCustodiansFor(child);
		}
		catch (NoCustodianFound ncf) {
			return null;
		}
	}

	@Override
	public Phone getChildHomePhone(User child) throws RemoteException {
		Address childAddress = getUsersMainAddress(child);
		Collection parents = getParentsForChild(child);
		if (parents != null) {
			Address parentAddress;
			Iterator iter = parents.iterator();
			while (iter.hasNext()) {
				User parent = (User) iter.next();
				parentAddress = getUsersMainAddress(parent);
				if (childAddress != null && parentAddress != null) {
					if (getIfUserAddressesMatch(childAddress, parentAddress)) {
						try {
							return this.getUsersHomePhone(parent);
						}
						catch (NoPhoneFoundException npfe) {
							// empty
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean haveSameAddress(User user, User compareUser) throws RemoteException {
		if (user == null || compareUser == null) {
			return false;
		}
		if (((Integer) user.getPrimaryKey()).intValue() == ((Integer) compareUser.getPrimaryKey()).intValue()) {
			return true;
		}

		Address userAddress = getUsersMainAddress(user);
		Address otherUserAddress = getUsersMainAddress(compareUser);
		if (userAddress != null && otherUserAddress != null) {
			return getIfUserAddressesMatch(userAddress, otherUserAddress);
		}

		return false;
	}

	private boolean getIfUserAddressesMatch(Address userAddress, Address userAddressToCompare) {
		if (((Integer) userAddress.getPrimaryKey()).intValue() == ((Integer) userAddressToCompare.getPrimaryKey()).intValue()) {
			return true;
		}

		String address1 = userAddress.getStreetAddress().toUpperCase();
		String address2 = userAddressToCompare.getStreetAddress().toUpperCase();

		if (TextSoap.findAndCut(address1, " ").equalsIgnoreCase(TextSoap.findAndCut(address2, " "))) {
			return true;
		}

		return false;
	}

	/**
	 * Creates (if not available) and returns the default usergroup all citizens,
	 * read from imports, are members of. throws a CreateException if it failed to
	 * locate or create the group.
	 */
	@Override
	public Group getRootCitizenGroup() throws CreateException, FinderException, RemoteException {
		if (this.rootCitizenGroup == null) {
			Commune commune = getCommuneBusiness().getDefaultCommune();
			if (commune != null) {
				this.rootCitizenGroup = getGroupCreateIfNecessaryStoreInCommune(commune, this.ROOT_CITIZEN_GROUP_ID_PARAMETER_NAME, "Commune Citizens", "The Root Group for all Citizens of the Commune");
			}
		}
		return this.rootCitizenGroup;
	}

	/**
	 * Creates (if not available) and returns the group for citizens that have a
	 * citizen account throws a CreateException if it failed to locate or create
	 * the group.
	 *
	 * @throws FinderException
	 */
	@Override
	public Group getRootAcceptedCitizenGroup() throws CreateException, RemoteException, FinderException {
		if (this.rootAcceptedCitizenGroup == null) {
			Group parent = getRootCitizenGroup();

			this.rootAcceptedCitizenGroup = getGroupCreateIfNecessaryStoreAsApplicationBinding(this.ROOT_ACCEPTED_CITIZEN_GROUP_ID_PARAMETER_NAME, EGOVConstants.COMMUNE_ACCPETED_CITIZENS, "The group for all citizens with an account in the commune", parent);
		}
		return this.rootAcceptedCitizenGroup;
	}

	/**
	 * Creates (if not available) and returns the default usergroup for all
	 * citizens not living in the commune, read from imports. throws a
	 * CreateException if it failed to locate or create the group.
	 */
	@Override
	public Collection getRootOtherCommuneCitizensGroups() throws CreateException, FinderException, RemoteException {
		if (this.rootOtherCommuneCitizenGroup == null) {
			this.rootOtherCommuneCitizenGroup = getGroupsCreateIfNecessaryStoreAsApplicationBinding(this.ROOT_OTHER_COMMUNE_CITIZEN_GROUP_ID_PARAMETER_NAME, "Non-Commune Citizens", "The Root Group for all Citizens in other Communes", null);
		}
		return this.rootOtherCommuneCitizenGroup;
	}

	private Group getGroupCreateIfNecessaryStoreInCommune(Commune commune, String parameter, String createName, String createDescription) throws RemoteException, CreateException, FinderException {
		Group group = commune.getGroup();
		if (group == null) {
			group = getGroupCreateIfNecessaryStoreAsApplicationBinding(parameter, createName, createDescription, null);
			commune.setGroup(group);
			commune.store();
		}
		return group;
	}

	private Group getGroupCreateIfNecessaryStoreAsApplicationBinding(String parameter, String createName, String createDescription, Group parentGroup) throws RemoteException, FinderException, CreateException {
		IWMainApplicationSettings settings = getIWMainApplication().getSettings();
		String groupId = settings.getProperty(parameter);

		Group group = null;
		if (groupId != null) {
			group = getGroupHome().findByPrimaryKey(new Integer(groupId));
		}
		else {
			System.err.println("Trying to store " + createName + " group");
			if (parentGroup == null) {
				group = getGroupBusiness().createGroup(createName, createDescription);
			}
			else {
				group = getGroupBusiness().createGroupUnder(createName, createDescription, parentGroup);
			}

			groupId = group.getPrimaryKey().toString();
			settings.setProperty(parameter, groupId);

		}
		return group;
	}

	private Collection getGroupsCreateIfNecessaryStoreAsApplicationBinding(String parameter, String createName, String createDescription, Group parentGroup) throws RemoteException, FinderException, CreateException {
		IWMainApplicationSettings settings = getIWMainApplication().getSettings();
		String groupIds = settings.getProperty(parameter);

		Collection collection = new ArrayList();

		Group group = null;
		if (groupIds != null) {
			StringTokenizer tokens = new StringTokenizer(groupIds, ",");
			while (tokens.hasMoreTokens()) {
				group = getGroupHome().findByPrimaryKey(new Integer(tokens.nextToken()));
				collection.add(group);
			}
		}
		else {
			System.err.println("Trying to store " + createName + " group");
			if (parentGroup == null) {
				group = getGroupBusiness().createGroup(createName, createDescription);
			}
			else {
				group = getGroupBusiness().createGroupUnder(createName, createDescription, parentGroup);
			}

			settings.setProperty(parameter, group.getPrimaryKey().toString());
			collection.add(group);
		}

		return collection;
	}

	@Override
	public Collection findUsersByConditions(String firstName, String middleName, String lastName, String pid) {
		UserHome home;
		try {
			home = (UserHome) IDOLookup.getHome(User.class);
			return home.findUsersByConditions(firstName, middleName, lastName, pid, null, null, -1, -1, -1, -1, null, null, true, false);
		}
		catch (IDOLookupException e1) {
			e1.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public FamilyLogic getMemberFamilyLogic() {
		try {
			return this.getServiceInstance(FamilyLogic.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	@Override
	public String getUsersCommuneURL(User user) {
		Collection addresses = user.getAddresses();
		Iterator iter = addresses.iterator();
		while (iter.hasNext()) {
			Address address = (Address) iter.next();
			Commune commune = address.getCommune();
			if (commune != null) {
				String URL = commune.getCommuneWebsiteURL();
				if (URL != null) {
					return URL;
				}
			}
		}

		return null;
	}

	private User getUser(String personalId, String fullName) {
		UserBusiness userBusiness = null;
		try {
			userBusiness = getServiceInstance(UserBusiness.class);
		} catch (Exception e) {}

		User user = null;
		try {
			user = userBusiness.getUser(personalId);
		} catch (Exception e) {}

		try {
			if (user == null) {
				Name name = new Name(fullName).capitalize();
				user = userBusiness.createUser(name.getFirstName(), name.getMiddleName(), name.getLastName(), personalId);
				CoreUtil.clearAllCaches();
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error creating user. Personal ID: " + personalId + ", full name: " + fullName, e);
		}

		return user;
	}

	@Override
	public String getHomePageForCitizen(IWContext iwc, String personalID, String fullName, String appProperty, String cookie) {
		if (StringUtil.isEmpty(personalID)) {
			return null;
		}

		if (!StringUtil.isEmpty(fullName)) {
			User user = getUser(personalID, fullName);
			if (user == null) {
				getLogger().warning("Failed to create user with personal ID: " + personalID + ", name: " + fullName);
				return null;
			}
		}

		LoginBusinessBean loginBusiness = LoginBusinessBean.getLoginBusinessBean(iwc.getRequest());
		boolean isLoggedOn = loginBusiness.isLoggedOn(iwc.getRequest());
		try {
			if (isLoggedOn) {
				loginBusiness.logOutUser(iwc);
			}

			IWMainApplication iwMainApplication = iwc.getIWMainApplication();
			IWApplicationContext iwac = iwMainApplication.getIWApplicationContext();
			UserBusiness userBusiness = IBOLookup.getServiceInstance(iwac, UserBusiness.class);

			// check if user has login, otherwise create a login and put in default group
			if (!loginBusiness.hasUserLogin(iwc.getRequest(), personalID)) {
				User user = userBusiness.getUser(personalID);
				LoginTable loginTable = userBusiness.generateUserLogin(user);
				LoginInfo loginInfo = LoginDBHandler.getLoginInfo(loginTable);
				if (loginInfo != null) {
					loginInfo.setChangeNextTime(Boolean.FALSE);
					loginInfo.store();
				}

				Group acceptedCitizens;
				try {
					acceptedCitizens = getRootAcceptedCitizenGroup();
					acceptedCitizens.addGroup(user,	IWTimestamp.getTimestampRightNow());
					if (user.getPrimaryGroup() == null) {
						user.setPrimaryGroup(acceptedCitizens);
						user.store();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (loginBusiness.logInByPersonalID(iwc, personalID)) {
				HttpSession session = iwc.getSession();
				session.setAttribute(LoginConstants.LOGIN_TYPE, LoginConstants.LoginType.ISLAND_DOT_IS.toString());
				session.setAttribute(LoggedInUserCredentials.LOGIN_TYPE, LoginType.AUTHENTICATION_GATEWAY.toString());

				String homePageForOAuth = getCustomHomePage(iwc, loginBusiness, session, appProperty, cookie);
				if (!StringUtil.isEmpty(homePageForOAuth)) {
					return homePageForOAuth;
				}

				User user = loginBusiness.getCurrentUserLegacy(session);

				int redirectPageId = userBusiness.getHomePageIDForUser(user);

				if (redirectPageId > 0) {
					URIUtil util = new URIUtil(getBuilderService(iwac).getPageURI(redirectPageId));

					Locale locale = userBusiness.getUsersPreferredLocale(user);
					if (locale == null) {
						locale = iwac.getIWMainApplication().getDefaultLocale();
					}
					if ("is".equals(locale.toString())) {
						locale = LocaleUtil.getIcelandicLocale();
					}
					util.setParameter(LocaleSwitcher.languageParameterString, locale.toString());

					String responseUri = util.getUri();
					return responseUri;
				} else {
					getLogger().warning(user + " (personal ID: " + personalID + ") does not have home page!");
					return null;
				}
			} else {
				getLogger().info("Failed to login via authorization gateway. Personal ID: " + personalID);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting home page for citizen with personal ID: " + personalID, e);
		}
		return null;
	}

	private String getCustomHomePage(IWContext iwc, LoginBusinessBean loginBusiness, HttpSession session, String appProperty, String cookie) {
		String homePage = StringUtil.isEmpty(appProperty) ? null : iwc.getApplicationSettings().getProperty(appProperty);
		if (StringUtil.isEmpty(homePage)) {
			return null;
		}

		String uuid = null;
		String username = null;
		try {
			LoggedOnInfo loggedOnInfo = loginBusiness.getLoggedOnInfo(session);
			UserLogin userLogin = loggedOnInfo.getUserLogin();

			com.idega.user.data.bean.User user = userLogin.getUser();
			if (user != null) {
				uuid = user.getUniqueId();
				if (StringUtil.isEmpty(uuid)) {
					UUIDBusiness uuidBean;
					try {
						uuidBean = IBOLookup.getServiceInstance(iwc, UUIDBusiness.class);
						uuidBean.addUniqueKeyIfNeeded(user, null);
						uuid = user.getUniqueId();
					} catch (Exception e) {
						getLogger().log(Level.WARNING, "Error generationg UUID for " + user + " (ID: " + user.getId() + ")", e);
					}
				}
			}

			username = userLogin.getUserLogin();
			ELUtil.getInstance().publishEvent(
					new LoggedInUserCredentials(
							iwc.getRequest(),
							RequestUtil.getServerURL(iwc.getRequest()),
							username,
							userLogin.getUserPassword(),
							LoginType.AUTHENTICATION_GATEWAY,
							userLogin.getId()
					)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error publishing event about logged in user", e);
			return null;
		}

		if (!StringUtil.isEmpty(homePage)) {
			getLogger().info("Found homepage from app. settings: " + homePage);
		}

		boolean paramSet = false;
		if (!StringUtil.isEmpty(uuid)) {
			homePage = homePage.concat(homePage.indexOf(CoreConstants.QMARK) == -1 ? CoreConstants.QMARK : CoreConstants.AMP).concat("uuid=").concat(uuid);
			paramSet = true;
		}
		Cookie client = StringUtil.isEmpty(cookie) ? null : iwc.getCookie(cookie);
		if (client != null && !StringUtil.isEmpty(client.getValue())) {
			homePage = homePage.concat(paramSet ? CoreConstants.AMP : CoreConstants.QOUTE_MARK).concat("clientId=").concat(client.getValue());
		}

		getLogger().info("Home page after authentication: " + homePage + " for user name: " + username);
		return homePage;
	}

	private BuilderService getBuilderService(IWApplicationContext iwac) throws RemoteException {
		return BuilderServiceFactory.getBuilderService(iwac);
	}

}