package com.queryio.core.adhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.MetadataConstants;

public class Log4JParser {
	private static final Log LOG = LogFactory.getLog(Log4JParser.class);
	private static final String CONVERSION_CHARS = "cCdFlLmMnprtxX";
	private static String fileName = null;
	private String tokens[];

	private Map<Integer, String> nativeColumnNames = new TreeMap<Integer, String>();

	private int index;
	private char start;

	private static boolean isFirstTime = false;

	private static String encodingType = "UTF-8";

	private static int dataCounter = 0;

	private static int recordsToAnalyze = 0;

	private StringBuffer lastMessage = new StringBuffer();

	public static final String NEW_LINE = System.getProperty("line.separator");

	private static LogEntry logEntry = null;

	SimpleDateFormat dateFormat = null;

	public static final String DT_VARCHAR = MetadataConstants.GENERIC_DATA_TYPE_STRING;
	public static final String DT_VARCHAR_DEFAULT[] = { "(128)", "(255)", "(512)", "(1024)", "(1280)", "(5000)" };

	private JSONArray data = new JSONArray();
	private JSONObject header = new JSONObject();
	private JSONArray details = new JSONArray();

	@SuppressWarnings("unchecked")
	public Log4JParser(String pattern, String name, String encoding, int noOfRecords) {
		fileName = name;
		encodingType = encoding;
		StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		logEntry = new LogEntry();
		int i = 0;
		dataCounter = 0;
		lastMessage = new StringBuffer();
		isFirstTime = false;

		this.recordsToAnalyze = noOfRecords;

		if (!isFirstTime)
			header.put(i, "FILEPATH");
		String colName = null;
		int messageIndex = -1;

		while (st.hasMoreTokens()) {
			tokens[i] = st.nextToken();
			colName = getNativeColNameFromConversionChar(tokens[i].toCharArray()[0]);
			if ("MESSAGE".equalsIgnoreCase(colName))
				messageIndex = i + 1;
			nativeColumnNames.put((i + 1), colName);
			if (!isFirstTime)
				header.put((i + 1), colName);
			++i;
		}
		if (!isFirstTime) {
			// for (int j=0; j<nativeColumnNames.size(); j++)
			// {
			// header.put(j, nativeColumnNames.get(j));
			// }

			// header.put(0, "FILEPATH");
			// header.put(1, "CATEGORY");
			// header.put(2, "CLASS_NAME");
			// header.put(3, "DATE");
			// header.put(4, "FILE_NAME");
			// header.put(5, "LOCATION");
			// header.put(6, "LINE_NUMBER");
			// header.put(7, "MESSAGE");
			// header.put(8, "METHOD");
			// header.put(9, "PRIORITY");
			// header.put(10, "MS_ELAPSED");
			// header.put(11, "THREAD");
			// header.put(12, "NDC");
			// header.put(13, "MDC");
			// header.put(14, "SEQUENCE");

			JSONObject object = new JSONObject();

			object = new JSONObject();
			object.put("index", 0);
			object.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]); // For
																	// FILEPATH
			details.add(0, object);

