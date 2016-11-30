package com.queryio.demo.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class CombinedSplit extends InputSplit implements Writable {

	private static final Log LOG = LogFactory.getLog(CombinedSplit.class);
	List<FileSplit> splits;
	private long length;
	private List<String> hosts;

	public List<FileSplit> getAllSplits() {
		return splits;
	}

	public CombinedSplit() {
		LOG.info("made new combined split.");
		splits = new ArrayList<FileSplit>();
		hosts = new ArrayList<String>();
		length = 0L;
	}

	public void createSplit(Path file, long start, long length, String[] hosts) {
		splits.add(new FileSplit(file, start, length, hosts));
		LOG.info(splits.size() + ". added file split for file: " + file.toString() + ".");
		this.length += length;
		for (String host : hosts) {
			if (!this.hosts.contains(host)) {
				this.hosts.add(host);
			}
		}
	}

	@Override
	public long getLength() throws IOException, InterruptedException {
		return this.length;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		String[] hostArray = new String[hosts.size()];
		hosts.toArray(hostArray);
		return hostArray;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int totalSplits = in.readInt();
		for (int i = 0; i < totalSplits; i++) {
			FileSplit split = new FileSplit();
			split.readFields(in);
			this.length += split.getLength();
			for (String host : split.getLocations()) {
				if (!this.hosts.contains(host)) {
					this.hosts.add(host);
				}
			}
			splits.add(split);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(splits.size());
		for (FileSplit split : splits) {
			split.write(out);
		}
	}
}
