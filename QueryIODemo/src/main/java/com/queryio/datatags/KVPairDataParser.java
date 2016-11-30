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
import java.util.TreeMap;

import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class KVPairDataParser extends AbstractDataTagParser {

	private HashMap<String, String> curValueMap = new HashMap<String, String>();
	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private String delimiter;
	private String valueSeparator;

	private String encoding = "UTF-8"; // Default
	private boolean skipAll = false; // Default

	private boolean isGlobalOperator = false; // Default

	public KVPairDataParser(JSONObject tagsJSON, Map<String, String> coreTags) {

		super(tagsJSON, coreTags, true); // for logical

		if (tagsJSON == null)
			return;

		JSONArray fieldsJSON = (JSONArray) tagsJSON.get(FIELDS_KEY);

		columns = new HashMap<Integer, String>();

		if (fieldsJSON == null) {
			isGlobalOperator = true;
			return;
		}

		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))),
					String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}

		JSONObject parsingDetailsJSON = (JSONObject) tagsJSON.get(PARSE_DETAILS_KEY);

		delimiter = String.valueOf(parsingDetailsJSON.get(DELIMITER_KEY));
		valueSeparator = String.valueOf(parsingDetailsJSON.get(VALUE_SEPERATOR_KEY));
		encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
		skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
	}

	private void parseLine(String line, boolean isRecordLevel) throws IOException, InterruptedException, SQLException {
		String[] str = line.split(delimiter);
		String[] pairs = null;

		if (coreTags != null) {
			Iterator<String> it = coreTags.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				curValueMap.put(key, coreTags.get(key));
				// curValueMap.put(this.columns.get(0),
				// coreTags.get("FILEPATH")); // For FILEPATH
			}
		}

		for (int i = 0; i < str.length; i++) {
			pairs = str[i].split(valueSeparator);
			if ((pairs != null) && (pairs.length == 2)) {
				String key = pairs[0];
				String value = pairs[1];
				if (key == null)
					continue;

				key = key.trim();
				if ((this.columns.containsValue(key)) && !key.startsWith("#")) {
					if (value != null)
						value = value.trim();
					curValueMap.put(key, value);
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
				} catch (Exception e) {
					e.printStackTrace();
					if (skipAll) {
						break;
					}
				}

			}

		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return super.getCustomTagList();
	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return new TableMetadata("TXT", new ArrayList<ColumnMetadata>());
	}

	@Override
	public boolean updateDbSchema() {
		return false;
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception {

	}

	@Override
	public boolean parseMapRecord(String value, long offset) throws Exception {
		if (isGlobalOperator)
			evaluateRecordEntry(curValueMap, value);
		else
			this.parseLine(value, true);
		return true;
	}
}