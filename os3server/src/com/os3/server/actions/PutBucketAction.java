package com.os3.server.actions;

import java.io.IOException;
import java.security.AccessControlException;
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
import com.os3.server.userinfo.UserInfoContainer;
import com.queryio.common.DFSMap;

public class PutBucketAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());

	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws ServletException, IOException {
		String bucketName = (String) helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, bucketName, requestId, apiType);
			return;
		}

		String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, bucketName, requestId, apiType);
			return;
		}

		dfs = DFSMap.getDFSForUser(username);

		String group = UserInfoContainer.getDefaultGroupForUser(username);

		if (!DataManager.isValidBucketName(bucketName)) {
			ErrorResponseWriter.invalidBucketName(helperMap, response, bucketName, requestId, apiType);
		} else if (DataManager.doesBucketExists(dfs, bucketName)) {
			ErrorResponseWriter.buketAlreadyExists(helperMap, response, bucketName, requestId, apiType);
		} else {
			String bucketOwner = request.getHeader(OS3Constants.OWNER);
			String bucketGroup = request.getHeader(OS3Constants.GROUP);
			String permission = request.getHeader(OS3Constants.PERMISSION);

			if (bucketOwner == null) {
				bucketOwner = username;
			}
			if (bucketGroup == null) {
				bucketGroup = group;
			}

			boolean status = false;
			if (permission != null) {
				try {
					status = DataManager.createBucket(dfs, bucketName, bucketOwner, bucketGroup,
							Short.parseShort(permission));
				} catch (NumberFormatException e) {
					ErrorResponseWriter.invalidArgument(helperMap, response, bucketName, requestId, apiType);
					return;
				} catch (AccessControlException e) {
					ErrorResponseWriter.permissionDenied(helperMap, response, bucketName, e.getLocalizedMessage(),
							requestId, apiType);
					return;
				}
			} else {
				try {
					status = DataManager.createBucket(dfs, bucketName, bucketOwner, bucketGroup);
				} catch (AccessControlException e) {
					ErrorResponseWriter.permissionDenied(helperMap, response, bucketName, e.getMessage(), requestId,
							apiType);
					return;
				}
			}

			if (status) {
				ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId,
						OS3Constants.TEXT_HTML);
				// Add location header
				if (apiType == OS3Constants.API_TYPE_AMAZON) {
					// FIXME should we return the complete absolute path here?
					response.setHeader(OS3Constants.LOCATION, "/" + bucketName);
				}
			} else {
				ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
			}
		}
	}
}