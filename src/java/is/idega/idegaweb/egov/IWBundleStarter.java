/*
 * $Id$
 * Created on Jun 19, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov;

import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;

public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
		updateData(starterBundle.getApplication().getIWApplicationContext());
	}

	public void stop(IWBundle starterBundle) {
	}

	private void updateData(IWApplicationContext iwac) {
		try {
			AccessController access = iwac.getIWMainApplication().getAccessController();
			access.addRoleToGroup(EGOVConstants.ROLE_KEY_CITIZEN, getUserBusiness(iwac).getRootAcceptedCitizenGroup(), iwac);
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

	}

	private CitizenBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (CitizenBusiness) IBOLookup.getServiceInstance(iwac, CitizenBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

}
