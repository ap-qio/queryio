package com.queryio.demo.adhoc.ncdcgsod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

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

public class NCDCParser implements IAdHocParser{
	private static final Log LOG = LogFactory.getLog(NCDCParser.class);
	private ParsedExpression parsedExpression = null;
	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
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
		IDataDefinition dataDefinition = new NCDCDataDefinitionImpl();
		int i = 0;
		for(ColumnMetadata metadata : dataDefinition.getColumnMetadata()){
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			i++;
		}
	}

	private void setFieldsJSonString(String fieldsJSonString){
		try{
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
		}catch(Exception e){
			LOG.fatal(e.getLocalizedMessage(), e);
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
	
	private void setArgumentsJSonString(String argumentsJSonString){
		
	}
	@Override
	public void parse(DBListener dbListener, String filePath, InputStream is)
			throws Exception {
		AdHocEntry entry = new AdHocEntry();
		entry.setColumns(columns);
		entry.setColumnTypes(columnTypes);
		
		new Parser(dbListener, filePath, entry).parse(is);
	}
	class Parser{
		AdHocEntry entry;
		StringTokenizer token; 
		DBListener dbListener;
		String filePath;
		Parser(DBListener dbListener, String filePath, AdHocEntry entry){
			this.dbListener = dbListener;
			this.filePath = filePath;
			this.entry = entry;
		}
		public void parse(InputStream is) throws IOException, InterruptedException, SQLException {
			
			is = new GZIPInputStream(is);
			BufferedReader rd = null;
			String str;
			try {
				InputStreamReader in = new InputStreamReader(is);

				rd = new BufferedReader(in);

				while ((str = rd.readLine()) != null) {
					this.parseLine(str);
					entry.clearValues();
				}
				
			} catch (Exception e) {
				LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
				throw new IOException(e);
			} finally {
				try {
					if (rd != null)
						rd.close();
				} catch (Exception e) {
					LOG.fatal(e.getMessage(), e);
				}
				
			}
		}
		public void parseLine(String line) throws IOException, InterruptedException, SQLException{
			line = line.replace("  ", " ");
			line = line.replace("\t", " ");
			line = line.replace(" ", ",");
			line = line.replaceAll("I","");
			
			LOG.info("LINE: " + line);
			
			token = new StringTokenizer(line, ",");
			
			boolean valid = true;
			this.entry.clearValues();
			String value = null;
			int index = 0;
			int i = 0;
			this.entry.addValue(i++, filePath);
			while(token.hasMoreTokens()) { 
				value = token.nextToken();	
				
				if(index==2){
					LOG.info("DATE: " + value);
					try{ this.entry.addValue(i++, value.substring(0, 4)); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Year
					try{ this.entry.addValue(i++, value.substring(4, 6)); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Month
					try{ this.entry.addValue(i++, value.substring(6, value.length())); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Day
				} else if(index==17){
					this.entry.addValue(i++, value.replace("*", ""));
					this.entry.addValue(i++, value.contains("*") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE));
				} else if(index==18){
					this.entry.addValue(i++, value.replace("*", ""));
					this.entry.addValue(i++, value.contains("*") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE));
				} else if(index==21){
					this.entry.addValue(i++, value.substring(0, 1).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // fog
					this.entry.addValue(i++, value.substring(1, 2).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // rain
					this.entry.addValue(i++, value.substring(2, 3).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // snow
					this.entry.addValue(i++, value.substring(3, 4).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // hail
					this.entry.addValue(i++, value.substring(4, 5).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // thunder
					this.entry.addValue(i++, value.substring(5, 6).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // tornado
				} else {
					if(value.contains(".")){
						if(value.endsWith("E"))	value = value + "+0";
						value = String.valueOf(Double.valueOf(value).doubleValue());
					}
					
					this.entry.addValue(i++, value);
				}
				
				index++;
	        }
			
			valid = parsedExpression.evaluateEntry(entry);
			
			
			if(valid){
				dbListener.insertAdHocEntry(entry);
			}
	    }
	}
}
