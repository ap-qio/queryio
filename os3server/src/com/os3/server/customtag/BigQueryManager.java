package com.os3.server.customtag;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.HadoopConstants;
import com.queryio.common.database.CoreDBManager;

public class BigQueryManager {
	protected static final Logger LOGGER = Logger.getLogger(BigQueryManager.class);

	public static void saveBigQuery(String jsonProperties, String user) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			JSONParser parser = new JSONParser();
			JSONObject properties = (JSONObject) parser.parse(jsonProperties);

			properties.put("namenode", HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			properties.put("username", user);
			String dbName = (String) properties.get(BigQueryIdentifiers.DBNAME);

			BigQueryDAO.deleteBigQuery(connection, (String) properties.get(BigQueryIdentifiers.QUERYID), user);
			BigQueryDAO.saveBigQuery(connection, (String) properties.get(BigQueryIdentifiers.QUERYID),
					(String) properties.get(BigQueryIdentifiers.QUERYDESC), properties, dbName, user);

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				LOGGER.fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteBigQuery(String queryId, String user) throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			BigQueryDAO.deleteBigQuery(connection, queryId, user);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				LOGGER.fatal(e.getMessage(), e);
			}
		}
	}

	public static JSONObject getAllBigQueries() throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return BigQueryDAO.getAllBigQueries(connection);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				LOGGER.fatal(e.getMessage(), e);
			}
		}
	}

	public static JSONObject getBigQuery(String id, String user) throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return BigQueryDAO.getBigQuery(connection, id, user);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				LOGGER.fatal(e.getMessage(), e);
			}
		}
	}

	public static int generateBigQueryReport(String queryId, String format, HttpServletResponse response, String user)
			throws Exception {
		URL url = null;
		HttpURLConnection httpCon = null;
		int responseCode = 500;
		try {
			String urlPrefix = HadoopConstants.getHadoopConf().get("queryio.server.url");
			if (urlPrefix == null) {
				urlPrefix = "http://localhost:5678/queryio/";
			}
			url = new URL(urlPrefix + "GenerateReport");

			httpCon = (HttpURLConnection) url.openConnection();

			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("GET");

			httpCon.addRequestProperty("queryId", queryId);
			httpCon.addRequestProperty("format", format);
			httpCon.addRequestProperty("namenode",
					HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_NAMESERVICE_ID));
			httpCon.addRequestProperty("username", user);

			httpCon.connect();

			responseCode = httpCon.getResponseCode();
			LOGGER.fatal("Resp. Code: " + responseCode);
			if (httpCon.getResponseCode() == HttpStatus.SC_OK) {
				InputStream is = null;
				OutputStream os = null;
				try {
					is = httpCon.getInputStream();
					os = response.getOutputStream();

					int bufferSize = EnvironmentalConstants.getStreamBufferSize();
					bufferSize = bufferSize != 0 ? bufferSize : 8192;

					IOUtils.copyBytes(is, os, bufferSize);
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception e) {
							LOGGER.fatal(e.getMessage(), e);
						}
					}
					if (os != null) {
						try {
							os.flush();
						} catch (Exception e) {
							LOGGER.fatal(e.getMessage(), e);
						}
					}
				}
			} else {
				throw new Exception(httpCon.getResponseMessage());
			}
		} finally {
			if (httpCon != null)
				httpCon.disconnect();
		}
		return responseCode;
	}

	public static void main(String[] args) throws ParseException {
		String jsonProperties = "{\"resultTableName\":\"\",\"sqlQuery\":\"SELECT * FROM DATATAGS_pdf\",\"chartDetail\":{},\"selectedOrderBy\":[],\"selectedTable\":[\"DATATAGS_pdf\"],\"queryHeader\":{\"header\":{\"title\":\"\"}},\"selectedWhere\":[],\"limitResultRowsValue\":5000,\"setHighFidelityOutput\":false,\"aggregateOnColumn\":\"\",\"groupHeader\":{},\"queryFooter\":{\"footer\":{\"title\":\"\"}},\"setLimitResultRows\":false,\"groupFooter\":{},\"queryDesc\":\"Sample query\",\"colDetail\":{},\"dbName\":\"MetaStore\",\"colHeaderDetail\":{},\"selectedGroupBy\":[],\"selectedColumn\":{},\"queryId\":\"putBigQuery_1424423764028420000\"}";
		JSONParser parser = new JSONParser();
		JSONObject properties = (JSONObject) parser.parse(jsonProperties);
	}
}
