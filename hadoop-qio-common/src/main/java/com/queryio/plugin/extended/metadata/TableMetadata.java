package com.queryio.plugin.extended.metadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.queryio.common.MetadataConstants;


public class TableMetadata {
	
	public static final String DEFAULT_TAG_FILEPATH = "FILEPATH";
	public static final Class DEFAULT_TAG_FILEPATH_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_ACCESSTIME = "ACCESSTIME";
	public static final Class DEFAULT_TAG_ACCESSTIME_SQLDATATYPE = MetadataConstants.TIMESTAMP_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_MODIFICATIONTIME = "MODIFICATIONTIME";
	public static final Class DEFAULT_TAG_MODIFICATIONTIME_SQLDATATYPE = MetadataConstants.TIMESTAMP_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_OWNER = "OWNER";
	public static final Class DEFAULT_TAG_OWNER_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_GROUP = "USERGROUP";
	public static final Class DEFAULT_TAG_GROUP_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_PERMISSION = "PERMISSION";
	public static final Class DEFAULT_TAG_PERMISSION_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_BLOCKSIZE = "BLOCKSIZE";
	public static final Class DEFAULT_TAG_BLOCKSIZE_SQLDATATYPE = MetadataConstants.LONG_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_REPLICATION = "REPLICATION";
	public static final Class DEFAULT_TAG_REPLICATION_SQLDATATYPE = MetadataConstants.SHORT_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_LENGTH = "LEN";
	public static final Class DEFAULT_TAG_LENGTH_SQLDATATYPE = MetadataConstants.LONG_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_COMPRESSION_TYPE = "COMPRESSION_TYPE";
	public static final Class DEFAULT_TAG_COMPRESSION_TYPE_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
	public static final Class DEFAULT_TAG_ENCRYPTION_TYPE_SQLDATATYPE = MetadataConstants.STRING_WRAPPER_CLASS;
	public static final String DEFAULT_TAG_BLOCKS = "BLOCKS";
	public static final Class DEFAULT_TAG_BLOCKS_SQLDATATYPE = MetadataConstants.BLOB_WRAPPER_CLASS;
	
	private String tableName;
	private LinkedHashMap<String,ColumnMetadata> map;
	public TableMetadata(String tableName,List<ColumnMetadata> columnData) {		
		this.tableName = tableName;
		map = new LinkedHashMap<String, ColumnMetadata>();
		
		map.put(DEFAULT_TAG_FILEPATH, new ColumnMetadata(DEFAULT_TAG_FILEPATH, DEFAULT_TAG_FILEPATH_SQLDATATYPE, 5000));
		map.put(DEFAULT_TAG_LENGTH, new ColumnMetadata(DEFAULT_TAG_LENGTH, DEFAULT_TAG_LENGTH_SQLDATATYPE));
		map.put(DEFAULT_TAG_OWNER, new ColumnMetadata(DEFAULT_TAG_OWNER, DEFAULT_TAG_OWNER_SQLDATATYPE, 255));
		map.put(DEFAULT_TAG_GROUP, new ColumnMetadata(DEFAULT_TAG_GROUP, DEFAULT_TAG_GROUP_SQLDATATYPE, 255));
		map.put(DEFAULT_TAG_ACCESSTIME, new ColumnMetadata(DEFAULT_TAG_ACCESSTIME, DEFAULT_TAG_ACCESSTIME_SQLDATATYPE));
		map.put(DEFAULT_TAG_MODIFICATIONTIME, new ColumnMetadata(DEFAULT_TAG_MODIFICATIONTIME, DEFAULT_TAG_MODIFICATIONTIME_SQLDATATYPE));
		map.put(DEFAULT_TAG_PERMISSION, new ColumnMetadata(DEFAULT_TAG_PERMISSION, DEFAULT_TAG_PERMISSION_SQLDATATYPE, 23));
		map.put(DEFAULT_TAG_BLOCKSIZE, new ColumnMetadata(DEFAULT_TAG_BLOCKSIZE, DEFAULT_TAG_BLOCKSIZE_SQLDATATYPE));
		map.put(DEFAULT_TAG_REPLICATION, new ColumnMetadata(DEFAULT_TAG_REPLICATION, DEFAULT_TAG_REPLICATION_SQLDATATYPE));
		map.put(DEFAULT_TAG_COMPRESSION_TYPE, new ColumnMetadata(DEFAULT_TAG_COMPRESSION_TYPE, DEFAULT_TAG_COMPRESSION_TYPE_SQLDATATYPE, 64));
		map.put(DEFAULT_TAG_ENCRYPTION_TYPE, new ColumnMetadata(DEFAULT_TAG_ENCRYPTION_TYPE, DEFAULT_TAG_ENCRYPTION_TYPE_SQLDATATYPE, 64));
		map.put(DEFAULT_TAG_BLOCKS, new ColumnMetadata(DEFAULT_TAG_BLOCKS, DEFAULT_TAG_BLOCKS_SQLDATATYPE));

		if(columnData != null){
			for(ColumnMetadata columnMetadata : columnData){
				map.put(columnMetadata.getColumnName(), columnMetadata);
			}
		}
		
	}

	public List<ColumnMetadata> getColumnData() {
		List<ColumnMetadata> list= new ArrayList<ColumnMetadata>();
		list.addAll(map.values());
		return list;
	}
	public String getTableName() {
		return tableName;
	}
	public List<String> getColumns(){
		List<String> list= new ArrayList<String>();
		list.addAll(map.keySet());
		return list;
	}
	public ColumnMetadata getColumnMetadataByColumnName(String columnName){
		return map.get(columnName.toUpperCase());
	}
}
