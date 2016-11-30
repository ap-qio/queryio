package com.queryio.datatags.common;

public class LogEntry {
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLineSeperator() {
		return lineSeperator;
	}

	public void setLineSeperator(String lineSeperator) {
		this.lineSeperator = lineSeperator;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getMsElapsed() {
		return msElapsed;
	}

	public void setMsElapsed(String msElapsed) {
		this.msElapsed = msElapsed;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getMdc() {
		return mdc;
	}

	public void setMdc(String mdc) {
		this.mdc = mdc;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String toString() {
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
