package com.queryio.installcluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SQLScriptRunner;
import com.queryio.config.db.DBConfigBean;
import com.queryio.config.db.DBConfigDAO;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.RemoteManager;


public class UpgradeCluster {
	
	private static StringBuffer output;
	String[] ipList = null;
	String[] usernameList = null;
	String[] passwordList = null;
	static String oldHadoopDir = "";
	static String oldHiveDir = "";
	
	public boolean startUpgrade(String qioInstallLoc)
	{
		FileInputStream fisCluster = null;
		FileInputStream fisMeta = null;
		FileOutputStream fos = null;
		StringBuffer sbFileName = null;
		StringBuffer sbFileNameMeta = null;
		Properties propHost = null;
		Properties prop = null;
		boolean flag = false;
		String ip = "";
		String username = "";
		String password = "";
		
		String hostList = "";
		String hostCredentials = "";
		String[] list;
		String dbPort = "";
		String sshPrivateKey = "";
		String sshPort = "";
		try
		{
			 sbFileNameMeta = new StringBuffer(System.getProperty( "user.home" ));
			 sbFileNameMeta.append(File.separator + QueryIOConstants.METADATA_FILE); 
			 prop = new Properties();
			 fisMeta = new FileInputStream(sbFileNameMeta.toString());  
		     prop.load(fisMeta);
		     hostList = prop.getProperty(QueryIOConstants.UPGRADE_CLUSTERHOSTS);
//		     System.out.println(qioInstallLoc);
			 sbFileName = new StringBuffer(qioInstallLoc + QueryIOConstants.UPGRADE_HOST_CREDENTIALS);
		     propHost = new Properties();
		     fisCluster = new FileInputStream(sbFileName.toString());  
		     propHost.load(fisCluster);
		     hostCredentials = propHost.getProperty(QueryIOConstants.UPGRADE_HOST_CREDENTIALS_KEY);
		     list = hostCredentials.split(" ");
		     dbPort = getDBPort();
		     
		     // Now user cant upgrade from previous version (v3.6 or earlier) to v4.0 as in v4.0 DB has been changed from postgres to HSQL. So we dont need to update db configs. 
			 // Update queryio db
			 //updateSystemDB(qioInstallLoc);
		     
		     if(hostList.contains("127.0.0.1"))
		     {
		    	 ip = "127.0.0.1";
		    	 username = "";
		    	 password = "";
		    	 
		    	 System.out.println("\nHost IP : " + ip);
		    	 
		    	 int ind = hostList.indexOf(ip);
			     String temp = hostList.substring( ind ,  hostList.indexOf(".!", ind));
				 String installDir = temp.substring( temp.indexOf(":")+1 ,  temp.length());
		    	 installDir = installDir.substring(0, installDir.indexOf(QueryIOConstants.QUERYIOPACKAGE_DIR));
		    	 String javaHome = RemoteManager.getLocalJavaHome();
		    	 
		    	 stopAgent(ip, username, password, "", "", installDir, true);
		    	 backupHadoopEtc(ip, username, password, "", "", installDir, true, true);
			     DWRResponse response = RemoteManager.insertHostInstaller( ip, username, password, "null", installDir , "", "", javaHome, "22", true,  true, dbPort);
			     if(response.isTaskSuccess())
			     {
			    	 System.out.println("Host : " + response.getId() + " Host upgraded successfully.");
			    	 startHadoop(ip, username, password, "", "", installDir, true);
			    	 flag = true;
			     }
			     else
			     {
			    	 flag = false;
			    	 System.out.println("Failed to start QueryIO services.");
			     }
		     }
		     
		     //Adding hosts
		     if(list.length > 4)
		     {
			     for(int i=0;i<list.length;i++)
			     {
			    	 ip = list[i++];
			    	 username = list[i++];
			    	 password = list[i++];
			    	 sshPrivateKey = list[i++];
			    	 sshPort = list[i];
			    	 password = (password.equals("null")) ? null : com.queryio.common.util.SecurityHandler.decryptData(password);
			    	 sshPrivateKey = (sshPrivateKey.equals("null")) ? null : com.queryio.common.util.SecurityHandler.decryptData(sshPrivateKey);
				     
			    	 System.out.println("\nHost IP : " + ip);
			    	 
			    	 int ind = hostList.indexOf(ip);
				     String temp = hostList.substring( ind ,  hostList.indexOf(".!", ind));
					 String installDir = temp.substring( temp.indexOf(":")+1 ,  temp.length());
			    	 installDir = installDir.substring(0, installDir.indexOf(QueryIOConstants.QUERYIOPACKAGE_DIR));
			    	 String javaHome = RemoteManager.getJavaHome(ip, username, password, sshPrivateKey, Integer.parseInt(sshPort)); 
			    	 
			    	 stopAgent(ip, username, password, sshPrivateKey, sshPort, installDir, false);
			    	 backupHadoopEtc(ip, username, password, sshPrivateKey, sshPort, installDir, true, false);
				     DWRResponse response = RemoteManager.insertHostInstaller( ip, username, password, sshPrivateKey, installDir , "", "", javaHome, sshPort, false, true, dbPort);
				     
				     if(response.isTaskSuccess())
				     {
				    	 System.out.println("Host : " + response.getId() + " Host upgraded successfully.");
				    	 startHadoop(ip, username, password, sshPrivateKey, sshPort, installDir, false);
				    	 flag = true;
				     }
				     else
				     {
				    	 flag = false;
				    	 System.out.println("Failed to start QueryIO services.");
				     }
			     }
		     }
		     
		     if(flag)
		     {
		    	 propHost.setProperty(QueryIOConstants.UPGRADE_HOST_CREDENTIALS_KEY, "");
		    	 fos = new FileOutputStream(sbFileName.toString());
		    	 propHost.store(fos, "");
		     }
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Failed to setup cluster during upgrade " + e.getMessage(), e);
			e.printStackTrace();
		}
		finally
		{
			if(fisMeta != null) {
				try{
					fisCluster.close();
				}catch(Exception e){
					
				}
			}
			if(fisCluster != null) {
				try{
					fisCluster.close();
				}catch(Exception e){
					
				}
			}
			if(fos != null) {
				try{
					fos.close();
				}catch(Exception e){
					
				}
			}
		}
		return flag;
	}
	
	
	public void updateSystemDB(String qioInstallLoc) {
		// Whenever we upgrade queryio, handling for additional scripts to be executed needs to be added here
		String systemUpdateFileName = "upgrade-3.6-to-4.0." + EnvironmentalConstants.getQueryIODatabaseType().toLowerCase() + "postgres.sql";
		String systemUpdateFolderPath = qioInstallLoc + File.separator + QueryIOConstants.SERVICES_DIR_NAME + File.separator
				+ QueryIOConstants.DB_UPGRADE_DIR_NAME + File.separator + QueryIOConstants.SYSTEM_DIR_NAME;

		String systemUpdateFilePath = systemUpdateFolderPath + File.separator + systemUpdateFileName;

		Connection connection = null;
		StringBuilder logWriter = new StringBuilder();
		StringBuilder errorLogWriter = new StringBuilder();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			SQLScriptRunner runner = new SQLScriptRunner(connection, false, true);
			runner.setLogWriter(logWriter);
			runner.setErrorLogWriter(errorLogWriter);
			runner.runScript(new BufferedReader(new FileReader(systemUpdateFilePath)));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error occured while executing script: " + systemUpdateFilePath + "\n" + errorLogWriter.toString(), e);
		} finally {
			CoreDBManager.closeConnectionSilently(connection);
		}

