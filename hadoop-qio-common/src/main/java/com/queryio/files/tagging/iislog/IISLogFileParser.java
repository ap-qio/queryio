package com.queryio.files.tagging.iislog;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import com.queryio.files.tagging.IFileParser;

public class IISLogFileParser extends IFileParser {
	
	private int index;
	private char start;
	
	HashMap<String, String> curValueMap = new HashMap<String, String>();
	private String delimiter;
	private String valueSeparator;
	private boolean hasHeader;
	private String encoding;
	private boolean skipAll;
	
	Map<Integer, String> columns;
	JSONObject iIndices = new JSONObject();
	JSONObject tagsIInfo;
	int indices[] ;
	public IISLogFileParser(JSONObject tagsIInfo) {
		
		super(tagsIInfo);
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsIInfo.get(PARSE_DETAILS_KEY);
		
		delimiter = String.valueOf(parsingDetailsJSON.get(DELIMITER_KEY));
		
		valueSeparator = String.valueOf(parsingDetailsJSON.get(VALUE_SEPERATOR_KEY));
		if(valueSeparator == null)
			valueSeparator = "\n";
		hasHeader = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(HAS_HEADER_KEY)));
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
        	
		JSONArray fieldsJSON = (JSONArray) tagsIInfo.get(FIELDS_KEY);
		columns = new HashMap<Integer, String>();
			
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		
		
	}
	
	private String getColumnName(int colIndex){
		String colName = "";
		colName = columns.get(colIndex);
		return colName;
	}
		
	
	
	public static String getPattern(String delimiter, String valueSeparator) {
		if ((valueSeparator != null) && (!valueSeparator.isEmpty()))
			return "\"([^" + valueSeparator + "]*)\"|([^" + delimiter + "]+)";
		else
			return "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";
	}
	
	public void parseLine(String str) throws Exception {
		
		String pattern =  getPattern(delimiter, valueSeparator);
		List<String> recordsPerLine = Arrays.asList(str.split(delimiter));
		for(int i = 0; i< recordsPerLine.size(); i++){
//			System.out.println("ColumnName : "+getColumnName(i+1)+" value : "+recordsPerLine.get(i));
			curValueMap.put(getColumnName(i+1),recordsPerLine.get(i));
		}
		evaluateCurrentEntry(curValueMap, str);
	}
	
	public void parse(InputStream is, Map<String, String> coreTags) throws IOException, InterruptedException, SQLException {
		BufferedReader br = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is , encoding);

			br = new BufferedReader(in);
			
			while((str = br.readLine())!=null){
				
				this.parseLine(str);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(br!=null)
					br.close();
				
			}catch(Exception e){
				
			}
			
		}
		
	}

}
