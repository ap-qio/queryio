package com.queryio.demo.adhoc.log;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class LogDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		// String[] colNames = new String[]{"FILEPATH", "CATEGORY", "CLASS",
		// "DATE", "FILE", "LINE", "LOCATION", "MDC", "MESSAGE", "METHOD",
		// "ELAPSED", "NDC", "PRIORITY", "SEQUENCE", "THREAD"};
		// String[] sqlTypes = new String[]{"VARCHAR(1280)", "VARCHAR(255)",
		// "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
		// "VARCHAR(255)", "VARCHAR(255)",
		// "VARCHAR(5000)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
		// "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)"};

		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("CATEGORY", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("CLASS", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("DATE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("FILE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("LINE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("LOCATION", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("MDC", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("MESSAGE", String.class, 5000));
		colMetaDataList.add(new ColumnMetadata("METHOD", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("ELAPSED", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("NDC", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("PRIORITY", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("SEQUENCE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("THREAD", String.class, 255));

		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_logparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new LogDataDefinitionImpl().getColumnMetadata());
	}
}
