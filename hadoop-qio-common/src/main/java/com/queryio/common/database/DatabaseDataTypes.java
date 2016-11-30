/*
 * @(#)  DatabaseDataTypes.java
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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;

import com.queryio.common.util.DataTypes;
import com.queryio.common.util.IntHashMap;

/**
 * This class maintains the dataType mapping for the database.
 * 
 * @author Exceed Consultancy Services
 */
public class DatabaseDataTypes {
	public static final int BOOLEAN = 16;
	static public final String DATATYPE_STRING_REPR = "VARCHAR";

	private static HashMap hmDatabaseSqlTypes = new HashMap(3);

	static {
		IntHashMap hm;

		// sql data types of HSSQL
		hm = new IntHashMap();
		hm.put(Types.INTEGER, "INTEGER");
		hm.put(Types.BIGINT, "BIGINT");
		hm.put(Types.REAL, "FLOAT");
		hm.put(Types.FLOAT, "FLOAT");
		hm.put(Types.DOUBLE, "DOUBLE");
		hm.put(Types.NUMERIC, "NUMERIC");
		hm.put(Types.DECIMAL, "NUMERIC");
		hm.put(Types.DATE, "DATE");
		hm.put(Types.TIME, "TIME");
		hm.put(Types.TIMESTAMP, "DATETIME");
		hm.put(Types.TINYINT, "TINYINT");
		hm.put(Types.VARCHAR, "VARCHAR");
		hm.put(Types.LONGVARCHAR, "TEXT");
		hm.put(DatabaseDataTypes.BOOLEAN, "BOOLEAN");
		hmDatabaseSqlTypes.put(DatabaseConstants.DB_HSQL, hm);

		// sql data types of MySQL
		hm = new IntHashMap();
		hm.put(Types.INTEGER, "INTEGER");
		hm.put(Types.BIGINT, "BIGINT");
		hm.put(Types.REAL, "FLOAT");
		hm.put(Types.FLOAT, "FLOAT");
		hm.put(Types.DOUBLE, "DOUBLE");
		hm.put(Types.NUMERIC, "NUMERIC");
		hm.put(Types.DECIMAL, "NUMERIC");
		hm.put(Types.DATE, "DATE");
		hm.put(Types.TIME, "TIME");
		hm.put(Types.TIMESTAMP, "DATETIME");
		hm.put(Types.TINYINT, "TINYINT");
		hm.put(Types.VARCHAR, "VARCHAR");
		hm.put(Types.LONGVARCHAR, "TEXT");
		hm.put(DatabaseDataTypes.BOOLEAN, "NUMERIC");
		hmDatabaseSqlTypes.put(DatabaseConstants.DB_MYSQL, hm);

		// sql data types of Oracle
		hm = new IntHashMap();
		hm.put(Types.INTEGER, "INTEGER");
		hm.put(Types.BIGINT, "NUMBER");
		hm.put(Types.REAL, "FLOAT");
		hm.put(Types.FLOAT, "FLOAT");
		hm.put(Types.DOUBLE, "DOUBLE");
		hm.put(Types.NUMERIC, "NUMBER");
		hm.put(Types.DECIMAL, "NUMBER");
		hm.put(Types.TINYINT, "NUMBER");
		hm.put(Types.BIT, "NUMBER");
		hm.put(Types.DATE, "DATE");
		hm.put(Types.TIME, "DATE");
		hm.put(Types.TIMESTAMP, "DATE");
		hm.put(Types.VARCHAR, "VARCHAR");
		hm.put(Types.LONGVARCHAR, "LONG");
		hm.put(DatabaseDataTypes.BOOLEAN, "NUMERIC");
		hmDatabaseSqlTypes.put(DatabaseConstants.DB_ORACLE, hm);
	}

	/**
	 * This method returns the String representation for the database for a
	 * given datatype.
	 * 
	 * @param dataTypeRequired
	 * @param maxLength
	 * @return
	 */
	public static String getDataTypeRepresentation(final String dataTypeRequired, final int maxLength,
			final String databaseName) {
		String dataRepr = null;
		final IntHashMap hm = (IntHashMap) hmDatabaseSqlTypes.get(databaseName);
		if (hm != null) {
			final int key = DataTypes.resolveDataType(dataTypeRequired);
			final int transform = getTypeforDataType(key);
			dataRepr = (String) hm.get(transform);
		}

		String sDataRepresentation = null;

		if (dataRepr != null) {
			final StringBuffer sbDataRepresentation = new StringBuffer(dataRepr);

			// if the string dataType then set the max length specified for the
			// VarChar
			if (dataTypeRequired.equals(DataTypes.getStringRepresentation((DataTypes.STRING)))) {
				sbDataRepresentation.append('(');
				sbDataRepresentation.append(maxLength);
				sbDataRepresentation.append(')');
			}
			sDataRepresentation = sbDataRepresentation.toString();
		} else {
			sDataRepresentation = DATATYPE_STRING_REPR + "(64)";
		}

		return sDataRepresentation;
	}

