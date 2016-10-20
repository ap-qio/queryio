package com.os3.server.customtag;

import java.sql.Connection;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.queryio.common.database.CoreDBManager;

public class CustomTagsManager {
	protected static final Logger LOGGER = Logger.getLogger(CustomTagsManager.class);
	
	public static TableModel getBigQueryResults(String poolName, String query) throws Exception{
		Connection connection = null;
		try{
			connection = CoreDBManager.getCustomTagDBConnection(poolName);
			LOGGER.debug("Connected to database: " + connection.getMetaData().getURL());
			return CustomTagsDAO.getBigQueryResults(connection, query);
		} finally {
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				LOGGER.fatal(e.getMessage(), e);
			}
		}
	}
}
