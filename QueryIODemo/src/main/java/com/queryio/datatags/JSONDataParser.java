package com.queryio.datatags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class JSONDataParser extends AbstractDataTagParser {
	
	private HashMap<String, String> curValueMap = new HashMap<String, String>();
	private Map<Integer, String> columns = new HashMap<Integer, String>();
	
	private String encoding = "UTF-8";			// Default
	private boolean skipAll = false;			// Default
	
	private boolean isGlobalOperator = false;			// Default
	
	public JSONDataParser(JSONObject tagsJSON, Map<String, String> coreTags) {
		
		super(tagsJSON, coreTags, true);			// for logical
		
		if (tagsJSON == null)
			return;
		
		JSONArray fieldsJSON = (JSONArray) tagsJSON.get(FIELDS_KEY);
		
		columns = new HashMap<Integer, String>();
		
		if (fieldsJSON == null)
		{
			isGlobalOperator = true;
			return;
		}
		
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsJSON.get(PARSE_DETAILS_KEY);
		
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
	}
		
	@SuppressWarnings("unchecked")
	private void parseLine(String line, boolean isRecordLevel) throws IOException, InterruptedException, SQLException, ParseException
	{
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(line);
		Iterator<String> keys = jsonObject.keySet().iterator();
		
		if (coreTags != null) {
			Iterator<String> it = coreTags.keySet().iterator();
			while (it.hasNext())
			{
				String key = it.next();
				curValueMap.put(key, coreTags.get(key));
//				curValueMap.put(this.columns.get(0), coreTags.get("FILEPATH"));			// For FILEPATH
			}
		}
		
		while (keys.hasNext())
		{
			String key = keys.next();
			if (this.columns.containsValue(key)) {
				if(jsonObject.get(key) != null) {
					curValueMap.put(key, (String) jsonObject.get(key));
				}
			}
		}

		if (isRecordLevel)
			evaluateRecordEntry(curValueMap, line);
		else
			evaluateCurrentEntry(curValueMap, line);
    }

	@Override
	public void parseStream(InputStream is, String fileExtension) throws Exception {
		
		if (tagsJSON == null)
			return;
		
		BufferedReader br = null;
		String str = null;
		try {
			InputStreamReader in = new InputStreamReader(is, encoding);

			br = new BufferedReader(in);
			
			while ((str = br.readLine()) != null) {
				try {
					if (isGlobalOperator)
						evaluateCurrentEntry(curValueMap, str);
					else
						this.parseLine(str, false);
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

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return super.getCustomTagList();
	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return new TableMetadata("JSON", new ArrayList<ColumnMetadata>());
	}

	@Override
	public boolean updateDbSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata,
			Metadata metadata) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean parseMapRecord(String value, long offset)
			throws Exception {
		if (tagsJSON == null)
			return false;
		if (isGlobalOperator)
			evaluateRecordEntry(curValueMap, value);
		else
			this.parseLine(value, true);
		return true;
		
	}
}