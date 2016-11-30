package com.os3.server.customtag;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;

public class BigQueryRequest extends Thread {
	protected final Logger logger = Logger.getLogger(getClass());

	String query;
	boolean success;

	TableModel bigQueryResult;

	public BigQueryRequest(String query) {
		this.query = query;
	}

	public void run() {
		try {
			this.bigQueryResult = CustomTagsManager.getBigQueryResults(OS3Constants.poolName, query);
			this.success = true;
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
		}
	}

	public TableModel getBigQueryResult() {
		return bigQueryResult;
	}

	public boolean isSuccess() {
		return success;
	}
}
