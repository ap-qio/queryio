/*
 * @(#)  EmailNotifier.java
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

package com.queryio.core.notifier.notifiers.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.INotifier;

/**
 * This is the EmailNotifier used for Email Notifications.
 *
 * @author Exceed Consultancy Services
 */
public abstract class EmailNotifier implements INotifier
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$
	private int iSmtpPort = 25;
	private boolean secureProtocol = false;
	private String sSmtpAddress = null;
	private String sSendersEmailAddress = null;
	private LinkedList llRecepientsAddresses = null;
	private String sSendersName = null;
	private boolean bAuthRequired = false;
	private String sSendersUserName = null;
	private String sSendersPassword = null;
	private int importanceLevel = 5;


	private String sEmailSubject = null;
	private String sMessageBody = null;
	private ArrayList attachmentFiles = new ArrayList();

	// private Vector vBaosFileStreams = null;
	// private Vector vFilenames = null;

	public abstract void setValue(NotificationEvent notifEvent) throws Exception;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.appperfect.monitor.notifier.notifiers.INotifier#initPropertySet(com.appperfect.monitor.notifier.dstruct.PropertySet)
	 */
	public void initPropertySet(final PropertySet propSet) throws Exception
	{
		LinkedList llValues = null;

		if (propSet == null)
		{
			throw new RuntimeException(RM.getString("VALUE_NO_PROPERTIES_FOR_EMAIL_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.SMTP_PORT);
		if (llValues != null)
		{
			try
			{
				this.setSmtpPort(Integer.parseInt((String) llValues.get(0)));
			}
			catch (final NumberFormatException e)
			{
				this.iSmtpPort = 25;
//				AppLogger.getLogger().log(
//						AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
			}
		}

		llValues = propSet.getProperty(INotifierConstants.SMTP_ADDRESS);
		if (llValues != null)
		{
			this.setSmtpAddress((String) llValues.get(0));
		}
		else
		{
			throw new RuntimeException(RM.getString("VALUE_NO_SMTP_ADDRESS_FOR_EMAIL_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.SMTP_SECURE);
		if (llValues != null)
		{
			this.setSecureProtocol(new Boolean((String) llValues.get(0)).booleanValue());
		}

		llValues = propSet.getProperty(INotifierConstants.SENDER_ADDRESS);
		if (llValues != null)
		{
			this.setSendersEmailAddress((String) llValues.get(0));
		}
		else
		{
			throw new RuntimeException(RM.getString("VALUE_NO_SENDER_ADDRESS_FOR_EMAIL_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.RECIPIENTS_ADDRESS);
		if (llValues != null)
		{
			this.setRecepientsAddresses(llValues);
		}
		else
		{
			throw new RuntimeException(RM.getString("VALUE_NO_RECEPIENT_ADDRESS_FOR_EMAIL_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.SENDER_NAME);
		if (llValues != null)
		{
			this.setSendersName((String) llValues.get(0));
		}
		else
		{
			throw new RuntimeException(RM.getString("VALUE_NO_SENDERS_NAME_FOR_EMAIL_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.AUTHREQD);
		if (llValues != null)
		{
			this.setAuthRequired(new Boolean((String) llValues.get(0)).booleanValue());
		}

		llValues = propSet.getProperty(INotifierConstants.SENDER_USER_NAME);
		if (llValues != null)
		{
			this.setSendersUserName((String) llValues.get(0));
		}

		llValues = propSet.getProperty(INotifierConstants.SENDER_PASSWORD);
		if (llValues != null)
		{
			this.setSendersPassword((String) llValues.get(0));
		}
		llValues = propSet.getProperty(INotifierConstants.NOTIF_PRIORITY);
		if (llValues != null)
		{
			this.setImportanceLevel(Integer.parseInt((String) llValues.get(0)));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.appperfect.monitor.notifier.notifiers.INotifier#notifyEvent(com.appperfect.monitor.notifier.common.NotificationEvent)
	 */
	public String notifyEvent(final NotificationEvent event) throws Exception
	{
		this.setValue(event);
		try
		{
			if (this.llRecepientsAddresses == null || this.llRecepientsAddresses.size() == 0)
			{
				throw new RuntimeException("Email notification is called without any recepients."); //$NON-NLS-1$
			}
			final Logger logger = AppLogger.getLogger();
			boolean debug = Level.DEBUG.isGreaterOrEqual(logger.getEffectiveLevel());
			EmailSender.sendMessage(this, debug);
			logger.log(AppLogger.getPriority(AppLogger.FATAL), "Email sent successfully, Subject of the mail: " + this.getEmailSubject()); //$NON-NLS-1$
		}
		catch (final Exception ex)
		{
//			AppLogger.getLogger().fatal("Error sending email", ex); //$NON-NLS-1$
			throw ex;
		}
		
		return null;
	}

	/**
	 * Set SmtpAddress.
	 *
	 * @param smtpAddress
	 */
	protected final void setSmtpAddress(final String smtpAddress)
	{
		this.sSmtpAddress = smtpAddress;
	}

	/**
	 * Get SmtpAddress.
	 *
	 * @return String
	 */
	protected final String getSmtpAddress()
	{
		return this.sSmtpAddress;
	}

	/**
	 * Set SmtpPort.
	 *
	 * @param smtpPort
	 */
	protected final void setSmtpPort(final int smtpPort)
	{
		this.iSmtpPort = smtpPort;
	}

	/**
	 * Get SmtpPort.
	 *
	 * @return int
	 */
	protected final int getSmtpPort()
	{
		return this.iSmtpPort;
	}

	/**
	 * Set SendersEmailAddress.
	 *
	 * @param sendersEmailAddress
	 */
	protected final void setSendersEmailAddress(final String sendersEmailAddress)
	{
		this.sSendersEmailAddress = sendersEmailAddress;
	}

	/**
	 * Get SendersEmailAddress.
	 *
	 * @return String
	 */
	protected final String getSendersEmailAddress()
	{
		return this.sSendersEmailAddress;
	}

	/**
	 * Set RecepientsAddresses.
	 *
	 * @param recepientsAddresses
	 */
	protected final void setRecepientsAddresses(final LinkedList recepientsAddresses)
	{
		this.llRecepientsAddresses = recepientsAddresses;
	}

	/**
	 * Get RecepientsAddresses.
	 *
	 * @return LinkedList
	 */
	protected final LinkedList getRecepientsAddresses()
	{
		return this.llRecepientsAddresses;
	}

	/**
	 * Set SendersName.
	 *
	 * @param sendersName
	 */
	protected final void setSendersName(final String sendersName)
	{
		this.sSendersName = sendersName;
	}

	/**
	 * Get SendersName.
	 *
	 * @return String
	 */
	protected final String getSendersName()
	{
		return this.sSendersName;
	}

	/**
	 * Set SendersUserName.
	 *
	 * @param sendersUserName
	 */
	protected final void setSendersUserName(final String sendersUserName)
	{
		this.sSendersUserName = sendersUserName;
	}

	/**
	 * Get SendersUserName.
	 *
	 * @return String
	 */
	protected final String getSendersUserName()
	{
		return this.sSendersUserName;
	}

	/**
	 * Set SendersPassword.
	 *
	 * @param sendersPassword
	 */
	protected final void setSendersPassword(final String sendersPassword)
	{
		this.sSendersPassword = sendersPassword;
	}

	/**
	 * Get SendersPassword.
	 *
	 * @return String
	 */
	protected final String getSendersPassword()
	{
		return this.sSendersPassword;
	}

	/**
	 * Set EmailSubject.
	 *
	 * @param emailSubject
	 */
	protected final void setEmailSubject(final String emailSubject)
	{
		this.sEmailSubject = emailSubject;
	}

	/**
	 * Get EmailSubject.
	 *
	 * @return String
	 */
	protected final String getEmailSubject()
	{
		return this.sEmailSubject;
	}

	/**
	 * Set MessageBody.
	 *
	 * @param messageBody
	 */
	protected final void setMessageBody(final String messageBody)
	{
		this.sMessageBody = messageBody;
	}

	/**
	 * Get MessageBody.
	 *
	 * @return String
	 */
	protected final String getMessageBody()
	{
		return this.sMessageBody;
	}

	/**
	 * Set Attachments.
	 *
	 * @param baosFileStreams
	 * @param filenames
	 */
	// protected final void setAttachments(Vector baosFileStreams, Vector
	// filenames)
	// {
	// vBaosFileStreams = baosFileStreams;
	// vFilenames = filenames;
	// }
	/**
	 * @return
	 */
	public final boolean isAuthRequired()
	{
		return this.bAuthRequired;
	}

	/**
	 * @param b
	 */
	public void setAuthRequired(final boolean b)
	{
		this.bAuthRequired = b;
	}

	/**
	 * @return
	 */
	public final ArrayList getAttachmentFiles()
	{
		return this.attachmentFiles;
	}

	/**
	 * @param list
	 */
	public void setAttachmentFiles(final ArrayList list)
	{
		this.attachmentFiles = list;
	}

	public boolean isSecureProtocol() 
	{
		return secureProtocol;
	}

	public void setSecureProtocol(boolean secureProtocol) 
	{
		this.secureProtocol = secureProtocol;
	}

	public int getImportanceLevel() {
		return importanceLevel;
	}

	public void setImportanceLevel(int importanceLevel) {
		this.importanceLevel = importanceLevel;
	}

}