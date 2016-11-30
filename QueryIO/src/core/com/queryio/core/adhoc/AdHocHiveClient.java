package com.queryio.core.adhoc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.ConfigurationManager;

public class AdHocHiveClient {
	public static String warehouseDir = "/hive";
	public static String templateDir = "/hiveTemplate/sample";

	public static Connection getHiveConnection(Connection connection, String nameNodeId) throws Exception {
		Connection hiveConnection = null;
		Configuration config = ConfigurationManager.getConfiguration(connection, nameNodeId);

		String driverName = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_DRIVER);
		String url = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_URL);
		String userName = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_USERNAME);
		String password = config.get(QueryIOConstants.HIVE_QUERYIO_CONNECTION_PASSWORD);

		Class.forName(driverName);
		hiveConnection = DriverManager.getConnection(url, userName, password);
		return hiveConnection;
	}

	public static void isHiveStarted(String nameNodeId) throws Exception {
		Connection connection = null;
		Connection hiveConnection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			hiveConnection = getHiveConnection(connection, nameNodeId);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
			try {
				DatabaseManager.closeDbConnection(hiveConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
	}

	public static void loadFileEntry(Connection connection, String path, String tableName) {
		// String filepath = "/AppPerfect/HiperCloudStore/demo/Data/csv";
		// sql = "load data local inpath '" + filepath + "' overwrite into table
		// " + tableName;
		// System.out.println("Running: " + sql);
		// res = stmt.executeQuery(sql);

		// Statement stmt = null;
		// String query = "load data inpath '" + path + "' overwrite into table
		// " + tableName;
		//
		// try
		// {
		// stmt = DatabaseFunctions.getStatement(connection);
		// DatabaseFunctions.executeStatement(stmt, query);
		// }
		// catch (Exception e)
		// {
		// AppLogger.getLogger().fatal("Error loading data: " + e.getMessage(),
		// e);
		// }
		// finally
		// {
		// try
		// {
		// DatabaseFunctions.closeStatement(stmt);
		// }
		// catch (Exception e)
		// {
		// AppLogger.getLogger().fatal("Error closing SQL Statement.", e);
		// }
		// }
	}

	public static String getQueryString(String type, String tableName, String[] colNames, String[] datatypes,
			String[] colIndex, String delimiter, String valueSeparator, String pattern, String location,
			boolean ifFirstHeader) throws Exception {
		StringBuffer createStatement = new StringBuffer(QueryIOConstants.HIVE_CREATE_EXTERNAL_TABLE).append(" ");
		createStatement.append(tableName).append(" (");
		boolean addComma = false;
		for (int i = 0; i < colNames.length; i++) {
			if (ColumnConstants.COL_TAG_VALUES_FILEPATH.equalsIgnoreCase(colNames[i]))
				continue;

			if (addComma)
				createStatement.append(", ");

			if (QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equalsIgnoreCase(type)
					|| QueryIOConstants.ADHOC_TYPE_REGEX.equalsIgnoreCase(type))
				createStatement.append(colNames[i]).append(" ").append("string");
			else
				createStatement.append(colNames[i]).append(" ").append(sqlTypeToHiveType(datatypes[i]));

			if (!addComma)
				addComma = true;
		}
		createStatement.append(")");

		if (QueryIOConstants.ADHOC_TYPE_CSV.equalsIgnoreCase(type)
				|| QueryIOConstants.ADHOC_TYPE_IISLOG.equalsIgnoreCase(type)
				|| QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(type)) {
			if (delimiter != null) {
				createStatement.append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY ");
				createStatement.append("'");
				createStatement.append(delimiter);
				createStatement.append("'");
			}
			if (valueSeparator != null && QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(type)) {
				createStatement.append(" COLLECTION ITEMS TERMINATED BY ");
				createStatement.append("'");
				createStatement.append(valueSeparator);
				createStatement.append("'");
			}

			createStatement.append(" STORED AS TEXTFILE LOCATION ");
			createStatement.append("'");
			createStatement.append(location);
			createStatement.append("'");

			if (ifFirstHeader) {
				createStatement.append(" TBLPROPERTIES (\"skip.header.line.count\"=\"1\")");
			}

		} else if (QueryIOConstants.ADHOC_TYPE_JSON.equalsIgnoreCase(type)) {
			// res = stmt.executeQuery("CREATE EXTERNAL TABLE " + tableName + "
			// (RAM INT,IP STRING,CPU INT) " +
			// "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.json.JsonSerDe'
			// STORED AS TEXTFILE LOCATION '" + location + "'");

			createStatement.append(" ROW FORMAT SERDE ");
			createStatement.append("'");
			createStatement.append(QueryIOConstants.HIVE_SERDE_CLASS_JSON);
			createStatement.append("'");
			createStatement.append(" STORED AS TEXTFILE LOCATION ");
			createStatement.append("'");
			createStatement.append(location);
			createStatement.append("'");
		} else if (QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equalsIgnoreCase(type)
				|| QueryIOConstants.ADHOC_TYPE_REGEX.equalsIgnoreCase(type)) {
			String regex = pattern;
			String formatString = "";

			if (QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equalsIgnoreCase(type))
				regex = convertPatternToRegex(pattern);

			formatString = getOutputFormatString(colIndex);

			createStatement.append(" ROW FORMAT SERDE ");
			createStatement.append("'");
			createStatement.append(QueryIOConstants.HIVE_SERDE_CLASS_REGEX);
			createStatement.append("'");
			createStatement.append(" WITH SERDEPROPERTIES  (");
			createStatement.append("\"input.regex\" = ");
			createStatement.append("\"" + regex + "\", ");
			createStatement.append("\"output.format.string\" = ");
			createStatement.append("\"" + formatString + "\"");
			createStatement.append(")");
			createStatement.append(" STORED AS TEXTFILE LOCATION ");
			createStatement.append("'");
			createStatement.append(location);
			createStatement.append("'");
		}

		return createStatement.toString();
	}

	private static String convertPatternToRegex(String pattern) throws Exception {
		String regex = "";

		String patternSeq[] = pattern.split(" ");

		for (int i = 0; i < patternSeq.length; i++) {
			patternSeq[i] = patternSeq[i].trim();
			// System.out.println(QueryIOConstants.ACCESS_LOG_PATTERN_TO_REGEX_MAP.get(patternSeq[i]).toString()
			// + " ");
			regex += QueryIOConstants.ACCESS_LOG_PATTERN_TO_REGEX_MAP.get(patternSeq[i]).toString() + " ";
		}
		regex = regex.trim();
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("regex: " + regex);

		return regex;
	}

	private static String getOutputFormatString(String[] colIndex) {
		String format = "";

		for (int i = 0; i < colIndex.length; i++) {
			if ("0".equals(colIndex[i]))
				continue;
			format += "%" + colIndex[i] + "$s ";
		}
		format = format.trim();
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("format: " + format);

		return format;
	}

	// public static void main(String args[])
	// {
	// String colIndex[] = {"1", "2", "4", "5"};
	// String pattern = "%h %l %u %b %t \"%r\" %>s";
	//// String regex = getRegex(colIndex, pattern);
	//// System.out.println(regex);
	// convertPatternToRegex(pattern);
	// getOutputFormatString(colIndex);
	// }

	public static String getQueryStringPersist(String resultTableName, String query) throws Exception {
		StringBuffer createStatement = new StringBuffer(QueryIOConstants.HIVE_CREATE_TABLE).append(" ");

		// CREATE TABLE NEW_" + tableName + " ROW FORMAT DELIMITED FIELDS
		// TERMINATED BY ',' STORED AS TEXTFILE AS SELECT * FROM " + tableName +
		// " WHERE CPU > 98

		createStatement.append(resultTableName);
		createStatement.append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ");
		createStatement.append(" STORED AS TEXTFILE ");
		createStatement.append(" AS ");

		if (query.substring(0, query.indexOf("FROM")).contains(" * ")) {
			StringBuilder sb = new StringBuilder();
			sb.append(query.substring(0, query.indexOf(" * ")));
			sb.append(" ");
			sb.append(QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
			sb.append(",* ");
			sb.append(query.substring(query.indexOf("FROM")));
			query = sb.toString();
		}
		if (query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH)
				|| query.contains(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase())) {
			query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH,
					QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
			query = query.replaceAll(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase(),
					QueryIOConstants.HIVE_FILEPATH_COLUMN_NAME);
		}

		createStatement.append(query);

		return createStatement.toString();
	}

	public static void createHiveTable(Connection connection, String createQuery) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("createHiveTable DDL: " + createQuery);

		Statement stmt = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			DatabaseFunctions.executeStatement(stmt, createQuery);
		} finally {
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static String sqlTypeToHiveType(String type) throws SQLException {
		type = type.toUpperCase();
		if (type == null)
			return "string";
		if (type.startsWith("VARCHAR"))
			return "string";

		if (type.equalsIgnoreCase("LONGVARCHAR"))
			return "string";

		if (type.equalsIgnoreCase("BOOLEAN"))
			return "boolean";

		if (type.equalsIgnoreCase("TINYINT"))
			return "tinyint";
		if (type.equalsIgnoreCase("SMALLINT"))
			return "smallint";
		if (type.equalsIgnoreCase("SHORT"))
			return "smallint";
		if (type.equalsIgnoreCase("INTEGER"))
			return "int";
		if (type.startsWith("INT"))
			return "int";
		if (type.equalsIgnoreCase("BIGINT"))
			return "bigint";
		if (type.equalsIgnoreCase("LONG"))
			return "bigint";

		if (type.equalsIgnoreCase("NUMERIC"))
			return "bigint";
		if (type.equalsIgnoreCase("DECIMAL"))
			return "decimal";

		if (type.equalsIgnoreCase("FLOAT"))
			return "float";
		if (type.equalsIgnoreCase("DOUBLE"))
			return "double";

		return "string";

		// if (type.startsWith("map<")) {
		// return Types.VARCHAR;
		// } else if (type.startsWith("array<")) {
		// return Types.VARCHAR;
		// } else if (type.startsWith("struct<")) {
		// return Types.VARCHAR;
		// }
	}

	public static void dropHiveTable(String tableName, String nameNodeId) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("dropHiveTable: tableName: " + tableName + " nameNodeId: " + nameNodeId);

		Connection connection = null;
		Connection hiveConnection = null;
		Statement statement = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			hiveConnection = AdHocHiveClient.getHiveConnection(connection, nameNodeId);

			statement = DatabaseFunctions.getStatement(hiveConnection);
			String query = "DROP TABLE IF EXISTS " + tableName;
			DatabaseFunctions.executeUpdateStatement(statement, query);
		} catch (Exception e) {
			if (e.getMessage() != QueryIOConstants.HIVE_EXECUTEUPDATE_ERROR_THROWN)
				throw e;
		} finally {
			try {
				DatabaseFunctions.closeStatement(statement);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseManager.closeDbConnection(hiveConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setConfiguration(Connection connection, HashMap keys) throws Exception {
		// sql = "SET -v";
		// System.out.println("Running: " + sql);
		// res = stmt.executeQuery(sql);
		// while (res.next()) {
		// System.out.println(res.getString(1));
		// }

		if ((keys != null) && (keys.size() > 0)) {
			Statement stmt = null;
			try {
				stmt = DatabaseFunctions.getStatement(connection);

				String key = null;
				String value = null;
				String query = null;

				Iterator it = keys.keySet().iterator();
				while (it.hasNext()) {
					key = String.valueOf(it.next());
					if (key == null)
						continue;
					value = String.valueOf(keys.get(key));
					query = "SET " + key + "=" + value;
					DatabaseFunctions.executeStatement(stmt, query);
				}
			} finally {
				try {
					DatabaseFunctions.closeStatement(stmt);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
	}
}