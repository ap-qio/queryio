package com.queryio.files.tagging.apachelog;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.util.AppLogger;
import com.queryio.files.tagging.IFileParser;

public class ApacheLogFileParser extends IFileParser {
	
	private static final Log LOG = LogFactory.getLog(ApacheLogFileParser.class);
	
	
	private static String  pattern = "";

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
	
	private int index;
	private char start;
	
	HashMap<String, String> curValueMap = new HashMap<String, String>();
	private String delimiter;
	private String valueSeparator;
	private boolean hasHeader;
	private String encoding;
	private boolean skipAll;
	String tokens[];
	Map<Integer, String> columns;
	JSONObject iIndices = new JSONObject();
	JSONObject tagsIInfo;
	int indices[] ;
	public ApacheLogFileParser(JSONObject tagsIInfo) {
		
		super(tagsIInfo);
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsIInfo.get(PARSE_DETAILS_KEY);
		
		pattern = String.valueOf(parsingDetailsJSON.get(LOG_PATTERN_KEY));
		
		pattern = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i";
        
		hasHeader = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(HAS_HEADER_KEY)));
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
        
        
		
        tokens = pattern.split("%");
		
		  JSONArray fieldsJSON = (JSONArray) tagsIInfo.get(FIELDS_KEY);
			System.out.println("Fields: " + fieldsJSON);
			columns = new HashMap<Integer, String>();
			
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		indices = new int[tokens.length];
		for(int j=0;j<tokens.length;j++){
			if(tokens[j].trim().indexOf(IP)!=-1){
				iIndices.put(IP, j);
			}else if(tokens[j].trim().indexOf(BLANK)!=-1){
				iIndices.put(BLANK, j);
			}else if(tokens[j].trim().indexOf(USERID)!=-1){
				iIndices.put(USERID, j);
			}else if(tokens[j].trim().startsWith(TIMEDATE)){
				iIndices.put(TIMEDATE, j);
				
			}else if ((tokens[j].trim().indexOf(REQUESTED_LINE)!=-1)){
				iIndices.put(REQUESTED_LINE, j);
				
			}else if(tokens[j].trim().indexOf(STATUS_CODE)!=-1){
				iIndices.put(STATUS_CODE, j);
				
			}else if(tokens[j].trim().indexOf(OBJ_SIZE)!=-1){
				iIndices.put(OBJ_SIZE, j);
				
			}else if(tokens[j].trim().indexOf(REFERRER)!=-1){
				iIndices.put(REFERRER, j);
				
			}else if(tokens[j].trim().indexOf(AGENT)!=-1){
				iIndices.put(AGENT, j);
			}
		}
	}
	
	private String getColumnName(int colIndex){
		String colName = "";
		colName = columns.get(colIndex);
		return colName;
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
		
		
		ArrayList<String> recordsPerLine = parseByLine(str);
		
		for(int i = 0; i< recordsPerLine.size(); i++){
//			System.out.println("ColumnName : "+getColumnName(i+1)+" value : "+recordsPerLine.get(i));
			curValueMap.put(getColumnName(i+1),recordsPerLine.get(i));
		}
		
		evaluateCurrentEntry(curValueMap, str);
		
		
	
	}
	
	public void parse(InputStream is, Map<String, String> coreTags) throws IOException, InterruptedException, SQLException {
		BufferedReader br = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is , encoding);

			br = new BufferedReader(in);
			
			while((str = br.readLine())!=null){
				
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
