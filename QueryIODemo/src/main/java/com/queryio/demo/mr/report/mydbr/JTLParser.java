package com.queryio.demo.mr.report.mydbr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;

public class JTLParser {
	private static final Log LOG = LogFactory.getLog(JTLParser.class);
	private static final String COL_TAG_VALUES_FILEPATH = "FILEPATH";
	private static final String OVERALL = "OVERALL";
	private static final String ORDER_STATUS_LABEL = "/CHECKOUTX/STATUS";
	private static final String ORDER_STATUS_LABEL_NEW = "Order Processing";
	private static final String ORDER_PLACEMENT_LABEL = "/CHECKOUTX - PAYMENTSERVICE";
	private static final String ORDER_PLACEMENT_LABEL_NEW_SUFFIX = "Place Order";
	
	ArrayList<String> columns = null;
	
	private int sampleDuration = 60; // in seconds, from User
	private int sampleCount = 0;
	private Map<String, Long> orderPlacementCallsMap = null;
	private Map<String, Long> orderStatusCallsMap = null;
	private Map<String, Long> orderPlacementTotalCallsMap = null;
	private Map<String, Long> orderStatusTotalCallsMap = null;

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
	
	// timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,bytes,grpThreads,allThreads,Latency,Hostname
	private int startTimestampIndex = 0;			// index of timestamp field, starting from 0, from User
	private int labelIndex = 2;						// index of label field, starting from 0, from User
	private int errorCountIndex = 7;					// index of success field, starting from 0, from User
	private int bytesIndex = 8;					// index of bytes field, starting from 0, from User
	private int latencyIndex = 11;					// index of latency field, starting from 0, from User
	private String startTime = null;
	private String endTime = null;
	private int waitTime = 500;					// think time in ms, for each Order Status call

	private long startTimestamp = -1;
	private long testStartTimestamp = -1;
	private long endTimestamp = 0;
	private long currentTimestamp = 0;
	private long startTimestampTotal = -1;
	
	private final boolean isHeaderRow = true;
	
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

	Set<String> insertDummyRowSet = null;
	
	Connection connection = null;
	PreparedStatement pst = null;
	String tableName = null;
	int maxBatchSize; 
	FileStatus fileStatus = null;
	String filePath = null;
	
	int currentBatchSize = 0;
	
	public JTLParser(final Connection connection, final String tableName, final FileStatus fileStatus, final int maxBatchSize, final int sampleDuration, final int startTimestampIndex,
			final int labelIndex, final int errorCountIndex, final int bytesIndex, final int latencyIndex, final String startTime, final String endTime, final int waitTime, final ArrayList<String> columns) {
		this.connection = connection;
		this.fileStatus = fileStatus;
		this.filePath = this.fileStatus.getPath().toUri().getPath();
		this.tableName = tableName;
		this.maxBatchSize = maxBatchSize;
		this.sampleDuration = sampleDuration;
		this.startTimestampIndex = startTimestampIndex;
		this.labelIndex = labelIndex;
		this.errorCountIndex = errorCountIndex;
		this.bytesIndex = bytesIndex;
		this.latencyIndex = latencyIndex;
		this.startTime = startTime;
		this.endTime = endTime;
		this.waitTime = waitTime;
		this.columns = columns;
	}
	
