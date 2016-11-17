package com.queryio.common.service.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.queryio.common.remote.SystemStatistics;


public interface QueryIOService extends RemoteService {

	QueryIOResponse startNode(String installDir, String nodeType, String nodeId);

	QueryIOResponse stopNode(String installDir, String nodeType, String nodeId);

	QueryIOResponse performBootstrapStandby(String installDir, String nodeId);
	
	QueryIOResponse initializeSharedEdits(String installDir, String nodeId);
	
	QueryIOResponse transitionNodeToActive(String installDir, String nodeId, String activeNode);

	QueryIOResponse formatDirectory(String installDir, String dirPath);

	QueryIOResponse formatNamenode(String installDirPath, String nodeId);

	QueryIOResponse clearConfiguration(String installDir, String nodeType, String nodeId);
	
	QueryIOResponse updateConfiguration(String installDir, String nodeType,
			String nodeId, String configFileName, ArrayList property,
			ArrayList value, boolean refresh);

	String[] getAllConfiguration(String installDir, String nodeType,
			String nodeId, String configFileName);

	ArrayList getConfiguration(String installDir, String nodeType,
			String nodeId, String configFileName, ArrayList property);

	QueryIOResponse test();

	boolean isMacOS();
	
	boolean isWindowsOS();
	
	void startMonitoring() throws Exception;
	
	void stopMonitoring();
	
	SystemStatistics getSystemStatistics();

	Map<String, String[]> getTemperatureStatus();

	Map<String, String[]> getFanStatus();

	Map<String, String[]> getVoltageStatus();

	HashMap getDiskMap();

	QueryIOResponse runFSCKCommand(String installDir, String nodeId);

	QueryIOResponse startBalancer(String installDir, String nodeId);

	QueryIOResponse updateHostsList(String installDir, String nodeId, String[] datanodeAdds);

	QueryIOResponse updateHostsExcludeList(String installDir, String nodeId,
			String[] datanodeAdds);

	QueryIOResponse refreshNodes(String installDir, String nodeId);

	QueryIOResponse setSafemode(String installDir, String nodeId, boolean safemode);

	ArrayList getPhysicalDisksInfo();

	ArrayList validateVolumeDiskMapping(ArrayList disks, ArrayList volumes);

	String formatDisks(ArrayList disks);

	QueryIOResponse updateNetworkConfig(String installDir, String nodeId,
			String[] hostIps, String rackNames[]);

	QueryIOResponse copyEditsDirToSharedDir(String installDir, String nodeId);

	QueryIOResponse performFailover(String installDir, String nodeId,
			String failoverArg);

	QueryIOResponse updateHadoopEnv(String installDir, String nodeType,
			String nodeId);

	QueryIOResponse updateYarnEnv(String installDir, String nodeType, String nodeId);

	QueryIOResponse copySharedDirLogstoEditsLogs(String installDir, String nodeId);

	String getOSUserName();

	QueryIOResponse startQueryIOServices(String installDir, String nodeId);

	QueryIOResponse stopQueryIOServices(String installDir, String nodeId);

	int getOS3ServerPort(String installDir, String nodeId);

	int getHDFSOverFTPServerPort(String installDir, String nodeId);

	QueryIOResponse runJob(String installDir, String nodeId, String jobName,
			String jarName, String libJars, String files, String className,
			String arguments, String hdfsUri, String dbSourceId, String analyticsDbName, String encryptionKey, boolean isRecursive, boolean isFilterApply, String filterQuery, boolean deleteJobFile);

	QueryIOResponse runJobDataTagging(String installDir, String nodeId,
			String jobName, String jarName, String libJars, String files,
			String className, String arguments, String hdfsUri, String dbSourceId, String encryptionKey, String fileTypeParser);

	QueryIOResponse createStream(String installDir, String relativePath, String objectName);
	
	QueryIOResponse deleteFile(String installDir, String folderName, String fileName);
	
	QueryIOResponse deleteFolder(String installDir, String folderName);

	QueryIOResponse writeToStream(String jobName, byte[] b, int offset, int length);

	QueryIOResponse closeStream(String installDir, String relativePath, String jobName, boolean unzip);

	QueryIOResponse unsetConfiguration(String installDir, String nodeType,
			String nodeId, String configFileName, ArrayList property);

	String getUserHome();

	QueryIOResponse createJarCopyStream(String installDir, String jarFileName);

	QueryIOResponse writeJarToStream(String jarName, byte[] b, int offset,
			int length);

	QueryIOResponse closeJarCopyStream(String installDir, String jarFileName);

	QueryIOResponse createDBConfigCopyStream(String installDir, String fileName);

	QueryIOResponse writeDBCOnfigFile(String fileName, byte[] b, int offset,
			int length);

	QueryIOResponse closeDBConfigCopyStream(String installDir, String fileName);

	boolean hasMapping(String hostName, String hostAddress) throws IOException;
	
	boolean isReachable(String hostname);
	
	String getHostName() throws Exception;

	String getHostAddress() throws Exception;
	
	HashMap getNodeManagerLogsPath(String installDir, String nodeId, String applicationId);
	
	QueryIOResponse checkPortAvailability(String installDir, String nodeType, String nodeId);
	
	QueryIOResponse checkPortAvailability(List<Integer> portList);
	
	void startDBToFileMigration(String migrationID, String dbName, String backupFolder) throws Exception;
	
	void terminateDBMigration(String migrationID);
	
	void deleteBackupData(String backupFolder) throws Exception;	
	
	QueryIOResponse fetchNamespaceId(String namespaceDir);
	
	QueryIOResponse fetchBlockPoolId(String namespaceDir);
	
	void updateJavaHome(String installDir,String javaHomePath) throws Exception;
	
	void updateHadoopPath(String path) throws Exception;
	
	void updateStartStopScripts(String hostHome) throws Exception;
	
	void updateHiveSite(String hostHome, String dbPort) throws Exception;
	
	void backupHadoopEtc(String hostHome) throws Exception ;
	
	void updateLoggerPropertiesFile(String installDir) throws Exception;
	
	public void updateHiveHadoopHome(String installDir) throws Exception;
	
	public QueryIOResponse updateHiveSiteConfiguration(String installDir, String nodeId, ArrayList property,
			ArrayList value, boolean refresh);
	
	public QueryIOResponse startHiveServer(String installDir) throws IOException;
	
	public QueryIOResponse stopHiveServer() throws IOException;
	
	public void startWindowsMonitoring(String installDir, String username, String password) throws Exception; 
	
	public QueryIOResponse refreshUserToGroupsMappings(String installDir, String nodeId) throws Exception;
}