package com.queryio.demo.mr.generic.parser;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.hadoop.mapreduce.Mapper;
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

public class DataTagMapper extends Mapper<List<FileStatus>, List<InputStream>, FileStatus, TagEntry> {
	private static final Log LOG = LogFactory.getLog(DataTagMapper.class);

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

	@Override
	public void map(List<FileStatus> fileStatuses, List<InputStream> streams, final Context context)
			throws IOException, InterruptedException {
		try {
			LOG.info("Initializing.");

			Configuration conf = context.getConfiguration();

			LOG.info("Initializing Connection.");
			UserDefinedTagResourceFactory.initConnectionPool(conf, true);
			LOG.info("Connection Initialized");

			String tagsJSONString = conf.get("tagsJSON");
			LOG.info("tagsJSONString : " + tagsJSONString);
			JSONParser parser = new JSONParser();
			JSONObject tagJSON = null;
			if (tagsJSONString != null && !tagsJSONString.trim().isEmpty()) {
				tagJSON = (JSONObject) parser.parse(tagsJSONString);
				LOG.info("tagJSON : " + tagJSON.toJSONString());
			}

			String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
			JSONObject fileTypeParsers = new JSONObject();
			if (fpJSON != null) {
				fileTypeParsers = (JSONObject) parser.parse(fpJSON);
				LOG.info("fileTypeParsers : " + fileTypeParsers.toJSONString());
			}

			long startTime = conf.getLong("startTime", 0);
			long endTime = conf.getLong("endTime", System.currentTimeMillis());

			String dbName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);

			boolean callTikaParser = Boolean.parseBoolean(String.valueOf(conf.get("callTikaParser")));

			FileStatus fileStatus = null;
			InputStream stream = null;
			ArrayList<FileStatus> fileStatusList = null;
			ArrayList<InputStream> streamList = null;

			int maxThreadCount = conf.getInt(QueryIOConstants.QUERYIO_THREAD_COUNT_MAX, 50);
			LOG.info("maxThreadCount: " + maxThreadCount);

			LOG.info(
					"Total Threads fileStatuses.size(): " + fileStatuses.size() + "	streams.size(): " + streams.size());
			LOG.info("Calculating files per threads");
			List<Thread> threads = new ArrayList<Thread>();
			int totalFiles = fileStatuses.size();
			int filesPerThread = 1;
			if (totalFiles <= maxThreadCount) {
				maxThreadCount = totalFiles;
			} else {
				filesPerThread = (int) Math.ceil((totalFiles * 1.0) / maxThreadCount);
			}

			if (filesPerThread < 0)
				throw new Exception("Files Per Thread accounts to a negative number. Total Files: " + totalFiles
						+ " max Thread Count: " + maxThreadCount);

			int remainder = totalFiles % maxThreadCount;
			int countFactor = 1;
			LOG.info("Running Threads");
			long savePt = System.currentTimeMillis();

			for (int i = 0; i < maxThreadCount; i++) {
				fileStatusList = new ArrayList<FileStatus>();
				streamList = new ArrayList<InputStream>();

				if ((remainder > 0) && (i >= remainder))
					countFactor = filesPerThread - 1;
				else
					countFactor = filesPerThread;

				for (int j = 0; j < countFactor; j++) {
					fileStatus = fileStatuses.get((j * maxThreadCount) + i);

					if (fileStatus.getModificationTime() < startTime || fileStatus.getModificationTime() > endTime) {
						continue;
					}

					fileStatusList.add(fileStatus);
					stream = streams.get((j * maxThreadCount) + i);
					streamList.add(stream);
				}

				LOG.info("Thread Count Created: " + i);
				GenericParserThread thread = new GenericParserThread(dbName, conf, fileStatusList, streamList, tagJSON,
						props, fileTypeParsers, callTikaParser);
				thread.start();
				threads.add(thread);
			}
			LOG.info("Waiting for all thread Threads");
			for (Thread t : threads) {
				t.join();
			}
			LOG.info("Total time taken by threads: " + (System.currentTimeMillis() - savePt) + " ms");
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		} finally {
			try {
				UserDefinedTagResourceFactory.removeConnectionPool(context.getConfiguration(), true);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
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
			LOG.info("tagName : " + tagName);
			LOG.info("tagClass : " + tagClass);
			tagsClassMap.put(tagName, MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(tagClass));
		}
		return tagsClassMap;
	}

