package com.os3.server.common;

import org.apache.hadoop.hdfs.DFSConfigKeys;


public class OS3Constants {

	//File constants
	public static final String LOG4J_PROPERTIES = "/WEB-INF/os3log4j.properties";
	public static final String OS3_PROPERTIES_PATH = "/WEB-INF/os3.properties";
	public static final String MAPPING_FILE = "/WEB-INF/mapping.properties";
	public static final String HADOOP_PROPERTIES_FILE = "/WEB-INF/hadoop.properties";
	public static final String JAAS_CONF = "/WEB-INF/jaas.conf";
	
	//DFS constants
	public static final String FS_DEFAULT_NAME_CONF = DFSConfigKeys.FS_DEFAULT_NAME_KEY;
	public static final String FS_DFS_REPLICATION = DFSConfigKeys.DFS_REPLICATION_KEY;
	
	public static final String FS_DEFAULT_NAME_PRIMARY = "FS_DEFAULT_NAME_PRIMARY";
	public static final String FS_DEFAULT_NAME_SECONDARY = "FS_DEFAULT_NAME_SECONDARY";
	
	public static final String USE_THREAD_POOL = "USE_THREAD_POOL";
	
	public static final String STRING_BUILDER = "STRING_BUILDER";
	
	public static final int API_TYPE_GOOGLE = 0;
	public static final int API_TYPE_AMAZON = 1;
	
	public static final int GETBUCKET_MAXKEYS_DEFAULT = 1000;
	public static final String GETBUCKET_REQUEST_PARAM_PREFIX = "prefix";
	public static final String GETBUCKET_REQUEST_PARAM_DELIMITER = "delimiter";
	public static final String GETBUCKET_REQUEST_PARAM_MARKER = "marker";
	public static final String GETBUCKET_REQUEST_PARAM_MAXKEYS = "max-keys";
	
	public static final String X_GOOG_API_VERSION_HEADER = "x-goog-api-version";
	public static final String X_AMZ_REQUEST_ID = "x-amz-request-id";
	public static final String X_AMZ_ID_2 = "x-amz-id-2";
	public static final String X_OS3_REQUESTID = "x-os3-request-id";
	
	public static final String X_OS3_AMZ_SERVER_SIDE_ENCRYPTION = "x-amz-server-side-encryption";
	public static final String X_OS3_AMZ_SERVER_SIDE_COMPRESSION = "x-amz-server-side-compression";
	
	public static final String X_OS3_BUCKET_NAME = "x-os3-bucket-name";
	public static final String X_OS3_OBJECT_NAME = "x-os3-object-name";
	public static final String HOST = "host"; 
	
	public static final String X_OS3_SQLQUERY = "sqlquery";
	public static final String X_OS3_HIVEARGS = "hivejsonargs";
	public static final String X_OS3_QUERYID = "queryid";
	public static final String X_OS3_BIGQUERYPROPERTIES = "bigquery-properties";
	public static final String X_OS3_BIGQUERYID = "bigquery-id";
	public static final String X_OS3_BIGQUERYREPORTFORMAT = "bigquery-report-format";
	public static final String X_OS3_BIGQUERY = "bigquery";
	public static final String X_OS3_MAXRESULTS = "maxresults";
	public static final String X_OS3_STARTINDEX = "startindex";
	public static final String X_OS3_TIMEOUTMS = "timeout";
	
	public static final String OBJECT_PATH_SEPARATOR = "/";
	
	public static final int DESTROY_TIMEOUT = 30000;
	
	public static final String CONTENT_MD5 = "content-md5";
	
	/* Thread Pool */
	public static final String ASYNC_REQUEST_EXECUTOR = "asyncReqExecutor";
	public static final String ASYNC_THREAD = "AsyncRequestProcessor-";
	public static final String CORE_POOL_SIZE = "CORE_POOL_SIZE";
	public static final String MAXIMUM_POOL_SIZE = "MAXIMUM_POOL_SIZE";
	public static final String KEEP_ALIVE_TIME = "KEEP_ALIVE_TIME";
	public static final String QUEUE_CAPACITY = "QUEUE_CAPACITY";
	public static final String MAX_REQUEST_QUEUE_SIZE = "MAX_REQUEST_QUEUE_SIZE";

