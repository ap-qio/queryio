package com.queryio.demo.mr.generic.parser;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.demo.job.CustomDBOutputFormat;
import com.queryio.demo.job.CustomInputFormat;
import com.queryio.demo.job.CustomRecordInputFormat;
	
public class GenericParserJob extends Configured implements Tool{

	/**
	 * @param args
	 */
	private static final Log LOG = LogFactory.getLog(GenericParserJob.class);
	public static void main(String[] args) throws Exception { 
		int exitCode = ToolRunner.run(new GenericParserJob(), args);
		System.exit(exitCode);	
	}
	
	public int run(String[] args) throws Exception {
		
		
		LOG.info("args.length: " + args.length);
		
		if (args.length < 5) {
			System.err.printf("Usage: %s [generic options] <input-folder>\n\n", getClass().getSimpleName()); 
			
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		JobConf conf = new JobConf(getConf(), GenericParserJob.class);
		
		String jobName = args[0];
		
		Path path = new Path(args[1]);
		LOG.info(DFSConfigKeys.FS_DEFAULT_NAME_KEY + ": " + conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		LOG.info(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + ": " + conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID));
		LOG.info(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + ": " + conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY));
		LOG.info(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY + ": " + conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY));
		
		String tagsJson = args[2];
		
		LOG.info("tagsJson: " + tagsJson);

		
		conf.set("tagsJSON", tagsJson);
		conf.set("startTime", args[3]);
		conf.set("endTime", args[4]);
		conf.set("callTikaParser", args[5]);
		
		conf.set("jobId", jobName);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(GenericParserJob.class); 
		job.setJobName(jobName);
		
		ArrayList<String> filesToPick = new ArrayList<String>();
		
		String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
		JSONObject fileTypeParsers = new JSONObject();
		if (fpJSON != null)
		{
			fileTypeParsers = (JSONObject) new JSONParser().parse(fpJSON.trim());
			LOG.info("fileTypeParsers : " + fileTypeParsers.toJSONString());
			
			Iterator it = fileTypeParsers.keySet().iterator();
			while (it.hasNext())
			{
				String valueOf = String.valueOf(it.next());
//				if (valueOf.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG) || valueOf.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
//					valueOf = QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase();
				filesToPick.add(valueOf);
			}
		}
		
		boolean recordLevel = false;
		JSONObject recordObject = new JSONObject();
		if (tagsJson != null) {
			recordObject = (JSONObject) new JSONParser().parse(tagsJson);
			Object object = recordObject.get(QueryIOConstants.QUERYIO_RECORD_LEVEL);
			if (object != null) {
				recordLevel = (Boolean) object;
			}
		}
		LOG.info("RecordLevel : " + recordLevel);
		LOG.info("filesToPick : " + filesToPick);
		
		
		if (recordLevel) {
			job.setInputFormatClass(CustomRecordInputFormat.class);
			
			CustomRecordInputFormat.setJobContext(job);
			FileInputFormat.setInputDirRecursive(job, true);
			FileInputFormat.setInputPathFilter(job, MultiPathFilter.class);
			FileInputFormat.addInputPath(job, path);
			FileSystem fs= FileSystem.get(conf); 
			LOG.info("Input path : " + fs.getFileStatus(path).getPath().toString());
			
			
			job.setMapperClass(RecordLevelTagMapper.class);
			
			job.setOutputKeyClass(Text.class); 
			job.setOutputValueClass(TagDataBean.class);
			
			job.setCombinerClass(RecordLevelTagCombiner.class);
			job.setReducerClass(RecordLevelTagReducer.class);
			
			job.setOutputFormatClass(NullOutputFormat.class);
			
			//job.setNumReduceTasks(10);
		} else {
			job.setInputFormatClass(CustomInputFormat.class);
			CustomInputFormat.setInputPaths(job, path);
			CustomInputFormat.setFileTypesToPick(filesToPick.toArray(new String[filesToPick.size()]));
			
			job.setMapperClass(DataTagMapper.class);
			job.setOutputKeyClass(FileStatus.class); 
			job.setOutputValueClass(TagEntry.class);
			
			job.setNumReduceTasks(0);
			
			CustomDBOutputFormat.setOutput(job);
		}
		