	private void addClassType(List<UserDefinedTag> tags, JSONObject tagsJSON) {
		LOG.info("tagsJSON : " + tagsJSON.toJSONString());
		Map<String, Class> tagsClassMap = getTagsClass(tagsJSON);
		Class tagClass = null;
		for (UserDefinedTag tag : tags) {
			LOG.info("tag.getKey() : " + tag.getKey().toUpperCase());
			tagClass = tagsClassMap.get(tag.getKey().toUpperCase());
			if (tagClass != null) {
				tag.setTagClass(tagClass);
			}
			LOG.info("TagClass after set: ");
			LOG.info(tag.getTagClass());
		}
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

		boolean tagEntryExists = tagEntryExists(connection, tableName, filePath);
		LOG.info("tagEntryExists : " + tagEntryExists);
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
								LOG.info("Loc 1");
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag.getTagClass(), props));
							} else if (tag.getValue() != null) {
								LOG.info("Loc 2");
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag.getValue().getClass(), props));
							} else {
								LOG.info("Loc 3");
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(String.class, props));
							}
						} catch (Exception e) {
							LOG.fatal(e.getMessage(), e);
							continue;
						}
					}
				}
				LOG.info("Loc 4");
				colNames.add(columnName);
				LOG.info("Loc 5");
				Object tagValuesCasted = castTagValueToClassType(tag);
				LOG.info("Loc 6");
				LOG.info("tagValuesCasted == null : " + (tagValuesCasted == null));
				tagValueObjs.add(tagValuesCasted);
			}
		}
		connection.setAutoCommit(false);

		if (!tableName.equals(TABLE_HDFS_METADATA)) {
			deleteFromDefaultTable(connection, filePath);
		}

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
				// if(colNames.size()>0) {
				// QUERY.append(", ");
				// }
				// QUERY.append(TableMetadata.DEFAULT_TAG_FILEPATH);//
				// UserDefinedTagUtils.DEFAULT_TAG_FILEPATH
				query.append(") VALUES (");
				query.append(valueStr);
				// if (colNames.size() != 0) {
				// QUERY.append(", ");
				// }
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
				} else if (tagValueObj instanceof Timestamp) {
					stmt.setTimestamp(i++, (Timestamp) tagValueObj);
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

	private static Object castTagValueToClassType(UserDefinedTag tag) {
		Object obj = null;
		try {
			Class tagClass = tag.getTagClass();
			LOG.info("tagClass : " + tagClass);
			if (tagClass != null) {
				if (tagClass.getName().equalsIgnoreCase("java.lang.String")) {
					LOG.info("String : ");
					obj = tag.getValue();
				}
				if (tagClass.getName().equalsIgnoreCase("java.lang.Integer") && tag.getValue() instanceof Double) {
					obj = ((Double) tag.getValue()).intValue();
				} else {
					LOG.info("non String ");
					Method method = tagClass.getMethod("valueOf", String.class);
					obj = method.invoke(tagClass, String.valueOf(tag.getValue()));
				}
			} else {
				obj = tag.getValue();
				LOG.info("tag.getValue() : " + tag.getValue());
			}
		} catch (Exception e) {
			LOG.fatal("Could not cast tag value to corresponding class." + tag.getValue(), e);
		}
		return obj;
	}

	class GenericParserThread extends Thread {
		String dbName;
		Configuration conf;
		ArrayList<FileStatus> fileStatusList;
		ArrayList<InputStream> streamList;
		DBTypeProperties props;
		String tableName;
		int maxBatchSize;
		JSONObject tagInfo = null;
		JSONObject fileTypeParsers = null;
		boolean callTikaParser = true;
		private IDataTagParser tagParser = null;

		GenericParserThread(String dbName, Configuration conf, ArrayList<FileStatus> fileStatusList,
				ArrayList<InputStream> streamList, JSONObject tagsJSON, DBTypeProperties props,
				JSONObject fileTypeParsers, boolean callTikaParser) {
			this.dbName = dbName;
			this.conf = conf;
			this.fileStatusList = fileStatusList;
			this.streamList = streamList;
			this.tagInfo = tagsJSON;
			this.props = props;
			this.fileTypeParsers = fileTypeParsers;
			this.callTikaParser = callTikaParser;
		}

		public void run() {

			Connection connection = null;
			try {
				connection = UserDefinedTagResourceFactory.getConnection(dbName, true);
				FileStatus fileStatus = null;
				InputStream stream = null;

				LOG.info("fileStatusList.size: " + fileStatusList.size());
				for (int i = 0; i < fileStatusList.size(); i++) {
					fileStatus = fileStatusList.get(i);
					stream = streamList.get(i);

					try {
						LOG.info("FileName: " + fileStatus.getPath());

						// String filePath = "/" +
						// fileStatus.getPath().getName();
						String filePath = fileStatus.getPath().toUri().getPath();

						String fileExtension = UserDefinedTagUtils.getFileExtension(filePath);

						LOG.info("filePath : " + filePath);
						LOG.info("fileExtension : " + fileExtension);

						List<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();

						Map<String, String> coreMap = new HashMap<String, String>();
						coreMap.put(ColumnConstants.COL_TAG_VALUES_FILEPATH, fileStatus.getPath().toUri().getPath());
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_FILEPATH,
						// fileStatus.getPath().toUri().getPath()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_ACCESSTIME,
								String.valueOf(fileStatus.getAccessTime()));
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_ACCESSTIME,
						// new Timestamp(fileStatus.getAccessTime())));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE,
								String.valueOf(fileStatus.getBlockSize()));
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE,
						// fileStatus.getBlockSize()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_LENGTH, String.valueOf(fileStatus.getLen()));
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_LENGTH,
						// fileStatus.getLen()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME,
								String.valueOf(fileStatus.getModificationTime()));
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME,
						// new Timestamp(fileStatus.getModificationTime())));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_OWNER, fileStatus.getOwner());
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_OWNER,
						// fileStatus.getOwner()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_PERMISSION, fileStatus.getPermission().toString());
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_PERMISSION,
						// fileStatus.getPermission().toString()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_REPLICATION,
								String.valueOf(fileStatus.getReplication()));
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_REPLICATION,
						// fileStatus.getReplication()));
						coreMap.put(ColumnConstants.COL_TAG_VALUES_USERGROUP, fileStatus.getGroup());
						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_USERGROUP,
						// fileStatus.getGroup()));

						// tags.add(new
						// UserDefinedTag(ColumnConstants.COL_TAG_VALUES_BLOCKS,
						// fileStatus.getBlocks()));

						// Map<String, Object> metadata =
						// getObjectMetadata(connection, filePath, tableName);

						tagParser = UserDefinedTagResourceFactory.getParserDataTaggingJob(fileExtension,
								fileTypeParsers, conf, tagInfo, coreMap);
						if (tagParser != null) {
							tagParser.parseStream(stream, fileExtension);
						}

						// ExecuteTagParser tagExecutor = new
						// ExecuteTagParser(filePath, tagParser, tagInfo);
						// ExecuteLogicalTagParser logicalTagExecutor = new
						// ExecuteLogicalTagParser(filePath, tagInfo);

						// final byte[] readBuffer = new byte[8192];
						// while(stream.read(readBuffer, 0, readBuffer.length)
						// != -1) {
						// tagExecutor.write(readBuffer, 0, readBuffer.length);
						// logicalTagExecutor.write(readBuffer, 0,
						// readBuffer.length);
						// }
						//
						// tagExecutor.join();
						// logicalTagExecutor.join();

						LOG.info("FileName: " + fileStatus.getPath() + " parsed");
						LOG.info("tagParser: " + tagParser);

						if (tagParser != null && tagParser.getCustomTagList() != null
								&& tagParser.getCustomTagList().size() > 0) {
							tags.addAll(tagParser.getCustomTagList());
						}

						if (this.tagInfo != null) {
							// String fileType =
							// String.valueOf(tagInfo.get("FileType"));
							// LOG.info("fileType : " + fileType);
							// if ((fileType != null) && (!fileType.isEmpty()))
							// {
							// if (fileType.equalsIgnoreCase(fileExtension))
							// tableName = "DATATAGS_" + fileType.toUpperCase();
							// }
							// else
							// tableName = "DATATAGS_" + fileExtension;
						}

						tableName = "DATATAGS_" + fileExtension.toUpperCase();

						LOG.info("tableName: " + tableName);
						LOG.info("allTags: " + tags);

						addClassType(tags, tagInfo);

						// for (UserDefinedTag tag : tags) {
						// LOG.info("Tag: " + tag.getKey() + " > " +
						// tag.getValue() + " (" + tag.getValue().getClass() +
						// ") " + ", " + tag.getTagClass() );
						// }

						insertTagValues(connection, props, tableName, filePath, tags, true, fileStatus);
					} catch (Exception e) {
						LOG.fatal("Exception in parsing file " + fileStatus.getPath() + ": " + e.getLocalizedMessage(),
								e);
					}
				}
			} catch (Exception e) {
				LOG.fatal(e.getLocalizedMessage(), e);
			} finally {
				try {
					InputStream stream = null;
					for (int i = 0; i < streamList.size(); i++) {
						stream = streamList.get(i);
						try {
							if (stream != null)
								stream.close();
						} catch (Exception e) {
							LOG.fatal("Error closing stream.", e);
						}
					}
				} catch (Exception e) {
					LOG.fatal("Exception: ", e);
				}
				try {
					if ((connection != null) && (!connection.isClosed())) {
						try {
							if (!connection.getAutoCommit())
								connection.commit();
						} catch (SQLException e) {
							LOG.fatal(e.getMessage(), e);
						}
						try {
							connection.close();
						} catch (SQLException e) {
							LOG.fatal(e.getMessage(), e);
						}
					}
				} catch (SQLException e) {
					LOG.fatal(e.getMessage(), e);
				}
			}

		}
	}
}