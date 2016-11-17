package com.queryio.files.tagging.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.IFileParser;

public class CSVFileParser extends IFileParser {
	HashMap<String, String> curValueMap = new HashMap<String, String>();
	Map<String, String> coreTags;
	
	Map<Integer, String> columns;
	
	JSONObject parsingDetailsJSON;
	
	private String delimiter;
	private String valueSeparator;
	private boolean hasHeader;
	private String encoding;
	private boolean skipAll;
	
	public CSVFileParser(JSONObject tagsIInfo) {
		super(tagsIInfo);
		
		JSONArray fieldsJSON = (JSONArray) tagsIInfo.get(FIELDS_KEY);
		
		columns = new HashMap<Integer, String>();
		
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsIInfo.get(PARSE_DETAILS_KEY);
		
		delimiter = String.valueOf(parsingDetailsJSON.get(DELIMITER_KEY));
        valueSeparator = String.valueOf(parsingDetailsJSON.get(VALUE_SEPERATOR_KEY));
        hasHeader = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(HAS_HEADER_KEY)));
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
	}

	@Override
	public void parse(InputStream is, Map<String, String> coreTags) throws Exception {
		String pattern = getPattern(delimiter, valueSeparator);
		this.coreTags = coreTags;
		if ((valueSeparator != null) && (!valueSeparator.isEmpty()))
			pattern = "\"([^" + valueSeparator + "]*)\"|([^" + delimiter + "]+)";
		else
			pattern = "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				encoding));
		String str = null;

		if (hasHeader) {
			reader.readLine();			
		}

		while ((str = reader.readLine()) != null) {
			try {
				this.parseLine(str, pattern);
			} catch (Exception e) {
				e.printStackTrace();
				if (skipAll) {
					break;
				}
			}
		}
	}

	public static String getPattern(String delimiter, String valueSeparator) {
		if ((valueSeparator != null) && (!valueSeparator.isEmpty()))
			return "\"([^" + valueSeparator + "]*)\"|([^" + delimiter + "]+)";
		else
			return "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";
	}
	
	public void parseLine(String line, String pattern) throws IOException, InterruptedException, SQLException{
		if(columns!=null){
			if (coreTags != null) {
				Iterator<String> it = coreTags.keySet().iterator();
				while (it.hasNext())
				{
					String key = it.next();
					curValueMap.put(key, coreTags.get(key));
//					curValueMap.put(this.columns.get(0), coreTags.get("FILEPATH"));			// For FILEPATH
				}
			}
			
			Matcher m = Pattern.compile(pattern).matcher(line);
	        int index=1;			// 0 = FILEPATH
			while (m.find()) {
	            if (m.group(1) != null) {
	            	if(this.columns.get(index)!=null) {
	            		curValueMap.put(this.columns.get(index), m.group(1));
	            	}
	            } else {
	            	if(this.columns.get(index)!=null) {
	            		curValueMap.put(this.columns.get(index), m.group(2));
	            	}
	            }
	            index++;
	        }
			
			evaluateCurrentEntry(curValueMap, line);
		} 
	}
}
