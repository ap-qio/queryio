package com.queryio.common.util;


public final class StartupParameters
{
	private static String hadoopDirLocation;
	private static String defaultDirRemote;
	private static int sessionTimeout;
	private static int dataFetchIntervalInSeconds = 60;
	private static String queryIOAgentPort;

	public static String getDefaultDirRemote()
	{
		return defaultDirRemote;
	}
	
	public static void setDefaultDirRemote(String remote)
	{
		defaultDirRemote = remote;
	}
	
	public static int getSessionTimeout()
	{
		return sessionTimeout;
	}
	
	public static void setSessionTimeout(int t)
	{
		sessionTimeout = t;
	}
	
	public static String getHadoopDirLocation() {
		return hadoopDirLocation;
	}

	public static void setHadoopDirLocation(String hadoopDirLocation) {
		StartupParameters.hadoopDirLocation = hadoopDirLocation;
	}

	public static int getDataFetchIntervalInSeconds() {
		return dataFetchIntervalInSeconds;
	}

	public static void setDataFetchIntervalInSeconds(int dataFetchInterval) {
		StartupParameters.dataFetchIntervalInSeconds = dataFetchInterval;
	}

	public static String getQueryIOAgentPort() {
		return queryIOAgentPort;
	}

	public static void setQueryIOAgentPort(String queryIOAgentPort) {
		StartupParameters.queryIOAgentPort = queryIOAgentPort;
	}
}