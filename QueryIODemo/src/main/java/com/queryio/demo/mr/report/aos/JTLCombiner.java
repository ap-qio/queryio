package com.queryio.demo.mr.report.aos;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class JTLCombiner extends Reducer<Text, Record, Text, Record> {
	
	@Override
	protected void reduce(Text key, Iterable<Record> values,
			Reducer<Text, Record, Text, Record>.Context context)
			throws IOException, InterruptedException {
		
		Record combinedRecord = null;
		long count = 0;
		for(Record record : values) {
			count ++;
			if(combinedRecord == null) {
				combinedRecord = new Record(record);
				continue;
			}
			
			combinedRecord.setBytes(combinedRecord.getBytes() + record.getBytes());
			combinedRecord.setErrorCount(combinedRecord.getErrorCount() + record.getErrorCount());
			combinedRecord.setLatency(combinedRecord.getLatency() + record.getLatency());
		}
		combinedRecord.setTaskCount(count);
		
		if(combinedRecord != null) {
			combinedRecord.setSuccess(combinedRecord.getErrorCount() == 0 ? true : false);
			context.write(key, combinedRecord);
		}
	}
}
