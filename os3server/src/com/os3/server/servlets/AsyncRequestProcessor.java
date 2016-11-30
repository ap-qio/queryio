package com.os3.server.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.os3.server.actions.BaseAction;
import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamUtilities;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;

public class AsyncRequestProcessor implements Runnable {
	// Creating logger with default scope to avoid - Read access to enclosing
	// field AsyncRequestProcessor.logger is emulated by a synthetic accessor
	// method
	static Logger logger = Logger.getLogger(AsyncRequestProcessor.class);
	private final AsyncContext asyncContext;
	private final OS3ActionServlet actionServlet;
	private final Map<String, BaseAction> actionObjects;
	private boolean notFirst = false;

	AsyncRequestProcessor(AsyncContext asyncContext, OS3ActionServlet servlet, Map<String, BaseAction> actionObjects) {
		this.asyncContext = asyncContext;
		this.actionServlet = servlet;
		this.actionObjects = actionObjects;
	}

	public void run() {
		if (this.notFirst) {
			return;
		}
		this.notFirst = true;
		HttpServletResponse response = (HttpServletResponse) this.asyncContext.getResponse();
		HttpServletRequest request = (HttpServletRequest) this.asyncContext.getRequest();
		this.asyncContext.addListener(new AsyncListener() {

			public void onTimeout(AsyncEvent event) throws IOException {
				HttpServletRequest request = (HttpServletRequest) event.getSuppliedRequest();
				HttpServletResponse response = (HttpServletResponse) event.getSuppliedResponse();
				String[] pathTokens = StreamUtilities.splitPathInfo(request);
				String operation = getOperation(request, pathTokens);
				if (logger.isDebugEnabled())
					logger.debug("Request onTimeout, "
							+ StreamUtilities.getRequestInformation(null, request, response.getStatus(), operation));
			}

			public void onStartAsync(AsyncEvent event) throws IOException {
				// if (logger.isDebugEnabled()) logger.debug("AsyncListener:
				// onStartAsync");
			}

			public void onError(AsyncEvent event) throws IOException {
				// if (logger.isDebugEnabled()) logger.debug("AsyncListener:
				// onError");
			}

			public void onComplete(AsyncEvent event) throws IOException {
				// if (logger.isDebugEnabled()) logger.debug("AsyncListener:
				// onComplete");
			}
		});
		executeRequest(this.asyncContext, this.actionServlet, actionObjects, request, response);
	}

	private static String getOperation(HttpServletRequest request, String[] pathTokens) {
		String operation = null;
		// Check if uri path has just one token, if yes then its bucket request
		// else its object request.

		String token = "/";
		if (pathTokens != null) {
			for (int i = 0; i < pathTokens.length; i++) {
				token += pathTokens[i] + "/";
			}
		}

		logger.debug("Token: " + token);

		if (token.contains("/hadoopsql")) {
			if (OS3Constants.METHOD_GET.equals(request.getMethod())) {
				operation = OS3Constants.OPERATION_GET_BIGQUERY;
			} else if (OS3Constants.METHOD_PUT.equals(request.getMethod())) {
				operation = OS3Constants.OPERATION_ADD_BIGQUERY;
			} else if (OS3Constants.METHOD_DELETE.equals(request.getMethod())) {
				operation = OS3Constants.OPERATION_DELETE_BIGQUERY;
			} else if (OS3Constants.METHOD_POST.equals(request.getMethod())) {
				operation = OS3Constants.OPERATION_EXECUTE_BIGQUERY;
			}

		} else if (token.contains("/hive")) {
				operation = OS3Constants.OPERATION_ADD_HIVE;
		} else {
			if (request.getHeader(OS3Constants.USERNAME) != null) {
				operation = OS3Constants.OPERATION_LOGIN;
			} else if (request.getParameter(OS3Constants.LOGOUT) != null) {
				operation = OS3Constants.OPERATION_LOGOUT;
			} else {
				if (pathTokens != null) {
					if (pathTokens.length == 1) {
						if (OS3Constants.METHOD_GET.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_GET_BUCKET;
							if (request.getParameter(OS3Constants.ACL) != null)
								operation = OS3Constants.OPERATION_GET_ACL;
						} else if (OS3Constants.METHOD_PUT.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_PUT_BUCKET;
							if (request.getParameter(OS3Constants.ACL) != null)
								operation = OS3Constants.OPERATION_PUT_ACL;
						} else if (OS3Constants.METHOD_DELETE.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_DELETE_BUCKET;
						}
					} else if (pathTokens.length > 1) {
						if (OS3Constants.METHOD_GET.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_GET_OBJECT;
							if (request.getParameter(OS3Constants.ACL) != null)
								operation = OS3Constants.OPERATION_GET_ACL;
						} else if (OS3Constants.METHOD_PUT.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_PUT_OBJECT;
							if (request.getParameter(OS3Constants.ACL) != null)
								operation = OS3Constants.OPERATION_PUT_ACL;
						} else if (OS3Constants.METHOD_DELETE.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_DELETE_OBJECT;
						} else if (OS3Constants.METHOD_HEAD.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_HEAD_OBJECT;
						}
					} else {
						if (OS3Constants.METHOD_GET.equals(request.getMethod())) {
							operation = OS3Constants.OPERATION_GET_SERVICE;
						}
					}
				} else {
					if (OS3Constants.METHOD_GET.equals(request.getMethod())) {
						operation = OS3Constants.OPERATION_GET_SERVICE;
					}
				}
			}
		}
		logger.debug("Operation: " + operation);
		return operation;
	}

