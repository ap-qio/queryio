package com.queryio.core.monitor.controllers;

interface JMXMonitorConstants extends JMXConstants {
	String DEFAULT_PROTOCOL_START = "service:jmx:rmi:///jndi/rmi://";
	String DEFAULT_PROTOCOL_END = "/jmxrmi";

	String PROPERTY_JVM_VENDOR = "JVM Vendor";
	String VENDOR_SUN = "Sun Microsystems";
	String VENDOR_IBM = "IBM Corporation";
	String VENDOR_BEA = "BEA Systems";
	String VENDOR_APPLE = "Apple Computer";
	String VENDOR_APPLE1 = "Apple Inc.";
}
