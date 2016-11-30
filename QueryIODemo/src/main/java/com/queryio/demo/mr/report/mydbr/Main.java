package com.queryio.demo.mr.report.mydbr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

public class Main {
	public static void main(String[] args)
			throws FileNotFoundException, IOException, InterruptedException, ClassNotFoundException, SQLException {

		InputStream is = new FileInputStream("/Users/queryio/QueryIO_v3/demo/sample.jtl");

		FileStatus fileStatus = new FileStatus();
		fileStatus.setPath(new Path("/Users/queryio/QueryIO_v3/demo/test"));

		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.2:5432/hive", "ADMIN",
				"ADMIN");

		ArrayList<String> columns = new JTLDataDefinitionImpl().getTableColumns();

		JTLParser parser = new JTLParser(connection, "TEST2", fileStatus, 100, 1, 0, 2, 7, 8, 11, "12:23:34 PDT",
				"12:28:34 PDT", 500, columns);
		parser.parse(is);
	}
}