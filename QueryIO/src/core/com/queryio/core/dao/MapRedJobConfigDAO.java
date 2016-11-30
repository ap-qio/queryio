package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.conf.DataTableParams;

public class MapRedJobConfigDAO {
	public static void insert(Connection connection, MapRedJobConfig config) throws SQLException {
		String query = "INSERT INTO " + TableConstants.TABLE_MAPREDJOBCONFIG + " ("
				+ ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID + "," + ColumnConstants.COL_MAPREDJOBCONFIG_RMID + ","
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + "," + ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE + ","
				+ ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS + "," + ColumnConstants.COL_MAPREDJOBCONFIG_FILES + ","
				+ ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME + "," + ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS
				+ ")" + " VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			int i = 1;
			pst.setString(i++, config.getNamenodeId());
			pst.setString(i++, config.getRmId());
			pst.setString(i++, config.getJobName());
			pst.setString(i++, config.getJarName());
			pst.setString(i++, config.getLibJars());
			pst.setString(i++, config.getFiles());
			pst.setString(i++, config.getClassName());
			pst.setString(i++, config.getArguments().trim());
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static MapRedJobConfig get(Connection connection, String jobName) throws SQLException {
		String query = "SELECT * FROM " + TableConstants.TABLE_MAPREDJOBCONFIG + " WHERE "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
		MapRedJobConfig config = null;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug(query);
			st = DatabaseFunctions.getPreparedStatement(connection, query);
			st.setString(1, jobName);
			rs = st.executeQuery();
			if (rs.next()) {
				config = new MapRedJobConfig(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_RMID),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_FILES),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME),
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS), true, false, null);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return config;
	}

	public static ArrayList getAllJobNames(Connection connection) throws SQLException {
		ArrayList list = new ArrayList();

		String query = "SELECT " + ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " FROM "
				+ TableConstants.TABLE_MAPREDJOBCONFIG;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAllStandardMRJobs(Connection connection) throws SQLException {
		JSONObject mrJobsList = new JSONObject();

		String query = "SELECT * FROM " + TableConstants.TABLE_MAPREDJOBCONFIG;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			rs = st.executeQuery(query);
			while (rs.next()) {

				JSONObject obj = new JSONObject();
				// String jobName =
				// rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME);
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME));
				// By default Recursive was true, and input path filter was
				// false.
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_RMID,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_RMID));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_FILES,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_FILES));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME));
				obj.put(ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS,
						rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS));

				// true, false, null);
				mrJobsList.put("JobDetails", obj);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return mrJobsList;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAllStandardMRJobs(Connection connection, String paramsDT) throws Exception {

		String[] dbColumnLabels = { "MAPREDJOBCONFIG.NAMENODEID", "MAPREDJOBCONFIG.RMID", "MAPREDJOBCONFIG.JOBNAME",
				"MAPREDJOBCONFIG.JARFILE", "MAPREDJOBCONFIG.LIBJARS", "MAPREDJOBCONFIG.FILES",
				"MAPREDJOBCONFIG.CLASSNAME", "MAPREDJOBCONFIG.ARGUMENTS" };
		DataTableParams params = null;
		JSONObject jobQueryInfo = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			params = new DataTableParams(paramsDT, dbColumnLabels.length);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Parsed DataTable params - " + params.toString());
			String query = "SELECT * FROM " + TableConstants.TABLE_MAPREDJOBCONFIG;

			if (!params.getSearchVal().isEmpty()) {
				query += " WHERE ( ";
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
				AppLogger.getLogger().debug("JObManager query : " + query);
			}

			// get filtered record count
			int filterCount = getJobQueryCount(connection, "SELECT COUNT(*) FROM (" + query + ") AS COUNTER"); // filtered
																												// query
																												// count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ADHOC recordsFiltered: " + filterCount);

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
				AppLogger.getLogger().debug("QueryConstants.MAPREDJOBCONFIG - " + query);
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			JSONArray tableAll = new JSONArray();
			while (rs.next()) {
				JSONArray tableRow = new JSONArray();

				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_RMID));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_FILES));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME));
				tableRow.add(rs.getString(ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS));
				tableAll.add(tableRow);
			}

			int totalCount = getJobQueryCount(connection, "SELECT COUNT(*) FROM (" + query + ") AS COUNTER"); // total
																												// query
																												// count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("recordsTotal: " + totalCount);
			jobQueryInfo.put("data", tableAll);
			jobQueryInfo.put("draw", params.getDraw());
			jobQueryInfo.put("recordsTotal", totalCount);
			jobQueryInfo.put("recordsFiltered", filterCount);
		}

		finally {
			DatabaseFunctions.closeSQLObjects(ps, rs);
		}
		return jobQueryInfo;
	}

	public static int getJobQueryCount(Connection connection, String query) throws SQLException {
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

	public static void update(Connection connection, MapRedJobConfig config) throws SQLException {
		String query = "UPDATE " + TableConstants.TABLE_MAPREDJOBCONFIG + " SET "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JARFILE + " = ?, " + ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME
				+ " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS + " = ? WHERE "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, config.getJarName());
			pst.setString(2, config.getClassName());
			pst.setString(3, config.getArguments());
			pst.setString(4, config.getJobName());
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void update(Connection connection, String jobName, String newJobName, String mainClass,
			String arguments, String nnId, String rmId) throws SQLException {
		String query = "UPDATE " + TableConstants.TABLE_MAPREDJOBCONFIG + " SET "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?, " + ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME
				+ " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS + " = ?,"
				+ ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID + " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_RMID
				+ " = ? WHERE " + ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, newJobName);
			pst.setString(2, mainClass);
			pst.setString(3, arguments);
			pst.setString(4, nnId);
			pst.setString(5, rmId);
			pst.setString(6, jobName);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void updateJobArguments(Connection connection, String jobName, String arguments) throws SQLException {
		String query = "UPDATE " + TableConstants.TABLE_MAPREDJOBCONFIG + " SET "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS + " = ? WHERE "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, arguments);
			pst.setString(2, jobName);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void updateJob(Connection connection, MapRedJobConfig config) throws SQLException {
		String query = "UPDATE " + TableConstants.TABLE_MAPREDJOBCONFIG + " SET "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_CLASSNAME + " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_LIBJARS
				+ " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_FILES + " = ?,"
				+ ColumnConstants.COL_MAPREDJOBCONFIG_NAMENODEID + " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_RMID
				+ " = ?," + ColumnConstants.COL_MAPREDJOBCONFIG_ARGUMENTS + " = ? WHERE "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, config.getClassName());
			pst.setString(2, config.getLibJars());
			pst.setString(3, config.getFiles());
			pst.setString(4, config.getNamenodeId());
			pst.setString(5, config.getRmId());
			pst.setString(6, config.getArguments());
			pst.setString(7, config.getJobName());
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static String getResultTableName(String arguments) throws Exception {
		String resultTableName = null;

		if (arguments != null) {
			resultTableName = arguments.substring(arguments.lastIndexOf("]") + 2);
			if (resultTableName != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("getResultTableName: resultTableName: " + resultTableName);
				resultTableName = resultTableName.trim();
			}
		}

		return resultTableName;
	}

	public static void delete(Connection connection, String jobName, boolean isDeleteMapping) throws SQLException {
		String query = "DELETE FROM " + TableConstants.TABLE_MAPREDJOBCONFIG + " WHERE "
				+ ColumnConstants.COL_MAPREDJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		String query2 = "DELETE FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
				+ ColumnConstants.COL_JOB_MAPPING_JOBNAME + " = ?";
		PreparedStatement pst2 = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, jobName);
			pst.execute();
			if (isDeleteMapping) {
				pst2 = DatabaseFunctions.getPreparedStatement(connection, query2);
				pst2.setString(1, jobName);
				pst2.execute();
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closePreparedStatement(pst2);
		}
	}
}
