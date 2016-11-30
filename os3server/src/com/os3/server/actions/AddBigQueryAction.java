package com.os3.server.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.customtag.BigQueryManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class AddBigQueryAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());

	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws Exception {
		final String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		final String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);

		final HttpEntity entity = new InputStreamEntity(request.getInputStream(), request.getContentLength());
		final String properties = EntityUtils.toString(entity);

		logger.debug("AddBigQuery request received");

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
			return;
		}

		final String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
			return;
		}

		if (properties == null) {
			ErrorResponseWriter.missingRequiredHeaders(helperMap, response, null, requestId, apiType);
			return;
		}

		BigQueryManager.saveBigQuery(properties, username);

		ResponseWriter.writeAddBigQueryResponse(null, apiType, response, requestId);
	}
}