	public static void executeRequest(AsyncContext asyncContext, OS3ActionServlet actionServlet,
			Map<String, BaseAction> actionObjects, HttpServletRequest request, HttpServletResponse response) {
		String[] pathTokens = StreamUtilities.splitPathInfo(request);
		String operation = getOperation(request, pathTokens);

		String requestId = System.nanoTime() + "_" + request.hashCode();
		long ts = System.nanoTime();
		Map<String, Object> helperMap = new HashMap<String, Object>();
		helperMap.put(OS3Constants.X_OS3_REQUESTID, requestId);
		int apiType = OS3Constants.API_TYPE_AMAZON;
		if (request.getHeader(OS3Constants.X_GOOG_API_VERSION_HEADER) != null) {
			apiType = OS3Constants.API_TYPE_GOOGLE;
		}
		try {
			actionServlet.enteringServiceMethod();
			BaseAction action = operation != null ? actionObjects.get(operation) : null;
			if (action != null) {
				if (logger.isDebugEnabled())
					logger.debug("request operation " + operation + " will be handled by "
							+ action.getClass().getSimpleName() + " class");
				try {
					action.execute(operation, request, response, pathTokens, requestId, helperMap, apiType);
				} catch (ServletException e) {
					logger.error("RequestId : " + requestId + ". Error executing request", e);
					ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
				} catch (IOException ioe) {
					logger.error("RequestId : " + requestId + ". Error executing request", ioe);
					ResponseWriter.sendConnectionClose(response);
					if (ioe instanceof java.net.ConnectException) {
						ErrorResponseWriter.serviceUnavailable(helperMap, response, requestId, apiType);
					} else if (ioe instanceof java.net.SocketTimeoutException) {
						ErrorResponseWriter.requestTimedOut(helperMap, response, requestId, apiType);
					} else {
						ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
					}
				} catch (Exception e) {
					logger.error("RequestId : " + requestId + ". Error executing request", e);
					ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
				}
			} else {
				logger.error("RequestId : " + requestId + "Unsupported operation: " + operation);
				ErrorResponseWriter.invalidRequestURI(helperMap, response, requestId, apiType);
			}
		} catch (Exception e) {
			logger.error("RequestId : " + requestId + ". Error executing request", e);
			ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
		} catch (Throwable e) {
			// we must re-throw ThreadDeath
			if (e instanceof ThreadDeath) {
				throw (ThreadDeath) e;
			}
			logger.error("RequestId : " + requestId + ". Error executing request", e);
			ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
		} finally {
			if (asyncContext != null) {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug(
								" Time taken(In Sec.) : " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - ts));
					}
					asyncContext.complete();
				} catch (Exception e) {
					logger.error("RequestId : " + requestId + ". error asyncContext.complete()", e);
					ErrorResponseWriter.internalServerError(helperMap, response, requestId, apiType);
				}
			}
			actionServlet.leavingServiceMethod();
			if (logger.isDebugEnabled()) {
				logger.debug("Request processed, "
						+ StreamUtilities.getRequestInformation(helperMap, request, response.getStatus(), operation));
			}
		}
	}
}