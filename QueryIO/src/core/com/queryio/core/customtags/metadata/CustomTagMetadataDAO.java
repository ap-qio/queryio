package com.queryio.core.customtags.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.DataTableParams;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class CustomTagMetadataDAO {

	public static boolean isTagExist(Connection connection, String id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_IS_TAG_EXIST);
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

	public static ArrayList<String> getAllCustomTagMetadaIds(Connection connection) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		String query = "SELECT " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + " FROM "
				+ TableConstants.TABLE_CUSTOM_TAG_METADATA;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			while (rs.next()) {
				list.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return list;
	}

	public static ArrayList<String> getAllCustomTagMetadataIds(Connection connection, boolean isActive)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		String query = "SELECT " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + " FROM "
				+ TableConstants.TABLE_CUSTOM_TAG_METADATA + " WHERE "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + "=?";
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			ps.setBoolean(1, isActive);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				list.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
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
		return list;
	}

	public static ArrayList<String> getAllCustomTagMetadataIds(Connection connection, String fileType, boolean isActive)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		String query = "SELECT " + ColumnConstants.COL_CUSTOMTAG_METADATA_ID + " FROM "
				+ TableConstants.TABLE_CUSTOM_TAG_METADATA + " WHERE "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + "=? AND "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_FILE_TYPE + "=?";
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			ps.setBoolean(1, isActive);
			ps.setString(2, fileType);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				list.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
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
		return list;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAllCustomTagsMetadataDetail(Connection connection,String paramsDT) throws Exception {
//		AppLogger.getLogger().debug("ID : " + tagID);
		
		String[] dbColumnLabels = { "CUSTOMTAG_METADATA.ID", "CUSTOMTAG_METADATA.JSON", "CUSTOMTAG_METADATA.DESCRIPTION",
				"CUSTOMTAG_METADATA.ISACTIVE", "CUSTOMTAG_METADATA.DB_TYPE", "CUSTOMTAG_METADATA.FILE_TYPE", "CUSTOMTAG_METADATA.NAMENODEID",
				"CUSTOMTAG_METADATA.TABLE_NAME", "CUSTOMTAG_METADATA.SCHEDULE_INFO", "CUSTOMTAG_METADATA.JOB_NAMES" };
		DataTableParams params = null;
		JSONObject customTagInfo = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			params = new DataTableParams(paramsDT, dbColumnLabels.length);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Parsed DataTable params - " + params.toString());
			String query = QueryConstants.PREPARED_QRY_ALL_CUSTOMTAG_METADATA_GET_DETAILS;
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
			 int filterCount = getCustomTagCount(connection, "SELECT COUNT(*) FROM (" + query + ") AS COUNTER"); //filtered query count
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
				AppLogger.getLogger().debug("QueryConstants.GETAHDOC_QUERYINFO - " + query);
			ps = DatabaseFunctions.getPreparedStatement(connection, query);
			// ps.setString(1, namenodeId);
			// ps.setString(2, user);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			JSONArray tableAll = new JSONArray();
			while (rs.next()) {
				JSONArray tableRow = new JSONArray();
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JSON));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DESC));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_FILE_TYPE));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_NAMENODE_ID));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO));
				tableRow.add(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JOB_NAMES));
				tableAll.add(tableRow);
			}

			int totalCount = getCustomTagCount(connection, "SELECT COUNT(*) FROM (" + originalQuery + ") AS COUNTER"); // total query  count
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("recordsTotal: " + totalCount);
			customTagInfo.put("data", tableAll);
			customTagInfo.put("draw", params.getDraw());
			customTagInfo.put("recordsTotal", totalCount);
			customTagInfo.put("recordsFiltered", filterCount);
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

		return customTagInfo;
	}

	public static int getCustomTagCount(Connection connection, String query) throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(query);
//			stmt.setString(1, tagID);
			rs = stmt.executeQuery();
			if(rs.next()){
				count = rs.getInt(1);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getAllCustomTagsMetadataDetail(Connection connection)
			throws Exception {
		JSONObject customDataObj = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;
//		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_ALL_CUSTOMTAG_METADATA_GET_DETAILS);
//			ps.setString(1, tagID);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
				obj.put("json", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JSON));
				obj.put("desc", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DESC));
				obj.put("isActive", String.valueOf(rs.getBoolean(ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE)));
				obj.put("db_type", String.valueOf(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE)));
				obj.put("tableName", String.valueOf(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME)));
				obj.put("dataTaggingTimeInfo",
						String.valueOf(rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO)));
				customDataObj.put("TagDetails" ,obj);
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
		return customDataObj;
	}

