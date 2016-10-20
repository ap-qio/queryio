package com.queryio.demo.mr.report.mydbr;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapreduce.Mapper;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;


public class JTLMapper extends Mapper<List<FileStatus>, List<InputStream>, FileStatus, JTLEntry>{
	private static final Log LOG = LogFactory.getLog(JTLMapper.class);
	@Override 
	public void map(List<FileStatus> fileStatuses, List<InputStream> streams, final Context context)
			throws IOException, InterruptedException {
		try {
			LOG.info("Initializing.");
			LOG.info("Initializing Connection.");
			UserDefinedTagResourceFactory.initConnectionPool(context.getConfiguration(), false);
			LOG.info("Connection Initialized");
			String tableName = new JTLDataDefinitionImpl().getTableName();
			String resultTableName = context.getConfiguration().get("resultTableName");
			if (resultTableName != null)
				tableName = resultTableName;
			LOG.info("tagTableName: " + tableName);
			LOG.info("Checking table");
			Connection connection = null;
			ResultSet tables = null;
			try{
				connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(context.getConfiguration(), false);
				String connectionName = context.getConfiguration().get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
				DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null);
				DatabaseMetaData dbm = connection.getMetaData();
				tables = dbm.getTables(null, null, tableName, null);
				if (!tables.next()) {
					ArrayList<ColumnMetadata> list = new JTLDataDefinitionImpl().getColumnMetadata();
					createDatabaseTable(connection, tableName, list, props, "CREATE TABLE");
				}	
			}catch(Exception e){
				LOG.fatal(e.getMessage(), e);
			}finally
			{
				if(tables != null){
					tables.close();
				}
				if(connection != null){
					connection.close();
				}
			}
			LOG.info("Checked");
			
			int sampleDuration = Integer.valueOf(context.getConfiguration().get("sampleDuration"));
			LOG.info("sampleDuration: " + sampleDuration);
			
			int startTimestampIndex = Integer.valueOf(context.getConfiguration().get("startTimestampIndex"));
			LOG.info("startTimestampIndex: " + startTimestampIndex);

			int labelIndex = Integer.valueOf(context.getConfiguration().get("labelIndex"));
			LOG.info("labelIndex: " + labelIndex);
			
			int errorCountIndex = Integer.valueOf(context.getConfiguration().get("errorCountIndex"));
			LOG.info("errorCountIndex: " + errorCountIndex);

			int bytesIndex = Integer.valueOf(context.getConfiguration().get("bytesIndex"));
			LOG.info("bytesIndex: " + bytesIndex);

			int latencyIndex = Integer.valueOf(context.getConfiguration().get("latencyIndex"));
			LOG.info("latencyIndex: " + latencyIndex);
			
			String startTime = context.getConfiguration().get("startTime");
			LOG.info("startTime: " + startTime);

			String endTime = context.getConfiguration().get("endTime");
			LOG.info("endTime: " + endTime);

			int waitTime = Integer.valueOf(context.getConfiguration().get("waitTime"));
			LOG.info("waitTime: " + waitTime);
			
			ArrayList<String> columns = new JTLDataDefinitionImpl().getTableColumns();
			LOG.info("columns: " + columns);
			
			int maxBatchSize  = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_DB_BATCH_SIZE_MAX, 100);
			LOG.info("maxBatchSize: " + maxBatchSize);
			
