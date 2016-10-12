package com.queryio.common;

import java.util.HashMap;
import java.util.Map;


public interface QueryIOConstants
{
	String DEFAULT_GROUP_NAME ="queryio";
	String DEFAULT_GROUP_NAME_DEMO ="demo";
	String HEALTH_CHECK_SUCCESS = "Health Check ran successfully.";
	String HEALTH_CHECK_FAILED = "Health Check run failed.";
	String BALANCER_RUN_SUCCESS = "Balancer ran successfully.";
	String BALANCER_RUN_FAILED = "Balancer failed.";
	String NOT_AN_AUTHORIZED_USER = "You do not have sufficient privileges to perform this operation.";
	String HOST_ALREADY_PRESENT = "Host is already present in the cluster's host list. Please add another host.";
	String NODE_ALREADY_PRESENT = "Node is already present on the host.";
	String NODE_ALREADY_PRESENT_ON_CLUSTER = "Node is already present in the cluster.";
	String NAMENODE_ALREADY_PRESENT = "NameNode is already present in the cluster.";
	String NO_NAMENODE_PRESENT = "NameNode is not present in the cluster.";
	String JOBTRACKER_ALREADY_PRESENT = "JobTracker is already present in the cluster.";
	String NO_JOBTRACKER_PRESENT = "JobTracker is not present in the cluster.";
	String HADOOP_INSTALLATION_FAILS = "Installation of Hadoop failed at this time. Please try after some time.";
	String HOST_ADDED_SUCCESS = "Installation was successful and host was added to the cluster's host list.";
	String NODE_ADDED_SUCCESS = "Node added successfully";
	String NODE_FORMAT_SUCCESS = "Node formatted successfully";
	String NODE_OPERATION_SUCCESS = "Operation performed successfully";
	String NODE_OPERATION_FAILED = "Operation failed.";
	String NODE_DELETED_SUCCESS = "Node deleted successfully";
	String HOST_DELETED_SUCCESS = "Host deleted successfully and removed from cluster's host list.";
	String CONFIGURATION_UPDATE_SUCCESS = "Configuration updated successfully.";
	String CONFIGURATION_UPDATE_FAILED = "Configuration updation failed.";
	String INSERTION_FAILURE = "Insertion in the DB failed.";
	String JAVAHOME_FAILURE = "Java home path is either empty or version is below v1.7. QueryIO requires java v1.7 or higher.";
	
	String NAMENODE_ADDED_SUCCESS = "NameNode added successfully.";
	String DATANODE_ADDED_SUCCESS = "DataNode added successfully.";
	String RESOURCEMANAGER_ADDED_SUCCESS = "Resource Manager added successfully. ";
	String NODEMANAGER_ADDED_SUCCESS = "Node Manager added successfully. Now you can submit jobs and run map-reduce operations.";
	
	String NAMENODE_STARTED_SUCCESS = "NameNode service launched successfully.";
	String NAMENODE_STARTED_FAILED = "Start operation on NameNode service failed.";
	String NAMENODE_SERVICES_STARTED_SUCCESS = "All the QueryIO services for this NameNode were also started.";
	String NAMENODE_SERVICES_STARTED_FAILED = "All the QueryIO services for this NameNode could not be started.";
	String NAMENODE_STOPPED_SUCCESS = "NameNode service stopped successfully. You will not be able to perform any operation on this namespace until you start the NameNode again.";
	String NAMENODE_STOPPED_FAILED = "Stop operation on NameNode service failed.";
	String NAMENODE_SERVICES_STOPPED_SUCCESS = "All the QueryIO services for this NameNode were also stopped.";
	String NAMENODE_SERVICES_STOPPED_FAILED = "All the QueryIO services for this NameNode could not be stopped.";
	
	String NAMENODE_SERVICES_NOT_RUNNING = "QueryIO services for this NameNode were not running.";
	
	String DATANODE_STARTED_SUCCESS = "DataNode service launched successfully.";
	String DATANODE_STARTED_FAILED = "Start operation on DataNode service failed.";
	String DATANODE_STOPPED_SUCCESS = "DataNode service stopped successfully.";
	String DATANODE_STOPPED_FAILED = "Stop operation on DataNode service failed.";
	
	String JOURNALNODE_STARTED_SUCCESS = "JournalNode service launched successfully.";
	String JOURNALNODE_STARTED_FAILED = "Start operation on JournalNode service failed.";
	String JOURNALNODE_STOPPED_SUCCESS = "JournalNode service stopped successfully.";
	String JOURNALNODE_STOPPED_FAILED = "Stop operation on JournalNode service failed.";
	
	String SECONDARYNAMENODE_STARTED_SUCCESS = "Checkpoint Node service launched successfully.";
	String SECONDARYNAMENODE_STARTED_FAILED = "Start operation on Checkpoint Node service failed.";
	String SECONDARYNAMENODE_STOPPED_SUCCESS = "Checkpoint Node service stopped successfully.";
	String SECONDARYNAMENODE_STOPPED_FAILED = "Stop operation on Checkpoint Node service failed.";
	
	String RESOURCEMANAGER_STARTED_SUCCESS = "Resource Manager service launched successfully.";
	String RESOURCEMANAGER_STARTED_FAILED = "Start operation on Resource Manager service failed.";
	String RESOURCEMANAGER_STOPPED_SUCCESS = "Resource Manager service stopped successfully.";
	String RESOURCEMANAGER_STOPPED_FAILED = "Stop operation on Resource Manager service failed.";
	
	String NODEMANAGER_STARTED_SUCCESS = "Node Manager service launched successfully.";
	String NODEMANAGER_STARTED_FAILED = "Start operation on Node Manager service failed.";
	String NODEMANAGER_STOPPED_SUCCESS = "Node Manager service stopped successfully.";
	String NODEMANAGER_STOPPED_FAILED = "Stop operation on Node Manager service failed.";
	
