package com.queryio.demo.mr.generic.parser;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class RecordLevelTagCombiner extends Reducer<Text, TagDataBean, Text, TagDataBean> {

	private static final Log LOG = LogFactory.getLog(RecordLevelTagCombiner.class);

	private static final String TABLE_HDFS_METADATA = "HDFS_METADATA";

	public static final String COL_COMPRESSION_TYPE = "COMPRESSION_TYPE";
	public static final String COL_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
	public static final String COL_TAG_VALUES_BLOCKS = "BLOCKS";

	public static final String SNAPPY = "SNAPPY";
	public static final String GZ = "GZ";
	public static final String LZ4 = "LZ4";
	public static final String NONE = "NONE";
	public static final String AES256 = "AES256";

	private final static String QUERY_START = "SELECT " + COL_COMPRESSION_TYPE + "," + COL_ENCRYPTION_TYPE + ","
			+ COL_TAG_VALUES_BLOCKS + " FROM ";
	private final static String QUERY_END = " WHERE FILEPATH=?";

	private static JSONObject tagJSON = null;
	private static JSONObject fileTypeParsers = new JSONObject();

	@Override
	protected void setup(Reducer<Text, TagDataBean, Text, TagDataBean>.Context context)
			throws IOException, InterruptedException {
		try {
			super.setup(context);

			Configuration conf = context.getConfiguration();
			UserDefinedTagResourceFactory.initConnectionPool(conf, true);

			String tagsJSONString = conf.get("tagsJSON");
			LOG.info("tagsJSONString : " + tagsJSONString);
			JSONParser parser = new JSONParser();
			if (tagsJSONString != null && !tagsJSONString.trim().isEmpty()) {
				tagJSON = (JSONObject) parser.parse(tagsJSONString);
				LOG.info("tagJSON : " + tagJSON.toJSONString());
			}

			String fpJSON = conf.get(QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY);
			if (fpJSON != null) {
				fileTypeParsers = (JSONObject) parser.parse(fpJSON);
				LOG.info("fileTypeParsers : " + fileTypeParsers.toJSONString());
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		}
	}

	@Override
	protected void reduce(Text filePath, Iterable<TagDataBean> value,
			Reducer<Text, TagDataBean, Text, TagDataBean>.Context context) throws IOException, InterruptedException {
		Connection connection = null;
		try {
			Configuration conf = context.getConfiguration();

			String fileExtension = UserDefinedTagUtils.getFileExtension(filePath.toString());

			Map<String, String> coreMap = new HashMap<String, String>();
			IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserDataTaggingJob(fileExtension,
					fileTypeParsers, conf, tagJSON, coreMap);
			if (tagParser != null) {
				long ts = System.currentTimeMillis();
				for (TagDataBean tdb : value) {
					if (tdb.isError()) {
						context.write(new Text(filePath), new TagDataBean(null, true, true));
						return;
					}
					List<UserDefinedTag> tags = tdb.getTags();

					for (UserDefinedTag tag : tags) {

						String valueOf = String.valueOf(tag.getValue());
						// LOG.info(filePath + " > " + tag.getValue());
						tagParser.parseTagData(tag.getKey(), valueOf, false);
					}
				}
				LOG.info("Time for processing combiner tags : " + (System.currentTimeMillis() - ts));
				List<UserDefinedTag> combinerTags = new ArrayList<UserDefinedTag>();
				if (tagParser != null && tagParser.getCustomTagList() != null
						&& tagParser.getCustomTagList().size() > 0) {
					combinerTags.addAll(tagParser.getCustomTagList());
				}

				for (UserDefinedTag tag : combinerTags) {
					LOG.info("Combiner Tag: " + tag.getKey() + " = " + tag.getValue() + " (" + tag.getValue().getClass()
							+ ") " + ", " + tag.getTagClass());
				}

				TagDataBean tdb = new TagDataBean(combinerTags, true, false);

				context.write(new Text(filePath), tdb);

			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			throw new IOException(e);
		}
	}

}
