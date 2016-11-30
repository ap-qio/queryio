package com.queryio.file.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.TagParserConfig;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.TagParserDAO;

public class TagParserFileUpload extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3497141003644506709L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		PrintWriter pw = null;
		JSONObject respObject = new JSONObject();
		boolean success = false;

		try {
			pw = res.getWriter();
			if (RemoteManager.isNonAdminAndDemo(req.getRemoteUser())) {
				throw new Exception(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			}
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("File upload request recevied from host: " + req.getRemoteAddr()
						+ ", user: " + req.getRemoteUser());
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator;

			iterator = upload.getItemIterator(req);

			String name = "";
			String desc = "";
			String jar = "";
			String className = "";
			String fileType = "";
			String interval = "";
			String scheduleTime = "";
			String nameNode = "";
			String resourceManager = "";
			String jarFile = null;
			boolean isMainJarFound = false;
			List extraJars = new ArrayList();
			String fileTagFT = null;

			boolean isIngest = true;
			boolean isActive = true;
			int index = 1;
			while (iterator.hasNext()) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(index++);
				FileItemStream item = iterator.next();
				if (item.isFormField()) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("item.getFieldName(): " + item.getFieldName());
					if (item.getFieldName().equals("tagName")) {
						name = getValue(item);
					} else if (item.getFieldName().equals("tagDesc")) {
						desc = getValue(item);
					} else if (item.getFieldName().equals("class_name")) {
						className = getValue(item);
					} else if (item.getFieldName().equals("file_type")) {
						fileType = getValue(item);
					} else if (item.getFieldName().equals("labelIngest")) {
						isIngest = Boolean.parseBoolean(getValue(item));
					} else if (item.getFieldName().equals("labelActive")) {
						isActive = Boolean.parseBoolean(getValue(item));
					} else if (item.getFieldName().equals("NameNodeId")) {
						nameNode = getValue(item);
					} else if (item.getFieldName().equals("ResourceManagerId")) {
						resourceManager = getValue(item);
					} else if (item.getFieldName().equals("fileTagText")) {
						jar = getValue(item);
						success = true;
					} else if (item.getFieldName().startsWith("fileTagFT")) {
						fileTagFT = getValue(item);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("fileTagFT: " + fileTagFT);
						if ((fileTagFT != null) && (!fileTagFT.isEmpty()))
							extraJars.add(fileTagFT.trim());
					}
				} else {

					if (item.getFieldName().equals("fileTag")) {
						jar = item.getName();
						if ((jar == null) || (jar.isEmpty()))
							continue;
						isMainJarFound = true;
					} else if (item.getFieldName().startsWith("fileTag")) {
						String fileTag = null;
						fileTag = item.getName();
						if ((fileTag == null) || (fileTag.isEmpty()))
							continue;
						extraJars.add(fileTag.trim());
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());

					String contentLength = "0";
					FileItemHeaders headers = item.getHeaders();
					if (headers != null) {
						contentLength = headers.getHeader("Content-Length");
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Content-Length: " + contentLength);

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Rcvd Headers");
						java.util.Iterator iter = headers.getHeaderNames();
						while (iter.hasNext()) {
							String header = (String) iter.next();
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug(header + " : " + headers.getHeader(header));
						}
					}

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Processing Upload Request");
					InputStream stream = null;
					FileOutputStream fos = null;
					try {
						stream = item.openStream();

						File file = null;

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("isIngest: " + isIngest);
						if (isIngest)
							file = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
									+ "/" + QueryIOConstants.TAGPARSER_JAR_DIR + "/" + name + "/" + item.getName());
						else
							file = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
									+ "/" + name + "/" + item.getName());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("FilePath: " + file.getAbsolutePath());
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						file.createNewFile();
						fos = new FileOutputStream(file);
						if (isMainJarFound) {
							jarFile = file.getAbsolutePath();
							isMainJarFound = false;
							success = true;
						}
						IOUtils.copy(stream, fos);
					} finally {
						try {
							if (stream != null) {
								stream.close();
							}
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
						try {
							if (fos != null) {
								fos.close();
							}
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			}

			StringBuilder extraJarsString = new StringBuilder();

			if (isIngest) {
				extraJarsString.append(jar);
				for (int i = 0; i < extraJars.size(); i++) {
					extraJarsString.append(',');
					extraJarsString.append(extraJars.get(i));
				}
			} else {
				String jarParent = "";
				if (jar != null)
					jarParent = jar.split("/")[0];
				if (jarParent.equalsIgnoreCase(name))
					extraJarsString.append(jar);
				else {
					extraJarsString.append(name);
					extraJarsString.append('/');
					extraJarsString.append(jar);
				}

				String jarExt = "";
				for (int i = 0; i < extraJars.size(); i++) {
					jarExt = String.valueOf(extraJars.get(i));
					if (jarExt != null)
						jarParent = jarExt.split("/")[0];
					if (jarParent.equalsIgnoreCase(name)) {
						extraJarsString.append(',');
						extraJarsString.append(jarExt);
					} else {
						extraJarsString.append(',');
						extraJarsString.append(name);
						extraJarsString.append('/');
						extraJarsString.append(jarExt);
					}
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("jarFile: " + jarFile);
			if (jarFile != null && !jarFile.isEmpty()) {
				TagParserDBSchemaUpdator dbUpdator = new TagParserDBSchemaUpdator(jarFile, className, nameNode,
						fileType);
				QueryIOResponse resp = dbUpdator.parse();
				if (!resp.isSuccessful()) {
					Connection connection = null;
					try {
						connection = CoreDBManager.getQueryIODBConnection();
						if (TagParserDAO.getByName(connection, name, isIngest) == null) {
							FileUtils.deleteDirectory(new File(jarFile).getParentFile());
						}
					} catch (Exception e) {

					} finally {
						CoreDBManager.closeConnection(connection);
					}
					throw new Exception(resp.getResponseMsg());
				}
			} else {
				if (isActive) {
					Connection connection = null;
					try {
						connection = CoreDBManager.getQueryIODBConnection();
						TagParserConfig config = TagParserDAO.getByName(connection, name, isIngest);
						if (config != null) {
							String jars = config.getJarName();
							for (String jarName : jars.split(",")) {
								if (isIngest) {
									jarName = EnvironmentalConstants.getAppHome() + "/"
											+ QueryIOConstants.MAPREDRESOURCE + "/" + QueryIOConstants.TAGPARSER_JAR_DIR
											+ "/" + config.getTagName() + "/" + jarName;
								} else {

									String jarParent = "";
									if (jarName != null)
										jarParent = jarName.split("/")[0];
									if (!jarParent.equalsIgnoreCase(name))
										jarName = name + "/" + jarName;
									jarName = EnvironmentalConstants.getAppHome() + "/"
											+ QueryIOConstants.MAPREDRESOURCE + "/" + jarName;
								}

								TagParserDBSchemaUpdator dbUpdator = new TagParserDBSchemaUpdator(jarName, className,
										nameNode, fileType);
								QueryIOResponse resp = dbUpdator.parse();
								if (resp.isSuccessful()) {
									break;
								} else {
									throw new Exception(resp.getResponseMsg());
								}
							}
						}

					} finally {
						CoreDBManager.closeConnection(connection);
					}
				}
			}

			extraJars.add(jar);

			File libDir = null;
			if (isIngest)
				libDir = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/"
						+ QueryIOConstants.TAGPARSER_JAR_DIR + "/" + name);
			else
				libDir = new File(
						EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + name);

			if (libDir.exists()) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("extraJars: " + extraJars);
				if ((extraJars != null) && (extraJars.size() > 0)) {
					File[] libFiles = libDir.listFiles();
					if (libFiles != null) {
						for (int i = 0; i < libFiles.length; i++) {
							File file = libFiles[i];
							if (file.exists()) {
								if (isIngest) {
									if (!extraJars.contains(file.getName())) {
										if (AppLogger.getLogger().isDebugEnabled())
											AppLogger.getLogger().debug("Deleted File: " + file.getName());
										file.delete();
									}
								}
							}
						}
					}
				} else {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Deleted Dir: " + libDir.getName());
					StaticUtilities.deleteFile(libDir);
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Data - name: " + name + " desc: " + desc + " jar: " + jar + " class_name: "
						+ className + " file_type: " + fileType + " isIngest: " + isIngest + " isActive: " + isActive
						+ " interval: " + interval + " scheduleTime: " + scheduleTime + " nameNode: " + nameNode
						+ " resourceManager: " + resourceManager + " extraJarsString: " + extraJarsString.toString());

			if (isIngest)
				RemoteManager.insertOnIngestTagParserConfig(name, desc, extraJarsString.toString(), fileType, className,
						nameNode, isActive);
			else
				RemoteManager.insertPostIngestTagParserConfig(name, desc, extraJarsString.toString(), fileType,
						className, nameNode, resourceManager);

			if (!success) {
				throw new Exception("Upload failed.");
			}
			// else{
			// try{
			// RemoteManager.insertTagParserConfig(filename, filedesc, jar,
			// file_type, class_name, true);
			// }
			// catch(Exception e){
			// pw.write(e.getMessage());
			// }
			// }

			// res.setContentType("text/plain");

			respObject.put(QueryIOConstants.STATUS, "sucess");
			respObject.put("message", "Tag Parser uploaded successfully.");
			respObject.put("error", "");
			pw.write(respObject.toJSONString());

		} catch (Exception ex) {
			res.setContentType("text/plain");
			if (ex.getMessage() != null && ex.getMessage().equals(QueryIOConstants.NOT_AN_AUTHORIZED_USER)) {
				respObject.put(QueryIOConstants.STATUS, "fail");
				respObject.put("message", QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				respObject.put("error", QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			} else {
				respObject.put(QueryIOConstants.STATUS, "fail");
				respObject.put("message", "Tag Parser upload failed ");
				respObject.put("error", ex.getMessage());
			}
			if (pw != null)
				pw.write(respObject.toJSONString());
			AppLogger.getLogger().fatal("Upload failed : " + ex.getMessage(), ex);
		} finally {
			if (pw != null) {
				pw.close();
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