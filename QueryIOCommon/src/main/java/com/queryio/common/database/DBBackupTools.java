package com.queryio.common.database;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.queryio.common.util.AppLogger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@SuppressWarnings("PMD.AvoidUsingShortType")
public class DBBackupTools {
	private static ArrayList<String> getTableNames(final Connection connection, DBTypeProperties props)
			throws SQLException {
		ArrayList<String> tables = new ArrayList<String>();
		DatabaseMetaData md = connection.getMetaData();

		ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
		while (rs.next()) {
			String tableName = rs.getString(3);
			AppLogger.getLogger().debug("tableName : " + tableName);
			if (tableName.equals(TableConstants.TABLE_LOOKUP) || tableName.equals(TableConstants.TABLE_DIRECTORIES))
				continue;
			tables.add(tableName);
		}
		return tables;
	}

	public static List<String> getNSTableNames(final Connection connection) {
		List<String> list = new ArrayList<String>();

		list.add("NS_METADATA");

		return list;
	}

	public static List<String> getMetadataTableNames(final Connection conenction, DBTypeProperties props)
			throws SQLException {
		List<String> list = new ArrayList<String>();

		list.add("HDFS_METADATA");

		ArrayList<String> allTables = getTableNames(conenction, props);
		AppLogger.getLogger().debug("allTables : " + allTables);
		for (int i = 0; i < allTables.size(); i++) {
			if (allTables.get(i).toUpperCase().startsWith("DATATAGS_")) {
				list.add(allTables.get(i));
			}
		}

		return list;
	}

