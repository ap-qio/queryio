package com.queryio.demo.adhoc.mbox;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class MBoxDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
//		String[] colNames = new String[] { "FILEPATH", "CONTENT_ID",
//				"CONTENT_LANGUAGE", "MD5_CONTENT", "CONTENT_TYPE",
//				"CONTENT_DESCRIPTION", "CONTENT_DISPOSITION",
//				"CONTENT_ENCODING", "FILENAME", "MAIL_FROM", "LINE_COUNT",
//				"MESSAGE_ID", "MESSAGE_NUMBER", "RECEIVED_DATE", "REPLY_TO",
//				"SENDER", "SENT_DATE", "MAIL_SUBJECT", "MAIL_SIZE" };
//
//		String[] sqlTypes = new String[] { "VARCHAR(1280)", "VARCHAR(128)",
//				"VARCHAR(128)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
//				"VARCHAR(255)", "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(255)",
//				"DECIMAL", "VARCHAR(128)", "DECIMAL", "TIMESTAMP",
//				"VARCHAR(512)", "VARCHAR(128)", "TIMESTAMP", "VARCHAR(255)",
//				"DECIMAL" };
		
		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("CONTENT_ID", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("CONTENT_LANGUAGE", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MD5_CONTENT", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("CONTENT_TYPE", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("CONTENT_DESCRIPTION", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("CONTENT_DISPOSITION", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("CONTENT_ENCODING", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("FILENAME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MAIL_FROM", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("LINE_COUNT", Float.class));
		colMetaDataList.add(new ColumnMetadata("MESSAGE_ID", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MESSAGE_NUMBER", Float.class));
		colMetaDataList.add(new ColumnMetadata("RECEIVED_DATE", Timestamp.class));
		colMetaDataList.add(new ColumnMetadata("REPLY_TO", String.class, 512));
		colMetaDataList.add(new ColumnMetadata("SENDER", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("SENT_DATE", Timestamp.class));
		colMetaDataList.add(new ColumnMetadata("MAIL_SUBJECT", String.class, 255));
		colMetaDataList.add(new ColumnMetadata("MAIL_SIZE", Float.class));
		
		return colMetaDataList;
	}
	@Override
	public String getTableName() {
		return "adhoc_mboxparserjob";
	}
	
	public static void main(String[] args){
		System.out.println(new MBoxDataDefinitionImpl().getColumnMetadata());
	}
}
