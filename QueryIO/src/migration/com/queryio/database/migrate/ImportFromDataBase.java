package com.queryio.database.migrate;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;

public class ImportFromDataBase implements Runnable {
	
	private MigrationInfo migrationInfo;
	private int unitCount = 10;
	
	boolean flag;
	
	boolean overwrite;
	private String username;
	
	private String loginUser;
	public static String hdfsuri;
	private String loginUserGroup;
	
	private Connection dbconnection = null;		
	private Statement dbstatement = null;
	
	private Configuration conf = null;
	 String tableName = "";
	private PoolingDataSource ps;
	
	public static String selectQuery = "SELECT * FROM ";
	
	public ImportFromDataBase(String user, String group , MigrationInfo migrationInfo, PoolingDataSource ps, String table,  Configuration conf ,String uri, boolean overwrite) throws Exception{
		
		this.migrationInfo = migrationInfo;
		this.loginUser = user;
		this.loginUserGroup = group;
		this.conf = conf;
		hdfsuri = uri;
		this.tableName = table;
		this.ps = ps;
	}
	
	@Override
	public void run(){

		flag = true;
		Thread.currentThread().setName(this.loginUser);
		try
		{
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), this.loginUser, this.loginUserGroup);
			
			AppLogger.getLogger().debug("Initiating import");
			
			AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			if(unitCount == 0)
				unitCount = 1;
						
			String encryptionType = migrationInfo.getEncryptionType();
			String compressionType = migrationInfo.getCompressionType();
			
			migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
			
			impl(this.tableName , migrationInfo.getDestinationPath(), encryptionType , compressionType);
			
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			AppLogger.getLogger().debug("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		}finally{
			Connection connection = null;
			try{
				connection = CoreDBManager.getQueryIODBConnection();
				MigrationInfoDAO.update(connection, migrationInfo);
			}catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}finally{
				try{
				if(connection!=null){
					CoreDBManager.closeConnection(connection);
				}
				}catch(Exception e){
					AppLogger.getLogger().fatal("Error closing database connection.", e);
				}
			}
			
		}
	}

	public void impl(String tableName ,  String path, String encryptionType, String compressionType){		//make it as run
		String query = selectQuery + tableName;
		AppLogger.getLogger().debug("firing query: " + query);
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;

		BufferedWriter bw = null;
		
		try{
			
			dbconnection = this.ps.getConnection();
			
			AppLogger.getLogger().debug("Config received: " + hdfsuri);
			
			FileSystem dfs = FileSystem.get(new URI(hdfsuri), this.conf );
			if (path.charAt(path.length() - 1) != '/')
				path = hdfsuri + path + "/" + tableName + ".csv";
			else 
				path = hdfsuri + path + tableName + ".csv";

			
			
			bw = new BufferedWriter(new OutputStreamWriter(dfs.create(new Path(path))));

			dbstatement = dbconnection.createStatement();
			
			rs = dbstatement.executeQuery(query);
			
			rsmd = rs.getMetaData();
			int i=1;
			
			while(i<=rsmd.getColumnCount()){
				String columnName = rsmd.getColumnName(i) + " ,";
				if(i == rsmd.getColumnCount())
					columnName = columnName.substring(0 , columnName.length()-2 );
				bw.write(columnName);
				i++;
			}
			
			bw.write(QueryIOConstants.NEW_LINE);
			
			rs.setFetchSize(1000);
			
			while(rs.next()){
				for(i=1;i<=rsmd.getColumnCount()-1;i++){
					bw.write(rs.getString(rsmd.getColumnName(i)) + " ,");
				}
				bw.write(rs.getString(rsmd.getColumnName(i)));
				bw.write(QueryIOConstants.NEW_LINE);
			}
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}finally{
			try{
				if(bw!=null)
					bw.close();
			}catch(Exception e){
				AppLogger.getLogger().fatal("Error closing outputstream connection.", e);
			}
			try{
				if(dbstatement!=null) {
					dbstatement.close();
				}
			}catch(Exception e){
				AppLogger.getLogger().fatal("Error closing statements.", e);
			}
			try{
				if(dbconnection!=null) {
					dbconnection.close();
				}
			}catch(Exception e){
				AppLogger.getLogger().fatal("Error closing connection.", e);
			}
		}
	}
	
}
