package com.queryio.demo.adhoc;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ReflectionUtils;

import com.queryio.common.QueryIOConstants;
import com.queryio.demo.common.CustomJobContext;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;


public class AdHocMapper extends Mapper<IntWritable, List<FileStatus>, FileStatus, AdHocEntry>{
	private static final Log LOG = LogFactory.getLog(AdHocMapper.class);
	@Override 
	public void map(IntWritable noOfFiles, List<FileStatus> fileStatuses, final Context context)
			throws IOException, InterruptedException {
		try {
			//Initialization
			UserDefinedTagResourceFactory.initConnectionPool(context.getConfiguration(), false);
			
			String tableName = context.getConfiguration().get(AdHocConstants.ADHOC_RESULTTABLE);
			LOG.info("TagTableName: " + tableName);
			
			String className = context.getConfiguration().get(AdHocConstants.ADHOC_PARSER_CLASSNAME);
			LOG.info("ClassName: " + className);
			
			String arguments = context.getConfiguration().get(AdHocConstants.ADHOC_PARSER_ARGUMENTS);
			LOG.info("Arguments: " + arguments);
			
			String expression = context.getConfiguration().get(AdHocConstants.ADHOC_PARSER_EXPRESSION);
			LOG.info("Expressions: " + expression);
			ParsedExpression parsedExpression = new ParsedExpression(expression);
			
			for(Expression e : parsedExpression.expressions){
				LOG.info("Parsed Expression: " + e.toString());	
			}
			for(ExpressionRelations e : parsedExpression.relations){
				LOG.info("Parsed ExpressionRelation: " + e.toString());	
			}
			LOG.info("Parsed BooleanExpression: " + parsedExpression.booleanExpressions);
			
			int maxBatchSize  = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_DB_BATCH_SIZE_MAX, 100);
			LOG.info("MaxBatchSize: " + maxBatchSize);
			
			int maxThreadCount = context.getConfiguration().getInt(QueryIOConstants.QUERYIO_THREAD_COUNT_MAX, 50);
			LOG.info("MaxThreadCount: " + maxThreadCount);
			
			String dbName = context.getConfiguration().get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			LOG.info("DBName: " + dbName);
			
			Class<? extends IAdHocParser> parserClass = (Class<? extends IAdHocParser>) Class.forName(className); 
			
			IAdHocParser parser = (IAdHocParser) ReflectionUtils.newInstance(
					parserClass, context.getConfiguration());
			
			parser.setArguments(arguments);
			parser.setExpressions(parsedExpression);
			
			// Calculation of files per threads and no of threads
			LOG.info("Total Threads fileStatuses.size(): " + fileStatuses.size());
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
			
			// Launching of threads
			int remainder = totalFiles % maxThreadCount;
			int countFactor = 1;
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
					stream = CustomJobContext.getInputStream(fileStatus);
					streamList.add(stream);
				}
				
				LOG.info("Thread Count Created: " + i);
				ParserThread thread = new ParserThread(dbName, parser, fileStatusList, streamList, tableName, maxBatchSize);
				thread.start();
				threads.add(thread);
			}
			
			for(Thread t : threads){
				t.join();
			}

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
	
	class ParserThread extends Thread{
		String dbName;
		ArrayList<FileStatus> fileStatusList;
		ArrayList<InputStream> streamList;
		String tableName;
		int maxBatchSize;
		IAdHocParser parser;
		ParserThread(String dbName,IAdHocParser parser, ArrayList<FileStatus> fileStatusList, ArrayList<InputStream> streamList, String tableName, int maxBatchSize){
			this.dbName = dbName;
			this.parser = parser;
			this.fileStatusList = fileStatusList;
			this.streamList = streamList;
			this.tableName = tableName;
			this.maxBatchSize = maxBatchSize; 
		}
		public void run(){

			DBListener dbListener = null;
			try
			{
				Connection connection = UserDefinedTagResourceFactory.getConnection(dbName, false);
				
				dbListener = new DBListener(connection, tableName, maxBatchSize);
				
				FileStatus fileStatus = null;
				InputStream stream = null;
				LOG.info("fileStatusList.size: " + fileStatusList.size());
				for (int i=0; i<fileStatusList.size(); i++)
				{
					fileStatus = fileStatusList.get(i);
					stream = streamList.get(i);
					String filePath = fileStatus.getPath().toUri().getPath();
					try
					{
						LOG.info("FileName: " + filePath);
						parser.parse(dbListener, filePath, stream);
						LOG.info("FileName: " + fileStatus.getPath() + " parsed");
					}
					catch (Exception e)
					{
						LOG.fatal("Exception in parsing file " + fileStatus.getPath() + ": " + e.getLocalizedMessage(), e);
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
					InputStream stream = null;
					for (int i=0; i<streamList.size(); i++)
					{
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
					if(dbListener != null)
						dbListener.close();
				} catch (SQLException e) {
					LOG.fatal(e.getMessage(), e);
					while(e != null) {
						LOG.fatal(e.getMessage(), e);
						e = e.getNextException();
					}
				}
			}
			
		}
	}
}