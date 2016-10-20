package com.queryio.files.tagging.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.queryio.files.tagging.IFileParser;
import com.queryio.plugin.dstruct.IDataDefinition;
import com.queryio.plugin.extended.metadata.ColumnMetadata;

public class JSONFileParser extends IFileParser {
	
	public JSONFileParser(JSONObject tagsJSON) {
		super(tagsJSON);
		JSONArray fieldsJSON = (JSONArray) tagsJSON.get(FIELDS_KEY);
		
		columns = new HashMap<Integer, String>();
		
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsJSON.get(PARSE_DETAILS_KEY);
		
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
	}
	
	private HashMap<String, String> curValueMap = new HashMap<String, String>();
	private Map<Integer, String> columns = new HashMap<Integer, String>();
	private Map<Integer, String> columnTypes = new HashMap<Integer, String>();
	private String encoding;
	private boolean skipAll;	
	
	@SuppressWarnings("unchecked")
	private void parseLine(String line) throws IOException, InterruptedException, SQLException, ParseException
	{
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(line);
		Iterator<String> keys = jsonObject.keySet().iterator();
		
		while (keys.hasNext())
		{
			String key = keys.next();
			if (this.columns.containsValue(key)) {
				if(jsonObject.get(key) != null) {
					curValueMap.put(key, (String) jsonObject.get(key));
				}
			}
		}
		evaluateCurrentEntry(curValueMap, line);
    }

	@Override
	public void parse(InputStream is, Map<String, String> coreTags) throws Exception {
		BufferedReader br = null;
		String str = null;
		try {
			InputStreamReader in = new InputStreamReader(is, encoding);

			br = new BufferedReader(in);
			
			while ((str = br.readLine()) != null) {
				System.out.println("str : " + str);
				try {
					this.parseLine(str);
				}
				catch(Exception e) {
					e.printStackTrace();
					if(skipAll) {
						break;
					}
				}
			}
		} finally {
			if(br != null)
				br.close();
		}
		
	}
}
