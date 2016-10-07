package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.conf.DataTableParams;

public class AdHocQueryDAO {
	public static void addAdHocQueryInfo(final Connection connection, String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String type, String adHocTableName, String filePathPattern,
			String fields, String encoding, String arguments) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_ADHOC_QUERY);

			ps.setString(1, adHocId);
			ps.setString(2, nameNodeId);
			ps.setString(3, rmId);
			ps.setString(4, sourcePath);
			ps.setBoolean(5, parseRecursive);
			ps.setString(6, type);
			ps.setString(7, adHocTableName);
			ps.setString(8, filePathPattern);
			ps.setString(9, fields);
			ps.setString(10, encoding);
			ps.setString(11, arguments);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static Map<String, Boolean> getAllDataTagTableNames(final Connection connection) throws Exception {
		HashMap<String, Boolean> tableMap = new HashMap<String, Boolean>();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, "SELECT "
					+ ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME + " FROM " + TableConstants.TABLE_ADHOC_QUERY);

			while (rs.next()) {

				tableMap.put(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME), true);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return tableMap;
	}

//	public static ArrayList getAdHocQueryInfoAllOld(final Connection connection) throws Exception {
//		ArrayList list = new ArrayList();
//		Statement statement = null;
//		ResultSet rs = null;
//
//		try {
//			statement = DatabaseFunctions.getStatement(connection);
//			rs = CoreDBManager.getQueryResultsForStatement(statement,
//					"SELECT * FROM  " + TableConstants.TABLE_ADHOC_QUERY);
//
//			while (rs.next()) {
//				AdHocQueryBean bean = new AdHocQueryBean();
//
//				bean.setAdHocId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOCID));
//				bean.setNamenodeId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID));
//				bean.setRmId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_RMID));
//				bean.setSourcePath(rs.getString(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH));
//				bean.setParseRecursive(rs.getBoolean(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE));
//				bean.setType(rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
//				bean.setAdHocTableName(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME));
//				bean.setFilePathPattern(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN));
//				bean.setFields(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FIELDS));
//				bean.setEncoding(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ENCODING));
//				bean.setArguments(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));
//
//				list.add(bean);
//			}
//		} finally {
//			try {
//				DatabaseFunctions.closeSQLObjects(statement, rs);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal("Database Objects could not be closed,Exception: " + e.getMessage(), e);
//			}
//		}
//
//		return list;
//	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAdHocQueryInfoAll(final Connection connection) throws Exception {
		// ArrayList list = new ArrayList();
		JSONObject queryInfo = new JSONObject();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement,
					"SELECT * FROM " + TableConstants.TABLE_ADHOC_QUERY);

			while (rs.next()) {
				// AdHocQueryBean bean = new AdHocQueryBean()
				JSONObject obj = new JSONObject();

				obj.put(ColumnConstants.COL_ADHOC_QUERY_ADHOCID, rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOCID));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_RMID, rs.getString(ColumnConstants.COL_ADHOC_QUERY_RMID));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE,
						rs.getBoolean(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_TYPE, rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_FIELDS, rs.getString(ColumnConstants.COL_ADHOC_QUERY_FIELDS));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_ENCODING,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_ENCODING));
				obj.put(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS,
						rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));

				queryInfo.put("ID", obj);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return queryInfo;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAdHocQueryInfoAll(final Connection connection, String paramsDT) throws Exception {

		String[] dbColumnLabels = { "ADHOCQUERY.ADHOCID", "ADHOCQUERY.NAMENODEID", "ADHOCQUERY.RMID",
				"ADHOCQUERY.SOURCEPATH", "ADHOCQUERY.PARSERECURSIVE", "ADHOCQUERY.TYPE", "ADHOCQUERY.ADHOCTABLENAME",
				"ADHOCQUERY.FILEPATHPATTERN", "ADHOCQUERY.ENCODING", "ADHOCQUERY.ARGUMENTS" };
		DataTableParams params = null;
		JSONObject adhocQueryInfo = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			params = new DataTableParams(paramsDT, dbColumnLabels.length);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Parsed DataTable params - " + params.toString());
			String query = "SELECT * FROM " + TableConstants.TABLE_ADHOC_QUERY;
            String originalQuery = query;
			if (!params.getSearchVal().isEmpty()) {
				query += " WHERE ( ";
				if (params.getSearchColIndex() > -1) {
					query += dbColumnLabels[params.getSearchColIndex()];
					query += " LIKE '%" + params.getSearchVal() + "%' ";
				} else {
					for (int i = 0; i < dbColumnLabels.length; i++){
						query += dbColumnLabels[i];
						query += " LIKE '%" + params.getSearchVal() + "%' ";
						if(i < dbColumnLabels.length - 1){
							query += " OR ";
						}
					}
				}
				query += " )";
				AppLogger.getLogger().fatal("query : " + query);
			}
			
			// get filtered record count
			 int filterCount = getAdhocQueryCount(connection, "SELECT COUNT(*) FROM (" + query + ") AS COUNTER"); //filtered query count
			 if (AppLogger.getLogger().isDebugEnabled())
			 AppLogger.getLogger().debug("ADHOC recordsFiltered: " +
			 filterCount);

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
				AppLogger.getLogger().debug("QueryConstants.GETAHDOC_QUERYINFO - " + query);
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			// ps.setString(1, namenodeId);
			// ps.setString(2, user);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			JSONArray tableAll = new JSONArray();
			while (rs.next()) {
				JSONArray tableRow = new JSONArray();
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOCID));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_RMID));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FIELDS));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ENCODING));
				tableRow.add(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));
				tableAll.add(tableRow);
			}

			int totalCount = getAdhocQueryCount(connection, "SELECT COUNT(*) FROM (" + originalQuery + ") AS COUNTER"); // total
																												// query
																												// count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("recordsTotal: " + totalCount);
			adhocQueryInfo.put("data", tableAll);
			adhocQueryInfo.put("draw", params.getDraw());
			adhocQueryInfo.put("recordsTotal", totalCount);
			 adhocQueryInfo.put("recordsFiltered", filterCount);
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

		return adhocQueryInfo;
	}

	public static int getAdhocQueryCount(Connection connection, String query) throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			stmt = connection.prepareStatement(query);
			// stmt.setString(1, namenodeId);
			// stmt.setString(2, user);
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

	public static AdHocQueryBean getAdHocQueryArguments(final Connection connection, String namenode, String tableName)
			throws Exception {
		Statement statement = null;
		ResultSet rs = null;
		AdHocQueryBean bean = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			String query = "SELECT " + ColumnConstants.COL_ADHOC_QUERY_TYPE + ","
					+ ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS + "," + ColumnConstants.COL_ADHOC_QUERY_TYPE + " FROM "
					+ TableConstants.TABLE_ADHOC_QUERY + " WHERE " + ColumnConstants.COL_ADHOC_QUERY_NAMENODEID + "='"
					+ namenode + "' AND " + ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME + "='" + tableName + "'";
			AppLogger.getLogger().debug("AdHocQuery: " + query);
			rs = CoreDBManager.getQueryResultsForStatement(statement, query);

			if (rs.next()) {
				bean = new AdHocQueryBean();
				bean.setType(rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
				bean.setArguments(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return bean;
	}

	public static AdHocQueryBean getAdHocQueryInfo(final Connection connection, String adHocId) throws Exception {
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			String query = "SELECT * FROM " + TableConstants.TABLE_ADHOC_QUERY + " WHERE "
					+ ColumnConstants.COL_ADHOC_QUERY_ADHOCID + "='" + adHocId + "'";
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("query: " + query);

			rs = CoreDBManager.getQueryResultsForStatement(statement, query);

			if (rs.next()) {
				AdHocQueryBean bean = new AdHocQueryBean();

				bean.setAdHocId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOCID));
				bean.setNamenodeId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID));
				bean.setRmId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_RMID));
				bean.setSourcePath(rs.getString(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH));
				bean.setParseRecursive(rs.getBoolean(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE));
				bean.setType(rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
				bean.setAdHocTableName(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME));
				bean.setFilePathPattern(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN));
				bean.setFields(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FIELDS));
				bean.setEncoding(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ENCODING));
				bean.setArguments(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));

				return bean;
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return null;
	}

	public static AdHocQueryBean getAdHocInfoFromTable(final Connection connection, String nameNodeId, String tableName)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String query = "SELECT * FROM " + TableConstants.TABLE_ADHOC_QUERY + " WHERE "
					+ ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME + "=?" + " AND "
					+ ColumnConstants.COL_ADHOC_QUERY_NAMENODEID + "=?";

			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			// pst.setString(1, tableName.toUpperCase());
			pst.setString(1, tableName.toLowerCase());
			pst.setString(2, nameNodeId);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(pst);
			if (rs.next()) {
				AdHocQueryBean bean = new AdHocQueryBean();

				bean.setAdHocId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOCID));
				bean.setNamenodeId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_NAMENODEID));
				bean.setRmId(rs.getString(ColumnConstants.COL_ADHOC_QUERY_RMID));
				bean.setSourcePath(rs.getString(ColumnConstants.COL_ADHOC_QUERY_SOURCEPATH));
				bean.setParseRecursive(rs.getBoolean(ColumnConstants.COL_ADHOC_QUERY_PARSE_RECURSIVE));
				bean.setType(rs.getString(ColumnConstants.COL_ADHOC_QUERY_TYPE));
				bean.setAdHocTableName(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ADHOC_TABLE_NAME));
				bean.setFilePathPattern(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FILE_PATH_PATTERN));
				bean.setFields(rs.getString(ColumnConstants.COL_ADHOC_QUERY_FIELDS));
				bean.setEncoding(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ENCODING));
				bean.setArguments(rs.getString(ColumnConstants.COL_ADHOC_QUERY_ARGUMENTS));

				return bean;
			}

		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return null;
	}

	public static void updateAdHocQueryInfo(final Connection connection, String adHocId, String rmId,
			String filePathPattern, String encoding) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_ADHOC_QUERY);

			ps.setString(1, rmId);
			ps.setString(2, filePathPattern);
			ps.setString(3, encoding);
			ps.setString(4, adHocId);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteAdHocQueryInfo(final Connection connection, String adHocId) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_ADHOC_QUERY);

			ps.setString(1, adHocId);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteAdHocQueryInfoFromList(final Connection connection, String adHocIds) throws Exception {
		Statement stmt = null;
		String deleteQuery = null;

		try {
			stmt = DatabaseFunctions.getStatement(connection);
			deleteQuery = "DELETE FROM " + TableConstants.TABLE_ADHOC_QUERY + " WHERE "
					+ ColumnConstants.COL_ADHOC_QUERY_ADHOCID + " IN (" + adHocIds + ")";

			CoreDBManager.executeUpdateStatement(connection, stmt, deleteQuery);
		} finally {
			DatabaseFunctions.closeStatement(stmt);
		}
	}

	@SuppressWarnings("unchecked")
	public static String getUpdatedArguments(Connection connection, String adHocId, String whereArgs,
			String resultTableName) // TODO add encoding
					throws Exception {
		StringBuilder args = new StringBuilder();
		AdHocQueryBean adHoc = getAdHocQueryInfo(connection, adHocId);

		if (adHoc != null) {
			args.append(adHocId);
			args.append(" ");
			if (QueryIOConstants.ADHOC_TYPE_CSV.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_CSV);
			else if (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_LOG);
			else if (QueryIOConstants.ADHOC_TYPE_IISLOG.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_IISLOG);
			else if (QueryIOConstants.ADHOC_TYPE_JSON.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_JSON);
			else if (QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_KVPAIRS);
			else if (QueryIOConstants.ADHOC_TYPE_MBOX.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_MBOX);
			else if (QueryIOConstants.ADHOC_TYPE_REGEX.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_REGEX);
			else // if
					// (QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(adHoc.getType()))
				args.append(QueryIOConstants.DEFAULT_ADHOC_JOB_CLASS_NAME_XML);

			args.append(" ");
			args.append(adHoc.getSourcePath());
			args.append(" ");
			args.append(adHoc.getFilePathPattern());
			args.append(" ");
			if (resultTableName != null)
				args.append(resultTableName);
			else
				args.append(adHoc.getAdHocTableName());
			args.append(" '");
			// args.append(adHoc.getArguments());
			// args.append(" ");
			// args.append(adHoc.getFields());

			JSONObject finalArguments = new JSONObject();
			finalArguments.put(QueryIOConstants.ADHOC_CONSTANT_ARGUMENTS, adHoc.getArguments());
			finalArguments.put(QueryIOConstants.ADHOC_CONSTANT_FIELDS, adHoc.getFields());

			args.append(finalArguments.toJSONString());
			args.append("' ");

			if (whereArgs.length() > 0)
				args.append("'[").append(whereArgs).append("]'");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("updated arguments: " + args.toString());
		}

		return args.toString();
	}
}