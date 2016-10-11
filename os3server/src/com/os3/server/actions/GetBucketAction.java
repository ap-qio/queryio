package com.os3.server.actions;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.data.manager.DataManager;
import com.os3.server.hadoop.BucketFilter;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class GetBucketAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());
	
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response, Map<String, Object> helperMap, int apiType) throws Exception {
		String bucketName = (String)helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		String requestId =  (String)helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token =  (String)request.getHeader(OS3Constants.AUTHORIZATION);
		
		FileSystem dfs = null;
		
		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, bucketName, requestId, apiType);
			return;
		}
		
		String username = DFSMap.getUserForToken(token);
		if(username==null){
			ErrorResponseWriter.invalidToken(helperMap, response, bucketName, requestId, apiType);
			return;
		}
		
		dfs = DFSMap.getDFSForUser(username);
		
		if(!DataManager.doesBucketExists(dfs, bucketName)){
			ErrorResponseWriter.buketNotfound(helperMap, response, bucketName, requestId, apiType);
		} else{
			String prefix = (String) request.getParameter(OS3Constants.GETBUCKET_REQUEST_PARAM_PREFIX);
			String delimiter = (String) request.getParameter(OS3Constants.GETBUCKET_REQUEST_PARAM_DELIMITER);
			String s_maxKeys = (String) request.getParameter(OS3Constants.GETBUCKET_REQUEST_PARAM_MAXKEYS);
			String marker = (String) request.getParameter(OS3Constants.GETBUCKET_REQUEST_PARAM_MARKER);
			
			logger.debug("prefix: " + prefix);
			logger.debug("delimiter: " + delimiter);
			logger.debug("s_maxKeys: " + s_maxKeys);
			logger.debug("marker: " + marker);
			
			int maxKeys = OS3Constants.GETBUCKET_MAXKEYS_DEFAULT;
			if(s_maxKeys != null){
				maxKeys = Integer.parseInt(s_maxKeys);
			}
			
			BucketFilter filter = new BucketFilter(dfs, prefix, delimiter, maxKeys, marker);
			
			ResponseWriter.writeListObjectsResponse(dfs, apiType, response, bucketName, prefix, delimiter, maxKeys, marker, 
					DataManager.getObjectList(dfs, bucketName, filter), filter.isTruncated(), filter.getCommonPrefixes(), requestId);
		}
	}
}