	String NODE_ID_ALREADY_PRESENT = "Node already present with same ID. Try adding node with different ID.";
	
	String BACKUP_STARTED = "Process started successfully.";
	String RESTORE_STARTED = "Process started successfully";
	String RESTORE_STOPPED = "Process stopped successfully";
	String RESTORE_STOP_FAILED = "Process failed to stop.";
	String BACKUP_FAILED = "Process failed to start.";
	String BACKUP_DELETED_SUCCESS = "Selected entry(s) deleted successfully.";
	String BACKUP_DELETED_FAILED = "Failed to delete selected backup entry(s).";
	String RESTORE_DELETED_FAILED = "Failed to delete selected restore entry(s).";
	
	String ADHOC_QUERY_SUCCESS = "Hive table for $FileType$ files created successfully";
	String ADHOC_QUERY_FAILED = "Failed to add AdHoc Query entry.";
	String ADHOC_QUERY_UPDATE_SUCCESS = "AdHoc Query entry updated successfully.";
	String ADHOC_QUERY_UPDATE_FAILED = "Failed to update AdHoc Query entry.";
	String ADHOC_QUERY_DELETED_SUCCESS = "Selected entry(s) deleted successfully.";
	String ADHOC_QUERY_DELETED_FAILED = "Failed to delete selected adHoc entry(s).";
	
	String HIVE_SERVER_START_FAILED = "Hive Server failed to start.";

	String HIVE_EXECUTEUPDATE_ERROR_THROWN = "QueryIO Handling";
	
	String AGENT_QUERYIO = "agentqueryio";	
	
	String USER_ADDED = "User successfully added.";
	String USER_UPDATED = "User successfully updated.";
	String USER_DELETED = "User successfully deleted.";
	
	String QUERYIOPROPERTIES_FILENAME = "queryio.properties";
	String QUERYIOAGENTPROPERTIES_FILENAME = "queryioagent.properties";
	
	// Directories at setup locartion
	String SERVICES_DIR_NAME = "services";
	String DB_UPGRADE_DIR_NAME = "db_upgrade";
	String SYSTEM_DIR_NAME = "system";
	
	String HADOOP_DIR_LOCATION = "hadoopDirectorylocation";
	String HADOOP_2_0_3_DIR_NAME= "hadoop-2.0.3-alpha";
	String HADOOP_2_0_4_DIR_NAME= "hadoop-2.0.4-alpha";
	String HADOOP_2_2_0_DIR_NAME= "hadoop-2.2.0";
	String HADOOP_2_4_0_DIR_NAME= "hadoop-2.4.0";
	String HADOOP_2_7_0_DIR_NAME= "Hadoop-2.7.0";
	String HADOOP_DIR_NAME= "Hadoop-2.7.1";
	String HIVE_0_10_0_DIR_NAME= "hive-0.10.0";
	String HIVE_0_11_0_DIR_NAME= "hive-0.11.0";
	String HIVE_0_12_0_DIR_NAME= "hive-0.12.0";
	String HIVE_0_13_0_DIR_NAME= "hive-0.13.0";
	String HIVE_1_2_0_DIR_NAME= "hive-1.2.0";
	String HIVE_DIR_NAME= "hive-1.2.1"; 
	String SCRIPTS_DIR_NAME= "bin";
	String NM_AUX_SERVICE="mapreduce.shuffle";
	String NM_AUX_SERVICE_UPDATE="mapreduce_shuffle";
	String RM_RESOURCE_CALC="org.apache.hadoop.yarn.server.resourcemanager.resource.DefaultResourceCalculator";
	String RM_RESOURCE_CALC_UPDATE="org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator";
	String QUERYIOAGENT_DIR_NAME= "QueryIOAgent";
	String QUERYIOSERVERS_DIR_NAME= "QueryIOServers";
	String MAPREDRESOURCE = "UserLibs";
	String DEFAULT_USERDEFINEDTAGSJAR = "Plugins";
	String TAGPARSER_JAR_DIR= "TagParserJarDirectory";
	String DEFAULT_DIR_REMOTE = "defaultRemoteDir";	
	String SESSION_TIMEOUT = "sessionTimeout";
	String DATA_FETCH_INTERVAL = "dataFetchIntervalInSeconds";
	String MONITOR_RETRY_COUNT = "monitorRetryCount";
	String QUERYIOAGENT_PORT = "queryIOAgentPort";
	
	String COMMON_FILE_TYPES = "commonFileTypes";
	
	String START = "start";
	String STOP = "stop";
	String DELETE = "delete";
	
	String DBNAME = "dbName";
	String HOST = "host";
	String NAMENODE = "namenode";
	String SECONDARYNAMENODE = "secondarynamenode";
	String DATANODE = "datanode";	
	String BALANCER = "balancer";
	String RESOURCEMANAGER = "resourcemanager";
	String NODEMANAGER = "nodemanager";	
	String JOBHISTORY = "jobhistory";
	String JOURNALNODE = "journalnode";
	
	String STATUS_STARTED = "Started";
	String STATUS_LAUNCHING = "Launching";
	String STATUS_NORMAL = "Normal";
	String STATUS_STARTED_WITH_OUTDATED_CONFIGURATION = "Started with outdated configuration";
	String STATUS_STOPPED = "Stopped";
	String STATUS_NOT_RESPONDING = "Not Responding";
	String STATUS_DECOMMISSIONING = "Decommissioning";
	String STATUS_DECOMMISSIONED = "Decommissioned";
	
