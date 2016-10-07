package com.queryio.core.adhoc;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.customtags.BigQueryDAO;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.dao.HiveTableDAO;
import com.queryio.job.definition.JobDefinitionDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class AdHocQueryManager
{
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryCSV(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String delimiter, String valueSeparator, boolean isFirstRowHeader, String encoding , String isSkipAllRecordsString)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_DELIMITER, delimiter);
		arguments.put(QueryIOConstants.ADHOC_PARSER_VALUESEPARATOR, valueSeparator);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ISFIRSTROWHEADER, isFirstRowHeader);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		boolean isSkipAllRecords = Boolean.valueOf(isSkipAllRecordsString);
		arguments.put(QueryIOConstants.ADHOC_PARSER_SKIP_ALL_RECORDS , isSkipAllRecords);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_CSV, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryLOG(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String pattern, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_LOG_PATTERN, pattern);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_LOG, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryAccessLog(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String pattern, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_LOG_PATTERN, pattern);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_ACCESSLOG, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryIISLOG(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String delimiter, boolean isFirstRowHeader, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_DELIMITER, delimiter);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ISFIRSTROWHEADER, isFirstRowHeader);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_IISLOG, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryJSON(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_JSON, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryKVPairs(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String delimiter, String valueSeparator, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_DELIMITER, delimiter);
		arguments.put(QueryIOConstants.ADHOC_PARSER_VALUESEPARATOR, valueSeparator);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_PAIRS, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryMBOX(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_MBOX, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryRegex(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String pattern, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_REGEX_PATTERN, pattern);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_REGEX, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static DWRResponse addAdHocQueryXML(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields, String nodeName, String encoding)
	{
		JSONObject arguments = new JSONObject();
		arguments.put(QueryIOConstants.ADHOC_PARSER_XML_NODENAME, nodeName);
		arguments.put(QueryIOConstants.ADHOC_PARSER_ENCODING, encoding);
		
		return addAdHocQuery(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, QueryIOConstants.ADHOC_TYPE_XML, adHocTableName, fileName, filePathPattern, fields, encoding, arguments.toJSONString());
	}
	
	public static DWRResponse addAdHocQuery(String adHocId, String nameNodeId, String rmId,
			String sourcePath, boolean parseRecursive, String type, String adHocTableName, String fileName, String filePathPattern, String fields, String encoding, String arguments)
	{
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		Connection customTagConnection = null;
		Connection hiveConnection = null;
		
		// FileName will be fake path i.e. C:\fakepath\sample.jtl, Trim path and
		// use file name only.
		if(fileName !=null){			
			int lastIndexOfSeparator = fileName.lastIndexOf('\\');
			if (lastIndexOfSeparator != -1) {
				fileName = fileName.substring(lastIndexOfSeparator + 1);
			}
		}
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			Configuration config = ConfigurationManager.getConfiguration(connection, nameNodeId);
			String connectionName = config.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			
			if (connectionName == null)
				connectionName = config.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			
			customTagConnection = CoreDBManager.getCustomTagDBConnection(connectionName);
			
			if (adHocTableName != null)
			{
//				adHocTableName = adHocTableName.toUpperCase();
				adHocTableName = adHocTableName.toLowerCase();
			}
			
			if (encoding != null)
				encoding = encoding.trim();

			if (fields != null)
				fields = fields.replace(" ", "");
			
			ArrayList<String> colNames = new ArrayList<String>();
			ArrayList<String> colTypes = new ArrayList<String>();
			ArrayList<String> colTypesHive = new ArrayList<String>();
			ArrayList<String> colIndex = new ArrayList<String>();
			JSONParser parser = new JSONParser();
			JSONArray array = (JSONArray) parser.parse(fields);
			
			JSONObject object = null;
			
			String dtType = "";
			String dataType = "";
			int size = 0;
			for (int i=0; i<array.size(); i++)
			{
				object = (JSONObject) array.get(i);
				String cName = String.valueOf(object.get("colName")).trim();
				
				colNames.add(cName);
				
				dtType = String.valueOf(object.get("colType")).trim();
				colTypesHive.add(dtType);
				if(dtType.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
				{
					dataType = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null).getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(MetadataConstants.GENERIC_DATA_TYPE_STRING));
					size = getSizeFromString(dtType);
					colTypes.add(dataType + " (" + size + ")");
				}
				else
				{
					dataType = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null).getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(dtType));
					colTypes.add(dataType);
				}
				
				colIndex.add(String.valueOf(object.get("colIndex")).trim());
			}
			if (!UserDefinedTagDAO.checkIfTableExists(customTagConnection, adHocTableName))
			{
				JobDefinitionDAO.createDatabaseTable(customTagConnection, adHocTableName, colNames.toArray(new String[colNames.size()]), colTypes.toArray(new String[colTypes.size()]), config.get(QueryIOConstants.CUSTOM_TAG_DB_CREATESTMT));
				
				if (QueryIOConstants.ADHOC_TYPE_CSV.equals(type) || QueryIOConstants.ADHOC_TYPE_JSON.equals(type) || QueryIOConstants.ADHOC_TYPE_IISLOG.equals(type)
						|| QueryIOConstants.ADHOC_TYPE_PAIRS.equals(type) || QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equals(type) || QueryIOConstants.ADHOC_TYPE_REGEX.equals(type))
				{
					object = (JSONObject) parser.parse(arguments);
					
					String delimiter = String.valueOf(object.get(QueryIOConstants.ADHOC_PARSER_DELIMITER));
					String valueSeparator = String.valueOf(object.get(QueryIOConstants.ADHOC_PARSER_VALUESEPARATOR));
					String pattern = null;
					if(QueryIOConstants.ADHOC_TYPE_REGEX.equals(type)) {
						pattern = String.valueOf(object.get(QueryIOConstants.ADHOC_PARSER_REGEX_PATTERN));
					} else {						
						pattern = String.valueOf(object.get(QueryIOConstants.ADHOC_PARSER_LOG_PATTERN));
					}
					
					AppLogger.getLogger().debug("pattern : " + pattern);
					
					boolean ifFirstHeader = false;					
					Object first = object.get(QueryIOConstants.ADHOC_PARSER_ISFIRSTROWHEADER);
					if (first != null) {
						ifFirstHeader = (Boolean) first;
					}
					
					String fsDefaultName = config.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
					String location = fsDefaultName.concat(sourcePath);
					
					hiveConnection = AdHocHiveClient.getHiveConnection(connection, nameNodeId);
					
					int retryCount = 0;
					int maxRetryCount = 2;
					for(int i = 0; i < maxRetryCount; i++) {
						retryCount ++;
						try
						{
							if (retryCount > 0) {
								// table from PostgreSQL got deleted in exception, so re-create this table also.
								JobDefinitionDAO.createDatabaseTable(customTagConnection, adHocTableName, colNames.toArray(new String[colNames.size()]), colTypes.toArray(new String[colTypes.size()]), config.get(QueryIOConstants.CUSTOM_TAG_DB_CREATESTMT));
							}
							AdHocHiveClient.createHiveTable(hiveConnection, AdHocHiveClient.getQueryString(type, adHocTableName, colNames.toArray(new String[colNames.size()]), colTypesHive.toArray(new String[colTypesHive.size()]), colIndex.toArray(new String[colIndex.size()]), delimiter, valueSeparator, pattern, location, ifFirstHeader));
							break;	// Break the loop if no error occurs. This is to fix add Hive entry issue in case of UI server restart.
						}
						catch (Exception e)
						{
							// Drop table if any error occurs.
							BigQueryDAO.dropBigQueryTable(customTagConnection, adHocTableName);
							AdHocHiveClient.dropHiveTable(adHocTableName, nameNodeId);
							if(retryCount == maxRetryCount) {								
								throw(e);
							}
						}
					}
				}
				HiveTableDAO.insertHiveTable(connection, adHocTableName, nameNodeId, rmId, type, fileName);
				
				if (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(type))
				{
					HiveTableDAO.insertHiveTable(connection, adHocTableName, nameNodeId, rmId, type, fileName);
				}
			}
			
			AdHocQueryDAO.addAdHocQueryInfo(connection, adHocId, nameNodeId, rmId, sourcePath, parseRecursive, type, adHocTableName, filePathPattern, fields, encoding, arguments);

			JobDefinitionDAO.addJobDefinition(connection, adHocId, adHocTableName);
			
			String responseMsg = QueryIOConstants.ADHOC_QUERY_SUCCESS;
			responseMsg = responseMsg.replace("$FileType$", type);
			dwrResponse.setDwrResponse(true, responseMsg, 200);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, QueryIOConstants.ADHOC_QUERY_FAILED + " Reason: " + e.getMessage(), 500);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try
			{
				CoreDBManager.closeConnection(customTagConnection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try
			{
				DatabaseManager.closeDbConnection(hiveConnection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing hive connection.", e);
			}
		}
		
		return dwrResponse;
	}
	
	private static int getSizeFromString(String type)
	{
		String size = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"));
		
		return Integer.parseInt(size);
	}
	
	public static void main(String args[])
	{
		System.out.println(getSizeFromString("STRING (255)"));
	}
	
	public static DWRResponse updateAdHocQuery(String adHocId, String nameNodeId, String rmId,
			String filePathPattern, String encoding)
	{
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		Connection customTagConnection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			Configuration config = ConfigurationManager.getConfiguration(connection, nameNodeId);
			String connectionName = config.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			
			if (connectionName == null)
				connectionName = config.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			
			customTagConnection = CoreDBManager.getCustomTagDBConnection(connectionName);
			
			if (encoding != null)
				encoding = encoding.trim();
			
			AdHocQueryDAO.updateAdHocQueryInfo(connection, adHocId, rmId, filePathPattern, encoding);
			
			dwrResponse.setDwrResponse(true, QueryIOConstants.ADHOC_QUERY_UPDATE_SUCCESS, 200);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, QueryIOConstants.ADHOC_QUERY_UPDATE_FAILED + " Reason: " + e.getMessage(), 500);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try
			{
				CoreDBManager.closeConnection(customTagConnection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return dwrResponse;
	}
	
	@SuppressWarnings("rawtypes")
	public static DWRResponse deleteAdHocQuery(ArrayList adHocIdList)
	{
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			StringBuilder adHocIds = null;
			if ((adHocIdList != null) && (adHocIdList.size() > 0))
			{
				adHocIds = new StringBuilder();
				String id = null;
				for (int i=0; i<adHocIdList.size() - 1; i++)
				{
					id = String.valueOf(adHocIdList.get(i));
					adHocIds.append("'" + id + "'");
					adHocIds.append(",");
				}
				id = String.valueOf("'" + adHocIdList.get(adHocIdList.size() - 1) + "'");
				adHocIds.append(id);

				AdHocQueryDAO.deleteAdHocQueryInfoFromList(connection, adHocIds.toString());
				JobDefinitionDAO.deleteJobDefinitionFromList(connection, adHocIds.toString());
				
				dwrResponse.setDwrResponse(true, QueryIOConstants.ADHOC_QUERY_DELETED_SUCCESS, 200);
			}
			else
			{
				dwrResponse.setDwrResponse(false, QueryIOConstants.ADHOC_QUERY_DELETED_FAILED + " Reason: No adHoc Id specified for delete.", 500);
			}
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false, QueryIOConstants.ADHOC_QUERY_DELETED_FAILED + " Reason: " + e.getLocalizedMessage(), 500);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return dwrResponse;
	}
	
//	@SuppressWarnings("rawtypes")
//	public static ArrayList getAdHocQueryInfoAll()
//	{
//		Connection connection = null;
//		try
//		{
//			connection = CoreDBManager.getQueryIODBConnection();
//			return AdHocQueryDAO.getAdHocQueryInfoAllOld(connection);
//		}
//		catch (Exception e)
//		{
//			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//		}
//		finally
//		{
//			try
//			{
//				CoreDBManager.closeConnection(connection);
//			}
//			catch (Exception e)
//			{
//				AppLogger.getLogger().fatal("Error closing database connection.", e);
//			}
//		}
//		
//		return null;
//	}
	
	public static JSONObject getAdHocQueryInfoAll() {
		return getAdHocQueryInfoAll(null);
	}
	
	//Added for server side pagenation
	
	public static JSONObject getAdHocQueryInfoAll(String params) {
		Connection connection = null;
		AppLogger.getLogger().debug("getAdHocQueryInfoAll ");
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			if(params == null) {
				 return AdHocQueryDAO.getAdHocQueryInfoAll(connection);
			} else { 
				return AdHocQueryDAO.getAdHocQueryInfoAll(connection, params);
			}
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}
	
	public static AdHocQueryBean getAdHocQueryArguments(String namenode, String tableName)
	{
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			return AdHocQueryDAO.getAdHocQueryArguments(connection, namenode, tableName);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getDefaultColumns(String type)
	{
		JSONObject columnList = new JSONObject();
		JSONArray array = new JSONArray();
		String[] colNames = new String[]{"FILEPATH", "CATEGORY", "CLASS", "DATE", "FILE", "LINE", "LOCATION", "MDC", "MESSAGE", "METHOD", "ELAPSED", "NDC", "PRIORITY", "SEQUENCE", "THREAD"};
		String[] sqlTypes = new String[]{
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(1280)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)",
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(5000)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)", 
				MetadataConstants.GENERIC_DATA_TYPE_STRING + "(128)"};
		JSONObject object = null;
		
		if (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(type))
		{
			for (int i=0; i<colNames.length; i++)
			{
				object = new JSONObject();
				object.put("colName", colNames[i]);
				object.put("colType", sqlTypes[i]);
				object.put("colIndex", i);

				array.add(object);
			}
		}
		else			// For CSV
		{
			object = new JSONObject();
			object.put("colName", colNames[0]);
			object.put("colType", sqlTypes[0]);
			object.put("colIndex", 0);
			
			array.add(object);
		}
		
		columnList.put("fields", array);
		return columnList;
	}



	
