/*
 * $Id$
 * Created on Jul 25, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.accounting.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.idega.block.process.data.CaseCode;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Last modified: $Date$ by $Author$
 * 
 * @author <a href="mailto:laddi@idega.com">Laddi</a>
 * @version $Revision$
 */
public class AccountingBusinessManager {
	
	private static AccountingBusinessManager accountingBusinessManager = null;
	
	private Map caseCodeBusinessMap = null;
	
	public static AccountingBusinessManager getInstance() { 
		if (accountingBusinessManager == null) {
			accountingBusinessManager = new AccountingBusinessManager();
		}
		return accountingBusinessManager;
	}
	
	
	public AccountingBusiness getAccountingBusinessOrDefault(CaseCode caseCode, IWApplicationContext iwac) throws IBOLookupException {
		
		Class businessClass =null;
		if(caseCode!=null){
			businessClass=getBusinessClass(caseCode);
		}
		if (businessClass == null) {
			businessClass = AccountingBusiness.class;
		}
		return getBusiness(businessClass, iwac);
	}
	
	
	public AccountingBusiness getAccountingBusiness(CaseCode caseCode, IWApplicationContext iwac) throws IBOLookupException {
		return getAccountingBusiness(caseCode.getCode(), iwac);
	}

	public AccountingBusiness getAccountingBusiness(String caseCode, IWApplicationContext iwac) throws IBOLookupException {
		Class businessClass = getBusinessClass(caseCode);
		if (businessClass == null) {
			throw new  IBOLookupException("[CaseCodeManager]: An entry for case code "+ caseCode +" could not be found");
		}
		return getBusiness(businessClass, iwac);
	}

	public void addCaseBusinessForCode(CaseCode caseCode, Class accountingBusiness) {
		addCaseBusinessForCode(caseCode.getCode(), accountingBusiness);
	}
		
	public void addCaseBusinessForCode(String code, Class accountingBusiness) {
		getCaseCodeBusinessMap().put(code, accountingBusiness);
	}

	private AccountingBusiness getBusiness(Class serviceClass, IWApplicationContext iwac) throws IBOLookupException {
		return (AccountingBusiness) IBOLookup.getServiceInstance(iwac, serviceClass);
	}
	
	private Class getBusinessClass(CaseCode caseCode) {
		return getBusinessClass(caseCode.getCode());
	}
	
	private Class getBusinessClass(String caseCode) {
		return (Class) getCaseCodeBusinessMap().get(caseCode);		
	}
	
	public Collection getCaseCodes() {
		if (this.caseCodeBusinessMap == null) {
			this.caseCodeBusinessMap = new HashMap();
		}
		return this.caseCodeBusinessMap.keySet();
	}
	
	private Map getCaseCodeBusinessMap() {
		if (this.caseCodeBusinessMap == null) {
			this.caseCodeBusinessMap = new HashMap();
		}
		return this.caseCodeBusinessMap;
	}
}