	String HDFS = "HDFS";
	String MAPREDUCE = "MAPREDUCE";
	
//	int DEFAULT_AGENT_PORT = 5681;
	
	String RETURN_SUCCESS = "1";
	String RETURN_FAILURE = "0";

//	int DATA_FETCH_INTERVAL = 15;
	
	int NAMENODE_MONITOR_PORT = 9004;

	String CONFIG_CORE_SITE_FILENAME = "core-site.xml";
	String CONFIG_HDFS_SITE_FILENAME = "hdfs-site.xml";	
	String CONFIG_MAPRED_SITE_FILENAME = "mapred-site.xml";
	
	String MONITORDATA_TABLE_PREFIX = "H_";
	String ATTRIBUTE_OBJECT_SEPERATOR = "#";
	
	String NAMENODE_STATUS_GROUP_NAME = "File/Block Operations";
	String JOBTRACKER_STATUS_GROUP_NAME = "Jobs";

	String DATATYPE_TIMESTAMP = "TIMESTAMP";
	
	String EMPTY_STRING = "";
		
	String LINE_BREAK = "<br />";
	String COLON = ":";
	
	String INTERVAL_ONE_HOUR = "onehour";
	String INTERVAL_ONE_DAY = "oneday";
	String INTERVAL_ONE_WEEK = "oneweek";
	String INTERVAL_ONE_MONTH = "onemonth";
	String INTERVAL_QUARTER = "quarter";
	String INTERVAL_HALF_YEAR = "halfyear";
	String INTERVAL_ONE_YEAR = "oneyear";
	
	int REPORT_HDFS_STATUS = 0;
	int REPORT_NAMENODE_STATUS = 1;
	int REPORT_DATANODE_STATUS = 2;
	int REPORT_STORAGE_STATUS = 3;
	int REPORT_IO_STATUS = 4;
	int REPORT_ALERT_STATUS = 5;
	int REPORT_STORAGE_FORECAST = 6;
	int REPORT_TOP10 = 7;
	int NN_DETAIL = 8;
	int DN_DETAIL = 9;
	int REPORT_RM_STATUS = 10;
	int REPORT_NM_STATUS = 11;
	int REPORT_RM_DETAIL = 12;
	int REPORT_NM_DETAIL = 13;
	int REPORT_MAPREDUCE_STATUS = 14;
	
	int BILLING_REPORT_USER = 100;
	int BILLING_REPORT_SYSTEM = 101;
	
	String REPORTS_QUERYIO = "Reports";
	String ALERT_REPORT_NAME = "AlertStatusReport_";
	String DN_DETAIL_REPORT = "DataNodeDetailReport_";
	String NN_DETAIL_REPORT = "NameNodeDetailReport_";
	String NN_STATUS_REPORT = "NameNodeStatusReport_";
	String DN_STATUS_REPORT = "DataNodeStatusReport_";
	String HDFS_SUMMARY_REPORT = "HDFSStatusReport_";
	String MAPREDUCE_SUMMARY_REPORT = "MapReduceStatusReport_";
	String RM_STATUS_REPORT = "ResourceManagerStatusReport_";
	String NM_STATUS_REPORT = "NodeManagerStatusReport_";
	String RM_DETAIL_REPORT = "ResourceManagerDetailReport_";
	String NM_DETAIL_REPORT = "NodeManagerDetailReport_";
	
	String REPORTS_HTML_FILES_APPEND = "_files";
	
	
	String REPORT_MESSAGE_HDFS_STATUS = "HDFS Status Report";
	String REPORT_MESSAGE_NAMENODE_STATUS = "NameNode Status Report";
	String REPORT_MESSAGE_DATANODE_STATUS = "DataNode Status Report";
	String REPORT_MESSAGE_STORAGE_STATUS = "Storage Status Report";
	String REPORT_MESSAGE_IO_STATUS = "I/O Status Report";
	String REPORT_MESSAGE_ALERT_STATUS = "Alert Status Report";
	String REPORT_MESSAGE_STORAGE_FORECAST = "Storage Forecast Report";
	String REPORT_MESSAGE_TOP10 = "Top 10 Report";
	String REPORT_MESSAGE_MAPREDUCE_STATUS = "MapReduce Status Report";
	String REPORT_MESSAGE_RM_STATUS = "ResourceManager Status Report";
	String REPORT_MESSAGE_NM_STATUS = "NodeManager Status Report";
	String REPORT_MESSAGE_NN_DETAIL = "NameNode Detail Report";
	String REPORT_MESSAGE_DN_DETAIL = "DataNode Detail Report";
	String REPORT_MESSAGE_RM_DETAIL = "ResourceManager Detail Report";
	String REPORT_MESSAGE_NM_DETAIL = "NodeManager Detail Report";
	
	String BILLING_INVOICE_REPORT = "BillingInvoiceReport_";
	
	String HADOOP_CONFIGTYPE_COMMON = "Common";
	String HADOOP_CONFIGTYPE_HDFS = "HDFS";
	String HADOOP_CONFIGTYPE_HA = "High Availability";
	String HADOOP_CONFIGTYPE_DATANODE = "Datanode";
	String HADOOP_CONFIGTYPE_NAMENODE = "Namenode";
	String HADOOP_CONFIGTYPE_MAPREDUCE = "Map Reduce";
	String HADOOP_CONFIGTYPE_RESOURCEMANAGER = "Resource Manager";
	String HADOOP_CONFIGTYPE_NODEMANAGER = "Node Manager";
	String HADOOP_CONFIGTYPE_CHECKPOINTNODE = "Checkpoint Node";
	String HADOOP_CONFIGTYPE_JOURNALNODE = "Journal Node";
	
