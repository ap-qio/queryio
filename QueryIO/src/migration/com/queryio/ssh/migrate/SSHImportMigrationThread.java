package com.queryio.ssh.migrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSOutputStream;

public class SSHImportMigrationThread extends Thread {
	private int unitCount = 10;
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

	public SSHImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String host, int port,
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
			// List filePaths = getAllFilePaths(migrationInfo.getSourcePath());

			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID),
					conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser,
					loginUserGroup);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());

			int count = 1;

			unitCount = 5 / 100 * count;
			if (unitCount == 0)
				unitCount = 1;
			int successCount = 0;
			int failCount = 0;
			int connectRetry = 0;

			for (int i = 0; i < count; i++) {
				if (flag) {
					if (i % unitCount == 0) {
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
					String fileName = this.path.toString();
					String filepath = fileName.substring(fileName.lastIndexOf("/"), fileName.length());

					Path importedFilePath = getImportedFilePath(migrationInfo.getDestinationPath(), filepath);
					try {
						try {
							String command = "scp -f " + fileName;
							executeCommand(command, importedFilePath, overwrite, migrationInfo.isUnzip());
							successCount++;
						} catch (IOException e) {
							if (!(e instanceof AccessControlException)) {
								Thread.sleep(3000);
								if (connectRetry == 3) {
									throw e;
								} else {
									try {
										AppLogger.getLogger()
												.fatal("IOException occurred... Exception: " + e.getMessage(), e);
										if (session.isConnected()) {
											AppLogger.getLogger()
													.fatal("Could not save " + fileName + ": " + e.getMessage());
											connectRetry = 0;
											failCount++;
											continue;
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
										AppLogger.getLogger().fatal(
												"Could not connect to Mail server, exception: " + ex.getMessage(), ex);
									}
									i--;
									continue;
								}
							}
						}
					} catch (Exception e) {
						connectRetry = 0;
						AppLogger.getLogger().fatal(
								"Could not save file. Source: " + this.path + ", exception: " + e.getMessage(), e);
						try {
							dfs.delete(importedFilePath, true);
						} catch (Exception ex) {
							AppLogger.getLogger().fatal(ex.getMessage(), ex);
						}
						failCount++;
					}

					migrationInfo.setProgress((successCount * 100 / count));
					migrationInfo.setStatus("Migrated " + successCount + " of " + count + " objects."
							+ (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
				}
			}
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		} finally {
			if (session != null) {
				try {
					session.disconnect();
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug(e.getMessage(), e);
				}
			}
			try {
				dfs.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		if (flag) {
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
	}

	private void executeCommand(String command, Path objectPath, boolean overwrite, boolean unzip) throws Exception {
		Channel channel = null;
		InputStream inputStream = null;
		OutputStream out = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			out = channel.getOutputStream();
			inputStream = channel.getInputStream();
			channel.connect();

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Creating object: " + objectPath.toString());
			writeOnOutputStream(inputStream, objectPath, out, unzip);
		} catch (AccessControlException e) {
			throw e;
		} catch (JSchException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			channel.disconnect();
		}
	}

	private void writeOnOutputStream(InputStream in, Path objectPath, OutputStream out, boolean unzip)
			throws Exception {
		byte[] buf = new byte[1024];
		try {
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				in.read(buf, 0, 6);
				if (buf[0] != 'C')
					break;

				int filesize = 0;
				while (true) {
					in.read(buf, 0, 1);
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10 + (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("file: " + file);
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				if (unzip && StaticUtilities.getFileExtension(objectPath.toString()).equalsIgnoreCase("ZIP")) {
					objectPath = new Path(objectPath.toString()
							.replace("." + StaticUtilities.getFileExtension(objectPath.toString()), ""));

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("ObjectPath: " + objectPath);

					ZipInputStream stream = null;
					DFSOutputStream dfsOutputStream = null;
					OutputStream qioOutputStream = null;
					try {
						stream = new ZipInputStream(in);

						ZipEntry entry = stream.getNextEntry();
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Entry: " + entry.getName());
						String fileName;
						DistributedFileSystem fs = (DistributedFileSystem) dfs;
						while (entry != null) {
							fileName = entry.getName();
							if (!entry.isDirectory()) {
								try {
									Path path = new Path(objectPath, fileName);
									dfsOutputStream = (DFSOutputStream) fs.getClient().create(path.toUri().toString(),
											true);
									try {
										qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream,
												migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(),
												null, path.toUri().toString());
									} catch (Exception e) {
										if (dfsOutputStream != null) {
											dfsOutputStream.close();
										}
										throw e;
									}

									dfs.setOwner(path, this.loginUser, this.loginUserGroup);

									writeToStream(stream, qioOutputStream);
								} catch (IOException e) {
									throw e;
								} finally {
									if (qioOutputStream != null) {
										try {
											qioOutputStream.close();
										} catch (Exception e) {
											AppLogger.getLogger().fatal(e.getMessage(), e);
										}
									}
								}
							} else {
								dfs.mkdirs(new Path(objectPath, fileName));
								dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
							}

							stream.closeEntry();
							entry = stream.getNextEntry();
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
						throw e;
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}
						}
					}
				} else {
					DFSOutputStream dfsOutputStream = null;
					OutputStream qioOutputStream = null;
					try {
						DistributedFileSystem fs = (DistributedFileSystem) dfs;
						dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true);
						try {
							qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream,
									migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null,
									objectPath.toUri().getPath());
						} catch (Exception e) {
							if (dfsOutputStream != null) {
								dfsOutputStream.close();
							}
							throw e;
						}
						dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
						// writeToStream(in, qioOutputStream);
						// writeOnOutputStream(inputStream, qioOutputStream,
						// out);
						int foo;
						while (true) {
							if (buf.length < filesize)
								foo = buf.length;
							else
								foo = filesize;
							in.read(buf, 0, foo);
							qioOutputStream.write(buf, 0, foo);
							filesize -= foo;
							if (filesize == 0)
								break;
						}
						// qioOutputStream.close();
						byte[] tmp = new byte[1];
						// wait for '\0'
						do {
							in.read(tmp, 0, 1);
						} while (tmp[0] != 0);
						// send '\0'
						buf[0] = 0;
						out.write(buf, 0, 1);
						out.flush();

					} catch (IOException e) {
						throw e;
					} finally {
						try {
							if (qioOutputStream != null) {
								qioOutputStream.close();
							}
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
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

	public long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		long readSoFar = 0;
		while ((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
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
			// new SSHImportMigrationThread("a", "queryio", null,
			// "192.168.0.23", 22, "admin", "App4ever#", "a", conf, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}