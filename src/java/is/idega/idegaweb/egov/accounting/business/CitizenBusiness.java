package is.idega.idegaweb.egov.accounting.business;


import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOService;
import com.idega.user.data.Group;
import com.idega.user.data.User;

public interface CitizenBusiness extends IBOService {

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#hasCitizenAccount
	 */
	public boolean hasCitizenAccount(User user) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#hasGuestAccount
	 */
	public boolean hasGuestAccount(User user) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootCitizenGroup
	 */
	public Group getRootCitizenGroup() throws CreateException, FinderException, RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootAcceptedCitizenGroup
	 */
	public Group getRootAcceptedCitizenGroup() throws CreateException, RemoteException, FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.accounting.business.CitizenBusinessBean#getRootOtherCommuneCitizensGroup
	 */
	public Collection getRootOtherCommuneCitizensGroups() throws CreateException, FinderException, RemoteException, RemoteException;
}