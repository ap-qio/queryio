/*
 * @(#)  SchedulerConstants.java Mar 28, 2005
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
package com.queryio.scheduler.service;

/**
 * SchedulerConstants
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public interface SchedulerConstants
{
	String USERNAME = "username";
	String NOTIFICATION_TYPE = "NotificationType";
	String NOTIFICATION_MESSAGE = "NotificationMessage";
	String EMAIL_ADDRESS = "EmailAddress";
	String NOTIFICATION_EMAIL = "Email";
	String NOTIFICATION_SMS = "SMS";
	String REPORT_ID = "ReportId";
	int SCH_FREQUENCY_ONCE = 0;
	int SCH_FREQUENCY_TWELVE_HOURS = 1;
	int SCH_FREQUENCY_DAILY = 2;
	int SCH_FREQUENCY_WEEKLY = 3;
	String SCHEDULER_NAME = "QueryIOScheduler";	
	String HIPHEN = "-";
	String TABLE_PREFIX = "QRTZ_";
	String FORMAT_TYPE = "FormatType";
	String FREQUENCY = "frequency";
	String REPORT_TIME =  "reportTime";
	String NODE_ID = "nodeId";
	String QUERY = "query";
	String MAPRED_JOB_NAME = "MAPREDJOBNAME";
	String MAPRED_GROUP_NAME = "MAPRED";
	String NAMENODE = "NAMENODE";
	String NOTIFICATION_ENABLE = "NotficationEnable";
	String STARTTIME = "STARTTIME";
	String ENDTIME = "ENDTIME";
	String STATUS = "STATUS";
	String REASON_FOR_FAILURE = "REASON_FOR_FAILURE";
	String SUCCESS  = "Success";
	String FAILURE  = "Failed";
	String ID = "ID";
	String JOB_NAME = "JOB_NAME";
	String JOB_GROUP = "JOB_GROUP";
	String BACKUP_JOB_NAME = "BACKUPJOBNAME";
	String BACKUP_GROUP_NAME = "BACKUP";
}