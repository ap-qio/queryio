package com.queryio.core.requestprocessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.Path;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.beans.SummaryTable;

public class ListSelectedFilesRequest extends RequestProcessorCore {
	SummaryTable filesList = new SummaryTable();
	Path path;
	int rows;
	int pageNo;
	String nodeId;
	String connectionPool = "";
	
	String query;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
	public ListSelectedFilesRequest(String user, String path, String nodeId, int rows, int pageNo, String query) throws Exception{
		super(user, null, new Path(path));
		this.nodeId = nodeId;
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			this.connectionPool = NodeDAO.getDBNameForNameNodeMapping(connection, nodeId);
		}finally{
			CoreDBManager.closeConnection(connection);
		}
		this.pageNo = pageNo;
		this.rows = rows;
		this.query = query;
	}
	
	public void process() throws Exception{
		Thread.currentThread().setName(this.user); /* CRUCIAL */
		
		ArrayList dirList = new ArrayList();
		ArrayList fileList = new ArrayList();
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Rcvd Query: " + query);
		
		int length = pageNo*rows;
		int offset = length==rows?0:length-rows-1;
		
		String nonLimitedQuery = query;
		
		query += " LIMIT " + offset + "," + length;

		String selectContent = query.substring(query.indexOf("SELECT ") + "SELECT ".length(), query.indexOf("FROM ")-1);
		if(selectContent.contains("*")){
			// No need to worry.
		} else {
			String[] cols = selectContent.split(",");
		
			StringBuffer sBuf = new StringBuffer();
			sBuf.append(ColumnConstants.COL_TAG_VALUES_FILEPATH);
			
			for(int i=0; i<cols.length; i++){
				cols[i] = cols[i].trim();
				
				if(!cols[i].equals("") && !cols[i].equals(ColumnConstants.COL_TAG_VALUES_FILEPATH)){
					sBuf.append("," + cols[i]);
				}
			}
			query = query.replace(selectContent, sBuf.toString());
		}

		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Executing Query: " + query);
		
		String tableName = query.substring(query.indexOf("FROM ") + "FROM ".length());
		tableName = tableName.trim();
		tableName = tableName.split(" ")[0];
		tableName = tableName.trim();
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Table Name: " + tableName);
		
		nonLimitedQuery = nonLimitedQuery.substring(0, nonLimitedQuery.indexOf("SELECT") + "SELECT".length() + 1) 
				+ " COUNT(*) " 
				+ nonLimitedQuery.substring(nonLimitedQuery.indexOf("FROM"));
	
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query for count: " + nonLimitedQuery);
		
		executeBigQuery(query, tableName, nonLimitedQuery);
		
		for(int i = 0; i < dirList.size(); i++){
			filesList.addRow((ArrayList)dirList.get(i));
		}
		for(int i = 0; i < fileList.size(); i++){
			filesList.addRow((ArrayList)fileList.get(i));
		}
	}
	
	public SummaryTable getFilesList() {
		return filesList;
	}
	
	public void executeBigQuery(String query, String tableName, String nonLimitedQuery) throws Exception{
		
		if(nonLimitedQuery.contains("ORDER BY")){
			nonLimitedQuery = nonLimitedQuery.substring(0, nonLimitedQuery.indexOf("ORDER BY"));
		}
		if(nonLimitedQuery.contains("GROUP BY")){
			nonLimitedQuery = nonLimitedQuery.substring(0, nonLimitedQuery.indexOf("GROUP BY"));
		}
		
		long totalCount = 0;
		ArrayList colNames = new ArrayList();
		ArrayList columnValues = new ArrayList();
		Connection connection = null;
		Statement stmt = null;
		ResultSet limitedRS = null;
		ResultSet nonLimitedRS = null;
		try{
			connection = RemoteManager.getCustomTagDBConnection(this.connectionPool);
			
			stmt = DatabaseFunctions.getStatement(connection);
			limitedRS = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			nonLimitedRS = DatabaseFunctions.getQueryResultsForStatement(stmt, nonLimitedQuery);
			
			if(nonLimitedRS.next()){
				totalCount = nonLimitedRS.getLong(1);
			}
			
			ResultSetMetaData rsmd = limitedRS.getMetaData();
			colNames.add("File");
			for(int i=2; i<=rsmd.getColumnCount(); i++){
				colNames.add(StaticUtilities.toCamelCase(rsmd.getColumnName(i)));
			}
			
			filesList.setColNames(colNames);
			
			while(limitedRS.next()){
				columnValues = new ArrayList();
				for(int i=1; i<=rsmd.getColumnCount(); i++){
					if(rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_ACCESSTIME)||rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME)){
						columnValues.add(dateFormatter.format(limitedRS.getObject(i)));
						continue;
					}
					
					columnValues.add(limitedRS.getObject(i).toString());
				}
			
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(colNames);
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(columnValues);
				filesList.addRow(columnValues);
			}
			
			filesList.setTotalRowCount(totalCount);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(limitedRS);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(nonLimitedRS);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
}