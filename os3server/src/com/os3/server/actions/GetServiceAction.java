package com.os3.server.actions;

import java.io.IOException;
import java.util.ArrayList;
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

public class GetServiceAction extends BaseAction {

	static Logger logger = Logger.getLogger(GetServiceAction.class);

	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws ServletException, IOException {
		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
			return;
		}

		String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
			return;
		}

		dfs = DFSMap.getDFSForUser(username);

		String ownerName = username;
		String ownerID = username;

		ArrayList dirStats = DataManager.getAllDirStats(dfs);

		ResponseWriter.writeListAllMyBucketsResponse(apiType, response, ownerID, ownerName, dirStats, requestId);
	}
}