package com.queryio.common.remote;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.Path;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.dao.NNMIgrationInfoDAO;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DBBackupTools;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;

public class DBOperationsManager {
	private static Map<String, Thread> threadMap = new HashMap<String, Thread>(); 
	
	public static void startDBToFileMigration(final String migrationID, final String dbName, final String backupFolder) throws Exception {
		Thread thread = new Thread(migrationID) {
			public void run() {
				
				File dir = new File(backupFolder);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				try {
					DBBackupTools.databaseToFile(dbName, new Path(backupFolder, "ns-metadata.xml").toString(), new Path(backupFolder, "hdfs-metadata.xml").toString(), null);
					
					updateDBMigrationStatus(migrationID, new Timestamp(System.currentTimeMillis()), QueryIOConstants.PROCESS_STATUS_COMPLETED, null);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
					updateDBMigrationStatus(migrationID, new Timestamp(System.currentTimeMillis()), QueryIOConstants.PROCESS_STATUS_FAILED, e.getMessage());
				}
				threadMap.remove(this);
			}
		};
		thread.start();
		threadMap.put(migrationID, thread);
	}

	public static void terminateDBMigration(String migrationID) {
		Thread thread;
		if((thread = threadMap.get(migrationID))!=null) {
			thread.interrupt();
		}
		threadMap.remove(migrationID);
	}
	
	public static void updateDBMigrationStatus(String migrationID, Timestamp endTime, String status, String error) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			
			NNMIgrationInfoDAO.updateMigrationInfo(connection, migrationID, endTime, status, error);
		} catch(Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if(connection!=null) {
					CoreDBManager.closeConnection(connection);
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static void deleteBackupData(String backupFolder) throws Exception
	{
		File file = new File(backupFolder);
		if (file.exists())
		{
			StaticUtilities.deleteFile(file);
		}
	}
}