/*
 * @(#)  CustomCarrierNotifier.java Nov 16, 2004
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
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CustomCarrierNotifier implements INotifier {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private String sMessage = null;
	private LinkedList llRecepientsUserIds = null;

	public abstract void setValue(NotificationEvent notifEvent) throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.appperfect.common.notifier.notifiers.INotifier#initPropertySet(com.
	 * appperfect.common.notifier.dstruct.PropertySet)
	 */
	public void initPropertySet(final PropertySet propSet) throws Exception {
		LinkedList llValues = null;

		if (propSet == null) {
			throw new RuntimeException(RM.getString("VALUE_NO_PROPERTIES_MSG")); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.CUSTOM_CARRIER_LOCATIONS);
		if (llValues != null) {
			this.setRecepientsUserIds(llValues);
		} else {
			throw new RuntimeException(RM.getString("VALUE_NO_RECEPIENT_USERS_MSG")); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.common.notifier.notifiers.INotifier#notifyEvent(com.
	 * appperfect.common.notifier.common.NotificationEvent)
	 */
	public String notifyEvent(final NotificationEvent event) throws Exception {
		this.setValue(event);
		final CustomCarrierSender sender = new CustomCarrierSender(this);
		sender.sendMessage();

		return null;
	}

	/**
	 * Set RecepientsuserIds.
	 * 
	 * @param recepientsUserIds
	 */
	protected final void setRecepientsUserIds(final LinkedList recepientsUserIds) {
		this.llRecepientsUserIds = recepientsUserIds;
	}

	/**
	 * Get RecepientsuserIds.
	 * 
	 * @return LinkedList
	 */
	protected final LinkedList getRecepientsUserIds() {
		return this.llRecepientsUserIds;
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
}
