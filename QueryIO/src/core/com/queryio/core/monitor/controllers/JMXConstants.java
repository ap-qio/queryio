package com.queryio.core.monitor.controllers;

public interface JMXConstants {
	String JMXCONNECTORPORT_PROPERTY = "JMX Connector Port";
	String PASSWORD_PROPERTY = "Password";
	String USERNAME_PROPERTY = "User Name";

	String DEFAULT_HOST_NAME = "localhost";
	String DEFAULT_HOST_IP = "127.0.0.1";
	int JMX_CONNECTOR_PORT = 9024;

	String ERROR_UNABLE_TO_CONNECT_TO_SERVER = "Unable to connect to server. This can happen for various reasons:<BR>";
	String ERROR_AUTHENTICATION = "Authentication error due to invalid Username / Password<BR>";
	String ERROR_UNABLE_TO_CONNECT_TO_SERVER_SUGGESTION1 = "1. IP Address / Host Name specified are invalid<BR>";
	String ERROR_UNABLE_TO_CONNECT_TO_SERVER_SUGGESTION2 = "2. Server is not available at this time<BR>";
	String ERROR_UNABLE_TO_CONNECT_TO_SERVER_SUGGESTION3 = "3. JMX Services not Enabled. Please refer to docs on how to configure the server";
}
