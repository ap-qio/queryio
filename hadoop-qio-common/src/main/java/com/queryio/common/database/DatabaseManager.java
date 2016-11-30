/*
 * @(#)  DatabaseManager.java
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
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.SQLNestedException;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * The Database Manager class is the interface to the database. It allows for
 * getting connections, releasing connections It maintains a connection pool
 * internally and provides connections from the same.
 * 
 * @author Exceed Consultancy Services
 */
public final class DatabaseManager {
	private static HashMap hmPoolingDataSource;
	private static HashMap hmPools;

	private static final String USER = "user"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	protected static final Logger LOGGER = Logger.getLogger(DatabaseManager.class);

	private DatabaseManager() {
		// Do nothing
	}

	/**
	 * This method is used when we want to load the driver class by
	 * URLClassloader. It takes Driver object as a parameter.
	 * 
	 * @param connectionName
	 * @param maxConnection
	 * @param strategy
	 * @param maxWait
	 * @param idleConnection
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public static void setInitialProperties(final String connectionName, final int maxConnection, final byte strategy,
			final long maxWait, final int idleConnection, final Driver driver, final String url, final String userName,
			final String password, boolean evictIdleConnections) throws IllegalStateException {
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("Connection URL: "+url);
		final Properties p = new Properties();
		p.put(USER, userName);
		p.put(PASSWORD, password);
		final ConnectionFactory connectionFactory = new DriverConnectionFactory(driver, url, p);
		setInitialProperties(connectionFactory, connectionName, maxConnection, strategy, maxWait, idleConnection,
				evictIdleConnections);
	}

	/**
	 * This methods is used when we want to use the default classloader to load
	 * the driver class. We pass the driver class name as parameter and load the
	 * class using the default class loader.
	 * 
	 * setInitialProperties
	 * 
	 * @param connectionName
	 * @param maxConnection
	 * @param strategy
	 * @param maxWait
	 * @param idleConnection
	 * @param driverClass
	 * @param url
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public static void setInitialProperties(final String connectionName, final int maxConnection, final byte strategy,
			final long maxWait, final int idleConnection, final String driverClass, final String url,
			final String userName, final String password, boolean evictIdleConnections)
			throws IllegalStateException, ClassNotFoundException {

		Class.forName(driverClass);
		final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, userName, password);
		setInitialProperties(connectionFactory, connectionName, maxConnection, strategy, maxWait, idleConnection,
				evictIdleConnections);
	}

	/**
	 * 
	 * @param connectionFactory
	 * @param connectionName
	 * @param maxConnection
	 * @param strategy
	 * @param maxWait
	 * @param idleConnection
	 * @throws Exception
	 */
	private static void setInitialProperties(final ConnectionFactory connectionFactory, final String connectionName,
			final int maxConnection, final byte strategy, final long maxWait, final int idleConnection,
			boolean evictIdleConnections) throws IllegalStateException {
		final GenericObjectPool objPool = new GenericObjectPool(null, maxConnection, strategy, maxWait, idleConnection);
		if (evictIdleConnections) {
			objPool.setTestOnBorrow(true);
			objPool.setTestWhileIdle(true);
			objPool.setMinEvictableIdleTimeMillis(5000);
		}
		final PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
				objPool, null, null, false, true);
		if (evictIdleConnections) {
			poolableConnectionFactory.setValidationQuery("SELECT 1");
		}
		objPool.setFactory(poolableConnectionFactory);
		if (hmPoolingDataSource == null) {
			hmPoolingDataSource = new HashMap(2);
			hmPools = new HashMap(2);
		}
		hmPools.put(connectionName, objPool);
		hmPoolingDataSource.put(connectionName, new PoolingDataSource(objPool));
	}

	/**
	 * 
	 * @param connectionName
	 */
	public static void removeConnectionPool(String connectionName) throws Exception {
		GenericObjectPool objPool = (GenericObjectPool) hmPools.remove(connectionName);
		hmPoolingDataSource.remove(connectionName);
		objPool.close();
	}

	public static Connection getConnectionOnStartup(final String connectionName) throws SQLException {
		Connection connection = null;
		try {
			connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
		} catch (SQLNestedException sqne) {
			if (sqne.getCause() != null && sqne.getCause() instanceof NoSuchElementException) {
				/* final GenericObjectPool gop = (GenericObjectPool) */hmPools.get(connectionName);
			}
		}
		return connection;
	}

	/**
	 * getConnection
	 * 
	 * @param connectionName
	 * @return
	 * @throws SQLException
	 * @throws NullPointerException
	 */
	public static Connection getConnection(final String connectionName) throws SQLException {
		long maxWait = 0;

		GenericObjectPool objPool = (GenericObjectPool) hmPools.get(connectionName);
		if (objPool != null) {
			maxWait = objPool.getMaxWait();
		}

		// get the current time stamp
		long now = System.currentTimeMillis();
		// see if there is one available immediately
		Connection connection = null;

		while (true) {
			try {
				connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
				break;
			} catch (Exception sqne) {
				if (sqne instanceof SQLNestedException) {
					if (sqne.getCause() != null && sqne.getCause() instanceof NoSuchElementException) {
						/* final GenericObjectPool gop = (GenericObjectPool) */hmPools.get(connectionName);
					}
				} else {
					if (maxWait == 0 && connection == null) { // no wait, return
																// one if we
																// have one
						throw new SQLException("NoWait: Unable to fetch a connection.", sqne);
					}
					// we didn't get a connection, lets see if we timed out
					if (connection == null) {
						if ((System.currentTimeMillis() - now) >= maxWait) {
							throw new SQLException(
									"Unable to fetch a connection. Wait for " + (maxWait / 1000) + " seconds.", sqne);
						} else {
							// no timeout, lets try again
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							continue;
						}
					}
				}
			}
		} // while

		if (connection == null) {
			try {
				connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
			} catch (Exception e) {
				throw new SQLException("Unable to fetch a connection.", e);
			}
		}
		return connection;
	}

	/**
	 * Closes the connection to the database pool.
	 * 
	 * @param con
	 *            connection to be closed
	 */
	public static void closeDbConnection(final Connection con) throws SQLException {
		// long hc = -1;
		if ((con != null) && !con.isClosed()) {
			// hc = con.hashCode();
			con.close();
		}
		// GenericObjectPool gop =
		// (GenericObjectPool)hmPools.get(EnvironmentalConstants.getQueryIODatabasePoolName());
		// if (gop != null)
		// {
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("close, Database: activedb Active: " +
		// gop.getNumActive() + " Idle: " + gop.getNumIdle() + " closed
		// connection: " + hc);
		// }
		/*
		 * gop = (GenericObjectPool)hmPools.get("archivedb"); if (gop != null) {
		 * AppLogger.getLogger().info("close, Database: archivedb Active: " +
		 * gop.getNumActive() + " Idle: " + gop.getNumIdle()); }
		 */
	}
}
