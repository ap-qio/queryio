package com.queryio.common.database;

import java.util.HashMap;
import java.util.Map;

public class DBTypeProperties {
	String dbType;
	String defaultSchema;
	Map<Class, String> typeMap;

	public DBTypeProperties() {
		typeMap = new HashMap<Class, String>();
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDefaultSchema() {
		return defaultSchema;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public Map<Class, String> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(Map<Class, String> typeMap) {
		this.typeMap = typeMap;
	}

	public void addDataType(Class clazz, String sqlType) {
		this.typeMap.put(clazz, sqlType);
	}

	@Override
	public String toString() {
		return "dbType : " + dbType + ", " + "defaultSchema : " + defaultSchema;
	}

	public Class getKeyFromValue(String value) {
		for (Class key : this.getTypeMap().keySet()) {
			if (this.getTypeMap().get(key).equalsIgnoreCase(value))
				return key;
		}
		return null;
	}
}
