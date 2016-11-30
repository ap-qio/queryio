package com.queryio.file.upload;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.NodeDAO;
import com.queryio.plugin.datatags.AbstractDataTagParser;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class TagParserDBSchemaUpdator {
	String jarFilePath;
	String className;
	String namenodeId;
	String fileTypes;

	public TagParserDBSchemaUpdator(String path, String className, String namenodeId, String fileTypes) {
		this.jarFilePath = path;
		this.className = className;
		this.namenodeId = namenodeId;
		this.fileTypes = fileTypes;
	}

	public QueryIOResponse parse() {
		File jarFile = new File(jarFilePath);
		List<TableMetadata> tableMetadataList = new ArrayList<TableMetadata>();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			URL fileURL = jarFile.toURI().toURL();
			String jarURL = "jar:" + fileURL + "!/";
			URL urls[] = { new URL(jarURL) };
			URLClassLoader ucl = new URLClassLoader(urls, this.getClass().getClassLoader());

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Loading class: " + className);
			Class<IDataTagParser> clazz = (Class<IDataTagParser>) ucl.loadClass(className);

			IDataTagParser parser = null;

			// IDataTagParser parser = clazz.newInstance();
			if (clazz != null) {
				try {
					parser = (AbstractDataTagParser) clazz.getConstructor(JSONObject.class, Map.class).newInstance(null,
							null);
				} catch (Exception e) {
					e.printStackTrace();
					// AppLogger.getLogger().fatal("Parser class could not be
					// initialized.", e);
					// throw new IOException("Parser class could not be
					// initialized,", e);
				}
			}
			if (parser != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Class " + parser.getClass() + " is loaded");

				for (String fileType : fileTypes.split(",")) {
					fileType = fileType.toLowerCase().trim();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Fetching TableMetadata tor filetype: " + fileType);
					TableMetadata tableMetadata = parser.getTableMetaData(fileType);
					if (tableMetadata == null) {
						return new QueryIOResponse(false, "Given TagParser is not supported for fileType: " + fileType);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Found table: " + tableMetadata.getTableName() + " tor filetype: " + fileType);
					tableMetadataList.add(tableMetadata);
				}
				Connection connection1 = null;
				String exceptions = null;
				try {
					connection1 = CoreDBManager
							.getCustomTagDBConnection(NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId));
					DBTypeProperties props = CustomTagDBConfigManager
							.getDatabaseDataTypeMap(NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId), null);

					for (TableMetadata tableMetadata : tableMetadataList) {
						try {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger()
										.debug("Verifying table: DATATAGS_" + tableMetadata.getTableName());
							if (!UserDefinedTagDAO.verifyDBSchema(connection1, props, tableMetadata)) {
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger()
											.debug("Creating table: " + "DATATAGS_" + tableMetadata.getTableName());
								UserDefinedTagDAO.createDatabaseTable(connection1, props,
										"DATATAGS_" + tableMetadata.getTableName(), tableMetadata.getColumnData());
							}
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
							exceptions += e.getLocalizedMessage() + "\n";
						}
					}
					if (exceptions != null) {
						throw new Exception(exceptions);
					}
				} catch (Exception e) {
					return new QueryIOResponse(false, e.getMessage());
				} finally {
					CoreDBManager.closeConnection(connection1);
				}
			}

		} catch (Exception e) {
			return new QueryIOResponse(false, e.getMessage());
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {

			}

		}
		return new QueryIOResponse(true, "");
	}
}
