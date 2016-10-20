package com.queryio.common.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.SQLNestedException;
import org.apache.commons.pool.impl.GenericObjectPool;
import com.queryio.common.database.DBManager;

public class TestPostgreSQL
{
	private static HashMap hmPoolingDataSource;
	private static HashMap hmPools;
	
	private static final String USER = "user"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$
	private static final String LOGIN_TIMEOUT = "loginTimeout"; //$NON-NLS-1$
	
	public static void main(String[] args)
	{
		String userName = "ADMIN";
		String password = "ADMIN";
		String connectionName = "test";
		int maxConnection = 10;
		byte strategy = GenericObjectPool.WHEN_EXHAUSTED_GROW;
		long maxWait = 2000;		// in milliseconds
		long loginTimeout = 60;		// in seconds
		int idleConnection = 10;
		boolean evictIdleConnections = false;
		String driverPath = "/Users/prasoon/QueryIO/tomcat/webapps/queryio/jdbcJars/postgresql-9.2-1002.jdbc4.jar";
		String driverClass = "org.postgresql.Driver";
		String url = "jdbc:postgresql://192.168.0.11:5432/queryio?loginTimeout=" + loginTimeout + "";
				
		Driver driver = null;
		try {
			driver = DBManager.getDriver(driverPath, driverClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final Properties p = new Properties();
		p.put(USER, userName);
		p.put(PASSWORD, password);
		
		final ConnectionFactory connectionFactory = new DriverConnectionFactory(driver, url, p);
		setInitialProperties(connectionFactory, connectionName, maxConnection, strategy, maxWait, idleConnection, evictIdleConnections);
		
		int i=0;
		while (i < 100)
		{
			try {
				if (getConnectionManual(connectionName) == null)
				{
					throw new SQLException("Connection is null.");
				}
				System.out.println("Get Connection: " + (i+1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
	
	private static void setInitialProperties(final ConnectionFactory connectionFactory, final String connectionName,
			final int maxConnection, final byte strategy, final long maxWait, final int idleConnection, boolean evictIdleConnections)
			throws IllegalStateException
	{
		final GenericObjectPool objPool = new GenericObjectPool(null, maxConnection, strategy, maxWait, idleConnection);
		if(evictIdleConnections){
			objPool.setTestOnBorrow(true);
			objPool.setTestWhileIdle(true);
			objPool.setMinEvictableIdleTimeMillis(5000);
		}
		final PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
				objPool, null, null, false, true);		
		if(evictIdleConnections){
			poolableConnectionFactory.setValidationQuery("SELECT 1");
		}
		objPool.setFactory(poolableConnectionFactory);
		if (hmPoolingDataSource == null)
		{
			hmPoolingDataSource = new HashMap(2);
			hmPools = new HashMap(2);
		}
		hmPools.put(connectionName, objPool);
		hmPoolingDataSource.put(connectionName, new PoolingDataSource(objPool));
	}
	
	public static Connection getConnection(final String connectionName) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
		}
		catch (SQLNestedException sqne)
		{
			if (sqne.getCause() != null && sqne.getCause() instanceof NoSuchElementException)
			{
				/*final GenericObjectPool gop = (GenericObjectPool)*/hmPools.get(connectionName);
			}
		}
		return connection;
	}
	
	public static Connection getConnectionManual(final String connectionName) throws SQLException
	{
		long maxWait = 0;
		
		GenericObjectPool objPool = (GenericObjectPool)hmPools.get(connectionName);
		if (objPool != null)
		{
			maxWait = objPool.getMaxWait();
		}
		
		//get the current time stamp
		long now = System.currentTimeMillis();
		//see if there is one available immediately
		Connection connection = null;
		
		while (true)
		{
			try
			{
				connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
				break;
			}
			catch (Exception sqne)
			{
				if (sqne instanceof SQLNestedException)
				{
					if (sqne.getCause() != null && sqne.getCause() instanceof NoSuchElementException)
					{
						/*final GenericObjectPool gop = (GenericObjectPool)*/hmPools.get(connectionName);
					}
				}
				else
				{
					System.out.println("maxWait: " + maxWait + " waitFor: " + (System.currentTimeMillis() - now));
					if (maxWait == 0 && connection == null) { //no wait, return one if we have one
		                throw new SQLException("NoWait: Unable to fetch a connection.", sqne);
		            }
					//we didn't get a connection, lets see if we timed out
		            if (connection == null) {
		                if ((System.currentTimeMillis() - now) >= maxWait) {
		                    throw new SQLException("Unable to fetch a connection. Wait for " + (maxWait / 1000) +
		                        " seconds.", sqne);
		                } else {
		                    //no timeout, lets try again
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
        } //while
		
		if (connection == null)
		{
			try
			{
				connection = ((PoolingDataSource) hmPoolingDataSource.get(connectionName)).getConnection();
			}
			catch (Exception e)
			{
				throw new SQLException("Unable to fetch a connection.", e);
			}
        }
		return connection;
	}
}