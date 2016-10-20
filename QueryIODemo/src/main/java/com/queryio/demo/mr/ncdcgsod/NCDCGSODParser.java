package com.queryio.demo.mr.ncdcgsod;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tika.metadata.Metadata;

import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class NCDCGSODParser implements IDataTagParser {
	private static Map<String, List<ColumnMetadata>> map = new HashMap<String, List<ColumnMetadata>>();
	static{
		String tableName= "NCDC_GSOD";
		List<ColumnMetadata> columnDataList = new ArrayList<ColumnMetadata>();
		
		columnDataList.add(new ColumnMetadata("station_number", Integer.class));
		columnDataList.add(new ColumnMetadata("wban_number", Integer.class));
		columnDataList.add(new ColumnMetadata("year", Integer.class));
		columnDataList.add(new ColumnMetadata("month", Integer.class));
		columnDataList.add(new ColumnMetadata("day", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_temp", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_temp_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_dew_point", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_dew_point_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_sealevel_pressure", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_sealevel_pressure_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_station_pressure", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_station_pressure_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_visibility", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_visibility_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("mean_wind_speed", Float.class));
		columnDataList.add(new ColumnMetadata("num_mean_wind_speed_samples", Integer.class));
		columnDataList.add(new ColumnMetadata("max_sustained_wind_speed", Float.class));
		columnDataList.add(new ColumnMetadata("max_gust_wind_speed", Float.class));
		columnDataList.add(new ColumnMetadata("max_temperature", Float.class));
		columnDataList.add(new ColumnMetadata("max_temperature_explicit", Boolean.class));
		columnDataList.add(new ColumnMetadata("min_temperature", Float.class));
		columnDataList.add(new ColumnMetadata("min_temperature_explicit", Boolean.class));
		columnDataList.add(new ColumnMetadata("total_precipitation", Float.class));
		columnDataList.add(new ColumnMetadata("snow_depth", Float.class));
		columnDataList.add(new ColumnMetadata("fog", Boolean.class));
		columnDataList.add(new ColumnMetadata("rain", Boolean.class));
		columnDataList.add(new ColumnMetadata("snow", Boolean.class));
		columnDataList.add(new ColumnMetadata("hail", Boolean.class));
		columnDataList.add(new ColumnMetadata("thunder", Boolean.class));
		columnDataList.add(new ColumnMetadata("tornado", Boolean.class));

		map.put("gz", columnDataList);
	}
	@Override
	public List<UserDefinedTag> getCustomTagList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public  TableMetadata getTableMetaData(String fileExtension){
		return new TableMetadata("gz", map.get(fileExtension));
	}

	@Override
	public void parseStream(InputStream stream, String fileExtension) throws Exception {
		DataInputStream in = new DataInputStream(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String str;
		StringTokenizer st;
		
		str = br.readLine(); // Ignore First Line
		while ((str = br.readLine()) != null) {
			str = str.replace("  ", " ").replace("*", "");
			st = new StringTokenizer(str);
			
			int index=0;
			while (st.hasMoreElements()) {
				if(index==2){
					String value = (String) st.nextElement();
				} else {
					
				}
				index++;
				System.out.println(st.nextElement());
			}
		}
	}

	@Override
	public boolean updateDbSchema() {
		return false;
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata,
			Metadata metadata) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean parseMapRecord(String value, long offset)
			throws Exception {
		return false;
	}

	@Override
	public void parseTagData(String key, String value, boolean isReducer) throws Exception {
		
	}
}
