package com.queryio.common.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.hadoop.conf.Configuration;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Angle3D;
import org.eclipse.birt.chart.model.attribute.AngleType;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.DateFormatType;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.RiserType;
import org.eclipse.birt.chart.model.attribute.Rotation3D;
import org.eclipse.birt.chart.model.attribute.SortOption;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.Angle3DImpl;
import org.eclipse.birt.chart.model.attribute.impl.AxisOriginImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.InsetsImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaDateFormatSpecifierImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.Rotation3DImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Scale;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.AxisImpl;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Query;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.SeriesGrouping;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesGroupingImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.ColumnHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.TableGroupHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;
import org.eclipse.birt.report.model.api.elements.structures.DateTimeFormatValue;
import org.eclipse.birt.report.model.api.elements.structures.NumberFormatValue;
import org.eclipse.birt.report.model.api.elements.structures.OdaResultSetColumn;
import org.eclipse.birt.report.model.api.elements.structures.StringFormatValue;
import org.eclipse.birt.report.model.api.metadata.IMetaDataDictionary;
import org.eclipse.birt.report.model.elements.interfaces.IReportItemModel;
import org.eclipse.birt.report.model.elements.interfaces.IStyleModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.icu.util.ULocale;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.util.AppLogger;
import com.queryio.config.db.DBConfigBean;
import com.queryio.config.db.DBConfigDAO;
import com.queryio.core.adhoc.AdHocHiveClient;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.customtags.BigQueryIdentifiers;
import com.queryio.core.customtags.CustomTagsDAO;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.dao.HiveTableDAO;

public class ReportDesignHandler {

	ReportDesignHandle reportDesignHandle = null;

	ElementFactory elementFactory = null;

	IMetaDataDictionary dict = null;

	JSONObject queryHeaderJSON;
	JSONObject queryFooterJSON;
	JSONObject groupHeaderJSON;
	JSONObject groupFooterJSON;
	JSONObject colDetailJSON;
	JSONObject colHeaderDetailJSON;
	JSONObject chartDetailJSON;
	JSONObject colSpanDetail;
	
	HashMap<String, String> patternDateTime = null;
	
	boolean setCheckBookStyle;
	boolean setHighFidelityOutput;
	boolean setLimitResultRows;
	boolean isHive = false;
	String dbName ;
	
	int limitResultRowsValue = BigQueryIdentifiers.REPORT_ROWS_LIMIT;
	
	String aggregateOnColumn = null;
	
	ArrayList<ComputedColumn> computedColumns = new ArrayList<ComputedColumn>();
	HashMap resultSetColumns = null;
	
//	public static void main(String[] args) throws Exception {
//		try {
//			
////			Field[] fields = IBuildInAggregation.class.getDeclaredFields();
////			for (int i=0; i<fields.length; i++)
////			{
////				System.out.println(fields[i].getName());
////			}
//			
//			String jsonProperties = readFile("/Users/bigquery/Desktop/fullJSON.txt");
//
//			JSONParser parser = new JSONParser();
//			JSONObject properties = (JSONObject) parser.parse(jsonProperties);
//
//			buildReport(properties, "/Users/bigquery/Desktop/new_query_1.rptdesign");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	public static void buildReport(JSONObject properties, String designFilePath)
			throws Exception {
		new ReportDesignHandler().createReport(properties, designFilePath);
	}

	public void createReport(JSONObject properties, String designFilePath) throws Exception {

		// Configure the Engine and start the Platform
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("JSON Properties");
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(properties);
		
		IDesignEngine engine = ReportHandler.getDesignEngine();

		SessionHandle session = engine.newSessionHandle(ULocale.ENGLISH);

		// Create a new report
		reportDesignHandle = session.createDesign();

		// Element factory is used to create instances of BIRT elements.
		elementFactory = reportDesignHandle.getElementFactory();

		dict = new DesignEngine(null).getMetaData();

		/* Parse JSON */
		
		String query = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
		
		this.queryHeaderJSON = (JSONObject) properties.get(BigQueryIdentifiers.QUERYHEADER);
		this.queryFooterJSON = (JSONObject) properties.get(BigQueryIdentifiers.QUERYFOOTER);

		this.colHeaderDetailJSON = (JSONObject) properties.get(BigQueryIdentifiers.COLHEADERDETAIL);
		this.colDetailJSON = (JSONObject) properties.get(BigQueryIdentifiers.COLDETAIL);
		this.setCheckBookStyle = true;
		
		this.setHighFidelityOutput = (Boolean) properties.get(BigQueryIdentifiers.SETHIGHFIDELITYOUTPUT);
		
		this.setLimitResultRows = (Boolean) properties.get(BigQueryIdentifiers.SETLIMITRESULTROWS);
		this.limitResultRowsValue = Integer.parseInt(String.valueOf(properties.get(BigQueryIdentifiers.LIMITRESULTROWSVALUE)));
		
		this.groupHeaderJSON = (JSONObject) properties.get(BigQueryIdentifiers.GROUPHEADER);
		this.groupFooterJSON = (JSONObject) properties.get(BigQueryIdentifiers.GROUPFOOTER);
		
		this.colSpanDetail =(JSONObject) properties.get("colspanDetail"); 
		this.chartDetailJSON = (JSONObject) properties.get(BigQueryIdentifiers.CHARTDETAIL);
		
		this.aggregateOnColumn = (String) properties.get(BigQueryIdentifiers.AGGREGATEONCOLUMN);

		createMasterPages();
		this.dbName = (String) properties.get(BigQueryIdentifiers.DBNAME);
		String nameNodeId = (String) properties.get(BigQueryIdentifiers.NAMENODEID);
		String selectedTable = null;
		JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);
		
		if ((allTables != null) && (allTables.size() > 0))
		{
			selectedTable = (String) allTables.get(0);
		}
		createDataSources(dbName, selectedTable, nameNodeId, properties);
		
		if (isHive) {
			if (query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH) || query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase()))
			{
				query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH, QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
				query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase(), QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
			}
		}
		
		String oldResultTable = (String) properties.get(BigQueryIdentifiers.OLD_RESULTTABLENAME);
		if (oldResultTable != null && (allTables != null) && (allTables.size() > 0))
		{
			selectedTable = (String) allTables.get(0);
			query = query.replace(selectedTable, oldResultTable);
		}
		
		AppLogger.getLogger().debug("ReportDesignHandler, isHive : " + isHive + " Query : " + query);
		
		createDataSets(query, dbName, properties, selectedTable, nameNodeId);
		createBody();
