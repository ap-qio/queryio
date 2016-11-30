package com.queryio.demo.mr.csv;

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

public class CSVParserJob extends Configured implements Tool {

	/**
	 * @param args
	 */
	private static final Log LOG = LogFactory.getLog(CSVParserJob.class);

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new CSVParserJob(), args);
		System.exit(exitCode);
	}

	public int run(String[] args) throws Exception {

		LOG.info("args.length: " + args.length);

		if (args.length < 1) {
			System.err.printf(
					"Usage: %s [generic options] <input-folder> [<result-table-name>] [<column1=value1> <column2=value2> ... ]\n\n",
					getClass().getSimpleName());

			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		// JobConf confa = new JobConf(getConf(), CSVParserJob.class);
		// conf.setInputFormat(DBInputFormat.class);
		// DBConfiguration.configureDB(confa, "com.mysql.jdbc.Driver",
		// "jdbc:mysql://localhost/mydatabase");
		// String [] fields = { �employee_id�, "name" };
		// DBInputFormat.setInput(conf, MyRecord.class, �employees�, null /*
		// conditions */, �employee_id�, fields); // set Mapper, etc., and call
		// JobClient.runJob(conf);

		JobConf conf = new JobConf(getConf(), CSVParserJob.class);
		Path path = new Path(args[0]);
		LOG.info(DFSConfigKeys.FS_DEFAULT_NAME_KEY + ": " + conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		LOG.info(
				QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.ANALYTICS_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + ": "
				+ conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY));
		if (args.length >= 2) {
			conf.set("resultTableName", args[1]);
		}
		LOG.info("resultTableName: " + conf.get("resultTableName"));

		if (args.length >= 3) {
			conf.set("expressions", args[2]);
		}
		LOG.info("Expressions: " + conf.get("expressions"));

		String jobName = "CSVParser";

		conf.set("jobId", jobName);

		Job job = Job.getInstance(conf);
		job.setJarByClass(CSVParserJob.class);
		job.setJobName(jobName);

		job.setInputFormatClass(CustomInputFormat.class);
		CustomInputFormat.setInputPaths(job, path);
		CustomInputFormat.setFileTypesToPick(new String[] { "csv" });
		job.setMapperClass(CSVMapper.class);
		CSVOutputFormat.setOutput(job);
		job.setOutputKeyClass(FileStatus.class);
		job.setOutputValueClass(CSVEntry.class);
		job.setMaxMapAttempts(1);

		job.setNumReduceTasks(0);

		Date startTime = new Date();
		LOG.info("Job started: " + startTime);
		int ret = job.waitForCompletion(true) ? 0 : 1;
		Date endTime = new Date();
		LOG.info("Job ended: " + endTime);
		LOG.info("The job took " + (endTime.getTime() - startTime.getTime()) / 1000 + " seconds.");
		return ret;
	}
}
