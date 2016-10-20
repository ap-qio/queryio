package com.queryio.core.consolidate;

import java.sql.Connection;
import java.sql.Timestamp;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;

public class HostConsolidationThread extends Thread 
{
	private Host host;
	
	
	boolean flag;
	
	public HostConsolidationThread(Host host)
	{
		this.host = host;
	}
	
	public void interrupt(){
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("INTERRUPTING THREAD FOR CONTROLLER " + host.getId() + "_HOST");
		flag = false;
		super.interrupt();
	}
	public void run() {
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("STARTING THREAD FOR CONTROLLER " + host.getId() + "_HOST");
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
					
					lastConsolidateTimestamp = ConsolidationUtility.getLastConsolidateTimestamp(connection, String.valueOf(host.getId()));
					if(lastConsolidateTimestamp == null)
					{
						Timestamp oldestEntryTimestamp = ConsolidationUtility.getOldestEntryTimestampForNode(connection, String.valueOf(host.getId()),  "HOST");
						if(oldestEntryTimestamp != null)
						{
							consolidator.consolidate(currentTimestampLowerBoundary, oldestEntryTimestamp, String.valueOf(host.getId()),  "HOST", false);
						}						
					}
					else
					{
						if(!lastConsolidateTimestamp.after(currentTimestampLowerBoundary))
							consolidator.consolidate(currentTimestampLowerBoundary, lastConsolidateTimestamp, String.valueOf(host.getId()),  "HOST", false);
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
					String monitoringTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_" +"HOST_MONITORDATA";
					String consoldationTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + host.getId() + "_" +"HOST_CONSOLIDATEDDATA";				
					
					connection = CoreDBManager.getQueryIODBConnection();		
					ConsolidationUtility.purgeTable(connection, monitoringTableName, ColumnConstants.COL_MONITORDATA_MONITOR_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORDATATABLE));
					ConsolidationUtility.purgeTable(connection, consoldationTableName, ColumnConstants.COL_MONITORDATA_MONITOR_TIME, currentTimestampLowerBoundary, new Long(QueryIOConstants.PURGEINTERVALFORCONSOLIDATEDDATATABLE));					
					
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