		if (AppLogger.getLogger().isDebugEnabled()) {
			AppLogger.getLogger().fatal("Executed script: " + systemUpdateFilePath + "\n" + logWriter.toString());
		}
	}


	public static int getHostID(String ip)
	{
		 ArrayList hostsList = new ArrayList();
		 hostsList.add(ip);
		 HashMap hostid = new HashMap();
		 hostid = RemoteManager.getHostIds(hostsList);
		 int host = (Integer) hostid.get(ip);
		 return host;
	}
	
	public int getIPIndex(String ip)
	{
		int i;
		for(i=0;i<ipList.length;i++)
		{
			if(ipList[i].equals(ip))
			{
				break;
			}
		}
		return i;
	}
	
	public static void executeCommand(String command)
	{
		Process p = null;
		BufferedReader inReader = null;
		try
		{
			p = Runtime.getRuntime().exec(command);
			inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while((line=inReader.readLine())!=null){
				if(line.startsWith("Hadoop")) oldHadoopDir = line;
				if(line.startsWith("hive"))oldHiveDir = line;
			}
			inReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = null;
			while((line=inReader.readLine())!=null){
				AppLogger.getLogger().fatal(line);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(p!=null)
				p.destroy();
			if(inReader != null)
			{
				try {
					inReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}
	}
	
	public static boolean backupHadoopEtc(String host, String user, String password, String sshPrivateKey, String port, String installDir , boolean isBackup, boolean isLocal)
	{
		Session session = null;
		boolean flag = false;
		String hostName = "";
		String command = "";
		try
		{
			if (!installDir.endsWith("/"))
				installDir = installDir + "/";
			
			if(!isLocal)
			{
				if(password != null) {
					session = createSession(host, user, password, Integer.parseInt(port));
				} else if(sshPrivateKey != null) {
					session = createSessionWithPrivateKeyFile(host, user, sshPrivateKey, Integer.parseInt(port));
				} else {
					return false;
				}
		        session.connect();
		        if(isBackup)
		        {
		        	String etcDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR;// + QueryIOConstants.HADOOP_DIR_NAME + "/etc";
					String etcBackUpDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR_BAK;// + QueryIOConstants.HADOOP_DIR_NAME + "/etc_bak";
		        	command = "mv "+etcDir+" "+etcBackUpDir+" ";
		        }
		        else
		        {
		        	command = "ls " + installDir + "QueryIOPackage_bak/ | grep adoop";
		        	executeCommand(session, command);
		        	String oldHodoopDir = getOutput().trim();
		        	command = "ls " + installDir + "QueryIOPackage_bak/ | grep hive";
		        	executeCommand(session, command);
		        	String oldHiveDir = getOutput().trim();
		        	String etcDir = installDir + "QueryIOPackage/" + QueryIOConstants.HADOOP_DIR_NAME +" ";
					String etcBackUpDir = installDir + "QueryIOPackage_bak/" + oldHodoopDir + "/etc";
					String hiveConf = installDir + "QueryIOPackage/" + QueryIOConstants.HIVE_DIR_NAME + "/conf";
					String hiveSite_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/conf/hive-env.sh";
					String hiveEnv_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/conf/hive-site.xml";
					String hiveLog_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/logs/out.log";
		        	command = "cp -r "+etcBackUpDir+" "+etcDir+"; cp -r "+hiveSite_bak+" "+hiveConf+"; cp -r "+hiveEnv_bak+" "+hiveConf+"; cp -r "+hiveLog_bak+" "+hiveConf+"/../logs;";		          
		        }
//	        	System.out.println(command);
		        executeCommand(session, command);
//		        String response = getOutput();
//		        System.out.println(response);
			}
			else
			{
				if(isBackup)
		        {
		        	String etcDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR;
					String etcBackUpDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR_BAK;					
		        	command = "mv "+etcDir+" "+etcBackUpDir+" ";
		        	executeCommand(command);
		        }
		        else
		        {
		        	command = "ls " + installDir + "QueryIOPackage_bak/";
		        	executeCommand(command);		        	
		        	String etcDir = installDir + "QueryIOPackage/" + QueryIOConstants.HADOOP_DIR_NAME +" ";
					String etcBackUpDir = installDir + "QueryIOPackage_bak/" + oldHadoopDir + "/etc";
					String hiveConf = installDir + "QueryIOPackage/" + QueryIOConstants.HIVE_DIR_NAME + "/conf";
					String hiveHome = installDir + "QueryIOPackage/" + QueryIOConstants.HIVE_DIR_NAME;
					String hiveSite_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/conf/hive-env.sh";
					String hiveEnv_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/conf/hive-site.xml";
					String hiveLog_bak = installDir + "QueryIOPackage_bak/" + oldHiveDir + "/logs/out.log";
		        	command = "cp -r "+etcBackUpDir+" "+etcDir+" ";
		        	executeCommand(command);
		        	command = "cp -r "+hiveSite_bak+" "+hiveConf+" ";
		        	executeCommand(command);
		        	command = "cp -r "+hiveEnv_bak+" "+hiveConf+" ";
		        	executeCommand(command);
		        	command = "mkdir "+hiveHome+"/logs";
		        	executeCommand(command);
		        	command = "cp -r "+hiveLog_bak+" "+hiveHome+"/logs ";
		        	executeCommand(command);
		        	
		        }
			}
	        
        }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			flag = false;
		}
		finally
		{
			if(session!=null){
				
				session.disconnect();				
			}
		}
		return flag;
	}

	
	public static boolean stopAgent(String host, String user, String password, String sshPrivateKey, String port, String installDir, boolean isLocal)
	{
		Session session = null;
		boolean flag = false;
		String hostName = "";
		String command = "";
		try
		{
			if (!installDir.endsWith("/"))
				installDir = installDir + "/";
			installDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR + File.separator;
			if(!isLocal)
			{
				if(password != null) {
					session = createSession(host, user, password, Integer.parseInt(port));
				} else if(sshPrivateKey != null) {
					session = createSessionWithPrivateKeyFile(host, user, sshPrivateKey, Integer.parseInt(port));
				} else {
					return false;
				}
		        session.connect();
		        command = "sh "+installDir+"bin/stop_hive.sh; sh "+installDir+"bin/stop_hadoop.sh; sh " +  installDir + "bin/stop_agent.sh; ";
	//	        System.out.println(command);
		        executeCommand(session, command);
		        String response = getOutput();
	//	        System.out.println(response);
		        
		        String pid = checkQueryIO("QueryIOAgent");
		        if(!pid.isEmpty())
		        {
		        	command = "crontab -r ; kill -9 "+pid+"; ";
			        executeCommand(session, command);
			        response = getOutput();
			        System.out.println(response);
		        }
		        Thread.sleep(5000);
			}
			else
			{
				command = "sh "+installDir + "bin/stop_hive.sh ";
				executeCommand(command);
				command = "sh "+installDir + "bin/stop_queryio_services.sh ";
				executeCommand(command);
				command = "sh "+installDir + "bin/stop_hadoop.sh ";
				executeCommand(command);
				command = "sh "+installDir + "bin/stop_agent.sh ";
				executeCommand(command);
			}
	        
        }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			flag = false;
		}
		finally
		{
			if(session!=null){
				
				session.disconnect();				
			}
		}
		return flag;
	}
	
	public static boolean startHadoop(String host, String user, String password, String sshPrivateKey, String port, String installDir, boolean isLocal)
	{
		System.out.println("Starting QueryIO nodes and services...");
		Session session = null;
		boolean flag = false;
		String hostName = "";
		String command = "";
		try
		{
			if (!installDir.endsWith("/"))
				installDir = installDir + "/";
			String backUpDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR_BAK;
			String newBackUpDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR_BAK + new java.text.SimpleDateFormat("MM-dd-yyyy-h-mm-ss-a").format(new java.util.Date());
			installDir = installDir + QueryIOConstants.QUERYIOPACKAGE_DIR + File.separator;
			
			if(!isLocal)
			{
				if(password != null) {
					session = createSession(host, user, password, Integer.parseInt(port));
				} else if(sshPrivateKey != null) {
					session = createSessionWithPrivateKeyFile(host, user, sshPrivateKey, Integer.parseInt(port));
				} else {
					return false;
				}
		        session.connect();
		        command = " sh "+installDir + "bin/start_hadoop.sh;  sh "+installDir + "bin/start_hive.sh; sh "+installDir + "bin/start_queryio_services.sh; mv  "+backUpDir+" "+newBackUpDir+"; " ;
		        executeCommand(session, command);
//		        String response = getOutput();
//	     	    System.out.println(response);
			}
			else
			{
				command = "sh "+installDir + "bin/start_hadoop.sh ";
				executeCommand(command);
				command = "sh "+installDir + "bin/start_queryio_services.sh ";
				executeCommand(command);
				command = "sh "+installDir + "bin/start_hive.sh ";
				executeCommand(command);
				command = "mv "+backUpDir+" "+newBackUpDir+" ";
				executeCommand(command);
				
			}
	        
        }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			flag = false;
		}
		finally
		{
			if(session!=null){
				
				session.disconnect();				
			}
		}
		return flag;
	}
	
	protected static void parseExecResult(BufferedReader lines) throws IOException
  	{
		output = new StringBuffer();
  		char[] buf = new char[512];
  		int nRead;
  		while ( (nRead = lines.read(buf, 0, buf.length)) > 0 )
  		{
  			output.append(buf, 0, nRead);
  		}
  	}
	
	public static String getOutput()
  	{
  		return (output == null) ? "" : output.toString();
  	}
	
	
	public static void executeCommand(Session session, String  command) throws Exception
  	{
  		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);
		channel.setInputStream(null);
		final BufferedReader errReader = new BufferedReader(new InputStreamReader(((ChannelExec)channel).getErrStream()));
		BufferedReader inReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
		channel.connect();
	  
		Thread errorThread = new Thread()
		{
			public void run()
			{
				try
				{
					String line = errReader.readLine();
				  	while((line != null) && !isInterrupted())
				  	{
				  		line = errReader.readLine();
//				  		System.out.println(line);
				  	}
//				  	System.out.println("Out from error thread");
				  	
				}
				catch(Throwable ioe)
				{
					ioe.printStackTrace();
				}
			}
		};
	  
		try
		{
			errorThread.start();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
	  
		try
		{
			parseExecResult(inReader);
			String line = inReader.readLine();
			while (line != null)
			{
				line = inReader.readLine();
			}
			if(channel.isClosed())
			{
//				System.out.println("Exit Code " + channel.getExitStatus());
			}
			try
			{
				errorThread.join();
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
		catch (Exception ie)
		{
			throw new IOException(ie.toString());
		}
		finally
		{
			try
			{
				inReader.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			try
			{
				errReader.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			channel.disconnect();
		}
  	}
	
	
	public static String checkQueryIO(String process)
	{
		BufferedReader input = null;
		String procPID = "";
		Process p = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			p = Runtime.getRuntime().exec("ps -ef");
			isr = new InputStreamReader(p.getInputStream());
	        br = new BufferedReader(isr);
	        String line = null;
	        
	        while((line=br.readLine())!=null)
	        {
	        	if(line.contains(process))
	        	{
	        		//System.out.println(line);
	        		line = line.trim();
                    
                    int i=0;
                    int col=0;
                    boolean space = false;
                    while(i<line.length())
                    {
                        if(line.charAt(i)==' ')    space = true;
                        else if(space==true)
                        {
                            space = false;
                            col++;
                        }
                        if(col==1 && !space)
                        {
                            procPID += line.charAt(i);
                        }
                        i++;
                    }
                    procPID += " ";
	        	}
	        }
		}
		catch (Exception err) {
		    err.printStackTrace();
		}
		finally{
			p.destroy();
			if(br != null) {
				try {
				br.close();
				} catch(Exception e) {
					
				}
			}
			if(isr != null) {
				try {
				isr.close();
				} catch(Exception e) {
					
				}
			}
		}
		//System.out.println(procPID);
		return procPID;
	}
	
	public static String getDBPort() throws Exception 
	{
		DBConfigBean connectionDetail = null;
		 String port = "";
		 
		 connectionDetail = DBConfigDAO.getConnectionDetail("Hive");
	    	String url = connectionDetail.getPrimaryConnectionURL();
	    	
	    	port = url.substring(url.lastIndexOf(":")+1, url.indexOf("/hive"));
	    	return port;
	}
	
	public static Session createSession(String hostName, String userName, String password, int portNumber) throws Exception
  	{
  		JSch jsch = null;
  		Session session = null;
  		
  		try
  		{
			jsch = new JSch();
			
			session = jsch.getSession(userName, hostName, portNumber);
			session.setPassword(password);
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
  		}
  		catch (Exception e)
  		{
  			
  		}
  		return session;
  	}
  	
  	public static Session createSessionWithPrivateKeyFile(String hostName, String userName, String privateKeyFile, int portNumber) throws Exception
  	{
  		JSch jsch = null;
  		Session session = null;
  		
  		try
  		{
			jsch = new JSch();
			
			jsch.addIdentity(privateKeyFile);
			
			session = jsch.getSession(userName, hostName, portNumber);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		}
  		catch (Exception e)
  		{
  			e.printStackTrace();
  		}
  		return session;
  	}
  	
	
	public static void main(String args[])
	{
//		System.out.println(new UpgradeCluster().startUpgrade("/Applications/QueryIO"));
//		stopAgent("" , "" , "" , "/Users/admin/" , "", true);
//		startHadoop("" , "" , "" , "/Users/admin/" , "", true);
//		backupHadoopEtc("", "", "", "/Users/admin", "", false, true);
//		System.out.println("YoTCBmZv7w7N1TjIqJdFEKwHyDPvn6gx1EmmXSsQHcU=".length());
//		String installDir = "/Applications/QueryIO/";
//		String command = "ls " + installDir + "QueryIOPackage_bak/ | grep hive";
//		System.out.println(command);
//    	executeCommand(command);		        	
//    	System.out.println("oldHiveDir: " + oldHiveDir);
//		backupHadoopEtc("", "", "", "", "", "/Applications/QueryIO", false, true);
	}
}
