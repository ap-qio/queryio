package com.queryio.files.tagging.mbox;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.StaticUtilities;
import com.queryio.files.tagging.FileAnalyzer;

public class TestMBOXParser {
	public static void main(String[] args) throws Exception {
		JSONObject tagInfo = (JSONObject)new JSONParser().parse(StaticUtilities.getFileContents("/AppPerfect/Testing/QueryIO/EMLTagging"));
		Map<String, String> coreTags = new HashMap<String, String>();
		coreTags.put("FILEPATH","/eml/eml_missel.eml");
		coreTags.put("USERGROUP","queryio");

		FileAnalyzer analyzer = new FileAnalyzer(tagInfo, "eml", coreTags);
		
		InputStream is = null;
		try {
			is = new FileInputStream("/AppPerfect/QueryIO/demo/Data/eml/eml_missel.eml");
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
