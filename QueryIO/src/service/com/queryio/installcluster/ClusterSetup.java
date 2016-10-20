package com.queryio.installcluster;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.bean.User;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.permissions.GroupDAO;
import com.queryio.core.permissions.UserGroupDAO;


public class ClusterSetup
{
	String ip;
	String userName;
	String password;
	String sshPrivateKey;
	String qioUsername;
	String qioPassword;
	String sshPort;
	HashMap hostid;
	ArrayList disk;
	HashMap datanodePorts;
	HashMap namenodePorts;
	HashMap rmPorts;
	HashMap nmPorts;
	String installDir = "";
	static String error;
	
	public HashMap<String, Boolean> installNodes(String cip, String cUsername, String cPassword, String queryioUser, String queryioPassword, boolean isLocal, String installUserHome, String installJavaHome, String ipPrivateKey, String port) 
	{
		HashMap<String, Boolean> flag = new HashMap<String, Boolean>();
		Boolean flagCheck = true;
		error = " ";
		DWRResponse response;
		ip = cip;
		userName = cUsername;
		password = cPassword;
		sshPrivateKey = ipPrivateKey;
		qioUsername = queryioUser;
		qioPassword = queryioPassword;
		sshPort = port;
		
		AppLogger.getLogger().fatal("Installing Host...");
		System.out.println("Installing Host...");
		response = installHost(isLocal, installUserHome, installJavaHome);
		if(response.isTaskSuccess()) 
		{
			AppLogger.getLogger().fatal("Host installed successfully");
			System.out.println("Host installed successfully");
			flag.put(QueryIOConstants.HOST, true);
			setupService(ip, isLocal, installUserHome);
			
			if(installRM(ip, isLocal, installUserHome))
			{
				flag.put(QueryIOConstants.RESOURCEMANAGER, true);
				if(installNM(ip, isLocal, installUserHome))
				{
					flag.put(QueryIOConstants.NODEMANAGER, true);
					flagCheck = true;
				}
				else
				{
					flag.put(QueryIOConstants.NODEMANAGER, false);
					flagCheck = false;
				}
			}
			else
			{
				flag.put(QueryIOConstants.RESOURCEMANAGER, false);
				flag.put(QueryIOConstants.NODEMANAGER, false);
				flagCheck =false;
			}
			
			if(installNameNode(ip, isLocal, installUserHome))
			{
				flag.put(QueryIOConstants.NAMENODE, true);
				if(installDataNode(ip, isLocal, installUserHome))
				{
					flag.put(QueryIOConstants.DATANODE, true);
					flagCheck =true;
				}
				else
				{
					flag.put(QueryIOConstants.DATANODE, false);
					flagCheck = false;
				}
			}
			else
			{
				flag.put(QueryIOConstants.NAMENODE, false);
				flag.put(QueryIOConstants.DATANODE, false);
				flagCheck = false;
			}
			
			
			
		}
		else
		{
			error += response.getResponseMessage();
			flag.put(QueryIOConstants.HOST, false);
			flag.put(QueryIOConstants.NAMENODE, false);
			flag.put(QueryIOConstants.DATANODE, false);
			flag.put(QueryIOConstants.RESOURCEMANAGER, false);
			flag.put(QueryIOConstants.NODEMANAGER, false);
			flagCheck = false;
		}
		if(flagCheck) {
			System.out.println("Cluster configured successfully");
			AppLogger.getLogger().fatal("Cluster configured successfully");
		}
		else {
			AppLogger.getLogger().fatal("Cluster setup failed : " + error);
			System.out.println("Cluster setup failed : " + error);
		}
		
		return flag;
	}
	
	
	public boolean addUser(String qioUsername, String qioPassword, String qioFname, String qioLname, String qioEmail)
	{
		
		String[] groupNames = new String[1];
		String userGroup = QueryIOConstants.DEFAULT_GROUP_NAME;
		String userRole = QueryIOConstants.ROLES_ADMIN;
		String userName = qioUsername;
		String firstName = qioFname;
		String lastName = qioLname;
		String password = qioPassword;
		String email = qioEmail;
		groupNames[0]=userGroup;
		System.out.print("\n UserName :"+userName+", "+userRole+","+userGroup);
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
		
			User user = new User();
			user.setUserName(userName);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setEmail(email);
			dwrResponse =new DWRResponse();
			
			GroupDAO.addGroup(connection, userGroup);
			UserDAO.insertUser(connection, user, userRole, dwrResponse);
			UserGroupDAO.addUserToGroup(connection, userName, userGroup, true);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		if(dwrResponse.isTaskSuccess())
			System.out.println("\nUser Successfully added");
		else
			System.out.println("\nFailed to register new user. ");
		return dwrResponse.isTaskSuccess();
	}
	
