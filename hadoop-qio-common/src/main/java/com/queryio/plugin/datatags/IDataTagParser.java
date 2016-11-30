package com.queryio.plugin.datatags;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.apache.tika.metadata.Metadata;

public interface IDataTagParser {
	/**
	 * @return List of CustomTag
	 */
	public List<UserDefinedTag> getCustomTagList();

	/**
	 * Provide tableName, column names and column sql datatypes of fields being
	 * parsed by this parser for a particular filetype.
	 * 
	 * @return List of ColumnMetaData
	 */
	TableMetadata getTableMetaData(String fileExtension);

	/**
	 * Read InputStream and parse the content to extract CustomTags.
	 * 
	 * @param is
	 *            InputStream of file
	 * @throws Exception
	 */
	void parseStream(InputStream is, String fileExtension) throws Exception;

	/**
	 * Specifies if the framework needs to verify database schema for the tags
	 * being inserted.
	 * 
	 * @return
	 */
	boolean updateDbSchema();

	void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception;

	boolean parseMapRecord(String value, long offset) throws Exception;

	void parseTagData(String key, String value, boolean isReducer) throws Exception;

}