//	public static ArrayList<Map<String, String>> getAllCustomTagsMetadataDetail(Connection connection)
//			throws Exception {
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
//
//		try {
//			ps = DatabaseFunctions.getPreparedStatement(connection,
//					QueryConstants.PREPARED_QRY_ALL_CUSTOMTAG_METADATA_GET_DETAILS);
//			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
//			Map<String, String> map = null;
//			while (rs.next()) {
//				map = new HashMap<String, String>();
//				map.put("id", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_ID));
//				map.put("json", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JSON));
//				map.put("desc", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DESC));
//				map.put("isActive", String.valueOf(rs.getBoolean(ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE)));
//				map.put("db_type", String.valueOf(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE)));
//				map.put("tableName", String.valueOf(rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME)));
//				map.put("dataTaggingTimeInfo",
//						String.valueOf(rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO)));
//				data.add(map);
//			}
//		} finally {
//			try {
//				DatabaseFunctions.closeResultSet(rs);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal(e.getMessage(), e);
//			}
//			try {
//				DatabaseFunctions.closePreparedStatement(ps);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal(e.getMessage(), e);
//			}
//		}
//		return data;
//	}
	
	
	public static Map<String, Object> getCustomTagMetaataDetailById(Connection connection, String id) throws Exception {
		AppLogger.getLogger().debug("ID : " + id);
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_GET_DETAILS);
			ps.setString(1, id);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				data.put("json", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JSON));
				data.put("desc", rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_DESC));
				data.put("isActive", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE));
				data.put("dbType", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_DB_TYPE));
				data.put("fileType", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_FILE_TYPE));
				data.put("nameNodeId", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_NAMENODE_ID));
				data.put("tableName", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_TABLE_NAME));
				data.put("dataTaggingTimeInfo",
						rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_TAGGING_SCHEDULE_INFO));
				data.put("jobName", rs.getObject(ColumnConstants.COL_CUSTOMTAG_METADATA_JOB_NAMES));
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
		return data;
	}
	public static void insertCustomTagMetadatData(Connection connection, String id, String metadata, String desc,
			boolean isActive, String dbType, String fileType, String nameNodeId, String tableName,
			String dataTaggingTimeInfo, String jobNames) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_INSERT);
			ps.setString(1, id);
			ps.setObject(2, metadata);
			ps.setObject(3, desc);
			ps.setObject(4, isActive);
			ps.setObject(5, dbType);
			ps.setObject(6, fileType);
			ps.setObject(7, nameNodeId);
			ps.setObject(8, tableName);
			ps.setObject(9, dataTaggingTimeInfo);
			ps.setObject(10, jobNames);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateCustomTagMetadatData(Connection connection, String id, String metadata, String desc,
			boolean isActive, String dataTaggingTimeInfo) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_UPDATE);
			ps.setObject(1, metadata);
			ps.setObject(2, desc);
			ps.setObject(3, isActive);
			ps.setObject(4, dataTaggingTimeInfo);
			ps.setString(5, id);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateCustomTagMetadatDataIsColumnValue(Connection connection, String id, boolean isActive)
			throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_ISCOLUMN_UPDATE);
			ps.setObject(1, isActive);
			ps.setString(2, id);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static int activateCustomTagMetadatData(Connection connection, ArrayList ids, boolean isActivate)
			throws Exception {
		Statement statement = null;
		StringBuilder commaSeparatedIds = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i == ids.size() - 1)
				commaSeparatedIds.append("'" + ids.get(i) + "'");
			else
				commaSeparatedIds.append("'" + ids.get(i) + "'" + ",");
		}
		String query = "UPDATE " + TableConstants.TABLE_CUSTOM_TAG_METADATA + " SET "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_IS_ACTIVE + "=" + isActivate + " WHERE "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_ID + " IN (" + commaSeparatedIds.toString() + ")";
		try {
			statement = DatabaseFunctions.getStatement(connection);
			return statement.executeUpdate(query);
		} finally {
			try {
				DatabaseFunctions.closeStatement(statement);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteCustomTagMetadatData(Connection connection, ArrayList<String> ids) throws Exception {
		Statement stmt = null;
		StringBuilder commaSeparatedIds = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i == ids.size() - 1) {
				commaSeparatedIds.append("'" + ids.get(i) + "'");
			} else {
				commaSeparatedIds.append("'" + ids.get(i) + "',");
			}
		}
		String query = "DELETE FROM " + TableConstants.TABLE_CUSTOM_TAG_METADATA + " WHERE "
				+ ColumnConstants.COL_CUSTOMTAG_METADATA_ID + " IN(" + commaSeparatedIds.toString() + ")";
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.executeUpdate(query);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteColumn(Connection connection, String tableName, String columnNameJSON) throws Exception {
		Statement stmt = null;
		try {

			Map<String, Boolean> cols = UserDefinedTagDAO.getAllColumns(connection, tableName);
			
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(columnNameJSON);
			JSONArray tags = (JSONArray) jsonObj.get("Tags");
			for (Object obj : tags) {
				JSONObject tag = (JSONObject) obj;
				String columnName = (String) tag.get("TagName");
				String query = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName.toUpperCase();
				if (cols.get(columnName.toUpperCase()) != null) {
					stmt = connection.createStatement();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Drop column query : " + query);
					stmt.execute(query);
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {

			}
		}
	}

	public static ArrayList<String> getAllTableNames(Connection connection) throws Exception {
		ResultSet rs = null;
		ArrayList<String> metadataTables = new ArrayList<String>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			rs = meta.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				AppLogger.getLogger().debug("tableName fetched : " + rs.getString("TABLE_NAME"));
				if (tableName.toUpperCase().startsWith("DATATAGS_")) {
					metadataTables.add(tableName);
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return metadataTables;
	}

//	// Added for fetching JobNames from CUSTOMTAG_METADATA table
//	public static String selectJobNamesForDataTag(Connection connection, String tagID) throws Exception {
//		ResultSet rs = null;
//		PreparedStatement ps = null;
//		String jobNames = null;
//		try {
//			ps = DatabaseFunctions.getPreparedStatement(connection,
//					QueryConstants.PREPARED_QRY_CUSTOMTAG_METADATA_GET_JOBDETAILS);
//			ps.setString(1, tagID);
//			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
//			jobNames= rs.getString(ColumnConstants.COL_CUSTOMTAG_METADATA_JOB_NAMES);
//		} finally {
//			try {
//				DatabaseFunctions.closeResultSet(rs);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal(e.getMessage(), e);
//			}
//			try {
//				DatabaseFunctions.closePreparedStatement(ps);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal(e.getMessage(), e);
//			}
//		}
//		return jobNames;
//	}
}
