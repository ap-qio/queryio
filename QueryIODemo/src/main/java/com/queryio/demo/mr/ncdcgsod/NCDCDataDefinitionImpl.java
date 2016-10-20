package com.queryio.demo.mr.ncdcgsod;

import java.util.ArrayList;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class NCDCDataDefinitionImpl implements IDataDefinition {

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
//		String[] colNames = new String[]{
//				"FILEPATH",
//				"STATION_NUMBER", 
//				"WBAN_NUMBER", 
//				"YEAR", 
//				"MONTH", 
//				"DAY",
//				"MEAN_TEMP",
//				"NUM_MEAN_TEMP_SAMPLES",
//				"MEAN_DEW_POINT",
//				"NUM_MEAN_DEW_POINT_SAMPLES",
//				"MEAN_SEALEVEL_PRESSURE",
//				"NUM_MEAN_SEALEVEL_PRESSURE_SAMPLES",
//				"MEAN_STATION_PRESSURE",
//				"NUM_MEAN_STATION_PRESSURE_SAMPLES",
//				"MEAN_VISIBILITY",
//				"NUM_MEAN_VISIBILITY_SAMPLES",
//				"MEAN_WIND_SPEED",
//				"NUM_MEAN_WIND_SPEED_SAMPLES",
//				"MAX_SUSTAINED_WIND_SPEED",
//				"MAX_GUST_WIND_SPEED",
//				"MAX_TEMPERATURE",
//				"MAX_TEMPERATURE_EXPLICIT",
//				"MIN_TEMPERATURE",
//				"MIN_TEMPERATURE_EXPLICIT",
//				"TOTAL_PRECIPITATION",
//				"SNOW_DEPTH",
//				"FOG",
//				"RAIN",
//				"SNOW",
//				"HAIL",
//				"THUNDER",
//				"TORNADO",};
//		
//		String[] sqlTypes = new String[]{
//				"varchar(128)",
//				"integer", 
//				"integer", 
//				"integer", 
//				"integer", 
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"integer",
//				"float",
//				"float",
//				"float",
//				"boolean",
//				"float",
//				"boolean",
//				"float",
//				"float",
//				"boolean",
//				"boolean",
//				"boolean",
//				"boolean",
//				"boolean",
//				"boolean",};
		
		ArrayList<ColumnMetadata> colMetaDataList = new ArrayList<ColumnMetadata>();
		
		colMetaDataList.add(new ColumnMetadata("FILEPATH", String.class, 128));
		colMetaDataList.add(new ColumnMetadata("STATION_NUMBER", Integer.class));
		colMetaDataList.add(new ColumnMetadata("WBAN_NUMBER", Integer.class));
		colMetaDataList.add(new ColumnMetadata("YEAR", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MONTH", Integer.class));
		colMetaDataList.add(new ColumnMetadata("DAY", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_TEMP", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_TEMP_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_DEW_POINT", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_DEW_POINT_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_SEALEVEL_PRESSURE", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_SEALEVEL_PRESSURE_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_STATION_PRESSURE", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_STATION_PRESSURE_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_VISIBILITY", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_VISIBILITY_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MEAN_WIND_SPEED", Float.class));
		colMetaDataList.add(new ColumnMetadata("NUM_MEAN_WIND_SPEED_SAMPLES", Integer.class));
		colMetaDataList.add(new ColumnMetadata("MAX_SUSTAINED_WIND_SPEED", Float.class));
		colMetaDataList.add(new ColumnMetadata("MAX_GUST_WIND_SPEED", Float.class));
		colMetaDataList.add(new ColumnMetadata("MAX_TEMPERATURE", Float.class));
		colMetaDataList.add(new ColumnMetadata("MAX_TEMPERATURE_EXPLICIT", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("MIN_TEMPERATURE", Float.class));
		colMetaDataList.add(new ColumnMetadata("MIN_TEMPERATURE_EXPLICIT", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("TOTAL_PRECIPITATION", Float.class));
		colMetaDataList.add(new ColumnMetadata("SNOW_DEPTH", Float.class));
		colMetaDataList.add(new ColumnMetadata("FOG", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("RAIN", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("SNOW", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("HAIL", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("THUNDER", Boolean.class));
		colMetaDataList.add(new ColumnMetadata("TORNADO", Boolean.class));
		
		return colMetaDataList;
	}

	@Override
	public String getTableName() {
		return "adhoc_ncdcparserjob";
	}
	
	public static void main(String[] args){
		System.out.println(new NCDCDataDefinitionImpl().getColumnMetadata());
	}
}
