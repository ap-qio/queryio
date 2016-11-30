/*
 * @(#)  FTPWrapper.java Dec 14, 2004
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

/**
 * 
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class FTPWrapper {
	public static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	private final FTPClient ftp;
	private String sHostName;

	public FTPWrapper() {
		this.ftp = new FTPClient();
	}

	public void connect(final String host) throws Exception {
		try {
			this.sHostName = host;
			this.ftp.connect(host, 21); // 21 Standard port no for FTP
		} catch (final Exception e) {
			throw new RuntimeException("FTP server problem while connecting on port NO. '21' on host: " + host);
		}
		final int reply = this.ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			final String replyString = this.ftp.getReplyString();
			this.ftp.disconnect(); // to properly clean up the system resources
			// used by FTPClient
			throw new RuntimeException("FTP server refused connection on port NO. '21' on host: " + host
					+ LINE_SEPARATOR + "The reply code is: " + replyString);
		}
	}

	public void login(final String sUserName, final String sPassword) throws Exception {
		this.login(sUserName, sPassword, false);
	}

	private void login(final String sUserName, final String sPassword, final boolean reconnected) throws Exception {
		try {
			if (!this.ftp.login(sUserName, sPassword)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.login(sUserName, sPassword, true);
				}
			} else {
				this.doForCommandFail("Could not login with user: '" + sUserName + "' and password: '" + sPassword
						+ "' on host: " + this.sHostName);
			}
		}
	}

	/**
	 * Returns true if successful , return false if the directory not
	 * available(or does not exist) throws exception with particulars in message
	 */
	public boolean changePWD(final String sAbsolutePath) throws Exception {
		return this.changePWD(sAbsolutePath, false);
	}

	private boolean changePWD(final String sAbsolutePath, final boolean reconnected) throws Exception {
		try {
			if (!this.ftp.changeWorkingDirectory(sAbsolutePath)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.changePWD(sAbsolutePath, true);
				}
			} else if (this.ftp.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
				return false;
			} else {
				this.doForCommandFail(
						"Could not change to directory '" + sAbsolutePath + "' on host: " + this.sHostName);
			}
		}
		return true;
	}

	public void createDirectory(final String sAbsolutePath) throws Exception {
		this.createDirectory(sAbsolutePath, false);
	}

	private void createDirectory(final String sAbsolutePath, final boolean reconnected) throws Exception {
		try {
			if (!this.ftp.makeDirectory(sAbsolutePath)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.createDirectory(sAbsolutePath, true);
				}
			} else {
				this.doForCommandFail("Could not create directory '" + sAbsolutePath + "' on host: " + this.sHostName);
			}
		}
	}

	public boolean deleteFile(final String remoteFilePath) throws Exception {
		return this.deleteFile(remoteFilePath, false);
	}

	public boolean deleteFile(final String remoteFilePath, final boolean reconnected) throws Exception {
		try {
			if (!this.ftp.deleteFile(remoteFilePath)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.deleteFile(remoteFilePath, true);
				}
			} else if (this.ftp.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
				return false;
			} else {
				this.doForCommandFail("Could not delete file '" + remoteFilePath + "' on host: " + this.sHostName);
			}
		}
		return true;
	}

	public void sendFile(final String localFilePath, final String remoteFilePath) throws Exception {
		this.sendFile(localFilePath, remoteFilePath, false);
	}

	private void sendFile(final String localFilePath, final String remoteFilePath, final boolean reconnected)
			throws Exception {
		final FileInputStream fis = new FileInputStream(localFilePath);
		try {
			if (!this.ftp.appendFile(remoteFilePath, fis)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FileNotFoundException) // thrown by FileInputStream
			{
				throw new RuntimeException("File not found: " + localFilePath);
			} else if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.sendFile(localFilePath, remoteFilePath, true);
				}
			} else {
				this.doForCommandFail("Could not send file '" + localFilePath + "' to '" + remoteFilePath
						+ "' on host: " + this.sHostName);
			}
		} finally {
			fis.close();
		}
	}

	public void getFile(final String remoteFilePath, final String localFilePath) throws Exception {
		this.getFile(remoteFilePath, localFilePath, false);
	}

	private void getFile(final String remoteFilePath, final String localFilePath, final boolean reconnected)
			throws Exception {
		final FileOutputStream fos = new FileOutputStream(localFilePath);
		try {
			if (!this.ftp.retrieveFile(remoteFilePath, fos)) {
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			if (e instanceof FileNotFoundException) // thrown by FileInputStream
			{
				throw new RuntimeException("File not found: " + localFilePath);
			} else if (e instanceof FTPConnectionClosedException) {
				if (reconnected) {
					this.doForConnectionTimeout();
				} else {
					this.disconnect(); // to properly clean up the system
					// resources used by FTPClient
					this.connect(this.sHostName);
					this.getFile(remoteFilePath, localFilePath, true);
				}
			} else {
				this.doForCommandFail("Could not retrieve file '" + remoteFilePath + "' to '" + localFilePath
						+ "' on host: " + this.sHostName);
			}
		} finally {
			fos.close();
		}
	}

	private void doForConnectionTimeout() throws Exception {
		final String reply = this.ftp.getReplyString();
		this.disconnect(); // to properly clean up the system resources used by
		// FTPClient
		throw new RuntimeException(
				"FTP server disconneced on host: " + this.sHostName + LINE_SEPARATOR + "The reply code is: " + reply);
	}

	private void doForCommandFail(final String sCommandMessage) throws Exception {
		final String replyString = this.ftp.getReplyString();
		this.disconnect(); // to properly clean up the system resources used by
		// FTPClient
		throw new RuntimeException(sCommandMessage + LINE_SEPARATOR + "The reply code is: " + replyString);
	}

	public void disconnect() throws Exception {
		try {
			this.ftp.disconnect();
		} catch (final Exception e) {
			throw new RuntimeException("FTP server problem while disconnecting on host: " + this.sHostName);
		}
	}
}