	String ACTION_ADDRULE = "add";
	String ACTION_UPDATERULE = "update";
	
	String ROLES_ADMIN = "Admin";
	
	String HDFS_STATUS_UI = "HDFS Summary";
	String DATANODE_STATUS_UI = "DataNode Summary";
	String NAMENODE_STATUS_UI = "NameNode Summary";
	String ALERT_STATUS_UI  = "Alert Summary";
	String NN_DETAIL_UI = "NameNode Detail";
	String DN_DETAIL_UI = "DataNode Detail";
	String STORAGE_FORECAST = "Storage Forecast";
	String MAPREDUCE_STATUS_UI = "MapReduce Summary";
	String RM_STATUS_UI = "ResourceManager Summary";
	String NM_STATUS_UI = "NodeManager Summary";
	String RM_DETAIL_UI = "ResourceManager Detail";
	String NM_DETAIL_UI = "NodeManager Detail";
	String START_NODES ="startNodes";
	
	String BILLING_REPORT_USER_UI = "";
	String BILLING_REPORT_SYSTEM_UI = "System Generated Billing Invoice";
	
	int AGENT_NODE_MONITOR_PERIOD = 60; // seconds
	

	String HADOOP_SERVICE_FSCK = "Health Check";
	String HADOOP_SERVICE_BALANCER = "Balancer";
	
	String PROCESS_STATUS_INPROGRESS = "In Progress";
	String PROCESS_STATUS_COMPLETED = "Completed";
	String PROCESS_STATUS_FAILED = "Failed";
	String PROCESS_STATUS_STOPPED = "Stopped";
	
	String PROCESS_STATUS_DIAGNOSIS_TERMINATED = "Diagnosis Terminated";
	String PROCESS_STATUS_DIAGNOSING = "Diagnosing";
	String PROCESS_STATUS_DIAGNOSIS_COMPLETE = "Diagnosis Complete";
	String PROCESS_STATUS_DIAGNOSIS_FAILED = "Diagnosis Failed";
	String PROCESS_STATUS_REPAIR_TERMINATED = "Repair Terminated";
	String PROCESS_STATUS_REPAIRING = "Repairing";
	String PROCESS_STATUS_REPAIR_COMPLETE = "Repair Complete";
	String PROCESS_STATUS_REPAIR_FAILED = "Repair Failed";
	
	
	int NODE_STATUS_SUSPENDED = 0;
	int NODE_STATUS_OK = 1;
	int NODE_STATUS_FAILURE = 2;
	
	long STATUS_TICKS_COUNT = 24;
	
	long MS_ONE_HOUR = 3600000;
	long MS_ONE_DAY = 86400000;
	long MS_ONE_WEEK = 604800056L;
	long MS_ONE_MONTH = 2629800000L;
	long MS_ONE_QUARTER = 10519200000L;
	long MS_ONE_YEAR = 31557600000L;
	
	String ALERT_ERROR = "Error";
	String ALERT_WARNING = "Warning";
	
	/** Purge time limits in millis. */
	public static final long purgeIntervalForConsolidatedDataTable = 31104000000L; // = 360 days
	public static final long purgeIntervalForDataTable = 86400000L; // = 1 days

	String SNAPSHOT_STATUS_COMPLETED = "Completed";
	
	long ONE_KB = 1024;
	long ONE_MB = ONE_KB * 1024;
	long ONE_GB = ONE_MB * 1024;
	long ONE_TB = ONE_GB * 1024;
	
	long TEN_MB = ONE_MB * 10;
	long HUNDRED_MB = TEN_MB * 10;
	
	String CONTROLLER_DATA_FETCH_INTERVAL_KEY = "queryio.controller.data.fetch.interval";
	String CONTROLLER_AGENT_MONITOR_INTERVAL_KEY = "queryio.agent.monitor.interval";
	String CONTROLLER_NODE_MONITOR_INTERVAL_KEY = "queryio.node.monitor.interval";
	
	int DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL = 5; //seconds
	int DEFAULT_NODE_MONITOR_INTERVAL = 60; //seconds
	int DEFAULT_AGENT_MONITOR_INTERVAL = 1; //minutes
	
	String DEFAULT_NAMENODE_WEB_PORT = "50070";
	
	String NEW_LINE = "\n";
	
	String SEPERATOR = "#@";
	
	String DEFAULT_RACK = "/default-rack";
	String REPLICATION_RACK = "/replication-rack";
	String ACTIVE = "Active";
	String NONHA = "Non-HA";
	String STANDBY = "Stand By";
	String DEFAULT_CLUSTER_NAME = "MyCluster";
	String DEFAULT_RACK_NAME = "/default-rack";
	
	String NAMENODE_OPTS_KEY = "queryio.namenode.options";
	String DATANODE_OPTS_KEY = "queryio.datanode.options";
	String SECONDARYNAMENODE_OPTS_KEY = "queryio.secondarynamenode.options";
	String JOURNALNODE_OPTS_KEY = "queryio.journalnode.options";
	String HADOOP_OPTS_KEY = "queryio.hadoop.options";
	String HADOOP_LOG_DIR_KEY = "queryio.hadoop.log-dir";
	String HADOOP_PID_DIR_KEY = "queryio.hadoop.pid-dir";
	String HADOOP_HEAP_SIZE = "queryio.hadoop.heap-size";
	String QUERYIO_OS3SERVER_PORT = "queryio.s3server.port";
	String QUERYIO_OS3SERVER_SECUREPORT = "queryio.s3server.ssl.port";
	String QUERYIO_HDFSOVERFTP_PORT = "queryio.hdfsoverftp.port";
	String QUERYIO_FTPSERVER_PORT = "queryio.ftpserver.port";
	String QUERYIO_FTPSERVER_SSLENABLED = "queryio.ftpserver.ssl.enabled";
	String QUERYIO_FTPSERVER_SSLPASSWORD = "queryio.ftpserver.ssl.password";
	String QUERYIO_FTPSERVER_SSLKEYSTORE = "queryio.ftpserver.ssl.keystore";
	String QUERYIO_FTPSERVER_SSLPORT = "queryio.ftpserver.ssl.port";
	
