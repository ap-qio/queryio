package com.queryio.core.adhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.MetadataConstants;
import com.queryio.common.util.AppLogger;

public class ApacheAccessLogParser {
	
	private static final Log LOG = LogFactory.getLog(ApacheAccessLogParser.class);
	
	private static String FILE_NAME = null;
	
	
	private static String  PATTERN = "";

//	"%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\""
	
	private static final String IP = "h";
	private static final String USERID = "u";
	private static final String BLANK = "l";
	private static final String TIMEDATE = "t";
	private static final String REQUESTED_LINE = "r\"";
	private static final String STATUS_CODE = ">s";
	private static final String OBJ_SIZE = "b";
	private static final String REFERRER = "{Referer}i";
	private static final String AGENT = "{User-agent}i";
	
	static String DT_VARCHAR = MetadataConstants.GENERIC_DATA_TYPE_STRING;
	static String DT_VARCHAR_DEFAULT [] = {"(128)" , "(255)" , "(512)" , "(1280)"};
	static String DT_INTEGER = MetadataConstants.GENERIC_DATA_TYPE_INTEGER;
	
	private int index;
	private char start;
	
	private static boolean isFirstTime = false;
	
	private static String  ENCODING_TYPE="UTF-8";
	
	private static int MAX_DATA_PROCESS = 10;
	
	private static int  dataCounter = 0;
	

	private JSONArray data = new JSONArray();
	private JSONObject header = new JSONObject();
	private JSONArray details = new JSONArray();
	
	public ApacheAccessLogParser(String pattern , String name, String encoding, int noOfRecords) {
		
		PATTERN = pattern;
		FILE_NAME = name;
		ENCODING_TYPE = encoding;
		dataCounter = 0;
		isFirstTime = false;
		ENCODING_TYPE="UTF-8";
		MAX_DATA_PROCESS = noOfRecords;
	}
	
	public JSONArray getData() {
		return data;
	}

	public JSONObject getHeader() {
		return header;
	}

	public JSONArray getDetails() {
		return details;
	}
	
	public static ArrayList<String> parseByLine(String str) {
		boolean flag = false;
		String temp = "";
		ArrayList<String> arr =new ArrayList<String>();
		for (int i=0; i<str.length(); i++)
		{
			if((str.charAt(i) == '"' && !flag)|| str.charAt(i) == '[')
			{
				flag = true;
				temp+=str.charAt(i);
			}
				
			else if((str.charAt(i) == '"' && flag) || str.charAt(i) == ']'){
				flag = false;
				temp += str.charAt(i);
			}
			else if(str.charAt(i) == ' ' && !flag)
			{
					arr.add(temp);
					temp = "";
			}
			else
			{
				temp += str.charAt(i);
			}
		}
		arr.add(temp);
		return arr;
	}
	
