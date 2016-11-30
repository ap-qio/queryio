package com.os3.server.actions;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.os3.server.common.OS3Constants;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;
import com.queryio.common.EnvironmentalConstants;

public class LogoutAction extends BaseAction {

	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws ServletException, IOException, NoSuchAlgorithmException {
		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		if (EnvironmentalConstants.isUseKerberos()) {
			if (token == null) {
				ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
				return;
			}

			String username = DFSMap.getUserForToken(token);
			if (username == null) {
				ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
				return;
			}

			DFSMap.removeToken(token);
			DFSMap.removeDFS(username);

			// UserGroupInformation.removeLoginUser(username);
		}

		ResponseWriter.writeLogoutActionResponse(apiType, response, requestId);
	}
}
