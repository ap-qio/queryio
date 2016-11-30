package com.queryio.core.bean;

public class MapRedJobConfig {
	String namenodeId;
	String rmId;
	String jobName;
	String jarName;
	String className;
	String arguments;
	String libJars;
	String files;

	// Input path filter
	boolean isFilterApply;
	boolean isRecursive;
	String filterQuery;

	public MapRedJobConfig(String namenodeId, String rmId, String jobName, String jarName, String libJars, String files,
			String className, String arguments, boolean isRecursive, boolean isFilterApply, String filterQuery) {
		this.namenodeId = namenodeId;
		this.rmId = rmId;
		this.jobName = jobName;
		this.jarName = jarName;
		this.libJars = libJars;
		this.files = files;
		this.className = className;
		this.arguments = arguments;
		this.isFilterApply = isFilterApply;
		this.isRecursive = isRecursive;
		this.filterQuery = filterQuery;
	}

	public String getRmId() {
		return rmId;
	}

	public void setRmId(String rmId) {
		this.rmId = rmId;
	}

	public String getNamenodeId() {
		return namenodeId;
	}

	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public void setLibJars(String libJars) {
		this.libJars = libJars;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getJobName() {
		return jobName;
	}

	public String getJarName() {
		return jarName;
	}

	public String getClassName() {
		return className;
	}

	public String getArguments() {
		return arguments;
	}

	public String getLibJars() {
		return libJars;
	}

	public String getFiles() {
		return files;
	}

	public boolean isFilterApply() {
		return isFilterApply;
	}

	public void setFilterApply(boolean isFilterApply) {
		this.isFilterApply = isFilterApply;
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}

	public String getFilterQuery() {
		return filterQuery;
	}

	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

}