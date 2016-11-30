package com.queryio.core.consolidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;

/**
 * The Class ConsolidationUtility.
 */
public class ConsolidationUtility {

	/**
	 * returns all the Attributes of the controller for which consolidation is
	 * to be performed
	 *
	 * @param controllerId
	 *            controller id of the controller
	 * @param connection
	 *            the database connection
	 * @return array of attribute names
	 * @throws Exception
	 *             the exception
	 */
	public static String[] getNodeConsolidationAttributes(Connection connection, String tableName) throws Exception {

		ArrayList nodeAttributeList = new ArrayList();
		String controllerAttributeArray[] = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		Statement st = null;
		StringBuffer sb = new StringBuffer("SELECT * FROM " + tableName);
		int dataType;
		try {
			st = connection.createStatement();
			rs = st.executeQuery(sb.toString());
			rsmd = rs.getMetaData();
			String colName;
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				nodeAttributeList.add(rsmd.getColumnName(i));
			}
			nodeAttributeList.remove(ColumnConstants.COL_MONITORDATA_MONITOR_TIME.toLowerCase());
		} finally {
			DatabaseFunctions.closeStatement(st);
			DatabaseFunctions.closeResultSet(rs);
		}
		controllerAttributeArray = new String[nodeAttributeList.size()];
		nodeAttributeList.toArray(controllerAttributeArray);
		return controllerAttributeArray;
	}

	/**
	 * Returns last consolidate Timestamp from LAST_CONSOLDIATE table.
	 *
	 * @param controllerId
	 *            controller id of the controller
	 * @param connection
	 *            the connection
	 * @param i
	 * @return Last consolidate Timestamp value.
	 * @throws Exception
	 *             the exception
	 */
	public static Timestamp getLastConsolidateTimestamp(Connection connection, String id) throws Exception {
		Timestamp timestamp = null;
		String selectQuery = "SELECT * FROM " + TableConstants.TABLE_LAST_CONSOLIDATE_LOG + " WHERE "
				+ ColumnConstants.COL_LAST_CONSOLIDATE_LOG_ID + " = ?";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = connection.prepareStatement(selectQuery);
			pst.setString(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				timestamp = rs.getTimestamp(ColumnConstants.COL_LAST_CONSOLIDATE_LOG_TIME);
			}
		} finally {
			DatabaseFunctions.closeStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return timestamp;
	}

	public static Timestamp clearLastConsolidationData(Connection connection, String id) throws Exception {
		Timestamp timestamp = null;
		String selectQuery = "DELETE FROM " + TableConstants.TABLE_LAST_CONSOLIDATE_LOG + " WHERE "
				+ ColumnConstants.COL_LAST_CONSOLIDATE_LOG_ID + " = ?";
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(selectQuery);
			pst.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(pst);
		} finally {
			DatabaseFunctions.closeStatement(pst);
		}
		return timestamp;
	}

	/**
	 * Construct and return TickTimestampArray from currentTimestamp.
	 *
	 * @param currentTimestamp
	 *            the current timestamp
	 * @param noOfTicks
	 *            the no of ticks
	 * @param tickDiff
	 *            the tick diff
	 * @return Timestamp Array
	 */
	public static Timestamp[] constructTickTimestampArray(Timestamp currentTimestamp, int noOfTicks, long tickDiff) {
		Timestamp[] tickTimestampArray = new Timestamp[noOfTicks];
		Timestamp t1 = currentTimestamp;
		for (int i = 0; i < noOfTicks; i++) {
			tickTimestampArray[i] = t1;
			t1 = createPreviousTimestamp(t1, tickDiff);
		}
		return tickTimestampArray;
	}

	/**
	 * return previous Timestamp w.r.t. diff
	 *
	 * @param current
	 *            the current
	 * @param diff
	 *            the diff
	 * @return the timestamp
	 */
	private static Timestamp createPreviousTimestamp(Timestamp current, long diff) {
		return new Timestamp(current.getTime() - diff);
	}

	/**
	 * Clears table i.e. deletes all entries in the table.
	 *
	 * @param connection
	 *            the database connection
	 * @param tableName
	 *            the table name
	 * @throws Exception
	 *             the exception
	 */
	public static void clearTables(Connection connection, String tableName) throws Exception {
		String query1 = "DELETE FROM " + tableName;
		Statement st = null;
		try {
			st = connection.createStatement();
			st.execute(query1);
		} finally {
			DatabaseFunctions.closeStatement(st);
		}
	}

	/**
	 * This function returns the lower boundary of the Timestamp passed as an
	 * argument. For example if 9:37 is passed, then it returns 9:00 (minutes,
	 * seconds, milliseconds = 0)
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return the lower boundary timestamp
	 */
	public static Timestamp truncateUptoMinute(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp boundaryTimestamp = new Timestamp(cal.getTimeInMillis());
		return boundaryTimestamp;
	}

	public static Timestamp truncateUptoHour(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Timestamp boundaryTimestamp = new Timestamp(cal.getTimeInMillis());
		return boundaryTimestamp;
	}

	/**
	 * Returns Timestamp value before 1 hour.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return the timestamp before hour
	 */
	public static Timestamp getTimestampBeforeHour(Timestamp timestamp) {
		return new Timestamp(timestamp.getTime() - 3600000L);
	}

	/**
	 * Returns Timestamp value after 1 hour.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return the timestamp after one hour
	 */
	public static Timestamp getTimestampAfterOneHour(Timestamp timestamp) {
		return new Timestamp(timestamp.getTime() + 3600000L);
	}

	public static Timestamp getOldestEntryTimestampForNode(Connection connection, String nodeId, String nodeType)
			throws Exception {
		String selectQuery = "SELECT MIN(" + ColumnConstants.COL_MONITORDATA_MONITOR_TIME + ") " + "FROM "
				+ QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_" + nodeType + "_MONITORDATA";
		Statement st = null;
		ResultSet rs = null;
		Timestamp t = null;
		try {
			st = connection.createStatement();
			rs = st.executeQuery(selectQuery);
			if (rs.next())
				t = rs.getTimestamp(1);
		} finally {
			DatabaseFunctions.closeStatement(st);
			DatabaseFunctions.closeResultSet(rs);
		}
		return t;
	}

	public static void purgeTable(Connection connection, String tableName, String timestampColumn, Timestamp timestamp,
			Long millis) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + timestampColumn + " <= ?");
			pst.setTimestamp(1, new Timestamp(timestamp.getTime() - millis.longValue()));
			pst.execute();
		} finally {
			DatabaseFunctions.closeStatement(pst);
		}

	}

	public static void purgeTable(Connection connection, String tableName, String timestampColumn, Timestamp timestamp,
			Long millis, String compareColumn, String value) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(
					"DELETE FROM " + tableName + " WHERE " + timestampColumn + " <= ? AND " + compareColumn + " = ?");
			pst.setLong(1, timestamp.getTime() - millis.longValue());
			pst.setString(2, value);
			pst.execute();
		} finally {
			DatabaseFunctions.closeStatement(pst);
		}

	}
}