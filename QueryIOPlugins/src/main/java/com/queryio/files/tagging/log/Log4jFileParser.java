package com.queryio.files.tagging.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.IFileParser;



public class Log4jFileParser extends IFileParser {
	
	int count = 0;
	private int index;
	
	private boolean hasHeader;
	private boolean skipAll;
	boolean includeDate = true;
	
	private char start;
	
	private static final String CONVERSION_CHARS = "cCdFlLmMnprtxX";
	private String tokens[];
	private StringBuffer lastMessage = new StringBuffer();
	private String encoding;
	static final String NEW_LINE = System.getProperty("line.separator");
	
	
	SimpleDateFormat dateFormat = null;
	String pattern ;
	
	private Date startDate = null;
	private Date endDate = null;
	
	Map<Integer, String> columns;
	HashMap<String, String> curValueMap = new HashMap<String, String>();
	private LogEntry logEntry = new LogEntry();

	private ArrayList<LogEntry> logEntries = new ArrayList<LogEntry>();
	JSONObject tagsIInfo;
	
	HashMap<String, Integer> conversionCharIndexMap;
	
	public Log4jFileParser(JSONObject tagsIInfo) {
		super(tagsIInfo);
		this.tagsIInfo = tagsIInfo; 
		
		conversionCharIndexMap =  new HashMap<String, Integer>();
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsIInfo.get(PARSE_DETAILS_KEY);
		
		pattern = String.valueOf(parsingDetailsJSON.get(LOG_PATTERN_KEY));
		
//		pattern = "%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n";
        
		hasHeader = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(HAS_HEADER_KEY)));
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));
        
        JSONArray fieldsJSON = (JSONArray) tagsIInfo.get(FIELDS_KEY);
		
        
		System.out.println("Fields: " + fieldsJSON);
		columns = new HashMap<Integer, String>();
		
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		int j = 0;
		while (st.hasMoreTokens())
		{
			tokens[j] = st.nextToken();
			conversionCharIndexMap.put(String.valueOf(tokens[j].toCharArray()[0]), j);
			++j;
		}
		Iterator<String> it = conversionCharIndexMap.keySet().iterator();
		
	}
	
	
	private String getColumnNameForIdentifyConversionCharacter(char c ){
		String colName = "";
		String key = String.valueOf(c); 
		int colIndex = conversionCharIndexMap.get(key);
		colName = columns.get(colIndex+1);
		return colName;
		
		
		
	}

	public void parse(InputStream is, Map<String, String> coreTags) throws IOException, InterruptedException, SQLException {
		BufferedReader rd = null;
		String str;
		try {
			
			rd = new BufferedReader(new InputStreamReader(is, encoding));
			

			final IMessageListener listener = new IMessageListener() 
			{
				
				public void messageComplete() throws IOException, InterruptedException, SQLException 
				{
					String lastMessage = Log4jFileParser.this.getLastMessage();
					
					Log4jFileParser.this.logEntry.setMessage(lastMessage.toString());
					
					String tokens [] =  Log4jFileParser.this.tokens;
					int i = 0;
					while(i < tokens.length)
					{
						
						String colName = Log4jFileParser.this.getColumnNameForIdentifyConversionCharacter(tokens[i].toCharArray()[0]);
						String colVal = Log4jFileParser.this.getColValueFromConversionChar(tokens[i].toCharArray()[0]);
						curValueMap.put(colName,colVal);
						i++;
						
					}
					evaluateCurrentEntry(curValueMap, "");
					Log4jFileParser.this.logEntry = new LogEntry();
					
					curValueMap.clear();
					
					
				}
			};
			if(hasHeader) {
				rd.readLine();
			}
			
			while ((str = rd.readLine()) != null) {
				try {
					
					this.parseLine(str, listener);
					
				} catch(Exception e) {
					e.printStackTrace();
					if(skipAll) {
						break;
					}
				}
				
			}
			
			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			
			
			try {
				if (rd != null)
					rd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void resetLastMessage()
	{
		lastMessage.setLength(0);
	}
	
	boolean parseLine(String str, IMessageListener listener) throws IOException, InterruptedException, SQLException
	{
		boolean flag = true;
		start = 0;
		index = 0;
		int i = 0;
		String s = null;
		while(i < tokens.length)
		{
			
			if(index < str.length() && !tokens[i].isEmpty())
			{
				
				char arr[] = tokens[i].toCharArray();
				int j = 0;
				while(j < arr.length && arr[j] != '{')
				{
					if(isConversionCharacter(arr[j]))
					{
						s = identifyConversionCharacter(arr[j], str, tokens[i]);
						if(s != null)
						{
							if (arr[j] == 'm')
							{
								if (listener != null && lastMessage.length() > 0)
								{
									listener.messageComplete();
								}
								resetLastMessage();
								lastMessage.append(s);
							}
							flag = false;
							break;
						}
					}
					++j;
				}
				if(s == null)
					break;
			}
			++i;
		}
		if(flag)
		{
			lastMessage.append(NEW_LINE);
			lastMessage.append("\t");
			lastMessage.append(str);
		}
		return !flag;
	}
	
	String getLastMessage()
	{
		return this.lastMessage.toString();
	}
	
	private boolean isConversionCharacter(char c)
	{
		return (CONVERSION_CHARS.indexOf(c) != -1);
	}
	
	private String identifyConversionCharacter(char c, String str, String token)
	{
		String res = null;
		//String identifier = null;
		switch(c)	
		{
			case 'c': 	
			{
				String category = this.getCategory(str, token);
				logEntry.setCategory(category);
				res = category;
				//identifier = "Category";
				break;
			}
			case 'C':
			{
				String className = this.getClassName(str, token);;
				logEntry.setClassName(className);
				res = className;
				//identifier = "Class Name";
				break;
			}
			case 'd':
			{
				String date = this.getDate(str, token);
				logEntry.setDate(date);
				res = date;
				//identifier = "Date";
				break;
			}
			case 'F': 	
			{
				String fileName = this.getFileName(str, token);
				logEntry.setFileName(fileName);
				res = fileName;
				//identifier = "File Name";
				break;
			}
			case 'l': 	
			{
				String location = this.getLocation(str, token);
				logEntry.setLocation(location);
				res = location;
				//identifier = "Location";
				break;
			}
			case 'L': 	
			{
				String line = this.getLineNumber(str, token);
				logEntry.setLineNumber(line);
				res = line;
				//identifier = "Line number";
				break;
			}
			case 'm':
			{
				String message = this.getMessage(str, token);
				logEntry.setMessage(message);
				res = message;
				//identifier = "Message";
				break;
			}
			case 'M':
			{
				String method = this.getMethodName(str, token);
				logEntry.setMethod(method);
				res = method;
				//identifier = "Method Name";
				break;
			}
			case 'n':
			{
				res = "\n";
				break;
			}
			case 'p':
			{
				String priority = this.getPriority(str, token);
				logEntry.setPriority(priority);
				res = priority;
				//identifier = "Level";
				break;
			}
			case 'r':
			{
				String elapsed = this.getElapsedMilliSecond(str, token);
				logEntry.setMsElapsed(elapsed);
				res = elapsed;
				//identifier = "Elapsed milliseconds";
				break;
			}
			case 't': 	
			{	
				String threadName = this.getThreadName(str, token);
				logEntry.setThread(threadName);
				res = threadName;
				//identifier = "Thread Name";
				break;
			}
			case 'x':
			{
				String ndc = this.getNDC(str, token);
				logEntry.setNdc(ndc);
				res = ndc;
				//identifier = "NDC";
				break;
			}
			case 'X':
			{
				String mdc = this.getMDC(str, token);
				logEntry.setMdc(mdc);
				res = mdc;
				//identifier = "Category";
				break;
			}
			default : break;
		}
		
		if (res != null)
		{
			//if (identifier != null)
			//	return identifier + ": " + res;
			return res;
		}
		return null;
	}
	
		
	private char getSeparator(char c, String token)	
	{
		char sep;
		int ind;
		if((ind = token.indexOf('}')) != -1)
		{
			ind = ind + 1;
		}
		else
		{
			ind = token.indexOf(c) + 1;
		}
		if(ind == token.length())
		{
			sep = 0;
			start = 0;
		}
		else
		{
			sep = token.charAt(ind);
			start = token.charAt(token.length() - 1);
		}
		return sep;
	}
	
	private String getCategory(String str, String token)
	{
		return getProperty(str, token, 'c');
	}
	
	private String getClassName(String str, String token)
	{
		return getProperty(str, token, 'C');
	}
	
	private String getDate(String str, String token)
	{
		String pattern = null;
		String s="yyyy-MM-dd HH:mm:ss,SSS";
		int ind = token.indexOf('{');
		if(ind != -1)
		{
			pattern = token.substring(ind + 1, token.indexOf('}'));
			if (pattern.equals("ABSOLUTE"))
			{
				s = "HH:mm:ss,SSS";
			}
			else if (pattern.equals("DATE"))
			{
				s = "dd MMM yyyy HH:mm:ss,SSS";
			}
			else if (pattern.equals("ISO8601"))
			{
				// do nothing
			}
			else
			{
				s = pattern;
			}
		}
		String result = null;
		
		if(dateFormat==null){
			dateFormat = new SimpleDateFormat(s);
		}
		
		ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		getSeparator('d', token);
		Date d = dateFormat.parse(str, new ParsePosition(ind));
		
		this.includeDate = true;
		
		if(d != null)
		{
			result = dateFormat.format(d);
			index = ind + s.length();
			
			boolean incl = true;
			if(this.startDate != null){
				if( ! this.startDate.after(d)){
					incl = false;
				}
			} 
			
			if(this.endDate != null){
				if( ! this.endDate.before(d)){
					incl = false;
				}
			}
			
			if(incl){
				this.includeDate = true;
			} else {
				this.includeDate= false;
			}
		}
		return result;
	}
	
	private String getFileName(String str, String token)
	{
		return getProperty(str, token, 'F');
	}
	
	private String getLocation(String str, String token)
	{
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		char sep = getSeparator('l', token);
		if (sep != 0)
		{
			if (sep ==')')
			{
				int end;
				end = str.indexOf(sep, ind);
				end = str.indexOf(sep, end);
				result = str.substring(ind, end);
				index  = end;
			}
			else
			{
				result = str.substring(ind, str.indexOf(sep, ind));
				index = str.indexOf(sep, ind);
			}
		}
		return result;

	}
	
	private String getLineNumber(String str, String token)
	{
		return getProperty(str, token, 'L');
	}
	
	private String getMessage(String str, String token)
	{
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		char sep = getSeparator('m', token);
		if(sep != 0)
		{
			result = str.substring(ind, str.indexOf(sep, ind));
			index = str.indexOf(sep, ind);
		}
		else
		{
			result = str.substring(ind);
			index = str.length();
		}
		return result;
	}
	
	private String getMethodName(String str, String token)
	{
		return getProperty(str, token, 'M');
	}
	
	private String getPriority(String str, String token)
	{
		return getProperty(str, token, 'p');
	}
	
	private String getElapsedMilliSecond(String str, String token)
	{
		return getProperty(str, token, 'r');
	}
	
	private String getThreadName(String str, String token)
	{
		return getProperty(str, token, 't');
	}
	
	private String getNDC(String str, String token)
	{
		return getProperty(str, token, 'x');
	}
	
	private String getMDC(String str, String token)
	{
		return getProperty(str, token, 'X');
	}
	
	private String getProperty(String str, String token, char separator)
	{
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1):index;
		char sep = getSeparator(separator, token);
		if(sep != 0)
		{
			result = str.substring(ind, str.indexOf(sep, ind));
			index = str.indexOf(sep, ind);
		}
		return result;
	}
	public String getColValueFromConversionChar(char c)
	{
		String res = null;
		
		switch(c)	
		{
			case 'c': 	
			{
				res = logEntry.getCategory();	//"CATEGORY";
				break;
			}
			case 'C':
			{
				res = logEntry.getClassName();	//"CLASS";
				break;
			}
			case 'd':
			{
				res = logEntry.getDate();		//"DATE";
				break;
			}
			case 'F': 	
			{
				res = logEntry.getFileName();	//"FILE";
				break;
			}
			case 'l':
			{
				res = logEntry.getLocation();	//"LOCATION";
				break;
			}
			case 'L': 	
			{
				res = logEntry.getLineNumber();	//"LINE";
				break;
			}
			case 'm':
			{
				res = logEntry.getMessage();	//"MESSAGE";
				break;
			}
			case 'n':
			{
				res = logEntry.getLineSeperator();	//lineSeperator : "\n";
				break;
			}
			case 'M':
			{
				res = logEntry.getMethod();		//"METHOD";
				break;
			}
			case 'p':
			{
				res = logEntry.getPriority();	//"PRIORITY";
				break;
			}
			case 'r':
			{
				res = logEntry.getMsElapsed();	//"ELAPSED";
				break;
			}
			case 't': 	
			{	
				res = logEntry.getThread();		//"THREAD";
				break;
			}
			case 'x':
			{
				res = logEntry.getNdc();		//"NDC";
				break;
			}
			case 'X':
			{
				res = logEntry.getMdc();		//"MDC";
				break;
			}
			default : break;
		}
		
		if (res != null)
		{
			return res;
		}
		return null;
	}
}

