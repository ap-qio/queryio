package com.queryio.core.customtags.metadata;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class MetaDataTagManager {

	public static ArrayList<String> getAllCustomTagMetadataIds() throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.getAllCustomTagMetadaIds(connection);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static ArrayList<String> getAllCustomTagMetadataIds(boolean isActive) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.getAllCustomTagMetadataIds(connection, isActive);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static ArrayList<String> getAllCustomTagMetadataIds(String fileType, boolean isActive) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.getAllCustomTagMetadataIds(connection, fileType, isActive);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static Map<String, Object> getCustomTagMetaataDetailById(String id) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.getCustomTagMetaataDetailById(connection, id);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static JSONObject getAllCustomTagsMetadataDetail(String aoData) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			if (aoData == null) {
				CustomTagMetadataDAO.getAllCustomTagsMetadataDetail(connection);
			} else
				return CustomTagMetadataDAO.getAllCustomTagsMetadataDetail(connection, aoData);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static boolean isTagExist(String id) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.isTagExist(connection, id);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	// public static ArrayList<Map<String, String>>
	// getAllCustomTagsMetadataDetail() throws Exception {
	// Connection connection = null;
	//
	// try {
	// connection = CoreDBManager.getQueryIODBConnection();
	//
	// return CustomTagMetadataDAO.getAllCustomTagsMetadataDetail(connection);
	// } finally {
	// try {
	// CoreDBManager.closeConnection(connection);
	// } catch (Exception e) {
	// AppLogger.getLogger().fatal("Error closing database connection.", e);
	// }
	// }
	// }

	public static void insertCustomTagMetadatData(String id, String metadata, String desc, boolean isActive,
			String dbType, String fileType, String nameNodeId, String tableName, String dataTaggingTimeInfo,
			String jobNames) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			CustomTagMetadataDAO.insertCustomTagMetadatData(connection, id, metadata, desc, isActive, dbType, fileType,
					nameNodeId, tableName, dataTaggingTimeInfo, jobNames);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void updateCustomTagMetadatDataIsColumnValue(String id, boolean isActive) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			CustomTagMetadataDAO.updateCustomTagMetadatDataIsColumnValue(connection, id, isActive);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void updateCustomTagMetadatData(String id, String metadata, String desc, boolean isActive,
			String dataTaggingTimeInfo) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			CustomTagMetadataDAO.updateCustomTagMetadatData(connection, id, metadata, desc, isActive,
					dataTaggingTimeInfo);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	// Added to update MapredJobConfig table
	public static void updateMapRedJobConfig(String jobName, String jobArguments) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			MapRedJobConfigDAO.updateJobArguments(connection, jobName, jobArguments);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
	// Added for fetching JobNames from CUSTOMTAG_METADATA table
	// public static String getJobNameForDataTag(String id) throws Exception {
	// Connection connection = null;
	// String jobNames = null;
	// try {
	// connection = CoreDBManager.getQueryIODBConnection();
	//
	// jobNames = CustomTagMetadataDAO.selectJobNamesForDataTag(connection, id);
	// if (jobNames == null) {
	// AppLogger.getLogger().fatal("Job Name not found");
	// }
	// } finally {
	// try {
	// CoreDBManager.closeConnection(connection);
	// } catch (Exception e) {
	// AppLogger.getLogger().fatal("Error closing database connection.", e);
	// }
	// }
	// return jobNames;
	// }

	public static int activateCustomTagMetadatData(ArrayList ids, boolean isActivate) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return CustomTagMetadataDAO.activateCustomTagMetadatData(connection, ids, isActivate);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void deleteCustomTagMetadatData(ArrayList<String> ids) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			CustomTagMetadataDAO.deleteCustomTagMetadatData(connection, ids);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void deleteColumn(String nameNodeId, String tableName, String columnName) throws Exception {
		Connection connection = null;
		Configuration conf = null;
		try {
			conf = RemoteManager.getNameNodeConfiguration(nameNodeId);
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			CustomTagMetadataDAO.deleteColumn(connection, tableName, columnName);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void deleteColumnFromAllTables(String nameNodeId, String columnName) throws Exception {
		Connection connection = null;
		Configuration conf = null;
		try {
			conf = RemoteManager.getNameNodeConfiguration(nameNodeId);
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			ArrayList<String> metadataTables = CustomTagMetadataDAO.getAllTableNames(connection);
			for (String tableName : metadataTables) {
				CustomTagMetadataDAO.deleteColumn(connection, tableName, columnName);
			}
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void addColumnInAllMetadataTables(String nameNodeId, DBTypeProperties props, JSONObject tagsJson)
			throws Exception {
		Connection connection = null;
		Configuration conf = null;
		try {
			conf = RemoteManager.getNameNodeConfiguration(nameNodeId);
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);

			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			JSONArray tagsArr = ((JSONArray) tagsJson.get("Tags"));
			for (Object obj : tagsArr) {
				JSONObject tag = (JSONObject) obj;

				String columnType = (String) tag.get("dataType");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("columnType : " + columnType);

				String columnName = (String) tag.get("TagName");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("columnName : " + columnName);

				columnType = props.getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(columnType));

				if (props.getKeyFromValue(columnType).equals(MetadataConstants.STRING_WRAPPER_CLASS))
					columnType += "(" + UserDefinedTagUtils.MAX_COL_SIZE + ")";
				ArrayList<String> metadataTables = CustomTagMetadataDAO.getAllTableNames(connection);
				for (String tableName : metadataTables) {
					UserDefinedTagDAO.addColumn(connection, tableName, columnName, columnType);
				}
			}
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void addColumnInMetadataTables(String nameNodeId, DBTypeProperties props, String tableName,
			JSONObject tagsJson) throws Exception {
		Connection connection = null;
		Configuration conf = null;
		try {

			conf = RemoteManager.getNameNodeConfiguration(nameNodeId);
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			JSONArray tagsArr = ((JSONArray) tagsJson.get("Tags"));
			for (Object obj : tagsArr) {
				JSONObject tag = (JSONObject) obj;

				String columnType = (String) tag.get("dataType");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("columnType : " + columnType);

				String columnName = (String) tag.get("TagName");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("columnName : " + columnName);

				columnType = props.getTypeMap().get(MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(columnType));

				if (props.getKeyFromValue(columnType).equals(MetadataConstants.STRING_WRAPPER_CLASS))
					columnType += "(" + UserDefinedTagUtils.MAX_COL_SIZE + ")";

				UserDefinedTagDAO.addColumn(connection, tableName, columnName, columnType);
			}

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}
