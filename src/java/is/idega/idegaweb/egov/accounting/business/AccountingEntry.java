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

public interface AccountingEntry {

	/**
	 * Gets the personalId value for this BillingEntry.
	 * 
	 * @return personalId
	 */
	public java.lang.String getPersonalId();

	/**
	 * Sets the personalId value for this BillingEntry.
	 * 
	 * @param personalId
	 */
	public void setPersonalId(java.lang.String personalId);

	/**
	 * Gets the payerPersonalId value for this BillingEntry.
	 * 
	 * @return payerPersonalId
	 */
	public java.lang.String getPayerPersonalId();

	/**
	 * Sets the payerPersonalId value for this BillingEntry.
	 * 
	 * @param payerPersonalId
	 */
	public void setPayerPersonalId(java.lang.String payerPersonalId);

	/**
	 * Gets the providerCode value for this BillingEntry.
	 * 
	 * @return providerCode
	 */
	public java.lang.String getProviderCode();

	/**
	 * Sets the providerCode value for this BillingEntry.
	 * 
	 * @param providerCode
	 */
	public void setProviderCode(java.lang.String providerCode);

	/**
	 * Gets the productCode value for this BillingEntry.
	 * 
	 * @return productCode
	 */
	public java.lang.String getProductCode();

	/**
	 * Sets the productCode value for this BillingEntry.
	 * 
	 * @param productCode
	 */
	public void setProductCode(java.lang.String productCode);

	/**
	 * Gets the projectCode value for this BillingEntry.
	 * 
	 * @return projectCode
	 */
	public java.lang.String getProjectCode();

	/**
	 * Sets the projectCode value for this BillingEntry.
	 * 
	 * @param projectCode
	 */
	public void setProjectCode(java.lang.String projectCode);

	/**
	 * Gets the amount value for this BillingEntry.
	 * 
	 * @return amount
	 */
	public int getAmount();

	/**
	 * Sets the amount value for this BillingEntry.
	 * 
	 * @param amount
	 */
	public void setAmount(int amount);

	/**
	 * Gets the startDate value for this BillingEntry.
	 * 
	 * @return startDate
	 */
	public java.util.Date getStartDate();

	/**
	 * Sets the startDate value for this BillingEntry.
	 * 
	 * @param startDate
	 */
	public void setStartDate(java.util.Date startDate);

	/**
	 * Gets the endDate value for this BillingEntry.
	 * 
	 * @return endDate
	 */
	public java.util.Date getEndDate();

	/**
	 * Sets the endDate value for this BillingEntry.
	 * 
	 * @param endDate
	 */
	public void setEndDate(java.util.Date endDate);

	/**
	 * Gets the paymentMethod value for this BillingEntry.
	 * 
	 * @return paymentMethod
	 */
	public java.lang.String getPaymentMethod();

	/**
	 * Sets the paymentMethod value for this BillingEntry.
	 * 
	 * @param paymentMethod
	 */
	public void setPaymentMethod(java.lang.String paymentMethod);

	/**
	 * Gets the unitPrice value for this BillingEntry.
	 * 
	 * @return unitPrice
	 */
	public float getUnitPrice();

	/**
	 * Sets the unitPrice value for this BillingEntry.
	 * 
	 * @param unitPrice
	 */
	public void setUnitPrice(float unitPrice);

	/**
	 * Gets the units value for this BillingEntry.
	 * 
	 * @return units
	 */
	public float getUnits();

	/**
	 * Sets the units value for this BillingEntry.
	 * 
	 * @param units
	 */
	public void setUnits(float units);

	/**
	 * Gets the cardNumber value for this BillingEntry.
	 * 
	 * @return cardNumber
	 */
	public java.lang.String getCardNumber();

	/**
	 * Sets the cardNumber value for this BillingEntry.
	 * 
	 * @param cardNumber
	 */
	public void setCardNumber(java.lang.String cardNumber);

	/**
	 * Gets the cardType value for this BillingEntry.
	 * 
	 * @return cardType
	 */
	public java.lang.String getCardType();

	/**
	 * Sets the cardType value for this BillingEntry.
	 * 
	 * @param cardType
	 */
	public void setCardType(java.lang.String cardType);

	/**
	 * Gets the cardExpirationMonth value for this BillingEntry.
	 * 
	 * @return cardExpirationMonth
	 */
	public int getCardExpirationMonth();

	/**
	 * Sets the cardExpirationMonth value for this BillingEntry.
	 * 
	 * @param cardExpirationMonth
	 */
	public void setCardExpirationMonth(int cardExpirationMonth);

	/**
	 * Gets the cardExpirationYear value for this BillingEntry.
	 * 
	 * @return cardExpirationYear
	 */
	public int getCardExpirationYear();

	/**
	 * Sets the cardExpirationYear value for this BillingEntry.
	 * 
	 * @param cardExpirationYear
	 */
	public void setCardExpirationYear(int cardExpirationYear);

}