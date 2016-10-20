package com.queryio.demo.adhoc.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.MetadataConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.demo.adhoc.AdHocEntry;
import com.queryio.demo.adhoc.DBListener;
import com.queryio.demo.adhoc.IAdHocParser;
import com.queryio.demo.adhoc.ParsedExpression;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class CSVParser implements IAdHocParser{
	
	private static final Log LOG = LogFactory.getLog(CSVParser.class);
	final String ADHOC_PARSER_ENCODING = "encoding";
	final String  ADHOC_PARSER_DELIMITER = "delimiter";
	final String ADHOC_PARSER_VALUESEPARATOR = "valueSeparator";
	final String ADHOC_PARSER_ISFIRSTROWHEADER = "isFirstRowHeader";
	final String ADHOC_PARSER_SKIP_ALL_RECORDS = "isSkipAllRecords";
	
	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
	private Map<Integer, Integer> columnSize = new TreeMap<Integer, Integer>();
	private String delimiter = ",";
	private boolean isSkipAllRecords = false;
	private String valueSeparator = "\"";
	private boolean isFirstRowHeader = false;
	private String encoding = "UTF-8";
	ParsedExpression parsedExpression;
	
	@Override
	public void setExpressions(ParsedExpression parsedExpression) {
		this.parsedExpression = parsedExpression;
	}
	
	@Override
	public void setArguments(String arguments) throws Exception
	{
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(arguments);
		String argString = (String) obj.get("arguments");
		String fieldsString = (String) obj.get("fields");
		
		if (argString == null){
			throw new Exception("Invalid Arguments. Expected: <Arguments-JSonString> [<Fields-JSonString>]");
		}
		setArgumentsJSonString(argString);
		if (fieldsString != null){
			setFieldsJSonString(fieldsString);
		}
		else{
			initFieldsFromDataDefinition();
		}
	}
	
	private void initFieldsFromDataDefinition() {
		IDataDefinition dataDefinition = new CSVDataDefinitionImpl();
		int i = 0;
		for(ColumnMetadata metadata : dataDefinition.getColumnMetadata()){
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			columnSize.put(i, metadata.getSize());
			i++;
		}
	}

	private void setFieldsJSonString(String fieldsJSonString){
		try{
			JSONParser parser = new JSONParser();
	        JSONArray array = (JSONArray) parser.parse(fieldsJSonString);
	        JSONObject obj = null;
	        String dataType = "";
	        for (int i=0; i<array.size(); i++)
	        {
	            obj = (JSONObject) array.get(i);
	            String column = String.valueOf(obj.get("colName"));
	            
	            Class columnType = getDataType(obj.get("colType").toString());
	            int size = getSizeFromString(obj.get("colType").toString());
	            int index = Integer.parseInt(String.valueOf(obj.get("colIndex")));
	            columns.put(index, column);
	            columnTypes.put(index, columnType);
	            columnSize.put(index, size);
	            
	            LOG.info("colName: " + column);
	            LOG.info("colType: " + columnType);
	            LOG.info("colSize: " + size);
	            LOG.info("colIndex: " + index);
	        }
		}catch(Exception e){
			LOG.fatal(e.getLocalizedMessage(), e);
		}
	}
	
//	private String getDataType(String type, DBTypeProperties props)
//	{
//		String dataType = "";
//		int size = -1;
//		
//		if(type.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
//		{
//			dataType = props.getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(MetadataConstants.GENERIC_DATA_TYPE_STRING));
//			size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.lastIndexOf(")")));
//			return dataType + " (" + size + ")";
//		}
//		else
//		{
//			dataType = props.getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(type));
//			return dataType;
//		}
//	}
	
	private Class getDataType(String type)
	{
		Class dataType;
		
		if(type.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(MetadataConstants.GENERIC_DATA_TYPE_STRING);
		else
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(type);
		return dataType;
	}
	
	private static int getSizeFromString(String type)
	{
		if(type.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
		{
			String size = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"));
			return Integer.parseInt(size);
		}
		return -1;
	}
	
	private void setArgumentsJSonString(String argumentsJSonString){
		try{
			JSONParser parser = new JSONParser();
	        JSONObject arguments = (JSONObject) parser.parse(argumentsJSonString);
	        delimiter = String.valueOf(arguments.get(ADHOC_PARSER_DELIMITER));
	        valueSeparator = String.valueOf(arguments.get(ADHOC_PARSER_VALUESEPARATOR));
	        isFirstRowHeader = Boolean.valueOf(String.valueOf(arguments.get(ADHOC_PARSER_ISFIRSTROWHEADER)));
	        encoding = String.valueOf(arguments.get(ADHOC_PARSER_ENCODING));
	        isSkipAllRecords = Boolean.valueOf(String.valueOf(arguments.get(ADHOC_PARSER_SKIP_ALL_RECORDS)));
	
	        LOG.info("delimiter: " + delimiter);
	        LOG.info("valueSeparator: " + valueSeparator);
	        LOG.info("isFirstRowHeader: " + isFirstRowHeader);
	        LOG.info("encoding: " + encoding);
	        LOG.info("isSkipAllRecords: " + isSkipAllRecords);
	        
		}catch(Exception e){
			LOG.fatal(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void parse(DBListener dbListener, String filePath, InputStream is) throws Exception {
		dbListener.createStatement(columns);
		
		AdHocEntry entry = new AdHocEntry();
		entry.setColumns(columns);
		entry.setColumnTypes(columnTypes);
		
		BufferedReader rd = null;
		String str = null;
		try {
			InputStreamReader in = new InputStreamReader(is, encoding);

			rd = new BufferedReader(in);
			
			if (isFirstRowHeader)
				str = rd.readLine();			// Skip First Line.

			while ((str = rd.readLine()) != null) {
				if(isSkipAllRecords){
					try{
						this.parseLine(dbListener, filePath, str, entry);
					}catch(Exception e){
						throw new Exception(e);
					}
				}
				else{
					try{
						this.parseLine(dbListener, filePath, str, entry);
					}catch(Exception e){
						LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
					}
				}
				entry.clearValues();
			}
		} catch (Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		} 	
	}
	
	private void parseLine(DBListener dbListener, String filePath, String line, AdHocEntry entry) throws IOException, InterruptedException, SQLException{
		boolean valid = false;

		entry.addValue(0, filePath);
		
		int i = 1;
		
		String regex = null;

        if ((valueSeparator != null) && (!valueSeparator.isEmpty()))
        	regex = "\"([^" + valueSeparator + "]*)\"|([^" + delimiter + "]+)";
        else
        	regex = "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";

        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            if (m.group(1) != null) {
            	if (this.columns.containsKey(i))
            		entry.addValue(i, m.group(1));
            	i ++;
            } else {
            	if (this.columns.containsKey(i))
            		entry.addValue(i, m.group(2));
            	i ++;
            }
        }
              
		valid = this.parsedExpression.evaluateEntry(entry);
		if(valid){
			dbListener.insertAdHocEntry(entry);
		}
    }
	
//	private void setColValue(int i, String value, AdHocEntry entry)
//	{
//		String colType = this.columnTypes.get(i);
//		try
//		{
//			if (("INTEGER".equalsIgnoreCase(colType)) || ("SMALLINT".equalsIgnoreCase(colType)))
//				entry.addValue(i, Integer.parseInt(value));
//			else if ("BIGINT".equalsIgnoreCase(colType))
//				entry.addValue(i, Long.parseLong(value));
//			else if ("FLOAT".equalsIgnoreCase(colType))
//				entry.addValue(i, Float.parseFloat(value));
//			else if (("DOUBLE".equalsIgnoreCase(colType)) || ("DECIMAL".equalsIgnoreCase(colType)) || ("NUMERIC".equalsIgnoreCase(colType)))
//				entry.addValue(i, Double.parseDouble(value));
//			else if ("BOOLEAN".equalsIgnoreCase(colType))
//			{
//				if ("true".equalsIgnoreCase(value))
//					entry.addValue(i, true);
//				else if ("false".equalsIgnoreCase(value))
//					entry.addValue(i, false);
//				else
//					entry.addValue(i, value);
//			}
//			else if ("DOUBLE".equalsIgnoreCase(colType))
//				entry.addValue(i, Double.parseDouble(value));
//			else
//				entry.addValue(i, value);
//		}
//		catch (Exception e)
//		{
//			entry.addValue(i, value);
//		}
//	}
}