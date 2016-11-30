package com.queryio.core.requestprocessor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.NumberFormat;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.stream.util.QIODFSInputStream;

public class GetFileRequest extends RequestProcessorCore {
	MigrationInfo migrationInfo = new MigrationInfo();
	private String nameNodeId;
	private String fsDefaultName;
	private long availableBytes;
	private long writtenBytes;
	private FileSystem dfs;
	String errorMessage = null;
	private HttpServletResponse res;
	private String mimeType;

	public GetFileRequest(String user, Path path, String nameNodeId, String fsDefaultName, HttpServletResponse res,
			String mimeType) {
		super(user, null, path);
		this.fsDefaultName = fsDefaultName;
		this.nameNodeId = nameNodeId;
		this.res = res;
		this.mimeType = mimeType;
	}

	public void process() throws Exception {
		this.successful = false;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("GetFileRequest, user: " + this.user);

		Connection connection = null;
		OutputStream os = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			if (EnvironmentalConstants.isUseKerberos()) {
				User us = UserDAO.getUserDetail(connection, user);

				Configuration conf = ConfigurationManager.getKerberosConfiguration(connection, nameNodeId);

				try {
					UserGroupInformation.setConfiguration(conf);
					// UserGroupInformation.getLoginUser(us.getUserName(),
					// SecurityHandler.decryptData(us.getPassword()));

					dfs = FileSystem.get(new URI(fsDefaultName), conf);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(),
							e);
					return;
				}
			} else {
				Configuration conf = ConfigurationManager.getConfiguration(connection, nameNodeId);
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}

			DFSInputStream dfsInputStream = null;
			InputStream qioInputStream = null;

			try {
				this.availableBytes = dfs.getFileStatus(this.path).getLen();

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Opening file path : " + this.path);

				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsInputStream = (DFSInputStream) fs.getClient().open(this.path.toUri().getPath());
				try {
					qioInputStream = new QIODFSInputStream(dfsInputStream, fs, this.path.toUri().getPath());
				} catch (Exception e) {
					if (dfsInputStream != null) {
						dfsInputStream.close();
					}
					throw e;
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Opened file");

				res.setContentType(mimeType);

				res.setHeader("Content-Disposition",
						"attachment; filename=\"" + (this.path.toString().toString().indexOf("/") > -1
								? this.path.toString().toString()
										.substring(this.path.toString().toString().lastIndexOf("/") + 1)
								: this.path.toString().toString()) + "\"");

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Writing to stream");

				os = res.getOutputStream();

				writeToStream(connection, qioInputStream, os);
			} finally {
				try {
					if (qioInputStream != null)
						qioInputStream.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}

			this.successful = true;
		} finally {
			try {
				if (dfs != null)
					dfs.close();
				if (os != null)
					os.flush();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error flushing input stream.", e);
			}
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing input stream.", e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public void writeToStream(final Connection connection, InputStream is, OutputStream baos) throws Exception {
		addTask(connection);

		int bufferSize = EnvironmentalConstants.getStreamBufferSize();
		if (bufferSize == 0) {
			bufferSize = 8192;
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Downloading file: " + this.path.toString() + ", size: "
					+ getFormattedStorageSize(this.availableBytes));

		final byte[] readBuffer = new byte[bufferSize];
		int bytesIn = 0;
		while ((bytesIn = is.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			this.writtenBytes += bytesIn;
			updateStatus(connection);
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("File download: " + this.path.toString() + " complete");

		updateStatusCompleted(connection);
	}

	public void updateStatusCompleted(final Connection connection) {
		try {
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setProgress(100);
			MigrationInfoDAO.update(connection, migrationInfo);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	public void updateStatus(final Connection connection) {
		if (this.availableBytes != 0) {
			try {
				migrationInfo.setProgress((this.writtenBytes * 100 / this.availableBytes));
				MigrationInfoDAO.update(connection, migrationInfo);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public void addTask(final Connection connection) throws Exception {
		this.migrationInfo = new MigrationInfo();

		migrationInfo.setSourcePath(this.path.toString());
		migrationInfo.setDestinationPath("N/A");
		migrationInfo.setNamenodeId(this.nameNodeId);
		migrationInfo.setImportType(false);
		migrationInfo.setTitle("Download file " + this.path.getName());
		migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
		migrationInfo.setDataStore(QueryIOConstants.DATASOURCE_LOCAL);
		migrationInfo.setProgress(0);
		migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_INPROGRESS);

		MigrationInfoDAO.insert(connection, migrationInfo);

		this.migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());
	}

	public static String getFormattedStorageSize(long bytes) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		if (bytes < QueryIOConstants.ONE_KB)
			return bytes + " bytes";

		if (bytes < QueryIOConstants.ONE_MB)
			return nf.format(bytes / (double) QueryIOConstants.ONE_KB) + " KB";

		if (bytes < QueryIOConstants.ONE_GB)
			return nf.format(bytes / (double) QueryIOConstants.ONE_MB) + " MB";

		if (bytes < QueryIOConstants.ONE_TB)
			return nf.format(bytes / (double) QueryIOConstants.ONE_GB) + " GB";

		return nf.format(bytes / (float) QueryIOConstants.ONE_TB) + " TB";
	}
}
