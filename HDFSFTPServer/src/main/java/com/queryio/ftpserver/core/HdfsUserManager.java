package com.queryio.ftpserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.util.BaseProperties;
import org.apache.ftpserver.util.IoUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import com.queryio.common.DFSMap;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.ftpserver.dao.UserGroupDAO;
import com.queryio.ftpserver.requestprocessor.LoginRequest;
import com.queryio.ftpserver.userinfo.UserInfoContainer;

/**
 * Extended AbstractUserManager to use HdfsUser
 */
public class HdfsUserManager extends AbstractUserManager {

	private static final Logger LOG = Logger.getLogger(HdfsUserManager.class);

	private final static String PREFIX = "ftpserver.user.";

	private BaseProperties userDataProp;

	private File userDataFile = null;

	private boolean isConfigured = false;

	public HdfsUserManager() {
		super(null, null);
	}

	public HdfsUserManager(String adminName, PasswordEncryptor passwordEncryptor) {
		super(adminName, passwordEncryptor);
	}

	/**
	 * Retrieve the file used to load and store users
	 *
	 * @return The file
	 */
	public File getFile() {
		return userDataFile;
	}

	/**
	 * Set the file used to store and read users. Must be set before
	 * {@link #configure()} is called.
	 *
	 * @param propFile
	 *            A file containing users
	 */
	public void setFile(File propFile) {
		if (isConfigured) {
			throw new IllegalStateException("Must be called before configure()");
		}

		this.userDataFile = propFile;
	}

	/**
	 * Lazy init the user manager
	 */
	private void lazyInit() {
		if (!isConfigured) {
			configure();
		}
	}

	/**
	 * Configure user manager.
	 */
	public void configure() {
		isConfigured = true;
		try {
			userDataProp = new BaseProperties();

			if (userDataFile != null && userDataFile.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(userDataFile);
					userDataProp.load(fis);
				} finally {
					IoUtils.close(fis);
				}
			}
		} catch (IOException e) {
			throw new FtpServerConfigurationException(
					"Error loading user data file : " + userDataFile.getAbsolutePath(), e);
		}
	}

	/**
	 * Get all user names.
	 */
	public synchronized String[] getAllUserNames() {

		lazyInit();

		Connection connection = null;

		ArrayList users = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			users = UserGroupDAO.getAllUserNames(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllUserNames() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		Collections.sort(users);

		String[] userList = (String[]) users.toArray(new String[0]);

		return userList;
	}

	/**
	 * Load user data.
	 */
	public synchronized User getUserByName(String userName) {
		lazyInit();

		String baseKey = PREFIX + '.';
		HdfsUser user = new HdfsUser();
		user.setName(userName);
		user.setDefaultGroup(UserInfoContainer.getDefaultGroupForUser(userName));

		user.setEnabled(userDataProp.getBoolean(baseKey + ATTR_ENABLE, true));
		user.setHomeDirectory(userDataProp.getProperty(baseKey + ATTR_HOME, "/"));

		ArrayList<String> groups = new ArrayList<String>();
		groups.add(QueryIOConstants.DEFAULT_GROUP_NAME);
		user.setGroups(groups);

		List<Authority> authorities = new ArrayList<Authority>();

		if (userDataProp.getBoolean(baseKey + ATTR_WRITE_PERM, false)) {
			authorities.add(new WritePermission());
		}

		// int maxLogin = userDataProp.getInteger(baseKey +
		// ATTR_MAX_LOGIN_NUMBER, 0);
		// int maxLoginPerIP = userDataProp.getInteger(baseKey +
		// ATTR_MAX_LOGIN_PER_IP, 0);

		authorities.add(new ConcurrentLoginPermission(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// int uploadRate = userDataProp.getInteger(baseKey +
		// ATTR_MAX_UPLOAD_RATE, 0);
		// int downloadRate = userDataProp.getInteger(baseKey +
		// ATTR_MAX_DOWNLOAD_RATE, 0);

		authorities.add(new TransferRatePermission(Integer.MAX_VALUE, Integer.MAX_VALUE));

		user.setAuthorities(authorities);

		user.setMaxIdleTime(userDataProp.getInteger(baseKey + ATTR_MAX_IDLE_TIME, 0));

		return user;
	}

	/**
	 * User existance check
	 */
	public synchronized boolean doesExist(String name) {
		lazyInit();

		String key = PREFIX + name + '.' + ATTR_HOME;
		return userDataProp.containsKey(key);
	}

	/**
	 * User authenticate method
	 */
	public synchronized User authenticate(Authentication authentication) throws AuthenticationFailedException {
		lazyInit();

		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

			String user = upauth.getUsername();
			String password = upauth.getPassword();

			if (user == null) {
				throw new AuthenticationFailedException("Authentication failed");
			}

			if (password == null) {
				password = "";
			}

			LOG.debug("Authenticating user: " + user);

			if (authenticate(user, password)) {
				LOG.debug("User authenticated successfully");
				User us = getUserByName(user);
				LOG.debug("User: " + us.getName());
				return us;
			} else {
				throw new AuthenticationFailedException("Authentication failed");
			}

		} else if (authentication instanceof AnonymousAuthentication) {
			if (doesExist("anonymous")) {
				return getUserByName("anonymous");
			} else {
				throw new AuthenticationFailedException("Authentication failed");
			}
		} else {
			throw new IllegalArgumentException("Authentication not supported by this user manager");
		}
	}

	private boolean authenticate(String username, String password) {
		HdfsUser user = new HdfsUser();
		user.setName(username);
		user.setPassword(password);
		user.setDefaultGroup(UserInfoContainer.getDefaultGroupForUser(username));

		LOG.debug("Processing authenticate request");

		LoginRequest request = new LoginRequest(user, null);
		FileSystem fs = null;
		try {
			fs = (FileSystem) request.process();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.debug("authenticate(): username: " + username + ", password: " + password + ", success: " + fs != null);
		LOG.debug("dfs: " + fs);

		if (fs != null) {
			DFSMap.addDFS(username, fs);
			return true;
		}

		return false;
	}

	/**
	 * Close the user manager - remove existing entries.
	 */
	public synchronized void dispose() {
		if (userDataProp != null) {
			userDataProp.clear();
			userDataProp = null;
		}
	}

	@Override
	public void delete(String arg0) throws FtpException {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(User user) throws FtpException {
		// TODO Auto-generated method stub
	}
}