			for (int count = 1; count < header.size(); count++) {
				object = new JSONObject();
				object.put("index", count);
				if (messageIndex == count)
					object.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[5]);
				else
					object.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
				details.add(count, object);
			}
			isFirstTime = true;
		}
	}

	public String getNativeColNameFromConversionChar(char c) {
		String res = null;

		switch (c) {
		case 'c': {
			res = "CATEGORY";
			break;
		}
		case 'C': {
			res = "CLASS";
			break;
		}
		case 'd': {
			res = "DATE";
			break;
		}
		case 'F': {
			res = "FILE";
			break;
		}
		case 'l': {
			res = "LOCATION";
			break;
		}
		case 'L': {
			res = "LINE";
			break;
		}
		case 'm': {
			res = "MESSAGE";
			break;
		}
		case 'M': {
			res = "METHOD";
			break;
		}
		case 'n': {
			res = "LINE_SEPERATOR"; // "\n"
			break;
		}
		case 'p': {
			res = "PRIORITY";
			break;
		}
		case 'r': {
			res = "ELAPSED";
			break;
		}
		case 't': {
			res = "THREAD";
			break;
		}
		case 'x': {
			res = "NDC";
			break;
		}
		case 'X': {
			res = "MDC";
			break;
		}
		default:
			break;
		}

		if (res != null) {
			return res;
		}
		return null;
	}

	public String getColValueFromConversionChar(char c) {
		String res = null;

		switch (c) {
		case 'c': {
			res = logEntry.getCategory(); // "CATEGORY";
			break;
		}
		case 'C': {
			res = logEntry.getClassName(); // "CLASS";
			break;
		}
		case 'd': {
			res = logEntry.getDate(); // "DATE";
			break;
		}
		case 'F': {
			res = logEntry.getFileName(); // "FILE";
			break;
		}
		case 'l': {
			res = logEntry.getLocation(); // "LOCATION";
			break;
		}
		case 'L': {
			res = logEntry.getLineNumber(); // "LINE";
			break;
		}
		case 'm': {
			res = logEntry.getMessage(); // "MESSAGE";
			break;
		}
		case 'n': {
			res = logEntry.getLineSeperator(); // lineSeperator : "\n";
			break;
		}
		case 'M': {
			res = logEntry.getMethod(); // "METHOD";
			break;
		}
		case 'p': {
			res = logEntry.getPriority(); // "PRIORITY";
			break;
		}
		case 'r': {
			res = logEntry.getMsElapsed(); // "ELAPSED";
			break;
		}
		case 't': {
			res = logEntry.getThread(); // "THREAD";
			break;
		}
		case 'x': {
			res = logEntry.getNdc(); // "NDC";
			break;
		}
		case 'X': {
			res = logEntry.getMdc(); // "MDC";
			break;
		}
		default:
			break;
		}

		if (res != null) {
			return res;
		}
		return null;
	}

	public Log4JParser(String pattern, String searchString, Date startDate, Date endDate) {
		StringTokenizer st = new StringTokenizer(pattern, "%");
		tokens = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			tokens[i] = st.nextToken();
			++i;
		}

		LOG.info("Pattern: " + pattern);
	}

	void resetLastMessage() {
		lastMessage.setLength(0);
	}

	boolean parseLine(String str, IMessageListener listener) throws IOException, InterruptedException, SQLException {
		boolean flag = true;
		start = 0;
		index = 0;
		int i = 0;
		String s = null;
		while (i < tokens.length) {
			if (index < str.length() && !tokens[i].isEmpty()) {
				char arr[] = tokens[i].toCharArray();
				int j = 0;
				while (j < arr.length && arr[j] != '{') {
					if (isConversionCharacter(arr[j])) {
						s = identifyConversionCharacter(arr[j], str, tokens[i]);

						if (s != null) {
							if (arr[j] == 'm') {
								if (listener != null && lastMessage.length() > 0) {
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
				if (s == null)
					break;
			}
			++i;
		}
		if (flag) {
			lastMessage.append(NEW_LINE);
			lastMessage.append("\t");
			lastMessage.append(str);
		}
		return !flag;
	}

	String getLastMessage() {
		return this.lastMessage.toString();
	}

	public void parse(InputStream is) throws IOException, InterruptedException, SQLException {
		BufferedReader rd = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is, encodingType);

			rd = new BufferedReader(in);

			final IMessageListener listener = new IMessageListener() {

				public void messageComplete() throws IOException, InterruptedException, SQLException {

					String lastMessage = Log4JParser.this.getLastMessage();

					Log4JParser.this.logEntry.setMessage(lastMessage.toString());

					// Handle

					List<String> rowdata = new ArrayList<String>();

					rowdata.add(fileName);
					for (int k = 0; k < tokens.length; k++) {
						rowdata.add(getColValueFromConversionChar(tokens[k].toCharArray()[0]));
					}

					// rowdata.add(FILE_NAME);
					// rowdata.add(logEntry.getCategory());
					// rowdata.add(logEntry.getClassName());
					// rowdata.add(logEntry.getDate());
					// rowdata.add(logEntry.getFileName());
					// rowdata.add(logEntry.getLocation());
					// rowdata.add(logEntry.getLineNumber());
					// rowdata.add(logEntry.getMessage());
					// rowdata.add(logEntry.getMethod());
					// rowdata.add(logEntry.getPriority());
					// rowdata.add(logEntry.getMsElapsed());
					// rowdata.add(logEntry.getThread());
					// rowdata.add(logEntry.getNdc());
					// rowdata.add(logEntry.getMdc());
					// rowdata.add(logEntry.getSequence());

					data.add(rowdata);

					dataCounter++;
					// Create new log entry for next line.

					Log4JParser.this.logEntry = new LogEntry();

				}
			};

			while ((str = rd.readLine()) != null && dataCounter < recordsToAnalyze) {
				LOG.info("Parsing Line: " + str);
				if (dataCounter <= recordsToAnalyze)
					this.parseLine(str, listener);
				else
					break;
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

	private boolean isConversionCharacter(char c) {
		return (CONVERSION_CHARS.indexOf(c) != -1);
	}

	private String identifyConversionCharacter(char c, String str, String token) {
		String res = null;
		// String identifier = null;
		switch (c) {
		case 'c': {
			String category = this.getCategory(str, token);
			logEntry.setCategory(category);
			res = category;
			// identifier = "Category";
			break;
		}
		case 'C': {
			String className = this.getClassName(str, token);
			;
			logEntry.setClassName(className);
			res = className;
			// identifier = "Class Name";
			break;
		}
		case 'd': {
			String date = this.getDate(str, token);
			logEntry.setDate(date);
			res = date;
			// identifier = "Date";
			break;
		}
		case 'F': {
			String fileName = this.getFileName(str, token);
			logEntry.setFileName(fileName);
			res = fileName;
			// identifier = "File Name";
			break;
		}
		case 'l': {
			String location = this.getLocation(str, token);
			logEntry.setLocation(location);
			res = location;
			// identifier = "Location";
			break;
		}
		case 'L': {
			String line = this.getLineNumber(str, token);
			logEntry.setLineNumber(line);
			res = line;
			// identifier = "Line number";
			break;
		}
		case 'm': {
			String message = this.getMessage(str, token);
			logEntry.setMessage(message);
			res = message;
			// identifier = "Message";
			break;
		}
		case 'M': {
			String method = this.getMethodName(str, token);
			logEntry.setMethod(method);
			res = method;
			// identifier = "Method Name";
			break;
		}
		case 'n': {
			res = "\n";
			break;
		}
		case 'p': {
			String priority = this.getPriority(str, token);
			logEntry.setPriority(priority);
			res = priority;
			// identifier = "Level";
			break;
		}
		case 'r': {
			String elapsed = this.getElapsedMilliSecond(str, token);
			logEntry.setMsElapsed(elapsed);
			res = elapsed;
			// identifier = "Elapsed milliseconds";
			break;
		}
		case 't': {
			String threadName = this.getThreadName(str, token);
			logEntry.setThread(threadName);
			res = threadName;
			// identifier = "Thread Name";
			break;
		}
		case 'x': {
			String ndc = this.getNDC(str, token);
			logEntry.setNdc(ndc);
			res = ndc;
			// identifier = "NDC";
			break;
		}
		case 'X': {
			String mdc = this.getMDC(str, token);
			logEntry.setMdc(mdc);
			res = mdc;
			// identifier = "Category";
			break;
		}
		default:
			break;
		}
		if (res != null) {
			// if (identifier != null)
			// return identifier + ": " + res;
			return res;
		}
		return null;
	}

	private char getSeparator(char c, String token) {
		char sep;
		int ind;
		if ((ind = token.indexOf('}')) != -1) {
			ind = ind + 1;
		} else {
			ind = token.indexOf(c) + 1;
		}
		if (ind == token.length()) {
			sep = 0;
			start = 0;
		} else {
			sep = token.charAt(ind);
			start = token.charAt(token.length() - 1);
		}
		return sep;
	}

	private String getCategory(String str, String token) {
		return getProperty(str, token, 'c');
	}

	private String getClassName(String str, String token) {
		return getProperty(str, token, 'C');
	}

	private String getDate(String str, String token) {
		String pattern = null;
		String s = "yyyy-MM-dd HH:mm:ss,SSS";
		int ind = token.indexOf('{');
		if (ind != -1) {
			pattern = token.substring(ind + 1, token.indexOf('}'));
			if (pattern.equals("ABSOLUTE")) {
				s = "HH:mm:ss,SSS";
			} else if (pattern.equals("DATE")) {
				s = "dd MMM yyyy HH:mm:ss,SSS";
			} else if (pattern.equals("ISO8601")) {
				// do nothing
			} else {
				s = pattern;
			}
		}
		String result = null;

		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat(s);
		}

		ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		getSeparator('d', token);
		Date d = dateFormat.parse(str, new ParsePosition(ind));
		if (d != null)
			result = d.toString();

		return result;
	}

	private String getFileName(String str, String token) {
		return getProperty(str, token, 'F');
	}

	private String getLocation(String str, String token) {
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		char sep = getSeparator('l', token);
		if (sep != 0) {
			if (sep == ')') {
				int end;
				end = str.indexOf(sep, ind);
				end = str.indexOf(sep, end);
				result = str.substring(ind, end);
				index = end;
			} else {
				result = str.substring(ind, str.indexOf(sep, ind));
				index = str.indexOf(sep, ind);
			}
		}
		return result;

	}

	private String getLineNumber(String str, String token) {
		return getProperty(str, token, 'L');
	}

	private String getMessage(String str, String token) {
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		char sep = getSeparator('m', token);
		if (sep != 0) {
			result = str.substring(ind, str.indexOf(sep, ind));
			index = str.indexOf(sep, ind);
		} else {
			result = str.substring(ind);
			index = str.length();
		}
		return result;
	}

	private String getMethodName(String str, String token) {
		return getProperty(str, token, 'M');
	}

	private String getPriority(String str, String token) {
		return getProperty(str, token, 'p');
	}

	private String getElapsedMilliSecond(String str, String token) {
		return getProperty(str, token, 'r');
	}

	private String getThreadName(String str, String token) {
		return getProperty(str, token, 't');
	}

	private String getNDC(String str, String token) {
		return getProperty(str, token, 'x');
	}

	private String getMDC(String str, String token) {
		return getProperty(str, token, 'X');
	}

	private String getProperty(String str, String token, char separator) {
		String result = null;
		int ind = start != 0 ? (str.indexOf(start, index) + 1) : index;
		char sep = getSeparator(separator, token);
		if (sep != 0) {
			result = str.substring(ind, str.indexOf(sep, ind));
			index = str.indexOf(sep, ind);
		}
		return result;
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
}
