/*
 * @(#) INotifierConstants.java
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
package com.queryio.core.notifier.common;

/**
 * 
 * @author Exceed Consultancy Services
 */
public interface INotifierConstants
{
	// Event types
	String ALERT_RAISED_NOTIFICATION = "AlertRaisedNotification"; //$NON-NLS-1$

	// Message Type
	String ALERT_SUBJECT = "alertSubject"; //$NON-NLS-1$
	String ALERT_MESSAGE = "alertMessage"; //$NON-NLS-1$
	String MESSAGE_ATTACHMENTS = "messageAttachments"; //$NON-NLS-1$

	// MC Notifier names
	String MC_EMAIL_NOTIFIER = "MCEmailNotifier"; //$NON-NLS-1$
	String MC_SMS_NOTIFIER = "MCSmsNotifier"; //$NON-NLS-1$
	String MC_YM_NOTIFIER = "MCYMNotifier"; //$NON-NLS-1$
	String MC_MSN_NOTIFIER = "MCMSNNotifier"; //$NON-NLS-1$
	String MC_AOL_NOTIFIER = "MCAOLNotifier"; //$NON-NLS-1$
	String MC_CUSTOM_NOTIFIER = "MCCustomNotifier"; //$NON-NLS-1$
	String MC_LOG_NOTIFIER = "MCLogNotifier"; //$NON-NLS-1$
	String MC_DB_NOTIFIER = "MCDatabaseNotifier"; //$NON-NLS-1$
	String MC_SNMPTRAP_NOTIFIER = "MCSNMPTrapNotifier"; //$NON-NLS-1$
	String MC_ESPRESSO_NOTIFIER = "MCEspressoNotifier"; //$NON-NLS-1$

	// Notifier Property set names
	String MC_EMAIL_NOTIFIER_PROPERTYSET = "MCEmailNotifierPropSet"; //$NON-NLS-1$
	String MC_SMS_NOTIFIER_PROPERTYSET = "MCSmsNotifierPropSet"; //$NON-NLS-1$
	String MC_YM_NOTIFIER_PROPERTYSET = "MCYmNotifierPropSet"; //$NON-NLS-1$
	String MC_MSN_NOTIFIER_PROPERTYSET = "MCMSNNotifierPropSet"; //$NON-NLS-1$
	String MC_AOL_NOTIFIER_PROPERTYSET = "MCAolNotifierPropSet"; //$NON-NLS-1$
	String MC_CUSTOM_NOTIFIER_PROPERTYSET = "MCCustomNotifierPropSet"; //$NON-NLS-1$
	String MC_LOG_NOTIFIER_PROPERTYSET = "MCLogNotifierPropSet"; //$NON-NLS-1$
	String MC_DB_NOTIFIER_PROPERTYSET = "MCDatabaseNotifierPropSet"; //$NON-NLS-1$
	String MC_SNMPTRAP_NOTIFIER_PROPERTYSET = "MCSNMPTrapNotifierPropSet"; //$NON-NLS-1$
	String MC_ESPRESSO_NOTIFIER_PROPERTYSET = "MCEspressoNotifierPropSet"; //$NON-NLS-1$

	// DevSuite Notifier names
	String DEVSUITE_EMAIL_NOTIFIER = "DevSuiteEmailNotifier"; //$NON-NLS-1$
	String DEVSUITE_SMS_NOTIFIER = "DevSuiteSmsNotifier"; //$NON-NLS-1$
	String DEVSUITE_YM_NOTIFIER = "DevSuiteYMNotifier"; //$NON-NLS-1$
	String DEVSUITE_MSN_NOTIFIER = "DevSuiteMSNNotifier"; //$NON-NLS-1$
	String DEVSUITE_AOL_NOTIFIER = "DevSuiteAOLNotifier"; //$NON-NLS-1$
	String DEVSUITE_CUSTOM_NOTIFIER = "DevSuiteCustomCarrierNotifier"; //$NON-NLS-1$

	// DevSuite Notifier Property set names
	String DEVSUITE_EMAIL_NOTIFIER_PROPERTYSET = "DevSuiteEmailNotifierPropSet"; //$NON-NLS-1$
	String DEVSUITE_SMS_NOTIFIER_PROPERTYSET = "DevSuiteSmsNotifierPropSet"; //$NON-NLS-1$
	String DEVSUITE_YM_NOTIFIER_PROPERTYSET = "DevSuiteYmNotifierPropSet"; //$NON-NLS-1$
	String DEVSUITE_MSN_NOTIFIER_PROPERTYSET = "DevSuiteMSNNotifierPropSet"; //$NON-NLS-1$
	String DEVSUITE_AOL_NOTIFIER_PROPERTYSET = "DevSuiteAolNotifierPropSet"; //$NON-NLS-1$
	String DEVSUITE_CUSTOM_NOTIFIER_PROPERTYSET = "DevSuiteCustomNotifierPropSet"; //$NON-NLS-1$

