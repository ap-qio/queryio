package com.queryio.demo.extended.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.queryio.plugin.extended.metadata.ColumnMetadata;
import com.queryio.plugin.extended.metadata.IUserDefinedParser;
import com.queryio.plugin.extended.metadata.TableMetadata;
import com.queryio.plugin.extended.metadata.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.WikiPage;

public class WikiTextParser implements IUserDefinedParser {

	List<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();
	String tableName = "WIKI";

	@Override
	public void parseStream(InputStream stream, String fileExtension) throws IOException, Exception {
		String wikiText = null;

		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer);
		wikiText = writer.toString();
		String category = "";
		try {
			WikiPage page = new WikiPage();
			page.setWikiText(wikiText);
			parseInfoBox(page.getInfoBox());
			category = getCategory(page.getInfoBox());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (category.trim().isEmpty())
			tableName = "WIKI_UNKNOWN";
		else
			tableName = "WIKI_" + category.trim().toUpperCase();
	}

	public void parseInfoBox(InfoBox infoBox) {
		if (infoBox == null)
			return;
		int parenthesisCount = 0;
		List<String> strings = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (char c : infoBox.dumpRaw().toCharArray()) {
			if (c == '{' || c == '[') {
				parenthesisCount++;
			} else if (c == '}' || c == ']') {
				parenthesisCount--;
			} else if (c == '|') {
				if (parenthesisCount == 2) {
					if (sb.length() != 0) {
						strings.add(sb.toString());
						sb.setLength(0);
					}
				} else
					sb.append(c);
			} else if (c == '\n' || c == '\r') {
				// ignore
			} else {
				sb.append(c);
			}
		}
		String key, value;
		for (String str : strings) {
			if (str.contains("=")) {
				String[] keyValue = str.split("=");
				if (keyValue.length > 1 && !(key = keyValue[0].trim()).isEmpty()
						&& !(value = keyValue[1].trim()).isEmpty()
						&& value.length() < UserDefinedTagUtils.MAX_COL_SIZE) {
					key = "COL_" + key.replaceAll("[']+", "");
					value = value.replaceAll("[']+", "");
					UserDefinedTag tag = new UserDefinedTag(key, value);
					if (!tags.contains(tag))
						tags.add(tag);
				}
			}
		}
	}

	public static String getCategory(InfoBox infoBox) {
		if (infoBox != null) {
			StringBuilder sb = new StringBuilder();
			for (char c : infoBox.dumpRaw().toCharArray()) {
				if (c == '|') {
					break;
				} else {
					sb.append(c);
				}
			}
			String str = sb.toString();

			if (str.length() > 0 && str.contains("Infobox")) {
				String[] arr = str.split("Infobox");
				if (arr.length > 1)
					return arr[1].trim().replaceAll("  ", " ").replaceAll("[^a-zA-Z0-9 ]", "");
			}
		}
		return "Unknown";
	}

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return tags;
	}

	@Override
	public void setFilterExpression(String expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return new TableMetadata(tableName, new ArrayList<ColumnMetadata>());
	}

	@Override
	public boolean updateDbSchema() {
		return true;
	}

	@Override
	public void parseStream(InputStream is, String fileExtension, JSONObject tagsJSON, Map<String, String> coreTags)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