	String QUERYIO_DATANODE_DATA_DISK = "queryio.datanode.data.disk";
	String QUERYIO_NAMENODE_DATA_DISK = "queryio.namenode.data.disk";
	
	String QUERYIO_OS3SERVER_CONTEXT = "/queryio";
	String QUERYIO_FTPSERVER_CONTEXT = "/hdfs-over-ftp";
	
	String USE_KERBEROS_KEY = "useKerberos";
	
	String GROUP_INFO_REQUEST_HEADER_KEY = "GroupInfo";
	String USER_INFO_REQUEST_HEADER_KEY = "UserInfo";
	
	String GROUP_INFO_UPDATE_URL = "/UpdateUGI";
	String USER_INFO_UPDATE_URL = "/UpdateUserInfo";
	
	String YARN_LOG_DIR_KEY = "queryio.yarn.log-dir";
	String YARN_HEAP_SIZE = "queryio.yarn.heap-size";
	String YARN_OPTS_KEY = "queryio.yarn.options";
	String NODEMANAGER_OPTS_KEY = "queryio.nodemanager.options";
	String RESOURCEMANAGER_OPTS_KEY = "queryio.resourcemanager.options";
	String YARN_PID_DIR_KEY = "queryio.yarn.pid-dir";
	int BUFFER_SIZE = 1024 * 1024;
	
	String CUSTOM_TAG_PARSER_ATTRIBUTES_PREFIX = "queryio.bigquery.parser";
	String CUSTOM_TAG_DB_ATTRIBUTES_PREFIX = "queryio.bigquery.db";
	
	String CUSTOM_TAG_DB_PRIMARY_URL = "queryio.bigquery.db.primary.url";
	String CUSTOM_TAG_DB_PRIMARY_DRIVER = "queryio.bigquery.db.primary.driver";
	String CUSTOM_TAG_DB_PRIMARY_USERNAME = "queryio.bigquery.db.primary.username";
	String CUSTOM_TAG_DB_PRIMARY_PASSWORD = "queryio.bigquery.db.primary.password";
	
	String CUSTOM_TAG_DB_SECONDARY_URL = "queryio.bigquery.db.secondary.url";
	String CUSTOM_TAG_DB_SECONDARY_DRIVER = "queryio.bigquery.db.secondary.driver";
	String CUSTOM_TAG_DB_SECONDARY_USERNAME = "queryio.bigquery.db.secondary.username";
	String CUSTOM_TAG_DB_SECONDARY_PASSWORD = "queryio.bigquery.db.secondary.password";
	
	String QUERYIO_UNIT_NUM_SPLITS = "queryio.unit.num.splits";
	String CUSTOM_TAG_DB_DBSOURCEID = "queryio.bigquery.db.dbsourceid";
	String ANALYTICS_DB_DBSOURCEID = "queryio.analytics.db.dbsourceid";
	String CUSTOM_TAG_DB_DBCONFIGPATH = "queryio.bigquery.db.dbconfig-path";
	String CUSTOM_TAG_DB_CREATESTMT = "queryio.bigquery.db.insert-statement";
	
	String QUERYIO_DB_BATCH_SIZE_MAX = "queryio.db.batch-size.max";
	String QUERYIO_THREAD_COUNT_MAX = "queryio.thread-count.max";
	
	String CUSTOM_TAG_PARSER_EXPRESSION = "queryio.bigquery.parser.expression";
	
	String CUSTOM_TAG_PARSER_FILETYPES = "queryio.bigquery.parser.filetypes";
	String CUSTOM_TAG_PARSER_CLASSNAME_PREFIX = "queryio.bigquery.parser.classname";
	
	// HIVE config properties. (Used in custom map-reduce jobs also)
	String HIVE_METASTORE_WAREHOUSE_DIR = "hive.metastore.warehouse.dir";
	String HIVE_MAPREDUCE_FRAMEWORK_NAME = "mapreduce.framework.name";
	String HIVE_QUERYLOG_LOCATION = "hive.querylog.location";
	String HIVE_YARN_RESOURCEMANAGER_ADDRESS = "yarn.resourcemanager.address";
	String HIVE_QUERYIO_HDFS_URI = "queryio.hive.hdfsUri";
	String HIVE_QUERYIO_FILEPATH_FILTER = "queryio.hive.filepath.filter";
	String HIVE_QUERYIO_PARSE_RECURSIVE = "queryio.hive.parse.recursive";
	String HIVE_QUERYIO_FILTER_APPLY = "queryio.hive.filter.apply";
	String HIVE_QUERYIO_FILTER_QUERY = "queryio.hive.filter.query";
	String HIVE_QUERYIO_DEFAULT_FS = "fs.defaultFS";
	
//	// Support recursive file paths when hive collects data from HDFS
//	String HIVE_QUERYIO_MAPREDUCE_INPUT_FILEINPUTFORMAT_INPUT_DIR_RECURSIVE = "mapreduce.input.fileinputformat.input.dir.recursive";
//	String HIVE_QUERYIO_QUERYIO_HIVE_FILTER_QUERY = "hive.mapred.supports.subdirectories";
	
	
	String HIVE_QUERYIO_FILEPATH_FILTER_SELECT_ALL = "*";
	String HIVE_FILEPATH_COLUMN_NAME = "INPUT__FILE__NAME";
	
