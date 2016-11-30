/*
 * @(#)  AlertEvent.java
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

import java.util.ArrayList;

/**
 * this even is fired when an alert is raised or reset
 * 
 * @author Exceed Consultancy Services
 */
public class AlertEvent extends NodeEvent {
	public static final int RAISED = 0;
	public static final int RESET = 1;

	/* denotes the state of the event */
	private final int state;

	/* stores the rules for this event */
	private final ArrayList rules;

	/* stores the attributes for this event */
	private final ArrayList attributes;

	public AlertEvent(final String nodeId, final int state, final ArrayList rules, final ArrayList attributes,
			final long timeStamp, final EventDispatcher dispatcher) {
		super(nodeId, timeStamp, dispatcher);
		this.state = state;
		this.rules = rules;
		this.attributes = attributes;
	}

	public ArrayList getAttributes() {
		return this.attributes;
	}

	public ArrayList getRules() {
		return this.rules;
	}

	public int getState() {
		return this.state;
	}
}
