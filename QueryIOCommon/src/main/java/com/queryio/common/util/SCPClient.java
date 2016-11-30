package com.queryio.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SCPClient {

	private static void sftpFile(File localFile, String username, String password, String hostname,
			String remoteDirectory) throws JSchException, SftpException, FileNotFoundException {

		JSch jsch = new JSch();
		String filename = localFile.getName();
		Session session = jsch.getSession(username, hostname, 22);
		session.setPassword(password);
		Properties config = new Properties();
		config.setProperty("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		channel.cd(remoteDirectory);
		channel.put(new FileInputStream(localFile), filename);
		channel.disconnect();
		session.disconnect();
	}
}
