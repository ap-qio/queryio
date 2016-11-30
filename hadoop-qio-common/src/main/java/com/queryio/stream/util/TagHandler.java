package com.queryio.stream.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.TableConstants;
import com.queryio.plugin.datatags.ExecuteTagParser;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class TagHandler {
	private Configuration conf;
	private String filePath;

	private IDataTagParser tagParser;
	private ExecuteTagParser executor = null;
	private List<UserDefinedTag> externalTags;
	private String compressionType;
	private String encryptionType;
	private static final Log LOG = LogFactory.getLog(TagHandler.class);
	FileSystem dfs;

	private JSONObject tagsJSON;
	private JSONObject tikaTags;
	private JSONObject logicalTags;

	public TagHandler(FileSystem dfs, Configuration conf, String filePath, ArrayList<UserDefinedTag> externalTags,
			String compressionType, String encryptionType, JSONObject tagsJSON) throws IOException {

		this.dfs = dfs;
		this.conf = conf;
		this.filePath = filePath;
		this.externalTags = externalTags;
		this.compressionType = compressionType;
		this.encryptionType = encryptionType;

		this.tagsJSON = tagsJSON;

		separateParserTags();

		FileStatus status = dfs.getFileStatus(new Path(filePath));
		Map<String, String> coreTags = new HashMap<String, String>();
		coreTags.put("FILEPATH", status.getPath().toUri().getPath());

		if (isTagsAvailable(tikaTags)) {
			this.tagsJSON = tikaTags;
		} else if (isTagsAvailable(logicalTags)) {
			this.tagsJSON = logicalTags;
		}

		try {
			tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(this.conf, this.filePath, this.tagsJSON,
					coreTags);
		} catch (Exception e) {
			throw new IOException(e);
		}

		if (tagParser != null) {
			executor = new ExecuteTagParser(filePath, tagParser); // Tika Global
			executor.start();
		}
	}

	private void separateParserTags() {
		if (this.tagsJSON == null || this.tagsJSON.get("Tags") == null) {
			return;
		}
		JSONArray tags = (JSONArray) this.tagsJSON.get("Tags");
		JSONObject currentTag = null;
		String parserType = null;
		try {
			tikaTags = (JSONObject) new JSONParser().parse(tagsJSON.toJSONString());
			logicalTags = (JSONObject) new JSONParser().parse(tagsJSON.toJSONString());
		} catch (ParseException e) {
			LOG.fatal("Error while separation of Tags.", e);
		}
		for (int i = 0; i < tags.size(); i++) {
			currentTag = (JSONObject) tags.get(i);
			parserType = (String) currentTag.get("ParserType");
			try {
				if (parserType.equalsIgnoreCase("TIKA")) {
					removeTagFromLogicalParser(currentTag); // The Tag should be
															// remove from
															// LogicalTags if
															// parser of this
															// tag is of type
															// Tika
				} else if (parserType.equalsIgnoreCase("LOGICAL")) {
					removeTagFromTikaParser(currentTag); // The Tag should be
															// remove from
															// TikaTags if
															// parser of this
															// tag is of type
															// Logical
				}
			} catch (ParseException e) {
				LOG.fatal("Error while separation of Logical and Tika Tags.", e);
			}
		}
	}

	private void removeTagFromTikaParser(JSONObject tagToBeRemoved) throws ParseException {
		if (tikaTags != null) {
			((JSONArray) tikaTags.get("Tags")).remove(tagToBeRemoved);
		}
	}

	private void removeTagFromLogicalParser(JSONObject tagToBeRemoved) throws ParseException {
		if (logicalTags != null) {
			((JSONArray) logicalTags.get("Tags")).remove(tagToBeRemoved);
		}
	}

	private boolean isTagsAvailable(JSONObject tags) {
		if (tags == null) {
			return false;
		}
		JSONArray allTags = (JSONArray) tags.get("Tags");
		if (allTags != null && allTags.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void write(int arg0) throws IOException {
		if (executor != null) {
			executor.write(arg0);
		}
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		if (executor != null) {
			executor.write(buf, off, len);
		}
	}

	public void close() throws IOException {
		// closePos
		if (executor != null)
			executor.closePos();

		// Wait for parser
		LOG.info("Waiting for parser");
		try {
			if (executor != null)
				executor.join();
		} catch (Exception e) {
			throw new IOException(e);
		}

		LOG.info("Parsing procedure complete");

		Connection connection = null;
		try {
			String dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(dbPoolName);
			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbPoolName, null);

			if (connection != null) {
				String tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);

				List<UserDefinedTag> tags = UserDefinedTagUtils.generateDefaultTags(dfs, filePath);

				if (tagParser != null && tagParser.getCustomTagList() != null
						&& tagParser.getCustomTagList().size() > 0) {
					tags.addAll(tagParser.getCustomTagList());
				}

				compressionType = EncryptionHandler.filterCompressionType(compressionType);
				encryptionType = EncryptionHandler.filterEncryptionType(encryptionType);

				tags.add(new UserDefinedTag(EncryptionHandler.COL_COMPRESSION_TYPE, compressionType));
				tags.add(new UserDefinedTag(EncryptionHandler.COL_ENCRYPTION_TYPE, encryptionType));

				if (this.tagsJSON != null && tagParser != null
						&& tableName.equalsIgnoreCase(TableConstants.TABLE_HDFS_METADATA)) {
					String fileExtension = UserDefinedTagUtils.getFileExtension(filePath);
					String fileType = String.valueOf(tagsJSON.get("FileType"));
					if ((fileType != null) && (!fileType.isEmpty())) {
						if (fileType.equalsIgnoreCase(fileExtension))
							tableName = "DATATAGS_" + fileType.toUpperCase();
					} else
						tableName = "DATATAGS_" + fileExtension;
				}

				for (UserDefinedTag tag : tags) {
					LOG.debug("tag.name : " + tag.getKey());
				}

				addClassType(tags);

				UserDefinedTagDAO.insertTagValues(connection, props, tableName, filePath, tags, externalTags, true,
						tagParser == null ? new TableMetadata("DEFAULT", null)
								: tagParser.getTableMetaData(UserDefinedTagUtils.getFileExtension(filePath)));
			}
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOG.fatal(e.getMessage(), e);
				}
			}
		}
	}

	private boolean isParseDetailAvailable() {
		if (this.tagsJSON != null) {
			return this.tagsJSON.get("ParseDetails") != null;
		} else {
			return false;
		}
	}

	private Map<String, Class> getTagsClass() {
		Map<String, Class> tagsClassMap = new HashMap<String, Class>();
		if (this.tagsJSON == null) {
			return tagsClassMap;
		}
		JSONArray tagsArray = (JSONArray) this.tagsJSON.get("Tags");
		JSONObject tag = null;
		String tagName = null;
		String tagClass = null;
		for (int i = 0; i < tagsArray.size(); i++) {
			tag = (JSONObject) tagsArray.get(i);
			tagClass = (String) tag.get("dataType");
			if (tagClass == null) {
				continue;
			}
			tagName = ((String) tag.get("TagName")).toUpperCase();
			tagClass = tagClass.toUpperCase();
			tagsClassMap.put(tagName, MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(tagClass));
		}
		return tagsClassMap;
	}

	private void addClassType(List<UserDefinedTag> tags) {
		Map<String, Class> tagsClassMap = getTagsClass();
		Class tagClass = null;
		for (UserDefinedTag tag : tags) {
			tagClass = tagsClassMap.get(tag.getKey());
			if (tagClass != null) {
				tag.setTagClass(tagClass);
			}
		}
	}
}
