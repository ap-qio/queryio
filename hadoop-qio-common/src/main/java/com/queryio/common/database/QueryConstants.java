package com.queryio.common.database;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.ReplicationConstants;

public interface QueryConstants {

	String PREPARED_QRY_INSERT_CREATE_OBJECT_FAILED_TASK = "INSERT INTO " + TableConstants.FAILED_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ',' + ColumnConstants.COL_TASKS_OBJECT_NAME + ','
			+ ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + ") VALUES(?,'" + ReplicationConstants.TASK_CREATE_OBJECT
			+ "',?,?,?)";

	String PREPARED_QRY_GET_REPLICATION_ATTEMPTS = "SELECT " + ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + " FROM "
			+ TableConstants.TABLE_TASKS + " WHERE " + ColumnConstants.COL_TASKS_ID + " = ?";

	String PREPARED_QRY_SET_REPLICATION_ATTEMPTS = "UPDATE " + TableConstants.TABLE_TASKS + " SET "
			+ ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + " = ? WHERE " + ColumnConstants.COL_TASKS_ID + " = ?";

	String PREPARED_QRY_UPDATE_REPLICATION_SETTINGS_IS_INITIAL = "UPDATE " + TableConstants.TABLE_REPLICATION_SETTINGS
			+ " SET " + ColumnConstants.COL_REPLICATION_SETTINGS_IS_INITIAL + "=?";

	String PREPARED_QRY_INSERT_HOST = "INSERT INTO " + TableConstants.TABLE_HOSTS + '(' + ColumnConstants.COL_HOST_IP
			+ ',' + ColumnConstants.COL_HOST_INSTALLDIR + ',' + ColumnConstants.COL_HOST_STATUS + ','
			+ ColumnConstants.COL_HOST_RACKNAME + ',' + ColumnConstants.COL_HOST_AGENTPORT + ','
			+ ColumnConstants.COL_HOST_MONITOR + ',' + ColumnConstants.COL_HOST_IS_WINDOWS + ')'
			+ " VALUES(?,?,?,?,?,?,?)";

	String PREPARED_QRY_INSERT_NODE = "INSERT INTO " + TableConstants.TABLE_NODES + '(' + ColumnConstants.COL_NODE_ID
			+ ',' + ColumnConstants.COL_NODE_HOSTID + ',' + ColumnConstants.COL_NODE_NODETYPE + ','
			+ ColumnConstants.COL_NODE_STATUS + ',' + ColumnConstants.COL_NODE_JMXPORT + ','
			+ ColumnConstants.COL_NODE_SERVICESTATUS + ',' + ColumnConstants.COL_NODE_HIVESERVICESTATUS + ','
			+ ColumnConstants.COL_NODE_MONITOR + ')' + " VALUES(?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_UPDATE_RACKDETAIL = "UPDATE " + TableConstants.TABLE_HOSTS + " SET "
			+ ColumnConstants.COL_HOST_RACKNAME + " = ? WHERE " + ColumnConstants.COL_HOST_ID + " = ?";

	String PREPARED_QRY_INSERT_NODERELATIONS = "INSERT INTO " + TableConstants.TABLE_NODERELATIONS + '('
			+ ColumnConstants.COL_NODERELATIONS_NODEID + ',' + ColumnConstants.COL_NODERELATIONS_NAMENODEID + ')'
			+ " VALUES(?,?)";

	String PREPARED_QRY_UPDATE_NODERELATIONS = "UPDATE " + TableConstants.TABLE_NODERELATIONS + " SET "
			+ ColumnConstants.COL_NODERELATIONS_NAMENODEID + " = ? WHERE " + ColumnConstants.COL_NODERELATIONS_NODEID
			+ " = ?";

	String PREPARED_QRY_GET_NODERELATIONS = "SELECT * FROM " + TableConstants.TABLE_NODERELATIONS + " WHERE "
			+ ColumnConstants.COL_NODERELATIONS_NODEID + "= ?";

	String PREPARED_QRY_GET_ALL_DATANODES = "SELECT * FROM " + TableConstants.TABLE_NODERELATIONS + " WHERE "
			+ ColumnConstants.COL_NODERELATIONS_NAMENODEID + "= ?";

	String PREPARED_QRY_INSERT_USER = "INSERT INTO " + TableConstants.TABLE_USERS + '('
			+ ColumnConstants.COL_USER_USERNAME + ',' + ColumnConstants.COL_USER_FIRSTNAME + ','
			+ ColumnConstants.COL_USER_LASTNAME + ',' + ColumnConstants.COL_USER_PASSWORD + ','
			+ ColumnConstants.COL_USER_EMAIL + ')' + " VALUES(?,?,?,?,?)";

	String PREPARED_QRY_INSERT_USER_ROLE = "INSERT INTO " + TableConstants.TABLE_USER_ROLES + '('
			+ ColumnConstants.COL_USER_USERNAME + ',' + ColumnConstants.COL_USER_ROLES_ROLENAME + ')' + " VALUES(?,?)";

	String PREPARED_QRY_INSERT_USER_GROUP = "INSERT INTO " + TableConstants.TABLE_USER_GROUPS + '('
			+ ColumnConstants.COL_USER_GROUPS_GROUPID + ',' + ColumnConstants.COL_USER_GROUPS_USERID + ')'
			+ " VALUES(?,?)";

	String PREPARED_QRY_DELETE_ROLE = "DELETE FROM " + TableConstants.TABLE_USER_ROLES + " WHERE "
			+ ColumnConstants.COL_USER_USERNAME + " = ?";

	String PREPARED_QRY_GET_ROLE = "SELECT " + ColumnConstants.COL_USER_ROLES_ROLENAME + " FROM "
			+ TableConstants.TABLE_USER_ROLES + " WHERE " + ColumnConstants.COL_USER_USERNAME + " = ?";

	String PREPARED_QRY_UPDATE_USER = "UPDATE " + TableConstants.TABLE_USERS + " SET "
			+ ColumnConstants.COL_USER_USERNAME + "=?, " + ColumnConstants.COL_USER_FIRSTNAME + "=?, "
			+ ColumnConstants.COL_USER_LASTNAME + "=?, " + ColumnConstants.COL_USER_EMAIL + "=? WHERE "
			+ ColumnConstants.COL_USER_ID + "=? ";

	String PREPARED_QRY_GET_PASSWORD = "SELECT " + ColumnConstants.COL_USER_PASSWORD + " FROM "
			+ TableConstants.TABLE_USERS + " WHERE " + ColumnConstants.COL_USER_ID + "=?";

	String PREPARED_QRY_UPDATE_PASSWORD = "UPDATE " + TableConstants.TABLE_USERS + " SET "
			+ ColumnConstants.COL_USER_PASSWORD + "=? WHERE " + ColumnConstants.COL_USER_ID + "=?";

	String QRY_GET_ALL_HOSTSINFO = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " ORDER BY "
			+ ColumnConstants.COL_HOST_RACKNAME + "," + ColumnConstants.COL_HOST_IP;

	String QRY_GET_HOST_ID = "SELECT " + ColumnConstants.COL_HOST_ID + " FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_IP + " =?";

	String QRY_GET_ALL_USERSINFO = "SELECT * FROM " + TableConstants.TABLE_USERS;

	String QRY_GET_ALL_DBNAME_MODE_FOR_OPERATION = "SELECT * FROM " + TableConstants.TABLE_DB_MIGRATION_STATUS;

	String QRY_GET_ALL_NODESFORHOST = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? ";

	String QRY_GET_ALL_NODES_TYPE_FORHOST = "SELECT " + ColumnConstants.COL_NODE_NODETYPE + " FROM "
			+ TableConstants.TABLE_NODES + " WHERE " + ColumnConstants.COL_NODE_HOSTID + " = ? ";

	String QRY_GET_ALL_DATA_NODESFORHOST = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? AND " + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.DATANODE + "'";

	String QRY_GET_ALL_RESOURCE_MANAGERS_FORHOST = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? AND " + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.RESOURCEMANAGER + "' ORDER BY " + ColumnConstants.COL_NODE_ID;

	String QRY_GET_ALL_NODE_MANAGERS_FORHOST = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? AND " + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.NODEMANAGER + "' ORDER BY " + ColumnConstants.COL_NODE_ID;

	String QRY_GET_ALL_NAME_NODESFORHOST = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? AND " + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.NAMENODE + "'";

	String QRY_GET_ALL_NODESFORTYPE = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = ? ";

	String QRY_GET_ALL_NODESFORTYPE_COUNT = "SELECT COUNT(*) FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = ? ";

	String QRY_GET_ALL_DECOMISSIONED = "SELECT * FROM " + TableConstants.TABLE_NODES + " A JOIN "
			+ TableConstants.TABLE_DECOMMISSIONNODES + " B ON A." + ColumnConstants.COL_NODE_ID + " = B."
			+ ColumnConstants.COL_DECOMMISSIONNODES_NODEID + " WHERE A." + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.DATANODE + "' ";

	String QRY_GET_ALL_DATANODES_STARTED = "SELECT COUNT(*) FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = '" + QueryIOConstants.DATANODE + "' AND "
			+ ColumnConstants.COL_NODE_STATUS + " = '" + QueryIOConstants.STATUS_STARTED + "'";

	String QRY_GET_ALL_NAMENODES = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = '" + QueryIOConstants.NAMENODE + "' ORDER BY "
			+ ColumnConstants.COL_NODE_ID;

	String QRY_GET_ALL_NAMENODES_ID = "SELECT " + ColumnConstants.COL_NODE_ID + " FROM " + TableConstants.TABLE_NODES
			+ " WHERE " + ColumnConstants.COL_NODE_NODETYPE + " = '" + QueryIOConstants.NAMENODE + "' ORDER BY "
			+ ColumnConstants.COL_NODE_ID;

	String QRY_GET_COUNT_ALL_NODESFORTYPE = "SELECT COUNT(*) FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = ? ";

	String QRY_GET_ALL_VOLUMES_FOR_NODEID = "SELECT * FROM " + TableConstants.TABLE_VOLUMES + " WHERE "
			+ ColumnConstants.COL_VOLUMES_NODEID + " = ? ";

	String QRY_GET_ALL_VOLUMES_COUNT_FOR_NODEID = "SELECT COUNT(*) FROM " + TableConstants.TABLE_VOLUMES + " WHERE "
			+ ColumnConstants.COL_VOLUMES_NODEID + " = ? ";

	String QRY_GET_NODE_HOST_TYPE = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_HOSTID + " = ? AND " + ColumnConstants.COL_NODE_NODETYPE + " = ?";

	String PREPARED_QRY_SEARCH_HOST = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_IP + "=?";

	String QRY_GET_ALL_HOSTNAMES = "SELECT " + ColumnConstants.COL_HOST_IP + " FROM " + TableConstants.TABLE_HOSTS;

	String PREPARED_QRY_DELETE_HOST = "DELETE FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_IP + " = ?";

	String PREPARED_QRY_DELETE_NODE = "DELETE FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_ID + " = ?";

	String PREPARED_QRY_DELETE_USER = "DELETE FROM " + TableConstants.TABLE_USERS + " WHERE "
			+ ColumnConstants.COL_USER_ID + " = ?";

	String QRY_GET_NAMENODE_HOST = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_ID + " IN " + "(SELECT " + ColumnConstants.COL_NODE_HOSTID + " FROM "
			+ TableConstants.TABLE_NODES + " WHERE " + ColumnConstants.COL_NODE_NODETYPE + " = '"
			+ QueryIOConstants.NAMENODE + "')";

	String QRY_GET_ALL_NAMENODE = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE " + TableConstants.TABLE_HOSTS
			+ "." + ColumnConstants.COL_HOST_ID + " IN ( SELECT " + TableConstants.TABLE_NODES + "."
			+ ColumnConstants.COL_NODE_HOSTID + " FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ TableConstants.TABLE_NODES + "." + ColumnConstants.COL_NODE_NODETYPE + "='" + QueryIOConstants.NAMENODE
			+ "')";

	String QRY_GET_NAMENODE_TYPE_FOR_HOST = "SELECT " + TableConstants.TABLE_NODES + "."
			+ ColumnConstants.COL_NODE_NODETYPE + " FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ TableConstants.TABLE_NODES + "." + ColumnConstants.COL_NODE_HOSTID + " = ? AND "
			+ TableConstants.TABLE_NODES + "." + ColumnConstants.COL_NODE_NODETYPE + "= '" + QueryIOConstants.NAMENODE
			+ "'";

