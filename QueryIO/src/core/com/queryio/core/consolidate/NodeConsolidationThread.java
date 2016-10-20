package com.queryio.core.consolidate;

import java.sql.Connection;
import java.sql.Timestamp;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Node;

public class NodeConsolidationThread extends Thread 
{
	private Node node;
	
	
	boolean flag;
	
	public NodeConsolidationThread(Node node)
	{
		this.node = node;
	}
	
	public void interrupt(){
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("INTERRUPTING THREAD FOR CONTROLLER " + node.getId() + "_" + node.getNodeType());
		flag = false;
		super.interrupt();
	}
	public void run() {
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("STARTING THREAD FOR CONTROLLER " + node.getId() + "_" +  node.getNodeType());
		flag = true;
		Connection connection = null;	
		Timestamp currentTimestamp = null;
		Timestamp currentTimestampLowerBoundary = null;
		Timestamp nextConsolidateTimestamp = null;
		Timestamp lastConsolidateTimestamp = null;
		while (flag) {					
			// current System Time
			currentTimestamp = new Timestamp(System.currentTimeMillis());
	
			// lower boundary of current System Time
			currentTimestampLowerBoundary = ConsolidationUtility.truncateUptoMinute(currentTimestamp);
	
			// timestamp of next consolidation to be performed, exactly after 1 hour from lower boundary of current System Time
			nextConsolidateTimestamp = ConsolidationUtility.getTimestampAfterOneHour(currentTimestampLowerBoundary);		
			
			if(flag)
			{
				// fetching last consolidate timestamp				
				try
				{
					connection = CoreDBManager.getQueryIODBConnection();
					
					
					Consolidator consolidator = new Consolidator(connection);
					
					lastConsolidateTimestamp = ConsolidationUtility.getLastConsolidateTimestamp(connection, node.getId());
					if(lastConsolidateTimestamp == null)
					{
						Timestamp oldestEntryTimestamp = ConsolidationUtility.getOldestEntryTimestampForNode(connection, node.getId(),  node.getNodeType());
						if(oldestEntryTimestamp != null)
						{
							consolidator.consolidate(currentTimestampLowerBoundary, oldestEntryTimestamp, node.getId(), node.getNodeType(), true);
						}						
					}
					else
					{
						if(!lastConsolidateTimestamp.after(currentTimestampLowerBoundary))
							consolidator.consolidate(currentTimestampLowerBoundary, lastConsolidateTimestamp, node.getId(), node.getNodeType(), true);
					}
				
				}
				catch (Exception e) 
				{
					flag = false;
					AppLogger.getLogger().error(e.getMessage(), e);
				}
				finally 
				{
					try 
					{
						CoreDBManager.closeConnection(connection);
					}
					catch (Exception e) 
					{
						flag = false;
						AppLogger.getLogger().error(e.getMessage(), e);
					}
				}	
			}
			
			// purging older data
			if(flag){
				try{
					String monitoringTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_" +node.getNodeType() + "_MONITORDATA";
					String consoldationTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + node.getId() + "_" +node.getNodeType()+ "_CONSOLIDATEDDATA";				
					
					connection = CoreDBManager.getQueryIODBConnection();		
					ConsolidationUtility.purgeTable(connection, monitoringTableName, ColumnConstants.COL_MONITORDATA_MONITOR_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORDATATABLE));
					ConsolidationUtility.purgeTable(connection, consoldationTableName, ColumnConstants.COL_MONITORDATA_MONITOR_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORCONSOLIDATEDDATATABLE));
					
					ConsolidationUtility.purgeTable(connection, TableConstants.TABLE_NODESTATUS, ColumnConstants.COL_NODESTATUS_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORDATATABLE), ColumnConstants.COL_NODESTATUS_NODEID, node.getId());
					ConsolidationUtility.purgeTable(connection, TableConstants.TABLE_NODESTATUS_CONSOLIDATEDDATA, ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORCONSOLIDATEDDATATABLE), ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_ID, node.getId());
				} catch (Exception e) {
					flag = false;
				} finally {
					try {
						CoreDBManager.closeConnection(connection);
					} catch (Exception e) {
						flag = false;
						AppLogger.getLogger().error(e.getMessage(), e);
					}
				}
			}
						
						
			if(flag)
			{				
				currentTimestamp = new Timestamp(System.currentTimeMillis());
				if(nextConsolidateTimestamp.after(currentTimestamp))
				{
					try 
					{
						Thread.sleep(nextConsolidateTimestamp.getTime()	- currentTimestamp.getTime());
					}
					catch (InterruptedException e) 
					{
						flag = false;
					}	
				}
			}			
		}			
	}
}
