package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.adhoc.AdHocHiveClient;

public class HiveTableDAO 
{
	public static void insertHiveTable(Connection connection, String tableName, String nameNodeId, String rmId, String fileType, String fileName) throws Exception
	{
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_HIVETABLES);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			ps.setString(3, rmId);
			ps.setString(4, fileType);
			ps.setString(5, fileName);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void deleteHiveTable (Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_HIVETABLES);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static boolean doesHiveTableExist(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		return doesHiveTableEntryQIOSchema(connection, tableName, nameNodeId);
	}
	
	public static boolean doesHiveTableEntryQIOSchema(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_HIVETABLE);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
				flag = true;
		}
		
		finally
		{
			try
			{
				DatabaseFunctions.closeResultSet(rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed: " + e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared Statement could not be closed: " + e);
			}
		}
		
		
		
		return flag;
	}
	
	public static boolean doesHiveTableEntryHiveSchema(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean flag = false;
		Connection hiveConnection  = null;
		try
		{
			hiveConnection = AdHocHiveClient.getHiveConnection(connection, nameNodeId);

			String query = "SHOW TABLES LIKE '"+tableName+"'";
			AppLogger.getLogger().debug("doesHiveTableEntryHiveSchema tablequery : " + query);
			ps = DatabaseFunctions.getPreparedStatement(hiveConnection, query);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next()) {
				flag = true;
				AppLogger.getLogger().debug("got result set data : " + rs.getString(1));
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
				AppLogger.getLogger().fatal("Result Set could not be closed: " + e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared Statement could not be closed: " + e);
			}
		}
		
		
		
		return flag;
	}

	public static String getResourceManager(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rmid = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_HIVETABLES_GET_RMID);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				rmid =  rs.getString(ColumnConstants.COL_HIVETABLES_RMID);
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
				AppLogger.getLogger().fatal("Result Set could not be closed: " + e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared Statement could not be closed: " + e);
			}
		}
		
		return rmid;
	}
	
	public static String getFileType(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String fileType = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_HIVETABLES_GET_FILE_TYPE);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				fileType =  rs.getString(ColumnConstants.COL_HIVETABLES_FILE_TYPE);
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
				AppLogger.getLogger().fatal("Result Set could not be closed: " + e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared Statement could not be closed: " + e);
			}
		}
		
		return fileType;
	}

	public static String getFileName(Connection connection, String tableName, String nameNodeId) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String fileName = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_HIVETABLES_GET_FILE_NAME);
			
			ps.setString(1, tableName);
			ps.setString(2, nameNodeId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				fileName =  rs.getString(ColumnConstants.COL_HIVETABLES_FILE_NAME);
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
				AppLogger.getLogger().fatal("Result Set could not be closed: " + e);
			}
			try
			{
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared Statement could not be closed: " + e);
			}
		}
		
		return fileName;
	}
}
