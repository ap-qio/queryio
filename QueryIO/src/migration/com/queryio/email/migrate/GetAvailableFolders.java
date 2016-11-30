package com.queryio.email.migrate;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import com.queryio.core.datasources.DataSourceManager;
import com.queryio.core.datasources.EmailDataSource;

public class GetAvailableFolders {
	private String emailAddress;
	private String password;
	private String mailServer;
	private String account;
	private String protocol;
	private String socket;
	private int port;
	private long connectionTimeOut;
	private long readTimeOut;

	ArrayList<String> folderList;

	public GetAvailableFolders(String dataConnection) throws Exception {
		EmailDataSource ds = new EmailDataSource();
		ds = DataSourceManager.getEmailDataSource(dataConnection);

		this.emailAddress = ds.getEmailAddress();
		this.password = ds.getPassword();
		this.mailServer = ds.getMailServerAddress();
		this.account = ds.getAccountName();
		this.protocol = ds.getProtocol();
		this.socket = ds.getSocketType();
		this.port = ds.getPort();
		this.connectionTimeOut = ds.getConnectionTimeOut();
		this.readTimeOut = ds.getReadTimeOut();

		folderList = new ArrayList<String>();
	}

	public ArrayList<String> connect() throws Exception {
		String connTimeProp = "mail." + this.protocol.toLowerCase() + ".connectiontimeout";
		String readTimeProp = "mail." + this.protocol.toLowerCase() + ".timeout";
		String portProp = "mail." + this.protocol.toLowerCase() + ".port";

		Properties props = System.getProperties();

		if (this.protocol.equalsIgnoreCase("imap"))
			props.setProperty("mail.store.protocol", this.protocol.toLowerCase() + "s");
		else
			props.setProperty("mail.store.protocol", this.protocol.toLowerCase());

		props.setProperty(connTimeProp, String.valueOf(this.connectionTimeOut));
		props.setProperty(readTimeProp, String.valueOf(this.readTimeOut));
		props.setProperty(portProp, String.valueOf(this.port));

		Session session = Session.getDefaultInstance(props, null);

		Store store = null;

		if (this.protocol.equalsIgnoreCase("imap"))
			store = session.getStore(this.protocol.toLowerCase() + "s");
		else
			store = session.getStore(this.protocol.toLowerCase());

		store.connect(this.mailServer, this.emailAddress, this.password);

		Folder[] f = store.getDefaultFolder().list();
		for (Folder fd : f) {
			folderList.add(fd.getName());
		}

		store.close();

		return folderList;
	}
}
