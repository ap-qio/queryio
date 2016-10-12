package com.queryio.config.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.database.TableDDLExporter;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.CryptManager;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.dao.NodeDAO;
import com.queryio.database.migration.DBMigrator;

public class DBConfigDAO {

	public static boolean isCustomTagDB(String connectionName) {
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		NodeList connectionList = null;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes();
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2) {
				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName()))
				{
					if (node.getAttributes()
							.getNamedItem(QueryIOConstants.DB_NAME_TYPE)
							.getTextContent()
							.equals(connectionName)) {
						return true;
					}
				}
			}

		} catch (Exception e) {
			AppLogger
					.getLogger()
					.debug("Exception at isCustomTagDB(String connectionName): ",
							e);
		}
		return false;

	}

	public static DBConfigBean getConnectionDetail(
			String connectionName) {

		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		NodeList connectionList = null;
		DBConfigBean dbBean = null;
		Connection connection = null;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes();
			
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2)
			{
				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName()))
				{
					if (node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE) != null)
					{
						if (connectionName.equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent()))
						{
							dbBean = new DBConfigBean();
							dbBean.setConnectionName(connectionName);
							
							dbBean.setMigrated(true);
							dbBean.setCustomTagDB(true);
							if (QueryIOConstants.DB_ROLE_METADATA.equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_ROLE).getTextContent()))
								dbBean.setIsPrimary(true);
							else
								dbBean.setIsPrimary(false);
							dbBean.setConnectionType(node.getAttributes().getNamedItem(QueryIOConstants.DB_TYPE).getTextContent());
							
							NodeList connectionDetailList = node.getChildNodes();
							for (int j = 0; j < connectionDetailList.getLength(); j++) {
								if (connectionDetailList.item(j).getNodeName()
										.equals("driver")) {
									dbBean.setPrimaryDriverName(connectionDetailList
											.item(j).getTextContent());
								} else if (connectionDetailList.item(j).getNodeName()
										.equals("userName")) {
									dbBean.setPrimaryUsername(connectionDetailList
											.item(j).getTextContent());
								} else if (connectionDetailList.item(j).getNodeName()
										.equals("password")) {
									String encryptedPassword = connectionDetailList.item(j).getTextContent();
									CryptManager cm = CryptManager.createInstance();
									 String password = cm.decryptData(encryptedPassword);
									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("connectionDetailList.item(j).getTextContent() "
												+ connectionDetailList.item(j).getTextContent());
									dbBean.setPrimaryPassword(password);
//									
//									dbBean.setPrimaryPassword(connectionDetailList
//											.item(j).getTextContent());
								} else if (connectionDetailList.item(j).getNodeName()
										.equals("url")) {
									dbBean.setPrimaryConnectionURL(connectionDetailList
											.item(j).getTextContent());
								} else if (connectionDetailList.item(j).getNodeName()
										.equals("driverJar")) {
									dbBean.setPrimaryJdbcJar(connectionDetailList.item(
											j).getTextContent());
								}
								else if (connectionDetailList.item(j).getNodeName()
										.equals("maxconnections")) {
									dbBean.setMaxConnection(Long.parseLong(connectionDetailList.item(
											j).getTextContent()));
									
								}
								else if (connectionDetailList.item(j).getNodeName()
										.equals("maxidleconnections")) {
									dbBean.setMaxIdleConnection(Long.parseLong(connectionDetailList.item(
											j).getTextContent()));
									
								}
								else if (connectionDetailList.item(j).getNodeName()
										.equals("waittimeinmillis")) {
									dbBean.setWaitTimeinMillis(Long.parseLong(connectionDetailList.item(
											j).getTextContent()));
									
								}
							}
							
							break;
						}
					}
				}
			}
			connection = CoreDBManager.getQueryIODBConnection();
			if (dbBean != null)
			{
				if (dbBean.isIsPrimary())
					dbBean.setNameNodeId(NodeDAO.getNameNodeForDBNameMapping(connection,connectionName));
				else
					dbBean.setNameNodeId(NodeDAO.getNameNodeForAnalyticsDBNameMapping(connection,connectionName));
			}
			
		} catch (Exception e) {
			AppLogger
					.getLogger()
					.debug("Exception at getConnectionDetail(String connectionName): ",
							e);
		}finally{
			try {
				if(connection!=null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing database connection.", e);
			}
		}
		
		return dbBean;
	}
	
	
	public static ArrayList getAllDBNameForOperation(final Connection connection) {
		ArrayList dbNames=new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
				
		try{
			
				statement = DatabaseFunctions.getStatement(connection);
			
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_DBNAME_MODE_FOR_OPERATION);
			while(rs.next())
			{
				ArrayList arr = new ArrayList();
				arr.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME));
				arr.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME));
				arr.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS));
				dbNames.add(arr);
			}
		}catch (Exception e) {
			AppLogger.getLogger().fatal("Error in fetching database name: " + e.getMessage(), e);
		}
		finally{
			try
			{
				DatabaseFunctions.closeSQLObjects(statement, rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		
		
		return dbNames;
		
	}

	public static ArrayList getAllDBNameMode(boolean isHiveViewSelected) {
		ArrayList arr = null;
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		NodeList connectionList = null;
		try {

			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes(); 	//TODO
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2) {
				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName()))
				{
					String[] dbNameMode = new String[3];
					dbNameMode[0] = node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent();
					
					if (QueryIOConstants.DB_ROLE_METADATA.equalsIgnoreCase(node.getAttributes().getNamedItem(QueryIOConstants.DB_ROLE).getTextContent()))
					{
						dbNameMode[1] = QueryIOConstants.DB_ROLE_METADATA;
					}
					else
					{
						dbNameMode[1] = QueryIOConstants.DB_ROLE_ANALYTICS;
					}
					dbNameMode[2] = node.getAttributes().getNamedItem(QueryIOConstants.DB_TYPE).getTextContent();
					
					if (arr == null) {
						arr = new ArrayList();
					}
					if (isHiveViewSelected)
					{
						if (QueryIOConstants.DB_ROLE_ANALYTICS.equalsIgnoreCase(dbNameMode[1]))
							arr.add(dbNameMode);
						continue;
					}
					arr.add(dbNameMode);
				}
			}
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(document);
			result = new StreamResult(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			transformer.transform(source, result);
		} catch (Exception e) {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
					"Exception caught while fetching DB Connections", e);
		}
		return arr;
	}
	
	public static ArrayList getAllCustomDBConnectionName(boolean isPrimary) {
		ArrayList arr = null;
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		NodeList connectionList = null;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes();
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2) {
				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName()))
				{
					if (arr == null) {
						arr = new ArrayList();
					}
					
					if (isPrimary)
					{
						if (QueryIOConstants.DB_ROLE_METADATA.equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_ROLE).getTextContent()))
							arr.add(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent());	
					}
					else
					{
						if (QueryIOConstants.DB_ROLE_ANALYTICS.equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_ROLE).getTextContent()))
							arr.add(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent());
					}
				}
			}
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(document);
			result = new StreamResult(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			transformer.transform(source, result);
		} catch (Exception e) {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
					"Exception caught while fetching DB Connections", e);
		}
		return arr;
	}

	private static void createMonitoringXMLNode(Document document,
			Element newConnection, String connectionURL, String username,
			String Password, String driverName, String jarFileName,
			long maxConnections2, long maxIdleConnections2, long waitTimeMilliSeconds, String connectionName) {

		Element driver = document.createElement("driver");
		driver.appendChild(document.createTextNode(driverName));
		newConnection.appendChild(driver);

		Element url = document.createElement("url");
		url.appendChild(document.createTextNode(connectionURL));
		newConnection.appendChild(url);

		Element userName = document.createElement("userName");
		userName.appendChild(document.createTextNode(username));
		newConnection.appendChild(userName);

		Element password = document.createElement("password");
		password.appendChild(document.createTextNode(Password));
		newConnection.appendChild(password);

		Element poolName = document.createElement("poolname");
		poolName.appendChild(document.createTextNode(connectionName));
		newConnection.appendChild(poolName);

		Element maxconnections = document.createElement("maxconnections");
		maxconnections.appendChild(document.createTextNode(String.valueOf(maxConnections2)));
		newConnection.appendChild(maxconnections);

		Element maxidleconnections = document
				.createElement("maxidleconnections");
		maxidleconnections.appendChild(document.createTextNode(String.valueOf(maxIdleConnections2)));
		newConnection.appendChild(maxidleconnections);

		Element waittimeinmillis = document.createElement("waittimeinmillis");
		waittimeinmillis.appendChild(document.createTextNode(String.valueOf(waitTimeMilliSeconds)));
		newConnection.appendChild(waittimeinmillis);

		Element jarFile = document.createElement("driverJar");
		jarFile.appendChild(document.createTextNode(jarFileName));
		newConnection.appendChild(jarFile);
	}
	

	
	public static void addDBConnection(String connectionName, String connectionType,
			String primaryConnectionURL, String primaryUsername,
			String primaryPassword, String primaryDriverName,
			String jarFileName, boolean isPrimary, long maxConnections, long maxIdleConnections, long waitTimeMilliSeconds) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeForType = null;
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeForType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			Element newConnection = null;

			newConnection = document.createElement(QueryIOConstants.DEFAULT_CUSTOMTAG_DB);
			
			if (isPrimary)
			{	
				newConnection.setAttribute(QueryIOConstants.DB_ROLE, QueryIOConstants.DB_ROLE_METADATA);
			}
			else
			{
				newConnection.setAttribute(QueryIOConstants.DB_ROLE, QueryIOConstants.DB_ROLE_ANALYTICS);
			}
			
			newConnection.setAttribute(QueryIOConstants.DB_NAME_TYPE, connectionName);
			newConnection.setAttribute(QueryIOConstants.DB_TYPE, connectionType);
			
			createMonitoringXMLNode(document, newConnection,
					primaryConnectionURL, primaryUsername, primaryPassword,
					primaryDriverName, jarFileName, maxConnections, maxIdleConnections, waitTimeMilliSeconds, connectionName);
			rootNode.appendChild(newConnection);
			
			if(!doesTypeTagAlreadyExist(rootNodeForType, connectionType))
			{
				Element newConnectionForType = null;
				
				//Creating the head tag
				newConnectionForType = document.createElement(QueryIOConstants.DATABASE_TYPE);
				newConnectionForType.setAttribute(QueryIOConstants.DB_NAME_TYPE, connectionType);
				
				createDBConfigTypeTags(document, newConnectionForType, connectionName, connectionType);
				rootNodeForType.appendChild(newConnectionForType);
			}
			else
				AppLogger.getLogger().debug("DB configuration for data type already exists");
			
			
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			source = new DOMSource(document);
			result = new StreamResult(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			transformer.transform(source, result);
		} catch (Exception e) {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
					"Exception caught while saving DB Connection", e);
			throw e;
		}
	}
	
	private static void createDBConfigTypeTags(Document document, Element newConnection, String connectionName, String connectionType) throws Exception
	{
		DBTypeProperties dbProp = new DBTypeProperties();
		CustomTagDBConfigManager.addCustomTagDBTypeConfig(null, connectionType);
		dbProp = CustomTagDBConfigManager.getDatabaseDataTypeMap(null, connectionType);
		
		//Creating the child elements
		Element stringTag = document.createElement(MetadataConstants.DB_CONFIG_STRING_ENTRY_TAG);
		stringTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.STRING_WRAPPER_CLASS)));
		newConnection.appendChild(stringTag);
		
		Element timestampTag = document.createElement(MetadataConstants.DB_CONFIG_TIMESTAMP_ENTRY_TAG);
		timestampTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.TIMESTAMP_WRAPPER_CLASS)));
		newConnection.appendChild(timestampTag);
		
		Element longTag = document.createElement(MetadataConstants.DB_CONFIG_LONG_ENTRY_TAG);
		longTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.LONG_WRAPPER_CLASS)));
		newConnection.appendChild(longTag);

		Element shortTag = document.createElement(MetadataConstants.DB_CONFIG_SHORT_ENTRY_TAG);
		shortTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.SHORT_WRAPPER_CLASS)));
		newConnection.appendChild(shortTag);
		
		Element integerTag = document.createElement(MetadataConstants.DB_CONFIG_INTEGER_ENTRY_TAG);
		integerTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.INTEGER_WRAPPER_CLASS)));
		newConnection.appendChild(integerTag);

		Element realTag = document.createElement(MetadataConstants.DB_CONFIG_REAL_ENTRY_TAG);
		realTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.REAL_WRAPPER_CLASS)));
		newConnection.appendChild(realTag);
		
		Element doubleTag = document.createElement(MetadataConstants.DB_CONFIG_DOUBLE_ENTRY_TAG);
		doubleTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.DOUBLE_WRAPPER_CLASS)));
		newConnection.appendChild(doubleTag);
		
		Element decimalTag = document.createElement(MetadataConstants.DB_CONFIG_DECIMAL_ENTRY_TAG);
		decimalTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.DECIMAL_WRAPPER_CLASS)));
		newConnection.appendChild(decimalTag);
		
		Element booleanTag = document.createElement(MetadataConstants.DB_CONFIG_BOOLEAN_ENTRY_TAG);
		booleanTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.BOOLEAN_WRAPPER_CLASS)));
		newConnection.appendChild(booleanTag);
		
		Element blobTag = document.createElement(MetadataConstants.DB_CONFIG_BLOB_ENTRY_TAG);
		blobTag.appendChild(document.createTextNode(dbProp.getTypeMap().get(MetadataConstants.BLOB_WRAPPER_CLASS)));
		newConnection.appendChild(blobTag);
		
		Element schemaTag = document.createElement(MetadataConstants.DB_CONFIG_DEFAULT_SCHEMA_ENTRY_TAG);
		schemaTag.appendChild(document.createTextNode(dbProp.getDefaultSchema()));
		newConnection.appendChild(schemaTag);
		
	}
	
	private static boolean doesTypeTagAlreadyExist(Node rootNode, String connectionType)
	{
		NodeList connectionList = null;
		try
		{
			connectionList = rootNode.getChildNodes();
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2) 
			{
				Node node = connectionList.item(i);
				if (QueryIOConstants.DATABASE_TYPE.equals(node.getNodeName()))
				{
					if (connectionType.equalsIgnoreCase(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent()))
						return true;
				}
			}
		}
		catch (Exception e) 
		{
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Exception caught while fetching DB Connections", e);
		}
		return false;
	}
	
	public static void updateDBConnection(String connectionName,
			String primaryConnectionURL, String primaryUsername,
			String primaryPassword, String primaryDriverName, String jarFile,
			long maxConnections, long maxIdleConnections, long waitTimeMilliSeconds) {

		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		NodeList connectionList = null;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes();
			for (int i = 1; i <= connectionList.getLength() - 1; i += 2) {

				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName())) {
					if (connectionName.equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent()))
					{
						NodeList connectionDetailList = node.getChildNodes();
						for (int j = 0; j < connectionDetailList.getLength(); j++) {
							if (connectionDetailList.item(j).getNodeName()
									.equals("driver")) {
								node.getChildNodes().item(j)
										.setTextContent(primaryDriverName);
							} else if (connectionDetailList.item(j).getNodeName()
									.equals("userName")) {
								node.getChildNodes().item(j)
										.setTextContent(primaryUsername);
							} else if (connectionDetailList.item(j).getNodeName()
									.equals("password")) {
								node.getChildNodes().item(j)
										.setTextContent(CryptManager.createInstance().encryptData(primaryPassword));
							} else if (connectionDetailList.item(j).getNodeName()
									.equals("url")) {
								node.getChildNodes().item(j)
										.setTextContent(primaryConnectionURL);
							} else if (connectionDetailList.item(j).getNodeName()
									.equals("driverJar")) {
								if ( jarFile != null && !jarFile.equals("")) {
									node.getChildNodes().item(8)
											.setTextContent(jarFile);
								}
							} else if (connectionDetailList.item(j).getNodeName()
									.equals("maxconnections")) {
								node.getChildNodes().item(j)
										.setTextContent(String.valueOf(maxConnections));
							}
							else if (connectionDetailList.item(j).getNodeName()
									.equals("maxidleconnections")) {
								node.getChildNodes().item(j)
										.setTextContent(String.valueOf(maxIdleConnections));
							}
							else if (connectionDetailList.item(j).getNodeName()
									.equals("waittimeinmillis")) {
								node.getChildNodes().item(j)
										.setTextContent(String.valueOf(waitTimeMilliSeconds));
							}
						}
						break;
					}
				}
			}
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(document);
			result = new StreamResult(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			transformer.transform(source, result);
		} catch (Exception e) {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Exception caught while updating DB ",
					e);
		}
	}
	
	public static DWRResponse startMigration(DBConfigBean source,
			DBConfigBean destination, boolean isCustomTagDB, com.queryio.core.bean.Node namenode, Host namenodeHost, boolean createSchemaFlag, String tablePrefix) {
		DWRResponse dwrResponse = new DWRResponse();
		boolean flag = true;
		DBMigrator migrator = new DBMigrator();
		migrator.setCustomTagDB(isCustomTagDB);
		
		migrator.setSourceDBName(source.getConnectionName());
		migrator.setSourceURL(source.getPrimaryConnectionURL());
		migrator.setSourceUserName(source.getPrimaryUsername());
		migrator.setSourcePassword(source.getPrimaryPassword());
		migrator.setSourceDriver(source.getPrimaryDriverName());
		migrator.setSourceJarFile(source.getPrimaryJdbcJar());
		migrator.setDestinationDBName(destination.getConnectionName());
		migrator.setDestinationURL(destination.getPrimaryConnectionURL());
		migrator.setDestinationUserName(destination.getPrimaryUsername());
		migrator.setDestinationPassword(destination.getPrimaryPassword());
		migrator.setDestinationDriver(destination.getPrimaryDriverName());
		migrator.setDestinationJarFile(destination.getPrimaryJdbcJar());
		
		migrator.setNamenodeHost(namenodeHost);
		migrator.setNamenode(namenode);
		migrator.setCreateSchemaFlag(createSchemaFlag);
		migrator.setTablePrefix(tablePrefix);
		
		migrator.startMigration();
		if (flag)
		{
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseMessage("DB Migration Started Successfully");
			dwrResponse.setResponseCode(200);
		}

		return dwrResponse;
	}
	
	public static DWRResponse startTableDDLExport(DBConfigBean source, com.queryio.core.bean.Node namenode, Host namenodeHost) throws Exception {
		DWRResponse dwrResponse = new DWRResponse();
		TableDDLExporter tableDDLExporter = new TableDDLExporter();

		tableDDLExporter.setSourceDBName(source.getConnectionName());
		tableDDLExporter.setSourceURL(source.getPrimaryConnectionURL());
		tableDDLExporter.setSourceUserName(source.getPrimaryUsername());
		tableDDLExporter.setSourcePassword(source.getPrimaryPassword());
		tableDDLExporter.setSourceDriver(source.getPrimaryDriverName());
		tableDDLExporter.setSourceJarFile(source.getPrimaryJdbcJar());
		
		String fileName = tableDDLExporter.startDDLExport();
		dwrResponse.setTaskSuccess(true);
		dwrResponse.setResponseMessage(fileName);
		dwrResponse.setResponseCode(200);

		return dwrResponse;
	}
	
	public static void insertDBMigrationStatusEntry(Connection connection, String sourceDBName, String destDBName, String status,
			String progress, long startTime, long endTime) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_DBMIGRATIONSTATUS);
			ps.setString(1, sourceDBName);
			ps.setString(2, destDBName);
			DatabaseFunctions.setDateTime(ps, 3, startTime);
			DatabaseFunctions.setDateTime(ps, 4, endTime);
			ps.setString(5, status);
			ps.setString(6, progress);
			ps.setString(7, "-");					// No Error Initially
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static void updateDBMigrationStatusFinal(Connection connection, String sourceDBName, String destDBName, String oldStatus, String status,
			String progress, long endTime) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_COMPLETE);
			ps.setString(1, status);
			ps.setString(2, progress);
			DatabaseFunctions.setDateTime(ps, 3, endTime);
			ps.setString(4, sourceDBName);
			ps.setString(5, destDBName);
			ps.setString(6, oldStatus);
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static void updateDBMigrationStatusErrorFinal(Connection connection, String sourceDBName, String destDBName, String oldStatus, String status,
			String error, long endTime) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_ERROR_COMPLETE);
			ps.setString(1, status);
			ps.setString(2, error);
			DatabaseFunctions.setDateTime(ps, 3, endTime);
			ps.setString(4, sourceDBName);
			ps.setString(5, destDBName);
			ps.setString(6, oldStatus);
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static void updateDBMigrationProgress(Connection connection, String sourceDBName, String destDBName, String status, String progress) throws Exception
	{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_DBMIGRATIONSTATUS_PROGRESS);
			ps.setString(1, progress);
			ps.setString(2, sourceDBName);
			ps.setString(3, destDBName);
			ps.setString(4, status);
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static ArrayList getAllDBMigrationStatus(Connection connection) throws Exception
	{
		ArrayList data = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_DB_MIGRATION_STATUS + " ORDER BY " + ColumnConstants.COL_DB_MIGRATION_STATUS_STARTTIME + " DESC");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			
			while(rs.next())
			{
				ArrayList rowData = new ArrayList();
				rowData.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_SOURCEDBNAME));
				rowData.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_DESTINATIONDBNAME));
				rowData.add(sdf.format(rs.getTimestamp(ColumnConstants.COL_DB_MIGRATION_STATUS_STARTTIME).getTime()));
				Timestamp endTime = rs.getTimestamp(ColumnConstants.COL_DB_MIGRATION_STATUS_ENDTIME);
				if(endTime != null && endTime.getTime()<rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME).getTime())
				{
					endTime = null;
				}
				if(endTime != null)
				{
					rowData.add(sdf.format(endTime.getTime()));
				}
				else
				{
					rowData.add("-");
				}											
				rowData.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_STATUS));
				rowData.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_PROGRESS));
				rowData.add(rs.getString(ColumnConstants.COL_DB_MIGRATION_STATUS_ERROR));
				data.add(rowData);
			}
		}
		finally
		{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return data;
	}
	
	public static DWRResponse removeConnection(String connectionName) {
		DWRResponse dwrResponse = null;
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		Node rootNode = null;
		Node rootNodeType = null;
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		NodeList connectionList = null;
		int updateIndex = 0;
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(EnvironmentalConstants
					.getWebinfDirectory()
					+ File.separator
					+ QueryIOConstants.DBCONFIG_XML);
			
			rootNode = document.getElementsByTagName(QueryIOConstants.DBCONFIG_ROOT).item(0);
			rootNodeType = document.getElementsByTagName(QueryIOConstants.DBCONFIG_TYPE_ROOT).item(0);
			
			connectionList = rootNode.getChildNodes();
			Node primaryNode = null;
			
			for (int i = 1; i <= connectionList.getLength() - 1; i++) {

				Node node = connectionList.item(i);
				if (QueryIOConstants.DEFAULT_CUSTOMTAG_DB.equals(node.getNodeName()))
				{
					if ((connectionName).equals(node.getAttributes().getNamedItem(QueryIOConstants.DB_NAME_TYPE).getTextContent())) {
						primaryNode = node;
						break;
					}	
				}
			}
			rootNode.removeChild(primaryNode);
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(document);
			result = new StreamResult(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			transformer.transform(source, result);
		} catch (Exception e) {
			dwrResponse = new DWRResponse();
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setResponseCode(500);
		}
		if (dwrResponse == null) {
			dwrResponse = new DWRResponse();
			dwrResponse.setTaskSuccess(true);
			dwrResponse
					.setResponseMessage("DB Connection Removed Successfully.");
			dwrResponse.setResponseCode(200);
		}
		return dwrResponse;
	}

	public static void replaceDBConfigFromHadoop() throws Exception {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File dbConfigHadoopFile = null;
		File queryIODBConfigFile = null;
		try {
			dbConfigHadoopFile = new File(StartupParameters.getHadoopDirLocation() + File.separator
							+ QueryIOConstants.QUERYIOAGENT_DIR_NAME
							+ File.separator + "webapps" + File.separator
							+ QueryIOConstants.AGENT_QUERYIO + File.separator
							+ "conf" + File.separator
							+ QueryIOConstants.DBCONFIG_XML);
			dbConfigHadoopFile.delete();
			dbConfigHadoopFile.createNewFile();
			queryIODBConfigFile = new File(
					EnvironmentalConstants.getWebinfDirectory()
							+ File.separator + QueryIOConstants.DBCONFIG_XML);
			fis = new FileInputStream(queryIODBConfigFile);
			fos = new FileOutputStream(dbConfigHadoopFile);
			byte[] b = new byte[QueryIOConstants.BUFFER_SIZE];
			int noOfBytes;
			while ((noOfBytes = fis.read(b)) != -1) {
				fos.write(b, 0, noOfBytes);
			}
			fis.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			throw e;
		}

	}

}
