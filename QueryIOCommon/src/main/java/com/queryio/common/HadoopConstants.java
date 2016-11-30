package com.queryio.common;

import org.apache.hadoop.conf.Configuration;

public class HadoopConstants {

	private static Configuration hadoopConf;

	public static Configuration getHadoopConf() {
		return hadoopConf;
	}

	public static void setHadoopConf(Configuration hadoopConf) {
		HadoopConstants.hadoopConf = hadoopConf;
	}
}
