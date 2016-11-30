package com.queryio.files.tagging.json;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.StaticUtilities;
import com.queryio.files.tagging.FileAnalyzer;

public class TestJSONParser {
	public static void main(String[] args) throws Exception {
		JSONObject tagInfo = (JSONObject) new JSONParser()
				.parse(StaticUtilities.getFileContents("/AppPerfect/Testing/QueryIO/KVTagging"));

		FileAnalyzer analyzer = new FileAnalyzer(tagInfo, "json", null);

		InputStream is = null;
		try {
			is = new FileInputStream("/AppPerfect/QueryIO/demo/samples/json_sample/json_1.json");
			analyzer.analyze(is);

			System.out.println("ParsedContent: ");

			HashMap<String, Object> tags = analyzer.getTags();
			Iterator<String> iter = tags.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				System.out.println(key + " : " + tags.get(key));
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
