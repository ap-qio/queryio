package com.os3.server.actions;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.data.manager.DataManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class DeleteBucketAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response, Map<String, Object> helperMap, int apiType) throws ServletException, IOException {
		final String bucketName = (String)helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		final String requestId =  (String)helperMap.get(OS3Constants.X_OS3_REQUESTID);
		final String token =  (String)request.getHeader(OS3Constants.AUTHORIZATION);
		
		FileSystem dfs = null;
		
		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, bucketName, requestId, apiType);
			return;
		}
		
		final String username = DFSMap.getUserForToken(token);
		if(username==null){
			ErrorResponseWriter.invalidToken(helperMap, response, bucketName, requestId, apiType);
			return;
		}
		
		dfs = DFSMap.getDFSForUser(username);
		
		if(!DataManager.doesBucketExists(dfs, bucketName)) {
			ErrorResponseWriter.buketNotfound(helperMap, response, bucketName, requestId, apiType);
		}
		else if(!DataManager.isBucketEmpty(dfs, bucketName)) {
			ErrorResponseWriter.buketNotEmpty(helperMap, response, bucketName, requestId, apiType);
		}
		else {
			if(DataManager.deleteBucket(dfs, bucketName)) {
				ResponseWriter.writeDeleteResponse(apiType, response, requestId);
			}
			else { // Could not delete bucket
				ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
			}
		}
	}
}
