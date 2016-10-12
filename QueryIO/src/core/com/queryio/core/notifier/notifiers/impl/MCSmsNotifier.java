/*
 * @(#)  MCSmsNotifier.java
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

import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;

/**
 * This class extends from the actual Sms Notification implementation. It
 * implements the setValue method. This is the main class which is responsible
 * for sending the sms and is called by the AlertGenerator.
 * 
 * @author Exceed Consultancy Services
 */
public class MCSmsNotifier extends SmsNotifier
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.notifier.notifiers.impl.sms.SmsNotifier#setValue(com.appperfect.monitor.notifier.common.NotificationEvent)
	 */
	public void setValue(final NotificationEvent event) throws Exception
	{
		this.setMessage((String) event.getProperty(INotifierConstants.ALERT_MESSAGE));
	}
}
