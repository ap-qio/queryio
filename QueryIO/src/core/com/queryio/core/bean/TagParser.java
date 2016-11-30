package com.queryio.core.bean;

public class TagParser {
	int id;
	String tagName;
	String description;
	String jarName;
	String fileTypes;
	String className;
	boolean onIngest;

	public TagParser(int id, String tagName, String description, String jarName, String fileTypes, String className,
			boolean onIngest) {
		this.id = id;
		this.tagName = tagName;
		this.description = description;
		this.jarName = jarName;
		this.fileTypes = fileTypes;
		this.className = className;
		this.onIngest = onIngest;
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
