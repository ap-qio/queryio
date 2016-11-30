package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.datasources.DataBaseDataSource;
import com.queryio.core.datasources.DataConnection;
import com.queryio.core.datasources.EmailDataSource;
import com.queryio.core.datasources.FTPDataSource;
import com.queryio.core.datasources.HDFSDataSource;
import com.queryio.core.datasources.HTTPDataSource;
import com.queryio.core.datasources.S3DataSource;
import com.queryio.core.datasources.SFTPDataSource;
import com.queryio.core.datasources.SSHDataSource;
import com.queryio.database.migrate.DBAcivator;

@SuppressWarnings({ "PMD.AvoidUsingShortType", "PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal" })
public class DataSourceDAO {
	public static short getDataConnectionType(final Connection connection, final String id) throws Exception {
		short type = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_DATA_CONNECTION);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				type = rs.getShort(ColumnConstants.COL_DATA_CONNECTION_TYPE);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return type;
	}

	public static void addDataConnection(final Connection connection, final String id, final short connectionType)
			throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_DATA_CONNECTION);

			ps.setString(1, id);
			ps.setShort(2, connectionType);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addFTPDataSource(final Connection connection, final FTPDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_FTP_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getHost());
			ps.setInt(3, ds.getPort());
			ps.setString(4, ds.getUsername());
			ps.setString(5, SecurityHandler.encryptData(ds.getPassword()));

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addDataBaseDataSource(final Connection connection, final DataBaseDataSource ds)
			throws Exception {

		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_DATABASE_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getDriver());
			ps.setString(3, ds.getConnectionURL());
			ps.setString(4, ds.getUserName());
			ps.setString(5, SecurityHandler.encryptData(ds.getPassword()));
			ps.setString(6, ds.getJarFileName());
			ps.setInt(7, ds.getMaxConnections());
			ps.setInt(8, ds.getMaxIdleConnections());
			ps.setInt(9, ds.getWaitTimeMilliSeconds());

			CoreDBManager.executeUpdateStatement(connection, ps);

		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addSFTPDataSource(final Connection connection, final SFTPDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_SFTP_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getHost());
			ps.setInt(3, ds.getPort());
			ps.setString(4, ds.getUsername());
			ps.setString(5, SecurityHandler.encryptData(ds.getPassword()));

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addHDFSDataSource(final Connection connection, final HDFSDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_HDFS_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getHost());
			ps.setInt(3, ds.getPort());
			ps.setString(4, ds.getGroup());
			ps.setString(5, ds.getUsername());

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addSSHDataSource(final Connection connection, final SSHDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_SSH_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getHost());
			ps.setInt(3, ds.getPort());
			ps.setString(4, ds.getUsername());
			ps.setString(5, SecurityHandler.encryptData(ds.getPassword()));
			ps.setString(6, ds.getKey());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addHTTPDataSource(final Connection connection, final HTTPDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_HTTP_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getBaseURL());
			ps.setString(3, ds.getUserName());
			ps.setString(4, SecurityHandler.encryptData(ds.getPassword()));

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addEmailDataSource(final Connection connection, final EmailDataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_EMAIL_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getEmailAddress());
			ps.setString(3, SecurityHandler.encryptData(ds.getPassword()));
			ps.setString(4, ds.getMailServerAddress());
			ps.setString(5, ds.getAccountName());
			ps.setString(6, ds.getProtocol());
			ps.setString(7, ds.getSocketType());
			ps.setInt(8, ds.getPort());
			ps.setLong(9, ds.getConnectionTimeOut());
			ps.setLong(10, ds.getReadTimeOut());

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void addS3DataSource(final Connection connection, final S3DataSource ds) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_S3_DATASOURCE);

			ps.setString(1, ds.getId());
			ps.setString(2, ds.getAccessKey());
			ps.setString(3, SecurityHandler.encryptData(ds.getSecretAccessKey()));

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static FTPDataSource getFTPDataSource(final Connection connection, String id) throws Exception {
		FTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_FTP_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new FTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_FTP_DATASOURCE_PORT));
				ds.setUsername(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_PASSWORD)));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static SFTPDataSource getSFTPDataSource(final Connection connection, String id) throws Exception {
		SFTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_SFTP_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new SFTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_SFTP_DATASOURCE_PORT));
				ds.setUsername(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_PASSWORD)));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static HDFSDataSource getHDFSDataSource(final Connection connection, String id) throws Exception {
		HDFSDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HDFS_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new HDFSDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_HDFS_DATASOURCE_PORT));
				ds.setGroup(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_GROUP));
				ds.setUsername(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_USERNAME));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static DataBaseDataSource getDBDataSource(final Connection connection, String id) throws Exception {
		DataBaseDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_GET_DATABASE_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				ds = new DataBaseDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_ID));
				ds.setDriver(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_DRIVER_CLASS));
				ds.setConnectionURL(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_CONNECTION_URL));
				ds.setUserName(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_USERNAME));
				ds.setPassword(
						SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_PASSWORD)));
				ds.setJarFileName(rs.getString(ColumnConstants.COL_DATABASE_DATASOURCE_DRIVER_JAR));
				ds.setTableNames(DBAcivator.getAllTableNames(ds.getDriver(), ds.getConnectionURL(), ds.getUserName(),
						ds.getPassword(), ds.getJarFileName()));
				ds.setMaxConnections(rs.getInt(ColumnConstants.COL_DATABASE_DATASOURCE_MAX_CONNECTIONS));
				ds.setMaxIdleConnections(rs.getInt(ColumnConstants.COL_DATABASE_DATASOURCE_MAX_IDLE_CONNECTIONS));
				ds.setWaitTimeMilliSeconds(rs.getInt(ColumnConstants.COL_DATABASE_DATASOURCE_MAX_WAITTIME));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static SSHDataSource getSSHDataSource(final Connection connection, String id) throws Exception {
		SSHDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_SSH_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new SSHDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_SSH_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_SSH_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_SSH_DATASOURCE_PORT));
				ds.setUsername(rs.getString(ColumnConstants.COL_SSH_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_SSH_DATASOURCE_PASSWORD)));
				ds.setKey(rs.getString(ColumnConstants.COL_SSH_DATASOURCE_KEY));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static HTTPDataSource getHTTPDataSource(final Connection connection, String id) throws Exception {
		HTTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HTTP_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new HTTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_ID));
				ds.setBaseURL(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_BASEURL));
				ds.setUserName(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_PASSWORD)));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static EmailDataSource getEmailDataSource(final Connection connection, String id) throws Exception {
		EmailDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_EMAIL_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new EmailDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_ID));
				ds.setEmailAddress(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_EMAILADDRESS));
				ds.setPassword(
						SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_PASSWORD)));
				ds.setMailServerAddress(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_MAILSERVERADDRESS));
				ds.setProtocol(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_PROTOCOL));
				ds.setAccountName(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_ACCOUNTNAME));
				ds.setPort(rs.getInt(ColumnConstants.COL_EMAIL_DATASOURCE_PORT));
				ds.setSocketType(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_SOCKET));
				ds.setConnectionTimeOut(rs.getLong(ColumnConstants.COL_EMAIL_DATASOURCE_CONNECTIONTIMEOUT));
				ds.setReadTimeOut(rs.getLong(ColumnConstants.COL_EMAIL_DATASOURCE_READTIMEOUT));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static S3DataSource getS3DataSource(final Connection connection, String id) throws Exception {
		S3DataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_S3_DATASOURCE);
			ps.setString(1, id);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				ds = new S3DataSource();
				ds.setId(rs.getString(ColumnConstants.COL_S3_DATASOURCE_ID));
				ds.setAccessKey(rs.getString(ColumnConstants.COL_S3_DATASOURCE_ACCESS_KEY));
				ds.setSecretAccessKey(
						SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_S3_DATASOURCE_SECERT_ACCESS_KEY)));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return ds;
	}

	public static ArrayList getALLFTPDataSources(final Connection connection) throws Exception {
		FTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_FTP_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new FTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_FTP_DATASOURCE_PORT));
				ds.setUsername(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_FTP_DATASOURCE_PASSWORD)));

				dataSources.add(ds);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static ArrayList getALLSFTPDataSources(final Connection connection) throws Exception {
		SFTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_SFTP_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new SFTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_SFTP_DATASOURCE_PORT));
				ds.setUsername(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_SFTP_DATASOURCE_PASSWORD)));

				dataSources.add(ds);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static ArrayList getALLHDFSDataSources(final Connection connection) throws Exception {
		HDFSDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_HDFS_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new HDFSDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_ID));
				ds.setHost(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_HOST));
				ds.setPort(rs.getInt(ColumnConstants.COL_HDFS_DATASOURCE_PORT));
				ds.setGroup(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_GROUP));
				ds.setUsername(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_USERNAME));

				dataSources.add(ds);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static ArrayList getALLSSHDataSources(final Connection connection) throws Exception {
		HDFSDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		/*
		 * try { ps = DatabaseFunctions.getPreparedStatement(connection,
		 * QueryConstants.GET_ALL_HDFS_DATASOURCES); rs =
		 * CoreDBManager.getQueryResultsForPreparedStatement(ps);
		 * 
		 * while (rs.next()) { ds = new HDFSDataSource();
		 * ds.setId(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_ID));
		 * ds.setHost(rs.getString(ColumnConstants.COL_HDFS_DATASOURCE_HOST));
		 * ds.setPort(rs.getInt(ColumnConstants.COL_HDFS_DATASOURCE_PORT));
		 * 
		 * dataSources.add(ds); } } finally {
		 * DatabaseFunctions.closePreparedStatement(ps);
		 * DatabaseFunctions.closeResultSet(rs); }
		 */

		return dataSources;
	}

	public static ArrayList getALLHTTPDataSources(final Connection connection) throws Exception {
		HTTPDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_HTTP_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new HTTPDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_ID));
				ds.setBaseURL(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_BASEURL));
				ds.setUserName(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_USERNAME));
				ds.setPassword(SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_HTTP_DATASOURCE_PASSWORD)));

				dataSources.add(ds);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static ArrayList getALLEmailDataSources(final Connection connection) throws Exception {
		EmailDataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_EMAIL_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new EmailDataSource();
				ds.setId(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_ID));
				ds.setEmailAddress(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_EMAILADDRESS));
				ds.setPassword(
						SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_PASSWORD)));
				ds.setMailServerAddress(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_MAILSERVERADDRESS));
				ds.setProtocol(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_PROTOCOL));
				ds.setAccountName(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_ACCOUNTNAME));
				ds.setPort(rs.getInt(ColumnConstants.COL_EMAIL_DATASOURCE_PORT));
				ds.setSocketType(rs.getString(ColumnConstants.COL_EMAIL_DATASOURCE_SOCKET));
				ds.setConnectionTimeOut(rs.getLong(ColumnConstants.COL_EMAIL_DATASOURCE_CONNECTIONTIMEOUT));
				ds.setReadTimeOut(rs.getLong(ColumnConstants.COL_EMAIL_DATASOURCE_READTIMEOUT));

				dataSources.add(ds);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static ArrayList getALLDataConnections(final Connection connection) throws Exception {
		DataConnection dc = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataConnections = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_DATA_CONNECTIONS);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				dc = new DataConnection();
				dc.setId(rs.getString(ColumnConstants.COL_DATA_CONNECTION_ID));
				dc.setType(rs.getShort(ColumnConstants.COL_DATA_CONNECTION_TYPE));

				dataConnections.add(dc);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataConnections;
	}

	public static ArrayList getALLS3DataSources(final Connection connection) throws Exception {
		S3DataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList dataSources = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_S3_DATASOURCES);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ds = new S3DataSource();
				ds.setId(rs.getString(ColumnConstants.COL_S3_DATASOURCE_ID));
				ds.setAccessKey(rs.getString(ColumnConstants.COL_S3_DATASOURCE_ACCESS_KEY));
				ds.setSecretAccessKey(
						SecurityHandler.decryptData(rs.getString(ColumnConstants.COL_S3_DATASOURCE_SECERT_ACCESS_KEY)));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return dataSources;
	}

	public static void deleteDataConnection(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_DATA_CONNECTION);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteFTPDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_FTP_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteSFTPDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_SFTP_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteHDFSDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_HDFS_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteSSHDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		/*
		 * try { ps = DatabaseFunctions.getPreparedStatement(connection,
		 * QueryConstants.PREPARED_QRY_DELETE_HDFS_DATASOURCE); ps.setString(1,
		 * id);
		 * 
		 * DatabaseFunctions.executeUpdateStatement(ps); } finally {
		 * DatabaseFunctions.closePreparedStatement(ps); }
		 */
	}

	public static void deleteHTTPDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_HTTP_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteEmailDataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_DELETE_EMAIL_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteS3DataSource(final Connection connection, String id) throws Exception {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_S3_DATASOURCE);
			ps.setString(1, id);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
}
