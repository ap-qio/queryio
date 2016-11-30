/*
 * @(#)  INotifier.java
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

package com.queryio.core.notifier.notifiers;

import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;

/**
 * This interface is the notifier interface which has to be implemented by all
 * the Notifier implementations.
 * 
 * @author Exceed Consultancy Services
 */
public interface INotifier {
	/**
	 * Initializes the PropertySet for the Notifier
	 * 
	 * @param propSet
	 * @throws Exception
	 */
	void initPropertySet(PropertySet propSet) throws Exception;

	/**
	 * Notifies the notifier for the event Posted by the EventManager
	 * 
	 * @param event
	 * @throws Exception
	 */
	String notifyEvent(NotificationEvent event) throws Exception;
}
