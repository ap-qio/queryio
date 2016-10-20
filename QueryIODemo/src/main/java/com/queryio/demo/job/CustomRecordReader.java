package com.queryio.demo.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import com.queryio.demo.common.CustomQIODFSInputStream;

public class CustomRecordReader extends RecordReader<List<FileStatus>, List<InputStream>>{

	  private static final Log LOG = LogFactory.getLog(LineRecordReader.class);
  
	  private List<InputStream> buffer = new ArrayList<InputStream>();
	  private boolean valueRead = false;
	  private List<FileStatus> fileStatus = new ArrayList<FileStatus>();
	  	
	  public void initialize(InputSplit genericSplit,
	                         TaskAttemptContext context) throws IOException {
//		try {
//			Thread.sleep(120000);
//		} catch (InterruptedException e1) {
//			LOG.fatal(e1.getMessage(), e1);
//		}
		CombinedSplit split = (CombinedSplit) genericSplit;
	    Configuration job = context.getConfiguration();
	    
	    for(FileSplit fileSplit : split.getAllSplits()){
	    	Path file = fileSplit.getPath();
	    	FileSystem dfs = file.getFileSystem(job);
		    // open the file and get InputStream
		    
	    	fileStatus.add(dfs.getFileStatus(file));
		    LOG.info("Reading file: "+ file);
		    
		    DFSInputStream dfsInputStream = null;
			InputStream qioInputStream = null;
			
			String filePath = file.toString();
			LOG.info("filePath: " + filePath);
			
			String src = filePath;
			if(src.contains("://")) {
				src = src.substring(src.indexOf("://") + 3);
				src = src.substring(src.indexOf("/"));
			}
			
			LOG.info("Modified filePath: " + src);
			
			DistributedFileSystem fs = (DistributedFileSystem) dfs;
			dfsInputStream = (DFSInputStream) fs.getClient().open(src);
			try {
				qioInputStream = new CustomQIODFSInputStream(dfsInputStream, job, src);
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
				if (dfsInputStream != null) {
					dfsInputStream.close();
				}
				throw new IOException(e.getMessage());
			}
			
		    buffer.add(qioInputStream);	
	    }
	  }
	  
	  
	  public boolean nextKeyValue() throws IOException {
	    return !valueRead;
	  }

	  public List<FileStatus> getCurrentKey() {
	    return fileStatus;
	  }

	  public List<InputStream> getCurrentValue() {
		valueRead = true;
	    return buffer;
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
