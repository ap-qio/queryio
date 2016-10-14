package com.queryio.file.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.Settings;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.customtags.metadata.MetaDataTagManager;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.requestprocessor.MKDIRRequest;
import com.queryio.core.requestprocessor.PutFileRequest;
import com.queryio.core.requestprocessor.TagParserException;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.stream.util.EncryptionHandler;

public class FileUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		final long startTime = System.currentTimeMillis();
		FileUploadListener fileUploadListener = null;

		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new IllegalArgumentException(
					"Request is not multipart, please use 'multipart/form-data' enctype for your form.");
		}

		PrintWriter writer = res.getWriter();
		res.setContentType("text/plain");
		JSONArray json = new JSONArray();
		JSONObject jsono = new JSONObject();
		Connection connection = null;
		boolean isRightsIssue = false;
		boolean deflate = false;

		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("File upload request recevied from host: " + req.getRemoteAddr()
						+ ", user: " + req.getRemoteUser());

			String user = req.getRemoteUser();
			String group = RemoteManager.getDefaultGroupForUser(user);

			if (RemoteManager.isNonAdminAndDemo(user)) {
				isRightsIssue = true;
				throw new Exception(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			}

			String nameNodeId = null;
			String destinationPath = null;
			boolean extractArchives = false;
			long fileSize = -1;

//			String logicalTagsJSONId = null;

			String compressionType = EncryptionHandler.NONE;
			String encryptionType = EncryptionHandler.NONE;

			String fsDefaultName = null;
			Configuration conf = null;

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(Integer.MAX_VALUE);

			ServletFileUpload upload = new ServletFileUpload();

			// set file upload progress listener
			fileUploadListener = new FileUploadListener();
			upload.setProgressListener(fileUploadListener);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Max File Size: " + upload.getFileSizeMax());

			FileItemStream item;
			FileItemIterator iterator;

			String tagsJSONString = null;

			iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				item = iterator.next();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Item: " + item.getFieldName());
				if (item.isFormField()) {
					if (item.getFieldName().equals("ClusterNameNodeId")) {
						nameNodeId = getValue(item);
					} else if (item.getFieldName().equals("hdfsPath")) {
						destinationPath = getValue(item);
					} else if (item.getFieldName().equals("compressionType")) {
						compressionType = getValue(item);
					} else if (item.getFieldName().equals("encryptionType")) {
						encryptionType = getValue(item);
					} else if (item.getFieldName().equals("tagsJSONString")) {
						tagsJSONString = getValue(item);
					} else if (item.getFieldName().equals("extractArchives")) {
						String data = getValue(item);
						if (Boolean.parseBoolean(data)) {
							extractArchives = true;
						} else {
							extractArchives = false;
						}
					} else if (item.getFieldName().equals("fileSize")) {
						String data = getValue(item);
						if ((data != null) && !data.isEmpty())
							fileSize = Long.valueOf(data);
					}
				} else {

					jsono.put("name", item.getName());

					deflate = (getFileExtension(item.getName()).equalsIgnoreCase("ZIP"));

					InputStream stream = null;
					ZipInputStream zStream = null;

					try {
						// possible cause of OM Perm Gen. //TODO
						stream = new BufferedInputStream(item.openStream(), 16 * 1024);
						long totalBytesAvailable = fileUploadListener.getContentLength();

						if (fileSize < 0)
							fileSize = totalBytesAvailable;

						jsono.put("size", fileSize);
					
						if (nameNodeId == null) {
							connection = CoreDBManager.getQueryIODBConnection();
							@SuppressWarnings("rawtypes")
							ArrayList arr = NodeDAO.getAllNameNodesID(connection);
							if (arr != null)
								nameNodeId = (String) arr.get(0);
							deflate = (getFileExtension(item.getName()).equalsIgnoreCase("ZIP"));
						}
						fsDefaultName = RemoteManager.getFsDefaultName(nameNodeId);
						conf = RemoteManager.getNameNodeConfiguration(nameNodeId);

						if (destinationPath == null)
							destinationPath = "/";

						deflate = deflate && extractArchives;

						FSOperationUtil.createDirectoryRecursively(conf, nameNodeId, fsDefaultName, destinationPath,
								user, group);

						JSONObject tags = null;

						if (tagsJSONString != null) {
							JSONParser parser = new JSONParser();
							tags = (JSONObject) parser.parse(tagsJSONString);
						}

						List<UserDefinedTag> extraTags = new ArrayList<UserDefinedTag>();
						if (tags != null) {
							Iterator it = tags.keySet().iterator();
							String key;
							while (it.hasNext()) {
								key = (String) it.next();
								extraTags.add(new UserDefinedTag(key, tags.get(key)));
							}
						}

						JSONObject logicalTagsJSON = null;

						if (deflate) {
							zStream = new ZipInputStream(stream);
							unzipAndSave(zStream, user, group, destinationPath, item.getName(), nameNodeId,
									fsDefaultName, fileSize, conf, deflate, compressionType, encryptionType,
									fileUploadListener, extraTags, logicalTagsJSON);
						} else {
							executePutFileRequest(user, group, destinationPath, item.getName(), nameNodeId,
									fsDefaultName, stream, fileSize, conf, deflate, compressionType, encryptionType,
									fileUploadListener, extraTags, logicalTagsJSON);
						}
					} finally {
						try {
							if (zStream != null) {
								zStream.close();
							}
							if (stream != null) {
								stream.close();
							}
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			}
			// fileUploadListener.setStatus("Success");
			jsono.put("success", "File uploaded successfully.");
		} catch (TagParserException ex) {
			String errorMessage = "Tag processing failed but file uploaded succesfully. Reason: " + ex.getMessage();
			jsono.put("success", errorMessage);
			
		} catch (Exception ex) {
			// fileUploadListener.setStatus("Failed");
			// fileUploadListener.setErrorMsg(ex.getLocalizedMessage());
			if (isRightsIssue)
				jsono.put("error", "Error: " + ex.getLocalizedMessage());
			else
				jsono.put("error", "Error occurred while uploading file.");

			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		} finally {
			json.add(jsono);
			writer.write(json.toString());
			writer.flush();
			writer.close();
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection.");
			}
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("totalTime " + (System.currentTimeMillis() - startTime));
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("File upload returning...");
	}

	private JSONObject getLogicalTagExtensionJSON(String fileType, String filePath) throws Exception {
		List<String> tagList = MetaDataTagManager.getAllCustomTagMetadataIds(fileType, true);
		JSONObject combinedTags = null;
		combinedTags = combineTags(tagList, combinedTags, filePath);
		List<String> genericTagsList = MetaDataTagManager.getAllCustomTagMetadataIds("ALL_FILES", true);
		combinedTags = combineTags(genericTagsList, combinedTags, filePath);
		combinedTags = addFileTypeInJSON(combinedTags, fileType);
		return combinedTags;
	}

	private JSONObject addFileTypeInJSON(JSONObject json, String fileType) throws Exception {
		if (json != null && json.get("FileType") == null) {
			json.put("FileType", fileType.toUpperCase());
		}
		return json;
	}

	private JSONObject combineTags(List<String> tagList, JSONObject logicalTagsJSON, String filePath) throws Exception {
		String logicalTagsJSONId = null;
		Map<String, Object> map = null;
		JSONObject tempJSONObj = null;
		if (tagList != null) {
			for (int i = 0; i < tagList.size(); i++) {
				logicalTagsJSONId = tagList.get(i);
				String logicalTagsJSONString = null;
				map = MetaDataTagManager.getCustomTagMetaataDetailById(logicalTagsJSONId);
				if (map != null && (Boolean) map.get("isActive")) {
					logicalTagsJSONString = (String) map.get("json");
				}

				if (logicalTagsJSONString != null) {
					tempJSONObj = (JSONObject) new JSONParser().parse(logicalTagsJSONString);
					if (tempJSONObj.get(QueryIOConstants.HIVETYPEPATH) != null) {
						String tagPath = (String) tempJSONObj.get(QueryIOConstants.HIVETYPEPATH);
						if (!filePath.startsWith(tagPath)) {
							continue;
						}
					}
					if (logicalTagsJSON == null) {
						logicalTagsJSON = (JSONObject) new JSONParser().parse(logicalTagsJSONString);
					} else {

						JSONArray tagsArray = (JSONArray) logicalTagsJSON.get("Tags");
						for (Object tagArr : (JSONArray) tempJSONObj.get("Tags")) {
							tagsArray.add((JSONObject) tagArr);
						}
						if ((logicalTagsJSON.get("ParseDetails") == null)
								&& (tempJSONObj.get("ParseDetails") != null)) {
							logicalTagsJSON.put("ParseDetails", tempJSONObj.get("ParseDetails"));
						}
						if ((logicalTagsJSON.get("Attributes") == null) && (tempJSONObj.get("Attributes") != null)) {
							logicalTagsJSON.put("Attributes", tempJSONObj.get("Attributes"));
						}
					}
				}
			}
		}
		if (logicalTagsJSON != null)
			AppLogger.getLogger().debug("logicalTagsJSON after combine : " + logicalTagsJSON.toJSONString());
		else
			AppLogger.getLogger().debug("logicalTagsJSON is null after combine!!");
		return logicalTagsJSON;
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

	public String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public String getFileNameWithoutExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(0, index);
		} else {
			return fileName;
		}
	}

	public void unzipAndSave(final ZipInputStream stream, final String userName, final String group,
			final String destinationPath, final String zipFileName, final String nameNodeId, final String fsDefaultName,
			final long contentLength, final Configuration conf, final boolean deflate, final String compressionType,
			final String encryptionType, final FileUploadListener fileUploadListener,
			final List<UserDefinedTag> extraTags, final JSONObject tagsJSON) throws Exception {
		Connection connection = null;
		MigrationInfo migrationInfo = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			migrationInfo = addTask(connection, nameNodeId, destinationPath, zipFileName);

			long totalBytes = contentLength;

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Uploading file: " + new Path(destinationPath, zipFileName) + ", size: "
						+ StaticUtilities.getFormattedStorageSize(totalBytes));

			ZipEntry entry = stream.getNextEntry();
			String fileName;

			Collection<Future<?>> futures = new LinkedList<Future<?>>();

			final AtomicLong submittedTasksSize = new AtomicLong(0);
			while (entry != null) {
				fileName = entry.getName();
				if (!entry.isDirectory()) {
					boolean doBuffer = false;
					if ((entry.getSize() > 0) && (entry.getSize() < QueryIOConstants.TEN_MB)
							&& ((submittedTasksSize.get() + entry.getSize()) < QueryIOConstants.HUNDRED_MB)) {
						doBuffer = true;
						submittedTasksSize.addAndGet(entry.getSize());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger()
									.debug("Submitting file " + fileName
											+ " for concurrent processing. New buffer size: "
											+ StaticUtilities.getFormattedStorageSize(submittedTasksSize.get()) + "/"
											+ StaticUtilities.getFormattedStorageSize(QueryIOConstants.HUNDRED_MB));
					} else {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Processing file " + fileName + " sequentially");
					}
					if (doBuffer) {
						byte[] data = IOUtils.toByteArray(stream);
						InputStream bufIn = null;
						try {
							final String fName = fileName;
							bufIn = new ByteArrayInputStream(data);
							final InputStream tempIn = bufIn;
							final ZipEntry tempEntry = entry;
							futures.add(Settings.getThreadPoolExecutor().submit(new Runnable() {
								public void run() {
									try {
										executePutFileRequest(userName, group, destinationPath, fName, nameNodeId,
												fsDefaultName, tempIn, 0, conf, deflate, compressionType,
												encryptionType, fileUploadListener, extraTags, tagsJSON);
										if (AppLogger.getLogger().isDebugEnabled())
											AppLogger.getLogger()
													.debug("Concurrent upload of file " + fName + " successful");
									} catch (Exception e) {
										AppLogger.getLogger().fatal(e.getMessage(), e);
										throw new RuntimeException(e);
									} finally {
										submittedTasksSize.addAndGet(-1 * tempEntry.getSize());
									}
								}
							}));
						} finally {
							if (bufIn != null) {
								try {
									bufIn.close();
								} catch (Exception e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
							}
						}
					} else {
						executePutFileRequest(userName, group, destinationPath, fileName, nameNodeId, fsDefaultName,
								stream, 0, conf, deflate, compressionType, encryptionType, fileUploadListener,
								extraTags, tagsJSON);
					}
				} else {
					executeMkdirsRequest(nameNodeId, fsDefaultName, userName, group,
							new Path(destinationPath, fileName), conf);
				}

				/**
				 * Commenting following update status call as its not required.
				 * We have file upload progress being updated in foreground now.
				 * This is unncessary processing to update status of migration
				 * which is shown in background, which is causing lot of
				 * overhead/
				 */
				// updateStatus(connection, migrationInfo,
				// fileUploadListener.getContentLength(),
				// fileUploadListener.getBytesRead());

				stream.closeEntry();
				entry = stream.getNextEntry();
			}

			// Wait for all tasks to complete for non demo setup.
			if (System.getProperty("querio.demo.setup") == null) {
				for (Future<?> future : futures) {
					future.get();
				}
			}

			updateStatusCompleted(connection, migrationInfo);
		} catch (TagParserException ex) {
			String errorMessage = "Tag processing failed but file uploaded succesfully. Reason: " + ex.getMessage();
			updateStatusCompleted(connection, migrationInfo);
			
		} catch (Exception e) {
			if (migrationInfo != null)
				updateStatusFailed(connection, migrationInfo);
			AppLogger.getLogger().fatal(e.getMessage(), e);

			throw e;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public MigrationInfo addTask(final Connection connection, String namenodeId, String path, String fileName)
			throws Exception {
		MigrationInfo migrationInfo = new MigrationInfo();
		migrationInfo.setSourcePath("N/A");
		migrationInfo.setDestinationPath(path);
		migrationInfo.setNamenodeId(namenodeId);
		migrationInfo.setImportType(true);
		migrationInfo.setTitle("Upload file " + new Path(path, fileName));
		migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
		migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis() - 1));
		migrationInfo.setDataStore(QueryIOConstants.DATASOURCE_LOCAL);
		migrationInfo.setProgress(0);
		migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_INPROGRESS);

		MigrationInfoDAO.insert(connection, migrationInfo);

		migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());

		return migrationInfo;
	}

	public void updateStatus(final Connection connection, MigrationInfo migrationInfo, long totalBytes,
			long writtenBytes) {
		try {
			if (totalBytes != 0)
				migrationInfo.setProgress((writtenBytes * 100 / totalBytes));
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	public void updateStatusCompleted(final Connection connection, MigrationInfo migrationInfo) {
		try {
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setProgress(100);
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	public void updateStatusFailed(final Connection connection, MigrationInfo migrationInfo) {
		try {
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setProgress(0);
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	public void executePutFileRequest(String userName, String group, String destinationPath, String fileName,
			String nameNodeId, String fsDefaultName, InputStream is, long contentLength, Configuration conf,
			boolean deflate, String compressionType, String encryptionType, FileUploadListener fileUploadListener,
			List<UserDefinedTag> extraTags, JSONObject tagsJSON) throws Exception {

		String fileType = StaticUtilities.getFileExtension(fileName);
		Path path = new Path(destinationPath, fileName);
		tagsJSON = getLogicalTagExtensionJSON(fileType.toUpperCase(), path.toString());
		PutFileRequest request = new PutFileRequest(userName, group, path, nameNodeId, fsDefaultName, is, contentLength,
				conf, deflate, compressionType, encryptionType, fileUploadListener, extraTags, tagsJSON);
		request.process();
	}

	public void executeMkdirsRequest(String nameNodeId, String fsDefaultName, String userName, String group, Path path,
			Configuration conf) throws Exception {
		MKDIRRequest request = new MKDIRRequest(nameNodeId, fsDefaultName, userName, group, path, conf);
		request.process();
	}

	public void decompressFile(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}

		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));

		ZipEntry entry = zipIn.getNextEntry();

		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[8192];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}