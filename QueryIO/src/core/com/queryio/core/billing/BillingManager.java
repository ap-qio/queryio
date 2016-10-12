package com.queryio.core.billing;

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
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.User;
import com.queryio.core.dao.BillingDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.core.reports.nodes.BillingInvoice;

public class BillingManager
{
	private static String imgSrc = EnvironmentalConstants.getAppHome() + "images/hpcsReport.jpg";
	
	private static String generateBillingReport(int exportType, String title, Timestamp startTimestamp, Timestamp endTimestamp) throws Exception
	{
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String fileName =  QueryIOConstants.BILLING_INVOICE_REPORT + currentTimestamp.getDate() + "_" + 
		(currentTimestamp.getMonth()+1) + "_" + (1900 + currentTimestamp.getYear() + "_" + 
		currentTimestamp.getHours() + "_" + currentTimestamp.getMinutes());
		
		BillingInvoice report = new BillingInvoice(fileName, startTimestamp, endTimestamp, title);	
		
		ExportManager.getInstance().exportNode(null, imgSrc, report, exportType);
		
		return fileName + "." + ExportConstants.getFileExtension(exportType);
	}

	public static void mailBillingReport(String exportFormat, ArrayList usersIdList, String title, String startTime, String endTime, boolean defaultReport) throws Exception
	{
		NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		NotifyBean notify = new NotifyBean();
		
		ArrayList usersList = null;
		
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			notify = NotifyDAO.getNotificationSettings(connection);
			
			if (defaultReport)
			{
				Timestamp start = parseDateString(startTime);
				Timestamp end = parseDateString(endTime);
				
				if (!BillingDAO.updateBillingReportEntry(connection, end))		// If returns true, then entry already present and updated. No need to insert entry.
					BillingDAO.insertBillingReportEntry(connection, BillingDAO.calculateBillingData(connection, NodeDAO.getAllDatanodes(connection), start, end));
			}
			
			usersList = new ArrayList();
			for(int i = 0; i < usersIdList.size(); i++)
			{
				User user = UserDAO.getUserDetail(connection, Integer.parseInt((String)usersIdList.get(i)));
				usersList.add(user);
			}
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("Error in Mailing Report", e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing connection", e);
			}	
		}
		
		ArrayList attachments = new ArrayList();
		
		String reportName = viewBillingReport(exportFormat, title, startTime, endTime);
		attachments.add(EnvironmentalConstants.getAppHome() + reportName);
		if (Integer.parseInt(exportFormat) == ExportConstants.EXPORT_TYPE_HTML)
			attachments.add(EnvironmentalConstants.getAppHome() + reportName + QueryIOConstants.REPORTS_HTML_FILES_APPEND);
		
		notify.setAttachments(attachments);
		NotificationHandler.generateEmailNotification(notifMgr, title, title, notify, usersList, true);
	}
	
	public static String viewBillingReport(String exportFormat, String title, String startTime, String endTime)
	{
		// generate attachments
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(exportFormat + " " + " " + title + " " + startTime + " " + endTime);
		
		Timestamp start = parseDateString(startTime);
		Timestamp end = parseDateString(endTime);
		
		String attachments = "";
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();

			attachments =  QueryIOConstants.REPORTS_QUERYIO + File.separator  + generateBillingReport(Integer.parseInt(exportFormat), title, start, end);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Error Exporting Report", e);
			return "";
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		return attachments;
	}
	
	// For Billing Report Data
	public static SummaryTable getBillingReportSummaryTable(Timestamp startTime, Timestamp endTime)
	{
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			return BillingDAO.getBillingReportSummaryTable(connection, HostDAO.getDataNodeHostNames(connection), startTime, endTime);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("getBillingReportSummaryTable() failed with exception: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}
	
	public static Timestamp parseDateString(String dateString)
	{
		Timestamp ts = null;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dt = format.parse(dateString);
			ts = new Timestamp(dt.getTime());
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(),e);
		}
		return ts;
	}
	
//	public static void main(String args[])
//	{
//		System.out.println(viewBillingReport("0", "Sample Invoice", "07/05/2012 06:00:00", "07/06/2012 06:00:00"));
//	}
}