		job.setMaxMapAttempts(1);			    
		
	    
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
	
	/**
	 * Proxy PathFilter that accepts a path only if all filters given in the
	 * constructor do. Used by the listPaths() to apply the built-in
	 * hiddenFileFilter together with a user provided one (if any).
	 */
	private static class MultiPathFilter extends Configured implements PathFilter {
		private List<PathFilter> filters;
		private long startTime;
		private long endTime;
		private FileSystem fs;
		ArrayList<String> filesToPick;
		
		public MultiPathFilter() {
			
		}

		public MultiPathFilter(List<PathFilter> filters, Configuration conf) {
			this.filters = filters;
			if (conf != null) {
				startTime = conf.getLong("startTime", 0);
				endTime = conf.getLong("endTime", System.currentTimeMillis());
				try {
					fs = FileSystem.get(conf);
				} catch (Exception e) {
					LOG.error("Failed to initialize Path filter.", e);
				}
			}
		}
		
		@Override
		public void setConf(Configuration conf) {
			try {
				super.setConf(conf);
				if (conf != null) {
					filesToPick = new ArrayList<String>();
					
					String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
					JSONObject fileTypeParsers = new JSONObject();
					if (fpJSON != null)
					{
						fileTypeParsers = (JSONObject) new JSONParser().parse(fpJSON);
						LOG.info("fileTypeParsers : " + fileTypeParsers.toJSONString());
						
						Iterator it = fileTypeParsers.keySet().iterator();
						while (it.hasNext())
						{
							String valueOf = String.valueOf(it.next());
//							if (valueOf.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG) || valueOf.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
//								valueOf = QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase();
							filesToPick.add(valueOf);
						}
					}
					LOG.info("filesToPick : " + filesToPick);
					
					fs = FileSystem.get(conf);
					
					startTime = conf.getLong("startTime", 0);
					endTime = conf.getLong("endTime", System.currentTimeMillis());
					filters = new ArrayList<PathFilter>();
					filters.add(CustomInputFormat.HIDDEN_FILE_FILTER);
					filters.add(new FileTypePathFilter(filesToPick));
//					PathFilter jobFilter = FileInputFormat.getInputPathFilter(job);
//					if (jobFilter != null) {
//						filters.add(jobFilter);
//					}
				}
			} catch (Exception e) {
				LOG.error("Error Initializing Path filters" , e);
			}
		}

		public boolean accept(Path path) {
			try {
	
				FileStatus fileStatus = fs.getFileStatus(path);
				if (fileStatus.isDirectory()) {
					return true;
				}
				for (PathFilter filter : filters) {
					if (!filter.accept(path)) {
						return false;
					}
				}
				LOG.info("File: " + path + ", Modify: " + fileStatus.getModificationTime() + ", startTime: " + startTime + ", EndTime : " + endTime);
				if (fileStatus.getModificationTime() < startTime || fileStatus.getModificationTime() > endTime) {
					return false;
				}

				return true;
			} catch (Exception e) {
				LOG.error("Failed to accept file : " + path.toUri().getPath(), e);
				return false;
			}
		}
	}
	
	private static class FileTypePathFilter implements PathFilter {

		List<String> fileTypesToPick;
		
		public FileTypePathFilter(List<String> fileTypes) {
			this.fileTypesToPick = fileTypes;
		}

		@Override
		public boolean accept(Path path) {
			for (String fileTypeToPick : fileTypesToPick) {
				LOG.info("File: " + path + ", fileTypeToPick: " + fileTypeToPick);
				if (path.toString().endsWith(fileTypeToPick)) {
					return true;
				}
			}
			return false;
		}
	}
}
