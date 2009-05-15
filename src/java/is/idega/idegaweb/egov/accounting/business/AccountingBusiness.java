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

import java.sql.Date;

import com.idega.business.IBOService;

public interface AccountingBusiness extends IBOService {

	public AccountingEntry[] getAccountingEntries(String productCode, String providerCode, Date fromDate, Date toDate);
	
}