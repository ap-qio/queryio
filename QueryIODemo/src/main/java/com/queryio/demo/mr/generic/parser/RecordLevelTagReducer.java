package com.queryio.demo.mr.generic.parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class RecordLevelTagReducer extends Reducer<Text, TagDataBean, NullWritable, NullWritable> {

	private static final Log LOG = LogFactory.getLog(RecordLevelTagReducer.class);

	private static final String TABLE_HDFS_METADATA = "HDFS_METADATA";

	public static final String COL_COMPRESSION_TYPE = "COMPRESSION_TYPE";
	public static final String COL_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
	public static final String COL_TAG_VALUES_BLOCKS = "BLOCKS";

	public static final String SNAPPY = "SNAPPY";
	public static final String GZ = "GZ";
	public static final String LZ4 = "LZ4";
	public static final String NONE = "NONE";
	public static final String AES256 = "AES256";

	private static final String QUERY_START = "SELECT " + COL_COMPRESSION_TYPE + "," + COL_ENCRYPTION_TYPE + ","
			+ COL_TAG_VALUES_BLOCKS + " FROM ";
	private static final String QUERY_END = " WHERE FILEPATH=?";

	private static JSONObject tagJSON = null;
	private static JSONObject fileTypeParsers = new JSONObject();

	@Override
	protected void setup(Reducer<Text, TagDataBean, NullWritable, NullWritable>.Context context)
			throws IOException, InterruptedException {
		try {
			super.setup(context);

			Configuration conf = context.getConfiguration();
			UserDefinedTagResourceFactory.initConnectionPool(conf, true);

			String tagsJSONString = conf.get("tagsJSON");
			LOG.info("tagsJSONString : " + tagsJSONString);
			JSONParser parser = new JSONParser();
			if (tagsJSONString != null && !tagsJSONString.trim().isEmpty()) {
				tagJSON = (JSONObject) parser.parse(tagsJSONString);
				LOG.info("tagJSON : " + tagJSON.toJSONString());
			}

			String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
			if (fpJSON != null) {
				fileTypeParsers = (JSONObject) parser.parse(fpJSON);
				LOG.info("fileTypeParsers : " + fileTypeParsers.toJSONString());
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		}
	}

	@Override
	protected void reduce(Text filePath, Iterable<TagDataBean> value,
			Reducer<Text, TagDataBean, NullWritable, NullWritable>.Context context)
			throws IOException, InterruptedException {
		Connection connection = null;
		try {
			Configuration conf = context.getConfiguration();

			String dbName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = UserDefinedTagResourceFactory.getConnection(dbName, true);
			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);

			String fileExtension = UserDefinedTagUtils.getFileExtension(filePath.toString());

			String tableName = "DATATAGS_" + fileExtension.toUpperCase();

			// LOG.info("filePath : " + filePath);
			// LOG.info("fileExtension : " + fileExtension);

			FileSystem fs = FileSystem.get(conf);
			FileStatus fileStatus = fs.getFileStatus(new Path(filePath.toString()));

			Map<String, String> coreMap = new HashMap<String, String>();
			//
			IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserDataTaggingJob(fileExtension,
					fileTypeParsers, conf, tagJSON, coreMap);
			if (tagParser != null) {
				long ts = System.currentTimeMillis();
				for (TagDataBean tdb : value) {
					if (tdb.isError())
						return;

					List<UserDefinedTag> tags = tdb.getTags();

					for (UserDefinedTag tag : tags) {
						String valueOf = String.valueOf(tag.getValue());
						// LOG.info(filePath + " > " + valueOf);
						tagParser.parseTagData(tag.getKey(), valueOf, true);
					}
				}
				LOG.info("Time for processing tags : " + (System.currentTimeMillis() - ts));
				List<UserDefinedTag> finalTags = new ArrayList<UserDefinedTag>();
				if (tagParser != null && tagParser.getCustomTagList() != null
						&& tagParser.getCustomTagList().size() > 0) {
					finalTags.addAll(tagParser.getCustomTagList());
					addClassType(finalTags, tagJSON);
				}

				LOG.info("finalTags : " + finalTags.size());

				for (UserDefinedTag tag : finalTags) {
					LOG.info("Tag: " + tag.getKey() + " = " + tag.getValue() + " (" + tag.getValue().getClass() + ") "
							+ ", " + tag.getTagClass());
				}
				ts = System.currentTimeMillis();
				insertTagValues(connection, props, tableName, filePath.toString(), finalTags, true, fileStatus);
				LOG.info("insertTag time : " + (System.currentTimeMillis() - ts));
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		}
	}

	private void addClassType(List<UserDefinedTag> tags, JSONObject tagsJSON) {
		// LOG.info("tagsJSON : " + tagsJSON.toJSONString());
		Map<String, Class> tagsClassMap = getTagsClass(tagsJSON);
		Class tagClass = null;
		for (UserDefinedTag tag : tags) {
			// LOG.info("tag.getKey() : " + tag.getKey().toUpperCase());
			tagClass = tagsClassMap.get(tag.getKey().toUpperCase());
			if (tagClass != null) {
				tag.setTagClass(tagClass);
			}
			// LOG.info("TagClass after set: ");
			// LOG.info(tag.getTagClass());
		}
	}

	private Map<String, Class> getTagsClass(JSONObject tagsJSON) {
		Map<String, Class> tagsClassMap = new HashMap<String, Class>();
		if (tagsJSON == null) {
			return tagsClassMap;
		}
		JSONArray tagsArray = (JSONArray) tagsJSON.get("Tags");
		JSONObject tag = null;
		String tagName = null;
		String tagClass = null;
		for (int i = 0; i < tagsArray.size(); i++) {
			tag = (JSONObject) tagsArray.get(i);
			tagClass = (String) tag.get("dataType");
			if (tagClass == null) {
				LOG.info("Tag class is null");
				continue;
			}
			tagName = ((String) tag.get("TagName")).toUpperCase();
			tagClass = tagClass.toUpperCase();
			// LOG.info("tagName : " + tagName);
			// LOG.info("tagClass : " + tagClass);
			tagsClassMap.put(tagName, MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(tagClass));
		}
		return tagsClassMap;
	}

	private static void insertTagValues(Connection connection, DBTypeProperties props, String tableName,
			String filePath, List<UserDefinedTag> tags, final boolean updateDbSchema, FileStatus fileStatus)
			throws SQLException {

		if (tableName == null) {
			return;
		}

		tableName = tableName.replaceAll("[^a-zA-Z0-9]+", "_");
		tableName = tableName.replace("-", "_");
		tableName = tableName.replace(".", "_");
		tableName = tableName.replace(" ", "_");
		tableName = tableName.toUpperCase();

		Map<String, Boolean> map = null;

		List<String> colNames = new ArrayList<String>();
		List<Object> tagValueObjs = new ArrayList<Object>();

		if (map == null)
			map = getAllColumns(connection, tableName);

		boolean tagEntryExists = tagEntryExists(connection, tableName, filePath.trim());
		LOG.info("tagEntryExists : " + tagEntryExists + " : " + filePath);
		if (!tagEntryExists) {
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_FILEPATH,
					fileStatus.getPath().toUri().getPath()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_ACCESSTIME,
					new Timestamp(fileStatus.getAccessTime())));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE, fileStatus.getBlockSize()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_LENGTH, fileStatus.getLen()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME,
					new Timestamp(fileStatus.getModificationTime())));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_OWNER, fileStatus.getOwner()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_PERMISSION,
					fileStatus.getPermission().toString()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_REPLICATION, fileStatus.getReplication()));
			tags.add(new UserDefinedTag(ColumnConstants.COL_TAG_VALUES_USERGROUP, fileStatus.getGroup()));

			// tags.add(new
			// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_BLOCKS,
			// fileStatus.getBlocks()));

			try {
				Map<String, Object> metadata = getObjectMetadata(connection, filePath, TABLE_HDFS_METADATA);
				if (metadata != null) {
					tags.add(new UserDefinedTag(COL_COMPRESSION_TYPE,
							filterCompressionType(String.valueOf(metadata.get(COL_COMPRESSION_TYPE)))));
					tags.add(new UserDefinedTag(COL_ENCRYPTION_TYPE,
							filterEncryptionType(String.valueOf(metadata.get(COL_ENCRYPTION_TYPE)))));
					tags.add(new UserDefinedTag(COL_TAG_VALUES_BLOCKS, metadata.get(COL_TAG_VALUES_BLOCKS)));
				}
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);

				tags.add(new UserDefinedTag(COL_COMPRESSION_TYPE, filterCompressionType("")));
				tags.add(new UserDefinedTag(COL_ENCRYPTION_TYPE, filterEncryptionType("")));
				tags.add(new UserDefinedTag(COL_TAG_VALUES_BLOCKS, null));
			}
		}

		for (UserDefinedTag tag : tags) {
			String columnName = tag.getKey().toUpperCase();

			columnName = columnName.replaceAll("[^a-zA-Z0-9]+", "_");
			columnName = columnName.replace("-", "_");
			columnName = columnName.replace(".", "_");
			columnName = columnName.replace(" ", "_");
			columnName = columnName.toUpperCase();

			if (!colNames.contains(columnName)) {
				if (updateDbSchema) {
					if (map.get(columnName) == null) {
						try {
							if (tag.getClass() != null) {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag.getTagClass(), props));
							} else if (tag.getValue() != null) {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag.getValue().getClass(), props));
							} else {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(String.class, props));
							}
						} catch (Exception e) {
							LOG.fatal(e.getMessage(), e);
							continue;
						}
					}
				}
				colNames.add(columnName);
				Object tagValuesCasted = castTagValueToClassType(tag);
				LOG.info("tagValuesCasted == null : " + (tagValuesCasted == null));
				tagValueObjs.add(tagValuesCasted);
			}
		}
		connection.setAutoCommit(false);

		if (!tableName.equals(TABLE_HDFS_METADATA)) {
			deleteFromDefaultTable(connection, filePath);
		}
		LOG.info("colNames: " + colNames);
		PreparedStatement stmt = null;
		try {

			StringBuffer query = new StringBuffer();
			if (!tagEntryExists) {

				StringBuffer colNmStr = new StringBuffer();
				StringBuffer valueStr = new StringBuffer();
				for (int i = 0; i < colNames.size(); i++) {
					if (i != 0) {
						colNmStr.append(", ");
						valueStr.append(", ");
					}
					colNmStr.append(colNames.get(i));
					valueStr.append("?");
				}
				query.append("INSERT INTO ");
				query.append(tableName).append(" (");
				query.append(colNmStr);
				query.append(") VALUES (");
				query.append(valueStr);
				query.append(")");

			} else {
				query.append("UPDATE ");
				query.append(tableName);
				query.append(" SET ");
				for (int i = 0; i < colNames.size(); i++) {
					if (i != 0)
						query.append(", ");
					query.append(colNames.get(i));
					query.append(" = ?");
				}
				query.append(" WHERE ");
				query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
				query.append(" =? ");

			}
			LOG.info("query : " + query);
			LOG.info("FilePath : " + filePath);
			stmt = DatabaseFunctions.getPreparedStatement(connection, query.toString());

			int i = 1;
			for (Object tagValueObj : tagValueObjs) {
				if (tagValueObj instanceof byte[]) {
					byte[] arr = (byte[]) tagValueObj;
					stmt.setBytes(i++, arr);
				} else {
					stmt.setObject(i++, tagValueObj);
				}

			}
			if (tagEntryExists) {
				stmt.setString(i++, filePath);
			}

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		connection.commit();

	}

	public static Map<String, Object> getObjectMetadata(Connection connection, String filePath, String tableName)
			throws Exception {
		Map<String, Object> map = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		try {
			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (res.next()) {
				if (res.getString("TABLE_NAME").toUpperCase().startsWith(tableName)) {
					stmt = connection.prepareStatement(QUERY_START + res.getString("TABLE_NAME") + QUERY_END);

					stmt.setString(1, filePath);

					rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
					ResultSetMetaData rsmd = rs.getMetaData();

					if (rs.next()) {
						map = new HashMap<String, Object>();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							map.put(rsmd.getColumnName(i).toUpperCase(), rs.getObject(rsmd.getColumnName(i)));
						}
						return map;
					}
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(res);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return map;
	}

	public static Map<String, Boolean> getAllColumns(Connection connection, String tableName) throws SQLException {

		Map<String, Boolean> result = new HashMap<String, Boolean>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + tableName + " LIMIT 0";

			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			ResultSetMetaData meta = rs.getMetaData();
			int numCol = meta.getColumnCount();

			for (int i = 1; i <= numCol; i++) {

				result.put(meta.getColumnName(i).toUpperCase(), true);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		return result;
	}

	private static boolean tagEntryExists(Connection connection, String tableName, String filePath)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("SELECT " + TableMetadata.DEFAULT_TAG_FILEPATH + " FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" ='" + filePath + "' ");

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = stmt.executeQuery(query.toString());
			if (rs.next())
				return true;
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(stmt, rs);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		return false;
	}

	public static String filterCompressionType(String type) {
		if (LZ4.equals(type)) {
			return LZ4;
		} else if (SNAPPY.equals(type)) {
			return SNAPPY;
		} else if (GZ.equals(type)) {
			return GZ;
		} else {
			return NONE;
		}
	}

	public static String filterEncryptionType(String type) {
		if (AES256.equals(type)) {
			return AES256;
		} else {
			return NONE;
		}
	}

	public static void addColumn(final Connection connection, final String tableName, final String columnName,
			final String type) throws Exception {
		Statement stmt = null;
		try {
			String alterStmt = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + type;

			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, alterStmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	private static Object castTagValueToClassType(UserDefinedTag tag) {
		Object obj = null;
		try {
			Class tagClass = tag.getTagClass();
			LOG.info("tagClass : " + tagClass);
			if (tagClass != null) {
				if (tagClass.getName().equalsIgnoreCase("java.lang.String")) {
					obj = tag.getValue();
				}
				if (tagClass.getName().equalsIgnoreCase("java.lang.Integer") && tag.getValue() instanceof Double) {
					obj = ((Double) tag.getValue()).intValue();
				} else {
					Method method = tagClass.getMethod("valueOf", String.class);
					obj = method.invoke(tagClass, String.valueOf(tag.getValue()));
				}
			} else {
				obj = tag.getValue();
				LOG.info("tag.getValue() : " + tag.getValue());
			}
		} catch (Exception e) {
			LOG.fatal("Could not cast tag value to corresponding class.", e);
		}
		return obj;
	}

	private static void deleteFromTable(Connection connection, String tableName, String filePath) throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("DELETE FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" LIKE '" + filePath + "%' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception : ", e);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteFromDefaultTable(Connection connection, String filePath) throws SQLException {
		deleteFromTable(connection, TableConstants.TABLE_HDFS_METADATA, filePath);
	}

}