	String HIVE_METASTORE_CONNECTION_DRIVER = "javax.jdo.option.ConnectionDriverName";
	String HIVE_METASTORE_CONNECTION_URL = "javax.jdo.option.ConnectionURL";
	String HIVE_METASTORE_CONNECTION_USERNAME = "javax.jdo.option.ConnectionUserName";
	String HIVE_METASTORE_CONNECTION_PASSWORD = "javax.jdo.option.ConnectionPassword";
	String HIVE_PROPERTY_ONE_NAME_FOR_MYSQL = "datanucleus.transactionIsolation";
	String HIVE_PROPERTY_TWO_NAME_FOR_MYSQL = "datanucleus.valuegeneration.transactionIsolation";
	String HIVE_PROPERTY_ONE_VALUE_FOR_MYSQL = "repeatable-read";
	String HIVE_PROPERTY_TWO_VALUE_FOR_MYSQL = "repeatable-read";

	// DB Connection config for input path filter in hive and custom MR jobs (XML) 
	String QUERYIO_HIVE_METASTORE_CONNECTION_DRIVER_FILTER = "queryio.hive.metastore.ConnectionDriverName";
	String QUERYIO_HIVE_METASTORE_CONNECTION_URL_FILTER = "queryio.hive.metastore.ConnectionURL";
	String QUERYIO_HIVE_METASTORE_CONNECTION_USERNAME_FILTER = "queryio.hive.metastore.ConnectionUserName";
	String QUERYIO_HIVE_METASTORE_CONNECTION_PASSWORD_FILTER = "queryio.hive.metastore.ConnectionPassword";
	
	String QUERYIO_RECORD_LEVEL = "RecordLevel";
	
	String HIVE_CREATE_EXTERNAL_TABLE = "CREATE EXTERNAL TABLE";
	String HIVE_CREATE_TABLE = "CREATE TABLE";
	
	String HIVE_SERDE_CLASS_JSON = "org.apache.hadoop.hive.serde2.json.JSONSerDe";
	String HIVE_SERDE_CLASS_REGEX = "org.apache.hadoop.hive.contrib.serde2.RegexSerDe";

	String HIVE_QUERYIO_WAREHOUSE_DIR = "queryio.hive.warehouse.dir";
	String HIVE_QUERYIO_TEMPLATE_DIR = "queryio.hive.template.dir";
	String HIVE_QUERYIO_MAPREDUCE_FRAMEWORK_NAME_KEY = "queryio.hive.framework.name";
	String HIVE_QUERYIO_CONNECTION_DRIVER = "queryio.hive.connection.driver";
	String HIVE_QUERYIO_CONNECTION_URL = "queryio.hive.connection.url";
	String HIVE_QUERYIO_CONNECTION_USERNAME = "queryio.hive.connection.username";
	String HIVE_QUERYIO_CONNECTION_PASSWORD = "queryio.hive.connection.password";
	
	String HIVE_HIVESERVER_CLASS = "org.apache.hive.service.server.HiveServer2";

	String HIVE_SERVICE_NOT_RUNNING = "Hive Service is not running.";
	String HIVE_SERVICE_STOPPED_SUCCESS = "Hive service stopped successfully.";
	
	String HIVE_STARTED_SUCCESS = "Hive process launched successfully.";
	String HIVE_STARTED_FAILED = "Start operation on Hive service failed.";
	
	String DEFAULT_CUSTOMTAGPARSER_JOBNAME = "CustomTagParser";
	String DEFAULT_CUSTOMTAGPARSER_JARNAME = "CustomTag.jar";
	String DEFAULT_CUSTOMTAGPARSER_CLASSNAME = "com.queryio.hadoop.yarn.customtag.CustomTagJob";
	String JDBC_JAR_DIR = "jdbcJars";
	
	String MYSQL_DB = "MySQL";
	
	String DEFAULT_MONITOR_DB = "SystemDB";
	String DEFAULT_CUSTOMTAG_DB = "QueryDB";
	String DATABASE_TYPE = "DBType";
	
	String DB_NAME_TYPE = "name";
	
	String DB_ROLE = "role";
	String DB_ROLE_METADATA = "Metadata / Tagging";
	String DB_ROLE_ANALYTICS = "Hive Schema";

	String DB_TYPE = "type";
	
	String ROOT_TAG = "DBConfig";
	String DBCONFIG_ROOT = "DBConfiguration";
	String DBCONFIG_TYPE_ROOT = "DBTypeConfiguration";
	String DBCONFIG_XML = "dbconfig.xml";
	
	String QUERYEXECUTION_STATUS_RUNNING = "Running";
	String QUERYEXECUTION_STATUS_SUCCESS = "Success";
	String QUERYEXECUTION_STATUS_FAILED = "Failed";
	
	String DBMIGRATION_STATUS_RUNNING = "Running";
	String DBMIGRATION_STATUS_SUCCESS = "Success";
	String DBMIGRATION_STATUS_FAILED = "Failed";
	
	String DESCRIPTION = "description";
	String STATUS = "status";
	
	String DATASOURCE_LOCAL = "Local";
	String DATASOURCE_FTP = "FTP";
	String DATASOURCE_AMAZON = "Amazon";
	
	String SERVICE_OS3 = "S3 Compatible Server";
	String SERVICE_HDFS_OVER_FTP = "FTP / Secure FTP Server";
	String SERVICE_HIVE = "Hive Server";
	
	String SSL_DEFAULT_PASSWORD = "hadoop";
	// Custom - For server side encryption
	String QUERYIO_DFS_DATA_ENCRYPTION_KEY = "queryio.dfs.data.encryption.key";
	
