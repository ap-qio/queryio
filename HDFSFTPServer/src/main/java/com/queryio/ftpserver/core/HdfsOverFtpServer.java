package com.queryio.ftpserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.log4j.Logger;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.HadoopConstants;
import com.queryio.common.QueryIOConstants;

public class HdfsOverFtpServer {
	private static Logger log = Logger.getLogger(HdfsOverFtpServer.class);
	private static int port = 2222;
	private static int sslPort = 2226;
	private static String passivePorts = null;
	private static String sslPassivePorts = null;
	private static String sslPassword = null;
	private static String nnUserName = null;
	private static boolean useKerberos = false;
	private static boolean sslEnabled = false;

	public static String HDFS_OVER_FTP_CONF_FILE_NAME = "hdfs-over-ftp.conf";
	public static String LOG4J_PROPERTIES_FILE = "/WEB-INF/log4j.properties";
	public static String keystoreFile = null;
	// public static String FTP_JKS_FILE_NAME = "ftp.jks";
	public static String USERS_CONF_FILE_NAME = "users.conf";
	public static String HADOOP_CONFIG_PATH = "";

	public static void launch(String configFileContainer) throws Exception {
		log.info("Starting...");

		startServer(configFileContainer);
		if (sslEnabled) {
			log.info("starting ssl server");
			startSSLServer(configFileContainer);
		}
		log.info("HdfsOverFtpServer started");
	}

	public static void loadConfig(String fileContainer) throws IOException {

		Properties props = new Properties();
		props.load(new FileInputStream(fileContainer
				+ HDFS_OVER_FTP_CONF_FILE_NAME));

		// queryio.ftpserver.port
		port = Integer.parseInt(HadoopConstants.getHadoopConf()
				.get(QueryIOConstants.QUERYIO_FTPSERVER_PORT));
		passivePorts = (port + 1) + "-" + (port + 3);
		log.info("port: " + port);
		log.info("passivePorts: " + passivePorts);
		log.info("sslEnabled: "
				+ HadoopConstants.getHadoopConf()
						.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLENABLED));
		sslEnabled = Boolean.parseBoolean(HadoopConstants.getHadoopConf().get(
				QueryIOConstants.QUERYIO_FTPSERVER_SSLENABLED, "false"));
		
		if (sslEnabled) {
			sslPort = Integer.parseInt(HadoopConstants.getHadoopConf()
					.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLPORT));
			sslPassivePorts = (sslPort + 1) + "-" + (sslPort + 3);
			sslPassword = HadoopConstants.getHadoopConf()
					.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLPASSWORD);
			keystoreFile = HadoopConstants.getHadoopConf()
					.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLKEYSTORE);
			log.info("sslPort: " + sslPort);
			log.info("sslPassivePorts: " + sslPassivePorts);
			log.info("sslPassword: " + sslPassword);
			log.info("keystoreFile: " + keystoreFile);
		}

		nnUserName = props.getProperty("namenode-full-user-name");
		if (nnUserName == null) {
			log.fatal("namenode-full-user-name is not set");
			System.exit(1);
		}

		useKerberos = Boolean.parseBoolean(props.getProperty("useKerberos"));

		EnvironmentalConstants.setUseKerberos(useKerberos);

		System.setProperty("sun.security.krb5.debug",
				props.getProperty("sun.security.krb5.debug"));
		System.setProperty("java.security.krb5.realm",
				props.getProperty("java.security.krb5.realm"));
		System.setProperty("java.security.krb5.kdc",
				props.getProperty("java.security.krb5.kdc"));

		String defaultGroup = props.getProperty("defaultGroup");
		if (defaultGroup == null || defaultGroup.equals("")) {
			log.fatal("defaultGroup is not set");
			System.exit(1);
		}
	}

	public static void startServer(String fileContainer) throws Exception {
		log.info("Starting Hdfs-Over-Ftp server. port: " + port
				+ " data-ports: " + passivePorts);

		FtpServerFactory ftpServerFactory = new FtpServerFactory();

		DataConnectionConfigurationFactory dataConFactory = new DataConnectionConfigurationFactory();
		dataConFactory.setPassivePorts(passivePorts);

		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setDataConnectionConfiguration(dataConFactory
				.createDataConnectionConfiguration());
		listenerFactory.setPort(port);

		Map listenerMap = new HashMap();
		listenerMap.put("default", listenerFactory.createListener());

		HdfsUserManager userManager = new HdfsUserManager();
		userManager.setFile(new File(fileContainer + USERS_CONF_FILE_NAME));

		ftpServerFactory.setUserManager(userManager);

		ftpServerFactory.setListeners(listenerMap);
		ftpServerFactory.setFileSystem(new HdfsFileSystemFactory());

		FtpServer ftpServer = ftpServerFactory.createServer();
		ftpServer.start();

		log.info("FTP Server started");
	}

	public static void startSSLServer(String fileContainer) throws Exception {
		log.info("Starting Hdfs-Over-Ftp SSL server. ssl-port: " + sslPort
				+ " ssl-data-ports: " + sslPassivePorts);

		FtpServerFactory ftpServerFactory = new FtpServerFactory();

		DataConnectionConfigurationFactory dataConFactory = new DataConnectionConfigurationFactory();
		dataConFactory.setPassivePorts(sslPassivePorts);

		SslConfigurationFactory sslConfigurationFactory = new SslConfigurationFactory();
		sslConfigurationFactory.setKeystoreFile(new File(keystoreFile));
		sslConfigurationFactory.setKeystorePassword(sslPassword);

		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setDataConnectionConfiguration(dataConFactory
				.createDataConnectionConfiguration());
		listenerFactory.setPort(sslPort);
		listenerFactory.setSslConfiguration(sslConfigurationFactory
				.createSslConfiguration());
		listenerFactory.setImplicitSsl(true);

		Map listenerMap = new HashMap();
		listenerMap.put("default", listenerFactory.createListener());

		HdfsUserManager userManager = new HdfsUserManager();
		userManager.setFile(new File(fileContainer + USERS_CONF_FILE_NAME));
		ftpServerFactory.setUserManager(userManager);

		ftpServerFactory.setListeners(listenerMap);
		ftpServerFactory.setFileSystem(new HdfsFileSystemFactory());

		FtpServer ftpServer = ftpServerFactory.createServer();
		ftpServer.start();
	}

	public static String getNNUserName() {
		return nnUserName;
	}

	public static boolean isUseKerberos() {
		return useKerberos;
	}
}