package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;

public class QueryExecutionDAO {

	public static int insertIntoPostgres(Connection connection, String queryId, String applicationId, String status, String path, String namenodeId, String userName) throws SQLException{
		String query = "INSERT INTO " + TableConstants.TABLE_QUERYEXECUTION  + "("
				+ ColumnConstants.COL_QUERYEXECUTION_QUERY_ID+","+ColumnConstants.COL_QUERYEXECUTION_APP_ID+ ","
				+ ColumnConstants.COL_QUERYEXECUTION_STATUS+","+ColumnConstants.COL_QUERYEXECUTION_PATH +","
				+ ColumnConstants.COL_QUERYEXECUTION_NAMENODEID + ","+ColumnConstants.COL_QUERYEXECUTION_USERNAME+") VALUES (?,?,?,?,?,?) RETURNING "+ColumnConstants.COL_QUERYEXECUTION_ID;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, queryId);
			pst.setString(2, applicationId);
			pst.setString(3, status);
			pst.setString(4, path);
			pst.setString(5, namenodeId);
			pst.setString(6, userName);
			rs = pst.executeQuery();
//			rs = pst.getGeneratedKeys();
			if(rs.next()){
				int keyGenerated = rs.getInt(1);
				AppLogger.getLogger().debug("keygenerated : " + keyGenerated);
				return keyGenerated;
			}
			else
			{
				AppLogger.getLogger().debug("ResultSet not found: ");
			}
			AppLogger.getLogger().debug("AppicationId : " + applicationId);
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return -1;
	}
	
	public static int insertIntoHSQL(Connection connection, String queryId, String applicationId, String status, String path, String namenodeId, String userName) throws SQLException{
		String query = "INSERT INTO " + TableConstants.TABLE_QUERYEXECUTION  + "("
				+ ColumnConstants.COL_QUERYEXECUTION_QUERY_ID+","+ColumnConstants.COL_QUERYEXECUTION_APP_ID+ ","
				+ ColumnConstants.COL_QUERYEXECUTION_STATUS+","+ColumnConstants.COL_QUERYEXECUTION_PATH +","
				+ ColumnConstants.COL_QUERYEXECUTION_NAMENODEID + ","+ColumnConstants.COL_QUERYEXECUTION_USERNAME+") VALUES (?,?,?,?,?,?)";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = DatabaseFunctions.getPreparedStatementWithReturnKeys(connection, query);
			pst.setString(1, queryId);
			pst.setString(2, applicationId);
			pst.setString(3, status);
			pst.setString(4, path);
			pst.setString(5, namenodeId);
			pst.setString(6, userName);
			pst.execute();
			rs = pst.getGeneratedKeys();
			if(rs.next()){
				int keyGenerated = rs.getInt(1);
				AppLogger.getLogger().debug("keygenerated : " + keyGenerated);
				return keyGenerated;
			}
			else
			{
				AppLogger.getLogger().debug("ResultSet not found: ");
			}
			AppLogger.getLogger().debug("AppicationId : " + applicationId);
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return -1;
	}
	
	public static int insert(Connection connection, String queryId, String applicationId, String status, String path, String namenodeId, String userName) throws SQLException {
		if (EnvironmentalConstants.getQueryIODatabaseType().equals(DatabaseConstants.DB_HSQL)) {
			return insertIntoHSQL(connection, queryId, applicationId, status, path, namenodeId, userName);
		}
		return insertIntoPostgres(connection, queryId, applicationId, status, path, namenodeId, userName);
	}
	
	public static String getApplicationId(Connection connection, int executionId) throws SQLException{
		String query = "SELECT " + ColumnConstants.COL_QUERYEXECUTION_APP_ID+ " FROM " + TableConstants.TABLE_QUERYEXECUTION  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				return rs.getString(ColumnConstants.COL_QUERYEXECUTION_APP_ID);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return null;
	}
	
	public static String[] getQueryId(Connection connection, int executionId) throws SQLException
	{
		String[] ids = null;
		String query = "SELECT " + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + "," + ColumnConstants.COL_QUERYEXECUTION_NAMENODEID
				+ " FROM " + TableConstants.TABLE_QUERYEXECUTION + " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next())
			{
				ids = new String[2];
				ids[0] = rs.getString(ColumnConstants.COL_QUERYEXECUTION_QUERY_ID);
				ids[1] = rs.getString(ColumnConstants.COL_QUERYEXECUTION_NAMENODEID);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return ids;
	}
	
	public static String getStatus(Connection connection, int executionId) throws SQLException{
		String query = "SELECT " + ColumnConstants.COL_QUERYEXECUTION_STATUS+ " FROM " + TableConstants.TABLE_QUERYEXECUTION  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				return rs.getString(ColumnConstants.COL_QUERYEXECUTION_STATUS);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return null;
	}
	
	public static String getPath(Connection connection, int executionId) throws SQLException{
		String query = "SELECT " + ColumnConstants.COL_QUERYEXECUTION_PATH+ " FROM " + TableConstants.TABLE_QUERYEXECUTION  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				return rs.getString(ColumnConstants.COL_QUERYEXECUTION_PATH);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return null;
	}
	
	public static void delete(Connection connection, int executionId) throws SQLException{
		String query = "DELETE FROM " + TableConstants.TABLE_QUERYEXECUTION  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		try{
			st = connection.createStatement();
			st.execute(query);
		}finally{
			DatabaseFunctions.closeStatement(st);
		}
	}
	public static void updateApplicationId(Connection connection, int executionId, String applicationId) throws SQLException{
		
		String query = null;
		if(applicationId == null){
			query = "UPDATE " + TableConstants.TABLE_QUERYEXECUTION + " SET " + ColumnConstants.COL_QUERYEXECUTION_APP_ID + " = null"   
					+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;
		}else{
			query = "UPDATE " + TableConstants.TABLE_QUERYEXECUTION + " SET " + ColumnConstants.COL_QUERYEXECUTION_APP_ID + " = '"+applicationId+"'"  
					+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId; 
		}
						
		Statement st = null;
		try{
			st = connection.createStatement();
			st.execute(query);
		}finally{
			DatabaseFunctions.closeStatement(st);
		}
	}
	public static void updateStatus(Connection connection, int executionId, String status) throws SQLException{
		String query = "UPDATE " + TableConstants.TABLE_QUERYEXECUTION + " SET " + ColumnConstants.COL_QUERYEXECUTION_STATUS + " = '"+status+"'"  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		try{
			st = connection.createStatement();
			st.execute(query);
		}finally{
			DatabaseFunctions.closeStatement(st);
		}
	}
	public static void updatePath(Connection connection, int executionId, String path) throws SQLException{
		String query = "UPDATE " + TableConstants.TABLE_QUERYEXECUTION + " SET " + ColumnConstants.COL_QUERYEXECUTION_PATH + " = '"+path+"'"  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		try{
			st = connection.createStatement();
			st.execute(query);
		}finally{
			DatabaseFunctions.closeStatement(st);
		}
	}
	public static void updatePathStatus(Connection connection, int executionId, String path, String status) throws SQLException{
		String query = "UPDATE " + TableConstants.TABLE_QUERYEXECUTION + " SET " + ColumnConstants.COL_QUERYEXECUTION_PATH + " = '"+path+"', "
				+ ColumnConstants.COL_QUERYEXECUTION_STATUS + " = '"+status+"'" 
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		try{
			st = connection.createStatement();
			st.execute(query);
		}finally{
			DatabaseFunctions.closeStatement(st);
		}
	}
	
	public static Map<String, String> getQueryDetailsByExecutionId(Connection connection, int executionId) throws SQLException{
		String query = "SELECT " + ColumnConstants.COL_QUERYEXECUTION_QUERY_ID + ","
				+ ColumnConstants.COL_QUERYEXECUTION_NAMENODEID + ","
				+ ColumnConstants.COL_QUERYEXECUTION_USERNAME
				+ " FROM " + TableConstants.TABLE_QUERYEXECUTION  
				+ " WHERE " + ColumnConstants.COL_QUERYEXECUTION_ID+ " = "+executionId;				
		Statement st = null;
		ResultSet rs = null;
		Map<String, String> details = new HashMap<String, String>();
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				details.put(ColumnConstants.COL_QUERYEXECUTION_QUERY_ID, rs.getString(ColumnConstants.COL_QUERYEXECUTION_QUERY_ID));
				details.put(ColumnConstants.COL_QUERYEXECUTION_NAMENODEID, rs.getString(ColumnConstants.COL_QUERYEXECUTION_NAMENODEID));
				details.put(ColumnConstants.COL_QUERYEXECUTION_USERNAME, rs.getString(ColumnConstants.COL_QUERYEXECUTION_USERNAME));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return details;
	}
}
