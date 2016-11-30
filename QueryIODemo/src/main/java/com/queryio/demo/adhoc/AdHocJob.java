package com.queryio.demo.adhoc;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.queryio.common.QueryIOConstants;

public class AdHocJob extends Configured implements Tool {

	/**
	 * @param args
	 */
	private static final Log LOG = LogFactory.getLog(AdHocJob.class);

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Configuration(), new AdHocJob(), args);
		System.exit(exitCode);
	}

	public int run(String[] args) throws Exception {

		LOG.info("args.length: " + args.length);

		if (args.length < 6) {
			System.err.printf(
					"Usage: %s [generic options] <job-name> <parser-classname> <input-path> <path-pattern> <result-table-name> <arguments> [<expressions>]\n\n",
					getClass().getSimpleName());

			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
		String jobName = args[0];
		String parserClassName = args[1];
		String inputPath = args[2];
		String pathPattern = args[3];
		// pathPattern = pathPattern.replace("?", ".?").replace("*", ".*?");
		String resultTableName = args[4];
		String arguments = args[5];
		String expressions = null;
		if (args.length > 6) {
			expressions = args[6];
		}

		JobConf conf = new JobConf(getConf(), AdHocJob.class);
		LOG.info(DFSConfigKeys.FS_DEFAULT_NAME_KEY + ": " + conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		LOG.info(
				QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.ANALYTICS_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + ": "
				+ conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY));

		conf.set(AdHocConstants.ADHOC_PARSER_CLASSNAME, parserClassName);
		LOG.info("ParserClassName: " + conf.get(AdHocConstants.ADHOC_PARSER_CLASSNAME));

		conf.set(AdHocConstants.ADHOC_PARSER_ARGUMENTS, arguments);
		LOG.info("Arguments: " + conf.get(AdHocConstants.ADHOC_PARSER_ARGUMENTS));

		if (expressions != null) {
			conf.set(AdHocConstants.ADHOC_PARSER_EXPRESSION, expressions);
			LOG.info("Expressions: " + conf.get(AdHocConstants.ADHOC_PARSER_EXPRESSION));
		}

		conf.set(AdHocConstants.ADHOC_RESULTTABLE, resultTableName);
		LOG.info("ResultTableName: " + conf.get(AdHocConstants.ADHOC_RESULTTABLE));

		LOG.info("InputPath: " + inputPath);
		LOG.info("PathPattern: " + pathPattern);

		Job job = Job.getInstance(conf);
		job.setJarByClass(AdHocJob.class);
		job.setJobName(jobName);

		job.setInputFormatClass(AdHocInputFormat.class);
		AdHocInputFormat.setInputPaths(job, new Path(inputPath));
		AdHocInputFormat.setPathFilter(pathPattern);
		job.setMapperClass(AdHocMapper.class);
		AdHocOutputFormat.setOutput(job);
		job.setOutputKeyClass(FileStatus.class);
		job.setOutputValueClass(AdHocEntry.class);
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
