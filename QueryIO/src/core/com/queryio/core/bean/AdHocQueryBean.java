package com.queryio.core.bean;

import java.io.Serializable;

public class AdHocQueryBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String adHocId;
	String namenodeId;
	String rmId;
	String sourcePath;
	boolean parseRecursive;
	String type;
	String adHocTableName;
	String filePathPattern;
	String fields;
	String encoding;
	String arguments;
	
	public String getAdHocId() {
		return adHocId;
	}
	public void setAdHocId(String adHocId) {
		this.adHocId = adHocId;
	}
	public String getNamenodeId() {
		return namenodeId;
	}
	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}
	public String getRmId() {
		return rmId;
	}
	public void setRmId(String rmId) {
		this.rmId = rmId;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public boolean isParseRecursive() {
		return parseRecursive;
	}
	public void setParseRecursive(boolean parseRecursive) {
		this.parseRecursive = parseRecursive;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAdHocTableName() {
		return adHocTableName;
	}
	public void setAdHocTableName(String adHocTableName) {
		this.adHocTableName = adHocTableName;
	}
	public String getFilePathPattern() {
		return filePathPattern;
	}
	public void setFilePathPattern(String filePathPattern) {
		this.filePathPattern = filePathPattern;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getArguments() {
		return arguments;
	}
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
}