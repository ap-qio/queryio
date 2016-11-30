package com.queryio.core.notification;

import java.sql.Connection;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.dao.NotifyDAO;

public class NotificationSettingsManager {
	public static NotifyBean getNotificationSettings() {
		NotifyBean notifyBean = new NotifyBean();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			notifyBean = NotifyDAO.getNotificationSettings(connection);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching Notification Settings");
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		return notifyBean;
	}

	public static void setNotificationSettings(NotifyBean notifyBean) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			NotifyDAO.updateNotificationSettings(connection, notifyBean);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching Notification Settings");
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
	}
}
