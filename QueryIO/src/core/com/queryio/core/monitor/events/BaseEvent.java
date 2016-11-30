/*
 * @(#)  BaseEvent.java
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

/**
 * This class is the base class of all the events in the application.
 * 
 * @author Exceed Consultancy Services
 */
public abstract class BaseEvent {
	private long timeStamp = -1;

	private EventDispatcher dispatcher = null;

	public BaseEvent(final long ts, final EventDispatcher ed) {
		this.timeStamp = ts;
		this.dispatcher = ed;
	}

	/**
	 * method getDispatcher
	 * 
	 * @return
	 */
	public EventDispatcher getDispatcher() {
		return this.dispatcher;
	}

	/**
	 * method getTimeStamp
	 * 
	 * @return
	 */
	public long getTimeStamp() {
		return this.timeStamp;
	}

}
