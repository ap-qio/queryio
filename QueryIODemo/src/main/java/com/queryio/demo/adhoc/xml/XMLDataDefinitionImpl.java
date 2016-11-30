package com.queryio.demo.adhoc.xml;

import java.util.ArrayList;

import com.queryio.demo.adhoc.csv.CSVDataDefinitionImpl;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class XMLDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		// String[] colNames = new String[]{"FILEPATH", "DRIVER", "URL",
		// "USERNAME", "PASSWORD", "POOLNAME", "MAXCONNECTIONS",
		// "MAXIDLECONNECTIONS","WAITTIMEINMILLIS","DRIVERJAR"};
		// String[] sqlTypes = new String[]{"VARCHAR(1280)", "VARCHAR(128)",
		// "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)", "VARCHAR(128)",
		// "DECIMAL", "DECIMAL" , "DECIMAL" , "VARCHAR(128)"};

		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();

		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 1280));
		colMetaDataList.add(new ColumnMetadata("DRIVER", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("URL", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("USERNAME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("PASSWORD", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("POOLNAME", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("MAXCONNECTIONS", Float.class));
		colMetaDataList.add(new ColumnMetadata("MAXIDLECONNECTIONS", Float.class));
		colMetaDataList.add(new ColumnMetadata("WAITTIMEINMILLIS", Float.class));
		colMetaDataList.add(new ColumnMetadata("DRIVERJAR", String.class, 128));

		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_xmlparserjob";
	}

	public static void main(String[] args) {
		System.out.println(new CSVDataDefinitionImpl().getColumnMetadata());
	}

}
