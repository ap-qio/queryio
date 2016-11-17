package com.queryio.ftp.migrate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSInputStream;
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

public class FTPExportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;
	
	private FileSystem dfs;
	
	boolean flag;
	
	boolean overwrite;
	String fsDefaultName;
	FTPClient client;
	
	private int ftpPort;
	private String ftpHost;
	private String userName;
	private String password;
	
	public FTPExportMigrationThread(String user, String group, MigrationInfo migrationInfo, String userName, String password, Configuration conf, boolean overwrite) throws Exception
	{
		super(user);
		Thread.currentThread().setName(user);
		
		this.migrationInfo = migrationInfo;

		String ftpHost = migrationInfo.getDestinationPath().split("/")[2];
		String ftpPort = String.valueOf(FTP.DEFAULT_PORT);
		if(ftpHost.contains(":"))
		{
			String[] arr = ftpHost.split(":");
			ftpHost = arr[0];
			ftpPort = arr[1];
		}
			
		this.ftpPort = Integer.parseInt(ftpPort);
		this.ftpHost = ftpHost;
		this.userName = userName;
		this.password = password;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("FTP export request received");
		
		try {			
			this.connect();
		} catch (Exception e) {
			updateMigrationStatusInDB();
			throw e;
		}
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connected to FTP server");
		
		if(EnvironmentalConstants.isUseKerberos())
		{
			Connection connection = null;
			try
			{
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
		
		this.overwrite = overwrite;
	}
	
	private void updateMigrationStatusInDB() {
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			migrationInfo.setStatus("Migration Failed. Could not connect to FTP Server.");
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			MigrationInfoDAO.update(connection, migrationInfo);
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}finally{
			try{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e){
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public void connect() throws SocketException, IOException{
		if(client!=null){
			try{
				client.disconnect();
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		client = new FTPClient();
		client.setDefaultPort(this.ftpPort);
		client.connect(this.ftpHost);
		client.login(this.userName, this.password);
		
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.setFileType(FTP.BINARY_FILE_TYPE);
		
		client.setControlKeepAliveTimeout(300);
	}
	
	@Override
	public void interrupt(){
		
		flag = false;
		
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_STOPPED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			MigrationInfoDAO.update(connection, migrationInfo);
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}finally{
			try{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e){
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
				client.disconnect();
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
			
			String pathPrefix = migrationInfo.getDestinationPath().replace("ftp://", "");
			pathPrefix = pathPrefix.substring(pathPrefix.indexOf("/"));
			pathPrefix = pathPrefix.trim();
			if(pathPrefix.endsWith("/"))
			{
				pathPrefix = pathPrefix.substring(0, pathPrefix.length()-1);
			}
			
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
							{
								throw e;
							}
							else 
							{
								try
								{
									
									if(client.isConnected())
									{
										connectRetry = 0;
										failCount ++;
										continue;
									}
									else
									{
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("IOException occurred...Reconnecting to FTP server...");
										this.connect();
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful");
									}
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
						String effectiveDestinationPath = migrationInfo.getSourcePath();
						
						if(effectiveDestinationPath.equals(filePath.toString())) {
							effectiveDestinationPath = "";
						} else {
							if( ! effectiveDestinationPath.endsWith("/")) {
								effectiveDestinationPath += "/";
							}
							effectiveDestinationPath = filePath.toString().substring(effectiveDestinationPath.length());							
						}
						
						if(effectiveDestinationPath.isEmpty()) {
							destPath = new Path(pathPrefix + "/" + filePath.getName());
						} else {							
							destPath = new Path(pathPrefix + "/" + effectiveDestinationPath);
						}
						
						String parent = destPath.toString().substring(0, destPath.toString().lastIndexOf("/")); 
						
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating directory on remote server: " + parent);
						
						try
						{
							ftpCreateDirectoryTree(this.client, parent);
							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Saving data: " + destPath.toString());
							success = client.storeFile(destPath.toString(), inputStream);
						}
						catch(IOException e)
						{
							throw e;
						}
						
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Saving file: " + destPath.toString() + " " + (success ? "successful" : "failed"));
						successCount ++;
						connectRetry = 0;
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
					client.disconnect();
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
	
	private static void ftpCreateDirectoryTree( FTPClient client, String dirTree ) throws IOException 
	{
		client.changeWorkingDirectory("/");
		  boolean dirExists = true;

		  //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
		  String[] directories = dirTree.split("/");
		  for (String dir : directories ) {
		    if (!dir.isEmpty() ) 
		    {
		      if (dirExists) 
		      {
		        dirExists = client.changeWorkingDirectory(dir);
		      }
		      if (!dirExists) 
		      {
		        if (!client.makeDirectory(dir)) 
		        {
		          throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString()+"'");
		        }
		        if (!client.changeWorkingDirectory(dir)) 
		        {
		          throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString()+"'");
		        }
		      }
		    }
		  }     
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
			qioInputStream = new QIODFSInputStream(dfsInputStream, fs, path.toUri().toString());
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

	public  long writeToStream(InputStream stream, OutputStream baos) throws IOException 
	{
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) 
		{
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("written " + readSoFar + " bytes");
		}
		return readSoFar;
	}
	
	public static void main(String[] args)
	{
		FTPClient client = new FTPClient();

	    try 
	    {
	        client.connect("192.168.0.16", 5660);
	        client.login("admin", "admin");

	        Configuration conf = new Configuration();
	        conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, "hdfs://192.168.0.12:9000");
	        
	        FileSystem dfs = FileSystem.get(conf);
	        
	        Path path = new Path("/AMATEST/Data111/Data/pdf/Doc_1352993880846.pdf");
	        
	        InputStream is = dfs.open(path);

	        System.out.println("Saving to " + new Path("/DC" + path.toString()));
	        
	        client.storeFile("/asd/asda/asd/asd/", is);
	        
	        is.close();
	        client.logout();
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	}
}
