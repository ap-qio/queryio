package com.queryio.demo.mr.report.aos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class JTLParser {
	private static final Log LOG = LogFactory.getLog(JTLParser.class);
	private static final String OVERALL = "OVERALL";
	ArrayList<String> columns = null;

	private static final Set<String> skipLabels = new HashSet<String>();
	static {
		skipLabels.add("75-25 DIVISION");
		skipLabels.add("PRETESTSAMPLER_US");
		skipLabels.add("30-70 DIVISION");
		skipLabels.add("LOG_SINGLE_SELECTION");
		skipLabels.add("50-50 DIVISION");
		skipLabels.add("ENGRAVED_ITEM_SELECTION");
		skipLabels.add("NON_ENGRAVED_ITEM_SELECTION");
		skipLabels.add("90 DIVISION");
		skipLabels.add("US-CHECKOUT-SINGLE");
		skipLabels.add("US-DN");
		skipLabels.add("LOG_MULTI_SELECTION");
		skipLabels.add("80-20 DIVISION");
		skipLabels.add("IPAD+SMARTCOVER");
		skipLabels.add("IPAD+SMARTCOVER+APPLECARE");
		skipLabels.add("ENGRAVED");
		skipLabels.add("NON-ENGRAVED");
		skipLabels.add("MULTI CHECKOUT");
		skipLabels.add("US-MULTI-DN");
		skipLabels.add("LOGGING");
		skipLabels.add("BEANSHELL SAMPLER");
		skipLabels.add("90-10 DIVISION");
		skipLabels.add("PRETESTSAMPLER_UK");
		skipLabels.add("UK-DN");
		skipLabels.add("UK-MULTI-DN");
		skipLabels.add("UK-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_FR");
		skipLabels.add("FR-DN");
		skipLabels.add("FR-MULTI-DN");
		skipLabels.add("FR-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_SG");
		skipLabels.add("SG-DN");
		skipLabels.add("SG-MULTI-DN");
		skipLabels.add("SG-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_CN");
		skipLabels.add("CN-DN");
		skipLabels.add("CN-MULTI-DN");
		skipLabels.add("CN-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_AU");
		skipLabels.add("AU-DN");
		skipLabels.add("AU-MULTI-DN");
		skipLabels.add("AU-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_DE");
		skipLabels.add("DE-DN");
		skipLabels.add("DE-MULTI-DN");
		skipLabels.add("DE-CHECKOUT-SINGLE");
		skipLabels.add("PRETESTSAMPLER_CA");
		skipLabels.add("CA-DN");
		skipLabels.add("CA-MULTI-DN");
		skipLabels.add("CA-CHECKOUT-SINGLE");
		skipLabels.add("PRINT CARTID");
		skipLabels.add("UPDATE PARTID");
		skipLabels.add("LINE ITEM SEQUENCE EDITOR");
		skipLabels.add("SG-MULTI CHECKOUT");
		skipLabels.add("PRODUCT DISTRIBUTION");
		skipLabels.add("LINEITEM DIVISION");
		skipLabels.add("50-50 DIVISION");
		skipLabels.add("ENGRAVED_ITEM_SELECTION");
		skipLabels.add("NOT_ENGRAVED_ITEM_SELECTION");
		skipLabels.add("1-LINEITEM SAMPLER");
		skipLabels.add("2-LINEITEMS SAMPLER");
		skipLabels.add("3-LINEITEMS SAMPLER");
		skipLabels.add("4-LINEITEMS SAMPLER");
		skipLabels.add("UPDATE FOR 1 LINEITEM");
		skipLabels.add("THREE_LINEITEM_CHOICE DIVISION");
		skipLabels.add("FOUR_LINEITEM_CHOICE DIVISION");
		skipLabels.add("UPDATE LINEITEMID");
		skipLabels.add("90 DIVISION");
		skipLabels.add("PRINT DISA");
		skipLabels.add("PRINT CH");
		skipLabels.add("UPDATE HERO1LOCATIONTYPE");
		skipLabels.add("UPDATE HERO2LOCATIONTYPE");
		skipLabels.add("UPDATE PICKUP QUOTES");
		skipLabels.add("PRINT CARTID");
		skipLabels.add("RSP PERCENTAGE");
		skipLabels.add("PRINT REC DETAILS");
		skipLabels.add("OPTION SAMPLER");
		skipLabels.add("LOG");
		skipLabels.add("BEANSHELL PREPROCESSOR");
		skipLabels.add("BEANSHELL SAMPLER");
		skipLabels.add("DIVISION");
		skipLabels.add("PRE SAMPLER");
		skipLabels.add("CATEGORY-BEAN-SHELL-SAMPLER");
		skipLabels.add("BEAN-SHELL-SAMPLER");
		skipLabels.add("ADD-TO-CART-PARAM CONFIG");
		skipLabels.add("IPHONE SELECT PARAMS");
		skipLabels.add("IPOD ENGRAVED SAMPLER");
		skipLabels.add("IPOD NOT ENGRAVED SAMPLER");
		skipLabels.add("MAC REQUEST PARAMS CONFIG");
		skipLabels.add("MULTILINE PARAM CONFIG");
		skipLabels.add("MULTILINE BOPIS PARAM CONFIG");
		skipLabels.add("UPDATE PICKUP QUOTES HERO1");
		skipLabels.add("UPDATE PICKUP QUOTES HERO2");
		skipLabels.add("CHECKOUT REQUEST PARAMS");
		skipLabels.add("REQUEST PARAM CONFIG");
		skipLabels.add("IPAD ENGRAVED SAMPLER");
		skipLabels.add("IPAD NOT ENGRAVED SAMPLER");
		skipLabels.add("IPHONE TYPE");
	}

	private static ArrayList<String> dummyEntryLablesListEndsWith = new ArrayList<String>();
	private static ArrayList<String> dummyEntryLablesListStartsWith = new ArrayList<String>();
	private static ArrayList<String> dummyEntryLablesListContains = new ArrayList<String>();
	
	static {
		dummyEntryLablesListEndsWith.add("/CHECKOUT/START");
		dummyEntryLablesListEndsWith.add("/CART");
		dummyEntryLablesListEndsWith.add("MOBILECART");
		dummyEntryLablesListEndsWith.add("MOBILECHECKOUTSTART W/PLTN");
		dummyEntryLablesListEndsWith.add("MOBILE QUICKBUY CART");
		dummyEntryLablesListEndsWith.add("MOBILE QUICKBUY CHECKOUT");
		dummyEntryLablesListEndsWith.add("MOBILECHECKOUTTHANKYOU");

		dummyEntryLablesListStartsWith.add(OVERALL);
		dummyEntryLablesListStartsWith.add("ORDERSTATUS-");
		dummyEntryLablesListStartsWith.add("HOME-");
		dummyEntryLablesListStartsWith.add("PRODUCT-CATEGORY-");
		dummyEntryLablesListStartsWith.add("PRODUCT-ACCESSORIES-");
		dummyEntryLablesListStartsWith.add("INDIVIDUAL-PRODUCT-");
		dummyEntryLablesListStartsWith.add("INDIVIDUAL-ACCESSORIES-");

		dummyEntryLablesListContains.add("/BROWSE");
		dummyEntryLablesListContains.add("/PRODUCT");
	}

	LinkedHashMap<String, ArrayList<Double>> labelPercentile = null;
	LinkedHashMap<String, Double> labelLatencyAvg = null;
	LinkedHashMap<String, Double> labelLatencyMin = null;
	LinkedHashMap<String, Double> labelLatencyMax = null;
	LinkedHashMap<String, Integer> labelErrorCount = null;
	LinkedHashMap<String, Long> labelBytes = null;

	LinkedHashMap<String, ArrayList<Double>> labelPercentileTotal = null;
	LinkedHashMap<String, Double> labelLatencyAvgTotal = null;
	LinkedHashMap<String, Double> labelLatencyMinTotal = null;
	LinkedHashMap<String, Double> labelLatencyMaxTotal = null;
	LinkedHashMap<String, Integer> labelErrorCountTotal = null;
	LinkedHashMap<String, Long> labelBytesTotal = null;

	public static Record parseLine(final String line, JTLRecordMetaData metaData, Mapper<Object, Text, Text, Record>.Context context) {
		Record recordBean = null;
		final String[] values = line.split("\\s*,\\s*");
		boolean isValidated = validateRecord(values);
		if (isValidated == false) {
			LOG.warn("JTL record is invalid, split size : " + values.length);
			return recordBean;
		}

		recordBean = getRecordBean(values, metaData);
		recordBean = isRecordToBeIncluded(recordBean, context);
		return recordBean;
	}

	private static Record isRecordToBeIncluded(Record recordBean, Mapper<Object, Text, Text, Record>.Context context) {

		if (recordBean != null) {
			if (skipLabels.contains(recordBean.getLabel().toUpperCase())) {
				recordBean = null;
				context.getCounter(JOB_COUNTER.SKIPPED_LABELS).increment(1);
			} else if (recordBean.getBytes() == 0
					&& recordBean.getLatency() == 0.0) {
				recordBean = null;
				context.getCounter(JOB_COUNTER.ZERO_BYTES_AND_LATENCY).increment(1);
			}
		}
		return recordBean;
	}

	private static Record getRecordBean(final String[] values, JTLRecordMetaData metaData) {
		
		Record record = new Record();
		try {			
			
			final String label = values[metaData.getLabelIndex()];
			record.setLabel(label);
			
			final double latency = Double.valueOf(values[metaData.getLatencyIndex()]);
			record.setLatency(latency);
			
			final long currentTimestamp = Long.valueOf(values[metaData.getStartTimestampIndex()]);
			
			int sampleIndex = (int) ((currentTimestamp - metaData.getStartTime()) / (metaData.getSampleDuration() * 1000));
			record.setSampleValue(sampleIndex + 1);
			
			record.setTickStartTime(getStartTickTime(currentTimestamp, metaData, sampleIndex));
			record.setTickEndTime(getEndTickTime(currentTimestamp, metaData, sampleIndex));
			
			final long bytes = Long.valueOf(values[metaData.getBytesIndex()]);
			record.setBytes(bytes);
			
			final boolean success = Boolean.parseBoolean(values[metaData.getErrorCountIndex()]);
			record.setSuccess(success);
			
			final long errorCount = success ? 0 : 1;
			record.setErrorCount(errorCount);
			
			record.setTaskCount(1l);
			
		} catch(Exception e) {
			e.printStackTrace();
			record = null;
		}
		
		return record;
		
	}

	private static long getStartTickTime(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex) {
		return getTick(currentTimestamp, metaData, sampleIndex, true);
	}

	private static long getTick(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex, boolean startBucket) {
		
		int index = (int) (sampleIndex + (startBucket ? 0 : 1));
		if(index >= metaData.getTicks().size()) {
			throw new RuntimeException("Record's time does not fit into ticks. calculated index : " + index + ", total ticks: " + metaData.getTicks().size() + ", currentTimestamp : " + new Date(currentTimestamp));
		}
		long tickTime = metaData.getTicks().get(index);
//		System.out.println("Current time : " + new Date(currentTimestamp) + ", tickTime : " + new Date(tickTime) + ", startBucket : " + startBucket);
		return tickTime;
	}
	
	private static long getEndTickTime(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex) {
		long tickTime = getTick(currentTimestamp, metaData, sampleIndex, false);
		
		return tickTime > metaData.getEndTime() ? metaData.getEndTime() : tickTime;
	}

	private static boolean validateRecord(String[] values) {
		return values != null && values.length >= 7;	// TODO: Is that fine?
	}
	
	public boolean insertDummyRow(final String label) {
		
		for (final String dummyLabel : JTLParser.dummyEntryLablesListEndsWith) {
			if (label.endsWith(dummyLabel)) {
				return true;
			}
		}
		for (final String dummyLabel : JTLParser.dummyEntryLablesListStartsWith) {
			if (label.startsWith(dummyLabel)) {
				return true;
			}
		}
		for (final String dummyLabel : JTLParser.dummyEntryLablesListContains) {
			if (label.contains(dummyLabel)) {
				return true;
			}
		}
		
		return false;
	}
}