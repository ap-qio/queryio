package com.os3.server.customtag;

import java.sql.Connection;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.queryio.common.database.CoreDBManager;

public class CustomTagsManager {
	protected static final Logger logger = Logger.getLogger(CustomTagsManager.class);
	
	public static TableModel getBigQueryResults(String poolName, String query) throws Exception{
		Connection connection = null;
		try{
			connection = CoreDBManager.getCustomTagDBConnection(poolName);
			logger.debug("Connected to database: " + connection.getMetaData().getURL());
			return CustomTagsDAO.getBigQueryResults(connection, query);
		} finally {
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				logger.fatal(e.getMessage(), e);
			}
		}
	}
}
