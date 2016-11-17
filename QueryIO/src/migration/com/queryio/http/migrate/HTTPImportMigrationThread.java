package com.queryio.http.migrate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.Credentials;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
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
import com.queryio.core.datasources.DataSourceManager;
import com.queryio.core.datasources.EmailDataSource;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSOutputStream;

public class HTTPImportMigrationThread extends Thread
{
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private FileSystem dfs;
	
	boolean flag;
	
	boolean overwrite;
	
	private String baseURL;
	private String userName = "";
	private String password = "";
	private String file;
	private String characterEncoding;
	
	private String loginUser;
	private String loginUserGroup;
	
	private InputStream is = null;
	
	ArrayList<File> totalFiles;
	
	DefaultHttpClient httpclient = null;
	
	Configuration conf = null;
	
	public HTTPImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String baseURL, String userName, String password, String file, String encoding, Configuration conf, boolean overwrite) throws Exception
	{
		this.loginUser = user;
		this.loginUserGroup = group;
		this.overwrite = overwrite;
		
		this.baseURL = baseURL;
		this.userName = userName;
		this.password = password;
		this.file = file;
		this.characterEncoding = encoding;

		if ((userName != null) && (!userName.isEmpty()))
			this.userName = userName;
		
		if ((password != null) && (!password.isEmpty()))
			this.password = password;
		
		Thread.currentThread().setName(this.loginUser);
		this.migrationInfo = migrationInfo;
		
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
		
		this.connect();
	}
	
	@Override
	public void interrupt(){
		
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
			if (httpclient != null) 
				httpclient.getConnectionManager().shutdown();
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
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			int count = 1;
			unitCount = 5/100 * 1;
			if(unitCount == 0)
				unitCount = 1;
			int successCount = 0;
			int failCount = 0;
			int connectRetry=0;
			for(int i = 0; i < count; i ++)
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
					Path importedFilePath = null;
					
					try
					{
						if(is != null)
						{
							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("importedFilePath: " + importedFilePath);
							importedFilePath = new Path(migrationInfo.getDestinationPath(), this.file);
							
							try
							{
								createObject(importedFilePath, true, migrationInfo.isUnzip());
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
							}
							
							successCount ++;
							connectRetry = 0;																											
						}
						else
						{
							throw new IOException("Output stream could not be opened for source file ");
						}							
					}
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal("Could not save file. Source: " + importedFilePath + ", exception: " + e.getMessage(), e);
						
						failCount ++;
					} 
					finally 
					{
						if(is != null)
						{
							try
							{
								is.close();
							}
							catch(Exception e)
							{
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}								
						}
					}
					
					migrationInfo.setProgress((successCount * 100 / count));
					migrationInfo.setStatus("Migrated "+successCount +" of "+ count +" objects." + (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
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
				if (httpclient != null) 
					httpclient.getConnectionManager().shutdown();
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
	
	private Path createObject(Path objectPath, boolean overwrite, boolean unzip) throws Exception
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating object: " + objectPath.toString());

		DFSOutputStream dfsOutputStream = null;
		OutputStream qioOutputStream = null;
		try
		{
			DistributedFileSystem fs = (DistributedFileSystem) dfs;
			dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
			try 
			{
				qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null, objectPath.toUri().getPath());
			}
			catch (Exception e) 
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
				if (dfsOutputStream != null) 
				{
					dfsOutputStream.close();
				}
				throw e;
			}
			
			writeToStream(qioOutputStream);
			dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
	    	
		}
		finally
		{
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Closing object: " + objectPath.toString());
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
		return objectPath;
	}
	
	public long writeToStream(OutputStream baos) throws IOException {
		
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = is.read(readBuffer, 0, readBuffer.length)) != -1) 
		{
			baos.write(readBuffer, 0, bytesIn);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(bytesIn + " bytes");
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}
	
	public void connect() throws Exception
	{
        httpclient = new DefaultHttpClient();

        Credentials cred = new UsernamePasswordCredentials(this.userName, this.password);

        httpclient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), cred);
        if(file.startsWith("/"))
        {
         file = file.replaceFirst("/", "");
         file = file.trim();    	
        }
        String url = "";
        if(this.baseURL.endsWith("/"))
        	url = this.baseURL + this.file;
        else
        	url = this.baseURL + "/" + this.file;
       
        AppLogger.getLogger().fatal("url: " + url);
        
        HttpPost httpost = new HttpPost(url);

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("key1", "1"));
        nvps.add(new BasicNameValuePair("key2", "2"));


        httpost.setEntity(new UrlEncodedFormEntity(nvps, this.characterEncoding));
        
        HttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        
        if (entity != null)
        {
        	is = entity.getContent();
            System.out.println("stream initialized");
        }
	}
	
	public static void main(String args[])
	{
		Configuration conf = null;
		
		System.out.println(HTTP.ISO_8859_1);
		System.out.println(HTTP.US_ASCII);
		System.out.println(HTTP.UTF_16);
		System.out.println(HTTP.UTF_8);
		System.out.println(HTTP.ASCII);
		System.out.println(HTTP.USER_AGENT);
		System.out.println(HTTP.TRANSFER_ENCODING);
		
		try 
		{
			new HTTPImportMigrationThread("a", "queryio", null, "http://www.google.com", "a", "a", "", "", conf, true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
