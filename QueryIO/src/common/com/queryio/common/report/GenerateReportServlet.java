package com.queryio.common.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.json.simple.JSONObject;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.ZipUtil;
import com.queryio.core.customtags.BigQueryManager;

public class GenerateReportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7694024674685375238L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doProcess(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doProcess(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String queryId = request.getHeader("queryId");
		String namenode = request.getHeader("namenode");
		String format = request.getHeader("format");
		String username = request.getHeader("username");

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("GenerateReport request received");
		try {
			if (queryId != null) {
				JSONObject object = BigQueryManager.executeQueryInternal(namenode, queryId, username, false, true,
						false, format);
				if (object != null) {
					int executionId = (Integer) object.get("executionId");
					JSONObject queryObj = BigQueryManager.isQueryCompleteInternal(executionId, false, format, username);
					String filePath = (String) queryObj.get("filePath");
					while (queryObj.get("error") == null) {
						Thread.sleep(1000); // For Adhoc jobs using OS3 Server.
						queryObj = BigQueryManager.isQueryCompleteInternal(executionId, false, format, username);
						filePath = (String) queryObj.get("filePath");
						if (filePath != null) {
							break;
						}
					}

					if (filePath != null) {
						String designFilePath = BigQueryManager.getDesignFilePath(namenode, queryId, username);
						// filePath = designFilePath.substring(0,
						// designFilePath.lastIndexOf(File.separator)) + "/" +
						// filePath;

						InputStream is = null;
						OutputStream os = null;

						String filename = null;

						try {

							if ("HTML".equalsIgnoreCase(format)) {
								String reportDir = designFilePath.substring(0,
										designFilePath.lastIndexOf(File.separator));
								ArrayList listFiles = new ArrayList();
								File reportFile = new File(reportDir + File.separator + filePath);
								listFiles.add(reportFile);
								String temp = filePath.replace(File.separator, "");
								File folder = new File(
										reportDir + File.separator + "resources_" + temp.replace(".html", ""));
								listFiles.add(folder);
								String destZipFile = reportDir + File.separator + filePath.replace(".html", ".zip");
								ZipUtil.compressFiles(listFiles, destZipFile);

								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger()
											.debug("File Path: " + filePath + " destZipFile: " + destZipFile);
								is = new FileInputStream(new File(destZipFile));

								filename = filePath;

							} else {
								filePath = designFilePath.substring(0, designFilePath.lastIndexOf(File.separator)) + "/"
										+ filePath;

								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("File Path: " + filePath);
								is = new FileInputStream(new File(filePath));

								filename = (filePath.indexOf(File.separator) > -1
										? filePath.substring(filePath.lastIndexOf(File.separator) + 1) : filePath);
							}

							os = response.getOutputStream();

							response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

							IOUtils.copy(is, os);

						} finally {
							if (is != null) {
								try {
									is.close();
								} catch (Exception e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
							}
							if (os != null) {
								try {
									os.flush();
								} catch (Exception e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
							}
						}
					} else {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}
}