	/**
	 * This method resolves the SQL data type. JAVA returns us the data type in
	 * the form of integer. This integer is mapped to the corresponding string
	 * using java.sql.Types
	 * 
	 * @param dataType
	 * @return
	 */
	public static String resolveSQLDataType(final int dataType, final String databaseName) {
		String sqlDataType = "";
		final IntHashMap hm = (IntHashMap) hmDatabaseSqlTypes.get(databaseName);
		if (hm != null) {
			sqlDataType = (String) hm.get(dataType);
		}
		return sqlDataType;
		/*
		 * switch(dataType) { case Types.INTEGER: { return "INTEGER"; } case
		 * Types.BIGINT: { return "BIGINT"; } case Types.REAL: case Types.FLOAT:
		 * { return "FLOAT"; } case Types.DOUBLE: { return "DOUBLE"; } case
		 * Types.NUMERIC: case Types.DECIMAL: { return "NUMERIC"; } case
		 * Types.DATE: { return "DATE"; } case Types.TIME: { return "TIME"; }
		 * case Types.TIMESTAMP: { return "DATETIME"; } case Types.TINYINT: {
		 * return "TINYINT"; } case Types.VARCHAR: { return "VARCHAR"; }
		 * default: { // In case of VARCHAR(400) it is returning data type = -1
		 * // Hence as a temporary fix VARCHAR is returned // Needs to be
		 * confirmed with other databases works fine in mysql return "TEXT"; } }
		 */
	}

	/**
	 * This method resolves the SQL data type. JAVA returns us the data type in
	 * the form of integer. This integer is mapped to the corresponding string
	 * using java.sql.Types
	 * 
	 * @param dataType
	 * @return
	 */
	public static Class resolveSQLDataTypeToClass(final int dataType) {
		switch (dataType) {
		case Types.INTEGER: {
			return Integer.class;
		}
		case Types.BIGINT: {
			return Long.class;
		}
		case Types.REAL:
		case Types.FLOAT: {
			return Float.class;
		}
		case Types.DOUBLE: {
			return Double.class;
		}
		case Types.DATE: {
			return Date.class;
		}
		case Types.TIME: {
			return Time.class;
		}
		case Types.TIMESTAMP: {
			return Timestamp.class;
		}
		case DatabaseDataTypes.BOOLEAN: {
			return Boolean.class;
		}
		case Types.VARCHAR:
		default: {
			return String.class;
		}
		}
	}

	/**
	 * This method resolves the MC data type to the appropriate class.
	 * 
	 * @param dataType
	 * @return
	 */
	public static Class resolveMCDataTypeToClass(final int dataType) {
		switch (dataType) {
		case DataTypes.INTEGER: {
			return Integer.class;
		}
		case DataTypes.LONG:
		case DataTypes.BIGINTEGER: {
			return Long.class;
		}
		case DataTypes.FLOAT: {
			return Float.class;
		}
		case DataTypes.DOUBLE: {
			return Double.class;
		}
		case DataTypes.BOOLEAN: {
			return Boolean.class;
		}
		case DataTypes.STRING:
		default: {
			return String.class;
		}
		}
	}

	private static int getTypeforDataType(final int dataType) {
		switch (dataType) {
		case DataTypes.INTEGER:
			return Types.INTEGER;
		case DataTypes.LONG:
			return Types.BIGINT;
		case DataTypes.FLOAT:
			return Types.FLOAT;
		case DataTypes.DOUBLE:
			return Types.DOUBLE;
		case DataTypes.STRING:
			return Types.VARCHAR;
		case DataTypes.BIGINTEGER:
			return Types.BIGINT;
		case DataTypes.BOOLEAN:
			return DatabaseDataTypes.BOOLEAN;
		case DataTypes.DATETIME:
			return Types.TIMESTAMP;
		default:
			return Types.VARCHAR;
		}
	}
}