//		System.out.println(designFilePath);
//		System.out.println(reportDesignHandle);
		reportDesignHandle.saveAs(designFilePath);
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Finished Designing.");
//		System.out.println("Finished Designing.");
	}

	// Scripted Data Set

	private void createDataSources(String dbName, String tableName, String nameNodeId, JSONObject properties) throws Exception {
		
		Connection queryIOConnection = null;
		
		isHive = true;
		
		reportDesignHandle.getDataSources().add(elementFactory.newScriptDataSource("dataSource"));
		
		OdaDataSourceHandle dsHandle = null;
		try {
			queryIOConnection = CoreDBManager.getQueryIODBConnection();
			isHive = HiveTableDAO.doesHiveTableExist(queryIOConnection, tableName, nameNodeId);
			DBConfigBean connectionDetail = null;
			connectionDetail = DBConfigDAO.getConnectionDetail("MetaStore");
			
			// in case of log4j or xml hive definition query. XML and log4j files are processed by custom MR jobs instead of hive
			String fileType = HiveTableDAO.getFileType(queryIOConnection, tableName, nameNodeId);
			if(QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(fileType) || QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(fileType)) {
				isHive = false;
				connectionDetail = DBConfigDAO.getConnectionDetail("Hive");
				AppLogger.getLogger().debug("1createDataSources isHive: " + isHive + " connectionDetails: " + connectionDetail.getPrimaryConnectionURL() + " fileType: " + fileType);
			}
			
			if (AppLogger.getLogger().isDebugEnabled()) {
				AppLogger.getLogger().debug("2createDataSources isHive: " + isHive + " connectionDetails: " + connectionDetail.getPrimaryConnectionURL() + " fileType: " + fileType + " tableName: " + tableName);
			}
			
			if (isHive) {
				Configuration config = ConfigurationManager.getConfiguration(queryIOConnection, nameNodeId);
				
				String driverName = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_DRIVER);
				String url = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_URL);
				String userName = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_USERNAME);
				String password = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_PASSWORD);
				
				
				HashMap<String, Object> configKeys = new HashMap<String, Object>();
				
				configureHiveConnection(properties, tableName, nameNodeId, queryIOConnection, configKeys, true);
				
				if (!configKeys.isEmpty()) {
					url += ";?";
					for (String key : configKeys.keySet()) {
						Object object = configKeys.get(key);
												
						url += key + "=" + object + ";";
					}
				}
				if (AppLogger.getLogger().isDebugEnabled()) {
					AppLogger.getLogger().debug("configKeys : " + configKeys);
					AppLogger.getLogger().debug("driverName : " + driverName);
					AppLogger.getLogger().debug("url : " + url);
					AppLogger.getLogger().debug("userName : " + userName);
					AppLogger.getLogger().debug("password : " + password);
				}
				
				
				dsHandle = reportDesignHandle.getElementFactory().newOdaDataSource("Data Source", "org.eclipse.birt.report.data.oda.hive");
				dsHandle.setProperty("odaDriverClass", driverName);
				/*"jdbc:hive2://localhost:10000/default;?queryio.hive.parse.recursive=true"*/
				dsHandle.setProperty("odaURL", url);
				dsHandle.setProperty("odaUser", userName);
				dsHandle.setProperty("odaPassword", password);
			} else {
				dsHandle = reportDesignHandle.getElementFactory().newOdaDataSource("Data Source", "org.eclipse.birt.report.data.oda.jdbc");
					
				dsHandle.setProperty("odaDriverClass", connectionDetail.getPrimaryDriverName()/*"org.hsqldb.jdbcDriver"*/);
				dsHandle.setProperty("odaURL", connectionDetail.getPrimaryConnectionURL()/*"jdbc:hsqldb:hsql://127.0.0.1:5434/metastore"*/);
				dsHandle.setProperty("odaUser", connectionDetail.getPrimaryUsername()/*"ADMIN"*/);
				dsHandle.setProperty("odaPassword", connectionDetail.getPrimaryPassword()/* "ADMIN"*/);
				
				dsHandle.setPrivateDriverProperty("metadataBidiFormatStr", "ILYNN");
				dsHandle.setPrivateDriverProperty("disabledMetadataBidiFormatStr", "");
				dsHandle.setPrivateDriverProperty("contentBidiFormatStr", "ILYNN");
				dsHandle.setPrivateDriverProperty("disabledContentBidiFormatStr", "");
			}
		} finally {
			try{
				CoreDBManager.closeConnection(queryIOConnection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		reportDesignHandle.getDataSources().add(dsHandle);
	}

	@SuppressWarnings("deprecation")
	private void createDataSets(String query, String dbName, JSONObject properties, String selectedTable, String nameNodeId) throws Exception {
		

		ScriptDataSetHandle dataSet = elementFactory.newScriptDataSet("Data Set");
		Boolean skipInputfileQIO = (Boolean) properties.get("skipInputfileQIO");
		
		if (isHive && skipInputfileQIO == null) {			
			String startPart = query.substring(0, query.indexOf("FROM") > 0 ? query.indexOf("FROM") : query.indexOf("from"));
			if (startPart.contains(" * "))
			{
				StringBuilder sb = new StringBuilder();
				sb.append(query.substring(0, query.indexOf(" * ")));
				sb.append(" ");
				sb.append(QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
				sb.append(",* ");
				sb.append(query.substring(query.indexOf("FROM")>0 ?query.indexOf("FROM") : query.indexOf("from")));
				query = sb.toString();
			}
		}
		if ((this.setLimitResultRows) && (!query.toUpperCase().contains(" LIMIT ")))
		{
			query = query.trim();
			if (query.endsWith(";"))
				query = query.substring(0, query.lastIndexOf(";"));
			
			query += " LIMIT " + String.valueOf(this.limitResultRowsValue);
		}
		
		Connection connection = null;
		Connection queryIOConnection = null;
		Connection hiveConnection = null;
		
		DBTypeProperties props = null;
		
		try{
			queryIOConnection = CoreDBManager.getQueryIODBConnection();
			
			AppLogger.getLogger().debug("** dbName: " + dbName + " , isHive : " + isHive);
			
			connection = CoreDBManager.getCustomTagDBConnection(dbName);
			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(null, CustomTagDBConfigManager.getConfig(dbName).getCustomTagDBType());
			if (isHive) {
				HashMap configKeys = new HashMap();
				
				String pathFilter = configureHiveConnection(properties, selectedTable,
						nameNodeId, queryIOConnection, configKeys, false);
				
				hiveConnection = AdHocHiveClient.getHiveConnection(queryIOConnection, nameNodeId);
				
				AdHocHiveClient.setConfiguration(hiveConnection, configKeys);
				
				AppLogger.getLogger().debug("Data sets Query : " + query);
				resultSetColumns = CustomTagsDAO.getResultSetColumnsHive(hiveConnection, props, query, nameNodeId);
				AppLogger.getLogger().debug("** ResultSetColumns: " + resultSetColumns + " pathFilter: " + pathFilter);
			} else {
				resultSetColumns = CustomTagsDAO.getResultSetColumns(connection, props, query);
				AppLogger.getLogger().debug("** ResultSetColumns: " + resultSetColumns);
			}


		} finally {
			try{
				CoreDBManager.closeConnection(queryIOConnection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				DatabaseManager.closeDbConnection(hiveConnection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		
		OdaDataSetHandle dsHandle = reportDesignHandle.getElementFactory().newOdaDataSet("Data Set", "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
		dsHandle.setDataSource("Data Source");
		dsHandle.setQueryText(query);
		
		int position = 1;
		PropertyHandle computedSet = dsHandle.getPropertyHandle(ScriptDataSetHandle.RESULT_SET_PROP);
		for (Object object : resultSetColumns.keySet()) {
			String name = (String) object;
			String dataType = (String) resultSetColumns.get(name);
			dataType = getDataType(props, dataType);

			System.out.printf("\n columnName=%s i=%d dataType=%s", name, position, dataType);

			OdaResultSetColumn resultColumn = StructureFactory.createOdaResultSetColumn();
			resultColumn.setPosition(position);
			resultColumn.setColumnName(name);
			resultColumn.setDataType(dataType);
			
			computedSet.addItem(resultColumn);
			position++;
		}
		dsHandle.setName("Data Set");
		reportDesignHandle.getDataSets().add(dsHandle);
		resultSetColumns.remove(ColumnConstants.COL_TAG_VALUES_BLOCKS.toLowerCase());

		
		
//		String path = EnvironmentalConstants.getReportsDirectory()
//				+ File.separator
//				+ ((String) properties.get(BigQueryIdentifiers.USERNAME))
//						.toLowerCase() + File.separator	// FIXME: Do we really need to perform lower case operation on all of these? It requires lowering the case in UI as well. 
//				+( (String) properties.get(BigQueryIdentifiers.NAMENODEID)).toLowerCase()
//				+ File.separator
//				+ ((String) properties.get(BigQueryIdentifiers.QUERYID)).toLowerCase()
//				+ ".json";
//			int recordFetch = -1;
//		if(this.setLimitResultRows){
//			recordFetch =  this.limitResultRowsValue;
//		}
//		String openScript = "count = 0;"+
//				"rdh = new Packages.com.queryio.common.cusrtomdatasource.ReportDataConnection();"+ 
//				"reportDataHandler = rdh.getReportDataHandler(\""+path+"\");"+
//				"maxrecord = reportDataHandler.getRowsLength("+recordFetch+");";
//		
//		
//
//		String fetchScript = "if(count < maxrecord){";
//		
//		PropertyHandle columnHint = dataSet.getPropertyHandle(IDataSetModel.COLUMN_HINTS_PROP);
//		PropertyHandle resultSet = dataSet.getPropertyHandle(IDataSetModel.RESULT_SET_PROP);
//		int i=0;
//		for (Object object : resultSetColumns.keySet()) {
//
//			String columnName = (String)object;
//			if(!resultSetColumns.containsKey(columnName))
//				continue;
//			String dataType = (String) resultSetColumns.get(columnName);
//
//			fetchScript += "dataSetRow[\""+columnName+"\"] = reportDataHandler.getColumnValue(count,\""+columnName+"\");";
//			
////			System.out.println("columnName: " + columnName + " dataType: " + dataType + " props.getKeyFromValue(dataType): " + props.getKeyFromValue(dataType));
//			
//			dataType = getDataType(props, dataType);
//			
//			
////			if( dataType.equalsIgnoreCase("varchar")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_STRING;
////			}else if(dataType.contains("int") ){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_INTEGER;
////			}else if(dataType.contains("time")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_TIME;
////			}else if(dataType.contains("date")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DATE;
////			}else if(dataType.contains("decimal")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DECIMAL;
////			}else if(dataType.contains("decimal")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DECIMAL;
////			}else if(dataType.contains("byte")){
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DECIMAL;
////			}
////			else
////				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_STRING;
//			
//			ResultSetColumn rs = StructureFactory.createResultSetColumn();
//			rs.setColumnName(columnName);
//			rs.setPosition(i);
//			rs.setDataType(dataType);
//			resultSet.addItem(rs);
//	
//			
//			ColumnHint ch = StructureFactory.createColumnHint();
//			ch.setProperty("columnName",columnName);
//			ch.setProperty("displayName", columnName.toLowerCase());
//			columnHint.addItem(ch);
//			i++;
//	
//						
//			
//			
//		}
//		
//		
//		fetchScript += "count++;";
//		fetchScript +="return true; }";
//		fetchScript +="return false;";
//		
//		dataSet.setDataSource("dataSource");
//		
//		dataSet.setOpen(openScript);
//		dataSet.setFetch(fetchScript);
//		
//		dataSet.setDataSetRowLimit(this.limitResultRowsValue);
//		reportDesignHandle.getDataSets().add(dataSet);

	}

	private String configureHiveConnection(JSONObject properties, String selectedTable,
			String nameNodeId, Connection queryIOConnection, HashMap<String, Object> configKeys, boolean isEncodePattern)
			throws Exception {
		AdHocQueryBean adHocBean;
		String pathFilter = HiveTableDAO.getFileName(queryIOConnection, selectedTable, nameNodeId);
		AppLogger.getLogger().debug("** pathFilter: " + pathFilter);
		
		String rmId = HiveTableDAO.getResourceManager(queryIOConnection, selectedTable, nameNodeId);
		Configuration rmConf = ConfigurationManager.getConfiguration(queryIOConnection, rmId);
		
		
		if (rmConf != null)
		{
			configKeys.put(QueryIOConstants.HIVE_YARN_RESOURCEMANAGER_ADDRESS, rmConf.get(QueryIOConstants.HIVE_YARN_RESOURCEMANAGER_ADDRESS));
			adHocBean = AdHocQueryDAO.getAdHocInfoFromTable(queryIOConnection, nameNodeId, selectedTable);
			
			if (adHocBean != null)
			{
				// encodes spaces to "+". Then replace all "+"(space) by %20. "+" can not be decoded back to space by hive.
				configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILEPATH_FILTER, isEncodePattern ? URLEncoder.encode(adHocBean.getFilePathPattern(), "UTF-8").replaceAll("\\+", "%20") 
						: adHocBean.getFilePathPattern());
				configKeys.put(QueryIOConstants.HIVE_QUERYIO_PARSE_RECURSIVE, adHocBean.isParseRecursive());
			}
		}
		
		boolean isFilterQuery = false;
		isFilterQuery = (Boolean) properties.get(BigQueryIdentifiers.ISFILTERQUERY);
		
		if ((pathFilter != null) && (isFilterQuery)) {
			isFilterQuery = true;
		}
		
		configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_APPLY, isFilterQuery);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("isFilterQuery: " + isFilterQuery);
		
		if (isFilterQuery)
		{
			JSONObject filterQueryObj = (JSONObject) properties.get(BigQueryIdentifiers.QUERYFILTERDETAIL);
			if (filterQueryObj != null)
			{
				String filterQuery = (String) filterQueryObj.get(BigQueryIdentifiers.FILTERQUERY);
//						String filterQueryNew = filterQuery;
//						
//						if (filterQuery != null) {
//							JSONObject filterSelectedWhere = (JSONObject) filterQueryObj.get(BigQueryIdentifiers.SELECTEDWHERE);
//							
//							if (filterSelectedWhere != null) {
//								JSONObject filterFilepath = (JSONObject) filterSelectedWhere.get(BigQueryIdentifiers.FILTERQUERY_FILEPATH);
//								if (filterFilepath != null) {
//									String filePathValue = (String) filterFilepath.get(BigQueryIdentifiers.FILTERQUERY_VALUE);
//									if (filePathValue != null) {
//										Configuration nnConf = ConfigurationManager.getConfiguration(queryIOConnection, nameNodeId);
//										String templateDir = nnConf.get(QueryIOConstants.HIVE_QUERYIO_TEMPLATE_DIR, AdHocHiveClient.templateDir);
//										if (!templateDir.endsWith("/")) {
//											templateDir = templateDir.concat("/");
//										}
//										templateDir = templateDir.substring(templateDir.lastIndexOf(":") + 1);
//										templateDir = templateDir.substring(templateDir.indexOf("/"));
//										filterQueryNew = filterQuery.replace(filePathValue, templateDir.concat(pathFilter));
//									}
//								}
//							}
//							configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_QUERY, filterQueryNew);
//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("filterQueryNew: " + filterQueryNew);
//						}
				if (isEncodePattern) {
					filterQuery = URLEncoder.encode(filterQuery, "UTF-8"); //encodes spaces to "+"
					filterQuery = filterQuery.replaceAll("\\+", "%20");	// replace all "+"(space) by %20. "+" can not be decoded back to space by hive.
				}
				configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_QUERY, filterQuery);
			}
		}
		return pathFilter;
	}

	private String getDataType(DBTypeProperties props, String dataType) {
		if(props.getKeyFromValue(dataType) != null)
		{
			if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.STRING_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_STRING;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.TIMESTAMP_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DATETIME;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.LONG_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DECIMAL;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.SHORT_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_INTEGER;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.INTEGER_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_INTEGER;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.REAL_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_FLOAT;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.DOUBLE_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_FLOAT;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.DECIMAL_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_DECIMAL;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.BOOLEAN_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_BOOLEAN;
			else if(props.getKeyFromValue(dataType).toString().equalsIgnoreCase(MetadataConstants.BLOB_WRAPPER_CLASS.toString()))
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_BLOB;
			else
				dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_STRING;
		}
		else
			dataType = DesignChoiceConstants.COLUMN_DATA_TYPE_STRING;
		return dataType;
	}

	private void createMasterPages() throws SemanticException {
		DesignElementHandle simpleMasterPage = elementFactory
				.newSimpleMasterPage("Master Page");//$NON-NLS-1$
		reportDesignHandle.getMasterPages().add(simpleMasterPage);
		simpleMasterPage.setProperty("orientation","landscape");	// TODO: Remove later
		reportDesignHandle.addCss(EnvironmentalConstants.getAppHome() + "css/sample.css");

		
	}

	public static int getIndex(ArrayList<ComputedColumn> computedColumns, String columnName) throws Exception {
		for(int i=0; i<computedColumns.size(); i++){
			if(computedColumns.get(i).getColumnName().equals(columnName)){
				return i;
			}
		}
		return -1;
	}
	
	private void createBody() throws Exception
	{
	
		GridHandle footerGrid = elementFactory.newGridItem(null, 1, 1);
		
		SimpleMasterPageHandle masterPage = (SimpleMasterPageHandle) reportDesignHandle.getMasterPages().getContents().get(0);
		masterPage.setStyleName("bodyClass");
		
		masterPage.getPageFooter().add(footerGrid);
		footerGrid.setWidth("100%");
		
		RowHandle row = (RowHandle) footerGrid.getRows().get(0);
		CellHandle cell = (CellHandle) row.getCells().get(0);
		TextItemHandle text = elementFactory.newTextItem(null);
		cell.getContent().add(text);
		if (this.setHighFidelityOutput)
			cell.setStyleName("reportFooter");
		text.setContentType(ReportConstants.TYPE_HTML);
		text.setWidth("100%");
		
		if (queryFooterJSON != null)
		{
			Iterator itQueryHeader = queryFooterJSON.keySet().iterator();
			while (itQueryHeader.hasNext())
			{
				String key = (String) itQueryHeader.next();
				
				JSONObject props = (JSONObject) queryFooterJSON.get(key);
				
				if(props != null)
				{
					Iterator i = props.keySet().iterator();
					
					while(i.hasNext())
					{
						String propKey = (String) i.next();
						
						if (propKey.equals("title"))
						{
							if ((props.get(propKey) != null) && !(((String) props.get(propKey)).equals("")))
							{
								text.setContent((String) props.get(propKey));
				            }
						}
						else if (propKey.equals("width"))
						{
							cell.getWidth().setValue(props.get(propKey));
							text.setWidth((String) props.get(propKey));
						}
						String propName = ReportConstants.getBirtPropertyFromCSS(propKey);
						if (propName != null)
						{
							if (this.setHighFidelityOutput)
							{
								if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
								{
									try
									{
										cell.setProperty(propName, props.get(propKey));
									}
									catch (Exception e)
									{
										cell.setProperty(propName, hex2Rgb((String) props.get(propKey)));
									}
								}
								else
									cell.setProperty(propName, props.get(propKey));	
							}
						}
					}
				}
			}
		}
		
		GridHandle headerGrid = elementFactory.newGridItem(null,1, 1);
		reportDesignHandle.getBody().add(headerGrid);
		
		headerGrid.setHeight("100%");
		headerGrid.setWidth("100%");
		
		row = (RowHandle) headerGrid.getRows().get(0);
		row.getHeight().setValue("100%");
		cell = (CellHandle) row.getCells().get(0);
		LabelHandle queryHeaderLabel = elementFactory.newLabel(null);
		cell.getContent().add(queryHeaderLabel);
//		int colspan =((Long)this.colSpanDetail.get("queryHeader")).intValue(); 
//		cell.setColumnSpan(colspan);
		if (this.setHighFidelityOutput)
			cell.setStyleName("reportHeader");
		queryHeaderLabel.setWidth("100%");
		
		if (queryHeaderJSON != null)
		{
			Iterator itQueryHeader = queryHeaderJSON.keySet().iterator();
			while (itQueryHeader.hasNext())
			{
				String key = (String) itQueryHeader.next();				
				
				JSONObject props = (JSONObject) queryHeaderJSON.get(key);
				
				if (props != null)
				{
					Iterator i = props.keySet().iterator();
					
					while(i.hasNext())
					{
						String propKey = (String) i.next();
						
						if (propKey.equals("title"))
						{
							if ((props.get(propKey) != null) && !(((String) props.get(propKey)).equals("")))
							{
								queryHeaderLabel.setText((String) props.get(propKey));
				            }
						}
						else if (propKey.equals("width"))
						{
							cell.getWidth().setValue(props.get(propKey));
							queryHeaderLabel.setWidth((String) props.get(propKey));
						}
						String propName = ReportConstants.getBirtPropertyFromCSS(propKey);
						if (propName != null)
						{
							if (this.setHighFidelityOutput)
							{
								if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
								{
									try
									{
										cell.setProperty(propName, props.get(propKey));
									}
									catch (Exception e)
									{
										cell.setProperty(propName, hex2Rgb((String) props.get(propKey)));
									}
								}
								else
									cell.setProperty(propName, props.get(propKey));
							}
						}
					}
				}
			}
		}
		

		TableHandle table = elementFactory.newTableItem(null, resultSetColumns.size(), 1, 1, 1);
		table.setProperty(IStyleModel.TEXT_ALIGN_PROP,
				DesignChoiceConstants.TEXT_ALIGN_CENTER);
		
		table.setProperty(SharedStyleHandle.BORDER_TOP_COLOR_PROP, "#9B9B9B");
		table.setProperty(SharedStyleHandle.BORDER_RIGHT_COLOR_PROP, "#9B9B9B");
		table.setProperty(SharedStyleHandle.BORDER_BOTTOM_COLOR_PROP, "#9B9B9B");
		table.setProperty(SharedStyleHandle.BORDER_LEFT_COLOR_PROP, "#9B9B9B");
		
		table.setProperty(SharedStyleHandle.BORDER_TOP_STYLE_PROP, "solid");
		table.setProperty(SharedStyleHandle.BORDER_RIGHT_STYLE_PROP, "solid");
		table.setProperty(SharedStyleHandle.BORDER_BOTTOM_STYLE_PROP, "solid");
		table.setProperty(SharedStyleHandle.BORDER_LEFT_STYLE_PROP, "solid");
		
		table.setProperty(SharedStyleHandle.BORDER_TOP_WIDTH_PROP, "1px");
		table.setProperty(SharedStyleHandle.BORDER_RIGHT_WIDTH_PROP, "1px");
		table.setProperty(SharedStyleHandle.BORDER_BOTTOM_WIDTH_PROP, "1px");
		table.setProperty(SharedStyleHandle.BORDER_LEFT_WIDTH_PROP, "1px");
		table.setProperty(SharedStyleHandle.OVERFLOW_PROP, "auto");
		
		table.setWidth("100%");//$NON-NLS-1$
		table.setProperty(IReportItemModel.DATA_SET_PROP, "Data Set");//$NON-NLS-1$

		PropertyHandle computedSet = table.getColumnBindings();
		
		ComputedColumn computedColumn;
		ArrayList<ComputedColumn> defaultComputedColumnBindings = new ArrayList<ComputedColumn>();
		ArrayList<ComputedColumn> groupHeaderComputedColumnBindings = new ArrayList<ComputedColumn>();
		ArrayList<ComputedColumn> groupFooterComputedColumnBindings = new ArrayList<ComputedColumn>();
		
		ArrayList<ReportGroupInfo> groupHeaders = getGroupHeaderList();
		ArrayList<ReportGroupInfo> groupFooters = getGroupFooterList();
		
		if(groupHeaders.size() > 0){
			if(this.aggregateOnColumn != null){
				this.aggregateOnColumn = "GROUP_" + this.aggregateOnColumn;
				
			} else {
				this.aggregateOnColumn = groupHeaders.get(0).getColumnName();
				this.aggregateOnColumn = "GROUP_" + this.aggregateOnColumn;
			}
		}
		
		ArrayList<ComputedColumn> computedCols = new ArrayList<ComputedColumn>();
		
		Iterator it = resultSetColumns.entrySet().iterator();
		Map.Entry column;
		
		String columnName;
		while (it.hasNext()){
			column = (Map.Entry) it.next();
			columnName = (String) column.getKey();
			if("blocks".equalsIgnoreCase(columnName))
				continue;
			computedColumn = StructureFactory.createComputedColumn();
		
			computedColumn.setName(columnName);
			computedColumn.setExpression("dataSetRow[\"" + columnName + "\"]");
			
			computedCols.add(computedColumn);
		}
		
		for(int i=0; i<groupHeaders.size(); i++){
			defaultComputedColumnBindings.add(computedCols.get(getIndex(computedCols, groupHeaders.get(i).getColumnName())));
		}
		for(int i=0; i<computedCols.size(); i++){
			boolean isGrouped = false;
			for(int j=0; j<groupHeaders.size(); j++){
				if(groupHeaders.get(j).getColumnName().equals(computedCols.get(i).getName())){
					isGrouped = true;
				}
			}
			if( ! isGrouped){
				defaultComputedColumnBindings.add(computedCols.get(i));
			}
		}
		
		for(int i=0; i<defaultComputedColumnBindings.size(); i++){
			computedSet.addItem(defaultComputedColumnBindings.get(i));
//			System.out.println("COL: " + defaultComputedColumnBindings.get(i).getColumnName());
		}
		
		ReportGroupInfo groupHeaderInfo;
		for(int i=0; i<groupHeaders.size(); i++){
			groupHeaderInfo = groupHeaders.get(i);
			
			computedColumn = StructureFactory.createComputedColumn();
			computedColumn.setName(groupHeaderInfo.getFunction() + "_" + groupHeaderInfo.getColumnName());
			if(groupHeaderInfo.getFunction()!=null && !groupHeaderInfo.getFunction().equals("")){
				computedColumn.setExpression("Total." + groupHeaderInfo.getFunction() 
						+ "(dataSetRow[\"" + groupHeaderInfo.getColumnName() + "\"])");
			} else {
				computedColumn.setExpression("dataSetRow[\"" + groupHeaderInfo.getColumnName() + "\"]");
			}
			
//			System.out.println("GRP HEADER: " + computedColumn.getColumnName());
			
			computedSet.addItem(computedColumn);
			
			groupHeaderComputedColumnBindings.add(computedColumn);
		}
		
		ReportGroupInfo groupFooterInfo;
		for(int i=0; i<groupFooters.size(); i++){
			groupFooterInfo = groupFooters.get(i);
			
			computedColumn = StructureFactory.createComputedColumn();
			computedColumn.setName(groupFooterInfo.getFunction() + "_" + groupFooterInfo.getColumnName());
			
			if (groupFooterInfo.getFunction()!=null && !groupFooterInfo.getFunction().equals("")){
                computedColumn.setExpression("Total." + groupFooterInfo.getFunction() 
                        + "(dataSetRow[\"" + groupFooterInfo.getColumnName() + "\"])");
            } else {
                computedColumn.setExpression("dataSetRow[\"" + groupFooterInfo.getColumnName() + "\"]");
            }
		
//			System.out.println("GRP FOOTER: " + computedColumn.getColumnName());
			
			if(groupHeaders.size() > 0){
				computedColumn.setAggregateOn(this.aggregateOnColumn);
				
				for(int j=0; j<groupHeaders.size(); j++){
					if( ! ("GROUP_" + groupHeaders.get(j).getColumnName()).equals(this.aggregateOnColumn)) {
						computedColumn.getAggregateOnList().add("GROUP_" + groupHeaders.get(j).getColumnName());
					}
				}
			}
		
			computedSet.addItem(computedColumn);
			
			groupFooterComputedColumnBindings.add(computedColumn);
		}

		// Header
		
		HashMap colDetailMap = new HashMap();
		
		if (colDetailJSON != null)
		{
			Iterator itDetail = colDetailJSON.keySet().iterator();
			while (itDetail.hasNext())
			{
				String key = (String) itDetail.next();
				if (key.equalsIgnoreCase("checkBookFlag"))
				{
					this.setCheckBookStyle = true;
				}
				else
				{
					this.setCheckBookStyle = false;
					JSONObject props = (JSONObject) colDetailJSON.get(key);
					colDetailMap.put(key, props);
				}
			}
		}
		
		HashMap colHeaderDetailMap = new HashMap();
		
		if (colHeaderDetailJSON != null)
		{
			Iterator itHeaderDetail = colHeaderDetailJSON.keySet().iterator();
			while (itHeaderDetail.hasNext())
			{
				String key = (String) itHeaderDetail.next();	
				JSONObject props = (JSONObject) colHeaderDetailJSON.get(key);
				colHeaderDetailMap.put(key, props);				
			}
		}
		
		RowHandle header = (RowHandle) table.getHeader().get(0);

		it = resultSetColumns.entrySet().iterator();
		
		ColumnHandle colHandle;
		CellHandle tcell;
		LabelHandle label;
		
		while (it.hasNext()){
			column = (Map.Entry) it.next();
			columnName = (String) column.getKey();
			if("blocks".equalsIgnoreCase(columnName))
				continue;
			int index = getIndex(defaultComputedColumnBindings, columnName);
			
			tcell = (CellHandle) header.getCells().get(index);
			label = elementFactory.newLabel(null);
			label.setText(columnName);
			if (this.setHighFidelityOutput)
				tcell.setStyleName(getStyleFromName(resultSetColumns, columnName, "tableHeader", this.dbName));
			tcell.getContent().add(label);
		    
			colHandle = (ColumnHandle) table.getColumns().get(index);
			
			
//			colHandle.getWidth().setValue(getWidthFromType(resultSetColumns, columnName));
//			label.setWidth(getWidthFromType(resultSetColumns, columnName));
			
			Object o = colHeaderDetailMap.get(columnName);
			
			if(o != null && o instanceof JSONObject)
			{
				JSONObject props = (JSONObject) o;
				Iterator i = props.keySet().iterator();
				
				while(i.hasNext())
				{
					String key = (String) i.next();
//					System.out.println("Key : " + key);
					if (key.equals("title"))
					{
						if ((props.get(key) != null) && !(((String) props.get(key)).equals("")))
						{
			                label.setText((String) props.get(key));
			            }
					}
					else if (key.equals("width"))
					{
//						System.out.println("props.get(key) : " + props.get(key));
						
						colHandle.getWidth().setValue(props.get(key));
						label.setWidth((String) props.get(key));
//						try {							
//							System.out.println("colHandle.getWidth().getAbsoluteValue().getMeasure() after : " + colHandle.getWidth().toString());
//							System.out.println("colHandle.getWidth().getAbsoluteValue().getMeasure() after : " + colHandle.getWidth().getAbsoluteValue().getMeasure());
//							System.out.println("colHandle.getWidth().getAbsoluteValue().getUnit() after : " + colHandle.getWidth().getAbsoluteValue().getUnits());
//						} catch(Exception e) {
//							e.printStackTrace();
//						}
					}
					String propName = ReportConstants.getBirtPropertyFromCSS(key);
					if (propName != null)
					{
						if (this.setHighFidelityOutput)
						{
							if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
							{
								try
								{
									tcell.setProperty(propName, props.get(key));
								}
								catch (Exception e)
								{
									tcell.setProperty(propName, hex2Rgb((String) props.get(key)));
								}
							}
							else
								tcell.setProperty(propName, props.get(key));
						}
					}
				}
			}
		}

		ArrayList<TableGroupHandle> tableGroupHandles = new ArrayList<TableGroupHandle>();
		
		int index = 0;
		TableGroupHandle group;
		
		for(int i=0; i<groupHeaders.size(); i++){
			group = elementFactory.newTableGroup();
			
			groupHeaderInfo = groupHeaders.get(i);
			
			group.setName("GROUP_" + groupHeaderInfo.getColumnName());
			group.setKeyExpr("row[\"" + groupHeaderInfo.getColumnName() + "\"]");//$NON-NLS-1$
			
			table.getGroups().add(group);
			
			RowHandle groupHeader = elementFactory.newTableRow(resultSetColumns.size());
			group.getHeader().add(groupHeader);
			
			tcell = (CellHandle) groupHeader.getCells().get(getIndex(defaultComputedColumnBindings, groupHeaderInfo.getColumnName()));
			tcell.setDrop(DesignChoiceConstants.DROP_TYPE_DETAIL);
			
			TextItemHandle textData = elementFactory.newTextItem(null);
			textData.setContentType(ReportConstants.TYPE_HTML);
			textData.setContent(groupHeaderInfo.getPrefix());
			tcell.getContent().add(textData);
			
			DataItemHandle data = elementFactory.newDataItem(null);
			data.setResultSetColumn(groupHeaderInfo.getColumnName());
			tcell.getContent().add(data);
			
			textData = elementFactory.newTextItem(null);
			textData.setContentType(ReportConstants.TYPE_HTML);
			textData.setContent(groupHeaderInfo.getSuffix());
			tcell.getContent().add(textData);
			
			Object o = groupHeaderInfo.getStyle();
			
			if (o != null && o instanceof JSONObject)
			{
				JSONObject props = (JSONObject) o;
				Iterator groupsStyleIter = props.keySet().iterator();
				
				while (groupsStyleIter.hasNext())
				{
					String key = (String) groupsStyleIter.next();
					
					if (key.equalsIgnoreCase("Format"))
					{
						JSONObject formatObject = (JSONObject) props.get(key);
						
						if ((formatObject != null) && (formatObject instanceof JSONObject))
						{
							String type = (String) formatObject.get("type");
							String category = (String) formatObject.get("category");
							
							JSONObject patternObject = (JSONObject) formatObject.get("pattern");
							
//							System.out.println(type);
//							System.out.println(category);
							
							if (type.equalsIgnoreCase("number"))
							{
								NumberFormatValue numberFormat = new NumberFormatValue();
								String format = getNumberFormat(category, patternObject);
								if (format != null)
								{
									numberFormat.setCategory(format);
									numberFormat.setPattern(format);
									data.setProperty("numberFormat", numberFormat);
								}
							}
							else if (type.equalsIgnoreCase("datetime"))
							{
								// TODO
								DateTimeFormatValue dateTimeFormat = new DateTimeFormatValue();
								String format = getDateTimeFormat(category);
								if (format != null) {
									dateTimeFormat.setCategory(category);
									if ("Custom".equalsIgnoreCase(category)) {
										dateTimeFormat.setPattern(format);
									} else {
										dateTimeFormat.setPattern(category);
									}
									data.setProperty("dateTimeFormat", dateTimeFormat);
								}
								
								if (patternDateTime == null) {
									patternDateTime = new HashMap<String, String>();
								}
								patternDateTime.put(groupHeaderInfo.getColumnName(), format);
							}
							else		// type = String
							{
								StringFormatValue stringFormat = new StringFormatValue();
								String format = getStringFormat(category);
								if (format != null)
								{
									stringFormat.setCategory(format);
									stringFormat.setPattern(format);
									data.setProperty("stringFormat", stringFormat);
								}
							}
						}
					}
					else
					{
						String propName = ReportConstants.getBirtPropertyFromCSS(key);
						if (propName != null)
						{
							if (this.setHighFidelityOutput)
							{
								if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
								{
									try
									{
										tcell.setProperty(propName, props.get(key));
									}
									catch (Exception e)
									{
										tcell.setProperty(propName, hex2Rgb((String) props.get(key)));
									}
								}
								else
									tcell.setProperty(propName, props.get(key));
							}
						}
					}
				}
			}
			
			tableGroupHandles.add(group);
			
			index++;
		}

		// Detail
		RowHandle detail = (RowHandle) table.getDetail().get(0);
		
		if (this.setCheckBookStyle)
		{
			reportDesignHandle.setOnPrepare("i=0;");
			detail.setOnRender("if (i == 0) { this.getStyle().backgroundColor = 'rgb(237, 243, 254)'; i=1; } else { this.getStyle().backgroundColor = 'lightgray'; i=0; }");
		}
		
		index = 0;
		for(int i=0; i<defaultComputedColumnBindings.size(); i++){
			computedColumn = defaultComputedColumnBindings.get(i);
			
			tcell = (CellHandle) detail.getCells().get(index);
			DataItemHandle data = elementFactory.newDataItem(null);
			data.setResultSetColumn(defaultComputedColumnBindings.get(index).getName());
			tcell.getContent().add(data);
			
			columnName = defaultComputedColumnBindings.get(index).getName();

			if (this.setHighFidelityOutput)
				tcell.setStyleName(getStyleFromName(resultSetColumns, columnName, "tableDetail", this.dbName));
			
//			tcell.setProperty("width", getWidthFromType(resultSetColumns, columnName));
//			data.setWidth(getWidthFromType(resultSetColumns, columnName));
			
			Object o = colDetailMap.get(columnName);
			if(o != null && o instanceof JSONObject){
				JSONObject props = (JSONObject) o;
				Iterator iter = props.keySet().iterator();
				
				while(iter.hasNext())
				{
					String key = (String) iter.next();
					
					if (key.equalsIgnoreCase("Format"))
					{
						JSONObject formatObject = (JSONObject) props.get(key);
						
						if ((formatObject != null) && (formatObject instanceof JSONObject))
						{
							String type = (String) formatObject.get("type");
							String category = (String) formatObject.get("category");
							
							JSONObject patternObject = (JSONObject) formatObject.get("pattern");
							
//							System.out.println(type);
//							System.out.println(category);
							
							if (type.equalsIgnoreCase("number"))
							{
								NumberFormatValue numberFormat = new NumberFormatValue();
								String format = getNumberFormat(category, patternObject);
								if (format != null)
								{
									numberFormat.setCategory(format);
									numberFormat.setPattern(format);
									data.setProperty("numberFormat", numberFormat);
								}
							}
							else if (type.equalsIgnoreCase("datetime"))
							{
								// TODO
								DateTimeFormatValue dateTimeFormat = new DateTimeFormatValue();
								String format = getDateTimeFormat(category);
								if (format != null) {
									dateTimeFormat.setCategory(category);
									if ("Custom".equalsIgnoreCase(category)) {
										dateTimeFormat.setPattern(format);
									} else {
										dateTimeFormat.setPattern(category);
									}
									data.setProperty("dateTimeFormat", dateTimeFormat);
								}
								
								if (patternDateTime == null) {
									patternDateTime = new HashMap<String, String>();
								}
								patternDateTime.put(defaultComputedColumnBindings.get(index).getName(), format);
							}
							else		// type = String
							{
								StringFormatValue stringFormat = new StringFormatValue();
								String format = getStringFormat(category);
								if (format != null)
								{
									stringFormat.setCategory(format);
									stringFormat.setPattern(format);
									data.setProperty("stringFormat", stringFormat);
								}
							}
						}
					}
					else
					{
						if (key.equals("width"))
						{
							tcell.setProperty(key, props.get(key));
							data.setWidth((String) props.get(key));
						}
						String propName = ReportConstants.getBirtPropertyFromCSS(key);
						if (propName != null)
						{
							if (this.setHighFidelityOutput)
							{
								if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
								{
									try
									{
										tcell.setProperty(propName, props.get(key));
									}
									catch (Exception e)
									{
										tcell.setProperty(propName, hex2Rgb((String) props.get(key)));
									}
								}
								else
									tcell.setProperty(propName, props.get(key));
							}
						}
					}
				}
			}
			
			index++;
		}
				
		// Footer		

		if (tableGroupHandles.size() > 0)
		{
			RowHandle groupFooterRow = elementFactory.newTableRow(resultSetColumns.size());
			for(int i=0; i<tableGroupHandles.size(); i++){
				if(tableGroupHandles.get(i).getName().equals(this.aggregateOnColumn)){
					tableGroupHandles.get(i).getFooter().add(groupFooterRow);
				}
			}
			index = 0;
			for(int i=0; i<groupFooters.size(); i++){
				groupFooterInfo = groupFooters.get(i);
				
				tcell = (CellHandle) groupFooterRow.getCells().get(getIndex(defaultComputedColumnBindings, groupFooterInfo.getColumnName()));
				
				TextItemHandle textData = elementFactory.newTextItem(null);
				textData.setContentType(ReportConstants.TYPE_HTML);
				textData.setContent(groupFooterInfo.getPrefix());
				tcell.getContent().add(textData);
				
				DataItemHandle data = elementFactory.newDataItem(null);
				data.setResultSetColumn(groupFooterComputedColumnBindings.get(index).getName());
				tcell.getContent().add(data);
				
				textData = elementFactory.newTextItem(null);
				textData.setContentType(ReportConstants.TYPE_HTML);
				textData.setContent(groupFooterInfo.getSuffix());
				tcell.getContent().add(textData);
				
				Object o = groupFooterInfo.getStyle();
				
				if(o != null && o instanceof JSONObject)
				{
					JSONObject props = (JSONObject) o;
					Iterator groupsStyleIter = props.keySet().iterator();
					
					while(groupsStyleIter.hasNext())
					{
						String key = (String) groupsStyleIter.next();
						
						if (key.equalsIgnoreCase("Format"))
						{
							JSONObject formatObject = (JSONObject) props.get(key);
							
//							System.out.println(formatObject);
							
							if ((formatObject != null) && (formatObject instanceof JSONObject))
							{
								String type = (String) formatObject.get("type");
								String category = (String) formatObject.get("category");
								
								JSONObject patternObject = (JSONObject) formatObject.get("pattern");
								
//								System.out.println(type);
//								System.out.println(category);
								
								if (type.equalsIgnoreCase("number"))
								{
									NumberFormatValue numberFormat = new NumberFormatValue();
									String format = getNumberFormat(category, patternObject);
									if (format != null)
									{
										numberFormat.setCategory(format);
										numberFormat.setPattern(format);
										data.setProperty("numberFormat", numberFormat);
									}
								}
								else if (type.equalsIgnoreCase("datetime"))
								{
									// TODO
									DateTimeFormatValue dateTimeFormat = new DateTimeFormatValue();
									String format = getDateTimeFormat(category);
									if (format != null) {
										dateTimeFormat.setCategory(category);
										if ("Custom".equalsIgnoreCase(category)) {
											dateTimeFormat.setPattern(format);
										} else {
											dateTimeFormat.setPattern(category);
										}
										data.setProperty("dateTimeFormat", dateTimeFormat);
									}
									
									if (patternDateTime == null) {
										patternDateTime = new HashMap<String, String>();
									}
									patternDateTime.put(groupFooterComputedColumnBindings.get(index).getName(), format);
								}
								else		// type = String
								{
									StringFormatValue stringFormat = new StringFormatValue();
									String format = getStringFormat(category);
									if (format != null)
									{
										stringFormat.setCategory(format);
										stringFormat.setPattern(format);
										data.setProperty("stringFormat", stringFormat);
									}
								}
							}
						}
						else
						{
							if (key.equals("width"))
							{
								tcell.setProperty(key, props.get(key));
								data.setWidth((String) props.get(key));
							}
							
							String propName = ReportConstants.getBirtPropertyFromCSS(key);
							if (propName != null)
							{
								if (this.setHighFidelityOutput)
								{
									if ((propName.equals(SharedStyleHandle.BACKGROUND_COLOR_PROP)) || (propName.equals(SharedStyleHandle.COLOR_PROP)))
									{
										try
										{
											tcell.setProperty(propName, props.get(key));
										}
										catch (Exception e)
										{
											tcell.setProperty(propName, hex2Rgb((String) props.get(key)));
										}
									}
									else
										tcell.setProperty(propName, props.get(key));
								}
							}
						}
					}
				}
				
				index++;
			}
		}
		
		reportDesignHandle.getBody().add(table);
		
//		System.out.println("Chart Details: " + this.chartDetailJSON);
		
		if (chartDetailJSON != null)
		{
			Iterator chartDetailIter = chartDetailJSON.keySet().iterator();
			
			while (chartDetailIter.hasNext())
			{
				String key = (String) chartDetailIter.next();
				if(key.equals("chartPreferences")){
					continue;
				}
				JSONObject chartProps = (JSONObject) chartDetailJSON.get(key);
				
				
				
				String type = (String) chartProps.get("type");
				JSONObject chartPreferences = (JSONObject) chartProps.get("chartPreferences");
				
				int chartWidth = 300;
				int chartHeight = 300;
				int dimension = 0;
				int rowPosition = 1;
				int colSpan = 1;
				String chartScale = (String) chartProps.get("chartscale");
				String yScaleMinVal = (String) chartProps.get("yScaleMinVal"); 
		   		
				try {
					chartWidth = Integer.parseInt(String.valueOf(chartProps.get("width")));
				} catch(Exception e) {
					AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//					e.printStackTrace();
				}
				try {
					chartHeight = Integer.parseInt(String.valueOf(chartProps.get("height")));
				} catch(Exception e) {
					AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//					e.printStackTrace();
				}
				try {
					dimension = Integer.parseInt(String.valueOf(chartProps.get("dimension")));
				} catch(Exception e) {
					AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//					e.printStackTrace();
				}
				try {
					rowPosition = Integer.parseInt(String.valueOf(chartProps.get("rowPosition")));
				} catch(Exception e) {
					AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//					e.printStackTrace();
				}
				try {
					colSpan = Integer.parseInt(String.valueOf(chartProps.get("colSpan")));
				} catch(Exception e) {
					AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
//					e.printStackTrace();
				}
				
				
				
				
				String title = (String) chartProps.get("title");
				String position = (String) chartProps.get("position");
				String xSeries = (String) chartProps.get(BigQueryIdentifiers.X_SERIES);
				String xseriesSortType = (String) chartProps.get(BigQueryIdentifiers.X_SERIES_SORT_TYPE);
				String xseriesSortColumn = (String) chartProps.get(BigQueryIdentifiers.X_SERIES_SORT_COLUMN);
				
				if ((xSeries != null) && (isHive))
					xSeries = xSeries.toLowerCase();
				
				JSONObject xSeriesObj = new JSONObject();
				xSeriesObj.put("xseries", xSeries);
				xSeriesObj.put("xseriesSortType", xseriesSortType);
				xSeriesObj.put("xseriesSortColumn", xseriesSortColumn);

				
				JSONObject ySeriesJSON = (JSONObject) chartProps.get("yseries");
				
				
				String yGrouping = 	(String) chartProps.get("ygrouping");
				String xSeriesTitle = (String) chartProps.get("xlegend");
				String ySeriesTitle = (String) chartProps.get("ylegend");
				
				if (ySeriesJSON != null)
				{
					ExtendedItemHandle chartComponent = elementFactory.newExtendedItem(null, "Chart");
					
					int keyGroupIndex = 0;
					for(int i=0; i<tableGroupHandles.size(); i++){
						if(tableGroupHandles.get(i).getName().equals(this.aggregateOnColumn)){
							keyGroupIndex = i;
						}
					}
					
					if (position.equalsIgnoreCase(BigQueryIdentifiers.GROUPHEADER))
					{
						TableGroupHandle groupHeader = (TableGroupHandle) table.getGroups().get(keyGroupIndex);
						
						if ((groupHeader != null) && (groupHeader.getHeader() != null))
						{
							for (int i=0; i<=rowPosition; i++)
							{
								Object o = null;
								try{
									o = groupHeader.getHeader().get(i);
								} catch(Exception e){
									// Ignore
									e.printStackTrace();
								}
//								System.out.println("i: " + i);
								if (o == null)
								{
									groupHeader.getHeader().add(elementFactory.newTableRow());
//									System.out.println("added i: " + i);
								}
							}
							row = (RowHandle) groupHeader.getHeader().get(rowPosition);
							if (row.getCells() != null)
							{
								if (row.getCells().getContents().size() == 0)
								{
									cell = elementFactory.newCell();
									row.getCells().add(cell);
								}
								for (int j=1; j<resultSetColumns.size(); j++)
								{
									cell = null;
									try
									{
										cell = (CellHandle) row.getCells().get(j);
									} catch(Exception e){
										// Ignore
										e.printStackTrace();
									}
									if (cell == null)
									{
										cell = elementFactory.newCell();
										row.getCells().add(cell);
										
										cell.setColumnSpan(colSpan);
										cell.getContent().add(chartComponent);

//										System.out.println("added j: " + j);
										break;
									}
								}
							}
						}
					}
					else if (position.equalsIgnoreCase(BigQueryIdentifiers.GROUPFOOTER))
					{
						TableGroupHandle groupFooter = (TableGroupHandle) table.getGroups().get(keyGroupIndex);
						
						if ((groupFooter != null) && (groupFooter.getFooter() != null))
						{
							for (int i=0; i<=rowPosition; i++)
							{
								Object o = null;
								try{
									o = groupFooter.getFooter().get(i);
								} catch(Exception e){
									// Ignore
									e.printStackTrace();
								}
//								System.out.println("i: " + i);
								if (o == null)
								{
									groupFooter.getFooter().add(elementFactory.newTableRow());
//									System.out.println("added i: " + i);
								}
							}
							row = (RowHandle) groupFooter.getFooter().get(rowPosition);
							if (row.getCells() != null)
							{
								for (int j=1; j<resultSetColumns.size(); j++)
								{
									cell = null;
									try
									{
										cell = (CellHandle) row.getCells().get(j);
									} catch(Exception e){
										// Ignore
										e.printStackTrace();
									}
									if (cell == null)
									{
										cell = elementFactory.newCell();
										row.getCells().add(cell);
										
										cell.setColumnSpan(colSpan);
										cell.getContent().add(chartComponent);

//										System.out.println("added j: " + j);
										break;
									}
								}
							}
						}						
					}
					else	// Page Header
					{
						if (headerGrid.getRows() != null)
						{
							for (int i=0; i<=rowPosition; i++)
							{
								if (headerGrid.getRows().get(i)==null)
								{
									headerGrid.getRows().add(elementFactory.newTableRow());
								}
							}
							RowHandle headerGridRow = (RowHandle) headerGrid.getRows().get(rowPosition);
							cell = (CellHandle) headerGridRow.getCells().get(0);
							if(cell == null){
								cell = elementFactory.newCell();
								cell.setStyleName("chartCell");
								headerGridRow.getCells().add(cell);
								
							}
							
							cell.getContent().add(chartComponent);
							
//							int j=0;
//							AppLogger.getLogger().debug("1 "+headerGridRow.getCells());
//							if (headerGridRow.getCells() != null)
//							{
//								
//								AppLogger.getLogger().debug("2 "+row.getCells().get(j++));
//								
//								while (row.getCells().get(j++) != null);
//								cell = elementFactory.newCell();
//								headerGridRow.getCells().add(cell);
//								cell.getContent().add(chartComponent);
//								cell.setColumnSpan(colSpan);
//							}
						}
						
						/*
						if (headerGrid.getRows() != null)
						{
							for (int i=0; i<=rowPosition; i++)
							{
								if (headerGrid.getRows().get(i)==null)
								{
									headerGrid.getRows().add(elementFactory.newTableRow());
								}
							}
							RowHandle headerGridRow = (RowHandle) headerGrid.getRows().get(rowPosition);
							chartComponent.setWidth(chartWidth);
							for (int j=1; j<resultSetColumns.size(); j++)
							{
								cell = null;
								try
								{
									cell = (CellHandle) headerGridRow.getCells().get(j);
								} catch(Exception e){
									// Ignore
//									e.printStackTrace();
								}
								if (cell == null)
								{
									cell = elementFactory.newCell();
									if(j == colSpan){
										cell.getContent().add(chartComponent);
//										cell.getWidth().setValue(chartWidth);
									}
									headerGridRow.getCells().add(cell);
									continue;
								}
								if(j == colSpan){
									cell = (CellHandle) headerGridRow.getCells().get(colSpan);
									
									cell.getContent().add(chartComponent);
//									cell.getWidth().setValue(chartWidth);
									
								}
							}

						}*/
					}
					
					chartComponent.setProperty("outputFormat", "PNG");
					chartComponent.setProperty("dataSet", "Data Set");
					try{
					if(type.equalsIgnoreCase("PIE")){
						
						chartComponent.getReportItem().setProperty("chart.instance", getPieChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("RADAR")){
						chartComponent.getReportItem().setProperty("chart.instance", RadarChartHandler.getRadarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("LINE")){
						chartComponent.getReportItem().setProperty("chart.instance", getLineChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("BAR")){
						chartComponent.getReportItem().setProperty("chart.instance", getBarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,"bar",this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("AREA")){
						chartComponent.getReportItem().setProperty("chart.instance", AreaChartHandler.getAreaChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("BUBBLE")){
						JSONObject ySeriesSizeObj = (JSONObject) chartProps.get("yseriesSize");
						JSONObject ySeriesValues = (JSONObject) chartProps.get("yseriesValue");
						chartComponent.getReportItem().setProperty("chart.instance", BubbleChartHandler.getBubbleChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, ySeriesValues, ySeriesSizeObj, xSeriesTitle, ySeriesTitle,chartPreferences,resultSetColumns, this.dbName, this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("SCATTER")){
						chartComponent.getReportItem().setProperty("chart.instance", ScatterChartHandler.getChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("PYRAMID")){
						chartComponent.getReportItem().setProperty("chart.instance", getBarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,"pyramid",this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("TUBE")){
						chartComponent.getReportItem().setProperty("chart.instance", getBarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,"tube",this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("CONE")){
						chartComponent.getReportItem().setProperty("chart.instance", getBarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,"cone",this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("STACKED")){
						chartComponent.getReportItem().setProperty("chart.instance", getBarChart(title, dimension, chartWidth, chartHeight, xSeriesObj, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,"stacked",this.isHive,yGrouping,chartScale,yScaleMinVal));
					}
					else if(type.equalsIgnoreCase("GANTT")){
						JSONObject ySeriesStartDateJSON =  (JSONObject) chartProps.get("yseriesStart");
						JSONObject ySeriesEndDateJSON =  (JSONObject) chartProps.get("yseriesEnd");
						JSONObject ySeriesLabelJSON = (JSONObject) chartProps.get("yseriesLabel");
						
						
						chartComponent.getReportItem().setProperty("chart.instance", GanttChartHandler.getChart(title, dimension, chartWidth, chartHeight, xSeries, ySeriesJSON, ySeriesStartDateJSON, ySeriesEndDateJSON, ySeriesLabelJSON, xSeriesTitle, ySeriesTitle, chartPreferences, resultSetColumns, this.dbName, this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("DIFFERENCE")){
						JSONObject ySeriesNegativeJSON = (JSONObject) chartProps.get("yseriesNegative");
						JSONObject ySeriesPostiveJSON = (JSONObject) chartProps.get("yseriesPositive");
						chartComponent.getReportItem().setProperty("chart.instance", DifferenceChartHandler.getChart(title, dimension, chartWidth, chartHeight, xSeries, ySeriesPostiveJSON, ySeriesNegativeJSON, xSeriesTitle, ySeriesTitle, chartPreferences, resultSetColumns, this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("STOCK")){
						JSONObject highSeriesJSON = (JSONObject) chartProps.get("yseriesHigh");
						JSONObject lowSeriesJSON = (JSONObject) chartProps.get("yseriesLow");
						JSONObject openSeriesJSON = (JSONObject) chartProps.get("yseriesOpen");
						JSONObject closeSeriesJSON = (JSONObject) chartProps.get("yseriesClose");
						chartComponent.getReportItem().setProperty("chart.instance", StockChartHandler.getChart(title, dimension, chartWidth, chartHeight, xSeries, ySeriesJSON, highSeriesJSON, lowSeriesJSON, openSeriesJSON, closeSeriesJSON, xSeriesTitle, ySeriesTitle, chartPreferences, resultSetColumns, this.dbName, this.isHive,yGrouping));
					}
					else if(type.equalsIgnoreCase("METER")){
						chartComponent.getReportItem().setProperty("chart.instance", MeterChartHandler.getChart(title, dimension, chartWidth, chartHeight, xSeries, ySeriesJSON, xSeriesTitle, ySeriesTitle,chartPreferences,resultSetColumns, this.isHive,yGrouping));
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Chart getBarChart(String title, int dimension, int chartWidth, int chartHeight, JSONObject xSeriesObj, JSONObject ySeriesJSON,
			String xSeriesTitle, String ySeriesTitle,JSONObject chartPreferences, String riserType, boolean isHive, String yGrouping,String ySeriesScale, String yScaleMinVal) throws Exception {
		
		
		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");
		
		ChartWithAxes chart = ChartWithAxesImpl.create();
		chart.setType("Bar Chart");
		chart.setSubType("Side-by-side");
		
		
		if (dimension == ChartDimension.THREE_DIMENSIONAL && ySeriesJSON.size()>1)
        {
			dimension = ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH;
        	
        }
		switch (dimension)
		{
			case ChartDimension.TWO_DIMENSIONAL : 
					chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
					break;
																
			case ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH : 	
				chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
				break;
			case ChartDimension.THREE_DIMENSIONAL : 
					chart.setDimension(ChartDimension.THREE_DIMENSIONAL_LITERAL);
				
				break;
			default : 
				chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
				break;
		}
		
		
		chart.getBlock().getBounds().setWidth(chartWidth);
		chart.getBlock().getBounds().setHeight(chartHeight);
		chart.getBlock().getOutline().setVisible(true);
		
		// Plot

		chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());

		chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.WHITE());

		// Title

		chart.getTitle().getLabel().getCaption().setValue(title);//$NON-NLS-1$
		
        chart.getPlot().setBackground(ColorDefinitionImpl.WHITE());
        chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.TRANSPARENT());
        
        /* Legend */
        Legend legend = chart.getLegend();
        JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);
        
        chart.getTitle().getLabel().getCaption().setValue(title);
        chart.getTitle().getLabel().getCaption().getFont().setSize(14);
       
        Axis xAxis = chart.getPrimaryBaseAxes()[0];
        String yAxisGridLineColor = null;
        
        JSONObject xAxisProperties = (JSONObject) chartPreferences.get("xAxisJson");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("xAxisProperties JSON : "+xAxisProperties.toJSONString());
		if (xAxisProperties != null && xAxisProperties.size() > 0) {
		
			Boolean visible = (Boolean) xAxisProperties.get("visible");
			if(visible){
				String position =  (String) xAxisProperties.get("position");
				if(position.equalsIgnoreCase("above")){
					xAxis.setLabelPosition(Position.ABOVE_LITERAL);
				}else if(position.equalsIgnoreCase("below")){
					xAxis.setLabelPosition(Position.BELOW_LITERAL);
				}
				
				Scale scale = xAxis.getScale();
				scale.setTickBetweenCategories(false);
				
				String xAxisLblBackground = (String) xAxisProperties.get("background");
				String xAxisLblShadow = (String) xAxisProperties.get("shadow");
				Label xAxisLbl = xAxis.getLabel();
				xAxisLbl.setVisible(true);
				xAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(xAxisLblBackground));
				xAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(xAxisLblShadow));
				JSONObject xAxisLblInsets = (JSONObject) xAxisProperties.get("insets");
				if (xAxisLblInsets != null && xAxisLblInsets.size() > 0) {
					int xAxisLblInsetTop = Integer.parseInt((String) xAxisLblInsets.get("top"));
			        int xAxisLblInsetBottom = Integer.parseInt((String) xAxisLblInsets.get("bottom"));
			        int xAxisLblInsetLeft = Integer.parseInt((String)  xAxisLblInsets.get("left"));
			        int xAxisLblInsetRight = Integer.parseInt((String)  xAxisLblInsets.get("right"));
			        xAxisLbl.setInsets(InsetsImpl.create(xAxisLblInsetTop, xAxisLblInsetLeft, xAxisLblInsetBottom, xAxisLblInsetRight));
			    }
				JSONObject xAxisLblOutLine = (JSONObject) xAxisProperties.get("outline");
				if (xAxisLblOutLine != null && xAxisLblOutLine.size() > 0) {
					Boolean isxAxisLblOutLineVisible = (Boolean) xAxisLblOutLine.get("visible");
			        if(isxAxisLblOutLineVisible){
			        	int xAxisLblOutlineWidthInt = Integer.parseInt((String) xAxisLblOutLine.get("width"));
				        String xAxisLblOutlineColor = (String) xAxisLblOutLine.get("color");
				        xAxisLbl.setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(xAxisLblOutlineColor),ChartPreferenceHandler.getLineStyleType((String) xAxisLblOutLine.get("style")), xAxisLblOutlineWidthInt));
			        }
				}
				
				String xAxisLblFontColor = (String)xAxisProperties.get("color");
				String sName = (String)xAxisProperties.get("font-family");
				float fSize = Float.parseFloat((String)xAxisProperties.get("font-size"));
				String xAxisLblFontStyle = (String)xAxisProperties.get("font-style");
				String xAxisLblTextAlign = (String)xAxisProperties.get("text-align");
				
				boolean bBold = false;
				boolean bItalic = false;
				boolean bUnderline = false;
				boolean bStrikethrough = false;
				boolean bWordWrap = false;
				if(xAxisLblFontStyle.equalsIgnoreCase("bold"))
					bBold = true;
				xAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisLblFontColor));
				xAxisLbl.getCaption().setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,ChartPreferenceHandler.getTextAlignment(xAxisLblTextAlign, null)));
				xAxis.setLabel(xAxisLbl);
				xAxis.getTitle().getCaption().setValue(xSeriesTitle);
				xAxis.getTitle().getCaption().getFont().setName(sName);
				if (xAxisProperties.containsKey("titleColor")) { 
					String xAxisTitleFontColor = (String)xAxisProperties.get("titleColor");
					xAxis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisTitleFontColor));
				}
				if (xAxisProperties.containsKey("titleFontSize")) {
					float xAxisTitleFontSize = Float.parseFloat((String)xAxisProperties.get("titleFontSize"));
					xAxis.getTitle().getCaption().getFont().setSize(xAxisTitleFontSize);
				}
				xAxis.getTitle().getCaption().getFont().setBold(bBold);
				
				xAxis.getTitle().getCaption().getFont().setRotation(0);
				xAxis.getTitle().setVisible(true);
			}else{
				xAxis.getLabel().setVisible(false);
			}
			JSONObject xAxisGridProperties = (JSONObject) xAxisProperties.get("gridline");
			if (xAxisGridProperties != null && xAxisGridProperties.size() > 0) {
				Boolean isXAxisGridLineVisible = (Boolean) xAxisGridProperties.get("visible");
		        if(isXAxisGridLineVisible){
		        	int xAxisGridLineWidthInt = Integer.parseInt((String) xAxisGridProperties.get("width"));
		        	String	xAxisGridLineColor = (String) xAxisGridProperties.get("color");
			    	xAxis.getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(xAxisGridLineColor));
			        xAxis.getMajorGrid().getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(xAxisGridLineColor));
			        xAxis.getMajorGrid().getLineAttributes().setStyle(ChartPreferenceHandler.getLineStyleType((String) xAxisGridProperties.get("style")));
			        xAxis.getMajorGrid().getLineAttributes().setThickness(xAxisGridLineWidthInt);
			        if(xAxisGridProperties.containsKey("gridstep")){
			        	int xAxisGridStep = Integer.parseInt((String) xAxisGridProperties.get("gridstep"));
			        	xAxis.getScale().setMajorGridsStepNumber(xAxisGridStep);
			        }
			        
		        }
		        xAxis.getMajorGrid().getLineAttributes().setVisible(isXAxisGridLineVisible);
			}
			
		}

		String xSeries = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES);
		
		if ((xSeries != null) && (patternDateTime != null)) {
			String pattern = patternDateTime.get(xSeries);
			if (pattern != null) {
				xAxis.setFormatSpecifier(JavaDateFormatSpecifierImpl.create(pattern));
			}
		}

		JSONArray topColors = null;
		if(commonJSON!=null){

			chart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			Plot p = chart.getPlot();
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			chart.getBlock().getBounds().setWidth(chartWidth);
			chart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
	        ClientArea clientArea = p.getClientArea();
	        clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
	        //Empty message
	        String msg = (String)commonJSON.get("emptyChartMessage");
	        if( msg != null && msg.length() > 0){
	        	chart.getEmptyMessage().setVisible(true);
	        	chart.getEmptyMessage().getCaption().setValue(msg);
	        }
	        //Series palette
	        topColors = (JSONArray)commonJSON.get("topColors");
	        
		}
        Series seriesX = SeriesImpl.create();

        Query xQuery = QueryImpl.create("row[\""+ xSeries + "\"]");
        seriesX.getDataDefinition().add(xQuery);
        seriesX.setSeriesIdentifier(xSeriesTitle);
        
        SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
        xAxis.getSeriesDefinitions().add(xSeriesDef);
        xSeriesDef.getSeriesPalette().shift(0);
        xSeriesDef.getSeries().add(seriesX);
        
        String sortType = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_TYPE);
		
		if (!BigQueryIdentifiers.X_SERIES_SORT_TYPE_NONE.equalsIgnoreCase(sortType)) {
			String sortColumn = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_COLUMN);
			Query xSortQuery = QueryImpl.create("row[\""+ sortColumn + "\"]");
			
			if (BigQueryIdentifiers.X_SERIES_SORT_TYPE_ASCENDING.equalsIgnoreCase(sortType))
				xSeriesDef.setSorting(SortOption.ASCENDING_LITERAL);
			else 
				xSeriesDef.setSorting(SortOption.DESCENDING_LITERAL);
			
			xSeriesDef.setSortKey(xSortQuery);
			xSeriesDef.setSortStrength(2);
		}
		
		/* Title */
		
		ChartPreferenceHandler.setTitleProperties(titleJSON, chart, title);
		
		//Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
	        if(isChartOutLineVisible){
	        	int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
		        String titleOutlineColor = (String) chartOutLine.get("color");
		        chart.getBlock().setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(titleOutlineColor),ChartPreferenceHandler.getLineStyleType((String) chartOutLine.get("style")), chartOutlineWidthInt));
	        }
				
		}
		//Chart insets.
		JSONObject chartInsets = (JSONObject) chartPreferences.get("insetsJson");
		if (chartInsets != null && chartInsets.size() > 0) {
			int titleInsetTop = Integer.parseInt((String) chartInsets.get("top"));
		    int titleInsetBottom = Integer.parseInt((String) chartInsets.get("bottom"));
		    int titleInsetLeft = Integer.parseInt((String)  chartInsets.get("left"));
		    int titleInsetRight = Integer.parseInt((String)  chartInsets.get("right"));
		    chart.getTitle().setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
		}
		
        boolean primary = true;
        
        SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
        seriesGroup.setEnabled(true);
		
        xSeriesDef.setGrouping(seriesGroup);
        
        Iterator it = ySeriesJSON.keySet().iterator();
        int i=0;
        
        while (it.hasNext())
        {
        	BarSeries seriesY = (BarSeries) BarSeriesImpl.create();
        	if(riserType.equalsIgnoreCase("pyramid")){
        		seriesY.setRiser(RiserType.TRIANGLE_LITERAL);
        	}else if(riserType.equalsIgnoreCase("tube")){
        		seriesY.setRiser(RiserType.TUBE_LITERAL);
        	}else if(riserType.equalsIgnoreCase("cone")){
        		seriesY.setRiser(RiserType.CONE_LITERAL);
        	}
        	JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
        	ChartPreferenceHandler.setLabelProperties(labelProperties, seriesY,false, dimension);
        	
        	String ySeries = (String) it.next();
        	String function = (String) ySeriesJSON.get(ySeries);
        	
        	if ((ySeries != null) && (isHive))
        		ySeries = ySeries.toLowerCase();
        	
        	Axis yAxis ;
        	
        	Axis[] axaOrthogonal = chart.getOrthogonalAxes(xAxis, true);
        	yAxis = axaOrthogonal[0];
        	
        	if("Linear".equals(ySeriesScale)){
        		yAxis.setType(AxisType.LINEAR_LITERAL);
        		try{
        			
        			Scale yscale = yAxis.getScale(); 
        			yscale.setAutoExpand(true); 
        			if (yScaleMinVal != null && !yScaleMinVal.isEmpty())
        					yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(yScaleMinVal)));
        		}catch(NumberFormatException e){
        			//ignore
        		}
        	}else if("Logarithmic".equals(ySeriesScale)){
        		yAxis.setType(AxisType.LOGARITHMIC_LITERAL);
        		Scale yscale = yAxis.getScale(); 
    			yscale.setAutoExpand(true);
        		try{
        			if (yScaleMinVal != null && !yScaleMinVal.isEmpty())
        				yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(yScaleMinVal)));
        		}catch(NumberFormatException e){
        		 	yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(1)));
        		}
        		yscale.setMin( NumberDataElementImpl.create(1)); 
        	}else if("DateTime".equals(ySeriesScale)){
        		yAxis.setType(AxisType.DATE_TIME_LITERAL);
        	}else{
        		yAxis.setType(AxisType.LINEAR_LITERAL);
        	}
        	if(primary){
        		 // Y-Axis

//        		yAxis = chart.getPrimaryOrthogonalAxis(xAxis);
        		yAxis.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
        		yAxis.getTitle().setVisible(true);
        		
//        		primary = false;
//        	} else {
//        		// Y-Axis (2)
//        		yAxis = AxisImpl.create(Axis.ORTHOGONAL);
//        		yAxis.setType(AxisType.LINEAR_LITERAL);
//        		yAxis.getMajorGrid().setTickStyle(TickStyle.RIGHT_LITERAL);
//        		xAxis.getAssociatedAxes().add(yAxis);
        		
        	}
        	//String yGrouping = (String) chartProps.get("ygrouping");
        	
        	JSONObject yAxisProperties = (JSONObject) chartPreferences.get("yAxisJson");
			if (yAxisProperties != null && yAxisProperties.size() > 0) {
			
				Boolean visible = (Boolean) yAxisProperties.get("visible");
				if(visible){
					String position =  (String) yAxisProperties.get("position");
					if(position.equalsIgnoreCase("right")){
						yAxis.setLabelPosition(Position.RIGHT_LITERAL);
					}else if(position.equalsIgnoreCase("left")){
						yAxis.setLabelPosition(Position.LEFT_LITERAL);
					}
					String yAxisLblBackground = (String) yAxisProperties.get("background");
					String yAxisLblShadow = (String) yAxisProperties.get("shadow");
//        					Label yAxisLbl = LabelImpl.create();
					Label yAxisLbl = yAxis.getLabel();
					yAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(yAxisLblBackground));
					yAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(yAxisLblShadow));
					JSONObject yAxisLblInsets = (JSONObject) yAxisProperties.get("insets");
					if (yAxisLblInsets != null && yAxisLblInsets.size() > 0 ) {
						int yAxisLblInsetTop = Integer.parseInt((String) yAxisLblInsets.get("top"));
				        int yAxisLblInsetBottom = Integer.parseInt((String) yAxisLblInsets.get("bottom"));
				        int yAxisLblInsetLeft = Integer.parseInt((String)  yAxisLblInsets.get("left"));
				        int yAxisLblInsetRight = Integer.parseInt((String)  yAxisLblInsets.get("right"));
				        yAxisLbl.setInsets(InsetsImpl.create(yAxisLblInsetTop, yAxisLblInsetLeft, yAxisLblInsetBottom, yAxisLblInsetRight));
				    }
					JSONObject yAxisLblOutLine = (JSONObject) yAxisProperties.get("outline");
					if (yAxisLblOutLine != null && yAxisLblOutLine.size() > 0) {
						Boolean isYAxisLblOutLineVisible = (Boolean) yAxisLblOutLine.get("visible");
				        if(isYAxisLblOutLineVisible){
				        	int yAxisLblOutlineWidthInt = Integer.parseInt((String) yAxisLblOutLine.get("width"));
					        String yAxisLblOutlineColor = (String) yAxisLblOutLine.get("color");
					        yAxisLbl.setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(yAxisLblOutlineColor),ChartPreferenceHandler.getLineStyleType((String) yAxisLblOutLine.get("style")), yAxisLblOutlineWidthInt));
				        }
					}
					
					String yAxisLblFontColor = (String)yAxisProperties.get("color");
					String sName = (String)yAxisProperties.get("font-family");
					float fSize = Float.parseFloat((String)yAxisProperties.get("font-size"));
					String yAxisLblFontStyle = (String)yAxisProperties.get("font-style");
					String yAxisLblTextAlign = (String)yAxisProperties.get("text-align");
					
					boolean bBold = false;
					boolean bItalic = false;
					boolean bUnderline = false;
					boolean bStrikethrough = false;
					boolean bWordWrap = false;
					if(yAxisLblFontStyle.equalsIgnoreCase("bold"))
						bBold = true;
					yAxisLbl.getCaption().setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,ChartPreferenceHandler.getTextAlignment(yAxisLblTextAlign, null)));
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("yAxisLblFontColor "+yAxisLblFontColor);
					yAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisLblFontColor));
					yAxis.getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
					yAxis.getTitle().getCaption().setValue(ySeriesTitle);
					
					if (yAxisProperties.containsKey("titleColor")) { 
						String yAxisTitleFontColor = (String)yAxisProperties.get("titleColor");
						yAxis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisTitleFontColor));
					}
					yAxis.getTitle().getCaption().getFont().setName(sName);
					xAxis.getTitle().getCaption().getFont().setBold(bBold);
					if (yAxisProperties.containsKey("titleFontSize")) {
						float yAxisTitleFontSize = Float.parseFloat((String)yAxisProperties.get("titleFontSize"));
						yAxis.getTitle().getCaption().getFont().setSize(yAxisTitleFontSize);
					}
				}
				yAxis.getLabel().setVisible(visible);
				JSONObject yAxisGridProperties = (JSONObject) yAxisProperties.get("gridline");
				if (yAxisGridProperties != null && yAxisGridProperties.size() > 0) {
					Boolean isYAxisGridLineVisible = (Boolean) yAxisGridProperties.get("visible");
			        if(isYAxisGridLineVisible){
			        	int yAxisGridLineWidthInt = Integer.parseInt((String) yAxisGridProperties.get("width"));
			        	
			        	if (yAxisGridLineColor == null) {
			        		yAxisGridLineColor = (String) yAxisGridProperties.get("color");
			        	}
			        	xAxis.getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor));
				        yAxis.getMajorGrid().getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor));
				        yAxis.getMajorGrid().getLineAttributes().setStyle(ChartPreferenceHandler.getLineStyleType((String) yAxisGridProperties.get("style")));
				        yAxis.getMajorGrid().getLineAttributes().setThickness(yAxisGridLineWidthInt);
				        if(yAxisGridProperties.containsKey("gridstep")){
				        	int yAxisGridStep = Integer.parseInt((String) yAxisGridProperties.get("gridstep"));
				        	xAxis.getScale().setMajorGridsStepNumber(yAxisGridStep);
				        }
			        }
			        yAxis.getMajorGrid().getLineAttributes().setVisible(isYAxisGridLineVisible);
				}
				
				
				
				yAxis.getTitle().getCaption().getFont().setRotation(90);
			}
        	
			if ((ySeries != null) && (patternDateTime != null)) {
				String pattern = patternDateTime.get(ySeries);
				if (pattern != null) {
					yAxis.setFormatSpecifier(JavaDateFormatSpecifierImpl.create(pattern));
				}
			}

        	Query yQuery = QueryImpl.create("row[\""+ ySeries + "\"]");
        	if ((function != null) && !(function.equals("#")))
        	{
        		SeriesGrouping ySeriesGroup = SeriesGroupingImpl.create();
        		ySeriesGroup.setEnabled(true);
        		if (function.equalsIgnoreCase("DISTINCTCOUNT"))
        			ySeriesGroup.setAggregateExpression(function);
        		else
        			ySeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
        		
        		yQuery.setGrouping(ySeriesGroup);
        	}
        	
        	seriesY.getDataDefinition().add(yQuery);
        	seriesY.getLabel().setVisible(true);
        	Label baseTitle = LabelImpl.create();
        	baseTitle.getCaption().setValue(ySeries);
        	seriesY.setSeriesIdentifier(baseTitle.getCaption().getValue());
        	
        	if(riserType.equalsIgnoreCase("stacked")){              
               seriesY.setStacked(true);   //TODO
               AppLogger.getLogger().debug("setStacked is set to true");
               seriesY.getLabel().setVisible(false);
               
           }
        	
        	SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			if (topColors != null && i < topColors.size()){
				ySeriesDef.getSeriesPalette().getEntries().add(ChartPreferenceHandler.hexToRGB( (String) topColors.get(i)));
			}
			
        	ySeriesDef.getSeries().add(seriesY);
        	yAxis.setSideBySide(true);
        	yAxis.getSeriesDefinitions().add(ySeriesDef);
