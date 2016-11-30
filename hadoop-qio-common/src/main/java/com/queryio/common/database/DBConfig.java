/*
 * @(#)  DBConfig.java
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

/**
 * This class stores all the information required to connect to the data base
 * through the connection pool
 * 
 * @author Exceed Consultancy Services
 */
class DBConfig {
	/* db connection name for database */
	private final String connectionName;

	/* max DB connections in the pool */
	private final int maxDBConnections;

	/* max idle DB connections in the pool at one time */
	private final int maxDBIdleConnections;

	/*
	 * max time to wait for getting the DB connection from the pool in
	 * milliseconds
	 */
	private final long maxDBWaitTime;

	/* strategy for getting the DB connection */
	private final byte waitStrategyForDB;

	/* boolean for checking whether data is presently migrating */
	private boolean bDatabaseMigrating;

	DBConfig(final String name, final int maxConnections, final int maxIdleConnections, final long maxWaitTime,
			final byte waitStrategy) {
		this.connectionName = name;
		this.maxDBConnections = maxConnections;
		this.maxDBIdleConnections = maxIdleConnections;
		this.maxDBWaitTime = maxWaitTime;
		this.waitStrategyForDB = waitStrategy;
		this.bDatabaseMigrating = false;
	}

	/**
	 * method isBDatabaseMigrating
	 * 
	 * @return
	 */
	public boolean isBDatabaseMigrating() {
		return this.bDatabaseMigrating;
	}

	/**
	 * method getConnectionName
	 * 
	 * @return
	 */
	public String getConnectionName() {
		return this.connectionName;
	}

	/**
	 * method getIfBusyWaitStrategyForDB
	 * 
	 * @return
	 */
	public byte getWaitStrategyForDB() {
		return this.waitStrategyForDB;
	}

	/**
	 * method getMaxDBConnections
	 * 
	 * @return
	 */
	public int getMaxDBConnections() {
		return this.maxDBConnections;
	}

	/**
	 * method getMaxDBIdleConnections
	 * 
	 * @return
	 */
	public int getMaxDBIdleConnections() {
		return this.maxDBIdleConnections;
	}

	/**
	 * method getMaxDBWaitTime
	 * 
	 * @return
	 */
	public long getMaxDBWaitTime() {
		return this.maxDBWaitTime;
	}

	/**
	 * method setBDatabaseMigrating
	 * 
	 * @param b
	 */
	public void setBDatabaseMigrating(final boolean b) {
		this.bDatabaseMigrating = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbToString = new StringBuffer("DBConfig: ");

		sbToString.append(" ConnectionName:");
		sbToString.append(this.connectionName);
		sbToString.append(" MaxDBConnections:");
		sbToString.append(this.maxDBConnections);
		sbToString.append(" MaxDBIdleConnections:");
		sbToString.append(this.maxDBIdleConnections);
		sbToString.append(" MaxDBWaitTime:");
		sbToString.append(this.maxDBWaitTime);
		sbToString.append(" WaitStrategyForDB:");
		sbToString.append(this.waitStrategyForDB);
		sbToString.append(" DatabaseMigrating:");
		sbToString.append(this.bDatabaseMigrating);

		return sbToString.toString();
	}

}
