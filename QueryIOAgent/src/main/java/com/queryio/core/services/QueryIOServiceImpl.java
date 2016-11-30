package com.queryio.core.services;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

import com.queryio.common.remote.DBOperationsManager;
import com.queryio.common.remote.MapRedOperation;
import com.queryio.common.remote.NodeOperation;
import com.queryio.common.remote.SystemStatistics;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.service.remote.QueryIOService;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.GetIpAddress;
import com.queryio.common.util.PlatformHandler;
import com.queryio.disk.manager.DiskManager;
import com.queryio.ipmi.IPMIMonitor;
import com.queryio.sysmoncommon.engine.SystemMonitor;

public class QueryIOServiceImpl implements QueryIOService {

	public QueryIOResponse startNode(String installDir, String nodeType, String nodeId) {
		return NodeOperation.startNode(installDir, nodeType, nodeId);
	}

	public QueryIOResponse stopNode(String installDir, String nodeType, String nodeId) {
		return NodeOperation.stopNode(installDir, nodeType, nodeId);
	}

	public QueryIOResponse performBootstrapStandby(String installDir, String nodeId) {
		return NodeOperation.performBootstrapStandby(installDir, nodeId);
	}

	public QueryIOResponse initializeSharedEdits(String installDir, String nodeId) {
		return NodeOperation.initializeSharedEdits(installDir, nodeId);
	}

	public QueryIOResponse transitionNodeToActive(String installDir, String nodeId, String activeNode) {
		return NodeOperation.transitionNodeToActive(installDir, nodeId, activeNode);
	}

	public QueryIOResponse formatNamenode(String installDir, String nodeId) {
		return NodeOperation.formatNamenode(installDir, nodeId);
	}

	public QueryIOResponse formatDirectory(String installDir, String dirPath) {
		return NodeOperation.formatDirectory(installDir, dirPath);
	}

	public QueryIOResponse test() {
		return new QueryIOResponse(true, "Running successsfully");
	}

	public QueryIOResponse clearConfiguration(String installDir, String nodeType, String nodeId) {
		return NodeOperation.clearConfiguration(installDir, nodeType, nodeId);
	}

	public QueryIOResponse updateConfiguration(String installDir, String nodeType, String nodeId, String configFileName,
			ArrayList property, ArrayList value, boolean refresh) {
		return NodeOperation.updateConfiguration(installDir, nodeType, nodeId, configFileName, property, value,
				refresh);
	}

	public ArrayList getConfiguration(String installDir, String nodeType, String nodeId, String configFileName,
			ArrayList property) {
		return NodeOperation.getConfiguration(installDir, nodeType, nodeId, configFileName, property);
	}

	public String[] getAllConfiguration(String installDir, String nodeType, String nodeId, String configFileName) {
		return NodeOperation.getAllConfiguration(installDir, nodeType, nodeId, configFileName);
	}

	public boolean isMacOS() {
		return SystemMonitor.isMacOS(SystemMonitor.OS_NAME);
	}

	public boolean isWindowsOS() {
		return SystemMonitor.isWindowsOS(SystemMonitor.OS_NAME);
	}

	public void startMonitoring() throws Exception {
		SystemMonitor.startMonitoring();
	}

	public void stopMonitoring() {
		SystemMonitor.stopMonitoring();
	}

	public SystemStatistics getSystemStatistics() {
		return SystemMonitor.getSystemStatistics();
	}

	public QueryIOResponse runFSCKCommand(String installDir, String nodeId) {
		return NodeOperation.runFSCKCommand(installDir, nodeId);
	}

	public QueryIOResponse startBalancer(String installDir, String nodeId) {
		return NodeOperation.startBalancer(installDir, nodeId);
	}

	public QueryIOResponse updateHostsList(String installDir, String nodeId, String[] datanodeAdds) {
		return NodeOperation.updateHostsList(installDir, nodeId, datanodeAdds);
	}

