package com.queryio.demo.mr.ncdcgsod;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.ftp.FTPFileSystem;

public class NCDCExtractor {
	private FTPFileSystem ftpFS;
	private final static String FTP_URL = "ftp://ftp.ncdc.noaa.gov/pub/data/gsod/";
	private final static String FTP_HOST = "ncdc.noaa.gov";
	private final static String FTP_PATH = "/pub/data/gsod/";

	private List getAllFilePaths(String str) throws Exception {
		Path path = new Path(str);
		FileStatus stat = ftpFS.getFileStatus(path);
		List list = new ArrayList();
		if (!stat.isDirectory()) {
			list.add(path);
		} else {
			FileStatus[] stats = ftpFS.listStatus(path);
			for (int i = 0; i < stats.length; i++) {
				if (!str.endsWith("/"))
					str += "/";
				list.addAll(getAllFilePaths(str + stats[i].getPath().getName()));
			}
		}
		return list;
	}

	private BufferedInputStream getObject(Path path) throws Exception {

		FSDataInputStream inputStream = ftpFS.open(path);
		return new BufferedInputStream(inputStream);
	}

	private void createObject(String bucketName, String path, BufferedInputStream bufferedInputStream,
			boolean overwrite) throws Exception {
		if (bucketName.endsWith("/"))
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		if (!bucketName.startsWith("/"))
			bucketName = "/" + bucketName;

		FSDataOutputStream outputStream = null;
		try {
			// outputStream = dfs.create(objectPath, overwrite);
			//
			// writeToStream(bufferedInputStream, outputStream);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while ((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}

	public void process() throws Exception {
		Configuration config = new Configuration();
		String ftpHost = FTP_HOST;
		String ftpPort = String.valueOf(22);

		config.set("fs.ftp.host", ftpHost);
		config.set("fs.ftp.host.port", ftpPort);
		config.set("fs.ftp.user." + ftpHost, "annonymous");
		config.set("fs.ftp.password." + ftpHost,
				System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName());

		ftpFS = new FTPFileSystem();
		ftpFS.setConf(config);

		List filePaths = getAllFilePaths(FTP_PATH);

		for (int i = 0; i < filePaths.size(); i++) {

			Path filePath = (Path) filePaths.get(i);
			BufferedInputStream inputStream = null;
			try {
				inputStream = getObject(filePath);

				// createObject("/", filePath.toUri().getPath(), inputStream,
				// true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new NCDCExtractor().process();
	}
}
