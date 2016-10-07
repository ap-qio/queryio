package com.queryio.core.consolidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;

public class Consolidator {
	
	
	/** Connection variable */
	private Connection connection;
	
	
	/**
	 * Constructor, initializes connection and currentTimestamp Objects.
	 *
	 * @param connection the connection
	 * @param bNodeStatusCondolidator 
	 * @param controllerId controller id of the controller
	 */
	Consolidator(Connection connection) throws Exception
	{
		this.connection = connection;
	}
	
	public void consolidate(Timestamp currentTimestamp, Timestamp oldestEntryTimestamp, String id, String type, boolean consolidateNodeStatus) throws Exception{
		oldestEntryTimestamp = ConsolidationUtility.truncateUptoMinute(oldestEntryTimestamp);
		Timestamp timestamp = ConsolidationUtility.getTimestampAfterOneHour(oldestEntryTimestamp);
		while(!timestamp.after(currentTimestamp)){
			consolidate(timestamp, id, type, consolidateNodeStatus);
			timestamp = ConsolidationUtility.getTimestampAfterOneHour(timestamp);
		}
	}
	
	private int indexOf(String[] list, String value)
	{
		for(int i=0; i<list.length; i++)
		{
			if(list[i].equals(value))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * handle consolidation in case of RENGEMONITORING consolidationType.
	 *
	 * @param currentTimestamp current timestamp
	 * @throws Exception the exception
	 */
	public void consolidate(Timestamp currentTimestamp, String id, String Type, boolean consolidateNodeStatusData) throws Exception{
		consolidateMonitorData(currentTimestamp, id, Type);
		
		if(consolidateNodeStatusData)
			consolidateNodeStatusData(currentTimestamp, id);

		updateLastConsoldateTable(currentTimestamp, id);
	}
	private void consolidateNodeStatusData(Timestamp currentTimestamp, String nodeId) throws Exception{
		PreparedStatement selectPS = null;
		PreparedStatement insertPS = null;
		ResultSet rs = null;		
		
		StringBuffer selectSb = new StringBuffer("SELECT ");
		selectSb.append(" MAX(");			
		selectSb.append(ColumnConstants.COL_NODESTATUS_STATUS);
		selectSb.append(")");		
		selectSb.append(" FROM ");
		selectSb.append(TableConstants.TABLE_NODESTATUS);
		selectSb.append(" WHERE ");
		selectSb.append(ColumnConstants.COL_NODESTATUS_TIME);
		selectSb.append(" >= ? AND ");
		selectSb.append(ColumnConstants.COL_NODESTATUS_TIME);
		selectSb.append(" < ? AND ");		
		selectSb.append(ColumnConstants.COL_NODESTATUS_NODEID);
		selectSb.append(" = ? ");
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(selectSb.toString());
		
		
		StringBuffer insertSb = new StringBuffer("INSERT INTO ");
		insertSb.append(TableConstants.TABLE_NODESTATUS_CONSOLIDATEDDATA);
		insertSb.append(" (");
		insertSb.append(ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_ID);					
		insertSb.append(", ");
		insertSb.append(ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_STATUS);					
		insertSb.append(", ");
		insertSb.append(ColumnConstants.COL_NODESTATUS_CONSOLIDATEDDATA_TIME);		
		insertSb.append(") VALUES (?,?,?)");
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(insertSb.toString());
		
		try{
			selectPS = this.connection.prepareStatement(selectSb.toString());
			insertPS = this.connection.prepareStatement(insertSb.toString());
			
			selectPS.setLong (1, ConsolidationUtility.getTimestampBeforeHour(currentTimestamp).getTime());
			selectPS.setLong (2, currentTimestamp.getTime());
			selectPS.setString(3, nodeId);
			
			rs = selectPS.executeQuery();
			if(rs.next()){
				insertPS.setString(1, nodeId);				
				insertPS.setInt(2, rs.getInt(1));								
				insertPS.setLong(3, currentTimestamp.getTime());
				insertPS.execute();
			}			
		}
		finally{
			DatabaseFunctions.closePreparedStatement(selectPS);
			DatabaseFunctions.closePreparedStatement(insertPS);
			DatabaseFunctions.closeResultSet(rs);
		}		
	}
	
	private void consolidateMonitorData(Timestamp currentTimestamp, String id, String type) throws Exception{
		PreparedStatement selectPS = null;
		PreparedStatement insertPS = null;
		ResultSet rs = null;
		
		String monitoringTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_" + type + "_MONITORDATA";
		String consoldationTableName = QueryIOConstants.MONITORDATA_TABLE_PREFIX + id + "_" + type + "_CONSOLIDATEDDATA";
		
		String[] nodeAttributes = ConsolidationUtility.getNodeConsolidationAttributes(this.connection, monitoringTableName);
		
		StringBuffer selectSb = new StringBuffer("SELECT ");
		for(int i = 0; i < nodeAttributes.length; i ++)
		{
			if(i != 0)	selectSb.append(", ");
			
			if(nodeAttributes[i].endsWith("STATUS")){
				selectSb.append(" MIN(");
			}
			else if(indexOf(ColumnConstants.PERSIST_COLUMN_NAMES, nodeAttributes[i])!=-1){	
				selectSb.append(" SUM(");
			}else{
				selectSb.append(" AVG(");
			}
			
			selectSb.append(nodeAttributes[i]);
			selectSb.append(")");
		}
		selectSb.append(" FROM ");
		selectSb.append(monitoringTableName);
		selectSb.append(" WHERE ");
		selectSb.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		selectSb.append(" >= ? AND ");
		selectSb.append(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
		selectSb.append(" < ? ");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(selectSb.toString());
		
		
		StringBuffer insertSb = new StringBuffer("INSERT INTO ");
		insertSb.append(consoldationTableName);
		insertSb.append(" (");
		for(int i = 0; i < nodeAttributes.length; i ++){
			if(i != 0)
				insertSb.append(", ");				
			insertSb.append(nodeAttributes[i]);		
		}
		insertSb.append(") VALUES (");
		for(int i = 0; i < nodeAttributes.length; i++){
			insertSb.append("?");
			if (i != (nodeAttributes.length-1))
				insertSb.append(", ");
		}
		insertSb.append(")");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(insertSb.toString());
		
		try{
			selectPS = this.connection.prepareStatement(selectSb.toString());
			insertPS = this.connection.prepareStatement(insertSb.toString());
			
			selectPS.setTimestamp (1, ConsolidationUtility.getTimestampBeforeHour(currentTimestamp));
			selectPS.setTimestamp (2, currentTimestamp);
			rs = selectPS.executeQuery();
			if(rs.next()){
				insertPS.setObject(1, currentTimestamp);
				for(int i = 2; i <= nodeAttributes.length ; i ++ ){					
					insertPS.setObject(i, rs.getObject(i));
				}				
				insertPS.execute();
			}			
		} catch (Exception e) {
			AppLogger.getLogger().error("Exception selectSb.toString() - " + selectSb.toString());
			AppLogger.getLogger().error("Exception insertSb.toString() - " + insertSb.toString());
			throw e;
		}
 		finally{
			DatabaseFunctions.closePreparedStatement(selectPS);
			DatabaseFunctions.closePreparedStatement(insertPS);
			DatabaseFunctions.closeResultSet(rs);
		}		
	}
	
	/**
	 * Update last consolidate Timestamp in LAST_CONSOLDIATE table.
	 *
	 * @param currentTimestamp current timestamp
	 * @throws Exception the exception
	 */
	private void updateLastConsoldateTable(Timestamp currentTimestamp, String id) throws Exception
	{
		String deleteQuery = "DELETE FROM " + TableConstants.TABLE_LAST_CONSOLIDATE_LOG + " WHERE " + 
		ColumnConstants.COL_LAST_CONSOLIDATE_LOG_ID + " = ? ";

		
		String insertQuery = "INSERT INTO " + TableConstants.TABLE_LAST_CONSOLIDATE_LOG + " VALUES (?,?)";
		
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try{
			ps = this.connection.prepareStatement(deleteQuery);
			
			ps.setString(1, id);			
			ps.execute();
			
			ps2 = this.connection.prepareStatement(insertQuery);
			ps2.setString(1, id);
			ps2.setTimestamp(2, currentTimestamp);
			ps2.execute();
		}finally{
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closePreparedStatement(ps2);
		}
	}
	/**
	 * Update last consolidate Timestamp in LAST_RANGEMONITORING_CONSOLDIATE table.
	 *
	 * @param currentTimestamp current timestamp
	 * @throws Exception the exception
	 */	
}