	public QueryIOResponse updateHostsExcludeList(String installDir, String nodeId, String[] datanodeAdds) {
		return NodeOperation.updateHostsExcludeList(installDir, nodeId, datanodeAdds);
	}

	public QueryIOResponse refreshNodes(String installDir, String nodeId) {
		return NodeOperation.refreshNodes(installDir, nodeId);
	}

	public QueryIOResponse setSafemode(String installDir, String nodeId, boolean safemode) {
		return NodeOperation.setSafemode(installDir, nodeId, safemode);
	}

	public ArrayList getPhysicalDisksInfo() {
		return DiskManager.getPhysicalDiskNames();
	}

	public ArrayList validateVolumeDiskMapping(ArrayList disks, ArrayList volumes) {
		return DiskManager.validateVolumeDiskMapping(disks, volumes);
	}

	public String formatDisks(ArrayList disks) {
		return DiskManager.formatDisks(disks);
	}

	public QueryIOResponse updateNetworkConfig(String installDir, String nodeId, String[] hostIps, String[] rackNames) {
		return NodeOperation.updateNetworkConfig(installDir, nodeId, hostIps, rackNames);
	}

	public QueryIOResponse copyEditsDirToSharedDir(String installDir, String nodeId) {
		return NodeOperation.copyEditsDirToSharedDir(installDir, nodeId);
	}

	public QueryIOResponse performFailover(String installDir, String nodeId, String failoverArg) {
		return NodeOperation.performFailover(installDir, nodeId, failoverArg);
	}

	public QueryIOResponse updateHadoopEnv(String installDir, String nodeType, String nodeId) {
		return NodeOperation.updateHadoopEnv(installDir, nodeType, nodeId);
	}

	public QueryIOResponse updateYarnEnv(String installDir, String nodeType, String nodeId) {
		return NodeOperation.updateYarnEnv(installDir, nodeType, nodeId);
	}

	public QueryIOResponse copySharedDirLogstoEditsLogs(String installDir, String nodeId) {
		return NodeOperation.copySharedDirLogstoEditsLogs(installDir, nodeId);
	}

	public HashMap getDiskMap() {
		return DiskManager.getDiskMap();
	}

