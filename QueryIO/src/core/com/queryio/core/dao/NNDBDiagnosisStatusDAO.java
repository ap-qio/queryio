package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DiagnosisStatusBean;

public class NNDBDiagnosisStatusDAO
{
	public static void addDiagnosisInfo(final Connection connection, String diagnosisId, String nameNodeId) throws Exception {
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_NN_DB_DIAGNOSIS_STATUS);
			
			ps.setString(1, diagnosisId);
			DatabaseFunctions.setDateTime(ps, 2, System.currentTimeMillis());
			ps.setString(3, nameNodeId);
			ps.setBoolean(4, false);
			ps.setString(5, QueryIOConstants.PROCESS_STATUS_INPROGRESS);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	public static ArrayList getDiagnosisStatus(final Connection connection) throws Exception
	{
		ArrayList ar=new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
				
		try{
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.GET_ALL_DIAGNOSIS_STATUS);
			while(rs.next())
			{
				DiagnosisStatusBean bean = new DiagnosisStatusBean();
				bean.setDiagnosisId(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID));
				bean.setNamenodeId(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_NAMENODEID));
				bean.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_START_TIME));
				bean.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_END_TIME));
				bean.setStatus(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_STATUS));
				bean.setError(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ERROR));
				bean.setIsRepair(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ISREPAIR));
				ar.add(bean);
			}
		}
		finally{
			try
			{
				DatabaseFunctions.closeSQLObjects(statement, rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return ar;
		
	}
	
	public static DiagnosisStatusBean getDiagnosisStatus(final Connection connection, String diagnosisId) throws Exception
	{
		PreparedStatement statement = null;
		ResultSet rs = null;
				
		try{
			statement = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_DIAGNOSIS_STATUS_FOR_ID);
			statement.setString(1, diagnosisId);
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(statement);
			if(rs.next())
			{
				DiagnosisStatusBean bean = new DiagnosisStatusBean();
				bean.setDiagnosisId(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_DIAGNOSISID));
				bean.setNamenodeId(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_NAMENODEID));
				bean.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_START_TIME));
				bean.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_END_TIME));
				bean.setStatus(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_STATUS));
				bean.setError(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ERROR));
				bean.setIsRepair(rs.getString(ColumnConstants.COL_NN_DB_DIAGNOSIS_STATUS_ISREPAIR));
				return bean;
			}
		}
		finally{
			try
			{
				DatabaseFunctions.closeSQLObjects(statement, rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return null;
		
	}
	
	public static void updateDiagnosisInfo(final Connection connection, String diagnosisId, Timestamp endTime, String status, String error, boolean isRepair) throws Exception {
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_NN_DB_DIAGNOSIS_STATUS);
			
			ps.setTimestamp(1, endTime);
			ps.setString(2, status);
			ps.setString(3, error);		
			ps.setBoolean(4, isRepair);
			ps.setString(5, diagnosisId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void deleteDiagnosisInfo(final Connection connection, String diagnosisId) throws Exception {
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_NN_DB_DIAGNOSIS_STATUS);
			
			ps.setString(1, diagnosisId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
}