	public static final int DEFAULT_CORE_POOL_SIZE = 100;
	public static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;
	public static final int DEFAULT_KEEP_ALIVE_TIME = 5;
	public static final int DEFAULT_QUEUE_CAPACITY = 1000;
	public static final int DEFAULT_MAX_REQUEST_QUEUE_SIZE = 10;
	
	/* standard http headers *******************/
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String LOCATION = "Location";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String IF_MODIFIED_SINCE = "if-modified-since";
	public static final String IF_UNMODIFIED_SINCE = "if-unmodified-since";
	public static final String IF_MATCH = "if-match";
	public static final String IF_NONE_MATCH = "if-none-match";
	public static final String RANGE = "range";
	
	public static final String APPLICATION_XML = "application/xml";
	public static final String TEXT_HTML = "text/html";
	public static final byte[] CONTENT_TYPE_BYTES = CONTENT_TYPE.getBytes();
	//Transfer-Encoding
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	//chunked
	public static final String CHUNKED = "chunked";
	//Connection
	public static final String CONNECTION = "Connection";
    //Closed
	public static final String CLOSE = "close";
	
	public static final String ACL = "acl";
	
	/* All Supported Operation types */
	public static final String OPERATION_GET_SERVICE = "GetService";
	public static final String OPERATION_GET_BUCKET = "GetBucket";
	public static final String OPERATION_PUT_BUCKET = "PutBucket";
	public static final String OPERATION_DELETE_BUCKET = "DeleteBucket";
	
	public static final String OPERATION_GET_OBJECT = "GetObject";
	public static final String OPERATION_PUT_OBJECT = "PutObject";
	public static final String OPERATION_DELETE_OBJECT = "DeleteObject";
	public static final String OPERATION_HEAD_OBJECT = "HeadObject";
	
	public static final String OPERATION_PUT_ACL = "PutACL";
	public static final String OPERATION_GET_ACL = "GetACL";
	
	public static final String OPERATION_LOGIN = "Login";
	public static final String OPERATION_LOGOUT = "Logout";
	
	public static final String OPERATION_ADD_BIGQUERY = "AddBigQuery";
	public static final String OPERATION_DELETE_BIGQUERY = "DeleteBigQuery";
	public static final String OPERATION_GET_BIGQUERY = "GetBigQuery";
	public static final String OPERATION_EXECUTE_BIGQUERY = "ExecuteBigQuery";
	public static final String OPERATION_ADD_HIVE = "AddHiveDefinition";
	
	/* HTTP method constants */
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	
	public static final String CHARSET_NAME = "UTF-8";
	
	public static final String EMPTY_STRING = "";
	
	public static final String TASK_CREATE_BUCKET = "Create Bucket";
	public static final String TASK_DELETE_BUCKET = "Delete Bucket";
	public static final String TASK_CREATE_OBJECT = "Create Object";
	public static final String TASK_DELETE_OBJECT = "Delete Object";
	
	public static final String STREAM_BUFFER_SIZE = "STREAM_BUFFER_SIZE";
	public static final String ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
	
	public static final String AES256 = "AES256";
	public static final String SNAPPY = "SNAPPY";
	public static final String GZ = "GZ";
	public static final String LZ4 = "LZ4";
	
	public static final String SUN_SECURITY_KRB5_DEBUG = "sun.security.krb5.debug";
	public static final String JAVA_SECURITY_KRB5_REALM = "java.security.krb5.realm";
	public static final String JAVA_SECURITY_KRB5_KDC = "java.security.krb5.kdc";
	public static final String JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";
	public static final String JAVAX_SECURITY_AUTH_USESUBJECTCREDSONLY = "javax.security.auth.useSubjectCredsOnly";
	
	public static final String SERVICE_PRINCIPAL_NAME = "service.principal.name";
	
	public static final String USER_REALM_SEPERATOR = "@";
	
	public static final String LOGOUT = "logout";
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String AUTHORIZATION = "authorization";
	public static final String UNZIP = "unzip";
	public static final String FETCH_METADATA = "fetch-metadata";
	
	public static final String USE_KERBEROS_KEY = "useKerberos";
	
	public static final String DFS_NAMENODE_USER_NAME_KEY = DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY;
	
	public static final String META_TAG_PREFIX = "x-amz-meta-";
	
	public static String POOL_NAME = "";
	
	public static final String OWNER = "owner";
	public static final String GROUP = "group";
	public static final String PERMISSION = "permission";
}
