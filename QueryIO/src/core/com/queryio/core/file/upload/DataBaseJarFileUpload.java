package com.queryio.core.file.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.DataSourceDAO;
import com.queryio.core.datasources.DataBaseDataSource;

public class DataBaseJarFileUpload extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pw = null;
		JSONObject respObject = new JSONObject();
		boolean success = false;

		String driver = "";
		String connecionID = "";
		String connectionurl = "";
		String username = "";
		String password = "";
		String driverjar = "";
		int maxConn = 20;
		int maxIdleConn = 10;
		int waitTime = 30000;
		boolean toBeParsed = false;
		FileItemIterator iterator = null;
		Connection connection = null;
		AppLogger.getLogger().debug("File upload request recevied from host: " + request.getRemoteAddr() + ", user: "
				+ request.getRemoteUser());

		try {
			pw = response.getWriter();
			ServletFileUpload upload = new ServletFileUpload();
			iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (item.isFormField()) {
					if (item.getFieldName().equals("connectionId")) {
						connecionID = getValue(item);
					} else if (item.getFieldName().equals("DBDriverClass")) {
						driver = getValue(item);
					} else if (item.getFieldName().equals("DBConnectionURL")) {
						connectionurl = getValue(item);
					} else if (item.getFieldName().equals("DataBaseUser")) {
						username = getValue(item);
					} else if (item.getFieldName().equals("DataBasePass")) {
						password = getValue(item);
					} else if (item.getFieldName().equals("maxConnection")) {
						String data = getValue(item);
						maxConn = Integer.parseInt(data);
					} else if (item.getFieldName().equals("maxIdleConnection")) {
						String data = getValue(item);
						maxIdleConn = Integer.parseInt(data);

					} else if (item.getFieldName().equals("waitTime")) {
						String data = getValue(item);
						waitTime = Integer.parseInt(data);
					}
				} else {
					if (item.getFieldName().equals("DataBaseDriverJar")) {
						driverjar = item.getName();
						AppLogger.getLogger()
								.debug("Got an uploaded file: " + item.getFieldName() + ", name = " + driverjar);
						AppLogger.getLogger().debug("Processing Upload Request");
						InputStream inStream = null;
						OutputStream outStream = null;
						try {

							String path = EnvironmentalConstants.getAppHome() + File.separator
									+ QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR;// +
																					// QueryIOConstants.SAMPLE_FILE_DIR;
							File parentDir = new File(path);

							if (!parentDir.exists())
								parentDir.mkdirs();
							inStream = new BufferedInputStream(item.openStream(), 16 * 1024);
							outStream = new FileOutputStream(
									new File(parentDir.getAbsolutePath() + File.separator + driverjar));

							int read = 0;
							final byte[] bytes = new byte[1024];
							while ((read = inStream.read(bytes)) != -1) {
								outStream.write(bytes, 0, read);
							}

							toBeParsed = true;
							if (toBeParsed) {
								connection = CoreDBManager.getQueryIODBConnection();
								int dC = DataSourceDAO.getDataConnectionType(connection, connecionID);
								if (dC != -1) {
									AppLogger.getLogger().debug("throwing exception : " + toBeParsed);
									throw new Exception();
								}
								AppLogger.getLogger().debug("toBeParsed: " + toBeParsed);
								AppLogger.getLogger().debug("connectionId: " + connecionID);
								AppLogger.getLogger().debug("driver: " + driver);
								AppLogger.getLogger().debug("connection url: " + connectionurl);
								AppLogger.getLogger().debug("username: " + username);
								AppLogger.getLogger().debug("password: " + password);
								AppLogger.getLogger().debug("driver jar: " + driverjar);
								AppLogger.getLogger().debug("max connection: " + maxConn);
								AppLogger.getLogger().debug("max idle connection: " + maxIdleConn);
								AppLogger.getLogger().debug("wait time: " + waitTime);

								DataSourceDAO.addDataConnection(connection, connecionID,
										QueryIOConstants.DATA_CONNECTION_TYPE_DATABASE);
								DataBaseDataSource ds = new DataBaseDataSource();
								ds.setId(connecionID);
								ds.setDriver(driver);
								ds.setConnectionURL(connectionurl);
								ds.setUserName(username);
								ds.setPassword(password);
								ds.setJarFileName(driverjar);
								ds.setMaxConnections(maxConn);
								ds.setMaxIdleConnections(maxIdleConn);
								ds.setWaitTimeMilliSeconds(waitTime);

								DataSourceDAO.addDataBaseDataSource(connection, ds);

								AppLogger.getLogger().debug("completed: " + toBeParsed);

								respObject.put(QueryIOConstants.STATUS, "sucess");
								respObject.put("message", "Tag Parser uploaded successfully.");
								respObject.put("error", "");
								pw.write(respObject.toJSONString());
							}

						} finally {
							try {
								if (inStream != null)
									inStream.close();
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
								throw new Exception(e);
							}
							try {
								if (outStream != null)
									outStream.close();
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
								throw new Exception(e);
							}
							try {
								if (connection != null) {
									connection.close();
								}
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
								throw new Exception(e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().debug("toBeParsed: " + toBeParsed);
			response.setContentType("text/plain");
			respObject.put(QueryIOConstants.STATUS, "fail");
			respObject.put("message", "jar upload failed ");
			respObject.put("error", e.getMessage());
			if (pw != null)
				pw.write(respObject.toJSONString());
			AppLogger.getLogger().fatal("Upload failed : " + e.getMessage(), e);
		} finally {
			if (pw != null)
				pw.close();
		}

	}

	public String getValue(FileItemStream item) throws IOException {
		String theString = null;
		InputStream is = null;
		try {
			is = item.openStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			theString = writer.toString();
			if (theString != null)
				theString = theString.trim();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return theString;
	}
}
