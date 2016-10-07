package com.queryio.demo.mr.report.aos;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class ConsolidatedRecord extends Record implements Writable {
	
	private Double minLatency;
	private Double maxLatency;
	private Double avgLatency;
	private Long minBytes;
	private Long maxBytes;
	private Double avgBytes;
	private Double avgTps;
	private Long successCount;
	private Double successPercentage;
	
	public Double getAvgTps() {
		return avgTps;
	}
	public void setAvgTps(Double avgTps) {
		this.avgTps = avgTps;
	}
	public Long getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}
	public Double getSuccessPercentage() {
		return successPercentage;
	}
	public void setSuccessPercentage(Double successPercentage) {
		this.successPercentage = successPercentage;
	}
	public Double getMinLatency() {
		return minLatency;
	}
	public void setMinLatency(Double minLatency) {
		this.minLatency = minLatency;
	}
	public Double getMaxLatency() {
		return maxLatency;
	}
	public void setMaxLatency(Double maxLatency) {
		this.maxLatency = maxLatency;
	}
	public Double getAvgLatency() {
		return avgLatency;
	}
	public void setAvgLatency(Double avgLatency) {
		this.avgLatency = avgLatency;
	}
	public Long getMinBytes() {
		return minBytes;
	}
	public void setMinBytes(Long minBytes) {
		this.minBytes = minBytes;
	}
	public Long getMaxBytes() {
		return maxBytes;
	}
	public void setMaxBytes(Long maxBytes) {
		this.maxBytes = maxBytes;
	}
	
	
	public ConsolidatedRecord() {
		
	}	
	public ConsolidatedRecord(Record record) {
		super(record);
		
		minLatency = Double.MAX_VALUE;
		maxLatency = Double.MIN_VALUE;
		avgLatency = 0.0;
		minBytes = Long.MAX_VALUE;
		maxBytes = Long.MIN_VALUE;
		avgBytes = 0.0;
		avgTps = 0.0;
		successCount = 0l;
		successPercentage = 0.0;
	}	
	
	@Override
	public void write(DataOutput out) throws IOException {
		
		super.write(out);
		
		out.writeDouble(minLatency);
		out.writeDouble(maxLatency);
		out.writeDouble(avgLatency);
		out.writeLong(minBytes);
		out.writeLong(maxBytes);
		out.writeDouble(getAvgBytes());
		out.writeDouble(avgTps);
		out.writeLong(getSuccessCount());
		out.writeDouble(successPercentage);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		
		super.readFields(in);
		
		minLatency = in.readDouble();
		maxLatency = in.readDouble();
		avgLatency = in.readDouble();
		minBytes = in.readLong();
		maxBytes = in.readLong();
		avgBytes = in.readDouble();
		avgTps = in.readDouble();
		successCount = in.readLong();
		successPercentage = in.readDouble();
	}
	
	@Override
	public String toString() {
		return label + "," + tickStartTime + "," + tickEndTime + "," + sampleValue + "," + latency + "," + bytes + "," + errorCount + "," + success + "," + minLatency + "," + maxLatency + "," + avgLatency + "," + minBytes + "," + maxBytes + "," + getAvgBytes() + "," + getTaskCount() + "," + avgTps + "," + getSuccessCount() + "," + successPercentage;
	}
	
	public static ConsolidatedRecord getObjectWithDefaultInitialization() {
		
		ConsolidatedRecord record = new ConsolidatedRecord();
		record.minLatency = 0.0;
		record.maxLatency = 0.0;
		record.avgLatency = 0.0;
		record.minBytes = 0l;
		record.maxBytes = 0l;
		record.avgBytes = 0.0;
		record.avgTps = 0.0;
		record.successCount = 0l;
		record.successPercentage = 0.0;
		
		record.tickStartTime = 0l;
		record.tickEndTime = 0l;
		record.latency = 0.0;
		record.bytes = 0l;
		record.errorCount = 0l;
		record.success = false;
		record.sampleValue = 0;
		record.taskCount = 0l;
		
		return record;
	}
	public Double getAvgBytes() {
		return avgBytes;
	}
	public void setAvgBytes(Double avgBytes) {
		this.avgBytes = avgBytes;
	}
	
	public void computeAdditionalDetail() {
		long taskCount = getTaskCount();
		
		setAvgBytes(taskCount == 0 ? 0.0 : getBytes() / taskCount);
		setAvgLatency(taskCount == 0 ? 0.0 : getLatency() / taskCount);
		
		long errorCount = getErrorCount();
		long totalTime = (getTickEndTime() - getTickStartTime()) / 1000;	// Seconds
		double successPercentage = (taskCount == 0) ? 0 : (((taskCount - errorCount) * 100.0) / taskCount);
		double avgTps = totalTime == 0 ? 0.0 : ((taskCount-errorCount) * 1.0) / ((float)(totalTime));
		
		setSuccessCount(taskCount - errorCount);
		setSuccessPercentage(successPercentage);
		setAvgTps(avgTps);
	}
}
