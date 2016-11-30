package com.queryio.demo.adhoc.iislog;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class IISLogDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();

		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("CLIENT_IP", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("USERNAME", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("DATE", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("TIME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("SERVICEINSTANCE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("SERVERNAME", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("SERVER_IP", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("TIMETAKEN", Float.class));
		colMetaDataList.add(new ColumnMetadata("CLIENT_BYTESSENT", Float.class));
		colMetaDataList.add(new ColumnMetadata("SERVER_BYTESSENT", Float.class));
		colMetaDataList.add(new ColumnMetadata("SERVICE_STATUSCODE", Float.class));
		colMetaDataList.add(new ColumnMetadata("WINDOWS_STATUSCODE", Float.class));
		colMetaDataList.add(new ColumnMetadata("REQUESTTYPE", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("TARGETOFOPER", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("PARAMETERS", String.class, 255));

		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_iislogparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new IISLogDataDefinitionImpl().getColumnMetadata());
	}
}
