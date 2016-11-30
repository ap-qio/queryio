package com.queryio.core.conf;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.json.simple.JSONObject;


import java.sql.Driver;
import com.queryio.amazon.migrate.AmazonExportMigrationThread;
import com.queryio.amazon.migrate.AmazonImportMigrationThread;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.datasources.DataSourceManager;
import com.queryio.core.datasources.FTPDataSource;
import com.queryio.core.datasources.S3DataSource;
import com.queryio.database.migrate.DataBaseConnectionCredentials;
import com.queryio.database.migrate.ExportFromHDFSToDataBase;
import com.queryio.database.migrate.ImportFromDataBase;
import com.queryio.email.migrate.EmailImportMigrationThread;
import com.queryio.email.migrate.GetAvailableFolders;
import com.queryio.ftp.migrate.FTPExportMigrationThread;
import com.queryio.ftp.migrate.FTPImportMigrationThread;
import com.queryio.hdfs.migrate.HDFSExportMigrationThread;
import com.queryio.hdfs.migrate.HDFSImportMigrationThread;
import com.queryio.http.migrate.HTTPExportMigrationThread;
import com.queryio.http.migrate.HTTPImportMigrationThread;
import com.queryio.sftp.migrate.SFTPExportMigrationThread;
import com.queryio.sftp.migrate.SFTPImportMigrationThread;
import com.queryio.ssh.migrate.SSHExportMigrationThread;
import com.queryio.ssh.migrate.SSHImportMigrationThread;

public class MigrationManager {
	
	private static final int CORE_THREAD_POOL_SIZE = 0;
	private static final int MAX_THREAD_POOL_SIZE = 10;
	private static final int TIME_TO_WAIT = 1;
	private static final boolean FAIR = true;
	private static HashMap migrationThreadMap = null;
	
	
	public static ArrayList getAllMigrationDetails(){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			return MigrationInfoDAO.getAll(connection);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;		
	}	
	public static MigrationInfo getByTitle(String title, int fileId) throws Exception{
		Connection connection = null;
		try
		{	
//			AppLogger.getLogger().debug("Title :"+title);
			connection = CoreDBManager.getQueryIODBConnection();
			
			MigrationInfo mi= MigrationInfoDAO.getByTitle(connection, title);
			mi.setId(fileId);
			mi.setTitle(title);
			return mi;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;		
	}
	
	public static String startS3MigrationDS(String nodeId, String title, String dataStore, String s3DataSourceId, String bucketName, String innerKey,
			String hdfsPath, boolean importType, boolean secure, boolean unzip, String compressionType, String encryptionType) {
		S3DataSource ds = DataSourceManager.getS3DataSource(s3DataSourceId);
		if(ds!=null) {
			return startS3Migration(nodeId, title, dataStore, ds.getAccessKey(), ds.getSecretAccessKey(), bucketName, innerKey, hdfsPath, importType, secure, unzip, compressionType, encryptionType);
		} else {
			return "Datasource is not available";
		}
	}
	
	public static String startS3Migration(String nodeId, String title, String dataStore, String accessKey, String secureAccessKey, 
			String bucketName, String innerKey, String hdfsPath, boolean importType, boolean secure, boolean unzip, String compressionType, String encryptionType){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("NodeId: " + node.getId());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Host: " + host.getHostIP());
			
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			keys2.add(DFSConfigKeys.DFS_NAMESERVICE_ID);
			
			ArrayList coreSiteKeys = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList hdfsSiteKeys = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(coreSiteKeys == null || hdfsSiteKeys == null ){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);

			MigrationInfo migrationInfo = new MigrationInfo();
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(importType);
			migrationInfo.setSecure(secure);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");
			migrationInfo.setUnzip(unzip);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);

			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String accessKey, String secureAccessKey, String bucketName, String innerKey, String hdfsPath, boolean importType, boolean secure, boolean unzip: "
					+ nodeId + ", " + title + ", " +  dataStore + ", " + accessKey + ", " + secureAccessKey + ", " + bucketName + ", " + innerKey + ", " + hdfsPath + ", " + importType + ", " + secure + ", " + unzip);
			
