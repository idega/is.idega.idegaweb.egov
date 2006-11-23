/*
 * $Id$ Created on Nov 14,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.accounting.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookupException;
import com.idega.core.location.business.CommuneBusiness;
import com.idega.core.location.data.Commune;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.user.business.UserBusinessBean;
import com.idega.user.data.Group;
import com.idega.user.data.User;

public class CitizenBusinessBean extends UserBusinessBean implements CitizenBusiness {

	private final String ROOT_CITIZEN_GROUP_ID_PARAMETER_NAME = "commune_id";
	private final String ROOT_ACCEPTED_CITIZEN_GROUP_ID_PARAMETER_NAME = "accepted_citizen_group_id";
	private final String ROOT_OTHER_COMMUNE_CITIZEN_GROUP_ID_PARAMETER_NAME = "special_citizen_group_id";

	private Group rootCitizenGroup;
	private Group rootAcceptedCitizenGroup;
	private Collection rootOtherCommuneCitizenGroup;

	private CommuneBusiness getCommuneBusiness() throws IBOLookupException {
		return (CommuneBusiness) getServiceInstance(CommuneBusiness.class);
	}

	public boolean hasCitizenAccount(User user) throws RemoteException {
		return hasUserLogin(user);
	}
	
	public boolean hasGuestAccount(User user) {
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

	/**
	 * Creates (if not available) and returns the default usergroup all citizens,
	 * read from imports, are members of. throws a CreateException if it failed to
	 * locate or create the group.
	 */
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
	public Group getRootAcceptedCitizenGroup() throws CreateException, RemoteException, FinderException {
		if (this.rootAcceptedCitizenGroup == null) {
			Group parent = getRootCitizenGroup();

			this.rootAcceptedCitizenGroup = getGroupCreateIfNecessaryStoreAsApplicationBinding(this.ROOT_ACCEPTED_CITIZEN_GROUP_ID_PARAMETER_NAME, "Commune Accepted Citizens", "The group for all citizens with an account in the commune", parent);
		}
		return this.rootAcceptedCitizenGroup;
	}

	/**
	 * Creates (if not available) and returns the default usergroup for all
	 * citizens not living in the commune, read from imports. throws a
	 * CreateException if it failed to locate or create the group.
	 */
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
}