package com.queryio.core.customtags;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.NodeDAO;

public class CustomTagsManager {
	public static HashMap getAllAvailableTags(String connectionPool, String namenodeId) {
		Connection connection = null;
		DBTypeProperties props = new DBTypeProperties();
		try {
			connection = RemoteManager.getCustomTagDBConnection(connectionPool);
			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionPool, null);
			return CustomTagsDAO.getAllAvailableTags(connection, namenodeId, props);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllAvailableTags() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static JSONObject getAllAvailableTags(String connectionPool, ArrayList tableNames) {
		Connection connection = null;
		DBTypeProperties props = new DBTypeProperties();
		try {
			connection = RemoteManager.getCustomTagDBConnection(connectionPool);
			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionPool, null);
			return CustomTagsDAO.getAllAvailableTags(connection, tableNames, props);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllAvailableTags() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static Map<String, Boolean> getAllTagTableNames(String connectionPool) {
		Connection connection = null;
		Connection queryIOConnection = null;

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		;

		try {
			queryIOConnection = CoreDBManager.getQueryIODBConnection();

			try {
				connection = RemoteManager.getCustomTagDBConnection(connectionPool);
				map.put("ConnectionError", false);
			} catch (Exception e) {
				map.put("ConnectionError", true);
				return map;
			}

			String nameNodeId = NodeDAO.getNameNodeForAnalyticsDBNameMapping(queryIOConnection, connectionPool);
			if (nameNodeId == null)
				nameNodeId = NodeDAO.getNameNodeForDBNameMapping(queryIOConnection, connectionPool);

			map = CustomTagsDAO.getAllTagTableNames(connection, nameNodeId);

			return map;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllTagTableNames() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(queryIOConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList<String> getAllTagTableNamesList(String connectionPool) {
		Connection connection = null;

		try {
			connection = RemoteManager.getCustomTagDBConnection(connectionPool);

			return CustomTagsDAO.getAllTagTableNamesList(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllTagTableNames() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static TableModel getBigQueryResultTableModel(String connectionPool, String query, int records,
			JSONObject properties) throws Exception {
		String tableName = query.substring(query.indexOf("FROM ") + "FROM ".length());
		tableName = tableName.trim();
		tableName = tableName.split(" ")[0];
		tableName = tableName.trim();
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Table Name: " + tableName);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Query: " + query);

		Connection connection = null;

		TableModel tableModel = null;

		try {
			connection = RemoteManager.getCustomTagDBConnection(connectionPool);

			tableModel = CustomTagsDAO.getBigQueryResultTableModel(connection, query, tableName, records);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return tableModel;
	}

	public static Object[] getObjectArray(int size) {
		return new Object[size];
	}

	public void set(Object[] objects, int index, Object value) {
		objects[index] = value;
	}

	public static int getIndex(Object[] values, Object value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public static TableModel alterTableModel(TableModel resultModel, JSONObject properties) throws Exception {
		int colCount = resultModel.getColumnCount();
		String[] colNames = new String[colCount];
		for (int i = 0; i < colCount; i++) {
			colNames[i] = resultModel.getColumnName(i);
		}

		ArrayList<Integer> groupByColumnIndices = new ArrayList<Integer>();
		JSONArray groupByColumns = (JSONArray) properties.get(BigQueryIdentifiers.GROUPHEADER);

		for (int i = 0; i < groupByColumns.size(); i++) {
			groupByColumnIndices.add(getIndex(colNames, groupByColumns.get(i)));
		}

		HashMap<Integer, ColumnFooter> footerMap = new HashMap<Integer, ColumnFooter>();

		JSONObject footer = null;
		JSONArray footers = (JSONArray) properties.get(BigQueryIdentifiers.GROUPFOOTER);
		String column = null;
		for (int i = 0; i < footers.size(); i++) {
			footer = (JSONObject) footers.get(i);
			footerMap.put(getIndex(colNames, footer.get("column")), new ColumnFooter((String) footer.get("column"),
					(String) footer.get("function"), (String) footer.get("prefix"), (String) footer.get("suffix")));
		}

		DefaultTableModel newModel = new DefaultTableModel(colNames, 0);

		double[] avgs = new double[colCount];
		double[] counts = new double[colCount];
		double[] maxs = new double[colCount];
		double[] mins = new double[colCount];
		double[] sums = new double[colCount];

		int rowCount = resultModel.getRowCount();

		ColumnFooter columnFooter = null;
		boolean groupChanges = true;
		int count = 0;
		for (int i = 0; i < rowCount; i++) {
			count++;

			if (i != 0) {
				for (int j = 0; j < groupByColumnIndices.size(); j++) {
					if (!resultModel.getValueAt(i, groupByColumnIndices.get(j))
							.equals(resultModel.getValueAt(i - 1, groupByColumnIndices.get(j)))) {
						groupChanges = true;
					}
				}
			}

			if (groupChanges) {
				if (i != 0) {
					// Add footer

					Object[] footerRow = new Object[colCount];

					for (int j = 0; j < colCount; j++) {
						columnFooter = footerMap.get(j);

						String value = "";
						switch (columnFooter.getFunction()) {
						case ColumnFooter.AVG:
							value = String.valueOf(avgs[j]);
							break;
						case ColumnFooter.COUNT:
							value = String.valueOf(counts[j]);
							break;
						case ColumnFooter.MAX:
							value = String.valueOf(maxs[j]);
							break;
						case ColumnFooter.MIN:
							value = String.valueOf(mins[j]);
							break;
						case ColumnFooter.SUM:
							value = String.valueOf(sums[j]);
							break;
						}

						footerRow[j] = columnFooter.getPrefix() + " " + value + " " + columnFooter.getSuffix();
					}

					newModel.addRow(footerRow);
				}

				// Add header

				Object[] headerRow = new Object[colCount];
				for (int j = 0; j < colCount; j++) {
					if (groupByColumnIndices.contains(j)) {
						headerRow[j] = resultModel.getValueAt(i + 1, j);
					} else {
						headerRow[j] = "";
					}
				}
				newModel.addRow(headerRow);

				// Perform reset

				groupChanges = false;
				count = 0;

				avgs = new double[colCount];
				counts = new double[colCount];
				maxs = new double[colCount];
				mins = new double[colCount];
				sums = new double[colCount];

				continue;
			}

			// Handle routine function calculations

			for (int j = 0; j < colCount; j++) {
				columnFooter = footerMap.get(j);

				if (columnFooter != null) {
					double currentValue = Double.parseDouble((String) resultModel.getValueAt(i, j));

					switch (columnFooter.getFunction()) {
					case ColumnFooter.AVG:
						sums[j] = sums[j] + currentValue;
						avgs[j] = sums[j] / count;
						break;
					case ColumnFooter.COUNT:
						counts[j] = count;
						break;
					case ColumnFooter.MAX:
						if (i == 0) {
							maxs[j] = currentValue;
						} else {
							maxs[i] = Math.max(maxs[j], currentValue);
						}
						break;
					case ColumnFooter.MIN:
						if (i == 0) {
							mins[j] = currentValue;
						} else {
							mins[i] = Math.min(mins[j], currentValue);
						}
						break;
					case ColumnFooter.SUM:
						sums[j] = sums[j] + currentValue;
						break;
					}
				}
			}

			// Add current row to newModel

			Object[] rowData = new Object[colCount];
			for (int j = 0; j < colCount; j++) {
				rowData[j] = resultModel.getValueAt(i, j);
			}
			newModel.addRow(rowData);
		}

		return newModel;
	}

	public static HashMap getResultSetColumns(String connectionPool, String query) {
		Connection connection = null;

		try {
			connection = RemoteManager.getCustomTagDBConnection(connectionPool);
			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(null,
					CustomTagDBConfigManager.getConfig(connectionPool).getCustomTagDBType());
			return CustomTagsDAO.getResultSetColumns(connection, props, query);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}
}

class ColumnFooter {
	public static final int AVG = 0;
	public static final int COUNT = 1;
	public static final int MAX = 2;
	public static final int MIN = 3;
	public static final int SUM = 4;

	private String prefix;

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getColumn() {
		return column;
	}

	public int getFunction() {
		return function;
	}

	private String suffix;
	private String column;
	private int function;

	public ColumnFooter(String column, String function, String prefix, String suffix) throws Exception {

		if (function.equals("AVG")) {
			this.function = AVG;
		} else if (function.equals("COUNT")) {
			this.function = COUNT;
		} else if (function.equals("MAX")) {
			this.function = MAX;
		} else if (function.equals("MIN")) {
			this.function = MIN;
		} else if (function.equals("SUM")) {
			this.function = SUM;
		} else {
			throw new Exception("Invalid function specified");
		}

		this.prefix = prefix;
		this.suffix = suffix;
		this.column = column;
	}

}