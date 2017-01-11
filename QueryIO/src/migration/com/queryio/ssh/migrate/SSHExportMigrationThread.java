package com.queryio.ssh.migrate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.security.UserGroupInformation;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSInputStream;

public class SSHExportMigrationThread extends Thread {
	private long successCount = 0;
	private long totalFiles = 0;
	private long failCount = 0;
	private long unitCount = 0;
	private MigrationInfo migrationInfo;

	private FileSystem dfs;

	boolean flag;

	boolean overwrite;

	static Session session = null;

	private String host;
	private int port;
	private String username;
	private String password;
	private String path;
	private boolean isSSHKey = false;

	private String loginUser;
	private String loginUserGroup;

	Configuration conf = null;

	public SSHExportMigrationThread(String user, String group, MigrationInfo migrationInfo, String host, int port,
			String username, String password, String path, boolean isSSHKey, Configuration conf, boolean overwrite)
			throws Exception {
		this.loginUser = user;
		this.loginUserGroup = group;

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.path = path;
		this.isSSHKey = isSSHKey;

		Thread.currentThread().setName(this.loginUser);
		this.migrationInfo = migrationInfo;

		dfs = FileSystem.get(conf);
		dfs.setConf(conf);

		this.conf = conf;
		this.overwrite = overwrite;

		this.connect();

		if (EnvironmentalConstants.isUseKerberos()) {
			Connection connection = null;
			try {
				connection = CoreDBManager.getQueryIODBConnection();

				User us = UserDAO.getUserDetail(connection, user);

				UserGroupInformation.setConfiguration(conf);
				// UserGroupInformation.getLoginUser(us.getUserName(),
				// SecurityHandler.decryptData(us.getPassword()));

				dfs = FileSystem.get(conf);
				dfs.setConf(conf);
			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		} else {
			dfs = QIODFSUtils.getFileSystemAs(user, group, conf);
		}
	}

	@Override
	public void interrupt() {
		flag = false;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_STOPPED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		super.interrupt();

		try {
			dfs.close();
		} catch (IOException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		if (session != null) {
			try {
				session.disconnect();
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(e.getMessage(), e);
			}
		}
	}

	@Override
	public void run() {
		flag = true;
		Connection connection = null;

		try {
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID),
					conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser,
					loginUserGroup);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());

			if (flag) {
				String fileName = migrationInfo.getSourcePath();
				Path sourcePath = new Path(fileName.trim());

				String filepath = fileName.substring(fileName.lastIndexOf("/"), fileName.length());
				Path destinationPath = new Path(path);
				Path importedFilePath = new Path(migrationInfo.getDestinationPath());

				RemoteIterator<LocatedFileStatus> iterator = dfs.listFiles(sourcePath, true);
				while (iterator.hasNext()) {
					totalFiles++;
					iterator.next();
				}

				AppLogger.getLogger().debug("Total files in given Path : " + totalFiles);
				unitCount = (5 * totalFiles) / 100;

				try {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Fetching object: " + destinationPath);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Fetching fileName: " + fileName);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Destination: " + importedFilePath);

					migrationInfo.setProgress(0);
					migrationInfo.setStatus("Migrated " + successCount + " of " + totalFiles + " objects."
							+ (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
					updateMigrationStatusInDB();

					FileStatus fileStatus = dfs.getFileStatus(sourcePath);
					if (fileStatus.isDirectory()) {
						createFolder(migrationInfo.getDestinationPath() + File.separator
								+ fileStatus.getPath().getName().toString());
						exportRecursive(sourcePath, migrationInfo.getDestinationPath() + File.separator
								+ fileStatus.getPath().getName().toString());// ,
																				// fileStatus);
					} else
						exportFile(sourcePath, importedFilePath);
				} catch (Exception e) {
					AppLogger.getLogger()
							.fatal("Could not save file. Source: " + this.path + ", Exception: " + e.getMessage(), e);
					failCount++;
				}
			}

			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		} finally {
			try {
				dfs.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			if (session != null) {
				try {
					session.disconnect();
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug(e.getMessage(), e);
				}
			}
		}
		if (flag) {
			updateMigrationStatusInDB();
		}
	}

	private void updateMigrationStatusInDB() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	private void exportRecursive(Path sourcePath, String folderName) throws Exception {
		FileStatus[] files = dfs.listStatus(sourcePath);

		for (FileStatus file : files) {
			if (file.isDirectory()) {
				createFolder(folderName + File.separator + file.getPath().getName().toString());
				exportRecursive(file.getPath(), folderName + File.separator + file.getPath().getName().toString());
			} else {
				exportFile(file.getPath(), new Path(folderName + File.separator + file.getPath().getName()));
			}
		}
	}

	private void createFolder(String path) {
		try {
			Channel channel = null;
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("mkdir " + path);
			channel.connect();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while creating directory in SSH export.");
		}
	}

	private void exportFile(Path sourcePath, Path importedFilePath) throws Exception {
		Object[] obj;
		InputStream inputStream;
		long fileLength;
		int connectRetry = 0;
		try {
			obj = getObject(sourcePath);

			inputStream = (InputStream) obj[0];
			fileLength = ((Long) obj[1]).longValue();
		} catch (Exception e) {
			Thread.sleep(3000);
			if (connectRetry == 3) {
				failCount++;
				throw e;
			} else {
				try {
					AppLogger.getLogger().fatal("IOException occurred... Exception: " + e.getMessage(), e);

					if (session.isConnected()) {
						AppLogger.getLogger().fatal("Could not export " + sourcePath.getName() + ": " + e.getMessage());
						connectRetry = 0;
						return;
					} else {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Reconnecting to SSH server...");
						this.connect();
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Connection successful");
					}
					connectRetry = 0;
				} catch (Exception ex) {
					connectRetry++;
					AppLogger.getLogger().fatal("Could not connect to SSH server, exception: " + e.getMessage(), e);
				}
				return;
			}
		}

		String command = "scp -t " + importedFilePath;
		executeCommand(command, inputStream, fileLength, importedFilePath, overwrite, migrationInfo.isUnzip());
		successCount++;
		if (totalFiles > 0) {
			migrationInfo.setProgress((successCount * 100 / totalFiles));
		} else {
			migrationInfo.setProgress(0);
		}
		migrationInfo.setStatus("Migrated " + successCount + " of " + totalFiles + " objects."
				+ (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
		if (unitCount != 0 && (successCount + failCount) % unitCount == 0) {
			updateMigrationStatusInDB();
		}
	}

	private Object[] getObject(Path path) throws Exception {
		DFSInputStream dfsInputStream = null;
		QIODFSInputStream qioInputStream = null;

		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		dfsInputStream = (DFSInputStream) fs.getClient().open(path.toUri().getPath());
		HdfsFileStatus fileStatus = fs.getClient().getFileInfo(path.toUri().getPath());
		try {
			qioInputStream = new QIODFSInputStream(dfsInputStream, fs, path.toUri().getPath());
		} catch (Exception e) {
			if (dfsInputStream != null) {
				dfsInputStream.close();
			}
			throw e;
		}

		return new Object[] { new BufferedInputStream(qioInputStream), fileStatus.getLen() };
	}

	private void executeCommand(String commandToExecute, InputStream fis, long fileLength, Path filePath,
			boolean overwrite, boolean unzip) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Command: " + commandToExecute);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("FilePath: " + filePath);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("FileLength: " + fileLength);

		Channel channel = null;
		OutputStream out = null;
		InputStream in = null;

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(commandToExecute);
			channel.connect();
			out = channel.getOutputStream();
			in = channel.getInputStream();

			if (checkAck(in) != 0) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error in IN");
			}

			String fileName = filePath.toString();

			String command = "C0644 " + fileLength + " ";
			if (fileName.lastIndexOf('/') >= 0) {
				command += fileName.substring(fileName.lastIndexOf('/') + 1);
			} else {
				command += fileName;
			}
			command += "\n";

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Command1: " + command);

			out.write(command.getBytes());
			out.flush();

			if (checkAck(in) != 0) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error in IN2");
			}

			// send a content of this.sourceFile

			fis = new BufferedInputStream(fis);

			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
				// out.flush();
			}
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			if (checkAck(in) != 0) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error in IN3");
			}

		} catch (IOException e) {
			AppLogger.getLogger().fatal("Error running build", e);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error running build", e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ee) {
				AppLogger.getLogger().fatal(ee);
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException ee) {
					AppLogger.getLogger().fatal(ee);
				} finally {
					try {
						if (in != null)
							in.close();
					} catch (IOException ee) {
						AppLogger.getLogger().fatal(ee);
					} finally {
						try {
							channel.disconnect();
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("RemoteScpTo complete");
						} catch (Exception ee) {
							AppLogger.getLogger().fatal(ee);
						}
					}
				}
			}
		}
	}

	private static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');

			if (b == 1) { // error
				AppLogger.getLogger().fatal(sb.toString());
			}

			if (b == 2) { // fatal error
				AppLogger.getLogger().fatal(sb.toString());
			}
		}
		return b;
	}

	private void write(InputStream is, Path filePath) throws Exception {
		ChannelSftp sftpChannel = null;
		sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();

		// copying a file
		String file = filePath.toString().substring(filePath.toString().lastIndexOf("/"), filePath.toString().length());

		sftpChannel.put(is, migrationInfo.getDestinationPath() + file);
	}

	private Path getImportedFilePath(String bucketName, String path) {
		if (bucketName.endsWith("/"))
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		if (!bucketName.startsWith("/"))
			bucketName = "/" + bucketName;
		path = bucketName + path;
		path = path.replaceAll("//", "/");
		Path objectPath = new Path(path);

		return objectPath;
	}

	public void connect() throws Exception {
		JSch jsch = new JSch();
		session = jsch.getSession(this.username, this.host, this.port);

		if (this.isSSHKey)
			jsch.addIdentity(this.password);
		else
			session.setPassword(this.password);

		session.setConfig("StrictHostKeyChecking", "no");

		session.connect();

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Connected to " + this.host);
	}

	public static void main(String args[]) {
		Configuration conf = null;

		try {
			// new SSHExportMigrationThread("a", "queryio", null,
			// "192.168.0.23", 22, "admin", "App4ever#", "a", conf, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
