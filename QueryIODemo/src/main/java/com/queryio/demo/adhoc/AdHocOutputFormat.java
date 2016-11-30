package com.queryio.demo.adhoc;

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

public class AdHocOutputFormat extends OutputFormat<FileStatus, AdHocEntry> {
	private static final Log LOG = LogFactory.getLog(AdHocOutputFormat.class);

	@Override
	public void checkOutputSpecs(JobContext arg0) throws IOException, InterruptedException {
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new FileOutputCommitter(FileOutputFormat.getOutputPath(context), context);
	}

	@Override
	public RecordWriter<FileStatus, AdHocEntry> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		try {
			return new CSVRecordWriter(context.getConfiguration());
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

	class CSVRecordWriter extends RecordWriter<FileStatus, AdHocEntry> {
		Connection connection;
		List<UserDefinedTag> tags;
		FileStatus fileStatus;
		Configuration conf;

		public CSVRecordWriter(Configuration conf) throws Exception {

		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {

		}

		@Override
		public void write(FileStatus fileStatus, AdHocEntry logEntry) throws IOException, InterruptedException {

		}
	}

	public static void setOutput(Job job) throws IOException {
		job.setOutputFormatClass(AdHocOutputFormat.class);
		job.setReduceSpeculativeExecution(false);
	}
}