	String DEFAULT_JAVA_HOME_MACOSX = "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home";
	
	String HOST_RULES_PREFIX = "HOST_IP_";
	
	String NS_NAMESPACE_ID = "ns.namespace.id";
	
	String NS_BLOCKPOOL_ID = "ns.blockpool.id";
	
	String DIAGNOSIS_REPORTS_DIR = "diagnostics";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_CSV = "com.queryio.demo.adhoc.csv.CSVParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_CSV = "Plugins/AdHocCSVParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_LOG = "com.queryio.demo.adhoc.log.LogParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_LOG = "Plugins/AdHocLOGParser.jar";

	String DEFAULT_ADHOC_JOB_CLASS_NAME_IISLOG = "com.queryio.demo.adhoc.iislog.IISLogParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_IISLOG = "Plugins/AdHocIISLOGParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_JSON = "com.queryio.demo.adhoc.json.JSONAdHocParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_JSON = "Plugins/AdHocJSONParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_KVPAIRS = "com.queryio.demo.adhoc.kvpairs.KVPairsParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_KVPAIRS = "Plugins/AdHocKVPairsParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_MBOX = "com.queryio.demo.adhoc.mbox.MBoxParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_MBOX = "Plugins/AdHocMBoxParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_REGEX = "com.queryio.demo.adhoc.regex.RegexParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_REGEX = "Plugins/AdHocRegexParser.jar";
	
	String DEFAULT_ADHOC_JOB_CLASS_NAME_XML = "com.queryio.demo.adhoc.xml.XMLParser";
	String DEFAULT_ADHOC_JOB_LIBJAR_XML = "Plugins/AdHocXMLParser.jar";
	
	
	String HSQL_DB_LIBJAR = "jdbcJars/hsqldb-2.3.2.jar";
	
	String ADHOC_TYPE_CSV = "CSV";
	String TYPE_JTL = "JTL";
	String ADHOC_TYPE_LOG = "LOG";
	String ADHOC_TYPE_ACCESSLOG = "ACCESSLOG";
	String ADHOC_TYPE_IISLOG = "IISLOG";
	String ADHOC_TYPE_JSON = "JSON";
	String ADHOC_TYPE_PAIRS = "PAIRS";
	String ADHOC_TYPE_PAIRS_EXTENSION = "TXT";
	String ADHOC_TYPE_MBOX = "MBOX";
	String ADHOC_TYPE_REGEX = "REGEX";
	String ADHOC_TYPE_XML = "XML";
	
	String ADHOC_SAMPLE_FILE_DIR = "AdHocSampleFiles";
	
	String DATABASE_DRIVER_JAR_FILE_DIR = "DBDriverJars";
	
	String DATABASE_SCHEMA_SAMPLE_FILE_DIR = "DBSchemaFiles";
	
	short DATA_CONNECTION_TYPE_FTP = 0;
	short DATA_CONNECTION_TYPE_S3 = 1;
	short DATA_CONNECTION_TYPE_HTTP = 2;
	short DATA_CONNECTION_TYPE_EMAIL = 3;
	short DATA_CONNECTION_TYPE_HDFS = 4;
	short DATA_CONNECTION_TYPE_SSH = 5;
	short DATA_CONNECTION_TYPE_SFTP = 6;
	short DATA_CONNECTION_TYPE_DATABASE = 9;
	
	String ADHOC_CONSTANT_ARGUMENTS = "arguments";
	String ADHOC_CONSTANT_FIELDS = "fields";
	
	String ADHOC_PARSER_ENCODING = "encoding";
	String ADHOC_PARSER_DELIMITER = "delimiter";
	String ADHOC_PARSER_CSV_VALUESEPARATOR = "valueSeparator";
	String ADHOC_PARSER_ISFIRSTROWHEADER = "isFirstRowHeader";
	String ADHOC_PARSER_VALUESEPARATOR = "valueSeparator";
	String ADHOC_PARSER_SKIP_ALL_RECORDS = "isSkipAllRecords";
	
	String ADHOC_PARSER_REGEX_PATTERN = "regex";
	String ADHOC_PARSER_XML_NODENAME = "nodeName";
	// For LOG
	String ADHOC_PARSER_LOG_PATTERN = "pattern";
	
	// For IIS LOG
	String ADHOC_PARSER_IISLOG_DELIMITER = "delimiter";
	String ADHOC_PARSER_IISLOG_ISFIRSTROWHEADER = "isFirstRowHeader";
	
	String DEFAULT_ADHOC_JAR = "Plugins/AdHocJob.jar";
	String DEFAULT_POSTINGEST_MAIN_CLASS = "com.queryio.demo.job.CustomTagJob";
	String DEFAULT_ADHOC_MAIN_CLASS = "com.queryio.demo.adhoc.AdHocJob";
	String DEFAULT_ONINGEST_PARSER_FILETYPES = "pdf,png,eml,doc,docx,xls,xlsx,ppt,xml,html,rtf,epub,mid,mp3,ogg,class,jar,zip,tar,rpm,dvi,odt,ods,mpp,flac,mp4,dll,flv,pages,key,numbers,3dm,3ds,max,obj,bmp,dds,dng,gif,jpg,jpeg,psd,pspimage,tga,thm,tif,yuv,ai,eps,ps,svg";
	String DEFAULT_ONINGEST_PARSER_CLASS_NAME_WIKI = "com.queryio.datatags.WikiTextParser";
	String DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI = "wiki";
	
