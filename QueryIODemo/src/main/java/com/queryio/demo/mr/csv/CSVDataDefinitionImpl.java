package com.queryio.demo.mr.csv;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class CSVDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		// String[] colNames = new String[]{"FILEPATH", "IP", "CPU", "RAM",
		// "DISKREAD", "DISKWRITE", "NETREAD", "NETWRITE"};
		// String[] sqlTypes = new String[]{"VARCHAR(128)", "VARCHAR(128)",
		// "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL"};

		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();

		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("IP", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("CPU", Float.class));
		colMetaDataList.add(new ColumnMetadata("RAM", Float.class));
		colMetaDataList.add(new ColumnMetadata("DISKREAD", Float.class));
		colMetaDataList.add(new ColumnMetadata("DISKWRITE", Float.class));
		colMetaDataList.add(new ColumnMetadata("NETREAD", Float.class));
		colMetaDataList.add(new ColumnMetadata("NETWRITE", Float.class));

		return colMetaDataList;
	}

	public String getTableName() {
		return "ADHOC_CSVPARSERJOB";
	}

	public static void main(String[] args) {
		System.out.println(new CSVDataDefinitionImpl().getColumnMetadata());
	}
}