	public DWRResponse installHost(boolean isLocal, String installUserHome, String installJavaHome)
	{
		AppLogger.getLogger().debug("Installing host");
		DWRResponse response = RemoteManager.insertHostAutomatationInstaller(ip, userName, password,
				sshPrivateKey, "/default-rack", sshPort, ip, isLocal, installUserHome, installJavaHome, false);
		AppLogger.getLogger().debug("host result : " + response.isTaskSuccess() + " : " + response.getResponseMessage());
		
		return response;
	}
	
	public void setupService(String ip, boolean isLocal, String installUserHome)
	{
		
		disk = RemoteManager.getPhysicalDiskNames(ip);

		installDir = "";
		if (isLocal)
			installDir = installUserHome;

		if (installDir.isEmpty())
			installDir = RemoteManager.getUserHomeDirectoryPathForHost(ip);

		installDir += "/QueryIONodes";
		//System.out.println("installDir : " + installDir);
		ArrayList hostsList = new ArrayList();
		hostsList.add(ip);
		hostid = new HashMap();
		hostid = RemoteManager.getHostIds(hostsList);
	}
	
	public Boolean installNameNode(String ip, boolean isLocal, String installUserHome)
	{
		AppLogger.getLogger().fatal("Installing NameNode...");
		System.out.println("Installing NameNode...");
		error = " ";
		setupService(ip, isLocal, installUserHome);
		HashMap<String, HadoopConfig> ports;
		ArrayList keyList = new ArrayList();
		keyList = new ArrayList();
		keyList.add("dfs.namenode.rpc-address");
		keyList.add("dfs.namenode.http-address");
		keyList.add("dfs.namenode.https-address");
		keyList.add("queryio.s3server.port");
		keyList.add("queryio.s3server.ssl.port");
		keyList.add("queryio.ftpserver.port");
		keyList.add("queryio.hdfsoverftp.port");
		keyList.add("queryio.ftpserver.ssl.port");
		keyList.add("queryio.namenode.options");
		
		ports = RemoteManager.getConfigurationServerPort(keyList);
		
		String serverPort = (ports.get("dfs.namenode.rpc-address")).getValue();
		serverPort = serverPort.substring(serverPort.indexOf(":")+1).trim();
		
		String httpPort = (ports.get("dfs.namenode.http-address")).getValue();
		httpPort = httpPort.substring(httpPort.indexOf(":")+1).trim();
		
		String httpsPort = (ports.get("dfs.namenode.https-address")).getValue();
		httpsPort = httpsPort.substring(httpsPort.indexOf(":")+1).trim();

		String s3serverPort = (ports.get("queryio.s3server.port")).getValue();
		
		String s3SslserverPort = (ports.get("queryio.s3server.ssl.port")).getValue();
		
		String hdfsOverFtpPort = (ports.get("queryio.hdfsoverftp.port")).getValue();
		
		String ftpServerPort = (ports.get("queryio.ftpserver.port")).getValue();
		
		String ftpSslServerPort = (ports.get("queryio.ftpserver.ssl.port")).getValue();
		
		String jmxPort = (ports.get("queryio.namenode.options")).getValue();
		jmxPort = jmxPort.substring(jmxPort.indexOf("jmxremote.port=")+1);
		jmxPort = jmxPort.substring(jmxPort.indexOf('=')+1,jmxPort.indexOf("-Dcom.sun.management")).trim();
		
		namenodePorts = new HashMap();
		namenodePorts.put("serverPort", serverPort);
		namenodePorts.put("http" , httpPort);
		namenodePorts.put("https" , httpsPort);
		namenodePorts.put("s3serverPort", s3serverPort);
		namenodePorts.put("s3SslserverPort", s3SslserverPort);
		namenodePorts.put("hdfsOverFtpPort", hdfsOverFtpPort);
		namenodePorts.put("ftpServerPort", ftpServerPort);
		namenodePorts.put("ftpSslServerPort", ftpSslServerPort);
		namenodePorts.put("jmxPort", jmxPort);
		
		String d = (String) disk.get(0);
		int host = (Integer) hostid.get(ip);
		DWRResponse response = RemoteManager.addNameNodeInstaller(host, "NameNode1",
				d, installDir,
				namenodePorts.get("serverPort").toString(),
				namenodePorts.get("http" ).toString(),
				namenodePorts.get("https").toString(),
				namenodePorts.get("s3serverPort").toString(),
				namenodePorts.get("s3SslserverPort").toString(),
				namenodePorts.get("hdfsOverFtpPort").toString(),
				namenodePorts.get("ftpServerPort").toString(),
				namenodePorts.get("ftpSslServerPort").toString(),
				namenodePorts.get("jmxPort").toString(), "MetaStore", "Hive",
				false);
		if(response.isTaskSuccess())
		{
			AppLogger.getLogger().fatal("NameNode Installed successfully.");
			System.out.println("NameNode Installed successfully.");
			AppLogger.getLogger().fatal("Starting NameNode...");
			System.out.println("Starting NameNode...");
			response = RemoteManager.startNodeInstaller( "NameNode1" , qioUsername, false);
			if(response.isTaskSuccess())
			{
				AppLogger.getLogger().fatal("NameNode started successfully.");
				System.out.println("NameNode started successfully.");
				return true;
			}
			else
			{
				AppLogger.getLogger().fatal("Failed to start NameNode due to " + response.getResponseMessage() );
				System.out.println("Failed to start NameNode due to " + response.getResponseMessage() );
				error += response.getResponseMessage();
				return false;
			}
		}
		else
		{
			AppLogger.getLogger().fatal("Failed to Install NameNode due to " + response.getResponseMessage() );
			System.out.println("Failed to Install NameNode due to " + response.getResponseMessage() );
			error += response.getResponseMessage();
			return false;
		}
	}
	
