package com.queryio.core.databrowser;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import com.queryio.common.util.AppLogger;

public class DataBrowserComparator implements Comparator<ArrayList>{
	
	private int sindex;
	private String sorder = "asc"; 
	public int compare(ArrayList list1, ArrayList list2) {
		
		if(sindex==2){
			int s1= (Short) list1.get(sindex);
			int s2= (Short) list2.get(sindex);
			return s1>s2?1:0;
		}
		String str1 = (String) list1.get(sindex);
        String str2 = (String) list2.get(sindex);
        if(sindex==3||sindex==4){ //3 for Last_Read and 4 for Last_write
        	if(sorder.equals("asc")){
            	return compareDate(str1,str2);	
            }else{
            	return compareDate(str2,str1);
            }
        }else{
        	
            if(sorder.equals("asc")){
            	return compare(str1,str2);	
            }else{
            	return compare(str2,str1);
            }
        }
	}
	
	public void setSortColIndex(int colIndex){
		this.sindex = colIndex;
	}
	
	private int compare(String s1, String s2)
	{
		if (s1 == null) {
			return (s2 == null ? 0 : -1);
		} else if (s2 == null) {
			return 1;
		}
	    return s1.compareToIgnoreCase(s2);
	}
	
	public void setSorder(String sorder) {
		this.sorder = sorder;
	}
	
	private int compareDate(String s1, String s2)
	{
		if (s1 == null) {
			return (s2 == null ? 0 : -1);
		} else if (s2 == null) {
			return 1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		Date date1;
		try {
			 date1 = (Date)sdf.parse(s1);
			 Date date2  = (Date)sdf.parse(s2); 
			 java.sql.Timestamp timeStampDate1 = new Timestamp(date1.getTime());
			 java.sql.Timestamp timeStampDate2 = new Timestamp(date1.getTime());
			 return timeStampDate1.compareTo(timeStampDate2);
		} catch (ParseException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return 0;
		}
	}
	
	
}
