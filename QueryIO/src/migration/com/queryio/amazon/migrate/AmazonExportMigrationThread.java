package com.queryio.amazon.migrate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.poi.util.IOUtils;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;
import com.amazon.s3.Utils;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.stream.util.QIODFSInputStream;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;

public class AmazonExportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;
	
	private AWSAuthConnection conn;
	private FileSystem dfs;
	
	boolean flag;
	
	boolean overwrite;
	String fsDefaultName;
	
	String nameNodeId = null;
	
	Configuration conf;
	
	public AmazonExportMigrationThread(String user, String group, MigrationInfo migrationInfo, String accessKey, String secureAccessKey, Configuration conf, boolean overwrite) throws Exception{
		super(user);
		Thread.currentThread().setName(user);
		
		this.migrationInfo = migrationInfo;
		conn = new AWSAuthConnection(accessKey, secureAccessKey, migrationInfo.isSecure(), Utils.DEFAULT_HOST, CallingFormat.getSubdomainCallingFormat());
		
		this.conf = conf;
		
		if(EnvironmentalConstants.isUseKerberos()){
			Connection connection = null;
			try{
				connection = CoreDBManager.getQueryIODBConnection();
				
				User us = UserDAO.getUserDetail(connection, user);
				
				UserGroupInformation.setConfiguration(conf);
//				UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));		
				
				dfs = FileSystem.get(conf);
				dfs.setConf(conf);
			}
			finally
			{
				try
				{
					CoreDBManager.closeConnection(connection);
				} 
				catch(Exception e)
				{
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		} 
		else 
		{
			dfs = QIODFSUtils.getFileSystemAs(user, group, conf);
		}
		
		this.overwrite = overwrite;
		this.nameNodeId = conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID);
	}
	
	@Override
	public void interrupt()
	{
		
		flag = false;
		
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_STOPPED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			MigrationInfoDAO.update(connection, migrationInfo);
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
		
		super.interrupt();
		
		try 
		{
			if(dfs != null)
				dfs.close();
		} 
		catch (IOException e) 
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	@Override
	public void run(){
		flag = true;
		Connection connection = null;		
		try
		{			
			int firstSlashIndex = migrationInfo.getDestinationPath().indexOf("/");
			String bucketName = null;
			String innerKey = null;
			if(firstSlashIndex != -1)
			{
				bucketName = migrationInfo.getDestinationPath().substring(0,firstSlashIndex);
				innerKey = migrationInfo.getDestinationPath().substring(firstSlashIndex);
			}
			else
			{
				bucketName = migrationInfo.getDestinationPath();
				innerKey = null;
			}		
			
			if(innerKey!=null)
			{
				if(innerKey.endsWith("/"))
				{
					innerKey  = innerKey.substring(0, innerKey.length()-1);
				}
			}
			
			int successCount = 0;
			int failCount = 0;
			
			createBucket(bucketName);
			
			List filePaths = getAllFilePaths(migrationInfo.getSourcePath());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("size of bucket " + migrationInfo.getSourcePath() + " " + filePaths.size());
			unitCount = 5/100 * filePaths.size();
			if(unitCount == 0)
				unitCount = 1;
			for(int i = 0; i < filePaths.size(); i ++)
			{
				if(flag)
				{
					if(i % unitCount == 0)
					{
						migrationInfo.setProgress((successCount * 100 / filePaths.size()));
						migrationInfo.setStatus("Migrated "+ successCount +" of "+ filePaths.size() +" objects");
						try
						{	
							connection = CoreDBManager.getQueryIODBConnection();
							MigrationInfoDAO.update(connection, migrationInfo);
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
					}
					
					try
					{
						String fileName =  (String)filePaths.get(i);
						
						byte [] data = null;
						Map metadata = null;
						try
						{
							data = getObject(fileName);
							metadata = getMetadata(fileName);							
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
							failCount ++;
							throw e;
						}
					
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Destination path: " + migrationInfo.getDestinationPath());
						
//						createBucket(migrationInfo.getDestinationPath());
						
						if(overwrite)
						{						
							try
							{
								deleteObject(bucketName, innerKey!=null ? (innerKey+fileName) : fileName);
							}
							catch(Exception e)
							{
								AppLogger.getLogger().fatal(e.getMessage(), e);
								failCount ++;
								throw e;
							}
						}
						try
						{
							createObject(bucketName, innerKey!=null ? (innerKey+fileName) : fileName, data, metadata);
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
							failCount ++;
							throw e;
						}
						
						successCount ++;
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal("Could not save file. Source: " + filePaths.get(i) + ", exception: " + e.getMessage(), e);
					}
					
					migrationInfo.setProgress((successCount * 100 / filePaths.size()));
					migrationInfo.setStatus("Migrated "+ successCount +" of "+ filePaths.size() +" objects." + (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
				}												
			}
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		}
		finally
		{
			try 
			{
				if(dfs != null)
					dfs.close();
			} 
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		if(flag)
		{
			try
			{	
				connection = CoreDBManager.getQueryIODBConnection();
				MigrationInfoDAO.update(connection, migrationInfo);
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
		}						
	}
	
	private Map getMetadata(String filePath) throws Exception
	{
		Map map = new HashMap();	
		Connection connection  = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);
			
			IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(this.conf, filePath, null, null);

			stmt = connection.prepareStatement("SELECT * FROM " + UserDefinedTagResourceFactory.getTableName(tagParser, filePath) + " WHERE FILEPATH=?");
			stmt.setString(1, filePath);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(rs.next())
			{
				for(int i=1; i<=rsmd.getColumnCount(); i++)
				{
					map.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
				}
			}
		} 
		finally 
		{
			try
			{
				DatabaseFunctions.closeResultSet(rs);
			} 
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(stmt);
			} 
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception ex)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}

	private List getAllFilePaths(String str) throws Exception{
		Path path = new Path(str);
		FileStatus stat = dfs.getFileStatus(path);
		List list = new ArrayList();
		if(!stat.isDirectory()){
			list.add(str);
		}else{
			FileStatus[] stats = dfs.listStatus(path);
			for(int i = 0; i < stats.length; i ++){
				if(!str.endsWith("/"))
					str += "/";
				list.addAll(getAllFilePaths(str + stats[i].getPath().getName()));
			}
		}
		return list;	    
	}
	
	private byte[] getObject(String fileName)throws Exception
	{
		DFSInputStream dfsInputStream = null;
		QIODFSInputStream qioInputStream = null;
		try
		{
			DistributedFileSystem fs = (DistributedFileSystem) dfs;
			dfsInputStream = (DFSInputStream) fs.getClient().open(fileName);
			try 
			{
				qioInputStream = new QIODFSInputStream(dfsInputStream, fs, fileName);
			} 
			catch (Exception e) 
			{
				if (dfsInputStream != null) 
				{
					dfsInputStream.close();
				}
				throw e;
			}
			
			byte b[] = new byte[qioInputStream.available()];
			IOUtils.readFully(qioInputStream, b);
			return b;
		}
		finally 
		{
			try
			{
				if(qioInputStream!=null)
				{
					qioInputStream.close();
				} 
			} 
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private void createBucket(String bucketName) throws Exception 
	{
		if(bucketName.startsWith("/"))
			bucketName = bucketName.substring(1);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("creating bucket " + bucketName);
		Response response = conn.createBucket(bucketName, null);
		if(response.connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
        	throw new RuntimeException("couldn't create bucket: expected " + HttpURLConnection.HTTP_OK + " but got " + response.connection.getResponseCode() +" Response Message: " + response.connection.getResponseMessage());    	   
        }
	}

	private void createObject(String bucketName, String key, byte[] data, Map metadata) throws Exception
	{		
		if(key.startsWith("/"))
			key = key.substring(1);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("creating object " + key +" of size " + data.length + " bytes in bucket "+ bucketName);		
		Response response = conn.put(bucketName, key, new S3Object(data, metadata), null);
		if(response.connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
        	throw new RuntimeException("couldn't create object: expected " + HttpURLConnection.HTTP_OK + " but got " + response.connection.getResponseCode() +" Response Message: " + response.connection.getResponseMessage());    	   
        }
	}
	private void deleteObject(String bucketName, String key) throws Exception
	{		
		if(key.startsWith("/"))
			key = key.substring(1);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("deleting object " + key +" from bucket "+ bucketName);
		Response response = conn.delete(bucketName, key, null);
		if(response.connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
        	throw new RuntimeException("couldn't delete entry: expected " + HttpURLConnection.HTTP_NO_CONTENT + " but got " + response.connection.getResponseCode() +" Response Message: " + response.connection.getResponseMessage());    	   
        }
	}
}
