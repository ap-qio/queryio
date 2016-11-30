package com.queryio.demo.adhoc.json;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class JSONDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("IP", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("CPU", Float.class));
		colMetaDataList.add(new ColumnMetadata("RAM", Float.class));

		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_jsonparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new JSONDataDefinitionImpl().getColumnMetadata());
	}
}
