package com.queryio.core.notifier.notifiers.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import com.queryio.common.database.DBManager;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.INotifier;

public class DatabaseNotifier implements INotifier 
{
	private String driver = null;
	private String userName = null;
	private String password = null;
	private String jar = null;
	private String dbUrl = null;
	private String dbTable = null;
	private String dbColumn = null;

	public void initPropertySet(PropertySet propSet) throws Exception 
	{
		List llValues;
		
		llValues = propSet.getProperty(INotifierConstants.DB_DRIVER);
		if (llValues != null)
		{
			this.driver = ((String) llValues.get(0));
			if (driver == null || driver.trim().length() == 0)
			{
				throw new RuntimeException("Database Driver cannot be null"); //$NON-NLS-1$
			}
		}
		else
		{
			throw new RuntimeException("Database Driver not specified"); //$NON-NLS-1$
		}

		llValues = propSet.getProperty(INotifierConstants.DB_URL);
		if (llValues != null)
		{
			this.dbUrl = ((String) llValues.get(0));
			if (dbUrl == null || dbUrl.trim().length() == 0)
			{
				throw new RuntimeException("Database URL cannot be null"); //$NON-NLS-1$
			}
		}
		else
		{
			throw new RuntimeException("Database URL not specified"); //$NON-NLS-1$
		}
	
		llValues = propSet.getProperty(INotifierConstants.DB_USER);
		if (llValues != null)
		{
			this.userName = ((String) llValues.get(0));
		}
		llValues = propSet.getProperty(INotifierConstants.DB_PWD);
		if (llValues != null)
		{
			this.password = ((String) llValues.get(0));
		}
		
		llValues = propSet.getProperty(INotifierConstants.DB_JAR);
		if (llValues != null)
		{
			this.jar = ((String) llValues.get(0));
		}

		llValues = propSet.getProperty(INotifierConstants.DB_TABLE);
		if (llValues != null)
		{
			this.dbTable = ((String) llValues.get(0));
			if (dbTable == null || dbTable.trim().length() == 0)
			{
				throw new RuntimeException("Database Table cannot be null"); //$NON-NLS-1$
			}
		}
		else
		{
			throw new RuntimeException("Database Table not specified"); //$NON-NLS-1$
		}
		llValues = propSet.getProperty(INotifierConstants.DB_COLUMN);
		if (llValues != null)
		{
			this.dbColumn = ((String) llValues.get(0));
			if (dbColumn == null || dbColumn.trim().length() == 0)
			{
				throw new RuntimeException("Database Column cannot be null"); //$NON-NLS-1$
			}
		}
		else
		{
			throw new RuntimeException("Database Column not specified"); //$NON-NLS-1$
		}
	}

	public String notifyEvent(NotificationEvent event) throws Exception 
	{
		final String message = (String) event.getProperty(INotifierConstants.ALERT_MESSAGE);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		buffer.append(new Date());
		buffer.append("] ");
		buffer.append(StaticUtilities.replaceAll(message, PlatformHandler.LINE_SEPARATOR, " "));
		
		// Write to database here
		Driver driverInstance = DBManager.getDriver(jar, driver);
        java.util.Properties info = new java.util.Properties();
    	if (userName != null) 
    	{
    	    info.put("user", userName);
    	}
    	if (password != null) 
    	{
    	    info.put("password", password);
    	}
    	
    	Connection connection = null;
    	try
    	{
    		connection = driverInstance.connect(dbUrl, info);
    		PreparedStatement ps = connection.prepareStatement("INSERT INTO " + dbTable + "(" + dbColumn + ") VALUES(?)");
    		ps.setString(1, buffer.toString());
    		ps.executeUpdate();
    		ps.close();
    		connection.commit();
    	}
    	finally
    	{
			DatabaseManager.closeDbConnection(connection);
    	}
		
    	return null;
	}

}
