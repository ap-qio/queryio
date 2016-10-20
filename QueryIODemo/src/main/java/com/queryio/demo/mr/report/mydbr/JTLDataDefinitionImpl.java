package com.queryio.demo.mr.report.mydbr;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class JTLDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		
		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 2550));
		colMetaDataList.add(new ColumnMetadata("SAMPLE_LABEL", String.class, 1275));
		colMetaDataList.add(new ColumnMetadata("SAMPLE_VALUE", Integer.class));
		colMetaDataList.add(new ColumnMetadata("START_TIMESTAMP", Timestamp.class));
		colMetaDataList.add(new ColumnMetadata("END_TIMESTAMP", Timestamp.class));
		colMetaDataList.add(new ColumnMetadata("TASK_COUNT", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MIN_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("MAX_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("AVG_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("AVG_TPS", Double.class));
		colMetaDataList.add(new ColumnMetadata("TP_999_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("TP_99_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("TP_95_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("TP_90_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("TP_80_LATENCY", Double.class));
		colMetaDataList.add(new ColumnMetadata("TOTAL_BYTES", Double.class));
		colMetaDataList.add(new ColumnMetadata("ERRORS_COUNT", Integer.class));
		colMetaDataList.add(new ColumnMetadata("SUCCESS_COUNT", Integer.class));
		colMetaDataList.add(new ColumnMetadata("SUCCESS_PERCENTAGE", Double.class));
		
		return colMetaDataList;
	}

	public ArrayList<String> getTableColumns() {
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.add("SAMPLE_LABEL");
		columnNames.add("SAMPLE_VALUE");
		columnNames.add("START_TIMESTAMP");
		columnNames.add("END_TIMESTAMP");
		columnNames.add("TASK_COUNT");
		columnNames.add("MIN_LATENCY");
		columnNames.add("MAX_LATENCY");
		columnNames.add("AVG_LATENCY");
		columnNames.add("AVG_TPS");
		columnNames.add("TP_999_LATENCY");
		columnNames.add("TP_99_LATENCY");
		columnNames.add("TP_95_LATENCY");
		columnNames.add("TP_90_LATENCY");
		columnNames.add("TP_80_LATENCY");
		columnNames.add("TOTAL_BYTES");
		columnNames.add("ERRORS_COUNT");
		columnNames.add("SUCCESS_COUNT");
		columnNames.add("SUCCESS_PERCENTAGE");
		
		return columnNames;			// do not add "FILEPATH", as it is added by default in parser.
	}
	
	public String getTableName() {
		return "JTL_PARSER_JOB";
	}
	
	public static void main(String[] args){
		System.out.println(new JTLDataDefinitionImpl().getColumnMetadata());
	}
}
