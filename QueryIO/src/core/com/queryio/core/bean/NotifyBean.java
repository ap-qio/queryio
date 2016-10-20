/*
 * @(#)  NotifyBean.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.core.bean;

import java.util.List;

/**
 * This is the bean class that will be used in the Notify Tab.
 * 
 * @author Exceed Consultancy Services
 */
public class NotifyBean
{
		
	private transient List attachments = null;
	
	// Email Notification
	private boolean emailEnabled = false;
	private String emailSenderName;
	private String emailSenderAddress ;
	private boolean securedProtocol = false;
	private String emailSMTPServer;
	private String emailSMTPPort ;
	private boolean authRequired = false;
	private String emailUsername ;
	private String emailPassword ;
	// SMS Notification
	private boolean smsEnabled = false;
	private String smsNumber;
	private String smsSerialPort;
	private String smsManufacturer;
	private String smsModel;
	private String smsSelectedModel;
	private String smsBaudRate;
//	// Yahoo Notification
//	private boolean ymEnabled = false;
//	private String ymID;
//	private String ymPassword;
//	// MSN Notification
//	private boolean msnEnabled = false;
//	private String msnID;
//	private String msnPassword;
//	// AOL Notification
//	private boolean aolEnabled = false;
//	private String aolID;
//	private String aolPassword;
//	// Custom Notification
//	private boolean customEnabled = false;
//	private String customProgram;
	// Log Notification
	private boolean logEnabled = false;
	private String logFilePath;
//	// Database notification
//	private boolean databaseEnabled = false;
//	private String databaseDriver = null;
//	private String databaseUser = null;
//	private String databasePassword = null;
//	private String databaseJar = null;
//	private String databaseURL = null;
//	private String databaseTable = null;
//	private String databaseColumn = null;
//	
//	// SNMP Trap notification
//	private boolean trapEnabled = false;
//	private String trapHostName = null;
//	private String trapPort = null;
//	private String trapVersion = null;
//	private String trapCommunity = null;
//	private String trapAuthType = null;
//	private String trapUserName = null;
//	private String trapPassword = null;
//	private String trapPrivacyType = null;
//	private String trapPrivacyPassword = null;
//	
//	private boolean espEnabled = false;
//	private String espEndPoint = null;
//	private String espAppID = null;
//	private String espProcessName = null;
//	private String espEnvironment = null;
//	//private boolean espEnabledOnFailure = false;
	
	private int validateClicked = -1;

	/**
	 * isEmailEnabled
	 * 
	 * @return
	 */
	public boolean isEmailEnabled()
	{
		return this.emailEnabled;
	}

	/**
	 * getEmailSenderName
	 * 
	 * @return
	 */
	public String getEmailSenderName()
	{
		return this.emailSenderName;
	}

	/**
	 * getEmailSenderAddress
	 * 
	 * @return
	 */
	public String getEmailSenderAddress()
	{
		return this.emailSenderAddress;
	}

	/**
	 * getEmailSMTPServer
	 * 
	 * @return
	 */
	public String getEmailSMTPServer()
	{
		return this.emailSMTPServer;
	}

	/**
	 * getEmailSMTPPort
	 * 
	 * @return
	 */
	public String getEmailSMTPPort()
	{
		return this.emailSMTPPort;
	}

	/**
	 * Method isAuthRequired
	 * 
	 * @return boolean
	 */
	public boolean isAuthRequired()
	{
		return this.authRequired;
	}

	/**
	 * getEmailUsername
	 * 
	 * @return
	 */
	public String getEmailUsername()
	{
		return this.emailUsername;
	}

	/**
	 * getEmailPassword
	 * 
	 * @return
	 */
	public String getEmailPassword()
	{
		return this.emailPassword;
	}

	/**
	 * isSmsEnabled
	 * 
	 * @return
	 */
	public boolean isSmsEnabled()
	{
		return this.smsEnabled;
	}

	/**
	 * getSmsNumber
	 * 
	 * @return
	 */
	public String getSmsNumber()
	{
		return this.smsNumber;
	}

	/**
	 * getSmsSerialPort
	 * 
	 * @return
	 */
	public String getSmsSerialPort()
	{
		return this.smsSerialPort;
	}

	/**
	 * setEmailEnabled
	 * 
	 * @param b
	 */
	public void setEmailEnabled(final boolean b)
	{
		this.emailEnabled = b;
	}

