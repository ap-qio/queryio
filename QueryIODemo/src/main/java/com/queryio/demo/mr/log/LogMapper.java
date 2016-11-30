package com.queryio.demo.mr.log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapreduce.Mapper;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;

public class LogMapper extends Mapper<List<FileStatus>, List<InputStream>, FileStatus, LogEntry> {
	private static final Log LOG = LogFactory.getLog(LogMapper.class);

	@Override
	public void map(List<FileStatus> fileStatuses, List<InputStream> streams, final Context context)
			throws IOException, InterruptedException {
		String tableName = new LogDataDefinitionImpl().getTableName();
		String resultTableName = context.getConfiguration().get("resultTableName");
		if (resultTableName != null)
			tableName = resultTableName;
		LOG.info("tagTableName: " + tableName);
		try {
			UserDefinedTagResourceFactory.initConnectionPool(context.getConfiguration(), false);
			Connection connection = null;
			ResultSet tables = null;
			try {
				connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(context.getConfiguration(), false);
				String connectionName = context.getConfiguration().get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
				DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null);
				DatabaseMetaData dbm = connection.getMetaData();
				tables = dbm.getTables(null, null, tableName, null);
				if (!tables.next()) {
					ArrayList<ColumnMetadata> list = new LogDataDefinitionImpl().getColumnMetadata();
					createDatabaseTable(connection, tableName, list, props, "CREATE TABLE");
				}
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			} finally {
				if (tables != null) {
					tables.close();
				}
				if (connection != null) {
					connection.close();
				}
			}

			int maxBatchSize = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_DB_BATCH_SIZE_MAX, 100);
			LOG.info("maxBatchSize: " + maxBatchSize);

			int maxThreadCount = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_THREAD_COUNT_MAX, 50);
			LOG.info("maxThreadCount: " + maxThreadCount);

			LOG.info(
					"Total Threads fileStatuses.size(): " + fileStatuses.size() + "	streams.size(): " + streams.size());
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
				LogParserThread thread = new LogParserThread(context, fileStatusList, streamList, tableName,
						maxBatchSize);
				thread.start();
				threads.add(thread);
			}

			for (Thread t : threads) {
				t.join();
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		} finally {
			try {
				UserDefinedTagResourceFactory.removeConnectionPool(context.getConfiguration(), false);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	private static void createDatabaseTable(Connection connection, String tableName, ArrayList<ColumnMetadata> list,
			DBTypeProperties props, String createStmt) throws SQLException {
		StringBuffer createStatement = new StringBuffer(createStmt).append(" ");
		createStatement.append(tableName).append(" (");
		for (int i = 0; i < list.size(); i++) {
			ColumnMetadata metadata = list.get(i);
			if (i != 0)
				createStatement.append(", ");
			createStatement.append(metadata.getColumnName()).append(" ")
					.append(props.getTypeMap().get(metadata.getColumnSqlDataType())
							+ (metadata.isSizable() ? " (" + metadata.getSize() + ")" : ""));
		}
		createStatement.append(")");

		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, createStatement.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	class LogParserThread extends Thread {
		Context context;
		ArrayList<FileStatus> fileStatusList = null;
		ArrayList<InputStream> streamList = null;
		String tableName;
		int maxBatchSize;

		LogParserThread(Context context, ArrayList<FileStatus> fileStatusList, ArrayList<InputStream> streamList,
				String tableName, int maxBatchSize) {
			this.context = context;
			this.fileStatusList = fileStatusList;
			this.streamList = streamList;
			this.tableName = tableName;
			this.maxBatchSize = maxBatchSize;
		}

		public void run() {

			String logPattern = context.getConfiguration().get("log-pattern");
			String searchString = context.getConfiguration().get("search-string");
			LOG.info("logPattern: " + logPattern + " searchString: " + searchString);
			Date startDate = null;
			Date endDate = null;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String date;
			date = context.getConfiguration().get("start-date");
			if (date != null)
				try {
					startDate = sdf.parse(date);
				} catch (ParseException e1) {
					LOG.fatal(e1.getMessage(), e1);
				}

			date = context.getConfiguration().get("end-date");
			if (date != null)
				try {
					endDate = sdf.parse(date);
				} catch (ParseException e1) {
					LOG.fatal(e1.getMessage(), e1);
				}

			Connection connection = null;
			try {
				connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(context.getConfiguration(), false);
				LOG.info("startDate: " + startDate);
				LOG.info("endDate: " + endDate);

				FileStatus fileStatus = null;
				InputStream stream = null;
				LOG.info("fileStatusList.size: " + fileStatusList.size());
				for (int i = 0; i < fileStatusList.size(); i++) {
					fileStatus = fileStatusList.get(i);
					stream = streamList.get(i);

					try {
						LOG.info("FileName: " + fileStatus.getPath());
						LogParser parser = new LogParser(connection, tableName, logPattern, searchString, startDate,
								endDate, fileStatus, maxBatchSize);
						parser.parse(stream);

						LOG.info("FileName: " + fileStatus.getPath() + " parsed");
					} catch (Exception e) {
						LOG.fatal("Exception in parsing file " + fileStatus.getPath() + ": " + e.getLocalizedMessage(),
								e);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
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
				if (connection != null)
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}

		}
	}
}