	public Boolean installDataNode(String ip, boolean isLocal, String installUserHome)
	{
		AppLogger.getLogger().fatal("Installing DataNode...");
		System.out.println("Installing DataNode...");
		setupService(ip, isLocal, installUserHome);
		HashMap<String, HadoopConfig> ports;
		ArrayList keyList = new ArrayList();
		
		keyList.add("dfs.datanode.address");
		keyList.add("dfs.datanode.https.address");
		keyList.add("dfs.datanode.http.address");
		keyList.add("dfs.datanode.ipc.address");
		keyList.add("queryio.datanode.options");
		
		ports = RemoteManager.getConfigurationServerPort(keyList);
		HadoopConfig server = ports.get("dfs.datanode.address");
		String sp = server.getValue();
		
		HadoopConfig https = ports.get("dfs.datanode.https.address");
		String httpsPort = https.getValue();
		
		HadoopConfig http = ports.get("dfs.datanode.http.address");
		String httpPort = http.getValue();
		
		HadoopConfig ipc = ports.get("dfs.datanode.ipc.address");
		String ipcPort = ipc.getValue();
		
		datanodePorts = new HashMap();
		datanodePorts.put("serverPort", sp.substring(sp.indexOf(":")+1));
		datanodePorts.put("http" , httpPort.substring(httpPort.indexOf(":")+1));
		datanodePorts.put("https" , httpsPort.substring(httpsPort.indexOf(":")+1));
		datanodePorts.put("ipc", ipcPort.substring(ipcPort.indexOf(":")+1));
		
		HadoopConfig jmx = ports.get("queryio.datanode.options");
		String jmxPort = jmx.getValue();
		jmxPort = jmxPort.substring(jmxPort.indexOf("jmxremote.port=")+1);
		datanodePorts.put("jmx" , jmxPort.substring(jmxPort.indexOf('=')+1,jmxPort.indexOf("-Dcom.sun.management")).trim());
		
		ArrayList d = new ArrayList();
		d.add((String) disk.get(0));
		ArrayList dir = new ArrayList();
		dir.add(installDir);
		int host = (Integer) hostid.get(ip);
		DWRResponse response = RemoteManager.addDataNodeInstaller(host, "DataNode1",
				datanodePorts.get("serverPort").toString(),
				datanodePorts.get("http").toString(),
				datanodePorts.get("https").toString(),
				datanodePorts.get("ipc").toString(),
				datanodePorts.get("jmx").toString(), d, dir, isLocal);
		if(response.isTaskSuccess())
		{
			AppLogger.getLogger().fatal("DataNode installed successfully");
			System.out.println("DataNode installed successfully");
			AppLogger.getLogger().fatal("Starting DataNode...");
			System.out.println("Starting DataNode...");
			response = RemoteManager.startNodeInstaller( "DataNode1" ,qioUsername, false);
			if(response.isTaskSuccess())
			{
				AppLogger.getLogger().fatal("DataNode started successfully.");
				System.out.println("DataNode started successfully.");
				return true;
			}
			else
			{
				AppLogger.getLogger().fatal("Failed to start DataNode due to " + response.getResponseMessage() );
				System.out.println("Failed to start DataNode due to " + response.getResponseMessage() );
				error += response.getResponseMessage();
				return false;
			}
		}
		else
		{
			AppLogger.getLogger().fatal("Failed to install DataNode due to " + response.getResponseMessage() );
			System.out.println("Failed to install DataNode due to " + response.getResponseMessage() );
			error += response.getResponseMessage();
			return false;
		}
	}
	