	/**
	 * setEmailSenderName
	 * 
	 * @param string
	 */
	public void setEmailSenderName(final String string)
	{
		this.emailSenderName = string;
	}

	/**
	 * setEmailSenderAddress
	 * 
	 * @param string
	 */
	public void setEmailSenderAddress(final String string)
	{
		this.emailSenderAddress = string;
	}

	/**
	 * setEmailSMTPServer
	 * 
	 * @param string
	 */
	public void setEmailSMTPServer(final String string)
	{
		this.emailSMTPServer = string;
	}

	/**
	 * setEmailSMTPPort
	 * 
	 * @param string
	 */
	public void setEmailSMTPPort(final String string)
	{
		this.emailSMTPPort = string;
	}

	/**
	 * Method setAuthRequired
	 * 
	 * @param b
	 *            void
	 */
	public void setAuthRequired(final boolean b)
	{
		this.authRequired = b;
	}

	/**
	 * setEmailUsername
	 * 
	 * @param string
	 */
	public void setEmailUsername(final String string)
	{
		this.emailUsername = string;
	}

	/**
	 * setEmailPassword
	 * 
	 * @param string
	 */
	public void setEmailPassword(final String string)
	{
		this.emailPassword = string;
	}

	/**
	 * setSmsEnabled
	 * 
	 * @param b
	 */
	public void setSmsEnabled(final boolean b)
	{
		this.smsEnabled = b;
	}

	/**
	 * setSmsNumber
	 * 
	 * @param string
	 */
	public void setSmsNumber(final String string)
	{
		this.smsNumber = string;
	}

	/**
	 * setSmsSerialPort
	 * 
	 * @param string
	 */
	public void setSmsSerialPort(final String string)
	{
		this.smsSerialPort = string;
	}

	public int getValidateClicked() 
	{
		return validateClicked;
	}

	public void setValidateClicked(int validateClicked) 
	{
		this.validateClicked = validateClicked;
	}
	
	public boolean isSecuredProtocol() 
	{
		return securedProtocol;
	}

	public void setSecuredProtocol(boolean securedProtocol) 
	{
		this.securedProtocol = securedProtocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("NotifyBean: ");
		sbToString.append(" EmailEnabled=");
		sbToString.append(this.emailEnabled);
		sbToString.append(" EmailSenderName=");
		sbToString.append(this.emailSenderName);
		sbToString.append(" EmailSenderAddress=");
		sbToString.append(this.emailSenderAddress);
		sbToString.append(" EmailSMTPServer=");
		sbToString.append(this.emailSMTPServer);
		sbToString.append(" EmailSMTPPort=");
		sbToString.append(this.emailSMTPPort);
		sbToString.append(" EmailUsername=");
		sbToString.append(this.emailUsername);
		sbToString.append(" EmailPassword=");
		sbToString.append(this.emailPassword);
		sbToString.append(" SMSEnabled=");
		sbToString.append(this.smsEnabled);
		sbToString.append(" SMSNumber=");
		sbToString.append(this.smsNumber);
		sbToString.append(" SMSSerialPort=");
		sbToString.append(this.smsSerialPort);
		sbToString.append(" LogEnabled=");
		sbToString.append(this.logEnabled);
		sbToString.append(" LogFilePath=");
		sbToString.append(this.logFilePath);

		return sbToString.toString();
	}

	public boolean isLogEnabled() 
	{
		return logEnabled;
	}

	public void setLogEnabled(boolean logEnabled) 
	{
		this.logEnabled = logEnabled;
	}

	public String getLogFilePath() 
	{
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) 
	{
		this.logFilePath = logFilePath;
	}

	public String getSmsManufacturer() 
	{
		return smsManufacturer;
	}

	public void setSmsManufacturer(String smsManufacturer) 
	{
		this.smsManufacturer = smsManufacturer;
	}

	public String getSmsModel() 
	{
		return smsModel;
	}

	public void setSmsModel(String smsModel) 
	{
		this.smsModel = smsModel;
	}

	public String getSmsBaudRate() 
	{
		return smsBaudRate;
	}

	public void setSmsBaudRate(String smsBaudRate) 
	{
		this.smsBaudRate = smsBaudRate;
	}

	public String getSmsSelectedModel() {
		return smsSelectedModel;
	}

	public void setSmsSelectedModel(String smsSelectedModel) {
		this.smsSelectedModel = smsSelectedModel;
	}

	public List getAttachments()
	{
		return attachments;
	}

	public void setAttachments(List attachments)
	{
		this.attachments = attachments;
	}
}
