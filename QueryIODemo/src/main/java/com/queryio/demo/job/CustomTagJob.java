package com.queryio.demo.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.queryio.common.QueryIOConstants;

public class CustomTagJob extends Configured implements Tool {

	/**
	 * @param args
	 */
	private static final Log LOG = LogFactory.getLog(CustomTagJob.class);

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new CustomTagJob(), args);
		System.exit(exitCode);
	}

	public int run(String[] args) throws Exception {

		System.out.println("Writing all recieved arguments");
		for (int i = 0; i < args.length; i++) {
			System.out.println(i + ": " + args[i]);
		}

		if (args.length < 4) {
			System.err.printf(
					"Usage: %s [generic options] <parser-name> <file-type(s)> <classname> <input-folder> [expression]\n\n",
					getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		JobConf conf = new JobConf(getConf(), CustomTagJob.class);
		conf.set(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES, args[1]);
		String[] arr = args[1].split(",");
		for (String str : arr) {
			conf.set(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str, args[2]);
		}
		if (args.length >= 5 && args[4] != null && !args[4].isEmpty()) {
			conf.set("queryio.customtag.parser.expression", args[4]);
		}
		CustomInputFormat.setFileTypesToPick(arr);
		Path path = new Path(args[3]);

		Job job = Job.getInstance(conf);
		job.setJarByClass(CustomTagJob.class);
		job.setJobName(args[0]);

		job.setInputFormatClass(CustomInputFormat.class);
		job.setMapperClass(CustomMapper.class);
		job.setOutputKeyClass(FileStatus.class);
		job.setOutputValueClass(DBEntry.class);
		job.setMaxMapAttempts(1);

		CustomInputFormat.setInputPaths(job, path);
		CustomDBOutputFormat.setOutput(job);

		// reducer NONE
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