			int maxThreadCount = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_THREAD_COUNT_MAX, 50);
			LOG.info("maxThreadCount: " + maxThreadCount);
			
			LOG.info("Total Threads fileStatuses.size(): " + fileStatuses.size() + "	streams.size(): " + streams.size());
			LOG.info("Calculating files per threads");
			List<Thread> threads = new ArrayList<Thread>();
			int totalFiles = fileStatuses.size();
			int filesPerThread = 1;
			if (totalFiles <= maxThreadCount){
				maxThreadCount = totalFiles;
			}else{
				filesPerThread = (int) Math.ceil((totalFiles * 1.0) / maxThreadCount);
			}
			
			if (filesPerThread < 0)
				throw new Exception("Files Per Thread accounts to a negative number. Total Files: " + totalFiles + " max Thread Count: " + maxThreadCount);
			
			ArrayList<FileStatus> fileStatusList = null;
			FileStatus fileStatus = null;
			ArrayList<InputStream> streamList = null;
			InputStream stream = null;
			
			int remainder = totalFiles % maxThreadCount;
			int countFactor = 1;
			LOG.info("Running Threads");
			long savePt = System.currentTimeMillis(); 
			for(int i=0; i<maxThreadCount; i++)
			{
				fileStatusList = new ArrayList<FileStatus>();
				streamList = new ArrayList<InputStream>();
				
				if ((remainder > 0) && (i >= remainder)) 
					countFactor = filesPerThread - 1;
				else
					countFactor = filesPerThread;
				
				for (int j=0; j<countFactor; j++)
				{
					fileStatus = fileStatuses.get((j * maxThreadCount) + i);
					fileStatusList.add(fileStatus);
					stream = streams.get((j * maxThreadCount) + i);
					streamList.add(stream);
				}
				
				LOG.info("Thread Count Created: " + i);
				JTLParserThread thread = new JTLParserThread(context, fileStatusList, streamList, tableName, maxBatchSize, sampleDuration, startTimestampIndex, labelIndex, errorCountIndex, bytesIndex, latencyIndex, startTime, endTime, waitTime, columns);
				thread.start();
				threads.add(thread);
			}
			LOG.info("Waiting for all thread Threads");
			for(Thread t : threads){
				t.join();
			}
			LOG.info("Total time taken by threads: " + (System.currentTimeMillis() - savePt) + " ms");
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		}
		finally {
			try {
				UserDefinedTagResourceFactory.removeConnectionPool(context.getConfiguration(), false);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}
	private static void createDatabaseTable(Connection connection, String tableName, ArrayList<ColumnMetadata> list, DBTypeProperties props, String createStmt) throws SQLException{		
		StringBuffer createStatement = new StringBuffer(createStmt).append(" ");
		createStatement.append(tableName).append(" (");
		for(int i = 0; i < list.size(); i ++){
			ColumnMetadata metadata = list.get(i);
			if(i != 0)
				createStatement.append(", ");
			createStatement.append(metadata.getColumnName()).append(" ").append(props.getTypeMap().get(metadata.getColumnSqlDataType()) + (metadata.isSizable() ? " (" + metadata.getSize() +")" : ""));
		}
		createStatement.append(")");
		
		Statement stmt = null;
		try{
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, createStatement.toString());
		} finally {
			try{
				DatabaseFunctions.closeStatement(stmt);
			} catch(Exception e){
				LOG.fatal(e.getMessage(), e);
			}
		}
	}
	class JTLParserThread extends Thread{
		Context context;
		ArrayList<FileStatus> fileStatusList;
		ArrayList<InputStream> streamList;
		String tableName;
		int maxBatchSize;
		int sampleDuration;
		int startTimestampIndex;
		int labelIndex;
		int errorCountIndex;
		int bytesIndex;
		int latencyIndex;
		String startTime;
		String endTime;
		int waitTime;
		ArrayList<String> columns;
		
		JTLParserThread(Context context, ArrayList<FileStatus> fileStatusList, ArrayList<InputStream> streamList, String tableName, int maxBatchSize, int sampleDuration, int startTimestampIndex,
				int labelIndex, int errorCountIndex, int bytesIndex, int latencyIndex, String startTime, String endTime, int waitTime, ArrayList<String> columns) {
			this.context = context;
			this.fileStatusList = fileStatusList;
			this.streamList = streamList;
			this.tableName = tableName;
			this.maxBatchSize = maxBatchSize;
			this.sampleDuration = sampleDuration;
			this.startTimestampIndex = startTimestampIndex;
			this.labelIndex = labelIndex;
			this.errorCountIndex = errorCountIndex;
			this.bytesIndex = bytesIndex;
			this.latencyIndex = latencyIndex;
			this.startTime = startTime;
			this.endTime = endTime;
			this.waitTime = waitTime;
			this.columns = columns;
		}
		
		public void run(){
			
			Connection connection = null;
			Configuration conf;
			try
			{
				conf = context.getConfiguration();
				connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf, false);
				
				
				FileStatus fileStatus = null;
				InputStream stream = null;
				LOG.info("fileStatusList.size: " + fileStatusList.size());
				for (int i=0; i<fileStatusList.size(); i++)
				{
					fileStatus = fileStatusList.get(i);
					stream = streamList.get(i);

					try
					{
						LOG.info("FileName: " + fileStatus.getPath());
						JTLParser parser = new JTLParser(connection, tableName, fileStatus, maxBatchSize, sampleDuration, startTimestampIndex, labelIndex, errorCountIndex, bytesIndex, latencyIndex, startTime, endTime, waitTime, columns);
						parser.parse(stream);
						
						LOG.info("FileName: " + fileStatus.getPath() + " parsed");
					}
					catch (Exception e)
					{
						LOG.fatal("Exception in parsing file " + fileStatus.getPath() + ": " + e.getLocalizedMessage(), e);
					}
					finally
					{
						try
						{
							if (stream != null)
								stream.close();
						}
						catch (Exception e)
						{
							LOG.fatal("Error closing stream.", e);
						}
					}
				}
			}
			catch (Exception e)
			{
				LOG.fatal(e.getLocalizedMessage(), e);
			}
			finally
			{
				try {
					if ((connection != null) && (!connection.isClosed()))
					{
						try
						{
							if (!connection.getAutoCommit())
								connection.commit();
						}
						catch (SQLException e)
						{
							LOG.fatal(e.getMessage(), e);
						}
						try 
						{
							connection.close();
						}
						catch (SQLException e)
						{
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