/*
 * @(#)  EventQueueItem.java
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

import com.queryio.common.util.QueueItem;

/**
 * The EventQueueItem class is added to the event queue and by event queue
 * manager. It encapsulates BaseEvent Object which is the actual event.
 * 
 * @author Exceed Consultancy Services
 */
public class EventQueueItem implements QueueItem {
	private BaseEvent baseEvent;
	static long counter = 0;
	final long number;

	/**
	 * Constructor of EventQueueItem.
	 * 
	 * @param event
	 */
	public EventQueueItem(final BaseEvent event) {
		this.baseEvent = event;
		number = ++counter;
	}

	/**
	 * Returns the baseEvent.
	 * 
	 * @return BaseEvent
	 */
	public BaseEvent getBaseEvent() {
		return this.baseEvent;
	}

	/**
	 * Sets the baseEvent.
	 * 
	 * @param baseEvent
	 *            The baseEvent to set
	 */
	public void setBaseEvent(final BaseEvent baseEvent) {
		this.baseEvent = baseEvent;
	}

	public void serve() {
		/*
		 * if (baseEvent == null) {
		 * AppLogger.getLogger(IProductConstants.AGENTLESS_MONITOR).
		 * fatal("Event served QueueItem (NULL base event) counter: " + number);
		 * } else if (baseEvent instanceof ControllerStateChangeEvent) {
		 * ControllerStateChangeEvent ce =
		 * (ControllerStateChangeEvent)baseEvent;
		 * AppLogger.getLogger(IProductConstants.AGENTLESS_MONITOR).
		 * fatal("Event served QueueItem counter: " + number + " controller: " +
		 * ce.getControllerId() + " current state: " + ce.getCurrentState() +
		 * " new state: " + ce.getNewState()); } else {
		 * AppLogger.getLogger(IProductConstants.AGENTLESS_MONITOR).
		 * fatal("Event served QueueItem counter: " + number + " class: " +
		 * baseEvent.getClass().getName()); }
		 */
		this.baseEvent.getDispatcher().dispatchEvent(this.baseEvent);
	}
}
