package com.queryio.demo.mr.ncdcgsod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.log4j.BasicConfigurator;

public class Main {
	public static void main(String[] args) throws SQLException, SocketException, IOException {

		BasicConfigurator.configure();

		Connection connection = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://192.168.0.16:5681/bigquery", "ADMIN", "ADMIN");

			FTPClient client = new FTPClient();
			client.connect("ftp.ncdc.noaa.gov");
			client.login("anonymous", null);

			FTPFile[] dirFtp = client.listDirectories("/pub/data/gsod/");

			Configuration conf = new Configuration();
			conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, "hdfs://192.168.0.16:9000");
			// FileSystem dfs = FileSystem.get(conf);

			for (int i = 0; i < dirFtp.length; i++) {
				System.out.println("Directory: " + dirFtp[i].getName());

				FTPFile[] fileFtp = client.listFiles("/pub/data/gsod/" + dirFtp[i].getName());

				for (int j = 0; j < fileFtp.length; j++) {
					try {
						is = client.retrieveFileStream(
								"/pub/data/gsod/" + dirFtp[i].getName() + "/" + fileFtp[j].getName());

						System.out.println(
								"File: " + "/pub/data/gsod/" + dirFtp[i].getName() + "/" + fileFtp[j].getName());

						if (is != null) {
							/* PARSE */
							FileStatus fileStatus = new FileStatus();
							fileStatus.setPath(
									new Path("/pub/data/gsod/" + dirFtp[i].getName() + "/" + fileFtp[j].getName()));
							NCDCParser parser = new NCDCParser(new NCDCExpressions("[DAY==1]&&[MONTH>5]"), connection,
									"NCDC_DATA", fileStatus, 500);

							parser.parse(is);

							/* SAVE TO CLUSTER */

							// os = dfs.create(new Path("/pub/data/gsod/" +
							// dirFtp[i].getName() + "/" +
							// fileFtp[j].getName()));
							// IOUtils.copy(is, os);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (is != null)
								is.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (os != null)
								os.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
