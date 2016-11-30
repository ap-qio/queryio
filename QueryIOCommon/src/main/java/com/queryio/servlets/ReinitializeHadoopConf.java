package com.queryio.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import com.queryio.common.ClassPathUtility;
import com.queryio.common.DFSMap;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.HadoopConstants;

public class ReinitializeHadoopConf extends HttpServlet {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger("Common");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		doGet(req, res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		logger.debug("Found ReinitializeHadoopConf request.");
		initialize();
		ClassPathUtility.recycleClassLoder();
		DFSMap.updateDFSConfig();
	}

	public static void initialize() {
		String hadoopConfigPath = EnvironmentalConstants.getHadoopConfPath();
		if (hadoopConfigPath != null && !hadoopConfigPath.isEmpty()) {
			logger.debug("hadoopConfigPath: " + hadoopConfigPath);
			Configuration hadoopConfiguration = new Configuration(true);
			Configuration conf = new Configuration();
			FileInputStream fis1 = null;
			FileInputStream fis2 = null;
			try {
				logger.debug("Loading config file: " + hadoopConfigPath + File.separator + "core-site.xml");
				fis1 = new FileInputStream(new File(hadoopConfigPath + File.separator + "core-site.xml"));
				logger.debug("Loading config file: " + hadoopConfigPath + File.separator + "hdfs-site.xml");
				fis2 = new FileInputStream(new File(hadoopConfigPath + File.separator + "hdfs-site.xml"));
				conf.addResource(fis1);
				conf.addResource(fis2);
				Iterator<Entry<String, String>> i = conf.iterator();
				String key;
				Entry<String, String> entry;
				while (i.hasNext()) {
					entry = i.next();
					key = entry.getKey();
					hadoopConfiguration.set(key, entry.getValue());
				}
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			} finally {
				try {
					if (fis1 != null)
						fis1.close();
				} catch (Exception e) {
					logger.fatal(e.getMessage(), e);
				}
				try {
					if (fis2 != null)
						fis2.close();
				} catch (Exception e) {
					logger.fatal(e.getMessage(), e);
				}
			}
			logger.debug("Configuration loaded successfully");
			HadoopConstants.setHadoopConf(hadoopConfiguration);
		}
	}
}
