package com.queryio.demo.mr.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;

import com.queryio.plugin.datatags.TableMetadata;

public class LogParser
{
	private static final Log LOG = LogFactory.getLog(LogParser.class);
	private static final String CONVERSION_CHARS = "cCdFlLmMnprtxX";
	
	private String tokens[];

	private int index;
	private char start;
	
	private StringBuffer lastMessage = new StringBuffer();
	
	static final String NEW_LINE = System.getProperty("line.separator");

	private LogEntry logEntry = new LogEntry();
	
	SimpleDateFormat dateFormat = null;
	
	boolean includeDate = true;
	
	private FileStatus fileStatus = null;
	
	private Date startDate = null;
	private Date endDate = null;
	private String searchString = null;
	
	private PreparedStatement INSERTPST = null;
	private PreparedStatement DELETEPST = null;
	
	int maxBatchSize; 
	int currentBatchSize = 0;
	
	public LogParser(String pattern)
	{
		StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens())
		{
			tokens[i] = st.nextToken();
			++i;
		}
	}
	
	public LogParser(Connection connection, String tableName,String pattern, String searchString, Date startDate, Date endDate, FileStatus fileStatus, int maxBatchSize) throws SQLException
	{
		this.maxBatchSize = maxBatchSize;
		StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens())
		{
			tokens[i] = st.nextToken();
			++i;
		}
		
		this.searchString = searchString;
		this.startDate = startDate;
		this.endDate = endDate;
		
		LOG.info("Pattern: " + pattern);
		LOG.info("SearchString: " + this.searchString);
		LOG.info("StartDate: " + this.startDate);
		LOG.info("EndDate: " + this.endDate);
		
		StringBuffer QUERY = new StringBuffer("INSERT INTO " + tableName + " (");
		
//		"FILEPATH", "CATEGORY", "CLASS_NAME", "DATE", "FILE_NAME", "LINE_NUMBER", "LOCATION", "MDC", "MESSAGE", "METHOD", "ELAPSED", "NDC", "PRIORITY", "SEQUENCE", "THREAD"
		
		QUERY.append("FILEPATH,");
		QUERY.append("CATEGORY,");
		QUERY.append("CLASS_NAME,");
		QUERY.append("DATE,");
		QUERY.append("FILE_NAME,");
		QUERY.append("LINE_NUMBER,");
		QUERY.append("LOCATION,");
		QUERY.append("MDC,");
		QUERY.append("MESSAGE,");
		QUERY.append("METHOD,");
		QUERY.append("ELAPSED,");
		QUERY.append("NDC,");
		QUERY.append("PRIORITY,");
		QUERY.append("SEQUENCE,");
		QUERY.append("THREAD");
		QUERY.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		LOG.info("QUERY: " + QUERY);
		INSERTPST = connection.prepareStatement(QUERY.toString());
		
		StringBuffer DELETEQUERY = new StringBuffer("DELETE FROM " + tableName + " WHERE ");
		DELETEQUERY.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		DELETEQUERY.append(" = ?");		
		LOG.info("DELETEQUERY: " + DELETEQUERY);
		DELETEPST = connection.prepareStatement(DELETEQUERY.toString());
		
		this.fileStatus = fileStatus;
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
	
	public void parse(InputStream is) throws IOException, InterruptedException, SQLException {
		BufferedReader rd = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is);

			rd = new BufferedReader(in);

			final IMessageListener listener = new IMessageListener() 
			{
				
				public void messageComplete() throws IOException, InterruptedException, SQLException 
				{
					String lastMessage = LogParser.this.getLastMessage();
					
					LogParser.this.logEntry.setMessage(lastMessage.toString());
					
					boolean incl = true;
					LOG.info("Message: " + LogParser.this.logEntry.getMessage());
					if(LogParser.this.searchString!=null){
						LOG.info("Search String: " + LogParser.this.searchString);
						if(LogParser.this.logEntry.getMessage() != null){
							if( ! LogParser.this.logEntry.getMessage().contains(searchString)){
								incl = false;
							}
						}
					}
					
					if(incl && LogParser.this.includeDate){
						DELETEPST.setString(1, fileStatus.getPath().toUri().getPath());
						DELETEPST.execute();
						
						int i=1;						
						INSERTPST.setString(i ++, fileStatus.getPath().toUri().getPath());
						INSERTPST.setString(i ++, logEntry.getCategory());
						INSERTPST.setString(i ++, logEntry.getClassName());
						INSERTPST.setString(i ++, logEntry.getDate());
						INSERTPST.setString(i ++, logEntry.getFileName());
						INSERTPST.setString(i ++, logEntry.getLineNumber());
						INSERTPST.setString(i ++, logEntry.getLocation());
						INSERTPST.setString(i ++, logEntry.getMdc());
						INSERTPST.setString(i ++, logEntry.getMessage());
						INSERTPST.setString(i ++, logEntry.getMethod());
						INSERTPST.setString(i ++, logEntry.getMsElapsed());
						INSERTPST.setString(i ++, logEntry.getNdc());
						INSERTPST.setString(i ++, logEntry.getPriority());
						INSERTPST.setString(i ++, logEntry.getSequence());
						INSERTPST.setString(i ++, logEntry.getThread());
						INSERTPST.addBatch();
						currentBatchSize ++;
						
						
						
						if (currentBatchSize % maxBatchSize == 0)
						{
							try
							{
								INSERTPST.executeBatch();
							}
							catch (Exception e)
							{
								LOG.fatal("Exception in executeBatch: ", e);
								if (e instanceof BatchUpdateException)
								{
									throw ((BatchUpdateException) e).getNextException();
								}
							}
							INSERTPST.clearBatch();
							currentBatchSize = 0;
						}	
						
						LogParser.this.logEntry = new LogEntry();						
					}
				}
			};
			
			while ((str = rd.readLine()) != null) {
				LOG.info("Parsing Line: " + str);
				this.parseLine(str, listener);
			}
			try
			{
				INSERTPST.executeBatch();
			}
			catch (Exception e)
			{
				LOG.fatal("Exception in executeBatch: ", e);
				if (e instanceof BatchUpdateException)
				{
					throw ((BatchUpdateException) e).getNextException();
				}
			}
		} finally {
			if (INSERTPST != null){
				try{
					if(currentBatchSize > 0)
						INSERTPST.executeBatch();
				} catch (Exception e) {
					LOG.fatal("Exception in executeBatch: ", e);
					if (e instanceof BatchUpdateException)
					{
						throw ((BatchUpdateException) e).getNextException();
					}
				}
				finally{
					try {
						INSERTPST.close();
					} catch (SQLException e) {
						LOG.fatal("Error Closing PreparedStatement", e);
					}	
				}
			}
			if(DELETEPST != null)
				DELETEPST.close();
			try {
				if (rd != null)
					rd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
