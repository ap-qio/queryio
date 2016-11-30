package com.queryio.job.definition;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;

public class JobDefinitionDAO {
	public static String getTableName(final Connection connection, String jobName) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
					+ ColumnConstants.COL_JOB_MAPPING_JOBNAME + "=?";
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, jobName);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(pst);
			if (rs.next()) {
				return rs.getString(ColumnConstants.COL_JOB_MAPPING_TABLENAME);
			}

		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return null;
	}

	public static String getJobName(final Connection connection, String tableName) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
					+ ColumnConstants.COL_JOB_MAPPING_TABLENAME + "=?";
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, tableName);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(pst);
			if (rs.next()) {
				return rs.getString(ColumnConstants.COL_JOB_MAPPING_JOBNAME);
			}

		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return null;
	}

	public static void addJobDefinition(final Connection connection, String jobName, String tableName)
			throws SQLException {
		PreparedStatement pst = null;
		try {
			String query = "INSERT INTO " + TableConstants.TABLE_JOB_MAPPING + " VALUES (?,?)";
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, tableName);
			pst.setString(2, jobName);
			DatabaseFunctions.executeUpdateStatement(pst);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteJobDefinition(final Connection connection, String jobName) throws SQLException {
		PreparedStatement pst = null;
		try {
			String query = "DELETE FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
					+ ColumnConstants.COL_JOB_MAPPING_JOBNAME + "=?";
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, jobName);
			DatabaseFunctions.executeUpdateStatement(pst);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteJobDefinitionFromList(final Connection connection, String adHocIds) throws Exception {
		Statement stmt = null;
		String deleteQuery = null;

		try {
			stmt = DatabaseFunctions.getStatement(connection);
			deleteQuery = "DELETE FROM " + TableConstants.TABLE_JOB_MAPPING + " WHERE "
					+ ColumnConstants.COL_JOB_MAPPING_JOBNAME + " IN (" + adHocIds + ")";

			CoreDBManager.executeUpdateStatement(connection, stmt, deleteQuery);
		} finally {
			DatabaseFunctions.closeStatement(stmt);
		}
	}
	//
	// public static void createJobTable(final Connection connection, String
	// tableName, ArrayList<ColumnMetadata> columnMetaData, String createStmt)
	// throws SQLException{
	// String[] colNames = new String[columnMetaData.size()];
	// String[] colTypes = new String[columnMetaData.size()];
	//
	// for(int i=0; i<columnMetaData.size(); i++){
	// colNames[i] = columnMetaData.get(i).getColumnName();
	// colTypes[i] = columnMetaData.get(i).getColumnSqlDataType();
	// }
	//
	// createDatabaseTable(connection, tableName, colNames, colTypes,
	// createStmt);
	// }

	public static void createDatabaseTable(Connection connection, String tableName, String[] colNames,
			String[] datatypes, String createStmt) throws SQLException {
		StringBuffer createStatement = new StringBuffer(createStmt).append(" ");
		createStatement.append(tableName).append(" (");
		for (int i = 0; i < colNames.length; i++) {
			if (i != 0)
				createStatement.append(", ");
			createStatement.append(colNames[i]).append(" ").append(datatypes[i]);
		}
		createStatement.append(")");

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Job Table DDL: " + createStatement.toString());

		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, createStatement.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void createResultTable(final Connection connection, String resultTableName, String tableName,
			String createStmt) throws SQLException {

		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String> colTypes = new ArrayList<String>();

		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			rs = dbmd.getColumns(null, null, tableName.toUpperCase(), "%");
			// Table name pattern should be in case for HSQL
			while (rs.next()) {
				colNames.add(rs.getString("COLUMN_NAME"));
				String type = rs.getString("TYPE_NAME");
				if ("VARCHAR".equalsIgnoreCase(type)) {
					int size = rs.getInt("COLUMN_SIZE");
					colTypes.add(type + "(" + size + ")");
				} else
					colTypes.add(type);
			}

			// We can remove this we are not planning to use any other database
			// in future
			if (colNames.size() == 0) {
				rs1 = dbmd.getColumns(null, null, tableName, "%");
				// For other dbs (if support is added in future)
				while (rs.next()) {
					colNames.add(rs.getString("COLUMN_NAME"));
					String type = rs.getString("TYPE_NAME");
					if ("VARCHAR".equalsIgnoreCase(type)) {
						int size = rs.getInt("COLUMN_SIZE");
						colTypes.add(type + "(" + size + ")");
					} else
						colTypes.add(type);
				}
			}

			createDatabaseTable(connection, resultTableName, colNames.toArray(new String[colNames.size()]),
					colTypes.toArray(new String[colTypes.size()]), createStmt);
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeResultSet(rs1);
		}
	}
}