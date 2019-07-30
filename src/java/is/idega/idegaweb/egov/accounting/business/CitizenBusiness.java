package is.idega.idegaweb.egov.accounting.business;


import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOService;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Commune;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;

import is.idega.block.family.business.FamilyLogic;

public interface CitizenBusiness extends IBOService, UserBusiness {

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getDefaultCommune
	 */
	public Commune getDefaultCommune() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#isCitizenOfDefaultCommune
	 */
	public boolean isCitizenOfDefaultCommune(User user) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#isCitizenOfCommune
	 */
	public boolean isCitizenOfCommune(User user, Commune commune) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#hasCitizenAccount
	 */
	public boolean hasCitizenAccount(User user) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#hasGuestAccount
	 */
	public boolean hasGuestAccount(User user) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getCustodianForChild
	 */
	public User getCustodianForChild(User child) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getParentsForChild
	 */
	public Collection getParentsForChild(User child) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getChildHomePhone
	 */
	public Phone getChildHomePhone(User child) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#haveSameAddress
	 */
	public boolean haveSameAddress(User user, User compareUser) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootCitizenGroup
	 */
	public Group getRootCitizenGroup() throws CreateException, FinderException, RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootAcceptedCitizenGroup
	 */
	public Group getRootAcceptedCitizenGroup() throws CreateException, RemoteException, FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootOtherCommuneCitizensGroups
	 */
	public Collection getRootOtherCommuneCitizensGroups() throws CreateException, FinderException, RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#findUsersByConditions
	 */
	public Collection<User> findUsersByConditions(String firstName, String middleName, String lastName, String pid) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getMemberFamilyLogic
	 */
	public FamilyLogic getMemberFamilyLogic() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getUsersCommuneURL
	 */
	public String getUsersCommuneURL(User user) throws RemoteException;

	public String getHomePageForCitizen(IWContext iwc, String personalID, String fullName, String appProperty, String cookie, String loginType);

}