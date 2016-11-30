package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.NotifyBean;

public class NotifyDAO {
	public static void updateNotificationSettings(Connection connection, NotifyBean notifyBean) throws Exception {
		PreparedStatement pst = null;
		Statement st = null;
		// String PREPARED_QRY_UPDATE_NOTIFICATIONSETTINGS = "INSERT INTO " +
		// TableConstants.TABLE_NOTIFICATIONSETTINGS + "("
		// + ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_ENABLED + "," +
		// ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERNAME
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERADD +
		// "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SECUREDPROTOCOL
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPSERVER +
		// "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPPORT
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_AUTHREQUIRED + "," +
		// ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_USERNAME
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_PASSWORD + ","
		// + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_ENABLED
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_NUMBER + "," +
		// ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SERIALPORT
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MANUFACTURER +
		// "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MODEL
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SELECTEDMODEL +
		// "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_BAUDRATE
		// + "," + ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_ENABLED + "," +
		// ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_FILEPATH
		// + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			st = connection.createStatement();
			st.execute("DELETE FROM " + TableConstants.TABLE_NOTIFICATIONSETTINGS);

			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_UPDATE_NOTIFICATIONSETTINGS);
			pst.setBoolean(1, notifyBean.isEmailEnabled());
			pst.setString(2, notifyBean.getEmailSenderName());
			pst.setString(3, notifyBean.getEmailSenderAddress());
			pst.setBoolean(4, notifyBean.isSecuredProtocol());
			pst.setString(5, notifyBean.getEmailSMTPServer());
			pst.setString(6, notifyBean.getEmailSMTPPort());
			pst.setBoolean(7, notifyBean.isAuthRequired());
			pst.setString(8, notifyBean.getEmailUsername());
			pst.setString(9, notifyBean.getEmailPassword());
			pst.setBoolean(10, notifyBean.isSmsEnabled());
			pst.setString(11, notifyBean.getSmsNumber());
			pst.setString(12, notifyBean.getSmsSerialPort());
			pst.setString(13, notifyBean.getSmsManufacturer());
			pst.setString(14, notifyBean.getSmsModel());
			pst.setString(15, notifyBean.getSmsSelectedModel());
			pst.setString(16, notifyBean.getSmsBaudRate());
			pst.setBoolean(17, notifyBean.isLogEnabled());
			pst.setString(18, notifyBean.getLogFilePath());

			CoreDBManager.executeUpdateStatement(connection, pst);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeStatement(st);
		}
	}

	public static NotifyBean getNotificationSettings(Connection connection) throws Exception {
		NotifyBean notifyBean = new NotifyBean();
		Statement st = null;
		ResultSet rs = null;
		try {
			// System.out.println("getNotificationSettings Try");

			String query = QueryConstants.QRY_GET_ALL_NOTIFICATIONSETTINGS;
			st = connection.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				// System.out.println("rs.next()");

				notifyBean.setAuthRequired(rs.getBoolean(ColumnConstants.COL_NOTIFICATIONSETTINGS_AUTHREQUIRED));
				notifyBean.setEmailEnabled(rs.getBoolean(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_ENABLED));
				notifyBean.setEmailPassword(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_PASSWORD));
				notifyBean
						.setEmailSenderAddress(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERADD));
				notifyBean.setEmailSenderName(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SENDERNAME));
				notifyBean.setEmailSMTPPort(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPPORT));
				notifyBean.setEmailSMTPServer(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_SMTPSERVER));
				notifyBean.setEmailUsername(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_EMAIL_USERNAME));
				notifyBean.setLogEnabled(rs.getBoolean(ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_ENABLED));
				notifyBean.setLogFilePath(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_LOG_FILEPATH));
				notifyBean.setSecuredProtocol(rs.getBoolean(ColumnConstants.COL_NOTIFICATIONSETTINGS_SECUREDPROTOCOL));
				notifyBean.setSmsBaudRate(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_BAUDRATE));
				notifyBean.setSmsEnabled(rs.getBoolean(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_ENABLED));
				notifyBean.setSmsManufacturer(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MANUFACTURER));
				notifyBean.setSmsModel(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_MODEL));
				notifyBean.setSmsNumber(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_NUMBER));
				notifyBean
						.setSmsSelectedModel(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SELECTEDMODEL));
				notifyBean.setSmsSerialPort(rs.getString(ColumnConstants.COL_NOTIFICATIONSETTINGS_SMS_SERIALPORT));

				// System.out.println("notifyBean");
				// System.out.println(notifyBean);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(st);
		}
		return notifyBean;
	}
}
