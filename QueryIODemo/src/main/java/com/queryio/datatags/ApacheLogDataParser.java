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

import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class ApacheLogDataParser extends AbstractDataTagParser {

	// private static final Log LOG =
	// LogFactory.getLog(ApacheLogDataParser.class);

	private static String pattern = "";

	// "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\""

	private static final String IP = "h";
	private static final String USERID = "u";
	private static final String BLANK = "l";
	private static final String TIMEDATE = "t";
	private static final String REQUESTED_LINE = "r\"";
	private static final String STATUS_CODE = ">s";
	private static final String OBJ_SIZE = "b";
	private static final String REFERRER = "{Referer}i";
	private static final String AGENT = "{User-agent}i";

	private String encoding = "UTF-8"; // Default
	private boolean skipAll = false; // Default

	private boolean isGlobalOperator = false; // Default

	HashMap<String, String> curValueMap = new HashMap<String, String>();

	String tokens[];
	Map<Integer, String> columns;
	JSONObject iIndices = new JSONObject();
	JSONObject tagsIInfo;
	int indices[];

	@SuppressWarnings("unchecked")
	public ApacheLogDataParser(JSONObject tagsIInfo, Map<String, String> coreTags) {

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

		pattern = String.valueOf(parsingDetailsJSON.get(LOG_PATTERN_KEY));

		pattern = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i";

		encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
		skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));

		tokens = pattern.split("%");

		indices = new int[tokens.length];
		for (int j = 0; j < tokens.length; j++) {
			if (tokens[j].trim().indexOf(IP) != -1) {
				iIndices.put(IP, j);
			} else if (tokens[j].trim().indexOf(BLANK) != -1) {
				iIndices.put(BLANK, j);
			} else if (tokens[j].trim().indexOf(USERID) != -1) {
				iIndices.put(USERID, j);
			} else if (tokens[j].trim().startsWith(TIMEDATE)) {
				iIndices.put(TIMEDATE, j);

			} else if ((tokens[j].trim().indexOf(REQUESTED_LINE) != -1)) {
				iIndices.put(REQUESTED_LINE, j);

			} else if (tokens[j].trim().indexOf(STATUS_CODE) != -1) {
				iIndices.put(STATUS_CODE, j);

			} else if (tokens[j].trim().indexOf(OBJ_SIZE) != -1) {
				iIndices.put(OBJ_SIZE, j);

			} else if (tokens[j].trim().indexOf(REFERRER) != -1) {
				iIndices.put(REFERRER, j);

			} else if (tokens[j].trim().indexOf(AGENT) != -1) {
				iIndices.put(AGENT, j);
			}
		}
	}

	private String getColumnName(int colIndex) {
		String colName = "";
		colName = columns.get(colIndex);
		return colName;
	}

	public static ArrayList<String> parseByLine(String str) {
		boolean flag = false;
		String temp = "";
		ArrayList<String> arr = new ArrayList<String>();
		for (int i = 0; i < str.length(); i++) {
			if ((str.charAt(i) == '"' && !flag) || str.charAt(i) == '[') {
				flag = true;
				temp += str.charAt(i);
			}

			else if ((str.charAt(i) == '"' && flag) || str.charAt(i) == ']') {
				flag = false;
				temp += str.charAt(i);
			} else if (str.charAt(i) == ' ' && !flag) {
				arr.add(temp);
				temp = "";
			} else {
				temp += str.charAt(i);
			}
		}
		arr.add(temp);
		return arr;
	}

	public void parseLine(String str, boolean isRecordLevel) throws Exception {

		ArrayList<String> recordsPerLine = parseByLine(str);

		if (coreTags != null) {
			Iterator<String> it = coreTags.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				curValueMap.put(key, coreTags.get(key));
				// curValueMap.put(this.columns.get(0),
				// coreTags.get("FILEPATH")); // For FILEPATH
			}
		}

		for (int i = 0; i < recordsPerLine.size(); i++) {
			// System.out.println("ColumnName : "+getColumnName(i+1)+" value :
			// "+recordsPerLine.get(i));
			curValueMap.put(getColumnName(i + 1), recordsPerLine.get(i));
		}

		if (isRecordLevel)
			evaluateRecordEntry(curValueMap, str);
		else
			evaluateCurrentEntry(curValueMap, str);

	}

	@Override
	public void parseStream(InputStream is, String fileExtension) throws Exception {

		if (tagsJSON == null)
			return;

		BufferedReader br = null;
		String str;
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
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return super.getCustomTagList();
	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return new TableMetadata("ACCESSLOG", new ArrayList<ColumnMetadata>());
	}

	@Override
	public boolean updateDbSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception {
		// TODO Auto-generated method stub

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