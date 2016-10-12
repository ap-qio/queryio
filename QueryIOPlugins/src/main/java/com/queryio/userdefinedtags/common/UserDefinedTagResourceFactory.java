package com.queryio.userdefinedtags.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.json.simple.JSONObject;

import com.queryio.common.ClassPathUtility;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.TableConstants;
import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.IDataTagParser;

public class UserDefinedTagResourceFactory {
	private static String DEFAULTCREATESTATEMENT = "CREATE TABLE";
	private static String DEFAULTNAMENODEID = "";
	private static Log LOG = LogFactory
			.getLog(UserDefinedTagResourceFactory.class);
	private static CustomTagDBConfig _DBCONFIG = null;

	public static void initConnectionPool(Configuration conf, boolean isMetadata) throws Exception {
		LOG.info("initConnectionPool");
		String dbPoolName = null;
		if (!isMetadata)
			dbPoolName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
		if (dbPoolName == null)
			dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
		String xmlFilePath = conf
				.get(QueryIOConstants.CUSTOM_TAG_DB_DBCONFIGPATH, System.getenv("HADOOP_YARN_HOME") +"/../"+QueryIOConstants.QUERYIOAGENT_DIR_NAME+"/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/conf/dbconfig.xml");
		
		System.out.println("DBConfig.xml path: " + System.getenv("HADOOP_YARN_HOME") +"/../"+QueryIOConstants.QUERYIOAGENT_DIR_NAME+"/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/conf/dbconfig.xml");
		
		LOG.info("xmlFilePath: " + xmlFilePath);
		String jdbcDriverPath = xmlFilePath.substring(0,
				xmlFilePath.lastIndexOf("/"))
				+ "/../jdbcJars";
		EnvironmentalConstants.setJdbcDriverPath(jdbcDriverPath);
		LOG.info("jdbcDriverPath: " + jdbcDriverPath);
		new DatabaseConfigParser().loadDatabaseConfiguration(xmlFilePath);
		CoreDBManager.initializeCustomTagDBConnection(dbPoolName);
	}

	public static void initConnection(Configuration conf, boolean isMetadata) throws Exception {
		LOG.info("initConnection");
		String dbPoolName = null;
		if (!isMetadata)
			dbPoolName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
		if (dbPoolName == null)
			dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
		String xmlFilePath = conf
				.get(QueryIOConstants.CUSTOM_TAG_DB_DBCONFIGPATH, System.getenv("HADOOP_YARN_HOME") +"/../"+QueryIOConstants.QUERYIOAGENT_DIR_NAME+"/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/conf/dbconfig.xml");
		LOG.info("xmlFilePath: " + xmlFilePath);
		String jdbcDriverPath = xmlFilePath.substring(0,
				xmlFilePath.lastIndexOf("/"))
				+ "/../jdbcJars";
		EnvironmentalConstants.setJdbcDriverPath(jdbcDriverPath);
		LOG.info("jdbcDriverPath: " + jdbcDriverPath);
		new DatabaseConfigParser().loadDatabaseConfiguration(xmlFilePath);
		_DBCONFIG = CustomTagDBConfigManager.getConfig(dbPoolName);

		DBManager.getDriver(_DBCONFIG.getCustomTagDriverJarPath(),
				_DBCONFIG.getCustomTagDriverClass());
	}

	public static Connection getConnectionWithPoolInit(Configuration conf, boolean isMetadata)
			throws Exception {
		String dbPoolName = null;
		if (!isMetadata)
			dbPoolName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
		if (dbPoolName == null)
			dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
		LOG.info("getConnectionWithPoolInit dbPoolName: " + dbPoolName);
		Connection connection = null;
		if (dbPoolName != null && !dbPoolName.isEmpty()) {
			try {
				connection = DatabaseManager.getConnection(dbPoolName);
			} catch (Exception e) {
				initConnectionPool(conf, isMetadata);
				connection = DatabaseManager.getConnection(dbPoolName);
			}
		}
		return connection;
	}
	
	public static Connection getConnection(String dbPoolName, boolean isMetadata)
			throws Exception {
		Connection connection = null;
		if (dbPoolName != null && !dbPoolName.isEmpty()) {
			connection = DatabaseManager.getConnection(dbPoolName);
		}
		return connection;
	}

	public static Connection getConnectionWithoutPoolInit(Configuration conf)
			throws Exception {
		Connection connection = null;
		connection = DriverManager.getConnection(_DBCONFIG.getCustomTagUrl(),
				_DBCONFIG.getCustomTagUserName(),
				_DBCONFIG.getCustomTagPassword());
		return connection;
	}

	public static void removeConnectionPool(Configuration conf, boolean isMetadata)
			throws Exception {
		LOG.info("removeConnectionPool");
		String dbPoolName = null;
		if (!isMetadata)
			dbPoolName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
		if (dbPoolName == null)
			dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
		DatabaseManager.removeConnectionPool(dbPoolName);
	}

