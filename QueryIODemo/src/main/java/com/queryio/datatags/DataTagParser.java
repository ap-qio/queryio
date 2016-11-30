package com.queryio.datatags;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParsingReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.helpers.DefaultHandler;

import com.queryio.common.MetadataConstants;
import com.queryio.common.util.StaticUtilities;
import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.plugin.datatags.common.Tag;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class DataTagParser extends AbstractDataTagParser {

	public DataTagParser(JSONObject tagsJSON, Map<String, String> coreTags) {
		super(tagsJSON, coreTags, false); // false for tika
	}

	private static final Log LOG = LogFactory.getLog(DataTagParser.class);
	List<UserDefinedTag> list = new ArrayList<UserDefinedTag>();
	private static Map<String, TableMetadata> map = new HashMap<String, TableMetadata>();
	private static AutoDetectParser parser = new AutoDetectParser();

	static {
		try {
			LOG.info("FileContentParser Class is loaded");

			// PDF
			List<ColumnMetadata> list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("CREATED", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("XMP_CREATORTOOL", String.class,
			// 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			// list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			list.add(new ColumnMetadata("PRODUCER", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			list.add(new ColumnMetadata("TRAPPED", String.class, 64));
			map.put("pdf", new TableMetadata("PDF", list));

			// CSV
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("csv", new TableMetadata("CSV", list));

			// LOG
			map.put("log", new TableMetadata("LOG", list));

			// IMAGE
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("DATA_BITSPERSAMPLE", String.class, 64));
			list.add(new ColumnMetadata("COMPRESSION_LOSSLESS", String.class, 64));
			list.add(new ColumnMetadata("TIFF_BITSPERSAMPLE", String.class, 64));
			list.add(new ColumnMetadata("WIDTH", Integer.class));
			list.add(new ColumnMetadata("DIMENSION_IMAGEORIENTATION", String.class, 64));
			list.add(new ColumnMetadata("DIMENSION_PIXELASPECTRATIO", String.class, 64));
			list.add(new ColumnMetadata("COMPRESSION_COMPRESSIONTYPENAME", String.class, 64));
			list.add(new ColumnMetadata("TIFF_IMAGELENGTH", Long.class));
			list.add(new ColumnMetadata("DATA_SAMPLEFORMAT", String.class, 64));
			list.add(new ColumnMetadata("TRANSPARENCY_ALPHA", String.class, 64));
			list.add(new ColumnMetadata("HEIGHT", Integer.class));
			list.add(new ColumnMetadata("CHROMA_NUMCHANNELS", String.class, 64));
			list.add(new ColumnMetadata("COMPRESSION_NUMPROGRESSIVESCANS", String.class, 64));
			list.add(new ColumnMetadata("CHROMA_COLORSPACETYPE", String.class, 64));
			list.add(new ColumnMetadata("TIFF_IMAGEWIDTH", Integer.class));
			list.add(new ColumnMetadata("IHDR", String.class, 128));
			// list.add(new ColumnMetadata("ICCP", String.class, 128));
			list.add(new ColumnMetadata("DATA_PLANARCONFIGURATION", String.class, 64));
			list.add(new ColumnMetadata("CHROMA_BLACKISZERO", String.class, 64));
			// list.add(new ColumnMetadata("TEXT_TEXTENTRY", "VARCHAR(1024)"));

			map.put("3dm", new TableMetadata("3DM", list));
			map.put("3ds", new TableMetadata("3DS", list));
			map.put("max", new TableMetadata("MAX", list));
			map.put("obj", new TableMetadata("OBJ", list));
			map.put("bmp", new TableMetadata("BMP", list));
			map.put("dds", new TableMetadata("DDS", list));
			map.put("dng", new TableMetadata("DNG", list));
			map.put("gif", new TableMetadata("GIF", list));
			map.put("jpg", new TableMetadata("JPG", list));
			map.put("jpeg", new TableMetadata("JPEG", list));
			map.put("png", new TableMetadata("PNG", list));
			map.put("psd", new TableMetadata("PSD", list));
			map.put("pspimage", new TableMetadata("PSPIMAGE", list));
			map.put("tga", new TableMetadata("TGA", list));
			map.put("thm", new TableMetadata("THM", list));
			map.put("tif", new TableMetadata("TIF", list));
			map.put("yuv", new TableMetadata("YUV", list));
			map.put("ai", new TableMetadata("AI", list));
			map.put("eps", new TableMetadata("EPS", list));
			map.put("ps", new TableMetadata("PS", list));
			map.put("svg", new TableMetadata("SVG", list));

			// DOC
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("CP_REVISION", String.class, 128));
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TEMPLATE",
			// String.class, 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("WORD_COUNT", Integer.class));
			// list.add(new ColumnMetadata("EDIT_TIME", String.class, 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION",
			// String.class, 128));
			list.add(new ColumnMetadata("COMPANY", String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			// list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			list.add(new ColumnMetadata("REVISION_NUMBER", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			list.add(new ColumnMetadata("COMMENTS", String.class, 128));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			list.add(new ColumnMetadata("TEMPLATE", String.class, 128));
			list.add(new ColumnMetadata("W_COMMENTS", String.class, 128));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_CHARACTER_COUNT", String.class,
			// 128));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY", String.class, 128));
			// list.add(new ColumnMetadata("META_WORD_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("CHARACTER_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_PAGE_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("COMMENT", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			map.put("doc", new TableMetadata("DOC", list));

			// docx
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("CHARACTER_COUNT_WITH_SPACES", Integer.class));
			list.add(new ColumnMetadata("TOTAL_TIME", Timestamp.class));
			// list.add(new ColumnMetadata("DC_DESCRIPTION", String.class,
			// 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("DESCRIPTION", String.class, 128));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION", String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			list.add(new ColumnMetadata("PARAGRAPH_COUNT", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("REVISION_NUMBER", Integer.class));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("META_CHARACTER_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_WORD_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			// list.add(new ColumnMetadata("DC_PUBLISHER", String.class, 128));
			// list.add(new ColumnMetadata("META_PAGE_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			// list.add(new ColumnMetadata("CP_REVISION", String.class, 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("APPLICATION_VERSION", String.class, 128));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TEMPLATE",
			// String.class, 128));
			// list.add(new ColumnMetadata("META_LINE_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("PUBLISHER", String.class, 128));
			list.add(new ColumnMetadata("WORD_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_PARAGRAPH_COUNT", String.class,
			// 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPVERSION",
			// String.class, 128));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("LINE_COUNT", Integer.class));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("TEMPLATE", String.class, 128));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY",
			// String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TOTALTIME",
			// String.class, 128));
			list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("CHARACTER_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_CHARACTER_COUNT_WITH_SPACES",
			// String.class, 128));
			map.put("docx", new TableMetadata("DOCX", list));

			// XLS
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("CP_REVISION", String.class, 128));
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TEMPLATE",
			// String.class, 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EDIT_TIME", String.class, 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION",
			// String.class, 128));
			list.add(new ColumnMetadata("COMPANY", String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			// list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			list.add(new ColumnMetadata("REVISION_NUMBER", String.class, 128));
			// list.add(new ColumnMetadata("LAST_PRINTED", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			// list.add(new ColumnMetadata("META_PRINT_DATE", String.class,
			// 128));
			list.add(new ColumnMetadata("COMMENTS", String.class, 128));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("TEMPLATE", String.class, 128));
			// list.add(new ColumnMetadata("W_COMMENTS", String.class, 128));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY",
			// String.class, 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("COMMENT", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			map.put("xls", new TableMetadata("XLS", list));

			// XLSX
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			// list.add(new ColumnMetadata("APPLICATION_VERSION", String.class,
			// 128));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_DESCRIPTION", String.class,
			// 128));
			list.add(new ColumnMetadata("PUBLISHER", String.class, 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPVERSION",
			// String.class, 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			list.add(new ColumnMetadata("PROTECTED", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			list.add(new ColumnMetadata("DESCRIPTION", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION",
			// String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			// list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY",
			// String.class, 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			// list.add(new ColumnMetadata("DC_PUBLISHER", String.class, 128));
			// list.add(new ColumnMetadata("DC_IDENTIFIER", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			list.add(new ColumnMetadata("IDENTIFIER", String.class, 128));
			map.put("xlsx", new TableMetadata("XLSX", list));

			// PPT
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_SLIDE_COUNT", String.class,
			// 128));
			// list.add(new ColumnMetadata("CP_REVISION", String.class, 128));
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			// list.add(new ColumnMetadata("SLIDE_COUNT", String.class, 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TEMPLATE",
			// String.class, 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("WORD_COUNT", Integer.class));
			list.add(new ColumnMetadata("EDIT_TIME", Timestamp.class));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION",
			// String.class, 128));
			list.add(new ColumnMetadata("COMPANY", String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			// list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			list.add(new ColumnMetadata("REVISION_NUMBER", String.class, 128));
			// list.add(new ColumnMetadata("LAST_PRINTED", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			// list.add(new ColumnMetadata("META_PRINT_DATE", String.class,
			// 128));
			list.add(new ColumnMetadata("COMMENTS", String.class, 128));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("TEMPLATE", String.class, 128));
			list.add(new ColumnMetadata("W_COMMENTS", String.class, 128));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY",
			// String.class, 128));
			// list.add(new ColumnMetadata("META_WORD_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("COMMENT", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			map.put("ppt", new TableMetadata("PPT", list));

			// PPTX
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 128));
			list.add(new ColumnMetadata("SLIDE_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TOTAL_TIME", Timestamp.class));
			// list.add(new ColumnMetadata("DC_DESCRIPTION", String.class,
			// 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("DESCRIPTION", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPLICATION",
			// String.class, 128));
			// list.add(new ColumnMetadata("CP_CATEGORY", String.class, 128));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 128));
			list.add(new ColumnMetadata("PARAGRAPH_COUNT", Integer.class));
			list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 128));
			list.add(new ColumnMetadata("LAST_PRINTED", Timestamp.class));
			list.add(new ColumnMetadata("REVISION_NUMBER", String.class, 128));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_WORD_COUNT", String.class,
			// 128));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			// list.add(new ColumnMetadata("DC_PUBLISHER", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			list.add(new ColumnMetadata("PRESENTATION_FORMAT", String.class, 128));
			// list.add(new ColumnMetadata("META_SLIDE_COUNT", String.class,
			// 128));
			// list.add(new ColumnMetadata("CP_REVISION", String.class, 128));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 128));
			list.add(new ColumnMetadata("SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_NAME", String.class,
			// 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("APPLICATION_VERSION", String.class,
			// 128));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TEMPLATE",
			// String.class, 128));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("PUBLISHER", String.class, 128));
			list.add(new ColumnMetadata("WORD_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_PARAGRAPH_COUNT", String.class,
			// 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_APPVERSION",
			// String.class, 128));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("MANAGER", String.class, 128));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 128));
			// list.add(new ColumnMetadata("CP_SUBJECT", String.class, 128));
			// list.add(new ColumnMetadata("META_PRINT_DATE", String.class,
			// 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER",
			// String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_MODIFIED",
			// Timestamp.class));
			// list.add(new ColumnMetadata("TEMPLATE", String.class, 128));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY",
			// String.class, 128));
			// list.add(new
			// ColumnMetadata("EXTENDED_PROPERTIES_PRESENTATIONFORMAT",
			// String.class, 128));
			// list.add(new ColumnMetadata("EXTENDED_PROPERTIES_TOTALTIME",
			// String.class, 128));
			// list.add(new ColumnMetadata("CATEGORY", String.class, 128));
			map.put("pptx", new TableMetadata("PPTX", list));

			// EML
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			list.add(new ColumnMetadata("SUBJECT", String.class, 5000));
			list.add(new ColumnMetadata("MESSAGE_FROM", String.class, 128));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 128));
			// list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("MESSAGE_TO", String.class, 128));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			list.add(new ColumnMetadata("CREATOR", String.class, 128));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 128));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			list.add(new ColumnMetadata("DC_TITLE", String.class, 5000));
			map.put("eml", new TableMetadata("EML", list));

			// JSON
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("json", new TableMetadata("JSON", list));

			// TXT
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("txt", new TableMetadata("TXT", list));

			// XML
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("DESCRIPTION", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("xml", new TableMetadata("XML", list));

			// HTML
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("html", new TableMetadata("HTML", list));

			// RTF
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 64));
			map.put("rtf", new TableMetadata("RTF", list));

			// Electronic Publication Format FOR BOTH NON IMAGE AND IMAGE
			list = new ArrayList<ColumnMetadata>();
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 64));
			list.add(new ColumnMetadata("SUBJECT", String.class, 64));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 64));
			list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("RIGHTS", String.class, 64));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("CREATOR", String.class, 64));
			// list.add(new ColumnMetadata("DC_RIGHTS", String.class, 64));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 64));
			// list.add(new ColumnMetadata("META_KEYWORD", String.class, 64));
			list.add(new ColumnMetadata("LANGUAGE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_IDENTIFIER", String.class, 64));
			list.add(new ColumnMetadata("IDENTIFIER", String.class, 64));
			list.add(new ColumnMetadata("KEYWORD", String.class, 64));
			// list.add(new ColumnMetadata("DC_LANGUAGE", String.class, 64));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			map.put("epub", new TableMetadata("EPUB", list));

			// MID files kind of a music file
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("PATCHES", String.class, 64));
			list.add(new ColumnMetadata("DIVISION_TYPE", String.class, 64));
			list.add(new ColumnMetadata("TRACKS", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			map.put("mid", new TableMetadata("MID", list));

			// MP3 files , ogg files,
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("XMPDM_RELEASE_DATE", Integer.class));
			list.add(new ColumnMetadata("XMPDM_AUDIO_CHANNEL_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_ALBUM", String.class, 64));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_ARTIST", String.class, 64));
			list.add(new ColumnMetadata("CHANNELS", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_AUDIO_SAMPLE_RATE", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_LOG_COMMENT", String.class, 64));
			list.add(new ColumnMetadata("VERSION", String.class, 64));
			list.add(new ColumnMetadata("CREATOR", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_COMPOSER", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_AUDIO_COMPRESSOR", String.class, 64));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("SAMPLE_RATE", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_GENRE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			map.put("mp3", new TableMetadata("MP3", list));
			map.put("ogg", new TableMetadata("OGG", list));

			// CLASS files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("RESURCE_NAME", String.class, 64));
			list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			map.put("class", new TableMetadata("CLASS", list));

			// jar , zip , tar , rpm , dvi files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			map.put("jar", new TableMetadata("JAR", list));
			map.put("zip", new TableMetadata("ZIP", list));
			map.put("tar", new TableMetadata("TAR", list));
			map.put("rpm", new TableMetadata("RPM", list));
			map.put("dvi", new TableMetadata("DVI", list));

			// ODT files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("IMAGE_COUNT", Integer.class));
			list.add(new ColumnMetadata("EDITING_CYCLES", String.class, 64));
			list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			list.add(new ColumnMetadata("CREATOR", String.class, 64));
			list.add(new ColumnMetadata("NBOBJECT", String.class, 64));
			list.add(new ColumnMetadata("WORD_COUNT", Integer.class));
			list.add(new ColumnMetadata("EDIT_TIME", Timestamp.class));
			// list.add(new ColumnMetadata("META_PARAGRAPH_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("OBJECT_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("NBIMG", String.class, 64));
			// list.add(new ColumnMetadata("META_OBJECT_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("GENERATOR", String.class, 64));
			list.add(new ColumnMetadata("PARAGRAPH_COUNT", Timestamp.class));
			list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("DCTERMS_MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_CHARACTER_COUNT", String.class,
			// 64));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 64));
			list.add(new ColumnMetadata("NBTAB", Integer.class));
			// list.add(new ColumnMetadata("META_WORD_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_TABLE_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_IMAGE_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("XMPTPG_NPAGES", Integer.class));
			list.add(new ColumnMetadata("TABLE_COUNT", Integer.class));
			list.add(new ColumnMetadata("NBPARA", Integer.class));
			list.add(new ColumnMetadata("CHARACTER_COUNT", Integer.class));
			// list.add(new ColumnMetadata("META_PAGE_COUNT", String.class,
			// 64));
			list.add(new ColumnMetadata("LANGUAGE", String.class, 64));
			list.add(new ColumnMetadata("NBWORD", Integer.class));
			list.add(new ColumnMetadata("NBPAGE", Integer.class));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_LANGUAGE", String.class, 64));
			list.add(new ColumnMetadata("NBCHARACTER", String.class, 64));
			map.put("odt", new TableMetadata("ODT", list));
			map.put("ods", new TableMetadata("ODS", list));

			// mpp files microsoft project files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("CP_REVISION", String.class, 64));
			// list.add(new ColumnMetadata("META_LAST_AUTHOR", String.class,
			// 64));
			list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_SUBJECT", String.class, 64));
			list.add(new ColumnMetadata("LAST_AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("SUBJECT", String.class, 64));
			list.add(new ColumnMetadata("APPLICATION_NAME", String.class, 64));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES", String.class, 64));
			list.add(new ColumnMetadata("CREATOR", String.class, 64));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			// list.add(new ColumnMetadata("META_AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("MANAGER", String.class, 64));
			list.add(new ColumnMetadata("CP_SUBJECT", String.class, 64));
			list.add(new ColumnMetadata("META", String.class, 64));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES", String.class, 64));
			list.add(new ColumnMetadata("COMPANY", String.class, 64));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 64));
			list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			list.add(new ColumnMetadata("REVISION_NUMBER", String.class, 64));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES_MANAGER", String.class, 64));
			list.add(new ColumnMetadata("COMMENTS", String.class, 64));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			list.add(new ColumnMetadata("DCTERMS_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("TEMPLATE", String.class, 64));
			list.add(new ColumnMetadata("W_COMMENTS", String.class, 64));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 64));
			list.add(new ColumnMetadata("EXTENDED_PROPERTIES_COMPANY", String.class, 64));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("CATEGORY", String.class, 64));
			list.add(new ColumnMetadata("COMMENT", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			map.put("mpp", new TableMetadata("MPP", list));

			// flac files type of audio files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("TAGGING_TIME", Timestamp.class));
			list.add(new ColumnMetadata("VENDOR", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_AUDIO_CHANNEL_TYPE", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_ALBUM", String.class, 128));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_ARTIST", String.class, 64));
			list.add(new ColumnMetadata("ITUNESCOMPILATION", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_AUDIOSAMPLERATE", String.class, 64));
			list.add(new ColumnMetadata("COPYRIGHT", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_LOG_COMMENT", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_TRACK_NUMBER", String.class, 64));
			list.add(new ColumnMetadata("PUBLISHER", String.class, 64));
			list.add(new ColumnMetadata("DISC", String.class, 64));
			list.add(new ColumnMetadata("COMPOSER", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_AUDIO_COMPRESSOR", String.class, 64));
			list.add(new ColumnMetadata("TITLE", String.class, 128));
			list.add(new ColumnMetadata("MUSICIAN_CREDITS_LIST", String.class, 64));
			list.add(new ColumnMetadata("PERFORMER", String.class, 64));
			list.add(new ColumnMetadata("XMPDM_GENRE", String.class, 64));
			list.add(new ColumnMetadata("LC", String.class, 64));
			list.add(new ColumnMetadata("YEAR", Timestamp.class));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			map.put("flac", new TableMetadata("FLAC", list));

			// mp4 files type of audio files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("DCTERMS_MODIFIED", Timestamp.class));
			// list.add(new ColumnMetadata("META_CREATION_DATE",
			// Timestamp.class));
			list.add(new ColumnMetadata("META_SAVE_DATE", Timestamp.class));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("XMPDM_AUDIO_SAMPLE_RATE", String.class, 64));
			list.add(new ColumnMetadata("DATE", Timestamp.class));
			list.add(new ColumnMetadata("MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("TIFF_IMAGELENGTH", Long.class));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("TIFF_IMAGE_WIDTH", Long.class));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("LAST_SAVE_DATE", Timestamp.class));
			map.put("mp4", new TableMetadata("MP4", list));

			// dll. dynamic link library for microsoft windows
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("MACHINE_MACHINE_TYPE", String.class, 64));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("MACHINE_ENDIAN", String.class, 64));
			list.add(new ColumnMetadata("MACHINE_PLATFORM", String.class, 64));
			list.add(new ColumnMetadata("MACHINE_ARCHITECTURE_BITS", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			map.put("dll", new TableMetadata("DLL", list));

			// flv files
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("AUDIO_CODEC_ID", String.class, 64));
			list.add(new ColumnMetadata("WIDTH", Integer.class));
			list.add(new ColumnMetadata("VIDEO_DATA_RATE", String.class, 64));
			list.add(new ColumnMetadata("CAN_SEEK_TO_END", String.class, 64));
			list.add(new ColumnMetadata("VIDEO_CODEC_ID", String.class, 64));
			list.add(new ColumnMetadata("DURATION", Double.class));
			list.add(new ColumnMetadata("HAS_VIDEO", String.class, 64));
			list.add(new ColumnMetadata("AUDIO_DATA_RATE", String.class, 64));
			list.add(new ColumnMetadata("HEIGHT", Integer.class));
			list.add(new ColumnMetadata("HAS_AUDIO", String.class, 64));
			list.add(new ColumnMetadata("AUDIO_DELAY", Double.class));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			list.add(new ColumnMetadata("FRAME_RATE", String.class, 64));
			map.put("flv", new TableMetadata("FLV", list));

			// PAGES MAC BASED DOC TYPE FILES
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("OS_VERSION", String.class, 64));
			list.add(new ColumnMetadata("SL_CREATION_LOCALE_PROPERTY", String.class, 64));
			list.add(new ColumnMetadata("CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("CT_VERSION", String.class, 64));
			list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			list.add(new ColumnMetadata("META_CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("ATSU_VERSION", String.class, 64));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("COPYRIGHT", String.class, 64));
			list.add(new ColumnMetadata("LANGUAGE", String.class, 64));
			list.add(new ColumnMetadata("COMMENT", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_LANGUAGE", String.class, 64));
			// list.add(new ColumnMetadata("kSFWP_HasHeaders_Property",
			// String.class, 64));
			list.add(new ColumnMetadata("ARRAY_PAGES", String.class, 64));
			list.add(new ColumnMetadata("DCTERMS_CREATED", Timestamp.class));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("SHOW_PAGE_GUIDES", String.class, 64));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			list.add(new ColumnMetadata("DECIMAL_TAB", String.class, 64));
			map.put("pages", new TableMetadata("PAGES", list));

			// key MAC BASED ppt TYPE FILES
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("SLIDE_COUNT", Integer.class));
			list.add(new ColumnMetadata("SLIDES_HEIGHT", Integer.class));
			list.add(new ColumnMetadata("SLIDES_WIDTH", Integer.class));
			list.add(new ColumnMetadata("META_CREATION_DATE", Timestamp.class));
			list.add(new ColumnMetadata("LAST_MODIFIED", Timestamp.class));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("COPYRIGHT", String.class, 64));
			list.add(new ColumnMetadata("LANGUAGE", String.class, 64));
			list.add(new ColumnMetadata("COMMENT", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_LANGUAGE", String.class, 64));
			// list.add(new ColumnMetadata("kSFWP_HasHeaders_Property",
			// String.class, 64));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			map.put("key", new TableMetadata("KEY", list));

			// key MAC BASED xls TYPE FILES
			list = new ArrayList<ColumnMetadata>();
			list.add(new ColumnMetadata("COMMENTS", String.class, 64));
			list.add(new ColumnMetadata("KEYWORDS", String.class, 64));
			// list.add(new ColumnMetadata("W_COMMENTS", String.class, 64));
			list.add(new ColumnMetadata("PAGE_COUNT", Integer.class));
			// list.add(new ColumnMetadata("DC_CREATOR", String.class, 64));
			list.add(new ColumnMetadata("AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("COPYRIGHT", String.class, 64));
			list.add(new ColumnMetadata("SHEET_NAMES", String.class, 64));
			list.add(new ColumnMetadata("CREATOR", String.class, 64));
			list.add(new ColumnMetadata("PROJECTS", String.class, 64));
			list.add(new ColumnMetadata("TITLE", String.class, 64));
			list.add(new ColumnMetadata("META_AUTHOR", String.class, 64));
			list.add(new ColumnMetadata("COMMENT", String.class, 64));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 64));
			// list.add(new ColumnMetadata("DC_TITLE", String.class, 64));
			list.add(new ColumnMetadata("LANGUAGE", String.class, 64));
			map.put("numbers", new TableMetadata("NUMBERS", list));

		} catch (Throwable t) {
			LOG.fatal("Error during initialization", t);
		}
	}

	public static void main(String[] args) throws Exception {
		// InputStream is = new
		// FileInputStream("/Users/prasoon/QueryIO_v20/demo/Data/pdf/Doc_1352993877980.pdf");
		// InputStream is = new
		// FileInputStream("/Users/prasoon/QueryIO_v20/demo/Data/eml/eml_consolement.eml");
		// InputStream is = new
		// FileInputStream("/Users/prasoon/QueryIO_v20/demo/Data/png/Img_1350469911625.png");
		// InputStream is = new
		// FileInputStream("/Users/indravardhan/Documents/AOS-Baseline/ASPEN_BF_214929141107.jtl");
		InputStream is = new FileInputStream(
				"/Users/indravardhan/Desktop/ASPEN_BF_1438017814.jtl/ASPEN_BF_1438017814_87.jtl");

		JSONObject tagsJSON = (JSONObject) new JSONParser()
				.parse(StaticUtilities.getFileContents("/Users/indravardhan/temp.json"));

		Map<String, String> coreTags = new HashMap<String, String>();

		coreTags.put("FILEPATH", "/JTL/json/json_7.doc");
		// coreTags.put("FILEPATH","/csv/MachineLogs_1364454240895.csv");

		// DataTagParser parser = new DataTagParser(tagsJSON, coreTags);
		CSVDataParser parser = new CSVDataParser(tagsJSON, coreTags);
		// JSONDataParser parser = new JSONDataParser(tagsJSON, coreTags);

		parser.parseStream(is, "jtl");
		// parser.parseStream(is, null);
		// parser.parseStream(is, "eml");
		// parser.parseStream(is, "png");

		List<UserDefinedTag> tags = parser.getCustomTagList();
		for (int i = 0; i < tags.size(); i++) {
			UserDefinedTag x = tags.get(i);
		}
	}

	@Override
	public void parseStream(InputStream is, String fileExtension) throws Exception {
		TableMetadata tableMetadata = map.get(fileExtension);

		Metadata metadata = new Metadata();
		LOG.debug("TikaCustomTagParser is now parsing document.");

		if (tagsJSON == null) // For default tags by tika.
		{
			DefaultHandler handler = new DefaultHandler();

			parser.parse(is, handler, metadata);

			addMetadata(tableMetadata, metadata);
		} else {
			ParseContext parseContext = new ParseContext();

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new ParsingReader(parser, is, metadata, parseContext));

				parse(reader, tableMetadata, metadata);

			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			addMetadataAll(tableMetadata, metadata);
		}
	}

	private Object convertStringValueToClass(ColumnMetadata columnMetadata, String value) {
		if (columnMetadata.getColumnSqlDataType() == MetadataConstants.TIMESTAMP_WRAPPER_CLASS) {
			return UserDefinedTagDAO.getTimestamp(value);
		} else if (columnMetadata.getColumnSqlDataType() == MetadataConstants.STRING_WRAPPER_CLASS) {
			return value;
		} else {
			try {
				Method method = columnMetadata.getColumnSqlDataType().getMethod("valueOf", String.class);
				return method.invoke(columnMetadata.getColumnSqlDataType(), value);
			} catch (Exception e) {
				LOG.fatal("Error converting String to corresponding class.", e);
			}
		}
		return null;
	}

	private void addMetadata(TableMetadata tableMetadata, Metadata metadata) {
		for (int i = 0; i < metadata.names().length; i++) {
			String key = metadata.names()[i];
			String value = metadata.get(key);
			key = key.replaceAll("[^a-zA-Z0-9]+", "_");
			key = key.replace("-", "_");
			key = key.replace(".", "_");
			key = key.replace(" ", "_");
			key = key.toUpperCase();

			ColumnMetadata columnMetadata = tableMetadata.getColumnMetadataByColumnName(key);
			if (columnMetadata != null) {
				if (value != null && !value.isEmpty()) {
					this.list.add(new UserDefinedTag(key, convertStringValueToClass(columnMetadata, value)));
					LOG.debug("Parser found tag: " + key + "\t" + value);
				}
			} else {
				this.list.add(new UserDefinedTag(key, value));
			}
		}
		LOG.debug("Total tags found by TikaCustomTagParser: " + this.list.size());
	}

	private void addMetadataAll(TableMetadata tableMetadata, Metadata metadata) {
		for (int i = 0; i < metadata.names().length; i++) {
			String key = metadata.names()[i];
			String value = metadata.get(key);
			key = key.replaceAll("[^a-zA-Z0-9]+", "_");
			key = key.replace("-", "_");
			key = key.replace(".", "_");
			key = key.replace(" ", "_");
			key = key.toUpperCase();

			// new UserDefinedTag(TableMetadata.DEFAULT_TAG_ACCESSTIME, new
			// Timestamp(status.getAccessTime()))
			ColumnMetadata columnMetadata = tableMetadata.getColumnMetadataByColumnName(key);
			if (columnMetadata != null) {
				if (value != null && !value.isEmpty()) {
					this.list.add(new UserDefinedTag(key, convertStringValueToClass(columnMetadata, value)));
					LOG.debug("Parser found tag: " + key + "\t" + value);
				}
			} else {
				this.list.add(new UserDefinedTag(key, value));
			}
		}
		LOG.debug("Total tags found by TikaCustomTagParser: " + this.list.size());
	}

	@Override
	public List<UserDefinedTag> getCustomTagList() {
		return list;
	}

	@Override
	public TableMetadata getTableMetaData(String fileExtension) {
		if (map.get(fileExtension.toLowerCase()) == null) {
			ArrayList list = new ArrayList();
			list.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 512));
			list.add(new ColumnMetadata("CONTENT_TYPE", String.class, 128));
			map.put(fileExtension.toLowerCase(), new TableMetadata(fileExtension.toUpperCase(), list));
		}
		return map.get(fileExtension);
	}

	@Override
	public boolean updateDbSchema() {
		return false;
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception {
		List<Tag> tags = super.getTags();

		for (int i = 0; i < tags.size(); i++) {
			Tag tag = tags.get(i);

			setMethod(tag, tag.getSubstitutionValue(), false);

			wordMatchCount.put(tag.getName(), 0l);
			patternMatchCount.put(tag.getName(), 0l);
			totalWordCount.put(tag.getName(), 0l);
			totalLineCount.put(tag.getName(), 0l);
		}

		BufferedReader br = new BufferedReader(reader);

		String str = null;

		while ((str = br.readLine()) != null) {
			try {
				parseLine(str, metadata);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		alterMetadata(tableMetadata, metadata);

		HashMap<String, String> curValueMap = new HashMap<String, String>();

		if (coreTags != null) {
			Iterator<String> it = coreTags.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				curValueMap.put(key, coreTags.get(key));
			}
		}

		for (int i = 0; i < metadata.names().length; i++) {
			String key = metadata.names()[i];
			String value = metadata.get(key);

			curValueMap.put(key, value);
		}

		this.evaluateCurrentEntry(curValueMap);

		this.addMetaData(metadata);
	}

	@Override
	public boolean parseMapRecord(String value, long offset) throws Exception {
		return false;
	}
}