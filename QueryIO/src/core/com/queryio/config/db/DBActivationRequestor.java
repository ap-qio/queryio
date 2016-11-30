package com.queryio.config.db;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpStatus;

import com.queryio.core.bean.Host;



public class DBActivationRequestor {

	
	public static void reInitializeFTPServer(Host host,String ftpPort,boolean isCustomtagDB) throws Exception
	{
		HttpURLConnection urlConnection = null;
		try{
			String url = "http://"+host.getHostIP()+":"+ftpPort+"/hdfs-over-ftp/reinitializeDB?isCustomTagDB="+isCustomtagDB;
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
	public static void reInitializeOS3Server(Host host,String os3Port,boolean isCustomtagDB) throws Exception
	{
		
		HttpURLConnection urlConnection = null;
		try{
			String url = "http://"+host.getHostIP()+":"+os3Port+"/queryio/reinitializeDB?isCustomTagDB="+isCustomtagDB;
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
