package com.queryio.datatags;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class IISLogDataParser extends AbstractDataTagParser {

	HashMap<String, String> curValueMap = new HashMap<String, String>();
	private String delimiter;
	private String valueSeparator;

	private boolean hasHeader = false; // Default
	private String encoding = "UTF-8"; // Default
	private boolean skipAll = false; // Default
	private String pattern;

	private boolean isGlobalOperator = false; // Default

	Map<Integer, String> columns;
	JSONObject iIndices = new JSONObject();
	JSONObject tagsIInfo;
	int indices[];

	public IISLogDataParser(JSONObject tagsIInfo, Map<String, String> coreTags) {

		super(tagsIInfo, coreTags, true); // for logical

		if (tagsIInfo == null)
			return;

		JSONArray fieldsJSON = (JSONArray) tagsIInfo.get(FIELDS_KEY);

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

		JSONObject parsingDetailsJSON = (JSONObject) tagsIInfo.get(PARSE_DETAILS_KEY);

		delimiter = String.valueOf(parsingDetailsJSON.get(DELIMITER_KEY));

		valueSeparator = String.valueOf(parsingDetailsJSON.get(VALUE_SEPERATOR_KEY));
		if (valueSeparator == null)
			valueSeparator = "\n";
		hasHeader = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(HAS_HEADER_KEY)));
		encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
		skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
		pattern = getPattern(delimiter, valueSeparator);
	}

	public static String getPattern(String delimiter, String valueSeparator) {
		if ((valueSeparator != null) && (!valueSeparator.isEmpty()))
			return "\"([^" + valueSeparator + "]*)\"|([^" + delimiter + "]+)";
		else
			return "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";
	}

	public void parseLine(String line, String pattern, boolean isRecordLevel) throws Exception {

		if (columns != null) {
			if (coreTags != null) {
				Iterator<String> it = coreTags.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					curValueMap.put(key, coreTags.get(key));
					// curValueMap.put(this.columns.get(0),
					// coreTags.get("FILEPATH")); // For FILEPATH
				}
			}

			Matcher m = Pattern.compile(pattern).matcher(line);

			int index = 1; // 0 = FILEPATH
			while (m.find()) {
				if (this.columns.get(index) != null) {
					curValueMap.put(this.columns.get(index - 1), m.group(1) == null ? m.group(2) : m.group(1));
				}
				index++;
			}
			if (isRecordLevel)
				evaluateRecordEntry(curValueMap, line);
			else
				evaluateCurrentEntry(curValueMap, line);
		}
	}

	@Override
	public void parseStream(InputStream is, String fileExtension) throws Exception {

		if (tagsJSON == null)
			return;

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
		String str = null;

		if (hasHeader) {
			reader.readLine();
		}

		while ((str = reader.readLine()) != null) {
			try {
				if (isGlobalOperator)
					evaluateCurrentEntry(curValueMap, str);
				else
					this.parseLine(str, pattern, false);
			} catch (Exception e) {
				e.printStackTrace();
				if (skipAll) {
					break;
				}
			}
		}
	}

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return super.getCustomTagList();
	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return new TableMetadata("IISLOG", new ArrayList<ColumnMetadata>());
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
		if (hasHeader && offset == 0) {
			return false;
		}
		if (isGlobalOperator)
			evaluateRecordEntry(curValueMap, value);
		else
			this.parseLine(value, pattern, true);
		return true;

	}
}
