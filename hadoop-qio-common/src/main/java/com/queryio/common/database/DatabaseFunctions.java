/*
 * @(#)  DatabaseFunctions.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.queryio.common.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * This class holds all the static function related to Database processing.
 * 
 * @author Exceed Consultancy Services
 */
public final class DatabaseFunctions
{
	/**
	 * @see java.lang.Object#Object()
	 */
	private DatabaseFunctions()
	{
		// Private Constructor provided
	}

	/**
	 * Creates a Statement Object
	 * 
	 * @param dbConn
	 *            Connection object
	 * @return Statement Statement Object
	 */
	public final static Statement getStatement(final Connection dbConn) throws SQLException
	{
		return dbConn.createStatement();
	}
	
	
	/**
	 * Creates a PreparedStatement Object
	 * 
	 * @param dbConn
	 *            Connection object
	 * @param query
	 *            Query
	 * @return PreparedStatement Prepared Statement for a query
	 */
	public final static PreparedStatement getPreparedStatementWithReturnKeys(final Connection dbConn, final String query)
			throws SQLException
	{
		//return dbConn.prepareStatement(query);
		return dbConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}

	/**
	 * Creates a PreparedStatement Object
	 * 
	 * @param dbConn
	 *            Connection object
	 * @param query
	 *            Query
	 * @return PreparedStatement Prepared Statement for a query
	 */
	public final static PreparedStatement getPreparedStatement(final Connection dbConn, final String query)
			throws SQLException
	{
		return dbConn.prepareStatement(query);
//		return dbConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}

	/**
	 * Executes the Query and returns the ResulSetObject
	 * 
	 * @param stmt
	 *            Statement object
	 * @param query
	 *            Query
	 * @return ResultSet ResultSet after executing the query
	 */
	public final static ResultSet getQueryResultsForStatement(final Statement stmt, final String query)
			throws SQLException
	{
		return stmt.executeQuery(query);
	}

	/**
	 * Executes the Query and returns the ResulSetObject
	 * 
	 * @param prpStatement
	 *            PreparedStatement object
	 * @return ResultSet ResultSet after executing the query
	 */
	public final static ResultSet getQueryResultsForPreparedStatement(final PreparedStatement prpStatement)
			throws SQLException
	{
		return prpStatement.executeQuery();
	}

	/**
	 * Closes the Statement Object
	 * 
	 * @param stmt
	 *            Statement object
	 */
	public final static void closeStatement(final Statement stmt) throws SQLException
	{
		if (stmt != null)
		{
			stmt.close();
		}
	}

	/**
	 * Closes the PreparedStatement Object
	 * 
	 * @param prpStatement
	 *            PreparedStatement object
	 */
	public final static void closePreparedStatement(final PreparedStatement prpStatement) throws SQLException
	{
		if (prpStatement != null)
		{
			prpStatement.close();
		}
	}

	/**
	 * Closes the ResultSet Object
	 * 
	 */
	public final static void closeResultSet(final ResultSet rs) throws SQLException
	{
		if (rs != null)
		{
			rs.close();
		}
	}

	/**
	 * Method executeUpdateStatement.
	 * 
	 * @param statement
	 * @param query
	 * @return int
	 * @throws SQLException
	 */
	public final static int executeUpdateStatement(final Statement statement, final String query) throws SQLException
	{
		return statement.executeUpdate(query);
	}
	
	public final static boolean executeStatement(final Statement statement, final String query) throws SQLException
	{
		return statement.execute(query);
	}

	/**
	 * Method executeUpdateStatement.
	 * 
	 * @param statement
	 * @return int
	 * @throws SQLException
	 */
	public final static int executeUpdateStatement(final PreparedStatement statement) throws SQLException
	{
		return statement.executeUpdate();
	}

	/**
	 * Method closeSQLObjects.
	 * 
	 * @param statement
	 * @param resultSet
	 * @throws SQLException
	 */
	public static void closeSQLObjects(final Statement statement, final ResultSet resultSet) throws SQLException
	{
		try
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
		}
		finally
		{
			if (statement != null)
			{
				statement.close();
			}
		}
	}

	/**
	 * @param statement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	public static void setBoolean(final PreparedStatement statement, final int index, final boolean value)
			throws SQLException
	{
		statement.setInt(index, (value ? DatabaseConstants.COL_VALUE_TRUE : DatabaseConstants.COL_VALUE_FALSE));
	}

	/**
	 * @param resultSet
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public static boolean getBoolean(final ResultSet resultSet, final int index) throws SQLException
	{
		return (resultSet.getInt(index) == DatabaseConstants.COL_VALUE_TRUE ? true : false);
	}

	/**
	 * @param resultSet
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static boolean getBoolean(final ResultSet resultSet, final String column) throws SQLException
	{
		return (resultSet.getInt(column) == DatabaseConstants.COL_VALUE_TRUE ? true : false);
	}

	/**
	 * @param statement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	/*
	 * public static void setTime(PreparedStatement statement, int index, long
	 * value) throws SQLException { Time time = null; if (value != -1) { time =
	 * new Time(value); } statement.setTime(index, time); }
	 */
	/**
	 * @param resultSet
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	/*
	 * public static long getTime(ResultSet resultSet, int index) throws
	 * SQLException { Time time = resultSet.getTime(index); return time == null ?
	 * -1:time.getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET); }
	 */
	/**
	 * @param resultSet
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	/*
	 * public static long getTime(ResultSet resultSet, String column) throws
	 * SQLException { Time time = resultSet.getTime(column); return time == null ?
	 * -1:time.getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET); }
	 */
	/**
	 * @param statement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	public static void setDateTime(final PreparedStatement statement, final int index, final long value)
			throws SQLException
	{
		Timestamp timeStamp = null;
		if (value != -1)
		{
			timeStamp = new Timestamp(value);
		}
		statement.setTimestamp(index, timeStamp);
	}

	/**
	 * @param resultSet
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public static long getDateTime(final ResultSet resultSet, final int index) throws SQLException
	{
		final Date date = resultSet.getDate(index);
		return date == null ? -1 : date.getTime() + resultSet.getTime(index).getTime()
				+ Calendar.getInstance().get(Calendar.ZONE_OFFSET);
	}

	/**
	 * @param resultSet
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static long getDateTime(final ResultSet resultSet, final String column) throws SQLException
	{
		final Date date = resultSet.getDate(column);
		return date == null ? -1 : date.getTime() + resultSet.getTime(column).getTime()
				+ Calendar.getInstance().get(Calendar.ZONE_OFFSET);
	}

}