	public void parseLine(String str) throws Exception {
		int i = 0;
		
		ArrayList<String> recordsPerLine = new ArrayList<String>();

		String tokens[];
		tokens = PATTERN.split("%");
		int differenceCounter = 0;
		for(int count=0;count<tokens.length;count++)
		{
			if(tokens[count].trim().equals("") || tokens[count]==null)
				differenceCounter++;
		}
		int length = tokens.length - differenceCounter;	
		AppLogger.getLogger().debug("Length : " + length);
		ArrayList<String> tempRec = new ArrayList<String>();
		
		recordsPerLine = parseByLine(str);

		if(!isFirstTime)
		{
			for(int count=0; count<length+1;count++){
				details.add(count, "");
			}
		}
		for(int count=0;count<length+1;count++){
			tempRec.add(count,"");
		}
		if(!isFirstTime)
		{
			header.put(0, "FILEPATH");
			JSONObject obj = new JSONObject();
			obj.put("index", 0);
			obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
			details.set(0, obj);
		}
		
		JSONArray jsonDataArray = new JSONArray();
		
		tempRec.set(0 , FILE_NAME) ;
		
		AppLogger.getLogger().debug(PATTERN.indexOf(IP));
		AppLogger.getLogger().debug(PATTERN.indexOf(BLANK));
		AppLogger.getLogger().debug(PATTERN.indexOf(USERID));
		AppLogger.getLogger().debug(PATTERN.indexOf(TIMEDATE));
		AppLogger.getLogger().debug(PATTERN.indexOf(REQUESTED_LINE));
		AppLogger.getLogger().debug(PATTERN.indexOf(STATUS_CODE));
		AppLogger.getLogger().debug(PATTERN.indexOf(OBJ_SIZE));
		AppLogger.getLogger().debug(PATTERN.indexOf(REFERRER));
		AppLogger.getLogger().debug(PATTERN.indexOf(AGENT));
		
		JSONObject iIndices = new JSONObject();
		
		int indices[] = new int[tokens.length];
		
		for(int j=0;j<tokens.length;j++){
			if(tokens[j].trim().indexOf(IP)!=-1){
				iIndices.put(IP, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + IP + " : " + j);
			}else if(tokens[j].trim().indexOf(BLANK)!=-1){
				iIndices.put(BLANK, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + BLANK + " : " + j);
			}else if(tokens[j].trim().indexOf(USERID)!=-1){
				iIndices.put(USERID, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + USERID + " : " + j);
			}else if(tokens[j].trim().startsWith(TIMEDATE)){
				iIndices.put(TIMEDATE, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + TIMEDATE + " : " + j);
			}else if ((tokens[j].trim().indexOf(REQUESTED_LINE)!=-1)){
				iIndices.put(REQUESTED_LINE, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + REQUESTED_LINE + " : " + j);
			}else if(tokens[j].trim().indexOf(STATUS_CODE)!=-1){
				iIndices.put(STATUS_CODE, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + STATUS_CODE+ " : " + j);
			}else if(tokens[j].trim().indexOf(OBJ_SIZE)!=-1){
				iIndices.put(OBJ_SIZE, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + OBJ_SIZE + " : " + j);
			}else if(tokens[j].trim().indexOf(REFERRER)!=-1){
				iIndices.put(REFERRER, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + REFERRER + " : " + j);
			}else if(tokens[j].trim().indexOf(AGENT)!=-1){
				iIndices.put(AGENT, j);
				AppLogger.getLogger().debug(tokens[j] + " : " + AGENT + " : " + j);
			}
		}

		if(PATTERN.indexOf(IP)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(IP).toString());
			if(!isFirstTime){
				AppLogger.getLogger().debug("Index of IP = " + ind);
				header.put(ind , "IP");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(BLANK)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(BLANK).toString());
			if(!isFirstTime){
				AppLogger.getLogger().debug("Index of BLANK = " + ind);
				header.put(ind , "HYPHEN");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				
				details.set(ind, obj);
				
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(USERID)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(USERID).toString());
			if(!isFirstTime){
				
				AppLogger.getLogger().debug("Index of USERID = " + ind);
				header.put(ind , "USER_NAME");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				
				details.set(ind, obj);
				
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf("%t") != -1){
			i++;
			int ind = Integer.parseInt(iIndices.get(TIMEDATE).toString());
			if(!isFirstTime){
				AppLogger.getLogger().debug("Index of TIMEDATe = " + ind);
				header.put(ind , "TIME_DATE");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if ((PATTERN.indexOf(REQUESTED_LINE)!=-1)){
			i++;
			int ind = Integer.parseInt(iIndices.get(REQUESTED_LINE).toString());
			if(!isFirstTime){
				AppLogger.getLogger().debug("Index of REQuseted_Line = " + ind);
				header.put(ind , "REQUESTED_LINE");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(STATUS_CODE)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(STATUS_CODE).toString());
			if(!isFirstTime){
				
				AppLogger.getLogger().debug("Index of STATUS_CODE = " + ind);
				header.put(ind , "STATUS_CODE");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_INTEGER);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(OBJ_SIZE)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(OBJ_SIZE).toString());
			if(!isFirstTime){
				AppLogger.getLogger().debug("Index of OBJ_Size = " + ind);
				header.put(ind , "OBJECT_SIZE");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_INTEGER);
				
				details.set(ind, obj);
			}
			tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(REFERRER)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(REFERRER).toString());
			if(!isFirstTime){
				
				AppLogger.getLogger().debug("Index of REFERRER = " + ind);
				header.put(ind , "REFERRER");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[2]);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		if(PATTERN.indexOf(AGENT)!=-1){
			i++;
			int ind = Integer.parseInt(iIndices.get(AGENT).toString());
			if(!isFirstTime){
				
				AppLogger.getLogger().debug("Index of USER_AGENT = " + ind);
				header.put(ind , "USER_AGENT");
				
				JSONObject obj = new JSONObject();
				obj.put("index", ind);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[2]);
				
				details.set(ind, obj);
			}
				tempRec.set(ind , recordsPerLine.get(ind-1));
		}
		//Adding array into json array
		AppLogger.getLogger().debug(PATTERN + " : " + REQUESTED_LINE + " : " + PATTERN.indexOf(REQUESTED_LINE));
		data.add(tempRec);
		isFirstTime = true;
		
		dataCounter++;
	}
	
	public void parse(InputStream is) throws IOException, InterruptedException, SQLException {
		BufferedReader br = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is , ENCODING_TYPE);

			br = new BufferedReader(in);
			
			while((str = br.readLine())!=null && dataCounter<MAX_DATA_PROCESS){
				AppLogger.getLogger().debug("DataCounter = " + dataCounter + " : String = " + str);
				this.parseLine(str);
			}
			
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage() , e);
		}
		finally{
			try{
				if(br!=null)
					br.close();
				
			}catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage() , e);
			}
			
		}
		
	}

}
