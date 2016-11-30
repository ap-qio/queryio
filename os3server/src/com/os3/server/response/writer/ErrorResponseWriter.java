package com.os3.server.response.writer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.os3.server.common.IErrorConstants;
import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamUtilities;

public class ErrorResponseWriter {
	protected final static Logger LOGGER = Logger.getLogger(ErrorResponseWriter.class);

	public static void preConditionFailed(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_PRECONDITION_FAILED, apiType,
					requestId, OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_PRECONDITION_FAILED_CODE,
					IErrorConstants.ERR_PRECONDITION_FAILED_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void invalidArgument(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INVALID_ARGUMENT_CODE,
					IErrorConstants.ERR_INVALID_ARGUMENT_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void permissionDenied(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String message, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_UNAUTHORIZED, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_PERMISSION_DENIED_CODE, message,
					resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void invalidRequestURI(Map<String, Object> helperMap, HttpServletResponse response, String requestId,
			int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INVALID_REQUEST_URI_CODE,
					IErrorConstants.ERR_INVALID_REQUEST_URI_DESC, null, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void serviceUnavailable(Map<String, Object> helperMap, HttpServletResponse response, String requestId,
			int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, apiType,
					requestId, OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_SERVICE_UNAVAILABLE_CODE,
					IErrorConstants.ERR_SERVICE_UNAVAILABLE_DESC, null, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void requestTimedOut(Map<String, Object> helperMap, HttpServletResponse response, String requestId,
			int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_REQUEST_TIMEOUT, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_REQUEST_TIMEOUT_CODE,
					IErrorConstants.ERR_REQUEST_TIMEOUT_DESC, null, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void buketAlreadyExists(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_CONFLICT, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_BUCKET_ALREADY_EXISTS_CODE,
					IErrorConstants.ERR_BUCKET_ALREADY_EXISTS_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void pathDoesNotExist(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_NOT_FOUND, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_PATH_DOES_NOT_EXIST_CODE,
					IErrorConstants.ERR_PATH_DOES_NOT_EXIST_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void invalidBucketName(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INVALID_BUCKET_NAME_CODE,
					IErrorConstants.ERR_INVALID_BUCKET_NAME_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void incompleteBody(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INCOMPLETE_BODY_CODE,
					IErrorConstants.ERR_INCOMPLETE_BODY_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingContentLength(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_LENGTH_REQUIRED, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_CONTENT_LENGTH_CODE,
					IErrorConstants.ERR_MISSING_CONTENT_LENGTH_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingAuthorizationHeader(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_AUTHORIZATION_HEADER_CODE,
					IErrorConstants.ERR_MISSING_AUTHORIZATION_HEADER_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingRequiredHeaders(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_REQUIRED_HEADERS_CODE,
					IErrorConstants.ERR_MISSING_REQUIRED_HEADERS_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingSQLQueryHeader(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_REQUIRED_HEADERS_CODE,
					IErrorConstants.ERR_MISSING_SQL_QUERY_HEADER_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingQueryIDHeader(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_REQUIRED_HEADERS_CODE,
					IErrorConstants.ERR_MISSING_QUERYID_HEADER_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void invalidToken(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_UNAUTHORIZED, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INVALID_TOKEN_CODE,
					IErrorConstants.ERR_INVALID_TOKEN_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void missingLoginCredentials(Map<String, Object> helperMap, HttpServletResponse response,
			String resource, String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_UNAUTHORIZED, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_MISSING_CREDENTIALS_HEADER_CODE,
					IErrorConstants.ERR_MISSING_CREDENTIALS_HEADER_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void loginFailure(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_UNAUTHORIZED, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_LOGIN_FAILURE_CODE,
					IErrorConstants.ERR_LOGIN_FAILURE_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void badDigest(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_BAD_REQUEST, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_BAD_DIGEST_CODE,
					IErrorConstants.ERR_BAD_DIGEST_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void buketNotEmpty(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_CONFLICT, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_BUCKET_NOT_EMPTY_CODE,
					IErrorConstants.ERR_BUCKET_NOT_EMPTY_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void buketNotfound(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_NOT_FOUND, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_NO_SUCH_BUCKET_CODE,
					IErrorConstants.ERR_NO_SUCH_BUCKET_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void objectNotfound(Map<String, Object> helperMap, HttpServletResponse response, String resource,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_NOT_FOUND, apiType, requestId,
					OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_NO_SUCH_KEY_CODE,
					IErrorConstants.ERR_NO_SUCH_KEY_DESC, resource, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	public static void internalServerError(Map<String, Object> helperMap, HttpServletResponse response,
			String requestId, int apiType) {
		try {
			ResponseWriter.handleResponseStatus(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, apiType,
					requestId, OS3Constants.APPLICATION_XML);
			writeErrorResponse(helperMap, response.getWriter(), IErrorConstants.ERR_INTERNAL_SERVER_ERROR_CODE,
					IErrorConstants.ERR_INTERNAL_SERVER_ERROR_DESC, null, requestId);
		} catch (IOException ex) {
			LOGGER.fatal("error writing response for requestId " + requestId, ex);
		}
	}

	private static String getErrorResponse(Map<String, Object> helperMap, String errorCode, String errorMessage,
			String resource, String requestId) {
		StringBuilder buffer = StreamUtilities.getStringBuilder(helperMap);
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		buffer.append(IErrorConstants.XML_RESPONSE_HEADER);
		buffer.append(IErrorConstants.NEWLINE);

		// <Error>
		buffer.append(IErrorConstants.XML_RESPONSE_ERROR_TAG);
		buffer.append(IErrorConstants.NEWLINE);

		// <Code>ErrorCode</Code>
		buffer.append(IErrorConstants.XML_RESPONSE_CODE_TAG);
		buffer.append(errorCode);
		buffer.append(IErrorConstants.XML_RESPONSE_CODE_CLTAG);
		buffer.append(IErrorConstants.NEWLINE);

		// <Message>Error Messge</Message>
		buffer.append(IErrorConstants.XML_RESPONSE_MESSAGE_TAG);
		buffer.append(errorMessage);
		buffer.append(IErrorConstants.XML_RESPONSE_MESSAGE_CLTAG);
		buffer.append(IErrorConstants.NEWLINE);

		if (resource != null) {
			// <Resource>/mybucket/myfoto.jpg</Resource>
			buffer.append(IErrorConstants.XML_RESPONSE_RESOURCE_TAG);
			buffer.append(resource);
			buffer.append(IErrorConstants.XML_RESPONSE_RESOURCE_CLTAG);
			buffer.append(IErrorConstants.NEWLINE);
		}

		if (requestId != null) {
			// <RequestId>4442587FB7D0A2F9</RequestId>
			buffer.append(IErrorConstants.XML_RESPONSE_REQUESTID_TAG);
			buffer.append(requestId);
			buffer.append(IErrorConstants.XML_RESPONSE_REQUESTID_CLTAG);
			buffer.append(IErrorConstants.NEWLINE);
		}

		// </Error>
		buffer.append(IErrorConstants.XML_RESPONSE_ERROR_CLTAG);
		buffer.append(IErrorConstants.NEWLINE);
		return buffer.toString();
	}

	public static void writeErrorResponse(Map<String, Object> helperMap, PrintWriter pw, String errorCode,
			String errorMessage, String resource, String requestId) {
		String errorString = getErrorResponse(helperMap, errorCode, errorMessage, resource, requestId);
		pw.write(errorString);
		pw.flush();
		LOGGER.error("RequestId : " + requestId + "- Error Response " + errorString);
	}

}