//        	yAxis.setOrigin(AxisOriginImpl.create(IntersectionType.VALUE_LITERAL, NumberDataElementImpl.create(0.0)));
            i++;
            
            
            
            if(yGrouping != null && !("none".equalsIgnoreCase(yGrouping)) && !("".equals(yGrouping)))
            {
	            SeriesDefinition sdGroup = SeriesDefinitionImpl.create( );
	            Query query = QueryImpl.create( "row[\""+yGrouping+"\"]" );
	            sdGroup.setQuery( query );
	            yAxis.getSeriesDefinitions( ).clear( ); // Clear the original
	            yAxis.getSeriesDefinitions( ).add( 0, sdGroup );
	            sdGroup.getSeries( ).add( ySeriesDef.getSeries( ).get( 0 ) );
            }
            
           
        	
           
        	
        }
        
        if (dimension == ChartDimension.THREE_DIMENSIONAL )
        {
        	Axis zAxis = AxisImpl.create(Axis.ANCILLARY_BASE);
        	zAxis.setType(AxisType.TEXT_LITERAL);
        	zAxis.getTitle().getCaption().setValue("Z - Axis Title");
    		zAxis.setPrimaryAxis(true);
    		
    		zAxis.getOrigin().setType(IntersectionType.MIN_LITERAL);
    		zAxis.getOrigin().setValue(NumberDataElementImpl.create(0.0));
    		
    		SeriesDefinition zSeriesDef = SeriesDefinitionImpl.create();
    		zSeriesDef.getSeriesPalette().shift(-(i+2));
    		zSeriesDef.getSeries().add(SeriesImpl.create());
    		
    		zAxis.getSeriesDefinitions().add(zSeriesDef);
    		zAxis.setOrientation(Orientation.HORIZONTAL_LITERAL);
    		xAxis.getAncillaryAxes().add(zAxis);
    		xAxis.setCategoryAxis(true);
    		
    		chart.setOrientation(Orientation.VERTICAL_LITERAL);
    		chart.setUnitSpacing(50.0);

    		Angle3D angle3D = Angle3DImpl.create(-20.0, 45.0, 0.0);
    		angle3D.setType(AngleType.NONE_LITERAL);
    		
    		Angle3D[] angle3DArray = new Angle3D[1];
    		angle3DArray[0] = angle3D;
    		
    		Rotation3D rotation3D = Rotation3DImpl.create(angle3DArray);
    		chart.setRotation(rotation3D);
    		String wallBackground = (String) commonJSON.get("wallBackground");
    		String floorBackground = (String) commonJSON.get("floorBackground");
    		chart.setWallFill(ChartPreferenceHandler.hexToRGB(wallBackground));
    		chart.setFloorFill(ChartPreferenceHandler.hexToRGB(floorBackground));
    		
        }
        
        return chart;
	}
	

	private Chart getLineChart(String title, int dimension, int chartWidth,
			int chartHeight, JSONObject xSeriesObj, JSONObject ySeriesJSON,
			String xSeriesTitle, String ySeriesTitle, JSONObject chartPreferences, boolean isHive, String yGrouping,String ySeriesScale, String yScaleMinVal)
			throws Exception {
		
		
		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");
		
		ChartWithAxes chart = ChartWithAxesImpl.create();
		
		chart.setType("Line Chart");
		chart.setSubType("Standard");
		Plot p = chart.getPlot();
		
		if (dimension == ChartDimension.THREE_DIMENSIONAL && ySeriesJSON.size()>1)
        {
			dimension = ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH;
        	
        }
		
		switch (dimension)
		{
			case ChartDimension.TWO_DIMENSIONAL : 				chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
																break;
			case ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH : 	chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
																break;
			case ChartDimension.THREE_DIMENSIONAL : 			chart.setDimension(ChartDimension.THREE_DIMENSIONAL_LITERAL);
																break;
			default : 											chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
																break;
		}

		
		chart.getBlock().getBounds().setWidth(chartWidth);
		chart.getBlock().getBounds().setHeight(chartHeight);

		chart.getBlock().getOutline().setVisible(true);
		
		// Plot

		chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());

		chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.WHITE());

		// Title

        chart.getPlot().setBackground(ColorDefinitionImpl.WHITE());
        chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.TRANSPARENT());
        
        
        
        /* Legend */
        Legend legend = chart.getLegend();
        JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);
        
		
        chart.getTitle().getLabel().getCaption().setValue(title);
        chart.getTitle().getLabel().getCaption().getFont().setSize(14);
       
        Axis xAxis = chart.getPrimaryBaseAxes()[0];
        String yAxisGridLineColor = null;
        xAxis.setOrigin(AxisOriginImpl.create(IntersectionType.VALUE_LITERAL, NumberDataElementImpl.create(0.0)));

        JSONObject xAxisProperties = (JSONObject) chartPreferences.get("xAxisJson");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("xAxisProperties JSON : "+xAxisProperties.toJSONString());
		if (xAxisProperties != null && xAxisProperties.size() > 0) {
		
			Boolean visible = (Boolean) xAxisProperties.get("visible");
			if(visible){
				String position =  (String) xAxisProperties.get("position");
				if(position.equalsIgnoreCase("above")){
					xAxis.setLabelPosition(Position.ABOVE_LITERAL);
				}else if(position.equalsIgnoreCase("below")){
					xAxis.setLabelPosition(Position.BELOW_LITERAL);
				}
				
				Scale scale = xAxis.getScale();
				scale.setTickBetweenCategories(false);

				String xAxisLblBackground = (String) xAxisProperties.get("background");
				String xAxisLblShadow = (String) xAxisProperties.get("shadow");
				Label xAxisLbl = xAxis.getLabel();
				xAxisLbl.setVisible(true);
				xAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(xAxisLblBackground));
				xAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(xAxisLblShadow));
				JSONObject xAxisLblInsets = (JSONObject) xAxisProperties.get("insets");
				if (xAxisLblInsets != null && xAxisLblInsets.size() > 0) {
					int xAxisLblInsetTop = Integer.parseInt((String) xAxisLblInsets.get("top"));
			        int xAxisLblInsetBottom = Integer.parseInt((String) xAxisLblInsets.get("bottom"));
			        int xAxisLblInsetLeft = Integer.parseInt((String)  xAxisLblInsets.get("left"));
			        int xAxisLblInsetRight = Integer.parseInt((String)  xAxisLblInsets.get("right"));
			        xAxisLbl.setInsets(InsetsImpl.create(xAxisLblInsetTop, xAxisLblInsetLeft, xAxisLblInsetBottom, xAxisLblInsetRight));
			    }
				JSONObject xAxisLblOutLine = (JSONObject) xAxisProperties.get("outline");
				if (xAxisLblOutLine != null && xAxisLblOutLine.size() > 0) {
					Boolean isxAxisLblOutLineVisible = (Boolean) xAxisLblOutLine.get("visible");
			        if(isxAxisLblOutLineVisible){
			        	int xAxisLblOutlineWidthInt = Integer.parseInt((String) xAxisLblOutLine.get("width"));
				        String xAxisLblOutlineColor = (String) xAxisLblOutLine.get("color");
				        xAxisLbl.setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(xAxisLblOutlineColor),ChartPreferenceHandler.getLineStyleType((String) xAxisLblOutLine.get("style")), xAxisLblOutlineWidthInt));
			        }
				}

				String xAxisLblFontColor = (String)xAxisProperties.get("color");
				String sName = (String)xAxisProperties.get("font-family");
				float fSize = Float.parseFloat((String)xAxisProperties.get("font-size"));
				String xAxisLblFontStyle = (String)xAxisProperties.get("font-style");
				String xAxisLblTextAlign = (String)xAxisProperties.get("text-align");
				
				boolean bBold = false;
				boolean bItalic = false;
				boolean bUnderline = false;
				boolean bStrikethrough = false;
				boolean bWordWrap = false;
				if(xAxisLblFontStyle.equalsIgnoreCase("bold"))
					bBold = true;
				xAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisLblFontColor));
				xAxisLbl.getCaption().setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,ChartPreferenceHandler.getTextAlignment(xAxisLblTextAlign, null)));
				xAxis.setLabel(xAxisLbl);
				xAxis.getTitle().getCaption().setValue(xSeriesTitle);
				if (xAxisProperties.containsKey("titleColor")) {
					String xAxisTitleFontColor = (String)xAxisProperties.get("titleColor");
					xAxis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisTitleFontColor));
				}
				if (xAxisProperties.containsKey("titleFontSize")) {
					float xAxisTitleFontSize = Float.parseFloat((String)xAxisProperties.get("titleFontSize"));
					xAxis.getTitle().getCaption().setFont(FontDefinitionImpl.create(sName, xAxisTitleFontSize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,ChartPreferenceHandler.getTextAlignment(xAxisLblTextAlign, null)));
					xAxis.getTitle().getCaption().getFont().setSize(xAxisTitleFontSize);
				}
				xAxis.getTitle().getCaption().getFont().setName(sName);
				xAxis.getTitle().getCaption().getFont().setBold(bBold);
				xAxis.getTitle().setVisible(true);
				xAxis.getTitle().getCaption().getFont().setRotation(0);
			}
			xAxis.getLabel().setVisible(visible);
			JSONObject xAxisGridProperties = (JSONObject) xAxisProperties.get("gridline");
			
			if (xAxisGridProperties != null && xAxisGridProperties.size() > 0) {
				Boolean isYAxisGridLineVisible = (Boolean) xAxisGridProperties.get("visible");
		        if(isYAxisGridLineVisible){
		        	int yAxisGridLineWidthInt = Integer.parseInt((String) xAxisGridProperties.get("width"));
		        	String xAxisGridLineColor = (String) xAxisGridProperties.get("color");
		        	xAxis.getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(xAxisGridLineColor));
		        	xAxis.getMajorGrid().getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(xAxisGridLineColor));
			        xAxis.getMajorGrid().getLineAttributes().setStyle(ChartPreferenceHandler.getLineStyleType((String) xAxisGridProperties.get("style")));
			        xAxis.getMajorGrid().getLineAttributes().setThickness(yAxisGridLineWidthInt);
			        if(xAxisGridProperties.containsKey("gridstep")){
			        	int xAxisGridStep = Integer.parseInt((String) xAxisGridProperties.get("gridstep"));
			        	xAxis.getScale().setMajorGridsStepNumber(xAxisGridStep);
			        }
			       
			        	
		        }
		        xAxis.getMajorGrid().getLineAttributes().setVisible(isYAxisGridLineVisible);
			}
			
		}
		
		String xSeries = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES);
		
		if ((xSeries != null)  && (patternDateTime != null)) {
			String pattern = patternDateTime.get(xSeries);
			if (pattern != null) {
				xAxis.setFormatSpecifier(JavaDateFormatSpecifierImpl.create(pattern));
			}
		}
		
		
		/* Plot */
		JSONArray topColors  = null;
		if(commonJSON!=null){

			chart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			chart.getBlock().getBounds().setWidth(chartWidth);
			chart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
	        ClientArea clientArea = p.getClientArea();
	        clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
	        
	        //Empty message
	        String msg = (String)commonJSON.get("emptyChartMessage");
	        if( msg != null && msg.length() > 0){
	        	chart.getEmptyMessage().setVisible(true);
	        	chart.getEmptyMessage().getCaption().setValue(msg);
	        }
	        //Series palette
	        topColors = (JSONArray) commonJSON.get("topColors");

		}
        
        Series seriesX = SeriesImpl.create();
        if(isHive){
        	xSeries =xSeries.toLowerCase();
        }
        	
        Query xQuery = QueryImpl.create("row[\""+ xSeries + "\"]");
        seriesX.getDataDefinition().add(xQuery);
        seriesX.setSeriesIdentifier(xSeriesTitle);
        
        SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
        xAxis.getSeriesDefinitions().add(xSeriesDef);
        if(topColors!=null){
	        for( int i = 0; i< topColors.size(); i++){
	        	xSeriesDef.getSeriesPalette().getEntries().add(ChartPreferenceHandler.hexToRGB((String)topColors.get(i)));
	        }
        }
        xSeriesDef.getSeriesPalette().shift(0);
        xSeriesDef.getSeries().add(seriesX);
        
        String sortType = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_TYPE);
		
		if (!BigQueryIdentifiers.X_SERIES_SORT_TYPE_NONE.equalsIgnoreCase(sortType)) {
			String sortColumn = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_COLUMN);
			Query xSortQuery = QueryImpl.create("row[\""+ sortColumn + "\"]");
			
			if (BigQueryIdentifiers.X_SERIES_SORT_TYPE_ASCENDING.equalsIgnoreCase(sortType))
				xSeriesDef.setSorting(SortOption.ASCENDING_LITERAL);
			else 
				xSeriesDef.setSorting(SortOption.DESCENDING_LITERAL);
			
			xSeriesDef.setSortKey(xSortQuery);
			xSeriesDef.setSortStrength(2);
		}
        
		ChartPreferenceHandler.setTitleProperties(titleJSON, chart, title);
		//Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
	        if(isChartOutLineVisible){
	        	int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
		        String titleOutlineColor = (String) chartOutLine.get("color");
		        chart.getBlock().setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(titleOutlineColor),ChartPreferenceHandler.getLineStyleType((String) chartOutLine.get("style")), chartOutlineWidthInt));
	        }
				
		}
		//Chart insets.
		JSONObject chartInsets = (JSONObject) chartPreferences.get("insetsJson");
		if (chartInsets != null && chartInsets.size() > 0) {
			int titleInsetTop = Integer.parseInt((String) chartInsets.get("top"));
		    int titleInsetBottom = Integer.parseInt((String) chartInsets.get("bottom"));
		    int titleInsetLeft = Integer.parseInt((String)  chartInsets.get("left"));
		    int titleInsetRight = Integer.parseInt((String)  chartInsets.get("right"));
		    chart.getTitle().setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
		}
		
		
				
        boolean primary = true;
        SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
        seriesGroup.setEnabled(true);
		
        xSeriesDef.setGrouping(seriesGroup);
        
        Iterator it = ySeriesJSON.keySet().iterator();
        int i=0;
        
        while (it.hasNext())
        {
        	LineSeries seriesY = (LineSeries) LineSeriesImpl.create();
        	JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
			ChartPreferenceHandler.setLabelProperties(labelProperties, seriesY, true,dimension);

			// TODO Add handling in UI for this.
			// for icons on line chart, rectangle / box / circle, etc.
			seriesY.getMarkers().get(0).setType(MarkerType.CIRCLE_LITERAL);
			seriesY.getMarkers().get(0).setSize(2);
//			seriesY.getMarkers().get(0).setVisible(false);
			
			//lineChartJson.
			JSONObject lineChartJson = (JSONObject) chartPreferences.get("lineChartJson");
			if (lineChartJson != null && lineChartJson.size() > 0) {
				Boolean showCurve = (Boolean) lineChartJson.get("showCurve");
				if(showCurve){
					String curveColor = (String) lineChartJson.get("curveColor");
					seriesY.setCurve(true);
				}
			}
        	String ySeries = (String) it.next();
        	String function = (String) ySeriesJSON.get(ySeries);

        	if ((ySeries != null) && (isHive))
        		ySeries = ySeries.toLowerCase();
        	
        	Axis yAxis;
        	
        	Axis[] axaOrthogonal = chart.getOrthogonalAxes(xAxis, true);
        	yAxis = axaOrthogonal[0];
        	if("Linear".equals(ySeriesScale)){
        		yAxis.setType(AxisType.LINEAR_LITERAL);
        		try{
        			
        			Scale yscale = yAxis.getScale(); 
        			yscale.setAutoExpand(true); 
        			if (yScaleMinVal != null && !yScaleMinVal.isEmpty())
        				yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(yScaleMinVal)));
        		}catch(NumberFormatException e){
        			//ignore.
        			AppLogger.getLogger().error(e.getMessage(), e);
        		}
        	}else if("Logarithmic".equals(ySeriesScale)){
        		yAxis.setType(AxisType.LOGARITHMIC_LITERAL);
        		Scale yscale = yAxis.getScale(); 
    			yscale.setAutoExpand(true);
        		try{
        			if (yScaleMinVal!= null && !yScaleMinVal.isEmpty())
        				yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(yScaleMinVal)));
        		}catch(NumberFormatException e){
        		 	yscale.setMin( NumberDataElementImpl.create(Integer.valueOf(1)));
        		}
        		yscale.setMin( NumberDataElementImpl.create(1)); 
        	}else if("DateTime".equals(ySeriesScale)){
        		yAxis.setType(AxisType.DATE_TIME_LITERAL);
        	}else{
        		yAxis.setType(AxisType.LINEAR_LITERAL);
        	}
        	if(primary){
        		 // Y-Axis
//        		yAxis = chart.getPrimaryOrthogonalAxis(xAxis);
        		yAxis.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
        		yAxis.getTitle().setVisible(true);
        		yAxis.setSideBySide(true);
        		
//        		primary = false;
        	} else {
        		// Y-Axis (2)
//        		yAxis = AxisImpl.create(Axis.ORTHOGONAL);
//        		yAxis.setType(AxisType.LINEAR_LITERAL);
//        		yAxis.getMajorGrid().setTickStyle(TickStyle.RIGHT_LITERAL);
//        		xAxis.getAssociatedAxes().add(yAxis);
//        		yAxis.setSideBySide(true);
        	}
        	
        	JSONObject yAxisProperties = (JSONObject) chartPreferences.get("yAxisJson");
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("yAxisProperties JSON : "+yAxisProperties.toJSONString());
			if (yAxisProperties != null && yAxisProperties.size() > 0) {
				Boolean visible = (Boolean) yAxisProperties.get("visible");
				if(visible){
					String position =  (String) yAxisProperties.get("position");
					if(position.equalsIgnoreCase("right")){
						yAxis.setLabelPosition(Position.RIGHT_LITERAL);
					}else if(position.equalsIgnoreCase("left")){
						yAxis.setLabelPosition(Position.LEFT_LITERAL);
					}
					String yAxisLblBackground = (String) yAxisProperties.get("background");
					String yAxisLblShadow = (String) yAxisProperties.get("shadow");
					Label yAxisLbl = yAxis.getLabel();
					yAxisLbl.setVisible(true);
					yAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(yAxisLblBackground));
					yAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(yAxisLblShadow));
					JSONObject yAxisLblInsets = (JSONObject) yAxisProperties.get("insets");
					if (yAxisLblInsets != null && yAxisLblInsets.size() > 0) {
						int yAxisLblInsetTop = Integer.parseInt((String) yAxisLblInsets.get("top"));
				        int yAxisLblInsetBottom = Integer.parseInt((String) yAxisLblInsets.get("bottom"));
				        int yAxisLblInsetLeft = Integer.parseInt((String)  yAxisLblInsets.get("left"));
				        int yAxisLblInsetRight = Integer.parseInt((String)  yAxisLblInsets.get("right"));
				        yAxisLbl.setInsets(InsetsImpl.create(yAxisLblInsetTop, yAxisLblInsetLeft, yAxisLblInsetBottom, yAxisLblInsetRight));
				    }
					JSONObject yAxisLblOutLine = (JSONObject) yAxisProperties.get("outline");
					if (yAxisLblOutLine != null && yAxisLblOutLine.size() > 0) {
						Boolean isYAxisLblOutLineVisible = (Boolean) yAxisLblOutLine.get("visible");
				        if(isYAxisLblOutLineVisible){
				        	int yAxisLblOutlineWidthInt = Integer.parseInt((String) yAxisLblOutLine.get("width"));
					        String yAxisLblOutlineColor = (String) yAxisLblOutLine.get("color");
					        yAxisLbl.setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(yAxisLblOutlineColor),ChartPreferenceHandler.getLineStyleType((String) yAxisLblOutLine.get("style")), yAxisLblOutlineWidthInt));
				        }
					}
					
					String yAxisLblFontColor = (String)yAxisProperties.get("color");
					String sName = (String)yAxisProperties.get("font-family");
					float fSize = Float.parseFloat((String)yAxisProperties.get("font-size"));
					String yAxisLblFontStyle = (String)yAxisProperties.get("font-style");
					String yAxisLblTextAlign = (String)yAxisProperties.get("text-align");
					
					boolean bBold = false;
					boolean bItalic = false;
					boolean bUnderline = false;
					boolean bStrikethrough = false;
					boolean bWordWrap = false;
					if(yAxisLblFontStyle.equalsIgnoreCase("bold"))
						bBold = true;
					yAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisLblFontColor));
					yAxisLbl.getCaption().setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,ChartPreferenceHandler.getTextAlignment(yAxisLblTextAlign, null)));
					yAxis.setLabel(yAxisLbl);
					
					yAxis.getTitle().getCaption().setValue(ySeriesTitle);
					
					if (yAxisProperties.containsKey("titleFontSize")) {
						float yAxisTitleFontSize = Float.parseFloat((String)yAxisProperties.get("titleFontSize"));
						yAxis.getTitle().getCaption().getFont().setSize(yAxisTitleFontSize);
					}
					if (yAxisProperties.containsKey("titleColor")) {
						String yAxisTitleFontColor = (String)yAxisProperties.get("titleColor");
						yAxis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisTitleFontColor));
					}
					yAxis.getTitle().getCaption().getFont().setName(sName);
					xAxis.getTitle().getCaption().getFont().setBold(bBold);					
					yAxis.getTitle().getCaption().getFont().setRotation(90);
				}
				JSONObject yAxisGridProperties = (JSONObject) yAxisProperties.get("gridline");
				if (yAxisGridProperties != null && yAxisGridProperties.size() > 0) {
					Boolean isYAxisGridLineVisible = (Boolean) yAxisGridProperties.get("visible");
			        if(isYAxisGridLineVisible){
			        	int yAxisGridLineWidthInt = Integer.parseInt((String) yAxisGridProperties.get("width"));
			        	

			        	if (yAxisGridLineColor == null) {
			        		yAxisGridLineColor = (String) yAxisGridProperties.get("color");
			        	}
			        	yAxis.getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor)); 
				        yAxis.getMajorGrid().getLineAttributes().setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor));
				        yAxis.getMajorGrid().getLineAttributes().setStyle(ChartPreferenceHandler.getLineStyleType((String) yAxisGridProperties.get("style")));
				        yAxis.getMajorGrid().getLineAttributes().setThickness(yAxisGridLineWidthInt);
				        if(yAxisGridProperties.containsKey("gridstep")){
				        	int yAxisGridStep = Integer.parseInt((String) yAxisGridProperties.get("gridstep"));
				        	yAxis.getScale().setMajorGridsStepNumber(yAxisGridStep);
				        }
			        }
			        yAxis.getMajorGrid().getLineAttributes().setVisible(isYAxisGridLineVisible);
				}
				
				yAxis.getLabel().setVisible(visible);
				
			}
	
        	
			if ((ySeries != null) && (patternDateTime != null)) {
				String pattern = patternDateTime.get(ySeries);
				if (pattern != null) {
					yAxis.setFormatSpecifier(JavaDateFormatSpecifierImpl.create(pattern));
				}
			}
        	
        	
        	Query yQuery = QueryImpl.create("row[\""+ ySeries + "\"]");

        	if ((function != null) && !(function.equals("#")))
        	{
        		SeriesGrouping ySeriesGroup = SeriesGroupingImpl.create();
        		ySeriesGroup.setEnabled(true);
        		if (function.equalsIgnoreCase("DISTINCTCOUNT"))
        			ySeriesGroup.setAggregateExpression(function);
        		else
        			ySeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
        		
        		yQuery.setGrouping(ySeriesGroup);
        	}
        	
        	seriesY.getDataDefinition().add(yQuery);
        	
        	Label baseTitle = LabelImpl.create();
        	baseTitle.getCaption().setValue(ySeries);
        	
        	seriesY.setSeriesIdentifier(baseTitle.getCaption().getValue());
        	
        	SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			if (topColors != null && i < topColors.size()){
				ySeriesDef.getSeriesPalette().getEntries().add(ChartPreferenceHandler.hexToRGB( (String) topColors.get(i)));
			}
        	yAxis.getSeriesDefinitions().add(ySeriesDef);
            ySeriesDef.getSeries().add(seriesY);
            if(yGrouping != null && !("none".equalsIgnoreCase(yGrouping)) && !("".equals(yGrouping)))
            {
	            SeriesDefinition sdGroup = SeriesDefinitionImpl.create( );
	            Query query = QueryImpl.create( "row[\""+yGrouping+"\"]" );
	            sdGroup.setQuery( query );
	            yAxis.getSeriesDefinitions( ).clear( ); // Clear the original
	            yAxis.getSeriesDefinitions( ).add( 0, sdGroup );
	            sdGroup.getSeries( ).add( ySeriesDef.getSeries( ).get( 0 ) );
            }
            
            i++;
        }
        
        if (dimension == ChartDimension.THREE_DIMENSIONAL )
        {
        	Axis zAxis = AxisImpl.create(Axis.ANCILLARY_BASE);
        	zAxis.setType(AxisType.TEXT_LITERAL);
        	zAxis.getTitle().getCaption().setValue("Z - Axis Title");
    		zAxis.setPrimaryAxis(true);
    		
    		zAxis.getOrigin().setType(IntersectionType.MIN_LITERAL);
    		zAxis.getOrigin().setValue(NumberDataElementImpl.create(0.0));
    		
    		SeriesDefinition zSeriesDef = SeriesDefinitionImpl.create();
    		zSeriesDef.getSeriesPalette().shift(-(i+2));
    		zSeriesDef.getSeries().add(SeriesImpl.create());
    		
    		zAxis.getSeriesDefinitions().add(zSeriesDef);
    		zAxis.setOrientation(Orientation.HORIZONTAL_LITERAL);
    		xAxis.getAncillaryAxes().add(zAxis);
    		xAxis.setCategoryAxis(true);
    		
    		chart.setOrientation(Orientation.VERTICAL_LITERAL);
    		chart.setUnitSpacing(50.0);

    		Angle3D angle3D = Angle3DImpl.create(-20.0, 45.0, 0.0);
    		angle3D.setType(AngleType.NONE_LITERAL);
    		
    		Angle3D[] angle3DArray = new Angle3D[1];
    		angle3DArray[0] = angle3D;
    		
    		Rotation3D rotation3D = Rotation3DImpl.create(angle3DArray);
    		chart.setRotation(rotation3D);
    		String wallBackground = (String) commonJSON.get("wallBackground");
    		String floorBackground = (String) commonJSON.get("floorBackground");
    		chart.setWallFill(ChartPreferenceHandler.hexToRGB(wallBackground));
    		chart.setFloorFill(ChartPreferenceHandler.hexToRGB(floorBackground));
        }
        return chart;
	}
	


	
	private LeaderLineStyle getLeaderLineStyle(int style)
	{
		switch(style)
		{
			case LeaderLineStyle.FIXED_LENGTH: return LeaderLineStyle.FIXED_LENGTH_LITERAL;
			case LeaderLineStyle.STRETCH_TO_SIDE: return LeaderLineStyle.STRETCH_TO_SIDE_LITERAL;
			default: return LeaderLineStyle.FIXED_LENGTH_LITERAL;
		}
	}
	
	private Chart getPieChart(String title, int dimension, int chartWidth,
			int chartHeight, JSONObject xSeriesObj, JSONObject ySeriesJSON,
			String xSeriesTitle, String ySeriesTitle, JSONObject chartPreferences, boolean isHive, String yGrouping)
			throws Exception
	{
		
		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");
		ChartWithoutAxes pieChart = ChartWithoutAxesImpl.create();
		
		
			
		pieChart.setType("Pie Chart");
		
		pieChart.setSubType("Standard");
		Plot p = pieChart.getPlot();
		ClientArea clientArea = p.getClientArea();
		
	        
		
		switch (dimension)
		{
			case ChartDimension.TWO_DIMENSIONAL : 				pieChart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
																break;
			case ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH : 	pieChart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
																break;
			default : 											pieChart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
																break;
		}
		
				
		/* common json properties */
		JSONArray topColors = null;
		if(commonJSON!=null){
			pieChart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			pieChart.getBlock().getBounds().setWidth(chartWidth);
			pieChart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
	        clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
	        //Empty message
	        String msg = (String)commonJSON.get("emptyChartMessage");
	        if( msg != null && msg.length() > 0){
	        	pieChart.getEmptyMessage().setVisible(true);
	        	pieChart.getEmptyMessage().getCaption().setValue(msg);
	        }
	        topColors = (JSONArray)commonJSON.get("topColors");
		}
		ChartPreferenceHandler.setTitleProperties(titleJSON, pieChart, title);
		//Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
	        if(isChartOutLineVisible){
	        	int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
		        String chartOutlineColor = (String) chartOutLine.get("color");
		        pieChart.getBlock().setOutline(LineAttributesImpl.create( ChartPreferenceHandler.hexToRGB(chartOutlineColor),ChartPreferenceHandler.getLineStyleType((String) chartOutLine.get("style")), chartOutlineWidthInt));
	        }
		}
		
		//Chart insets.
		JSONObject titleinsets = (JSONObject) chartPreferences.get("insetsJson");
		if (titleinsets != null && titleinsets.size() > 0) {
			int titleInsetTop = Integer.parseInt((String) titleinsets.get("top"));
	        int titleInsetBottom = Integer.parseInt((String) titleinsets.get("bottom"));
	        int titleInsetLeft = Integer.parseInt((String)  titleinsets.get("left"));
	        int titleInsetRight = Integer.parseInt((String)  titleinsets.get("right"));
	        pieChart.getTitle().setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
	    }
		
		
		
        
        /* Legend */
        Legend legend = pieChart.getLegend();
        JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);
        
        
        /* X-Axis */
        Series xSeries = SeriesImpl.create();
        
        String xSeriesPie = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES);
        if(isHive){
        	xSeriesPie = xSeriesPie.toLowerCase();
        }
        	 
        Query xQuery = QueryImpl.create("row[\""+ xSeriesPie + "\"]");
        xSeries.getDataDefinition().add(xQuery);
        
        xSeries.getLabel().getCaption().setValue(xSeriesTitle);
        xSeries.getLabel().setVisible(true);
        SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
        pieChart.getSeriesDefinitions().add(xSeriesDef);
        for( int i = 0; i< topColors.size(); i++){
        	xSeriesDef.getSeriesPalette().getEntries().add(i,ChartPreferenceHandler.hexToRGB((String)topColors.get(i)));
        }
        
        xSeriesDef.getSeriesPalette().shift(0);
        xSeriesDef.getSeries().add(xSeries);
        SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
        seriesGroup.setEnabled(true);
		xSeriesDef.setGrouping(seriesGroup);
		
		String sortType = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_TYPE);
		
		if (!BigQueryIdentifiers.X_SERIES_SORT_TYPE_NONE.equalsIgnoreCase(sortType)) {
			String sortColumn = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_COLUMN);
			Query xSortQuery = QueryImpl.create("row[\""+ sortColumn + "\"]");
			
			if (BigQueryIdentifiers.X_SERIES_SORT_TYPE_ASCENDING.equalsIgnoreCase(sortType))
				xSeriesDef.setSorting(SortOption.ASCENDING_LITERAL);
			else 
				xSeriesDef.setSorting(SortOption.DESCENDING_LITERAL);
			
			xSeriesDef.setSortKey(xSortQuery);
			xSeriesDef.setSortStrength(2);
		}
		
        Iterator it = ySeriesJSON.keySet().iterator();
        int i = 0;
        while(it.hasNext())
        {
        	PieSeries pieLeader  =  (PieSeries) PieSeriesImpl.create();
    		pieLeader.setCurveFitting(null);

    		//Label JSON
    		JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
    		ChartPreferenceHandler.setLabelProperties(labelProperties, pieLeader, false,dimension);
    		

    		//Leader Lines
    		
    		JSONObject leaderLinesProperties = (JSONObject) chartPreferences.get("leaderLineJson");
    		if (leaderLinesProperties != null && leaderLinesProperties.size() > 0) {
    		
    			Boolean visible = (Boolean) leaderLinesProperties.get("visible");
    			LineAttributes ls = pieLeader.getLeaderLineAttributes();;
    			
    			if(visible){
    		        String leaderStyle = (String)leaderLinesProperties.get("style");
    		        Integer leaderWidth = Integer.parseInt((String) leaderLinesProperties.get("width"));
    		        String leaderColor = (String)leaderLinesProperties.get("color");
    		        Double leaderLineLength = Double.parseDouble((String)leaderLinesProperties.get("lineLength"));
    		        ls.setThickness(leaderWidth);
    		        ls.setStyle(ChartPreferenceHandler.getLineStyleType((String)leaderLinesProperties.get("lineStyle")));
    		        ls.setColor(ChartPreferenceHandler.hexToRGB(leaderColor));
    		        		        	
    		        
    		        pieLeader.setLeaderLineLength(leaderLineLength);
    		         if(leaderStyle.equalsIgnoreCase("stretch")){
    			    	pieLeader.setLeaderLineStyle(LeaderLineStyle.STRETCH_TO_SIDE_LITERAL);
    			      }else{
    			      	pieLeader.setLeaderLineStyle(LeaderLineStyle.FIXED_LENGTH_LITERAL);
    			    
    		        }
    			}
    			ls.setVisible(visible);
    		}
    		
        	String ySeriesPie = (String) it.next();
        	String function = (String) ySeriesJSON.get(ySeriesPie);

        	if ((ySeriesPie != null) && (isHive))
        		ySeriesPie = ySeriesPie.toLowerCase();
        	
        	Query yQuery = QueryImpl.create("row[\""+ ySeriesPie + "\"]");
        	
        	if ((function != null) && !(function.equals("#")))
        	{
        		SeriesGrouping pieSeriesGroup = SeriesGroupingImpl.create();
        		pieSeriesGroup.setEnabled(true);
        		if (function.equalsIgnoreCase("DISTINCTCOUNT"))
        			pieSeriesGroup.setAggregateExpression(function);
        		else
        			pieSeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
        		yQuery.setGrouping(pieSeriesGroup);
        	}
        	pieLeader.getDataDefinition().add(yQuery);
        	
        	
        	pieLeader.getLabel().getCaption().setValue(ySeriesTitle);
        	Label baseTitle = LabelImpl.create();
        	pieLeader.setSeriesIdentifier(baseTitle.getCaption().getValue());
        	SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
        	if(topColors != null){
        		ySeriesDef.getSeriesPalette( ).getEntries( ).clear();
        		for(int j=0;j<topColors.size();j++){
        			ySeriesDef.getSeriesPalette().getEntries().add(j,ChartPreferenceHandler.hexToRGB((String)topColors.get(j)));
        		}
        	}
        	xSeriesDef.getSeriesDefinitions().add(ySeriesDef);
        	ySeriesDef.getSeries().add(pieLeader);
        	
        	if(yGrouping != null && !("none".equalsIgnoreCase(yGrouping)) && !("".equals(yGrouping)))
            {
	            SeriesDefinition sdGroup = SeriesDefinitionImpl.create( );
	            Query query = QueryImpl.create( "row[\""+yGrouping+"\"]" );
	            sdGroup.setQuery( query );
	            xSeriesDef.getSeriesDefinitions( ).clear( ); // Clear the original
	            xSeriesDef.getSeriesDefinitions( ).add( 0, sdGroup );
	            sdGroup.getSeries( ).add( ySeriesDef.getSeries( ).get( 0 ) );
            
            }
        	
        }
        
        return pieChart;
	}
	

	
	private String getNumberFormat(String category, JSONObject patternObject) throws Exception
	{
		String format = null;
		FormatPatternInfo formatPatternInfo = getPatternInfo(patternObject);
		
		if (category.equals("Fixed"))
		{
//			realPattern = "#0.00"; //$NON-NLS-1$
//			numberFormat = new DecimalFormat(realPattern, new DecimalFormatSymbols());
		}
		else if (category.equals("Percent"))
		{
//			#,##0.0000%{RoundingMode=HALF_UP}
//			%#,##0.0000{RoundingMode=HALF_UP}
//			%#,##0{RoundingMode=HALF_UP}
			
			StringBuilder sb = new StringBuilder();
			
			if (formatPatternInfo.getSymbolPosition().equalsIgnoreCase("before"))
			{
				sb.append(formatPatternInfo.getSymbolNumber());
			}
			if (formatPatternInfo.isCommaSeparator())
			{
				sb.append("#,##0");
			}
			else
			{
				sb.append("###0");
			}
			int dec = formatPatternInfo.getDecimalPlace();
			if (dec > 0)
			{
				sb.append(".");
				for (int i=0; i<dec; i++)
					sb.append("0");
			}
			if (formatPatternInfo.getSymbolPosition().equalsIgnoreCase("after"))
			{
				sb.append(formatPatternInfo.getSymbolNumber());
			}
			sb.append("{RoundingMode=");
			sb.append(getRoundingModeType(formatPatternInfo.getRoundingMode()));
			sb.append("}");
			
			format = sb.toString();
		}
		else if (category.equals("Scientific"))
		{
		}
		else if (category.equals("Currency"))
	    {
//			$ #,##0.0000{RoundingMode=HALF_UP}
			
			StringBuilder sb = new StringBuilder();
			
			if (formatPatternInfo.getSymbolPosition().equalsIgnoreCase("before"))
			{
				sb.append(formatPatternInfo.getSymbolNumber());
				if (formatPatternInfo.isUseSymbolSpace())
				{
					sb.append(" ");
				}
			}
			if (formatPatternInfo.isCommaSeparator())
			{
				sb.append("#,##0");
			}
			else
			{
				sb.append("###0");
			}
			int dec = formatPatternInfo.getDecimalPlace();
			if (dec > 0)
			{
				sb.append(".");
				for (int i=0; i<dec; i++)
					sb.append("0");
			}
			if (formatPatternInfo.getSymbolPosition().equalsIgnoreCase("after"))
			{
				if (formatPatternInfo.isUseSymbolSpace())
				{
					sb.append(" ");
				}
				sb.append(formatPatternInfo.getSymbolNumber());
			}
			sb.append("{RoundingMode=");
			sb.append(getRoundingModeType(formatPatternInfo.getRoundingMode()));
			sb.append("}");
			
			format = sb.toString();
		}
		else			// (category.equals("General Number") || category.equals("Unformatted"))
		{

		}
		
		return format;
	}
	
	private String getRoundingModeType(String modeType) throws Exception
	{
		if (modeType.equalsIgnoreCase("half_up"))
			return RoundingMode.HALF_UP.toString();
		if (modeType.equalsIgnoreCase("half_down"))
			return RoundingMode.HALF_DOWN.toString();
		if (modeType.equalsIgnoreCase("half_even"))
			return RoundingMode.HALF_EVEN.toString();
		if (modeType.equalsIgnoreCase("up"))
			return RoundingMode.UP.toString();
		if (modeType.equalsIgnoreCase("down"))
			return RoundingMode.DOWN.toString();
		if (modeType.equalsIgnoreCase("ceiling"))
			return RoundingMode.CEILING.toString();
		if (modeType.equalsIgnoreCase("floor"))
			return RoundingMode.FLOOR.toString();
	
		return RoundingMode.UNNECESSARY.toString();
	}
	
	private String getStringFormat(String category) throws Exception
	{
		String format = null;
		
		if (category.equalsIgnoreCase("uppercase"))
		{
			format = ">";
		}
		else if (category.equalsIgnoreCase("lowercase"))
		{
			format = "<";
		}
		else		// Unformatted or Custom
		{
			
		}
		
		return format;
	}
	
	private String getDateTimeFormat(String birtFormat) throws Exception
	{
	    if ( "General Date".equalsIgnoreCase( birtFormat ) ) {
	            return "dd/MM/yyyy hh:mm";
	    }
	    if ( "Long Date".equalsIgnoreCase( birtFormat ) ) {
	            return "dddd, mmmm dd, yyyy";
	    }
	    if ( "Medium Date".equalsIgnoreCase( birtFormat ) ) {
	            return "ddd, dd mmm yyyy";
	    }
	    if ( "Short Date".equalsIgnoreCase( birtFormat ) ) {
	            return "yyyy-MM-dd";
	    }
	    if ( "Long Time".equalsIgnoreCase( birtFormat ) ) {
	            return "hh:mm:ss";
	    }
	    if ( "Medium Time".equalsIgnoreCase( birtFormat ) ) {
	            return "hh:mm";
	    }
	    if ( "Short Time".equalsIgnoreCase( birtFormat ) ) {
	            return "hh:mm";
	    }
        return birtFormat;
    }
	
	private int getDateFormatTime(String birtFormat) throws Exception
	{
	    if ( "General Date".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.FULL;
	    }
	    if ( "Long Date".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.FULL;
	    }
	    if ( "Medium Date".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.MEDIUM;
	    }
	    if ( "Short Date".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.SHORT;
	    }
	    if ( "Long Time".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.FULL;
	    }
	    if ( "Medium Time".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.MEDIUM;
	    }
	    if ( "Short Time".equalsIgnoreCase( birtFormat ) ) {
	    	return DateFormatType.SHORT;
	    }
	    
		return DateFormatType.MEDIUM;
    }
	
	private FormatPatternInfo getPatternInfo(JSONObject patternObject) throws Exception
	{
		FormatPatternInfo formatPatternInfo = null;
		if (patternObject != null)
		{
			Iterator it = patternObject.keySet().iterator();

			if (it.hasNext())
			{
				formatPatternInfo = new FormatPatternInfo();
				formatPatternInfo.setDecimalPlace(Integer.parseInt(String.valueOf(patternObject.get("decimalPlace"))));
				formatPatternInfo.setRoundingMode((String) patternObject.get("roundingMode"));
				formatPatternInfo.setCommaSeparator((Boolean) patternObject.get("commaSeparator"));
				formatPatternInfo.setUseSymbolSpace((Boolean) patternObject.get("useSymbolSpace"));				
				formatPatternInfo.setSymbolPosition((String) patternObject.get("symbolPosition"));
				formatPatternInfo.setSymbolNumber((String) patternObject.get("symbolNumber"));
				formatPatternInfo.setNegativeNumber((String) patternObject.get("negativeNumber"));
			}
		}
		
		return formatPatternInfo;
	}
	
	private ArrayList<ReportGroupInfo> getGroupInfoList(JSONObject groupJSON) throws Exception {
		ArrayList<ReportGroupInfo> groupInfoList = new ArrayList<ReportGroupInfo>();
		
		if(groupJSON!=null){
			Iterator it = groupJSON.keySet().iterator();

			while (it.hasNext())
			{
				String key = (String) it.next();
				JSONObject props = (JSONObject) groupJSON.get(key);
				
				ReportGroupInfo groupInfo = new ReportGroupInfo();
				groupInfo.setColumnName(key);
				groupInfo.setFunction((String) props.get("function"));
				groupInfo.setPrefix((String) props.get("prefix"));
				groupInfo.setSuffix((String) props.get("suffix"));
				groupInfo.setStyle((JSONObject) props.get("style"));
				
				groupInfoList.add(groupInfo);
			}
		}
		
		return groupInfoList;
	}
	
	private ArrayList<ReportGroupInfo> getGroupFooterList() throws Exception {
		return getGroupInfoList(this.groupFooterJSON);
	}

	private ArrayList<ReportGroupInfo> getGroupHeaderList() throws Exception {
		return getGroupInfoList(this.groupHeaderJSON);
	}
	
	public static String hex2Rgb(String colorStr) {
		
		if (colorStr.length() == 6)
			return ("#" + colorStr);
		
//		//// //AppLogger.getLogger().debug(colorStr);
	    return ColorDefinitionImpl.create(
	            Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
	            Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
	            Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) ).toString();
	}
	
	
	
	private static String getStyleFromName(HashMap allColumns, String columnName, String cssClass, String dbName) throws Exception
	{
		String columnType = null;
		
		DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);
		
		columnType = (String) allColumns.get(columnName);
		if (columnType == null)
			return cssClass;
		
		if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.LONG_WRAPPER_CLASS)) || 
				columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.INTEGER_WRAPPER_CLASS)) || 
				columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.DECIMAL_WRAPPER_CLASS)))
		{
			cssClass = cssClass.concat("Number");
		}
		else if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.STRING_WRAPPER_CLASS)))
		{
			cssClass = cssClass.concat("String");
		}
		else if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.TIMESTAMP_WRAPPER_CLASS)))
		{
			// if needed can add here.
		}
		
		return cssClass;
	}
	
	private static String getWidthFromType(HashMap allColumns, String columnName, String dbName) throws Exception
	{
		DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);
		String columnType = null;
		String width = "100px";
		columnType = (String) allColumns.get(columnName);
		if (columnType == null)
			return width;
		
		if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.LONG_WRAPPER_CLASS)) || 
				columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.INTEGER_WRAPPER_CLASS)) || 
				columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.DECIMAL_WRAPPER_CLASS)))
		{
			width = "100px";
		}
		else if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.STRING_WRAPPER_CLASS)))
		{
			width = "250px";
		}
		else if(columnType.equalsIgnoreCase(props.getTypeMap().get(MetadataConstants.TIMESTAMP_WRAPPER_CLASS)))
		{
			// if needed can add here.
		}
		
		return width;
	}
	
