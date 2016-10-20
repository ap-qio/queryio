/*
 * @(#)  EventQueueManager.java
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
package com.queryio.core.monitor.events;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.Queue;
import com.queryio.common.util.QueueManager;


/**
 * The EventQueueManager class is responsible for queuing the events fired and
 * then notifying the listeners when ever an event is added to an event queue.
 * It is in wait stage when ever the event queue is empty.
 * 
 * @author Exceed Consultancy Services
 */
public final class EventQueueManager
{
	private static final String EVENT_QUEUE_NAME = "AppPerfect Event Queue";

	private static Queue eventQueue;
	
	private static Object mutex = new Object();

	// default constructor is private as this class has all static methods
	private EventQueueManager()
	{
	}

	private static void init()
	{
		if (eventQueue == null || (!eventQueue.isRunning()))
		{
			eventQueue = QueueManager.startQueue(EVENT_QUEUE_NAME);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Event Queue Started");
		}
	}

	/**
	 * Method shutdown.
	 */
	public static void shutdown()
	{
		QueueManager.stopQueue(EVENT_QUEUE_NAME);
		eventQueue = null;
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Event Queue Stopped");
	}

	/**
	 * Method postEvent.
	 * 
	 * @param event
	 */
	public static void postEvent(final BaseEvent event)
	{
		synchronized (mutex) 
		{
			init();
			final EventQueueItem eqi = new EventQueueItem(event);
			
			eventQueue.addItem(eqi);
		}
	}
}
