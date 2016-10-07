package com.queryio.core.customtags;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.config.db.GenericDBConfigManager;

public class CustomTagsDAO {
	public static HashMap getAllAvailableTags(final Connection connection, String tableName, DBTypeProperties props) throws SQLException{
		HashMap tags = new HashMap();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + tableName + " LIMIT 0";

			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			ResultSetMetaData meta = rs.getMetaData();
			int numCol = meta.getColumnCount();
			String columnName;
			String dataType = "";
			for (int i = 1; i <= numCol; i++) {
				columnName = meta.getColumnName(i);
				dataType = GenericDBConfigManager.getGenericDataType(meta.getColumnTypeName(i), props);
				tags.put(columnName, dataType);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return tags;
	}
	
	public static JSONObject getAllAvailableTags(final Connection connection, ArrayList tableNames, DBTypeProperties props) throws SQLException{
		LinkedHashMap<String, Object> tags = new LinkedHashMap();
		JSONObject tagListObject = new JSONObject();
		JSONObject tableSchema = new JSONObject();
		Statement stmt = null;
		ResultSet rs = null;
		String tableName = null;
		
		for(int j=0; j<tableNames.size(); j++){
			
			tableName = ((String) tableNames.get(j)).toLowerCase();
			JSONArray columnList = new JSONArray();
			try {
				String query = "SELECT * FROM " + tableName + " LIMIT 0";

				stmt = DatabaseFunctions.getStatement(connection);
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				ResultSetMetaData meta = rs.getMetaData();
				int numCol = meta.getColumnCount();
				String columnName;
				String dataType = "";
				for (int i = 1; i <= numCol; i++) {
					columnName = meta.getColumnName(i).toLowerCase();
					if("blocks".equalsIgnoreCase(columnName))
						continue;
//					if(!columnName.equals(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE) && !columnName.equals(ColumnConstants.COL_TAG_VALUES_REPLICATION))
						dataType = GenericDBConfigManager.getGenericDataType(meta.getColumnTypeName(i), props);
						tags.put(columnName, dataType);
						columnList.add(columnName);
				}
			} finally {
				try {
					DatabaseFunctions.closeResultSet(rs);
				} catch (SQLException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				try {
					DatabaseFunctions.closeStatement(stmt);
				} catch (SQLException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			tableSchema.put(tableName, columnList);
		}
		
		tagListObject.put("columnMap", tags);
		tagListObject.put("tableSchema", tableSchema);
		return tagListObject;
	}
	
	public static Map<String, Boolean> getAllTagTableNames(final Connection connection, String nameNodeId) throws SQLException {
		Map<String, Boolean> tableMap = new TreeMap<String, Boolean>();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = null;
		ArrayList<String> tableNameList = new ArrayList<String>();
		try{
			rs = md.getTables(null, null, "%", new String[]{"TABLE"});
			while (rs.next()) {
				tableNameList.add(rs.getString(3).toLowerCase());
			}
			java.util.Collections.sort(tableNameList);
			for (String tableName : tableNameList) {
				if(tableName.equalsIgnoreCase(TableConstants.TABLE_LOOKUP) || tableName.equalsIgnoreCase(TableConstants.TABLE_DIRECTORIES)
						|| tableName.equalsIgnoreCase(TableConstants.TABLE_NS_METADATA) || excludeTableName(tableName) || tableName.startsWith("backup_"))
					continue;
				tableMap.put(tableName, BigQueryManager.isAdhocTable(tableName, nameNodeId));
			}
		} finally{
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return tableMap;
	}
	
	public static ArrayList<String> getAllTagTableNamesList(final Connection connection) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = null;
		try{
			rs = md.getTables(null, null, "%", new String[]{"TABLE"});
			while (rs.next()) {
				String tableName = rs.getString(3);
				if(tableName.equals(TableConstants.TABLE_LOOKUP) || tableName.equals(TableConstants.TABLE_DIRECTORIES)
						|| tableName.equalsIgnoreCase(TableConstants.TABLE_NS_METADATA) || excludeTableName(tableName))
					continue;
				
				list.add(tableName);
			}
			java.util.Collections.sort(list);
		} finally{
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return list;
	}
	
	private static boolean excludeTableName(String tableName)
	{
		for(int i = 0; i < TableConstants.TABLES_TO_BE_EXCLUDED.length; i++)
		{
			String excludedTable = TableConstants.TABLES_TO_BE_EXCLUDED[i];
			if(excludedTable.equalsIgnoreCase(tableName))
				return true;
		}
		return false;
	}
	
	public static ArrayList getAllTemporaryTableNames(final Connection connection) throws SQLException {
		ArrayList list = new ArrayList();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = null;
		try{
			rs = md.getTables(null, null, "APPLICATION_%", new String[]{"TABLE"});
			while (rs.next()) {
				list.add(rs.getString(3));  
			}
		} finally{
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return list;
	}

	public static void purgeTemporaryTables(Connection connection) throws SQLException {
		ArrayList tableNames = getAllTemporaryTableNames(connection);
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt=  DatabaseFunctions.getStatement(connection);
			
			String query = null;
			for(int i=0; i<tableNames.size(); i++){
				query = "DROP TABLE " + tableNames.get(i);
				DatabaseFunctions.executeStatement(stmt, query);
			}
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static TableModel getBigQueryResultTableModel(final Connection connection, String query, String tableName, int records) {
		DefaultTableModel tableModel = null;
		ArrayList colNames = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(records!=-1){
				query += " LIMIT " + records;
			}
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query: " + query);
			
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			colNames.add("File");
			for(int i=2; i<=rsmd.getColumnCount(); i++){
				colNames.add(StaticUtilities.toCamelCase(rsmd.getColumnName(i)));
			}
			
			String[] tableModelColumns = (String[]) colNames.toArray(new String[0]);
			
			tableModel = new DefaultTableModel(tableModelColumns, 0);
			
			Object[] object;
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
			while(rs.next())
			{
				int index=0;
				object = new Object[tableModelColumns.length];
				
				for(int i=1; i<=rsmd.getColumnCount(); i++)
				{
					if(rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_ACCESSTIME)||rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME)){
						object[index++]=dateFormatter.format(rs.getObject(i));
						continue;
					}
					object[index++] = rs.getObject(i).toString();
				}
				tableModel.addRow(object);
			}
		} catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return tableModel;
	}

	public static HashMap getResultSetColumns(final Connection connection, DBTypeProperties props, String query) throws SQLException {
		LinkedHashMap colMap = new LinkedHashMap();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = DatabaseFunctions.getStatement(connection);
			AppLogger.getLogger().fatal("** getResultSetColumns Query: " + query);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1; i<=rsmd.getColumnCount(); i++){				
				String columnName = rsmd.getColumnName(i);
//				if(columnName.equalsIgnoreCase(ColumnConstants.COL_TAG_VALUES_BLOCKS)) 
//					continue;
				colMap.put(columnName.toLowerCase(), rsmd.getColumnTypeName(i));
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return colMap;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap getResultSetColumnsHive(final Connection hiveConnection, DBTypeProperties props, String query, String nameNodeId) throws Exception {
		LinkedHashMap colMap = new LinkedHashMap();
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = DatabaseFunctions.getStatement(hiveConnection);
			AppLogger.getLogger().fatal("** getResultSetColumnsHive Query: " + query + " , NameNode " + nameNodeId);
			
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String columnName = rsmd.getColumnName(i);
				if(columnName.equalsIgnoreCase(QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME)) {
					columnName = ColumnConstants.COL_TAG_VALUES_FILEPATH;
				}
				columnName = columnName.substring(columnName.indexOf('.') + 1);
				colMap.put(columnName.toLowerCase(),
						rsmd.getColumnTypeName(i));
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return colMap;
	}
}