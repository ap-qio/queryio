package com.queryio.demo.adhoc.log;

import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualTreeBidiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.queryio.demo.adhoc.AdHocEntry;


public class LogEntry extends AdHocEntry{
	
	private static final Log LOG = LogFactory.getLog(LogEntry.class);
	
	final static String FILEPATH = "FILEPATH";
	final static String CATEGORY = "CATEGORY";
	final static String CLASS = "CLASS";
	final static String DATE = "DATE";
	final static String FILE = "FILE";
	final static String LINE = "LINE";
	final static String LOCATION = "LOCATION";
	final static String MDC = "MDC";
	final static String MESSAGE = "MESSAGE";
	final static String METHOD = "METHOD";
	final static String ELAPSED = "ELAPSED";
	final static String NDC = "NDC";
	final static String PRIORITY = "PRIORITY";
	final static String SEQUENCE = "SEQUENCE";
	final static String THREAD = "THREAD";
	final static String LINE_SEPERATOR = "LINE_SEPERATOR";
	
	private String filePath = "";
	private String category = "";
	private String className = "";
	private String date = "";
	private String fileName = "";
	private String location = "";
	private String lineNumber = "";
	private String message = "";
	private String method = "";
	private String lineSeperator = "";
	private String priority = "";
	private String msElapsed = "";
	private String thread = "";
	private String ndc = "";
	private String mdc = "";
	private String sequence = "";
	
	BidiMap nativeColumnNames = new DualTreeBidiMap();
	
	public BidiMap getNativeColumns() {
		return nativeColumnNames;
	}
	public void setNativeColumns(Map<Integer, String> nativeColumnNames) {
		this.nativeColumnNames = new DualTreeBidiMap(nativeColumnNames);
	}
	
	public int getIndex(String colName)
	{
		int index = -1;
		try {
			index = (Integer) getNativeColumns().inverseBidiMap().get(colName);
		} catch(Exception e) {
			LOG.fatal("Error: " , e);
		}
		if (index == -1)
			index = (Integer) super.getColumns().inverseBidiMap().get(colName);
		
		return index;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		int index = getIndex(FILEPATH);
		if(index != -1)
			super.addValue(index, filePath);
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
		int index = getIndex(CATEGORY);
		if(index != -1)
			super.addValue(index, category);
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
		int index = getIndex(CLASS);
		if(index != -1)
			super.addValue(index, className);
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
		int index = getIndex(DATE);
		if(index != -1)
			super.addValue(index, date);
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
		int index = getIndex(FILE);
		if(index != -1)
			super.addValue(index, fileName);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
		int index = getIndex(LOCATION);
		if(index != -1)
			super.addValue(index, location);
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
		int index = getIndex(LINE);
		if(index != -1)
			super.addValue(index, lineNumber);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
		int index = getIndex(MESSAGE);
		if(index != -1)
			super.addValue(index, message);
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
		int index = getIndex(METHOD);
		if(index != -1)
			super.addValue(index, method);
	}
	public String getLineSeperator() {
		return lineSeperator;
	}
	public void setLineSeperator(String lineSeperator) {
		this.lineSeperator = lineSeperator;
		int index = getIndex(LINE_SEPERATOR);
		if(index != -1)
			super.addValue(index, lineSeperator);
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
		int index = getIndex(PRIORITY);
		if(index != -1)
			super.addValue(index, priority);
	}
	public String getMsElapsed() {
		return msElapsed;
	}
	public void setMsElapsed(String msElapsed) {
		this.msElapsed = msElapsed;
		int index = getIndex(ELAPSED);
		if(index != -1)
			super.addValue(index, msElapsed);
	}
	public String getThread() {
		return thread;
	}
	public void setThread(String thread) {
		this.thread = thread;
		int index = getIndex(THREAD);
		if(index != -1)
			super.addValue(index, thread);
	}
	public String getNdc() {
		return ndc;
	}
	public void setNdc(String ndc) {
		this.ndc = ndc;
		int index = getIndex(NDC);
		if(index != -1)
			super.addValue(index, ndc);
	}
	public String getMdc() {
		return mdc;
	}
	public void setMdc(String mdc) {
		this.mdc = mdc;
		int index = getIndex(MDC);
		if(index != -1)
			super.addValue(index, mdc);
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
		int index = getIndex(SEQUENCE);
		if(index != -1)
			super.addValue(index, sequence);
	}
	
	public String toString(){
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("category: " + category).append(NEW_LINE);
		sBuf.append("className: " + className).append(NEW_LINE);
		sBuf.append("date: " + date).append(NEW_LINE);
		sBuf.append("fileName: " + fileName).append(NEW_LINE);
		sBuf.append("location: " + location).append(NEW_LINE);
		sBuf.append("lineNumber: " + lineNumber).append(NEW_LINE);
		sBuf.append("message: " + message).append(NEW_LINE);
		sBuf.append("method: " + method).append(NEW_LINE);
		sBuf.append("priority: " + priority).append(NEW_LINE);
		sBuf.append("msElapsed: " + msElapsed).append(NEW_LINE);
		sBuf.append("thread: " + thread).append(NEW_LINE);
		sBuf.append("ndc: " + ndc).append(NEW_LINE);
		sBuf.append("mdc: " + mdc).append(NEW_LINE);
		
		return sBuf.toString();
	}
	
	public final String NEW_LINE = "\n";
}
