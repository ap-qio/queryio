package com.queryio.demo.mr.log;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;

public class LogDataDefinitionImpl {

	public ArrayList<ColumnMetadata> getColumnMetadata() {
		// String[] colNames = new String[]{"FILEPATH", "CATEGORY",
		// "CLASS_NAME", "DATE", "FILE_NAME", "LINE_NUMBER", "LOCATION", "MDC",
		// "MESSAGE", "METHOD", "ELAPSED", "NDC", "PRIORITY", "SEQUENCE",
		// "THREAD"};
		// String[] sqlTypes = new String[]{"VARCHAR(1280)", "VARCHAR(128)",
		// "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)",
		// "VARCHAR(128)", "VARCHAR(128)",
		// "VARCHAR(5000)", "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)",
		// "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)"};

		// FILEPATH
		// CATEGORY
		// CLASS_NAME
		// DATE
		// FILE_NAME
		// LOCATION
		// LINE_NUMBER
		// MESSAGE
		// METHOD
		// PRIORITY
		// ELAPSED
		// THREAD
		// NDC
		// MDC
		// SEQUENCE

		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("CATEGORY", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("CLASS_NAME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("DATE", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("FILE_NAME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("LINE_NUMBER", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("LOCATION", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MDC", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MESSAGE", String.class, 5000));
		colMetaDataList.add(new ColumnMetadata("METHOD", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("ELAPSED", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("NDC", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("PRIORITY", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("SEQUENCE", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("THREAD", String.class, 128));

		return colMetaDataList;
	}

	public String getTableName() {
		return "adhoc_logparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new LogDataDefinitionImpl().getColumnMetadata());
	}
}
