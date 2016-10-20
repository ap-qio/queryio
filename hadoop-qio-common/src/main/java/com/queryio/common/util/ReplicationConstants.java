package com.queryio.common.util;


public interface ReplicationConstants 
{
	String CONTENT_LENGTH = "Content-Length";
	String LOCATION = "Location";
	String CONTENT_TYPE = "Content-Type";
	String IF_MODIFIED_SINCE = "If-Modified-Since";
	String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	String IF_MATCH = "If-Match";
	String IF_NONE_MATCH = "If-None-Match";
	String RANGE = "Range";
	
	String X_GOOG_API_VERSION_HEADER = "x-goog-api-version";
	String X_AMZ_REQUEST_ID = "x-amz-request-id";
	String X_AMZ_ID_2 = "x-amz-id-2";
	String X_OS3_REQUESTID = "x-os3-request-id";
	
	String X_OS3_BUCKET_NAME = "x-os3-bucket-name";
	String X_OS3_OBJECT_NAME = "x-os3-object-name";
	String HOST = "host"; 
	
	String STRING_BUILDER = "STRING_BUILDER";

	String FS_DEFAULT_NAME_CONF = "fs.defaultFS";
	
	String FS_DEFAULT_NAME_PRIMARY = "FS_DEFAULT_NAME_PRIMARY";
	String FS_DEFAULT_NAME_SECONDARY = "FS_DEFAULT_NAME_SECONDARY";
	
	String TASK_CREATE_BUCKET = "Create Bucket";
	String TASK_DELETE_BUCKET = "Delete Bucket";
	String TASK_CREATE_OBJECT = "Create Object";
	String TASK_DELETE_OBJECT = "Delete Object";
	
	int REPLICATION_WAIT = 10; //ms
	
	String EMPTY_STRING = "";
	
	String REPLICATOR_PROPERTIES_PATH = "replicator.properties";
}
