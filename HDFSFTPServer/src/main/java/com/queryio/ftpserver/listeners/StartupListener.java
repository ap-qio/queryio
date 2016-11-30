package com.queryio.ftpserver.listeners;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.queryio.common.ClassPathUtility;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.ftpserver.core.HdfsOverFtpServer;
import com.queryio.ftpserver.userinfo.UserInfoManager;
import com.queryio.servlets.ReinitializeHadoopConf;

public class StartupListener implements ServletContextListener {

	/** The logger. */
	// Creating logger with default scope to avoid - Read access to enclosing
	// field StartupListener.logger is emulated by a synthetic accessor method
	static Logger logger = null;

	/**
	 * Context initialized.
	 *
	 * @param event
	 *            the event
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {

		final ServletContext context = event.getServletContext();

		String log4j = event.getServletContext().getRealPath(HdfsOverFtpServer.LOG4J_PROPERTIES_FILE);
		if (log4j != null) {
			PropertyConfigurator.configure(log4j);
		} else {
			BasicConfigurator.configure();
		}

		logger = Logger.getLogger("FTP");

		try {
			logger.debug("FTPSERVER: " + log4j);
			String hadoopConfigPath = (String) context.getAttribute("hadoop_conf");
			HdfsOverFtpServer.hadoopConfigPath = hadoopConfigPath;

			EnvironmentalConstants.setAppHome(context.getRealPath("/"));
			EnvironmentalConstants.setWebinfDirectory(context.getRealPath("/WEB-INF"));
			EnvironmentalConstants.setJdbcDriverPath(EnvironmentalConstants.getAppHome() + "/../../../"
					+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps" + File.separator
					+ QueryIOConstants.AGENT_QUERYIO + File.separator + QueryIOConstants.JDBC_JAR_DIR);
			EnvironmentalConstants.setDbConfigFilePath(
					EnvironmentalConstants.getAppHome() + "/../../../" + QueryIOConstants.QUERYIOAGENT_DIR_NAME
							+ File.separator + "webapps" + File.separator + QueryIOConstants.AGENT_QUERYIO
							+ File.separator + "conf" + File.separator + QueryIOConstants.DBCONFIG_XML);

			try {
				new DatabaseConfigParser().loadDatabaseConfiguration(EnvironmentalConstants.getDbConfigFilePath());
				CoreDBManager.initialize();
			} catch (Exception e) {
				logger.fatal("Error Initialization Database Connection", e);
				throw new Error("Error Initialization Database Connection", e);
			}

			String container = context.getRealPath("/WEB-INF") + "/";

			try {

				logger.debug("Configuring...");
				EnvironmentalConstants.setHadoopConfPath(hadoopConfigPath);
				ReinitializeHadoopConf.initialize();
				ClassPathUtility.recycleClassLoder();
				HdfsOverFtpServer.loadConfig(container);

				UserInfoManager.fetchUserInformation();

				HdfsOverFtpServer.launch(container);
			} catch (Exception e) {
				logger.fatal("FTP server could not be started", e);
				throw new Error("FTP server could not be started", e);
			}

			logger.debug("FTPServer started...");
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			throw new Error("FTP server could not be started", e);
		}
	}

	/**
	 * Context destroyed.
	 *
	 * @param event
	 *            the event
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {

	}
}