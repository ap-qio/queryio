package com.queryio.demo.adhoc.json;

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

import com.queryio.common.MetadataConstants;
import com.queryio.demo.adhoc.AdHocEntry;
import com.queryio.demo.adhoc.DBListener;
import com.queryio.demo.adhoc.IAdHocParser;
import com.queryio.demo.adhoc.ParsedExpression;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class JSONAdHocParser implements IAdHocParser {

	private static final Log LOG = LogFactory.getLog(JSONAdHocParser.class);
	final String ADHOC_PARSER_ENCODING = "encoding";

	private Map<Integer, String> columns = new HashMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new HashMap<Integer, Class>();
	private String encoding = "UTF-8";
	ParsedExpression parsedExpression;

	@Override
	public void setArguments(String arguments) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(arguments);
		String argString = (String) obj.get("arguments");
		String fieldsString = (String) obj.get("fields");

		if (argString == null) {
			throw new Exception("Invalid Arguments. Expected: <Arguments-JSonString> [<Fields-JSonString>]");
		}
		setArgumentsJSonString(argString);
		if (fieldsString != null) {
			setFieldsJSonString(fieldsString);
		} else {
			initFieldsFromDataDefinition();
		}
	}

	private void initFieldsFromDataDefinition() {
		IDataDefinition dataDefinition = new JSONDataDefinitionImpl();
		int i = 0;
		for (ColumnMetadata metadata : dataDefinition.getColumnMetadata()) {
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			i++;
		}
	}

	public void setExpressions(ParsedExpression parsedExpression) {
		this.parsedExpression = parsedExpression;
	}

	private void setFieldsJSonString(String fieldsJSonString) throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray array = (JSONArray) parser.parse(fieldsJSonString);
		JSONObject obj = null;
		for (int i = 0; i < array.size(); i++) {
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

	private Class getDataType(String type) {
		Class dataType;

		if (type.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP
					.get(MetadataConstants.GENERIC_DATA_TYPE_STRING);
		else
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(type);
		return dataType;
	}

	private void setArgumentsJSonString(String argumentsJSonString) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject arguments = (JSONObject) parser.parse(argumentsJSonString);
		encoding = String.valueOf(arguments.get(ADHOC_PARSER_ENCODING));

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

			LOG.info("str: " + str);

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

	@SuppressWarnings("unchecked")
	private void parseLine(DBListener dbListener, String filePath, String line, AdHocEntry entry)
			throws IOException, InterruptedException, SQLException, ParseException {
		boolean valid = false;

		entry.addValue(0, filePath);
		int i = 1;

		JSONObject jsonObject = (JSONObject) new JSONParser().parse(line);
		Iterator<String> keys = jsonObject.keySet().iterator();
		String key = null;

		while (keys.hasNext()) {
			key = keys.next();
			if (this.columns.containsKey(i))
				entry.addValue(i, (jsonObject.get(key).toString()));
			i++;
		}

		valid = this.parsedExpression.evaluateEntry(entry);
		if (valid) {
			dbListener.insertAdHocEntry(entry);
		}
	}
}