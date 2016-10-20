package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Snapshot;

public class SnapshotDAO 
{
	public static void addSnapshot(final Connection connection, String id, String namenodeHost, String location) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_SNAPSHOT);
			ps.setString(1, id);
			ps.setString(2, namenodeHost);
			ps.setString(3, location);
			ps.setString(4, QueryIOConstants.SNAPSHOT_STATUS_COMPLETED);
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static void updateSnapshotStatus(final Connection connection, String id, String status) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_SNAPSHOT_STATUS);
			ps.setString(1, status);
			ps.setString(2, id);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static ArrayList getSnapshots(final Connection connection) throws Exception
	{
		ArrayList list = new ArrayList();
		
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_SNAPSHOTS);
			
			Snapshot snapshot = null;
			while(rs.next())
			{
				snapshot = new Snapshot();
				
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				
				snapshot.setId(rs.getString(ColumnConstants.COL_SNAPSHOTS_ID));
				snapshot.setHostname(rs.getString(ColumnConstants.COL_SNAPSHOTS_HOSTNAME));
				snapshot.setLocation(rs.getString(ColumnConstants.COL_SNAPSHOTS_LOCATION));
				snapshot.setStatus(rs.getString(ColumnConstants.COL_SNAPSHOTS_STATUS));
				snapshot.setTime(format.format(rs.getTimestamp(ColumnConstants.COL_SNAPSHOTS_TIME)));
				
				list.add(snapshot);
			}
		}
		finally
		{
			try
			{
				DatabaseFunctions.closeResultSet(rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return list;
	}

	public static void deleteSnapshot(Connection connection, String id) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_SNAPSHOT);
			ps.setString(1, id);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static boolean doesSnapshotExist(Connection connection, String id) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exists = true;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_SELECT_SNAPSHOT_WHERE_ID);
			ps.setString(1, id);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			while(rs.next())
			{
				exists = true;
			}
		}
		finally
		{
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return exists;
	}
}
