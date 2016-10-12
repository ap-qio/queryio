package com.queryio.core.monitor.managers;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.beans.MonitorData;
import com.queryio.core.monitor.beans.SystemAttribute;
import com.queryio.core.monitor.charts.ChartData;
import com.queryio.core.monitor.charts.Series;

public class ChartManager 
{
	public static ArrayList getNameNodeChartData(String nodeId, String interval)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			List nodeSystemAttributes = MonitorDAO.getNameNodeSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getNameNodeMonitorData(connection, nodeId, interval);			
			
			List hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData hostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), interval);			
			
			chartDataList = (ArrayList) getChartDataList(nodeSystemAttributes, monitorData, hostSystemAttributes, hostMonitorData);
			
			return chartDataList;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getNameNodeChartData failed with exception: " + e.getMessage(), e);
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
	
	public static ArrayList getNameNodeChartDataBetweenTime(String nodeId, Timestamp startTimestamp, Timestamp endTimestamp)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			
			ArrayList systemAttributes = MonitorDAO.getNameNodeSystemAttributes(connection);			
			MonitorData monitorData = MonitorDAO.getNameNodeMonitorData(connection, nodeId, startTimestamp, endTimestamp);
			
			ArrayList hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData HostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), startTimestamp, endTimestamp);
			
			chartDataList = (ArrayList) getChartDataList(systemAttributes, monitorData, hostSystemAttributes, HostMonitorData);
			
			return chartDataList;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getNameNodeChartData failed with exception: " + e.getMessage(), e);
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
	
	public static ArrayList getDataNodeChartData(String nodeId, String interval)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getDataNodeSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getDataNodeMonitorData(connection, nodeId, interval);
			
			List hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData hostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), interval);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, hostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getDataNodeChartData failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static ArrayList getDataNodeChartDataBetweenTime(String nodeId, Timestamp startTimestamp, Timestamp endTimestamp)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getDataNodeSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getDataNodeMonitorData(connection, nodeId, startTimestamp, endTimestamp);
			
			ArrayList hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData HostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), startTimestamp, endTimestamp);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, HostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getDataNodeChartData failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static ArrayList getResourceManagerChartData(String nodeId, String interval)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getResourceManagerSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getResourceManagerMonitorData(connection, nodeId, interval);
			
			List hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData hostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), interval);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, hostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getResourceManagerChartData failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static ArrayList getResourceManagerChartDataBetweenTime(String nodeId, Timestamp startTimestamp, Timestamp endTimestamp)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getResourceManagerSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getResourceManagerMonitorData(connection, nodeId, startTimestamp, endTimestamp);
			
			ArrayList hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData HostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), startTimestamp, endTimestamp);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, HostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getResourceManagerChartDataBetweenTime failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static ArrayList getNodeManagerChartData(String nodeId, String interval)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getNodeManagerSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getNodeManagerMonitorData(connection, nodeId, interval);
			
			List hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData hostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), interval);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, hostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getNodeManagerChartData failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static ArrayList getNodeManagerChartDataBetweenTime(String nodeId, Timestamp startTimestamp, Timestamp endTimestamp)
	{
		ArrayList chartDataList = new ArrayList();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
		
			ArrayList monitorAttributes = MonitorDAO.getNodeManagerSystemAttributes(connection);
			MonitorData monitorData = MonitorDAO.getNodeManagerMonitorData(connection, nodeId, startTimestamp, endTimestamp);
			
			ArrayList hostSystemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			MonitorData HostMonitorData = MonitorDAO.getHostMonitorData(connection, host.getId(), startTimestamp, endTimestamp);
			
			chartDataList = (ArrayList) getChartDataList(monitorAttributes, monitorData, hostSystemAttributes, HostMonitorData);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getNodeManagerChartDataBetweenTime failed with exception: " + e.getMessage(), e);
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
		
		return chartDataList;
	}
	
	public static List getChartDataList(List systemAttributesList, MonitorData monitorData, List hostSystemAttributesList, MonitorData hostMonitorData)
	{
		List chartDataList = getChartDataList(systemAttributesList, monitorData);
		chartDataList.addAll(getChartDataList(hostSystemAttributesList, hostMonitorData));
		
		return chartDataList;
	}
	public static ArrayList getChartDataList(List systemAttributesList, MonitorData monitorData)
	{
		ArrayList chartDataList = new ArrayList();
		HashMap attributeMap = new HashMap();
		
		ArrayList groupNames = new ArrayList();
		for(int i=1; i<monitorData.getAttributeCount(); i++) //0th attribute is timestamp.
		{
			for(int j=0; j<systemAttributesList.size(); j++)
			{
				if(((SystemAttribute)systemAttributesList.get(j)).getAttributeName().equals(monitorData.getAttributeData(i).getName().toUpperCase()))
				{
					boolean addGroupToList = true;
					for(int p=0; p<groupNames.size(); p++)
					{
						if(groupNames.get(p).equals(((SystemAttribute)systemAttributesList.get(j)).getGroupName()))
						{
							addGroupToList = false;
							attributeMap.put(monitorData.getAttributeData(i).getName().toUpperCase(), p);
						}
					}
					if(addGroupToList==true)
					{
						groupNames.add(((SystemAttribute)systemAttributesList.get(j)).getGroupName());
						attributeMap.put(monitorData.getAttributeData(i).getName().toUpperCase(), groupNames.size()-1);
					}
					break;
				}
			}
		}		
		
		ArrayList al = monitorData.getAttributeData(0).getValues();
		for(int i=0; i<al.size(); i++)
		{
			al.set(i, ((Timestamp)al.get(i)).getTime());
		}
		
		ChartData cd = null;
		for(int i=0; i<groupNames.size(); i++)
		{
			cd = new ChartData((String) groupNames.get(i));
			cd.setDataPoints(al);
			chartDataList.add(cd);
		}
		
		for(int i=1; i<monitorData.getAttributeCount(); i++)
		{
			((ChartData)chartDataList.get((Integer) attributeMap.get(monitorData.getAttributeData(i).getName().toUpperCase()))).addSeries(new Series(monitorData.getAttributeData(i).getDescription().toUpperCase(), monitorData.getAttributeData(i).getValues()));
		}
		
		return chartDataList;
	}
	
	public static ChartData getStatusChartData(ArrayList systemAttributesList, MonitorData monitorData, String nodeType)
	{
		ArrayList chartDataList = new ArrayList();
		HashMap attributeMap = new HashMap();
		
		ArrayList groupNames = new ArrayList();
		for(int i=1; i<monitorData.getAttributeCount(); i++) //0th attribute is timestamp.
		{
			for(int j=0; j<systemAttributesList.size(); j++)
			{
				if(((SystemAttribute)systemAttributesList.get(j)).getAttributeName().equals(monitorData.getAttributeData(i).getName()))
				{
					boolean addGroupToList = true;
					for(int p=0; p<groupNames.size(); p++)
					{
						if(groupNames.get(p).equals(((SystemAttribute)systemAttributesList.get(j)).getGroupName()))
						{
							addGroupToList = false;
							attributeMap.put(monitorData.getAttributeData(i).getName(), p);
						}
					}
					if(addGroupToList==true)
					{
						groupNames.add(((SystemAttribute)systemAttributesList.get(j)).getGroupName());
						attributeMap.put(monitorData.getAttributeData(i).getName(), groupNames.size()-1);
					}
					break;
				}
			}
		}
		
		ArrayList al = monitorData.getAttributeData(0).getValues();
		for(int i=0; i<al.size(); i++)
		{
			al.set(i, ((Timestamp)al.get(i)).getTime());
		}
		
		ChartData cd = null;
		int statusGroupIndex = -1;
		
		String statusGroupName = null;
		
		if(nodeType.equals(QueryIOConstants.NAMENODE))	statusGroupName = QueryIOConstants.NAMENODE_STATUS_GROUP_NAME;
		else 	statusGroupName = QueryIOConstants.JOBTRACKER_STATUS_GROUP_NAME;
		
		for(int i=0; i<groupNames.size(); i++)
		{
			if(groupNames.get(i).equals(statusGroupName))
			{
				statusGroupIndex = i;
				cd = new ChartData((String) groupNames.get(i));
				cd.setDataPoints(al);
			
				chartDataList.add(cd);
			}
		}
		
		int insertIndex = -1;
		for(int i=1; i<monitorData.getAttributeCount(); i++)
		{
			insertIndex = (Integer) attributeMap.get(monitorData.getAttributeData(i).getName());
			if(insertIndex==statusGroupIndex)
			{
				((ChartData)chartDataList.get(insertIndex)).addSeries(new Series(monitorData.getAttributeData(i).getDescription(), monitorData.getAttributeData(i).getValues()));
			}
		}
		
		if(chartDataList.size()>0)
		{
			return (ChartData) chartDataList.get(0);
		}
		
		return null;
	}
}
