package com.queryio.demo.mr.report.aos;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class JTLReducer extends Reducer<Text, Record, NullWritable, ConsolidatedRecord> {

	@Override
	protected void reduce(Text key, Iterable<Record> values,
			Reducer<Text, Record, NullWritable, ConsolidatedRecord>.Context context)
			throws IOException, InterruptedException {

		long reduceStartTime = System.currentTimeMillis();

		ConsolidatedRecord consolidatedRecord = null;
		for (Record record : values) {
			if (consolidatedRecord == null) {
				consolidatedRecord = new ConsolidatedRecord(record);
				continue;
			}
			consolidateRecord(consolidatedRecord, record);
		}

		consolidatedRecord.computeAdditionalDetail();
		if (consolidatedRecord != null) {
			consolidatedRecord.setSuccess(consolidatedRecord.getErrorCount() == 0 ? true : false);
			context.write(NullWritable.get(), consolidatedRecord);
		}
		context.getCounter(JOB_COUNTER.REDUCE_TIME).increment(System.currentTimeMillis() - reduceStartTime);
	}

	private void consolidateRecord(ConsolidatedRecord consolidatedRecord, Record record) {

		consolidatedRecord.setMinLatency(Math.min(consolidatedRecord.getMinLatency(), record.getLatency()));
		consolidatedRecord.setMaxLatency(Math.max(consolidatedRecord.getMaxLatency(), record.getLatency()));
		consolidatedRecord.setMinBytes(Math.min(consolidatedRecord.getMinBytes(), record.getBytes()));
		consolidatedRecord.setMaxBytes(Math.max(consolidatedRecord.getMaxBytes(), record.getBytes()));

		consolidatedRecord.setBytes(consolidatedRecord.getBytes() + record.getBytes());
		consolidatedRecord.setErrorCount(consolidatedRecord.getErrorCount() + record.getErrorCount());
		consolidatedRecord.setLatency(consolidatedRecord.getLatency() + record.getLatency());

		consolidatedRecord.setTaskCount(consolidatedRecord.getTaskCount() + record.getTaskCount());
	}
}
