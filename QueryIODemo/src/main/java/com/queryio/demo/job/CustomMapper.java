package com.queryio.demo.job;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapreduce.Mapper;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.demo.common.CustomQIODFSInputStream;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class CustomMapper extends Mapper<List<FileStatus>, List<InputStream>, FileStatus, DBEntry> {
	private static final Log LOG = LogFactory.getLog(CustomMapper.class);

	public void map(List<FileStatus> fileStatuses, List<InputStream> streams, Context context)
			throws IOException, InterruptedException {
		try {
			UserDefinedTagResourceFactory.initConnectionPool(context.getConfiguration(), true);

			int maxThreadCount = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_THREAD_COUNT_MAX, 50);
			LOG.info("maxThreadCount: " + maxThreadCount);

			LOG.info(
					"Total Threads fileStatuses.size(): " + fileStatuses.size() + "	streams.size(): " + streams.size());
			List<ParserThread> threads = new ArrayList<ParserThread>();

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

			ArrayList<FileStatus> fileStatusList = null;
			FileStatus fileStatus = null;
			ArrayList<InputStream> streamList = null;
			InputStream stream = null;

			int remainder = totalFiles % maxThreadCount;
			int countFactor = 1;
			for (int i = 0; i < maxThreadCount; i++) {
				fileStatusList = new ArrayList<FileStatus>();
				streamList = new ArrayList<InputStream>();

				if ((remainder > 0) && (i >= remainder))
					countFactor = filesPerThread - 1;
				else
					countFactor = filesPerThread;

				for (int j = 0; j < countFactor; j++) {
					fileStatus = fileStatuses.get((j * maxThreadCount) + i);
					fileStatusList.add(fileStatus);
					stream = streams.get((j * maxThreadCount) + i);
					streamList.add(stream);
				}

				LOG.info("Thread Count Created: " + i);
				ParserThread thread = new ParserThread(context, fileStatusList, streamList);
				thread.start();
				threads.add(thread);
			}

			for (ParserThread thread : threads) {
				thread.join();
			}

			for (ParserThread thread : threads) {
				if (!thread.isSuccessful) {
					throw new IOException(thread.e);
				}
			}
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			try {
				UserDefinedTagResourceFactory.removeConnectionPool(context.getConfiguration(), true);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	class ParserThread extends Thread {
		Context context;
		ArrayList<FileStatus> fileStatusList = null;
		ArrayList<InputStream> streamList = null;
		boolean isSuccessful = false;
		Exception e = null;

		ParserThread(Context context, ArrayList<FileStatus> fileStatusList, ArrayList<InputStream> streamList) {
			this.context = context;
			this.fileStatusList = fileStatusList;
			this.streamList = streamList;
		}

		public void run() {
			Connection connection = null;
			String dbName = "";
			try {
				connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(context.getConfiguration(), true);
				dbName = context.getConfiguration().get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);

				FileStatus fileStatus = null;
				InputStream stream = null;
				LOG.info("fileStatusList.size: " + fileStatusList.size());
				for (int i = 0; i < fileStatusList.size(); i++) {
					fileStatus = fileStatusList.get(i);
					stream = streamList.get(i);

					String filePath = fileStatus.getPath().toUri().getPath();

					try {
						IDataTagParser parser = UserDefinedTagResourceFactory
								.getParserFromConstructor(context.getConfiguration(), filePath, null, null);

						List<UserDefinedTag> tags = UserDefinedTagUtils.generateDefaultTags(fileStatus);
						CustomQIODFSInputStream customStream = (CustomQIODFSInputStream) stream;
						if (customStream.getCompressionType() != null && !customStream.getCompressionType().isEmpty())
							tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_COMPRESSION_TYPE,
									customStream.getCompressionType()));
						if (customStream.getEncryptionType() != null && !customStream.getEncryptionType().isEmpty())
							tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_ENCRYPTION_TYPE,
									customStream.getEncryptionType()));
						if (parser != null) {
							LOG.info("Using Parser: " + parser.getClass() + " for file: " + filePath);
							// String expression =
							// context.getConfiguration().get(QueryIOConstants.CUSTOM_TAG_PARSER_EXPRESSION);
							// if (expression != null) {
							// parser.setFilterExpression(expression);
							// }
							parser.parseStream(stream, UserDefinedTagUtils.getFileExtension(filePath));
							if (parser.getCustomTagList() != null && parser.getCustomTagList().size() > 0) {
								tags.addAll(parser.getCustomTagList());
							}
						}
						String tableName = UserDefinedTagResourceFactory.getTableName(parser, filePath);
						DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);
						UserDefinedTagDAO.insertTagValues(connection, props, tableName, filePath, tags, null,
								parser == null ? false : parser.updateDbSchema(),
								parser == null ? new TableMetadata("DEFAULT", null)
										: parser.getTableMetaData(UserDefinedTagUtils.getFileExtension(filePath)));
						LOG.info("Done with file: " + fileStatus.getPath().toUri().getPath());
						isSuccessful = true;
					} catch (Exception e) {
						LOG.fatal("Exception in parsing file " + filePath + ": " + e.getLocalizedMessage(), e);
						isSuccessful = false;
						this.e = new Exception(e);
					}
				}
			} catch (Throwable e) {
				LOG.fatal(e.getLocalizedMessage(), e);
				this.e = new Exception(e);
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
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						LOG.fatal(e.getLocalizedMessage(), e);
					}
				}
			}
		}
	}
}
