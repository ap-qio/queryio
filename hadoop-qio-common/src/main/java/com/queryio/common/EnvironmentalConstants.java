package com.queryio.common;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class EnvironmentalConstants {

	private static boolean isDemoVersion;
	
	private static String queryIODatabaseDriverPath;
	private static String queryIODatabaseDriverClass;
	private static String queryIODatabasePoolName;
	private static String queryIODatabaseType;
	private static int queryIODatabaseMaxConn;
	private static long queryIODatabaseMaxWaitTime;
	private static int queryIODatabaseMaxIdleConn;
	private static String queryIODatabaseURL;
	private static String queryIODatabaseUserName;
	private static String queryIODatabasePassword;
	private static String webinfDirectory;
	private static String reportsDirectory;
	private static String spreadSheetsDirectory;
	private static String appHome;
	private static String encryptionType;
	private static int streamBufferSize;
	private static int monitorRetryCount;
	private static String dbConfigFilePath; 
	
	private static int minThreadCount = 50;
	private static int maxThreadCount = 200;

	private static String hadoopConfPath;
	
	private static boolean useKerberos;
	private static String fsDefaultName;
	
	private static String nnUserName;
	
	private static ArrayList<String> commomFileTypeNames;
	
	private static String ddlFileContainer;

	public static boolean isDemoVersion() {
		return isDemoVersion;
	}

	public static void setDemoVersion(boolean isDemoVersion) {
		EnvironmentalConstants.isDemoVersion = isDemoVersion;
	}

	public static String getDDLFileContainer() {
		return ddlFileContainer;
	}

	public static void setDDLFileContainer(String dDLFileContainer) {
		ddlFileContainer = dDLFileContainer;
	}

	public static ArrayList<String> getCommomFileTypeNames() {
		return commomFileTypeNames;
	}

	public static void setCommomFileTypeNames(ArrayList<String> commomFileTypeNames) {
		EnvironmentalConstants.commomFileTypeNames = commomFileTypeNames;
	}

	protected static final Logger LOGGER = Logger.getLogger(EnvironmentalConstants.class);
	
	
	public static String getDbConfigFilePath() {
		return dbConfigFilePath;
	}

	public static void setDbConfigFilePath(String dbConfigFilePath) {
		EnvironmentalConstants.dbConfigFilePath = dbConfigFilePath;
	}

	private static String nameNodeId;
	private static boolean isReplicationEnabled;
	private static boolean isPrimaryNameNode;
	
	
	
	private static String jdbcDriverPath;
	
	public static String getJdbcDriverPath() {
		return jdbcDriverPath;
	}

	public static void setJdbcDriverPath(String jdbcDriverPath) {
		EnvironmentalConstants.jdbcDriverPath = jdbcDriverPath;
	}
	
	public static String getEncryptionType() {
		return encryptionType;
	}

	public static void setEncryptionType(String encryptionType) {
		EnvironmentalConstants.encryptionType = encryptionType;
	}

	public static int getStreamBufferSize() {
		return streamBufferSize;
	}

	public static void setStreamBufferSize(int streamBufferSize) {
		EnvironmentalConstants.streamBufferSize = streamBufferSize;
	}
	
	public static boolean isReplicationEnabled()
	{
		return isReplicationEnabled;
	}
	
	public static void setReplicationEnabled(boolean enable)
	{
		isReplicationEnabled = enable;
	}
	
	public static boolean isPrimaryNameNode() {
		return isPrimaryNameNode;
	}

	public static void setPrimaryNameNode(boolean isPrimaryNameNode) 
	{
		EnvironmentalConstants.isPrimaryNameNode = isPrimaryNameNode;
	}

	public static String getQueryIODatabaseDriverPath() {
		return queryIODatabaseDriverPath;
	}
	
	public static void setQueryIODatabaseDriverPath(String value) {
		queryIODatabaseDriverPath = value;
	}	
	
	public static void setQueryIODatabaseDriverClass(String queryIODatabaseDriverClass) {
		EnvironmentalConstants.queryIODatabaseDriverClass = queryIODatabaseDriverClass;
		
	}

	public static String getQueryIODatabaseDriverClass(){
		return queryIODatabaseDriverClass;
	}
	
	public static String getQueryIODatabasePoolName() {
		return queryIODatabasePoolName;
	}

	public static void setQueryIODatabasePoolName(String queryIODatabasePoolName) {
		EnvironmentalConstants.queryIODatabasePoolName = queryIODatabasePoolName;
	}

	public static int getQueryIODatabaseMaxConn() {
		return queryIODatabaseMaxConn;
	}

	public static void setQueryIODatabaseMaxConn(int queryIODatabaseMaxConn) {
		EnvironmentalConstants.queryIODatabaseMaxConn = queryIODatabaseMaxConn;
	}

	public static long getQueryIODatabaseMaxWaitTime() {
		return queryIODatabaseMaxWaitTime;
	}

	public static void setQueryIODatabaseMaxWaitTime(long queryIODatabaseMaxWaitTime) {
		EnvironmentalConstants.queryIODatabaseMaxWaitTime = queryIODatabaseMaxWaitTime;
	}

	public static int getQueryIODatabaseMaxIdleConn() {
		return queryIODatabaseMaxIdleConn;
	}

	public static void setQueryIODatabaseMaxIdleConn(int queryIODatabaseMaxIdleConn) {
		EnvironmentalConstants.queryIODatabaseMaxIdleConn = queryIODatabaseMaxIdleConn;
	}

	public static String getQueryIODatabaseURL() {
		return queryIODatabaseURL;
	}
	
	public static void setQueryIODatabaseURL(String queryIODatabaseURL) {
		EnvironmentalConstants.queryIODatabaseURL = queryIODatabaseURL;
	}

	public static String getQueryIODatabaseUserName() {
		return queryIODatabaseUserName;
	}

	public static void setQueryIODatabaseUserName(String queryIODatabaseUserName) {
		EnvironmentalConstants.queryIODatabaseUserName = queryIODatabaseUserName;
	}

	public static String getQueryIODatabasePassword() {
		return queryIODatabasePassword;
	}

	public static void setQueryIODatabasePassword(String queryIODatabasePassword) {
		EnvironmentalConstants.queryIODatabasePassword = queryIODatabasePassword;
	}

	public static String getWebinfDirectory() {
		return webinfDirectory;
	}

	public static void setWebinfDirectory(String webinfDirectory) {
		EnvironmentalConstants.webinfDirectory = webinfDirectory;
	}

	public static void setAppHome(String appHome) {
		EnvironmentalConstants.appHome = appHome;		
	}
	
	public static String getAppHome() {
		return appHome;		
	}

	public static String getReportsDirectory() {
		return reportsDirectory;
	}

	public static void setReportsDirectory(String reportsDirectory) {
		EnvironmentalConstants.reportsDirectory = reportsDirectory;
	}
	
	public static String getSpreadSheetsDirectory() {
		return spreadSheetsDirectory;
	}
	
	public static void setSpreadSheetsDirectory(String spreadSheetsDirectory) {
		EnvironmentalConstants.spreadSheetsDirectory = spreadSheetsDirectory;
	}
	
	public static int getMonitorRetryCount() {
		return monitorRetryCount;
	}

	public static void setMonitorRetryCount(int monitorRetryCount) {
		EnvironmentalConstants.monitorRetryCount = monitorRetryCount;
	}

	public static String getNnUserName() {
		return nnUserName;
	}

	public static void setNnUserName(String nnUserName) {
		EnvironmentalConstants.nnUserName = nnUserName;
	}

	public static boolean isUseKerberos() {
		return useKerberos;
	}

	public static void setUseKerberos(boolean useKerberos) {
		EnvironmentalConstants.useKerberos = useKerberos;
	}

	public static String getFsDefaultName() {
		return fsDefaultName;
	}

	public static void setFsDefaultName(String fsDefaultName) {
		EnvironmentalConstants.fsDefaultName = fsDefaultName;
	}

	public static String getNameNodeId() {
		return nameNodeId;
	}

	public static void setNameNodeId(String nameNodeId) {
		EnvironmentalConstants.nameNodeId = nameNodeId;
	}

	public static String getHadoopConfPath() {
		return hadoopConfPath;
	}

	public static void setHadoopConfPath(String hadoopConfPath) {
		EnvironmentalConstants.hadoopConfPath = hadoopConfPath;
	}
	
	public static int getMaxThreadCount() {
		return maxThreadCount;
	}

	public static void setMaxThreadCount(int maxThreadCount) {
		EnvironmentalConstants.maxThreadCount = maxThreadCount;
	}
	
	public static int getMinThreadCount() {
		return minThreadCount;
	}

	public static void setMinThreadCount(int minThreadCount) {
		EnvironmentalConstants.minThreadCount = minThreadCount;
	}

	public static String getQueryIODatabaseType() {
		return queryIODatabaseType;
	}

	public static void setQueryIODatabaseType(String queryIODatabaseType) {
		EnvironmentalConstants.queryIODatabaseType = queryIODatabaseType;
	}
}