package com.queryio.demo.mr.report.aos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TransformCsvData {
	

	private static final String ORDER_STATUS_LABEL = "/CHECKOUTX/STATUS";
	private static final String ORDER_PLACEMENT_LABEL = "/CHECKOUTX - PAYMENTSERVICE";
	
	private static Map<String, Long> orderPlacementCallsMap = new HashMap<String, Long>();
	private static Map<String, Long> orderStatusCallsMap = new HashMap<String, Long>();
	private static long waitTime = 500;
	
	private static void updateOrderStatus(ConsolidatedRecord record) {
		String labelUpperCase = record.getLabel().toUpperCase();
		// Changes for Order Processing Step.
		if (labelUpperCase.endsWith(ORDER_PLACEMENT_LABEL)) {
			System.err.println("label : " + record.getLabel() + ", Placement task count : " + record.getTaskCount() + ", tick: " + record.getTickStartTime() + "_" + record.getTickEndTime());
			incrementCalls(orderPlacementCallsMap, record.getLabel() + record.getTickStartTime() + "_" + record.getTickEndTime(), record.getTaskCount());
		} else if (labelUpperCase.endsWith(ORDER_STATUS_LABEL)) {
			System.err.println("label : " + record.getLabel() + ", Status task count : " + record.getTaskCount() + ", tick: " + record.getTickStartTime() + "_" + record.getTickEndTime());
			incrementCalls(orderStatusCallsMap, record.getLabel() + record.getTickStartTime() + "_" + record.getTickEndTime(), record.getTaskCount());
		}
	}
	
	private static void incrementCalls(Map<String, Long> callsMap, final String label, Long taskCount) {
		Long calls = callsMap.get(label);
		if(calls != null) {
			System.err.println("Found entry in map for - " + label + " key with value : " + calls);
		}
		callsMap.put(label, calls == null ? taskCount : calls + taskCount);
	}
	
	private static void calculateOrderStatusCalls(ConsolidatedRecord record) {
		String label = record.getLabel();
		Long orderStatusCalls = -1L;
		Long orderPlacementCalls = -1L;
		double statusCallsPerOrder = -1;
		double waitTime = -1;
		String labelUpperCase = label.toUpperCase();
		if (isOrderStatus(labelUpperCase)) {
			orderStatusCalls = orderStatusCallsMap.get(record.getLabel() + record.getTickStartTime() + "_" + record.getTickEndTime());
			
			final String region = labelUpperCase.split("/")[1];
			final String orderLabel = "/" + region.toLowerCase() + "/checkoutx - PaymentService";
			orderPlacementCalls = orderPlacementCallsMap.get(orderLabel + record.getTickStartTime() + "_" + record.getTickEndTime());

			if (orderPlacementCalls == null || orderStatusCalls == null) {
				orderPlacementCalls = 1L;
				orderStatusCalls = 1L;
			}

			statusCallsPerOrder = (orderStatusCalls - orderPlacementCalls) / orderPlacementCalls;
			waitTime = TransformCsvData.waitTime * statusCallsPerOrder;
			
			if(statusCallsPerOrder > -1) {
				String orderStatusLabel = "Order Processing" + "-" + region.toLowerCase();
				record.setLabel(orderStatusLabel);
				record.setTaskCount(orderPlacementCalls);
				
				record.setErrorCount((long) (record.getErrorCount() / statusCallsPerOrder));
				record.computeAdditionalDetail();
				
				record.setAvgLatency(record.getAvgLatency() + waitTime);
				record.setMinLatency(record.getMinLatency() + waitTime);
				record.setMaxLatency(record.getMaxLatency() + waitTime);
			}
		}
	}
	
	private static boolean isOrderStatus(String label) {
		return label.endsWith(ORDER_STATUS_LABEL);
	}

	public static List<String> makeReportGeneratorCompatible(List<String> data, long samplingInterval) {
			Collections.sort(data);
			return updateRecords(data, samplingInterval);
	}

	private static List<String> updateRecords(List<String> data, long samplingInterval) {
		Map<String, ConsolidatedRecord> records = new LinkedHashMap<String, ConsolidatedRecord>();
		
		for(String record : data) {
			ConsolidatedRecord recordBean = DBHandler.getBeanFromString(record);
			records.put(recordBean.getLabel() + recordBean.getTickStartTime() + "_" + recordBean.getTickEndTime(), recordBean);
			updateOrderStatus(recordBean);
		}
		
		System.err.println(orderStatusCallsMap);
		System.err.println(orderPlacementCallsMap);
		
		List<String> updatedDataSet = new ArrayList<String>();
		String lastLabel = null;
		for(Entry<String, ConsolidatedRecord> entry : records.entrySet()) {			
			ConsolidatedRecord record = entry.getValue();
			ConsolidatedRecord dummyRecord = getDummyRow(record, lastLabel, samplingInterval);
			if(dummyRecord != null) {
				updatedDataSet.add(dummyRecord.toString());
				lastLabel = record.getLabel();
			}
			calculateOrderStatusCalls(record);
			
			updatedDataSet.add(record.toString());
		}
		return updatedDataSet;
	}

	private static ConsolidatedRecord getDummyRow(ConsolidatedRecord record, String lastLabel, long samplingInterval) {
		ConsolidatedRecord consolidatedRecord = null;
		if(lastLabel == null || !lastLabel.equals(record.getLabel())) {
			consolidatedRecord = ConsolidatedRecord.getObjectWithDefaultInitialization();
			consolidatedRecord.setLabel(record.getLabel());
			consolidatedRecord.setSampleValue(-1);
			consolidatedRecord.setTickEndTime(record.getTickStartTime());
			consolidatedRecord.setTickStartTime(record.getTickStartTime() - samplingInterval);
		}
		return consolidatedRecord;
	}
}
