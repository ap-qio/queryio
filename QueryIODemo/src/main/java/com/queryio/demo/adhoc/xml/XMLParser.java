package com.queryio.demo.adhoc.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.queryio.common.MetadataConstants;
import com.queryio.demo.adhoc.AdHocEntry;
import com.queryio.demo.adhoc.DBListener;
import com.queryio.demo.adhoc.IAdHocParser;
import com.queryio.demo.adhoc.ParsedExpression;
import com.queryio.demo.adhoc.csv.CSVDataDefinitionImpl;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class XMLParser implements IAdHocParser {

	private static final Log LOG = LogFactory.getLog(XMLParser.class);
	final String ADHOC_PARSER_XML_NODE_NAME = "nodeName";
	final String ADHOC_PARSER_XML_NEW_LINE = "\n";

	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
	private String delimiter = "";

	ParsedExpression parsedExpression;

	@Override
	public void setExpressions(ParsedExpression parsedExpression) {
		this.parsedExpression = parsedExpression;
	}

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
		IDataDefinition dataDefinition = new CSVDataDefinitionImpl();
		int i = 0;
		for (ColumnMetadata metadata : dataDefinition.getColumnMetadata()) {
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			i++;
		}
	}

	private void setFieldsJSonString(String fieldsJSonString) {
		try {
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
		} catch (Exception e) {
			LOG.fatal(e.getLocalizedMessage(), e);
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

	private void setArgumentsJSonString(String argumentsJSonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject arguments = (JSONObject) parser.parse(argumentsJSonString);
			delimiter = String.valueOf(arguments.get(ADHOC_PARSER_XML_NODE_NAME));

			LOG.info("delimiter: " + delimiter);
		} catch (Exception e) {
			LOG.fatal(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void parse(DBListener dbListener, String filePath, InputStream is) throws Exception {

		dbListener.createStatement(this.columns);
		AdHocEntry entry = new AdHocEntry();
		boolean valid = false;
		DocumentBuilder dBuilder = null;
		Document doc = null;
		entry.setColumns(columns);
		entry.setColumnTypes(columnTypes);
		try {
			dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(is);
			if (doc.getDocumentElement() != null)
				doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName(delimiter);
			for (int count = 0; count < nodeList.getLength(); count++) {
				Node tempNode = nodeList.item(count);
				String data[] = tempNode.getTextContent().trim().split(ADHOC_PARSER_XML_NEW_LINE);

				valid = false;
				entry.clearValues();
				entry.addValue(0, filePath);

				for (int i = 0; i < data.length; i++) {
					data[i] = data[i].trim();
					entry.addValue(i + 1, data[i]);
				}
				valid = this.parsedExpression.evaluateEntry(entry);
				if (valid) {
					dbListener.insertAdHocEntry(entry);
				}
			}
		} catch (Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		} finally {
			if (dBuilder != null) {
				try {
					dBuilder = null;
				} catch (Exception e) {
					LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
				} finally {
					if (doc != null) {
						try {
							doc = null;
						} catch (Exception e) {
							LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream is = new FileInputStream(
					new File("/AppPerfect/QueryIO/demo/Data/xml/XMLMachineLogs_1364898980201.xml"));
			Document doc = dBuilder.parse(is);
			if (doc.getDocumentElement() != null)
				doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("LogDetails");
			System.out.println("nodeList.getLength() : " + nodeList.getLength());
			AdHocEntry entry = new AdHocEntry();
			for (int count = 0; count < nodeList.getLength(); count++) {
				Node tempNode = nodeList.item(count);
				System.out.println("text : " + tempNode.getTextContent());
				System.out.println("length : " + tempNode.getTextContent().trim().split("\n").length);
				String data[] = tempNode.getTextContent().trim().split("\n");

				entry.addValue(0, "filePath");

				for (int i = 0; i < data.length; i++) {
					data[i] = data[i].trim();
					entry.addValue(i + 1, data[i]);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
