/*
 * @(#)  SmsNotifier.java
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

import java.util.LinkedList;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.INotifier;

/**
 * This is the SmsNotifier used for Sms Notifications.
 * 
 * @author Exceed Consultancy Services
 */
public abstract class SmsNotifier implements INotifier {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private String comPort = null;
	private String sSendersMobileNo = null;
	private String sMobileMfg = null;
	private String sMobileModel = null;
	private int baudRate;
	private LinkedList llRecepientsMobileNos = null;

	private String sMessage = null;

	public abstract void setValue(NotificationEvent notifEvent) throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.appperfect.monitor.notifier.notifiers.INotifier#initPropertySet(com.
	 * appperfect.monitor.notifier.dstruct.PropertySet)
	 */
	public void initPropertySet(final PropertySet propSet) throws Exception {
		LinkedList llValues = null;

		if (propSet == null) {
			throw new RuntimeException(RM.getString("VALUE_NO_PROPERTIES_FOR_SMS_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.COM_PORT);
		if (llValues != null) {
			this.setComPort((String) llValues.get(0));
		}

		llValues = propSet.getProperty(INotifierConstants.SENDERS_MOBILE_NO);
		if (llValues != null) {
			this.setSendersMobileNo((String) llValues.get(0));
		} else {
			throw new RuntimeException(RM.getString("VALUE_NO_SENDERS_MOBILE_NUMBER_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.MOBILE_MFG);
		if (llValues != null) {
			this.sMobileMfg = (String) llValues.get(0);
		} else {
			throw new RuntimeException("Mobile manfacturer not set"); //$NON-NLS-1$
		}
		llValues = propSet.getProperty(INotifierConstants.MOBILE_MODEL);
		if (llValues != null) {
			this.sMobileModel = (String) llValues.get(0);
		} else {
			throw new RuntimeException("Mobile's model not set"); //$NON-NLS-1$
		}
		llValues = propSet.getProperty(INotifierConstants.BAUD_RATE);
		if (llValues != null) {
			try {
				this.baudRate = Integer.parseInt((String) llValues.get(0));
			} catch (final NumberFormatException e) {
				throw new RuntimeException("Baud rate should be integer, specified value: " + (String) llValues.get(0)); //$NON-NLS-1$
			}
		} else {
			throw new RuntimeException("Baud rate not set"); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.RECIPIENTS_MOBILE_NO);
		if (llValues != null) {
			this.setRecepientsMobileNos(llValues);
		} else {
			throw new RuntimeException(RM.getString("VALUE_NO_RECEPIENT_MOBILE_NUMBER_MSG")); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.notifier.notifiers.INotifier#notifyEvent(com.
	 * appperfect.monitor.notifier.common.NotificationEvent)
	 */
	public String notifyEvent(final NotificationEvent event) throws Exception {
		this.setValue(event);
		new SmsSender().sendMessage(this);

		return null;
	}

	/**
	 * Set Com Port for Communication.
	 * 
	 * @param s
	 */
	protected final void setComPort(final String s) {
		this.comPort = s;
	}

	/**
	 * Get ComPort.
	 * 
	 * @return String
	 */
	protected final String getComPort() {
		return this.comPort;
	}

	/**
	 * Set SendersMobileNo.
	 * 
	 * @param sendersMobileNo
	 */
	protected final void setSendersMobileNo(final String sendersMobileNo) {
		this.sSendersMobileNo = sendersMobileNo;
	}

	/**
	 * Get SendersMobileNo.
	 * 
	 * @return String
	 */
	protected final String getSendersMobileNo() {
		return this.sSendersMobileNo;
	}

	/**
	 * Set RecepientsMobileNos.
	 * 
	 * @param recepientsAddresses
	 */
	protected final void setRecepientsMobileNos(final LinkedList recepientsMobileNos) {
		this.llRecepientsMobileNos = recepientsMobileNos;
	}

	/**
	 * Get RecepientsMobileNos.
	 * 
	 * @return LinkedList
	 */
	protected final LinkedList getRecepientsMobileNos() {
		return this.llRecepientsMobileNos;
	}

	/**
	 * Set Message.
	 * 
	 * @param message
	 */
	protected final void setMessage(final String message) {
		this.sMessage = message;
	}

	/**
	 * Get Message.
	 * 
	 * @return String
	 */
	protected final String getMessage() {
		return this.sMessage;
	}

	public String getMobileManufacturer() {
		return sMobileMfg;
	}

	public String getMobileModel() {
		return sMobileModel;
	}

	public int getBaudRate() {
		return baudRate;
	}
}
