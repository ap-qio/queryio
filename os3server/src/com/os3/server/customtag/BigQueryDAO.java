package com.os3.server.customtag;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.HadoopConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;

public class BigQueryDAO {
	protected static final Logger logger = Logger.getLogger(BigQueryDAO.class);
	
	public static void deleteBigQuery(final Connection connection, String id, String user) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt =  DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_BIG_QUERY);
			stmt.setString(1, id);
			stmt.setString(2, HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			stmt.setString(3, user);
			
			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
		}
	}
	
	public static void saveBigQuery(final Connection connection, String id, String description, JSONObject properties, String dbName, String user) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt=  DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_BIG_QUERY);
			stmt.setString(1, id);
			stmt.setString(2, properties.toJSONString());
			stmt.setString(3, description);
			stmt.setString(4, HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			stmt.setString(5, dbName);
			stmt.setString(6, user);
			
			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
		}
	}
	
	public static JSONObject getAllBigQueries(final Connection connection)
			throws Exception {
		JSONArray jsonArray = new JSONArray();
		PreparedStatement stmt = null;
		InputStream is = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_BIGQUERIES);
			stmt.setString(1, HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);

			JSONObject object = null;
			while (rs.next()) {
				try {
					String queryId = rs.getString(ColumnConstants.COL_BIGQUERIES_ID);
					String queryDesc = rs.getString(ColumnConstants.COL_BIGQUERIES_DESCRIPTION);
					
					object = new JSONObject();
					object.put("id", queryId);
					object.put("description", queryDesc);

					jsonArray.add(object);
				} finally {
					try {
						if(is!=null)	is.close();
					} catch (Exception e) {
						logger.fatal(e.getMessage(), e);
					}
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
		}
		JSONObject queryList = new JSONObject();
		queryList.put("queries", jsonArray);
		
		return queryList;
	}
	
	public static JSONObject getBigQuery(final Connection connection, String searchQueryId, String user)
			throws Exception {
		JSONArray jsonArray = new JSONArray();
		PreparedStatement stmt = null;
		InputStream is = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_BIGQUERIES);
			stmt.setString(1, searchQueryId);
			stmt.setString(2, HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			stmt.setString(3, user);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);

			JSONObject object = null;
			while (rs.next()) {
				JSONObject properties = null;
				try {
					String queryId = rs.getString(ColumnConstants.COL_BIGQUERIES_ID);
					String queryDesc = rs.getString(ColumnConstants.COL_BIGQUERIES_DESCRIPTION);
					String jsonString = rs.getString(ColumnConstants.COL_BIGQUERIES_PROPERTIES);
					
					JSONParser parser = new JSONParser();
					properties = (JSONObject)parser.parse(jsonString);
					
					object = new JSONObject();
					object.put("id", queryId);
					object.put("description", queryDesc);
					object.put("properties", properties);
					
					jsonArray.add(properties);
				} finally {
					try {
						if(is!=null)	is.close();
					} catch (Exception e) {
						logger.fatal(e.getMessage(), e);
					}
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				logger.fatal(e.getMessage(), e);
			}
		}
		JSONObject queryList = new JSONObject();
		queryList.put("queries", jsonArray);
		
		return queryList;
	}
}
