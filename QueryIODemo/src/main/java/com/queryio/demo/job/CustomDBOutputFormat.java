package com.queryio.demo.job;

import java.io.IOException;
import java.sql.Connection;

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

public class CustomDBOutputFormat extends OutputFormat<FileStatus, DBEntry> {
	private static final Log LOG = LogFactory.getLog(CustomDBOutputFormat.class);

	public void checkOutputSpecs(JobContext arg0) throws IOException, InterruptedException {
	}

	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new FileOutputCommitter(FileOutputFormat.getOutputPath(context), context);
	}

	public RecordWriter<FileStatus, DBEntry> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		try {
			return new CustomDBRecordWriter(context.getConfiguration());
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

	class CustomDBRecordWriter extends RecordWriter<FileStatus, DBEntry> {
		Connection connection;

		FileStatus fileStatus;

		public CustomDBRecordWriter(Configuration conf) throws Exception {
			LOG.info("Initializing CustomDBRecordWriter");
			// UserDefinedTagResourceFactory.initConnectionPool(conf);
			// connection =
			// UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf);
		}

		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			LOG.info("Closing CustomDBRecordWriter");
			// try{
			// if(!connection.getAutoCommit())
			// connection.commit();
			// }catch(SQLException e){
			// LOG.fatal(e.getMessage(), e);
			// }
			// try{
			// connection.close();
			// }catch(SQLException e){
			// LOG.fatal(e.getMessage(), e);
			// }
		}

		public void write(FileStatus fileStatus, DBEntry entry) throws IOException, InterruptedException {
			// try {
			// LOG.info("Writing tags for file " +
			// fileStatus.getPath().toUri().getPath() + " into " +
			// entry.getTableName());
			// UserDefinedTagDAO.insertTagValues(connection,
			// entry.getTableName(), fileStatus.getPath().toUri().getPath(),
			// entry.getTags(), true, null);
			// } catch (SQLException e) {
			// throw new IOException(e);
			// }
		}
	}

	public static void setOutput(Job job) throws IOException {
		job.setOutputFormatClass(CustomDBOutputFormat.class);
		job.setReduceSpeculativeExecution(false);
	}
}
