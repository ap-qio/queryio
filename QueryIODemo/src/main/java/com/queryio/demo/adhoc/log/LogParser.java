package com.queryio.demo.adhoc.log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.MetadataConstants;
import com.queryio.demo.adhoc.DBListener;
import com.queryio.demo.adhoc.IAdHocParser;
import com.queryio.demo.adhoc.ParsedExpression;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class LogParser implements IAdHocParser{

	private static final Log LOG = LogFactory.getLog(LogParser.class);
	String ADHOC_PARSER_ENCODING = "encoding";
	String ADHOC_PARSER_LOG_PATTERN = "pattern";

	private static final String CONVERSION_CHARS = "cCdFlLmMnprtxX";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private String tokens[];
	private Map<Integer, String> nativeColumnNames = new TreeMap<Integer, String>();
	private Map<Integer, String> columns = new TreeMap<Integer, String>();
	private Map<Integer, Class> columnTypes = new TreeMap<Integer, Class>();
	private String pattern = "%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n";
	private String encoding = "UTF-8";
	ParsedExpression expressions;
	
	@Override
	public void setExpressions(ParsedExpression expressions) {
		this.expressions = expressions;
	}
	
	@Override
	public void setArguments(String arguments) throws Exception
	{
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(arguments);
		String argString = (String) obj.get("arguments");
		String fieldsString = (String) obj.get("fields");
		
		if (argString == null){
			throw new Exception("Invalid Arguments. Expected: <Arguments-JSonString> [<Fields-JSonString>]");
		}
		setArgumentsJSonString(argString);
		if (fieldsString != null){
			setFieldsJSonString(fieldsString);
		}
		else{
			initFieldsFromDataDefinition();
		}
	}
	
	private void initFieldsFromDataDefinition() {
		IDataDefinition dataDefinition = new LogDataDefinitionImpl();
		int i = 0;
		for(ColumnMetadata metadata : dataDefinition.getColumnMetadata()){
			columns.put(i, metadata.getColumnName());
			columnTypes.put(i, metadata.getColumnSqlDataType());
			i++;
		}
	}
	
	private void setFieldsJSonString(String fieldsJSonString) throws Exception{
		JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(fieldsJSonString);
        JSONObject obj = null;
        
        for (int i=0; i<array.size(); i++)
        {
            obj = (JSONObject) array.get(i);
            String column = String.valueOf(obj.get("colName"));
            Class columnType = getDataType(obj.get("colType").toString());
            this.columns.put(i, column);
            this.columnTypes.put(i, columnType);
            LOG.info("colName: " + column);
            LOG.info("colType: " + obj.get("colType"));
            LOG.info("colIndex: " + obj.get("colIndex"));
        }
	}
	
	private Class getDataType(String type)
	{
		Class dataType;
		
		if(type.indexOf(MetadataConstants.GENERIC_DATA_TYPE_STRING) != -1)
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(MetadataConstants.GENERIC_DATA_TYPE_STRING);
		else
			dataType = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(type);
		return dataType;
	}
	
	private void setArgumentsJSonString(String argumentsJSonString) throws Exception{
		JSONParser parser = new JSONParser();
        JSONObject arguments = (JSONObject) parser.parse(argumentsJSonString);
        pattern = String.valueOf(arguments.get(ADHOC_PARSER_LOG_PATTERN));
        encoding = String.valueOf(arguments.get(ADHOC_PARSER_ENCODING));
        LOG.info("pattern: " + pattern);
        LOG.info("encoding: " + encoding);
        StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		int i = 0;
		nativeColumnNames.put(i, "FILEPATH");
		while(st.hasMoreTokens())
		{
			tokens[i] = st.nextToken();
			nativeColumnNames.put((i + 1), getNativeColNameFromConversionChar(tokens[i].toCharArray()[0]));
			++i;
		}
	}
	
	public String getNativeColNameFromConversionChar(char c)
	{
		String res = null;
		
		switch(c)	
		{
			case 'c': 	
			{
				res = "CATEGORY";
				break;
			}
			case 'C':
			{
				res = "CLASS";
				break;
			}
			case 'd':
			{
				res = "DATE";
				break;
			}
			case 'F': 	
			{
				res = "FILE";
				break;
			}
			case 'l':
			{
				res = "LOCATION";
				break;
			}
			case 'L': 	
			{
				res = "LINE";
				break;
			}
			case 'm':
			{
				res = "MESSAGE";
				break;
			}
			case 'M':
			{
				res = "METHOD";
				break;
			}
			case 'n':
			{
				res = "LINE_SEPERATOR";			//"\n"
				break;
			}
			case 'p':
			{
				res = "PRIORITY";
				break;
			}
			case 'r':
			{
				res = "ELAPSED";
				break;
			}
			case 't': 	
			{	
				res = "THREAD";
				break;
			}
			case 'x':
			{
				res = "NDC";
				break;
			}
			case 'X':
			{
				res = "MDC";
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
	
	@Override
	public void parse(DBListener dbListener, String filePath, InputStream is)
			throws Exception {
		new Parser().parse(dbListener, filePath, is);
	}
	
	class Parser{
		SimpleDateFormat dateFormat = null;
		
		boolean includeDate = true;
		
		private Date startDate = null;
		private Date endDate = null;
		private int index;
		private char start;
		LogEntry logEntry;
		private StringBuffer lastMessage = new StringBuffer();
	
		public void parse(final DBListener dbListener, final String filePath, InputStream is)
				throws Exception {
			BufferedReader rd = null;
			String str;
			logEntry = new LogEntry();
			lastMessage = new StringBuffer();
			
			logEntry.setColumns(columns);
			logEntry.setColumnTypes(columnTypes);
			logEntry.setNativeColumns(nativeColumnNames);
			
			dbListener.createStatement(columns);
			try {
				InputStreamReader in = new InputStreamReader(is, encoding);

				rd = new BufferedReader(in);

				final IMessageListener listener = new IMessageListener() 
				{
					public void messageComplete() throws IOException, InterruptedException, SQLException 
					{
						String message = lastMessage.toString();
						logEntry.setMessage(message);
						
						if (expressions.evaluateEntry(logEntry) && includeDate){
							logEntry.setFilePath(filePath);
							dbListener.insertAdHocEntry(logEntry);	
						}
					}
				};
				
				while ((str = rd.readLine()) != null) {
//					LOG.info("Parsing Line: " + str);
					parseLine(str, listener);
				}
			} finally {
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
	}
}
