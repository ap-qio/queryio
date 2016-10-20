package com.queryio.demo.mr.generic.parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;


public class RecordLevelTagMapper extends Mapper<LongWritable, Text, Text, TagDataBean>{
	
	private static final Log LOG = LogFactory.getLog(RecordLevelTagMapper.class);
	
	private static JSONObject tagJSON = null;
	private static JSONObject fileTypeParsers = new JSONObject();
	private static IDataTagParser tagParser;
	@Override 
	public void map(LongWritable offset, Text value, final Context context)
			throws IOException, InterruptedException {
		String filePath = null;
		try {
			LOG.info("offset : "+offset + "Value : "+value);
			FileSplit inputSplit = (FileSplit)context.getInputSplit();
	        filePath = inputSplit.getPath().toUri().getPath();
			int indexOf = filePath.indexOf(context.getJobID().toString());
			if (indexOf > 0) {
				filePath = filePath.substring(indexOf + context.getJobID().toString().length()).trim();
			}

			String fileExtension = UserDefinedTagUtils.getFileExtension(filePath);
			if (tagParser == null) {
				Configuration conf = context.getConfiguration();
				
				String tagsJSONString = conf.get("tagsJSON");
				LOG.info("tagsJSONString : " + tagsJSONString);
				JSONParser parser = new JSONParser();
				if (tagsJSONString != null && !tagsJSONString.trim().isEmpty()) {				
					tagJSON = (JSONObject) parser.parse(tagsJSONString);
					LOG.info("tagJSON : " + tagJSON.toJSONString());
				}
				
				String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
				if (fpJSON != null)
				{
					fileTypeParsers = (JSONObject) parser.parse(fpJSON);
				}
				
				Map<String, String> coreMap = new HashMap<String, String>();
				tagParser = UserDefinedTagResourceFactory.getParserDataTaggingJob(fileExtension, fileTypeParsers, conf, tagJSON, coreMap);
			}
			if (tagParser != null) {
				try{
				if (!tagParser.parseMapRecord(value.toString(), offset.get())) {
					return;
				}
				}
				catch(Exception e)
				{
					TagDataBean tdb = new TagDataBean(null, true, true);
					context.write(new Text(filePath), tdb);
					LOG.fatal(e.getMessage() + " : " + value.toString() + " : " + offset, e);
					return;
				}
			
				List<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();
				if (tagParser != null && tagParser.getCustomTagList() != null
						&& tagParser.getCustomTagList().size() > 0) {
					tags.addAll(tagParser.getCustomTagList());
				}
				
//				for (UserDefinedTag tag : tags) {
//					LOG.info(value + " = " + tag.getValue());
//				}
				
				TagDataBean tdb = new TagDataBean(tags, true, false);
				//LOG.info("filePath: " + filePath);
				context.write(new Text(filePath), tdb);
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage() + " : " + value.toString() + " : " + offset, e);
			throw new IOException(e);
		}
	}
}