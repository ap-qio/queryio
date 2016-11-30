package com.queryio.demo.adhoc.mbox;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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

public class MBoxParser implements IAdHocParser {

	private static final Log LOG = LogFactory.getLog(MBoxParser.class);
	final String ADHOC_PARSER_ENCODING = "encoding";

	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
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
		IDataDefinition dataDefinition = new MBoxDataDefinitionImpl();
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

		boolean valid = false;

		entry.addValue(0, filePath);

		try {
			Session session = Session.getDefaultInstance(new Properties());
			MimeMessage message = new MimeMessage(session, is);

			entry.addValue(1, message.getContentID());
			StringBuilder sb = new StringBuilder();
			sb.append("");
			String temp[] = message.getContentLanguage();
			if (temp != null) {
				for (int i = 0; i < temp.length; i++) {
					sb.append(temp[i]);
					sb.append(" ");
				}
			}
			entry.addValue(2, sb.toString().trim());
			entry.addValue(3, message.getContentMD5());
			entry.addValue(4, message.getContentType());
			entry.addValue(5, message.getDescription());
			entry.addValue(6, message.getDisposition());
			entry.addValue(7, message.getEncoding());
			entry.addValue(8, message.getFileName());
			Address temp1[] = message.getFrom();
			sb.setLength(0);
			if (temp1 != null) {
				for (int i = 0; i < temp1.length; i++) {
					sb.append(temp1[i]);
					sb.append(" ");
				}
			}
			entry.addValue(9, sb.toString().trim());
			entry.addValue(10, String.valueOf(message.getLineCount()));
			entry.addValue(11, message.getMessageID());
			entry.addValue(12, String.valueOf(message.getMessageNumber()));
			Date date = message.getReceivedDate();
			if (date != null)
				entry.addValue(13, String.valueOf(new Timestamp(date.getTime())));
			else
				entry.addValue(13, null);
			temp1 = message.getReplyTo();
			sb.setLength(0);
			if (temp1 != null) {
				for (int i = 0; i < temp1.length; i++) {
					sb.append(temp1[i]);
					sb.append(" ");
				}
			}
			entry.addValue(14, sb.toString().trim());
			entry.addValue(15, String.valueOf(message.getSender()));
			date = message.getSentDate();
			if (date != null)
				entry.addValue(16, String.valueOf(new Timestamp(date.getTime())));
			else
				entry.addValue(16, null);
			entry.addValue(17, message.getSubject());
			entry.addValue(18, String.valueOf(message.getSize()));

			valid = this.parsedExpression.evaluateEntry(entry);
			if (valid) {
				dbListener.insertAdHocEntry(entry);
			}

			entry.clearValues();
		} catch (Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		}
	}
}