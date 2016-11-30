package com.queryio.demo.mr.csv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

public class Main {
	public static void main(String[] args)
			throws FileNotFoundException, IOException, InterruptedException, ClassNotFoundException, SQLException {
		// String expressions = null;
		// String expressions = "[IP NOTCONTAINEDIN
		// (\"AP01\",\"AP\\\"02\",\"AP,03\")]";
		// String expressions = "[IP CONTAINEDIN (\"AP01\",\"AP02\",\"AP03\")]";
		String expressions = "[CPU CONTAINEDIN (\"97\", \"100\")]";
		// String expressions = "[CPU >= 99]";
		// String expressions = "[CPU BETWEEN (\"97\", \"100\")]";

		CSVExpressions csvExpressions = new CSVExpressions(expressions);

		InputStream is = new FileInputStream("/AppPerfect/QueryIO/demo/Data/csv/MachineLogs_1362132655978.csv");

		FileStatus fileStatus = new FileStatus();
		fileStatus.setPath(new Path("/test"));

		Class.forName("org.hsqldb.jdbcDriver");
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://192.168.0.12:5681/MetaStore", "ADMIN",
				"ADMIN");

		CSVParser parser = new CSVParser(csvExpressions, connection, "TEST", fileStatus, 100);
		parser.parse(is);
	}
}