			// remove all slashes from bucketName
			while(bucketName.startsWith("/")){
				bucketName = bucketName.substring(1);
			}
			while(bucketName.endsWith("/")){
				bucketName = bucketName.substring(0,bucketName.length()-1);
			}
			if(hdfsPath == null || hdfsPath.length() == 0){
				hdfsPath = "/";
			}else{
				if(!hdfsPath.startsWith("/")){
					hdfsPath = "/" + hdfsPath;
				}
			}
			
			if(importType){
				// innerkey: /innerKey/
				if(innerKey != null && innerKey.length() != 0){
					if(!innerKey.startsWith("/")){
						innerKey = "/" + innerKey;
					}
					if(!innerKey.endsWith("/")){
						innerKey += "/";
					}
					migrationInfo.setSourcePath(bucketName + innerKey);
				}else{
					migrationInfo.setSourcePath(bucketName);
				}				
				migrationInfo.setDestinationPath(hdfsPath);
			}else{
				// innerkey: /innerKey/
				if(innerKey != null && innerKey.length() != 0){
					if(!innerKey.startsWith("/")){
						innerKey = "/" + innerKey;
					}
					if(!innerKey.endsWith("/")){
						innerKey += "/";
					}
					migrationInfo.setDestinationPath(bucketName + innerKey);
				}else{
					migrationInfo.setDestinationPath(bucketName);	
				}	
				migrationInfo.setSourcePath(hdfsPath);				
			}	
								
