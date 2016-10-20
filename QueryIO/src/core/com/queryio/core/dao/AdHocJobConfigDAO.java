package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.AdHocJobConfig;
import com.queryio.userdefinedtags.common.DatabaseFunctions;

public class AdHocJobConfigDAO {
	//CREATE TABLE PUBLIC.ADHOCJOBCONFIG(NAMENODEID VARCHAR(255),RMID VARCHAR(255),JOBNAME VARCHAR(255),JARFILE VARCHAR(1024)
	//,LIBJARS VARCHAR(10240),FILES VARCHAR(10240),CLASSNAME VARCHAR(1024),SOURCEPATH VARCHAR(1024),PATHPATTERN VARCHAR(1024)
	//,ARGUMENTS VARCHAR(10240))
	public static void insert(Connection connection, AdHocJobConfig jobConfig) throws SQLException{
		String query = "INSERT INTO " + TableConstants.TABLE_ADHOCJOBCONFIG 
				+ "(" + ColumnConstants.COL_ADHOCJOBCONFIG_NAMENODEID +"," + ColumnConstants.COL_ADHOCJOBCONFIG_RMID
				+ "," + ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME +"," + ColumnConstants.COL_ADHOCJOBCONFIG_JARFILE
				+ "," + ColumnConstants.COL_ADHOCJOBCONFIG_LIBJARS+"," + ColumnConstants.COL_ADHOCJOBCONFIG_FILES
				+ "," + ColumnConstants.COL_ADHOCJOBCONFIG_CLASSNAME +"," + ColumnConstants.COL_ADHOCJOBCONFIG_SOURCEPATH
				+ "," + ColumnConstants.COL_ADHOCJOBCONFIG_PATHPATTERN +"," + ColumnConstants.COL_ADHOCJOBCONFIG_ARGUMENTS
				+ ")VALUES(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pst = null;
		try{
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			int i = 1;
			pst.setString(i++, jobConfig.getNamenodeId());
			pst.setString(i++, jobConfig.getRmId());
			pst.setString(i++, jobConfig.getJobName());
			pst.setString(i++, jobConfig.getJarFile());
			pst.setString(i++, jobConfig.getLibjars());
			pst.setString(i++, jobConfig.getFiles());
			pst.setString(i++, jobConfig.getClassName());
			pst.setString(i++, jobConfig.getSourcePath());
			pst.setString(i++, jobConfig.getPathPattern());
			pst.setString(i++, jobConfig.getArguments());
			DatabaseFunctions.executeUpdateStatement(pst);
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
		}
 	}
	
	public static AdHocJobConfig get(Connection connection, String jobName) throws SQLException{
		String query = "SELECT * FROM  " + TableConstants.TABLE_ADHOCJOBCONFIG 
				+ " WHERE " + ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME+" = '" + jobName + "'";
		Statement st = null;
		ResultSet rs = null;
		try{
			st = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(st, query);
			if(rs.next()){
				return new AdHocJobConfig(rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_NAMENODEID),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_RMID),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_JARFILE),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_LIBJARS),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_FILES),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_CLASSNAME),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_SOURCEPATH),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_PATHPATTERN),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_ARGUMENTS));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return null;
 	}
	public static List<AdHocJobConfig> getAll(Connection connection) throws SQLException {
		List<AdHocJobConfig> list = new ArrayList<AdHocJobConfig>();

		String query = "SELECT * FROM " + TableConstants.TABLE_ADHOCJOBCONFIG;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(new AdHocJobConfig(rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_NAMENODEID),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_RMID),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_JARFILE),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_LIBJARS),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_FILES),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_CLASSNAME),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_SOURCEPATH),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_PATHPATTERN),
						rs.getString(ColumnConstants.COL_ADHOCJOBCONFIG_ARGUMENTS)));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return list;
	}
	public static void delete(Connection connection, String jobName) throws SQLException{
		String query = "DELETE FROM " + TableConstants.TABLE_ADHOCJOBCONFIG 
				+ " WHERE " + ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME + " = '" + jobName + "'";
		Statement st = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			st.executeUpdate(query);
		} finally {
			DatabaseFunctions.closeStatement(st);
		}
	}

	public static void updateJob(Connection connection,
			AdHocJobConfig jobConfig)  throws Exception{
		String query = "UPDATE " + TableConstants.TABLE_ADHOCJOBCONFIG + " SET " + 
				ColumnConstants.COL_ADHOCJOBCONFIG_NAMENODEID +" = ?, " + ColumnConstants.COL_ADHOCJOBCONFIG_RMID + " = ?, " + 
				ColumnConstants.COL_ADHOCJOBCONFIG_CLASSNAME +" = ?, " + ColumnConstants.COL_ADHOCJOBCONFIG_SOURCEPATH + " = ?, " + 
				ColumnConstants.COL_ADHOCJOBCONFIG_PATHPATTERN +" = ?, " + ColumnConstants.COL_ADHOCJOBCONFIG_ARGUMENTS + " = ? " +
				"WHERE " + ColumnConstants.COL_ADHOCJOBCONFIG_JOBNAME + " = ?";
		PreparedStatement pst = null;
		try{
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			int i = 1;
			pst.setString(i++, jobConfig.getNamenodeId());
			pst.setString(i++, jobConfig.getRmId());
			pst.setString(i++, jobConfig.getClassName());
			pst.setString(i++, jobConfig.getSourcePath());
			pst.setString(i++, jobConfig.getPathPattern());
			pst.setString(i++, jobConfig.getArguments());
			pst.setString(i++, jobConfig.getJobName());
			DatabaseFunctions.executeUpdateStatement(pst);
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static String getUpdatedArguments(Connection connection,
			String jobName, String whereArgs, String resultTableName)
			throws SQLException {

		String arguments = "";
		AdHocJobConfig jobConfig = get(connection, jobName);
		if (jobConfig != null) {
				arguments = jobName + " " + jobConfig.getClassName() + " " + jobConfig.getSourcePath() + " " + jobConfig.getPathPattern() + " " + resultTableName + " ";

				JSONObject finalArguments = new JSONObject();
				finalArguments.put(QueryIOConstants.ADHOC_CONSTANT_ARGUMENTS, jobConfig.getArguments());

				arguments = arguments.concat(finalArguments.toJSONString());
				
				if (whereArgs != null && whereArgs.length() > 0)
					arguments = arguments.concat(" '[").concat(whereArgs)
							.concat("]'");
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("updated arguments: " + arguments);
		}

		return arguments;
	}

}