	String QRY_GET_DATANODE = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE " + ColumnConstants.COL_HOST_ID
			+ " IN " + "(SELECT " + ColumnConstants.COL_NODE_HOSTID + " FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " = '" + QueryIOConstants.DATANODE + "')";

	String PREPARED_QRY_GET_HOST_ID = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_ID + "=?";

	String PREPARED_QRY_GET_HOST_ID_FROM_NAMENODEID = "SELECT A." + ColumnConstants.COL_HOST_IP + " FROM "
			+ TableConstants.TABLE_HOSTS + " A JOIN " + TableConstants.TABLE_NODES + " B ON A."
			+ ColumnConstants.COL_HOST_ID + "=B." + ColumnConstants.COL_NODE_HOSTID + " WHERE B."
			+ ColumnConstants.COL_NODE_ID + "=?";

	String PREPARED_QRY_GET_HOST_IP = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_IP + "=?";

	String PREPARED_QRY_GET_HOST_RACKNAME = "SELECT * FROM " + TableConstants.TABLE_HOSTS + " WHERE "
			+ ColumnConstants.COL_HOST_RACKNAME + "=?";

	String PREPARED_QRY_GET_USER = "SELECT * FROM " + TableConstants.TABLE_USERS + " WHERE "
			+ ColumnConstants.COL_USER_ID + "=?";

	String PREPARED_QRY_GET_USER_FROM_USERNAME = "SELECT * FROM " + TableConstants.TABLE_USERS + " WHERE "
			+ ColumnConstants.COL_USER_USERNAME + "=?";

	String PREPARED_QRY_GET_USER_WITH_USERNAME_PASSWORD = "SELECT * FROM " + TableConstants.TABLE_USERS + " WHERE "
			+ ColumnConstants.COL_USER_USERNAME + "=? AND " + ColumnConstants.COL_USER_PASSWORD + "=?";

	String PREPARED_QRY_UPDATE_STATUS_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_STATUS + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_UPDATE_SERVICESTATUS_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_SERVICESTATUS + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_UPDATE_HIVESERVICESTATUS_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_HIVESERVICESTATUS + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_UPDATE_MONITOR_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_MONITOR + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_NODETYPE_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_NODETYPE + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_JMXPORT_NODE = "UPDATE " + TableConstants.TABLE_NODES + " SET "
			+ ColumnConstants.COL_NODE_JMXPORT + " = ? WHERE " + ColumnConstants.COL_NODE_ID + " = ? ";

	String PREPARED_QRY_UPDATE_STATUS_HOST = "UPDATE " + TableConstants.TABLE_HOSTS + " SET "
			+ ColumnConstants.COL_HOST_STATUS + " = ? WHERE " + ColumnConstants.COL_HOST_ID + " = ? ";

	String PREPARED_QRY_UPDATE_MONITOR_HOST = "UPDATE " + TableConstants.TABLE_HOSTS + " SET "
			+ ColumnConstants.COL_HOST_MONITOR + " = ? WHERE " + ColumnConstants.COL_HOST_ID + " = ? ";

	String QRY_GET_NODE = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE " + ColumnConstants.COL_NODE_ID
			+ " = ?";

	String QRY_GET_NAMENODE = "SELECT * FROM " + TableConstants.TABLE_NODES + " WHERE "
			+ ColumnConstants.COL_NODE_NODETYPE + " ='" + QueryIOConstants.NAMENODE + "'";

	String QRY_GET_ALL_NODES_NODEID = "SELECT " + ColumnConstants.COL_NODE_ID + " FROM " + TableConstants.TABLE_NODES;

	String QRY_GET_NAMENODE_SYSTEM_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_NAMENODE_SYSTEM_ATTRIBUTES;
	String QRY_GET_DATANODE_SYSTEM_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_DATANODE_SYSTEM_ATTRIBUTES;
	String QRY_GET_HOST_SYSTEM_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_HOST_SYSTEM_ATTRIBUTES;

	String QRY_GET_RESOURCE_MANAGER_SYSTEM_ATTRIBUES = "SELECT * FROM "
			+ TableConstants.TABLE_RESOURCE_MANAGER_SYSTEM_ATTRIBUES;
	String QRY_GET_NODE_MANAGER_SYSTEM_ATTRIBUES = "SELECT * FROM "
			+ TableConstants.TABLE_NODE_MANAGER_SYSTEM_ATTRIBUES;

	String QRY_GET_NAMENODE_LIVE_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_NAMENODE_LIVE_ATTRIBUTES;
	String QRY_GET_DATANODE_LIVE_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES;
	String QRY_GET_NODE_MANAGER_LIVE_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_NODE_MANAGER_LIVE_ATTRIBUES;
	String QRY_GET_RESOURCE_MANAGER_LIVE_ATTRIBUTES = "SELECT * FROM "
			+ TableConstants.TABLE_RESOURCE_MANAGER_LIVE_ATTRIBUTES;
	String QRY_GET_HOST_LIVE_ATTRIBUTES = "SELECT * FROM " + TableConstants.TABLE_HOST_LIVE_ATTRIBUTES;

	String QRY_SELECT_ALL_FROM = "SELECT * FROM ";
	String QRY_DROP_TABLE = "DROP TABLE ";
	String QRY_CREATE_CACHED_TABLE = "CREATE TABLE ";

	String BRACKET_START = " (";
	String BRACKET_END = ") ";
	String COMMA = ", ";
	String SPACE = " ";

	String QRY_INSERT_VOLUME = "INSERT INTO " + TableConstants.TABLE_VOLUMES + " (" + ColumnConstants.COL_VOLUMES_NODEID
			+ ", " + ColumnConstants.COL_VOLUMES_DISK + ", " + ColumnConstants.COL_VOLUMES_PATH + ") VALUES (?,?,?)";

	String QRY_UPDATE_VOLUME_DISK = "UPDATE " + TableConstants.TABLE_VOLUMES + " SET "
			+ ColumnConstants.COL_VOLUMES_DISK + " = ? WHERE " + ColumnConstants.COL_VOLUMES_NODEID + " = ?";
	String QRY_UPDATE_VOLUME_PATH = "UPDATE " + TableConstants.TABLE_VOLUMES + " SET "
			+ ColumnConstants.COL_VOLUMES_PATH + " = ? WHERE " + ColumnConstants.COL_VOLUMES_NODEID + " = ?";

	String QRY_DELETE_VOLUME_FOR_NODEID = "DELETE FROM " + TableConstants.TABLE_VOLUMES + " WHERE "
			+ ColumnConstants.COL_VOLUMES_NODEID + " = ? ";

	String QRY_DELETE_VOLUME_FOR_NODEID_PATH = "DELETE FROM " + TableConstants.TABLE_VOLUMES + " WHERE "
			+ ColumnConstants.COL_VOLUMES_NODEID + " = ? " + " AND " + ColumnConstants.COL_VOLUMES_PATH + " = ? ";

	String QRY_INSERT_DISKMONITOREDDATA = "INSERT INTO " + TableConstants.TABLE_DISKMONITOREDDATA + " ("
			+ ColumnConstants.COL_DISKMONITOREDDATA_HOSTID + ", " + ColumnConstants.COL_DISKMONITOREDDATA_DISKNAME
			+ ", " + ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEREADSPERSEC + ", "
			+ ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEWRITESPERSEC + ", "
			+ ColumnConstants.COL_DISKMONITOREDDATA_DISKHEALTHSTATUS + ") VALUES (?,?,?,?,?)";

	String QRY_SELECT_DISKMONITOREDDATA = "SELECT * FROM " + TableConstants.TABLE_DISKMONITOREDDATA + " WHERE "
			+ ColumnConstants.COL_DISKMONITOREDDATA_HOSTID + " = ?";

	String QRY_SELECT_DISKMONITOREDDATA_DISK = "SELECT * FROM " + TableConstants.TABLE_DISKMONITOREDDATA + " WHERE "
			+ ColumnConstants.COL_DISKMONITOREDDATA_HOSTID + " = ?" + " AND "
			+ ColumnConstants.COL_DISKMONITOREDDATA_DISKNAME + " = ?";

	String QRY_DELETE_DISKMONITOREDDATA = "DELETE FROM " + TableConstants.TABLE_DISKMONITOREDDATA + " WHERE "
			+ ColumnConstants.COL_DISKMONITOREDDATA_HOSTID + " = ? " + "AND "
			+ ColumnConstants.COL_DISKMONITOREDDATA_DISKNAME + " = ?";

	String PREPARED_QRY_INSERT_NAMENODE_SYSTEM_ATTRIBUTES = "INSERT INTO NAMENODE_SYSTEM_ATTRIBUTES VALUES(?,?,?,?,?)";

	String QRY_SELECT_ALL_FROM_HOST_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_HOST_SYSTEM_ATTRIBUTES + " WHERE COLUMNNAME IN";

	String QRY_SELECT_ALL_FROM_NAMENODE_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_NAMENODE_SYSTEM_ATTRIBUTES + " WHERE COLUMNNAME IN";
	String QRY_SELECT_ALL_FROM_DATANODE_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_DATANODE_SYSTEM_ATTRIBUTES + " WHERE COLUMNNAME IN";

	String QRY_SELECT_ALL_FROM_RESOURCE_MANAGER_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_RESOURCE_MANAGER_SYSTEM_ATTRIBUES + " WHERE COLUMNNAME IN";
	String QRY_SELECT_ALL_FROM_NODE_MANAGER_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_NODE_MANAGER_SYSTEM_ATTRIBUES + " WHERE COLUMNNAME IN";

	String QRY_SELECT_ALL_FROM_NAMENODE_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_NAMENODE_LIVE_ATTRIBUTES + " WHERE COLUMNNAME IN";
	String QRY_SELECT_ALL_FROM_DATANODE_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES + " WHERE COLUMNNAME IN";

	String QRY_SELECT_ALL_FROM_RESOURCE_MANAGER_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_RESOURCE_MANAGER_LIVE_ATTRIBUTES + " WHERE COLUMNNAME IN";
	String QRY_SELECT_ALL_FROM_NODE_MANAGER_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN = "SELECT * FROM "
			+ TableConstants.TABLE_NODE_MANAGER_LIVE_ATTRIBUES + " WHERE COLUMNNAME IN";

	String QRY_SELECT_ALL_FROM_NAMENODE_LIVE_ATTRIBUTES = QRY_SELECT_ALL_FROM
			+ TableConstants.TABLE_NAMENODE_LIVE_ATTRIBUTES;
	String QRY_SELECT_ALL_FROM_DATANODE_LIVE_ATTRIBUTES = QRY_SELECT_ALL_FROM
			+ TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES;
	String QRY_SELECT_ALL_FROM_JOBTRACKER_LIVE_ATTRIBUTES = QRY_SELECT_ALL_FROM
			+ TableConstants.TABLE_JOBTRACKER_LIVE_ATTRIBUTES;
	String QRY_SELECT_ALL_FROM_TASKTRACKER_LIVE_ATTRIBUTES = QRY_SELECT_ALL_FROM
			+ TableConstants.TABLE_TASKTRACKER_LIVE_ATTRIBUTES;

	String QRY_GET_ALL_NOTIFICATIONSETTINGS = "SELECT * FROM " + TableConstants.TABLE_NOTIFICATIONSETTINGS;

	String PREPARED_QRY_UPDATE_NOTIFICATIONSETTINGS = "INSERT INTO " + TableConstants.TABLE_NOTIFICATIONSETTINGS + "("
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_ENABLED + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERNAME + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERADD + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SECUREDPROTOCOL + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPSERVER + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPPORT + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_AUTHREQUIRED + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_USERNAME + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_PASSWORD + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_ENABLED + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_NUMBER + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SERIALPORT + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MANUFACTURER + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MODEL + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SELECTEDMODEL + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_BAUDRATE + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_ENABLED + ","
			+ ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_FILEPATH + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_UPDATE_ATTRIBUTESTATE = "UPDATE " + TableConstants.TABLE_ATTRIBUTESTATE + " SET "
			+ ColumnConstants.COL_ATTRIBUTESTATE_ENDTIME + "=? WHERE " + ColumnConstants.COL_ATTRIBUTESTATE_NODEID
			+ "=? AND " + ColumnConstants.COL_ATTRIBUTESTATE_ATTRIBUTENAME + "=?";

	String QRY_GET_ALL_RULEIDS = "SELECT " + ColumnConstants.COL_RULES_RULEID + " FROM " + TableConstants.TABLE_RULES;

	String PREPARED_QRY_INSERT_RULE = "INSERT INTO " + TableConstants.TABLE_RULES + '('
			+ ColumnConstants.COL_RULES_RULEID + ',' + ColumnConstants.COL_RULES_NODEID + ','
			+ ColumnConstants.COL_RULES_SEVERITY + ',' + ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG + ','
			+ ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB + ',' + ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG + ','
			+ ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB + ',' + ColumnConstants.COL_RULES_IGNORERULE + ','
			+ ColumnConstants.COL_RULES_NOTIFICATIONTYPE + ") VALUES(?,?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_INSERT_RULEEXPRESSION = "INSERT INTO " + TableConstants.TABLE_RULEEXPRESSIONS + '('
			+ ColumnConstants.COL_RULEEXPRESSIONS_RULEID + ',' + ColumnConstants.COL_RULEEXPRESSIONS_CONDITION + ','
			+ ColumnConstants.COL_RULEEXPRESSIONS_VALUE + ',' + ColumnConstants.COL_RULEEXPRESSIONS_STARTTIME + ','
			+ ColumnConstants.COL_RULEEXPRESSIONS_ENDTIME + ',' + ColumnConstants.COL_RULEEXPRESSIONS_DURATION + ','
			+ ColumnConstants.COL_RULEEXPRESSIONS_ATTRIBUTENAME + ',' + ColumnConstants.COL_RULEEXPRESSIONS_NODEID + ')'
			+ " VALUES(?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_SELECT_RULE = "SELECT * FROM " + TableConstants.TABLE_RULES + " WHERE "
			+ ColumnConstants.COL_ALERTS_RULEID + "=?";

	String PREPARED_QUERY_SELECT_RULE_EXPRESSIONS = "SELECT * FROM " + TableConstants.TABLE_RULEEXPRESSIONS + " WHERE "
			+ ColumnConstants.COL_RULEEXPRESSIONS_RULEID + "=?";

	String PREPARED_QRY_UPDATE_RULE = "UPDATE " + TableConstants.TABLE_RULES + " SET "
			+ ColumnConstants.COL_RULES_NODEID + "=?, " + ColumnConstants.COL_RULES_SEVERITY + "=?, "
			+ ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG + "=?, " + ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB
			+ "=?, " + ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG + "=?, "
			+ ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB + "=?, " + ColumnConstants.COL_RULES_IGNORERULE + "=?,"
			+ ColumnConstants.COL_RULES_NOTIFICATIONTYPE + "=?" + " WHERE " + ColumnConstants.COL_RULES_RULEID + "=?";

	String PREPARED_QRY_SUSPEND_RULE = "UPDATE " + TableConstants.TABLE_RULES + " SET "
			+ ColumnConstants.COL_RULES_IGNORERULE + "=true WHERE " + ColumnConstants.COL_RULES_RULEID + "=?";

	String PREPARED_QRY_RESTART_RULE = "UPDATE " + TableConstants.TABLE_RULES + " SET "
			+ ColumnConstants.COL_RULES_IGNORERULE + "=false WHERE " + ColumnConstants.COL_RULES_RULEID + "=?";

	String PREPARED_QRY_DELETE_RULEEXPRESSIONS = "DELETE FROM " + TableConstants.TABLE_RULEEXPRESSIONS + " WHERE "
			+ ColumnConstants.COL_RULEEXPRESSIONS_RULEID + "=?";

	String QRY_RULES_LIST = "SELECT * FROM " + TableConstants.TABLE_RULES;

	String PREPARED_QRY_RULE = "SELECT * FROM " + TableConstants.TABLE_RULES + " WHERE "
			+ ColumnConstants.COL_ALERTS_RULEID + "=?";

	String PREPARED_QRY_ISACTIVEALERTPRESENT_FORRULE = "SELECT " + ColumnConstants.COL_ALERTS_RULEID + " FROM "
			+ TableConstants.TABLE_ALERTS + " WHERE " + ColumnConstants.COL_ALERTS_RULEID + "=?";

	String PREPARED_QRY_GETCONROLLERID_FORRULE = "SELECT " + ColumnConstants.COL_RULES_NODEID + ", "
			+ ColumnConstants.COL_RULES_SEVERITY + " FROM " + TableConstants.TABLE_RULES + " WHERE "
			+ ColumnConstants.COL_RULES_RULEID + "=?";

	String PREPARED_QRY_SELECT_ATTRIBUTESTATE = "SELECT " + ColumnConstants.COL_ATTRIBUTESTATE_ATTRIBUTENAME + " FROM "
			+ TableConstants.TABLE_ATTRIBUTESTATE + " WHERE " + ColumnConstants.COL_ATTRIBUTESTATE_NODEID + "= ? AND "
			+ ColumnConstants.COL_ATTRIBUTESTATE_ENDTIME + " IS NULL";

	String PREPARED_QRY_DELETE_RULE = "DELETE FROM " + TableConstants.TABLE_RULES + " WHERE "
			+ ColumnConstants.COL_RULES_RULEID + "=?";

	String PREPARED_QRY_ALERT_LIST = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " > ? ORDER BY " + ColumnConstants.COL_ALERTS_STARTTIME;

	String PREPARED_QRY_ACTIVE_ALERT_LIST_BEFORE_TIME = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " < ? AND " + ColumnConstants.COL_ALERTS_NODEID + " = ? ORDER BY "
			+ ColumnConstants.COL_ALERTS_STARTTIME;

	String PREPARED_QRY_ALERT_LIST_FOR_NODE = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " > ? AND " + ColumnConstants.COL_ALERTS_NODEID + "=? ORDER BY "
			+ ColumnConstants.COL_ALERTS_STARTTIME;

	String PREPARED_QRY_ALERT_LIST_FOR_NODE_BETWEEN = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " > ? AND " + ColumnConstants.COL_ALERTS_STARTTIME + " < ? AND "
			+ ColumnConstants.COL_ALERTS_NODEID + "=? ORDER BY " + ColumnConstants.COL_ALERTS_STARTTIME;

	String PREPARED_QRY_ALERT_COUNT_FOR_NODE = "SELECT COUNT(*) FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_NODEID + "=?";

	String PREPARED_QRY_ALERT_LIST_FOR_RULE = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " > ? AND " + ColumnConstants.COL_ALERTS_RULEID + "=? ORDER BY "
			+ ColumnConstants.COL_ALERTS_STARTTIME;

	String QRY_ALERT_ATTRIBUTES = "SELECT " + TableConstants.TABLE_RULES + '.' + ColumnConstants.COL_RULES_NODEID + ','
			+ ColumnConstants.COL_RULEEXPRESSIONS_ATTRIBUTENAME + " FROM " + TableConstants.TABLE_RULES + ','
			+ TableConstants.TABLE_RULEEXPRESSIONS + ',' + TableConstants.TABLE_ALERTS + " WHERE "
			+ TableConstants.TABLE_ALERTS + '.' + ColumnConstants.COL_ALERTS_NODEID + '=' + TableConstants.TABLE_RULES
			+ '.' + ColumnConstants.COL_RULES_NODEID + " AND " + TableConstants.TABLE_RULES + '.'
			+ ColumnConstants.COL_RULES_RULEID + '=' + TableConstants.TABLE_RULEEXPRESSIONS + '.'
			+ ColumnConstants.COL_RULEEXPRESSIONS_RULEID + " AND " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL";

	String PREPARED_QRY_ALERT_ATTRIBUTE_TIMES = "SELECT " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + ',' + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + " FROM " + TableConstants.TABLE_ALERTS + ','
			+ TableConstants.TABLE_ALERTATTRIBUTES + " WHERE " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_NODEID + '=' + TableConstants.TABLE_ALERTATTRIBUTES + '.'
			+ ColumnConstants.COL_ALERTATTRIBUTES_NODEID + " AND " + TableConstants.TABLE_ALERTATTRIBUTES + '.'
			+ ColumnConstants.COL_ALERTATTRIBUTES_RULEID + '=' + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_RULEID + " AND " + TableConstants.TABLE_ALERTATTRIBUTES + '.'
			+ ColumnConstants.COL_ALERTATTRIBUTES_ALERTTIME + '=' + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + " AND " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_NODEID + "=? AND " + TableConstants.TABLE_ALERTATTRIBUTES + '.'
			+ ColumnConstants.COL_ALERTATTRIBUTES_ATTRIBUTENAME + "=? AND ((" + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + "<=? AND ? <= " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + ")" + "OR (" + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + " <=? AND ? <= " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + ")  OR (" + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL AND ? >= " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + ") OR (" + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + " >= ? AND  " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_ENDTIME + " <= ?))" + " ORDER BY " + TableConstants.TABLE_ALERTS + '.'
			+ ColumnConstants.COL_ALERTS_STARTTIME + " ASC";

	String PREPARED_QRY_ALERT_CONTROLLER_TIMES = "SELECT " + ColumnConstants.COL_ALERTS_STARTTIME + ','
			+ ColumnConstants.COL_ALERTS_ENDTIME + " FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_NODEID + "=? AND " + ColumnConstants.COL_ALERTS_STARTTIME + ">=? AND ("
			+ ColumnConstants.COL_ALERTS_ENDTIME + "<=? OR " + ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL)";

	String PREPARED_QRY_RESET_ALERT = "UPDATE " + TableConstants.TABLE_ALERTS + " SET "
			+ ColumnConstants.COL_ALERTS_ENDTIME + "=? WHERE " + ColumnConstants.COL_ALERTS_RULEID + "=? AND "
			+ ColumnConstants.COL_ALERTS_NODEID + "=? AND " + ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL";

	String GET_CONTROLLER_RULES = "SELECT * FROM " + TableConstants.TABLE_RULES + " WHERE "
			+ ColumnConstants.COL_RULES_NODEID + "=?";

	String GET_RULE_EXPRESSIONS = "SELECT * FROM " + TableConstants.TABLE_RULEEXPRESSIONS + " WHERE "
			+ ColumnConstants.COL_RULEEXPRESSIONS_RULEID + "=?";

	String GET_ALL_ACTIVE_ALERT_RULEIDS = "SELECT " + ColumnConstants.COL_ALERTS_RULEID + " FROM "
			+ TableConstants.TABLE_ALERTS + " WHERE " + ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL";

	String GET_ACTIVE_ALERT_RULEIDS = "SELECT " + ColumnConstants.COL_ALERTS_RULEID + " FROM "
			+ TableConstants.TABLE_ALERTS + " WHERE " + ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL AND "
			+ ColumnConstants.COL_ALERTS_NODEID + "=?";

	String PREPARED_QRY_VIOLATEDRULEWITHTHESEVERITYEXISTFORCONTROLLER = "SELECT " + ColumnConstants.COL_ALERTS_RULEID
			+ " FROM " + TableConstants.TABLE_ALERTS + " WHERE " + ColumnConstants.COL_ALERTS_SEVERITY + "=? AND "
			+ ColumnConstants.COL_ALERTS_NODEID + "=? AND " + ColumnConstants.COL_ALERTS_RULEID + "!=? AND "
			+ ColumnConstants.COL_ALERTS_ENDTIME + " IS NULL";

	String PREPARED_QRY_INSERT_ALERT = "INSERT INTO " + TableConstants.TABLE_ALERTS + "("
			+ ColumnConstants.COL_ALERTS_RULEID + ", " + ColumnConstants.COL_ALERTS_NODEID + ", "
			+ ColumnConstants.COL_ALERTS_SEVERITY + ", " + ColumnConstants.COL_ALERTS_STARTTIME + ") VALUES(?,?,?,?)";

	String PREPARED_QRY_INSERT_ALERTATTRIBUTES = "INSERT INTO " + TableConstants.TABLE_ALERTATTRIBUTES + "("
			+ ColumnConstants.COL_ALERTATTRIBUTES_RULEID + ", " + ColumnConstants.COL_ALERTATTRIBUTES_NODEID + ", "
			+ ColumnConstants.COL_ALERTATTRIBUTES_ALERTTIME + ", " + ColumnConstants.COL_ALERTATTRIBUTES_ATTRIBUTENAME
			+ ") VALUES(?,?,?,?)";

	String PREPARED_QRY_RESET_ALLALERTS = "UPDATE ALERTS SET ENDTIME=? WHERE ENDTIME IS NULL";

	String PREPARED_QRY_INSERT_ATTRIBUTESTATE = "INSERT INTO " + TableConstants.TABLE_ATTRIBUTESTATE + '('
			+ ColumnConstants.COL_ATTRIBUTESTATE_STATE + ',' + ColumnConstants.COL_ATTRIBUTESTATE_ATTRIBUTENAME + ','
			+ ColumnConstants.COL_ATTRIBUTESTATE_NODEID + ',' + ColumnConstants.COL_ATTRIBUTESTATE_STARTTIME
			+ ") VALUES(?,?,?,?)";

	String PREPARED_QRY_DELETE_ALERT_ATTRIBUTE = "DELETE FROM " + TableConstants.TABLE_ALERTATTRIBUTES + " WHERE "
			+ ColumnConstants.COL_ALERTATTRIBUTES_RULEID + " = ? AND " + ColumnConstants.COL_ALERTATTRIBUTES_ALERTTIME
			+ " >= ? AND " + ColumnConstants.COL_ALERTATTRIBUTES_ALERTTIME + " <= ?";

	String PREPARED_QRY_DELETE_ALERT = "DELETE FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_RULEID + " = ? AND " + ColumnConstants.COL_ALERTS_STARTTIME + " >= ? AND "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " <= ? ";

	String PREPARED_QRY_INSERT_HADOOPSERVICE = "INSERT INTO " + TableConstants.TABLE_HADOOPSERVICES + "("
			+ ColumnConstants.COL_HADOOPSERVICES_NODEID + ", " + ColumnConstants.COL_HADOOPSERVICES_TIMEOFCALL + ", "
			+ ColumnConstants.COL_HADOOPSERVICES_TYPE + ", " + ColumnConstants.COL_HADOOPSERVICES_STATUS + ", "
			+ ColumnConstants.COL_HADOOPSERVICES_OUTPUTFILEPATH + ") VALUES(?,?,?,?,?)";

	String PREPARED_QRY_INSERT_MIGRATIONINFO = "INSERT INTO " + TableConstants.TABLE_MIGRATIONINFO + "("
			+ ColumnConstants.COL_MIGRATIONINFO_NAMENODEID + ", " + ColumnConstants.COL_MIGRATIONINFO_ISIMPORTTYPE
			+ ", " + ColumnConstants.COL_MIGRATIONINFO_TITLE + ", " + ColumnConstants.COL_MIGRATIONINFO_STARTTIME + ", "
			+ ColumnConstants.COL_MIGRATIONINFO_ENDTIME + ", " + ColumnConstants.COL_MIGRATIONINFO_DATASTORE + ", "
			+ ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH + ", " + ColumnConstants.COL_MIGRATIONINFO_SOURCEPATH
			+ ", " + ColumnConstants.COL_MIGRATIONINFO_STATUS + ", " + ColumnConstants.COL_MIGRATIONINFO_PROGRESS + ", "
			+ ColumnConstants.COL_MIGRATIONINFO_ISSECURE + ", " + ColumnConstants.COL_MIGRATIONINFO_UNZIP + ", "
			+ ColumnConstants.COL_MIGRATIONINFO_COMPRESSION_TYPE + ", "
			+ ColumnConstants.COL_MIGRATIONINFO_ENCRYPTION_TYPE + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_SELECT_MIGRATIONINFO_ID = "SELECT * FROM " + TableConstants.TABLE_MIGRATIONINFO + " WHERE "
			+ ColumnConstants.COL_MIGRATIONINFO_ID + " = ?";

	String PREPARED_QRY_SELECT_MIGRATIONINFO_TITLE = "SELECT * FROM MIGRATIONSTATUS  WHERE TITLE=?";

	String PREPARED_QRY_SELECT_MIGRATIONINFO_ON_NAMENODE = "SELECT * FROM " + TableConstants.TABLE_MIGRATIONINFO
			+ "  WHERE " + ColumnConstants.COL_MIGRATIONINFO_TITLE + " = ? AND "
			+ ColumnConstants.COL_MIGRATIONINFO_NAMENODEID + " = ? AND "
			+ ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH + " = ?";

	String PREPARED_QRY_SELECT_MIGRATIONINFO = "SELECT * FROM " + TableConstants.TABLE_MIGRATIONINFO + " WHERE "
			+ ColumnConstants.COL_MIGRATIONINFO_STARTTIME + " = ?";

	String PREPARED_QRY_UPDATE_HADOOPSERVICE = "UPDATE " + TableConstants.TABLE_HADOOPSERVICES + " SET "
			+ ColumnConstants.COL_HADOOPSERVICES_STATUS + " = ?, " + ColumnConstants.COL_HADOOPSERVICES_OUTPUTFILEPATH
			+ " = ? " + "WHERE " + ColumnConstants.COL_HADOOPSERVICES_NODEID + " = ? AND "
			+ ColumnConstants.COL_HADOOPSERVICES_TIMEOFCALL + " = ? " + "AND " + ColumnConstants.COL_HADOOPSERVICES_TYPE
			+ " = ? ";

	String PREPARED_QRY_UPDATE_MIGRATIONSTATUS = "UPDATE " + TableConstants.TABLE_MIGRATIONINFO + " SET "
			+ ColumnConstants.COL_MIGRATIONINFO_PROGRESS + " = ?, " + ColumnConstants.COL_MIGRATIONINFO_STATUS
			+ " = ?, " + ColumnConstants.COL_MIGRATIONINFO_STARTTIME + " = ?, "
			+ ColumnConstants.COL_MIGRATIONINFO_ENDTIME + " = ? " + "WHERE " + ColumnConstants.COL_MIGRATIONINFO_ID
			+ " = ? ";

	String PREPARED_QRY_ALERT_LIST_IN_BETWEEN = "SELECT * FROM " + TableConstants.TABLE_ALERTS + " WHERE "
			+ ColumnConstants.COL_ALERTS_STARTTIME + " >= ? AND " + ColumnConstants.COL_ALERTS_ENDTIME
			+ " <= ? ORDER BY " + ColumnConstants.COL_ALERTS_STARTTIME;

	String PREPARED_QRY_INSERT_NODE_STATUS = "INSERT INTO " + TableConstants.TABLE_NODESTATUS + " VALUES (?,?,?)";
	String PREPARED_QRY_DELETE_NODE_STATUS = "DELETE FROM " + TableConstants.TABLE_NODESTATUS + " WHERE "
			+ ColumnConstants.COL_NODESTATUS_NODEID + " = ?";
	String PREPARED_QRY_DELETE_NODE_STATUS_CONSOLIDATEDDATA = "DELETE FROM "
			+ TableConstants.TABLE_NODESTATUS_CONSOLIDATEDDATA + " WHERE "
			+ ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_ID + " = ?";

	String PREPARED_QRY_NODE_STATUS_IN_BETWEEN = "SELECT COUNT(*) FROM " + TableConstants.TABLE_NODESTATUS + " WHERE "
			+ ColumnConstants.COL_NODESTATUS_TIME + ">=? AND " + ColumnConstants.COL_NODESTATUS_TIME + "<? AND "
			+ ColumnConstants.COL_NODESTATUS_NODEID + "=? AND " + ColumnConstants.COL_NODESTATUS_STATUS + "=?";

	String PREPARED_QRY_NODE_STATUS_LIST_BEFORE_TIME = "SELECT * FROM " + TableConstants.TABLE_NODESTATUS + " WHERE "
			+ ColumnConstants.COL_NODESTATUS_TIME + "<? AND " + ColumnConstants.COL_NODESTATUS_NODEID + "=?";

	String PREPARED_QRY_NODE_CONSOLIDATED_STATUS_IN_BETWEEN = "SELECT COUNT(*) FROM "
			+ TableConstants.TABLE_NODESTATUS_CONSOLIDATEDDATA + " WHERE " + ColumnConstants.COL_NODESTATUS_TIME
			+ ">=? AND " + ColumnConstants.COL_NODESTATUS_TIME + "<? AND " + ColumnConstants.COL_NODESTATUS_NODEID
			+ "=? AND " + ColumnConstants.COL_NODESTATUS_STATUS + "=?";

	String PREPARED_QRY_CONSOLIDATED_NODE_STATUS_LIST_BEFORE_TIME = "SELECT * FROM "
			+ TableConstants.TABLE_NODESTATUS_CONSOLIDATEDDATA + " WHERE " + ColumnConstants.COL_NODESTATUS_TIME
			+ "<? AND " + ColumnConstants.COL_NODESTATUS_NODEID + "=?";

	String PREPARED_QRY_INSERT_SNAPSHOT = "INSERT INTO " + TableConstants.TABLE_SNAPSHOTS + " VALUES (?,?,?,?,?)";

	String PREPARED_QRY_UPDATE_SNAPSHOT_STATUS = "UPDATE " + TableConstants.TABLE_SNAPSHOTS + " SET "
			+ ColumnConstants.COL_SNAPSHOTS_STATUS + "=? WHERE " + ColumnConstants.COL_SNAPSHOTS_ID + "=?";

	String PREPARED_QRY_DELETE_SNAPSHOT = "DELETE FROM " + TableConstants.TABLE_SNAPSHOTS + " WHERE "
			+ ColumnConstants.COL_SNAPSHOTS_ID + "=?";

	String PREPARED_QRY_SELECT_SNAPSHOT_WHERE_ID = "SELECT * FROM " + TableConstants.TABLE_SNAPSHOTS + " WHERE "
			+ ColumnConstants.COL_SNAPSHOTS_ID + "=?";

	String QRY_GET_ALL_SNAPSHOTS = "SELECT * FROM " + TableConstants.TABLE_SNAPSHOTS + " ORDER BY "
			+ ColumnConstants.COL_SNAPSHOTS_ID + " ASC";

	String PREPARED_QRY_INSERT_BILLING_REPORT_ENTRY = "INSERT INTO " + TableConstants.TABLE_BILLING_REPORT_DATA + '('
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BILLINGID + ","
			+ ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS + ','
			+ ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS + ')' + " VALUES(?,?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_GET_BILLING_DATA = "SELECT SUM(" + ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS + ", SUM("
			+ ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS + ") AS "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS + " FROM "
			+ TableConstants.TABLE_BILLING_REPORT_DATA + " WHERE " + ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP
			+ ">=? AND " + ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP + "<?";

	String PREPARED_QRY_GET_BILLING_DATA_FOR_ID = "SELECT * FROM " + TableConstants.TABLE_BILLING_REPORT_DATA
			+ " WHERE " + ColumnConstants.COL_BILLING_REPORT_DATA_BILLINGID + "=?";

	String PREPARED_QRY_GET_BILLING_DATA_TIMESTAMP_FOR_ID = "SELECT "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP + " FROM " + TableConstants.TABLE_BILLING_REPORT_DATA
			+ " WHERE " + ColumnConstants.COL_BILLING_REPORT_DATA_BILLINGID + "=?";

	String DELETE_QRY_BILLING_DATA_FOR_ID = "DELETE FROM " + TableConstants.TABLE_BILLING_REPORT_DATA + " WHERE "
			+ ColumnConstants.COL_BILLING_REPORT_DATA_BILLINGID + "=?";

	String PREPARED_QRY_INSERT_CREATE_BUCKET_TASK = "INSERT INTO " + TableConstants.TABLE_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ") VALUES(?,'" + ReplicationConstants.TASK_CREATE_BUCKET + "',?)";

	String PREPARED_QRY_INSERT_DELETE_BUCKET_TASK = "INSERT INTO " + TableConstants.TABLE_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ") VALUES(?,'" + ReplicationConstants.TASK_DELETE_BUCKET + "',?)";

	String PREPARED_QRY_INSERT_CREATE_OBJECT_TASK = "INSERT INTO " + TableConstants.TABLE_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ',' + ColumnConstants.COL_TASKS_OBJECT_NAME + ") VALUES(?,'"
			+ ReplicationConstants.TASK_CREATE_OBJECT + "',?,?)";

	String PREPARED_QRY_INSERT_DELETE_OBJECT_TASK = "INSERT INTO " + TableConstants.TABLE_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ',' + ColumnConstants.COL_TASKS_OBJECT_NAME + ") VALUES(?,'"
			+ ReplicationConstants.TASK_DELETE_OBJECT + "',?,?)";

	String PREPARED_QRY_INCREMENT_REPLICATION_ATTEMPTS = "UPDATE " + TableConstants.TABLE_TASKS + "SET "
			+ ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + " = ( SELECT "
			+ ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + " FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + " = ?) WHERE " + ColumnConstants.COL_TASKS_ID + " = ?";

	String PREPARED_QRY_DELETE_TASK = "DELETE FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + "=?";

	String PREPARED_QRY_DELETE_CREATE_BUCKET_TASKS = "DELETE FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + ">? AND " + ColumnConstants.COL_TASKS_ID + "<? AND "
			+ ColumnConstants.COL_TASKS_TYPE + "'" + ReplicationConstants.TASK_CREATE_BUCKET + "'";

	String PREPARED_QRY_DELETE_CREATE_BUCKET_CREATE_OBJECT_DELETE_OBJECT_TASKS = "DELETE FROM "
			+ TableConstants.TABLE_TASKS + " WHERE " + ColumnConstants.COL_TASKS_ID + ">? AND "
			+ ColumnConstants.COL_TASKS_ID + "<? AND " + ColumnConstants.COL_TASKS_BUCKET_NAME + "=? AND ("
			+ ColumnConstants.COL_TASKS_TYPE + "'" + ReplicationConstants.TASK_CREATE_BUCKET + "' OR"
			+ ColumnConstants.COL_TASKS_TYPE + "'" + ReplicationConstants.TASK_CREATE_OBJECT + "' OR"
			+ ColumnConstants.COL_TASKS_TYPE + "'" + ReplicationConstants.TASK_DELETE_OBJECT + "' )";

	String PREPARED_QRY_DELETE_CREATE_OBJECT_TASKS = "DELETE FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + ">? AND " + ColumnConstants.COL_TASKS_ID + "<? AND "
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + "=? AND " + ColumnConstants.COL_TASKS_OBJECT_NAME + "=? AND ("
			+ ColumnConstants.COL_TASKS_TYPE + "'" + ReplicationConstants.TASK_CREATE_OBJECT + "' )";

	String PREPARED_QRY_GET_NEXT_TASK = "SELECT * FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + ">? LIMIT 1";

	String PREPARED_QRY_GET_NEXT_DELETE_BUCKET_TASK_FOR_BUCKET = "SELECT * FROM " + TableConstants.TABLE_TASKS
			+ " WHERE " + ColumnConstants.COL_TASKS_ID + ">? AND " + ColumnConstants.COL_TASKS_BUCKET_NAME + "=? AND "
			+ ColumnConstants.COL_TASKS_TYPE + "='" + ReplicationConstants.TASK_DELETE_BUCKET + "'" + " LIMIT 1";

	String PREPARED_QRY_GET_NEXT_DELETE_OBJECT_TASK_FOR_OBJECT = "SELECT * FROM " + TableConstants.TABLE_TASKS
			+ " WHERE " + ColumnConstants.COL_TASKS_ID + ">? AND " + ColumnConstants.COL_TASKS_BUCKET_NAME + "=? AND "
			+ ColumnConstants.COL_TASKS_OBJECT_NAME + "=? AND " + ColumnConstants.COL_TASKS_TYPE + "='"
			+ ReplicationConstants.TASK_DELETE_OBJECT + "'" + " LIMIT 1";

	String QRY_GET_LAST_SYNC_ID = "SELECT * FROM " + TableConstants.TABLE_LAST_SYNC;

	String PREPARED_QRY_UPDATE_LAST_SYNC_ID = "UPDATE " + TableConstants.TABLE_LAST_SYNC + " SET "
			+ ColumnConstants.COL_LAST_SYNC_ID + "=?";

	String QRY_GET_REPLICATION_CONFIG = "SELECT * FROM " + TableConstants.TABLE_REPLICATION_CONFIG;

	String QRY_SET_REPLICATION_CONFIG = "UPDATE " + TableConstants.TABLE_REPLICATION_CONFIG + " SET "
			+ ColumnConstants.COL_REPLICATION_CONFIG_REPLICATE + "=?";

	String PREPARED_QRY_GET_TASKS = "SELECT * FROM " + TableConstants.TABLE_TASKS + " LIMIT ?,?";

	String QRY_GET_TASK_COUNT = "SELECT COUNT(*) FROM " + TableConstants.TABLE_TASKS;

	String PREPARED_QRY_GET_TASK_WITH_TASKID = "SELECT * FROM " + TableConstants.TABLE_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + "=? LIMIT 1";

	String PREPARED_QRY_INSERT_FAILED_TASK = "INSERT INTO " + TableConstants.TABLE_FAILED_TASKS + '('
			+ ColumnConstants.COL_TASKS_TIMESTAMP + ',' + ColumnConstants.COL_TASKS_TYPE + ','
			+ ColumnConstants.COL_TASKS_BUCKET_NAME + ',' + ColumnConstants.COL_TASKS_OBJECT_NAME + ','
			+ ColumnConstants.COL_TASKS_REPLICATION_ATTEMPTS + ") VALUES(?,?,?,?,?)";

	String PREPARED_QRY_GET_FAILED_TASKS = "SELECT * FROM " + TableConstants.TABLE_FAILED_TASKS + " LIMIT ?,?";

	String QRY_GET_FAILED_TASK_COUNT = "SELECT COUNT(*) FROM " + TableConstants.TABLE_FAILED_TASKS;

	String PREPARED_QRY_DELETE_FAILED_TASK = "DELETE FROM " + TableConstants.TABLE_FAILED_TASKS + " WHERE "
			+ ColumnConstants.COL_TASKS_ID + "=?";

	String PREPARED_QRY_UPDATE_REPLICATION_SETTINGS = "UPDATE " + TableConstants.TABLE_REPLICATION_SETTINGS + " SET "
			+ ColumnConstants.COL_REPLICATION_SETTINGS_PRIMARY_NAMENODE_HOST + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_SECONDARY_NAMENODE_HOST + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_DELAY + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_BATCH_SIZE + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_IS_INITIAL + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_PRIMARY_NAMENODE_PORT + "=?,"
			+ ColumnConstants.COL_REPLICATION_SETTINGS_SECONDARY_NAMENODE_PORT + "=?";

	String QRY_GET_REPLICATION_SETTINGS = "SELECT * FROM " + TableConstants.TABLE_REPLICATION_SETTINGS;
	String QRY_GET_ALL_BILLING_RATE = "SELECT * FROM " + TableConstants.TABLE_BILLING_CONFIG_DATA;

	String QRY_GET_REPLICATION_SETTINGS_PRIMARY_NAMENODE_HOST = "SELECT "
			+ ColumnConstants.COL_REPLICATION_SETTINGS_PRIMARY_NAMENODE_HOST + " FROM "
			+ TableConstants.TABLE_REPLICATION_SETTINGS;

	String PREPARED_QRY_GET_USERID_FROM_USERNAME = "SELECT " + ColumnConstants.COL_USER_ID + " FROM "
			+ TableConstants.TABLE_USERS + " WHERE " + ColumnConstants.COL_USER_USERNAME + "=?";

	String PREPARED_QRY_GET_GROUPS_FOR_USER = "SELECT " + ColumnConstants.COL_USER_GROUPS_GROUPID + " FROM "
			+ TableConstants.TABLE_USER_GROUPS + " WHERE " + ColumnConstants.COL_USER_GROUPS_USERID + "=?";

	String PREPARED_QRY_GET_DEFAULT_GROUP_FOR_USER = "SELECT " + ColumnConstants.COL_USER_GROUPS_GROUPID + " FROM "
			+ TableConstants.TABLE_USER_GROUPS + " WHERE " + ColumnConstants.COL_USER_GROUPS_USERID + "=? AND "
			+ ColumnConstants.COL_USER_GROUPS_ISDEFAULT + "=?";

	String PREPARED_QRY_GET_USERS_FOR_GROUP = "SELECT " + ColumnConstants.COL_USER_GROUPS_USERID + " FROM "
			+ TableConstants.TABLE_USER_GROUPS + " WHERE " + ColumnConstants.COL_USER_GROUPS_GROUPID + "=?";

	String PREPARED_QRY_ADD_USER_TO_GROUP = "INSERT INTO " + TableConstants.TABLE_USER_GROUPS + "("
			+ ColumnConstants.COL_USER_GROUPS_USERID + ", " + ColumnConstants.COL_USER_GROUPS_GROUPID + ", "
			+ ColumnConstants.COL_USER_GROUPS_ISDEFAULT + ") VALUES(?,?,?) ";

	String PREPARED_QRY_DELETE_USER_FROM_ALL_GROUP = "DELETE FROM " + TableConstants.TABLE_USER_GROUPS + " WHERE "
			+ ColumnConstants.COL_USER_GROUPS_USERID + "=?";

	String PREPARED_QRY_DELETE_USER_FROM_GROUP = "DELETE FROM " + TableConstants.TABLE_USER_GROUPS + " WHERE "
			+ ColumnConstants.COL_USER_GROUPS_USERID + "=? AND " + ColumnConstants.COL_USER_GROUPS_GROUPID + "=?";

	String PREPARED_QRY_ADD_GROUP = "INSERT INTO " + TableConstants.TABLE_GROUPS + " ("
			+ ColumnConstants.COL_GROUPS_GROUPNAME + ") VALUES (?) ";

	String PREPARED_QRY_DELETE_GROUP_WITH_NAME = "DELETE FROM " + TableConstants.TABLE_GROUPS + " WHERE "
			+ ColumnConstants.COL_GROUPS_GROUPNAME + "=?";

	String QRY_GET_ALL_GROUP_NAMES = "SELECT " + ColumnConstants.COL_GROUPS_GROUPNAME + " FROM "
			+ TableConstants.TABLE_GROUPS;

	String PREPARED_QRY_GET_GROUP_NAME_FOR_ID = "SELECT " + ColumnConstants.COL_GROUPS_GROUPNAME + " FROM "
			+ TableConstants.TABLE_GROUPS + " WHERE " + ColumnConstants.COL_GROUPS_GROUPID + "=?";

	String PREPARED_QRY_GET_GROUP_ID_FOR_NAME = "SELECT " + ColumnConstants.COL_GROUPS_GROUPID + " FROM "
			+ TableConstants.TABLE_GROUPS + " WHERE " + ColumnConstants.COL_GROUPS_GROUPNAME + "=?";

	String QRY_GET_ALL_USERNAMES = "SELECT " + ColumnConstants.COL_USER_USERNAME + " FROM "
			+ TableConstants.TABLE_USERS;

	String QRY_GET_ALL_USERNAMES_PASSWORDS = "SELECT " + ColumnConstants.COL_USER_USERNAME + ","
			+ ColumnConstants.COL_USER_PASSWORD + " FROM " + TableConstants.TABLE_USERS;

	String QRY_GET_RM_APPS_DETAIL = "SELECT " + ColumnConstants.COL_RESOURCE_MANAGER_APPSSUBMITTED + ", "
			+ ColumnConstants.COL_RESOURCE_MANAGER_APPSRUNNING + ", " + ColumnConstants.COL_RESOURCE_MANAGER_APPSPENDING
			+ ", " + ColumnConstants.COL_RESOURCE_MANAGER_APPSCOMPLETED + ", "
			+ ColumnConstants.COL_RESOURCE_MANAGER_APPSKILLED + " FROM ";

	String QRY_GET_NM_CONTAINER_DETAIL = "SELECT " + ColumnConstants.COL_NODE_MANAGER_CONTAINERSLAUNCHED + ", "
			+ ColumnConstants.COL_NODE_MANAGER_CONTAINERSCOMPLETED + ", "
			+ ColumnConstants.COL_NODE_MANAGER_CONTAINERSFAILED + ", "
			+ ColumnConstants.COL_NODE_MANAGER_CONTAINERSKILLED + ", "
			+ ColumnConstants.COL_NODE_MANAGER_CONTAINERSRUNNING + ", "
			+ ColumnConstants.COL_NODE_MANAGER_CONTAINERSINITING + " FROM ";

	String QRY_GET_LOOKUP_VALUE_FOR_ID = "SELECT " + ColumnConstants.COL_LOOKUP_VALUE + " FROM  "
			+ TableConstants.TABLE_LOOKUP + " WHERE " + ColumnConstants.COL_LOOKUP_ID + "=?";

	String QRY_GET_LOOKUP_VALUE_ID = "SELECT " + ColumnConstants.COL_LOOKUP_ID + " FROM  " + TableConstants.TABLE_LOOKUP
			+ " WHERE " + ColumnConstants.COL_LOOKUP_VALUE + "=?";
	String QRY_INSERT_LOOKUP_VALUE = "INSERT INTO " + TableConstants.TABLE_LOOKUP + "("
			+ ColumnConstants.COL_LOOKUP_VALUE + ") VALUES(?)";

	String QRY_GET_DIRECTORY_ID_FOR_DIRECTORY = "SELECT " + ColumnConstants.COL_DIRECTORIES_ID + " FROM  "
			+ TableConstants.TABLE_DIRECTORIES + " WHERE " + ColumnConstants.COL_DIRECTORIES_PATH + "=?";

	String QRY_GET_DIRECTORY_PATH_FOR_DIRECTORY_ID = "SELECT " + ColumnConstants.COL_DIRECTORIES_PATH + " FROM  "
			+ TableConstants.TABLE_DIRECTORIES + " WHERE " + ColumnConstants.COL_FILES_ID + "=?";

	String QRY_INSERT_DIRECTORY = "INSERT INTO " + TableConstants.TABLE_DIRECTORIES + "("
			+ ColumnConstants.COL_DIRECTORIES_PATH + ") VALUES(?)";

	String INSERT_INTO = "INSERT INTO ";

	// New Addition for fixes

	String INSERT_BIG_QUERY = "INSERT INTO " + TableConstants.TABLE_BIGQUERIES + "(" + ColumnConstants.COL_BIGQUERIES_ID
			+ "," + ColumnConstants.COL_BIGQUERIES_PROPERTIES + "," + ColumnConstants.COL_BIGQUERIES_DESCRIPTION + ","
			+ ColumnConstants.COL_BIGQUERIES_NAMENODEID + "," + ColumnConstants.COL_BIGQUERIES_DBNAME + ","
			+ ColumnConstants.COL_BIGQUERIES_USERNAME + ") VALUES (?,?,?,?,?,?)";

	String INSERT_CHART_QUERY = "INSERT INTO " + TableConstants.TABLE_CHARTS + "(" + ColumnConstants.COL_CHARTS_ID + ","
			+ ColumnConstants.COL_CHARTS_PROPERTIES + "," + ColumnConstants.COL_CHARTS_DESCRIPTION + ","
			+ ColumnConstants.COL_CHARTS_QUERY_ID + ") VALUES (?,?,?,?)";

	String INSERT_TABLE_QUERY = "INSERT INTO " + TableConstants.TABLE_VIEW_TABLES + "(" + ColumnConstants.COL_TABLES_ID
			+ "," + ColumnConstants.COL_TABLES_PROPERTIES + "," + ColumnConstants.COL_TABLES_DESCRIPTION + ","
			+ ColumnConstants.COL_TABLES_QUERY_ID + ") VALUES (?,?,?,?)";

	String INSERT_QUERY_OBJ_QUERY = "INSERT INTO " + TableConstants.TABLE_QUERIES + "(" + ColumnConstants.COL_QUERIES_ID
			+ "," + ColumnConstants.COL_QUERIES_PROPERTIES + "," + ColumnConstants.COL_QUERIES_DESCRIPTION + ","
			+ ColumnConstants.COL_QUERIES_NAMENODEID + "," + ColumnConstants.COL_QUERIES_DBNAME + ","
			+ ColumnConstants.COL_QUERIES_USERNAME + ") VALUES (?,?,?,?,?,?)";

	String DELETE_QUERY = "DELETE FROM " + TableConstants.TABLE_QUERIES + " WHERE " + ColumnConstants.COL_QUERIES_ID
			+ "=? AND " + ColumnConstants.COL_QUERIES_NAMENODEID + "=? AND " + ColumnConstants.COL_QUERIES_USERNAME
			+ "=?";

	String DELETE_CHART = "DELETE FROM " + TableConstants.TABLE_CHARTS + " WHERE " + ColumnConstants.COL_CHARTS_ID
			+ " = ?";

	String DELETE_VIEW_TABLE = "DELETE FROM " + TableConstants.TABLE_VIEW_TABLES + " WHERE "
			+ ColumnConstants.COL_TABLES_ID + " = ?";

	String GET_ALL_QUERIES = "SELECT * FROM " + TableConstants.TABLE_QUERIES + " WHERE "
			+ ColumnConstants.COL_QUERIES_NAMENODEID + "=? " + " ORDER BY " + ColumnConstants.COL_QUERIES_ID + " ASC";

	String GET_ALL_CHARTS = "SELECT * FROM " + TableConstants.TABLE_CHARTS + " ORDER BY "
			+ ColumnConstants.COL_CHARTS_ID + " ASC";

	String GET_ALL_TABLES = "SELECT * FROM " + TableConstants.TABLE_VIEW_TABLES + " ORDER BY "
			+ ColumnConstants.COL_TABLES_ID + " ASC";

	String GET_QUERY_BY_ID = "SELECT * FROM " + TableConstants.TABLE_QUERIES + " WHERE "
			+ ColumnConstants.COL_QUERIES_ID + "=? ";

	String GET_CHART_BY_ID = "SELECT * FROM " + TableConstants.TABLE_CHARTS + " WHERE " + ColumnConstants.COL_CHARTS_ID
			+ "=? ";

	String GET_TABLE_BY_ID = "SELECT * FROM " + TableConstants.TABLE_VIEW_TABLES + " WHERE "
			+ ColumnConstants.COL_TABLES_ID + "=? ";

	// ------------ //

	String UPDATE_CHART_PREFERENCES = "UPDATE " + TableConstants.TABLE_CHARTPREFERENCES + " SET "
			+ ColumnConstants.COL_CHARTPREFERENCES_PROPERTIES + " = ?";

	String DELETE_BIG_QUERY = "DELETE FROM " + TableConstants.TABLE_BIGQUERIES + " WHERE "
			+ ColumnConstants.COL_BIGQUERIES_ID + "=? AND " + ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=? AND "
			+ ColumnConstants.COL_BIGQUERIES_USERNAME + "=?";

	String GET_ALL_BIGQUERIES = "SELECT * FROM " + TableConstants.TABLE_BIGQUERIES + " WHERE "
			+ ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=? " + " ORDER BY " + ColumnConstants.COL_BIGQUERIES_ID
			+ " ASC";

	// String GET_ALL_BIGQUERIES_INFO = SELECT BIGQUERIES.ID,
	// BIGQUERIES.DESCRIPTION, BIGQUERIES.NAMENODEID,
	// BIGQUERIES.DBNAME, queryexecution.STATUS, queryexecution.PATH FROM
	// BIGQUERIES LEFT JOIN (select * from queryexecution QE where
	// QE.executionid=(select max(QE1.executionid) from queryExecution QE1 where
	// QE.queryid=QE1.queryid)) queryexecution ON
	// queryexecution.QUERYID=BIGQUERIES.ID WHERE
	// BIGQUERIES.NAMENODEID=? AND BIGQUERIES.USERNAME=?'
	//
	// Using internal query for join - to avoid multiple rows for same queryId,
	// as queryexecution table will have multiple entries for a query and
	// result is now stored in array not map, so UI shows multiple row for same
	// query.
	String GET_ALL_BIGQUERIES_INFO = "SELECT " + TableConstants.TABLE_BIGQUERIES + "."
			+ ColumnConstants.COL_BIGQUERIES_ID + ", " + TableConstants.TABLE_BIGQUERIES + "."
			+ ColumnConstants.COL_BIGQUERIES_DESCRIPTION + ", " + TableConstants.TABLE_BIGQUERIES + "."
			+ ColumnConstants.COL_BIGQUERIES_NAMENODEID + ", " + TableConstants.TABLE_BIGQUERIES + "."
			+ ColumnConstants.COL_BIGQUERIES_DBNAME + ", " + TableConstants.TABLE_QUERYEXECUTION + "."
			+ ColumnConstants.COL_QUERYEXECUTION_STATUS + ", " + TableConstants.TABLE_QUERYEXECUTION + "."
			+ ColumnConstants.COL_QUERYEXECUTION_PATH + " FROM " + TableConstants.TABLE_BIGQUERIES + " LEFT JOIN "
			+ " (SELECT * FROM " + TableConstants.TABLE_QUERYEXECUTION + " QE where QE."
			+ ColumnConstants.COL_QUERYEXECUTION_ID + "=(select max( QE1." + ColumnConstants.COL_QUERYEXECUTION_ID
			+ ") FROM " + TableConstants.TABLE_QUERYEXECUTION + " QE1 where QE."
			+ ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + "=QE1." + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID
			+ ")) " + TableConstants.TABLE_QUERYEXECUTION
			// + TableConstants.TABLE_QUERYEXECUTION
			+ " ON " + TableConstants.TABLE_QUERYEXECUTION + "." + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + "="
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_ID + " WHERE "
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=? AND "
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_USERNAME + "= ? ";
	// +"ORDER BY "+ TableConstants.TABLE_BIGQUERIES + "."
	// + ColumnConstants.COL_BIGQUERIES_ID ;

	String GET_ALL_BIGQUERIES_COUNT = "SELECT COUNT(*) FROM " + TableConstants.TABLE_BIGQUERIES + " LEFT JOIN "
			+ " (SELECT * FROM " + TableConstants.TABLE_QUERYEXECUTION + " QE where QE."
			+ ColumnConstants.COL_QUERYEXECUTION_ID + "=(select max( QE1." + ColumnConstants.COL_QUERYEXECUTION_ID
			+ ") FROM " + TableConstants.TABLE_QUERYEXECUTION + " QE1 where QE."
			+ ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + "=QE1." + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID
			+ ")) " + TableConstants.TABLE_QUERYEXECUTION
			// + TableConstants.TABLE_QUERYEXECUTION
			+ " ON " + TableConstants.TABLE_QUERYEXECUTION + "." + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + "="
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_ID + " WHERE "
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=? AND "
			+ TableConstants.TABLE_BIGQUERIES + "." + ColumnConstants.COL_BIGQUERIES_USERNAME + "= ? ";

	String GET_BIGQUERIES = "SELECT * FROM " + TableConstants.TABLE_BIGQUERIES + " WHERE "
			+ ColumnConstants.COL_BIGQUERIES_ID + " =? " + " AND " + ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=?"
			+ " AND " + ColumnConstants.COL_BIGQUERIES_USERNAME + "=?";

	String GET_BIGQUERIES_DBNAME = "SELECT " + ColumnConstants.COL_BIGQUERIES_DBNAME + " FROM "
			+ TableConstants.TABLE_BIGQUERIES + " WHERE " + ColumnConstants.COL_BIGQUERIES_ID + " =? " + " AND "
			+ ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=?" + " AND " + ColumnConstants.COL_BIGQUERIES_USERNAME
			+ "=?";

	String GET_CHART_PREFERENCES = "SELECT " + ColumnConstants.COL_CHARTPREFERENCES_PROPERTIES + " FROM "
			+ TableConstants.TABLE_CHARTPREFERENCES;

	String GET_QUERYEXECUTION_MAX_ID = "SELECT MAX(" + ColumnConstants.COL_QUERYEXECUTION_ID + ") FROM "
			+ TableConstants.TABLE_QUERYEXECUTION;

	String UPDATE_BIG_QUERY = "UPDATE " + TableConstants.TABLE_BIGQUERIES + " SET "
			+ ColumnConstants.COL_BIGQUERIES_PROPERTIES + "=? WHERE " + ColumnConstants.COL_BIGQUERIES_ID + "=? AND "
			+ ColumnConstants.COL_BIGQUERIES_NAMENODEID + "=?";

	String GET_ALL_DATABASE_CONNECTION_NAME = "SELECT " + ColumnConstants.COL_DATABASE_CONNNECTION_NAME + " FROM "
			+ TableConstants.TABLE_DATABASE;

	String GET_DATABASE_CONNECTION_DETAIL = "SELECT *  FROM " + TableConstants.TABLE_DATABASE + " WHERE "
			+ ColumnConstants.COL_DATABASE_CONNNECTION_NAME + " = ?";

	String INSERT_NEW_DATABASE_CONNECTION = "INSERT INTO " + TableConstants.TABLE_DATABASE + " VALUES (?, ?, ?, ?, ?)";

	String UPDATE_DATABASE_CONNECTION = "UPDATE " + TableConstants.TABLE_DATABASE + " SET "
			+ ColumnConstants.COL_DATABASE_CONNECTION_URL + "= ?," + ColumnConstants.COL_DATABASE_USERNAME + " = ?,"
			+ ColumnConstants.COL_DATABASE_PASSWORD + " = ?," + ColumnConstants.COL_DATABASE_DRIVER_NAME + " = ?"
			+ " WHERE " + ColumnConstants.COL_DATABASE_CONNNECTION_NAME + "= ?";

	String DELETE_DB_CONNECTION = "DELETE FROM " + TableConstants.TABLE_DATABASE + " WHERE "
			+ ColumnConstants.COL_DATABASE_CONNNECTION_NAME + " = ?";

	String PREPARED_QRY_INSERT_NAMENODE_DB_MAPPING = "INSERT INTO " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING
			+ '(' + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + ','
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME + ','
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + ')' + " VALUES(?,?,?)";

	String PREPARED_QRY_DELETE_NAMENODE_DB_MAPPING = "DELETE FROM " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING
			+ " WHERE " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " = ?";

	String PREPARED_QRY_UPDATE_NAMENODE_DB_MAPPING = "UPDATE " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING
			+ " SET " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME + " = ? WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " = ?";

	String PREPARED_QRY_UPDATE_NAMENODE_ANALYTICS_DB_MAPPING = "UPDATE "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING + " SET "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + " = ? WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " = ?";

	String PREPARED_QRY_INSERT_DECOMMISSIONNODES = "INSERT INTO " + TableConstants.TABLE_DECOMMISSIONNODES + '('
			+ ColumnConstants.COL_DECOMMISSIONNODES_NODEID + ')' + " VALUES(?)";

	String PREPARED_QRY_DELETE_DECOMMISSIONNODES = "DELETE FROM " + TableConstants.TABLE_DECOMMISSIONNODES + " WHERE "
			+ ColumnConstants.COL_DECOMMISSIONNODES_NODEID + " = ?";

	String QRY_IS_NODE_DECOMMISSION = "SELECT " + ColumnConstants.COL_DECOMMISSIONNODES_NODEID + " FROM "
			+ TableConstants.TABLE_DECOMMISSIONNODES + " WHERE " + ColumnConstants.COL_DECOMMISSIONNODES_NODEID
			+ " = ? ";

	String QRY_GET_DBNAME_FROM_NAMENODE_MAPPING = "SELECT " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME
			+ " FROM " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING + " WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " = ? ";

	String QRY_GET_ANALYTICS_DBNAME_FROM_NAMENODE_MAPPING = "SELECT "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING + " WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " = ? ";

	String QRY_GET_NAMENODE_FROM_DBNAME_MAPPING = "SELECT " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID
			+ " FROM " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING + " WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME + " = ? ";

	String QRY_GET_NAMENODE_FROM_ANALYTICS_DBNAME_MAPPING = "SELECT "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING + " WHERE "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + " = ? ";

	String QRY_GET_ALL_DBNAMES_MAPPING = "SELECT " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING;

	String QRY_GET_ALL_ANALYTICS_DBNAMES_MAPPING = "SELECT "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING;

	String QRY_GET_ALL_NAMENODES_MAPPING = "SELECT * FROM " + TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING;

	String QRY_GET_ALL_NAMENODES_DB_MAPPING = "SELECT " + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID
			+ "," + ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING;

	String QRY_GET_ALL_NAMENODES_ANALYTICS_DB_MAPPING = "SELECT "
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID + ","
			+ ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME + " FROM "
			+ TableConstants.TABLE_NAMENODE_CUSTOMDB_MAPPING;

	String PREPARED_QRY_INSERT_DBMIGRATIONSTATUS = "INSERT INTO " + TableConstants.TABLE_DB_MIGRATION_STATUS + "("
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME + ", "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME + ", "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_STARTTIME + ", " + ColumnConstants.COL_DB_MIGRATION_STATUS_ENDTIME
			+ ", " + ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + ", "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_PROGRESS + ", " + ColumnConstants.COL_DB_MIGRATION_STATUS_ERROR
			+ ") VALUES(?,?,?,?,?,?,?)";

	String PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_PROGRESS = "UPDATE " + TableConstants.TABLE_DB_MIGRATION_STATUS
			+ " SET " + ColumnConstants.COL_DB_MIGRATION_STATUS_PROGRESS + " = ? WHERE "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + " = ?";

	String PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_COMPLETE = "UPDATE " + TableConstants.TABLE_DB_MIGRATION_STATUS
			+ " SET " + ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + " = ?, "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_PROGRESS + " = ?, "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_ENDTIME + " = ? WHERE "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + " = ?";

	String PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_ERROR_COMPLETE = "UPDATE " + TableConstants.TABLE_DB_MIGRATION_STATUS
			+ " SET " + ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + " = ?, "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_ERROR + " = ?, " + ColumnConstants.COL_DB_MIGRATION_STATUS_ENDTIME
			+ " = ? WHERE " + ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME + " = ? AND "
			+ ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS + " = ?";

	String QRY_GET_BIGQUERY_ID = "SELECT ID FROM BIGQUERIES ";

	String PREPARED_QRY_GET_FILE_METADATA = "SELECT * FROM " + TableConstants.TABLE_HDFS_METADATA + " WHERE FILEPATH=?";

	String INSERT_SCHEDULE_JOB_STATE = "INSERT INTO " + TableConstants.TABLE_TRIGGERED_SCHEDULEJOB_STATE + " ( "
			+ ColumnConstants.JOB_NAME + ", " + ColumnConstants.JOB_GROUP + ", " + ColumnConstants.STARTTIME + ", "
			+ ColumnConstants.ENDTIME + ", " + ColumnConstants.STATUS + ", " + ColumnConstants.REASON_FOR_FAILURE
			+ " ) VALUES (?,?,?,?,?,?)";

	String GET_JOBNAME_FOR_TABLE_NAME = "SELECT " + TableConstants.TABLE_JOB_MAPPING + "."
			+ ColumnConstants.COL_JOB_MAPPING_JOBNAME + " FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
			+ TableConstants.TABLE_JOB_MAPPING + "." + ColumnConstants.COL_JOB_MAPPING_TABLENAME + " = ?";

	String DELETE_CONFIG_KEY = "DELETE FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "
			+ ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY + " = ?";

	String INSERT_NN_DB_MIGRATION_STATUS = "INSERT INTO " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " ( "
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_START_TIME + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_NAMENODEID + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_HOSTID + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTPATH + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTDBNAME + ","
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_STATUS + " ) VALUES ( ?,?,?,?,?,?,? )";

	String INSERT_NN_RESTOTRE_STATUS = "INSERT INTO " + TableConstants.TABLE_NN_RESTORE_STATUS + " ( "
			+ ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + "," + ColumnConstants.COL_NN_RESTORE_STATUS_MIGRATIONID
			+ "," + ColumnConstants.COL_NN_RESTORE_STATUS_START_TIME + ","
			+ ColumnConstants.COL_NN_RESTORE_STATUS_NAMENODEID + "," + ColumnConstants.COL_NN_RESTORE_STATUS_STATUS
			+ " ) VALUES ( ?,?,?,?,? )";

	String UPDATE_NN_DB_MIGRATION_STATUS = "UPDATE " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " SET "
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_END_TIME + "=?,"
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_STATUS + "=?,"
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_ERROR + "=? WHERE "
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID + "=?";

	String GET_NN_DB_MIGRATION_STATUS = "SELECT * FROM " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " WHERE "
			+ ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID + "=?";

	String UPDATE_NN_RESTORE_STATUS = "UPDATE " + TableConstants.TABLE_NN_RESTORE_STATUS + " SET "
			+ ColumnConstants.COL_NN_RESTORE_STATUS_END_TIME + "=?," + ColumnConstants.COL_NN_RESTORE_STATUS_STATUS
			+ "=?" + " WHERE " + ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + "=?";

	String DELETE_NN_DB_MIGRATION = "DELETE FROM " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " WHERE "
			+ ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + "=?";

	String DELETE_NN_RESTORE = "DELETE FROM " + TableConstants.TABLE_NN_RESTORE_STATUS + " WHERE "
			+ ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + "=?";

	String INSERT_NN_DB_DIAGNOSIS_STATUS = "INSERT INTO " + TableConstants.TABLE_NN_DB_DIAGNOSIS_STATUS + " ( "
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID + ","
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_START_TIME + ","
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_NAMENODEID + ","
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ISREPAIR + ","
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_STATUS + " ) VALUES ( ?,?,?,?,? )";

	String UPDATE_NN_DB_DIAGNOSIS_STATUS = "UPDATE " + TableConstants.TABLE_NN_DB_DIAGNOSIS_STATUS + " SET "
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_END_TIME + "=?,"
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_STATUS + "=?,"
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ERROR + "=?,"
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ISREPAIR + "=? WHERE "
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID + "=?";

	String DELETE_NN_DB_DIAGNOSIS_STATUS = "DELETE FROM " + TableConstants.TABLE_NN_DB_DIAGNOSIS_STATUS + " WHERE "
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID + "=?";

	String INSERT_ADHOC_QUERY = "INSERT INTO " + TableConstants.TABLE_ADHOC_QUERY + " ( "
			+ ColumnConstants.COL_ADHOC_QUERY_ADHOCID + "," + ColumnConstants.COL_ADHOC_QUERY_NAMENODEID + ","
			+ ColumnConstants.COL_ADHOC_QUERY_RMID + "," + ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH + ","
			+ ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE + "," + ColumnConstants.COL_ADHOC_QUERY_TYPE + ","
			+ ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME + "," + ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN
			+ "," + ColumnConstants.COL_ADHOC_QUERY_FIELDS + "," + ColumnConstants.COL_ADHOC_QUERY_ENCODING + ","
			+ ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS + " ) VALUES ( ?,?,?,?,?,?,?,?,?,?,? )";

	String UPDATE_ADHOC_QUERY = "UPDATE " + TableConstants.TABLE_ADHOC_QUERY + " SET "
			+ ColumnConstants.COL_ADHOC_QUERY_RMID + "=?," + ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN + "=?,"
			+ ColumnConstants.COL_ADHOC_QUERY_ENCODING + "=? WHERE " + ColumnConstants.COL_ADHOC_QUERY_ADHOCID + "=?";

	String DELETE_ADHOC_QUERY = "DELETE FROM " + TableConstants.TABLE_ADHOC_QUERY + " WHERE "
			+ ColumnConstants.COL_ADHOC_QUERY_ADHOCID + "=?";

	String DELETE_HIVETABLES = "DELETE FROM " + TableConstants.TABLE_HIVETABLE + " WHERE "
			+ ColumnConstants.COL_HIVETABLES_TABLENAME + "=? AND " + ColumnConstants.COL_HIVETABLES_NAMENODEID + "=?";

	String GET_HIVETABLE = "SELECT * FROM " + TableConstants.TABLE_HIVETABLE + " WHERE "
			+ ColumnConstants.COL_HIVETABLES_TABLENAME + "=? AND " + ColumnConstants.COL_HIVETABLES_NAMENODEID + "=?";

	String GET_DIAGNOSIS_STATUS_FOR_ID = "SELECT * FROM " + TableConstants.TABLE_NN_DB_DIAGNOSIS_STATUS + " WHERE "
			+ ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID + "=?";

	String GET_ALL_DIAGNOSIS_STATUS = "SELECT * FROM " + TableConstants.TABLE_NN_DB_DIAGNOSIS_STATUS;

	String INSERT_DATA_CONNECTION = "INSERT INTO " + TableConstants.TABLE_DATA_CONNECTIONS + "( "
			+ ColumnConstants.COL_DATA_CONNECTION_ID + "," + ColumnConstants.COL_DATA_CONNECTION_TYPE
			+ " ) VALUES (?,?)";

	String INSERT_FTP_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_FTP_DATASOURCES + "( "
			+ ColumnConstants.COL_FTP_DATASOURCE_ID + "," + ColumnConstants.COL_FTP_DATASOURCE_HOST + ","
			+ ColumnConstants.COL_FTP_DATASOURCE_PORT + "," + ColumnConstants.COL_FTP_DATASOURCE_USERNAME + ","
			+ ColumnConstants.COL_FTP_DATASOURCE_PASSWORD + " ) VALUES (?,?,?,?,?)";

	String INSERT_DATABASE_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_DATABASE_DATASOURCES + "( "
			+ ColumnConstants.COL_DATABASE_DATASOURCE_ID + "," + ColumnConstants.COL_DATABASE_DATASOURCE_DRIVER_CLASS
			+ "," + ColumnConstants.COL_DATABASE_DATASOURCE_CONNECTION_URL + ","
			+ ColumnConstants.COL_DATABASE_DATASOURCE_USERNAME + "," + ColumnConstants.COL_DATABASE_DATASOURCE_PASSWORD
			+ "," + ColumnConstants.COL_DATABASE_DATASOURCE_DRIVER_JAR + ","
			+ ColumnConstants.COL_DATABASE_DATASOURCE_MAX_CONNECTIONS + ","
			+ ColumnConstants.COL_DATABASE_DATASOURCE_MAX_IDLE_CONNECTIONS + ","
			+ ColumnConstants.COL_DATABASE_DATASOURCE_MAX_WAITTIME + " ) VALUES (?,?,?,?,?,?,?,?,?)";

	String INSERT_SFTP_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_SFTP_DATASOURCES + "( "
			+ ColumnConstants.COL_SFTP_DATASOURCE_ID + "," + ColumnConstants.COL_SFTP_DATASOURCE_HOST + ","
			+ ColumnConstants.COL_SFTP_DATASOURCE_PORT + "," + ColumnConstants.COL_SFTP_DATASOURCE_USERNAME + ","
			+ ColumnConstants.COL_SFTP_DATASOURCE_PASSWORD + " ) VALUES (?,?,?,?,?)";

	String INSERT_HDFS_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_HDFS_DATASOURCES + "( "
			+ ColumnConstants.COL_HDFS_DATASOURCE_ID + "," + ColumnConstants.COL_HDFS_DATASOURCE_HOST + ","
			+ ColumnConstants.COL_HDFS_DATASOURCE_PORT + "," + ColumnConstants.COL_HDFS_DATASOURCE_GROUP + ","
			+ ColumnConstants.COL_HDFS_DATASOURCE_USERNAME + " ) VALUES (?,?,?,?,?)";

	String INSERT_SSH_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_SSH_DATASOURCES + "( "
			+ ColumnConstants.COL_SSH_DATASOURCE_ID + "," + ColumnConstants.COL_SSH_DATASOURCE_HOST + ","
			+ ColumnConstants.COL_SSH_DATASOURCE_PORT + "," + ColumnConstants.COL_SSH_DATASOURCE_USERNAME + ","
			+ ColumnConstants.COL_SSH_DATASOURCE_PASSWORD + "," + ColumnConstants.COL_SSH_DATASOURCE_KEY
			+ " ) VALUES (?,?,?,?,?,?)";

	String INSERT_HTTP_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_HTTP_DATASOURCES + "( "
			+ ColumnConstants.COL_HTTP_DATASOURCE_ID + "," + ColumnConstants.COL_HTTP_DATASOURCE_BASEURL + ","
			+ ColumnConstants.COL_HTTP_DATASOURCE_USERNAME + "," + ColumnConstants.COL_HTTP_DATASOURCE_PASSWORD
			+ " ) VALUES (?,?,?,?)";

	String INSERT_EMAIL_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_EMAIL_DATASOURCES + "( "
			+ ColumnConstants.COL_EMAIL_DATASOURCE_ID + "," + ColumnConstants.COL_EMAIL_DATASOURCE_EMAILADDRESS + ","
			+ ColumnConstants.COL_EMAIL_DATASOURCE_PASSWORD + ","
			+ ColumnConstants.COL_EMAIL_DATASOURCE_MAILSERVERADDRESS + ","
			+ ColumnConstants.COL_EMAIL_DATASOURCE_ACCOUNTNAME + "," + ColumnConstants.COL_EMAIL_DATASOURCE_PROTOCOL
			+ "," + ColumnConstants.COL_EMAIL_DATASOURCE_SOCKET + "," + ColumnConstants.COL_EMAIL_DATASOURCE_PORT + ","
			+ ColumnConstants.COL_EMAIL_DATASOURCE_CONNECTIONTIMEOUT + ","
			+ ColumnConstants.COL_EMAIL_DATASOURCE_READTIMEOUT + " ) VALUES (?,?,?,?,?,?,?,?,?,?)";

	String INSERT_S3_DATASOURCE = "INSERT INTO " + TableConstants.TABLE_S3_DATASOURCES + "( "
			+ ColumnConstants.COL_S3_DATASOURCE_ID + "," + ColumnConstants.COL_S3_DATASOURCE_ACCESS_KEY + ","
			+ ColumnConstants.COL_S3_DATASOURCE_SECERT_ACCESS_KEY + " ) VALUES (?,?,?)";

	String INSERT_HIVETABLES = "INSERT INTO " + TableConstants.TABLE_HIVETABLE + " ( "
			+ ColumnConstants.COL_HIVETABLES_TABLENAME + "," + ColumnConstants.COL_HIVETABLES_NAMENODEID + ","
			+ ColumnConstants.COL_HIVETABLES_RMID + "," + ColumnConstants.COL_HIVETABLES_FILE_TYPE + ","
			+ ColumnConstants.COL_HIVETABLES_FILE_NAME + " ) VALUES ( ?,?,?,?,? )";

	String PREPARED_QRY_GET_DATA_CONNECTION = "SELECT * FROM " + TableConstants.TABLE_DATA_CONNECTIONS + " WHERE "
			+ ColumnConstants.COL_DATA_CONNECTION_ID + "=?";

	String PREPARED_QRY_GET_FTP_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_FTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_FTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_SFTP_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_SFTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_SFTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_HDFS_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_HDFS_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_HDFS_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_DATABASE_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_DATABASE_DATASOURCES
			+ " WHERE " + ColumnConstants.COL_DATABASE_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_SSH_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_SSH_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_SSH_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_HTTP_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_HTTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_HTTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_GET_EMAIL_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_EMAIL_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_EMAIL_DATASOURCE_ID + "=?";

	String GET_ALL_DATA_CONNECTIONS = "SELECT * FROM " + TableConstants.TABLE_DATA_CONNECTIONS;

	String GET_ALL_FTP_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_FTP_DATASOURCES;

	String GET_ALL_SFTP_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_SFTP_DATASOURCES;

	String GET_ALL_HDFS_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_HDFS_DATASOURCES;

	String GET_ALL_HTTP_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_HTTP_DATASOURCES;

	String GET_ALL_EMAIL_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_EMAIL_DATASOURCES;

	String GET_ALL_HIVETABLES = "SELECT * FROM " + TableConstants.TABLE_HIVETABLE;

	String PREPARED_QRY_GET_S3_DATASOURCE = "SELECT * FROM " + TableConstants.TABLE_S3_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_S3_DATASOURCE_ID + "=?";

	String GET_ALL_S3_DATASOURCES = "SELECT * FROM " + TableConstants.TABLE_S3_DATASOURCES;

	String PREPARED_QRY_DELETE_FTP_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_FTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_FTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_SFTP_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_SFTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_SFTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_HDFS_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_HDFS_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_HDFS_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_S3_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_S3_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_S3_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_HTTP_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_HTTP_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_HTTP_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_EMAIL_DATASOURCE = "DELETE FROM " + TableConstants.TABLE_EMAIL_DATASOURCES + " WHERE "
			+ ColumnConstants.COL_EMAIL_DATASOURCE_ID + "=?";

	String PREPARED_QRY_DELETE_DATA_CONNECTION = "DELETE FROM " + TableConstants.TABLE_DATA_CONNECTIONS + " WHERE "
			+ ColumnConstants.COL_DATA_CONNECTION_ID + "=?";

	String PREPARED_QRY_SPREADSHEETS_INSERT_SHEET = "INSERT INTO " + TableConstants.TABLE_SPREADSHEETS + "("
			+ ColumnConstants.COL_SPREADSHEETS_SHEET_ID + "," + ColumnConstants.COL_SPREADSHEETS_PATH + ","
			+ ColumnConstants.COL_SPREADSHEETS_NAMENODEID + ") VALUES (?,?,?)";

	String PREPARED_QRY_SPREADSHEETS_GET_SHEET_IDS = "SELECT " + ColumnConstants.COL_SPREADSHEETS_SHEET_ID + " FROM "
			+ TableConstants.TABLE_SPREADSHEETS + " WHERE " + ColumnConstants.COL_SPREADSHEETS_NAMENODEID + " = ? ";

	String PREPARED_QRY_SPREADSHEETS_GET_PATH = "SELECT " + ColumnConstants.COL_SPREADSHEETS_PATH + " FROM "
			+ TableConstants.TABLE_SPREADSHEETS + " WHERE " + ColumnConstants.COL_SPREADSHEETS_SHEET_ID + " = ? ";

	String PREPARED_QRY_HIVETABLES_GET_RMID = "SELECT " + ColumnConstants.COL_HIVETABLES_RMID + " FROM "
			+ TableConstants.TABLE_HIVETABLE + " WHERE " + ColumnConstants.COL_HIVETABLES_TABLENAME + " =? AND "
			+ ColumnConstants.COL_HIVETABLES_NAMENODEID + " =? ";

	String PREPARED_QRY_HIVETABLES_GET_FILE_TYPE = "SELECT " + ColumnConstants.COL_HIVETABLES_FILE_TYPE + " FROM "
			+ TableConstants.TABLE_HIVETABLE + " WHERE " + ColumnConstants.COL_HIVETABLES_TABLENAME + " =? AND "
			+ ColumnConstants.COL_HIVETABLES_NAMENODEID + " =? ";

	String PREPARED_QRY_HIVETABLES_GET_FILE_NAME = "SELECT " + ColumnConstants.COL_HIVETABLES_FILE_NAME + " FROM "
			+ TableConstants.TABLE_HIVETABLE + " WHERE " + ColumnConstants.COL_HIVETABLES_TABLENAME + " =? AND "
			+ ColumnConstants.COL_HIVETABLES_NAMENODEID + " =? ";

	String PREPARED_QRY_SPREADSHEETS_DELETE_SHEET = "DELETE FROM " + TableConstants.TABLE_SPREADSHEETS + " WHERE "
			+ ColumnConstants.COL_SPREADSHEETS_SHEET_ID + " = ? ";

	String PREPARED_QRY_CUSTOMTAG_METADATA_INSERT = "INSERT INTO " + TableConstants.TABLE_CUSTOM_TAG_METADATA + " ( "
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "," + ColumnConstants.COL_CUSTOMTAG_METADATA_JSON + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_DESC + "," + ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE + "," + ColumnConstants.COL_CUSTOMTAG_METADATA_FILE_TYPE
			+ "," + ColumnConstants.COL_CUSTOMTAG_METADATA_NAMENODE_ID + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_JOB_NAMES + ")" + " VALUES (?,?,?,?,?,?,?,?,?,?)";

	String PREPARED_QRY_CUSTOMTAG_METADATA_UPDATE = "UPDATE " + TableConstants.TABLE_CUSTOM_TAG_METADATA + " SET "
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_JSON + "=?," + ColumnConstants.COL_CUSTOMTAG_METADATA_DESC + "=?,"
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + "=?,"
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO + "=?" + " WHERE "
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_CUSTOMTAG_METADATA_ISCOLUMN_UPDATE = "UPDATE " + TableConstants.TABLE_CUSTOM_TAG_METADATA
			+ " SET " + ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + "=?" + " WHERE "
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_CUSTOMTAG_METADATA_DELETE = "DELETE FROM " + TableConstants.TABLE_CUSTOM_TAG_METADATA
			+ " WHERE " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_CUSTOMTAG_METADATA_GET_JSON = "SELECT " + ColumnConstants.COL_CUSTOMTAG_METADATA_JSON + " FROM "
			+ TableConstants.TABLE_CUSTOM_TAG_METADATA + " WHERE " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_CUSTOMTAG_METADATA_GET_DETAILS = "SELECT " + ColumnConstants.COL_CUSTOMTAG_METADATA_JSON + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_DESC + "," + ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE + "," + ColumnConstants.COL_CUSTOMTAG_METADATA_FILE_TYPE
			+ "," + ColumnConstants.COL_CUSTOMTAG_METADATA_NAMENODE_ID + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO + ","
			+ ColumnConstants.COL_CUSTOMTAG_METADATA_JOB_NAMES + " FROM " + TableConstants.TABLE_CUSTOM_TAG_METADATA
			+ " WHERE " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_CUSTOMTAG_METADATA_IS_TAG_EXIST = "SELECT COUNT(*) " + " FROM "
			+ TableConstants.TABLE_CUSTOM_TAG_METADATA + " WHERE " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + "=?";

	String PREPARED_QRY_ALL_CUSTOMTAG_METADATA_GET_DETAILS = "SELECT * FROM "
			+ TableConstants.TABLE_CUSTOM_TAG_METADATA;

	String PREPARED_QRY_BIGQUERY_IS_QUERY_EXIST = "SELECT COUNT(*) " + " FROM " + TableConstants.TABLE_BIGQUERIES
			+ " WHERE " + ColumnConstants.COL_BIGQUERIES_ID + "=?";
}