package com.queryio.core.applications;

import java.io.File;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.net.NetUtils;
//import org.apache.hadoop.yarn.api.ClientRMProtocol;
//import org.apache.hadoop.yarn.api.protocolrecords.GetAllApplicationsRequest;
//import org.apache.hadoop.yarn.api.protocolrecords.GetAllApplicationsResponse;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.util.Records;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.AdHocJobConfig;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.TagParserConfig;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.conf.TagParserConfigManager;
import com.queryio.core.customtags.metadata.CustomTagMetadataDAO;
import com.queryio.core.dao.AdHocJobConfigDAO;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.TagParserDAO;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.job.definition.JobDefinitionDAO;

public class ApplicationManager {

	public static SummaryTable getAllApplicationsSummary() {
		Connection connection = null;
		SummaryTable summaryTable = new SummaryTable();
		String rmWebAppAddress = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			colNames.add("ID");
			colNames.add("User");
			colNames.add("Name");
			colNames.add("Queue");
			colNames.add("StartTime");
			colNames.add("FinishTime");
			colNames.add("State");
			colNames.add("Final Status");

			summaryTable.setColNames(colNames);

			ArrayList keys = new ArrayList();
			keys.add(YarnConfiguration.RM_ADDRESS);
			keys.add(YarnConfiguration.RM_WEBAPP_ADDRESS);

			ArrayList values = null;

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (Object obj : nodes) {
					Node node = (Node) obj;
					values = QueryIOAgentManager.getConfig(HostDAO.getHostDetail(connection, node.getHostId()), keys,
							node, "yarn-site.xml");

					if (values != null && values.size() > 0) {
						Configuration conf = new Configuration();
						conf.set(YarnConfiguration.RM_ADDRESS, (String) values.get(0));
						conf.set(YarnConfiguration.RM_WEBAPP_ADDRESS, (String) values.get(1));
						YarnConfiguration yarnConf = new YarnConfiguration(conf);

						InetSocketAddress rmAddress = NetUtils
								.createSocketAddr(yarnConf.get(YarnConfiguration.RM_ADDRESS));
						rmWebAppAddress = yarnConf.get(YarnConfiguration.RM_WEBAPP_ADDRESS);
						YarnRPC rpc = YarnRPC.create(conf);

						// ClientRMProtocol applicationsManager =
						// ((ClientRMProtocol)
						// rpc.getProxy(ClientRMProtocol.class, rmAddress,
						// conf));
						//
						// GetAllApplicationsResponse allAppsResponse =
						// applicationsManager.getAllApplications(Records.newRecord(GetAllApplicationsRequest.class));
						ApplicationClientProtocol applicationsManager = (ApplicationClientProtocol) rpc
								.getProxy(ApplicationClientProtocol.class, rmAddress, conf);

						GetApplicationsResponse allAppsResponse = applicationsManager
								.getApplications(Records.newRecord(GetApplicationsRequest.class));

						if (allAppsResponse != null) {
							List<ApplicationReport> applicationList = allAppsResponse.getApplicationList();
							ApplicationReport report;
							SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
							for (int i1 = 0; i1 < applicationList.size(); i1++) {
								report = applicationList.get(i1);

								colValues = new ArrayList();

								colValues.add(report.getApplicationId().toString());
								colValues.add(report.getUser());
								colValues.add(report.getName());
								colValues.add(report.getQueue());
								colValues.add(dateFormatter.format(report.getStartTime()));
								colValues.add((report.getFinishTime() == 0) ? ""
										: dateFormatter.format(report.getFinishTime()));
								colValues
										.add(StaticUtilities.toProperCase(report.getYarnApplicationState().toString()));
								colValues.add(
										StaticUtilities.toProperCase(report.getFinalApplicationStatus().toString()));
								colValues.add(rmWebAppAddress);

								summaryTable.addRow(colValues);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllApplicationsSummary() method caught Exception, Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return summaryTable;
	}

	public static String getApllicationStatus(String applicationId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList keys = new ArrayList();
			keys.add(YarnConfiguration.RM_ADDRESS);

			ArrayList values = null;

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (Object obj : nodes) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("nodeId: " + ((Node) obj).getId());
					Node node = (Node) obj;
					values = QueryIOAgentManager.getConfig(HostDAO.getHostDetail(connection, node.getHostId()), keys,
							node, "yarn-site.xml");

					if (values != null && values.size() > 0) {
						Configuration conf = new Configuration();
						conf.set(YarnConfiguration.RM_ADDRESS, (String) values.get(0));
						YarnConfiguration yarnConf = new YarnConfiguration(conf);

						InetSocketAddress rmAddress = NetUtils
								.createSocketAddr(yarnConf.get(YarnConfiguration.RM_ADDRESS));

						YarnRPC rpc = YarnRPC.create(conf);

						// ClientRMProtocol applicationsManager =
						// ((ClientRMProtocol)
						// rpc.getProxy(ClientRMProtocol.class, rmAddress,
						// conf));
						ApplicationClientProtocol applicationsManager = (ApplicationClientProtocol) rpc
								.getProxy(ApplicationClientProtocol.class, rmAddress, conf);

						GetApplicationsResponse allAppsResponse = applicationsManager
								.getApplications(Records.newRecord(GetApplicationsRequest.class));

						if (allAppsResponse != null) {
							List<ApplicationReport> applicationList = allAppsResponse.getApplicationList();
							ApplicationReport report;
							SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
							for (int i1 = 0; i1 < applicationList.size(); i1++) {
								report = applicationList.get(i1);
								if (report.getApplicationId().toString().equalsIgnoreCase(applicationId)) {
									return report.getFinalApplicationStatus().toString();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getApllicationStatus() method caught Exception, Exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static boolean killApplication(String applicationId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
			Node node = null;
			if (nodes.size() > 0) {
				node = (Node) nodes.get(0);
			}

			if (node != null) {
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				if (host != null) {
					ArrayList keys = new ArrayList();
					keys.add(YarnConfiguration.RM_ADDRESS);

					ArrayList values = QueryIOAgentManager.getConfig(
							HostDAO.getHostDetail(connection, node.getHostId()), keys, node, "yarn-site.xml");

					if (values != null && values.size() > 0) {
						Configuration conf = new Configuration();
						conf.set(YarnConfiguration.RM_ADDRESS, (String) values.get(0));
						YarnConfiguration yarnConf = new YarnConfiguration(conf);

						InetSocketAddress rmAddress = NetUtils
								.createSocketAddr(yarnConf.get(YarnConfiguration.RM_ADDRESS));

						YarnRPC rpc = YarnRPC.create(conf);

						// ClientRMProtocol applicationsManager =
						// ((ClientRMProtocol)
						// rpc.getProxy(ClientRMProtocol.class, rmAddress,
						// conf));
						//
						// GetAllApplicationsResponse allAppsResponse =
						// applicationsManager.getAllApplications(Records.newRecord(GetAllApplicationsRequest.class));

						ApplicationClientProtocol applicationsManager = (ApplicationClientProtocol) rpc
								.getProxy(ApplicationClientProtocol.class, rmAddress, conf);

						GetApplicationsResponse allAppsResponse = applicationsManager
								.getApplications(Records.newRecord(GetApplicationsRequest.class));

						if (allAppsResponse != null) {
							List<ApplicationReport> applicationList = allAppsResponse.getApplicationList();
							ApplicationReport report;
							for (int i1 = 0; i1 < applicationList.size(); i1++) {
								report = applicationList.get(i1);

								if (report.getApplicationId().toString().equals(applicationId)) {
									KillApplicationRequest killRequest = Records
											.newRecord(KillApplicationRequest.class);
									killRequest.setApplicationId(report.getApplicationId());
									applicationsManager.forceKillApplication(killRequest);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("killApplication() failed with exception: " + e.getMessage(), e);
			return false;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return true;
	}

	public static JSONObject getAllJobsList() {
		return getAllJobsList(null);

	}

	public static JSONObject getAllJobsList(String paramsDT) {
		Connection connection = null;
		SummaryTable summaryTable = new SummaryTable();
		JSONObject obj = new JSONObject();
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			// ArrayList colNames = new ArrayList();
			// ArrayList colValues = null;
			// colNames.add("Job Name");
			// colNames.add("Main Class");
			// colNames.add("Arguments");
			// colNames.add("NameNode");
			// colNames.add("ResourceManager");
			// colNames.add("Jar File");
			// colNames.add("Lib Jar(s)");
			// colNames.add("Native File(s)");
			// summaryTable.setColNames(colNames);

			if (paramsDT == null) {
				return MapRedJobConfigDAO.getAllStandardMRJobs(connection);
			} else {
				return MapRedJobConfigDAO.getAllStandardMRJobs(connection, paramsDT);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllJobsList() method caught Exception, Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;

	}

	public static SummaryTable getAllAdhocJobsList() {
		Connection connection = null;
		SummaryTable summaryTable = new SummaryTable();
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			colNames.add("Job Name");
			colNames.add("Main Class");
			colNames.add("Source File");
			colNames.add("Path Pattern");
			colNames.add("Arguments");
			colNames.add("NameNode");
			colNames.add("ResourceManager");
			colNames.add("Jar File");
			colNames.add("Lib Jar(s)");
			colNames.add("Native File(s)");
			summaryTable.setColNames(colNames);

			List<AdHocJobConfig> list = AdHocJobConfigDAO.getAll(connection);

			AdHocJobConfig jobConfig;
			for (int i = 0; i < list.size(); i++) {
				colValues = new ArrayList();
				jobConfig = (AdHocJobConfig) list.get(i);

				colValues.add(jobConfig.getJobName());
				colValues.add(jobConfig.getClassName());
				colValues.add(jobConfig.getSourcePath());
				colValues.add(jobConfig.getPathPattern());
				colValues.add(jobConfig.getArguments());
				colValues.add(jobConfig.getNamenodeId());
				colValues.add(jobConfig.getRmId());
				colValues.add(jobConfig.getJarFile());
				colValues.add(jobConfig.getLibjars());
				colValues.add(jobConfig.getFiles());

				summaryTable.addRow(colValues);
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllAdhocJobsList() method caught Exception, Exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return summaryTable;
	}

	// public static boolean deleteJobDB(String jobName){
	// Connection connection = null;
	//
	// try
	// {
	// connection = CoreDBManager.getQueryIODBConnection();
	//
	// MapRedJobConfig job = MapRedJobConfigDAO.get(connection, jobName);
	//
	// if(job!=null)
	// {
	// if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("Deleting job: " + jobName);
	//
	// MapRedJobConfigDAO.delete(connection, job.getJobName(), false);
	// }
	//
	// return true;
	// }
	// catch (Exception e)
	// {
	// AppLogger.getLogger().fatal("deleteJobDB() failed with exception: " +
	// e.getMessage(), e);
	// }
	// finally
	// {
	// try
	// {
	// CoreDBManager.closeConnection(connection);
	// }
	// catch(Exception e)
	// {
	// AppLogger.getLogger().fatal("Error closing database connection.", e);
	// }
	// }
	// return false;
	// }

	public static DWRResponse deleteJob(String jobName) {
		Connection connection = null;
		DWRResponse response = new DWRResponse();
		if (RemoteManager.isNonAdminAndDemo(null)) {
			response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return response;
		}
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			MapRedJobConfig job = MapRedJobConfigDAO.get(connection, jobName);

			if (job != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Deleting job: " + jobName);

				TagParserConfig parserConfig = null;
				parserConfig = TagParserDAO.getByName(connection, job.getJobName(), false);
				if (parserConfig != null) {
					TagParserConfigManager.handleDeletePostIngestParser(connection, parserConfig);
				} else {
					MapRedJobConfigDAO.delete(connection, job.getJobName(), true);

					// Delete Folder
					StaticUtilities.deleteFile(new File(EnvironmentalConstants.getAppHome() + "/"
							+ QueryIOConstants.MAPREDRESOURCE + "/" + jobName));
				}
			}
			response.setDwrResponse(true, "Job(s) deleted successfully.", 200);
		} catch (Exception e) {
			response.setDwrResponse(false, e.getMessage(), 400);
			AppLogger.getLogger().fatal("deleteJob() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse deleteAdHocJob(String jobName) {
		Connection connection = null;
		DWRResponse response = new DWRResponse();
		if (RemoteManager.isNonAdminAndDemo(null)) {
			response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return response;
		}
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			// MapRedJobConfig job = MapRedJobConfigDAO.get(connection,
			// jobName);
			AdHocJobConfig adjob = AdHocJobConfigDAO.get(connection, jobName);

			if (adjob != null) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Deleting job: " + jobName);

				// MapRedJobConfigDAO.delete(connection, job.getJobName(),
				// true);
				AdHocJobConfigDAO.delete(connection, adjob.getJobName());

				// Delete Folder
				StaticUtilities.deleteFile(new File(
						EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + jobName));
			}
			response.setDwrResponse(true, "Job(s) deleted successfully.", 200);
		} catch (Exception e) {
			response.setDwrResponse(false, e.getMessage(), 400);
			AppLogger.getLogger().fatal("deleteJob() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse deleteAllJobs(ArrayList<String> jobs) {
		DWRResponse response = null;
		if (RemoteManager.isNonAdminAndDemo(null)) {
			response = new DWRResponse();
			response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return response;
		}
		for (int i = 0; i < jobs.size(); i++) {
			response = deleteJob(jobs.get(i).toString());
			if (!response.isTaskSuccess())
				break;
		}
		return response;
	}

	public static DWRResponse deleteAllAdHocJobs(ArrayList<String> jobs) {
		DWRResponse response = null;
		if (RemoteManager.isNonAdminAndDemo(null)) {
			response = new DWRResponse();
			response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return response;
		}
		for (int i = 0; i < jobs.size(); i++) {
			response = deleteAdHocJob(jobs.get(i).toString());
			if (!response.isTaskSuccess())
				break;
		}
		return response;
	}

	public static boolean updateJobDetails(String jobName, String newJobName, String mainClass, String arguments,
			String nnId, String rmId) {
		Connection connection = null;

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return false;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Updating job: " + jobName);
			MapRedJobConfigDAO.update(connection, jobName, newJobName, mainClass, arguments, nnId, rmId);
			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("updateJobDetails() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;
	}

	public static boolean updateAdhocJobDetails(String jobName, String newJobName, String mainClass, String arguments,
			String pathPattern, String srcPath, String nnId, String rmId) {
		Connection connection = null;

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return false;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Updating job: " + jobName);

			// MapRedJobConfigDAO.update(connection, jobName, newJobName,
			// mainClass, arguments, nnId, rmId);
			AdHocJobConfig adhocJobConfig = new AdHocJobConfig(nnId, rmId, jobName, null, null, null, mainClass,
					srcPath, pathPattern, arguments);
			AdHocJobConfigDAO.updateJob(connection, adhocJobConfig);

			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("updateAdhocJobDetails() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;
	}

	public static DWRResponse runJob(String jobName, boolean isRecursive, boolean isFilterApply, String filterQuery) {
		Connection connection = null;
		DWRResponse response = new DWRResponse();
		response.setDwrResponse(false, "", 500);
		try {

			if ((jobName != null) && (jobName.startsWith(QueryIOConstants.DATATAGGING_PREFIX))) // call
																								// run
																								// jobDataTagging
				return runJobDataTagging(jobName);

			connection = CoreDBManager.getQueryIODBConnection();

			MapRedJobConfig jobConfig = MapRedJobConfigDAO.get(connection, jobName);
			if (jobConfig == null) { // AdHoc Job
				AdHocJobConfig adHocJobConfig = AdHocJobConfigDAO.get(connection, jobName);
				String arguments = AdHocJobConfigDAO.getUpdatedArguments(connection, jobName, null,
						JobDefinitionDAO.getTableName(connection, jobName));
				jobConfig = new MapRedJobConfig(adHocJobConfig.getNamenodeId(), adHocJobConfig.getRmId(),
						adHocJobConfig.getJobName(), QueryIOConstants.DEFAULT_ADHOC_JAR,
						adHocJobConfig.getJarFile()
								+ ((adHocJobConfig.getLibjars() != null) ? "," + adHocJobConfig.getLibjars() : ""),
						adHocJobConfig.getFiles(), QueryIOConstants.DEFAULT_ADHOC_MAIN_CLASS, arguments, isRecursive,
						isFilterApply, filterQuery);
			}

			Node node = NodeDAO.getNode(connection, jobConfig.getRmId());
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Transferring jar");
			QueryIOAgentManager.transferFolder(host, "/", jobConfig.getJobName());

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Sending command to start job");
			QueryIOResponse resp = QueryIOAgentManager.runJob(host, node, jobConfig, true);

			response.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(), 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("runJob() failed with exception: " + e.getMessage(), e);
			response.setDwrResponse(false, "runJob() failed with exception: " + e.getLocalizedMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return response;
	}

	public static DWRResponse runJobDataTagging(String jobName) {
		Connection connection = null;
		DWRResponse response = new DWRResponse();
		response.setDwrResponse(false, "", 500);
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			MapRedJobConfig jobConfig = MapRedJobConfigDAO.get(connection, jobName);

			if (jobConfig != null) {
				Node node = null;
				if (jobConfig.getRmId() == null) {
					List<Node> nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
					if (nodes != null && nodes.size() > 0) {
						node = nodes.get(0);
					} else {
						AppLogger.getLogger().fatal("No ResourceManager Found.");
						return response;
					}
				} else {
					node = NodeDAO.getNode(connection, jobConfig.getRmId());
				}
				Host host = HostDAO.getHostDetail(connection, node.getHostId());

				String libJars = jobConfig.getLibJars();
				String[] jars = libJars.split(",");
				for (String path : jars) {
					if (path.startsWith(jobConfig.getJobName())) {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Transferring jar");
						QueryIOAgentManager.transferFolder(host, "/", jobConfig.getJobName());
					}
				}

				Configuration conf = ConfigurationManager.getConfiguration(connection, jobConfig.getNamenodeId());
				String hdfsURI = conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
				String dbName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
				String encryptionKey = conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY);
				String arguments = jobConfig.getArguments();
				AppLogger.getLogger().debug("Job args before : " + arguments);
				if (arguments != null) {
					if (jobName.equalsIgnoreCase(QueryIOConstants.DATATAGGING_DEFAULT_JOB)) {
						String[] splittedArgs = arguments.split(" ");
						arguments = "";
						for (int i = 0; i < splittedArgs.length; i++) {

							if (i == splittedArgs.length - 3) {
								if (splittedArgs[i + 1].equalsIgnoreCase("-1")) {
									splittedArgs[i] = "0";
								} else {
									splittedArgs[i] = splittedArgs[i + 1];
								}
							}
							if (i == splittedArgs.length - 2) {
								splittedArgs[i] = String.valueOf(System.currentTimeMillis()); // Assign
																								// End
																								// time
																								// to
																								// current
																								// time
							}

							if (i == splittedArgs.length - 1) {
								arguments += splittedArgs[i];
							} else {
								arguments += splittedArgs[i] + " ";
							}
						}
						jobConfig.setArguments(arguments);
						MapRedJobConfigDAO.update(connection, jobConfig);
					}
					AppLogger.getLogger().debug("Job args after : " + arguments);
					String id = arguments.substring(0, arguments.indexOf(" "));

					jobConfig.setArguments(arguments.substring(arguments.indexOf(" ")).trim());

					String fileType = "ALL_FILES";
					Map data = CustomTagMetadataDAO.getCustomTagMetaataDetailById(connection, id);
					if (data != null) {
						String tempFileType = (String) data.get("fileType");
						if (tempFileType != null)
							fileType = tempFileType.toLowerCase();
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("fileType: " + fileType);

					JSONObject fileTypeParsers = new JSONObject();

					String parserFileTypes = conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES, "");

					boolean foundParser = false;
					if (!parserFileTypes.isEmpty()) {
						if ("ALL_FILES".equalsIgnoreCase(fileType)) {
							String className = null;
							for (String str : parserFileTypes.split(",")) {
								className = conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str);
								fileTypeParsers.put(str, className);
							}
						} else {
							for (String str : parserFileTypes.split(",")) {
								if (str.equalsIgnoreCase(fileType)) {
									foundParser = true;
									break;
								}
							}
						}
					}
					if (foundParser) {
						String className = conf
								.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + fileType);
						fileTypeParsers.put(fileType, className);
					}

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("fileTypeParsers: " + fileTypeParsers);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Sending command to start job");
					QueryIOResponse resp = QueryIOAgentManager.runJobDataTagging(host, node, jobConfig, hdfsURI, dbName,
							encryptionKey, fileTypeParsers.toJSONString());

					response.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(), 200);
				} else {
					response.setDwrResponse(false, "No arguments apecified for the job: " + jobName, 500);
				}
			} else {
				response.setDwrResponse(false, "No Job Exists with the provided jobName: " + jobName, 500);
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal("runJob() failed with exception: " + e.getMessage(), e);
			response.setDwrResponse(false, "runJob() failed with exception: " + e.getLocalizedMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return response;
	}

	public static QueryIOResponse runJobQuery(final Connection connection ,String jobName, String arguments, boolean isAdHocId, boolean isRecursive,
			boolean isFilterApply, String filterQuery) {
//		Connection connection = null;

		try {
//			connection = CoreDBManager.getQueryIODBConnection();
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("jobName: " + jobName);

			MapRedJobConfig jobConfig = null;

			if (isAdHocId) {
				AdHocQueryBean bean = AdHocQueryDAO.getAdHocQueryInfo(connection, jobName); // jobName
																							// is
																							// adHocId
																							// for
																							// this
																							// case

				if (bean != null) {
					String libJobJarType = null;

					if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_CSV)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_CSV;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_LOG)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_LOG;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_IISLOG)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_IISLOG;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_JSON)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_JSON;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_PAIRS)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_KVPAIRS;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_MBOX)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_MBOX;
					} else if (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_REGEX)) {
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_REGEX;
					} else // if
							// (bean.getType().equals(QueryIOConstants.ADHOC_TYPE_XML))
					{
						libJobJarType = QueryIOConstants.DEFAULT_ADHOC_JOB_LIBJAR_XML;
					}

					// DB jar is required by custom MR jobs to query DB
					libJobJarType = libJobJarType + "," + QueryIOConstants.HSQL_DB_LIBJAR;

					jobConfig = new MapRedJobConfig(bean.getNamenodeId(), bean.getRmId(), jobName,
							QueryIOConstants.DEFAULT_ADHOC_JAR, libJobJarType, "",
							QueryIOConstants.DEFAULT_ADHOC_MAIN_CLASS, arguments, isRecursive, isFilterApply,
							filterQuery);
					jobConfig.setNamenodeId(bean.getNamenodeId());
					jobConfig.setRmId(bean.getRmId());
				}
			} else {
				AdHocJobConfig adHocJobConfig = AdHocJobConfigDAO.get(connection, jobName);
				jobConfig = new MapRedJobConfig(adHocJobConfig.getNamenodeId(), adHocJobConfig.getRmId(),
						adHocJobConfig.getJobName(), QueryIOConstants.DEFAULT_ADHOC_JAR,
						adHocJobConfig.getJarFile()
								+ ((adHocJobConfig.getLibjars() != null) ? "," + adHocJobConfig.getLibjars() : ""),
						adHocJobConfig.getFiles(), QueryIOConstants.DEFAULT_ADHOC_MAIN_CLASS, arguments, isRecursive,
						isFilterApply, filterQuery);
			}

			Node node = NodeDAO.getNode(connection, jobConfig.getRmId());
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (!isAdHocId) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Transferring jar");
				QueryIOAgentManager.transferFolder(host, "/", jobConfig.getJobName());
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Sending command to start job");

			return QueryIOAgentManager.runJob(host, node, jobConfig, !isAdHocId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("runJob() failed with exception: " + e.getMessage(), e);
		} 
//		finally {
//			try {
//				CoreDBManager.closeConnection(connection);
//			} catch (Exception e) {
//				AppLogger.getLogger().fatal("Error closing database connection.", e);
//			}
//		}

		return null;
	}

	public static ArrayList getJobNameLists() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return MapRedJobConfigDAO.getAllJobNames(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllJobsList() method caught Exception, Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static JSONObject getNodeManagerLogsPath(String applicationId) {
		JSONObject object = new JSONObject();
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList nodeManagersList = NodeDAO.getAllNMs(connection);
			if ((nodeManagersList != null) && (nodeManagersList.size() > 0)) {
				JSONArray array = new JSONArray();
				for (int i = 0; i < nodeManagersList.size(); i++) {
					Node nodeManagerNode = (Node) nodeManagersList.get(i);
					Host host = HostDAO.getHostDetail(connection, nodeManagerNode.getHostId());
					HashMap logsLink = QueryIOAgentManager.getNodeManagerLogsPath(nodeManagerNode.getId(),
							applicationId, host);
					if ((logsLink != null) && (logsLink.size() > 0)) {
						JSONObject obj = new JSONObject();
						obj.put("id", nodeManagerNode.getId());
						obj.put("hostIP", host.getHostIP());
						obj.put("hostPort", host.getAgentPort());
						JSONArray containerArray = new JSONArray();
						Iterator it = logsLink.keySet().iterator();
						while (it.hasNext()) {
							String containerName = String.valueOf(it.next());
							JSONObject containerObj = new JSONObject();
							containerObj.put("containerName", containerName);
							containerObj.put("logFilePath", String.valueOf(logsLink.get(containerName)));
							containerArray.add(containerObj);
						}
						obj.put("containerArray", containerArray);
						array.add(obj);
					}
				}
				object.put("nodeManagerHosts", array);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNodeManagerLogsPath() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return object;
	}
}
