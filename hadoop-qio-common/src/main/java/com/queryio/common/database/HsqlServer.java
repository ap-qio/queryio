/*
 * @(#)  HsqlServer.java Jan 23, 2005
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StreamPumper;


/**
 * 
 * @author Exceed Consultancy Services.
 */
public class HsqlServer extends Thread
{
	/**
	 * @see java.lang.Runnable#run()
	 */
	String dbNames[]; 
	String dbPort;
	
	public HsqlServer(String[] dbNames, String dbPort){
		this.dbNames = dbNames;
		this.dbPort = dbPort;
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
		buffer.append("database");
		buffer.append(File.separatorChar);
		final File hsqlFolder = new File(buffer.toString());
		File execFile = new File(hsqlFolder, PlatformHandler.isWindows() ? "startdatabase.bat":"startdatabase.sh");	

		// start active db
		System.out.println("[HSQL Server]: Starting " + Arrays.toString(dbNames) +" DB");
		StringBuilder prefix = new StringBuilder();
		for(String dbName: dbNames){
			prefix.append(dbName);
			prefix.append("-");
		}
		
		startDatabaseServer(hsqlFolder, (String[])ArrayUtils.addAll(new String[] { execFile.getAbsolutePath(), dbPort }, dbNames), prefix.toString());
	}
	
	private static void startDatabaseServer(File folder, String [] cmdArray, String prefix)
	{
		try
		{
			FileOutputStream fosOut = null;
			FileOutputStream fosErr = null;
			// for debugging
			fosOut = new FileOutputStream(new File(folder, prefix + "out.txt"));
			fosErr = new FileOutputStream(new File(folder, prefix + "err.txt"));

			
			System.out.println(Arrays.toString(cmdArray));
			final Process process = Runtime.getRuntime().exec(cmdArray, null, folder);

			final StreamPumper spOut = new StreamPumper(new BufferedInputStream(process.getInputStream()), fosOut);
			spOut.start();
			final StreamPumper spErr = new StreamPumper(new BufferedInputStream(process.getErrorStream()), fosErr);
			spErr.start();
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().fatal("Error starting database: " + prefix, e);
		}
		
	}	
	
	public static void stopServer(Connection con)
	{
		final String msg = "Error while stopping server";
		final String SHUTDOWN = "SHUTDOWN";
		try
		{
			final Statement st = con.createStatement();
			st.execute(SHUTDOWN);
			st.close();
		}
		catch (final Exception ex)
		{
			AppLogger.getLogger().fatal(msg, ex);
		}
	}
}
