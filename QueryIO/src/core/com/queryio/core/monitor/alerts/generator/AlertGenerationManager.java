/*
 * @(#)  AlertGenerationManager.java
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
package com.queryio.core.monitor.alerts.generator;

import com.queryio.core.monitor.events.AlertListenerImpl;

public class AlertGenerationManager
{
	// notification's constants
	public static final String NOTIF_EMAIL = "Email";
	public static final String NOTIF_SMS = "Sms";
	public static final String NOTIF_YAHOO = "Yahoo";
	public static final String NOTIF_MSN = "MSN";
	public static final String NOTIF_AOL = "AOL";
	public static final String NOTIF_CUSTOM = "Custom";
	public static final String NOTIF_LOG = "Log";
	public static final String NOTIF_DATABASE = "Database";
	public static final String NOTIF_SNMPTRAP = "SNMP Trap";
	public static final String NOTIF_ESPRESSO = "Espresso";

	/* the data received listener */
	private static AlertListenerImpl alertListener = null;

	/* default constructor is private as this class contains only static methods */
	private AlertGenerationManager()
	{
	}
}