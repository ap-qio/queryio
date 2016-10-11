package com.os3.server.actions;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.customtag.BigQueryManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class GetBigQueryAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());
	
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response, Map<String, Object> helperMap, int apiType) throws Exception {
		String requestId =  (String)helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token =  (String)request.getHeader(OS3Constants.AUTHORIZATION);
		
		String queryId = (String) request.getHeader(OS3Constants.X_OS3_BIGQUERYID);
		
		logger.debug("GetBigQuery request received");
		
		FileSystem dfs = null;
		
		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
			return;
		}
		
		String username = DFSMap.getUserForToken(token);
		if(username==null){
			ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
			return;
		}
		
		if(queryId==null){
			ResponseWriter.writeGetAllBigQueriesResponse(dfs, apiType, response, requestId, BigQueryManager.getAllBigQueries());
		} else {
			ResponseWriter.writeGetAllBigQueriesResponse(dfs, apiType, response, requestId, BigQueryManager.getBigQuery(queryId, username));
		}
	}
}