			MigrationInfoDAO.insert(connection, migrationInfo);
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread;
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			if(importType){
				thread = new AmazonImportMigrationThread(loginUser, loginUserGroup, migrationInfo, accessKey, secureAccessKey, conf, secure);
			}else{
				thread = new AmazonExportMigrationThread(loginUser, loginUserGroup, migrationInfo, accessKey, secureAccessKey, conf, secure);
			}
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static boolean resumeS3Migration(int id, String accessKey,String secureAccessKey, boolean overwrite){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			MigrationInfo migrationInfo = MigrationInfoDAO.getById(connection, id);
			migrationInfo.setStatus("Listing objects");
			migrationInfo.setProgress(0);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setEndTime(null);
			MigrationInfoDAO.update(connection, migrationInfo);			
			
			Node node = NodeDAO.getNode(connection, migrationInfo.getNamenodeId());
			if(node == null){
				return false;
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(migrationInfo.getNamenodeId());
			
			if(values == null || values2 == null){
				return false;
			}
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			Thread thread;
			if(migrationInfo.isImportType()){
				thread = new AmazonImportMigrationThread(loginUser, loginUserGroup, migrationInfo, accessKey, secureAccessKey, conf, overwrite);	
			}else{
				thread = new AmazonExportMigrationThread(loginUser, loginUserGroup, migrationInfo, accessKey, secureAccessKey, conf, overwrite);
			}
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return true;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;		
	}
	
	public static String startFTPMigrationDS(String ndoeId, String title, String dataStore, String ftpDataSourceId, String hdfsPath, String sourcePath, boolean importType, boolean unzip, String compressionType, String encryptionType) {
		FTPDataSource ds = DataSourceManager.getFTPDataSource(ftpDataSourceId);
		if(ds!=null) {
			return startFTPMigration(ndoeId, title, dataStore, ds.getHost() + ":" + ds.getPort(), ds.getUsername(), ds.getPassword(), hdfsPath, sourcePath, importType, unzip, compressionType, encryptionType);
		} else {
			return "Datasource is not available";
		}
	}
	
	public static String startFTPMigration(String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip, String compressionType, String encryptionType){		
		Connection connection = null;
		MigrationInfo migrationInfo = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("values : " + values + "values2 ::" + values2);
			if(values == null || values2 == null){
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
			migrationInfo = new MigrationInfo();
			
			if(sourcePath == null || !sourcePath.startsWith("/")){
				sourcePath = "/" + sourcePath;
			}
			if(importType){
				migrationInfo.setSourcePath("ftp://" + hostName + sourcePath); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}else{
				migrationInfo.setDestinationPath("ftp://" + hostName + sourcePath); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(importType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");		
			migrationInfo.setUnzip(unzip);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + userName + ", " + password + ", " + hdfsPath + ", " + sourcePath + ", " + importType + ", " + unzip);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			if(importType){
				thread = new FTPImportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}else{
				thread = new FTPExportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	public static String exportToDataBase(String nodeId ,String title ,String dataStore ,String driverClass ,String connectionUrl ,String username ,String password,String hdfsPath,String responseJson,String dbdriverjar, String maxConn, String maxIdleConn, String waitTime) {
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			AppLogger.getLogger().debug("response JSON : " + responseJson);
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			MigrationInfo migrationInfo = new MigrationInfo();
			migrationInfo.setDestinationPath(connectionUrl); 
			migrationInfo.setSourcePath(hdfsPath);
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(false);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setUnzip(false);
			migrationInfo.setStatus("Listing objects");
			 
			final Configuration config = conf; 
			AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String driver, String driverJar,  String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + driverClass + ", " + username + ", " + password + ", " + hdfsPath + ", " + connectionUrl + ", " + false + ", " + "false" + dbdriverjar);
			
//			MigrationInfoDAO.insert(connection, migrationInfo);			
//			
//			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			int maxConnect;
			int maxIdleConnect;
			int maxWaitTime;
			try{
				maxConnect = Integer.parseInt(maxConn);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxConn", e);
				maxConnect = 20;
			}
			try{
				maxIdleConnect = Integer.parseInt(maxIdleConn);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxIdleConn", e);
				maxIdleConnect = 10;
			}
			try{
				maxWaitTime = Integer.parseInt(waitTime);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxwaitTime", e);
				maxWaitTime = 30000;
			}
			
			final DataBaseConnectionCredentials dbconfig = new DataBaseConnectionCredentials();
			dbconfig.setDriver(driverClass);
			dbconfig.setConnectionURL(connectionUrl);
			dbconfig.setUserName(username);
			dbconfig.setPassword(password);
			dbconfig.setJarFileName(dbdriverjar);
			dbconfig.setMaxConnections(maxConnect);
			dbconfig.setMaxIdleConnections(maxIdleConnect);
			dbconfig.setWaitTimeMilliSeconds(maxWaitTime);
			
			AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String driver, String driverJar,  String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + driverClass + ", " + username + ", " + password + ", " + hdfsPath + ", " + connectionUrl + ", " +  ", " + "false" + dbdriverjar);
			
			
			AppLogger.getLogger().debug("File Path = " + EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbconfig.getJarFileName());
			File file = new File(EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbconfig.getJarFileName());
			URL u = file.toURI().toURL();
			AppLogger.getLogger().debug(u.toString());
			
			final URLClassLoader ucl = new URLClassLoader(new URL[] { u },
					Thread.currentThread().getContextClassLoader());
			

			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			final MigrationInfo mInfo = migrationInfo;
			
			final String loginUser = RemoteManager.getLoggedInUser();
			final String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			final String respJson = responseJson;
			final int maxThreadConnection = maxConnect;
			final int maxWaitTimeThread = maxWaitTime;
			new Thread() {
				public void run() {
					final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
							CORE_THREAD_POOL_SIZE, // core thread pool size
							maxThreadConnection, // maximum thread pool size
						    maxWaitTimeThread, // time to wait before resizing pool
						    TimeUnit.MILLISECONDS, 
						    new ArrayBlockingQueue<Runnable>(MAX_THREAD_POOL_SIZE, FAIR),
						    new ThreadPoolExecutor.CallerRunsPolicy());
					GenericObjectPool connectionPool =null;
					PoolableConnectionFactory pcf = null;
					try {
						Driver driver = (Driver) Class.forName(dbconfig.getDriver(), true, ucl).newInstance();
						
						ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
						
						connectionPool = new GenericObjectPool();
				        connectionPool.setMaxActive(dbconfig.getMaxConnections());
				        connectionPool.setMaxIdle(dbconfig.getMaxIdleConnections());
				        connectionPool.setMaxWait(dbconfig.getWaitTimeMilliSeconds());
						
				        Properties p = new Properties();
						p.put("user", dbconfig.getUserName());
						p.put("password", dbconfig.getPassword());
				        
				        ConnectionFactory cf = new DriverConnectionFactory(driver, dbconfig.getConnectionURL(), p);
						
				        pcf =
				                new PoolableConnectionFactory(cf, connectionPool,
				                        null, null, false, true);
						
						connectionPool.setFactory(pcf);
						
						final PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
						int unitCount; 
						final FileSystem dfs = FileSystem.get(config);
						FileStatus[] fileList  =  dfs.listStatus(new Path(mInfo.getSourcePath())) ;
						
						unitCount = fileList.length;
						if(unitCount == 0)
							unitCount = 1;
						
						for(int i=0;i<fileList.length;i++){
							Runnable thread;
							try {
								thread = new ExportFromHDFSToDataBase(loginUser, loginUserGroup, mInfo, dataSource, respJson, config , fileList[i].getPath());
								futures.add(threadPoolExecutor.submit(thread));
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}finally{
								
							}
						}
						
						boolean statusList[] = new boolean[futures.size()];
						
						boolean complete = false;
						AppLogger.getLogger().debug("Total array Size = " + futures.size());
						while( ! complete) {
							for (int i=0;i<futures.size();i++) {
								if(futures.get(i).isDone()) {
									statusList[i] =  true;
								} else {
									statusList[i] =  false;
								}
							}
							
							complete = true;
							int progress;
							int successCount = 0;
							for(int k=0; k<statusList.length; k++) {
								if(statusList[k]==false) {
									complete = false;
								}else{
									successCount++;
								}
							}
							progress = (successCount) * 100 / (statusList.length);
							mInfo.setProgress(progress);
							mInfo.setStatus("Migrated "+successCount +" of "+ statusList.length +" objects." );
							AppLogger.getLogger().debug("Id : " + mInfo.getId() + " Migrated "+successCount +" of "+ statusList.length +" objects." );
							Thread.sleep(1000);
							if(complete){
								mInfo.setProgress(progress);
								mInfo.setStatus("Migrated "+successCount +" of "+ statusList.length +" objects." );
								mInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
								Connection conn = null;
								try{
									conn = CoreDBManager.getQueryIODBConnection();
									MigrationInfoDAO.update(conn, mInfo);
								}catch(Exception e)
								{
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
								finally
								{
									try
									{
										if(conn !=null)
										CoreDBManager.closeConnection(conn);
									}
									catch(Exception e)
									{
										AppLogger.getLogger().fatal("Error closing database connection.", e);
									}
								}
							}
								
						}
						
						
					} catch(Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}finally{
						try{
							if(connectionPool!=null)
								connectionPool.close();
						}catch(Exception e){
							AppLogger.getLogger().fatal("Error occurred while closing connection pool " , e);
						}try{
							if(threadPoolExecutor!=null){
								threadPoolExecutor.shutdown();
							}
						}catch(Exception e){
							AppLogger.getLogger().fatal("Error occurred while shutting down thread pool executer." , e);
						}
						
					}
				}
			}.start();
			
			try	
			{	
				connection = CoreDBManager.getQueryIODBConnection();
				mInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
				migrationInfo = mInfo;
				MigrationInfoDAO.update(connection, migrationInfo);
				AppLogger.getLogger().debug("Last Progress = "+migrationInfo.getProgress());
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			finally
			{
				try
				{
					CoreDBManager.closeConnection(connection);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Error closing database connection.", e);
				}
			}
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}finally{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	public static String startDataBaseMigration(String nodeId,String title,String dataStore,String driverClass,String connectionUrl,String username,String password,String hdfsPath,ArrayList<String> tables,String dbdriverjar, String compressionType, String encryption ,boolean importType,String maxConn, String maxIdleConn, String waitTime){
		Connection connection = null;
		MigrationInfo migrationInfo = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			
			final Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			final String uri = conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			
			migrationInfo = new MigrationInfo();
			if(importType){
				migrationInfo.setSourcePath(connectionUrl); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}else{
				migrationInfo.setDestinationPath(connectionUrl); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(importType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setUnzip(false);
			migrationInfo.setStatus("Listing objects");
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryption);
			
			int maxConnect;
			int maxIdleConnect;
			int maxWaitTime;
			try{
				maxConnect = Integer.parseInt(maxConn);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxConn", e);
				maxConnect = 20;
			}
			try{
				maxIdleConnect = Integer.parseInt(maxIdleConn);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxIdleConn", e);
				maxIdleConnect = 10;
			}
			try{
				maxWaitTime = Integer.parseInt(waitTime);
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occurred while converting maxwaitTime", e);
				maxWaitTime = 30000;
			}
			
			final DataBaseConnectionCredentials dbconfig = new DataBaseConnectionCredentials();
			dbconfig.setDriver(driverClass);
			dbconfig.setConnectionURL(connectionUrl);
			dbconfig.setUserName(username);
			dbconfig.setPassword(password);
			dbconfig.setJarFileName(dbdriverjar);
			dbconfig.setTableNames(tables);
			dbconfig.setMaxConnections(maxConnect);
			dbconfig.setMaxIdleConnections(maxIdleConnect);
			dbconfig.setWaitTimeMilliSeconds(maxWaitTime);
			
			AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String driver, String driverJar,  String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + driverClass + ", " + username + ", " + password + ", " + hdfsPath + ", " + connectionUrl + ", " + importType + ", " + "false" + dbdriverjar);
			
			
			AppLogger.getLogger().debug("File Path = " + EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbconfig.getJarFileName());
			File file = new File(EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbconfig.getJarFileName());
			URL u = file.toURI().toURL();
			AppLogger.getLogger().debug(u.toString());
			
			final URLClassLoader ucl = new URLClassLoader(new URL[] { u },
					Thread.currentThread().getContextClassLoader());
			

			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
	
			final MigrationInfo mInfo = migrationInfo;
			
			final String loginUser = RemoteManager.getLoggedInUser();
			final String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			final int maxThreadConnection = maxConnect;
			final int maxWaitTimeThread = maxWaitTime;

			new Thread() {
				public void run() {
					final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
							CORE_THREAD_POOL_SIZE, // core thread pool size
							maxThreadConnection, // maximum thread pool size
							maxWaitTimeThread, // time to wait before resizing pool
							TimeUnit.MILLISECONDS, 
							new ArrayBlockingQueue<Runnable>(maxThreadConnection, FAIR),
							new ThreadPoolExecutor.CallerRunsPolicy());
					GenericObjectPool connectionPool = null;
					try {
						ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
						Driver driver = (Driver) Class.forName(dbconfig.getDriver(), true, ucl).newInstance();
						
						connectionPool = new GenericObjectPool();
				        connectionPool.setMaxActive(dbconfig.getMaxConnections());
				        connectionPool.setMaxIdle(dbconfig.getMaxIdleConnections());
				        connectionPool.setMaxWait(dbconfig.getWaitTimeMilliSeconds());
						
				        Properties p = new Properties();
						p.put("user", dbconfig.getUserName());
						p.put("password", dbconfig.getPassword());
				        
				        ConnectionFactory cf = new DriverConnectionFactory(driver, dbconfig.getConnectionURL(), p);
						
				        PoolableConnectionFactory pcf =
				                new PoolableConnectionFactory(cf, connectionPool,
				                        null, null, false, true);
						
						connectionPool.setFactory(pcf);
						
						final PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
						
						int successCount = 0;
						for(int i=0;i<dbconfig.getTableNames().size();i++){
							Runnable thread;
							try {
								thread = new ImportFromDataBase(loginUser, loginUserGroup, mInfo, dataSource, dbconfig.getTableNames().get(i), conf, uri,true);
								futures.add(threadPoolExecutor.submit(thread));
							} catch (Exception e) {
								
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}finally{
								
							}
						}
						boolean statusList[] = new boolean[futures.size()];
						
						boolean complete = false;
						AppLogger.getLogger().debug("Total array Size = " + futures.size());
						while( ! complete) {
							for (int i=0;i<futures.size();i++) {
								if(futures.get(i).isDone()) {
									statusList[i] =  true;
								} else {
									statusList[i] =  false;
								}
							}
							
							complete = true;
							int progress;
							successCount = 0;
							for(int k=0; k<statusList.length; k++) {
								if(statusList[k]==false) {
									complete = false;
								}else{
									successCount++;
								}
							}
							progress = (successCount) * 100 / (statusList.length);
							mInfo.setProgress(progress);
							mInfo.setStatus("Migrated "+successCount +" of "+ statusList.length +" objects." );
							
							Thread.sleep(1000);
							Connection conn = null;
							if(complete){
								mInfo.setProgress(progress);
								mInfo.setStatus("Migrated "+successCount +" of "+ statusList.length +" objects." );
								mInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
								try{
									conn = CoreDBManager.getQueryIODBConnection();
									MigrationInfoDAO.update(conn, mInfo);
								}catch(Exception e)
								{
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
								finally
								{
									try
									{
										if(conn !=null)
										CoreDBManager.closeConnection(conn);
									}
									catch(Exception e)
									{
										AppLogger.getLogger().fatal("Error closing database connection.", e);
									}
								}
							}
								
						}
					} catch(Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}finally{
						try{
							if(connectionPool!=null){
								connectionPool.close();
							}
						}catch(Exception e){
							AppLogger.getLogger().fatal("Error occurred while closing connection pool " , e);
						}try{
							if(threadPoolExecutor!=null){
								threadPoolExecutor.shutdown();
							}
						}catch (Exception e) {
							AppLogger.getLogger().fatal("Error occurred while closing thread pool " , e);
					}
					}
				}
			}.start();

			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}finally{
			try
			{
				migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
				MigrationInfoDAO.update(connection, migrationInfo);
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static String startSFTPMigration(String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip, String compressionType, String encryptionType){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
			MigrationInfo migrationInfo = new MigrationInfo();
			
			if(sourcePath == null || !sourcePath.startsWith("/")){
				sourcePath = "/" + sourcePath;
			}
			if(importType){
				migrationInfo.setSourcePath("sftp://" + hostName + sourcePath); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}else{
				migrationInfo.setDestinationPath("sftp://" + hostName + sourcePath); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(importType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");		
			migrationInfo.setUnzip(unzip);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + userName + ", " + password + ", " + hdfsPath + ", " + sourcePath + ", " + importType + ", " + unzip);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			if(importType){
				thread = new SFTPImportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}else{
				thread = new SFTPExportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static boolean resumeFTPMigration(int id, String userName,String password, boolean overwrite){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			MigrationInfo migrationInfo = MigrationInfoDAO.getById(connection, id);
			migrationInfo.setStatus("Listing objects");
			migrationInfo.setProgress(0);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setEndTime(null);
			MigrationInfoDAO.update(connection, migrationInfo);			
			
			Node node = NodeDAO.getNode(connection, migrationInfo.getNamenodeId());
			if(node == null){
				return false;
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
				return false;
			}
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(node.getId());
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			Thread thread;
			if(migrationInfo.isImportType()){
				thread = new FTPImportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}else{
				thread = new FTPExportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return true;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;		
	}
	
	public static String startEmailMigration(String nodeId, String title, String dataStore, String dataConnection, String folder, String startDate, String endDate, String prefix, String suffix, String hdfsPath, String compressionType, String encryptionType){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
			MigrationInfo migrationInfo = new MigrationInfo();
			
			migrationInfo.setSourcePath("");
			migrationInfo.setDestinationPath(hdfsPath);
			
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(true);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing mails");		
			migrationInfo.setUnzip(false);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
//					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + userName + ", " + password + ", " + hdfsPath + ", " + sourcePath + ", " + importType + ", " + unzip);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			thread = new EmailImportMigrationThread(loginUser, loginUserGroup, migrationInfo, dataConnection, folder, startDate, endDate, prefix, suffix, conf, true);
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static JSONObject getAllAvailableEmailFolders(String dataConnection)
	{
		ArrayList<String> folderList = null;
		JSONObject obj = new JSONObject();
		
		try
		{
			folderList = (new GetAvailableFolders(dataConnection)).connect();
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Folder List: " + folderList);
			
			String protocol = DataSourceManager.getEmailDataSource(dataConnection).getProtocol();
			
			obj.put("folderList", folderList);
			obj.put("protocol", protocol);
			obj.put("error", null);
			
		}
		catch(Exception e)
		{
			obj.put("folderList", null);
			obj.put("protocol", null);
			obj.put("error", e.getMessage());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(e.getMessage(), e);
		}
		return obj;
	}
	
	public static String startHTTPMigration(String nodeId, String title, String dataStore, String baseURL, String userName, String password, String file, String encoding, String hdfsPath, boolean isImportType, String compressionType, String encryptionType)
	{
		AppLogger.getLogger().fatal("here");
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
			MigrationInfo migrationInfo = new MigrationInfo();
			if(file.startsWith("/"))
	        {
	         file = file.replaceFirst("/", "");
	         file = file.trim();    	
	        }
			String sourcePath = "";
			if(baseURL.endsWith("/"))
				sourcePath = baseURL + file;
			else
				sourcePath = baseURL + "/" + file;
				
			if(isImportType){
				migrationInfo.setSourcePath(sourcePath); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}else{
				migrationInfo.setDestinationPath(sourcePath); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(isImportType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");		
			migrationInfo.setUnzip(false);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip: " 
//					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + userName + ", " + password + ", " + hdfsPath + ", " + sourcePath + ", " + importType + ", " + unzip);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			if(migrationInfo.isImportType()){
				thread = new HTTPImportMigrationThread(loginUser, loginUserGroup, migrationInfo, baseURL, userName, password, file, encoding, conf, true);
			}else{
				thread = new HTTPExportMigrationThread(loginUser, loginUserGroup, migrationInfo, userName, password, conf, true);
			}
			
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static String startHDFSMigration(String nodeId, String title, String dataStore, String hostName, String file, String user, String group, String hdfsPath, boolean isImportType, boolean unzip, String compressionType, String encryptionType)
	{
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
			}
			
//			boolean isImportType = Boolean.parseBoolean(isImport);
			
			MigrationInfo migrationInfo = new MigrationInfo();
			
							
			if(isImportType){
				migrationInfo.setSourcePath(file); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}else{
				migrationInfo.setDestinationPath(file); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(isImportType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");		
			migrationInfo.setUnzip(unzip);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String file, String hdfsPath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + hdfsPath + ", " + isImportType + ", " + compressionType + ", " + encryptionType);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread = null;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			if(migrationInfo.isImportType()){
				thread = new HDFSImportMigrationThread(loginUser, loginUserGroup, migrationInfo, hostName, file, conf, true);
			}
			else
			{
				thread = new HDFSExportMigrationThread(loginUser, loginUserGroup, migrationInfo, hostName, file, user, group, conf, true);
			}
			
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static String startSSHMigration(String nodeId, String title, String dataStore, String hostName, String port, String userName, String password, String path, boolean isSSH, String hdfsPath, boolean isImportType, boolean unzip, String compressionType, String encryptionType)
	{
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node == null){
				return QueryIOConstants.RETURN_FAILURE + "_" + QueryIOConstants.NO_NAMENODE_PRESENT;
			}
			if(node.getStatus().equalsIgnoreCase("stopped")){
				return QueryIOConstants.RETURN_FAILURE + "_NameNode not Responding";
			}
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList keys2 = new ArrayList();
			keys2.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			
			ArrayList values = QueryIOAgentManager.getConfig(host, keys, node, "core-site.xml");
			ArrayList values2 = QueryIOAgentManager.getConfig(host, keys2, node, "hdfs-site.xml");
			
			if(values == null || values2 == null){
//				return QueryIOConstants.RETURN_FAILURE + "_Namenode not responding";
				return QueryIOConstants.RETURN_FAILURE + "_Host " + host.getHostIP() + " not responding";
				
			}
			
//			boolean isImportType = Boolean.parseBoolean(isImport);
			
			MigrationInfo migrationInfo = new MigrationInfo();
			
							
			if(isImportType)
			{
				migrationInfo.setSourcePath(path); 
				migrationInfo.setDestinationPath(hdfsPath);					
			}
			else
			{
				migrationInfo.setDestinationPath(path); 
				migrationInfo.setSourcePath(hdfsPath);
			}
			
			migrationInfo.setNamenodeId(nodeId);
			migrationInfo.setImportType(isImportType);
			migrationInfo.setTitle(title);
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setDataStore(dataStore);			
			migrationInfo.setProgress(0);
			migrationInfo.setStatus("Listing objects");		
			migrationInfo.setUnzip(unzip);
			migrationInfo.setCompressionType(compressionType);
			migrationInfo.setEncryptionType(encryptionType);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("String nodeId, String title, String dataStore, String hostName, String port, String hdfsPath, boolean importType, boolean unzip: " 
					+ nodeId + ", " + title + ", " +  dataStore + ", " + hostName + ", " + hdfsPath + ", " + isImportType + ", " + compressionType + ", " + encryptionType);
			
			MigrationInfoDAO.insert(connection, migrationInfo);			
			
			migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());			
			
			Thread thread = null;
			
			Configuration conf = RemoteManager.getNameNodeConfiguration(nodeId);
			
			String loginUser = RemoteManager.getLoggedInUser();
			String loginUserGroup = RemoteManager.getDefaultGroupForUser(loginUser);
			
			int portInt = Integer.parseInt(port);
			
//			ArrayList<String> paths = new ArrayList<String>();
//			paths.add(0, "/new_query_3.xls");
//			paths.add(1, "/file.txt");
//			paths.add(2, "/jaxb-api.jar");
			
			if(migrationInfo.isImportType())
				thread = new SSHImportMigrationThread(loginUser, loginUserGroup, migrationInfo, hostName, portInt, userName, password, path, isSSH, conf, true);
			else
				thread = new SSHExportMigrationThread(loginUser, loginUserGroup, migrationInfo, hostName, portInt, userName, password, path, isSSH, conf, true);
			
			
			if(migrationThreadMap == null){
				migrationThreadMap = new HashMap();
			}
			migrationThreadMap.put(migrationInfo.getId() , thread);
			thread.start();
			return QueryIOConstants.RETURN_SUCCESS + "_" + "Migration Started Successfully";
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + "Migration failed";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static boolean stopMigration(int id){		
		Connection connection = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			MigrationInfo migrationInfo = MigrationInfoDAO.getById(connection, id);
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_STOPPED);
			MigrationInfoDAO.update(connection, migrationInfo);			
			
			if(migrationThreadMap != null){
				Thread thread = (Thread) migrationThreadMap.get(id);
				if(thread != null){
					thread.interrupt();
				}
			}			
			return true;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;		
	}
	
	public static boolean deleteMigration(ArrayList id){		
		Connection connection = null;
		try
		{	
			if(migrationThreadMap != null){
				Thread thread = (Thread) migrationThreadMap.get(id);
				if(thread != null){
					thread.interrupt();
				}					
			}
			connection = CoreDBManager.getQueryIODBConnection();
			
			for(int i=0; i<id.size(); i++)
				MigrationInfoDAO.delete(connection, Integer.parseInt(id.get(i).toString()));			
						
			return true;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;		
	}
	
	public static void stopAllMigrationThreads(){
		if(migrationThreadMap != null){
			if(migrationThreadMap.size() != 0){
				final Iterator itr = migrationThreadMap.keySet().iterator();
				int key;
				while (itr.hasNext())
				{
					key = (Integer)itr.next();
					Thread migrationThread = (Thread) migrationThreadMap.get(key);
					if(migrationThread != null && migrationThread.isAlive()){
						migrationThread.interrupt();	
					}
									
				}
				migrationThreadMap.clear();
			}
		}
	}
}