	public Boolean installRM(String ip, boolean isLocal, String installUserHome)
	{
		AppLogger.getLogger().fatal("Installing ResourceManager...");
		System.out.println("Installing ResourceManager...");
		setupService(ip, isLocal, installUserHome);
		HashMap<String, HadoopConfig> ports;
		ArrayList keyList = new ArrayList();
		keyList = new ArrayList();
		keyList.add("yarn.resourcemanager.address");
		keyList.add("yarn.resourcemanager.scheduler.address");
		keyList.add("yarn.resourcemanager.webapp.address");
		keyList.add("yarn.resourcemanager.admin.address");
		keyList.add("yarn.resourcemanager.resource-tracker.address");
		keyList.add("mapreduce.jobhistory.address");
		keyList.add("mapreduce.jobhistory.webapp.address");
		keyList.add("queryio.resourcemanager.options");
		
		ports = RemoteManager.getConfigurationServerPort(keyList);
		
		String serverPort = (ports.get("yarn.resourcemanager.address")).getValue();
		serverPort = serverPort.substring(serverPort.indexOf(':')+1).trim();
		
		String scheduler = (ports.get("yarn.resourcemanager.scheduler.address")).getValue();
		scheduler = scheduler.substring(scheduler.indexOf(':')+1).trim();
		
		String webapp = (ports.get("yarn.resourcemanager.webapp.address")).getValue();
		webapp = webapp.substring(webapp.indexOf(':')+1).trim();
		
		String adminPort = (ports.get("yarn.resourcemanager.admin.address")).getValue();
		adminPort = adminPort.substring(adminPort.indexOf(':')+1).trim();
		
		String tracker = (ports.get("yarn.resourcemanager.resource-tracker.address")).getValue();
		tracker = tracker.substring(tracker.indexOf(':')+1).trim();
		
		
		String hostoryPort = (ports.get("mapreduce.jobhistory.address")).getValue();
		hostoryPort = hostoryPort.substring(hostoryPort.indexOf(':')+1).trim();
		
		String webhostoryPort = (ports.get("mapreduce.jobhistory.webapp.address")).getValue();
		webhostoryPort = webhostoryPort.substring(webhostoryPort.indexOf(':')+1).trim();
		
		
		String jmxPort = (ports.get("queryio.resourcemanager.options")).getValue();
		jmxPort = jmxPort.substring(jmxPort.indexOf("jmxremote.port=")+1);
		jmxPort = jmxPort.substring(jmxPort.indexOf('=')+1,jmxPort.indexOf("-Dcom.sun.management")).trim();
		
		rmPorts = new HashMap();
		rmPorts.put("serverPort", serverPort);
		rmPorts.put("scheduler" , scheduler);
		rmPorts.put("webapp" , webapp);
		rmPorts.put("adminPort", adminPort);
		rmPorts.put("tracker", tracker);
		rmPorts.put("hostoryPort", hostoryPort);
		rmPorts.put("webhostoryPort", webhostoryPort);
		rmPorts.put("jmxPort", jmxPort);
		
		int host = (Integer) hostid.get(ip);
		DWRResponse response = RemoteManager.addResourceManagerInstaller("ResourceManager1", host,
				rmPorts.get("serverPort").toString(),
				rmPorts.get("scheduler").toString(),
				rmPorts.get("webapp").toString(),
				rmPorts.get("adminPort").toString(),
				rmPorts.get("tracker").toString(),
				rmPorts.get("hostoryPort").toString(),
				rmPorts.get("webhostoryPort").toString(),
				rmPorts.get("jmxPort").toString(), installDir);
		if(response.isTaskSuccess())
		{
			AppLogger.getLogger().fatal("ResourceManager installed successfully.");
			System.out.println("ResourceManager installed successfully.");
			AppLogger.getLogger().fatal("Starting ResourceManager...");
			System.out.println("Starting ResourceManager...");
			response = RemoteManager.startNodeInstaller( "ResourceManager1" , qioUsername, false);
			if(response.isTaskSuccess())
			{
				AppLogger.getLogger().fatal("ResourceManager started successfully");
				System.out.println("ResourceManager started successfully");
				return true;
			}
			else
			{
				AppLogger.getLogger().fatal("Failed to start ResourceManager due to " + response.getResponseMessage() );
				System.out.println("Failed to install ResourceManager due to " + response.getResponseMessage() );
				error += response.getResponseMessage();
				return false;
			}
		}
		else
		{
			AppLogger.getLogger().fatal("Failed to install ResourceManager due to " + response.getResponseMessage() );
			System.out.println("Failed to install ResourceManager due to " + response.getResponseMessage() );
			error += response.getResponseMessage();
			return false;
		}
	}
	
