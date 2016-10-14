package com.queryio.userdefinedtags.common;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.queryio.common.MetadataConstants;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.TableConstants;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class UserDefinedTagDAO {
	private static final Log LOG = LogFactory.getLog(UserDefinedTagDAO.class);

	private static final String DEFAULT_TABLE = TableConstants.TABLE_HDFS_METADATA;

	public static final int NEGATIVE = -1;

	public static synchronized Map<String, Boolean> getAllColumns(Connection connection,
			String tableName) throws SQLException {

		Map<String, Boolean> result = new HashMap<String, Boolean>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + tableName + " LIMIT 0";

			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			ResultSetMetaData meta = rs.getMetaData();
			int numCol = meta.getColumnCount();

			for (int i = 1; i <= numCol; i++) {

				result.put(meta.getColumnName(i).toUpperCase(), true);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		return result;
	}
	public static Map<String, String> getAllColumnsDataTye(Connection connection,
			String tableName) throws SQLException {

		Map<String, String> result = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM " + tableName + " LIMIT 0";

			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			ResultSetMetaData meta = rs.getMetaData();
			int numCol = meta.getColumnCount();

			for (int i = 1; i <= numCol; i++) {
				result.put(meta.getColumnName(i).toUpperCase(), meta.getColumnTypeName(i));
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		return result;
	}

	public static void addColumn(final Connection connection,
			final String tableName, final String columnName, final String type)
			throws Exception {
		Statement stmt = null;
		try {
			String alterStmt = "ALTER TABLE " + tableName + " ADD COLUMN "
					+ columnName + " " + type;

			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, alterStmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static boolean checkIfTableExists(Connection connection, String tagTableName) throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		ResultSet tables1 = null;

		try {
			// We need to check for table name with lower case in case of
			// POSTGRES
			tables = dbm.getTables(null, null, tagTableName.toLowerCase(), new String[] { "TABLE" });
			if (tables.next()) {
				return true;
			}
			// We need to check for table name with exact case in case of HSQL
			tables1 = dbm.getTables(null, null, tagTableName, new String[] { "TABLE" });
			if (tables1.next()) {
				return true;
			}
		} finally {
			DatabaseFunctions.closeResultSetSilently(tables);
			DatabaseFunctions.closeResultSetSilently(tables1);
		}
		return false;
	}

	public static void createDatabaseTable(Connection connection, DBTypeProperties props,
			String tagTableName, List<ColumnMetadata> columnList) {
		List<String> columnNames = new ArrayList<String>();
		List<String> columnTypes = new ArrayList<String>();
		if (columnList != null && columnList.size() > 0) {
			for (ColumnMetadata columnData : columnList) {
				columnNames.add(columnData.getColumnName());
				columnTypes.add(props.getTypeMap().get(columnData.getColumnSqlDataType()) + (columnData.isSizable() ? " (" + columnData.getSize() +")" : ""));
			}
		}
		createDatabaseTable(connection, tagTableName,
				columnNames.toArray(new String[columnNames.size()]),
				columnTypes.toArray(new String[columnTypes.size()]));
	}

	private static void createDatabaseTable(Connection connection,
			String tableName, String[] colNames, String[] datatypes) {
		StringBuffer insertQuery = new StringBuffer("CREATE TABLE ");
		insertQuery.append(tableName).append(" (");
		for (int i = 0; i < colNames.length; i++) {
			if (i != 0)
				insertQuery.append(", ");
			insertQuery.append(colNames[i]).append(" ").append(datatypes[i]);
		}
		insertQuery.append(")");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, insertQuery.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal("IGNORING " + e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void insertTagValues(Connection connection, DBTypeProperties props, String tableName,
			String filePath, List<UserDefinedTag> tags,
			List<UserDefinedTag> extraTags, final boolean updateDbSchema,
			TableMetadata tableMetadata) throws SQLException {
		
		tableName = tableName.replaceAll("[^a-zA-Z0-9]+", "_");
		tableName = tableName.replace("-", "_");
		tableName = tableName.replace(".", "_");
		tableName = tableName.replace(" ", "_");
		tableName = tableName.toUpperCase();

		Map<String, Boolean> map = null;
		
		if (updateDbSchema) {
			if (!checkIfTableExists(connection, tableName))
				createDatabaseTable(connection, props, tableName,
						tableMetadata.getColumnData());
			map = getAllColumns(connection, tableName);
		}
		
		List<String> colNames = new ArrayList<String>();
		List<Object> tagValueObjs = new ArrayList<Object>();

		if (extraTags != null && extraTags.size() > 0) {
			if (map == null) {
				map = getAllColumns(connection, tableName);
			}
			for (UserDefinedTag tag : extraTags) {
				String columnName = tag.getKey().toUpperCase();
				columnName = columnName.replaceAll("[^a-zA-Z0-9]+", "_");
				columnName = columnName.replace("-", "_");
				columnName = columnName.replace(".", "_");
				columnName = columnName.replace(" ", "_");
				columnName = columnName.toUpperCase();
				if (!colNames.contains(columnName)) {
					if (map.get(columnName) == null) {
						try {
							if(tag.getValue()!=null) {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag
												.getValue().getClass(), props));
							} else {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(MetadataConstants.STRING_WRAPPER_CLASS, props));	
							}
							
						} catch (Exception e) {
							LOG.fatal(e.getMessage(), e);
							continue;
						}
					}
					colNames.add(columnName);
					tagValueObjs.add(tag.getValue());
				}
			}
		}
		
		for (UserDefinedTag tag : tags) {
			String columnName = tag.getKey().toUpperCase();

			columnName = columnName.replaceAll("[^a-zA-Z0-9]+", "_");
			columnName = columnName.replace("-", "_");
			columnName = columnName.replace(".", "_");
			columnName = columnName.replace(" ", "_");
			columnName = columnName.toUpperCase();

			if (!colNames.contains(columnName)) {
				if (updateDbSchema) {
					if (map.get(columnName) == null) {
						try {
							if(tag.getClass() != null) {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag.getTagClass(), props));
							} else if(tag.getValue()!=null) {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(tag
												.getValue().getClass(), props));
							} else {
								addColumn(connection, tableName, columnName,
										UserDefinedTagUtils.getDataType(String.class, props));
							}
						} catch (Exception e) {
							if (e.getMessage().contains("object name already exists")) {
								LOG.debug(e.getMessage(), e);
							} else {
								LOG.fatal(e.getMessage(), e);
								continue;
							}
						}
					}
				}
				colNames.add(columnName);
				Object tagValuesCasted = castTagValueToClassType(tag);
				tagValueObjs.add(tagValuesCasted);
			}
		}

		boolean tagEntryExists = tagEntryExists(connection, tableName, filePath);

		connection.setAutoCommit(false);

		if (!tableName.equals(DEFAULT_TABLE)) {
			deleteFromDefaultTable(connection, filePath);
		}
		PreparedStatement stmt = null;
		try {
			
			StringBuffer query = new StringBuffer();
			if (!tagEntryExists) {
				StringBuffer colNmStr = new StringBuffer();
				StringBuffer valueStr = new StringBuffer();
				for (int i = 0; i < colNames.size(); i++) {
					if (i != 0) {
						colNmStr.append(", ");
						valueStr.append(", ");
					}
					colNmStr.append(colNames.get(i));
					valueStr.append("?");
				}
				query.append("INSERT INTO ");
				query.append(tableName).append(" (");
				query.append(colNmStr);
				if(colNames.size()>0) {
					query.append(", ");
				}
				query.append(TableMetadata.DEFAULT_TAG_FILEPATH);// UserDefinedTagUtils.DEFAULT_TAG_FILEPATH
				query.append(") VALUES (");
				query.append(valueStr);
				if (colNames.size() != 0) {
					query.append(", ");
				}
				query.append("?)");
			} else {
				
				query.append("UPDATE ");
				query.append(tableName);
				query.append(" SET ");
				for (int i = 0; i < colNames.size(); i++) {
					if (i != 0)
						query.append(", ");
					query.append(colNames.get(i));
					query.append(" = ?");
				}
				query.append(" WHERE ");
				query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
				query.append(" =? ");
			}
			
			stmt = DatabaseFunctions.getPreparedStatement(connection,
					query.toString());
			int i = 1;
			for (Object tagValueObj : tagValueObjs) {
				if (tagValueObj instanceof byte[]) {
					byte[] arr = (byte[]) tagValueObj;
					stmt.setBytes(i++, arr);
				} else {

					if (tagValueObj instanceof Timestamp) {
						String val = String.valueOf(tagValueObj);
						stmt.setTimestamp(i++, getTimestamp(val));
					} else {

						stmt.setObject(i++, tagValueObj);
					}
				}

			}

			stmt.setString(i++, filePath);

			DatabaseFunctions.executeUpdateStatement(stmt);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		connection.commit();
	}

	private static Object castTagValueToClassType(UserDefinedTag tag) {
		Object obj = null;
		try {
			Class tagClass = tag.getTagClass();
			if(tagClass != null) {
				LOG.debug("tag.getClass : " + tagClass.getName());
			}
			LOG.debug("tag.getValue : " + tag.getValue());  
			if(tagClass != null) {
				if(tagClass.getName().equalsIgnoreCase("java.lang.String")) {
					obj = tag.getValue();
				} else {
					Method method = tagClass.getMethod("valueOf", String.class);
					obj = method.invoke(tagClass, String.valueOf(tag.getValue()));
				}
			} else {
				obj = tag.getValue();
			}
		} catch (Exception e) {
			LOG.fatal("Could not cast tag value to corresponding class.", e);
		}
		return obj;
	}
	
	private static boolean tagEntryExists(Connection connection,
			String tableName, String filePath) throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("SELECT " + TableMetadata.DEFAULT_TAG_FILEPATH + " FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" ='"+filePath+"' ");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = stmt.executeQuery(query.toString());
			if (rs.next())
				return true;
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(stmt, rs);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		return false;
	}

	public static boolean verifyDBSchema(Connection connection1, DBTypeProperties props,
			TableMetadata tableMetadata) throws Exception {
		if (checkIfTableExists(connection1,
				"DATATAGS_" + tableMetadata.getTableName())) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				String query = "SELECT * FROM DATATAGS_"
						+ tableMetadata.getTableName() + " LIMIT 0";

				stmt = DatabaseFunctions.getStatement(connection1);
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
				ResultSetMetaData meta = rs.getMetaData();
				int numCol = meta.getColumnCount();
				List<String> columnsInDb = new ArrayList<String>();
				//CustomTagDBConfigManager.displayDBTypeProperties(props);
				for (int i = 1; i <= numCol; i++) {
					String columnName = meta.getColumnName(i);
					ColumnMetadata cmd = tableMetadata
							.getColumnMetadataByColumnName(columnName);
					if (cmd == null
							|| !props.getTypeMap().get(cmd.getColumnSqlDataType())
									.equalsIgnoreCase(meta.getColumnTypeName(i)) || (cmd.isSizable() ? (cmd.getSize() != meta.getColumnDisplaySize(i)) : false)) {
						throw new Exception("Table DATATAGS_"
								+ tableMetadata.getTableName()
								+ " already exists with different definition. For column : " + columnName + " " + props.getTypeMap().get(cmd.getColumnSqlDataType())+ (cmd.isSizable() ? " (" + cmd.getSize() +")" : ""));
					}
					columnsInDb.add(columnName);
				}
				if (columnsInDb.size() != tableMetadata.getColumns().size()) {
					throw new Exception("Table DATATAGS_"
							+ tableMetadata.getTableName()
							+ " already exists with different definition.");
				}
			} finally {
				try {
					DatabaseFunctions.closeResultSet(rs);
				} catch (SQLException e) {
					LOG.fatal(e.getMessage(), e);
				}
				try {
					DatabaseFunctions.closeStatement(stmt);
				} catch (SQLException e) {
					LOG.fatal(e.getMessage(), e);
				}
			}

			return true;
		} else {
			return false;
		}
	}
	
	private static void deleteFromTable(Connection connection,
			String tableName, String filePath) throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("DELETE FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" LIKE '" + filePath + "%' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} catch(Exception e){
			LOG.fatal("Exception : " , e);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteFromDefaultTable(Connection connection,
			String filePath) throws SQLException {
		deleteFromTable(connection, TableConstants.TABLE_HDFS_METADATA,
				filePath);
	}

	public static void deleteFileFromAllMetadataTables(Connection connection,
			String filePath) throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "DATATAGS_%", new String[]{"TABLE"});
			while (tables.next()) {
				deleteFromTable(connection, tables.getString(3), filePath);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}
	}
	
