package com.os3.server.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamWriteStatus;
import com.os3.server.data.manager.DataManager;
import com.os3.server.hadoop.DFSManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.os3.server.userinfo.UserInfoContainer;
import com.queryio.common.DFSMap;
import com.queryio.plugin.datatags.UserDefinedTag;

public class PutObjectAction extends BaseAction {

	protected final Logger LOGGER = Logger.getLogger(getClass());

	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> helperMap, int apiType) throws Exception {
		String bucketName = (String) helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		String objectName = (String) helperMap.get(OS3Constants.X_OS3_OBJECT_NAME);
		String requestId = (String) helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String contentLength = request.getHeader(OS3Constants.CONTENT_LENGTH);
		String token = (String) request.getHeader(OS3Constants.AUTHORIZATION);
		String unzip = (String) request.getHeader(OS3Constants.UNZIP);

		LOGGER.debug("PutObjectAction: request received, unzip=" + unzip);

		LOGGER.debug("Bucket: " + bucketName);
		LOGGER.debug("Object: " + objectName);

		boolean deflate = false;
		try {
			deflate = Boolean.parseBoolean(unzip);
		} catch (Exception e) {
			// IGNORE
		}

		LOGGER.debug("Deflate: " + deflate);

		FileSystem dfs = null;

		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, objectName, requestId, apiType);
			return;
		}

		String username = DFSMap.getUserForToken(token);
		if (username == null) {
			ErrorResponseWriter.invalidToken(helperMap, response, objectName, requestId, apiType);
			return;
		}

		dfs = DFSMap.getDFSForUser(username);

		if (contentLength == null) {
			ErrorResponseWriter.missingContentLength(helperMap, response, objectName, requestId, apiType);
			return;
		}

		String group = UserInfoContainer.getDefaultGroupForUser(username);

		String bucketOwner = request.getHeader(OS3Constants.OWNER);
		String bucketGroup = request.getHeader(OS3Constants.GROUP);
		String permission = request.getHeader(OS3Constants.PERMISSION);

		@SuppressWarnings("PMD.AvoidUsingShortType")
		short permissionValue = 0;
		if (permission != null) {
			try {
				permissionValue = Short.parseShort(permission);
			} catch (NumberFormatException e) {
				ErrorResponseWriter.invalidArgument(helperMap, response, bucketName, requestId, apiType);
				return;
			}
		}

		if (bucketOwner == null) {
			bucketOwner = username;
		}
		if (bucketGroup == null) {
			bucketGroup = group;
		}

		long contentLen = Long.parseLong(contentLength);
		String md5Checksum = (String) helperMap.get(OS3Constants.CONTENT_MD5);
		if (!DataManager.doesBucketExists(dfs, bucketName)) {
			ErrorResponseWriter.buketNotfound(helperMap, response, bucketName, requestId, apiType);
		} else {
			StreamWriteStatus status = null;

			if (deflate) {
				ZipInputStream stream = null;
				try {
					stream = new ZipInputStream(request.getInputStream());

					ZipEntry entry = stream.getNextEntry();
					String fileName;
					while (entry != null) {
						fileName = entry.getName();

						if (!entry.isDirectory()) {
							if (permission == null) {
								try {
									status = DataManager.createObject(bucketOwner, bucketGroup, dfs, bucketName,
											objectName.substring(0, objectName.lastIndexOf(File.separator) + 1)
													+ fileName,
											contentLen, stream, getAllTags(request),
											request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_COMPRESSION),
											request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_ENCRYPTION));
								} catch (AccessControlException e) {
									ErrorResponseWriter.permissionDenied(helperMap, response, objectName,
											e.getLocalizedMessage(), requestId, apiType);
									return;
								}
							} else {
								try {
									status = DataManager.createObject(bucketOwner, bucketGroup, permissionValue, dfs,
											bucketName,
											objectName.substring(0, objectName.lastIndexOf(File.separator) + 1)
													+ fileName,
											contentLen, stream, getAllTags(request),
											request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_COMPRESSION),
											request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_ENCRYPTION));
								} catch (AccessControlException e) {
									ErrorResponseWriter.permissionDenied(helperMap, response, objectName,
											e.getMessage(), requestId, apiType);
									return;
								}

							}

						} else {
							try {
								Path path = new Path(DFSManager.ROOT_PATH + bucketName, fileName);
								dfs.mkdirs(path);
								dfs.setOwner(path, bucketOwner, bucketGroup);
								if (permission != null) {
									dfs.setPermission(path, DFSManager.parsePermissions(permissionValue));
								}
							} catch (AccessControlException e) {
								ErrorResponseWriter.permissionDenied(helperMap, response, objectName, e.getMessage(),
										requestId, apiType);
								return;
							}
						}

						stream.closeEntry();
						entry = stream.getNextEntry();
					}
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (Exception e) {
							LOGGER.fatal(e.getMessage(), e);
						}
					}
				}
			} else {
				InputStream stream = null;
				try {
					stream = request.getInputStream();
					if (permission == null) {
						try {
							status = DataManager.createObject(bucketOwner, bucketGroup, dfs, bucketName, objectName,
									contentLen, new BufferedInputStream(stream), getAllTags(request),
									request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_COMPRESSION),
									request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_ENCRYPTION));
						} catch (AccessControlException e) {
							ErrorResponseWriter.permissionDenied(helperMap, response, objectName,
									e.getLocalizedMessage(), requestId, apiType);
							return;
						}
					} else {
						try {
							status = DataManager.createObject(bucketOwner, bucketGroup, permissionValue, dfs,
									bucketName, objectName, contentLen, new BufferedInputStream(stream),
									getAllTags(request),
									request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_COMPRESSION),
									request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_ENCRYPTION));
						} catch (AccessControlException e) {
							ErrorResponseWriter.permissionDenied(helperMap, response, objectName, e.getMessage(),
									requestId, apiType);
							return;
						}
					}
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (Exception e) {
							LOGGER.fatal(e.getMessage(), e);
						}
					}
				}
			}

			// Did we write all the bytes from request data?

			if (!deflate) {
				if (contentLen != status.getSize()) {
					// Delete whatever we wrote and send incomplete body
					// response to caller
					DataManager.deleteObject(dfs, bucketName, objectName);
					ErrorResponseWriter.incompleteBody(helperMap, response, objectName, requestId, apiType);

					return;
				} else {
					// We wrote all data fine, do integrity check if asked for

					// If Integrity check failed, return bad digest!!
					if (md5Checksum != null && !md5Checksum.equals(status.getCheckSum())) {
						ErrorResponseWriter.badDigest(helperMap, response, objectName, requestId, apiType);
						return;
					}

					ResponseWriter.writePutObjectResponse(apiType, response, status.getCheckSum(), requestId);
				}
			} else {
				ResponseWriter.writePutObjectResponse(apiType, response, "", requestId);
			}
		}
	}

	public List<UserDefinedTag> getAllTags(HttpServletRequest request) {
		Enumeration enames;
		String tagName;
		List<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();
		enames = request.getHeaderNames();
		while (enames.hasMoreElements()) {
			tagName = (String) enames.nextElement();
			if (tagName.startsWith(OS3Constants.META_TAG_PREFIX)) {
				tags.add(new UserDefinedTag(tagName, request.getHeader(tagName)));
			}
		}
		return tags;
	}
}