	public Boolean installNM(String ip, boolean isLocal, String installUserHome)
	{
		AppLogger.getLogger().fatal("Installing NodeManager...");
		System.out.println("Installing NodeManager...");
		setupService(ip, isLocal, installUserHome);
		HashMap<String, HadoopConfig> ports;
		ArrayList keyList = new ArrayList();
		keyList = new ArrayList();
		keyList.add("yarn.nodemanager.localizer.address");
		keyList.add("yarn.nodemanager.webapp.address");
		keyList.add("queryio.nodemanager.options");
		
		ports = RemoteManager.getConfigurationServerPort(keyList);
		
		String localizer = (ports.get("yarn.nodemanager.localizer.address")).getValue();
		localizer = localizer.substring(localizer.indexOf(':')+1).trim();
		
		String webapp = (ports.get("yarn.nodemanager.webapp.address")).getValue();
		webapp = webapp.substring(webapp.indexOf(':')+1).trim();
		
		String jmxPort = (ports.get("queryio.nodemanager.options")).getValue();
		jmxPort = jmxPort.substring(jmxPort.indexOf("jmxremote.port=")+1);
		jmxPort = jmxPort.substring(jmxPort.indexOf('=')+1,jmxPort.indexOf("-Dcom.sun.management")).trim();
		
		nmPorts = new HashMap();
		nmPorts.put("localizer", localizer);
		nmPorts.put("webapp" , webapp);
		nmPorts.put("jmxPort" , jmxPort);
		
		int host = (Integer) hostid.get(ip);
		DWRResponse response = RemoteManager.addNodeManagerInstaller("NodeManager1", host, "ResourceManager1",
				nmPorts.get("localizer").toString(),
				nmPorts.get("webapp").toString(),
				nmPorts.get("jmxPort").toString(), installDir, isLocal);
		if(response.isTaskSuccess())
		{
			AppLogger.getLogger().fatal("NodeManager installed successfully");
			System.out.println("NodeManager installed successfully");
			AppLogger.getLogger().fatal("Starting NodeManager...");
			System.out.println("Starting NodeManager...");
			response = RemoteManager.startNodeInstaller( "NodeManager1" ,qioUsername, false);
			if(response.isTaskSuccess())
			{
				AppLogger.getLogger().fatal("NodeManager started successfully");
				System.out.println("NodeManager started successfully");
				return true;
			}
			else
			{
				AppLogger.getLogger().fatal("Failed to start NodeManager due to " + response.getResponseMessage() );
				System.out.println("Failed to start NodeManager due to " + response.getResponseMessage() );
				error += response.getResponseMessage();
				return false;
			}
		}
		else
		{
			AppLogger.getLogger().fatal("Failed to install NodeManager due to " + response.getResponseMessage() );
			System.out.println("Failed to install NodeManager due to " + response.getResponseMessage() );
			error += response.getResponseMessage();
			return false;
		}
	}
}