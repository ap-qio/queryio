/*
 * @(#)  EventManager.java
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

import com.queryio.common.util.AppLogger;
import com.queryio.core.notifier.common.NotificationEvent;

/**
 * This is the Singleton class and is used by the client to post the Event to
 * the Notification Server. This class initializes the event module by parsing
 * the xml file.
 *
 * @author Exceed Consultancy Services
 */
public final class EventManager
{
	private static EventManager evtMgr = null;
	private String sConfigXMLFilePath = null;
	private String sNotifMgrIPAddress = null;
	private int iNotifMgrPort = -1;

	private EventSender evtSender = null;
	/**
	 * Default Constructor
	 */
	private EventManager()
	{
		this.sNotifMgrIPAddress = null;
		this.iNotifMgrPort = EventConstants.DEF_SOCKET_PORT;

		this.evtSender = null;
	}

	/**
	 * Retuns the singleton Object of the EventManager.
	 *
	 * @return EventManager
	 */
	public static EventManager getInstance()
	{
		if (evtMgr == null)
		{
			evtMgr = new EventManager();
		}

		return evtMgr;
	}

	/**
	 * Initializes EventManager.
	 *
	 * @throws Exception
	 */
	public void initializeEventManager() throws Exception
	{
		final EventXMLFileParser fileParser = new EventXMLFileParser();
		fileParser.retreiveNotificationManagerProperties();

		this.evtSender = new EventSender(this.sNotifMgrIPAddress, this.iNotifMgrPort);
		// bEventThreadStarted = false;
	}

	/**
	 * postEvent method is called when the event has to be fired.
	 *
	 * @param event
	 */
	public void postEvent(final NotificationEvent evt) throws Exception
	{
		this.evtSender.sendEventToNotifMgr(evt);
	}

	/**
	 * Set ConfigXMLFilePath.
	 *
	 * @param path
	 */
	public void setConfigXMLFilePath(final String path)
	{
		this.sConfigXMLFilePath = path;
	}

	/**
	 * Get ConfigXMLFilePath.
	 *
	 * @return String
	 */
	public final String getConfigXMLFilePath()
	{
		return this.sConfigXMLFilePath;
	}

	/**
	 * Sets the NotifMgrPort.
	 *
	 * @param notifMgrPort
	 *            The NotifMgrPort to set
	 */
	public void setNotifMgrPort(final String notifMgrPort)
	{
		try
		{
			this.iNotifMgrPort = Integer.parseInt(notifMgrPort);
		}
		catch (final NumberFormatException e)
		{
			this.iNotifMgrPort = EventConstants.DEF_SOCKET_PORT;
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * Sets the NotifMgrIPAddress.
	 *
	 * @param notifMgrIPAddress
	 *            The NotifMgrIPAddress to set
	 */
	public void setNotifMgrIPAddress(final String notifMgrIPAddress)
	{
		this.sNotifMgrIPAddress = notifMgrIPAddress;
	}

	/*
	 * public static void main(String [] args) { EventManager evtMgr =
	 * EventManager.getInstance(); NotificationEvent evt = null;
	 *
	 * evtMgr.setConfigXMLFilePath("."); try { evtMgr.initializeEventManager();
	 *
	 * evt = new NotificationEvent("trial1"); evt.addProperty("name", "check it
	 * out"); evt.addProperty("id", new Integer(1));
	 *
	 * evtMgr.postEvent(evt);
	 *
	 * evt = new NotificationEvent("trial2"); evt.addProperty("name", "check
	 * trial2"); evt.addProperty("id", new Integer(100)); } catch(Exception ex) { } }
	 */
}