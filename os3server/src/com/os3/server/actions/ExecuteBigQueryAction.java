package com.os3.server.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import com.os3.server.common.IErrorConstants;
import com.os3.server.common.OS3Constants;
import com.os3.server.customtag.BigQueryManager;
import com.os3.server.customtag.BigQueryRequest;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class ExecuteBigQueryAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());

	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws Exception {
		final String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		final String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		final String queryId = request.getHeader(OS3Constants.X_OS3_QUERYID);
		String sqlQuery = request.getHeader(OS3Constants.X_OS3_SQLQUERY);

		final String startIndex = request.getHeader(OS3Constants.X_OS3_STARTINDEX);
		final String maxResults = request.getHeader(OS3Constants.X_OS3_MAXRESULTS);
		final String timeoutMs = (String) helperMap.get(OS3Constants.X_OS3_TIMEOUTMS);

		logger.debug("BigQuery request received");

		String format = request.getHeader(OS3Constants.X_OS3_BIGQUERYREPORTFORMAT);

		if (format == null)
			format = "PLAIN";

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
			return;
		}

		final String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
			return;
		}

		int startIndexValue = 0;
		int maxResultsValue = -1;
		long timeoutMsValue = -1;

		if (startIndex != null) {
			try {
				startIndexValue = Integer.parseInt(startIndex);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			}
		}
		if (maxResults != null) {
			try {
				maxResultsValue = Integer.parseInt(maxResults);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			}
		}
		if (timeoutMs != null) {
			try {
				timeoutMsValue = Long.parseLong(timeoutMs);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			}
		}

		if (maxResultsValue == -1) {
			maxResultsValue = (Integer.MAX_VALUE - startIndexValue);
		}

		if (format.equals("PLAIN")) {
			if (sqlQuery == null) {
				logger.fatal("sqlQuery null");
				ErrorResponseWriter.missingSQLQueryHeader(helperMap, response, null, requestId, apiType);
				return;
			}
			sqlQuery += " LIMIT " + maxResultsValue + " OFFSET " + startIndexValue;
			BigQueryRequest bigQueryRequest = new BigQueryRequest(sqlQuery);
			bigQueryRequest.start();
			if (timeoutMsValue == -1) {
				try {
					bigQueryRequest.join();
				} catch (InterruptedException e) {
					logger.fatal(e.getMessage(), e);
				}
			} else {
				try {
					bigQueryRequest.join(timeoutMsValue);
				} catch (InterruptedException e) {
					logger.fatal(e.getMessage(), e);
				}
			}

			ResponseWriter.writeBigQueryResponse(dfs, apiType, response, requestId, sqlQuery,
					bigQueryRequest.getBigQueryResult(), bigQueryRequest.isSuccess());
		} else {
			if (queryId == null) {
				logger.fatal("queryId null");
				ErrorResponseWriter.missingQueryIDHeader(helperMap, response, null, requestId, apiType);
				return;
			}
			try {
				BigQueryManager.generateBigQueryReport(queryId, format, response, username);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ErrorResponseWriter.writeErrorResponse(helperMap, response.getWriter(),
						IErrorConstants.ERR_REPORT_CREATION, e.getMessage(), null, requestId);
			}
		}
	}
}
