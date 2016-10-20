package com.queryio.common.database;

public interface IOSProtocolConstants
{
	public final int WINDOWS = 0;
	public final int LINUX = 1;
	public final int SOLARIS = 2;
	public final int MACOSX = 3;
	public final int AIX = 4;
	public final int LINUX_OLD = 5;
	public final int SOLARIS_OLD = 6;
	
	public final int UNDEFINED = -1;
	public final int TELNET = 3;
	public final int RSTAT = 4;
	public final int SSH = 5;
	public final int PDH = 6;
	public final int SNMP = 7;

	public final int DEFAULT_TELNET_PORT = 23;
	public final int DEFAULT_SSH_PORT = 22;

	public final String LOOPBACKADDRESS = "127.0.0.1"; //$NON-NLS-1$
	public final String LOCALHOST = "localhost"; //$NON-NLS-1$
	
	public final int AUTH_METHOD_PASSWORD = 0;
	public final int AUTH_METHOD_PRIVATE_KEY = 1;
}
