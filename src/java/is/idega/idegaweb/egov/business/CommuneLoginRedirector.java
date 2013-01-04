/*
 * $Id: CommuneLoginRedirector.java,v 1.1 2007/04/03 16:47:37 palli Exp $
 * Created on Nov 3, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.business;

import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.AuthenticationBusiness;
import com.idega.core.accesscontrol.business.AuthenticationListener;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.ServletFilterChainInterruptException;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.data.UserHome;

public class CommuneLoginRedirector implements AuthenticationListener, ServletContextListener {

	public static final String PARAMETER_REDIRECT_TO_COMMUNE_WEB = "redirectToCommuneWeb";

	/**
	 *
	 * @return a human readable identifier for the listener
	 */
	@Override
	public String getAuthenticationListenerName() {
		return "CommuneLoginRedirector";
	}

	/**
	 * Called when a user successfully logs on
	 *
	 * @param iwc
	 *            request and response wrapper
	 * @param currentUser
	 * @throws ServletFilterChainInterruptException
	 */
	@Override
	public void onLogon(IWContext iwc, com.idega.user.data.bean.User currentUser) throws ServletFilterChainInterruptException {
		// get address and commune code and forward to correct server...
		boolean tryRedirect = iwc.isParameterSet(PARAMETER_REDIRECT_TO_COMMUNE_WEB);

		if (tryRedirect) {
			com.idega.user.data.User user = null;
			try {
				UserHome userHome = (UserHome) IDOLookup.getHome(com.idega.user.data.User.class);
				user = userHome.findByPrimaryKey(currentUser.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}

			String communeURL;
			try {
				communeURL = getCitizenBusiness(iwc).getUsersCommuneURL(user);
				if (communeURL != null) {
					try {
						String uuid = currentUser.getUniqueId();

						if (uuid != null && communeURL.indexOf("?") == -1) {
							communeURL += "?";
						}

						if (uuid != null) {
							communeURL += "&"
									+ LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID
									+ "=" + uuid;
							communeURL += "&"
									+ LoginBusinessBean.LoginStateParameter
									+ "=" + LoginBusinessBean.LOGIN_EVENT_LOGIN;
						}

						iwc.getResponse().sendRedirect(communeURL);
						throw new ServletFilterChainInterruptException(
								"CommuneLoginRedirector sending the user to his home commune website: "
										+ communeURL);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Called when a user successfully logs off
	 *
	 * @param iwc
	 *            request and response wrapper
	 * @param lastUser
	 */
	@Override
	public void onLogoff(IWContext iwc, com.idega.user.data.bean.User lastUser) {
		// do nothing
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		IWApplicationContext iwac = IWMainApplication
				.getDefaultIWApplicationContext();
		try {
			AuthenticationBusiness biz = (AuthenticationBusiness) IBOLookup
					.getServiceInstance(iwac, AuthenticationBusiness.class);
			biz.addAuthenticationListener(this);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out
				.println("[eGov] LogonRedirector(AuthenticationListener) initialized.");
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public CitizenBusiness getCitizenBusiness(IWContext iwc) {
		CitizenBusiness biz = null;
		try {
			biz = (CitizenBusiness) IBOLookup.getServiceInstance(iwc,
					CitizenBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return biz;
	}
}