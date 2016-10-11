package com.os3.server.actions;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.ietf.jgss.GSSException;

import com.os3.server.common.OS3Constants;
import com.os3.server.data.manager.DataManager;
import com.os3.server.hadoop.DFSManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.queryio.common.DFSMap;

public class PutACLAction extends BaseAction {

	@Override
	public void execute(String operation, HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> helperMap,
			int apiType) throws ServletException, IOException,
			NoSuchAlgorithmException, GSSException, LoginException, Exception {
		String bucketName = (String) helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		String objectName = (String)helperMap.get(OS3Constants.X_OS3_OBJECT_NAME);
		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response,
					bucketName, requestId, apiType);
			return;
		}

		String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, bucketName,
					requestId, apiType);
			return;
		}

		dfs = DFSMap.getDFSForUser(username);

		String owner = request.getHeader(OS3Constants.OWNER);
		String group = request.getHeader(OS3Constants.GROUP);
		String permission = request.getHeader(OS3Constants.PERMISSION);

		Path path = objectName!=null ? new Path(DFSManager.ROOT_PATH + bucketName, objectName) : new Path(DFSManager.ROOT_PATH + bucketName);
		
		if( ! DataManager.doesPathExist(dfs, path)) {
			ErrorResponseWriter.pathDoesNotExist(helperMap, response,
					bucketName, requestId, apiType);
			return;
		} else {
			try {
				if(owner!=null && group!=null) {
					DataManager.setOwner(dfs, path, owner, group);
				}
				if(permission!=null) {
					DataManager.setPermissions(dfs, path, Short.parseShort(permission));
				}
			} catch(NumberFormatException e) {
				ErrorResponseWriter.invalidArgument(helperMap, response, objectName, requestId, apiType);
				return;
			} catch(AccessControlException e) {
				ErrorResponseWriter.permissionDenied(helperMap, response, objectName, e.getLocalizedMessage(), requestId, apiType);
				return;
			}
		}
	}

}
