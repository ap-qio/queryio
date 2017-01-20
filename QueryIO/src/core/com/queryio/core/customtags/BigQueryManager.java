package com.queryio.core.customtags;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.report.ReportConstants;
import com.queryio.common.report.ReportDesignHandler;
import com.queryio.common.report.ReportDesigner;
import com.queryio.common.report.ReportGenerator;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.core.adhoc.AdHocHiveClient;
import com.queryio.core.applications.ApplicationManager;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.bean.Chart;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.AdHocJobConfigDAO;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.dao.HiveTableDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.QueryExecutionDAO;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.job.definition.JobDefinitionDAO;

public class BigQueryManager {

	public static DWRResponse saveBigQuery(String namenodeId, String dbName, String jsonProperties) {

		DWRResponse dwrResponse = new DWRResponse();

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String user = RemoteManager.getLoggedInUser();

			JSONParser parser = new JSONParser();
			JSONObject properties = (JSONObject) parser.parse(jsonProperties);
			properties.put("username", user);
			String queryId = (String) properties.get(BigQueryIdentifiers.QUERYID);

			BigQueryDAO.deleteBigQuery(connection, queryId, namenodeId, user);
			BigQueryDAO.saveBigQuery(connection, queryId, (String) properties.get(BigQueryIdentifiers.QUERYDESC),
					properties, namenodeId, dbName, user);

			dwrResponse = checkRptDesignExists(connection, namenodeId, queryId, properties, true, true);

			if (dwrResponse.isTaskSuccess())
				dwrResponse.setDwrResponse(true, "Query saved successfully", 200);
			else {
				String resp = dwrResponse.getResponseMessage();
				dwrResponse.setDwrResponse(false, "Failed to save query: " + resp, 500);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to save query: " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse saveChart(String queryId, String jsonProperties) {

		DWRResponse dwrResponse = new DWRResponse();

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String user = RemoteManager.getLoggedInUser();

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to save query: " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse saveTable(String queryId, String jsonProperties) {

		DWRResponse dwrResponse = new DWRResponse();

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String user = RemoteManager.getLoggedInUser();

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to save query: " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse saveQuery(String queryId, String desc, String namenodeId, String dbName, String qs) {

		DWRResponse dwrResponse = new DWRResponse();

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String user = RemoteManager.getLoggedInUser();

			BigQueryDAO.deleteQuery(connection, queryId);
			BigQueryDAO.createQuery(connection, queryId, qs, desc, namenodeId, dbName, user);
			
			dwrResponse.setDwrResponse(true, "Query saved successfully", 200);
			
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to save query: " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse saveChartPreferences(String chartPreferencesJson) {

		DWRResponse dwrResponse = new DWRResponse();

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			JSONParser parser = new JSONParser();
			JSONObject preferencesProperties = (JSONObject) parser.parse(chartPreferencesJson);
			BigQueryDAO.saveChartPreferences(connection, preferencesProperties);
			dwrResponse.setDwrResponse(true, "Chart preferences saved successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to save chart preferences" + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static JSONObject getChartPreferences() {

		Connection connection = null;
		JSONObject chartPreferernces = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			chartPreferernces = BigQueryDAO.getChartPreferences(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return chartPreferernces;
	}

	public static JSONObject executeQuery(String namenodeId, String queryId, String userName, boolean forceCreateNew) {
		return executeQueryInternal(namenodeId, queryId, userName, forceCreateNew, forceCreateNew, true,
				ReportConstants.TYPE_HTML);
	}

	public static JSONObject executeQuery(String namenodeId, String queryId, String userName, boolean forceCreateNew,
			String format) {
		return executeQueryInternal(namenodeId, queryId, userName, forceCreateNew, forceCreateNew, true, format);
	}

	public static boolean isAdhocTable(String tableName, String nameNodeId) {
		boolean isAdhoc = false;

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_JOBNAME_FOR_TABLE_NAME);
			pst.setString(1, tableName);
			rs = pst.executeQuery();
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
						.debug("tableName : " + tableName + " " + QueryConstants.GET_JOBNAME_FOR_TABLE_NAME);
			if (rs != null) {
				if (rs.next()) {
					isAdhoc = true;
				}
			}

			if (!isAdhoc)
				isAdhoc = HiveTableDAO.doesHiveTableExist(connection, tableName, nameNodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(pst);
				DatabaseFunctions.closeResultSet(rs);
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return isAdhoc;
	}

	public static JSONObject isQueryAdhoc(String nameNodeId, String queryId, int executionId) {
		boolean isAdhoc = false;
		JSONObject json = new JSONObject();
		json.put("executionId", executionId);
		json.put("namenode", nameNodeId);
		json.put("queryId", queryId);
		try {
			JSONObject properties = getBigQueryInfo(nameNodeId, queryId, RemoteManager.getLoggedInUser());
			String selectedTable = "";
			if (properties != null) {
				JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);

				if ((allTables != null) && (allTables.size() > 0)) {
					selectedTable = (String) allTables.get(0);
				}
				isAdhoc = isAdhocTable(selectedTable, nameNodeId);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("selectedTable: " + selectedTable + " isAdhoc: " + isAdhoc);

				json.put("error", null);
				json.put("adhoc", isAdhoc);
			}
		} catch (Exception e) {
			json.put("error", e.getMessage());
			json.put("adhoc", isAdhoc);
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject executeQueryInternal(String namenodeId, String queryId, String userName,
			boolean forceCreateNew, boolean checkNCreate, boolean isNewThread, String format) {
		JSONObject json = new JSONObject();
		Connection connection = null;
		Connection customTagConnection = null;
		Connection hiveConnection = null;
		final String fileName = "C:\\fakpah\\BigQueryManager_executeQueryInternal.fixme";
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Configuration conf = RemoteManager.getNameNodeConfiguration(namenodeId);

			String dbName = BigQueryDAO.getBigQueryDbName(connection, queryId, namenodeId, userName);
			customTagConnection = RemoteManager.getCustomTagDBConnection(dbName);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("dbName :: " + dbName);
			JSONObject properties = BigQueryDAO.getBigQueryInfo(connection, queryId, namenodeId, userName);
			properties.put("isHiveQuery", false);
			boolean isAdHocId = false;
			boolean isHive = false;
			String applicationId = null;
			int executionId = -1;
			String selectedTable = null;
			String whereClause = null;
			String selectedType = null;

			if (properties != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("executequery - jsonProperties: " + properties.toJSONString());
				userName = (String) properties.get(BigQueryIdentifiers.USERNAME);
				JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);

				if ((allTables != null) && (allTables.size() > 0)) {
					selectedTable = (String) allTables.get(0);
				}
				String jobName = JobDefinitionDAO.getJobName(connection, selectedTable);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("selectedTable: " + selectedTable + " jobName: " + jobName);

				AdHocQueryBean adHocBean = AdHocQueryDAO.getAdHocInfoFromTable(connection, namenodeId, selectedTable);

				if (adHocBean != null) {
					isAdHocId = true;
					selectedType = adHocBean.getType();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("selectedTable: " + selectedTable + " jobName: " + jobName + " adHocId: "
										+ adHocBean.getAdHocId() + " adHocType: " + selectedType + " isAdHocId: "
										+ isAdHocId + " parseRecursive: " + adHocBean.isParseRecursive());
				}

				isHive = HiveTableDAO.doesHiveTableExist(connection, selectedTable, namenodeId);
				if (AppLogger.getLogger().isDebugEnabled()) {
					AppLogger.getLogger()
							.debug("HIVE RESULT TABLE selectedTable: " + selectedTable + " jobName: " + jobName
									+ " adHocId: " + selectedType + " isAdHocId: " + isAdHocId + " isHive: " + isHive);
					AppLogger.getLogger().debug("HiveTableDAO.getFileType(connection, selectedTable, namenodeId) : "
							+ HiveTableDAO.getFileType(connection, selectedTable, namenodeId));

				}
				boolean isPutOldResultTable = false;
				if (QueryIOConstants.ADHOC_TYPE_LOG
						.equalsIgnoreCase(HiveTableDAO.getFileType(connection, selectedTable, namenodeId))
						|| QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(selectedType)) {
					isHive = false;
					isPutOldResultTable = true;
				}

				if ((!isHive) && (jobName != null)) {
					executionId = QueryExecutionDAO.insert(connection, queryId, applicationId, null, null, namenodeId,
							userName);

					JSONObject whereJSON = (JSONObject) properties.get(BigQueryIdentifiers.SELECTEDWHERE);

					if ((whereJSON != null) && (whereJSON instanceof JSONObject)) {
						StringBuilder sb = new StringBuilder();
						Iterator it = whereJSON.keySet().iterator();
						while (it.hasNext()) {
							String key = (String) it.next();
							if (key != null) {
								JSONObject clause = (JSONObject) whereJSON.get(key);
								sb.append(key.trim());
								sb.append(((String) clause.get("roperator")).trim());
								sb.append(((String) clause.get("value")).trim());
								String loperator = (String) clause.get("loperator");
								if ((loperator != null) && (!loperator.equals(""))) {
									loperator = loperator.trim();
									if (loperator.equalsIgnoreCase("AND"))
										sb.append("] && [");
									else if (loperator.equalsIgnoreCase("OR"))
										sb.append("] || [");
								}
							}
						}
						whereClause = sb.toString();
					}

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("whereClause: " + whereClause);

					String resultTableName = null;

					resultTableName = (String) properties.get(BigQueryIdentifiers.RESULTTABLENAME);

					if (resultTableName != null)
						resultTableName.toLowerCase();

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("resultTableName: " + resultTableName);

					String arguments = null;

					if (isAdHocId)
						arguments = AdHocQueryDAO.getUpdatedArguments(connection, jobName, whereClause,
								resultTableName);
					else
						arguments = AdHocJobConfigDAO.getUpdatedArguments(connection, jobName, whereClause,
								resultTableName);

					JobDefinitionDAO.createResultTable(customTagConnection, resultTableName, selectedTable,
							conf.get(QueryIOConstants.CUSTOM_TAG_DB_CREATESTMT));

					boolean isFilterQuery = (Boolean) properties.get(BigQueryIdentifiers.ISFILTERQUERY);
					String filterQuery = null;
					if (isFilterQuery) {
						JSONObject filterQueryObj = (JSONObject) properties.get(BigQueryIdentifiers.QUERYFILTERDETAIL);
						if (filterQueryObj != null) {
							filterQuery = (String) filterQueryObj.get(BigQueryIdentifiers.FILTERQUERY);
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("filterQuery: " + filterQuery);
						}
					}
					QueryIOResponse response = ApplicationManager.runJobQuery(connection, jobName, arguments, isAdHocId,
							adHocBean.isParseRecursive(), isFilterQuery, filterQuery);

					if (response != null) {
						if (response.isSuccessful()) {
							applicationId = response.getResponseMsg();
						} else {
							QueryExecutionDAO.updatePathStatus(connection, executionId,
									"MapRed Job Execution Failed." + response.getResponseMsg(),
									QueryIOConstants.QUERYEXECUTION_STATUS_FAILED);
						}
					} else {
						QueryExecutionDAO.updatePathStatus(connection, executionId, "MapRed Job Execution Failed.",
								QueryIOConstants.QUERYEXECUTION_STATUS_FAILED);
					}

					if (applicationId != null) {
						QueryExecutionDAO.updateApplicationId(connection, executionId, applicationId);
					}

					// update properties stored in db with new tablename
					String sqlQuery = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
					if (isPutOldResultTable) {
						properties.put(BigQueryIdentifiers.OLD_RESULTTABLENAME,
								(String) properties.get(BigQueryIdentifiers.RESULTTABLENAME));
					}
					updateProperties(connection, properties, sqlQuery, selectedTable, resultTableName, allTables,
							queryId, namenodeId);

				} else {
					properties.put("isHiveQuery", false);

					boolean isCreateReport = false;

					executionId = QueryExecutionDAO.insert(connection, queryId, applicationId,
							QueryIOConstants.QUERYEXECUTION_STATUS_RUNNING, null, namenodeId, userName);
					if (((adHocBean != null) && (QueryIOConstants.ADHOC_TYPE_CSV.equalsIgnoreCase(adHocBean.getType())
							|| QueryIOConstants.ADHOC_TYPE_JSON.equalsIgnoreCase(adHocBean.getType())
							|| QueryIOConstants.ADHOC_TYPE_IISLOG.equalsIgnoreCase(adHocBean.getType())
							|| QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(adHocBean.getType()))) || isHive) {
						Configuration rmConf = null;
						HashMap configKeys = new HashMap();

						String rmId = null;

						if (isHive) {
							rmId = HiveTableDAO.getResourceManager(connection, selectedTable, namenodeId);
							rmConf = ConfigurationManager.getConfiguration(connection, rmId);
						} else {
							rmId = adHocBean.getRmId();
							rmConf = ConfigurationManager.getConfiguration(connection, rmId);
						}

						if (rmConf != null) {
							configKeys.put(QueryIOConstants.HIVE_YARN_RESOURCEMANAGER_ADDRESS,
									rmConf.get(QueryIOConstants.HIVE_YARN_RESOURCEMANAGER_ADDRESS));
							if (adHocBean != null) {
								configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILEPATH_FILTER,
										adHocBean.getFilePathPattern());
								configKeys.put(QueryIOConstants.HIVE_QUERYIO_PARSE_RECURSIVE,
										adHocBean.isParseRecursive());

								// Support recursive file paths when hive
								// collects data from HDFS
								// configKeys.put(QueryIOConstants.HIVE_QUERYIO_MAPREDUCE_INPUT_FILEINPUTFORMAT_INPUT_DIR_RECURSIVE,
								// adHocBean.isParseRecursive());
								// configKeys.put(QueryIOConstants.HIVE_QUERYIO_QUERYIO_HIVE_FILTER_QUERY,
								// adHocBean.isParseRecursive());
							}

							// No need to update hive-site.xml, just set
							// properties at run time in session.
							// DWRResponse response =
							// QueryIOAgentManager.updateHiveSiteConfiguration(namenodeId,
							// configKeys, configValues);
						}

						boolean isFilterQuery = false;
						isFilterQuery = (Boolean) properties.get(BigQueryIdentifiers.ISFILTERQUERY);
						configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_APPLY, isFilterQuery);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("isFilterQuery: " + isFilterQuery);

						if (isFilterQuery) {
							JSONObject filterQueryObj = (JSONObject) properties
									.get(BigQueryIdentifiers.QUERYFILTERDETAIL);
							if (filterQueryObj != null) {
								String filterQuery = (String) filterQueryObj.get(BigQueryIdentifiers.FILTERQUERY);
								configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_QUERY, filterQuery);
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("filterQuery: " + filterQuery);
							}
						}

						boolean persistResults = false;
						persistResults = (Boolean) properties.get(BigQueryIdentifiers.PERSISTRESULTS);

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("persistResults: " + persistResults);

						hiveConnection = AdHocHiveClient.getHiveConnection(connection, namenodeId);

						AdHocHiveClient.setConfiguration(hiveConnection, configKeys);

						if (persistResults) {
							String resultTableName = null;

							resultTableName = (String) properties.get(BigQueryIdentifiers.RESULTTABLENAME);
							String sqlQuery = (String) properties.get(BigQueryIdentifiers.SQLQUERY);

							if (resultTableName != null)
								resultTableName.toLowerCase();

							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("persistResults: " + persistResults + " resultTableName: "
										+ resultTableName + " sqlQuery: " + sqlQuery);

							JobDefinitionDAO.createResultTable(customTagConnection, resultTableName, selectedTable,
									conf.get(QueryIOConstants.CUSTOM_TAG_DB_CREATESTMT));

							AdHocHiveClient.createHiveTable(hiveConnection,
									AdHocHiveClient.getQueryStringPersist(resultTableName, sqlQuery));

							HiveTableDAO.insertHiveTable(connection, resultTableName, namenodeId, rmId,
									HiveTableDAO.getFileType(connection, selectedTable, namenodeId), fileName);

							// properties.put(BigQueryIdentifiers.ISFILTERQUERY,
							// false);
							// properties.put(BigQueryIdentifiers.QUERYFILTERDETAIL,new
							// JSONObject());

							updateProperties(connection, properties, sqlQuery, selectedTable, resultTableName,
									allTables, queryId, namenodeId);

							// configKeys = new HashMap();
							// configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILEPATH_FILTER,
							// QueryIOConstants.HIVE_QUERYIO_FILEPATH_FILTER_SELECT_ALL);
							// configKeys.put(QueryIOConstants.HIVE_QUERYIO_FILTER_APPLY,
							// false);
							//
							// AdHocHiveClient.setConfiguration(hiveConnection,
							// configKeys);

							isCreateReport = true;
						}
						String query = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
						if (isPutOldResultTable) {
							query = query.replace(selectedTable,
									(String) properties.get(BigQueryIdentifiers.RESULTTABLENAME));
						}
						BigQueryDAO.writeQueryResultInJSONFile(hiveConnection, true, query, namenodeId, queryId,
								userName, properties);

						properties.put("isHiveQuery", true);
					} else {
						AppLogger.getLogger().debug("Else writeQueryResultInJSONFile " + queryId);
						BigQueryDAO.writeQueryResultInJSONFile(customTagConnection, false,
								(String) properties.get(BigQueryIdentifiers.SQLQUERY), namenodeId, queryId, userName,
								properties);
						// properties.put("isHiveQuery", false);
					}

					String designFilePath = getDesignFilePath(namenodeId, queryId,
							(String) properties.get(BigQueryIdentifiers.USERNAME));

					if (!isCreateReport) {
						AppLogger.getLogger().debug("queryid " + queryId);
						DWRResponse dwrResponse = checkRptDesignExists(connection, namenodeId, queryId, properties,
								forceCreateNew, checkNCreate);
						if (dwrResponse.isTaskSuccess())
							isCreateReport = true;
						else
							throw new Exception("Exception: " + dwrResponse.getResponseMessage());
					}

					if (isCreateReport) {

						if (isNewThread)
							ReportDesigner.buildReportInNewThread(properties, designFilePath, executionId, format);
						else
							ReportDesigner.buildReport(properties, designFilePath, executionId, format);
					}
				}
			}
			json.put("executionId", executionId);
			json.put("error", null);
		} catch (Exception e) {
			json.put("executionId", null);
			json.put("error", e.getMessage());
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(customTagConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
			try {
				DatabaseManager.closeDbConnection(hiveConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}

		return json;
	}

	private static void updateProperties(Connection connection, JSONObject properties, String sqlQuery,
			String selectedTable, String resultTableName, JSONArray allTables, String queryId, String namenodeId)
			throws Exception {
		// sqlQuery = sqlQuery.replace(selectedTable, resultTableName);
		properties.remove(BigQueryIdentifiers.RESULTTABLENAME);
		// properties.put(BigQueryIdentifiers.SQLQUERY, sqlQuery);
		// properties.put(BigQueryIdentifiers.PERSISTRESULTS, false);
		JSONObject resp = BigQueryDAO.getResultTableName(connection, selectedTable);
		if (resp != null) {
			resultTableName = (String) resp.get("resultTableName");
		}
		properties.put(BigQueryIdentifiers.RESULTTABLENAME, resultTableName);

		JSONArray newTables = new JSONArray();

		// for (int i=0; i<allTables.size(); i++)
		// {
		// String tempTable = (String) allTables.get(i);
		// if ((tempTable != null) && tempTable.equalsIgnoreCase(selectedTable))
		// {
		// newTables.add(resultTableName);
		// }
		// else
		// newTables.add(tempTable);
		// }
		//// properties.remove(BigQueryIdentifiers.SELECTEDTABLE);
		// properties.put(BigQueryIdentifiers.SELECTEDTABLE, newTables);

		DWRResponse dwrResponse = checkRptDesignExists(connection, namenodeId, queryId, properties, true, true);

		if (!dwrResponse.isTaskSuccess())
			throw new Exception("Exception: " + dwrResponse.getResponseMessage());
	}

	public static JSONObject getResultTableName(String selectedTable, String nameNodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			JSONObject response = BigQueryDAO.getResultTableName(connection, selectedTable);
			response.put("adhoc", isAdhocTable(selectedTable, nameNodeId));
			response.put("tableName", selectedTable);
			return response;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
		return null;
	}

	public static int getQueryExecutionId() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return BigQueryDAO.getQueryExecutionId(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
		return 0;
	}

	public static boolean deleteSpreadSheetQueryStatus(String queryId, String namenodeId, String userName) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			BigQueryDAO.deleteSpreadSheetQueryStatus(connection, queryId, namenodeId, userName);
			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			return false;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}

	}

	public static JSONObject getSpreadSheetQueryStatus(String queryId, String namenodeId, String userName) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return BigQueryDAO.getSpreadSheetQueryStatus(connection, queryId, namenodeId, userName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
		return null;
	}

	public static JSONObject executeQueryReportFormat(String jsonProperties, String format) {
		JSONObject json = new JSONObject();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			JSONParser parser = new JSONParser();
			JSONObject properties = (JSONObject) parser.parse(jsonProperties);

			String queryId = (String) properties.get(BigQueryIdentifiers.QUERYID);
			String namenodeId = (String) properties.get(BigQueryIdentifiers.NAMENODEID);

			DWRResponse dwrResponse = checkRptDesignExists(connection, namenodeId, queryId, properties, true, true);

			if (!dwrResponse.isTaskSuccess())
				throw new Exception("Exception: " + dwrResponse.getResponseMessage());

			String designFilePath = getDesignFilePath(namenodeId, queryId,
					(String) properties.get(BigQueryIdentifiers.USERNAME));

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("executequery - jsonProperties: " + jsonProperties);

			if (!(ReportConstants.TYPE_HTML.equalsIgnoreCase(format)
					|| ReportConstants.TYPE_PDF.equalsIgnoreCase(format)
					|| ReportConstants.TYPE_XLS.equalsIgnoreCase(format))) {
				format = ReportConstants.TYPE_HTML;
			}

			String outputFileName = ReportGenerator.generateViewReport(designFilePath, format);

			json.put("filePath", outputFileName);
			json.put("error", null);
		} catch (Exception e) {
			json.put("filePath", null);
			json.put("error", e.getLocalizedMessage());
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if (connection != null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return json;
	}

	public static ArrayList getAllBigQueriesId(String namenodeId) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return BigQueryDAO.getAllBigQueriesId(connection, namenodeId);
		} catch (Throwable e) {
			e.printStackTrace();
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

	@SuppressWarnings("unchecked")
	public static JSONObject getBigQueryInfo(String namenodeId, String queryId, String userName) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			JSONObject json = BigQueryDAO.getBigQueryInfo(connection, queryId, namenodeId, userName);
			// json.put("oldColumns", getBigQuerySavedColumnInfo(namenodeId,
			// queryId, userName)); //FIXME: Not being used anywhere.
			return json;
		} catch (Throwable e) {
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

	public static JSONArray getBigQuerySavedColumnInfo(String namenodeId, String queryId, String userName) {
		BufferedReader jsonFile = null;
		try {
			String oldColumnsListJSONFile = EnvironmentalConstants.getReportsDirectory() + File.separatorChar
					+ userName.toLowerCase() + File.separatorChar + namenodeId.toLowerCase() + File.separatorChar
					+ queryId.toLowerCase() + ".json";

			AppLogger.getLogger().debug("oldColumnsListJSONFile : " + oldColumnsListJSONFile);
			AppLogger.getLogger()
					.debug("oldColumnsListJSONFile isExist : " + new File(oldColumnsListJSONFile).exists());
			AppLogger.getLogger().debug("oldColumnsListJSONFile length : " + new File(oldColumnsListJSONFile).length());
			jsonFile = new BufferedReader(new FileReader(new File(oldColumnsListJSONFile)));
			String line = null;
			StringBuilder jsonString = new StringBuilder();
			while ((line = jsonFile.readLine()) != null) {
				jsonString.append(line);
			}
			AppLogger.getLogger().debug("savedColumn jsonString : " + jsonString.toString());
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonString.toString());
			JSONArray array = (JSONArray) jsonObj.get("columns");
			array.add("FakeTest"); // FIXME: While sending this array to UI,
									// last element is getting disappeared. So
									// added this fake element for proper
									// functioning.
			return array;

		} catch (Throwable e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			if (jsonFile != null) {
				try {
					jsonFile.close();
				} catch (IOException e) {
					AppLogger.getLogger().fatal("Error closing file ", e);
				}
			}
		}
		return null;
	}

	public static JSONObject getAllBigQueriesInfo(String namenodeId) {
		JSONObject allBigQueriesInfo = getAllBigQueriesInfo(namenodeId, null);
		AppLogger.getLogger().fatal("getAllBigQueriesInfo::::::" + allBigQueriesInfo.toJSONString());
		return allBigQueriesInfo;
	}

	public static JSONObject getAllBigQueriesInfo(String namenodeId, String paramsDT) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String user = RemoteManager.getLoggedInUser();
			if (paramsDT == null)
				return BigQueryDAO.getAllBigQueriesInfo(connection, namenodeId, user);
			else
				return BigQueryDAO.getAllBigQueriesInfo(connection, namenodeId, user, paramsDT);
		} catch (Throwable e) {
			e.printStackTrace();
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

	public static DWRResponse getAllChartsInfo() {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			List<Chart> charts = BigQueryDAO.getAllCharts(connection);
			dwrResponse.setDwrResponse(true, new ObjectMapper().writeValueAsString(charts), 200);
		} catch (Throwable e) {
			e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static String exportBigQueryReport(String namenodeId, String queryId, String formatType) {
		String outputFileName = null;

		try {
			String designFilePath = getDesignFilePath(namenodeId, queryId, RemoteManager.getLoggedInUser());
			outputFileName = ReportGenerator.generateViewReport(designFilePath, formatType); // Either
																								// PDF
																								// or
																								// XLS.
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in report generation: " + e.getLocalizedMessage(), e);
		}

		return outputFileName;
	}

	public static DWRResponse deleteBigQuery(String namenodeId, String queryId) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(queryId);
		try {
			String user = RemoteManager.getLoggedInUser();
			connection = CoreDBManager.getQueryIODBConnection();
			BigQueryDAO.deleteBigQuery(connection, queryId, namenodeId, user);
			dwrResponse.setDwrResponse(true, "Query deleted successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false, "Failed to delete query: " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
		}
		return dwrResponse;
	}

	public static JSONObject isQueryComplete(int executionId) {
		return isQueryCompleteInternal(executionId, true, ReportConstants.TYPE_HTML, RemoteManager.getLoggedInUser());
	}

	public static JSONObject isQueryCompleteInternal(int executionId, boolean isNewThread, String format,
			String username) {
		JSONObject result = new JSONObject();
		result.put("executionId", executionId);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String applicationId = QueryExecutionDAO.getApplicationId(connection, executionId);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("applicationId: " + applicationId);

			String status = null;
			boolean generateReport = false;
			if (applicationId != null) {
				String applicationStatus = ApplicationManager.getApllicationStatus(applicationId);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("applicationStatus: " + applicationStatus);

				if (applicationStatus.equals(FinalApplicationStatus.SUCCEEDED.toString())) {

					writeResultInJSONFile(connection, executionId);

					QueryExecutionDAO.updateApplicationId(connection, executionId, null);
					generateReport = true;
				} else if (applicationStatus.equals(FinalApplicationStatus.FAILED.toString())
						|| applicationStatus.equals(FinalApplicationStatus.KILLED.toString())) {
					QueryExecutionDAO.updateApplicationId(connection, executionId, null);
					status = QueryIOConstants.QUERYEXECUTION_STATUS_FAILED;
					String path = "Mapred Job failed.";
					QueryExecutionDAO.updatePathStatus(connection, executionId, path, status);

					result.put("appStatus", false);
					result.put("filePath", path);
					result.put("error", true);

				} else if (applicationStatus.equals(FinalApplicationStatus.UNDEFINED.toString())) {
					status = QueryIOConstants.QUERYEXECUTION_STATUS_RUNNING;

					result.put("appStatus", false);
					result.put("filePath", null);
					result.put("error", false);
				}
			} else {
				String currentStatus = QueryExecutionDAO.getStatus(connection, executionId);

				AppLogger.getLogger().debug("executionId: " + executionId + " currentStatus: " + currentStatus);

				if (currentStatus == null) {
					generateReport = true;
				} else {
					if (currentStatus.equals(QueryIOConstants.QUERYEXECUTION_STATUS_SUCCESS)) {
						result.put("appStatus", true);
						result.put("filePath", QueryExecutionDAO.getPath(connection, executionId));
						result.put("error", false);
						dropQueryTable(executionId, connection, username);
					} else if (currentStatus.equals(QueryIOConstants.QUERYEXECUTION_STATUS_FAILED)) {
						result.put("appStatus", true);
						result.put("filePath", QueryExecutionDAO.getPath(connection, executionId));
						result.put("error", true);
						dropQueryTable(executionId, connection, username);
					}

				}
			}
			if (generateReport) {
				QueryExecutionDAO.updateStatus(connection, executionId, QueryIOConstants.QUERYEXECUTION_STATUS_RUNNING);
				String[] ids = QueryExecutionDAO.getQueryId(connection, executionId);
				if (ids != null) {
					String queryId = ids[0];
					String namenodeId = ids[1];
					JSONObject properties = BigQueryDAO.getBigQueryInfo(connection, queryId, namenodeId,
							RemoteManager.getLoggedInUser());
					String designFilePath = getDesignFilePath(namenodeId, queryId,
							(String) properties.get(BigQueryIdentifiers.USERNAME));

					DWRResponse dwrResponse = checkRptDesignExists(connection, namenodeId, queryId, properties, false,
							false);

					if (dwrResponse.isTaskSuccess()) {
						if (isNewThread)
							ReportDesigner.buildReportInNewThread(properties, designFilePath, executionId, format);
						else
							ReportDesigner.buildReport(properties, designFilePath, executionId, format);
					} else
						throw new Exception("Exception: " + dwrResponse.getResponseMessage());

					result.put("appStatus", true);
					result.put("filePath", null);
					result.put("error", false);
				} else {
					String path = "Query not present in DB for the specified namenode. Please save query first and then try again.";
					QueryExecutionDAO.updatePathStatus(connection, executionId, path,
							QueryIOConstants.QUERYEXECUTION_STATUS_SUCCESS);
					result.put("appStatus", true);
					result.put("filePath", path);
					result.put("error", true);
				}
			}
		} catch (Throwable e) {
			result.put("appStatus", false);
			result.put("filePath", e.getMessage());
			result.put("error", true);
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
		}

		return result;
	}

	private static void dropQueryTable(int executionId, Connection connection, String username) // drop
																								// query
																								// table
																								// in
																								// case
																								// of
																								// persist
																								// reault
																								// set
																								// to
																								// false
																								// and
																								// file
																								// type
																								// is
																								// xml
																								// of
																								// log
			throws SQLException, Exception {
		String[] ids = QueryExecutionDAO.getQueryId(connection, executionId);
		if (ids != null) {
			String queryId = ids[0];
			String namenodeId = ids[1];
			JSONObject properties = BigQueryDAO.getBigQueryInfo(connection, queryId, namenodeId, username);

			if (properties != null) {
				JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);
				String selectedTable = null;
				if ((allTables != null) && (allTables.size() > 0)) {
					selectedTable = (String) allTables.get(0);
					boolean isHive = HiveTableDAO.doesHiveTableExist(connection, selectedTable, namenodeId);
					String fileType = HiveTableDAO.getFileType(connection, selectedTable, namenodeId);
					if (isHive && (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(fileType)
							|| QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(fileType))) {
						String oldResultTable = (String) properties.get(BigQueryIdentifiers.OLD_RESULTTABLENAME);
						if ((Boolean) properties.get(BigQueryIdentifiers.PERSISTRESULTS) == false) {
							AppLogger.getLogger().debug("drop old result table:  " + oldResultTable);
							String dbName = BigQueryDAO.getBigQueryDbName(connection, queryId, namenodeId,
									RemoteManager.getLoggedInUser());
							dropBigQueryTable(dbName, new String[] { oldResultTable });
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void writeResultInJSONFile(Connection connection, int executionId) {
		Connection customTagConnection = null;
		try {
			Map<String, String> details = QueryExecutionDAO.getQueryDetailsByExecutionId(connection, executionId);

			String namenodeId = details.get(ColumnConstants.COL_QUERYEXECUTION_NAMENODEID);
			String queryId = details.get(ColumnConstants.COL_QUERYEXECUTION_QUERY_ID);
			String userName = details.get(ColumnConstants.COL_QUERYEXECUTION_USERNAME);

			JSONObject properties = BigQueryDAO.getBigQueryInfo(connection, queryId, namenodeId, userName);
			String query = (String) properties.get(BigQueryIdentifiers.SQLQUERY);

			if (properties.get(BigQueryIdentifiers.OLD_RESULTTABLENAME) == null) {
				return;
			}

			String selectedTable = null;
			JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);
			String oldResultTable = (String) properties.get(BigQueryIdentifiers.OLD_RESULTTABLENAME);
			if ((allTables != null) && (allTables.size() > 0)) {
				selectedTable = (String) allTables.get(0);
				query = query.replace(selectedTable, oldResultTable);
			}

			String dbName = BigQueryDAO.getBigQueryDbName(connection, queryId, namenodeId, userName);
			customTagConnection = RemoteManager.getCustomTagDBConnection(dbName);

			BigQueryDAO.writeQueryResultInJSONFile(customTagConnection, false, query, namenodeId, queryId, userName,
					properties);

			// if((Boolean) properties.get(BigQueryIdentifiers.PERSISTRESULTS)
			// == false) {
			// dropBigQueryTable(dbName, new String [] {oldResultTable});
			// }

		} catch (Throwable th) {
			AppLogger.getLogger().fatal("Error wrting Result in JSON file.", th);
		} finally {
			try {
				CoreDBManager.closeConnection(customTagConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
		}
	}

	public static DWRResponse dropBigQueryTable(String databaseName, String[] tableList) {
		DWRResponse response = new DWRResponse();
		Connection connection = null;
		Connection customTagConnection = null;
		String nameNodeId = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			nameNodeId = NodeDAO.getNameNodeForAnalyticsDBNameMapping(connection, databaseName);

			customTagConnection = RemoteManager.getCustomTagDBConnection(databaseName);

			if (tableList == null)
				throw new Exception("Table List Empty.");

			for (int i = 0; i < tableList.length; i++) {
				BigQueryDAO.dropBigQueryTable(customTagConnection, tableList[i]);
				if (nameNodeId != null) {
					if (HiveTableDAO.doesHiveTableExist(connection, tableList[i], nameNodeId)) {
						AdHocHiveClient.dropHiveTable(tableList[i], nameNodeId);
						HiveTableDAO.deleteHiveTable(connection, tableList[i], nameNodeId);
					}
				}
			}

			response.setDwrResponse(true, "Table deleted successfully.", 200);
		} catch (Exception e) {
			response.setDwrResponse(false, e.getMessage(), 200);
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(customTagConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
		}

		return response;
	}

	public static DWRResponse clearBigQueryTable(String databaseName, String[] tableList) {
		DWRResponse response = new DWRResponse();
		Connection connection = null;
		try {
			if (tableList == null)
				throw new Exception("Table List Empty.");
			connection = RemoteManager.getCustomTagDBConnection(databaseName);

			for (int i = 0; i < tableList.length; i++)
				BigQueryDAO.clearBigQueryTable(connection, tableList[i]);

			response.setDwrResponse(true, "Table cleared successfully.", 200);
		} catch (Exception e) {
			response.setDwrResponse(false, e.getMessage(), 200);
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}

		}

		return response;
	}

	public static SummaryTable viewSchemaBigQueryTable(String databaseName, String tableName) {

		SummaryTable table = null;
		Connection connection = null;
		try {
			connection = RemoteManager.getCustomTagDBConnection(databaseName);

			table = BigQueryDAO.viewSchemaBigQueryTable(connection, tableName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection: " + e.getLocalizedMessage(), e);
			}
		}

		return table;
	}

	public static DWRResponse checkRptDesignExists(Connection connection, String namenodeId, String queryId,
			JSONObject properties, boolean forceCreateNew, boolean checkNCreate) {
		boolean exists = true;
		DWRResponse dwrResponse = new DWRResponse();
		try {

			String designFilePath = getDesignFilePath(namenodeId, queryId,
					(String) properties.get(BigQueryIdentifiers.USERNAME));
			File rptDesignFile = new File(designFilePath);

			if (forceCreateNew) {
				if (rptDesignFile.exists())
					rptDesignFile.delete();
				new ReportDesignHandler().createReport(properties, designFilePath);
			} else {
				if (checkNCreate) {
					if (!rptDesignFile.exists())
						new ReportDesignHandler().createReport(properties, designFilePath);
				}
			}

			exists = rptDesignFile.exists();
			if (exists)
				dwrResponse.setDwrResponse(true, "", 200);
			else
				dwrResponse.setDwrResponse(false, "Design File for Birt Report Generation does not exists.", 500);

			BigQueryDAO.updateBigQuery(connection, queryId, properties, namenodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in function checkRptDesignExists(): " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}

		return dwrResponse;
	}

	public static String getDesignFilePath(String namenodeId, String queryId, String username) throws Exception {
		File namenodeDirectory = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
				+ username.toLowerCase() + File.separator + namenodeId.replaceAll(" ", "_").toLowerCase());
		if (!namenodeDirectory.exists())
			namenodeDirectory.mkdirs();
		String designFilePath = namenodeDirectory.getAbsolutePath() + File.separator
				+ queryId.replaceAll(" ", "_").toLowerCase() + ".rptdesign";
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("designFilePath: " + designFilePath);
		return designFilePath;
	}

	public static void getSpreadSheetResults(String queryId, String namenodeId, String userName, PrintWriter writer,
			boolean isRunQuery) {
		Connection connection = null;
		Connection secondaryConnection = null;
		JSONObject properties = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP1", "SUCCESS",
					"", false);

			properties = getBigQueryInfo(namenodeId, queryId, userName);
			if (properties != null) {
				writer.write("{");
				writer.write("\"title\": ");
				writer.write("\"" + queryId + "\"");

				boolean isResultExist = BigQueryManager.checkIfResultFileExist(queryId, namenodeId, userName);
				if (isRunQuery) {
					AppLogger.getLogger().debug("executequery - jsonProperties: " + properties.toJSONString());
					executeQuery(namenodeId, queryId, userName, true);
					BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP2",
							"SUCCESS", "", true);
					BigQueryManager.getSpreadSheetResultFromDisk(writer, connection, namenodeId, queryId, userName,
							properties);
				} else {
					if (isResultExist) {
						AppLogger.getLogger().debug("Result Found");
						BigQueryManager.getSpreadSheetResultFromDisk(writer, connection, namenodeId, queryId, userName,
								properties);
						BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP2",
								"SUCCESS", "", true);
					} else {
						AppLogger.getLogger().debug("Result Not Found");
						BigQueryManager.getEmptySpreadSheetResult(writer, connection, namenodeId, queryId, userName,
								properties);
						BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP2",
								"SUCCESS", "", true);

					}
				}

				writer.write("}");
			}

			AppLogger.getLogger().debug("writer flused with data");

		} catch (Exception e) {
			BigQueryDAO.updateSpreadSheetQueryError(connection, queryId, namenodeId, userName, e.getMessage());
			try {
				BigQueryManager.getEmptySpreadSheetResult(writer, connection, namenodeId, queryId, userName,
						properties);
			} catch (Exception e1) {
				AppLogger.getLogger().fatal(e.getMessage(), e1);
			}
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				DatabaseManager.closeDbConnection(secondaryConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static boolean checkIfResultFileExist(String queryId, String namenodeId, String userName) throws Exception {

		File file = new File(EnvironmentalConstants.getReportsDirectory() + File.separator + userName.toLowerCase()
				+ File.separator + namenodeId.toLowerCase() + File.separator + queryId.toLowerCase() + ".json");
		return file.exists();

	}

	@SuppressWarnings("unchecked")
	public static void getEmptySpreadSheetResult(PrintWriter writer, Connection connection, String namenodeId,
			String queryId, String userName, JSONObject properties) throws Exception {

		try {
			BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP3", "SUCCESS",
					"", true);

			JSONObject widthObject = new JSONObject();
			JSONArray columnWidths = new JSONArray();
			JSONArray columnsArray = new JSONArray();
			JSONObject column = null;

			columnWidths.add("120px");
			column = new JSONObject();
			column.put("value", "No data found for query " + queryId + ". Please press view button to execute query.");
			columnsArray.add(0, column);

			columnWidths.add("120px");
			widthObject.put("widths", columnWidths);
			writer.write(",");
			writer.write("\"metadata\": ");
			writer.write(widthObject.toJSONString());
			JSONObject row = null;
			writer.write(",");
			writer.write("\"rows\": [");

			row = new JSONObject();
			row.put("height", "18px");
			row.put("columns", columnsArray);

			writer.write(row.toJSONString());

			writer.write("]");

			BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP4", "SUCCESS",
					"", true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error in writing spread sheet data reading ", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static void getSpreadSheetResultFromDisk(PrintWriter writer, Connection connection, String namenodeId,
			String queryId, String userName, JSONObject properties) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Method : getSpreadSheetResultFromDisk");
		FileReader reader = null;
		try {
			BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP3", "SUCCESS",
					"", true);

			JSONParser parser = new JSONParser();

			String filePath = EnvironmentalConstants.getReportsDirectory() + File.separator + userName.toLowerCase()
					+ File.separator + namenodeId.toLowerCase() + File.separator + queryId.toLowerCase() + ".json";
			File file = new File(filePath);

			if (file.exists()) {
				AppLogger.getLogger().debug("File Size " + file.length());
				if (file.length() == 0) {
					BigQueryManager.getEmptySpreadSheetResult(writer, connection, namenodeId, queryId, userName,
							properties);
					return;
				}

				reader = new FileReader(file);

				JSONObject obj = (JSONObject) parser.parse(reader);

				JSONArray columnList = (JSONArray) obj.get("columns");

				JSONArray rows = (JSONArray) obj.get("rows");
				if (rows.size() == 0) {
					BigQueryManager.getEmptySpreadSheetResult(writer, connection, namenodeId, queryId, userName,
							properties);
					return;
				}
				JSONObject widthObject = new JSONObject();
				JSONArray columnWidths = new JSONArray();
				JSONArray columnsArray = new JSONArray();
				JSONObject column = null;
				for (int i = 0; i < columnList.size(); i++) {
					columnWidths.add("120px");
					column = new JSONObject();
					column.put("value", ((String) columnList.get(i)).toUpperCase());
					columnsArray.add(i, column);
				}
				widthObject.put("widths", columnWidths);
				writer.write(",");
				writer.write("\"metadata\": ");
				writer.write(widthObject.toJSONString());

				JSONObject row = null;

				writer.write(",");
				writer.write("\"rows\": [");

				row = new JSONObject();
				row.put("height", "18px");
				row.put("columns", columnsArray);

				writer.write(row.toJSONString());

				int recordCount = 0;

				while (recordCount < rows.size()) {
					JSONObject rowData = (JSONObject) rows.get(recordCount);
					// Iterator<Object>it = rowData.keySet().iterator();
					writer.write(",");
					row = new JSONObject();
					columnsArray = new JSONArray();

					for (int i = 0; i < columnList.size(); i++) {
						column = new JSONObject();
						String key = (String) columnList.get(i);
						String value = (String) rowData.get(key);
						if ("null".equalsIgnoreCase(value) || "\\N".equalsIgnoreCase(value)) {
							value = "";
						}
						column.put("value", value);
						columnsArray.add(i, column);

					}
					row.put("height", "18px");
					row.put("columns", columnsArray);
					writer.write(row.toJSONString());
					recordCount++;
				}
			}
			AppLogger.getLogger().fatal("writer " + writer);
			writer.write("]");

			BigQueryDAO.addUpdateSpreadSheetQueryStatus(connection, queryId, namenodeId, userName, "STEP4", "SUCCESS",
					"", true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error in writing spread sheet data reading ", e);
		} finally {
			if (reader != null)
				reader.close();

		}

	}

	public static JSONObject getSpreadSheetSlickResultsMetadata(String queryId, String namenodeId) {
		Connection connection = null;
		Connection customTagConnection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			String dbName = BigQueryDAO.getBigQueryDbName(connection, queryId, namenodeId,
					RemoteManager.getLoggedInUser());
			customTagConnection = RemoteManager.getCustomTagDBConnection(dbName);

			JSONObject properties = getBigQueryInfo(namenodeId, queryId, RemoteManager.getLoggedInUser());

			if (properties != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("executequery - jsonProperties: " + properties.toJSONString());

				String sqlQuery = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
				String countQuery = (String) properties.get(BigQueryIdentifiers.SQLQUERY);

				if (countQuery != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("SELECT COUNT(*) ");
					sb.append(countQuery.substring(countQuery.indexOf(" FROM ")));

					countQuery = sb.toString();
				}

				return BigQueryDAO.getSpreadSheetSlickResultsMetadata(customTagConnection, sqlQuery, countQuery);
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				CoreDBManager.closeConnection(customTagConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static void getSpreadSheetSlickResults(String queryId, String namenodeId, int count, int offset,
			PrintWriter writer) {
		Connection connection = null;
		Connection secondaryConnection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			JSONObject properties = getBigQueryInfo(namenodeId, queryId, RemoteManager.getLoggedInUser());

			if (properties != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("executequery - jsonProperties: " + properties.toJSONString());

				String sqlQuery = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
				boolean setLimitResultRows = (Boolean) properties.get(BigQueryIdentifiers.SETLIMITRESULTROWS);
				int limitResultRowsValue = Integer
						.parseInt(String.valueOf(properties.get(BigQueryIdentifiers.LIMITRESULTROWSVALUE)));

				String selectedTable = null;
				JSONArray allTables = (JSONArray) properties.get(BigQueryIdentifiers.SELECTEDTABLE);

				if ((allTables != null) && (allTables.size() > 0)) {
					selectedTable = (String) allTables.get(0);
				}

				String query = sqlQuery;

				if (setLimitResultRows) {
					if (offset > limitResultRowsValue)
						offset = limitResultRowsValue;
					if ((offset + count) > limitResultRowsValue)
						count = (offset + count) - limitResultRowsValue;
				}

				StringBuilder sb = new StringBuilder();
				sb.append(sqlQuery);
				sb.append(" LIMIT ");
				sb.append(count);
				sb.append(" OFFSET ");
				sb.append(offset);
				query = sb.toString();

				writer.write("{");
				writer.write("\"count\": " + count + "");
				writer.write(",");
				writer.write("\"stories\": [");

				boolean isHive = HiveTableDAO.doesHiveTableExist(connection, selectedTable, namenodeId);

				if (isHive) {

					secondaryConnection = AdHocHiveClient.getHiveConnection(connection, namenodeId);
					BigQueryDAO.getSpreadSheetSlickResults(secondaryConnection, query, offset, writer);
				} else {
					String dbName = BigQueryDAO.getBigQueryDbName(connection, queryId, namenodeId,
							RemoteManager.getLoggedInUser());
					secondaryConnection = RemoteManager.getCustomTagDBConnection(dbName);
					BigQueryDAO.getSpreadSheetSlickResults(secondaryConnection, query, offset, writer);
				}

				writer.write("]");
				writer.write("}");
			}

			writer.write("");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				DatabaseManager.closeDbConnection(secondaryConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void saveSpreadSheet(String sheetId, String nameNodeId, InputStream stream) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			String path = getSpreadSheetPath(sheetId, nameNodeId);

			writeSheetToPath(path, stream);
			SpreadSheetDAO.insert(connection, sheetId, path, nameNodeId);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	private static String getSpreadSheetPath(String sheetId, String nameNodeId) throws Exception {
		String path = null;

		File namenodeDirectory = new File(EnvironmentalConstants.getSpreadSheetsDirectory() + File.separator
				+ nameNodeId.replaceAll(" ", "_").toLowerCase());
		if (!namenodeDirectory.exists())
			namenodeDirectory.mkdirs();
		path = namenodeDirectory.getAbsolutePath() + File.separator + sheetId.replaceAll(" ", "_").toLowerCase()
				+ ".json";

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("path: " + path);

		return path;
	}

	private static void writeSheetToPath(String path, InputStream stream) throws Exception {
		BufferedInputStream inStream = null;
		BufferedOutputStream outStream = null;

		try {
			inStream = new BufferedInputStream(stream);
			outStream = new BufferedOutputStream(new FileOutputStream(new File(path)));

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("path: " + path + " Request: ");

			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = inStream.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Request Finished.");
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
			try {
				if (outStream != null)
					outStream.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
	}

	public static ArrayList getSpreadSheets(String nameNodeId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return SpreadSheetDAO.getSheetId(connection, nameNodeId);
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

	public static void getSpreadSheetContent(String sheetId, OutputStream outStream) {
		BufferedInputStream inStream = null;
		String path = null;
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			path = SpreadSheetDAO.getPath(connection, sheetId);

			if (path != null) {
				inStream = new BufferedInputStream(new FileInputStream(new File(path)));
				int read = 0;
				final byte[] bytes = new byte[1024];
				while ((read = inStream.read(bytes)) != -1) {
					outStream.write(bytes, 0, read);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
	}

	public static String getQueryFilterTableName(String tableName, String nameNodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String fileType = HiveTableDAO.getFileType(connection, tableName, nameNodeId);
			if (fileType != null) {
				if (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_MBOX))
					fileType = "eml";

				if (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG))
					fileType = "log";

				if (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
					fileType = "log";

				if (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_PAIRS))
					fileType = "txt";

				return "DATATAGS_".concat(fileType.toUpperCase());
			}
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

	public static boolean isQueryExist(String id) throws Exception {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return BigQueryDAO.isQueryExist(connection, id);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}