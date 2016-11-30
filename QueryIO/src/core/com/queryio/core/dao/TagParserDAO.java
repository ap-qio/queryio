package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.TagParserConfig;
import com.queryio.core.monitor.beans.SummaryTable;

public class TagParserDAO {

	public static void insert(Connection connection, String name, String description, String jarName, String fileTypes,
			String className, String namenodeId, boolean onIngest, boolean isActive) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs = null;
		try {
			String query1 = "INSERT INTO " + TableConstants.TABLE_TAGPARSERS + "("
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME + "," + ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC
					+ "," + ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB + "," + ColumnConstants.COL_TAGPARSERS_FILETYPE
					+ "," + ColumnConstants.COL_TAGPARSERS_CLASSNAME + "," + ColumnConstants.COL_TAGPARSERS_NAMENODEID
					+ "," + ColumnConstants.COL_TAGPARSERS_ONINGEST + "," + ColumnConstants.COL_TAGPARSERS_ISACTIVE
					+ ") VALUES (?,?,?,?,?,?,?,?)";
			st1 = connection.prepareStatement(query1);
			st1.setString(1, name);
			st1.setString(2, description);
			st1.setString(3, jarName);
			st1.setString(4, fileTypes);
			st1.setString(5, className);
			st1.setString(6, namenodeId);
			st1.setBoolean(7, onIngest);
			st1.setBoolean(8, isActive);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs);
		}
	}

	public static void updateExceptJarInfo(Connection connection, String name, String description, String fileTypes,
			String className, String namenodeId, boolean onIngest, boolean isActive) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs = null;
		try {
			String query1 = "UPDATE " + TableConstants.TABLE_TAGPARSERS + " SET "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC + "=?," + ColumnConstants.COL_TAGPARSERS_FILETYPE
					+ "=?," + ColumnConstants.COL_TAGPARSERS_CLASSNAME + "=?,"
					+ ColumnConstants.COL_TAGPARSERS_NAMENODEID + "=?," + ColumnConstants.COL_TAGPARSERS_ONINGEST
					+ "=?," + ColumnConstants.COL_TAGPARSERS_ISACTIVE + "=?" + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME + "=?";
			st1 = connection.prepareStatement(query1);
			st1.setString(1, description);
			st1.setString(2, fileTypes);
			st1.setString(3, className);
			st1.setString(4, namenodeId);
			st1.setBoolean(5, onIngest);
			st1.setBoolean(6, isActive);
			st1.setString(7, name);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs);
		}
	}

	public static TagParserConfig get(Connection connection, int parserId) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		TagParserConfig parser = null;
		try {
			String query1 = "SELECT * FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERID + " = ?";
			st1 = connection.prepareStatement(query1);
			st1.setInt(1, parserId);
			rs1 = st1.executeQuery();
			if (rs1.next()) {
				int id = rs1.getInt(ColumnConstants.COL_TAGPARSERS_TAGPARSERID);
				String name = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME);
				String description = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC);
				String jarName = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB);
				String fileTypes = rs1.getString(ColumnConstants.COL_TAGPARSERS_FILETYPE);
				String className = rs1.getString(ColumnConstants.COL_TAGPARSERS_CLASSNAME);
				String namenodeId = rs1.getString(ColumnConstants.COL_TAGPARSERS_NAMENODEID);
				boolean onIngest = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ONINGEST);
				boolean isActive = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ISACTIVE);
				parser = new TagParserConfig(id, name, description, jarName, fileTypes, className, namenodeId, onIngest,
						isActive);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs1);
		}
		return parser;
	}

	public static ArrayList getAllOnIngest(Connection connection) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		ArrayList result = new ArrayList();
		try {
			String query1 = "SELECT * FROM " + TableConstants.TABLE_TAGPARSERS;
			st1 = connection.prepareStatement(query1);
			rs1 = st1.executeQuery();
			while (rs1.next()) {
				boolean onIngest = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ONINGEST);
				if (onIngest) {
					int id = rs1.getInt(ColumnConstants.COL_TAGPARSERS_TAGPARSERID);
					String name = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME);
					String description = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC);
					String jarName = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB);
					String fileTypes = rs1.getString(ColumnConstants.COL_TAGPARSERS_FILETYPE);
					String className = rs1.getString(ColumnConstants.COL_TAGPARSERS_CLASSNAME);
					String namenodeId = rs1.getString(ColumnConstants.COL_TAGPARSERS_NAMENODEID);
					boolean isActive = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ISACTIVE);
					TagParserConfig parser = new TagParserConfig(id, name, description, jarName, fileTypes, className,
							namenodeId, onIngest, isActive);
					result.add(parser);
				}
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs1);
		}
		return result;
	}

	public static ArrayList getAllOnIngestForNamenode(Connection connection, String namenodeId) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		ArrayList result = new ArrayList();
		try {
			String query1 = "SELECT * FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_NAMENODEID + " = ? AND " + ColumnConstants.COL_TAGPARSERS_ONINGEST
					+ " = ?";
			st1 = connection.prepareStatement(query1);
			st1.setString(1, namenodeId);
			st1.setBoolean(2, true);
			rs1 = st1.executeQuery();
			while (rs1.next()) {
				boolean onIngest = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ONINGEST);
				int id = rs1.getInt(ColumnConstants.COL_TAGPARSERS_TAGPARSERID);
				String name = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME);
				String description = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC);
				String jarName = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB);
				String fileTypes = rs1.getString(ColumnConstants.COL_TAGPARSERS_FILETYPE);
				String className = rs1.getString(ColumnConstants.COL_TAGPARSERS_CLASSNAME);
				boolean isActive = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ISACTIVE);
				TagParserConfig parser = new TagParserConfig(id, name, description, jarName, fileTypes, className,
						namenodeId, onIngest, isActive);
				result.add(parser);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs1);
		}
		return result;
	}

	public static SummaryTable getAllPostIngest(Connection connection) throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		SummaryTable result = new SummaryTable();
		ArrayList colNames = new ArrayList();
		colNames.add("Id");
		colNames.add("Name");
		colNames.add("Description");
		colNames.add("Jar");
		colNames.add("File Types");
		colNames.add("Class name");
		colNames.add("NameNode");
		colNames.add("Resource Manager");

		result.setColNames(colNames);
		try {
			String query1 = "SELECT * FROM " + TableConstants.TABLE_TAGPARSERS;
			st1 = connection.prepareStatement(query1);
			rs1 = st1.executeQuery();
			while (rs1.next()) {
				boolean onIngest = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ONINGEST);
				if (!onIngest) {
					ArrayList row = new ArrayList();
					int id = rs1.getInt(ColumnConstants.COL_TAGPARSERS_TAGPARSERID);
					row.add(id);
					String name = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME);
					row.add(name);
					String desc = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC);
					row.add(desc);
					String jarName = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB);
					row.add(jarName);
					String fileTypes = rs1.getString(ColumnConstants.COL_TAGPARSERS_FILETYPE);
					row.add(fileTypes);
					String className = rs1.getString(ColumnConstants.COL_TAGPARSERS_CLASSNAME);
					row.add(className);
					MapRedJobConfig jobConfig = MapRedJobConfigDAO.get(connection, name);
					if (jobConfig != null) {
						row.add(jobConfig.getNamenodeId());
						row.add(jobConfig.getRmId());
					} else {
						row.add("-");
						row.add("-");
					}
					result.addRow(row);
				}
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs1);
		}
		return result;
	}

	public static void updateTagParser(Connection connection, int id, String name, String description, String jarName,
			String fileTypes, String className, String namenodeId, boolean onIngest, boolean isActive)
			throws SQLException {
		PreparedStatement st1 = null;
		try {
			String query1 = "UPDATE " + TableConstants.TABLE_TAGPARSERS + " SET "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME + " = ? ,"
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC + " = ? " + ","
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB + " = ? , " + ColumnConstants.COL_TAGPARSERS_FILETYPE
					+ " = ? " + "," + ColumnConstants.COL_TAGPARSERS_CLASSNAME + " = ? , "
					+ ColumnConstants.COL_TAGPARSERS_NAMENODEID + " = ? , " + ColumnConstants.COL_TAGPARSERS_ONINGEST
					+ " = ? " + "," + ColumnConstants.COL_TAGPARSERS_ISACTIVE + " = ? " + "WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERID + " = ?";

			st1 = connection.prepareStatement(query1);
			st1.setString(1, name);
			st1.setString(2, description);
			st1.setString(3, jarName);
			st1.setString(4, fileTypes);
			st1.setString(5, className);
			st1.setString(6, namenodeId);
			st1.setBoolean(6, onIngest);
			st1.setBoolean(7, isActive);
			st1.setInt(8, id);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
		}
	}

	public static void delete(Connection connection, int parserId) throws SQLException {
		PreparedStatement st1 = null;
		try {
			String query1 = "DELETE FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERID + " = ?";

			st1 = connection.prepareStatement(query1);

			st1.setInt(1, parserId);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
		}
	}

	public static void deleteByNamenodeId(Connection connection, String namenodeId) throws SQLException {
		PreparedStatement st1 = null;
		try {
			String query1 = "DELETE FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_NAMENODEID + " = ?";

			st1 = connection.prepareStatement(query1);

			st1.setString(1, namenodeId);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
		}
	}

	public static void deleteByName(Connection connection, String name, boolean onIngest) throws SQLException {
		PreparedStatement st1 = null;
		try {
			String query1 = "DELETE FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME + " = ?" + " AND "
					+ ColumnConstants.COL_TAGPARSERS_ONINGEST + " = ?";

			st1 = connection.prepareStatement(query1);

			st1.setString(1, name);
			st1.setBoolean(2, onIngest);
			st1.execute();

		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
		}
	}

	public static TagParserConfig getByName(Connection connection, String parserName, boolean isOnIngest)
			throws SQLException {
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		TagParserConfig parser = null;
		try {
			String query1 = "SELECT * FROM " + TableConstants.TABLE_TAGPARSERS + " WHERE "
					+ ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME + " = ?" + " AND "
					+ ColumnConstants.COL_TAGPARSERS_ONINGEST + " = ?";
			st1 = connection.prepareStatement(query1);
			st1.setString(1, parserName);
			st1.setBoolean(2, isOnIngest);
			rs1 = st1.executeQuery();
			if (rs1.next()) {
				int id = rs1.getInt(ColumnConstants.COL_TAGPARSERS_TAGPARSERID);
				String name = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERNAME);
				String description = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERDESC);
				String jarName = rs1.getString(ColumnConstants.COL_TAGPARSERS_TAGPARSERLIB);
				String fileTypes = rs1.getString(ColumnConstants.COL_TAGPARSERS_FILETYPE);
				String className = rs1.getString(ColumnConstants.COL_TAGPARSERS_CLASSNAME);
				String namenodeId = rs1.getString(ColumnConstants.COL_TAGPARSERS_NAMENODEID);
				boolean onIngest = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ONINGEST);
				boolean isActive = rs1.getBoolean(ColumnConstants.COL_TAGPARSERS_ISACTIVE);
				parser = new TagParserConfig(id, name, description, jarName, fileTypes, className, namenodeId, onIngest,
						isActive);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(st1);
			DatabaseFunctions.closeResultSet(rs1);
		}
		return parser;
	}
}
