package com.queryio.sysmoncommon.sysmon.protocol;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * This class is used to configure SSH. By default SSH prompts when a new
 * SSH key is received. This configuration disables all prompts do that the
 * login can be carried out with the provided username and password.
 * 
 * see the jsch library for more details.
 * 
 */
public class SSHUserInfo implements UserInfo, UIKeyboardInteractive 
{
	private final String passwd;
	private final boolean bPassphrase;

	public SSHUserInfo(final String passwd, boolean passphrase)
	{
		this.passwd = passwd;
		this.bPassphrase = passphrase;
	}

	public String getPassword()
	{
		return bPassphrase ? null : passwd;
	}

	public boolean promptYesNo(final String str)
	{
		return true;
	}

	public String getPassphrase()
	{
	    return bPassphrase ? passwd : null;
	}

	public boolean promptPassphrase(final String message)
	{
		return true;
	}

	public boolean promptPassword(final String message)
	{
		return true;
	}

	public void showMessage(final String message)
	{
	}

	public String[] promptKeyboardInteractive(String arg0, String arg1, String arg2, String[] arg3, boolean[] arg4) 
	{
		return new String [] {getPassword()};
	}
}
