package com.queryio.core.snapshots;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.SnapshotDAO;

public class SnapshotManager 
{
	public static boolean addSnapshot(String id, String namenodeHost, String location)
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			SnapshotDAO.addSnapshot(connection, id, namenodeHost, location);
			
			return true;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("addSnapshot() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return false;
	}
	
	public static boolean doesSnapshotExist(String id)
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return SnapshotDAO.doesSnapshotExist(connection, id);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("doesSnapshotExist() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return false;
	}
	
	public static boolean deleteSnapshots(ArrayList ids)
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			for(int i=0; i<ids.size(); i++)
			{
				SnapshotDAO.deleteSnapshot(connection, (String)ids.get(i));
			}
			
			return true;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("deleteSnapshots() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return false;
	}
	
	public static void updateSnapshotStatus(String id, String status)
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			SnapshotDAO.updateSnapshotStatus(connection, id, status);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("updateSnapshotStatus() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	
	public static ArrayList getSnapshots()
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return SnapshotDAO.getSnapshots(connection);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getSnapshots() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	
		return null;
	}
}