	public static String getNamenodeId(Configuration conf) {
		return conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID, DEFAULTNAMENODEID);
	}

	/**
	 * Returns a connection object o the DB
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	public static Connection getConnection(String dbUrl, String driverClass,
			String dbUsername, String dbPassword)
			throws ClassNotFoundException, SQLException {
		Class.forName(driverClass);

		if (dbUsername == null) {
			return DriverManager.getConnection(dbUrl);
		} else {
			return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		}
	}

//	public static IDataTagParser getParser(Configuration conf,
//			String filePath) throws Exception{
//		Class<? extends IDataTagParser> parserClass = null;
//		IDataTagParser parser = null;
//		if (conf != null) {
//			// read ITagParser Implementation based upon filetype provided
//			// in user configuration,
//			// default is com.queryio.hadoop.hdfs.itag.CustomTagParserDefault
//			String fileType = UserDefinedTagUtils.getFileExtension(filePath);
//			String parserFileTypes = conf.get(
//					QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES, "");
//
//			boolean foundParser = false;
//			if (!parserFileTypes.isEmpty())
//			{
//				for (String str : parserFileTypes.split(",")) {
//					if (str.equals(fileType)) {
//						foundParser = true;
//						break;
//					}
//				}
//			}
//
//			if (foundParser) {
//				String className = conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + fileType);
//				parserClass = (Class<? extends IDataTagParser>) ClassPathUtility.getClass(className);
//				
//				parser = (IDataTagParser) ReflectionUtils.newInstance(parserClass, conf);
//			}
//		}		
//		return parser;
//	}
	
	public static IDataTagParser getParserFromConstructor(Configuration conf, String filePath, JSONObject tagInfo, Map<String, String> coreTags) throws Exception{
		Class<? extends IDataTagParser> parserClass = null;
		IDataTagParser parser = null;
		if (conf != null) {
			// read ITagParser Implementation based upon filetype provided
			// in user configuration,
			// default is com.queryio.hadoop.hdfs.itag.CustomTagParserDefault
			String fileType = UserDefinedTagUtils.getFileExtension(filePath);
			String parserFileTypes = conf.get(
					QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES, "");

			boolean foundParser = false;
			if (!parserFileTypes.isEmpty())
			{
				for (String str : parserFileTypes.split(",")) {
					if (str.equals(fileType)) {
						foundParser = true;
						break;
					}
				}
			}
			String className = null;
			if (foundParser) {
				className = conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + fileType);
				parser = getTagParserInstance(tagInfo, coreTags, parser, className);				
			}
		}		
		return parser;
	}

	@SuppressWarnings("unchecked")
	private static IDataTagParser getTagParserInstance(JSONObject tagInfo,
			Map<String, String> coreTags, IDataTagParser parser,
			String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		Class<? extends IDataTagParser> parserClass;
		parserClass = (Class<? extends IDataTagParser>) ClassPathUtility
				.getClass(className);

		if (parserClass != null) {
			try {
				parser = (AbstractDataTagParser) parserClass.getConstructor(
						JSONObject.class, Map.class).newInstance(tagInfo,
						coreTags);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return parser;
	}

	public static IDataTagParser getParserDataTaggingJob(String fileExtension, JSONObject fileTypeParsers, Configuration conf, JSONObject tagInfo, Map<String, String> coreTags) throws Exception{
		Class<? extends IDataTagParser> parserClass = null;
		IDataTagParser parser = null;
		if (fileTypeParsers != null) {

			Iterator fileTypes = fileTypeParsers.keySet().iterator();
			String fileType = null;
			while (fileTypes.hasNext())
			{
				fileType = String.valueOf(fileTypes.next());
//				String whichLogFile = fileType;
//				if (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG) || fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
//					whichLogFile = QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase();
//				if (whichLogFile.equalsIgnoreCase(fileExtension))
				if (fileType.equalsIgnoreCase(fileExtension))
				{
					String className = String.valueOf(fileTypeParsers.get(fileType));
					parserClass = (Class<? extends IDataTagParser>) ClassPathUtility.getClass(className);
					if (parserClass != null) {
						try {
							parser = (AbstractDataTagParser) parserClass.getConstructor(JSONObject.class, Map.class).newInstance(tagInfo, coreTags);
						} catch(Exception e) {
							e.printStackTrace();
//							AppLogger.getLogger().fatal("Parser class could not be initialized.", e);
//							throw new IOException("Parser class could not be initialized,", e);
						}
					}
					break;
				}
			}
		}		
		return parser;
	}

	public static String getTableName(IDataTagParser parser, String fileName) {
		if (parser != null) {
			return "DATATAGS_" + parser.getTableMetaData(UserDefinedTagUtils.getFileExtension(fileName)).getTableName();
		} else {
			return TableConstants.TABLE_HDFS_METADATA;
		}
	}

	public static String getCreateTableStatement(Configuration conf) {
		return conf.get("queryio.bigquery.db.insert-statement",
				DEFAULTCREATESTATEMENT);
	}
}
