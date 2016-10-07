/*
 * @(#)  NotificationManager.java
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.Notifier;

/**
 *
 * @author Exceed Consultancy Services
 */
public class NotificationManager
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private static NotificationManager notifMgr = null;

	private EventReader reader = null;

	private String sConfigXMLFilePath = null;

	private boolean isSocketNotificationEnabled = false;

	private String sServerIP = null;
	private int iSocketPort = NotifierConstants.DEF_SOCKET_PORT;

	// this Hashtable will have the
	// key = eventtype
	// value = linkList of the Notifier Objects
	private Hashtable htEventNotifiers = null;

	// static
	// {
	// // //turn off Hamsam logging here
	// // LogManager.getLogger().setLevel(Level.OFF);
	// try
	// {
	// Class hamsamLogMgr = Class.forName("hamsam.util.log.LogManager");
	// Method getLogger = hamsamLogMgr.getMethod("getLogger", new Class[] {});
	// Object objLogger = getLogger.invoke(null, new Object[]{});
	// Class clsLogger = objLogger.getClass();
	// Class clsLevel = Class.forName("java.util.logging.Level");
	// Method setLevel = clsLogger.getMethod("setLevel", new Class[]{clsLevel});
	// Field field = clsLevel.getDeclaredField("OFF");
	// setLevel.invoke(objLogger, new Object[]{field.get(null)});
	// }
	// catch (Exception e)
	// {
	// }
	// }

	private NotificationManager()
	{
		this.htEventNotifiers = new Hashtable();
	}

	/**
	 * Retuns the singleton class of the NotificationManager.
	 *
	 * @return NotificationManager
	 */
	public static NotificationManager getInstance()
	{
		if (notifMgr == null)
		{
			notifMgr = new NotificationManager();
		}

		return notifMgr;
	}

	/**
	 * Initializes NotificationManager.
	 *
	 * @throws Exception
	 */
	public void initializeNotificationManager() throws Exception
	{
		final NotifierXMLFileParser fileParser = new NotifierXMLFileParser();
		fileParser.retreiveNotifierConfiguration();

		// if socket communication is enabled then create the server
		// socket to listen for incoming events
		if (this.isSocketNotificationEnabled)
		{
			this.reader = new EventReader(this.sServerIP, this.iSocketPort);
			new Thread(this.reader).start();
		}
	}

	/**
	 * This method is used to notify the Notifier regarding the new Event
	 * received and it notifies to all the notifers for that event.
	 *
	 * @param event
	 * @throws Exception
	 */
	public void fireEventReceived(final NotificationEvent event) throws Exception
	{
		final String sEventType = event.getEventType();
		final Object oValue = this.htEventNotifiers.get(sEventType);

		if (oValue != null)
		{
			final LinkedList llNotifiers = (LinkedList) oValue;
			final int iSize = llNotifiers.size();

			for (int i = 0; i < iSize; i++)
			{
				this.fireEventReceived(event, (Notifier) llNotifiers.get(i));
			}
		}
	}

	/**
	 * This method is used to notify the Notifier regarding the new Event
	 * received and it notifies to a single notifier only.
	 *
	 * @param event
	 * @param notif
	 * @throws Exception
	 */
	public String fireEventReceived(final NotificationEvent event, final Notifier notif) throws Exception
	{
		INotifier notifier = null;
		
		if (notif.getPropertySet() != null)
		{
			try
			{
				// instantiate the notifier class
				final Class notifierClass = Class.forName(notif.getClassName());
				notifier = (INotifier) notifierClass.newInstance();
			}
			catch (final Exception ex)
			{
				throw new RuntimeException(RM.getString("VALUE_ERROR_NOTIFIER_CLASS_MSG") + ex.toString()); //$NON-NLS-1$
			}
			
			try
			{
				// initialize the propertySet of the Notifier
				notifier.initPropertySet(notif.getPropertySet());
				// notifyEvent to the Notifier
				return notifier.notifyEvent(event);
			}
			catch (final Exception ex)
			{
//				AppLogger.getLogger().fatal(ex.getMessage(), ex);
				
//				ex.printStackTrace();		//TODO remove it.
				
				final StringBuffer sbException = new StringBuffer(RM.getString("VALUE_ERROR_NOTIFYING_EVENT_MSG")); //$NON-NLS-1$
				sbException.append(event.getEventType());
				sbException.append(" using Notifier:"); //$NON-NLS-1$
				sbException.append(notif.getName());
				sbException.append('\n');
				sbException.append(ex.toString());
				
				throw new RuntimeException(sbException.toString());
			}
		}
		
		return null;
	}

	/**
	 * This method is used to register a Notifier for a particular EventType .
	 *
	 * @param sEventType
	 * @param notif
	 */
	public void registerEventNotifier(final String sEventType, final Notifier notif)
	{
		LinkedList llNotifiers = null;
		final Object oValue = this.htEventNotifiers.get(sEventType);

		if (oValue == null)
		{
			llNotifiers = new LinkedList();
			llNotifiers.addLast(notif);
			this.htEventNotifiers.put(sEventType, llNotifiers);
		}
		else
		{
			llNotifiers = (LinkedList) oValue;
			llNotifiers.addLast(notif);
		}

	}

	/**
	 * This method is used to get all the Notifiers registered for the Event.
	 *
	 * @param sEventType
	 * @return llNotifiers
	 * @throws Exception
	 */
	public LinkedList getAllEventNotifiers(final String sEventType) throws Exception
	{
		final Object oValue = this.htEventNotifiers.get(sEventType);

		if (oValue == null)
		{
			throw new RuntimeException(RM.getString("VALUE_ERROR_EVENT_MSG")); //$NON-NLS-1$
		}

		return (LinkedList) oValue;
	}

	/**
	 * This method is used to get the Notifier registered for the Event.
	 *
	 * @param sEventType
	 * @param sNotifierName
	 * @return Notifier
	 * @throws Exception
	 */
	public Notifier getEventNotifier(final String sEventType, final String sNotifierName) throws Exception
	{
		try
		{
			AppLogger.getLogger().debug("Total Keys : " + this.htEventNotifiers.keySet().size());
			AppLogger.getLogger().debug("Event Type : " + sEventType);
			Iterator iter = this.htEventNotifiers.keySet().iterator();
			while(iter.hasNext())
				AppLogger.getLogger().debug("Key : " + iter.next());
			final Object oValue = this.htEventNotifiers.get(sEventType);
			if (oValue == null)
			{
				throw new RuntimeException("The event type is not registered with the Notification Manager"); //$NON-NLS-1$
			}

			final LinkedList llNotifiers = (LinkedList) oValue;
			Notifier notif = null;
			final int iSize = llNotifiers.size();
			boolean bNotifierFound = false;

			for (int i = 0; i < iSize; i++)
			{
				notif = (Notifier) llNotifiers.get(i);
				if (notif.getName().equals(sNotifierName))
				{
					bNotifierFound = true;
					return notif;
				}
			}

			if (bNotifierFound)
			{
				throw new RuntimeException(RM.getString("VALUE_NOTIFIER_NOT_ADDED_MSG")); //$NON-NLS-1$
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		return null;
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
	 * Sets the SocketPort.
	 *
	 * @param socketPort
	 *            The SocketPort to set
	 */
	public void setSocketPort(final String socketPort)
	{
		try
		{
			this.iSocketPort = Integer.parseInt(socketPort);
		}
		catch (final NumberFormatException e)
		{
			this.iSocketPort = NotifierConstants.DEF_SOCKET_PORT;
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * Get the SocketPort.
	 *
	 * @return int
	 */
	public final int getSocketPort()
	{
		return this.iSocketPort;
	}

	/**
	 * Sets the ServerIP.
	 *
	 * @param serverIP
	 *            The sServerIP to set
	 */
	public void setServerIP(final String serverIP)
	{
		this.sServerIP = serverIP;
	}

	/**
	 * Returns the ServerIP.
	 *
	 * @return String
	 */
	public final String getServerIP()
	{
		return this.sServerIP;
	}

	/**
	 * Sets the isSocketNotificationEnabled.
	 *
	 * @param isSocketNotificationEnabled
	 *            The isSocketNotificationEnabled to set
	 */
	public void setIsSocketNotificationEnabled(final String sIsNotificationEnabled)
	{
		if (sIsNotificationEnabled.equalsIgnoreCase("true")) //$NON-NLS-1$
		{
			this.isSocketNotificationEnabled = true;
		}
	}

	/**
	 * Returns the isSocketNotificationEnabled.
	 *
	 * @return boolean
	 */
	public final boolean isSocketNotificationEnabled()
	{
		return this.isSocketNotificationEnabled;
	}

	/*
	 * public static void main(String [] args) { NotificationManager notifMgr =
	 * NotificationManager.getInstance(); try {
	 * notifMgr.initializeNotificationManager(); } catch(Exception ex) { } }
	 */
}
