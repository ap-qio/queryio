package com.queryio.demo.mr.log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.BasicConfigurator;

public class LogParserTest {
	public static void main(String[] args) throws SQLException, IOException, InterruptedException {
		BasicConfigurator.configure();

		InputStream is = null;
		Connection connection = null;
		try {
			is = new FileInputStream("/AppPerfect/Logs/Log_1.log");

			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://192.168.0.12:5681/bigquery", "ADMIN", "ADMIN");
			FileStatus status = new FileStatus();
			status.setPath(new Path("/Log_1350485943116.log"));

			String searchString = "NullPointerException";
			Date startDate = null;
			Date endDate = null;

			LogParser parser = new LogParser(connection, "DATA_LOG",
					"%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n", searchString,
					startDate, endDate, status, 100);
			parser.parse(is);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public static void main(String[] args) throws SQLException, IOException,
	// InterruptedException{
	// BasicConfigurator.configure();
	//
	// FileInputStream stream = new FileInputStream(new
	// File("/AppPerfect/MESSAGE"));
	// try {
	// FileChannel fc = stream.getChannel();
	// MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
	// fc.size());
	//
	// String content = Charset.defaultCharset().decode(bb).toString();
	//
	// System.out.println("CONTENT: " + content);
	// System.out.println("MATCHES: " +
	// content.matches("NullPointerException"));
	// }
	// finally {
	// stream.close();
	// }
	// }
}