	public void parse(final InputStream is) throws IOException, InterruptedException, SQLException {

		BufferedReader rd = null;
		String str;
		try {
			rd = new BufferedReader(new InputStreamReader(is));

			if (isHeaderRow) {
				rd.readLine(); // skip header entry
			}

			while ((str = rd.readLine()) != null) {
				this.parseLine(str);
			}

			if (labelPercentile != null) {
				this.endTimestamp = this.currentTimestamp;
				insertEntry(true);
			}

			if (currentBatchSize > 0) {
				pst.executeBatch();
			}
		} catch (final Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (final SQLException e) {
					LOG.fatal("Error Closing PreparedStatement", e);
				}
			}
		}
	}

	public void parseLine(final String line) throws Exception {
		final String[] values = line.split("\\s*,\\s*");
		String label = null;
		long bytes = 0;

		init();
		
		// Label
		String labelUpperCase = null;
		if (values.length > this.labelIndex) {
			label = values[this.labelIndex];
			if (label != null) {
				labelUpperCase = label.toUpperCase();
				if (SKIP_LABELS.contains(labelUpperCase)) {
					return;
				}
			}
		}
		
		double latency = 0.0;		
		// Latency
		if (values.length > this.latencyIndex) {
			try {
				latency = Double.valueOf(values[this.latencyIndex]);
			} catch (final Exception e) {
				LOG.warn("Error parsing latency value: " + e.getLocalizedMessage(), e);
			}
//No more in use.
//			if (latency == 0.0) {
//				// Skip rows with zero latency
//				return;
//			}
		}

		// Timestamp
		if (values.length > this.startTimestampIndex) {
			try {
				currentTimestamp = Long.valueOf(values[this.startTimestampIndex]);
			} catch (final NumberFormatException e) {
				LOG.warn("Error parsing timestamp value: " + e.getLocalizedMessage(), e);
				return;
			}
			if (startTimestamp == -1) {
				testStartTimestamp = startTimestamp = getStartTimestamp();
				endTimestamp = startTimestamp + (sampleDuration * 1000);
				startTimestampTotal = currentTimestamp;
			}
		}

		

		// Bytes
		if (values.length > this.bytesIndex) {
			try {
				bytes = Long.valueOf(values[this.bytesIndex]);
			} catch (final Exception e) {
				LOG.warn("Error parsing bytes value: " + e.getLocalizedMessage(), e);
				return;
			}

			Long bytesStored = labelBytes.get(label);
			labelBytes.put(label, (bytesStored == null ? bytes : (bytesStored + bytes)));

			bytesStored = labelBytes.get(OVERALL);
			labelBytes.put(OVERALL, (bytesStored == null ? bytes : (bytesStored + bytes)));
		}

		// Error
		if (values.length > this.errorCountIndex) {
			final boolean success = Boolean.parseBoolean(values[this.errorCountIndex]);
			Integer errors = labelErrorCount.get(label);
			if (errors == null) {
				errors = 0;
			}
			if (!success) {
				errors++;
			}
			labelErrorCount.put(label, errors);

			errors = labelErrorCount.get(OVERALL);
			if (errors == null) {
				errors = 0;
			}
			if (!success) {
				errors++;
			}
			labelErrorCount.put(OVERALL, errors);
		}

		if (labelUpperCase != null) {
			// Changes for Order Processing Step.
			if (labelUpperCase.endsWith(ORDER_PLACEMENT_LABEL)) {
				incrementCalls(orderPlacementCallsMap, orderPlacementTotalCallsMap, labelUpperCase);
			} else if (labelUpperCase.endsWith(ORDER_STATUS_LABEL)) {
				incrementCalls(orderStatusCallsMap, orderStatusTotalCallsMap, labelUpperCase);
			}
		}

		// Latency
		ArrayList<Double> percentileLatency = labelPercentile.get(label);
		if (percentileLatency == null) {
			percentileLatency = new ArrayList<Double>();
			labelPercentile.put(label, percentileLatency);
		}
		percentileLatency.add(latency);

		percentileLatency = labelPercentile.get(OVERALL);
		if (percentileLatency == null) {
			percentileLatency = new ArrayList<Double>();
			labelPercentile.put(OVERALL, percentileLatency);
		}
		percentileLatency.add(latency);

		percentileLatency = labelPercentileTotal.get(label);
		if (percentileLatency == null) {
			percentileLatency = new ArrayList<Double>();
			labelPercentileTotal.put(label, percentileLatency);
		}
		percentileLatency.add(latency);

		percentileLatency = labelPercentileTotal.get(OVERALL);
		if (percentileLatency == null) {
			percentileLatency = new ArrayList<Double>();
			labelPercentileTotal.put(OVERALL, percentileLatency);
		}
		percentileLatency.add(latency);

		Double latencyAvg = labelLatencyAvg.get(label);
		labelLatencyAvg.put(label, (latencyAvg == null ? latency : (latencyAvg + latency)));

		latencyAvg = labelLatencyAvg.get(OVERALL);
		labelLatencyAvg.put(OVERALL, (latencyAvg == null ? latency : (latencyAvg + latency)));

		latencyAvg = labelLatencyMin.get(label);
		if (latencyAvg == null) {
			latencyAvg = Double.MAX_VALUE;
		}
		labelLatencyMin.put(label, Math.min(latency, latencyAvg));

		latencyAvg = labelLatencyMin.get(OVERALL);
		if (latencyAvg == null) {
			latencyAvg = Double.MAX_VALUE;
		}
		labelLatencyMin.put(OVERALL, Math.min(latency, latencyAvg));

		latencyAvg = labelLatencyMax.get(label);
		if (latencyAvg == null) {
			latencyAvg = Double.MIN_VALUE;
		}
		labelLatencyMax.put(label, Math.max(latency, latencyAvg));

		latencyAvg = labelLatencyMax.get(OVERALL);
		if (latencyAvg == null) {
			latencyAvg = Double.MIN_VALUE;
		}
		labelLatencyMax.put(OVERALL, Math.max(latency, latencyAvg));

		if (currentTimestamp >= endTimestamp) {
			insertEntry(false);
			startTimestamp = endTimestamp;
			endTimestamp = startTimestamp + (sampleDuration * 1000);
		}
	}

	private void incrementCalls(Map<String, Long> callsMap, Map<String, Long> totalCallsMap, final String label) {
		Long calls = callsMap.get(label);
		callsMap.put(label, calls == null ? 1L : ++calls);

		calls = totalCallsMap.get(label);
		totalCallsMap.put(label, calls == null ? 1L : ++calls);
	}

	private long getStartTimestamp() throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.currentTimestamp);
		LOG.info("StartTimestamp(JTL): " + calendar.getTime());

		final Calendar startTimeCal = Calendar.getInstance();
		final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		final Date startDate = formatter.parse(this.startTime);
		startTimeCal.setTime(startDate);
		LOG.info("StartTimestamp(Arguments): " + startTimeCal.getTime());

		// Calculate Start Time
		if (calendar.get(Calendar.HOUR_OF_DAY) < startTimeCal.get(Calendar.HOUR_OF_DAY)) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, startTimeCal.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, startTimeCal.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, startTimeCal.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, 0);

		LOG.info("StartTimestamp(Final): " + calendar.getTime());

		return calendar.getTimeInMillis();
	}

	private void init() {
		if (labelPercentile == null) {
			labelPercentile = new LinkedHashMap<String, ArrayList<Double>>();
			labelLatencyAvg = new LinkedHashMap<String, Double>();
			labelLatencyMin = new LinkedHashMap<String, Double>();
			labelLatencyMax = new LinkedHashMap<String, Double>();
			labelErrorCount = new LinkedHashMap<String, Integer>();
			labelBytes = new LinkedHashMap<String, Long>();
			orderPlacementCallsMap = new HashMap<String, Long>();
			orderStatusCallsMap = new HashMap<String, Long>();
		}

		if (labelPercentileTotal == null) {
			labelPercentileTotal = new LinkedHashMap<String, ArrayList<Double>>();
			labelLatencyAvgTotal = new LinkedHashMap<String, Double>();
			labelLatencyMinTotal = new LinkedHashMap<String, Double>();
			labelLatencyMaxTotal = new LinkedHashMap<String, Double>();
			labelErrorCountTotal = new LinkedHashMap<String, Integer>();
			labelBytesTotal = new LinkedHashMap<String, Long>();
			orderPlacementTotalCallsMap = new HashMap<String, Long>();
			orderStatusTotalCallsMap = new HashMap<String, Long>();
			insertDummyRowSet = new HashSet<String>();
		}
	}

	private void prepareStatement() throws SQLException {
		final StringBuffer query = new StringBuffer();
		final StringBuffer valueBuf = new StringBuffer();

		query.append("INSERT INTO ");
		query.append(tableName).append(" (");

		for (final String columnName : columns) {
			query.append(columnName).append(",");
			valueBuf.append("?").append(",");
		}

		query.append(COL_TAG_VALUES_FILEPATH).append(") VALUES (").append(valueBuf.toString());
		query.append("?)");

		pst = connection.prepareStatement(query.toString());
	}


	private void prepareBatch(final String label, final int sampleValue, final long startTime, final long endTime, int taskCount, double minLatency, double maxLatency, double avgLatency,
			double percentileLatency999, double percentileLatency99, double percentileLatency95, double percentileLatency90, double percentileLatency80, final double totalBytes, int errorCount,
			double statusCallsPerOrder, double waitTime) throws SQLException {

		if((endTime-startTime)<1){
			return;
		}
		
		errorCount = (int) ((statusCallsPerOrder == -1) ? errorCount : (errorCount / statusCallsPerOrder));

		minLatency = (statusCallsPerOrder == -1) ? minLatency : (minLatency + waitTime);
		maxLatency = (statusCallsPerOrder == -1) ? maxLatency : (maxLatency + waitTime);
		avgLatency = (statusCallsPerOrder == -1) ? avgLatency : (avgLatency + waitTime);

		percentileLatency999 = (statusCallsPerOrder == -1) ? percentileLatency999 : (percentileLatency999 + waitTime);
		percentileLatency99 = (statusCallsPerOrder == -1) ? percentileLatency99 : (percentileLatency99 + waitTime);
		percentileLatency95 = (statusCallsPerOrder == -1) ? percentileLatency95 : (percentileLatency95 + waitTime);
		percentileLatency90 = (statusCallsPerOrder == -1) ? percentileLatency90 : (percentileLatency90 + waitTime);
		percentileLatency80 = (statusCallsPerOrder == -1) ? percentileLatency80 : (percentileLatency80 + waitTime);

		double successPer = (taskCount == 0) ? 0 : (((taskCount - errorCount) * 100.0) / taskCount);
		double avgTps = ((taskCount-errorCount) * 1.0) / ((float)(endTime - startTime) / 1000);

		System.out.println("Label: " + label + " Sample Count: " + sampleValue + " Total Count: " + taskCount + " Error Count: " + errorCount + " Success Count: " + (taskCount - errorCount)
				+ " SuccessPer: " + successPer);

		int index = 0;
		pst.setString(++index, label);// sample_label
		pst.setInt(++index, sampleValue);// sample_value
		pst.setTimestamp(++index, new Timestamp(startTime));// start_timestamp
		pst.setTimestamp(++index, new Timestamp(endTime));// end_timestamp
		pst.setInt(++index, taskCount);// task_count
		pst.setDouble(++index, minLatency);// min_latency
		pst.setDouble(++index, maxLatency);// max_latency
		pst.setDouble(++index, avgLatency);// avg_latency
		pst.setDouble(++index, avgTps);// avg_tps
		pst.setDouble(++index, percentileLatency999);// tp_999_latency
		pst.setDouble(++index, percentileLatency99);// tp_99_latency
		pst.setDouble(++index, percentileLatency95);// tp_95_latency
		pst.setDouble(++index, percentileLatency90);// tp_90_latency
		pst.setDouble(++index, percentileLatency80);// tp_80_latency
		pst.setDouble(++index, totalBytes);// total_bytes
		pst.setInt(++index, errorCount);// errors_count
		pst.setInt(++index, (taskCount - errorCount));// success_count
		pst.setDouble(++index, successPer);// success_percentage
		pst.setString(++index, this.filePath);
		pst.addBatch();

		if (++currentBatchSize % maxBatchSize == 0) {
			pst.executeBatch();
			pst.clearBatch();
			currentBatchSize = 0;
		}
	}

	private void insertEntry(final boolean isInsertTotal) throws Exception {
		if (pst == null) {
			prepareStatement();
		}

		if (this.labelPercentile != null) {
			this.sampleCount ++;
			insertSampleEntries();
		}

		if (isInsertTotal && (this.labelPercentileTotal != null)) {
			insertOverallEntries();
		}
	}

	private boolean isOrderStatus(String label) {
		return label.endsWith(ORDER_STATUS_LABEL);
	}

	private void insertOverallEntries() throws SQLException {
		String label = null;
		ArrayList<Double> percentileLatency = null;

		final Iterator<String> it = this.labelPercentileTotal.keySet().iterator();
		while (it.hasNext()) {
			label = String.valueOf(it.next());
			percentileLatency = this.labelPercentileTotal.get(label);

			if ((percentileLatency != null) && (percentileLatency.size() > 0)) {
				int latencyCount = percentileLatency.size();
				Collections.sort(percentileLatency);

				final int index999 = (int) ((99.9 / 100) * latencyCount);
				final int index99 = (int) ((99.0 / 100) * latencyCount);
				final int index95 = (int) ((95.0 / 100) * latencyCount);
				final int index90 = (int) ((90.0 / 100) * latencyCount);
				final int index80 = (int) ((80.0 / 100) * latencyCount);

				double latencyAvg = this.labelLatencyAvgTotal.get(label);
				latencyAvg = latencyAvg / latencyCount;

				String labelUpperCase = label.toUpperCase();
				double statusCallsPerOrder = -1;
				double waitTime = -1;
				String orderStatusLabel = null;
				Long orderStatusCalls = -1L;
				Long orderPlacementCalls = -1L;
				if (isOrderStatus(labelUpperCase)) {
					orderStatusCalls = orderStatusTotalCallsMap.get(labelUpperCase);
					final String region = labelUpperCase.substring(labelUpperCase.indexOf('/') + 1, labelUpperCase.indexOf(ORDER_STATUS_LABEL));
					final String orderLabel = "/" + region + ORDER_PLACEMENT_LABEL;
					orderPlacementCalls = orderPlacementTotalCallsMap.get(orderLabel);

					if (orderPlacementCalls == null || orderStatusCalls == null) {
						LOG.warn("Either orderPlacement or orderStatus calls are null. Labels: " + label + ", " + label + ", orderPlacementCallsMap: " + orderPlacementTotalCallsMap
								+ " orderStatusCallsMap: " + orderStatusTotalCallsMap);
						orderPlacementCalls = 1L;
						orderStatusCalls = 1L;
					}

					statusCallsPerOrder = (orderStatusCalls - orderPlacementCalls) / orderPlacementCalls;
					waitTime = this.waitTime * statusCallsPerOrder;
					orderStatusLabel = ORDER_STATUS_LABEL_NEW + "-" + region.toLowerCase();
					latencyCount = orderPlacementCalls.intValue();
					if (LOG.isDebugEnabled()) {
						LOG.debug("Label: " + label + " statusLabel: " + label + " orderStatusCalls: " + orderStatusCalls + " orderPlacementCalls: " + orderPlacementCalls + " statusCallsPerOrder: "
								+ statusCallsPerOrder + " waitTime: " + waitTime);
					}
				}

				prepareBatch(((statusCallsPerOrder == -1) ? getLabel(label) : orderStatusLabel), 0, this.startTimestampTotal, this.currentTimestamp, latencyCount,
						this.labelLatencyMinTotal.get(label), this.labelLatencyMaxTotal.get(label), latencyAvg, percentileLatency.get(index999), percentileLatency.get(index99),
						percentileLatency.get(index95), percentileLatency.get(index90), percentileLatency.get(index80), (this.labelBytesTotal.get(label) * 1.0), this.labelErrorCountTotal.get(label),
						statusCallsPerOrder, waitTime);
			}
		}

		this.labelPercentileTotal = null;
		this.labelLatencyAvgTotal = null;
		this.labelLatencyMinTotal = null;
		this.labelLatencyMaxTotal = null;
		this.labelErrorCountTotal = null;
		this.labelBytesTotal = null;
		this.orderStatusTotalCallsMap = null;
		this.orderPlacementTotalCallsMap = null;
		this.insertDummyRowSet = null;
	}

	private void insertSampleEntries() throws SQLException {
		String label;
		ArrayList<Double> percentileLatency = null;

		final Iterator<String> it = this.labelPercentile.keySet().iterator();
		while (it.hasNext()) {
			label = String.valueOf(it.next());
			percentileLatency = this.labelPercentile.get(label);

			if ((percentileLatency != null) && (percentileLatency.size() > 0)) {
				int latencyCount = percentileLatency.size();

				Collections.sort(percentileLatency);

				final int index999 = (int) ((99.9 / 100) * latencyCount);
				final int index99 = (int) ((99.0 / 100) * latencyCount);
				final int index95 = (int) ((95.0 / 100) * latencyCount);
				final int index90 = (int) ((90.0 / 100) * latencyCount);
				final int index80 = (int) ((80.0 / 100) * latencyCount);

				final Double latencyTotal = this.labelLatencyAvg.get(label);
				final Double latencyAvg = latencyTotal / latencyCount;
				final Integer errorCount = this.labelErrorCount.get(label);
				final Long bytes = this.labelBytes.get(label);
				double statusCallsPerOrder = -1;
				double waitTime = -1;

				String labelUpperCase = label.toUpperCase();
				String orderStatusLabel = null;
				Long orderStatusCalls = -1L;
				Long orderPlacementCalls = -1L;
				if (isOrderStatus(labelUpperCase)) {
					orderStatusCalls = orderStatusCallsMap.get(labelUpperCase);
					final String region = labelUpperCase.substring(labelUpperCase.indexOf('/') + 1, labelUpperCase.indexOf(ORDER_STATUS_LABEL));
					final String orderLabel = "/" + region + ORDER_PLACEMENT_LABEL;
					orderPlacementCalls = orderPlacementCallsMap.get(orderLabel);

					if (orderPlacementCalls == null || orderStatusCalls == null) {
						LOG.warn("Either orderPlacement or orderStatus calls are null. Labels: " + label + ", " + label + ", orderPlacementCallsMap: " + orderPlacementCallsMap
								+ " orderStatusCallsMap: " + orderStatusCallsMap);
						orderPlacementCalls = 1L;
						orderStatusCalls = 1L;
					}

					statusCallsPerOrder = (orderStatusCalls - orderPlacementCalls) / orderPlacementCalls;
					waitTime = this.waitTime * statusCallsPerOrder;
					orderStatusLabel = ORDER_STATUS_LABEL_NEW + "-" + region.toLowerCase();
					latencyCount = orderPlacementCalls.intValue();
					if (LOG.isDebugEnabled()) {
						LOG.debug("Label: " + label + " statusLabel: " + label + " orderStatusCalls: " + orderStatusCalls + " orderPlacementCalls: " + orderPlacementCalls + " statusCallsPerOrder: "
								+ statusCallsPerOrder + " waitTime: " + waitTime);
					}
				}

				// Insert Dummy Values First Time
				if (insertDummyRow(label.toUpperCase())) {
					prepareBatch(((statusCallsPerOrder == -1) ? getLabel(label) : orderStatusLabel), -1, ((this.testStartTimestamp - (sampleDuration * 1000))), this.testStartTimestamp, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, statusCallsPerOrder, waitTime);
				}

				prepareBatch(((statusCallsPerOrder == -1) ? getLabel(label) : orderStatusLabel), this.sampleCount, this.startTimestamp, this.endTimestamp, latencyCount,
						this.labelLatencyMin.get(label), this.labelLatencyMax.get(label), latencyAvg, percentileLatency.get(index999), percentileLatency.get(index99), percentileLatency.get(index95),
						percentileLatency.get(index90), percentileLatency.get(index80), (bytes * 1.0), errorCount, statusCallsPerOrder, waitTime);

				Double latencyAvgTotal = this.labelLatencyAvgTotal.get(label);
				this.labelLatencyAvgTotal.put(label, (latencyAvgTotal == null) ? latencyTotal : (latencyAvgTotal + latencyTotal));

				final Integer errorCountTotal = this.labelErrorCountTotal.get(label);
				this.labelErrorCountTotal.put(label, (errorCountTotal == null) ? errorCount : (errorCountTotal + errorCount));

				final Long bytesTotal = this.labelBytesTotal.get(label);
				this.labelBytesTotal.put(label, (bytesTotal == null) ? bytes : (bytesTotal + bytes));

				latencyAvgTotal = this.labelLatencyMinTotal.get(label);
				if (latencyAvgTotal == null) {
					latencyAvgTotal = Double.MAX_VALUE;
				}
				this.labelLatencyMinTotal.put(label, Math.min(this.labelLatencyMin.get(label), latencyAvgTotal));

				latencyAvgTotal = this.labelLatencyMaxTotal.get(label);
				if (latencyAvgTotal == null) {
					latencyAvgTotal = 0.0;
				}
				this.labelLatencyMaxTotal.put(label, Math.max(this.labelLatencyMax.get(label), latencyAvgTotal));

			}
		}

		this.labelPercentile = null;
		this.labelLatencyAvg = null;
		this.labelLatencyMin = null;
		this.labelLatencyMax = null;
		this.labelErrorCount = null;
		this.labelBytes = null;
		this.orderStatusCallsMap = null;
		this.orderPlacementCallsMap = null;
	}

	private boolean insertDummyRow(final String label) {
		// Insert Only First Time
		if (insertDummyRowSet.contains(label)) {
			return false;
		}
		
		for (final String dummyLabel : JTLParser.dummyEntryLablesListEndsWith) {
			if (label.endsWith(dummyLabel)) {
				insertDummyRowSet.add(label);
				return true;
			}
		}
		for (final String dummyLabel : JTLParser.dummyEntryLablesListStartsWith) {
			if (label.startsWith(dummyLabel)) {
				insertDummyRowSet.add(label);
				return true;
			}
		}
		for (final String dummyLabel : JTLParser.dummyEntryLablesListContains) {
			if (label.contains(dummyLabel)) {
				insertDummyRowSet.add(label);
				return true;
			}
		}
		
		return false;
	}

	private String getLabel(final String label) {
		if (label.toUpperCase().endsWith(ORDER_PLACEMENT_LABEL))
			return label.toLowerCase().replace("paymentservice", ORDER_PLACEMENT_LABEL_NEW_SUFFIX);

		return label;
	}
}