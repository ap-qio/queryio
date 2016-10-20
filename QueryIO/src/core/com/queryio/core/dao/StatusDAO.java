package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DashboardCell;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.StatusTreeBean;

public class StatusDAO 
{
	
	public static void deleteNodeData(final Connection connection, String nodeId) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_NODE_STATUS);
			ps.setString(1, nodeId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
			
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_NODE_STATUS_CONSOLIDATEDDATA);
			ps.setString(1, nodeId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	public static void addStatus(final Connection connection, String nodeId, long timestamp, int status) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_NODE_STATUS);
			ps.setString(1, nodeId);
			ps.setLong(2, timestamp);
			ps.setInt(3, status);
			
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
	
	public static long getTimestampBefore(long timestamp, String interval)
	{
		if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_HOUR))
		{
			return timestamp - QueryIOConstants.MS_ONE_HOUR;
		}
		else if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_DAY))
		{
			return timestamp - QueryIOConstants.MS_ONE_DAY;
		}
		else if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_WEEK))
		{
			return timestamp - QueryIOConstants.MS_ONE_WEEK;
		}
		else if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_MONTH))
		{
			return timestamp - QueryIOConstants.MS_ONE_MONTH;
		}
		else if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_QUARTER))
		{
			return timestamp - QueryIOConstants.MS_ONE_QUARTER;
		}
		else if(interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_YEAR))
		{
			return timestamp - QueryIOConstants.MS_ONE_YEAR;
		}
		
		return 0;
	}
	
	public static long getTimestampBefore(long timestamp, long msBefore)
	{
		return timestamp - msBefore;
	}
	
	public static ArrayList getStatus(final Connection connection, String interval) throws Exception
	{
		return getStatusList(connection, interval);
	}
	
	public static int getHigherPriorityStatus(int status1, int status2)
	{
		return status2 > status1 ? status2 : status1;
	}
	
	private static ArrayList modifyStatusList(ArrayList statusList)  throws Exception
	{
		StatusTreeBean hostStatusTreeBean = null;
		StatusTreeBean nodeStatusTreeBean = null;
		DashboardCell dashboardCell = null;
		DashboardCell hostCell = null;
		
		ArrayList childList = null;
		ArrayList hostCellList = null;
		ArrayList nodeCellList = null;
		
		if(statusList!=null)
		{
			for(int i=0; i<statusList.size(); i++)
			{
				hostStatusTreeBean = (StatusTreeBean) statusList.get(i);
				
				hostCellList = hostStatusTreeBean.getDashboardCells();
				
				if(hostCellList==null)	hostCellList = new ArrayList();
				
				childList = hostStatusTreeBean.getChilds();
				
				for(int j=0; j<childList.size(); j++)
				{
					nodeStatusTreeBean = (StatusTreeBean) childList.get(j);
					
					nodeCellList = nodeStatusTreeBean.getDashboardCells();
					
					for(int k=0; k<nodeCellList.size(); k++)
					{
						dashboardCell = (DashboardCell)nodeCellList.get(k);

						if(hostCellList.size() < (k+1))
						{
							hostCell = new DashboardCell();
							hostCellList.add(hostCell);
						}
						else
						{
							hostCell = (DashboardCell)hostCellList.get(k);
						}
						
						hostCell.setStartTime(dashboardCell.getStartTime());
						hostCell.setState(getHigherPriorityStatus(hostCell.getState(), dashboardCell.getState()));
					}
					
				}
				
				hostStatusTreeBean.setDashboardCells(hostCellList);
			}
		}
		
		return statusList;
	}

	public static ArrayList getStatusList(final Connection connection, String interval) throws Exception
	{
		ArrayList statusList = new ArrayList();
		
		ArrayList hostList = null;
		ArrayList nodeList = null;
		Host host = null;
		Node node = null;
		
		StatusTreeBean hostStatusTreeBean = null;
		StatusTreeBean nodeStatusTreeBean = null;
		DashboardCell dashboardCell = null; 
		ArrayList cellList = null;
		
		long toTime = System.currentTimeMillis();
		long fromTime = getTimestampBefore(toTime, interval);
		
		long tickLength = (toTime - fromTime)/(QueryIOConstants.STATUS_TICKS_COUNT);
		
		long endTime = System.currentTimeMillis();
		long startTime = 0;
		
		hostList = HostDAO.getAllHostDetails(connection);
		
		for(int i=0; i<hostList.size(); i++)
		{
			host = (Host) hostList.get(i);
			nodeList = NodeDAO.getAllNodesForHost(connection, host.getId());
			if(nodeList.size() > 0){
				hostStatusTreeBean = new StatusTreeBean();
				hostStatusTreeBean.setName(host.getHostIP());
				
				endTime = toTime;
				startTime = toTime - tickLength;
				
				cellList = new ArrayList();
				
				for(int k=0; k<QueryIOConstants.STATUS_TICKS_COUNT; k++)
				{
					dashboardCell = new DashboardCell();
					dashboardCell.setStartTime(startTime);
					
					if(hasStatus(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), startTime, endTime, QueryIOConstants.NODE_STATUS_FAILURE, interval))
					{
						dashboardCell.setState(DashboardCell.CELL_FAILURE);
					}	
					else if(hasAlert(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), startTime, endTime, QueryIOConstants.ALERT_ERROR))
					{
						dashboardCell.setState(DashboardCell.CELL_ERROR);
					}
					else if(hasAlert(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), startTime, endTime, QueryIOConstants.ALERT_WARNING))
					{
						dashboardCell.setState(DashboardCell.CELL_WARNING);
					}
					else if(hasStatus(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), startTime, endTime, QueryIOConstants.NODE_STATUS_OK, interval))
					{
						dashboardCell.setState(DashboardCell.CELL_OK);
					}
					else if(hasStatus(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), startTime, endTime, QueryIOConstants.NODE_STATUS_SUSPENDED, interval))
					{
						dashboardCell.setState(DashboardCell.CELL_SUSPENDED);
					}
					else if(hasNonEmptyStatusBeforeTime(connection, QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(), endTime, interval))
					{
						dashboardCell.setState(DashboardCell.CELL_SUSPENDED);
					}
					else
					{
						dashboardCell.setState(DashboardCell.CELL_EMPTY);
					}
					
					startTime -= tickLength;
					endTime -= tickLength;
					
					cellList.add(dashboardCell);
				}
				
				hostStatusTreeBean.setDashboardCells(cellList);
				
				for(int j=0; j<nodeList.size(); j++)
				{
					node = (Node) nodeList.get(j);
					if(node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE) || node.getNodeType().equals(QueryIOConstants.JOURNALNODE))
						continue;
					nodeStatusTreeBean = new StatusTreeBean();
					nodeStatusTreeBean.setName(node.getId());
					nodeStatusTreeBean.setNodeType(node.getNodeType());
					
					endTime = toTime;
					startTime = toTime - tickLength;
					
					cellList = new ArrayList();
					
					for(int k=0; k<QueryIOConstants.STATUS_TICKS_COUNT; k++)
					{
						dashboardCell = new DashboardCell();
						dashboardCell.setStartTime(startTime);
						
						if(hasStatus(connection, node.getId(), startTime, endTime, QueryIOConstants.NODE_STATUS_FAILURE, interval))
						{
							dashboardCell.setState(DashboardCell.CELL_FAILURE);
						}	
						else if(hasAlert(connection, node.getId(), startTime, endTime, QueryIOConstants.ALERT_ERROR))
						{
							dashboardCell.setState(DashboardCell.CELL_ERROR);
						}
						else if(hasAlert(connection, node.getId(), startTime, endTime, QueryIOConstants.ALERT_WARNING))
						{
							dashboardCell.setState(DashboardCell.CELL_WARNING);
						}
						else if(hasStatus(connection, node.getId(), startTime, endTime, QueryIOConstants.NODE_STATUS_OK, interval))
						{
							dashboardCell.setState(DashboardCell.CELL_OK);
						}
						else if(hasStatus(connection, node.getId(), startTime, endTime, QueryIOConstants.NODE_STATUS_SUSPENDED, interval))
						{
							dashboardCell.setState(DashboardCell.CELL_SUSPENDED);
						}
						else if(hasNonEmptyStatusBeforeTime(connection, node.getId(), endTime, interval))
						{
							dashboardCell.setState(DashboardCell.CELL_SUSPENDED);
						}
						else
						{
							dashboardCell.setState(DashboardCell.CELL_EMPTY);
						}
						
						startTime -= tickLength;
						endTime -= tickLength;
						
						cellList.add(dashboardCell);
					}
					
					nodeStatusTreeBean.setDashboardCells(cellList);
					
					hostStatusTreeBean.addChilds(nodeStatusTreeBean);
				}
				
				statusList.add(hostStatusTreeBean);
			}
			
		}
		
		return statusList;
	}
	
	private static int getRowCount(Connection connection, String tableName) throws Exception
	{
		String query = "SELECT COUNT(*) FROM " + tableName;
		
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			while(rs.next())
			{
				count = rs.getInt(1);
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
		
		return count;
	}
	
	private static boolean hasAlert(Connection connection, String nodeId, long startTime, long endTime, String alertType) throws Exception
	{
		return AlertDAO.hasAlert(connection, nodeId, startTime, endTime, alertType); 
	}
	
	public static boolean hasNonEmptyStatusBeforeTime(final Connection connection, String nodeId, long beforeTime, String interval) throws SQLException {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			if (interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_HOUR))
			{
				ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_NODE_STATUS_LIST_BEFORE_TIME);
			}
			else
			{
				ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_CONSOLIDATED_NODE_STATUS_LIST_BEFORE_TIME);
			}
			
			ps.setLong(1, beforeTime);
			ps.setString(2, nodeId);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			
			while(rs.next())
			{
				count ++;
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
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		if(count>0)	return true;
		
		return false;
	}
	
	public static boolean hasStatus(final Connection connection, String nodeId, long startTime, long endTime, int status, String interval) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			if (interval.equalsIgnoreCase(QueryIOConstants.INTERVAL_ONE_HOUR))
			{
				ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_NODE_STATUS_IN_BETWEEN);
			}
			else
			{
				ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_NODE_CONSOLIDATED_STATUS_IN_BETWEEN);
			}
			
			ps.setLong(1, startTime);
			ps.setLong(2, endTime);
			ps.setString(3, nodeId);
			ps.setInt(4, status);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
			
			while(rs.next())
			{
				count = rs.getInt(1);
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
				DatabaseFunctions.closePreparedStatement(ps);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		if(count>0)	return true;
		
		return false;
	}
}
