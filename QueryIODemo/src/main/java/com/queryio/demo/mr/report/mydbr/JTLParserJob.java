package com.queryio.demo.mr.report.mydbr;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.queryio.common.QueryIOConstants;
import com.queryio.demo.job.CustomInputFormat;

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
		LOG.info(DFSConfigKeys.FS_DEFAULT_NAME_KEY + ": " + conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		LOG.info(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.ANALYTICS_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + ": " + conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY));
		if (args.length>=2){
			conf.set("resultTableName", args[1]);
		}
		LOG.info("resultTableName: " + conf.get("resultTableName"));
		
		if (args.length>=3){
			conf.set("sampleDuration", args[2]);
		}
		LOG.info("sampleDuration: " + conf.get("sampleDuration"));
		
		if (args.length>=4){
			conf.set("startTimestampIndex", args[3]);
		}
		LOG.info("startTimestampIndex: " + conf.get("startTimestampIndex"));

		if (args.length>=5){
			conf.set("labelIndex", args[4]);
		}
		LOG.info("labelIndex: " + conf.get("labelIndex"));

		if (args.length>=6){
			conf.set("errorCountIndex", args[5]);
		}
		LOG.info("errorCountIndex: " + conf.get("errorCountIndex"));

		if (args.length>=7){
			conf.set("bytesIndex", args[6]);
		}
		LOG.info("bytesIndex: " + conf.get("bytesIndex"));

		if (args.length>=8){
			conf.set("latencyIndex", args[7]);
		}
		LOG.info("latencyIndex: " + conf.get("latencyIndex"));

		if (args.length>=9){
			conf.set("startTime", args[8]);
		}
		LOG.info("startTime: " + conf.get("startTime"));
		
		if (args.length>=10){
			conf.set("endTime", args[9]);
		}
		LOG.info("endTime: " + conf.get("endTime"));

		if (args.length>=11){
			conf.set("waitTime", args[10]);
		}
		LOG.info("waitTime: " + conf.get("waitTime"));
		
		String jobName = "JTLParser";
		
		conf.set("jobId", jobName);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(JTLParserJob.class); 
		job.setJobName(jobName);
				
		job.setInputFormatClass(CustomInputFormat.class);
		CustomInputFormat.setInputPaths(job, path);		
		CustomInputFormat.setFileTypesToPick(new String[]{"jtl"});
		job.setMapperClass(JTLMapper.class);
		JTLOutputFormat.setOutput(job);
		job.setOutputKeyClass(FileStatus.class); 
		job.setOutputValueClass(JTLEntry.class);			
		job.setMaxMapAttempts(1);			    
		
		job.setNumReduceTasks(0);
	    
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
}
