package com.queryio.core.hdfsoverftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StreamPumper;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class HOFManager {
	private static String CONF_FILE = EnvironmentalConstants.getAppHome() + "../../../" + "hdfs-over-ftp/hdfs-over-ftp.conf";
	private static String HADOOP_PROP_FILE = EnvironmentalConstants.getAppHome() + "../../../" + "hdfs-over-ftp/hadoop.properties";
	private static String USER_FILE =  EnvironmentalConstants.getAppHome() + "../../../" + "hdfs-over-ftp/users.conf";
	private static Properties props = null;
	private static Properties hadoopProps = null;
	private static boolean serverStarted = false;
	
	public static void registerUser(String username, String password)throws Exception{
		Properties props = new Properties();			
		String prefix = "ftpserver.user.";
		props.setProperty(prefix + username + ".userpassword", password);
		props.setProperty(prefix + username + ".homedirectory", "/");
		props.setProperty(prefix + username + ".enableflag", "true");
		props.setProperty(prefix + username + ".writepermission", "true");
		props.setProperty(prefix + username + ".maxloginnumber", "0");
		props.setProperty(prefix + username + ".maxloginperip", "0");
		props.setProperty(prefix + username + ".idletime", "0");
		props.setProperty(prefix + username + ".uploadrate", "0");
		props.setProperty(prefix + username + ".downloadrate", "0");
		props.setProperty(prefix + username + ".groups", username + ",users");
		props.store(new FileOutputStream(USER_FILE), null);		
	}
	
	public static void storeConfig() throws Exception{
		props.store(new FileOutputStream(CONF_FILE), null);
		hadoopProps.store(new FileOutputStream(HADOOP_PROP_FILE), null);
	}
	
	public static void initConfig() throws Exception{	
		props = new Properties();
		hadoopProps = new Properties();
	    props.load(new FileInputStream(CONF_FILE));
	    hadoopProps.load(new FileInputStream(HADOOP_PROP_FILE));
	}
	
	public static DWRResponse startServer(){
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Performing start on hdfs-over-ftp server");
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "",500);
		StringWriter inputWriter = null;
		StringWriter errorWriter = null;
    	try {
    		if(!RemoteManager.isAdmin()){
    			dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
    		String cmd = "./start-server.sh";    		
    		    		
			final Process targetProcess = Runtime.getRuntime().exec(cmd, null, new File( EnvironmentalConstants.getAppHome() + "../../../" + "hdfs-over-ftp"));
			serverStarted = true;
			inputWriter = new StringWriter();
			final StreamPumper spInput = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
					.getInputStream())), inputWriter);
			spInput.start();
			
			errorWriter = new StringWriter();
			if (targetProcess.getErrorStream() != null)
			{
				final StreamPumper spError = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
						.getErrorStream())), errorWriter);
				spError.start();
			}
			targetProcess.waitFor();
			int count = 0;
			while (!spInput.isProcessCompleted() && (count < 5))
			{
				Thread.sleep(100);
				count++;
			}

			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("targetProcess.exitValue()" + targetProcess.exitValue());
			if (targetProcess.exitValue() != 0)
			{
				String message = "Following error occured while starting hdfs-over-ftp server";
				AppLogger.getLogger().fatal(message);			
				//return false;
			}		
			dwrResponse.setDwrResponse(true, "FTP Server started successfully.", 200);
			return dwrResponse;
		}
    	catch (final Exception ex) 
    	{
			AppLogger.getLogger().fatal(ex.getMessage(), ex);	
			dwrResponse.setDwrResponse(false, ex.getMessage(), 500);
			return dwrResponse;
		}
    	finally
    	{
			try 
			{
				if(errorWriter != null)
					errorWriter.close();
				if(inputWriter != null)
					inputWriter.close();
			} 
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}			
		}	
    	
	}

	public static boolean stopServer(boolean checkIfAdmin){
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Performing stop on hdfs-over-ftp server");

		StringWriter inputWriter = null;
		StringWriter errorWriter = null;
    	try {
    		
    		if(checkIfAdmin && !RemoteManager.isAdmin()){
				return false;
			}
    		String cmd = "./stop-server.sh";
    					
			final Process targetProcess = Runtime.getRuntime().exec(cmd, null, new File( EnvironmentalConstants.getAppHome() + "../../../" + "hdfs-over-ftp"));
			serverStarted = false;
			inputWriter = new StringWriter();
			final StreamPumper spInput = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
					.getInputStream())), inputWriter);
			spInput.start();
			
			errorWriter = new StringWriter();
			if (targetProcess.getErrorStream() != null)
			{
				final StreamPumper spError = new StreamPumper(new BufferedReader(new InputStreamReader(targetProcess
						.getErrorStream())), errorWriter);
				spError.start();
			}
			targetProcess.waitFor();
			int count = 0;
			while (!spInput.isProcessCompleted() && (count < 5))
			{
				Thread.sleep(100);
				count++;
			}

			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("targetProcess.exitValue()" + targetProcess.exitValue());
			if (targetProcess.exitValue() != 0)
			{
				String message = "Following error occured while stopping hdfs-over-ftp server: " + inputWriter.toString();
				AppLogger.getLogger().fatal(message);			
				return false;
			}		
			return true;
		}
    	catch (final Exception ex) 
    	{
			AppLogger.getLogger().fatal(ex.getMessage(), ex);			
		}
    	finally
    	{
			try 
			{
				if(errorWriter != null)
					errorWriter.close();
				if(inputWriter != null)
					inputWriter.close();
			} 
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal("Error closing stream", e);
			}			
		}	
    	return false;
	}
	
	public static ArrayList getAllConfig(){
		ArrayList result = new ArrayList();
		try{			
			initConfig();
			String configFlag = props.getProperty("config.set");
			if(configFlag ==null || !configFlag.equals("true"))
				return null;			
			result.add(hadoopProps.getProperty(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
			result.add(props.getProperty("host-name"));			
			result.add(props.getProperty("port"));
		}catch(Exception e){
			AppLogger.getLogger().fatal("Error getting config details", e);
		}
		return result; 
	}
	public static DWRResponse setConfig(String nodeId, String port){
		Connection connection = null;
		DWRResponse dwrResponse =  new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		try{
			if(!RemoteManager.isAdmin()){
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList keys=new ArrayList();
			keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			String[] values = (String[])(QueryIOAgentManager.getConfig(host,keys, node, "core-site.xml").toArray());
			
			
			
			if(values == null || values.length != 1){
				dwrResponse.setDwrResponse(false, "No volume found for Configuration.",404);
				return dwrResponse;
			}
			
			initConfig();
			props.setProperty("host-name", InetAddress.getLocalHost().getHostAddress());
			hadoopProps.setProperty(DFSConfigKeys.FS_DEFAULT_NAME_KEY, values[0]);
			props.setProperty("port", port);
			props.setProperty("config.set", "true");			
			storeConfig();
			dwrResponse.setDwrResponse(true, "FTP configuration saved successfully.",200);
			return dwrResponse;
			
		}catch(Exception e){
			AppLogger.getLogger().fatal("Error getting config details", e);
			dwrResponse.setDwrResponse(false, e.getMessage(),500);
			return dwrResponse;
		}finally
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
	public static String getStatus(){
		String response = "";
		FTPClient client = null;
		if(serverStarted){
			try{
				initConfig();
				client = new FTPClient();
				client.connect(props.getProperty("host-name"), Integer.parseInt(props.getProperty("port")));
				if(client.isConnected()){
					response = "Started";
				}else{
					response = "Not Responding";
				}
			}catch(Exception e){
				AppLogger.getLogger().fatal("Error connecting to FTP", e);
				response = "Not Responding";
			}finally{
				if(client != null && client.isConnected()){
					try {
						client.disconnect();
					} catch (IOException e) {
						AppLogger.getLogger().fatal("Error disconnecting to FTP", e);
					}
				}
			}			
		}else{
			response =  "Stopped";
		}
		return response;
	}
}
