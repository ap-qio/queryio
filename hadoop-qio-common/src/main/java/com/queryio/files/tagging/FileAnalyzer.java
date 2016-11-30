package com.queryio.files.tagging;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.queryio.common.util.AppLogger;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.apachelog.ApacheLogFileParser;
import com.queryio.files.tagging.csv.CSVFileParser;
import com.queryio.files.tagging.iislog.IISLogFileParser;
import com.queryio.files.tagging.json.JSONFileParser;
import com.queryio.files.tagging.keyvaluepair.KVPairFileParser;
import com.queryio.files.tagging.log.Log4jFileParser;
import com.queryio.files.tagging.mbox.MBOXFileParser;

public class FileAnalyzer {
	Logger logger = Logger.getLogger(this.getClass());
	
	private IFileParser parser;
	private Map<String, String> coreTags;
	
	public FileAnalyzer(JSONObject tagInfo, String fileType, Map<String, String> coreTags) throws IOException {
		this.coreTags = coreTags;
		Class parserClass = getParser(fileType);
		if(parserClass!=null) {
			try {
				parser = (IFileParser) parserClass.getConstructor(JSONObject.class).newInstance(tagInfo);
			} catch(Exception e) {
				AppLogger.getLogger().fatal("Parser class could not be initialized.");
//				throw new IOException("Parser class could not be initialized,", e);
			}
		}
	}
	
	public Class getParser(String fileType) {
		if(fileType.equalsIgnoreCase("csv")) {
			return CSVFileParser.class;
		} else if(fileType.equalsIgnoreCase("log")) {
			return Log4jFileParser.class;
		}else if(fileType.equalsIgnoreCase("apachelog")) {
			return ApacheLogFileParser.class;
		}else if(fileType.equalsIgnoreCase("iislog")) {
			return IISLogFileParser.class;
		}
		else if(fileType.equalsIgnoreCase("txt")) {
			return KVPairFileParser.class;
		} else if(fileType.equalsIgnoreCase("json")) {
			return JSONFileParser.class;
		} else if(fileType.equalsIgnoreCase("eml")) {
			return MBOXFileParser.class;
		}
		return null;
	}
	
	public void analyze(InputStream is) throws Exception {
		if(parser != null)
		{
			parser.parse(is, coreTags);
		}
	}
	
	public HashMap<String, Object> getTags() {
		if(parser!=null) {
			return parser.getResultTags();
		} 
		return null;
	}
}
