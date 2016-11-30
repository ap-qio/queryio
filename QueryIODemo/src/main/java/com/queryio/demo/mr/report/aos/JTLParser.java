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

	private static final Set<String> SKIP_LABELS = new HashSet<String>();
	static {
		SKIP_LABELS.add("75-25 DIVISION");
		SKIP_LABELS.add("PRETESTSAMPLER_US");
		SKIP_LABELS.add("30-70 DIVISION");
		SKIP_LABELS.add("LOG_SINGLE_SELECTION");
		SKIP_LABELS.add("50-50 DIVISION");
		SKIP_LABELS.add("ENGRAVED_ITEM_SELECTION");
		SKIP_LABELS.add("NON_ENGRAVED_ITEM_SELECTION");
		SKIP_LABELS.add("90 DIVISION");
		SKIP_LABELS.add("US-CHECKOUT-SINGLE");
		SKIP_LABELS.add("US-DN");
		SKIP_LABELS.add("LOG_MULTI_SELECTION");
		SKIP_LABELS.add("80-20 DIVISION");
		SKIP_LABELS.add("IPAD+SMARTCOVER");
		SKIP_LABELS.add("IPAD+SMARTCOVER+APPLECARE");
		SKIP_LABELS.add("ENGRAVED");
		SKIP_LABELS.add("NON-ENGRAVED");
		SKIP_LABELS.add("MULTI CHECKOUT");
		SKIP_LABELS.add("US-MULTI-DN");
		SKIP_LABELS.add("LOGGING");
		SKIP_LABELS.add("BEANSHELL SAMPLER");
		SKIP_LABELS.add("90-10 DIVISION");
		SKIP_LABELS.add("PRETESTSAMPLER_UK");
		SKIP_LABELS.add("UK-DN");
		SKIP_LABELS.add("UK-MULTI-DN");
		SKIP_LABELS.add("UK-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_FR");
		SKIP_LABELS.add("FR-DN");
		SKIP_LABELS.add("FR-MULTI-DN");
		SKIP_LABELS.add("FR-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_SG");
		SKIP_LABELS.add("SG-DN");
		SKIP_LABELS.add("SG-MULTI-DN");
		SKIP_LABELS.add("SG-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_CN");
		SKIP_LABELS.add("CN-DN");
		SKIP_LABELS.add("CN-MULTI-DN");
		SKIP_LABELS.add("CN-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_AU");
		SKIP_LABELS.add("AU-DN");
		SKIP_LABELS.add("AU-MULTI-DN");
		SKIP_LABELS.add("AU-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_DE");
		SKIP_LABELS.add("DE-DN");
		SKIP_LABELS.add("DE-MULTI-DN");
		SKIP_LABELS.add("DE-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRETESTSAMPLER_CA");
		SKIP_LABELS.add("CA-DN");
		SKIP_LABELS.add("CA-MULTI-DN");
		SKIP_LABELS.add("CA-CHECKOUT-SINGLE");
		SKIP_LABELS.add("PRINT CARTID");
		SKIP_LABELS.add("UPDATE PARTID");
		SKIP_LABELS.add("LINE ITEM SEQUENCE EDITOR");
		SKIP_LABELS.add("SG-MULTI CHECKOUT");
		SKIP_LABELS.add("PRODUCT DISTRIBUTION");
		SKIP_LABELS.add("LINEITEM DIVISION");
		SKIP_LABELS.add("50-50 DIVISION");
		SKIP_LABELS.add("ENGRAVED_ITEM_SELECTION");
		SKIP_LABELS.add("NOT_ENGRAVED_ITEM_SELECTION");
		SKIP_LABELS.add("1-LINEITEM SAMPLER");
		SKIP_LABELS.add("2-LINEITEMS SAMPLER");
		SKIP_LABELS.add("3-LINEITEMS SAMPLER");
		SKIP_LABELS.add("4-LINEITEMS SAMPLER");
		SKIP_LABELS.add("UPDATE FOR 1 LINEITEM");
		SKIP_LABELS.add("THREE_LINEITEM_CHOICE DIVISION");
		SKIP_LABELS.add("FOUR_LINEITEM_CHOICE DIVISION");
		SKIP_LABELS.add("UPDATE LINEITEMID");
		SKIP_LABELS.add("90 DIVISION");
		SKIP_LABELS.add("PRINT DISA");
		SKIP_LABELS.add("PRINT CH");
		SKIP_LABELS.add("UPDATE HERO1LOCATIONTYPE");
		SKIP_LABELS.add("UPDATE HERO2LOCATIONTYPE");
		SKIP_LABELS.add("UPDATE PICKUP QUOTES");
		SKIP_LABELS.add("PRINT CARTID");
		SKIP_LABELS.add("RSP PERCENTAGE");
		SKIP_LABELS.add("PRINT REC DETAILS");
		SKIP_LABELS.add("OPTION SAMPLER");
		SKIP_LABELS.add("LOG");
		SKIP_LABELS.add("BEANSHELL PREPROCESSOR");
		SKIP_LABELS.add("BEANSHELL SAMPLER");
		SKIP_LABELS.add("DIVISION");
		SKIP_LABELS.add("PRE SAMPLER");
		SKIP_LABELS.add("CATEGORY-BEAN-SHELL-SAMPLER");
		SKIP_LABELS.add("BEAN-SHELL-SAMPLER");
		SKIP_LABELS.add("ADD-TO-CART-PARAM CONFIG");
		SKIP_LABELS.add("IPHONE SELECT PARAMS");
		SKIP_LABELS.add("IPOD ENGRAVED SAMPLER");
		SKIP_LABELS.add("IPOD NOT ENGRAVED SAMPLER");
		SKIP_LABELS.add("MAC REQUEST PARAMS CONFIG");
		SKIP_LABELS.add("MULTILINE PARAM CONFIG");
		SKIP_LABELS.add("MULTILINE BOPIS PARAM CONFIG");
		SKIP_LABELS.add("UPDATE PICKUP QUOTES HERO1");
		SKIP_LABELS.add("UPDATE PICKUP QUOTES HERO2");
		SKIP_LABELS.add("CHECKOUT REQUEST PARAMS");
		SKIP_LABELS.add("REQUEST PARAM CONFIG");
		SKIP_LABELS.add("IPAD ENGRAVED SAMPLER");
		SKIP_LABELS.add("IPAD NOT ENGRAVED SAMPLER");
		SKIP_LABELS.add("IPHONE TYPE");
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

	public static Record parseLine(final String line, JTLRecordMetaData metaData,
			Mapper<Object, Text, Text, Record>.Context context) {
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
			if (SKIP_LABELS.contains(recordBean.getLabel().toUpperCase())) {
				recordBean = null;
				context.getCounter(JOB_COUNTER.SKIPPED_LABELS).increment(1);
			} else if (recordBean.getBytes() == 0 && recordBean.getLatency() == 0.0) {
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

			int sampleIndex = (int) ((currentTimestamp - metaData.getStartTime())
					/ (metaData.getSampleDuration() * 1000));
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

		} catch (Exception e) {
			e.printStackTrace();
			record = null;
		}

		return record;

	}

	private static long getStartTickTime(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex) {
		return getTick(currentTimestamp, metaData, sampleIndex, true);
	}

	private static long getTick(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex,
			boolean startBucket) {

		int index = (int) (sampleIndex + (startBucket ? 0 : 1));
		if (index >= metaData.getTicks().size()) {
			throw new RuntimeException(
					"Record's time does not fit into ticks. calculated index : " + index + ", total ticks: "
							+ metaData.getTicks().size() + ", currentTimestamp : " + new Date(currentTimestamp));
		}
		long tickTime = metaData.getTicks().get(index);
		// System.out.println("Current time : " + new Date(currentTimestamp) +
		// ", tickTime : " + new Date(tickTime) + ", startBucket : " +
		// startBucket);
		return tickTime;
	}

	private static long getEndTickTime(long currentTimestamp, JTLRecordMetaData metaData, long sampleIndex) {
		long tickTime = getTick(currentTimestamp, metaData, sampleIndex, false);

		return tickTime > metaData.getEndTime() ? metaData.getEndTime() : tickTime;
	}

	private static boolean validateRecord(String[] values) {
		return values != null && values.length >= 7; // TODO: Is that fine?
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