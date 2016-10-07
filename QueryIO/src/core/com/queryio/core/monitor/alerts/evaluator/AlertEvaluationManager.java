/*
 * @(#)  AlertEvaluationManager.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.queryio.common.util.AppLogger;
import com.queryio.core.monitor.events.AlertEvent;
import com.queryio.core.monitor.events.EventQueueManager;

/**
 * This class is responsible for managing various events for the AlertEvaluator.
 * If some alert rule evaluates to true, or an existing alert condition is reset
 * it fires appropriate events. At the end of the Evaluation it also fires the
 * dataProcessed event.
 * 
 * @author Exceed Consultancy Services
 */
public class AlertEvaluationManager
{
	// CONSTANT FOR RANGEMONITORING RULE
	public static final String RULE_RANGEMONITORING="_RM";
	public static final String RULE_SYSTEMRULE="_SYSTEMRULE";
	
	// Constants for Expression types
	public static final String CONDITION_OVER = "Over";
	public static final String CONDITION_UNDER = "Under";
	public static final String CONDITION_EQUALS = "Equals";
	public static final String CONDITION_NOTEQUALS = "Not Equals";

	// rule's constants
	public static final String RULESEVERITY_WARNING = "Warning";
	public static final String RULESEVERITY_ERROR = "Error";

	// rule expression's constants
	public static final char EXPR_ATTRIBUTE_ID_ENCAPSULATOR = '@';
	public static final String EXPR_TS = "time_stamp";
	public static final char EXPR_ATTRIBUTE_NAME_BEGIN = '{';
	public static final char EXPR_ATTRIBUTE_NAME_END = '}';
	public static final String EXPR_VALID_TOKENS = "<>=!|&+-*/()^%" + EXPR_ATTRIBUTE_ID_ENCAPSULATOR;

//	private static EventDispatcherImpl eventDispatcher = null;
	private static final Map durationRulesCache;
	private static EventDispatcherImpl eventDispatcher = null;
	

	/* default constructor is private as this class contains only static methods */
	private AlertEvaluationManager()
	{
		
	}

	static
	{
		eventDispatcher = new EventDispatcherImpl();
		
		durationRulesCache = new HashMap();
	}

	public static void fireAlertResetEvent(final String nodeId, final ArrayList resetRules,
			final ArrayList resetAttributes, final long timeStamp)
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fireAlertResetEvent");
		EventQueueManager.postEvent(new AlertEvent(nodeId, AlertEvent.RESET, resetRules, resetAttributes,
				timeStamp, eventDispatcher));
	}

	public static void fireAlertRaisedEvent(final String nodeId, final ArrayList violatedRules,
			final ArrayList violatedAttributes, final long timeStamp)
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fireAlertRaisedEvent");
		EventQueueManager.postEvent(new AlertEvent(nodeId, AlertEvent.RAISED, violatedRules, violatedAttributes,
				timeStamp, eventDispatcher));
	}
	
	public static Map getDurationRulesCache()
	{
		return durationRulesCache;
		
	}

	public static void ruleDeleted(String ruleId)
	{
		Iterator itr = durationRulesCache.keySet().iterator();
		while (itr.hasNext())
		{
			String key = (String)itr.next();
			if (key.startsWith(ruleId + "_"))
			{
				itr.remove();
			}
		}
	}
}
