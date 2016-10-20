/*
 * @(#)  EventSender.java
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

package com.queryio.core.notifier.events;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.core.notifier.common.NotificationEvent;

/**
 * This is the class which actually posts the events to the Notification Server.
 * 
 * @author Exceed Consultancy Services
 */
class EventSender
{
	// private EventManager evtMgr = null;

	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private ObjectOutputStream os = null;

	/**
	 * This is the Constructor for the class. This initializes the Socket and
	 * retreived the OutputStream of the Socket.
	 * 
	 * @param notifMgrIPAddress
	 * @param notifMgrPort
	 */
	EventSender(final String notifMgrIPAddress, final int notifMgrPort)
	{
		try
		{
			final Socket sockTarget = new Socket(notifMgrIPAddress, notifMgrPort);
			this.os = new ObjectOutputStream(new BufferedOutputStream(sockTarget.getOutputStream()));
		}
		catch (final Exception ex)
		{
			throw new RuntimeException(RM.getString("VALUE_ERROR_SOCKET_MSG") + ex.toString()); //$NON-NLS-1$
		}

		// evtMgr = EventManager.getInstance();
	}

	/**
	 * This is the method which sends the notification Event in the Socket and
	 * is responsible to actually post the Event to the Notification Server
	 */
	void sendEventToNotifMgr(final NotificationEvent evt)
	{
		try
		{
			this.os.writeObject(evt);
			this.os.flush();
		}
		catch (final Exception ex)
		{
			throw new RuntimeException(RM.getString("VALUE_ERROR_EVENT_OBJECT_MSG") + ex.toString()); //$NON-NLS-1$
		}
	}
}
