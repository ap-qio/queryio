package com.queryio.demo.extended.metadata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.json.simple.JSONObject;

import com.queryio.plugin.extended.metadata.ColumnMetadata;
import com.queryio.plugin.extended.metadata.IUserDefinedParser;
import com.queryio.plugin.extended.metadata.TableMetadata;
import com.queryio.plugin.extended.metadata.UserDefinedTag;

public class PDFCustomTagParser implements IUserDefinedParser {
	List<UserDefinedTag> list = new ArrayList<UserDefinedTag>();
	String tableName = "";
	private static final Log LOG = LogFactory.getLog(PDFCustomTagParser.class);
	private static Map<String,TableMetadata> map = new HashMap<String, TableMetadata>();
	static {		
		// PDF
		List<ColumnMetadata> list = new ArrayList<ColumnMetadata>();
		list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
		list.add(new ColumnMetadata("TITLE", String.class, 128));		
		list.add(new ColumnMetadata("SUBJECT", String.class, 128));
		list.add(new ColumnMetadata("AUTHOR", String.class, 128));
		list.add(new ColumnMetadata("KEYWORDS", String.class, 1024));
		list.add(new ColumnMetadata("CREATOR", String.class, 128));
		list.add(new ColumnMetadata("PRODUCER", String.class, 128));
		list.add(new ColumnMetadata("CREATION_DATE", String.class, 128));
		list.add(new ColumnMetadata("LAST_MODIFIED", String.class, 128));
		list.add(new ColumnMetadata("TRAPPED", String.class, 64));
		map.put("pdf", new TableMetadata("PDF", list));
	}
	@Override
	public void parseStream(InputStream is, String fileExtension) throws Throwable {
		LOG.debug("New PDFCustomTagParser is now parsing document.");
		PDDocument doc = PDDocument.load(is);
		PDDocumentInformation info = doc.getDocumentInformation();
		Set<String> keys = info.getMetadataKeys();
		for(String key : keys){
			String value = info.getCustomMetadataValue(key);
			if(key != null && !key.isEmpty() && value != null && !value.isEmpty()){
				this.list.add(new UserDefinedTag(key, value));
				LOG.debug("Parser found tag: " + key + "\t" + value);
			}				
		}
		String key = "NPages";
		Object value = doc.getNumberOfPages();
		this.list.add(new UserDefinedTag(key, value));
		LOG.debug("Parser found tag: " + key + "\t" + value);
		LOG.debug("Total tags found by PDFCustomTagParser: " + this.list.size());
	}

	boolean completed = false;

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return list;
	}

	@Override
	public void setFilterExpression(String expression) {

	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		return map.get(fileExtension);
	}

	@Override
	public boolean updateDbSchema() {
		return false;
	}

	@Override
	public void parseStream(InputStream is, String fileExtension,
			JSONObject tagsJSON, Map<String, String> coreTags) throws Exception {
		
	}
}
