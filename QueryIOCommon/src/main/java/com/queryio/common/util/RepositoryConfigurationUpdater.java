package com.queryio.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class RepositoryConfigurationUpdater {

	public static void main(final String[] args) {
		if ((args == null) || (args.length != 3)) {
			System.out.println("Usage : RepositoryConfigurationUpdater <RepositoryHome> <UserName> <Password>");
			return;
		}
		updateSVNServeConfFile(args[0]);
		updatePasswdFile(args[0], args[1], args[2]);
	}

	public static void updatePasswdFile(final String repositoryHome, final String userName, final String password) {
		final ArrayList alLines = new ArrayList();
		try {
			final File passwdFile = new File(
					repositoryHome + File.separatorChar + "conf" + File.separatorChar + "passwd");
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(passwdFile), "UTF8")); //$NON-NLS-1$
			String line = reader.readLine();
			while (line != null) {
				alLines.add(line);
				line = reader.readLine();
			}
			alLines.add(userName + " = " + password);
			reader.close();

			// we have read and updated all the lines...Now write the lines to
			// the file
			final BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(passwdFile), "UTF-8")); //$NON-NLS-1$
			for (int i = 0; i < alLines.size(); i++) {
				writer.write((String) alLines.get(i));
				writer.write(PlatformHandler.LINE_SEPARATOR);
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void updateSVNServeConfFile(final String repositoryHome) {
		final ArrayList alLines = new ArrayList();
		try {
			final File svnServeFile = new File(
					repositoryHome + File.separatorChar + "conf" + File.separatorChar + "svnserve.conf");
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(svnServeFile), "UTF8")); //$NON-NLS-1$
			String line = reader.readLine();
			while (line != null) {
				if ((line.indexOf("anon-access =") != -1) || (line.indexOf("anon-access=") != -1)) {
					alLines.add("anon-access = none");
				} else if ((line.indexOf("auth-access =") != -1) || (line.indexOf("auth-access=") != -1)) {
					alLines.add("auth-access = write");
				} else if ((line.indexOf("password-db =") != -1) || (line.indexOf("password-db=") != -1)) {
					alLines.add("password-db = passwd");
				} else {
					alLines.add(line);
				}
				line = reader.readLine();
			}

			reader.close();

			// we have read and updated all the lines...Now write the lines to
			// the file
			final BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(svnServeFile), "UTF-8")); //$NON-NLS-1$
			for (int i = 0; i < alLines.size(); i++) {
				writer.write((String) alLines.get(i));
				writer.write(PlatformHandler.LINE_SEPARATOR);
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
