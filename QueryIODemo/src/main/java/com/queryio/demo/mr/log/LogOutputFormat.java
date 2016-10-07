package com.queryio.demo.mr.log;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.queryio.plugin.datatags.UserDefinedTag;

public class LogOutputFormat extends OutputFormat<FileStatus, LogEntry>{
private static final Log LOG = LogFactory.getLog(LogOutputFormat.class);	
	
	
	@Override
	public void checkOutputSpecs(JobContext arg0) throws IOException,
			InterruptedException {}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		 return new FileOutputCommitter(FileOutputFormat.getOutputPath(context),
                 context);
	}

	@Override
	public RecordWriter<FileStatus, LogEntry> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		try {	        
	      return new LogRecordWriter(context.getConfiguration());
	    } catch (Exception ex) {
	      throw new IOException(ex);
	    }
	}
	
	class LogRecordWriter extends RecordWriter<FileStatus, LogEntry>{
		Connection connection;
		List<UserDefinedTag> tags;
		FileStatus fileStatus;
		Configuration conf;
		
		String cols[] = null;
		String tableName = new LogDataDefinitionImpl().getTableName();
		
		public LogRecordWriter(Configuration conf) throws Exception {
//			LOG.debug("Initializing LogRecordWriter");
//			String path = conf.get("mapreduce.cluster.local.dir");
//			LOG.info("path: " + path);
////			if(path != null && !path.isEmpty())			
////				tableName = path.substring(path.lastIndexOf("/") + 1);
//			LOG.info("tagTableName: " + tableName);
//			this.conf = conf;
//			connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(this.conf);	    
//			tags = new ArrayList<UserDefinedTag>();			
		}
		@Override
		public void close(TaskAttemptContext context) throws IOException,
				InterruptedException {
//			LOG.debug("Closing LogRecordWriter");
//		
//			try{
//				if(!connection.getAutoCommit())
//					connection.commit();
//			}catch(SQLException e){
//				LOG.fatal(e.getMessage(), e);
//			}
//			try{
//				connection.close();
//			}catch(SQLException e){
//				LOG.fatal(e.getMessage(), e);
//			}
		}	
		
		@Override
		public void write(FileStatus fileStatus, LogEntry logEntry) throws IOException,
				InterruptedException {
//			this.fileStatus = fileStatus;
//			
//			LOG.debug("Writing log entry " + logEntry);
//			
//			ArrayList<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();
//			
//			if(logEntry.getCategory()!=null)
//				tags.add(new UserDefinedTag("Category", logEntry.getCategory()));
//			if(logEntry.getClassName()!=null)
//				tags.add(new UserDefinedTag("Class", logEntry.getClassName()));
//			if(logEntry.getDate()!=null)
//				tags.add(new UserDefinedTag("Date", logEntry.getDate()));
//			if(logEntry.getFileName()!=null)
//				tags.add(new UserDefinedTag("File", logEntry.getFileName()));
//			if(logEntry.getLineNumber()!=null)
//				tags.add(new UserDefinedTag("Line", logEntry.getLineNumber()));
//			if(logEntry.getLocation()!=null)
//				tags.add(new UserDefinedTag("Location", logEntry.getLocation()));
//			if(logEntry.getMdc()!=null)
//				tags.add(new UserDefinedTag("MDC", logEntry.getMdc()));
//			if(logEntry.getMessage()!=null)
//				tags.add(new UserDefinedTag("Message", logEntry.getMessage()));
//			if(logEntry.getMethod()!=null)
//				tags.add(new UserDefinedTag("Method", logEntry.getMethod()));
//			if(logEntry.getMsElapsed()!=null)
//				tags.add(new UserDefinedTag("Elapsed", logEntry.getMsElapsed()));
//			if(logEntry.getNdc()!=null)
//				tags.add(new UserDefinedTag("NDC", logEntry.getNdc()));
//			if(logEntry.getPriority()!=null)
//				tags.add(new UserDefinedTag("Priority", logEntry.getPriority()));
//			if(logEntry.getSequence()!=null)
//				tags.add(new UserDefinedTag("Sequence", logEntry.getSequence()));
//			if(logEntry.getThread()!=null)
//				tags.add(new UserDefinedTag("Thread", logEntry.getThread()));
//			
//			try {
//				UserDefinedTagDAO.insertTagValues(connection, this.tableName, fileStatus.getPath().toUri().getPath(), tags, true, false);
//			} catch (SQLException e) {
//				LOG.fatal(e.getMessage(), e);
//			}
			
		}
	}
	public static void setOutput(Job job) throws IOException {
		job.setOutputFormatClass(LogOutputFormat.class);
		job.setReduceSpeculativeExecution(false);
	}
}
