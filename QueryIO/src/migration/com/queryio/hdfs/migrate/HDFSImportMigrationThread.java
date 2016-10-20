package com.queryio.hdfs.migrate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSInputStream;
import com.queryio.stream.util.QIODFSOutputStream;

public class HDFSImportMigrationThread extends Thread
{
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private FileSystem client;
	
	private FileSystem dfs;
	
	boolean flag;
	
	boolean overwrite;
	
	private String host;
	private String path;
	
	private String loginUser;
	private String loginUserGroup;
	
	Configuration conf = null;
	
	public HDFSImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String host, String path, Configuration conf, boolean overwrite) throws Exception
	{
		this.loginUser = user;
		this.loginUserGroup = group;
		
		this.host = host;
		this.path = path;

		
		Thread.currentThread().setName(this.loginUser);
		this.migrationInfo = migrationInfo;
		
		dfs = FileSystem.get(conf);
		dfs.setConf(conf);
		
		this.conf = conf;
		this.overwrite = overwrite;
		
		this.connect();
		
		if(EnvironmentalConstants.isUseKerberos())
		{
			Connection connection = null;
			try{
				connection = CoreDBManager.getQueryIODBConnection();
				
				User us = UserDAO.getUserDetail(connection, user);
				
				UserGroupInformation.setConfiguration(conf);
				UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));		
				
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
			List filePaths = getAllFilePaths(migrationInfo.getSourcePath());
			
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser, loginUserGroup);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
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
					Path filePath =  (Path)filePaths.get(i);
					InputStream inputStream = null;
					Path importedFilePath = getImportedFilePath(migrationInfo.getDestinationPath(), filePath.toUri().getPath());
					try
					{
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Fetching object: " + filePath);
						
						try
						{
							inputStream = getObject(filePath);
						}
						catch(Exception e)
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
									AppLogger.getLogger().fatal("IOException occurred... exception: " + e.getMessage(), e);
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reconnecting to HDFS server...");
									
									this.connect();
									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful");
									connectRetry = 0;
								} 
								catch(Exception ex)
								{
									connectRetry ++;
									AppLogger.getLogger().fatal("Could not connect to HDFS, Exception: " + ex.getMessage(), ex);
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
							throw new IOException("Output stream could not be opened for source file: " + filePath);
						}
					} 
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal("Could not save file. Source: " + filePath + ", Exception: " + e.getMessage(), e);
						
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
	
	private BufferedInputStream getObject(Path path)throws Exception
	{
		DFSInputStream dfsInputStream = null;
		QIODFSInputStream qioInputStream = null;
		
		DistributedFileSystem fs = (DistributedFileSystem) client;
		dfsInputStream = (DFSInputStream) fs.getClient().open(path.toUri().toString());
		try 
		{
			qioInputStream = new QIODFSInputStream(dfsInputStream);
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
	
	private List getAllFilePaths(String str) throws Exception
	{
		Path path = new Path(str);
		FileStatus stat = client.getFileStatus(path);
		List list = new ArrayList();
		if(!stat.isDirectory())
		{
			list.add(path);
		}
		else
		{
			FileStatus[] stats = client.listStatus(path);
			for(int i = 0; i < stats.length; i ++)
			{
				if(!str.endsWith("/"))
					str += "/";
				list.addAll(getAllFilePaths(str + stats[i].getPath().getName()));
			}
		}
		return list;	    
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
								qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null);
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
						catch(Exception e)
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
						Path path = new Path(objectPath, fileName);
				    	dfs.mkdirs(new Path(path, fileName));
				    	dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
				    }
				
					stream.closeEntry();
				    entry = stream.getNextEntry();
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
				throw e;
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
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
				try 
				{
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null);
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
				AppLogger.getLogger().fatal(e.getMessage(), e);
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
	
	public void connect() throws Exception
	{
		Configuration conf = new Configuration();
		conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, this.host + this.path);
		
		client = FileSystem.get(conf);
		client.setConf(conf);
	}
	
	public static void main(String args[])
	{
		Configuration conf = null;
		
		try 
		{
			new HDFSImportMigrationThread("a", "queryio", null, "192.168.0.23:5678", "a", conf, true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
