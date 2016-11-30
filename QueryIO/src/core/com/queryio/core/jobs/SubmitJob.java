package com.queryio.core.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.AdHocJobConfig;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.AdHocJobConfigDAO;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.job.definition.JobDefinitionDAO;
import com.queryio.job.definition.JobMappingExtractor;
import com.queryio.plugin.dstruct.IDataDefinition;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class SubmitJob extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter pw = null;
		try {
			pw = res.getWriter();
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Job submit request recevied from host: " + req.getRemoteAddr() + ", user: "
						+ req.getRemoteUser());

			if (RemoteManager.isNonAdminAndDemo(req.getRemoteUser())) {
				throw new Exception(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			}

			String namenodeId = null;
			String rmId = null;
			String jobJarFilePath = null;
			String jobName = null;
			String arguments = null;
			String sourcePath = null;
			String pathPattern = null;
			String mainClass = null;
			String jarFile = null;
			String jarFileText = null;
			String executionJarFile = null;

			boolean isAdhoc = false;

			boolean isNewJob = false;
			List libJars = new ArrayList();
			String libJarFT = null;
			List files = new ArrayList();
			String nativeFT = null;

			ServletFileUpload upload = new ServletFileUpload();

			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Item Field Name :" + item.getFieldName() + " Item Name : "
							+ item.getName() + " Content Type :" + item.getContentType());
				if (item.isFormField()) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Got a form field: " + item.getFieldName());

					if (item.getFieldName().equals("jobName")) {
						jobName = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("jobName: " + jobName);
					} else if (item.getFieldName().equals("arguments")) {
						arguments = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("arguments: " + arguments);
					} else if (item.getFieldName().equals("sourcePath")) {
						sourcePath = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("sourcePath: " + sourcePath);
					} else if (item.getFieldName().equals("pathPattern")) {
						pathPattern = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("pathPattern: " + pathPattern);
					} else if (item.getFieldName().equals("mainClass")) {
						mainClass = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("mainClass: " + mainClass);
					} else if (item.getFieldName().equals("jarText")) {
						jarFile = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("jarFile: " + jarFile);
					} else if (item.getFieldName().equals("jarFileText")) {
						jarFileText = getValue(item);
						if ((jarFileText != null) && (!jarFileText.isEmpty()))
							jarFile = jarFileText;
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("jarFileText: " + jarFile);
					} else if (item.getFieldName().equals("nameNodeId")) {
						namenodeId = getValue(item);
						AppLogger.getLogger().debug("nameNodeId: " + namenodeId);
					} else if (item.getFieldName().equals("resourceManagerId")) {
						rmId = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("resourceManagerId: " + rmId);
					} else if (item.getFieldName().startsWith("libJarFT")) {
						libJarFT = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("libJarFT: " + libJarFT);
						if ((libJarFT != null) && (!libJarFT.isEmpty()))
							libJars.add(libJarFT.trim());
					} else if (item.getFieldName().startsWith("nativeFT")) {
						nativeFT = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("nativeFT: " + nativeFT);
						if ((nativeFT != null) && (!nativeFT.isEmpty()))
							files.add(nativeFT.trim());
					} else if (item.getFieldName().equals("isAdhoc")) {
						String data = getValue(item);
						if (Boolean.parseBoolean(data)) {
							isAdhoc = true;
						}
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("isAdhoc: " + isAdhoc + " data: " + data);
					}
				} else {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());

					FileItemHeaders headers = item.getHeaders();
					if (headers != null) {
						Iterator iter = headers.getHeaderNames();
						while (iter.hasNext()) {
							String header = (String) iter.next();
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug(header + " : " + headers.getHeader(header));
						}
					}
					if (item.getFieldName().contains("executionJarFile")) {

						executionJarFile = item.getName();

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("executionJarFile: " + jarFile);

						if ((executionJarFile != null) && (!executionJarFile.isEmpty())) {
							jarFile = executionJarFile;
							isNewJob = true;
							InputStream is = null;
							OutputStream os = null;
							try {
								is = item.openStream();
								new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
										+ "/" + jobName).mkdirs();
								File file = new File(
										EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
												+ File.separator + jobName + File.separator + executionJarFile);

								jobJarFilePath = file.getAbsolutePath();

								file.createNewFile();
								os = new FileOutputStream(file);
								IOUtils.copy(is, os);
							} finally {
								try {
									if (is != null)
										is.close();
								} catch (Exception e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
								try {
									if (os != null)
										os.close();
								} catch (Exception e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
							}
						}
					} else {
						// type= headers.getHeader("upload-type");
						// if(type.equals("lib-jar")){
						if (item.getFieldName().contains("libJar")) {

							String libJarFile = null;
							if (item.getFieldName().startsWith("libJarFile"))
								libJarFile = item.getName();

							if ((libJarFile != null) && (!libJarFile.isEmpty())) {
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("Lib File Name :" + libJarFile);
								if (libJarFile.length() != 0) {

									libJars.add(libJarFile.trim());
									InputStream is = null;
									OutputStream os = null;
									try {
										is = item.openStream();
										new File(EnvironmentalConstants.getAppHome() + "/"
												+ QueryIOConstants.MAPREDRESOURCE + File.separator + jobName
												+ File.separator + "lib").mkdirs();
										File file = new File(EnvironmentalConstants.getAppHome() + "/"
												+ QueryIOConstants.MAPREDRESOURCE + File.separator + jobName
												+ File.separator + "lib" + File.separator + libJarFile);
										file.createNewFile();

										os = new FileOutputStream(file);
										IOUtils.copy(is, os);
									} finally {
										try {
											if (is != null)
												is.close();
										} catch (Exception e) {
											AppLogger.getLogger().fatal(e.getMessage(), e);
										}
										try {
											if (os != null)
												os.close();
										} catch (Exception e) {
											AppLogger.getLogger().fatal(e.getMessage(), e);
										}
									}
								}
							}

						} else if (item.getFieldName().contains("native")) {

							String nativeFile = null;
							if (item.getFieldName().startsWith("nativeFile"))
								nativeFile = item.getName();

							if ((nativeFile != null) && (!nativeFile.isEmpty())) {
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("Native File Name :" + nativeFile);
								if (nativeFile.length() != 0) {

									files.add(nativeFile.trim());
									InputStream is = null;
									OutputStream os = null;
									try {
										is = item.openStream();
										new File(EnvironmentalConstants.getAppHome() + "/"
												+ QueryIOConstants.MAPREDRESOURCE + File.separator + jobName
												+ File.separator + "files").mkdirs();
										File file = new File(EnvironmentalConstants.getAppHome() + "/"
												+ QueryIOConstants.MAPREDRESOURCE + File.separator + jobName
												+ File.separator + "files" + File.separator + nativeFile);
										file.createNewFile();

										os = new FileOutputStream(file);
										IOUtils.copy(is, os);
									} finally {
										try {
											if (is != null)
												is.close();
										} catch (Exception e) {
											AppLogger.getLogger().fatal(e.getMessage(), e);
										}
										try {
											if (os != null)
												os.close();
										} catch (Exception e) {
											AppLogger.getLogger().fatal(e.getMessage(), e);
										}
									}
								}
							}
						}
					}
				}
			}

			if (jarFile == null) {
				throw new Exception("Did not recieve job jar file");
			}
			jarFile = jobName + "/" + executionJarFile;
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Processing Submit Job Request");

			String libJarsString = "";
			for (int i = 0; i < libJars.size(); i++) {
				if (i != 0)
					libJarsString += ",";
				String currentLibJar = (String) libJars.get(i);
				if (jobName.equalsIgnoreCase(QueryIOConstants.DATATAGGING_DEFAULT_JOB)
						|| currentLibJar.startsWith(jobName + "/lib/")
						|| (jobName.startsWith(QueryIOConstants.DATATAGGING_PREFIX)
								&& currentLibJar.startsWith("Plugins"))) {
					libJarsString += currentLibJar;
				} else {
					libJarsString += jobName + "/lib/" + currentLibJar;
				}
			}

			String filesString = "";
			for (int i = 0; i < files.size(); i++) {
				if (i != 0)
					filesString += ",";
				filesString += jobName + "/files/" + files.get(i);
			}

			Connection connection = null;
			Connection customTagConnection = null;
			try {
				connection = CoreDBManager.getQueryIODBConnection();
				if (NodeDAO.getNode(connection, rmId) == null) {
					throw new Exception("No Resource Managers found by this Id");
				}
				if (NodeDAO.getNode(connection, namenodeId) == null) {
					throw new Exception("No Namenode found by this Id");
				}
				if (NodeDAO.getAllNMs(connection).size() < 1) {
					throw new Exception("No Node Managers found. Please configure Node Manager(s) before adding jobs.");
				}

				Configuration config = ConfigurationManager.getConfiguration(connection, namenodeId);

				String connectionName = config.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
				if (connectionName == null)
					connectionName = config.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
				customTagConnection = CoreDBManager.getCustomTagDBConnection(connectionName);
				DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null);

				if (isNewJob) {
					if (isAdhoc) {
						AdHocJobConfig adHocJobConfig = new AdHocJobConfig(namenodeId, rmId, jobName, jarFile,
								libJarsString, filesString, mainClass, sourcePath, pathPattern, arguments);
						if ((jarFileText != null) && (executionJarFile != null)) {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Job jarFilePath: " + jobJarFilePath);

							jarFile = executionJarFile;
							boolean isParsed = false;

							if (jobJarFilePath != null && isAdhoc) {
								JobMappingExtractor extractor = new JobMappingExtractor(jobJarFilePath);
								try {
									extractor.parse();
									if (extractor.getTableName() != null) {
										isParsed = true;
										if (AppLogger.getLogger().isDebugEnabled())
											AppLogger.getLogger().debug("Table Name: " + extractor.getTableName());
										if (AppLogger.getLogger().isDebugEnabled())
											AppLogger.getLogger().debug("Meta Data: " + extractor.getColumnMetaData());
										JobDefinitionDAO.addJobDefinition(connection, jobName,
												extractor.getTableName());

										if (!UserDefinedTagDAO.checkIfTableExists(customTagConnection,
												extractor.getTableName()))
											UserDefinedTagDAO.createDatabaseTable(customTagConnection, props,
													extractor.getTableName(), extractor.getColumnMetaData());
									}
								} catch (Exception e) {
									AppLogger.getLogger()
											.fatal("Job jar parsing failed with exception: " + e.getMessage(), e);
								}
							}

							if ((!isParsed) && (isAdhoc))
								throw new Exception(
										"Interface not implemented for the given Adhoc Job. Please submit jar containing implementation of "
												+ IDataDefinition.class.getName() + " interface.");

							AdHocJobConfigDAO.delete(connection, jobName);
							AdHocJobConfigDAO.insert(connection, adHocJobConfig);
						} else if (executionJarFile != null) {
							if (AdHocJobConfigDAO.get(connection, jobName) != null) {
								throw new Exception("Job already exist by this name. Please use a different Job name");
							} else {
								AdHocJobConfigDAO.updateJob(connection, adHocJobConfig);
							}
						}
					} else {
						// By default Recursive was true, and input path filter
						// was false.
						MapRedJobConfig mapredJobConfig = new MapRedJobConfig(namenodeId, rmId, jobName, jarFile,
								libJarsString, filesString, mainClass, arguments, true, false, null);
						if (isNewJob) {
							MapRedJobConfigDAO.delete(connection, jobName, false);
							MapRedJobConfigDAO.insert(connection, mapredJobConfig);
						} else {
							MapRedJobConfigDAO.updateJob(connection, mapredJobConfig);
						}
					}
				} else {
					if (isAdhoc) {
						AdHocJobConfig adHocJobConfig = new AdHocJobConfig(namenodeId, rmId, jobName, jarFile,
								libJarsString, filesString, mainClass, sourcePath, pathPattern, arguments);
						AdHocJobConfigDAO.updateJob(connection, adHocJobConfig);
					} else {
						// By default Recursive was true, and input path filter
						// was false.
						MapRedJobConfig mapredJobConfig = new MapRedJobConfig(namenodeId, rmId, jobName, jarFile,
								libJarsString, filesString, mainClass, arguments, true, false, null);
						MapRedJobConfigDAO.updateJob(connection, mapredJobConfig);
					}

				}

				// if (isNewJob)
				// {
				// if ((jarFileText != null) && (executionJarFile != null))
				// {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug(
				// "Job jarFilePath: " + jobJarFilePath);
				//
				// jarFile = executionJarFile;
				// boolean isParsed = false;
				//
				// if (jobJarFilePath != null && isAdhoc)
				// {
				// JobMappingExtractor extractor = new JobMappingExtractor(
				// jobJarFilePath);
				// try {
				// extractor.parse();
				// if (extractor.getTableName() != null) {
				// isParsed = true;
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug(
				// "Table Name: " + extractor.getTableName());
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug(
				// "Meta Data: " + extractor.getColumnMetaData());
				// JobDefinitionDAO.addJobDefinition(connection, jobName,
				// extractor.getTableName());
				//
				// if(!UserDefinedTagDAO.checkIfTableExists(customTagConnection,
				// extractor.getTableName()))
				// UserDefinedTagDAO.createDatabaseTable(customTagConnection,
				// extractor.getTableName(), extractor.getColumnMetaData());
				// }
				// } catch (Exception e) {
				// AppLogger.getLogger().fatal(
				// "Job jar parsing failed with exception: "
				// + e.getMessage(), e);
				// }
				// }
				//
				// if ((!isParsed) && (isAdhoc))
				// throw new Exception("Interface not implemented for the given
				// Adhoc Job. Please submit jar containing implementation of " +
				// IDataDefinition.class.getName() + " interface.");
				//
				// ApplicationManager.deleteJobDB(jobName);
				// MapRedJobConfigDAO.insert(connection, mapredJobConfig);
				// }
				// else if (executionJarFile != null)
				// {
				// if (MapRedJobConfigDAO.get(connection, jobName) != null)
				// {
				// throw new Exception("Job already exist by this name. Please
				// use a different Job name");
				// }
				// }
				// }
				// else
				// {
				// MapRedJobConfigDAO.updateJob(connection, mapredJobConfig);
				// }

				File libDir = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
						+ File.separator + jobName + File.separator + "lib");
				if (libDir.exists()) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("libJars: " + libJars);
					if ((libJars != null) && (libJars.size() > 0)) {
						File[] libFiles = libDir.listFiles();
						if (libFiles != null) {
							for (int i = 0; i < libFiles.length; i++) {
								File file = libFiles[i];
								if ((file.exists()) && (!libJars.contains(file.getName()))) {
									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("Deleted File: " + file.getName());
									file.delete();
								}
							}
						}
					} else {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Deleted Dir: " + libDir.getName());
						StaticUtilities.deleteFile(libDir);
					}
				}

				File nativeDir = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
						+ File.separator + jobName + File.separator + "files");
				if (nativeDir.exists()) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("files: " + files);
					if ((files != null) && (files.size() > 0)) {
						File[] nativeFiles = nativeDir.listFiles();
						if (nativeFiles != null) {
							for (int i = 0; i < nativeFiles.length; i++) {
								File file = nativeFiles[i];
								if ((file.exists()) && (!files.contains(file.getName()))) {
									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("Deleted File: " + file.getName());
									file.delete();
								}
							}
						}
					} else {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Deleted Dir: " + nativeDir.getName());
						StaticUtilities.deleteFile(nativeDir);
					}
				}

			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				try {
					CoreDBManager.closeConnection(customTagConnection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			res.setContentType("text/plain");
			pw.write("Job Saved Successfully.");
			pw.close();
		} catch (Exception ex) {
			String msg = null;
			if (ex.getMessage() != null && ex.getMessage().equals(QueryIOConstants.NOT_AN_AUTHORIZED_USER)) {
				msg = QueryIOConstants.NOT_AN_AUTHORIZED_USER;
			} else {
				AppLogger.getLogger().fatal(ex.getMessage(), ex);
				msg = "Job Submission Failed. " + ex.getMessage();
			}
			res.setContentType("text/plain");
			pw.write(msg);
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