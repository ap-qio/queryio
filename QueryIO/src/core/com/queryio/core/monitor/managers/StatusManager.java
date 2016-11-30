package com.queryio.core.monitor.managers;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.StatusDAO;

public class StatusManager {
	public static ArrayList getStatus(String interval) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return StatusDAO.getStatus(connection, interval);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getStatus() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static void addStatus(String nodeId, long timestamp, int status) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			StatusDAO.addStatus(connection, nodeId, timestamp, status);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("addStatus() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}
