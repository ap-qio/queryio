package com.queryio.core.monitor.controllers;

import java.sql.Connection;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.adhoc.AdHocHiveClient;
import com.queryio.core.dao.QueryIOServiceDAO;

public class HiveServicesController extends Thread {
	private String nodeId;
	private boolean flag;
	private Connection connection;

	public HiveServicesController(String nodeId) {
		this.nodeId = nodeId;
	}

	public void run() {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting HiveServicesController for node: " + nodeId);
		flag = true;

		String statusHive = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Failed to start QueryIOServicesController for nodeId: " + nodeId, e);
			flag = false;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error closing connection.", e);
			}
		}
		while (flag) {
			try {
				connection = CoreDBManager.getQueryIODBConnection();

				try {
					AdHocHiveClient.isHiveStarted(nodeId);
					statusHive = QueryIOConstants.STATUS_STARTED;
				} catch (Exception e) {
					statusHive = QueryIOConstants.STATUS_NOT_RESPONDING;
				}

				QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_HIVE, statusHive);

			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error occured during HiveServices monitoring for nodeId: " + nodeId,
							e);
				flag = false;
			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Error closing connection.", e);
				}
			}
			if (this.flag) {
				try {
					Thread.sleep(StartupParameters.getDataFetchIntervalInSeconds() * 1000);
				} catch (InterruptedException e1) {
					// AppLogger.getLogger().fatal(e1.getMessage(), e1);
				}
			}
		}
	}

	public void stopCollectingData() {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping HiveServicesController for node: " + nodeId);
		this.flag = false;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_HIVE,
					QueryIOConstants.STATUS_STOPPED);
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Failed to stop HiveServicesController for nodeId: " + nodeId, e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error closing connection.", e);
			}
		}
		super.interrupt();
	}
}
