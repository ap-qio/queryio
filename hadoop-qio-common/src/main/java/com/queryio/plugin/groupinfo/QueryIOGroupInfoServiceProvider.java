package com.queryio.plugin.groupinfo;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.GroupMappingServiceProvider;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;

public class QueryIOGroupInfoServiceProvider implements
		GroupMappingServiceProvider {
	private static final Log LOG = LogFactory
			.getLog(QueryIOGroupInfoServiceProvider.class);

	static {
		LOG.info("QueryIOGroupInfoServiceProvider class loaded");
	}

	public QueryIOGroupInfoServiceProvider() throws IOException {
		// Connect to QueryIO DB and update Group Information
		Connection connection = null;
		try {
			String xmlFilePath = new File("").getAbsolutePath() + "/../"
					+ QueryIOConstants.QUERYIOAGENT_DIR_NAME
					+ "/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/conf/" + QueryIOConstants.DBCONFIG_XML;

			LOG.info("xmlFilePath: " + xmlFilePath);
			String jdbcDriverPath = xmlFilePath.substring(0,
					xmlFilePath.lastIndexOf("/"))
					+ "/../jdbcJars";
			LOG.info("jdbcDriverPath: " + jdbcDriverPath);
			
			EnvironmentalConstants.setDbConfigFilePath(xmlFilePath);
			EnvironmentalConstants.setJdbcDriverPath(jdbcDriverPath);
			
			try {				
				new DatabaseConfigParser()
						.loadDatabaseConfiguration(EnvironmentalConstants
								.getDbConfigFilePath());
				CoreDBManager.initialize();
			} catch (Exception e) {
				LOG.fatal("Error Initialization Database Connection.", e);
			}

			connection = CoreDBManager.getQueryIODBConnection();

			GroupInfoContainer.setGroupInfo(getUserGroupInformation());
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException("Group Information could not be updated", e);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}
	
	public static String getUserGroupInformation() throws Exception{
		String response = "";
		
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			
			ArrayList userNames = getAllUserNames(connection);
			ArrayList groupNames = null;
			String userName = null;
			
			for(int i=0; i<userNames.size(); i++){
				userName = (String) userNames.get(i);
				
				groupNames = getGroupsForUser(connection, userName);
				
				if (groupNames.size() > 0) {
					response += userName;
					response += ":";
				
					for(int j=0; j<groupNames.size(); j++){
						response += (String) groupNames.get(j);
						if(j != groupNames.size()-1)	response += ",";
					}
				}
				
				response += "@NEWLINE@";
			}
		} finally{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return response;
	}

	public static ArrayList getAllUserNames(final Connection connection) throws Exception
	{
		ArrayList list=new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
				
		try{
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_USERNAMES);
			while(rs.next())
			{
				list.add(rs.getString(ColumnConstants.COL_USER_USERNAME));
			}
		}
		finally{
			try
			{
				DatabaseFunctions.closeSQLObjects(statement, rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return list;
		
	}
	
	public static ArrayList getGroupsForUser(final Connection connection, String userName) throws Exception{
		ArrayList list = new ArrayList();
		
		int userId = getUserId(connection, userName);
		int groupId = -1;
		String groupName = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUPS_FOR_USER);
			ps.setInt(1, userId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				groupName = getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));
				
				if(groupName != null)	list.add(groupName);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return list;
	}
	
	public static int getUserId(Connection connection, String userName) throws Exception
	{
		int id = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_USERID_FROM_USERNAME);
			ps.setString(1, userName);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				id = rs.getInt(ColumnConstants.COL_USER_ID);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return id;
	}
	
	public static String getGroupName(final Connection connection, int id) throws Exception{
		String groupName = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUP_NAME_FOR_ID);
			ps.setInt(1, id);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				groupName = rs.getString(ColumnConstants.COL_GROUPS_GROUPNAME);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return groupName;		
	}
	
	
	public List<String> getGroups(String user) throws IOException {
		return getQueryIOGroups(user);
	}

	
	public void cacheGroupsRefresh() throws IOException {

		// Connect to QueryIO DB and update Group Information
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			GroupInfoContainer.refreshGroupInformation(getUserGroupInformation());
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException("Group Information could not be updated", e);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	
	}

	
	public void cacheGroupsAdd(List<String> groups) throws IOException {

	}

	private List<String> getQueryIOGroups(final String user) throws IOException {
		if (GroupInfoContainer.getGroupInfo().get(user) != null)
			return GroupInfoContainer.getGroupInfo().get(user);

		return new LinkedList<String>();
	}
}
