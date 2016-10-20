package com.os3.server.actions;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.ietf.jgss.GSSException;

import com.os3.server.common.OS3Constants;
import com.os3.server.data.manager.DataManager;
import com.os3.server.hadoop.DFSManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class GetACLAction extends BaseAction {

	@Override
	public void execute(String operation, HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> helperMap,
			int apiType) throws ServletException, IOException,
			NoSuchAlgorithmException, GSSException, LoginException, Exception {
		final String bucketName = (String) helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		final String objectName = (String)helperMap.get(OS3Constants.X_OS3_OBJECT_NAME);
		final String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		final String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response,
					bucketName, requestId, apiType);
			return;
		}

		final String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, bucketName,
					requestId, apiType);
			return;
		}

		dfs = DFSMap.getDFSForUser(username);

		Path path = objectName!=null ? new Path(DFSManager.ROOT_PATH + bucketName, objectName) : new Path(DFSManager.ROOT_PATH + bucketName);
		
		if( ! DataManager.doesPathExist(dfs, path)) {
			ErrorResponseWriter.pathDoesNotExist(helperMap, response,
					bucketName, requestId, apiType);
		} else {
			try {
				FileStatus status = DataManager.getPathStatus(dfs, path);
				ResponseWriter.writeGetACLResponse(request, response, apiType, requestId, status);
			} catch(AccessControlException e) {
				ErrorResponseWriter.permissionDenied(helperMap, response, objectName, e.getLocalizedMessage(), requestId, apiType);
				return;
			}
		}
	}

}
