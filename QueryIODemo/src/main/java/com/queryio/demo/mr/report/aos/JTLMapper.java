package com.queryio.demo.mr.report.aos;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class JTLMapper extends Mapper<Object, Text, Text, Record>{
	private static final String OVERALL = "OVERALL";

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(JTLMapper.class);
	
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

	private JTLRecordMetaData metaData;
	
	@Override
	protected void map(Object key, Text value,
			Mapper<Object, Text, Text, Record>.Context context)
			throws IOException, InterruptedException {
		long reduceStartTime = System.currentTimeMillis();
		Record record = JTLParser.parseLine(value.toString(), metaData, context);
		if(record == null) {
			return;
		}
		
		writeRecordWithOverallAndTick(context, record);
		
		writeRecordWithLabel(context, record);
		
		writeRecordWithLabelAndTick(context, record);
		
		context.getCounter(JOB_COUNTER.MAP_TIME).increment(System.currentTimeMillis() - reduceStartTime);
	}

	private void writeRecordWithLabel(Mapper<Object, Text, Text, Record>.Context context,
			Record record) throws IOException, InterruptedException {
		
		Record completeTimeframeRecord = new Record(record);
		
		completeTimeframeRecord.setTickStartTime(metaData.getTicks().get(0));
		completeTimeframeRecord.setTickEndTime(metaData.getTicks().get(metaData.getTicks().size() - 1));
		completeTimeframeRecord.setSampleValue(0);
		
		Text labelBucketIdcompleteTimeframe = new Text(record.getLabel());
		context.write(labelBucketIdcompleteTimeframe, completeTimeframeRecord);
	}

	private void writeRecordWithOverallAndTick(Mapper<Object, Text, Text, Record>.Context context,
			Record record) throws IOException, InterruptedException {
		
		Record overAllRecord = new Record(record);
		
		overAllRecord.setLabel(OVERALL);
		
		Text labelBucketIdOverall = new Text(OVERALL + "_" + overAllRecord.getTickStartTime());
		context.write(labelBucketIdOverall, overAllRecord);
	}

	private void writeRecordWithLabelAndTick(Mapper<Object, Text, Text, Record>.Context context,
			Record record) throws IOException, InterruptedException {
		
		Text labelBucketId = new Text(record.getLabel() + "_" + record.getTickStartTime());
		context.write(labelBucketId, record);
	}

	@Override
	protected void setup(Mapper<Object, Text, Text, Record>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		try {
			metaData = getMetaData(context);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private JTLRecordMetaData getMetaData(Mapper<Object, Text, Text, Record>.Context context) throws ParseException {
		
		JTLRecordMetaData recordMetaData = new JTLRecordMetaData();
		
		int sampleDuration = Integer.valueOf(context.getConfiguration().get("sampleDuration"));
		recordMetaData.setSampleDuration(sampleDuration);
		
		int startTimestampIndex = Integer.valueOf(context.getConfiguration().get("startTimestampIndex"));
		recordMetaData.setStartTimestampIndex(startTimestampIndex);
		
		int labelIndex = Integer.valueOf(context.getConfiguration().get("labelIndex"));
		recordMetaData.setLabelIndex(labelIndex);
		
		int errorCountIndex = Integer.valueOf(context.getConfiguration().get("errorCountIndex"));
		recordMetaData.setErrorCountIndex(errorCountIndex);
		
		int bytesIndex = Integer.valueOf(context.getConfiguration().get("bytesIndex"));
		recordMetaData.setBytesIndex(bytesIndex);
		
		int latencyIndex = Integer.valueOf(context.getConfiguration().get("latencyIndex"));
		recordMetaData.setLatencyIndex(latencyIndex);
		
		String startTime = context.getConfiguration().get("startTime");
		recordMetaData.setStartTime(formatter.parse(startTime).getTime());
		
		String endTime = context.getConfiguration().get("endTime");
		recordMetaData.setEndTime(formatter.parse(endTime).getTime());
		
		int waitTime = Integer.valueOf(context.getConfiguration().get("waitTime"));
		recordMetaData.setWaitTime(waitTime);
		
		recordMetaData.setTestDuration(recordMetaData.getEndTime() - recordMetaData.getStartTime());
		
		List<Long> ticks = new ArrayList<Long>();
		long tick = recordMetaData.getStartTime();
		while(tick <= recordMetaData.getEndTime()) {
			ticks.add(tick);
			tick += (recordMetaData.getSampleDuration() * 1000);
		}
		ticks.add(recordMetaData.getEndTime());
		recordMetaData.setTicks(ticks);
		
		return recordMetaData; 
	}
}