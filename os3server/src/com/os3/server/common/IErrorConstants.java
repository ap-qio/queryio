package com.os3.server.common;

public interface IErrorConstants {

	//XML Error Response constants
	String NEWLINE = "\n";
	String XML_RESPONSE_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	String XML_RESPONSE_ERROR_TAG = "<Error>";
	String XML_RESPONSE_ERROR_CLTAG = "</Error>";
	String XML_RESPONSE_CODE_TAG = "<Code>";
	String XML_RESPONSE_CODE_CLTAG = "</Code>";
	String XML_RESPONSE_MESSAGE_TAG = "<Message>";
	String XML_RESPONSE_MESSAGE_CLTAG = "</Message>";
	String XML_RESPONSE_RESOURCE_TAG = "<Resource>";
	String XML_RESPONSE_RESOURCE_CLTAG = "</Resource>";
	String XML_RESPONSE_REQUESTID_TAG = "<RequestId>";
	String XML_RESPONSE_REQUESTID_CLTAG = "</RequestId>";
	
	//Error Code Constants 
	String ERR_NO_SUCH_KEY_CODE = "NoSuchKey";
	String ERR_NO_SUCH_KEY_DESC = "The specified key does not exist.";
	
	String ERR_NO_SUCH_BUCKET_CODE = "NoSuchBucket";
	String ERR_NO_SUCH_BUCKET_DESC = "The specified bucket does not exist.";
	
	String ERR_BUCKET_NOT_EMPTY_CODE = "BucketNotEmpty";
	String ERR_BUCKET_NOT_EMPTY_DESC = "The bucket you tried to delete is not empty.";
	
	String ERR_INVALID_BUCKET_NAME_CODE = "InvalidBucketName";
	String ERR_INVALID_BUCKET_NAME_DESC = "The specified bucket is not valid.";
	
	String ERR_BUCKET_ALREADY_EXISTS_CODE = "BucketAlreadyExists";
	String ERR_BUCKET_ALREADY_EXISTS_DESC = "The requested bucket name is not available. The bucket namespace is shared by all users of the system. Please select a different name and try again.";
	
	String ERR_PATH_DOES_NOT_EXIST_CODE = "PathDoesNotExist";
	String ERR_PATH_DOES_NOT_EXIST_DESC = "Specified path does not exist. Please specify a different name and try again.";
	
	String ERR_INTERNAL_SERVER_ERROR_CODE = "InternalError";
	String ERR_INTERNAL_SERVER_ERROR_DESC = "We encountered an internal error. Please try again.";
	
	String ERR_INVALID_REQUEST_URI_CODE = "InvalidURI";
	String ERR_INVALID_REQUEST_URI_DESC = "Couldn't parse the specified URI.";
	
	String ERR_SERVICE_UNAVAILABLE_CODE = "ServiceUnavailable";
	String ERR_SERVICE_UNAVAILABLE_DESC = "Please reduce your request rate.";

	String ERR_REQUEST_TIMEOUT_CODE = "RequestTimeout";
	String ERR_REQUEST_TIMEOUT_DESC = "Your socket connection to the server was not read from or written to within the timeout period.";
	
	String ERR_PRECONDITION_FAILED_CODE = "PreconditionFailed";
	String ERR_PRECONDITION_FAILED_DESC = "At least one of the preconditions you specified did not hold.";
	
	String ERR_INVALID_ARGUMENT_CODE = "InvalidArgument";
	String ERR_INVALID_ARGUMENT_DESC = "Invalid Argument";
	
	String ERR_PERMISSION_DENIED_CODE = "PermissionDenied";
	String ERR_PERMISSION_DENIED_DESC = "Permission Denied";
	
	String ERR_MISSING_CONTENT_LENGTH_CODE = "MissingContentLength";
	String ERR_MISSING_CONTENT_LENGTH_DESC = "You must provide the Content-Length HTTP header.";
	
	String ERR_MISSING_AUTHORIZATION_HEADER_CODE = "MissingAuthorizationHeader";
	String ERR_MISSING_AUTHORIZATION_HEADER_DESC = "You must provide the Authorization header.";
	String ERR_MISSING_QUERYID_HEADER_DESC = "You must provide the QUERYID in bigquery-properties if bigquery-report-format provided.";
	String ERR_MISSING_SQL_QUERY_HEADER_DESC = "You must provide the SQL Query in bigquery-properties if bigquery-report-format is not provided.";
	
	String ERR_MISSING_REQUIRED_HEADERS_CODE = "MissingRequiredHeaders";
	String ERR_MISSING_REQUIRED_HEADERS_DESC = "You must provide the required headers.";
	
	String ERR_INVALID_TOKEN_CODE = "InvalidToken";
	String ERR_INVALID_TOKEN_DESC = "Please check your token.";
	
	String ERR_MISSING_CREDENTIALS_HEADER_CODE = "MissingLoginCredentials";
	String ERR_MISSING_CREDENTIALS_HEADER_DESC = "You must provide username and password to login.";
	
	String ERR_LOGIN_FAILURE_CODE = "LoginFailure";
	String ERR_LOGIN_FAILURE_DESC = "Please check your credentials and try again.";
	
	String ERR_INCOMPLETE_BODY_CODE = "IncompleteBody";
	String ERR_INCOMPLETE_BODY_DESC = "You did not provide the number of bytes specified by the Content-Length HTTP header.";
	
	String ERR_BAD_DIGEST_CODE = "BadDigest";
	String ERR_BAD_DIGEST_DESC = "The Content-MD5 you specified did not match what we received.";
	String ERR_REPORT_CREATION = "Error while Generating Reports.";
}
