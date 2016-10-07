package com.queryio.demo.mr.report.aos;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JTLParserJob extends Configured implements Tool{

	/**
	 * @param args
	 */
	private static final Log LOG = LogFactory.getLog(JTLParserJob.class);
	public static void main(String[] args) throws Exception { 
		int exitCode = ToolRunner.run(new JTLParserJob(), args);
		System.exit(exitCode);
	}
	
	public int run(String[] args) throws Exception {
		
		
		LOG.info("args.length: " + args.length);
		
		if (args.length < 1) {
			System.err.printf("Usage: %s [generic options] <input-folder> [<result-table-name>] [<sample-duration>] [start-timestamp-index] [label-index] [error-count-index] [bytes-index] [latency-index] [start-time] [end-time] [wait-time-for-order-status]\n\n", getClass().getSimpleName()); 
			
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
		
		JobConf conf = new JobConf(getConf(), JTLParserJob.class);
		Path path = new Path(args[0]);
		Path outputPath = new Path(args[1]);
		
		loadConfigParameters(args, conf);
		
		String jobName = "JTLParser";
		conf.set("jobId", jobName);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(JTLParserJob.class); 
		job.setJobName(jobName);
				
		job.setMapperClass(JTLMapper.class);
		job.setReducerClass(JTLReducer.class);
//		job.setCombinerClass(JTLCombiner.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Record.class);
		
		FileInputFormat.setInputPathFilter(job, InputPathFilter.class);
		FileInputFormat.setInputDirRecursive(job, true);
		FileSystem fs= FileSystem.get(conf); 
		LOG.info("Input path : " + fs.getFileStatus(path).getPath().toString());
//		FileInputFormat.addInputPath(job, fs.getFileStatus(path).getPath());

		//get the FileStatus list from given dir
		FileStatus[] status_list = fs.listStatus(path);
		if(status_list != null){
		    for(FileStatus status : status_list){
		    	if(status.isDirectory() || !status.getPath().getName().endsWith(".jtl")) {
		    		System.out.println("Skipping "  + status.getPath().getName());
		    		continue;
		    	}
		        //add each file to the list of inputs for the map-reduce job
		    	System.out.println("Adding "  + status.getPath().toUri().toString() + ", " + status.getPath().toString());
		        FileInputFormat.addInputPath(job, status.getPath());
		    }
		}
		
		FileOutputFormat.setOutputPath(job, outputPath);
		
	    Date startTime = new Date();
	    LOG.info("Job started: " + startTime);
	    int ret = job.waitForCompletion(true) ? 0 : 1;
	    Date endTime = new Date();
	    
	    LOG.info("Job ended: " + endTime);
	    LOG.info("The job took " + 
		                       (endTime.getTime() - startTime.getTime()) /1000 + 
		                       " seconds.");
	    return ret;
	}

	private void loadConfigParameters(String[] args, JobConf conf) {

		conf.set("sampleDuration", args[2]);
		LOG.info("sampleDuration: " + conf.get("sampleDuration"));

		conf.set("startTimestampIndex", args[3]);
		LOG.info("startTimestampIndex: " + conf.get("startTimestampIndex"));

		conf.set("labelIndex", args[4]);
		LOG.info("labelIndex: " + conf.get("labelIndex"));

		conf.set("errorCountIndex", args[5]);
		LOG.info("errorCountIndex: " + conf.get("errorCountIndex"));

		conf.set("bytesIndex", args[6]);
		LOG.info("bytesIndex: " + conf.get("bytesIndex"));

		conf.set("latencyIndex", args[7]);
		LOG.info("latencyIndex: " + conf.get("latencyIndex"));

		conf.set("startTime", args[8]);
		LOG.info("startTime: " + conf.get("startTime"));

		conf.set("endTime", args[9]);
		LOG.info("endTime: " + conf.get("endTime"));

		conf.set("waitTime", args[10]);
		LOG.info("waitTime: " + conf.get("waitTime"));
	}	
}
