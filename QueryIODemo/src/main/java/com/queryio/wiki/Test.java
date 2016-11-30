package com.queryio.wiki;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;

import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;

public class Test {
	public static int count = 0;
	public static int maxCount = 0;
	public static long systime = 0L;
	private static FileSystem fs = null;
	private static String hdfsUri = null;
	private static String dbConfigFilePath = null;
	private static String dbSource = null;

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setName("admin");
		if (args.length != 5) {
			System.err.println("Invalid Input.");
			System.err.println("Usage: <input-filepath> <hdfs-uri> <dbconfig-filepath> <DBSource> <no-of-entries>");
			return;
		}
		systime = System.currentTimeMillis();
		if (args[4].equalsIgnoreCase("ALL"))
			maxCount = Integer.MAX_VALUE;
		else
			maxCount = Integer.parseInt(args[4]);
		hdfsUri = args[1];
		dbConfigFilePath = args[2];
		dbSource = args[3];
		BZip2CompressorInputStream is = new BZip2CompressorInputStream(new FileInputStream(args[0]));
		WikiXMLParser wxsp = new WikiXMLSAXParser(is);
		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				public void process(final WikiPage page) {
					try {
						insertIntoHDFS(page);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			wxsp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			is.close();
		}
		long newSystime = System.currentTimeMillis();
		System.err.println("Parser took " + (newSystime - systime) + " ms to parse " + count + " entries.");
	}

	private static void insertIntoHDFS(WikiPage page) throws Exception {
		if (count == maxCount) {
			long newSystime = System.currentTimeMillis();
			System.err.println("Parser took " + (newSystime - systime) + " ms to parse " + count + " entries.");
			System.exit(0);
		}
		FileSystem fs = getFileSystem();

		String fileName = "/wiki/" + getCategory(page.getInfoBox()).replaceAll("[.:,]", " ") + "/"
				+ page.getTitle().trim().replaceAll("[.:,]", " ") + ".wiki";
		fileName = fileName.replaceAll("  ", " ");
		System.err.println(++count + ". " + fileName);
		FSDataOutputStream os = fs.create(new Path(fileName));
		os.write(page.getWikiText().getBytes());
		os.close();
	}

	private static FileSystem getFileSystem() throws IOException {
		if (fs == null) {
			Configuration conf = new Configuration();
			conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, hdfsUri);
			conf.setInt(DFSConfigKeys.DFS_REPLICATION_KEY, 1);
			conf.set("queryio.bigquery.db.dbsourceid", dbSource);
			conf.set("queryio.bigquery.db.dbconfig-path", dbConfigFilePath);
			conf.set("queryio.bigquery.parser.filetypes", "wiki");
			conf.set("queryio.bigquery.parser.classname.wiki", "com.queryio.datatags.WikiTextParser");
			fs = FileSystem.get(conf);
		}
		return fs;
	}

	private static String getCategory(InfoBox infoBox) {
		if (infoBox != null) {
			StringBuilder sb = new StringBuilder();
			for (char c : infoBox.dumpRaw().toCharArray()) {
				if (c == '|') {
					break;
				} else {
					sb.append(c);
				}
			}
			String str = sb.toString();

			if (str.length() > 0 && str.contains("Infobox")) {
				String[] arr = str.split("Infobox");
				if (arr.length > 1)
					return arr[1].trim().replaceAll("  ", " ").replaceAll("[^a-zA-Z0-9 ]", "");
			}
		}
		return "Unknown";
	}
}