//	private static void loadDataHive(String nameNodeId, String filePathPattern, String tableName)
//	{
//		Connection connection = null;
//		Connection hiveConnection = null;
//		FileSystem dfs = null;
//		Configuration conf = null;
//		List<FileStatus> files = new ArrayList<FileStatus>();
//		Path p = new Path("/");
//		List<IOException> errors = new ArrayList<IOException>();
//
//		try
//		{
//			connection = CoreDBManager.getQueryIODBConnection();
//			
//			Node node = NodeDAO.getNode(connection, nameNodeId);
//			if(node==null)	return;
//			
//			Host host = HostDAO.getHostDetail(connection, node.getHostId());
//			ArrayList list = new ArrayList();
//			list.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
//			ArrayList result = QueryIOAgentManager.getConfig(host, list, node, "core-site.xml");
//			if (result == null) {
//				return;
//			}
//			if (result.size() == 0) {
//				return;
//			}
//			
//			String fsDefaultName = (String)result.get(0);
//			
////			if(EnvironmentalConstants.isUseKerberos())
////			{
////				User us = UserDAO.getUserDetail(connection, user);
////				
////				conf = ConfigurationManager.getKerberosConfiguration(connection, nameNodeId);
////				
////				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("User: " + us.getUserName() + ",password: " + SecurityHandler.decryptData(us.getPassword()));
////				
////				try{
////					UserGroupInformation.setConfiguration(conf);
////					UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));
////					
////					dfs = FileSystem.get(new URI(fsDefaultName), conf);
////				}
////				catch(Exception e){
////					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(), e);
////					return;
////				}
////			}
////			else {
//				conf = ConfigurationManager.getConfiguration(connection, nameNodeId); 
//				dfs = FileSystem.get(URI.create(fsDefaultName), conf);
////			}
//			
//			PathFilter hiddenFileFilter = new PathFilter() {
//				public boolean accept(Path p) {
//					String name = p.getName();
//					return !name.startsWith("_") && !name.startsWith(".")
//							&& !name.startsWith("job_") && !name.equalsIgnoreCase(AdHocHiveClient.warehouseDir);
//				}
//			};
//			
//			files = getFiles(dfs, p, hiddenFileFilter, filePathPattern, errors);
//			
//			if (!errors.isEmpty()) {
//				AppLogger.getLogger().fatal(errors);
//			}
//			
//			if (files == null) {
//				return;
//			}
//			if (files.size() == 0) {
//				return;
//			}
//			
//			AppLogger.getLogger().debug("Total input paths to process : " + files.size());
//			
//			hiveConnection = AdHocHiveClient.getHiveConnection();
//			
//			Path tempPath = new Path("/tempWarehouse");
//			dfs.create(tempPath);
//			for (FileStatus file : files) {
//				Path path = file.getPath();
//				FileUtil.copy(dfs, path, dfs, tempPath, false, conf);
//			}
//			
//			AdHocHiveClient.loadFileEntry(connection, fsDefaultName + tempPath.toString(), tableName);
//		}
//		catch (Exception e)
//		{
//			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//		}
//		finally
//		{
//			try
//			{
//				CoreDBManager.closeConnection(connection);
//			}
//			catch (Exception e)
//			{
//				AppLogger.getLogger().fatal("Error closing database connection.", e);
//			}
//			try
//			{
//				CoreDBManager.closeConnection(hiveConnection);
//			}
//			catch (Exception e)
//			{
//				AppLogger.getLogger().fatal("Error closing database connection.", e);
//			}
//		}
//	}
//	
//	private static List<FileStatus> getFiles(FileSystem fs, Path p,
//			PathFilter inputFilter, String filePathPattern, List<IOException> errors)
//			throws IOException {
//		List<FileStatus> result = new ArrayList<FileStatus>();
//		FileStatus[] matches = fs.globStatus(p, inputFilter);
//		if (matches == null) {
//			errors.add(new IOException("Input path does not exist: " + p));
//		} else if (matches.length == 0) {
//			errors.add(new IOException("Input Pattern " + p
//					+ " matches 0 files"));
//		} else {
//			for (FileStatus globStat : matches) {
//				if (globStat.isDirectory()) {
//					for (FileStatus stat : fs.listStatus(globStat.getPath(),
//							inputFilter)) {
//						if (stat.isDirectory()) {
//							result.addAll(getFiles(fs, stat.getPath(),
//									inputFilter, filePathPattern, errors));
//						} else {
//							if (filePathPattern != null) {
//								if(stat.getPath().toString().matches(filePathPattern))
//									result.add(stat);
//							} else {
//								result.add(stat);
//							}
//						}
//					}
//				} else {
//					if (filePathPattern != null) {
//						if(globStat.getPath().toString().matches(filePathPattern))
//							result.add(globStat);
//					} else {
//						result.add(globStat);
//					}
//				}
//			}
//		}
//		return result;
//	}
	
}