	public static void writeNSMetadata(final Connection connection, String metadataFilePath)
			throws SQLException, IOException {
		List<String> tableNames = getNSTableNames(connection);

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		BufferedOutputStream bos = null;

		try {
			fos = new FileOutputStream(metadataFilePath);
			bos = new BufferedOutputStream(fos);

			XStream xstream = new XStream(new StaxDriver()); // does not require
																// XPP3 library
																// starting with
																// Java 6
			xstream.alias("ns-metadata", NSMetadata.class);

			oos = xstream.createObjectOutputStream(bos);

			Statement stmt = null;
			ResultSet rs = null;

			String table = null;
			NSMetadata metadata = null;
			String queryStart = "SELECT * FROM ";
			for (int i = 0; i < tableNames.size(); i++) {
				table = tableNames.get(i);

				try {
					stmt = DatabaseFunctions.getStatement(connection);
					stmt.setFetchSize(1000);

					rs = DatabaseFunctions.getQueryResultsForStatement(stmt, queryStart + table);

					while (rs.next()) {
						metadata = new NSMetadata();

						metadata.setKey(rs.getString(ColumnConstants.COL_NS_METADATA_KEY));
						metadata.setValue(rs.getString(ColumnConstants.COL_NS_METADATA_VALUE));

						oos.writeObject(metadata);
					}
				} finally {
					try {
						oos.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						DatabaseFunctions.closeSQLObjects(stmt, rs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeHDFSMetadata(final Connection connection, String metadataFilePath, DBTypeProperties props)
			throws SQLException, IOException {
		List<String> tableNames = getMetadataTableNames(connection, props);
		AppLogger.getLogger().debug("tableNames : " + tableNames);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		BufferedOutputStream bos = null;

		try {
			fos = new FileOutputStream(metadataFilePath);
			bos = new BufferedOutputStream(fos, 16 * 1024);

			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("hdfs-metadata", HDFSMetadata.class);

			oos = xstream.createObjectOutputStream(bos);

			Statement stmt = null;
			ResultSet rs = null;

			String table = null;
			HDFSMetadata metadata = null;
			String queryStart = "SELECT * FROM ";
			Timestamp ts = null;
			for (int i = 0; i < tableNames.size(); i++) {
				table = tableNames.get(i);

				try {
					stmt = DatabaseFunctions.getStatement(connection);
					stmt.setFetchSize(1000);

					rs = DatabaseFunctions.getQueryResultsForStatement(stmt, queryStart + table);

					AppLogger.getLogger().debug("Will iterate resultset now for query : " + (queryStart + table));

					while (rs.next()) {
						metadata = new HDFSMetadata();

						metadata.setFilePath(rs.getString(ColumnConstants.COL_TAG_VALUES_FILEPATH));
						ts = rs.getTimestamp(ColumnConstants.COL_TAG_VALUES_ACCESSTIME);
						metadata.setAccessTime(ts != null ? ts.getTime() : -1);
						ts = rs.getTimestamp(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME);
						metadata.setModificationTime(ts != null ? ts.getTime() : -1);
						metadata.setBlocks(rs.getBytes(ColumnConstants.COL_TAG_VALUES_BLOCKS));
						metadata.setBlockSize(rs.getLong(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE));
						metadata.setOwner(rs.getString(ColumnConstants.COL_TAG_VALUES_OWNER));
						metadata.setPermission(rs.getString(ColumnConstants.COL_TAG_VALUES_PERMISSION));
						metadata.setReplication(rs.getShort(ColumnConstants.COL_TAG_VALUES_REPLICATION));
						metadata.setUsergroup(rs.getString(ColumnConstants.COL_TAG_VALUES_USERGROUP));
						metadata.setLength(rs.getLong(ColumnConstants.COL_TAG_VALUES_LENGTH));

						oos.writeObject(metadata);
					}
				} finally {
					try {
						oos.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						DatabaseFunctions.closeSQLObjects(stmt, rs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void databaseToFile(String dbName, String schemaFilePath, String dataFilePath, String backupId)
			throws Exception {
		Connection connection = null;
		DBTypeProperties props = null;
		try {

			AppLogger.getLogger().debug("dbName : " + dbName);

			connection = CoreDBManager.getCustomTagDBConnection(dbName);

			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);

			writeNSMetadata(connection, schemaFilePath);
			writeHDFSMetadata(connection, dataFilePath, props);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void dropBackupTables(Connection connection, String backupId, DBTypeProperties props)
			throws Exception {
		Statement stmt = null;
		try {
			String dropQuery = null;
			ArrayList<String> tableNames = getBackupTableNames(connection, backupId, props);
			for (int i = 0; i < tableNames.size(); i++) {
				dropQuery = "DROP TABLE " + tableNames.get(i);
				CoreDBManager.executeUpdateStatement(connection, stmt, dropQuery);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
		}
	}

	public static ArrayList<String> getBackupTableNames(final Connection connection, String backupId,
			DBTypeProperties props) throws Exception {
		ArrayList<String> tables = new ArrayList<String>();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, props.getDefaultSchema(), backupId + "%", new String[] { "TABLE" });
		while (rs.next()) {
			String tableName = rs.getString(3);
			tables.add(tableName);
		}
		return tables;
	}

	public static void generateRandomData(String url, String driverClassName, String username, String password)
			throws SQLException, ClassNotFoundException {
		String query = "INSERT INTO HDFS_METADATA(FILEPATH, ACCESSTIME, MODIFICATIONTIME, OWNER, USERGROUP, PERMISSION, BLOCKSIZE, REPLICATION, LEN) VALUES "
				+ "(?,?,?,?,?,?,?,?,?)";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			Class.forName(driverClassName);
			connection = DriverManager.getConnection(url, username, password);

			ps = DatabaseFunctions.getPreparedStatement(connection, query);

			for (int i = 0; i < 1000000; i++) {
				ps.setString(1, String.valueOf(System.nanoTime()));
				ps.setTimestamp(2, new Timestamp(System.nanoTime()));
				ps.setTimestamp(3, new Timestamp(System.nanoTime()));
				ps.setString(4, String.valueOf(System.nanoTime()));
				ps.setString(5, String.valueOf(System.nanoTime()));
				ps.setString(6, String.valueOf(System.nanoTime()));
				ps.setLong(7, System.nanoTime());
				ps.setShort(8, (short) i);
				ps.setLong(9, System.nanoTime());

				DatabaseFunctions.executeUpdateStatement(ps);
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static ArrayList<String> test(final Connection connection) throws SQLException {
		ArrayList<String> tables = new ArrayList();
		DatabaseMetaData md = connection.getMetaData();

		ResultSet rs = md.getTables(null, "SELECT current_schema()", "%", new String[] { "TABLE" });
		while (rs.next()) {
			String tableName = rs.getString(3);
			if (tableName.equals(TableConstants.TABLE_LOOKUP) || tableName.equals(TableConstants.TABLE_DIRECTORIES))
				continue;
			tables.add(tableName);
		}
		return tables;
	}

	public static void main(String[] args) throws Exception {

		// System.out.println("Starting random data generation at: " + new
		// Timestamp(System.currentTimeMillis()));
		// generateRandomData("jdbc:hsqldb:hsql://192.168.0.12:5681/MetaStore",
		// "org.hsqldb.jdbcDriver", "ADMIN", "ADMIN");
		// System.out.println("Completed random data generation at: " + new
		// Timestamp(System.currentTimeMillis()));

		// System.out.println("Starting DB to file migration at: " + new
		// Timestamp(System.currentTimeMillis()));
		// databaseToFile("jdbc:hsqldb:hsql://192.168.0.22:5432/MetaStore",
		// "org.hsqldb.jdbcDriver", "ADMIN", "ADMIN",
		// "/AppPerfect/temp/480411042014/backup_22_5_2014_19_34_09/ns-metadata.xml",
		// "/AppPerfect/temp/480411042014/backup_22_5_2014_19_34_09/hdfs-metadata.xml",
		// "backup_1");
		// System.out.println("Completed DB to file migration at: " + new
		// Timestamp(System.currentTimeMillis()));

		// String namespaceId = null;
		// String blockPoolId = null;
		//
		// NSMetadataReader nsMetadataReader = new
		// NSMetadataReader("/AppPerfect/ns-metadata.xml");
		// nsMetadataReader.start();
		//
		// NSMetadata nsMetadata = null;
		// while((nsMetadata = nsMetadataReader.next())!=null) {
		// if(nsMetadata.getKey().equals(QueryIOConstants.NS_NAMESPACE_ID)) {
		// namespaceId = nsMetadata.getValue();
		// } else
		// if(nsMetadata.getKey().equals(QueryIOConstants.NS_BLOCKPOOL_ID)) {
		// blockPoolId = nsMetadata.getValue();
		// }
		// }
		//
		// System.out.println("NamespaceId: " + namespaceId);
		// System.out.println("BlockPoolId: " + blockPoolId);
		//
		// nsMetadataReader.close();
		//
		// HDFSMetadataReader hdfsMetadataReader = new
		// HDFSMetadataReader("/AppPerfect/hdfs-metadata.xml");
		// hdfsMetadataReader.start();
		//
		// HDFSMetadata hdfsMetadata = null;
		// while((hdfsMetadata = hdfsMetadataReader.next())!=null) {
		// Path path = new Path(hdfsMetadata.getFilePath());
		//
		// long mTime = hdfsMetadata.getModificationTime();
		// long aTime = hdfsMetadata.getAccessTime();
		// String owner = hdfsMetadata.getOwner();
		// String group = hdfsMetadata.getUsergroup();
		// String permStr = hdfsMetadata.getPermission();
		// String perm = "";
		// int count = 0;
		// count = permStr.substring(0, 1).equals("-") ? 0 : 4;
		// count = permStr.substring(1, 2).equals("-") ? 0 : 2;
		// count = permStr.substring(2, 3).equals("-") ? 0 : 1;
		// perm += count;
		// count = 0;
		// count = permStr.substring(3, 4).equals("-") ? 0 : 4;
		// count = permStr.substring(4, 5).equals("-") ? 0 : 2;
		// count = permStr.substring(5, 6).equals("-") ? 0 : 1;
		// perm += count;
		// count = 0;
		// count = permStr.substring(6, 7).equals("-") ? 0 : 4;
		// count = permStr.substring(7, 8).equals("-") ? 0 : 2;
		// count = permStr.substring(8).equals("-") ? 0 : 1;
		// perm += count;
		//
		// FsPermission permission = new FsPermission(Short.parseShort(perm));
		// long blockSize = hdfsMetadata.getBlockSize();
		// short replication = hdfsMetadata.getReplication();
		// long len = hdfsMetadata.getLength();
		//
		// FileStatus fs = new FileStatus(len, false, replication, blockSize,
		// mTime, aTime, permission, owner, group, path);
		//
		// System.out.println("Path: " + fs.getPath() + ", size: " +
		// fs.getLen());
		// }
		//
		// hdfsMetadataReader.close();
	}
}
