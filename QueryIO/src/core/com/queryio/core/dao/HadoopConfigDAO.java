package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.monitor.beans.SummaryTable;

public class HadoopConfigDAO {
	public static SummaryTable getAllHadoopConfigs(Connection connection) throws Exception{
		SummaryTable table = new SummaryTable();
		ArrayList columnNames = new ArrayList();
		columnNames.add("Type");
		columnNames.add("Key");
		columnNames.add("Value");
		columnNames.add("Description");
		Statement st = null;
		ResultSet rs = null;
		ArrayList data = new ArrayList();
		try{
			st = connection.createStatement();
			String query = "SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG; 
			rs = st.executeQuery(query);		
			
			HadoopConfig config = null;
			while(rs.next()){
				ArrayList list = new ArrayList();
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				list.add(config.getType());
				list.add(config.getKey());
				list.add("<input type=\"text\" id=\"" + config.getKey() +  "\" value=\"" + config.getValue() + "\">");
				list.add(config.getDescription());
				data.add(list);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		table.setColNames(columnNames);
		table.setRows(data);
		return table;		
	}
	
	public static SummaryTable getHAHadoopConfigs(Connection connection) throws Exception{
		SummaryTable table = new SummaryTable();
		ArrayList columnNames = new ArrayList();
//		columnNames.add("Type");
		columnNames.add("Key");
		columnNames.add("Value");
		columnNames.add("Description");
		Statement st = null;
		ResultSet rs = null;
		ArrayList data = new ArrayList();
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE " + ColumnConstants.COL_HADOOPCONFIG_TYPE 
					+ " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_HA + "'" + " ORDER BY " + ColumnConstants.COL_HADOOPCONFIG_TYPE + "," + ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY + " ASC");		
			
			HadoopConfig config = null;
			while(rs.next()){
				ArrayList list = new ArrayList();
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
			//	list.add(config.getType());
				list.add(config.getKey());
				list.add("<input type=\"text\" id=\"" + config.getKey() +  "\" value=\"" + config.getValue() + "\">");
				list.add(config.getDescription());
				data.add(list);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		table.setColNames(columnNames);
		table.setRows(data);
		return table;		
	}
	
	public static HadoopConfig getHadoopConfig(Connection connection, String key) throws Exception{
		HadoopConfig config = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY + " = '" +key + "'");

			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return config;		
	}
	
	public static ArrayList getAllHadoopConfigs(Connection connection, String configType) throws Exception{
		ArrayList result = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_TYPE + " = '" +configType + "'");
			HadoopConfig config = null;
			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.add(config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	public static HashMap getHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG);			
			while(rs.next()){				
				HadoopConfig config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	public static HashMap getAllDataNodeHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_TYPE 
					+ " IN ('" + QueryIOConstants.HADOOP_CONFIGTYPE_HA + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_HDFS + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_DATANODE + "')");
			HadoopConfig config = null;
			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	public static HashMap getAllNameNodeHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_TYPE 
					+ " IN ('" + QueryIOConstants.HADOOP_CONFIGTYPE_COMMON + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_HA + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_HDFS + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_NAMENODE+ "')");
			HadoopConfig config = null;
			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	public static HashMap getAllResourceManagerHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_TYPE 
					+ " IN ('" + QueryIOConstants.HADOOP_CONFIGTYPE_MAPREDUCE + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_RESOURCEMANAGER + "')");
			HadoopConfig config = null;
			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	public static HashMap getAllNodeManagerHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE "+ColumnConstants.COL_HADOOPCONFIG_TYPE 
					+ " IN ('" + QueryIOConstants.HADOOP_CONFIGTYPE_MAPREDUCE + "','" + QueryIOConstants.HADOOP_CONFIGTYPE_NODEMANAGER + "')");
			HadoopConfig config = null;
			while(rs.next()){
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;		
	}
	
	
	
	public static void updateHadoopConfigDefaultValue(Connection connection, ArrayList configKeys, ArrayList configValues) throws Exception{
		for(int i = 0; i < configKeys.size(); i ++){
			updateHadoopConfigDefaultValue(connection, (String) configKeys.get(i), (String) configValues.get(i));
		}
		
	}
	
	public static void updateHadoopConfigDefaultValue(Connection connection, String configkey, String configValue) throws Exception{
		
		Statement stmt = null;
		try{
			String query = "UPDATE " + TableConstants.TABLE_HADOOPCONFIG + " SET " + ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE +"='" + configValue + "' WHERE " + ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY + "='" + configkey + "'";
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
		}finally{
			DatabaseFunctions.closeStatement(stmt);
		}		
	}
	
	public static void addHadoopConfigValue(Connection connection, String type, String configkey, String configValue, String description) throws Exception{
		
		Statement stmt = null;
		try{
			String query = "INSERT INTO " + TableConstants.TABLE_HADOOPCONFIG
							+ " VALUES ('" + type + "', '" + configkey + "', '" + configValue + "', '" + description + "')";
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
		}finally{
			DatabaseFunctions.closeStatement(stmt);
		}		
	}
	public static void deleteHadoopConfigValue(Connection connection,String configkey) throws Exception{
		
		PreparedStatement pst = null;
		try{
			
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_CONFIG_KEY);
			pst.setString(1, configkey);
			pst.execute();
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
			
		}		
	}
	
	public static SummaryTable getAllHDFSHadoopConfigs(Connection connection) throws Exception{
		SummaryTable table = new SummaryTable();
		ArrayList columnNames = new ArrayList();
		columnNames.add("Type");
		columnNames.add("Key");
		columnNames.add("Value");
		columnNames.add("Description");
		Statement st = null;
		ResultSet rs = null;
		ArrayList data = new ArrayList();
		try{
			st = connection.createStatement();
			String query = "SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_HDFS + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_HA + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_DATANODE + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_NAMENODE + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_CHECKPOINTNODE+ "'";
			
			rs = st.executeQuery(query);		
			
			HadoopConfig config = null;
			while(rs.next()){
				ArrayList list = new ArrayList();
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				list.add(config.getType());
				list.add(config.getKey());
				list.add("<input type=\"text\" id=\"" + config.getKey() +  "\" value=\"" + config.getValue() + "\">");
				list.add(config.getDescription());
				data.add(list);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		table.setColNames(columnNames);
		table.setRows(data);
		return table;		
	}
	
	
	
	public static SummaryTable getAllMapReduceHadoopConfigs(Connection connection) throws Exception{
		SummaryTable table = new SummaryTable();
		ArrayList columnNames = new ArrayList();
		columnNames.add("Type");
		columnNames.add("Key");
		columnNames.add("Value");
		columnNames.add("Description");
		Statement st = null;
		ResultSet rs = null;
		ArrayList data = new ArrayList();
		try{
			st = connection.createStatement();
			String query = "SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_MAPREDUCE + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_RESOURCEMANAGER + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_NODEMANAGER + "'";
					
			rs = st.executeQuery(query);		
			
			HadoopConfig config = null;
			while(rs.next()){
				ArrayList list = new ArrayList();
				config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				list.add(config.getType());
				list.add(config.getKey());
				list.add("<input type=\"text\" id=\"" + config.getKey() +  "\" value=\"" + config.getValue() + "\">");
				list.add(config.getDescription());
				data.add(list);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		table.setColNames(columnNames);
		table.setRows(data);
		return table;		
	}
	public static HashMap getAllCheckpointHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE " 
					+ ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_CHECKPOINTNODE + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_HDFS + "'");			
			while(rs.next()){				
				HadoopConfig config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;	
	}
	
	public static HashMap getAllJournalNodeHadoopConfigs(Connection connection) throws Exception{
		HashMap result = new HashMap();
		Statement st = null;
		ResultSet rs = null;
		
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPCONFIG + " WHERE " 
					+ ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_JOURNALNODE + "'"
					+ " OR " + ColumnConstants.COL_HADOOPCONFIG_TYPE  + " = '" + QueryIOConstants.HADOOP_CONFIGTYPE_HDFS + "'");			
			while(rs.next()){				
				HadoopConfig config = new HadoopConfig();
				config.setType(rs.getString(ColumnConstants.COL_HADOOPCONFIG_TYPE));
				config.setKey(rs.getString(ColumnConstants.COL_HADOOPCONFIG_HADOOPKEY));
				config.setValue(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DEFAULT_VALUE));
				config.setDescription(rs.getString(ColumnConstants.COL_HADOOPCONFIG_DESCRIPTION));
				result.put(config.getKey(), config);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return result;	
	}
	
	
	
}
