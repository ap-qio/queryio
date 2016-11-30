/*
 * @(#)  CustomCarrierSender.java Nov 16, 2004
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

/**
 * 
 * 
 * @author Exceed Consultancy Services
 */
class CustomCarrierSender {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private CustomCarrierNotifier customCarrierNotifier = null;

	/**
	 * @param ymNotif
	 */
	CustomCarrierSender(final CustomCarrierNotifier customCarrierNotifier) {
		this.customCarrierNotifier = customCarrierNotifier;
	}

	/**
	 * sendMessage
	 * 
	 * @throws Exception
	 */
	void sendMessage() throws Exception {
		try {
			final LinkedList llCustomCarriers = this.customCarrierNotifier.getRecepientsUserIds();
			final int iSize = llCustomCarriers.size();
			final String strMsg = this.customCarrierNotifier.getMessage();
			for (int i = 0; i < iSize; i++) {
				Runtime.getRuntime().exec(((String) llCustomCarriers.get(i)) + " " + strMsg); //$NON-NLS-1$ //$IGN_String_concatenation_in_loop$
			}
		} catch (final Exception ex) {
			throw new RuntimeException(RM.getString("VALUE_ERR_NOTIFICATION") + ex.toString()); //$NON-NLS-1$
		}
	}
}
