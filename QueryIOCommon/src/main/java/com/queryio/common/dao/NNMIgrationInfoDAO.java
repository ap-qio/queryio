package com.queryio.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.beans.NNDBMigrationInfo;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;

public class NNMIgrationInfoDAO
{
	public static void addMigrationInfo(final Connection connection, String migrationId, String nameNodeId, int hostId, String destPath, String dbName) throws Exception {
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_NN_DB_MIGRATION_STATUS);
			
			ps.setString(1, migrationId);
			DatabaseFunctions.setDateTime(ps, 2, System.currentTimeMillis());
			ps.setString(3, nameNodeId);
			ps.setInt(4, hostId);
			ps.setString(5, destPath);
			ps.setString(6, dbName);
			ps.setString(7, QueryIOConstants.PROCESS_STATUS_INPROGRESS);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void updateMigrationInfo(final Connection connection, String migrationId,
			Timestamp endTime, String status, String error) throws Exception {
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_NN_DB_MIGRATION_STATUS);
			
			ps.setTimestamp(1, endTime);
			ps.setString(2, status);
			ps.setString(3, error);			
			ps.setString(4, migrationId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	 
	}
	
	public static NNDBMigrationInfo getMigrationInfo(final Connection connection, String migrationId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		NNDBMigrationInfo info = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_NN_DB_MIGRATION_STATUS);
			ps.setString(1, migrationId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs.next())
			{
				info = new NNDBMigrationInfo();
				
				info.setMigrationId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID));
				info.setNamenodeId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_NAMENODEID));
				info.setDbName(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTDBNAME));
				
				int hostId = rs.getInt(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_HOSTID);
				info.setHostId(hostId);
				info.setHostIP(getHostDetail(connection, hostId));
				
				info.setBackupFolder(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTPATH));
				info.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_START_TIME));
				info.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_END_TIME));
				info.setStatus(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_STATUS));
				info.setError(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_ERROR));
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return info;
	}
	
	public static void deleteMigrationInfo(final Connection connection, String migrationId) throws Exception {
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_NN_DB_MIGRATION);
			ps.setString(1, migrationId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void deleteMigrationInfoFromList(final Connection connection, String migrationIds) throws Exception {
		Statement stmt = null;
		String deleteQuery = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			deleteQuery = "DELETE FROM " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " WHERE " + ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID + " IN (" + migrationIds + ")";

			CoreDBManager.executeUpdateStatement(connection, stmt, deleteQuery);
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static ArrayList<NNDBMigrationInfo> getMigrationInfoList(final Connection connection) throws Exception {
		ArrayList<NNDBMigrationInfo> list = new ArrayList<NNDBMigrationInfo>();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, "SELECT * FROM " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS);
			
			while(rs.next()) {
				NNDBMigrationInfo info = new NNDBMigrationInfo();
				
				info.setMigrationId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID));
				info.setNamenodeId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_NAMENODEID));
				info.setDbName(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTDBNAME));
				
				int hostId = rs.getInt(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_HOSTID);
				info.setHostId(hostId);
				info.setHostIP(getHostDetail(connection, hostId));
				
				info.setBackupFolder(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTPATH));
				info.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_START_TIME));
				info.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_END_TIME));
				info.setStatus(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_STATUS));
				info.setError(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_ERROR));
				
				list.add(info);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeResultSet(rs);
		}
		
		return list;
	}
	
	public static ArrayList<NNDBMigrationInfo> getMigrationInfoSelectedList(final Connection connection, String migrationIds) throws Exception {
		ArrayList<NNDBMigrationInfo> list = new ArrayList<NNDBMigrationInfo>();
		
		Statement stmt = null;
		ResultSet rs = null;
		String selectQuery = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			selectQuery = "SELECT * FROM " + TableConstants.TABLE_NN_DB_MIGRATION_STATUS + " WHERE " + ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID + " IN (" + migrationIds + ")";
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, selectQuery);
			
			while(rs.next()) {
				NNDBMigrationInfo info = new NNDBMigrationInfo();
				
				info.setMigrationId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_MIGRATIONID));
				info.setNamenodeId(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_NAMENODEID));
				info.setDbName(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTDBNAME));
				
				int hostId = rs.getInt(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_HOSTID);
				info.setHostId(hostId);
				info.setHostIP(getHostDetail(connection, hostId));
				
				info.setBackupFolder(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_DESTPATH));
				info.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_START_TIME));
				info.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_END_TIME));
				info.setStatus(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_STATUS));
				info.setError(rs.getString(ColumnConstants.COL_NN_DB_MIGRATION_STATUS_ERROR));
				
				list.add(info);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeResultSet(rs);
		}
		
		return list;
	}
	
	public static String getHostDetail(final Connection connection, int hostID) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HOST_ID);
			ps.setInt(1, hostID);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				return rs.getString(ColumnConstants.COL_HOST_IP);
			}
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}
		
		return null;
	}
}
