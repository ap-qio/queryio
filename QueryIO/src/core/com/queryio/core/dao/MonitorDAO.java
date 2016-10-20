package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.DiskMonitoredData;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.consolidate.ConsolidationUtility;
import com.queryio.core.monitor.beans.AttributeData;
import com.queryio.core.monitor.beans.LiveAttribute;
import com.queryio.core.monitor.beans.MonitorData;
import com.queryio.core.monitor.beans.Parameter;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.monitor.beans.SystemAttribute;
import com.queryio.core.monitor.controllers.ControllerAttribute;
import com.queryio.core.monitor.controllers.ControllerData;

public class MonitorDAO
{
	public static ArrayList getHostControllerHistoricalAttributes(final Connection connection, int hostId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_MONITORDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_HOST_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getHistoricalControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getNameNodeControllerHistoricalAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_MONITORDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_NAMENODE_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getHistoricalControllerAttributes(connection, query1, query2);
	}

	public static ArrayList getDataNodeControllerHistoricalAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_MONITORDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_DATANODE_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getHistoricalControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getResourceManagerControllerHistoricalAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_MONITORDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_RESOURCE_MANAGER_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getHistoricalControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getNodeManagerControllerHistoricalAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_MONITORDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_NODE_MANAGER_SYSTEM_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getHistoricalControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getNameNodeControllerSummaryAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_SUMMARYDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_NAMENODE_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getSummaryControllerAttributes(connection, query1, query2);
	}

	public static ArrayList getDataNodeControllerSummaryAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_SUMMARYDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_DATANODE_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getSummaryControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getResourceManagerControllerSummaryAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_SUMMARYDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_RESOURCE_MANAGER_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getSummaryControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getNodeManagerControllerSummaryAttributes(final Connection connection, String nodeId) throws Exception
	{
		String query1 = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_SUMMARYDATA LIMIT 0";
		String query2 = QueryConstants.QRY_SELECT_ALL_FROM_NODE_MANAGER_LIVE_ATTRIBUTES_WHERE_COLUMNNAME_IN;
		
		return getSummaryControllerAttributes(connection, query1, query2);
	}
	
	public static ArrayList getHistoricalControllerAttributes(final Connection connection, String queryMonitorData, String querySystemAttributes) throws Exception
	{
		ResultSet rs = null;
		ResultSet rs1 = null;
		ArrayList alControllerAttributes = new ArrayList();
		Statement stmt = null;
		try
		{
			ControllerAttribute controllerAttribute = null;
	
			stmt = DatabaseFunctions.getStatement(connection);
	
			rs = CoreDBManager.getQueryResultsForStatement(stmt, queryMonitorData);
	
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(rsmd.getColumnCount()<=1)	return alControllerAttributes;
			
			StringBuffer sBuf = new StringBuffer();
			sBuf.append("(");
			for(int i=2; i<=rsmd.getColumnCount(); i++)
			{
				sBuf.append("\'"+ rsmd.getColumnName(i).toUpperCase() + "\'");
				if(i!=rsmd.getColumnCount()) sBuf.append(", ");
			}
			sBuf.append(")");
			
			HashMap map = new HashMap();
			HashMap descMap = new HashMap();
			
			rs1 = CoreDBManager.getQueryResultsForStatement(stmt, querySystemAttributes + " " + sBuf.toString());
			
			while(rs1.next())
			{
				map.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_OBJECTNAME) + QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR + rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
				descMap.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_GROUPNAME) + " : " + rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			}
			
			for(int i=2; i<=rsmd.getColumnCount(); i++)
			{
				String curColName = rsmd.getColumnName(i).toUpperCase();
				
				controllerAttribute = new ControllerAttribute();
				controllerAttribute.setName((String) map.get(curColName));
				
				controllerAttribute.setShortName(curColName);
				
				if(StaticUtilities.indexOf(ColumnConstants.PERSIST_COLUMN_NAMES, curColName)!=-1) {
					controllerAttribute.setDisplayName((String) descMap.get(curColName) + " (Rate)");	
				} else {
					controllerAttribute.setDisplayName((String) descMap.get(curColName));
				}
				
				controllerAttribute.setColumnName(curColName);
				controllerAttribute.setDataType(rsmd.getColumnType(i));
				controllerAttribute.setMaxLength(rsmd.getColumnDisplaySize(i));
				controllerAttribute.setToBeMonitored(true);
				alControllerAttributes.add(controllerAttribute);
			}
		}
		finally
		{
			try
			{
				DatabaseFunctions.closeResultSet(rs1);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
		}

		return alControllerAttributes;
	}
	
	public static ArrayList getSummaryControllerAttributes(final Connection connection, String queryMonitorData, String querySystemAttributes) throws Exception
	{
		ArrayList alControllerAttributes = new ArrayList();
		ResultSet rs1 = null;
		ResultSet rs = null;
		Statement stmt = null;
		try
		{
			ControllerAttribute controllerAttribute = null;
	
			stmt = DatabaseFunctions.getStatement(connection);
	
			rs = CoreDBManager.getQueryResultsForStatement(stmt, queryMonitorData);
	
			ResultSetMetaData rsmd = rs.getMetaData();
			if(rsmd.getColumnCount()<=1)	return alControllerAttributes;
			
			StringBuffer sBuf = new StringBuffer();
			sBuf.append("(");
			for(int i=1; i<=rsmd.getColumnCount(); i++)
			{
				sBuf.append("\'"+ rsmd.getColumnName(i).toUpperCase() + "\'");
				if(i!=rsmd.getColumnCount()) sBuf.append(", ");
			}
			sBuf.append(")");
			HashMap map = new HashMap();
			HashMap descMap = new HashMap();
			
			rs1 = CoreDBManager.getQueryResultsForStatement(stmt, querySystemAttributes + " " + sBuf.toString());
			
			while(rs1.next())
			{
				map.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_OBJECTNAME) + QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR + rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
				descMap.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			}
			
			for(int i=1; i<=rsmd.getColumnCount(); i++)
			{
				String columnName = rsmd.getColumnName(i).toUpperCase();
				controllerAttribute = new ControllerAttribute();
				controllerAttribute.setName((String) map.get(columnName));
				
				controllerAttribute.setShortName(columnName);
				controllerAttribute.setDisplayName((String) descMap.get(columnName));
				controllerAttribute.setColumnName(columnName);
				controllerAttribute.setDataType(rsmd.getColumnType(i));
				controllerAttribute.setMaxLength(rsmd.getColumnDisplaySize(i));
				controllerAttribute.setToBeMonitored(true);
	
				alControllerAttributes.add(controllerAttribute);
			}
		}
		finally
		{
			try
			{
				DatabaseFunctions.closeResultSet(rs1);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result Set could not be closed", e);
			}
		}

		return alControllerAttributes;
	}
	
	public static MonitorData getHostMonitorData(final Connection connection, int hostId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime()));
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_HOST_MONITORDATA");
		else
			intervalCondition.append("_HOST_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("NAMENODE getNameNodeMonitorData");
		
		return getMonitorData(connection, "HOST_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + intervalCondition.toString());
	}
	
	
	public static MonitorData getNameNodeMonitorData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime()));
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_NAMENODE_MONITORDATA");
		else
			intervalCondition.append("_NAMENODE_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("NAMENODE getNameNodeMonitorData");
		
		return getMonitorData(connection, "NAMENODE_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	
	public static MonitorData getNameNodeMonitorData(final Connection connection, String nodeId, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp lowerLimit = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
		
		if(startTimestamp.getTime() < lowerLimit.getTime())
			intervalCondition.append("_NAMENODE_CONSOLIDATEDDATA");
		else
			intervalCondition.append("_NAMENODE_MONITORDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTimestamp);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTimestamp);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DATANODE getDataNodeMonitorData");
		
		return getMonitorData(connection, "NAMENODE_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static MonitorData getHostMonitorData(final Connection connection, int hostId, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp lowerLimit = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
		
		if(startTimestamp.getTime() < lowerLimit.getTime())
			intervalCondition.append("_HOST_CONSOLIDATEDDATA");
		else
			intervalCondition.append("_HOST_MONITORDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTimestamp);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTimestamp);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DATANODE getDataNodeMonitorData");
		
		return getMonitorData(connection, "HOST_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + intervalCondition.toString());
	}
	
	public static MonitorData getDataNodeMonitorData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime()));
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_DATANODE_MONITORDATA");
		else
			intervalCondition.append("_DATANODE_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DATANODE getDataNodeMonitorData");
		
		return getMonitorData(connection, "DATANODE_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static MonitorData getResourceManagerMonitorData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime()));
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_RESOURCEMANAGER_MONITORDATA");
		else
			intervalCondition.append("_RESOURCEMANAGER_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DATANODE getDataNodeMonitorData");
		
		return getMonitorData(connection, "RESOURCE_MANAGER_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static MonitorData getResourceManagerMonitorData(final Connection connection, String nodeId, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp lowerLimit = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
		
		if(startTimestamp.getTime() < lowerLimit.getTime())
			intervalCondition.append("_RESOURCEMANAGER_MONITORDATA");
		else
			intervalCondition.append("_RESOURCEMANAGER_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTimestamp);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTimestamp);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		
		return getMonitorData(connection, "RESOURCE_MANAGER_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static MonitorData getNodeManagerMonitorData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime()));
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_NODEMANAGER_MONITORDATA");
		else
			intervalCondition.append("_NODEMANAGER_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DATANODE getDataNodeMonitorData");
		
		return getMonitorData(connection, "NODE_MANAGER_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static MonitorData getNodeManagerMonitorData(final Connection connection, String nodeId, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp lowerLimit = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
		
		if(startTimestamp.getTime() < lowerLimit.getTime())
			intervalCondition.append("_NODEMANAGER_MONITORDATA");
		else
			intervalCondition.append("_NODEMANAGER_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTimestamp);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTimestamp);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		
		return getMonitorData(connection, "NODE_MANAGER_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}

	public static MonitorData getDataNodeMonitorData(final Connection connection, String nodeId, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp lowerLimit = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
		
		if(startTimestamp.getTime() < lowerLimit.getTime())
			intervalCondition.append("_DATANODE_CONSOLIDATEDDATA");
		else
			intervalCondition.append("_DATANODE_MONITORDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTimestamp);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTimestamp);
		intervalCondition.append("' ORDER BY ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);

		
		return getMonitorData(connection, "DATANODE_SYSTEM_ATTRIBUTES", QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString());
	}
	
	public static long[] getNameNodeSummaryIntervalData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime())-1);
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_NAMENODE_MONITORDATA");
		else
			intervalCondition.append("_NAMENODE_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("'");
		
		String query = "SELECT SUM(" + ColumnConstants.COL_NN_MONITORDATA_FILESREAD + "), SUM(" + ColumnConstants.COL_NN_MONITORDATA_FILESWRITE + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString();
		
		long totalFileReads = 0;
		long totalFileWrites = 0;
		
		Statement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query: " + query);
			
			rs = CoreDBManager.getQueryResultsForStatement(stmt, query);
			
			if(rs.next())
			{
				try{ totalFileReads = rs.getLong(1); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileWrites = rs.getLong(2); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
		
		return new long[]{ totalFileReads, totalFileWrites };
	}
	
	public static long[] getDataNodeSummaryIntervalData(final Connection connection, String nodeId, String interval) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime())-1);
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_DATANODE_MONITORDATA");
		else
			intervalCondition.append("_DATANODE_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("'");
		
		String query = "SELECT SUM(" + ColumnConstants.COL_DN_MONITORDATA_BYTESREAD + "), SUM(" + ColumnConstants.COL_DN_MONITORDATA_BYTESWRITE + "), SUM("
				+ ColumnConstants.COL_DN_MONITORDATA_BLOCKSREAD + "), SUM(" + ColumnConstants.COL_DN_MONITORDATA_BLOCKSWRITE + ")"
				+ " FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + intervalCondition.toString();
		
		long totalBytesRead = 0;
		long totalBytesWritten = 0;
		long totalBlocksRead = 0;
		long totalBlocksWritten = 0;
		
		Statement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query: " + query);
			
			rs = CoreDBManager.getQueryResultsForStatement(stmt, query);
			
			if(rs.next())
			{
				try{ totalBytesRead = rs.getLong(1); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBytesWritten = rs.getLong(2); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksRead = rs.getLong(3); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksWritten = rs.getLong(4); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
		
		return new long[]{ totalBytesRead, totalBytesWritten, totalBlocksRead, totalBlocksWritten };
	}

	public static ArrayList getNameNodeSummaryData(final Connection connection, String nodeId, String interval) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		Object val;
		
		float jvmHeapUsed = 0;
		long jvmStartedOn = 0;
		long totalAlerts = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT * FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_NAMENODE_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				
				while(rs.next())
				{
					try{ jvmHeapUsed = rs.getFloat(ColumnConstants.COL_NN_SUMMARYDATA_JVMHEAPUSED); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ jvmStartedOn = rs.getLong(ColumnConstants.COL_NN_SUMMARYDATA_JVMSTARTEDON); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("getNameNodeSummaryData failed node Id: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		long[] vals = getNameNodeSummaryIntervalData(connection, nodeId, interval);
		
		ArrayList readWrites = getNameNodeReadWrites(connection, nodeId);
		
		totalAlerts = getNameNodeAlertCount(connection, nodeId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		
		long[] filesAndBlocks = getTotalFilesAndBlocks(connection, nodeId);
		
		list.add(new Parameter("Total Files and Directories", filesAndBlocks[0]));
		list.add(new Parameter("Total Blocks", filesAndBlocks[1]));
		
		list.add(new Parameter("JVM Heap Used", nf.format(jvmHeapUsed) + " MB"));
		list.add(new Parameter("JVM Started On", sdf.format(jvmStartedOn)));
		list.add(new Parameter("Total File Reads", readWrites.get(0)));
		list.add(new Parameter("Total File Writes", readWrites.get(1)));
		list.add(new Parameter("Total Alerts", totalAlerts));
		
		return list;
	}
	
	public static ArrayList getNameNodeSummaryData(final Connection connection) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
		ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
		Node namenode = null;
		Node datanode = null;
		long totalFileReads = 0;
		long totalFileWrites = 0;
		long totalFileAppends = 0;
		long totalFileRenames = 0;
		long totalListFileOps = 0;
		long totalDeleteFileOps = 0;
		
		long totalBytesRead = 0;
		long totalBytesWritten = 0;
		long totalBlocksRead = 0;
		long totalBlocksWritten = 0;
		
		long totalFilesAndDirectories = 0;
		long totalBlocks = 0;
		
		try
		{
			for(int var = 0 ; var < namenodes.size(); var++){
				namenode = (Node) namenodes.get(var);
				
				long[] filesAndBlocks = getTotalFilesAndBlocks(connection, namenode.getId());
				totalFilesAndDirectories += filesAndBlocks[0];
				totalBlocks += filesAndBlocks[1];
				
				try{ totalFileReads += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileWrites += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileAppends += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESAPPENDED); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileRenames += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESRENAMED); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalListFileOps += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_LISTOPS); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalDeleteFileOps += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_DELETEFILEOPS); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
			}	
			
			for(int j=0; j<datanodes.size(); j++){
				datanode = (Node) datanodes.get(j);
				
				try{ totalBytesRead += getDataNodeCurrentValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBytesWritten += getDataNodeCurrentValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksRead += getDataNodeCurrentValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksWritten += getDataNodeCurrentValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSWRITE); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
//		list.add(new Parameter("Total Files and Directories", totalFilesAndDirectories));
//		list.add(new Parameter("Total Blocks", totalBlocks));
		
		list.add(new Parameter("Total Bytes Read", getFormattedStorageSize(totalBytesRead)));
		list.add(new Parameter("Total Bytes Written", getFormattedStorageSize(totalBytesWritten)));
		list.add(new Parameter("Total Blocks Read", totalBlocksRead));
		list.add(new Parameter("Total Blocks Written", totalBlocksWritten));
		list.add(new Parameter("Total Files Read", totalFileReads));
		list.add(new Parameter("Total Files Written", totalFileWrites));
		list.add(new Parameter("Total Files Appended", totalFileAppends));
		list.add(new Parameter("Total Files Renamed", totalFileRenames));
		list.add(new Parameter("Total List File Ops", totalListFileOps));
		list.add(new Parameter("Total Delete File Ops", totalDeleteFileOps));
		return list;
	}

	public static ArrayList getDataNodeSummaryData(final Connection connection, String nodeId, String interval) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		long totalCapacity = 0;
		long usedCapacity = 0;
		float jvmHeapUsed = 0;
		long jvmStartedOn = 0;
		long totalAlerts = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT * FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_DATANODE_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				
				while(rs.next())
				{
					try{ jvmHeapUsed = rs.getFloat(ColumnConstants.COL_DN_SUMMARYDATA_JVMHEAPUSED);
					}
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ jvmStartedOn = rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_JVMSTARTEDON); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try
					{
						fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));
						
						for(int j=0; j<fullVolumeInfoList.size(); j++)
						{
							tList = (ArrayList) fullVolumeInfoList.get(j);
							totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
							usedCapacity += (Long)tList.get(2);
						}
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("getDataNodeSummaryData failed node Id: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		long[] vals = getDataNodeSummaryIntervalData(connection, nodeId, interval);
		
		totalAlerts = getDataNodeAlertCount(connection, nodeId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		
		list.add(new Parameter("JVM Heap Used", nf.format(jvmHeapUsed) + " MB"));
		list.add(new Parameter("JVM Started On", sdf.format(jvmStartedOn)));
		list.add(new Parameter("Total Bytes Read", getDataNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD)));
		list.add(new Parameter("Total Bytes Written", getDataNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN)));
		list.add(new Parameter("Total Blocks Read", getDataNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSREAD)));
		list.add(new Parameter("Total Blocks Written", getDataNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSWRITE)));
		list.add(new Parameter("Total Storage", getFormattedStorageSize(totalCapacity)));
		list.add(new Parameter("Used Storage", getFormattedStorageSize(usedCapacity)));
		list.add(new Parameter("Free Storage", getFormattedStorageSize(totalCapacity - usedCapacity)));
		list.add(new Parameter("Total Alerts", totalAlerts));
		
		return list;
	}
	
	public static int getNameNodeAlertCount(final Connection connection, String nodeId)
	{
		int totalAlerts = 0;
		try
		{
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node!=null)
			{
				totalAlerts = AlertDAO.getAlertCountForNode(connection, node.getId());
			}
	
				
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Alerts Information could not be retrieved", e);
		}
		
		return totalAlerts;
	}
	
	public static int getDataNodeAlertCount(final Connection connection, String nodeId)
	{
		int totalAlerts = 0;
		try
		{
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node!=null)
			{
				totalAlerts = AlertDAO.getAlertCountForNode(connection, node.getId());
			}
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Alerts Information could not be retrieved", e);
		}
		
		return totalAlerts;
	}
	
	public static ArrayList getDataNodeSummaryData(final Connection connection,ArrayList dataNodes) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		
		Node datanode = null;
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		long totalCapacity = 0;
		long usedCapacity = 0;
		long totalBytesRead = 0;
		long totalBytesWritten = 0;
		long totalBlocksRead = 0;
		long totalBlocksWritten = 0;
		
		try
		{
			for(int var = 0 ; var < dataNodes.size(); var++)
			{
				datanode = (Node) dataNodes.get(var);
				
				StringBuffer sBuf = new StringBuffer();
				
				sBuf.setLength(0);
				sBuf.append("SELECT * FROM ");
				sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
				sBuf.append(datanode.getId());
				sBuf.append("_DATANODE_SUMMARYDATA");
				
				try
				{
					stmt = DatabaseFunctions.getStatement(connection);
					
					rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
					
					while(rs.next())
					{
						try
						{
							fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));
							
							ArrayList<String> diskList = new ArrayList<String>();
							for(int i=0; i<fullVolumeInfoList.size(); i++)
							{
								tList = (ArrayList) fullVolumeInfoList.get(i);
								String volumePath = (String) tList.get(0);
								
								int volLastIndex = volumePath.indexOf(datanode.getId() + "/current") !=-1 ? volumePath.indexOf("current") : volumePath.length();
								String diskName = VolumeDAO.getDisk(connection, datanode.getId(), volumePath.substring(0, volLastIndex));
								if(!diskList.contains(diskName))
								{
									totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
									usedCapacity += (Long)tList.get(2);
									diskList.add(diskName);
								}
							}
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				
				try{ totalBytesRead += getDataNodeTotalValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_MONITORDATA_BYTESREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBytesWritten += getDataNodeTotalValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_MONITORDATA_BYTESWRITE); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksRead += getDataNodeTotalValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_MONITORDATA_BLOCKSREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalBlocksWritten += getDataNodeTotalValueForColumn(connection, datanode.getId(), ColumnConstants.COL_DN_MONITORDATA_BLOCKSWRITE); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		int dnCount = 0;
		try{	dnCount = NodeDAO.getAllNodeCountForType(connection, QueryIOConstants.DATANODE);	}
		catch(Exception e){	AppLogger.getLogger().fatal(e.getMessage(), e);	}
		
		int volCount = 0;
		try{	volCount = VolumeDAO.getAllDataNodeVolumesCount(connection);	}
		catch(Exception e){	AppLogger.getLogger().fatal(e.getMessage(), e);	}
		//TODO:
//		list.add(new Parameter("JVM Heap Used", nf.format(jvmHeapUsed) + " MB"));
//		list.add(new Parameter("Total Bytes Read", getFormattedStorageSize(totalBytesRead)));
//		list.add(new Parameter("Total Bytes Written", getFormattedStorageSize(totalBytesWritten)));
//		list.add(new Parameter("Total Blocks Read", totalBlocksRead));
//		list.add(new Parameter("Total Blocks Written", totalBlocksWritten));
		
		long totalFilesAndDirectories = 0;
		long totalBlocks = 0;
		
		ArrayList nodes = NodeDAO.getAllNameNodes(connection);
		for(int i=0; i<nodes.size(); i++){
			long[] data = getTotalFilesAndBlocks(connection, ((Node)nodes.get(i)).getId());
			totalFilesAndDirectories += data[0];
			totalBlocks += data[1];
		}
		
		list.add(new Parameter("Total Files and Directories", totalFilesAndDirectories));
		list.add(new Parameter("Total Blocks", totalBlocks));
		
		list.add(new Parameter("Total Storage", getFormattedStorageSize(totalCapacity)));
		list.add(new Parameter("Used Storage", getFormattedStorageSize(usedCapacity)));
		list.add(new Parameter("Free Storage", getFormattedStorageSize(totalCapacity - usedCapacity)));
//		list.add(new Parameter("Total Alerts", totalAlerts));
		list.add(new Parameter("Datanodes", dnCount));
		list.add(new Parameter("Volumes", volCount));
		
		return list;
	}
	
	public static MonitorData getMonitorData(Connection connection, String systemAttributesTableName, String query) throws Exception
	{
		MonitorData monitorData = new MonitorData();
		Statement stmt = null; 
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		
		//if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("query getMonitorData: " + query);
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
//			AppLogger.getLogger().fatal(" MonitorData.getMonitorData : Query : "+query);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			rsmd = rs.getMetaData();
			
			ArrayList[] values = new ArrayList[rsmd.getColumnCount()];
			for(int i=0; i<values.length; i++)	values[i] = new ArrayList();
			
			while(rs.next())
			{
				for(int i=0; i<rsmd.getColumnCount(); i++)
				{
					values[i].add(rs.getObject(i+1));
				}
			}
			
			HashMap mapAttributeName = new HashMap();
			HashMap mapDescription = new HashMap();
			
			rs1 = CoreDBManager.getQueryResultsForStatement(stmt, QueryConstants.QRY_SELECT_ALL_FROM + systemAttributesTableName);
			
			while(rs1.next())
			{
				mapAttributeName.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
				mapDescription.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			}
			for(int i=0; i<rsmd.getColumnCount(); i++)
			{
				monitorData.addAttributeData(new AttributeData((String) mapAttributeName.get(rsmd.getColumnName(i+1).toUpperCase()), (String) mapDescription.get(rsmd.getColumnName(i+1).toUpperCase()), values[i]));
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs1);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		return monitorData;
	}
	
	public static ArrayList getSummaryData(Connection connection, String liveAttributesTableName, String query) throws Exception
	{
		ArrayList summaryData = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		
		try
		{
			HashMap mapAttributeName = new HashMap();
			HashMap mapDescription = new HashMap();
			
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs1 = CoreDBManager.getQueryResultsForStatement(stmt, QueryConstants.QRY_SELECT_ALL_FROM + liveAttributesTableName);
			
			while(rs1.next())
			{
				mapAttributeName.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
				mapDescription.put(rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME), rs1.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			}
			
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				for(int i=1; i<=rsmd.getColumnCount(); i++)
				{
					summaryData.add(new Parameter(String.valueOf(mapDescription.get(rsmd.getColumnName(i))), rs.getLong(i))); 
				}
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs1);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		return summaryData;
	}
	
	public static ArrayList getClusterMemoryInfo(Connection connection, ArrayList namenodes)
	{
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		
		double totalCapacity = 0;
		double usedCapacity = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			for(int i=0; i<namenodes.size(); i++)
			{
				sBuf.setLength(0);
				sBuf.append("SELECT ");
				sBuf.append(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYTOTALGB);
				sBuf.append(",");
				sBuf.append(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYUSEDGB);
				sBuf.append(" FROM ");
				sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
				sBuf.append(((Node)namenodes.get(i)).getId());
				sBuf.append("_NAMENODE_SUMMARYDATA");
				
				try
				{
					stmt = DatabaseFunctions.getStatement(connection);
					
					rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
					
					while(rs.next())
					{
						totalCapacity += rs.getDouble(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYTOTALGB);
						usedCapacity += rs.getDouble(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYUSEDGB);
					}
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Storage capacity information could not be retrieved for nodeId: " +((Node)namenodes.get(i)).getId(), e);
				}
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		list.add(totalCapacity);
		list.add(usedCapacity);
		
		return list;
	}
	
	public static ArrayList getNameNodeMemoryInfo(Connection connection, String nodeId)
	{
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		
		double totalCapacity = 0;
		double usedCapacity = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT ");
			sBuf.append(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYTOTALGB);
			sBuf.append(",");
			sBuf.append(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYUSEDGB);
			sBuf.append(" FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_NAMENODE_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				
				while(rs.next())
				{
					totalCapacity = rs.getDouble(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYTOTALGB);
					usedCapacity = rs.getDouble(ColumnConstants.COL_NN_SUMMARYDATA_CAPACITYUSEDGB);
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Storage capacity information could not be retrieved for nodeId: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		list.add(totalCapacity);
		list.add(usedCapacity);
		
		return list;
	}
	
	public static ArrayList getDataNodeMemoryInfo(Connection connection, String nodeId)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		
		double totalCapacity = 0;
		double usedCapacity = 0;
		
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT ");
			sBuf.append(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO);
			sBuf.append(" FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_DATANODE_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				
				while(rs.next())
				{
					fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));
					
					for(int i=0; i<fullVolumeInfoList.size(); i++)
					{
						tList = (ArrayList) fullVolumeInfoList.get(i);
						totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
						usedCapacity += (Long)tList.get(2);
					}
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Storage capacity information could not be retrieved for nodeId: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		list.add(nf.format(totalCapacity/(float)(1024*1024*1024)));
		list.add(nf.format(usedCapacity/(float)(1024*1024*1024)));
		
		return list;
	}
	
	public static ArrayList getAllDataNodeMemoryInfo(Connection connection,ArrayList datanodes) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		
		double totalCapacity = 0;
		double usedCapacity = 0;
		
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		Node datanode;
	
		
		try
		{
			for(int j=0; j<datanodes.size(); j++)
			{
				datanode = (Node)datanodes.get(j);
			
				StringBuffer sBuf = new StringBuffer();
				
				sBuf.setLength(0);
				sBuf.append("SELECT ");
				sBuf.append(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO);
				sBuf.append(" FROM ");
				sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
				sBuf.append(datanode.getId());
				sBuf.append("_DATANODE_SUMMARYDATA");
				
				try
				{
					stmt = DatabaseFunctions.getStatement(connection);
					
					rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
					
					
					while(rs.next())
					{
						fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));
						
						ArrayList<String> diskList = new ArrayList<String>();
						for(int i=0; i<fullVolumeInfoList.size(); i++)
						{
							tList = (ArrayList) fullVolumeInfoList.get(i);
							String volumePath = (String) tList.get(0);
							
							int volLastIndex = volumePath.indexOf(datanode.getId() + "/current") !=-1 ? volumePath.indexOf("current") : volumePath.length();
							String diskName = VolumeDAO.getDisk(connection, datanode.getId(), volumePath.substring(0, volLastIndex));
							if(!diskList.contains(diskName))
							{
								totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
								usedCapacity += (Long)tList.get(2);
								diskList.add(diskName);
							}
						}
					}
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Storage capacity information could not be retrieved for nodeID: " + datanode.getId(), e);
				}
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Total Capacity: " + totalCapacity);
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Used Capacity: " + usedCapacity);
//		
		list.add(nf.format(totalCapacity/(float)(1024*1024*1024)));
		list.add(nf.format(usedCapacity/(float)(1024*1024*1024)));
		
		return list;
	}
	
	public static ArrayList parseVolumeInfo(String volumeInfo) 
	{
		ArrayList fullVolumeList = new ArrayList();
		ArrayList volumeInfoList = new ArrayList();
		String[] details = null;
		String[] memInfos = null;
		String[] temp = null;
		int tdex = -1;
		
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Volume Info:" + volumeInfo + "#");
		long freeSpace = 0;
		long usedSpace = 0;
		long reservedSpace = 0;
		if(volumeInfo!=null && volumeInfo.length()!=0 && !volumeInfo.equals("0"))
		{
			String[] volumes = volumeInfo.split("},\"");
			if(volumes.length!=0)	volumes[0] = volumes[0].substring(2, volumes[0].length());
			
			for(int i=0; i<volumes.length; i++)
			{
				details = volumes[i].split("\":\\{");
				String path = details[0];
				memInfos = details[1].split(",");
				
				temp = memInfos[0].split(":");
				freeSpace = Long.parseLong(temp[1]);
				
				temp = memInfos[1].split(":");
				usedSpace = Long.parseLong(temp[1]);
				
				temp = memInfos[2].split(":");
				tdex = temp[1].indexOf('}');
				if(tdex!=-1)	reservedSpace = Long.parseLong(temp[1].substring(0, tdex));
				else	reservedSpace = Long.parseLong(temp[1]);
				
				volumeInfoList = new ArrayList();
				volumeInfoList.add(path);
				volumeInfoList.add(freeSpace);
				volumeInfoList.add(usedSpace);
				volumeInfoList.add(reservedSpace);
				
				fullVolumeList.add(volumeInfoList);
			}
		}
		
		return fullVolumeList;
	}

	public static ArrayList getClusterSummaryData(Connection connection, String liveAttributesTableName, ArrayList namenodes) throws Exception
	{
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		String queryNameNode = null;
		String queryDataNode = null;
		
		long totalFileReads = 0;
		long totalFileWrites = 0;
		
		long totalBytesRead = 0;
		long totalBytesWritten = 0;
		long totalBlocksRead = 0;
		long totalBlocksWritten = 0;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			Object key = null;
			String type = null;
			
			
			for(int i=0; i<namenodes.size(); i++)
			{	
				queryNameNode = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + ((Node)namenodes.get(i)).getId() + "_NAMENODE_SUMMARYDATA";
				
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, queryNameNode);
				
				while(rs.next())
				{
					try{ totalFileReads += rs.getLong(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ totalFileWrites += rs.getLong(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
			}
			
			ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			
			for (int i=0; i<datanodes.size(); i++)
			{
				queryDataNode = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + ((Node)datanodes.get(i)).getId() + "_DATANODE_SUMMARYDATA";
				
				rs1 = DatabaseFunctions.getQueryResultsForStatement(stmt, queryDataNode);
				
				while(rs1.next())
				{
					try{ totalBytesRead += rs1.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ totalBytesWritten += rs1.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ totalBlocksRead += rs1.getLong(ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSREAD); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try{ totalBlocksWritten += rs1.getLong(ColumnConstants.COL_DN_SUMMARYDATA_TOTALBLOCKSWRITE); }
					catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs1);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		list.add(new Parameter("Total File Reads", totalFileReads));
		list.add(new Parameter("Total File Writes", totalFileWrites));
		list.add(new Parameter("Total Bytes Read", totalBytesRead));
		list.add(new Parameter("Total Bytes Written", totalBytesWritten));
		list.add(new Parameter("Total Blocks Read", totalBlocksRead));
		list.add(new Parameter("Total Blocks Written", totalBlocksWritten));
		
		return list;
	}
	
	public static String getFormattedStorageSize(long bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
	public static String getFormattedStorageSize(float bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
	public static String getFormattedRate(long bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes/s";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB/s";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB/s";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB/s";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
	public static String getFormattedRate(float bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes/s";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB/s";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB/s";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB/s";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
	public static SummaryTable getAllNameNodesSummaryTable(Connection connection, String liveAttributesTableName, ArrayList namenodes,boolean isSummaryForReport) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		SummaryTable summaryTable = new SummaryTable();
		Statement stmt = null; 
		ResultSet rs = null;
		Statement hostDataStmt = null;
		ResultSet hostDataRS = null;
		String query = null;
		
		try
		{
			
			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			
			colNames.add("NameNode");
			colNames.add("Host");
			colNames.add("Type");
			colNames.add("JVM Heap");
			colNames.add("File Reads");
			colNames.add("File Writes");
			colNames.add("Alerts");
			colNames.add("CPU Usage");
			colNames.add("Used RAM");
			colNames.add("Available RAM");
			colNames.add("N/W Rcvd");
			colNames.add("N/W Sent");
			colNames.add("Disk Read");
			colNames.add("Disk Write");
			colNames.add("Safemode");
			if(!isSummaryForReport){
				colNames.add("Logs");
			}
			
			colNames.add("Status");
			colNames.add("Monitoring");
			colNames.add("NodeId");
			
			
			summaryTable.setColNames(colNames);
			
			Object key = null;
			String type = null;
			
			for(int i=0; i<namenodes.size(); i++)
			{
				stmt = DatabaseFunctions.getStatement(connection);
				hostDataStmt = DatabaseFunctions.getStatement(connection);
				Node node = ((Node)namenodes.get(i));
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_NAMENODE_SUMMARYDATA";
				
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_HOST_SUMMARYDATA";
				
				hostDataRS = DatabaseFunctions.getQueryResultsForStatement(hostDataStmt, query);
				
				colValues = new ArrayList();	
				
				colValues.add(node.getId()); // Host Name				
				colValues.add(host.getHostIP());	
				colValues.add(HAStatusDAO.isHANode(connection, node.getId())?(HAStatusDAO.getStandbyNodeId(connection, node.getId()) == null?QueryIOConstants.STANDBY:QueryIOConstants.ACTIVE):QueryIOConstants.NONHA); // HA Status 
				
				if(rs.next() && hostDataRS.next())
				{
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_NN_SUMMARYDATA_JVMHEAPUSED)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					colValues.add(getNameNodeAlertCount(connection, ((Node)namenodes.get(i)).getId())); // Alerts
					
					try	{	colValues.add(nf.format(hostDataRS.getFloat(ColumnConstants.COL_CPUUSAGE)) + " %"); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{
						if(hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) == 0) {
							colValues.add("N/A");							
						} else {							
							colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) - hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
						}
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }

					try	{	colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_RECDBYTESPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_SENTBYTESPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_DSK_BYTESREADPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_DSK_BYTESWRITEPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	
						String safeMode = rs.getString(ColumnConstants.COL_NN_SUMMARYDATA_SAFEMODESTATUS);
						if(safeMode == null || safeMode.length() == 0)
							colValues.add("Off");		
						else
							colValues.add(safeMode.indexOf("ON") != -1 ? "On" : "Off");
					}catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
				
				if(!isSummaryForReport){
					colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
				}
				colValues.add(node.getStatus());
				colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
				colValues.add(node.getId());				
				summaryTable.addRow(colValues);
				try
				{
					DatabaseFunctions.closeSQLObjects(stmt, rs);
					DatabaseFunctions.closeSQLObjects(hostDataStmt, hostDataRS);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
				}
			}
		}
		finally
		{
			
			try
			{
				if(hostDataStmt!=null){
					DatabaseFunctions.closeStatement(hostDataStmt);
				}
				if(stmt!=null){
					DatabaseFunctions.closeStatement(stmt);
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		return summaryTable;
	}
	
	private static long[] getTotalFilesAndBlocks(final Connection connection, String nameNodeId) throws SQLException{
		long[] filesAndDirectories = new long[]{0, 0};
		
		ResultSet rs = null;
		Statement stmt = null;
		try{
			stmt = DatabaseFunctions.getStatement(connection);
			String query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nameNodeId + "_NAMENODE_SUMMARYDATA";
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			if(rs.next()){
				try{ filesAndDirectories[0] += rs.getLong(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILES); } catch(Exception e) { AppLogger.getLogger().fatal(e.getMessage(), e); }
				try{ filesAndDirectories[1] += rs.getLong(ColumnConstants.COL_NN_SUMMARYDATA_TOTALBLOCKS); } catch(Exception e) { AppLogger.getLogger().fatal(e.getMessage(), e); }
			}
		} finally {
			try{
				DatabaseFunctions.closeResultSet(rs);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				DatabaseFunctions.closeStatement(stmt);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return filesAndDirectories;
	}
	
	private static String constructOutLink(Node node, Host host){
		return "http://" + host.getHostIP() + ":" + host.getAgentPort() + "/" + QueryIOConstants.AGENT_QUERYIO + "/log" +
				"?node-id="+node.getId() + "&node-type="+node.getNodeType() + "&host-dir="+host.getInstallDirPath() + 
				"&file-type=out";
	}
	private static String constructLogLink(Node node, Host host){
		return "http://" + host.getHostIP() + ":" + host.getAgentPort() + "/" + QueryIOConstants.AGENT_QUERYIO +"/log" +
				"?node-id="+node.getId() + "&node-type="+node.getNodeType() + "&host-dir="+host.getInstallDirPath() + 
				"&file-type=log";
	}
	
	public static String processHealthStatus(String diskStatus)
	{
		if(diskStatus != null && diskStatus.equalsIgnoreCase("1"))
		{
			diskStatus = "Healthy";
		}
		else if(diskStatus != null && diskStatus.equalsIgnoreCase("0"))
		{
			diskStatus = "Failure is Imminent";
		}
		else
		{
			diskStatus = "N/A";
		}
		return diskStatus;
	}
	
	public static SummaryTable getAllDataNodesSummaryTable(Connection connection, String liveAttributesTableName, ArrayList datanodes,boolean isSummaryForReport) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		SummaryTable summaryTable = new SummaryTable();
		Statement stmt = null; 
		Statement hostDataStmt = null;
		ResultSet rs = null;
		ResultSet hostDataRS = null;
		
		String query = null;
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		int volLastIndex = -1;
		
		String volumePath= null;
		
		try
		{
			
			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			
			colNames.add("DataNode");
			colNames.add("Host");
			colNames.add("Rack Name");
			colNames.add("JVM Heap");
			colNames.add("Volume");
			colNames.add("Disk");
			colNames.add("Size");			
			colNames.add("Disk Read");
			colNames.add("Disk Write");
			colNames.add("Disk Usage");
			colNames.add("Bytes Read");
			colNames.add("Bytes Written");
			colNames.add("Alerts");
			colNames.add("CPU Usage");
			colNames.add("Used RAM");
			colNames.add("Available RAM");
			colNames.add("N/W Rcvd");
			colNames.add("N/W Sent");			
			if(!isSummaryForReport){
				colNames.add("Logs");
			}
			colNames.add("Status");
			colNames.add("Monitoring");
			colNames.add("Disk Health Status");
			colNames.add("NodeId");
			summaryTable.setColNames(colNames);
			
			Object key = null;
			String type = null;
			DiskMonitoredData diskMonitoredData = null;
			for(int i=0; i<datanodes.size(); i++)
			{
				stmt = DatabaseFunctions.getStatement(connection);
				hostDataStmt = DatabaseFunctions.getStatement(connection);
				long totalCapacity = 0;
				long usedCapacity = 0;
				
				Node node = (Node)datanodes.get(i);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_DATANODE_SUMMARYDATA";
				
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_HOST_SUMMARYDATA";
				
				hostDataRS = DatabaseFunctions.getQueryResultsForStatement(hostDataStmt, query);
				
				if(rs.next() && hostDataRS.next())
				{
					try
					{
						fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));
						
						if(fullVolumeInfoList==null)
						{
							ArrayList lst = new ArrayList();
							lst.add("N/A");
							lst.add(0);
							lst.add(0);
							lst.add(0);
							fullVolumeInfoList.add(lst);
						}
												
//						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fullVolumeInfoList" + fullVolumeInfoList);
						if(fullVolumeInfoList.size()==1){
							
							tList = (ArrayList) fullVolumeInfoList.get(0);
							volumePath = ((String) tList.get(0)).replace("\\\\", "\\");	//  in case of windows, volume path has \\ as separator.
							totalCapacity = (Long)tList.get(1) + (Long)tList.get(2);
							usedCapacity = (Long)tList.get(2);
							
							colValues = new ArrayList();
							
							colValues.add(node.getId()); // Host Name
							colValues.add(host.getHostIP());
							colValues.add(host.getRackName()); // Rack Name
							
							try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_NN_SUMMARYDATA_JVMHEAPUSED)*QueryIOConstants.ONE_MB)); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add(volumePath);
							
							volLastIndex = volumePath.indexOf(node.getId() + "/current") !=-1 ? volumePath.indexOf("current") : volumePath.length();
							String diskName = VolumeDAO.getDisk(connection, node.getId(), volumePath.substring(0, volLastIndex));
							diskMonitoredData = DiskMonitoredDataDAO.getMonitoredData(connection, host.getId(), diskName);
							
							try {colValues.add(diskName); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add(getFormattedStorageSize(totalCapacity));
							
							try	{	colValues.add(getFormattedRate(diskMonitoredData != null ? diskMonitoredData.getDiskByteReadsPerSec() : 0)); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedRate(diskMonitoredData != null ? diskMonitoredData.getDiskByteWritesPerSec() : 0)); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							if(totalCapacity==0)	colValues.add("0 %");
							else	colValues.add(nf.format(usedCapacity * 100 /(float) totalCapacity) + " %");
							
							try	{	colValues.add(getFormattedStorageSize(rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedStorageSize(rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add(getDataNodeAlertCount(connection, node.getId())); // Alerts
							
							try	{	colValues.add(nf.format(hostDataRS.getFloat(ColumnConstants.COL_CPUUSAGE)) + " %"); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{
								if(hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) == 0) {
									colValues.add("N/A");							
								} else {							
									colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) - hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
								}
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }

							try	{	colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_RECDBYTESPERSEC))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_SENTBYTESPERSEC))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							if(!isSummaryForReport){
								colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
							}
							colValues.add(node.getStatus());
							colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
							//disk status goes here
							try	{
								
								colValues.add(diskMonitoredData != null ? processHealthStatus(diskMonitoredData.getDiskHealthStatus()) : "N/A");
							}
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							
							colValues.add(node.getId());
							summaryTable.addRow(colValues);
						}
						else
						{
							colValues = new ArrayList();
							
							colValues.add(node.getId()); // Host Name
							colValues.add(host.getHostIP());
							colValues.add(host.getRackName()); // Rack Name
							try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_NN_SUMMARYDATA_JVMHEAPUSED)*QueryIOConstants.ONE_MB)); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add(""); // Volumes in their respective rows
							colValues.add(""); // Disks in their respective rows
							
							ArrayList<String> diskList = new ArrayList<String>();
							for(int j=0; j<fullVolumeInfoList.size(); j++)
							{
								tList = (ArrayList) fullVolumeInfoList.get(j);
								volumePath = ((String) tList.get(0)).replace("\\\\", "\\");;
								
								volLastIndex = volumePath.indexOf(node.getId() + "/current") !=-1 ? volumePath.indexOf("current") : volumePath.length();
								String diskName = VolumeDAO.getDisk(connection, node.getId(), volumePath.substring(0, volLastIndex));
								if(!diskList.contains(diskName))
								{
									totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
									usedCapacity += (Long)tList.get(2);
									diskList.add(diskName);
								}
							}
							
							colValues.add(getFormattedStorageSize(totalCapacity));
							
							colValues.add("N/A");
							colValues.add("N/A");							
							
							if(totalCapacity==0)	colValues.add("0 %");
							else	colValues.add(nf.format(usedCapacity * 100 /(float) totalCapacity) + " %");
							
							try	{	colValues.add(getFormattedStorageSize(rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedStorageSize(rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add(getDataNodeAlertCount(connection, node.getId())); // Alerts
							
							try	{	colValues.add(nf.format(hostDataRS.getFloat(ColumnConstants.COL_CPUUSAGE)) + " %"); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{
								if(hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) == 0) {
									colValues.add("N/A");							
								} else {							
									colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) - hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
								}
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }

							try	{	colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_RECDBYTESPERSEC))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_SENTBYTESPERSEC))); }
							catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
							
							colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
							
							colValues.add(node.getStatus());
							colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
							
							colValues.add("");
							
							
							colValues.add(node.getId());
							summaryTable.addRow(colValues);
							int index = summaryTable.getRows().size() - 1;
							
							ArrayList tempList = colValues;
							float totalReads = 0;
							float totalWrites = 0;
							totalCapacity = 0;
							usedCapacity = 0;
							
							for(int j=0; j<fullVolumeInfoList.size(); j++)
							{
								tList = (ArrayList) fullVolumeInfoList.get(j);
								volumePath = (String) tList.get(0); 
								totalCapacity = (Long)tList.get(1) + (Long)tList.get(2);
								usedCapacity = (Long)tList.get(2);
								
								colValues = new ArrayList();
								colValues.add(node.getId());
								colValues.add(""); // Host Name
								colValues.add(""); // Rack name
								colValues.add(""); // Heap Used
								
								colValues.add(volumePath);
								
								volLastIndex = volumePath.indexOf(node.getId() + "/current") !=-1 ? volumePath.indexOf("current") : volumePath.length();
								
								String diskName = VolumeDAO.getDisk(connection, node.getId(), volumePath.substring(0, volLastIndex));
								diskMonitoredData = DiskMonitoredDataDAO.getMonitoredData(connection, host.getId(), diskName);
								try {colValues.add(diskName); }
								catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
								
								colValues.add(getFormattedStorageSize(totalCapacity));
								
								//	MAC does not provide status of invisual disks.
								if(QueryIOAgentManager.isMacOS(host))
								{
									colValues.add(""); // Disk Read
									colValues.add(""); // Disk Write
									colValues.add(""); // Disk Usage
									
									if(diskMonitoredData != null){
										totalReads = diskMonitoredData.getDiskByteReadsPerSec();
										totalWrites = diskMonitoredData.getDiskByteWritesPerSec();
									}
								}
								else
								{
									ArrayList<String> volumeList = new ArrayList<String>();
									if(diskMonitoredData != null){
										if(!volumeList.contains(diskMonitoredData.getDiskName()))
										{
											totalReads += diskMonitoredData.getDiskByteReadsPerSec();
											totalWrites += diskMonitoredData.getDiskByteWritesPerSec();
											volumeList.add(diskMonitoredData.getDiskName());
										}
									}
									colValues.add(getFormattedRate(diskMonitoredData != null ? diskMonitoredData.getDiskByteReadsPerSec() : 0)); // Disk
									colValues.add(getFormattedRate(diskMonitoredData != null ? diskMonitoredData.getDiskByteWritesPerSec() : 0));
									
									if(totalCapacity==0)	colValues.add("0 %");
									else	colValues.add(nf.format(usedCapacity * 100 /(float) totalCapacity) + " %");
								}
								
								
								colValues.add(""); // Bytes Read
								colValues.add(""); // Bytes Written
								colValues.add(""); // Alerts
								colValues.add(""); // CPU Usage
								colValues.add(""); // RAM
								colValues.add(""); // Network
								colValues.add(""); // Network								
								colValues.add(""); // Status								
								
								colValues.add(""); // monitoring
								//disk status goes here
								try	{
									colValues.add(diskMonitoredData != null ? processHealthStatus(diskMonitoredData.getDiskHealthStatus()) : "N/A");
								}
								catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
								
								colValues.add(node.getId());
								summaryTable.addRow(colValues);
							}
							tempList.set(7, getFormattedRate(totalReads));
							tempList.set(8, getFormattedRate(totalWrites));
							summaryTable.setRow(index, tempList);
						}
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal("getAllDataNodesSummaryTable failed with exception: " + e.getMessage(), e);
					}
				}
				try{
					DatabaseFunctions.closeSQLObjects(stmt, rs);
					DatabaseFunctions.closeSQLObjects(hostDataStmt, hostDataRS);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("Error while clossing SQL objects: " + e.getMessage(), e);
				}
			}
		}
		finally
		{
			try
			{
				if(stmt!=null){
					DatabaseFunctions.closeStatement(stmt);
				}
				if(hostDataStmt!=null){
					DatabaseFunctions.closeStatement(hostDataStmt);
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			
		}
		
		return summaryTable;
	}
	
	public static ArrayList getNameNodeSystemAttributes(final Connection connection) throws Exception
	{
		return getSystemAttributes(connection, QueryConstants.QRY_GET_NAMENODE_SYSTEM_ATTRIBUTES);
	}
	
	public static ArrayList getHostSystemAttributes(final Connection connection) throws Exception
	{
		return getSystemAttributes(connection, QueryConstants.QRY_GET_HOST_SYSTEM_ATTRIBUTES);
	}
	
	public static ArrayList getDataNodeSystemAttributes(final Connection connection) throws Exception
	{
		return getSystemAttributes(connection, QueryConstants.QRY_GET_DATANODE_SYSTEM_ATTRIBUTES);
	}
	
	public static ArrayList getResourceManagerSystemAttributes(final Connection connection) throws Exception
	{
		return getSystemAttributes(connection, QueryConstants.QRY_GET_RESOURCE_MANAGER_SYSTEM_ATTRIBUES);
	}
	
	public static ArrayList getNodeManagerSystemAttributes(final Connection connection) throws Exception
	{
		return getSystemAttributes(connection, QueryConstants.QRY_GET_NODE_MANAGER_SYSTEM_ATTRIBUES);
	}
	
	public static ArrayList getNameNodeLiveAttributes(final Connection connection) throws Exception
	{
		return getLiveAttributes(connection, QueryConstants.QRY_GET_NAMENODE_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getDataNodeLiveAttributes(final Connection connection) throws Exception
	{
		return getLiveAttributes(connection, QueryConstants.QRY_GET_DATANODE_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getResourceManagerLiveAttributes(final Connection connection) throws Exception
	{
		return getLiveAttributes(connection, QueryConstants.QRY_GET_RESOURCE_MANAGER_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getNodeManagerLiveAttributes(final Connection connection) throws Exception
	{
		return getLiveAttributes(connection, QueryConstants.QRY_GET_NODE_MANAGER_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getHostLiveAttributes(final Connection connection) throws Exception
	{
		return getLiveAttributes(connection, QueryConstants.QRY_GET_HOST_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getSystemAttributes(final Connection connection, String query) throws Exception
	{
		ArrayList monitorAttributes = new ArrayList();
		
		Statement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			while(rs.next())
			{
				monitorAttributes.add(new SystemAttribute(rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME).toUpperCase(),
						rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_OBJECTNAME).toUpperCase(),
								rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_COLUMNNAME).toUpperCase(),
									rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION).toUpperCase(),
						rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DATATYPE).toUpperCase(),
								rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_GROUPNAME).toUpperCase()));
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		
		return monitorAttributes;
	}
	
	public static ArrayList getLiveAttributes(final Connection connection, String query) throws Exception
	{
		ArrayList liveAttributes = new ArrayList();
		
		Statement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			while(rs.next())
			{
				liveAttributes.add(new LiveAttribute(rs.getString(ColumnConstants.COL_LIVE_ATTRIBUTES_ATTRIBUTENAME).toUpperCase(),
						rs.getString(ColumnConstants.COL_LIVE_ATTRIBUTES_OBJECTNAME),
								rs.getString(ColumnConstants.COL_LIVE_ATTRIBUTES_COLUMNNAME),
									rs.getString(ColumnConstants.COL_LIVE_ATTRIBUTES_DESCRIPTION),
						rs.getString(ColumnConstants.COL_LIVE_ATTRIBUTES_DATATYPE).toUpperCase()));
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		
		return liveAttributes;
	}
	
	public static void createTable(final Connection connection, String tableName, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{		
		Statement stmt = null; 
		
		StringBuffer sBuf = new StringBuffer();
		sBuf.append(QueryConstants.QRY_CREATE_CACHED_TABLE);
		sBuf.append(tableName);
		sBuf.append(QueryConstants.BRACKET_START);
		
		for(int i=0; i<columnNames.size(); i++)
		{
			sBuf.append(columnNames.get(i));
			sBuf.append(QueryConstants.SPACE);
			sBuf.append(columnTypes.get(i));
			if(i!=columnNames.size()-1)
			{
				sBuf.append(QueryConstants.COMMA);
			}
		}
		
		sBuf.append(QueryConstants.BRACKET_END);
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, sBuf.toString());
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteNameNodeMonitorTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_MONITORDATA");
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_CONSOLIDATEDDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteDataNodeMonitorTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_MONITORDATA");
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_CONSOLIDATEDDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	public static void deleteHostMonitorTable(final Connection connection, int hostId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_MONITORDATA");
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_CONSOLIDATEDDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteResourceManagerMonitorTable(final Connection connection, String id) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_RESOURCEMANAGER_MONITORDATA");
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_RESOURCEMANAGER_CONSOLIDATEDDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteResourceManagerSummaryTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_SUMMARYDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteNodeManagerMonitorTable(final Connection connection, String id) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_NODEMANAGER_MONITORDATA");
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_NODEMANAGER_CONSOLIDATEDDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteNodeManagerSummaryTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_SUMMARYDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteNameNodeSummaryTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_SUMMARYDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteHostSummaryTable(final Connection connection, int hostId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_SUMMARYDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void deleteDataNodeSummaryTable(final Connection connection, String nodeId) throws SQLException
	{
		Statement stmt = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, QueryConstants.QRY_DROP_TABLE  + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_SUMMARYDATA");
		}
		finally
		{
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void createHostMonitorTable(final Connection connection, int hostId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_MONITORDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_CONSOLIDATEDDATA";
		createTable(connection, tableName, columnNames, columnTypes);
	}
	
	public static void createNameNodeMonitorTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_MONITORDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_CONSOLIDATEDDATA";
		createTable(connection, tableName, columnNames, columnTypes);
	}
	
	public static void createDataNodeMonitorTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_MONITORDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_CONSOLIDATEDDATA";
		createTable(connection, tableName, columnNames, columnTypes);
	}
	
	public static void createNodeManagerMonitorTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_MONITORDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_CONSOLIDATEDDATA";
		createTable(connection, tableName, columnNames, columnTypes);
	}
	
	public static void createResourceManagerMonitorTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_MONITORDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_CONSOLIDATEDDATA";
		createTable(connection, tableName, columnNames, columnTypes);
	}
	
	public static void createHostSummaryTable(final Connection connection, int hostId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_SUMMARYDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		insertNullValues(connection, tableName);
	}
	
	public static void createNameNodeSummaryTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_SUMMARYDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		insertNullValues(connection, tableName);
	}
	
	public static void createDataNodeSummaryTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_SUMMARYDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		insertNullValues(connection, tableName);
	}
	
	public static void createResourceManagerSummaryTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_SUMMARYDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		insertNullValues(connection, tableName);
	}
	
	public static void createNodeManagerSummaryTable(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnTypes) throws Exception
	{
		String tableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_SUMMARYDATA";
		createTable(connection, tableName, columnNames, columnTypes);
		
		insertNullValues(connection, tableName);
	}
	
	public static void insertNullValues(final Connection connection, String tableName) throws SQLException
	{
		String query = "SELECT * FROM " + tableName + " LIMIT 0";
		
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			rsmd = rs.getMetaData();
			
			int colCount = rsmd.getColumnCount();
			
			String insertQry = null;
			
			StringBuffer sBuf = new StringBuffer();
			sBuf.append("INSERT INTO " + tableName + " VALUES(");
			for(int i=0; i<colCount; i++)
			{
				sBuf.append("0");
				if(i!=colCount-1)
				{
					sBuf.append(",");
				}
			}
			sBuf.append(")");
			
			insertQry = sBuf.toString();
			
			stmt1 = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt1, insertQry);
			
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeStatement(stmt1);
		}
	}
	
	public static void insertNameNodeSystemAttributes(final Connection connection, ArrayList list) throws Exception
	{
		String query = QueryConstants.PREPARED_QRY_INSERT_NAMENODE_SYSTEM_ATTRIBUTES;
		
		PreparedStatement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getPreparedStatement(connection, query);
			
			for(int i=0; i<list.size(); i++)
			{
				stmt.setObject(1, ((SystemAttribute)list.get(i)).getAttributeName());
				stmt.setObject(2, ((SystemAttribute)list.get(i)).getObjectname());
				stmt.setObject(3, ((SystemAttribute)list.get(i)).getColumnName());
				stmt.setObject(4, ((SystemAttribute)list.get(i)).getDataType());
				stmt.setObject(5, ((SystemAttribute)list.get(i)).getGroupName());
				
				DatabaseFunctions.executeUpdateStatement(stmt);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	
	public static void insertNameNodeMonitorData(final Connection connection, String nodeId, ArrayList columnValues) throws Exception
	{
		final Statement stmt = DatabaseFunctions.getStatement(connection);
		final ResultSet rs = CoreDBManager.getQueryResultsForStatement(stmt, "SELECT * FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_MONITORDATA");

		ResultSetMetaData rsmd = rs.getMetaData();
		
		int colCount = rsmd.getColumnCount();
		
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("INSERT INTO NAMENODE_SYSTEM_ATTRIBUTES VALUES(");
		for(int i=0; i<rsmd.getColumnCount(); i++)
		{
			sBuf.append("?");
			if(i!=rsmd.getColumnCount()-1)	sBuf.append(",");
		}
		sBuf.append(")");
		
		String query = sBuf.toString();
		
		PreparedStatement pst = null; 
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			
			for(int j=0; j<colCount; j++)
			{
				pst.setObject(j+1, columnValues.get(j));
				System.out.println((j+1)+" "+columnValues.get(j));	
			}
				
			DatabaseFunctions.executeUpdateStatement(pst);
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeStatement(pst);
		}
	}
	
	public static void updateSummaryData(final Connection connection, String queryInsert, ArrayList columnNames, ArrayList columnValues) throws SQLException
	{
		if(columnNames != null && columnNames.size() > 0){
			StringBuffer sBuf = new StringBuffer();
			sBuf.append(queryInsert);
			
			for(int i=0; i<columnNames.size(); i++)
			{
				sBuf.append(columnNames.get(i));
				sBuf.append("=");
				if(columnValues.get(i) instanceof String)
				{
					sBuf.append("'");
					sBuf.append(columnValues.get(i));
					sBuf.append("'");
				}
				else
				{
					sBuf.append(columnValues.get(i));
				}
				if(i!=columnNames.size()-1)	sBuf.append(",");
			}
			
			String query = sBuf.toString();
			
			Statement stmt = null; 
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				DatabaseFunctions.executeUpdateStatement(stmt, query);
			}
			finally
			{
				try
				{
					DatabaseFunctions.closeStatement(stmt);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Prepared statement could not be closed, Exception: " + e.getMessage(), e);
				}
			}
		}
	}
	
	public static void insertMonitorData(final Connection connection, String queryInsert, ArrayList columnNames, ArrayList columnValues) throws SQLException
	{
		StringBuffer sBuf = new StringBuffer();
		sBuf.append(queryInsert);
		sBuf.append("(");
		for(int i=0; i<columnNames.size(); i++)
		{
			sBuf.append(columnNames.get(i));
			if(i!=columnNames.size()-1)	sBuf.append(",");
		}
		sBuf.append(") VALUES (");
		for(int i=0; i<columnNames.size(); i++)
		{
			sBuf.append("?");
			if(i!=columnNames.size()-1)	sBuf.append(",");
		}
		sBuf.append(")");
		
		String query = sBuf.toString();
		PreparedStatement pst = null; 
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			for(int j=0; j<columnValues.size(); j++)
			{
				pst.setObject(j+1, columnValues.get(j));
			}
				
			DatabaseFunctions.executeUpdateStatement(pst);
		}
		finally
		{
			try
			{
				DatabaseFunctions.closeStatement(pst);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Prepared statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public static void insertNameNodeMonitorData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "INSERT INTO " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_MONITORDATA ";
		
		insertMonitorData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void insertDataNodeMonitorData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "INSERT INTO " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_MONITORDATA ";
		
		insertMonitorData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void insertHostMonitorData(final Connection connection, int hostId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "INSERT INTO " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + "_HOST_MONITORDATA ";
		
		insertMonitorData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void insertResourceManagerMonitorData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "INSERT INTO " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_RESOURCEMANAGER_MONITORDATA ";
		
		insertMonitorData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void insertNodeManagerMonitorData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "INSERT INTO " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_MONITORDATA ";
		
		insertMonitorData(connection, queryInsert, columnNames, columnValues);
	}
	
	
	public static void updateNameNodeSummaryData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "UPDATE " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_SUMMARYDATA SET ";
		
		updateSummaryData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void updateDataNodeSummaryData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "UPDATE " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId+ "_DATANODE_SUMMARYDATA SET ";
		
		updateSummaryData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void updateHostSummaryData(final Connection connection, int hostId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "UPDATE " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId+ "_HOST_SUMMARYDATA SET ";
		
		updateSummaryData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void updateNodeManagerSummaryData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "UPDATE " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NODEMANAGER_SUMMARYDATA SET ";
		
		updateSummaryData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static void updateResourceManagerSummaryData(final Connection connection, String nodeId, ArrayList columnNames, ArrayList columnValues) throws Exception
	{
		String queryInsert = "UPDATE " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId+ "_RESOURCEMANAGER_SUMMARYDATA SET ";
		
		updateSummaryData(connection, queryInsert, columnNames, columnValues);
	}
	
	public static ArrayList getLiveControllerAttributes(final Connection connection, String queryLiveAttributes) throws Exception
	{
		ArrayList alLiveControllerAttributes = new ArrayList();
		ControllerAttribute controllerAttribute = null;

		final Statement stmt = DatabaseFunctions.getStatement(connection);

		final ResultSet rs = CoreDBManager.getQueryResultsForStatement(stmt, queryLiveAttributes);

		while(rs.next())
		{
			controllerAttribute = new ControllerAttribute();
			
			controllerAttribute.setName(rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_OBJECTNAME) + QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR + rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
			controllerAttribute.setShortName(rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_ATTRIBUTENAME));
			controllerAttribute.setDisplayName(rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			controllerAttribute.setColumnName(rs.getString(ColumnConstants.COL_SYSTEM_ATTRIBUTES_DESCRIPTION));
			controllerAttribute.setToBeMonitored(true);

			alLiveControllerAttributes.add(controllerAttribute);
		}
		DatabaseFunctions.closeSQLObjects(stmt, rs);

		return alLiveControllerAttributes;
	}
	
	public static ArrayList getNameNodeLiveControllerAttributes(final Connection connection) throws Exception
	{
		return getLiveControllerAttributes(connection, QueryConstants.QRY_SELECT_ALL_FROM_NAMENODE_LIVE_ATTRIBUTES);
	}
	
	public static ArrayList getDataNodeLiveControllerAttributes(final Connection connection) throws Exception
	{
		return getLiveControllerAttributes(connection, QueryConstants.QRY_SELECT_ALL_FROM_DATANODE_LIVE_ATTRIBUTES);
	}
	
	public static long calculateStartTime(final String interval, long endTime)
	{
		final long oneHour = 1000 * 60 * 60;
		final long oneDay = 24 * oneHour;
		final long oneWeek = 7 * oneDay;
		final long oneMonth = ((long)30 * oneDay);
		long startTime = endTime;
		
		if(!interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Timestamp(endTime));
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);	
			
			endTime = cal.getTimeInMillis();
		}
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
		{
			startTime = endTime - oneHour;
		}
		else
		{
			if (interval.equals(QueryIOConstants.INTERVAL_ONE_DAY))
				startTime = endTime - (24 * oneHour);
			else if (interval.equals(QueryIOConstants.INTERVAL_ONE_WEEK))
				startTime = endTime - oneWeek;
			else if (interval.equals(QueryIOConstants.INTERVAL_ONE_MONTH))
				startTime = endTime - oneMonth;
			else if (interval.equals(QueryIOConstants.INTERVAL_QUARTER))
				startTime = endTime - (13 * oneWeek);
			else if (interval.equals(QueryIOConstants.INTERVAL_HALF_YEAR))
				startTime = endTime - (27 * oneWeek);
			else if (interval.equals(QueryIOConstants.INTERVAL_ONE_YEAR))
				startTime = endTime - (((long)12 * oneMonth)+((long)5*oneDay));
			else
			{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Unknown interval received: " + interval);
			}
		}

		// return (startTime > 0? startTime:endTime);
		return startTime;
	}

	public static ArrayList getNameNodeReadWrites(Connection connection, String nodeId) throws Exception
	{
		long filesRead = 0;
		long filesWritten = 0;
		
		filesRead = getNameNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD);
		filesWritten = getNameNodeCurrentValueForColumn(connection, nodeId, ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE);
		
		ArrayList list = new ArrayList();
		list.add(filesRead);
		list.add(filesWritten);
		
		return list;
	}
	
	public static ArrayList getNameNodeReadWrites(Connection connection, String nodeId, String interval) throws Exception
	{
//		long[] vals = getNameNodeSummaryIntervalData(connection, nodeId, interval);
//		
//		ArrayList list = new ArrayList();
//		list.add(vals[0]);
//		list.add(vals[1]);
//		
//		return list;
		
		return getNameNodeReadWrites(connection, nodeId);
	}
	
	public static long getNameNodeTotalFilesWritten(Connection connection, String nodeId) throws Exception
	{
		return getNameNodeTotalValueForColumn(connection, nodeId, ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE);
	}

	public static ArrayList getDataNodeReadWrites(Connection connection, String nodeId) 
	{
		Statement stmt = null; 
		ResultSet rs = null;
		
		long bytesRead = 0;
		long bytesWritten = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT ");
			sBuf.append(ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD);
			sBuf.append(",");
			sBuf.append(ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN);
			sBuf.append(" FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_DATANODE_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				while(rs.next())
				{
					bytesRead = rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESREAD);
					bytesWritten = rs.getLong(ColumnConstants.COL_DN_SUMMARYDATA_BYTESWRITTEN);
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("File Read/Write information could not be retrieved for nodeId: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		ArrayList list = new ArrayList();
		list.add(bytesRead);
		list.add(bytesWritten);
		
		return list;
	}
	
	public static ArrayList getNameNodeReadWrites(Connection connection) throws Exception 
	{
		long fileReads = 0;
		long fileWrites = 0;
		
		ArrayList hostRW = null;
		
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
			
		if(namenodes!=null)
		{
			for(int i=0; i<namenodes.size(); i++)
			{
				hostRW = getNameNodeReadWrites(connection, ((Node)namenodes.get(i)).getId());
				if(hostRW!=null)
				{
					fileReads += (Long)hostRW.get(0);
					fileWrites += (Long)hostRW.get(1);
				}
			}
		}
			
		ArrayList list = new ArrayList();
		list.add(fileReads);
		list.add(fileWrites);
		
		return list;
	}

	public static ArrayList getDataNodeReadWrites(Connection connection) throws Exception 
	{
		long fileReads = 0;
		long fileWrites = 0;
		
		ArrayList hostRW = null;
		
		ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			
		if(datanodes!=null)
		{
			for(int i=0; i<datanodes.size(); i++)
			{
				hostRW = getDataNodeReadWrites(connection, ((Node)datanodes.get(i)).getId());
				if(hostRW!=null)
				{
					fileReads += (Long)hostRW.get(0);
					fileWrites += (Long)hostRW.get(1);
				}
			}
		}
			
		ArrayList list = new ArrayList();
		list.add(fileReads);
		list.add(fileWrites);
		
		return list;
	}
	
	public static ControllerAttribute getHistoricalControllerAttribute(Connection connection, String nodeId, String attributeName) throws Exception 
	{
		ControllerAttribute ca = null;
		ArrayList list = null;
		
		if(nodeId.startsWith(QueryIOConstants.HOST_RULES_PREFIX)) {
			list = getHostControllerHistoricalAttributes(connection, HostDAO.getHostDetail(connection, nodeId.replace(QueryIOConstants.HOST_RULES_PREFIX, QueryIOConstants.EMPTY_STRING)).getId());
		} else {
			Node node = NodeDAO.getNode(connection, nodeId);
			
			if(node.getNodeType().equals(QueryIOConstants.NAMENODE))
			{
				list = getNameNodeControllerHistoricalAttributes(connection, nodeId);
			}
			else if(node.getNodeType().equals(QueryIOConstants.DATANODE))
			{
				list = getDataNodeControllerHistoricalAttributes(connection, nodeId);
			}
			else if(node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER))
			{
				list = getResourceManagerControllerHistoricalAttributes(connection, nodeId);
			}
			else if(node.getNodeType().equals(QueryIOConstants.NODEMANAGER))
			{
				list = getNodeManagerControllerHistoricalAttributes(connection, nodeId);
			}
		}
		if(list != null){
			for(int i=0; i<list.size(); i++)
			{
				ca = (ControllerAttribute)list.get(i);
				if(ca.getName().equals(attributeName))
				{
					return ca;
				}
			}
		}
		return null;
	}
	
	public static ControllerAttribute getNameNodeHistoricalControllerAttribute(Connection connection, String nodeId, String attributeName) throws Exception 
	{
		ControllerAttribute ca = null;
		ArrayList list = getNameNodeControllerHistoricalAttributes(connection, nodeId);
		for(int i=0; i<list.size(); i++)
		{
			ca = (ControllerAttribute)list.get(i);
			if(ca.getName().equals(attributeName))
			{
				return ca;
			}
		}
		
		return null;
	}
	
	public static ControllerAttribute getDataNodeHistoricalControllerAttribute(Connection connection, String nodeId, String attributeName) throws Exception 
	{
		ControllerAttribute ca = null;
		ArrayList list = getDataNodeControllerHistoricalAttributes(connection, nodeId);
		for(int i=0; i<list.size(); i++)
		{
			ca = (ControllerAttribute)list.get(i);
			if(ca.getName().equals(attributeName))
			{
				return ca;
			}
		}
		
		return null;
	}
	
	public static ControllerAttribute getNameNodeLiveControllerAttribute(Connection connection, String attributeName) throws Exception 
	{
		ControllerAttribute ca = null;
		ArrayList list = getSystemAttributes(connection, QueryConstants.QRY_GET_NAMENODE_LIVE_ATTRIBUTES);
		for(int i=0; i<list.size(); i++)
		{
			ca = (ControllerAttribute)list.get(i);
			if(ca.getName().equals(attributeName))
			{
				return ca;
			}
		}
		
		return null;
	}
	
	public static ControllerAttribute getDataNodeLiveControllerAttribute(Connection connection, String attributeName) throws Exception 
	{
		ControllerAttribute ca = null;
		ArrayList list = getSystemAttributes(connection, QueryConstants.QRY_GET_DATANODE_LIVE_ATTRIBUTES);
		for(int i=0; i<list.size(); i++)
		{
			ca = (ControllerAttribute)list.get(i);
			if(ca.getName().equals(attributeName))
			{
				return ca;
			}
		}
		
		return null;
	}
	
	public static ControllerData getControllerData(Connection connection, String nodeId, long dataTimeStamp) throws Exception
	{
		ControllerData controllerData = new ControllerData();
		Node node = NodeDAO.getNode(connection, nodeId);
//		Host host = null;
		if(node!=null)
		{
		
			PreparedStatement pst = null;
			ResultSet rs = null;
			ResultSetMetaData rsmd = null;
			ArrayList colNames = new ArrayList();
			ArrayList colValues = new ArrayList();
			try
			{
				
					String query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId();
					if(node.getNodeType().equals(QueryIOConstants.NAMENODE))
							query += "_NAMENODE_MONITORDATA";
					else if(node.getNodeType().equals(QueryIOConstants.DATANODE))
							query += "_DATANODE_MONITORDATA";
					else if(node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER))
						query += "_RESOURCEMANAGER_MONITORDATA";
					else if(node.getNodeType().equals(QueryIOConstants.NODEMANAGER))
						query += "_NODEMANAGER_MONITORDATA";
					
					query += " WHERE " + ColumnConstants.COL_MONITORDATA_MONITOR_TIME + "=?";
					
					AppLogger.getLogger().debug("getControllerData QUeryIO : " + query + " : " + nodeId + " : " + dataTimeStamp);
					
					pst = DatabaseFunctions.getPreparedStatement(connection, query);
					pst.setTimestamp(1, new Timestamp(dataTimeStamp));
					
					rs = CoreDBManager.getQueryResultsForPreparedStatement(pst);
					rsmd = rs.getMetaData();
					
					if(rs.next())
					{
						for(int i=1; i<=rsmd.getColumnCount(); i++)
						{
							colNames.add(rsmd.getColumnName(i));
							colValues.add(rs.getObject(rsmd.getColumnName(i)));
						}
					}
					
					controllerData.setColumnNames(colNames);
					controllerData.setValues(colValues);
				
				controllerData.setColumnNames(colNames);
				controllerData.setValues(colValues);
			}
			finally
			{
				try
				{
					DatabaseFunctions.closeResultSet(rs);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("ResultSet could not be closed", e);
				}
				try
				{
					DatabaseFunctions.closePreparedStatement(pst);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("PreparedStatement could not be closed", e);
				}
			}
		}
		return controllerData;
	}

	public static HashMap getDataNodeStorageReportSummary(final Connection connection, String nodeId, int hostId) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		HashMap map = new HashMap();
		Statement stmt = null;
		Statement stmt2 = null; 
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList fullVolumeInfoList = null;
		ArrayList tList = null;
		
		long totalCapacity = 0;
		long usedCapacity = 0;
		long bytesWritePerSec = 0;
		
		try
		{
			StringBuffer sBuf = new StringBuffer();
			
			sBuf.setLength(0);
			sBuf.append("SELECT * FROM ");
			sBuf.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf.append(nodeId);
			sBuf.append("_DATANODE_SUMMARYDATA");
			
			StringBuffer sBuf2 = new StringBuffer();
			
			sBuf2.setLength(0);
			
			sBuf2.append("SELECT * FROM ");
			sBuf2.append(QueryIOConstants.MONITORDATA_TABLE_PREFIX);
			sBuf2.append(hostId);
			sBuf2.append("_HOST_SUMMARYDATA");
			
			try
			{
				stmt = DatabaseFunctions.getStatement(connection);
				stmt2 = DatabaseFunctions.getStatement(connection);
				rs = CoreDBManager.getQueryResultsForStatement(stmt, sBuf.toString());
				rs2 = CoreDBManager.getQueryResultsForStatement(stmt2, sBuf2.toString());
				
				if(rs2.next()){
					bytesWritePerSec = rs2.getLong(ColumnConstants.COL_DSK_BYTESWRITEPERSEC);
				}
				
				while(rs.next())
				{
					try
					{
						fullVolumeInfoList = parseVolumeInfo(rs.getString(ColumnConstants.COL_DN_SUMMARYDATA_VOLUMEINFO));						
						
						for(int j=0; j<fullVolumeInfoList.size(); j++)
						{
							tList = (ArrayList) fullVolumeInfoList.get(j);
							totalCapacity += (Long)tList.get(1) + (Long)tList.get(2);
							usedCapacity += (Long)tList.get(2);
						}
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("getDataNodeSummaryData failed nodeId: " + nodeId, e);
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeResultSet(rs2);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		map.put("Total Capacity (GB)", nf.format((totalCapacity)/(float)(1024*1024*1024)));
		map.put("Used Capacity (GB)", nf.format((usedCapacity)/(float)(1024*1024*1024)));
		map.put("Free Capacity (GB)", nf.format((totalCapacity - usedCapacity)/(float)(1024*1024*1024)));
		map.put("Rate(bytes/sec)", bytesWritePerSec);
		
		return map;
	}
	
	public static long getDataNodeAvgDiskBytesWritePerSecForOneYear(final Connection connection, int hostId) throws Exception
	{
		StringBuffer intervalCondition = new StringBuffer();
		
		String interval = QueryIOConstants.INTERVAL_ONE_YEAR;

		long avgBytesWritePerSec = 0;
		
		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp startTime = new Timestamp(calculateStartTime(interval, endTime.getTime())-1);
		
		if (interval.equals(QueryIOConstants.INTERVAL_ONE_HOUR))
			intervalCondition.append("_HOST_MONITORDATA");
		else
			intervalCondition.append("_HOST_CONSOLIDATEDDATA");

		intervalCondition.append(" WHERE ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append(">='");
		intervalCondition.append(startTime);
		intervalCondition.append("' AND ");
		intervalCondition.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		intervalCondition.append("<='");
		intervalCondition.append(endTime);
		intervalCondition.append("'");
		
		String query = "SELECT AVG(" + ColumnConstants.COL_DSK_BYTESWRITEPERSEC + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + hostId + intervalCondition.toString();
		
		long totalFileReads = 0;
		long totalFileWrites = 0;
		
		Statement stmt = null; 
		ResultSet rs = null;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = CoreDBManager.getQueryResultsForStatement(stmt, query);
			
			if(rs.next())
			{
				try{ avgBytesWritePerSec = rs.getLong(1); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
		
		return avgBytesWritePerSec;
	}
	
	public static TableModel getStorageForecastTableModel(final Connection connection) throws Exception 
	{
		String[] colNames = new String[]{ "Device Name", "Total Capacity", "Used Capacity", "Used % ", "Rate", "Eighty Percent", "Ninety Percent", "One Hundred Percent", "Free Capacity", "% Free Capacity" };
		DefaultTableModel tableModel = new DefaultTableModel(colNames, 0);
		
		Object[] object;
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		float totalCapacity = 0;
		float usedCapacity = 0;
		float freeCapacity = 0;
		long avgBytesWrittenPerSec = 0;
		float percentUsed = 0;
		String eightyPercent = "";
		String ninetyPercent = "";
		String oneHundredPercent = "";
		long seconds = 0;
		HashMap map = null;
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		
		ArrayList nodeList = NodeDAO.getAllDatanodes(connection);
		Host host = null;
		Node node = null;
		for(int i = 0; i < nodeList.size(); i ++)
		{		
			node = (Node) nodeList.get(i);
			host=HostDAO.getHostDetail(connection, node.getHostId());
			avgBytesWrittenPerSec = getDataNodeAvgDiskBytesWritePerSecForOneYear(connection, host.getId());
			
			map = getDataNodeStorageReportSummary(connection, node.getId(), host.getId());
			
			object = new Object[colNames.length];
			
			totalCapacity = Float.parseFloat(((String) map.get("Total Capacity (GB)")).replace(",", ""));
			usedCapacity = Float.parseFloat(((String) map.get("Used Capacity (GB)")).replace(",", ""));
			freeCapacity = Float.parseFloat(((String) map.get("Free Capacity (GB)")).replace(",", ""));
			
			
			
			//object[0] = i+1;
			object[0] = host.getHostIP();
			
//			object[1] = totalCapacity;
//			object[2] = usedCapacity;
			
			object[1] = getFormattedStorageSize(totalCapacity * QueryIOConstants.ONE_GB);
			object[2] = getFormattedStorageSize(usedCapacity * QueryIOConstants.ONE_GB);
			
			percentUsed = totalCapacity!=0 ? (usedCapacity * 100/(float)totalCapacity) : 0;
			
			object[3] = nf.format(percentUsed);
//			object[4] = nf.format(avgBytesWrittenPerSec/(float)(1024*1024));
			object[4] = getFormattedRate(avgBytesWrittenPerSec);
			
			if(percentUsed>=80)
			{
				eightyPercent = "Already Reached";
			}
			else
			{
				if(avgBytesWrittenPerSec==0) eightyPercent = "";
				else
				{
					seconds = (long) (freeCapacity*1024*1024*1024 / avgBytesWrittenPerSec);
					eightyPercent = sdf.format(new Timestamp(System.currentTimeMillis() + seconds*1000).getTime());
				}
			}
			
			if(percentUsed>=90)
			{
				eightyPercent = "Already Reached";
			}
			else
			{
				if(avgBytesWrittenPerSec==0) ninetyPercent = "";
				else
				{
					seconds = (long) (freeCapacity*1024*1024*1024 / avgBytesWrittenPerSec);
					ninetyPercent = sdf.format(new Timestamp(System.currentTimeMillis() + seconds*1000).getTime());
				}
			}
			
			if(percentUsed>=100)
			{
				eightyPercent = "Already Reached";
			}
			else
			{
				if(avgBytesWrittenPerSec==0) oneHundredPercent = "";
				else
				{
					seconds = (long) (freeCapacity*1024*1024*1024 / avgBytesWrittenPerSec);
					oneHundredPercent = sdf.format(new Timestamp(System.currentTimeMillis() + seconds*1000).getTime());
				}
			}
			
			object[5] = eightyPercent;
			object[6] = ninetyPercent;
			object[7] = oneHundredPercent;
//			object[8] = freeCapacity;
			object[8] = getFormattedStorageSize(freeCapacity * QueryIOConstants.ONE_GB);
			object[9] = nf.format(100f - percentUsed);
		
			tableModel.addRow(object);
		}			
		
		return tableModel;
	}
	
	public static long getMonitorDataTotalValueForColumn(final Connection connection, String query) throws Exception
	{
		Statement stmt = null; 
		ResultSet rs = null;
		
		long value = 0;
		
		try
		{
			stmt = DatabaseFunctions.getStatement(connection);
			
			rs = CoreDBManager.getQueryResultsForStatement(stmt, query);
			
			if(rs.next())
			{
				try{ value = rs.getLong(1); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
		
		return value;
	}
	
	public static long getNameNodeTotalValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT SUM(" + columnName + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId;
		
		Timestamp limit = ConsolidationUtility.truncateUptoMinute(new Timestamp(System.currentTimeMillis()));
		
		String query1 = query + "_NAMENODE_MONITORDATA WHERE MONITOR_TIME > '" + limit + "'";
		String query2 = query + "_NAMENODE_CONSOLIDATEDDATA";
		
		long value = 0;
		
		value += getMonitorDataTotalValueForColumn(connection, query1);
		value += getMonitorDataTotalValueForColumn(connection, query2);
		
		return value;
	}
	
	public static long getNameNodeCurrentValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT " + columnName + " FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_NAMENODE_SUMMARYDATA";
		
		return getMonitorDataTotalValueForColumn(connection, query);
	}
	
	public static long getDataNodeCurrentValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT " + columnName + " FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId + "_DATANODE_SUMMARYDATA";
		
		return getMonitorDataTotalValueForColumn(connection, query);
	}
	
	public static long getNameNodeLatestValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT SUM(" + columnName + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId;
		
		String query1 = query + "_NAMENODE_MONITORDATA ORDER BY MONITOR_TIME DESC LIMIT 1";
		
		long value = 0;
		
		value += getMonitorDataTotalValueForColumn(connection, query1);
		
		return value;
	}
	
	public static long getDataNodeTotalValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT SUM(" + columnName + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId;
		
		Timestamp limit = ConsolidationUtility.truncateUptoMinute(new Timestamp(System.currentTimeMillis()));
		
		String query1 = query + "_DATANODE_MONITORDATA WHERE MONITOR_TIME > '" + limit + "'";
		String query2 = query + "_DATANODE_CONSOLIDATEDDATA";
		
		long value = 0;
		
		value += getMonitorDataTotalValueForColumn(connection, query1);
		value += getMonitorDataTotalValueForColumn(connection, query2);
		
		return value;
	}
	
	public static long getDataNodeLatestValueForColumn(final Connection connection, String nodeId, String columnName) throws Exception
	{
		String query = "SELECT SUM(" + columnName + ") FROM " + QueryIOConstants.MONITORDATA_TABLE_PREFIX + nodeId;
		
		String query1 = query + "_DATANODE_MONITORDATA ORDER BY MONITOR_TIME DESC LIMIT 1";
		
		long value = 0;
		
		value += getMonitorDataTotalValueForColumn(connection, query1);
		
		return value;
	}
	
	public static SummaryTable getAllResourceManagersSummaryTable(Connection connection, String liveAttributesTableName, ArrayList namenodes, boolean isSummaryForReport) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		SummaryTable summaryTable = new SummaryTable();
		Statement stmt = null; 
		Statement hostDataStmt = null;
		ResultSet rs = null;
		ResultSet hostDataRS = null;
		String query = null;
		
		try
		{
		
			
			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			
			colNames.add("Node");
			colNames.add("Host");
			colNames.add("Submitted");
			colNames.add("Pending");
			colNames.add("Completed");
			colNames.add("Containers Running");
			colNames.add("Memory Allocated");
			colNames.add("Memory Available");
			colNames.add("Memory Pending");
			colNames.add("Memory Reserved");
			colNames.add("Active Nodes");
			colNames.add("Decom. Nodes");
			colNames.add("Lost Nodes");
			colNames.add("Unhealthy Nodes");
			colNames.add("Rebooted Nodes");
			if( ! isSummaryForReport){
				colNames.add("Logs");	
			}
			colNames.add("Status");
			colNames.add("Monitoring");

			summaryTable.setColNames(colNames);
			
			Object key = null;
			String type = null;
			
			for(int i=0; i<namenodes.size(); i++)
			{
				hostDataStmt = DatabaseFunctions.getStatement(connection);
				stmt = DatabaseFunctions.getStatement(connection);
				Node node = ((Node)namenodes.get(i));
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_RESOURCEMANAGER_SUMMARYDATA";
				
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_HOST_SUMMARYDATA";
				
				hostDataRS = DatabaseFunctions.getQueryResultsForStatement(hostDataStmt, query);
				
				colValues = new ArrayList();	
				
				colValues.add(node.getId()); // Host Name				
				
				colValues.add(host.getHostIP());
				
				if(rs.next() || hostDataRS.next())
				{
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_APPSSUBMITTED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_APPSPENDING)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_APPSCOMPLETED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_CONTAINERSRUNNING)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_RESOURCE_MANAGER_ALLOCATEDMB)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_RESOURCE_MANAGER_AVAILABLEMB)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_RESOURCE_MANAGER_PENDINGMB)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_RESOURCE_MANAGER_RESERVEDMB)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_NUMACTIVENMS)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_NUMDECOMMISSIONEDNMS)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_NUMLOSTNMS)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_NUMUNHEALTHYNMS)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_RESOURCE_MANAGER_NUMREBOOTEDNMS)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
				if(!isSummaryForReport){
					colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
				}
				colValues.add(node.getStatus());
				colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
				summaryTable.addRow(colValues);
				try{
					DatabaseFunctions.closeSQLObjects(stmt,rs);
					DatabaseFunctions.closeSQLObjects(hostDataStmt,hostDataRS);
				}
				catch(Exception e){
					
				}
			}
		}
		finally
		{
			try
			{
				if(stmt!=null){
					DatabaseFunctions.closeStatement(stmt);
				}
				if(hostDataStmt!=null){
					DatabaseFunctions.closeStatement(hostDataStmt);
				}
				
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error while clossing SQL statements: " + e.getMessage(), e);
			}
			
		}
		
		return summaryTable;
	}
	
	public static SummaryTable getAllNodeManagersSummaryTable(Connection connection, String liveAttributesTableName, ArrayList namenodes, boolean isSummaryForReport) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		SummaryTable summaryTable = new SummaryTable();
		Statement stmt = null; 
		Statement hostDataStmt = null;
		ResultSet rs = null;
		ResultSet hostDataRS = null;
		String query = null;
		
		try
		{
			
			
			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			
			colNames.add("Node");
			colNames.add("Host");
			colNames.add("Launched");
			colNames.add("Completed");
			colNames.add("Failed");
			colNames.add("Killed");
			colNames.add("Running");
			colNames.add("Initing");
			if( ! isSummaryForReport){
				colNames.add("Logs");	
			}
			colNames.add("Status");
			colNames.add("Monitoring");

			summaryTable.setColNames(colNames);
			
			Object key = null;
			String type = null;
			
			for(int i=0; i<namenodes.size(); i++)
			{
				stmt = DatabaseFunctions.getStatement(connection);
				hostDataStmt = DatabaseFunctions.getStatement(connection);
				Node node = ((Node)namenodes.get(i));
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_NODEMANAGER_SUMMARYDATA";
				
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_HOST_SUMMARYDATA";
				
				hostDataRS = DatabaseFunctions.getQueryResultsForStatement(hostDataStmt, query);
				
				colValues = new ArrayList();	
				
				colValues.add(node.getId()); // Host Name				
				
				colValues.add(host.getHostIP());
				
				if(rs.next() || hostDataRS.next())
				{
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSLAUNCHED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSCOMPLETED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSFAILED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSKILLED)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSRUNNING)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NODE_MANAGER_CONTAINERSINITING)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
				if( ! isSummaryForReport){
					colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
				}
				colValues.add(node.getStatus());
				colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
				summaryTable.addRow(colValues);
				try{
					DatabaseFunctions.closeSQLObjects(stmt,rs);
					DatabaseFunctions.closeSQLObjects(hostDataStmt,hostDataRS);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("Error while clossing SQL statements: " + e.getMessage(), e);
				}
			}
		}
		finally
		{
			try
			{
				if(stmt!=null){
					DatabaseFunctions.closeStatement(stmt);
				}
				if(hostDataStmt!=null){
					DatabaseFunctions.closeStatement(hostDataStmt);
				}
				
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error while clossing SQL statements: " + e.getMessage(), e);
			}
		}
		
		return summaryTable;
	}
	public static ArrayList getAllResourceManagerAppDetails(Connection connection, ArrayList rmIDs)
	{
		ArrayList appData = new ArrayList();
//		int appsSubmitted = 0;
		int appsRunning = 0;
		int appsPending = 0;
		int appsCompleted = 0;
		int appsKilled = 0;
		Statement stmt = null;
		ResultSet rs = null; 
		try{
			for(Object nodeID:rmIDs){
				stmt = DatabaseFunctions.getStatement(connection);
				AppLogger.getLogger().fatal(QueryIOConstants.MONITORDATA_TABLE_PREFIX + (String)nodeID + "_RESOURCEMANAGER_SUMMARYDATA");
				
				
				String query = QueryConstants.QRY_GET_RM_APPS_DETAIL + QueryIOConstants.MONITORDATA_TABLE_PREFIX + (String)nodeID + "_RESOURCEMANAGER_SUMMARYDATA"; 
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				while(rs.next()){
//					appsSubmitted += rs.getInt(ColumnConstants.COL_RESOURCE_MANAGER_APPSSUBMITTED);
					appsRunning += rs.getInt(ColumnConstants.COL_RESOURCE_MANAGER_APPSRUNNING);
					appsPending += rs.getInt(ColumnConstants.COL_RESOURCE_MANAGER_APPSPENDING);
					appsCompleted += rs.getInt(ColumnConstants.COL_RESOURCE_MANAGER_APPSCOMPLETED);
					appsKilled += rs.getInt(ColumnConstants.COL_RESOURCE_MANAGER_APPSKILLED);
				}
				try{
					DatabaseFunctions.closeSQLObjects(stmt, rs);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("getAllResourceManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
				}
				finally{
					stmt = null;
					rs = null;
				}
			}
//			appData.add(new Integer(appsSubmitted));
			appData.add(new Integer(appsRunning));
			appData.add(new Integer(appsPending));
			appData.add(new Integer(appsCompleted));
			appData.add(new Integer(appsKilled));
		}
		catch(Exception e){
			AppLogger.getLogger().fatal("getAllResourceManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
		}
		finally{
			try{
				DatabaseFunctions.closeSQLObjects(stmt, rs);
			}
			catch(Exception e){
				AppLogger.getLogger().fatal("getAllResourceManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
			}
		}
		return appData;
	}
	
	
	public static ArrayList getAllNodeManagerAppDetails(Connection connection, ArrayList rmIDs)
	{
		ArrayList appData = new ArrayList();
		int containersLaunched = 0;
		int containersCompleted = 0;
		int containersFailed = 0;
		int containersKilled = 0;
		int containersRunning = 0;
		int containersIniting = 0;
		

		Statement stmt = null;
		ResultSet rs = null; 
		try{
			for(Object nodeID:rmIDs){
				stmt = DatabaseFunctions.getStatement(connection);
				AppLogger.getLogger().fatal(QueryIOConstants.MONITORDATA_TABLE_PREFIX + (String)nodeID + "_NODEMANAGER_SUMMARYDATA");
				
				
				String query = QueryConstants.QRY_GET_NM_CONTAINER_DETAIL + QueryIOConstants.MONITORDATA_TABLE_PREFIX + (String)nodeID + "_NODEMANAGER_SUMMARYDATA"; 
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				
				while(rs.next()){
					containersLaunched += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSLAUNCHED);
					containersCompleted += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSCOMPLETED);
					containersFailed += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSFAILED);
					containersKilled += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSKILLED);
					containersRunning += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSRUNNING);
					containersIniting += rs.getInt(ColumnConstants.COL_NODE_MANAGER_CONTAINERSINITING);
				}
				try{
					DatabaseFunctions.closeSQLObjects(stmt, rs);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("getAllNodeManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
				}
				finally{
					stmt = null;
					rs = null;
				}
			}
			appData.add(new Integer(containersLaunched));
			appData.add(new Integer(containersCompleted));
			appData.add(new Integer(containersFailed));
			appData.add(new Integer(containersKilled));
			appData.add(new Integer(containersRunning));
			appData.add(new Integer(containersIniting));
		}
		catch(Exception e){
			AppLogger.getLogger().fatal("getAllNodeManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
		}
		finally{
			try{
				DatabaseFunctions.closeSQLObjects(stmt, rs);
			}
			catch(Exception e){
				AppLogger.getLogger().fatal("getAllNodeManagerAppDetails() caught Exception, Reason: "+e.getMessage(),e);
			}
		}
		return appData;
	}
	
	public static SummaryTable getNameNodeSummaryDataBasedOnList(Connection connection, ArrayList namenodes) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		SummaryTable summaryTable = new SummaryTable();
		Statement stmt = null;
		Statement hostDatastmt = null;
		ResultSet rs = null;
		ResultSet hostDataRS = null;
		String query = null;
		
		try
		{
			
			hostDatastmt = DatabaseFunctions.getStatement(connection);
			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			
			colNames.add("NameNode");
			colNames.add("Host");
			colNames.add("Type");
			colNames.add("JVM Heap");
			colNames.add("File Reads");
			colNames.add("File Writes");
			colNames.add("Alerts");
			colNames.add("CPU Usage");
			colNames.add("RAM");
			colNames.add("N/W Rcvd");
			colNames.add("N/W Sent");
			colNames.add("Disk Read");
			colNames.add("Disk Write");
			colNames.add("Safemode");
			
			colNames.add("Logs");
			
			colNames.add("Status");
			colNames.add("Monitoring");
			colNames.add("NodeId");
			
			summaryTable.setColNames(colNames);
			
			Object key = null;
			String type = null;
			
			for(int i=0; i<namenodes.size(); i++)
			{
				stmt = DatabaseFunctions.getStatement(connection);
				Node node = ((Node)namenodes.get(i));
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_NAMENODE_SUMMARYDATA";
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				query = QueryConstants.QRY_SELECT_ALL_FROM + QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_HOST_SUMMARYDATA";
				hostDataRS = DatabaseFunctions.getQueryResultsForStatement(hostDatastmt, query);
				
				colValues = new ArrayList();	
				
				colValues.add(node.getId()); // Host Name				
				colValues.add(host.getHostIP());	
				colValues.add(HAStatusDAO.isHANode(connection, node.getId())?(HAStatusDAO.getStandbyNodeId(connection, node.getId()) == null?QueryIOConstants.STANDBY:QueryIOConstants.ACTIVE):QueryIOConstants.NONHA); // HA Status 
				
				if(rs.next() && hostDataRS.next())
				{
					try	{	colValues.add(getFormattedStorageSize(rs.getFloat(ColumnConstants.COL_NN_SUMMARYDATA_JVMHEAPUSED)*QueryIOConstants.ONE_MB)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(rs.getObject(ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE)); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					colValues.add(getNameNodeAlertCount(connection, ((Node)namenodes.get(i)).getId())); // Alerts
					
					try	{	colValues.add(nf.format(hostDataRS.getFloat(ColumnConstants.COL_CPUUSAGE)) + " %"); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedStorageSize(((hostDataRS.getLong(ColumnConstants.COL_RAMTOTAL) - hostDataRS.getLong(ColumnConstants.COL_RAMFREE))*QueryIOConstants.ONE_MB))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_RECDBYTESPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_NW_SENTBYTESPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_DSK_BYTESREADPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	colValues.add(getFormattedRate(hostDataRS.getFloat(ColumnConstants.COL_DSK_BYTESWRITEPERSEC))); }
					catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
					
					try	{	
						String safeMode = rs.getString(ColumnConstants.COL_NN_SUMMARYDATA_SAFEMODESTATUS);
						if(safeMode == null || safeMode.length() == 0)
							colValues.add("Off");		
						else
							colValues.add(safeMode.indexOf("ON") != -1 ? "On" : "Off");
					}catch(Exception e){ colValues.add("N/A"); AppLogger.getLogger().fatal(e.getMessage(), e); }
				}
				
				colValues.add("<a href='"+constructOutLink(node, host)+"' target=\"_blank\">stdout/stderr</a><br><a href='"+constructLogLink(node, host)+"' target=\"_blank\">full log file</a>");
				
				colValues.add(node.getStatus());
				colValues.add(node.isMonitor() ? QueryIOConstants.STATUS_STARTED : QueryIOConstants.STATUS_STOPPED);
				colValues.add(node.getId());	
				
				summaryTable.addRow(colValues);
				try{
					DatabaseFunctions.closeSQLObjects(stmt, rs);
					DatabaseFunctions.closeSQLObjects(hostDatastmt, hostDataRS);
					stmt = null;
					rs = null;
					hostDatastmt = null;
					hostDataRS = null;
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
				}
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			
			try {
			DatabaseFunctions.closeStatement(stmt);
			} catch(Exception e){
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}

			try {
			DatabaseFunctions.closeResultSet(hostDataRS);
			} catch(Exception e){
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			
			try {
			DatabaseFunctions.closeStatement(hostDatastmt);
			} catch(Exception e){
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		return summaryTable;
	}
	
	
	public static ArrayList getNameNodeSummaryDataBasedOnNNList(final Connection connection, ArrayList namenodes) throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		ArrayList list = new ArrayList();
		Statement stmt = null; 
		ResultSet rs = null;
		Node namenode = null;

		long totalFileReads = 0;
		long totalFileWrites = 0;
		long totalFileAppends = 0;
		long totalFileRenames = 0;
		long totalListFileOps = 0;
		long totalDeleteFileOps = 0;
		
		long totalBytesRead = 0;
		long totalBytesWritten = 0;
		long totalBlocksRead = 0;
		long totalBlocksWritten = 0;
		
		try
		{
			for(int var = 0 ; var < namenodes.size(); var++)
			{
				namenode = (Node) namenodes.get(var);
				
				try{ totalFileReads += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESREAD); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileWrites += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESWRITE); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileAppends += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESAPPENDED); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalFileRenames += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_SUMMARYDATA_TOTALFILESRENAMED); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalListFileOps += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_MONITORDATA_LISTOPS); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
				
				try{ totalDeleteFileOps += getNameNodeCurrentValueForColumn(connection, namenode.getId(), ColumnConstants.COL_NN_MONITORDATA_DELETEFILEOPS); }
				catch(Exception e){ AppLogger.getLogger().fatal(e.getMessage(), e); }
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
				AppLogger.getLogger().fatal("Result set could not be closed, Exception: " + e.getMessage(), e);
			}
			try
			{
				DatabaseFunctions.closeStatement(stmt);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Statement could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		list.add(new Parameter("Total File Reads", totalFileReads));
		list.add(new Parameter("Total File Writes", totalFileWrites));
		list.add(new Parameter("Total File Appended", totalFileAppends));
		list.add(new Parameter("Total File Renamed", totalFileRenames));
//		list.add(new Parameter("Total List File Ops", totalListFileOps));
//		list.add(new Parameter("Total Delete File Ops", totalDeleteFileOps));
		return list;
	}
	
	
	public static ArrayList getNameNodeReadWritesBasedOnNNList(Connection connection, ArrayList namenodes) throws Exception 
	{
		long fileReads = 0;
		long fileWrites = 0;
		
		ArrayList hostRW = null;
			
		if(namenodes!=null)
		{
			for(int i=0; i<namenodes.size(); i++)
			{
				hostRW = getNameNodeReadWrites(connection, ((Node)namenodes.get(i)).getId());
				if(hostRW!=null)
				{
					fileReads += (Long)hostRW.get(0);
					fileWrites += (Long)hostRW.get(1);
				}
			}
		}
			
		ArrayList list = new ArrayList();
		list.add(fileReads);
		list.add(fileWrites);
		
		return list;
	}
}