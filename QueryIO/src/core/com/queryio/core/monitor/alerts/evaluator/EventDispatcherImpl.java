/*
 * @(#)  EventDispatcherImpl.java
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
package com.queryio.core.monitor.alerts.evaluator;

import com.queryio.core.monitor.events.AlertEvent;
import com.queryio.core.monitor.events.AlertListener;
import com.queryio.core.monitor.events.AlertListenerImpl;
import com.queryio.core.monitor.events.BaseEvent;
import com.queryio.core.monitor.events.EventDispatcher;

public class EventDispatcherImpl implements EventDispatcher {
	private AlertListener alertListener = null;

	public EventDispatcherImpl() {
		alertListener = new AlertListenerImpl();
	}

	public void dispatchEvent(final BaseEvent event) {
		if (event instanceof AlertEvent) {
			this.dispatchAlertEvent((AlertEvent) event);
		}
	}

	private void dispatchAlertEvent(final AlertEvent event) {
		if (this.alertListener != null) {
			switch (event.getState()) {
			case AlertEvent.RAISED: {
				alertListener.alertRaised(event);
				break;
			}
			case AlertEvent.RESET: {
				alertListener.alertReset(event);
				break;
			}
			default: {
				// do nothing
			}
			}
		}
	}
}
