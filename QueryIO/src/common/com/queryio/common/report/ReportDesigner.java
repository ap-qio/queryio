package com.queryio.common.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.QueryExecutionDAO;

public class ReportDesigner {
	public static void main(String[] args) throws SQLException {
		try {
			String jsonProperties = readFile("/Users/eshan/Desktop/JSONProperties");

			JSONParser parser = new JSONParser();
			JSONObject properties = (JSONObject) parser.parse(jsonProperties);

			buildReportInNewThread(properties, "/Users/eshan/Desktop/Reports/reporter.rptdesign", -1, "HTML");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	public static void buildReport(final JSONObject properties, final String designFilePath, final int executionId,
			final String format) {
		Connection connection = null;
		String path = null;
		String status = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			try {

				path = ReportGenerator.generateViewReport(designFilePath, format);
				status = QueryIOConstants.QUERYEXECUTION_STATUS_SUCCESS;
				QueryExecutionDAO.updatePathStatus(connection, executionId, path, status);
			} catch (Exception e) {
				path = e.getMessage();
				status = QueryIOConstants.QUERYEXECUTION_STATUS_FAILED;

				try {
					QueryExecutionDAO.updatePathStatus(connection, executionId, path, status);
				} catch (SQLException e1) {
					AppLogger.getLogger().fatal(e1.getMessage(), e1);
				}

				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void buildReportInNewThread(final JSONObject properties, final String designFilePath,
			final int executionId, final String format) throws Exception {
		new Thread() {
			public void run() {
				buildReport(properties, designFilePath, executionId, format);
			}
		}.start();
	}
}