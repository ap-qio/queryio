package com.queryio.ftp.migrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSOutputStream;

public class FTPImportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private FileSystem dfs;
	FTPClient client;
	boolean flag;
	
	boolean overwrite;
	
	private String sourcePath;
	
	private int ftpPort;
	private String ftpHost;
	private String userName;
	private String password;
	
	long totalFileCount = 1;
	
	private String loginUser;
	private String loginUserGroup;
	
	Configuration conf = null;
	
	public FTPImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String userName, String password, Configuration conf, boolean overwrite) throws Exception{
		super(user);
		
		this.loginUser = user;
		this.loginUserGroup = group;
		
		Thread.currentThread().setName(user);
//		TagParserConfigManager.init();
		this.migrationInfo = migrationInfo;

		String[] array = migrationInfo.getSourcePath().split("/");
		String ftpHost = array[2];
		String ftpPort = String.valueOf(FTP.DEFAULT_PORT);
		if(ftpHost.contains(":")){
			String[] arr = ftpHost.split(":");
			ftpHost = arr[0];
			ftpPort = arr[1];
		}
		sourcePath = "";
		for(int i = 3; i < array.length; i ++){
			sourcePath += "/" + array[i];	
		}

		this.ftpPort = Integer.parseInt(ftpPort);
		this.ftpHost = ftpHost;
		this.userName = userName;
		this.password = password;
		
		this.connect();
		
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
		
		this.conf = conf;
		
		this.overwrite = overwrite;
	}
	
	public void connect() throws SocketException, IOException{
		if(client!=null)
		{
			try
			{
				client.disconnect();
			} 
			catch(Exception e)
			{
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
	public void run(){
		flag = true;
		Connection connection = null;		
		try{			
			
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser, loginUserGroup);
			
			List filePaths;
			if(isAbsoluteFilePath(sourcePath))
			{
				filePaths = new ArrayList();
				filePaths.add(new Path(sourcePath));
			}
			else 
			{
				filePaths = getAllFilePaths(sourcePath);
			}
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("size of folder " + sourcePath + " " + filePaths.size());
			unitCount = 5/100 * filePaths.size();
			if(unitCount == 0)
				unitCount = 1;
			int successCount = 0;
			int failCount = 0;
			int connectRetry=0;
			totalFileCount = filePaths.size();
			connection = CoreDBManager.getQueryIODBConnection();
			for(int i = 0; i < filePaths.size(); i ++)
			{
				if(flag)
				{
					if(i % unitCount == 0)
					{
						try	
						{	
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
								
							}
							catch(Exception e)
							{
								AppLogger.getLogger().fatal("Error closing database connection.", e);
							}
						}				
					}
					Path filePath =  (Path)filePaths.get(i);
					InputStream inputStream = null;
					Path importedFilePath = getImportedFilePath(migrationInfo.getDestinationPath(), filePath.toUri().getPath());
					
					try
					{
						try
						{
							inputStream = getObject(filePath);
						}
						catch(IOException e)
						{
							Thread.sleep(3000);
							if(connectRetry == 3)
							{
								throw e;
							} 
							else 
							{
								try
								{
									AppLogger.getLogger().fatal("IOException occurred..., exception: " + e.getMessage(), e);
									
									if(client.isConnected())
									{
										connectRetry = 0;
										failCount ++;
										continue;
									}
									else
									{
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reconnecting to FTP server...");
										this.connect();
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful");
									}
									connectRetry = 0;
								} 
								catch(Exception ex)
								{
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
								migrationInfo.setStatus("Migrating "+(i+1)+" object of "+totalFileCount+" object");
								migrationInfo.setProgress((i*100)/totalFileCount);
								MigrationInfoDAO.update(connection, migrationInfo);
								createObject(importedFilePath, inputStream, overwrite, migrationInfo.isUnzip());
							}
							catch(Exception e)
							{
								try
								{
									dfs.delete(importedFilePath, true);
								} 
								catch(Exception ex)
								{
									AppLogger.getLogger().fatal(ex.getMessage(), ex);
								}
								throw e;
							}
							successCount ++;
							connectRetry = 0;
						}
						else 
						{
							throw new IOException("Output stream could not be opened for source file: " + filePath);
						}
						
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Completing pending command");
						
						try
						{
							this.client.completePendingCommand();
						} 
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
						
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Completed pending command");
					} 
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal("Could not save file. Source: " + filePath + ", exception: " + e.getMessage(), e);
						
						failCount ++;
					}
					finally 
					{
						try
						{
							if(inputStream!=null)	
								inputStream.close();
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
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
			if(connection != null){
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
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
	
	static void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, int level) throws IOException 
	{
        String dirToList = parentDir;
        if (!currentDir.equals("")) 
        {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
        if (subFiles != null && subFiles.length > 0) 
        {
            for (FTPFile aFile : subFiles) 
            {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) 
                {
                    // skip parent directory and directory itself
                    continue;
                }
                for (int i = 0; i < level; i++) 
                {
                    System.out.print("\t");
                }
                if (aFile.isDirectory()) 
                {
                    System.out.println("[" + currentFileName + "]");
                    listDirectory(ftpClient, dirToList, currentFileName, level + 1);
                } 
                else 
                {
                    System.out.println(currentFileName);
                }
            }
        }
    }
	
	public boolean isAbsoluteFilePath(String str) throws IOException
	{
		str = str.trim();
		
		if(str.endsWith("/") && str.length()>1)
		{
			str = str.substring(0, str.length()-1);
		} 
		
		String parent = str.substring(0, str.lastIndexOf("/")+1);
		String child = str.substring(str.lastIndexOf("/")+1); 
		
		boolean isAbsFilePath = false;
		FTPFile[] fileList = client.listFiles(parent);
		for(int i=0; i<fileList.length; i++)
		{
			if( ! fileList[i].isFile())	continue;
			
			if(child.equals(fileList[i].getName()))
				isAbsFilePath = true;
		}
		
		return isAbsFilePath;
	}

	public List getAllFilePaths(String str) throws IOException
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Listing paths for: " + str);
		
		ArrayList list = new ArrayList();
		
		FTPFile[] fileList = null;
		try
		{
			fileList = client.listFiles(str);
		} 
		catch(SocketException e) 
		{
			this.connect();
			fileList = client.listFiles(str);
		} 
		
		for(int i=0; i<fileList.length; i++)
		{
			if( ! fileList[i].isFile())	continue;
			
			if(str.equals(fileList[i].getName()))
			{
				list.add(new Path(fileList[i].getName()));
				return list;
			}
			
			if(str.endsWith("/"))	if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("File: " + new Path(str + fileList[i].getName()).toString());
			else if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("File: " + new Path(str + "/" + fileList[i].getName()).toString());
			
			if(str.endsWith("/")) list.add(new Path(str + fileList[i].getName()));
			else list.add(new Path(str + "/" + fileList[i].getName()));
		}
		
		FTPFile[] dirList = null;
		try
		{
			dirList = client.listDirectories(str);
		} 
		catch(SocketException e) 
		{
			this.connect();
			dirList = client.listDirectories(str);
		}
		
		for(int i=0; i<dirList.length; i++)
		{
			if( ! fileList[i].isDirectory())	
				continue;
			
			if(str.endsWith("/"))
				list.addAll(getAllFilePaths(new Path(str + dirList[i].getName()).toString()));
			else
				list.addAll(getAllFilePaths(new Path(str + "/" + dirList[i].getName()).toString()));
		}
		return list;	
	}

	private InputStream getObject(Path path)throws Exception
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Retreiving input stream for file: " + path.toString());
		InputStream inputStream = client.retrieveFileStream(path.toString());
		return inputStream;
	}

	private void createFolder(String bucketName) throws Exception 
	{		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("creating folder " + bucketName);
		Path path = new Path("/", bucketName); 
		dfs.mkdirs(path);
		dfs.setOwner(path, this.loginUser, this.loginUserGroup);
	}

	private Path getImportedFilePath(String bucketName, String path)
	{
		if(bucketName.endsWith("/"))
			bucketName = bucketName.substring(0, bucketName.length()-1);
		if(!bucketName.startsWith("/"))
			bucketName = "/" + bucketName;
		path = bucketName + path;
		path = path.replaceAll("//", "/");
		Path objectPath = new Path(path);
		
		return objectPath;
	}
	
	private Path createObject(Path objectPath, InputStream inputStream, boolean overwrite, boolean unzip) throws Exception
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating object: " + objectPath.toString());
		
		if(unzip && StaticUtilities.getFileExtension(objectPath.toString()).equalsIgnoreCase("ZIP"))
		{
			objectPath = new Path(objectPath.toString().replace("." + StaticUtilities.getFileExtension(objectPath.toString()), ""));
			
			ZipInputStream stream = null;
			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			Connection dbConnection =  null;
			try
			{
				stream = new ZipInputStream(inputStream);
				dbConnection = CoreDBManager.getQueryIODBConnection();
				
				ZipEntry entry = stream.getNextEntry();
				String fileName;
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				while (entry != null) 
				{
					fileName = entry.getName();
					if ( ! entry.isDirectory()) 
					{
						try
						{
							Path path = new Path(objectPath, fileName);
							dfsOutputStream = (DFSOutputStream) fs.getClient().create(path.toUri().toString(), true); 
							try 
							{
								qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null, path.toUri().toString());
							} 
							catch (Exception e) 
							{
								if (dfsOutputStream != null) 
								{
									dfsOutputStream.close();
								}
								throw e;
							}
							
							dfs.setOwner(path, this.loginUser, this.loginUserGroup);
							writeToStream(stream, qioOutputStream, dbConnection);
						}
						catch(IOException e)
						{
							throw e;
						}
						finally 
						{
							if(qioOutputStream != null){
								try
								{
									qioOutputStream.close();
								} 
								catch(Exception e)
								{
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
							}
						}
					} 
					else 
					{
				    	dfs.mkdirs(new Path(objectPath, fileName));
				    	dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
				    }
				
					stream.closeEntry();
				    entry = stream.getNextEntry();
				}
			}
			finally 
			{
				if(stream!=null)
				{
					try
					{
						stream.close();
					} 
					catch(Exception e)
					{
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
				if(dbConnection != null){
					CoreDBManager.closeConnection(dbConnection);
				}
			}
		}
		else
		{
			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			Connection dbConnection =  null;
			
			
			try
			{
				dbConnection = CoreDBManager.getQueryIODBConnection();
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
				try 
				{
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null, objectPath.toUri().toString());
				} 
				catch (Exception e) 
				{
					if (dfsOutputStream != null) 
					{
						dfsOutputStream.close();
					}
					throw e;
				}
				dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
				writeToStream(inputStream, qioOutputStream,dbConnection);			
			}
			catch(IOException e)
			{
				throw e;
			}
			finally
			{
				try
				{
					if(qioOutputStream != null)
					{
						qioOutputStream.close();
					}
					if(dbConnection != null){
						CoreDBManager.closeConnection(dbConnection);
					}
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
		return objectPath;
	}
	public  long writeToStream(InputStream stream, OutputStream baos, Connection dbConnection) throws Exception
	{
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		long readSoFar = 0;
		long available  = stream.available();
		if(available == 0)
			available = 1;
		long prog = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) 
		{
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
			if(totalFileCount == 1 ){
				prog = readSoFar * 100 / available;
				if(prog > 100){
					prog = 100;
					migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
				}
				migrationInfo.setProgress(prog);
				MigrationInfoDAO.update(dbConnection, migrationInfo);
			}
		}
		return readSoFar;
	}
	
	public static List getAllFilePathsList(FTPClient client, String str) throws IOException
	{
		ArrayList list = new ArrayList();
		
		str = str.trim();
		
		if(str.endsWith("/") && str.length()>1)
		{
			str = str.substring(0, str.length()-1);
		} 
		
		String parent = str.substring(0, str.lastIndexOf("/")+1);
		String child = str.substring(str.lastIndexOf("/")+1); 
		
		boolean isAbsFilePath = false;
		FTPFile[] fileList = client.listFiles(parent);
		for(int i=0; i<fileList.length; i++)
		{
			if( ! fileList[i].isFile())	continue;
			
			if(child.equals(fileList[i].getName()))
				isAbsFilePath = true;
		}
		
		return list;
	}
	
	public static void main(String[] args) throws SocketException, IOException
	{
		FTPClient client = new FTPClient();
		
		Integer.parseInt("21");
		client.connect("ftp.ncdc.noaa.gov");
		client.login("anonymous", "a");
		
//		client.setDefaultPort(Integer.parseInt("5660"));
//		client.connect("192.168.0.3");
//		client.login("a", "a");
		
//		getAllFilePathsList(client, "/pub/data/gsod/1933/");
		System.out.println(getAllFilePathsList(client, "/welcome.msg"));
		
		
	}
}
