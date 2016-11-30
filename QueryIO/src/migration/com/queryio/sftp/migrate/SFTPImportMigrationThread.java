package com.queryio.sftp.migrate;

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
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.cache.SoftRefFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.apache.commons.vfs2.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
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

public class SFTPImportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private FileSystem dfs;
	boolean flag;
	
	boolean overwrite;
	
	private String sourcePath;
	
	private int sftpPort;
	private String sftpHost;
	private String username;
	private String password;
	
	private String loginUser;
	private String loginUserGroup;
	
	Configuration conf = null;
	
	FileSystemOptions fileSystemOptions;
	DefaultFileSystemManager manager;
	
	String rootURI = null;
	
	public SFTPImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String username, String password, Configuration conf, boolean overwrite) throws Exception
	{
		super(user);
		
		this.loginUser = user;
		this.loginUserGroup = group;
		
		Thread.currentThread().setName(user);
	
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

		this.sftpPort = Integer.parseInt(ftpPort);
		this.sftpHost = ftpHost;
		this.username = username;
		this.password = password;
		
		rootURI = "sftp://" + username + "@" + sftpHost + ":" + sftpPort;
		
		sourcePath = rootURI + sourcePath;
		
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
		fileSystemOptions = new FileSystemOptions();
		
		UserAuthenticator auth = new StaticUserAuthenticator(null, username,
				password);
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions,
                auth);
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(fileSystemOptions, "no");
		SftpFileSystemConfigBuilder.getInstance( ).setUserDirIsRoot(fileSystemOptions, false);
		
        SftpFileProvider provider = new SftpFileProvider();
        
        manager = new DefaultFileSystemManager();
        
        manager.addProvider("sftp", provider);
        
        manager.setFilesCache(new SoftRefFilesCache());
        
        manager.init();
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
			dfs.close();
		} 
		catch (IOException e) 
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		try 
		{
			if(manager != null)
				manager.close();
		} 
		catch (Exception e) 
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
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser, loginUserGroup);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Listing files");
			
			List filePaths = getAllFilePaths(manager.resolveFile(sourcePath, fileSystemOptions));
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Initiating import");
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("size of folder " + sourcePath + " " + filePaths.size());
			unitCount = 5/100 * filePaths.size();
			if(unitCount == 0)
				unitCount = 1;
			int successCount = 0;
			int failCount = 0;
			int connectRetry=0;
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
					FileObject object =  (FileObject)filePaths.get(i);
					InputStream inputStream = null;
					Path importedFilePath = getImportedFilePath(migrationInfo.getDestinationPath(), object);
					
					try
					{
						try
						{
							inputStream = ((SftpFileObject) object).getInputStream();
						}
						catch(FileSystemException e)
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
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reconnecting to FTP server...");
									this.connect();
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful");
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
							throw new IOException("Output stream could not be opened for source file: " + object.getName().getPath());
						}
					} 
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal("Could not save file. Source: " + object.getName().getPath() + ", exception: " + e.getMessage(), e);
						
						failCount ++;
					} 
					 
					finally 
					{
						try
						{
							if(inputStream!=null)	inputStream.close();
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
			try 
			{
				dfs.close();
			} 
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try 
			{
				if(manager != null)
					manager.close();
			} 
			catch (Exception e) 
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

	public List getAllFilePaths(FileObject object) throws IOException
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Listing paths for: " + object.getName().getPath());
		
		ArrayList list = new ArrayList();
		
		if(object.getType()==FileType.FILE) 
		{
			list.add(object);
		} 
		else 
		{
			FileObject[] children = object.getChildren();
			for(FileObject child : children) 
			{
				list.addAll(getAllFilePaths(child));
			}
		}
		
		return list;	
	}

	private void createFolder(String bucketName, FileObject object) throws Exception {
		
		bucketName = bucketName + object.getName().getPath();
		
		ArrayList tokens = new ArrayList();
		while(object.getParent()!=null) 
		{
			tokens.add(object.getName());
			object = object.getParent();
		}
		
		String dirPath = "";
		for(int i=tokens.size()-1; i>=0; i--) 
		{
			dirPath += tokens.get(i);
		}
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("creating folder " + bucketName);
		Path path = new Path("/", bucketName); 
		dfs.mkdirs(path);
		dfs.setOwner(path, this.loginUser, this.loginUserGroup);
	}

	private Path getImportedFilePath(String bucketName, FileObject object)
	{
		if(bucketName.endsWith("/"))
			bucketName = bucketName.substring(0, bucketName.length()-1);
		if(!bucketName.startsWith("/"))
			bucketName = "/" + bucketName;
		
		return new Path(bucketName, object.getName().getPath());
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
			try
			{
				stream = new ZipInputStream(inputStream);
				
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
							writeToStream(stream, qioOutputStream);
						}
						catch(IOException e)
						{
							throw e;
						}
						finally 
						{
							if(qioOutputStream != null)
							{
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
			}
		}
		else 
		{
			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			try
			{
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				
				if(!migrationInfo.getDestinationPath().equals("/")) {
				String finalPath = migrationInfo.getDestinationPath() + objectPath.toString();
				objectPath = new Path(finalPath);
				}
				
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
				writeToStream(inputStream, qioOutputStream);			
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
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
		return objectPath;
	}
	public  long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}
	
	public static List getAllFilePathsList(FTPClient client, String str) throws IOException{
		ArrayList list = new ArrayList();
		
		str = str.trim();
		
		if(str.endsWith("/") && str.length()>1){
			str = str.substring(0, str.length()-1);
		} 
		
		String parent = str.substring(0, str.lastIndexOf("/")+1);
		String child = str.substring(str.lastIndexOf("/")+1); 
		
		boolean isAbsFilePath = false;
		FTPFile[] fileList = client.listFiles(parent);
		for(int i=0; i<fileList.length; i++){
			if( ! fileList[i].isFile())	continue;
			
			if(child.equals(fileList[i].getName()))
				isAbsFilePath = true;
		}
		
		return list;
	}
	
	public static void main(String[] args) throws SocketException, IOException{
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
