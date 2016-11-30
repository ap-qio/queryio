package com.queryio.amazon.migrate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.GetResponse;
import com.amazon.s3.ListBucketResponse;
import com.amazon.s3.ListEntry;
import com.amazon.s3.S3Object;
import com.amazon.s3.Utils;
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
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.stream.util.QIODFSOutputStream;

public class AmazonImportMigrationThread extends Thread {
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private AWSAuthConnection conn;
	private FileSystem dfs;

	boolean flag;

	boolean overwrite;
	String fsDefaultName;

	String user;
	String group;

	Configuration conf;

	public AmazonImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String accessKey,
			String secureAccessKey, Configuration conf, boolean overwrite) throws Exception {
		super(user);

		this.user = user;
		this.group = group;

		Thread.currentThread().setName(user);
		// TagParserConfigManager.init();
		this.migrationInfo = migrationInfo;
		conn = new AWSAuthConnection(accessKey, secureAccessKey, migrationInfo.isSecure(), Utils.DEFAULT_HOST,
				CallingFormat.getSubdomainCallingFormat());

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

		this.conf = conf;

		this.overwrite = overwrite;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Migration NN ID: " + migrationInfo.getNamenodeId());
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
			if (dfs != null)
				dfs.close();
		} catch (IOException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		flag = true;
		Connection connection = null;
		try {
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), fsDefaultName,
					migrationInfo.getDestinationPath(), user, group);

			int firstSlashIndex = migrationInfo.getSourcePath().indexOf("/");
			String bucketName = null;
			String innerKey = null;
			if (firstSlashIndex != -1) {
				bucketName = migrationInfo.getSourcePath().substring(0, firstSlashIndex);
				innerKey = migrationInfo.getSourcePath().substring(firstSlashIndex);
			} else {
				bucketName = migrationInfo.getSourcePath();
				innerKey = null;
			}
			List listEntryList = getAllKeys(bucketName, innerKey);

			if (AppLogger.getLogger().isDebugEnabled()) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Objects to import");
				for (int i = 0; i < listEntryList.size(); i++) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Import item #" + (i + 1) + " : " + listEntryList.get(i));
				}
			}

			int failCount = 0;
			int successCount = 0;

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
						.debug("size of bucket " + migrationInfo.getDestinationPath() + " " + listEntryList.size());
			unitCount = 5 / 100 * listEntryList.size();
			if (unitCount == 0)
				unitCount = 1;

			S3Object s3Object;
			Map metadata;

			ArrayList<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();

			for (int i = 0; i < listEntryList.size(); i++) {
				if (listEntryList.get(i).toString().endsWith("/"))
					continue;

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
					ListEntry listEntry = (ListEntry) listEntryList.get(i);

					try {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("getting object for key " + listEntry.key);
						try {
							s3Object = getObject(bucketName, listEntry.key);
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
							failCount++;
							throw e;
						}
						metadata = s3Object.metadata;

						tags.clear();
						Iterator iter = metadata.keySet().iterator();
						String key;
						while (iter.hasNext()) {
							key = (String) iter.next();
							tags.add(new UserDefinedTag(key, metadata.get(key)));
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Metadata: " + key + " : " + metadata.get(key));
						}

						byte[] data = s3Object.data;

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("copying contents to hdfs for key " + listEntry.key);

						try {
							createObject(migrationInfo.getDestinationPath(), listEntry.key,
									new ByteArrayInputStream(data), tags, overwrite, migrationInfo.isUnzip());
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
							try {
								dfs.delete(new Path(migrationInfo.getDestinationPath()), true);
							} catch (IOException ex) {
								AppLogger.getLogger().fatal(ex.getMessage(), ex);
							}
							failCount++;
							throw e;
						}

						successCount++;
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger()
									.debug("File imported from amazon, path: " + migrationInfo.getDestinationPath());
					} catch (Exception e) {
						AppLogger.getLogger().fatal("Could not save file. Exception: " + e.getMessage(), e);
					}

					migrationInfo.setProgress((successCount * 100 / listEntryList.size()));
					migrationInfo.setStatus("Migrated " + successCount + " of " + listEntryList.size() + " objects."
							+ (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
				}
			}
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		} finally {
			try {
				if (dfs != null)
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

	private List getAllKeys(String bucketName, String innerKey) throws Exception {
		ListBucketResponse listBucketResponse = conn.listBucket(bucketName, innerKey, null, null, null);
		if (listBucketResponse.connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("couldn't list bucket: expected " + HttpURLConnection.HTTP_OK + " but got "
					+ listBucketResponse.connection.getResponseCode() + " Response Message: "
					+ listBucketResponse.connection.getResponseMessage());
		}
		return listBucketResponse.entries;
	}

	private byte[] getObjectData(String bucketName, String key) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("getting object for key " + key);
		GetResponse getResponse = conn.get(bucketName, key, null);
		if (getResponse.connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("couldn't list bucket: expected " + HttpURLConnection.HTTP_OK + " but got "
					+ getResponse.connection.getResponseCode() + " Response Message: "
					+ getResponse.connection.getResponseMessage());
		}
		return getResponse.object.data;
	}

	private S3Object getObject(String bucketName, String key) throws Exception {
		GetResponse getResponse = conn.get(bucketName, key, null);
		if (getResponse.connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("couldn't list bucket: expected " + HttpURLConnection.HTTP_OK + " but got "
					+ getResponse.connection.getResponseCode() + " Response Message: "
					+ getResponse.connection.getResponseMessage());
		}
		return getResponse.object;
	}

	private void createFolder(String folderName) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("creating folder " + folderName);
		Path path = new Path("/", folderName);
		dfs.mkdirs(path);
		dfs.setOwner(path, this.user, this.group);
	}

	private void createObject(String folderName, String key, InputStream inputStream, ArrayList<UserDefinedTag> tags,
			boolean overwrite, boolean unzip) throws Exception {
		Path objectPath = new Path(folderName, key);

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Creating object: " + objectPath.toString());

		if (unzip && StaticUtilities.getFileExtension(objectPath.toString()).equalsIgnoreCase("ZIP")) {
			objectPath = new Path(
					objectPath.toString().replace("." + StaticUtilities.getFileExtension(objectPath.toString()), ""));

			ZipInputStream stream = null;
			DFSOutputStream dfsOutputStream = null;
			QIODFSOutputStream qioOutputStream = null;
			try {
				stream = new ZipInputStream(inputStream);

				ZipEntry entry = stream.getNextEntry();
				String fileName;
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				while (entry != null) {
					fileName = entry.getName();
					if (!entry.isDirectory()) {
						try {
							Path path = new Path(objectPath, fileName);
							dfsOutputStream = (DFSOutputStream) fs.getClient().create(path.toUri().getPath(), true);

							try {
								qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream,
										migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null,
										path.toUri().getPath());
								if (tags != null && tags.size() > 0)
									qioOutputStream.addTags(tags);
							} catch (Exception e) {
								if (dfsOutputStream != null) {
									dfsOutputStream.close();
								}
								throw e;
							}
							dfs.setOwner(path, this.user, this.group);
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
						dfs.setOwner(objectPath, this.user, this.user);
					}

					stream.closeEntry();
					entry = stream.getNextEntry();
				}
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
			QIODFSOutputStream qioOutputStream = null;
			try {
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true);

				try {
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(),
							migrationInfo.getEncryptionType(), null, objectPath.toUri().getPath());
					if (tags != null && tags.size() > 0)
						qioOutputStream.addTags(tags);
				} catch (Exception e) {
					if (dfsOutputStream != null) {
						dfsOutputStream.close();
					}
					throw e;
				}
				dfs.setOwner(objectPath, this.user, this.group);
				writeToStream(inputStream, qioOutputStream);
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

	public long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while ((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}

	// public static void main(String[] args) throws IOException{
	// Configuration config = new Configuration();
	// config.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY,
	// "hdfs://192.168.0.12:9000");
	//
	// AWSAuthConnection connection = new
	// AWSAuthConnection("AKIAIJN3NGFEZZ2SMBHQ",
	// "Vk7/f223kD2miPmzIxfUv4qm0v6K8EEiF5vZQykx", true, Utils.DEFAULT_HOST,
	// CallingFormat.getSubdomainCallingFormat());
	//
	// ListBucketResponse listBucketResponse =
	// connection.listBucket("appperfect-testing-1", null, null, null, null);
	// if(listBucketResponse.connection.getResponseCode() !=
	// HttpURLConnection.HTTP_OK){
	// throw new RuntimeException("couldn't list bucket: expected " +
	// HttpURLConnection.HTTP_OK + " but got " +
	// listBucketResponse.connection.getResponseCode() +" Response Message: " +
	// listBucketResponse.connection.getResponseMessage());
	// }
	// for(int i=0; i<listBucketResponse.entries.size(); i++){
	// if(listBucketResponse.entries.get(i).toString().endsWith("/"))
	// continue;
	// System.out.println(listBucketResponse.entries.get(i));
	// }
	// }

	public static void main(String[] args) throws IOException {
		Configuration config = new Configuration();
		config.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY,
				"s3://AKIAIJN3NGFEZZ2SMBHQ:Vk7/f223kD2miPmzIxfUv4qm0v6K8EEiF5vZQykx");

		FileSystem dfs = FileSystem.get(config);

		FileStatus[] statusList = dfs.listStatus(new Path("/"));
		for (int i = 0; i < statusList.length; i++) {
			// System.out.println(statusList[i].getPath());
		}
	}
}
