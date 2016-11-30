package com.queryio.core.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.ExportManager;
import com.queryio.common.report.ReportConstants;
import com.queryio.common.report.ReportGenerator;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.ZipUtil;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.User;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.customtags.BigQueryManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.core.reports.nodes.AlertStatus;
import com.queryio.core.reports.nodes.BigQueryResult;
import com.queryio.core.reports.nodes.DatanodeInfo;
import com.queryio.core.reports.nodes.DatanodeStatus;
import com.queryio.core.reports.nodes.HDFSStatus;
import com.queryio.core.reports.nodes.MapReduceStatus;
import com.queryio.core.reports.nodes.NamenodeInfo;
import com.queryio.core.reports.nodes.NamenodeStatus;
import com.queryio.core.reports.nodes.NodeManagerInfo;
import com.queryio.core.reports.nodes.NodeManagerStatus;
import com.queryio.core.reports.nodes.ResourceManagerInfo;
import com.queryio.core.reports.nodes.ResourceManagerStatus;
import com.queryio.core.reports.nodes.StorageForecast;

public class ReportManager {

	private static String imgSrc = EnvironmentalConstants.getAppHome() + "images/hpcsReport.jpg";

	public static String generateHDFSStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {

		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.HDFS_SUMMARY_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		HDFSStatus report = new HDFSStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	public static String generateMapReduceStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {

		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.MAPREDUCE_SUMMARY_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		MapReduceStatus report = new MapReduceStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateNamenodeStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.NN_STATUS_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		NamenodeStatus report = new NamenodeStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateDatanodeStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.DN_STATUS_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		DatanodeStatus report = new DatanodeStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateResourceManagerStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.RM_STATUS_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		ResourceManagerStatus report = new ResourceManagerStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateNodeManagerStatusReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.NM_STATUS_REPORT + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		NodeManagerStatus report = new NodeManagerStatus(fileName, startTimestamp, endTimestamp, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateNamenodeInfoReport(Timestamp startTimestamp, Timestamp endTimestamp, int exportType,
			Host host, Node node, String title) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.NN_DETAIL_REPORT + host.getHostIP().replace(".", "_") + "_"
				+ currentTimestamp.getDate() + "_" + currentTimestamp.getMonth() + "_"
				+ (1900 + currentTimestamp.getYear() + "_" + currentTimestamp.getHours() + "_"
						+ currentTimestamp.getMinutes());

		NamenodeInfo report = new NamenodeInfo(fileName, startTimestamp, endTimestamp, host, node, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateDatanodeInfoReport(Timestamp startTimestamp, Timestamp endTimestamp, int exportType,
			Host host, Node node, String title) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.DN_DETAIL_REPORT + node.getId() + "_" + currentTimestamp.getDate() + "_"
				+ currentTimestamp.getMonth() + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		DatanodeInfo report = new DatanodeInfo(fileName, startTimestamp, endTimestamp, host, node, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateResourceManagerInfoReport(Timestamp startTimestamp, Timestamp endTimestamp,
			int exportType, Host host, Node node, String title) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.RM_DETAIL_REPORT + node.getId() + "_" + currentTimestamp.getDate() + "_"
				+ currentTimestamp.getMonth() + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		ResourceManagerInfo report = new ResourceManagerInfo(fileName, startTimestamp, endTimestamp, host, node, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateNodeManagerInfoReport(Timestamp startTimestamp, Timestamp endTimestamp,
			int exportType, Host host, Node node, String title) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.NM_DETAIL_REPORT + node.getId() + "_" + currentTimestamp.getDate() + "_"
				+ currentTimestamp.getMonth() + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		NodeManagerInfo report = new NodeManagerInfo(fileName, startTimestamp, endTimestamp, host, node, title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static String generateStorageForecastReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "StorageForecastReport_" + currentTimestamp.getDate() + "_" + currentTimestamp.getMonth()
				+ "_" + (1900 + currentTimestamp.getYear() + "_" + currentTimestamp.getHours() + "_"
						+ currentTimestamp.getMinutes());

		StorageForecast report = new StorageForecast(fileName, endTimestamp);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	private static void mailReport(ArrayList attachments, ArrayList usersList, String message) throws Exception {

		NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		NotifyBean notify = new NotifyBean();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			notify = NotifyDAO.getNotificationSettings(connection);
		} finally {
			CoreDBManager.closeConnection(connection);
		}

		notify.setAttachments(attachments);
		if (notify.isEmailEnabled()) {
			NotificationHandler.generateEmailNotification(notifMgr, message, message, notify, usersList, true);
		} else {
			throw new Exception("Error Generating Notification");
		}
	}

	private static void logReport(ArrayList attachments, String message) throws Exception {

		NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		NotifyBean notify = new NotifyBean();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			notify = NotifyDAO.getNotificationSettings(connection);
		} finally {
			CoreDBManager.closeConnection(connection);
		}
		if (notify.isLogEnabled()) {
			NotificationHandler.generateLogNotification(notifMgr, message + " - " + attachments.toString(), notify,
					true);
		} else {
			throw new Exception("Error Generating Notification");
		}
	}

	public static DWRResponse mailNodeReport(String nodeId, ArrayList exportFormatList, ArrayList usersIdList,
			String title, String startTime, String endTime) {

		Timestamp startTimestamp = parseDateString(startTime);
		Timestamp endTimestamp = parseDateString(endTime);
		Connection connection = null;
		ArrayList attachments = new ArrayList();
		ArrayList usersList = new ArrayList();
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "Report Generation Failed", 500);
		String mailMessage = "";
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			if (usersIdList != null) {
				for (int i = 0; i < usersIdList.size(); i++) {
					User user = UserDAO.getUserDetail(connection, Integer.parseInt((String) usersIdList.get(i)));
					usersList.add(user);
				}
			}
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			// generate attachments

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateNamenodeInfoReport(startTimestamp, endTimestamp, exportId, host, node,
							title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_NN_DETAIL;
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateDatanodeInfoReport(startTimestamp, endTimestamp, exportId, host, node,
							title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_DN_DETAIL;
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateResourceManagerInfoReport(startTimestamp, endTimestamp, exportId, host,
							node, title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_RM_DETAIL;
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateNodeManagerInfoReport(startTimestamp, endTimestamp, exportId, host,
							node, title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_NM_DETAIL;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			dwrResponse.setDwrResponse(false, "Error Exporting Report", 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		try {
			if (usersIdList != null) {
				mailReport(attachments, usersList, mailMessage);
			}
		} catch (Exception e) {
			dwrResponse.setDwrResponse(false, "Mailing Report Failed.", 500);
			return dwrResponse;
		}
		dwrResponse.setDwrResponse(true, "Report Mailed Successfully.", 200);
		return dwrResponse;
	}

	public static DWRResponse logNodeReport(String nodeId, ArrayList exportFormatList, String title, String startTime,
			String endTime) {

		Timestamp startTimestamp = parseDateString(startTime);
		Timestamp endTimestamp = parseDateString(endTime);
		Connection connection = null;
		ArrayList attachments = new ArrayList();
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "Report Generation Failed", 500);
		String mailMessage = "";
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			// generate attachments

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateNamenodeInfoReport(startTimestamp, endTimestamp, exportId, host, node,
							title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_ALERT_STATUS;
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateDatanodeInfoReport(startTimestamp, endTimestamp, exportId, host, node,
							title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_IO_STATUS;
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateResourceManagerInfoReport(startTimestamp, endTimestamp, exportId, host,
							node, title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_RM_DETAIL;
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				for (int i = 0; i < exportFormatList.size(); i++) {
					int exportId = Integer.parseInt((String) exportFormatList.get(i));
					String reportName = generateNodeManagerInfoReport(startTimestamp, endTimestamp, exportId, host,
							node, title);
					if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
						File reportHTML = new File(
								EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
						File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
						ArrayList listFiles = new ArrayList();
						listFiles.add(reportHTML);
						listFiles.add(reportHTMLFiles);
						String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
								+ reportName.replace(".html", ".zip");
						ZipUtil.compressFiles(listFiles, destZipFile);

						attachments.add(destZipFile);
					} else
						attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
				}
				mailMessage = QueryIOConstants.REPORT_MESSAGE_NM_DETAIL;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			dwrResponse.setDwrResponse(false, "Error Exporting Report", 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		try {
			AppLogger.getLogger().fatal("Attachments: " + attachments.toString());
			logReport(attachments, mailMessage);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Logging Report", e);
			dwrResponse.setDwrResponse(false, "Error Logging Report", 500);
			return dwrResponse;
		}
		dwrResponse.setDwrResponse(true, "Report mailed successfully.", 200);
		return dwrResponse;
	}

	public static String viewNodeReport(String nodeId, String exportFormat, String title, String startTime,
			String endTime) {
		Timestamp startTimestamp = parseDateString(startTime);
		Timestamp endTimestamp = parseDateString(endTime);
		Connection connection = null;
		String reportPath = "";
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				reportPath = QueryIOConstants.REPORTS_QUERYIO + File.separator + generateNamenodeInfoReport(
						startTimestamp, endTimestamp, Integer.parseInt(exportFormat), host, node, title);
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				reportPath = QueryIOConstants.REPORTS_QUERYIO + File.separator + generateDatanodeInfoReport(
						startTimestamp, endTimestamp, Integer.parseInt(exportFormat), host, node, title);
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				reportPath = QueryIOConstants.REPORTS_QUERYIO + File.separator + generateResourceManagerInfoReport(
						startTimestamp, endTimestamp, Integer.parseInt(exportFormat), host, node, title);
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				reportPath = QueryIOConstants.REPORTS_QUERYIO + File.separator + generateNodeManagerInfoReport(
						startTimestamp, endTimestamp, Integer.parseInt(exportFormat), host, node, title);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Generating Report", e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		return reportPath;
	}

	public static Timestamp parseDateString(String dateString) {
		Timestamp ts = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dt = format.parse(dateString);
			ts = new Timestamp(dt.getTime());
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return ts;
	}

	public static DWRResponse mailGeneralReport(ArrayList reportTypeList, ArrayList exportFormatList,
			ArrayList usersIdList, String title, String startTime, String endTime) {
		// generate attachments

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(reportTypeList + " " + exportFormatList + " " + usersIdList + " " + title + " "
					+ startTime + " " + endTime);

		Timestamp start = parseDateString(startTime);
		Timestamp end = parseDateString(endTime);
		DWRResponse dwrResponse = new DWRResponse();
		ArrayList attachments = new ArrayList();
		String mailMessage = "";

		ArrayList usersList = new ArrayList();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			if (usersIdList != null) {
				for (int i = 0; i < usersIdList.size(); i++) {
					User user = UserDAO.getUserDetail(connection, Integer.parseInt((String) usersIdList.get(i)));
					usersList.add(user);
				}
			}
			for (int var = 0; var < reportTypeList.size(); var++) {
				switch (Integer.parseInt((String) reportTypeList.get(var))) {

				case QueryIOConstants.REPORT_DATANODE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateDatanodeStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_DATANODE_STATUS;
					break;

				case QueryIOConstants.REPORT_HDFS_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateHDFSStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_HDFS_STATUS;
					break;

				case QueryIOConstants.REPORT_MAPREDUCE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateMapReduceStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_MAPREDUCE_STATUS;
					break;

				case QueryIOConstants.REPORT_NAMENODE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateNamenodeStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_NAMENODE_STATUS;
					break;

				case QueryIOConstants.REPORT_RM_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateResourceManagerStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_RM_STATUS;
					break;

				case QueryIOConstants.REPORT_NM_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateNodeManagerStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_NM_STATUS;
					break;

				case QueryIOConstants.REPORT_STORAGE_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_STORAGE_STATUS;
					break;

				case QueryIOConstants.REPORT_TOP10:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_TOP10;
					break;
				case QueryIOConstants.REPORT_ALERT_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_ALERT_STATUS;
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateAlertReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					break;
				case QueryIOConstants.REPORT_STORAGE_FORECAST:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateStorageForecastReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_STORAGE_FORECAST;
					break;
				case QueryIOConstants.REPORT_IO_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_IO_STATUS;
					break;
				default:
					dwrResponse.setDwrResponse(false, "Wrong Report Type", 500);
					return dwrResponse;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		try {
			if (usersIdList != null) {
				mailReport(attachments, usersList, mailMessage);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error mailing Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		}
		dwrResponse.setDwrResponse(true, "Report Generated Successfully", 200);
		return dwrResponse;
	}

	public static DWRResponse logGeneralReport(ArrayList reportTypeList, ArrayList exportFormatList, String title,
			String startTime, String endTime) {
		// generate attachments

		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug(reportTypeList + " " +exportFormatList +
		// " " + usersIdList + " " + title + " " + startTime + " " + endTime);

		Timestamp start = parseDateString(startTime);
		Timestamp end = parseDateString(endTime);
		DWRResponse dwrResponse = new DWRResponse();
		ArrayList attachments = new ArrayList();
		String mailMessage = "";

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			for (int var = 0; var < reportTypeList.size(); var++) {
				switch (Integer.parseInt((String) reportTypeList.get(var))) {

				case QueryIOConstants.REPORT_DATANODE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateDatanodeStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_DATANODE_STATUS;
					break;

				case QueryIOConstants.REPORT_HDFS_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateHDFSStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_HDFS_STATUS;
					break;

				case QueryIOConstants.REPORT_MAPREDUCE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateMapReduceStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_MAPREDUCE_STATUS;
					break;

				case QueryIOConstants.REPORT_NAMENODE_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateNamenodeStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_NAMENODE_STATUS;
					break;

				case QueryIOConstants.REPORT_RM_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateResourceManagerStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_RM_STATUS;
					break;

				case QueryIOConstants.REPORT_NM_STATUS:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateNodeManagerStatusReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_NM_STATUS;
					break;

				case QueryIOConstants.REPORT_STORAGE_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_STORAGE_STATUS;
					break;

				case QueryIOConstants.REPORT_TOP10:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_TOP10;
					break;
				case QueryIOConstants.REPORT_ALERT_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_ALERT_STATUS;
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateAlertReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					break;
				case QueryIOConstants.REPORT_STORAGE_FORECAST:
					for (int i = 0; i < exportFormatList.size(); i++) {
						int exportId = Integer.parseInt((String) exportFormatList.get(i));
						String reportName = generateStorageForecastReport(exportId, title, start, end);
						if (exportId == ExportConstants.EXPORT_TYPE_HTML) {
							File reportHTML = new File(
									EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
							File reportHTMLFiles = new File(EnvironmentalConstants.getReportsDirectory()
									+ File.separator + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
							ArrayList listFiles = new ArrayList();
							listFiles.add(reportHTML);
							listFiles.add(reportHTMLFiles);
							String destZipFile = EnvironmentalConstants.getReportsDirectory() + File.separator
									+ reportName.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);

							attachments.add(destZipFile);
						} else
							attachments.add(EnvironmentalConstants.getReportsDirectory() + File.separator + reportName);
					}
					mailMessage = QueryIOConstants.REPORT_MESSAGE_STORAGE_FORECAST;
					break;
				case QueryIOConstants.REPORT_IO_STATUS:
					mailMessage = QueryIOConstants.REPORT_MESSAGE_IO_STATUS;
					break;
				default:
					dwrResponse.setDwrResponse(false, "Wrong Report Type", 500);
					return dwrResponse;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		try {
			logReport(attachments, mailMessage);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error mailing Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		}
		dwrResponse.setDwrResponse(true, "Report Generated Successfully", 200);
		return dwrResponse;
	}

	public static String viewGeneralReport(ArrayList reportTypeList, String exportFormat, String title,
			String startTime, String endTime) {
		// generate attachments

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger()
					.debug(reportTypeList + " " + exportFormat + " " + " " + title + " " + startTime + " " + endTime);

		Timestamp start = parseDateString(startTime);
		Timestamp end = parseDateString(endTime);

		String attachments = "";
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			for (int var = 0; var < reportTypeList.size(); var++) {
				switch (Integer.parseInt((String) reportTypeList.get(var))) {

				case QueryIOConstants.REPORT_DATANODE_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateDatanodeStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_HDFS_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateHDFSStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_MAPREDUCE_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateMapReduceStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_NAMENODE_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateNamenodeStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_RM_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateResourceManagerStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_NM_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateNodeManagerStatusReport(Integer.parseInt(exportFormat), title, start, end);
					break;

				case QueryIOConstants.REPORT_STORAGE_STATUS:
					break;

				case QueryIOConstants.REPORT_TOP10:
					break;
				case QueryIOConstants.REPORT_ALERT_STATUS:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateAlertReport(Integer.parseInt(exportFormat), title, start, end);
					break;
				case QueryIOConstants.REPORT_STORAGE_FORECAST:
					attachments = QueryIOConstants.REPORTS_QUERYIO + File.separator
							+ generateStorageForecastReport(Integer.parseInt(exportFormat), title, start, end);
					break;
				case QueryIOConstants.REPORT_IO_STATUS:
					break;
				default:
					return "";
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			return "";
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}

		return attachments;
	}

	public static ArrayList getExportFormat(String exportFormatList) {
		ArrayList exportTypeList = new ArrayList();
		String exportType[] = exportFormatList.substring(1, exportFormatList.length() - 1).split(",");
		for (String exportId : exportType) {
			exportTypeList.add(exportId.trim());
		}
		ArrayList exportFormatType = new ArrayList();
		for (int i = 0; i < exportTypeList.size(); i++) {
			switch (Integer.parseInt((String) exportTypeList.get(i))) {
			case ExportConstants.EXPORT_TYPE_HTML:
				exportFormatType.add(ExportConstants.EXPORT_TYPES[ExportConstants.EXPORT_TYPE_HTML]);
				break;
			case ExportConstants.EXPORT_TYPE_PDF:
				exportFormatType.add(ExportConstants.EXPORT_TYPES[ExportConstants.EXPORT_TYPE_PDF]);
				break;
			case ExportConstants.EXPORT_TYPE_XLS:
				exportFormatType.add(ExportConstants.EXPORT_TYPES[ExportConstants.EXPORT_TYPE_XLS]);
				break;
			default:
				return null;
			}
		}
		return exportFormatType;
	}

	public static ArrayList getReportName(String reportIdList) {
		ArrayList reportTypeList = new ArrayList();
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("getReportName reportIdList: " + reportIdList);
		String reportType[] = reportIdList.substring(1, reportIdList.length() - 1).split(",");

		for (String reportId : reportType) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("getReportName reportId: " + reportId);
			if (!(reportId.trim().equals(QueryIOConstants.EMPTY_STRING)))
				reportTypeList.add(reportId.trim());
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("getReportName reportTypeList: " + reportTypeList);

		ArrayList reportNameList = new ArrayList();
		for (int i = 0; i < reportTypeList.size(); i++) {
			switch (Integer.parseInt((String) reportTypeList.get(i))) {
			case QueryIOConstants.REPORT_DATANODE_STATUS:
				reportNameList.add(QueryIOConstants.DATANODE_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_HDFS_STATUS:
				reportNameList.add(QueryIOConstants.HDFS_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_MAPREDUCE_STATUS:
				reportNameList.add(QueryIOConstants.MAPREDUCE_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_NAMENODE_STATUS:
				reportNameList.add(QueryIOConstants.NAMENODE_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_RM_STATUS:
				reportNameList.add(QueryIOConstants.RM_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_NM_STATUS:
				reportNameList.add(QueryIOConstants.NM_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_STORAGE_STATUS:
				break;
			case QueryIOConstants.REPORT_TOP10:
				break;
			case QueryIOConstants.REPORT_ALERT_STATUS:
				reportNameList.add(QueryIOConstants.ALERT_STATUS_UI);
				break;
			case QueryIOConstants.REPORT_STORAGE_FORECAST:
				reportNameList.add(QueryIOConstants.STORAGE_FORECAST);
				break;
			case QueryIOConstants.REPORT_IO_STATUS:
				break;
			case QueryIOConstants.NN_DETAIL:
				reportNameList.add(QueryIOConstants.NN_DETAIL_UI);
				break;
			case QueryIOConstants.DN_DETAIL:
				reportNameList.add(QueryIOConstants.DN_DETAIL_UI);
				break;
			case QueryIOConstants.REPORT_RM_DETAIL:
				reportNameList.add(QueryIOConstants.RM_DETAIL_UI);
				break;
			case QueryIOConstants.REPORT_NM_DETAIL:
				reportNameList.add(QueryIOConstants.NM_DETAIL_UI);
				break;
			case QueryIOConstants.BILLING_REPORT_USER:
				reportNameList.add(QueryIOConstants.BILLING_REPORT_USER_UI);
				break;
			case QueryIOConstants.BILLING_REPORT_SYSTEM:
				reportNameList.add(QueryIOConstants.BILLING_REPORT_SYSTEM_UI);
				break;
			default:
				return null;
			}
		}
		return reportNameList;
	}

	private static String generateAlertReport(int exportType, String title, Timestamp startTimestamp,
			Timestamp endTimestamp) throws Exception {

		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = QueryIOConstants.ALERT_REPORT_NAME + currentTimestamp.getDate() + "_"
				+ (currentTimestamp.getMonth() + 1) + "_" + (1900 + currentTimestamp.getYear() + "_"
						+ currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());

		AlertStatus report = new AlertStatus(fileName, currentTimestamp, startTimestamp, endTimestamp);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	public static String generateBigQueryResult(String nodeId, String title, int exportType, int pages, String query)
			throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Exporting big query result pages :" + pages);

		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "BigQueryResult_" + currentTimestamp.getDate() + "_" + currentTimestamp.getMonth() + "_"
				+ (1900 + currentTimestamp.getYear() + "_" + currentTimestamp.getHours() + "_"
						+ currentTimestamp.getMinutes());

		BigQueryResult report = new BigQueryResult(nodeId, fileName, query, pages);
		report.setTitle(title);

		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);

		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	public static DWRResponse emailBigDataReport(ArrayList reportTypeList, ArrayList exportFormatList,
			ArrayList usersIdList, String namenodeId, String queryId) {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Email Details :" + reportTypeList + " " + exportFormatList + " " + usersIdList
					+ " " + namenodeId + " " + queryId);

		DWRResponse dwrResponse = new DWRResponse();
		ArrayList attachments = new ArrayList();
		String mailMessage = "BigQuery Result Reports";

		ArrayList usersList = new ArrayList();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			for (int i = 0; i < usersIdList.size(); i++) {
				User user = UserDAO.getUserDetail(connection, Integer.parseInt((String) usersIdList.get(i)));
				usersList.add(user);
			}

			String designFilePath = BigQueryManager.getDesignFilePath(namenodeId, queryId,
					RemoteManager.getLoggedInUser());

			for (int var = 0; var < exportFormatList.size(); var++) {
				String format = (String) exportFormatList.get(var);
				String reportName = ReportGenerator.generateViewReport(designFilePath, format);
				String reportDir = designFilePath.substring(0, designFilePath.lastIndexOf(File.separator));
				if (format.equalsIgnoreCase(ReportConstants.TYPE_HTML)) {
					ArrayList listFiles = new ArrayList();
					File reportFile = new File(reportDir + File.separator + reportName);
					listFiles.add(reportFile);
					String temp = reportName.replace(File.separator, "");
					File folder = new File(reportDir + File.separator + "resources_" + temp.replace(".html", ""));
					listFiles.add(folder);
					String destZipFile = reportDir + File.separator + reportName.replace(".html", ".zip");
					ZipUtil.compressFiles(listFiles, destZipFile);

					attachments.add(destZipFile);
				} else
					attachments.add(reportDir + File.separator + reportName);
			}
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Attachments:" + attachments);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		try {
			mailReport(attachments, usersList, mailMessage);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error mailing Report", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		}
		dwrResponse.setDwrResponse(true, "Report Mailed Successfully", 200);
		return dwrResponse;
	}

	public static void main(String args[]) {

	}
}