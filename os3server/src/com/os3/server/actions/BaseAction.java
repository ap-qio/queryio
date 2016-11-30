package com.os3.server.actions;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;

import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamUtilities;
import com.os3.server.exception.InvalidRequestURIException;
import com.os3.server.response.writer.ErrorResponseWriter;

public abstract class BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());

	public BaseAction() {
		// do nothing.
	}

	/**
	 * An abstract method which is inherited by all the classes extending the
	 * BaseAction class. Process the request for the specified operation.
	 * 
	 * @param operation
	 *            It defines the type of the service for which the request has
	 *            come.
	 * @param request
	 *            The request object of the servlet.
	 * @param response
	 *            The response object of the servlet.
	 * @throws ServletException
	 *             Signals that a ServletException has occurred.
	 * @throws IOException
	 *             Signals that an IOException has occurred.
	 * @throws NoSuchAlgorithmException
	 * @throws GSSException
	 * @throws LoginException
	 * @throws Exception
	 */
	public abstract void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType)
			throws ServletException, IOException, NoSuchAlgorithmException, GSSException, LoginException, Exception;

	public void execute(String operation, HttpServletRequest request, HttpServletResponse response, String[] pathTokens,
			String requestId, Map<String, Object> helperMap, int apiType) throws Exception {
		try {
			populateHelperMap(helperMap, pathTokens, request, requestId);
		} catch (InvalidRequestURIException e) {
			logger.error(e.getMessage(), e);
			ErrorResponseWriter.invalidRequestURI(helperMap, response, requestId, apiType);
			return;
		}
		execute(operation, request, response, helperMap, apiType);
	}

	private void populateHelperMap(Map<String, Object> helperMap, String[] pathTokens, HttpServletRequest request,
			String requestId) throws InvalidRequestURIException {
		if (pathTokens == null || pathTokens.length == 0) {
			// throw new InvalidRequestURIException("BadRequest for requestId :
			// " + requestId + ". No Request URI specified in request " +
			// request.getPathInfo());
		} else {
			helperMap.put(OS3Constants.X_OS3_BUCKET_NAME, pathTokens[0]);
			if (pathTokens.length > 1) {
				StringBuilder stringBuilder = StreamUtilities.getStringBuilder(helperMap);
				for (int i = 1; i < (pathTokens.length - 1); i++) {
					stringBuilder.append(pathTokens[i]);
					stringBuilder.append(OS3Constants.OBJECT_PATH_SEPARATOR);
				}
				stringBuilder.append(pathTokens[pathTokens.length - 1]);
				helperMap.put(OS3Constants.X_OS3_OBJECT_NAME, stringBuilder.toString());
			}

			// Add all Request headers in map.
			Enumeration<String> headerEnum = request.getHeaderNames();
			if (headerEnum != null) {
				while (headerEnum.hasMoreElements()) {
					String headerName = headerEnum.nextElement();
					// Skip host header
					if (headerName.equals(OS3Constants.HOST))
						continue;
					helperMap.put(headerName, request.getHeader(headerName));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"Request parameters/headers have been translated into a map containing required information. Map has following key-value pairs: "
							+ helperMap + "");
		}
	}
}
