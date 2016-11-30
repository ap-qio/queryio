/*
 * @(#)  DBManager.java     1.0     Created on Sep 3, 2003
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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * This class is reponsible for getting and setting connection for DevSuite
 * products. This class contains the basic properties to get connected with the
 * Database. It has getConnection and closeConnection methods which should be
 * used by all devsuite products for getting and returning connection. As we are
 * having in memory tables I think we should be fine with having Max 3
 * connections.
 *
 * @author Exceed consultancy Services
 * @version 1.0 Sep 3, 2003
 */
public final class DBManager {
	protected static final Logger LOGGER = Logger.getLogger(DBManager.class);

	// for saving DB
	public static final String BIT = "BIT"; //$NON-NLS-1$
	public static final String BOOLEAN = "BOOLEAN"; //$NON-NLS-1$
	public static final String TRUE = "TRUE"; //$NON-NLS-1$
	public static final String BINARY = "BINARY"; //$NON-NLS-1$
	public static final String INTEGER = "INTEGER"; //$NON-NLS-1$
	public static final String VARCHAR = "VARCHAR"; //$NON-NLS-1$
	public static final String NUMERIC = "NUMERIC"; //$NON-NLS-1$
	public static final String TINY_INT = "TINYINT"; //$NON-NLS-1$
	public static final String TIME = "TIMESTAMP"; //$NON-NLS-1$
	public static final String FLOAT = "FLOAT"; //$NON-NLS-1$
	public static final String VARBINARY = "BLOB"; //$NON-NLS-1$
	public static final String DOUBLE = "DOUBLE"; //$NON-NLS-1$
	public static final String BIGINT = "BIGINT"; //$NON-NLS-1$
	public static final String OBJECT = "OBJECT"; //$NON-NLS-1$
	public static final String CHAR = "CHAR"; //$NON-NLS-1$
	public static final String CHARACTER = "CHARACTER"; //$NON-NLS-1$
	// public static final String CLOB = "CLOB"; //$NON-NLS-1$

	public static final char BO = '(';
	public static final char BC = ')';
	public static final char COMMA = ',';
	public static final String INSERT_INTO = " INSERT INTO "; //$NON-NLS-1$
	public static final String VALUES = " VALUES"; //$NON-NLS-1$
	public static final String QC = "?,"; //$NON-NLS-1$
	public final static String TABLE = "TABLE"; //$NON-NLS-1$

	/* Connection Properties */
	public static final int MAX_CONNECTION = 20;
	public static final byte STRATEGY = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
	public static final long MAX_WAIT = 1000;// in milliseconds
	public static final int MAX_IDLE_CONNECTION = 10;

	// public static final String ES_INMEMORY_DATABASE = "AppEnterpriseDB";
	// //$NON-NLS-1$
	public static final String ES_INMEMORY_DATABASE = "AppEnterpriseDB;DB_CLOSE_DELAY=-1;LOCK_MODE=0"; //$NON-NLS-1$
	public static final String ES_DATABASE_NAME = "AppEnterpriseDB"; //$NON-NLS-1$

	/**
	 * getConnection
	 *
	 * @return
	 * @throws SQLException
	 * @throws NullPointerException
	 * @return Connection
	 */
	public static Connection getConnection(final String connectionName) throws SQLException {
		return DatabaseManager.getConnection(connectionName);
	}

	/**
	 * closeConnection
	 *
	 * @param con
	 * @throws SQLException
	 * @return void
	 */
	public static void closeConnection(final Connection con, final String connectionName) {
		try {
			DatabaseManager.closeDbConnection(con);
		} catch (final SQLException e) {
			LOGGER.fatal(e.getMessage(), e); // $NON-NLS-1$
		}
	}

	/**
	 * getRowCount
	 *
	 * @param tableOrViewName
	 * @return int
	 */
	public static int getRowCount(final String connectionName, final String tableOrViewName) {
		final String sqlQuery = " SELECT count(*) from " + tableOrViewName; //$NON-NLS-1$
		Connection cConnection = null;
		int rowCount = 0;
		try {
			cConnection = DBManager.getConnection(connectionName);
			final Statement sStatement = cConnection.createStatement();
			final ResultSet rs = sStatement.executeQuery(sqlQuery);
			if (rs.next()) {
				rowCount = rs.getInt(1);
			}
			rs.close();
			sStatement.close();
		} catch (final SQLException e) {
			LOGGER.fatal(e.getMessage(), e); // $NON-NLS-1$
		} finally {
			DBManager.closeConnection(cConnection, connectionName);
		}
		return rowCount;
	}

	public static Driver getDriver(String jarPath, String driverClassName) throws Exception {
		File file = new File(jarPath);
		URL u = file.toURI().toURL();
		final URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver driver = (Driver) Class.forName(driverClassName, true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		return driver;
	}
}