//	public static void main(String[] args) {
//		Connection connection = null;
//		String filePath = "/Data/xls";
//		try {
//			try {
//				Class.forName("org.postgresql.Driver");
//		    } catch (ClassNotFoundException e) {
//		    	e.printStackTrace();
//		    	System.exit(1);
//		    }
//			connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.11:5432/metastore", "ADMIN", "ADMIN");
//			deleteFileFromAllMetadataTables(connection, filePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (connection != null)
//					connection.close();
//		    } catch (Exception e) {
//		    	e.printStackTrace();
//		    }
//		}
//	}

	private static void updatePermission(Connection connection,
			String tableName, String filePath, String permissions)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("UPDATE ");
		query.append(tableName);
		query.append(" SET ");
		query.append(TableMetadata.DEFAULT_TAG_PERMISSION);
		query.append(" = '" + permissions + "' ");
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" = '" + filePath + "' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void updatePermissionInDefaultTable(Connection connection,
			String filePath, String permissions) throws SQLException {
		updatePermission(connection, TableConstants.TABLE_HDFS_METADATA,
				filePath, permissions);
	}

	public static void updatePermissionInAllMetadataTables(
			Connection connection, String filePath, String permissions)
			throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "datatags_%", null);
			while (tables.next()) {
				String tableName = tables.getString(3);
				updatePermission(connection, tableName, filePath, permissions);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}

	}

	private static void renameFileInTable(Connection connection,
			String tableName, String oldFilePath, String filePath)
			throws SQLException {
		ResultSet rs = null;
		Statement st = null;
		Statement st2 = null;
		String query = "SELECT " + TableMetadata.DEFAULT_TAG_FILEPATH
				+ " FROM " + tableName + " WHERE "
				+ TableMetadata.DEFAULT_TAG_FILEPATH + " LIKE '" + oldFilePath
				+ "%'";
		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				String oldValue = rs
						.getString(TableMetadata.DEFAULT_TAG_FILEPATH);
				StringBuffer q = new StringBuffer();
				q.append("UPDATE ");
				q.append(tableName);
				q.append(" SET ");
				q.append(TableMetadata.DEFAULT_TAG_FILEPATH);
				q.append(" = '" + filePath
						+ oldValue.substring(oldFilePath.length()) + "'");
				q.append(" WHERE ");
				q.append(TableMetadata.DEFAULT_TAG_FILEPATH);
				q.append(" = '" + oldValue + "' ");
				try {
					st2 = DatabaseFunctions.getStatement(connection);
					st2.execute(q.toString());
				} finally {
					DatabaseFunctions.closeStatement(st2);
				}
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}

	}

	public static void renameFileInAllMetadataTables(Connection connection,
			String oldFilePath, String filePath) throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "DATATAGS_%", null);
			while (tables.next()) {
				String tableName = tables.getString(3);
				renameFileInTable(connection, tableName, oldFilePath, filePath);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}
	}

	public static void renameFileInDefaultTable(Connection connection,
			String oldFilePath, String filePath) throws SQLException {
		renameFileInTable(connection, TableConstants.TABLE_HDFS_METADATA,
				oldFilePath, filePath);
	}

	private static void updateReplication(Connection connection,
			String tableName, String filePath, short replication)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("UPDATE ");
		query.append(tableName);
		query.append(" SET ");
		query.append(TableMetadata.DEFAULT_TAG_REPLICATION);
		query.append(" = " + replication);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" = '" + filePath + "' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateReplicationInDefaultTable(Connection connection,
			String filePath, short replication) throws SQLException {
		updateReplication(connection, TableConstants.TABLE_HDFS_METADATA,
				filePath, replication);
	}

	public static void updateReplicationInAllMetadataTables(
			Connection connection, String filePath, short replication)
			throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "DATATAGS_%", null);
			while (tables.next()) {
				String tableName = tables.getString(3);
				updateReplication(connection, tableName, filePath, replication);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}
	}

	private static void updateOwner(Connection connection, String tableName,
			String filePath, String username, String groupname)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("UPDATE ");
		query.append(tableName);
		query.append(" SET ");
		query.append(TableMetadata.DEFAULT_TAG_OWNER);
		query.append(" = '" + username + "', ");
		query.append(TableMetadata.DEFAULT_TAG_GROUP);
		query.append(" = '" + groupname + "'");
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" = '" + filePath + "' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}

	}

	public static void updateOwnerInDefaultTable(Connection connection,
			String filePath, String username, String groupname)
			throws SQLException {
		updateOwner(connection, TableConstants.TABLE_HDFS_METADATA, filePath,
				username, groupname);
	}

	public static void updateOwnerInAllMetadataTables(Connection connection,
			String filePath, String username, String groupname)
			throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "DATATAGS_%", null);
			while (tables.next()) {
				String tableName = tables.getString(3);
				updateOwner(connection, tableName, filePath, username,
						groupname);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}

	}

	private static void updateTimes(Connection connection, String tableName,
			String filePath, long mtime, long atime) throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("UPDATE ");
		query.append(tableName);
		query.append(" SET ");
		query.append(TableMetadata.DEFAULT_TAG_MODIFICATIONTIME);
		query.append(" = " + mtime + ", ");
		query.append(TableMetadata.DEFAULT_TAG_ACCESSTIME);
		query.append(" = " + atime);
		query.append(" WHERE ");
		query.append(TableMetadata.DEFAULT_TAG_FILEPATH);
		query.append(" = '" + filePath + "' ");
		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			stmt.execute(query.toString());
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateTimesInDefaultTable(Connection connection,
			String filePath, long mtime, long atime) throws SQLException {
		updateTimes(connection, TableConstants.TABLE_HDFS_METADATA, filePath,
				mtime, atime);
	}

	public static void updateTimesInAllMetadataTables(Connection connection,
			String filePath, long mtime, long atime) throws SQLException {
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, "DATATAGS_%", null);
			while (tables.next()) {
				String tableName = tables.getString(3);
				updateTimes(connection, tableName, filePath, mtime, atime);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}

	}
	public static Timestamp getTimestamp(String date){
		SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Long val = System.currentTimeMillis();
		try{
			if(date.contains("-")){
				if(date.contains("T")){
					date = date.replace("T", " ").replace("Z", ".001");
				}
				if(date.contains("."))
					date = date.substring(0, date.indexOf("."));
				try{
					Date lFromDate1 = datetimeFormatter.parse(date);
					val = lFromDate1.getTime();
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				val = Long.valueOf(date);
			}
		}catch(Exception e){
			//ignore.
		}
		return new Timestamp(val);
	}
	
	public static void main(String[] args) {
		args = new String[] {
			"1357661170000",
			"2013-04-17 20:52:23.514",
			"2013-01-08T16:06:14Z",
			"2013-01-08 16:06:14",
		};
		
		for (int i = 0; i < args.length; i++) {
			System.out.println(getTimestamp(args[i]));
			
		}
	}
}