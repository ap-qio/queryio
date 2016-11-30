package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.BillingReportInfo;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.monitor.beans.SummaryTable;

public class BillingDAO {
	public static void insertBillingReportEntry(Connection connection, BillingReportInfo info) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("insertBillingReportEntry: info.getUsedStorage(): " + info.getUsedStorage());

		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_INSERT_BILLING_REPORT_ENTRY);

			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(new Date(System.currentTimeMillis()));

			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
			String id = (currentTimestamp.getYear() + 1900) + "_" + (currentTimestamp.getMonth() + 1) + "_"
					+ currentTimestamp.getDate();

			ps.setString(1, id);
			ps.setTimestamp(2, currentTimestamp);
			ps.setLong(3, info.getUsedStorage());
			ps.setLong(4, info.getBytesRead());
			ps.setLong(5, info.getBytesWritten());
			ps.setLong(6, info.getPutRequests());
			ps.setLong(7, info.getGetRequests());
			ps.setLong(8, info.getListRequests());
			ps.setLong(9, info.getDeleteRequests());

			CoreDBManager.executeUpdateStatement(connection, ps);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while inserting new entry for Billing: " + e.getMessage(), e);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static boolean updateBillingReportEntry(Connection connection, Timestamp timestamp) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateBillingReportEntry: timestamp: " + timestamp.toString());

		PreparedStatement ps = null;
		ResultSet rs = null;

		BillingReportInfo reportInfo = null;

		try {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(new Date(System.currentTimeMillis()));

			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
			String id = (currentTimestamp.getYear() + 1900) + "_" + (currentTimestamp.getMonth() + 1) + "_"
					+ currentTimestamp.getDate();

			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_GET_BILLING_DATA_FOR_ID);
			ps.setString(1, id);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) // Entry already present. Calculate new Usage and
							// then add with the previous Usage.
			{
				reportInfo = new BillingReportInfo();
				BillingReportInfo calculatedReportInfo = calculateBillingData(connection,
						NodeDAO.getAllDatanodes(connection),
						(rs.getTimestamp(ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP)), timestamp);

				reportInfo.setUsedStorage(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE)
						+ calculatedReportInfo.getUsedStorage());
				reportInfo.setBytesRead(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD)
						+ calculatedReportInfo.getBytesRead());
				reportInfo.setBytesWritten(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN)
						+ calculatedReportInfo.getBytesWritten());
				reportInfo.setPutRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS)
						+ calculatedReportInfo.getPutRequests());
				reportInfo.setGetRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS)
						+ calculatedReportInfo.getGetRequests());
				reportInfo.setListRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS)
						+ calculatedReportInfo.getListRequests());
				reportInfo.setDeleteRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS)
						+ calculatedReportInfo.getDeleteRequests());

				deletePreviousEntry(connection, id);
				insertBillingReportEntry(connection, reportInfo);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while inserting new entry for Billing: " + e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return false;
	}

	public static void deletePreviousEntry(Connection connection, String id) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("deletePreviousEntry: id: " + id);

		PreparedStatement statement = null;
		try {
			statement = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.DELETE_QRY_BILLING_DATA_FOR_ID);
			statement.setString(1, id);
			CoreDBManager.executeUpdateStatement(connection, statement);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while inserting new entry for Billing: " + e.getMessage(), e);
		} finally {
			DatabaseFunctions.closePreparedStatement(statement);
		}
	}

	public static BillingReportInfo getBillingReportData(Connection connection, Timestamp startTime, Timestamp endTime)
			throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("startTime: " + startTime + " endTime: " + endTime);

		PreparedStatement ps = null;
		ResultSet rs = null;

		BillingReportInfo reportInfo = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_BILLING_DATA);

			ps.setTimestamp(1, startTime);
			ps.setTimestamp(2, endTime);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				reportInfo = new BillingReportInfo();
				reportInfo.setUsedStorage(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE));
				reportInfo.setBytesRead(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD));
				reportInfo.setBytesWritten(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN));
				reportInfo.setPutRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS));
				reportInfo.setGetRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS));
				reportInfo.setListRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS));
				reportInfo.setDeleteRequests(rs.getLong(ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS));
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while fetching data for Billing: " + e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return reportInfo;
	}

	public static int getReplicationCount(Connection connection, Node node) throws Exception {

		int replicationCount = 1;
		try {
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.DFS_REPLICATION_KEY);
			ArrayList values = QueryIOAgentManager.getConfig(host, list, node, "hdfs-site.xml");
			if (!(values == null || values.size() != 1)) {
				replicationCount = Integer.parseInt((String) values.get(0));
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Failed to get Replication count, Exception: " + e.getMessage(), e);
		}
		return replicationCount;
	}

	private static String getNameNodeHost(Connection connection, Node node) throws Exception {
		Host nameHost = HostDAO.getHostDetail(connection, node.getHostId());
		return nameHost.getHostIP();
	}

	public static long getUsedStorageTotal(Connection connection, String nodeId) throws Exception {
		long usedStorage = 0;

		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;

		Statement stmt = null;
		ResultSet rsUsedStorage = null;
		String queryUsedStorage = null;

		try {
			stmt = DatabaseFunctions.getStatement(connection);

			queryUsedStorage = "SELECT " + ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO + " FROM "
					+ QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_SUMMARYDATA";
			rsUsedStorage = DatabaseFunctions.getQueryResultsForStatement(stmt, queryUsedStorage);

			if (rsUsedStorage.next()) {
				fullVolumeInfoList = MonitorDAO
						.parseVolumeInfo(rsUsedStorage.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));

				if (fullVolumeInfoList == null) {
					ArrayList lst = new ArrayList();
					lst.add("N/A");
					lst.add(0);
					lst.add(0);
					lst.add(0);
					fullVolumeInfoList = new ArrayList();
					fullVolumeInfoList.add(lst);
				}

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("fullVolumeInfoList" +
				// fullVolumeInfoList);
				if (fullVolumeInfoList.size() == 1) {
					tList = (ArrayList) fullVolumeInfoList.get(0);
					usedStorage = (Long) tList.get(2);
				} else {
					for (int j = 0; j < fullVolumeInfoList.size(); j++) {
						tList = (ArrayList) fullVolumeInfoList.get(j);
						usedStorage += (Long) tList.get(2);
					}
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rsUsedStorage);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return usedStorage;
	}

	public static long[] getDataTransferred(Connection connection, String nodeId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		long[] dataTransferred = { 0, 0 };

		PreparedStatement ps = null;
		ResultSet rsBytesTransferred = null;
		String queryBytesTransferred = null;

		try {
			queryBytesTransferred = "SELECT SUM(" + ColumnConstants.COL_DN_MONITORDATA_BYTESREAD + ") AS "
					+ ColumnConstants.COL_DN_MONITORDATA_BYTESREAD + ", SUM("
					+ ColumnConstants.COL_DN_MONITORDATA_BYTESWRITE + ") AS "
					+ ColumnConstants.COL_DN_MONITORDATA_BYTESWRITE + " FROM "
					+ QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_CONSOLIDATEDDATA" + " WHERE "
					+ ColumnConstants.COL_MONITORDATA_MONITOR_TIME + ">=? " + " AND "
					+ ColumnConstants.COL_MONITORDATA_MONITOR_TIME + "<?";

			ps = DatabaseFunctions.getPreparedStatement(connection, queryBytesTransferred);
			ps.setTimestamp(1, startTime);
			ps.setTimestamp(2, endTime);

			rsBytesTransferred = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			if (rsBytesTransferred.next()) {

				dataTransferred[0] = rsBytesTransferred.getLong(ColumnConstants.COL_DN_MONITORDATA_BYTESREAD);
				dataTransferred[1] = rsBytesTransferred.getLong(ColumnConstants.COL_DN_MONITORDATA_BYTESWRITE);

			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rsBytesTransferred);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return dataTransferred;
	}

	public static long[] getAllRequestsCount(Connection connection, String nodeId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		long[] requests = { 0, 0, 0, 0 };

		PreparedStatement ps = null;
		ResultSet rsFileOperations = null;
		String queryFileOperations = null;

		try {
			queryFileOperations = "SELECT SUM(" + ColumnConstants.COL_NN_MONITORDATA_FILESWRITE + ") AS "
					+ ColumnConstants.COL_NN_MONITORDATA_FILESWRITE + ", SUM("
					+ ColumnConstants.COL_NN_MONITORDATA_FILESREAD + ") AS "
					+ ColumnConstants.COL_NN_MONITORDATA_FILESREAD + ", SUM("
					+ ColumnConstants.COL_NN_MONITORDATA_LISTOPS + ") AS " + ColumnConstants.COL_NN_MONITORDATA_LISTOPS
					+ ", SUM(" + ColumnConstants.COL_NN_MONITORDATA_DELETEFILEOPS + ") AS "
					+ ColumnConstants.COL_NN_MONITORDATA_DELETEFILEOPS + " FROM "
					+ QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_CONSOLIDATEDDATA" + " WHERE "
					+ ColumnConstants.COL_MONITORDATA_MONITOR_TIME + ">=? " + " AND "
					+ ColumnConstants.COL_MONITORDATA_MONITOR_TIME + "<?";

			ps = DatabaseFunctions.getPreparedStatement(connection, queryFileOperations);
			ps.setTimestamp(1, startTime);
			ps.setTimestamp(2, endTime);

			rsFileOperations = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			if (rsFileOperations.next()) {
				requests[0] = rsFileOperations.getLong(ColumnConstants.COL_NN_MONITORDATA_FILESWRITE);
				requests[1] = rsFileOperations.getLong(ColumnConstants.COL_NN_MONITORDATA_FILESREAD);
				requests[2] = rsFileOperations.getLong(ColumnConstants.COL_NN_MONITORDATA_LISTOPS);
				requests[3] = rsFileOperations.getLong(ColumnConstants.COL_NN_MONITORDATA_DELETEFILEOPS);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rsFileOperations);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return requests;
	}

	public static BillingReportInfo calculateBillingData(Connection connection, ArrayList datanodes,
			Timestamp startTime, Timestamp endTime) throws Exception {
		BillingReportInfo reportInfo = new BillingReportInfo();
		;

		Node node = null;

		long usedStorageTotal = 0;
		long bytesReadTotal = 0;
		long bytesWrittenTotal = 0;
		long putRequestsTotal = 0;
		long getRequestsTotal = 0;
		long listRequestsTotal = 0;
		long deleteRequestsTotal = 0;

		try {
			for (int i = 0; i < datanodes.size(); i++) {
				node = (Node) datanodes.get(i);

				long usedStorage = getUsedStorageTotal(connection, node.getId());
				int replicationCountForHost = Integer.parseInt(
						HadoopConfigDAO.getHadoopConfig(connection, DFSConfigKeys.DFS_REPLICATION_KEY).getValue());
				usedStorageTotal += (long) (usedStorage / replicationCountForHost); // TODO
																					// need
																					// to
																					// be
																					// divided
																					// by
																					// replication
																					// count
																					// for
																					// each
																					// dataNode.
																					// Done

				long[] dataTransferred = getDataTransferred(connection, node.getId(), startTime, endTime);
				bytesReadTotal += dataTransferred[0];
				bytesWrittenTotal += dataTransferred[1];
			}

			ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
			for (int var = 0; var < namenodes.size(); var++) {
				Node namenode = (Node) namenodes.get(var);
				if (namenode != null) {
					long[] requests = getAllRequestsCount(connection, namenode.getId(), startTime, endTime);
					putRequestsTotal += requests[0];
					getRequestsTotal += requests[1];
					listRequestsTotal += requests[2];
					deleteRequestsTotal += requests[3];
				}
			}

			reportInfo.setUsedStorage(usedStorageTotal);
			reportInfo.setBytesRead(bytesReadTotal);
			reportInfo.setBytesWritten(bytesWrittenTotal);
			reportInfo.setPutRequests(putRequestsTotal);
			reportInfo.setGetRequests(getRequestsTotal);
			reportInfo.setListRequests(listRequestsTotal);
			reportInfo.setDeleteRequests(deleteRequestsTotal);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getBillingReportSummaryTable failed with exception: " + e.getMessage(), e);
		} finally {

		}

		return reportInfo;
	}

	public static double[] getBillingRates(Connection connection) throws Exception {
		double[] rates = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_BILLING_RATE);

			if (rs.next()) {
				rates[0] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_USEDSTORAGE + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[1] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_BYTESREAD + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[2] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_BYTESWRITTEN + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[3] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_PUTREQUESTS + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[4] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_GETREQUESTS + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[5] = rs.getDouble(
						ColumnConstants.COL_BILLING_REPORT_DATA_LISTREQUESTS + ColumnConstants.COL_BILLING_APPEND_RATE);
				rates[6] = rs.getDouble(ColumnConstants.COL_BILLING_REPORT_DATA_DELETEREQUESTS
						+ ColumnConstants.COL_BILLING_APPEND_RATE);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return rates;
	}

	public static SummaryTable getBillingReportSummaryTable(Connection connection, ArrayList hostList,
			Timestamp startTime, Timestamp endTime) throws Exception {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		SummaryTable summaryTable = new SummaryTable();
		BillingReportInfo reportInfo = null;

		try {
			// reportInfo = calculateBillingData(connection, hostList,
			// startTime, endTime);
			reportInfo = getBillingReportData(connection, startTime, endTime);

			double[] rates = getBillingRates(connection);

			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;

			colNames.add("Description");
			colNames.add("Usage");
			colNames.add("Charges");

			summaryTable.setColNames(colNames);

			colValues = new ArrayList();
			colValues.add("Storage Used");
			colValues.add(MonitorDAO.getFormattedStorageSize(reportInfo.getUsedStorage()));
			double usedStorageCharge = reportInfo.getUsedStorage() * rates[0];
			colValues.add(usedStorageCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Data transferred out of QueryIO (Bytes Read)");
			colValues.add(MonitorDAO.getFormattedStorageSize(reportInfo.getBytesRead()));
			double bytesReadCharge = reportInfo.getBytesRead() * rates[1];
			colValues.add(bytesReadCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Data transferred in QueryIO (Bytes Written)");
			colValues.add(MonitorDAO.getFormattedStorageSize(reportInfo.getBytesWritten()));
			double bytesWrittenCharge = reportInfo.getBytesWritten() * rates[2];
			colValues.add(bytesWrittenCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Put Requests");
			colValues.add(reportInfo.getPutRequests());
			double putRequestsCharge = reportInfo.getPutRequests() * rates[3];
			colValues.add(putRequestsCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Get Requests");
			colValues.add(reportInfo.getGetRequests());
			double getRequestsCharge = reportInfo.getGetRequests() * rates[4];
			colValues.add(getRequestsCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("List Requests");
			colValues.add(reportInfo.getListRequests());
			double listRequestsCharge = reportInfo.getListRequests() * rates[5];
			colValues.add(listRequestsCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Delete Requests");
			colValues.add(reportInfo.getDeleteRequests());
			double deleteRequestsCharge = reportInfo.getDeleteRequests() * rates[6];
			colValues.add(deleteRequestsCharge);
			summaryTable.addRow(colValues);

			colValues = new ArrayList();
			colValues.add("Total Charges"); // TotalCharges
			colValues.add("");
			colValues.add(usedStorageCharge + bytesReadCharge + bytesWrittenCharge + putRequestsCharge
					+ getRequestsCharge + listRequestsCharge + deleteRequestsCharge);
			summaryTable.addRow(colValues);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getBillingReportSummaryTable failed with exception: " + e.getMessage(), e);
		}

		return summaryTable;
	}
}