package com.queryio.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HsqlBackup 
{
	public static void backupDatabase(String database, String url, String username, String password) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();
			statement.executeUpdate("SCRIPT '" + "backup_" + database + ".sql'");
			statement.close();
		}
		catch (Exception ex)
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception 
	{
		Class.forName("org.hsqldb.jdbcDriver");
		
		String password = "";
		if (args.length < 3)
		{
			System.out.println("Run: java com.appperfect.util.HsqlBackup databaseName db_url db_user [db_password]");
		}
		else if (args.length > 3)
		{
			password = args[3];
		}
		else
		{
			System.out.println("Warning, connecting to DB using empty password.");
		}
		System.out.println("Backing up " + args[0] + " to backup_" + args[0] + ".sql");
		backupDatabase(args[0], args[1], args[2], password);
		
	}

}
