package com.queryio.config.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.HostDAO;

public class DBConnectionManager extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	public void doProcess(HttpServletRequest request, HttpServletResponse response) {
		String operation = null;
		String connectionName = null;
		String connectionType = null;
		String primaryURL = null;
		String primaryUsername = null;
		String primaryPassword = null;
		String primaryDriver = null;
		String driverJar = null;
		long maxConnections = -1;
		long maxIdleConnections = -1;
		long waitTimeMilliSeconds = -1;

		boolean isPrimary = true;

		ServletFileUpload fservletUpload = null;
		String message = "";
		boolean flag = true;
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			if (RemoteManager.isNonAdminAndDemo(request.getRemoteUser())) {
				throw new Exception(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			}
			fservletUpload = new ServletFileUpload();
			FileItemIterator iter = fservletUpload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream fstream = iter.next();
				if (fstream.getFieldName().equals("secondaryDriverJar") || fstream.getFieldName().equals("driverJar")) {
					driverJar = fstream.getName();
					if (fstream.getName() != null) {
						writeFile(fstream);
						// message = "Jar Uploaded Successfully.";
					}
				} else {
					if (fstream.getFieldName().equals("connectionName")) {
						connectionName = getValue(fstream);
					} else if (fstream.getFieldName().equals("url")) {
						primaryURL = getValue(fstream);
					} else if (fstream.getFieldName().equals("type")) {
						connectionType = getValue(fstream);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("connectionType: " + connectionType);
					} else if (fstream.getFieldName().equals("username")) {
						primaryUsername = getValue(fstream);
					} else if (fstream.getFieldName().equals("passwd")) {
						primaryPassword = getValue(fstream);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("primaryPassword ::>" + primaryPassword);
					} else if (fstream.getFieldName().equals("driver")) {
						primaryDriver = getValue(fstream);
					} else if (fstream.getFieldName().equals("isPrimary")) {
						isPrimary = Boolean.parseBoolean(getValue(fstream));
					} else if (fstream.getFieldName().equals("operation")) {
						operation = getValue(fstream);
					} else if (fstream.getFieldName().equals("maxConnections")) {
						String value = getValue(fstream);
						maxConnections = Long.parseLong(value);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("maxconnection " + maxConnections);
					} else if (fstream.getFieldName().equals("maxIdleConnections")) {

						maxIdleConnections = Long.parseLong(getValue(fstream));
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("maxIdleConnections " + maxIdleConnections);
					} else if (fstream.getFieldName().equals("waitTimeMilliSeconds")) {
						waitTimeMilliSeconds = Long.parseLong(getValue(fstream));
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("waitTimeMilliSeconds " + waitTimeMilliSeconds);
					}
				}
			}
		} catch (Exception e) {
			flag = false;
			if (QueryIOConstants.NOT_AN_AUTHORIZED_USER.equals(e.getMessage())) {
				if (pw != null) {
					pw.write(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				}
			} else {
				if (pw != null)
					pw.write("Error configuring database connection " + e.getMessage());
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error configuring database connection : ", e);
			}
		}
		if (flag) {
			try {
				if (operation.equals("Save")) {
					DBConfigManager.createConnection(connectionName, connectionType, primaryURL, primaryUsername,
							primaryPassword, primaryDriver, driverJar, isPrimary, maxConnections, maxIdleConnections,
							waitTimeMilliSeconds);
				} else {
					DBConfigManager.updateConnection(connectionName, primaryURL, primaryUsername, primaryPassword,
							primaryDriver, driverJar, isPrimary, maxConnections, maxIdleConnections,
							waitTimeMilliSeconds);
				}
				message += "DB Connection saved successfully.";
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				message += "DB Connection configuration failed. " + e.getLocalizedMessage();
			}
			pw.write(message);
		}
		if (pw != null) {
			try {
				pw.close();
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Caught Exception while clossing response,  ", e);
			}
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

	public void writeFile(FileItemStream fis) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = fis.openStream();
			File dir = new File(EnvironmentalConstants.getAppHome() + QueryIOConstants.JDBC_JAR_DIR);
			File hadoopPackageDir = new File(StartupParameters.getHadoopDirLocation() + File.separator
					+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps" + File.separator
					+ QueryIOConstants.AGENT_QUERYIO + File.separator + QueryIOConstants.JDBC_JAR_DIR);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!hadoopPackageDir.exists()) {
				hadoopPackageDir.mkdir();
			}
			File file = new File(dir.getAbsolutePath() + File.separator + fis.getName());
			File hadoopPackageFile = new File(hadoopPackageDir.getAbsolutePath() + File.separator + fis.getName());

			if (!file.exists()) {
				file.createNewFile();
				os = new FileOutputStream(file);
				writeToStream(is, os);
			}
			if (!hadoopPackageFile.exists()) {
				hadoopPackageFile.createNewFile();
				os = new FileOutputStream(hadoopPackageFile);
				writeToStream(is, os);
			}
			if (fis.getName() != null && !fis.getName().isEmpty()) {
				Connection connection = null;
				try {
					connection = CoreDBManager.getQueryIODBConnection();
					List hosts = HostDAO.getAllHostDetails(connection);
					for (int i = 0; i < hosts.size(); i++) {
						Host host = (Host) hosts.get(i);
						QueryIOAgentManager.transferDriverJar(host, fis.getName());
					}
				} finally {
					CoreDBManager.closeConnection(connection);
				}
			}
		} catch (IOException e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Caught Exception while uploading file.", e);
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Caught Exception while uploading file.", e);
		} finally {
			try {
				if (os != null)
					os.close();
				is.close();
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(e.getMessage(), e);
			}
		}
	}

	public void writeToStream(InputStream inputStream, OutputStream outputStream) throws Exception {
		int bufferSize = EnvironmentalConstants.getStreamBufferSize();
		if (bufferSize == 0) {
			bufferSize = 1024000;
		}
		final byte[] readBuffer = new byte[bufferSize];
		int bytesIn = 0;
		while ((bytesIn = inputStream.read(readBuffer, 0, readBuffer.length)) != -1) {
			outputStream.write(readBuffer, 0, bytesIn);
		}
	}

}
