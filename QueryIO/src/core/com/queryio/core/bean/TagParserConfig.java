package com.queryio.core.bean;

public class TagParserConfig {
	int id;
	String tagName;
	String description;
	String jarName;
	String fileTypes;
	String className;
	String namenodeId;
	boolean onIngest;
	boolean isActive;

	public TagParserConfig(int id, String tagName, String description, String jarName, String fileTypes,
			String className, String namenodeId, boolean onIngest, boolean isActive) {
		this.id = id;
		this.tagName = tagName;
		this.description = description;
		this.jarName = jarName;
		this.fileTypes = fileTypes;
		this.className = className;
		this.namenodeId = namenodeId;
		this.onIngest = onIngest;
		this.isActive = isActive;
	}

	public String getNamenodeId() {
		return namenodeId;
	}

	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}

	public boolean isIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public boolean isOnIngest() {
		return onIngest;
	}

	public void setOnIngest(boolean onIngest) {
		this.onIngest = onIngest;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getJarName() {
		return jarName;
	}

	public String getFileTypes() {
		return fileTypes;
	}

	public String getClassName() {
		return className;
	}
}
