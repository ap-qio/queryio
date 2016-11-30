package com.queryio.web.listener;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.proc.manager.ProcManager;
import com.queryio.sysmoncommon.engine.SystemMonitor;
import com.queryio.sysmoncommon.sysmon.DataParserFactory;

/**
 * <p>
 * StartupListener class used to initialize and database settings and populate
 * any application-wide drop-downs.
 * <p>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the Dao or service level to avoid
 * LazyInitializationException. Hibernate.initialize() works well for doing
 * this.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class AgentStartupListener implements ServletContextListener {
	public void contextInitialized(final ServletContextEvent event) {
		final ServletContext context = event.getServletContext();
		System.out.println("INFO: Starting QueryIOAgent");
		EnvironmentalConstants.setAppHome(context.getRealPath("/") + "/");
		EnvironmentalConstants.setWebinfDirectory(context.getRealPath("/WEB-INF"));

		AppLogger.initLoggerProperties(EnvironmentalConstants.getAppHome(),
				EnvironmentalConstants.getAppHome() + "logs/");

		FileInputStream fis = null;
		try {
			final StringBuffer sbFileName = new StringBuffer(EnvironmentalConstants.getAppHome());
			sbFileName.append("conf/");
			sbFileName.append(QueryIOConstants.QUERYIOAGENTPROPERTIES_FILENAME);
			Properties prop = new Properties();
			fis = new FileInputStream(sbFileName.toString());
			prop.load(fis);
			StartupParameters.setDefaultDirRemote(prop.getProperty(QueryIOConstants.DEFAULT_DIR_REMOTE));
			StartupParameters.setHadoopDirLocation(
					EnvironmentalConstants.getAppHome() + prop.getProperty(QueryIOConstants.HADOOP_DIR_LOCATION));
			StartupParameters.setSessionTimeout(Integer.parseInt(prop.getProperty(QueryIOConstants.SESSION_TIMEOUT)));
			StartupParameters.setQueryIOAgentPort(prop.getProperty(QueryIOConstants.QUERYIOAGENT_PORT));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error reading queryio.properties file", e);
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}

		DataParserFactory.initialize(EnvironmentalConstants.getAppHome());
		try {
			SystemMonitor.startMonitoring();
		} catch (Exception e) {
			AppLogger.getLogger().error("Error While Starting System monitoring:" + e.getLocalizedMessage());
		}

		ProcManager.INSTALL_DIR = EnvironmentalConstants.getAppHome() + "../../../";
		ProcManager.MONITOR_CONFIG_FILE = EnvironmentalConstants.getAppHome() + "monitor.config";
		ProcManager.APP_HOME = EnvironmentalConstants.getAppHome();

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("INSTALL_DIR: " + ProcManager.INSTALL_DIR);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("STATUS_FILE: " + ProcManager.STATUS_FILE);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("MONITOR_CONFIG_FILE: " + ProcManager.MONITOR_CONFIG_FILE);

		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Callling ProcManager.addAgentStartUpMonitor()");

			ProcManager.addAgentStartUpMonitor();

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Completed ProcManager.addAgentStartUpMonitor()");

			AppLogger.getLogger().fatal("QueryIOAgent startup scheduler configured.");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("QueryIOAgent startup scheduler could not be configured.", e);
			e.printStackTrace();
		}

		EnvironmentalConstants.setJdbcDriverPath(EnvironmentalConstants.getAppHome() + QueryIOConstants.JDBC_JAR_DIR);

		EnvironmentalConstants
				.setDbConfigFilePath(EnvironmentalConstants.getAppHome() + "conf" + File.separator + "dbconfig.xml");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Initializing Database Connection");
		try {
			new DatabaseConfigParser().loadDatabaseConfiguration(EnvironmentalConstants.getDbConfigFilePath());
			CoreDBManager.initialize();
		} catch (Throwable e) {
			AppLogger.getLogger().fatal("Error Initialization Database Connection", e);
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Database Connection Initialized");
	}

	public void contextDestroyed(final ServletContextEvent event) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping Node Status Monitor");
	}
}
