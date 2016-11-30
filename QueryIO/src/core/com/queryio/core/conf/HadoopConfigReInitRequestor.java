package com.queryio.core.conf;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpStatus;

import com.queryio.core.bean.Host;

public class HadoopConfigReInitRequestor {
	public static void reInitializeFTPServer(Host host,String ftpPort) throws Exception
	{
		HttpURLConnection urlConnection = null;
		try{
			String url = "http://"+host.getHostIP()+":"+ftpPort+"/hdfs-over-ftp/reinitializeHadoopConf";
			URL os3DBReinitialzeRequestURL = new URL(url); 
			urlConnection = (HttpURLConnection)os3DBReinitialzeRequestURL.openConnection();
			urlConnection.connect();
			
			if(urlConnection.getResponseCode() != HttpStatus.SC_OK)
			{
				throw new Exception ("Improper response code received");
			}
		}
		finally{
			if(urlConnection != null)
				urlConnection.disconnect();
		}
	
	}
	public static void reInitializeOS3Server(Host host,String os3Port) throws Exception
	{
		
		HttpURLConnection urlConnection = null;
		try{
			String url = "http://"+host.getHostIP()+":"+os3Port+"/queryio/reinitializeHadoopConf";
//			System.out.println("url: "+url);	
			URL os3DBReinitialzeRequestURL = new URL(url); 
			urlConnection = (HttpURLConnection)os3DBReinitialzeRequestURL.openConnection();
			urlConnection.connect();
			
			if(urlConnection.getResponseCode() != HttpStatus.SC_OK)
			{
				throw new Exception ("Improper response code received");
			}
		}
		finally{
			if(urlConnection != null)
				urlConnection.disconnect();
		}
	}
}
