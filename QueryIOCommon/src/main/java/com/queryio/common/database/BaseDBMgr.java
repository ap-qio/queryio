/*
 * @(#)  BaseDBMgr.java     1.0     Created on Sep 4, 2003
 *
 * Copyright (C) 2002 - 2003 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.queryio.common.database.DBManager;
import com.queryio.common.database.DatabaseConstants;
import com.queryio.common.util.IntHashMap;



/**
 * This class contains DB Functions to clear tables, get primary key for the
 * table etc.
 *
 * @author Exceed consultancy Services
 * @version 1.0 Sep 4, 2003
 */
public abstract class BaseDBMgr
{
	protected static final transient String END_BRACKET = ")"; //$NON-NLS-1$
	public static final String TREND_TIMESTAMP_COLUMN_INSERT = ", " + DatabaseConstants.TREND_TIMESTAMP_COLUMN_NAME + " TIMESTAMP NOT NULL)"; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PRIMARY_KEY = "PRIMARY KEY"; //$NON-NLS-1$

	// public static ReportTableObject getTableColumns(String tableName, String
	// columns[],
	// Class klasses[])
	// {
	// final String headings[] = columns;
	// return getTableColumns(tableName, columns, headings, klasses, null);
	// }
	/**
	 * Method getTableColumns. Creates a ReportTableObject according to the
	 * parameters specified.
	 *
	 * @param tableName
	 *            The name of the table
	 * @param columns
	 *            A String array containing the names of the columns in the
	 *            table
	 * @param headings
	 *            A String array containing the headings for the columns in the
	 *            table
	 * @param klasses
	 *            A String array containing the classes of the columns in the
	 *            table
	 * @return ReportTableObject
	 */
	// public static ReportTableObject getTableColumns(String tableName, String
	// columns[],
	// String headings[], Class klasses[])
	// {
	// return getTableColumns(tableName, columns, headings, klasses, null);
	// }
	/**
	 * Method getTableColumns Creates a ReportTableObject according to the
	 * parameters specified.
	 *
	 * @param tableName
	 *            The name of the table
	 * @param columns
	 *            A String array containing the names of the columns in the
	 *            table
	 * @param headings
	 *            A String array containing the headings for the columns in the
	 *            table
	 * @param klasses
	 *            A String array containing the classes of the columns in the
	 *            table
	 * @param relations
	 *            A String array containing the names of the columns in the
	 *            table that participate in any relation
	 * @return ReportTableObject
	 */
	// public static ReportTableObject getTableColumns(String tableName, String
	// columns[],
	// String headings[], Class klasses[], String relations[])
	// {
	// ArrayList alColumns = new ArrayList();
	// for(int i = 0; i < columns.length; i++)
	// {
	// alColumns.add(new
	// ReportColumnObject(tableName,columns[i],headings[i],klasses[i]));
	// }
	// ReportTableObject rtoReportTableObject = new ReportTableObject(tableName,
	// alColumns);
	// if (relations != null)
	// {
	// for(int i = 0; i < relations.length; i++)
	// {
	// rtoReportTableObject.addRelationshipColumn(relations[i]);
	// }
	// }
	// return rtoReportTableObject;
	// }
	/**
	 * This method will return the list of all the TableNames in the DataBase in
	 * a String Array.
	 */
	public static String[] getTableNames(final String connectionName)
	{
		final String[] types = { DBManager.TABLE };
		final ArrayList alTableNames = new ArrayList();
		Connection oConnection = null;
		try
		{
			oConnection = DBManager.getConnection(connectionName);
			final DatabaseMetaData dbmd = oConnection.getMetaData();
			final ResultSet resultSetTableInfo = dbmd.getTables(null, null, "%", types); //$NON-NLS-1$

			while (resultSetTableInfo.next())
			{
				alTableNames.add(resultSetTableInfo.getString(3));
			}
		}
		catch (final Exception e)
		{
			
		}
		finally
		{
			if (oConnection != null)
			{
				DBManager.closeConnection(oConnection, connectionName);
			}
		}
		return (String[]) alTableNames.toArray(new String[alTableNames.size()]);
	}

	public static int[] getUniqueValues(final int[] values)
	{
		if (values != null)
		{
			final IntHashMap map = new IntHashMap(values.length);
			for (int i = 0; i < values.length; i++)
			{
				map.put(values[i], null);
			}
			return map.keys();
		}

		return null;
	}

}