package com.queryio.demo.adhoc.iislog;

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
import com.queryio.demo.adhoc.AdHocEntry;
import com.queryio.demo.adhoc.DBListener;
import com.queryio.demo.adhoc.IAdHocParser;
import com.queryio.demo.adhoc.ParsedExpression;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class IISLogParser implements IAdHocParser{
	
	private static final Log LOG = LogFactory.getLog(IISLogParser.class);
	String ADHOC_PARSER_ENCODING = "encoding";
	String ADHOC_PARSER_DELIMITER = "delimiter";
	String ADHOC_PARSER_ISFIRSTROWHEADER = "isFirstRowHeader";

	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
	private String delimiter = ",";					// Comma (,) default for IIS LOG (6.0)
	private boolean isFirstRowHeader = false;
	private String encoding = "UTF-8";
	ParsedExpression parsedExpression;
	
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
		IDataDefinition dataDefinition = new IISLogDataDefinitionImpl();
		int i = 0;
		for(ColumnMetadata metadata : dataDefinition.getColumnMetadata()){
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			i++;
		}
	}
	
	public void setExpressions(ParsedExpression parsedExpression) {
		this.parsedExpression = parsedExpression;
	}
	
	private void setFieldsJSonString(String fieldsJSonString) throws Exception{
		JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(fieldsJSonString);
        JSONObject obj = null;
        for (int i=0; i<array.size(); i++)
        {
            obj = (JSONObject) array.get(i);
            String column = String.valueOf(obj.get("colName"));
            Class columnType = getDataType(obj.get("colType").toString());
            int index = Integer.parseInt(String.valueOf(obj.get("colIndex")));          
            columns.put(index, column);
            columnTypes.put(index, columnType);
            LOG.info("colName: " + column);
            LOG.info("colType: " + columnType);
            LOG.info("colIndex: " + index);
        }
	}
	
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
	
	private void setArgumentsJSonString(String argumentsJSonString) throws Exception{
		JSONParser parser = new JSONParser();
        JSONObject arguments = (JSONObject) parser.parse(argumentsJSonString);
        delimiter = String.valueOf(arguments.get(ADHOC_PARSER_DELIMITER));
        isFirstRowHeader = Boolean.valueOf(String.valueOf(arguments.get(ADHOC_PARSER_ISFIRSTROWHEADER)));
        encoding = String.valueOf(arguments.get(ADHOC_PARSER_ENCODING));
        
        LOG.info("delimiter: " + delimiter);
        LOG.info("isFirstRowHeader: " + isFirstRowHeader);
        LOG.info("encoding: " + encoding);
	}
	
	@Override
	public void parse(DBListener dbListener, String filePath, InputStream is) throws Exception {
		dbListener.createStatement(this.columns);
		
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
			
			LOG.info("isFirstRowHeader:" + isFirstRowHeader + " str: " + str);

			while ((str = rd.readLine()) != null) {
				LOG.info("str: " + str);
				this.parseLine(dbListener, filePath, str, entry);
				entry.clearValues();
			}
		} catch (Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		} 	
	}
		
	
	private void parseLine(DBListener dbListener, String filePath, String line, AdHocEntry entry) throws IOException, InterruptedException, SQLException{
//		StringTokenizer token = new StringTokenizer(line, pattern); 
		
		
		boolean valid = false;
//		String value = null;
		 
		entry.addValue(0, filePath);
		int i = 1;
		
		String regex = null;

       	regex = "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";

        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            if (m.group(1) != null) {
            	if (this.columns.containsKey(i))
            		entry.addValue(i, m.group(1).trim());
            	i ++;
            } else {
            	if (this.columns.containsKey(i))
            		entry.addValue(i, m.group(2).trim());
            	i ++;
            }
        }
        
//		while(token.hasMoreTokens()) { 
//			value = token.nextToken();
//			if(this.columns.containsKey(i))
//				entry.addValue(i, value);
//			i ++;
//        }
        
		valid = this.parsedExpression.evaluateEntry(entry);
		if(valid){
			dbListener.insertAdHocEntry(entry);
		}
    }
}
