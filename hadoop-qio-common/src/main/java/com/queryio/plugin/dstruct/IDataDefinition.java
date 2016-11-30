package com.queryio.plugin.dstruct;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;

public interface IDataDefinition {
	ArrayList<ColumnMetadata> getColumnMetadata();

	String getTableName();
}