package com.queryio.demo.mr.report.aos;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class Record implements Writable {

	protected String label;
	protected Long tickStartTime;
	protected Long tickEndTime;
	protected Double latency;
	protected Long bytes;
	protected Long errorCount;
	protected Boolean success;
	protected int sampleValue;
	protected Long taskCount;

	public Record() {

	}

	public Record(String label, Long tickStartTime, Long tickEndTime, Double latency, Long bytes, Long errorCount,
			Boolean success, int sampleValue, Long taskCount) {

		this.label = label;
		this.tickStartTime = tickStartTime;
		this.tickEndTime = tickEndTime;
		this.latency = latency;
		this.bytes = bytes;
		this.errorCount = errorCount;
		this.success = success;
		this.sampleValue = sampleValue;
		this.taskCount = taskCount;
	}

	public Record(Record record) {

		this.label = record.label;
		this.tickStartTime = record.tickStartTime;
		this.tickEndTime = record.tickEndTime;
		this.latency = record.latency;
		this.bytes = record.bytes;
		this.errorCount = record.errorCount;
		this.success = record.success;
		this.sampleValue = record.sampleValue;
		this.taskCount = record.taskCount;
	}

	@Override
	public String toString() {
		return label + "," + tickStartTime + "," + tickEndTime + "," + sampleValue + "," + latency + "," + bytes + ","
				+ errorCount + "," + success + "," + taskCount;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getBytes() {
		return bytes;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	public Double getLatency() {
		return latency;
	}

	public void setLatency(Double latency) {
		this.latency = latency;
	}

	@Override
	public void write(DataOutput out) throws IOException {

		out.writeUTF(label);
		out.writeLong(tickStartTime);
		out.writeLong(tickEndTime);
		out.writeDouble(latency);
		out.writeLong(bytes);
		out.writeLong(errorCount);
		out.writeBoolean(success);
		out.writeInt(sampleValue);
		out.writeLong(taskCount);
	}

	@Override
	public void readFields(DataInput in) throws IOException {

		label = in.readUTF();
		tickStartTime = in.readLong();
		tickEndTime = in.readLong();
		latency = in.readDouble();
		bytes = in.readLong();
		errorCount = in.readLong();
		success = in.readBoolean();
		sampleValue = in.readInt();
		taskCount = in.readLong();
	}

	public long getTickStartTime() {
		return tickStartTime;
	}

	public void setTickStartTime(long tickStartTime) {
		this.tickStartTime = tickStartTime;
	}

	public long getTickEndTime() {
		return tickEndTime;
	}

	public void setTickEndTime(long tickEndTime) {
		this.tickEndTime = tickEndTime;
	}

	public int getSampleValue() {
		return sampleValue;
	}

	public void setSampleValue(int sampleValue) {
		this.sampleValue = sampleValue;
	}

	public Long getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Long taskCount) {
		this.taskCount = taskCount;
	}
}
