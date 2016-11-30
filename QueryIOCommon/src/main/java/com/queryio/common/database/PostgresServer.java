package com.queryio.common.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StreamPumper;


/**
 * 
 * @author Exceed Consultancy Services.
 */
public class PostgresServer extends Thread
{
	/**
	 * @see java.lang.Runnable#run()
	 */
	String dbName;
	public PostgresServer(String dbName){
		this.dbName = dbName;
	}
	
	
	public void startServer()
	{
		this.start();
	}
	
	public void run()
	{
		startDatabaseServers();
	}
	
	private void startDatabaseServers()
	{
		//final String msg = "Error while starting server";
		StringBuffer buffer = new StringBuffer(EnvironmentalConstants.getAppHome() +"../../../");
		buffer.append(File.separatorChar);
		buffer.append("postgres");
		buffer.append(File.separatorChar);
		final File hsqlFolder = new File(buffer.toString());
		File execFile = new File(hsqlFolder, PlatformHandler.isWindows() ? "start-postgres.bat":"start-postgres.sh");	

		// start active db
		startDatabaseServer(hsqlFolder, new String [] {execFile.getAbsolutePath()}, dbName);
	}
	
	public void stopDatabaseServers()
	{
		//final String msg = "Error while stopping server";
		StringBuffer buffer = new StringBuffer(EnvironmentalConstants.getAppHome() +"../../../");
		buffer.append(File.separatorChar);
		buffer.append("postgres");
		buffer.append(File.separatorChar);
		final File hsqlFolder = new File(buffer.toString());
		File execFile = new File(hsqlFolder, PlatformHandler.isWindows() ? "stop-postgres.bat":"stop-postgres.sh");	

		// stop active db
		startDatabaseServer(hsqlFolder, new String [] {execFile.getAbsolutePath()}, dbName);
	}
	
	private static void startDatabaseServer(File folder, String [] cmdArray, String prefix)
	{
		try
		{
			FileOutputStream fosOut = null;
			FileOutputStream fosErr = null;
			// for debugging
			fosOut = new FileOutputStream(new File(folder, prefix + "-out.txt"));
			fosErr = new FileOutputStream(new File(folder, prefix + "-err.txt"));

			final Process process = Runtime.getRuntime().exec(cmdArray, null, folder);

			final StreamPumper spOut = new StreamPumper(new BufferedInputStream(process.getInputStream()), fosOut);
			spOut.start();
			final StreamPumper spErr = new StreamPumper(new BufferedInputStream(process.getErrorStream()), fosErr);
			spErr.start();
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().fatal("Error: " + prefix, e);
		}
	}	
	
//	public static void stopServer(Connection con)
//	{
//		final String msg = "Error while stopping server";
//		final String SHUTDOWN = "SHUTDOWN";
//		try
//		{
//			final Statement st = con.createStatement();
//			st.execute(SHUTDOWN);
//			st.close();
//		}
//		catch (final Exception ex)
//		{
//			AppLogger.getLogger().fatal(msg, ex);
//		}
//	}
}
