package com.os3.server.actions;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.ietf.jgss.GSSException;

import com.os3.server.common.OS3Constants;
import com.os3.server.common.QIODFSUtils;
import com.os3.server.common.TokenGenerator;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.os3.server.userinfo.UserInfoContainer;
import com.queryio.common.DFSMap;
import com.queryio.common.EnvironmentalConstants;

public class LoginAction extends BaseAction {

	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType)
			throws ServletException, IOException, NoSuchAlgorithmException, GSSException, LoginException {

		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String username = request.getHeader(OS3Constants.USERNAME);
		String password = request.getHeader(OS3Constants.PASSWORD);

		logger.debug("Username: " + username);
		logger.debug("Password: " + password);

		if (username == null || password == null) {
			ErrorResponseWriter.missingLoginCredentials(helperMap, response, null, requestId, apiType);
			return;
		}

		/* Login code goes here */

		FileSystem dfs = null;
		String token = null;

		if (EnvironmentalConstants.isUseKerberos()) {
			final HdfsConfiguration conf = DFSMap.getKerberosConfiguration();
			UserGroupInformation.setConfiguration(conf);

			try {
				// UserGroupInformation.getLoginUser(username, password);

				dfs = FileSystem.get(conf);
				dfs.getStatus();
			} catch (Exception e) {
				ErrorResponseWriter.loginFailure(helperMap, response, null, requestId, apiType);
				logger.error(e.getMessage(), e);
				return;
			}
		} else {
			try {
				if (!UserInfoContainer.validateUser(username, password)) {
					ErrorResponseWriter.loginFailure(helperMap, response, null, requestId, apiType);
					return;
				}

				final HdfsConfiguration conf = DFSMap.getConfiguration();

				dfs = QIODFSUtils.getFileSystemAs(username, UserInfoContainer.getDefaultGroupForUser(username), conf);

				dfs.getStatus();
			} catch (Exception e) {
				ErrorResponseWriter.loginFailure(helperMap, response, null, requestId, apiType);
				logger.error(e.getMessage(), e);
				return;
			}
		}

		token = TokenGenerator.generateToken();

		DFSMap.addToken(token, username);
		DFSMap.addDFS(username, dfs);

		ResponseWriter.writeLoginActionResponse(apiType, response, requestId, token);
	}
}
