package com.queryio.demo.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class CustomRecordReader extends RecordReader<IntWritable, List<FileStatus>> {

	private boolean valueRead = false;
	private List<FileStatus> fileStatuses = new ArrayList<FileStatus>();

	public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException {
		CustomJobContext.initialize(context.getConfiguration());
		CombinedSplit split = (CombinedSplit) genericSplit;

		for (FileSplit fileSplit : split.getAllSplits()) {
			fileStatuses.add(CustomJobContext.getFileStatus(fileSplit.getPath()));
		}
	}

	public boolean nextKeyValue() throws IOException {
		return !valueRead;
	}

	public IntWritable getCurrentKey() {
		return new IntWritable(fileStatuses.size());
	}

	public List<FileStatus> getCurrentValue() {
		valueRead = true;
		return fileStatuses;
	}

	/**
	 * Get the progress within the split
	 */
	public float getProgress() throws IOException {
		return 1;
	}

	public synchronized void close() throws IOException {
	}
}
