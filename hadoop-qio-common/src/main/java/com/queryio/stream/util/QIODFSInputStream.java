package com.queryio.stream.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class QIODFSInputStream extends InputStream {
	InputStream in;

	static String QUERY_START = "SELECT " + EncryptionHandler.COL_COMPRESSION_TYPE + ","
			+ EncryptionHandler.COL_ENCRYPTION_TYPE + " FROM ";
	static String QUERY_END = " WHERE FILEPATH=?";

	public void init(InputStream dfsIn, Configuration conf, String filePath) throws IOException, Exception {
		Map<String, String> metadata = null;

		if (conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID) == null) {
			in = new EncryptionHandler(Cipher.DECRYPT_MODE, false,
					conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY)).getCompressedCipherInputStream(dfsIn,
							EncryptionHandler.COMPRESSION_TYPE_NONE);
		} else {
			metadata = getObjectMetadata(conf, filePath, TableConstants.TABLE_HDFS_METADATA);

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath,
						("DATATAGS_" + UserDefinedTagUtils.getFileExtension(filePath)).toUpperCase());
			}

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath, null);
			}

			if (metadata == null) {
				in = new EncryptionHandler(Cipher.DECRYPT_MODE, false,
						conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
								.getCompressedCipherInputStream(dfsIn, EncryptionHandler.COMPRESSION_TYPE_NONE);
			} else {
				int compressionType = EncryptionHandler
						.getCompressionTypeValue(metadata.get(EncryptionHandler.COL_COMPRESSION_TYPE));
				boolean encryptionType = EncryptionHandler
						.getEncryptionTypeValue(metadata.get(EncryptionHandler.COL_ENCRYPTION_TYPE));

				in = new EncryptionHandler(Cipher.DECRYPT_MODE, encryptionType,
						conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
								.getCompressedCipherInputStream(dfsIn, compressionType);
			}
		}
	}

	public QIODFSInputStream(DFSInputStream dfsIn, DistributedFileSystem fs, String filePath) throws Exception {
		init(dfsIn, fs.getConf(), filePath);
	}

	public QIODFSInputStream(InputStream dfsIn, Configuration conf, String filePath) throws Exception {
		init(dfsIn, conf, filePath);
	}

	public QIODFSInputStream(DFSInputStream dfsIn, String compressionType, String encryptionType,
			DistributedFileSystem fs) throws Exception {
		int compressionTypeValue = EncryptionHandler.getCompressionTypeValue(compressionType);
		boolean encryptionTypeValue = EncryptionHandler.getEncryptionTypeValue(encryptionType);

		in = new EncryptionHandler(Cipher.DECRYPT_MODE, encryptionTypeValue,
				fs.getConf().get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
						.getCompressedCipherInputStream(dfsIn, compressionTypeValue);
	}

	public Map<String, String> getObjectMetadata(Configuration conf, String filePath, String tableName)
			throws Exception {
		Map map = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try {
			connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf, true);

			if (tableName == null) {
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);

				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}

			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, new String[] { "TABLE" });
			boolean found = false;
			while (res.next()) {
				if (res.getString("TABLE_NAME").equalsIgnoreCase(tableName)) {
					found = true;
				}
			}

			if (!found) {
				return null;
			}

			stmt = connection.prepareStatement(QUERY_START + tableName + QUERY_END);

			stmt.setString(1, filePath);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();

			if (rs.next()) {
				map = new HashMap();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i).toUpperCase(), rs.getObject(rsmd.getColumnName(i)));
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(res);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception ex) {
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}

	@Override
	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}
}
