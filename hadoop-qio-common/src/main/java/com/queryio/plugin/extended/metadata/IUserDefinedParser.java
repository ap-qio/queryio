package com.queryio.plugin.extended.metadata;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public interface IUserDefinedParser {
	/**
	 * @return List of CustomTag
	 */
	List<UserDefinedTag> getCustomTagList();

	/**
	 * Provide tableName, column names and column sql datatypes of fields being
	 * parsed by this parser for a particular filetype.
	 * 
	 * @return List of ColumnMetaData
	 */
	TableMetadata getTableMetaData(String fileExtension);

	/**
	 * Sets Filter Expression that the parser should use to filter records from
	 * the file.
	 * 
	 * @param expression
	 */
	void setFilterExpression(String expression);

	/**
	 * Read InputStream and parse the content to extract CustomTags.
	 * 
	 * @param is
	 *            InputStream of file
	 * @throws Exception
	 */
	void parseStream(InputStream stream, String fileExtension) throws Throwable;

	/**
	 * Specifies if the framework needs to verify database schema for the tags
	 * being inserted.
	 * 
	 * @return
	 */
	boolean updateDbSchema();

	void parseStream(InputStream is, String fileExtension, JSONObject tagsJSON, Map<String, String> coreTags)
			throws Exception;
}