	String DEFAULT_ONINGEST_PARSER_NAME = "DataTagParser";
	String DEFAULT_ONINGEST_PARSER_DESCRIPTION = "QueryIO Content Parser registered on addition of NameNode.";
	String DEFAULT_ONINGEST_PARSER_CLASS_NAME = "com.queryio.datatags.DataTagParser";
	String DEFAULT_ONINGEST_PARSER_LIBJAR = "DataTagParser.jar";
	
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV = "com.queryio.datatags.CSVDataParser";
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_LOG4J = "com.queryio.datatags.Log4jDataParser";
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_APACHE_LOG = "com.queryio.datatags.ApacheLogDataParser";
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_IIS_LOG = "com.queryio.datatags.IISLogDataParser";
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_JSON = "com.queryio.datatags.JSONDataParser";
	String DEFAULT_DATA_TAG_PARSER_CLASS_NAME_KVPAIRS = "com.queryio.datatags.KVPairDataParser";
	
	String MIN_THREAD_COUNT_KEY = "minThreadCount";
    String MAX_THREAD_COUNT_KEY = "maxThreadCount";
    
    String LOGGED_IN_USER_INSTALLER = "loggedInUser";
    
    String CLUSTER_SETUP = "cluster.setup";
    String CLUSTER_SETUP_HOST = "cluster.setup.host";
    String CLUSTER_SETUP_NN = "cluster.setup.namenode";
    String CLUSTER_SETUP_DN = "cluster.setup.datanode";
    String CLUSTER_SETUP_RM = "cluster.setup.rm";
    String CLUSTER_SETUP_NM = "cluster.setup.nm";
    String CLUSTER_SETUP_USER = "cluster.setup.qiouser";
    String CLUSTER_SETUP_LOC = "cluster.setup.local";
    String CLUSTER_SETUP_USERHOME = "cluster.setup.userHome";
    String CLUSTER_SETUP_JAVAHOME = "cluster.setup.javaHome";
    String CLUSTER_IP = "cluster.ip";
    String CLUSTER_USERNAME = "cluster.username";
    String CLUSTER_PASSWORD = "cluster.password";
    String CLUSTER_PRIVATE_KEY = "cluster.qio.sshPrivateKey";
    String CLUSTER_SSH_PORT = "cluster.ssh.port";
    String CLUSTER_QIO_USERNAME = "cluster.qio.username";
    String CLUSTER_QIO_PASSWORD = "cluster.qio.password";
    String CLUSTER_QIO_FNAME = "cluster.qio.fname";
    String CLUSTER_QIO_LNAME = "cluster.qio.lname";
    String CLUSTER_QIO_EMAIL = "cluster.qio.email";
    int HIVE_CONNECTION_RETRY_LIMIT = 50;
    
    String METADATA_FILE = ".queryio.install";
    String METADATA_FILE_HOSTS = "clusterHosts";
    String METADATA_FILE_NN = "NameNode";
    String METADATA_FILE_DN = "DataNode";
    String METADATA_FILE_RM = "ResourceManager";
    String METADATA_FILE_NM = "NodeManager";
    String METADATA_FILE_JN = "JournalNode";
    String METADATA_FILE_CPN = "CheckpointNode";
    String isUPGRADE = "isUpgrade";
    String QUERYIO_INSTALL_LOC = "installLoc";
    String UPGRADE_CLUSTERHOSTS = "clusterHosts";
    String UPGRADE_HOST_CREDENTIALS = "/tomcat/webapps/queryio/WEB-INF/UpgradeHostList.properties";
    String UPGRADE_HOST_CREDENTIALS_KEY = "hostList";
    String QUERYIOPACKAGE_DIR = "QueryIOPackage";
    String QUERYIOPACKAGE_DIR_BAK = "QueryIOPackage_bak";
    
    Map<String, String> ACCESS_LOG_PATTERN_TO_REGEX_MAP = new HashMap<String, String>()
    {
    	{
    		put("%h", "([^ ]*)");
    		put("%l", "([^ ]*)");
    		put("%u", "([^ ]*)");
    		put("%t", "(-|\\\\[[^\\\\]]*\\\\])");
    		put("\"%r\"", "([^ \\\"]*|\\\"[^\\\"]*\\\")");
    		put("%>s", "(-|[0-9]*)");
    		put("%b", "(-|[0-9]*)");
    		put("\"%{Referer}i\"", "([^ \\\"]*|\\\"[^\\\"]*\\\")");
    		put("\"%{User-agent}i\"", "([^ \\\"]*|\\\"[^\\\"]*\\\")");
    	}
    };
//    "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\""
    
    String DB_CONFIG_FOLDER = "DBTypeConfig";
    String DB_CONFIG_METADATA_PROPERTIES = "dbtypemetadata.properties";
    
    String DATATAGGING_PREFIX = "DATATAGGING_";
    String DATATAGGING_DEFAULT_JOB =  DATATAGGING_PREFIX + "DEFAULT_JOB"; 
    String DATATAGGING_GENERIC_PARSER_JOB_CLASS = "com.queryio.demo.mr.generic.parser.GenericParserJob";
    String DATATAGGING_GENERIC_PARSER_JOB_NAME = "Plugins/GenericPostIngestJob.jar";
    String DATATAGGING_GENERIC_PARSER_JOB_LIB_JAR_NAME = "Plugins/tika-app-1.3-modified.jar";
    String DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY = "queryio.generic.parser.filetypes.class.map";
    
    String hiveTypePath = "hiveTypePath";
    
	String SNAPPY = "SNAPPY";
	String GZ = "GZ";
	String LZ4 = "LZ4";
	String NONE = "NONE";
	String AES256 = "AES256";
	
	int COMPRESSION_TYPE_NONE = 0;
	int COMPRESSION_TYPE_GZIP = 1;
	int COMPRESSION_TYPE_SNAPPY = 2;
	int COMPRESSION_TYPE_LZ4 = 3;
    
}