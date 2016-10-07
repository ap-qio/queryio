package com.queryio.common.exporter.dstruct;

import com.queryio.common.util.PlatformHandler;

public class ReportHeader {
	
	private String projectName;
	private String executionTime;
	private String reportTitle;
	
	public ReportHeader(String projectName, String executionTime,
			String reportTitle) {
		this.projectName = projectName;
		this.executionTime = executionTime;
		this.reportTitle = reportTitle;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
	public String toString(boolean showReportTitle) {
		StringBuffer buff = new StringBuffer();
		if(showReportTitle && reportTitle != null){
			buff.append(reportTitle);
			buff.append(" for ");
			buff.append(projectName);
			buff.append(PlatformHandler.LINE_SEPARATOR);
		} else{
			buff.append("Project Execution Report for ");
			buff.append(projectName);
			buff.append(PlatformHandler.LINE_SEPARATOR);
		}
		if(executionTime != null){
			buff.append("Execution started at : ");
			buff.append(executionTime);
			buff.append(PlatformHandler.LINE_SEPARATOR);
		}
		return buff.toString();
	}
}