	public String getOSUserName() {
		String username = null;

		BufferedReader reader = null;
		try {
			Process p = null;
			if (SystemUtils.IS_OS_WINDOWS) {
				p = Runtime.getRuntime().exec("cmd.exe /C echo %username%");
			} else {
				p = Runtime.getRuntime().exec("whoami");
			}
			p.waitFor();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			if (line != null) {
				username = line;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching OS username.", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}

		return username;
	}

	public Map<String, String[]> getTemperatureStatus() {
		return IPMIMonitor.getTemperatureStatus();
	}

	public Map<String, String[]> getFanStatus() {
		return IPMIMonitor.getFanStatus();
	}

	public Map<String, String[]> getVoltageStatus() {
		return IPMIMonitor.getVoltageStatus();
	}

	public QueryIOResponse startQueryIOServices(String installDir, String nodeId) {
		return NodeOperation.startQueryIOServices(installDir, nodeId);
	}

	public QueryIOResponse stopQueryIOServices(String installDir, String nodeId) {
		return NodeOperation.stopQueryIOServices(installDir, nodeId);
	}

	public QueryIOResponse runJob(String installDir, String nodeId, String jobName, String jarName, String libJars,
			String files, String className, String arguments, String hdfsUri, String dbSourceId, String analyticsDbName,
			String encryptionKey, boolean isRecursive, boolean isFilterApply, String filterQuery,
			boolean deleteJobFile) {
		return MapRedOperation.runJob(installDir, nodeId, jobName, jarName, libJars, files, className, arguments,
				hdfsUri, dbSourceId, analyticsDbName, encryptionKey, isRecursive, isFilterApply, filterQuery,
				deleteJobFile);
	}

	public QueryIOResponse runJobDataTagging(String installDir, String nodeId, String jobName, String jarName,
			String libJars, String files, String className, String arguments, String hdfsUri, String dbSourceId,
			String encryptionKey, String fileTypeParser) {
		return MapRedOperation.runJobDataTagging(installDir, nodeId, jobName, jarName, libJars, files, className,
				arguments, hdfsUri, dbSourceId, encryptionKey, fileTypeParser);
	}

	public QueryIOResponse createStream(String installDir, String relativePath, String objectName) {
		return MapRedOperation.createStream(installDir, relativePath, objectName);
	}

	public QueryIOResponse createJarCopyStream(String installDir, String jarFileName) {
		return MapRedOperation.createJarCopyStream(installDir, jarFileName);
	}

	public QueryIOResponse writeJarToStream(String jarName, byte[] b, int offset, int length) {
		return MapRedOperation.writeJarToStream(jarName, b, offset, length);
	}

	public QueryIOResponse closeJarCopyStream(String installDir, String jarFileName) {
		return MapRedOperation.closeJarCopyStream(installDir, jarFileName);
	}

	public QueryIOResponse closeStream(String installDir, String relativePath, String jobName, boolean unzip) {
		return MapRedOperation.closeStream(installDir, relativePath, jobName, unzip);
	}

	public QueryIOResponse writeToStream(String fileName, byte[] b, int offset, int length) {
		return MapRedOperation.writeToStream(fileName, b, offset, length);
	}

	public QueryIOResponse unsetConfiguration(String installDir, String nodeType, String nodeId, String configFileName,
			ArrayList property) {
		return NodeOperation.unsetConfiguration(installDir, nodeType, nodeId, configFileName, property);
	}

	public int getOS3ServerPort(String installDir, String nodeId) {
		return NodeOperation.getOS3ServerPort(installDir, nodeId);
	}

	public int getHDFSOverFTPServerPort(String installDir, String nodeId) {
		return NodeOperation.getHDFSOverFTPServerPort(installDir, nodeId);
	}

	public String getUserHome() {
		AppLogger.getLogger().debug("User home : " + System.getProperty("user.home"));
		return System.getProperty("user.home");
	}

	public QueryIOResponse createDBConfigCopyStream(String installDir, String fileName) {
		return MapRedOperation.createDBConfigCopyStream(installDir, fileName);
	}

	public QueryIOResponse writeDBCOnfigFile(String fileName, byte[] b, int offset, int length) {
		return MapRedOperation.writeDBCOnfigFile(fileName, b, offset, length);
	}

	public QueryIOResponse closeDBConfigCopyStream(String installDir, String fileName) {
		return MapRedOperation.closeDBConfigCopyStream(installDir, fileName);
	}

	public boolean hasMapping(String hostName, String hostAddress) throws IOException {
		File file = null;
		if (PlatformHandler.isWindows()) {
			AppLogger.getLogger().debug("Checking mapping on windows platform.");
			file = new File(System.getenv("WINDIR") + "\\system32\\drivers\\etc\\hosts");
			AppLogger.getLogger().debug("hosts file name : " + file.getCanonicalPath());
		} else {
			file = new File("/etc/hosts");
		}
		FileInputStream is = null;
		DataInputStream in = null;
		BufferedReader br = null;
		AppLogger.getLogger().debug("HostName : " + hostName);
		AppLogger.getLogger().debug("HostAddress : " + hostAddress);
		try {
			AppLogger.getLogger().debug("Getting InputStream of hosts file.");
			is = new FileInputStream(file);
			AppLogger.getLogger().debug("InputStream captured for hosts file.");

			in = new DataInputStream(is);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				AppLogger.getLogger().debug("StrLine : " + strLine);
				if ((!strLine.startsWith("#")) && strLine.contains(hostName) && strLine.contains(hostAddress))
					return true;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error reading content of hosts file.", e);
			throw new IOException(e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return false;
	}

	public boolean isReachable(String hostname) {
		try {
			if (InetAddress.getByName(hostname).isReachable(1000) == true) {
				return true;
			}
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Host " + hostname + " is not reachable from this machine");
		}

		return false;
	}

	public String getHostName() throws Exception {
		final GetIpAddress gp = new GetIpAddress();
		gp.fetchAllDetails();
		return gp.getCanonicalHostName();
	}

	public String getHostAddress() throws Exception {
		final GetIpAddress gp = new GetIpAddress();
		gp.fetchAllDetails();
		return gp.getPrimaryIPAddress();
	}

	public QueryIOResponse deleteFile(String installDir, String folderName, String fileName) {
		return MapRedOperation.deleteFile(installDir, folderName, fileName);
	}

	public QueryIOResponse deleteFolder(String installDir, String folderName) {
		return MapRedOperation.deleteFolder(installDir, folderName);
	}

	public HashMap getNodeManagerLogsPath(String installDir, String nodeId, String applicationId) {
		return MapRedOperation.getNodeManagersLogsPath(installDir, nodeId, applicationId);
	}

	@Override
	public QueryIOResponse checkPortAvailability(String installDir, String nodeType, String nodeId) {
		return NodeOperation.checkPortAvailability(installDir, nodeType, nodeId);
	}

	public QueryIOResponse checkPortAvailability(List<Integer> portList) {
		return NodeOperation.checkPortAvailability(portList);
	}

	public void startDBToFileMigration(String migrationID, String dbName, String backupFolder) throws Exception {
		DBOperationsManager.startDBToFileMigration(migrationID, dbName, backupFolder);
	}

	public void terminateDBMigration(String migrationID) {
		DBOperationsManager.terminateDBMigration(migrationID);
	}

	public void deleteBackupData(String backupFolder) throws Exception {
		DBOperationsManager.deleteBackupData(backupFolder);
	}

	public QueryIOResponse fetchNamespaceId(String namespaceDir) {
		return NodeOperation.fetchNamespaceId(namespaceDir);
	}

	public QueryIOResponse fetchBlockPoolId(String namespaceDir) {
		return NodeOperation.fetchBlockPoolId(namespaceDir);
	}

	public void updateJavaHome(String installDir, String javaHomePath) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("update Java home QueryIOServiceImpl");
		NodeOperation.updateJavaHome(installDir, javaHomePath);
	}

	public void updateHadoopPath(String path) throws Exception {
		NodeOperation.updateHadoopPath(path);
	}

	public void updateStartStopScripts(String hostHome) throws Exception {
		NodeOperation.updateStartStopScripts(hostHome);
	}

	public void updateHiveSite(String hostHome, String dbPort) throws Exception {
		NodeOperation.updateHiveSite(hostHome, dbPort);
	}

	public void backupHadoopEtc(String hostHome) throws Exception {
		NodeOperation.backupHadoopEtc(hostHome);
	}

	public void updateLoggerPropertiesFile(String installDir) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("update logger properties QueryIOServiceImpl");
		NodeOperation.updateLoggerPropertiesFile(installDir);

	}

	public void updateHiveHadoopHome(String installDir) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("update Hadoop home QueryIOServiceImpl");
		NodeOperation.updateHiveHadoopHome(installDir);
	}

	public QueryIOResponse updateHiveSiteConfiguration(String installDir, String nodeId, ArrayList property,
			ArrayList value, boolean refresh) {
		return NodeOperation.updateHiveSiteConfiguration(installDir, nodeId, property, value, refresh);
	}

	public QueryIOResponse startHiveServer(String installDir) throws IOException {
		return NodeOperation.startHiveServer(installDir);
	}

	public QueryIOResponse stopHiveServer() throws IOException {
		return NodeOperation.stopHiveServer();
	}

	public void startWindowsMonitoring(String installDir, String username, String password) throws Exception {
		SystemMonitor.startWindowsMonitoring(installDir, username, password);
	}

	public QueryIOResponse refreshUserToGroupsMappings(String installDir, String nodeId) throws IOException {
		return NodeOperation.refreshUserToGroupsMappings(installDir, nodeId);
	}
}