//	public static CusomTagDatabase getSecondaryCustomDBProperties(){
//		CusomTagDatabase dbProp = new CusomTagDatabase();
//		
//		dbProp.setUrl("jdbc:hsqldb:hsql://192.168.0.11:5681/bigquery");
//		dbProp.setDriverName("org.hsqldb.jdbcDriver");
//		dbProp.setUserName("ADMIN");
//		dbProp.setPassword("ADMIN");
//		
//		return dbProp;
//	}
}

class ReportGroupInfo{
	private String columnName;
	private String function;
	private String prefix;
	private String suffix;
	private JSONObject style;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public JSONObject getStyle() {
		return style;
	}
	public void setStyle(JSONObject style) {
		this.style = style;
	}
}

class FormatPatternInfo{
	private int decimalPlace;
	private String roundingMode;
	private boolean commaSeparator;
	private boolean useSymbolSpace;
	private String symbolPosition;
	private String symbolNumber;
	private String negativeNumber;
	
	/**
	 * @return the decimalPlace
	 */
	public int getDecimalPlace() {
		return decimalPlace;
	}
	/**
	 * @param decimalPlace the decimalPlace to set
	 */
	public void setDecimalPlace(int decimalPlace) {
		this.decimalPlace = decimalPlace;
	}
	/**
	 * @return the roundingMode
	 */
	public String getRoundingMode() {
		return roundingMode;
	}
	/**
	 * @param roundingMode the roundingMode to set
	 */
	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}
	/**
	 * @return the commaSeparator
	 */
	public boolean isCommaSeparator() {
		return commaSeparator;
	}
	/**
	 * @param commaSeparator the commaSeparator to set
	 */
	public void setCommaSeparator(boolean commaSeparator) {
		this.commaSeparator = commaSeparator;
	}
	/**
	 * @return the useSymbolSpace
	 */
	public boolean isUseSymbolSpace() {
		return useSymbolSpace;
	}
	/**
	 * @param useSymbolSpace the useSymbolSpace to set
	 */
	public void setUseSymbolSpace(boolean useSymbolSpace) {
		this.useSymbolSpace = useSymbolSpace;
	}
	/**
	 * @return the symbolPosition
	 */
	public String getSymbolPosition() {
		return symbolPosition;
	}
	/**
	 * @param symbolPosition the symbolPosition to set
	 */
	public void setSymbolPosition(String symbolPosition) {
		this.symbolPosition = symbolPosition;
	}
	/**
	 * @return the symbolNumber
	 */
	public String getSymbolNumber() {
		return symbolNumber;
	}
	/**
	 * @param symbolNumber the symbolNumber to set
	 */
	public void setSymbolNumber(String symbolNumber) {
		this.symbolNumber = symbolNumber;
	}
	/**
	 * @return the negativeNumber
	 */
	public String getNegativeNumber() {
		return negativeNumber;
	}
	/**
	 * @param negativeNumber the negativeNumber to set
	 */
	public void setNegativeNumber(String negativeNumber) {
		this.negativeNumber = negativeNumber;
	}
}