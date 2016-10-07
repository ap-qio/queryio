package com.queryio.core.permissions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.AccessControlException;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.User;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.requestprocessor.DeleteSelectedFilesRequest;
import com.queryio.core.requestprocessor.SetPermissionsRequest;

public class PermissionsManager {
	
	public static ArrayList getAllGroupNames(){
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return GroupDAO.getAllGroupsList(connection);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getAllGroupNames() failed with exception: " + e.getMessage(), e);
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
	
	public static ArrayList getAllUserNames(){
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return UserDAO.getAllUserNames(connection);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getAllUserNames() failed with exception: " + e.getMessage(), e);
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
	
	public static DWRResponse addGroup(String groupName){
		boolean success = true;
		Connection connection = null;
		
		DWRResponse dwrResponse = new DWRResponse();
		
		dwrResponse.setDwrResponse(false, "", 403);
		dwrResponse.setId("0");
		
		if (!QueryIOConstants.DEFAULT_GROUP_NAME_DEMO.equalsIgnoreCase(groupName)) {
			if (!RemoteManager.isAdmin()) {
				dwrResponse.setDwrResponse(false,
						QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
		}
		
		try
		{
			
			connection = CoreDBManager.getQueryIODBConnection();
			GroupDAO.addGroup(connection, groupName);
			
			dwrResponse.setDwrResponse(true, "Group added successfully", 200);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("addGroup() failed with exception: " + e.getMessage(), e);
			success = false;
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
		
		return dwrResponse;
	}
	
	public static DWRResponse deleteGroup(String groupName){
		
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 403);
		dwrResponse.setId("0");
		
		if (!RemoteManager.isAdmin()) {
			dwrResponse.setDwrResponse(false,
					QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return dwrResponse;
		}
		
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			if(getUserForGroup(groupName).size()>0) {
				dwrResponse.setDwrResponse(false, "Please remove all users from this group before deleting the group", 500);
				return dwrResponse;
			}
			
			if(groupName.equals(QueryIOConstants.DEFAULT_GROUP_NAME)) {
				dwrResponse.setDwrResponse(false, "Super group cannot be deleted", 500);
				return dwrResponse;
			}
			
			GroupDAO.deleteGroup(connection, groupName);
			
			dwrResponse.setDwrResponse(true, "Group deleted successfully", 200);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("deleteGroup() failed with exception: " + e.getMessage(), e);
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
		
		return dwrResponse;
	}
	
	public static boolean isUserAddedToGroup(String userName, String groupName){
		boolean success = true;
		
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			UserGroupDAO.isUserAddedToGroup(connection, userName, groupName);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("isUserAddedToGroup() failed with exception: " + e.getMessage(), e);
			success = false;
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
		
		return success;
	}
	
	public static ArrayList getUserForGroup(String groupName){
		
		
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return UserGroupDAO.getUsersForGroup(connection, groupName);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getGroupsForUser() failed with exception: " + e.getMessage(), e);
			return null;
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

	public static HashMap<String, ArrayList<User>> getAllUserForGroup(){
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("getAllUserForGroup");
		
		Connection connection = null;
		HashMap<String, ArrayList<User>> usersForGroup = new HashMap<String, ArrayList<User>>();
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList<String>groups= getAllGroupNames();
			if(groups.size()>0){
				for(String group : groups){
					usersForGroup.put(group,UserGroupDAO.getUsersForGroup(connection, group));
				}
			}else{
				usersForGroup.put(QueryIOConstants.DEFAULT_GROUP_NAME,UserDAO.getAllUserNames(connection));
			}
			return usersForGroup;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getGroupsForUser() failed with exception: " + e.getMessage(), e);
			return null;
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
	
	public static ArrayList getGroupsForUser(String userName){
		
		
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			return UserGroupDAO.getGroupsForUser(connection, userName);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getGroupsForUser() failed with exception: " + e.getMessage(), e);
			return null;
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
	
	public static DWRResponse setOwnerAndPermissions(String nodeId, String path, String owner, String group, short permissions, boolean recursive){
		
		DWRResponse dwrResponse = new DWRResponse();
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Setting file permissions");
		
		if (RemoteManager.isNonAdminAndDemo(null)) {
			dwrResponse.setDwrResponse(false,
					QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return dwrResponse;
		}
		
		SetPermissionsRequest request = new SetPermissionsRequest(nodeId, RemoteManager.getLoggedInUser(), new Path(path), owner, group, permissions, recursive);
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Processing");
		
		try {
			request.process();
			dwrResponse.setDwrResponse(true, "Permission Configured Successfully.", 200);
		} catch (AccessControlException ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			dwrResponse.setDwrResponse(false, "Permission Denied", 500);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Internal Server Error", 500);
		}
		
		return dwrResponse;
	}
	
	public static DWRResponse deleteFiles(String nodeId, String dirPath, ArrayList list) {
		
		DWRResponse dwrResponse = new DWRResponse();
		
		DeleteSelectedFilesRequest request = null;
		String path = "";
		
		try {
			
			if (!RemoteManager.isAdmin()) {
				dwrResponse.setDwrResponse(false,
						QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Processing");
			for(int i=0; i<list.size(); i++)
			{
				path = dirPath + list.get(i).toString();
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Deleting files: " + path);
				request = new DeleteSelectedFilesRequest(nodeId, RemoteManager.getLoggedInUser(), new Path(path));
				request.process();
			}
			
			dwrResponse.setDwrResponse(true, "Selected Files Deleted Successfully.", 200);
		} catch (AccessControlException ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			dwrResponse.setDwrResponse(false, "Permission Denied", 500);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Internal Server Error", 500);
		}
		
		return dwrResponse;
	}
	
	public static boolean setFileOwnerAndPermissions(String nodeId, String username, String path, String owner, String group, short permissions, boolean recursive){
		SetPermissionsRequest request = new SetPermissionsRequest(nodeId, username, new Path(path), owner, group, permissions, recursive);
		
		try {
			request.process();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		return request.isSuccessFul();
	}
}