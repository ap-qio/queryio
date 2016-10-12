package com.queryio.demo.mr.report.aos;

import java.util.List;


public class JTLRecordMetaData {
	
	private int sampleDuration;
	private int startTimestampIndex;
	private int labelIndex;
	private int errorCountIndex;
	private int bytesIndex;
	private int latencyIndex;
	private long startTime;
	private long endTime;
	private int waitTime;
	private List<Long> ticks;
	
	private long testDuration;
	
	public int getSampleDuration() {
		return sampleDuration;
	}
	public void setSampleDuration(int sampleDuration) {
		this.sampleDuration = sampleDuration;
	}
	public int getStartTimestampIndex() {
		return startTimestampIndex;
	}
	public void setStartTimestampIndex(int startTimestampIndex) {
		this.startTimestampIndex = startTimestampIndex;
	}
	public int getLabelIndex() {
		return labelIndex;
	}
	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}
	public int getErrorCountIndex() {
		return errorCountIndex;
	}
	public void setErrorCountIndex(int errorCountIndex) {
		this.errorCountIndex = errorCountIndex;
	}
	public int getBytesIndex() {
		return bytesIndex;
	}
	public void setBytesIndex(int bytesIndex) {
		this.bytesIndex = bytesIndex;
	}
	public int getLatencyIndex() {
		return latencyIndex;
	}
	public void setLatencyIndex(int latencyIndex) {
		this.latencyIndex = latencyIndex;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getTestDuration() {
		return testDuration;
	}
	public void setTestDuration(long testDuration) {
		this.testDuration = testDuration;
	}
	public List<Long> getTicks() {
		return ticks;
	}
	public void setTicks(List<Long> ticks) {
		this.ticks = ticks;
	}

}
