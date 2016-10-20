package com.queryio.db.reinit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.database.DatabaseManager;

public class ReinitializeDBServer extends HttpServlet
{
	protected static final Logger LOGGER = Logger.getLogger(ReinitializeDBServer.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res){
		doGet(req, res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		LOGGER.debug("Found DB re-initailize request.");
		String isCustomTagDB =  req.getParameter("isCustomTagDB");
		try{
			String currentQueryIOConnection = EnvironmentalConstants.getQueryIODatabasePoolName();
			new DatabaseConfigParser().loadDatabaseConfiguration(EnvironmentalConstants.getDbConfigFilePath());
			if(Boolean.parseBoolean(isCustomTagDB))
			{
				CoreDBManager.reInitializeCustomTagConnection();
			}
			else
			{
				DatabaseManager.removeConnectionPool(currentQueryIOConnection);
				CoreDBManager.initialize();
			}
		}
		catch (Exception e) 
		{
			LOGGER.fatal(e.getMessage(),e);
		}
	}
	
}
