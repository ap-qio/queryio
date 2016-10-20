package com.queryio.common;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


public interface MetadataConstants 
{
	String STRING_TYPE_METADATA = "db.datatype.STRING";
	String TIMESTAMP_TYPE_METADATA = "db.datatype.TIMESTAMP";
	String LONG_TYPE_METADATA = "db.datatype.LONG";
	String SHORT_TYPE_METADATA = "db.datatype.SHORT";
	String INTEGER_TYPE_METADATA = "db.datatype.INTEGER";
	String REAL_TYPE_METADATA = "db.datatype.REAL";
	String DOUBLE_TYPE_METADATA = "db.datatype.DOUBLE";
	String DECIMAL_TYPE_METADATA = "db.datatype.DECIMAL";
	String BOOLEAN_TYPE_METADATA = "db.datatype.BOOLEAN";
	String BLOB_TYPE_METADATA = "db.datatype.BLOB";
	String DEFAULT_SCHEMA = "db.defaultschema";

	Class STRING_WRAPPER_CLASS = String.class;
	Class TIMESTAMP_WRAPPER_CLASS = Timestamp.class;
	Class LONG_WRAPPER_CLASS = Long.class;
	Class SHORT_WRAPPER_CLASS = Short.class;
	Class INTEGER_WRAPPER_CLASS = Integer.class;
	Class REAL_WRAPPER_CLASS = Double.class;	// TODO no unique java.lang.* class was found so used this.
	Class DOUBLE_WRAPPER_CLASS = Double.class;
	Class DECIMAL_WRAPPER_CLASS = Float.class;
	Class BOOLEAN_WRAPPER_CLASS = Boolean.class;
	Class BLOB_WRAPPER_CLASS = Blob.class;
	
	String DB_CONFIG_STRING_ENTRY_TAG = "datatypeString";
	String DB_CONFIG_TIMESTAMP_ENTRY_TAG = "datatypeTimestamp";
	String DB_CONFIG_LONG_ENTRY_TAG = "datatypeLong";
	String DB_CONFIG_SHORT_ENTRY_TAG = "datatypeShort";
	String DB_CONFIG_INTEGER_ENTRY_TAG = "datatypeInteger";
	String DB_CONFIG_REAL_ENTRY_TAG = "datatypeReal";
	String DB_CONFIG_DOUBLE_ENTRY_TAG = "datatypeDouble";
	String DB_CONFIG_DECIMAL_ENTRY_TAG = "datatypeDecimal";
	String DB_CONFIG_BOOLEAN_ENTRY_TAG = "datatypeBoolean";
	String DB_CONFIG_BLOB_ENTRY_TAG = "datatypeBlob";
	String DB_CONFIG_DEFAULT_SCHEMA_ENTRY_TAG = "defaultSchema";
	
	String GENERIC_DATA_TYPE_STRING = "STRING";
	String GENERIC_DATA_TYPE_TIMESTAMP = "TIMESTAMP";
	String GENERIC_DATA_TYPE_LONG = "LONG";
	String GENERIC_DATA_TYPE_SHORT = "SHORT";
	String GENERIC_DATA_TYPE_INTEGER = "INTEGER";
	String GENERIC_DATA_TYPE_DOUBLE = "DOUBLE";
	String GENERIC_DATA_TYPE_DECIMAL = "DECIMAL";
	String GENERIC_DATA_TYPE_BOOLEAN = "BOOLEAN";
	String GENERIC_DATA_TYPE_BLOB = "BLOB";
	
	
	Map<String, Class> STATIC_DATATYPES_TO_WRAPPER_MAP = new HashMap<String, Class>()
	{
		{
			put("STRING", String.class);
			put("TIMESTAMP", Timestamp.class);
			put("LONG", Long.class);
			put("SHORT", Short.class);
			put("INTEGER", Integer.class);
			put("REAL", Double.class);
			put("DOUBLE", Double.class);
			put("DECIMAL", Float.class);
			put("BOOLEAN", Boolean.class);
			put("BLOB", Blob.class);
		}
	};

	Map<Class, String> STATIC_WRAPPER_MAP_TO_DATATYPES = new HashMap<Class, String>()
	{
		{
			put(String.class, "STRING");
			put(Timestamp.class, "TIMESTAMP");
			put(Long.class, "LONG");
			put(Short.class, "SHORT");
			put(Integer.class, "INTEGER");
			put(Double.class, "REAL"); 
			put(Double.class, "DOUBLE");
			put(Float.class, "DECIMAL");
			put(Boolean.class, "BOOLEAN");
			put(Blob.class, "BLOB");
		}
	};
}
