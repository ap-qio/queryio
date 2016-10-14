package com.os3.server.listeners;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.os3.server.common.CustomizableThreadFactory;
import com.os3.server.common.OS3Constants;
import com.os3.server.common.Settings;
import com.os3.server.userinfo.UserInfoManager;
import com.queryio.common.ClassPathUtility;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.servlets.ReinitializeHadoopConf;

public class StartupListener implements ServletContextListener {

	/** The logger. */
    // Creating logger with default scope to avoid - Read access to enclosing field StartupListener.logger is emulated by a synthetic accessor method
    static Logger logger = null;

	/**
	 * Context initialized.
	 *
	 * @param event the event
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) 
	{	
		final ServletContext context = event.getServletContext();
	
		//Reset Thread Count
		
		ManagementFactory.getThreadMXBean().resetPeakThreadCount();
	
		// load log4J properties file
		
		String log4j = event.getServletContext().getRealPath(OS3Constants.LOG4J_PROPERTIES);
		if (log4j != null) {
			PropertyConfigurator.configure(log4j);
		} else {
			BasicConfigurator.configure();
		}
	
		logger = Logger.getLogger("OS3");
		
		try{
			logger.fatal("OS3SERVER: " + log4j);
			if(logger.isDebugEnabled()) logger.debug(event.getServletContext().getServletContextName() + " context initialized.");
	
			EnvironmentalConstants.setAppHome(context.getRealPath("/"));
			EnvironmentalConstants.setWebinfDirectory(context.getRealPath("/WEB-INF"));

			
			logger.info("AppHome: " + EnvironmentalConstants.getAppHome());
			
			// load os3 properties file
			Settings.loadProperties(event.getServletContext().getRealPath(OS3Constants.OS3_PROPERTIES_PATH));
			
			String hadoopConfigPath = (String) context.getAttribute("hadoop_conf");
			EnvironmentalConstants.setHadoopConfPath(hadoopConfigPath);
			ReinitializeHadoopConf.initialize();
			ClassPathUtility.recycleClassLoder();
			OS3Constants.poolName = EnvironmentalConstants.getHadoopConf().get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID, "");
			
			if(EnvironmentalConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID)==null){
				throw new Error("NameNode configuration could not be loaded");
			}
			
			logger.debug("Setting namenodeid");
			
			EnvironmentalConstants.setNameNodeId(EnvironmentalConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			
			EnvironmentalConstants.setEncryptionType(Settings.get(OS3Constants.ENCRYPTION_TYPE));
			EnvironmentalConstants.setStreamBufferSize(Integer.parseInt(Settings.get(OS3Constants.STREAM_BUFFER_SIZE)));
			
			EnvironmentalConstants.setUseKerberos(Settings.getBoolean(OS3Constants.USE_KERBEROS_KEY));
			
			if(EnvironmentalConstants.isUseKerberos()){
	        	String nnUserName = Settings.get(OS3Constants.DFS_NAMENODE_USER_NAME_KEY);
	        	if(nnUserName==null || nnUserName.equals("")){
	        		throw new Error(OS3Constants.DFS_NAMENODE_USER_NAME_KEY + " not set ");
	        	}
	        	
	        	EnvironmentalConstants.setNnUserName(nnUserName);
	        }
			
			System.setProperty( OS3Constants.SUN_SECURITY_KRB5_DEBUG, Settings.get(OS3Constants.SUN_SECURITY_KRB5_DEBUG));
			System.setProperty( OS3Constants.JAVA_SECURITY_KRB5_REALM, Settings.get(OS3Constants.JAVA_SECURITY_KRB5_REALM)); 
			System.setProperty( OS3Constants.JAVA_SECURITY_KRB5_KDC, Settings.get(OS3Constants.JAVA_SECURITY_KRB5_KDC));
			System.setProperty( OS3Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG, event.getServletContext().getRealPath(OS3Constants.JAAS_CONF));
			System.setProperty( OS3Constants.JAVAX_SECURITY_AUTH_USESUBJECTCREDSONLY, Settings.get(OS3Constants.JAVAX_SECURITY_AUTH_USESUBJECTCREDSONLY));
			
			logger.info(OS3Constants.SUN_SECURITY_KRB5_DEBUG + " : " + Settings.get(OS3Constants.SUN_SECURITY_KRB5_DEBUG));
			logger.info(OS3Constants.JAVA_SECURITY_KRB5_REALM + " : " + Settings.get(OS3Constants.JAVA_SECURITY_KRB5_REALM));
			logger.info(OS3Constants.JAVA_SECURITY_KRB5_KDC + " : " + Settings.get(OS3Constants.JAVA_SECURITY_KRB5_KDC));
			logger.info(OS3Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG + " : " + event.getServletContext().getRealPath(OS3Constants.JAAS_CONF));
			logger.info(OS3Constants.JAVAX_SECURITY_AUTH_USESUBJECTCREDSONLY + " : " + Settings.get(OS3Constants.JAVAX_SECURITY_AUTH_USESUBJECTCREDSONLY));
			
			logger.info("Enc Type: " + EnvironmentalConstants.getEncryptionType());
			logger.info("Buffer Size: " + EnvironmentalConstants.getStreamBufferSize());
			
			logger.debug("Configuring thread pool");
			
			if (Settings.getBoolean(OS3Constants.USE_THREAD_POOL)) {
				// Creation of a global async Executor with CallerRunsPolicy. If Queue is full then calling thread will execute the task.
				final ThreadPoolExecutor executor = new ThreadPoolExecutor(Settings.getIntProperty(OS3Constants.CORE_POOL_SIZE, OS3Constants.DEFAULT_CORE_POOL_SIZE),
						Settings.getIntProperty(OS3Constants.MAXIMUM_POOL_SIZE, OS3Constants.DEFAULT_MAXIMUM_POOL_SIZE), Settings.getIntProperty(OS3Constants.KEEP_ALIVE_TIME, OS3Constants.DEFAULT_KEEP_ALIVE_TIME), TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(Settings.getIntProperty(OS3Constants.QUEUE_CAPACITY, OS3Constants.DEFAULT_QUEUE_CAPACITY)),  new ThreadPoolExecutor.CallerRunsPolicy());
				executor.setThreadFactory(new CustomizableThreadFactory(true, OS3Constants.ASYNC_THREAD));
				event.getServletContext().setAttribute(OS3Constants.ASYNC_REQUEST_EXECUTOR, executor);
			}
			
			logger.info("Use Kerberos: " + EnvironmentalConstants.isUseKerberos());
			
			logger.debug("Loading database configuration");
			EnvironmentalConstants.setJdbcDriverPath( EnvironmentalConstants.getAppHome() + "/../../../" 
					+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps" + File.separator + QueryIOConstants.AGENT_QUERYIO 
					+ File.separator +QueryIOConstants.JDBC_JAR_DIR);
			EnvironmentalConstants.setDbConfigFilePath(EnvironmentalConstants.getAppHome() + "/../../../" 
					+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps" + File.separator + QueryIOConstants.AGENT_QUERYIO
					+ File.separator + "conf" + File.separator +  QueryIOConstants.DBCONFIG_XML);
			
			try 
			{
				new DatabaseConfigParser().loadDatabaseConfiguration(EnvironmentalConstants.getDbConfigFilePath());
				CoreDBManager.initialize();
			}
			catch (Exception e) 
			{
				logger.fatal("Error Initialization Database Connection", e);
			}
			
			try {
				UserInfoManager.fetchUserInformation();
			} catch (Exception e) {
				logger.fatal("User information could not be retrieved", e);
				throw new Error("User information could not be retrieved", e);
			}
			
			logger.debug("OS3Server started...");
		}catch(Exception e){
			logger.fatal("Initialization failed with exception: " + e.getMessage(), e);
			throw new Error("OS3 server could not be started", e);
		}
	}
	
	/**
	 * Context destroyed.
	 *
	 * @param event the event
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		if (Settings.getBoolean(OS3Constants.USE_THREAD_POOL) && event.getServletContext().getAttribute(OS3Constants.ASYNC_REQUEST_EXECUTOR) != null) {
			((ThreadPoolExecutor)event.getServletContext().getAttribute(OS3Constants.ASYNC_REQUEST_EXECUTOR)).shutdown();
			event.getServletContext().removeAttribute(OS3Constants.ASYNC_REQUEST_EXECUTOR);
		}
		if(logger.isDebugEnabled()) logger.debug(event.getServletContext().getServletContextName() + " context destroyed.");
	}
}