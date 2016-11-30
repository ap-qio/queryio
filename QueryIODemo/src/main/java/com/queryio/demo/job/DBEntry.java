package com.queryio.demo.job;

import java.util.List;

import com.queryio.plugin.datatags.UserDefinedTag;

public class DBEntry {

	List<UserDefinedTag> tags;
	String tableName;

	public DBEntry(List<UserDefinedTag> tags, String tableName) {
		this.tags = tags;
		this.tableName = tableName;
	}

	public List<UserDefinedTag> getTags() {
		return tags;
	}

	public String getTableName() {
		return tableName;
	}
}
