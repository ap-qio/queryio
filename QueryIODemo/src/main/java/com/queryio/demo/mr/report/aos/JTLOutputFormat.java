package com.queryio.demo.mr.report.aos;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JTLOutputFormat extends FileOutputFormat<Text, Record>{

	@Override
	public RecordWriter<Text, Record> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {

		return null;
	}
	
}
