package com.queryio.core.customtags;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.icu.text.SimpleDateFormat;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.DataTableParams;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.monitor.beans.SummaryTable;

public class BigQueryDAO {

	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");

	public static void deleteBigQuery(final Connection connection, String id, String namenodeId, String user)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_BIG_QUERY);
			stmt.setString(1, id);
			stmt.setString(2, namenodeId);
			stmt.setString(3, user);

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateBigQuery(final Connection connection, String id, JSONObject properties, String namenodeId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_BIG_QUERY);
			stmt.setString(1, properties.toJSONString());
			stmt.setString(2, id);
			stmt.setString(3, namenodeId);

			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("Properties: " +
			// properties.toJSONString());

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void saveBigQuery(final Connection connection, String id, JSONObject properties) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_BIG_QUERY);
			stmt.setString(1, id);
			stmt.setString(2, properties.toJSONString());

			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("Properties: " +
			// properties.toJSONString());

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static boolean isQueryExist(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_BIGQUERY_IS_QUERY_EXIST);
			ps.setString(1, id);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				if (rs.getLong(1) == 0)
					return false;
				else
					return true;
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return false;
	}

	public static void saveBigQuery(final Connection connection, String id, String queryDesc, BufferedReader reader)
			throws SQLException, IOException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_BIG_QUERY);

			StringWriter writer = new StringWriter();
			IOUtils.copy(reader, writer);

			stmt.setString(1, id);
			stmt.setString(2, writer.toString());
			stmt.setString(3, queryDesc);
			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

	}

	public static void saveBigQuery(final Connection connection, String id, String queryDesc, JSONObject properties,
			String namenodeId, String dbName, String user) throws SQLException, IOException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_BIG_QUERY);
			stmt.setString(1, id);
			stmt.setString(2, properties.toJSONString());
			stmt.setString(3, queryDesc);
			stmt.setString(4, namenodeId);
			stmt.setString(5, dbName);
			stmt.setString(6, user);

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

	}

	public static void saveChartPreferences(final Connection connection, JSONObject properties)
			throws SQLException, IOException {
		PreparedStatement stmt = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_CHART_PREFERENCES);
			stmt.setString(1, properties.toJSONString());
			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

	}

	public static JSONObject getChartPreferences(final Connection connection) throws Exception {
		JSONObject chartPreferences = null;
		JSONParser parser = new JSONParser();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_CHART_PREFERENCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				String str = rs.getString(ColumnConstants.COL_CHARTPREFERENCES_PROPERTIES);
				if (str != null) {
					chartPreferences = (JSONObject) parser.parse(str);
				}
			}
		} catch (ParseException pe) {
			AppLogger.getLogger().fatal("Chart preference object found null in database.");
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return chartPreferences;
	}

	public static String getBigQueryDbName(final Connection connection, String queryId, String namenodeId,
			String userName) throws Exception {

		String dbName = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_BIGQUERIES_DBNAME);
			ps.setString(1, queryId);
			ps.setString(2, namenodeId);
			ps.setString(3, userName);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				dbName = rs.getString(ColumnConstants.COL_BIGQUERIES_DBNAME);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return dbName;
	}

	public static JSONObject getBigQueryInfo(final Connection connection, String queryId, String namenodeId,
			String userName) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONObject properties = null;
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_BIGQUERIES);
			ps.setString(1, queryId);
			ps.setString(2, namenodeId);
			ps.setString(3, userName);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				String jsonString = rs.getString(ColumnConstants.COL_BIGQUERIES_PROPERTIES);
				JSONParser parser = new JSONParser();
				properties = (JSONObject) parser.parse(jsonString);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return properties;
	}

	public static ArrayList<String> getAllBigQueriesId(final Connection connection, String namenodeId)
			throws Exception {
		ArrayList<String> queries = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_BIGQUERIES);
			ps.setString(1, namenodeId);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				String queryId = rs.getString(ColumnConstants.COL_BIGQUERIES_ID);
				queries.add(queryId);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return queries;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAllBigQueriesInfo(final Connection connection, String namenodeId, String user)
			throws Exception {

		JSONObject queryInfo = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
						.debug("QueryConstants.GET_ALL_BIGQUERIES_INFO - " + QueryConstants.GET_ALL_BIGQUERIES_INFO);
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_BIGQUERIES_INFO);
			ps.setString(1, namenodeId);
			ps.setString(2, user);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", rs.getString(ColumnConstants.COL_BIGQUERIES_ID));
				obj.put(QueryIOConstants.DESCRIPTION, rs.getString(ColumnConstants.COL_BIGQUERIES_DESCRIPTION));
				obj.put(QueryIOConstants.NAMENODE, rs.getString(ColumnConstants.COL_BIGQUERIES_NAMENODEID));
				obj.put(QueryIOConstants.DBNAME, rs.getString(ColumnConstants.COL_BIGQUERIES_DBNAME));
				obj.put(QueryIOConstants.STATUS, rs.getString(ColumnConstants.COL_QUERYEXECUTION_STATUS));
				obj.put("reportpath", rs.getString(ColumnConstants.COL_QUERYEXECUTION_PATH));
				queryInfo.put(rs.getString(ColumnConstants.COL_BIGQUERIES_ID), obj);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return queryInfo;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAllBigQueriesInfo(final Connection connection, String namenodeId, String user,
			String paramsDT) throws Exception {
		String[] dbColumnLabels = { "BIGQUERIES.ID", "BIGQUERIES.DESCRIPTION", "QUERYEXECUTION.STATUS",
				"BIGQUERIES.DBNAME" };
		DataTableParams params = null;
		JSONObject bigQueryInfo = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			params = new DataTableParams(paramsDT, dbColumnLabels.length);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Parsed DataTable params - " + params.toString());
			String query = QueryConstants.GET_ALL_BIGQUERIES_INFO;

			// add search condition
			if (!params.getSearchVal().isEmpty()) {
				query += " AND ( ";
				if (params.getSearchColIndex() > -1) {
					query += dbColumnLabels[params.getSearchColIndex()];
					query += " LIKE '%" + params.getSearchVal() + "%' ";
				} else {
					for (int i = 0; i < dbColumnLabels.length; i++) {
						query += dbColumnLabels[i];
						query += " LIKE '%" + params.getSearchVal() + "%' ";
						if (i < dbColumnLabels.length - 1) {
							query += " OR ";
						}
					}
				}
				query += " )";
			}
			// get filtered record count
			int filterCount = getBigQueryCount(connection, "SELECT COUNT(*) FROM (" + query + ") AS COUNTER",
					namenodeId, user); // filtered query count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("recordsFiltered: " + filterCount);

			// apply sorting
			query += " ORDER BY UPPER ("
					+ dbColumnLabels[params.getOrderByCol() > 0 ? (int) params.getOrderByCol() - 1 : 0] + ") "
					+ params.getOrderByDir();

			// set range of records
			if (params.getCountInt() > 0) {
				query += " LIMIT ";
				query += params.getOffsetInt();
				query += ",";
				query += params.getCountInt();
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("QueryConstants.GET_ALL_BIGQUERIES_INFO - " + query);
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			ps.setString(1, namenodeId);
			ps.setString(2, user);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			JSONArray tableAll = new JSONArray();
			while (rs.next()) {
				JSONArray tableRow = new JSONArray();
				tableRow.add(rs.getString(ColumnConstants.COL_BIGQUERIES_ID));
				tableRow.add(rs.getString(ColumnConstants.COL_BIGQUERIES_DESCRIPTION));
				tableRow.add(rs.getString(ColumnConstants.COL_BIGQUERIES_NAMENODEID));
				tableRow.add(rs.getString(ColumnConstants.COL_BIGQUERIES_DBNAME));
				tableRow.add(rs.getString(ColumnConstants.COL_QUERYEXECUTION_STATUS));
				tableRow.add(rs.getString(ColumnConstants.COL_QUERYEXECUTION_PATH));
				tableAll.add(tableRow);
			}

			int totalCount = getBigQueryCount(connection, QueryConstants.GET_ALL_BIGQUERIES_COUNT, namenodeId, user); // total
																														// query
																														// count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("recordsTotal: " + totalCount);
			bigQueryInfo.put("data", tableAll);
			bigQueryInfo.put("draw", params.getDraw());
			bigQueryInfo.put("recordsTotal", totalCount);
			bigQueryInfo.put("recordsFiltered", filterCount);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return bigQueryInfo;
	}

	public static int getBigQueryCount(Connection connection, String query, String namenodeId, String user)
			throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(query);
			stmt.setString(1, namenodeId);
			stmt.setString(2, user);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return count;
	}

	public static int getQueryExecutionId(Connection connection) throws Exception {

		Statement stmt = null;
		ResultSet rs = null;
		int id = 0;

		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.GET_QUERYEXECUTION_MAX_ID);

			if (rs.next()) {
				id = rs.getInt(1);
				id++;
			}

		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return id;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static JSONObject getResultTableName(Connection connection, String selectedTable) throws Exception {
		JSONObject response = null;
		String resultTableName = null;
		Statement stmt = null;
		ResultSet rs = null;
		int id = 0;

		try {
			// stmt = DatabaseFunctions.getStatement(connection);
			// rs = DatabaseFunctions.getQueryResultsForStatement(stmt,
			// QueryConstants.GET_QUERYEXECUTION_MAX_ID);

			// if (rs.next())
			// {
			// id = rs.getInt(1);
			// }

			Date date = new Date(System.currentTimeMillis());
			StringBuffer sb = new StringBuffer();
			sb.append(1900 + date.getYear());
			sb.append('_');
			sb.append(date.getMonth() + 1);
			sb.append('_');
			sb.append(date.getDate());
			sb.append('_');
			sb.append(date.getHours());
			sb.append('_');
			sb.append(date.getMinutes());
			sb.append('_');
			sb.append(date.getSeconds());

			resultTableName = selectedTable + "_result_" + sb.toString();
			// resultTableName = "result_" + (id + 1) + "_" + selectedTable;
			response = new JSONObject();
			response.put("resultTableName", resultTableName);
			response.put("executionId", (id + 1));
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return response;
	}

	public static void addUpdateSpreadSheetQueryStatus(Connection connection, String queryId, String namenodeId,
			String userName, String currentstep, String status, String error, boolean isUpdate) throws Exception {

		PreparedStatement ps = null;
		try {
			if (isUpdate) {
				ps = DatabaseFunctions.getPreparedStatement(connection,
						"UPDATE SPREADSHEETQUERYSTATUS  SET CURRENTSTEP =?, STATUS =?, ERROR =?  WHERE QUERYID = ? AND NAMENODEID =? AND USERNAME =?");
				ps.setString(1, currentstep);
				ps.setString(2, status);
				ps.setString(3, error);

				ps.setString(4, queryId);
				ps.setString(5, namenodeId);
				ps.setString(6, userName);
			} else {
				deleteSpreadSheetQueryStatus(connection, queryId, namenodeId, userName);
				ps = DatabaseFunctions.getPreparedStatement(connection,
						"INSERT INTO SPREADSHEETQUERYSTATUS (QUERYID, NAMENODEID, USERNAME, CURRENTSTEP, STATUS, ERROR ) VALUES (?, ?, ?, ?, ?, ?)");
				ps.setString(1, queryId);
				ps.setString(2, namenodeId);
				ps.setString(3, userName);
				ps.setString(4, currentstep);
				ps.setString(5, status);
				ps.setString(6, error);
			}

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateSpreadSheetQueryError(Connection connection, String queryId, String namenodeId,
			String userName, String error) {

		PreparedStatement ps = null;
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection,
					"UPDATE SPREADSHEETQUERYSTATUS  SET STATUS ='FAILED', ERROR =?  WHERE QUERYID = ? AND NAMENODEID =? AND USERNAME =?");
			ps.setString(1, error);

			ps.setString(2, queryId);
			ps.setString(3, namenodeId);
			ps.setString(4, userName);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException se) {
				AppLogger.getLogger().fatal(se.getMessage(), se);

			}
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getSpreadSheetQueryStatus(Connection connection, String queryId, String namenodeId,
			String username) throws Exception {

		PreparedStatement ps = null;
		JSONObject statusObj = new JSONObject();
		statusObj.put("ID", queryId);
		try {

			ps = DatabaseFunctions.getPreparedStatement(connection,
					"SELECT * FROM SPREADSHEETQUERYSTATUS  WHERE QUERYID = ? AND NAMENODEID =? AND USERNAME =?");
			ps.setString(1, queryId);
			ps.setString(2, namenodeId);
			ps.setString(3, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				statusObj.put("STATUS", rs.getString("STATUS"));
				statusObj.put("CURRENTSTEP", rs.getString("CURRENTSTEP"));
				statusObj.put("ERROR", rs.getString("ERROR"));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
		return statusObj;
	}

	public static void deleteSpreadSheetQueryStatus(Connection connection, String queryId, String namenodeId,
			String username) throws Exception {

		PreparedStatement ps = null;

		try {

			ps = DatabaseFunctions.getPreparedStatement(connection,
					"DELETE FROM SPREADSHEETQUERYSTATUS  WHERE QUERYID = ? AND NAMENODEID =? AND USERNAME =?");
			ps.setString(1, queryId);
			ps.setString(2, namenodeId);
			ps.setString(3, username);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}

	}

	public static void dropBigQueryTable(final Connection connection, String tableName) throws SQLException {
		Statement statement = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			String query = "DROP TABLE " + tableName;
			DatabaseFunctions.executeUpdateStatement(statement, query);
		} finally {
			DatabaseFunctions.closeStatement(statement);
		}
	}

	public static void clearBigQueryTable(final Connection connection, String tableName) throws SQLException {
		Statement statement = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			String query = "DELETE FROM " + tableName;
			DatabaseFunctions.executeUpdateStatement(statement, query);
		} finally {
			DatabaseFunctions.closeStatement(statement);
		}
	}

	public static SummaryTable viewSchemaBigQueryTable(final Connection connection, String tableName)
			throws SQLException {
		SummaryTable summaryTable = new SummaryTable();
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String> colTypes = null;

		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rs = null;
		try {
			colNames.add("Column Name");
			colNames.add("Column Type");
			summaryTable.setColNames(colNames);

			rs = dbmd.getColumns(null, null, tableName, "%");
			while (rs.next()) {
				colTypes = new ArrayList<String>();

				colTypes.add(rs.getString("COLUMN_NAME"));
				String type = rs.getString("TYPE_NAME");
				if ("VARCHAR".equalsIgnoreCase(type)) {
					int size = rs.getInt("COLUMN_SIZE");
					colTypes.add(type + "(" + size + ")");
				} else
					colTypes.add(type);

				summaryTable.addRow(colTypes);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
		}

		return summaryTable;
	}

	@SuppressWarnings("unchecked")
	public static void getSpreadSheetResults(Connection customTagConnection, String query, PrintWriter writer,
			Connection primaryConnection, String namenodeId, String queryId, String userName) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> columnList = new ArrayList<String>();

		try {
			stmt = DatabaseFunctions.getStatement(customTagConnection);

			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			BigQueryDAO.addUpdateSpreadSheetQueryStatus(primaryConnection, queryId, namenodeId, userName, "STEP3",
					"SUCCESS", "", true);
			JSONObject widthObject = new JSONObject();
			JSONArray columnWidths = new JSONArray();

			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnList.add(rsmd.getColumnName(i));
				columnWidths.add("120px");
			}
			widthObject.put("widths", columnWidths);

			writer.write(",");
			writer.write("\"metadata\": ");
			writer.write(widthObject.toJSONString());

			/*
			 * "metadata": { "widths": [ "120px", //widths for each column,
			 * required "80px" ] }, "rows": [ { // row 1, repeats for each
			 * column of the spreadsheet "height": "18px", //optional "columns":
			 * [ { //column A "class": "css classes", //optional "value":
			 * "value", //optional "style": "css cell style" //optional }, {}
			 * //column B ] }, { // row 2 "height": "18px", //optional
			 * "columns": [ { // column A "class": "css classes", //optional
			 * "value": "value", //optional "style": "css cell style" //optional
			 * }, {} // column B ] } ]
			 */
			JSONObject row = null;

			writer.write(",");
			writer.write("\"rows\": [");

			row = new JSONObject();
			JSONArray columnsArray = new JSONArray();
			JSONObject column = null;
			for (int i = 0; i < columnList.size(); i++) {
				column = new JSONObject();
				column.put("value", columnList.get(i).toUpperCase());
				columnsArray.add(i, column);
			}
			row.put("height", "18px");
			row.put("columns", columnsArray);

			writer.write(row.toJSONString());

			while (rs.next()) {
				writer.write(",");

				row = new JSONObject();
				columnsArray = new JSONArray();
				for (int i = 0; i < columnList.size(); i++) {
					column = new JSONObject();
					String value = String.valueOf(rs.getObject(columnList.get(i)));
					if (columnList.get(i).equalsIgnoreCase(ColumnConstants.COL_TAG_VALUES_BLOCKS)) {
						value = rs.getString(columnList.get(i));
					}
					if (value == "null") {
						value = "";
					}
					column.put("value", value);
					columnsArray.add(i, column);
				}
				row.put("height", "18px");
				row.put("columns", columnsArray);

				writer.write(row.toJSONString());
			}
			writer.write("]");
			BigQueryDAO.addUpdateSpreadSheetQueryStatus(primaryConnection, queryId, namenodeId, userName, "STEP4",
					"SUCCESS", "", true);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	// In case of Hive Queries, customTagConnection is Hive Connection and
	// connection is Custom Tag Connection to get table schema.

	public static void writeQueryResultInJSONFile(Connection customTagConnection, boolean isHive, String query,
			String namenodeId, String queryId, String userName, JSONObject properties) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		Map<Integer, String> columnList = new TreeMap<Integer, String>();
		BufferedWriter writer = null;
		String tablename = null;
		int index = -1;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("writeQueryResultInJSONFile query" + query);
			if (isHive) {
				String startPart = query.substring(0, query.indexOf("FROM"));
				if (startPart.contains(" * ")) {
					StringBuilder sb = new StringBuilder();
					sb.append(query.substring(0, query.indexOf(" * ")));
					sb.append(" ");
					sb.append(QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
					sb.append(",* ");
					sb.append(query.substring(query.indexOf("FROM")));
					query = sb.toString();
				}

				else if (startPart.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH)) {
					String[] cols = (query.substring(query.indexOf("SELECT") + 7, query.indexOf("FROM"))).split(",");
					if (cols != null) {
						for (int i = 0; i < cols.length; i++) {
							if (ColumnConstants.COL_TAG_VALUES_FILEPATH.equalsIgnoreCase(cols[i].trim()))
								index = i;
						}
					}
				}
				tablename = query.substring(query.indexOf("FROM") + 5).split(" ")[0];
				AppLogger.getLogger().debug(" tablename: " + tablename);
				if (query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH)
						|| query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase())) {
					query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH,
							QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
					query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase(),
							QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("isHive: " + isHive + " query: " + query);

			boolean setLimitResultRows = (Boolean) properties.get(BigQueryIdentifiers.SETLIMITRESULTROWS);
			int limitResultRowsValue = Integer
					.parseInt(String.valueOf(properties.get(BigQueryIdentifiers.LIMITRESULTROWSVALUE)));
			File file = new File(EnvironmentalConstants.getReportsDirectory() + File.separator + userName.toLowerCase()
					+ File.separator + namenodeId.toLowerCase() + File.separator + queryId.toLowerCase() + ".json");

			if (!file.exists()) {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			stmt = DatabaseFunctions.getStatement(customTagConnection);

			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);

			ResultSetMetaData rsmd = rs.getMetaData();
			writer.write('{');
			writer.write('"');
			writer.write("columns");
			writer.write('"');
			writer.write(':');
			writer.write('[');
			boolean addComma = false;
			ArrayList<String> DataTypeList = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if ((isHive) && ColumnConstants.COL_TAG_VALUES_FILEPATH.equalsIgnoreCase(rsmd.getColumnName(i)))
					continue;
				if (ColumnConstants.COL_TAG_VALUES_BLOCKS.equalsIgnoreCase(rsmd.getColumnName(i)))
					continue;
				if (addComma)
					writer.write(',');
				if (QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME.equalsIgnoreCase(rsmd.getColumnName(i))) {
					columnList.put(i, ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase());
					DataTypeList.add(rsmd.getColumnTypeName(i));

					writer.write('"');
					writer.write(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase());
					writer.write('"');
					index = i;
				} else {
					String columnNameTrimmed = rsmd.getColumnName(i).toLowerCase();
					columnNameTrimmed = columnNameTrimmed.substring(columnNameTrimmed.indexOf('.') + 1);
					columnList.put(i, columnNameTrimmed); // In Hive-0.13,
															// columnList:
															// {1=filepath,
															// 2=hivecsvtable1.ip,
															// 3=hivecsvtable1.cpu,
															// 4=hivecsvtable1.ram,
															// 5=hivecsvtable1.diskread,
															// 6=hivecsvtable1.diskwrite,
															// 7=hivecsvtable1.netread,
															// 8=hivecsvtable1.netwrite}
					DataTypeList.add(rsmd.getColumnTypeName(i));
					writer.write('"');
					writer.write(columnNameTrimmed);
					writer.write('"');
				}
				if (!addComma)
					addComma = true;
			}
			writer.write(']');
			writer.write(',');
			writer.write('"');
			writer.write("rows");
			writer.write('"');
			writer.write(':');
			writer.write('[');

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("index: " + index);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("columnList: " + columnList);

			int j = 0;
			// Fixes: While searching Query for hive table like 'csv' were
			// getting one extra blank column in result
			boolean removeFirstRowHeader = false;
			if (isHive && !isQueryHasFilters(query)) {
				Connection connection = null;
				connection = CoreDBManager.getQueryIODBConnection();
				AdHocQueryBean bean = AdHocQueryDAO.getAdHocQueryArguments(connection, namenodeId, tablename);
				if (bean != null) {
					String jsonString = bean.getArguments();
					JSONObject arguments = (JSONObject) new JSONParser().parse(jsonString);

					if (arguments != null && arguments.containsKey(QueryIOConstants.ADHOC_PARSER_ISFIRSTROWHEADER)) {
						removeFirstRowHeader = (Boolean) arguments.get(QueryIOConstants.ADHOC_PARSER_ISFIRSTROWHEADER);
					}
				}
			}

			String filePath = "";
			while (rs.next()) {
				if (setLimitResultRows && j == limitResultRowsValue) {
					break;
				}

				if (removeFirstRowHeader) {
					if (j == 0) {
						j++;
						filePath = rs.getString(1);
						continue; // Column names are coming in ResultSet, need
									// to exclude those in result table.
					} else if (!filePath.equals(filePath = rs.getString(1))) {
						j++;
						continue; // Column names are coming in ResultSet, need
									// to exclude those in result table.
					}
				}

				if (j > 0) {
					writer.write(",");
				}
				writer.write("{");
				int k = -1;
				int colIndex = 1;
				addComma = false;
				Iterator<Integer> it = columnList.keySet().iterator();
				while (it.hasNext()) {
					k = it.next();
					String value = null;
					if (isHive) {
						value = String.valueOf(rs.getObject(colIndex));
					} else {
						value = String.valueOf(rs.getObject(k));
					}
					// if
					// (columnList.get(k).equalsIgnoreCase(ColumnConstants.COL_TAG_VALUES_BLOCKS)){
					// value = rs.getString(colIndex);
					// }
					if (value == null || value.equalsIgnoreCase("null") || value.isEmpty() || value.length() == 0) {
						if (DataTypeList.get(colIndex - 1).contains("int")
								|| DataTypeList.get(colIndex - 1).contains("INT"))
							value = "0";
					}
					if (k == index) {
						value = value.substring(value.indexOf("//") + 2);
						value = value.substring(value.indexOf("/"));
					}
					if (addComma)
						writer.write(",");

					writer.write('"');
					writer.write(columnList.get(k));
					writer.write('"');

					writer.write(':');

					writer.write('"');

					if (value != null) {

						value = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n",
								"\\n");
					}

					writer.write(value);
					writer.write('"');

					colIndex++;

					if (!addComma)
						addComma = true;
				}
				// for (int i=0; i<columnList.size(); i++)
				// {
				// if (i>0)
				// writer.write(",");
				//
				// String value =
				// String.valueOf(rs.getObject(columnList.get(i)));
				// if (columnList.get(i).equalsIgnoreCase("BLOCKS")){
				// value = rs.getString(columnList.get(i));
				// }
				// if (i == index)
				// {
				// value = value.substring(value.indexOf("//"));
				// value = value.substring(value.indexOf("/"));
				// }
				// writer.write('"');
				// writer.write(columnList.get(i));
				// writer.write('"');
				//
				// writer.write(':');
				//
				// writer.write('"');
				// writer.write(value);
				// writer.write('"');
				// }
				writer.write('}');
				j++;
			}
			writer.write(']');
			writer.write('}');
			writer.flush();
			AppLogger.getLogger().debug(" " + j + " number of record writing on disk in json file.");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while writing in file ", e);
			throw e;
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			if (writer != null)
				writer.close();
		}
	}

	private static boolean isQueryHasFilters(String query) {
		return query != null && query.indexOf("WHERE") != -1;
	}

	public static void writeJobResultInJSONFile(Connection connection, String namenodeId, String queryId,
			String userName, JSONObject properties, String selectedTable) throws Exception {

		JSONArray columnList = new JSONArray();
		BufferedWriter writer = null;
		DFSInputStream dfsInputStream = null;
		BufferedReader in = null;
		try {

			File file = new File(EnvironmentalConstants.getReportsDirectory() + File.separator + userName
					+ File.separator + namenodeId + File.separator + queryId + ".json");

			if (!file.exists()) {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				file.createNewFile();
			}
			columnList = (JSONArray) properties.get("selectedColumnList");

			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			writer.write('{');
			writer.write('"');
			writer.write("columns");
			writer.write('"');
			writer.write(':');

			writer.write(columnList.toJSONString());

			writer.write(',');
			writer.write('"');
			writer.write("rows");
			writer.write('"');
			writer.write(':');

			writer.write('[');

			String fsDefaultName = RemoteManager.getFsDefaultName(namenodeId);
			String filePath = "/hive/" + selectedTable + "/000000_0";
			Path path = new Path(filePath);
			Configuration conf = ConfigurationManager.getConfiguration(connection, namenodeId);
			FileSystem dfs;
			dfs = FileSystem.get(URI.create(fsDefaultName), conf);
			DistributedFileSystem fs = (DistributedFileSystem) dfs;

			dfsInputStream = (DFSInputStream) fs.getClient().open(path.toUri().getPath());
			in = new BufferedReader(new InputStreamReader(dfsInputStream));
			boolean setLimitResultRows = (Boolean) properties.get(BigQueryIdentifiers.SETLIMITRESULTROWS);
			int limitResultRowsValue = Integer
					.parseInt(String.valueOf(properties.get(BigQueryIdentifiers.LIMITRESULTROWSVALUE)));
			String str;

			int recordCount = 0;
			while ((str = in.readLine()) != null) {

				if (setLimitResultRows && recordCount == limitResultRowsValue) {
					break;
				}
				if (recordCount > 0) {
					writer.write(",");

				}
				String[] ar = str.split(",");
				writer.write("{");

				for (int i = 0; i < ar.length; i++) {
					if (i > 0)
						writer.write(",");
					writer.write('"');
					writer.write((String) columnList.get(i));
					writer.write('"');

					writer.write(':');

					writer.write('"');
					writer.write(ar[i]);
					writer.write('"');
				}
				writer.write('}');
				recordCount++;

			}
			writer.write(']');
			writer.write('}');
			writer.flush();
			dfsInputStream.close();

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while writing csv ", e);
			throw e;
		} finally {

			if (dfsInputStream != null) {
				dfsInputStream.close();
			}
			if (in != null)
				in.close();
			if (writer != null)
				writer.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getSpreadSheetSlickResultsMetadata(Connection customTagConnection, String query,
			String countQuery) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rsCount = null;
		JSONObject resultObject = new JSONObject();
		ArrayList<String> columnList = new ArrayList<String>();
		int total = -1;

		try {
			stmt = DatabaseFunctions.getStatement(customTagConnection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);

			ResultSetMetaData rsmd = rs.getMetaData();

			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnList.add(rsmd.getColumnName(i));
			}

			resultObject.put("columnList", columnList);

			rsCount = DatabaseFunctions.getQueryResultsForStatement(stmt, countQuery);

			if (rsCount.next()) {
				total = rsCount.getInt(1);
			}

			resultObject.put("total", total);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(rsCount);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return resultObject;
	}

	@SuppressWarnings("unchecked")
	public static void getSpreadSheetSlickResults(Connection customTagConnection, String query, int offset,
			PrintWriter writer) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		// ArrayList<String> columnList = new ArrayList<String>();

		try {
			stmt = DatabaseFunctions.getStatement(customTagConnection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);

			// JSONArray data = new JSONArray();
			// JSONObject dataObject = null;
			// for (int i=0; i<count; i++)
			// {
			// dataObject = new JSONObject();
			// dataObject.put("num", (i + 1 + offset));
			// dataObject.put("0", "0 Task " + (i + offset));
			// dataObject.put("1", "1 Task " + (i + offset));
			// dataObject.put("2", "2 Task " + (i + offset));
			// dataObject.put("3", "3 Task " + (i + offset));
			// dataObject.put("4", "4 Task " + (i + offset));
			// data.add(i, dataObject);
			// }
			// writer.write(data.toJSONString());

			ResultSetMetaData rsmd = rs.getMetaData();

			// for (int i=1; i<=rsmd.getColumnCount(); i++)
			// {
			// columnList.add(rsmd.getColumnName(i));
			// }

			int size = rsmd.getColumnCount();

			JSONObject dataObject = null;

			boolean isCommaRequired = false;

			int index = 0;
			while (rs.next()) {
				if (isCommaRequired)
					writer.write(",");
				else
					isCommaRequired = true;

				dataObject = new JSONObject();
				dataObject.put("num", (index + 1 + offset));
				for (int i = 0; i < size; i++) {
					dataObject.put(i, String.valueOf(rs.getObject(i + 1)));
				}

				writer.write(dataObject.toJSONString());
				index++;
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
}