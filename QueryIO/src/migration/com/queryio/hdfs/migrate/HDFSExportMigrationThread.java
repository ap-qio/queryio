package com.queryio.hdfs.migrate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSInputStream;

public class HDFSExportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;
	
	private FileSystem dfs;
	private FileSystem client;
	
	boolean flag;
	
	boolean overwrite;
	String fsDefaultName;
	
	private String host;
	private String path;
	
	private String loginUser;
	private String loginUserGroup;
	
	private String hdfsUser;
	private String hdfsUserGroup;
	
	public HDFSExportMigrationThread(String user, String group, MigrationInfo migrationInfo, String host, String path, String userHDFS, String groupHDFS, Configuration conf, boolean overwrite) throws Exception
	{
		super(user);
		Thread.currentThread().setName(userHDFS);
		
		this.migrationInfo = migrationInfo;

		this.loginUser = user;
		this.loginUserGroup = group;
		
		this.hdfsUser = userHDFS;
		this.hdfsUserGroup = groupHDFS;
		
		this.overwrite = overwrite;
		
		this.host = host;
		this.path = path;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("HDFS export request received");
		
		this.connect();
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connected to HDFS");
		
		if(EnvironmentalConstants.isUseKerberos()){
			Connection connection = null;
			try{
				connection = CoreDBManager.getQueryIODBConnection();
				
				User us = UserDAO.getUserDetail(connection, user);
				
				UserGroupInformation.setConfiguration(conf);
//				UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));		
				
				dfs = FileSystem.get(conf);
				dfs.setConf(conf);
			} finally {
				try{
					CoreDBManager.closeConnection(connection);
				} catch(Exception e){
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		} else {
			dfs = QIODFSUtils.getFileSystemAs(user, group, conf);
		}
		
	}
	
	public void connect() throws SocketException, IOException
	{
		Configuration conf = new Configuration();
		conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, this.host + this.path);
		
		client = FileSystem.get(conf);
		client.setConf(conf);
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
		try 
		{
			if(client != null)
				client.close();
		} 
		catch (IOException e) 
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	@Override
	public void run()
	{
		flag = true;
		Connection connection = null;		
		try
		{			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Listing file paths");
			List filePaths = getAllFilePaths(migrationInfo.getSourcePath());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("size of folder " + migrationInfo.getSourcePath() + " " + filePaths.size());
			unitCount = 5/100 * filePaths.size();
			if(unitCount == 0)
				unitCount = 1;

			int connectRetry=0;
			int successCount = 0;
			int failCount = 0;
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Initiating export...");
			Path destPath;
			boolean success;
			for(int i = 0; i < filePaths.size(); i ++)
			{
				if(flag)
				{
					if(i % unitCount == 0)
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
					
					Path filePath =  (Path)filePaths.get(i);
					InputStream inputStream = null;
					Path importedFilePath = getImportedFilePath(migrationInfo.getDestinationPath(), filePath);

					try
					{
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Fetching object: " + filePath);
						
						try
						{
							inputStream = getObject(filePath);
						}
						catch(IOException e)
						{
							if(connectRetry == 3)
								throw e;
							else 
							{
								try
								{
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("IOException occurred...Reconnecting to FTP server...");
									this.connect();
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful");
									connectRetry = 0;
								} 
								catch(Exception ex)
								{
									Thread.sleep(1000);
									connectRetry ++;
									AppLogger.getLogger().fatal("Could not connect to FTP server, exception: " + ex.getMessage(), ex);
								}
								i--;
								continue;
							}
						}
						
						if(inputStream != null)
						{
							try
							{
								createObject(importedFilePath, inputStream);
							}
							catch(Exception e)
							{
								throw e;
							}
							successCount ++;
							connectRetry = 0;
						}
						else 
						{
							throw new IOException("Output stream could not be opened for source file: " + filePath);
						}
					} 
					
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal(e.getMessage(), e);
						failCount ++;
					} 
					finally 
					{
						if(inputStream!=null)
						{
							try
							{
								inputStream.close();
							} 
							catch(Exception e)
							{
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}
						}
					}
					
					migrationInfo.setProgress((successCount * 100 / filePaths.size()));
					migrationInfo.setStatus("Migrated "+successCount +" of "+ filePaths.size() +" objects." + (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
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
			try 
			{
				if(client != null)
					client.close();
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
	
	private Path createObject(Path objectPath, InputStream inputStream) throws Exception
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating object: " + objectPath.toString());
		
		DFSOutputStream dfsOutputStream = null;
		try
		{
			DistributedFileSystem fs = (DistributedFileSystem) client;
			dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
			client.setOwner(objectPath, this.hdfsUser, this.hdfsUserGroup);
			writeToStream(inputStream, dfsOutputStream);			
		}
		finally
		{
			try
			{
				if(dfsOutputStream != null)
				{
					dfsOutputStream.close();
				}
			} 
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return objectPath;
	}
	
	private Path getImportedFilePath(String bucketName, Path pathObj)
	{
		if(bucketName.endsWith("/"))
			bucketName = bucketName.substring(0, bucketName.length()-1);
		if(!bucketName.startsWith("/"))
			bucketName = "/" + bucketName;
		
		String effectiveDestinationPath = migrationInfo.getSourcePath();
		String path = pathObj.toString();
		if(effectiveDestinationPath.equals(path)) {
			effectiveDestinationPath = "";
		} else {
			if( ! effectiveDestinationPath.endsWith("/")) {
				effectiveDestinationPath += "/";
			}
			effectiveDestinationPath = path.toString().substring(effectiveDestinationPath.length());							
		}
		
		if(effectiveDestinationPath.isEmpty()) {
			path = bucketName + "/" + pathObj.getName();
		} else {							
			path = bucketName + "/" + effectiveDestinationPath;
		}
		
		path = path.replaceAll("//", "/");
		Path objectPath = new Path(path);
		
		return objectPath;
	}

	private List getAllFilePaths(String str) throws Exception
	{
		Path path = new Path(str);
		FileStatus stat = dfs.getFileStatus(path);
		List list = new ArrayList();
		if(!stat.isDirectory())
		{
			list.add(path);
		}
		else
		{
			FileStatus[] stats = dfs.listStatus(path);
			for(int i = 0; i < stats.length; i ++)
			{
				if(!str.endsWith("/"))
					str += "/";
				list.addAll(getAllFilePaths(str + stats[i].getPath().getName()));
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("list: " + stats[i].getPath().getName());
			}
		}
		return list;	    
	}
	
	private BufferedInputStream getObject(Path path)throws Exception
	{
		DFSInputStream dfsInputStream = null;
		QIODFSInputStream qioInputStream = null;
		
		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		dfsInputStream = (DFSInputStream) fs.getClient().open(path.toUri().toString());
		try 
		{
			qioInputStream = new QIODFSInputStream(dfsInputStream, fs, path.toUri().getPath());
		} 
		catch (Exception e) 
		{
			if (dfsInputStream != null) 
			{
				dfsInputStream.close();
			}
			throw e;
		}
		
		return new BufferedInputStream(qioInputStream);
	}

	public long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("written " + readSoFar + " bytes");
		}
		return readSoFar;
	}
	
	public static void main(String[] args)
	{
		
	}
}