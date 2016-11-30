package com.queryio.core.namespace;

import java.util.ArrayList;

public class NamespaceConflict {
	public final static int CONFLICT_TYPE_NONE = 0;
	public final static int CONFLICT_TYPE_METADATA_DIFFERENCE = 1;
	public final static int CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE = 2;
	public final static int CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE = 3;

	public final static String CONFLICT_TYPE_NONE_DESC = "No conflict";
	public final static String CONFLICT_TYPE_METADATA_DIFFERNECE_DESC = "Metadata is different";
	public final static String CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE_DESC = "Entry missing in namespace";
	public final static String CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE_DESC = "Entry missing in database";

	private String filePath;
	private int conflictType = CONFLICT_TYPE_NONE;
	private String conflictTypeDescription;

	private ArrayList<ConflictInfo> conflictInfo;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getConflictType() {
		return conflictType;
	}

	public void setConflictType(int conflictType) {
		this.conflictType = conflictType;
	}

	public ArrayList<ConflictInfo> getConflictInfo() {
		return conflictInfo;
	}

	public void addConflictInfo(String columnName, String expectedValue, String foundValue) {
		if (conflictInfo == null) {
			conflictInfo = new ArrayList<ConflictInfo>();
		}
		this.conflictInfo.add(new ConflictInfo(columnName, expectedValue, foundValue));
	}

	public void setConflictInfo(ArrayList<ConflictInfo> conflictInfo) {
		this.conflictInfo = conflictInfo;
	}

	public void setConflictTypeDescription() {
		switch (conflictType) {
		case CONFLICT_TYPE_NONE:
			this.conflictTypeDescription = CONFLICT_TYPE_NONE_DESC;
			break;
		case CONFLICT_TYPE_METADATA_DIFFERENCE:
			this.conflictTypeDescription = CONFLICT_TYPE_METADATA_DIFFERNECE_DESC;
			break;
		case CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE:
			this.conflictTypeDescription = CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE_DESC;
			break;
		case CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE:
			this.conflictTypeDescription = CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE_DESC;
			break;
		}
	}

	public void setConflictTypeDescription(String conflictTypeDescription) {
		this.conflictTypeDescription = conflictTypeDescription;
	}

	public String getConflictTypeDescription() {
		return conflictTypeDescription;
	}
}

class ConflictInfo {
	private String columnName;
	private String expectedValue;
	private String foundValue;

	public ConflictInfo() {

	}

	public ConflictInfo(String columnName, String expectedValue, String foundValue) {
		this.columnName = columnName;
		this.expectedValue = expectedValue;
		this.foundValue = foundValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getFoundValue() {
		return foundValue;
	}

	public void setFoundValue(String foundValue) {
		this.foundValue = foundValue;
	}
}
