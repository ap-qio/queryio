package com.queryio.files.tagging;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.StaticUtilities;

public class TestParser {
	public static void main(String[] args) throws Exception {
		JSONObject tagInfo = (JSONObject)new JSONParser().parse(StaticUtilities.getFileContents("/AppPerfect/Testing/QueryIO/JSONTagging3"));
		Map<String, String> coreTags = new HashMap<String, String>();
		coreTags.put("FILEPATH","/dev/MachineLogs_1364454240895.csv");
		FileAnalyzer analyzer = new FileAnalyzer(tagInfo, "csv", coreTags);
		
		InputStream is = null;
		try {
			is = new FileInputStream("/AppPerfect/QueryIO/demo/Data/csv/MachineLogs_1364454240895.csv");
			analyzer.analyze(is);
			
		
			System.out.println("ParsedContent: ");
			
			HashMap<String, Object> tags = analyzer.getTags();
			Iterator<String> iter = tags.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				System.out.println(key + " : " + tags.get(key));
			}
		} finally {
			if(is!=null) {
				is.close();
			}
		}
	}
}
