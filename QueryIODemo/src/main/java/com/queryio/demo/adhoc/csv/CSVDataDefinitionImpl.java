package com.queryio.demo.adhoc.csv;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class CSVDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {

		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("IP", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("CPU", Float.class));
		colMetaDataList.add(new ColumnMetadata("RAM", Float.class));
		colMetaDataList.add(new ColumnMetadata("DISKREAD", Float.class));
		colMetaDataList.add(new ColumnMetadata("DISKWRITE", Float.class));
		colMetaDataList.add(new ColumnMetadata("NETREAD", Float.class));
		colMetaDataList.add(new ColumnMetadata("NETWRITE", Float.class));

		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_csvparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new CSVDataDefinitionImpl().getColumnMetadata());
	}
}
