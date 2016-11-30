package com.queryio.core.bean;

public class AdHocJobConfig {
	String namenodeId;
	String rmId;
	String jobName;
	String jarFile;
	String libjars = "";
	String files = "";
	String className;
	String sourcePath;
	String pathPattern;
	String arguments;

	public AdHocJobConfig(String namenodeId, String rmId, String jobName, String jarFile, String libjars, String files,
			String className, String sourcePath, String pathPattern, String arguments) {
		this.namenodeId = namenodeId;
		this.rmId = rmId;
		this.jobName = jobName;
		this.jarFile = jarFile;
		this.libjars = libjars;
		this.files = files;
		this.className = className;
		this.sourcePath = sourcePath;
		this.pathPattern = pathPattern;
		this.arguments = arguments;
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

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJarFile() {
		return jarFile;
	}

	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	public String getLibjars() {
		return libjars;
	}

	public void setLibjars(String libjars) {
		this.libjars = libjars;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getPathPattern() {
		return pathPattern;
	}

	public void setPathPattern(String pathPattern) {
		this.pathPattern = pathPattern;
	}

	public String getArguments() {
		return arguments;
	}
}