	// Constants for Email Notifier
	String SMTP_ADDRESS = "SmtpAddress"; //$NON-NLS-1$
	String SMTP_SECURE = "SmtpProtocol"; //$NON-NLS-1$
	String SMTP_PORT = "SmtpPort"; //$NON-NLS-1$
	String SENDER_ADDRESS = "SenderAddress"; //$NON-NLS-1$
	String RECIPIENTS_ADDRESS = "RecepientsAddress"; //$NON-NLS-1$
	String SENDER_NAME = "SenderName"; //$NON-NLS-1$
	String SENDER_USER_NAME = "SenderUserName"; //$NON-NLS-1$
	String SENDER_PASSWORD = "SenderPassword"; //$NON-NLS-1$
	String AUTHREQD = "AuthenticationRequired"; //$NON-NLS-1$
	
	String NOTIF_PRIORITY = "ImpotanceLevel";	// For e-mail Priority
	
	// Constants for SMS Notifier
	String COM_PORT = "ComPort"; //$NON-NLS-1$
	String SENDERS_MOBILE_NO = "SendersMobileNo"; //$NON-NLS-1$
	String RECIPIENTS_MOBILE_NO = "RecepientsMobileNo"; //$NON-NLS-1$
	String MOBILE_MFG = "MobileManufacturer"; //$NON-NLS-1$
	String MOBILE_MODEL = "MobileModel"; //$NON-NLS-1$
	String BAUD_RATE = "BaudRate"; //$NON-NLS-1$

	// Constants for YAHOO Notifier
	String YM_USERID = "YMUserId"; //$NON-NLS-1$
	String YM_PASSWORD = "YMPassword"; //$NON-NLS-1$
	String YM_RECIPIENTS_USERIDS = "RecepientsUserIds"; //$NON-NLS-1$

	// Constants for AOL Notifier
	String AOL_USERID = "AOLUserId"; //$NON-NLS-1$
	String AOL_PASSWORD = "AOLPassword"; //$NON-NLS-1$
	String AOL_RECIPIENTS_USERIDS = "RecepientsUserIds"; //$NON-NLS-1$

	// Constants for Custom Notifier
	String CUSTOM_PROGRAM = "CustomProgram"; //$NON-NLS-1$
	
	// Constants for Log Notifier
	String LOGFILE_NAME = "LogFile"; //$NON-NLS-1$
	
	// Constants for Database Notifier
	String DB_DRIVER = "DatabaseDriver"; //$NON-NLS-1$
	String DB_URL = "DatabaseURL"; //$NON-NLS-1$
	String DB_USER = "DatabaseUser"; //$NON-NLS-1$
	String DB_PWD = "DatabasePwd"; //$NON-NLS-1$
	String DB_JAR = "DatabaseJar"; //$NON-NLS-1$
	String DB_TABLE = "DatabaseTable"; //$NON-NLS-1$
	String DB_COLUMN = "DatabaseColumn"; //$NON-NLS-1$
	
	// Constants for SNMP Trap Notifier
	String SNMP_TRAP_HOST = "Host";
	String SNMP_TRAP_VERSION = "SNMP Version";
	String SNMP_TRAP_PORT = "Port";
	String SNMP_TRAP_COMMUNITY = "Community";
	String SNMP_TRAP_V3_AUTH_TYPE = "Authentication (for v3)";
	String SNMP_TRAP_V3_USERNAME = "User Name (for v3)";
	String SNMP_TRAP_V3_PASSWORD = "Authentication Password (for v3)";
	String SNMP_TRAP_V3_PRIVACY_TYPE = "Privacy Type (for v3)";
	String SNMP_TRAP_V3_PRIVACY_PWD = "Privacy Password (for v3)";

	// Constants for MSN Notifier
	String MSN_USERID = "MSNUserId"; //$NON-NLS-1$
	String MSN_PASSWORD = "MSNPassword"; //$NON-NLS-1$
	String MSN_RECIPIENTS_USERIDS = "RecepientsUserIds"; //$NON-NLS-1$
	String CLASS_PATH = "ClassPath"; //$NON-NLS-1$

	// Constants for Custom Carrier Notifiers
	String CUSTOM_CARRIER_LOCATIONS = "CustomCarrierLocations"; //$NON-NLS-1$

	// Constants for Espresso Notifier
	String ES_ENDPOINT = "EspressoEndPoint"; //$NON-NLS-1$
	String ES_APP_ID = "EspressoAppID"; //$NON-NLS-1$
	String ES_PROCESSNAME = "EspressoProcessName"; //$NON-NLS-1$
	String ES_ENVIRONMENT = "EspressoEnvironment"; //$NON-NLS-1$
	String ES_PRIORITY = "EspressoPriority"; //$NON-NLS-1$

	String NOTIF_EMAIL = "Email";

	String NOTIF_SMS = "SMS";

	String NOTIF_LOG = "Log";
}