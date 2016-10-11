package com.os3.server.actions;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
//import com.os3.server.hive.HiveDataRequest;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.queryio.common.DFSMap;
import com.queryio.common.HadoopConstants;

public class AddHiveDataDefinitionAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());

	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws Exception {
		final String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		final String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);
		logger.debug("HiveData request received");

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, null, requestId, apiType);
			return;
		}

		final String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, null, requestId, apiType);
			return;
		}

		forwardRequest(request, response);
	}

	private void forwardRequest(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String urlPrefix = HadoopConstants.getHadoopConf().get("queryio.server.url");
			URL url;
			if (urlPrefix == null) {
				url = new URL("http://" + req.getRemoteHost() + ":5678/queryio/" + OS3Constants.OPERATION_ADD_HIVE
						+ (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
			}
			url = new URL(urlPrefix + OS3Constants.OPERATION_ADD_HIVE
					+ (req.getQueryString() != null ? "?" + req.getQueryString() : ""));

			logger.debug("URL " + url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			final Enumeration<String> headers = req.getHeaderNames();
			while (headers.hasMoreElements()) {
				final String header = headers.nextElement();
				if (header.equalsIgnoreCase("host")) {
					continue;
				}
				final Enumeration<String> values = req.getHeaders(header);
				while (values.hasMoreElements()) {
					final String value = values.nextElement();
					conn.addRequestProperty(header, value);
				}
			}

			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();

			final byte[] buffer = new byte[1024];
			while (true) {
				final int read = req.getInputStream().read(buffer);
				if (read <= 0)
					break;
				conn.getOutputStream().write(buffer, 0, read);
			}

			resp.setStatus(conn.getResponseCode());
			for (int i = 0;; ++i) {
				final String header = conn.getHeaderFieldKey(i);
				if (header == null)
					break;
				final String value = conn.getHeaderField(i);
				resp.setHeader(header, value);
			}

			while (true) {
				final int read = conn.getInputStream().read(buffer);
				if (read <= 0)
					break;
				resp.getOutputStream().write(buffer, 0, read);
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getStackTrace());
		}

	}
}
