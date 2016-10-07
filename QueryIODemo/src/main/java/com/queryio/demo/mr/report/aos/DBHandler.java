package com.queryio.demo.mr.report.aos;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DBHandler {
	
	private static final String TABLE_PREFIX = "JTL_SORTED_FINAL_";
	private static String runId;
	private static File dataDirectory;
	private static long samplingInterval;
	private static List<String> columnNames;
	private static Connection connection;
	private static PreparedStatement pst;
	
	private static int maxBatchSize = 100; 
	private static int currentBatchSize = 0;
	private static ArrayList<String> columnType;
	
	public static void main(String[] args) {
		if(args.length != 3) {
			usage();
			return;
		}
		try {			
			loadParameters(args);
			validate();
			initialize();
			processFiles();
		} catch(SQLException e) {
			e.printStackTrace();
			if(e.getNextException() != null) {
				e.getNextException().printStackTrace();
			}
		} catch(Exception e) {
			System.err.println("Error processing the data. " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void initialize() throws ClassNotFoundException, SQLException {
		loadFields();
		getConnection();
		dropTable();
		createTable();
		prepareStatement();
	}

	private static void createTable() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(TABLE_PREFIX).append(runId).append("( ");
		for(int i = 0; i < columnNames.size(); i++) {
			sb.append(columnNames.get(i)).append(" ").append(columnType.get(i));
			if(i != columnNames.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		System.out.println("Create table query : " + sb.toString());
		try(PreparedStatement pst = connection.prepareStatement(sb.toString())) {
			pst.execute();
		}
	}

	private static void dropTable() throws SQLException {		
		try(PreparedStatement pst = connection.prepareStatement("drop table if exists " + TABLE_PREFIX + runId)) {
			pst.execute();
		}
	}

	private static void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection("jdbc:postgresql://qaperf4.apple.com:5432/hive", "ADMIN", "ADMIN");
	}

	private static void validate() {
		if(runId.trim().isEmpty()) {
			throw new IllegalArgumentException("Run ID is not specified.");
		}
		if(dataDirectory.getAbsolutePath().trim().isEmpty()) {
			throw new IllegalArgumentException("Data directory is not specified.");			
		}
		if(!dataDirectory.exists()) {
			throw new IllegalArgumentException("Data directory does not exist.");			
		}
		if(!dataDirectory.isDirectory()) {
			throw new IllegalArgumentException("Specified data directory is file, not directory.");			
		}
	}

	private static void loadParameters(String[] args) {
		runId = args[0];
		dataDirectory = new File(args[1]);
		samplingInterval = TimeUnit.SECONDS.toMillis(Integer.parseInt(args[2]));
	}
	
	private static void usage() {
		System.err.println("USAGE: " + DBHandler.class.getCanonicalName() + " <runId> <data directory>");
	}
	
	private static void processFiles() throws FileNotFoundException, IOException, SQLException {
		File[] files = dataDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file != null && file.isFile() && file.getName().endsWith(".csv");
			}
		});
		
		List<String> data = new ArrayList<String>();
		for(File file : files) {
			data.addAll(Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.forName("UTF-8")));
		}
		System.out.println("data size before dummy rows : " + data.size());
		data = TransformCsvData.makeReportGeneratorCompatible(data, samplingInterval);
		File ouputFile = new File(dataDirectory.getAbsolutePath() + "/output.txt");
		ouputFile.createNewFile();
		Files.write(Paths.get(ouputFile.getAbsolutePath()), data, Charset.forName("utf-8"), StandardOpenOption.TRUNCATE_EXISTING);
		System.out.println("data size after dummy rows : " + data.size());
		storeFileInDatabase(data);
	}

	private static void storeFileInDatabase(List<String> data) throws FileNotFoundException, IOException, SQLException {
		
		for(String line : data) {			
			ConsolidatedRecord record = getBeanFromString(line);
			if(record != null) {				
				prepareBatch(record);
			}
		}
		if (currentBatchSize > 0) {			
			pst.executeBatch();
		}
	}
	
	public static ConsolidatedRecord getBeanFromString(String line) {
//		label,tickStartTime,tickEndTime,sampleValue,latency,bytes,errorCount,success,minLatency,maxLatency,avgLatency,minBytes,maxBytes,avgBytes,taskCount,avgTps,successCount,successPercentage
		
		String[] token = line.split(",");
		if(token.length < 18) {
			System.err.println("Record is invaid, skipping: " + line);
			return null;
		}
		ConsolidatedRecord record = new ConsolidatedRecord();
		record.setLabel(token[0]);
		record.setTickStartTime(Long.valueOf(token[1]));
		record.setTickEndTime(Long.valueOf(token[2]));
		record.setSampleValue(Integer.valueOf(token[3]));
		record.setLatency(Double.valueOf(token[4]));
		record.setBytes(Long.valueOf(token[5]));
		record.setErrorCount(Long.valueOf(token[6]));
		record.setSuccess(Boolean.valueOf(token[7]));
		
		record.setMinLatency(Double.valueOf(token[8]));
		record.setMaxLatency(Double.valueOf(token[9]));
		record.setAvgLatency(Double.valueOf(token[10]));
		record.setMinBytes(Long.valueOf(token[11]));
		record.setMaxBytes(Long.valueOf(token[12]));		
		record.setAvgBytes(Double.valueOf(token[13]));
		
		record.setTaskCount(Long.valueOf(token[14]));
		record.setAvgTps(Double.valueOf(token[15]));
		record.setSuccessCount(Long.valueOf(token[16]));
		record.setSuccessPercentage(Double.valueOf(token[17]));
		
		System.out.println("record from string : " + record);
		
		return record;
	}

	private static void prepareStatement() throws SQLException {
		final StringBuffer query = new StringBuffer();
		final StringBuffer valueBuf = new StringBuffer();

		query.append("INSERT INTO ");
		query.append(TABLE_PREFIX + runId).append(" (");

		for (int i = 0; i < columnNames.size(); i++) {
			query.append(columnNames.get(i));
			valueBuf.append("?");
			if(i != columnNames.size() - 1) {				
				query.append(",");
				valueBuf.append(",");
			}
		}

		query.append(") VALUES (").append(valueBuf.toString());
		query.append(")");
		System.out.println("Insert query : " + query.toString());
		pst = connection.prepareStatement(query.toString());
	}
	
	private static void prepareBatch(ConsolidatedRecord record) throws SQLException {
		
		int index = 0;
		pst.setString(++index, record.getLabel());// sample_label
		pst.setInt(++index, record.getSampleValue());// sample_value
		pst.setTimestamp(++index, new Timestamp(record.getTickStartTime()));// start_timestamp
		pst.setTimestamp(++index, new Timestamp(record.getTickEndTime()));// end_timestamp
		pst.setLong(++index, record.getTaskCount());// task_count
		pst.setDouble(++index, record.getMinLatency());// min_latency
		pst.setDouble(++index, record.getMaxLatency());// max_latency
		pst.setDouble(++index, record.getAvgLatency());// avg_latency
		pst.setDouble(++index, record.getAvgTps());// avg_tps
		pst.setDouble(++index, record.getBytes());// total_bytes
		pst.setLong(++index, record.getErrorCount());// errors_count
		pst.setLong(++index, record.getSuccessCount());// success_count
		pst.setDouble(++index, record.getSuccessPercentage());// success_percentage
		pst.addBatch();

		currentBatchSize ++;
		
		executeBatch();
	}

	private static void executeBatch() throws SQLException {
		if (currentBatchSize > 0 && currentBatchSize % maxBatchSize == 0) {
			pst.executeBatch();
			pst.clearBatch();
			currentBatchSize = 0;
		}
	}
	
	private static void loadFields() {
		columnNames = new ArrayList<String>();
		columnNames.add("SAMPLE_LABEL");
		columnNames.add("SAMPLE_VALUE");
		columnNames.add("START_TIMESTAMP");
		columnNames.add("END_TIMESTAMP");
		columnNames.add("TASK_COUNT");
		columnNames.add("MIN_LATENCY");
		columnNames.add("MAX_LATENCY");
		columnNames.add("AVG_LATENCY");
		columnNames.add("AVG_TPS");
		columnNames.add("TOTAL_BYTES");
		columnNames.add("ERRORS_COUNT");
		columnNames.add("SUCCESS_COUNT");
		columnNames.add("SUCCESS_PERCENTAGE");
		
		columnType = new ArrayList<String>();
		columnType.add("VARCHAR(1024)");
		columnType.add("BIGINT");
		columnType.add("TIMESTAMP");
		columnType.add("TIMESTAMP");
		columnType.add("BIGINT");
		columnType.add("DECIMAL");
		columnType.add("DECIMAL");
		columnType.add("DECIMAL");
		columnType.add("DECIMAL");
		columnType.add("DECIMAL");
		columnType.add("BIGINT");
		columnType.add("BIGINT");
		columnType.add("DECIMAL");	
	}

}
