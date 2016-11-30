package com.queryio.files.tagging.keyvaluepair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.IFileParser;

public class KVPairFileParser extends IFileParser {

	public KVPairFileParser(JSONObject tagsJSON) {
		super(tagsJSON);

		JSONArray fieldsJSON = (JSONArray) tagsJSON.get(FIELDS_KEY);

		columns = new HashMap<Integer, String>();

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

	private HashMap<String, String> curValueMap = new HashMap<String, String>();
	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private String delimiter;
	private String valueSeparator;
	private String encoding;
	private boolean skipAll;

	private void parseLine(String line) throws IOException, InterruptedException, SQLException {
		String[] str = line.split(delimiter);
		String[] pairs = null;
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
	}

	@Override
	public void parse(InputStream is, Map<String, String> coreTags) throws Exception {

		BufferedReader br = null;
		String str = null;
		try {
			InputStreamReader in = new InputStreamReader(is, encoding);

			br = new BufferedReader(in);

			while ((str = br.readLine()) != null) {
				try {
					this.parseLine(str);
				} catch (Exception e) {
					e.printStackTrace();
					if (skipAll) {
						break;
					}
				}

			}

			evaluateCurrentEntry(curValueMap, str);

		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}