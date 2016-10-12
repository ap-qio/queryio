package com.queryio.core.monitor.alerts.generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.QueueItem;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.User;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.monitor.controllers.ControllerAttribute;
import com.queryio.core.monitor.controllers.ControllerData;
import com.queryio.core.monitor.dstruct.Rule;
import com.queryio.core.monitor.managers.AlertManager;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.common.INotifierConstants;

/**
 * This is the class responsible for actual generation of Alerts for the User.
 * 
 * @author Exceed Consultancy Services
 */
public class AlertGenerator implements QueueItem
{
	private static final String DONOT_ALERT = "{do.not.notify}";
	private static final String ALERT_DETAILS = "{details}";
	private ArrayList rules = null;
	private String nodeId = null;
	private long dataTimeStamp = -1;
	private final boolean alertRaised;

	public AlertGenerator(boolean alertRaised, String nodeId, long dataTimeStamp, ArrayList rules)
	{
		this.alertRaised = alertRaised;
		this.rules = rules;
		this.nodeId = nodeId;
		this.dataTimeStamp = dataTimeStamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.queue.QueueItem#serve()
	 */
	public void serve()
	{
		try
		{
			// generates the alerts for the various rules violated
			this.generate();
		}
		catch (final Exception ex)
		{
			AppLogger.getLogger().fatal("serve() failed in AlertGenerator with Exception: " + ex.getMessage(), ex);
		}
	}

	/**
	 * This is the method which generates the alerts for violated rules.
	 * 
	 * @throws Exception
	 */
	private void generate() throws Exception
	{
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("AlertGenerator generate()");
		
		Connection connection = null;

		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			// 2. get the Notification Settings for all the Notifs
			final NotifyBean notify = NotifyDAO.getNotificationSettings(connection);

//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("NotifyBean: " + notify);
			// 3. make the list of enabled notifications
			final ArrayList alEnabledNotifications = new ArrayList(5);
			if (notify.isEmailEnabled())
			{
				alEnabledNotifications.add(AlertGenerationManager.NOTIF_EMAIL);
			}
			if (notify.isSmsEnabled())
			{
				alEnabledNotifications.add(AlertGenerationManager.NOTIF_SMS);
			}
			if(notify.isLogEnabled())
			{
				alEnabledNotifications.add(AlertGenerationManager.NOTIF_LOG);
			}
			// Added to handle SMS via email internally without user configuring SMS notification
			boolean implicit = false;
			if (notify.isEmailEnabled() && !notify.isSmsEnabled())
			{
				implicit = true;
				alEnabledNotifications.add(AlertGenerationManager.NOTIF_SMS);
			}
			
			alEnabledNotifications.trimToSize();

			// 4. if none of the notifications are enabled then return no
			// further processing needed
			if (alEnabledNotifications.size() == 0)
			{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("No notifications enabled");
				return;
			}
			
			String details = null;
			String details1 = null;
			
			// 5. for each Rule Violated get all the Users to be notified
			// for each enabled notifiactions
			for (final Iterator iter = this.rules.iterator(); iter.hasNext();)
			{
				final Rule rule = (Rule) iter.next();

				// 6. for each enabled notifier which has got users for notification iterate through the Users
				// and notify the users for the alert violation
				final String subject = alertRaised ? rule.getAlertRaisedNotificationSubject():
					rule.getAlertResetNotificationSubject();
				
				if(DONOT_ALERT.equals(subject))
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Not performing notification for rule id: " + rule.getRuleId() + " alert state: " + (alertRaised ? "raised":"reset") + " as subject of the notification is " + DONOT_ALERT); //$NON-NLS-1$
					continue;
				}
				
				String msg = alertRaised ? rule.getAlertRaisedNotificationMessage():
					rule.getAlertResetNotificationMessage();
				String msg1 = msg;
				
				if (msg != null && msg.indexOf(ALERT_DETAILS) != -1)
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Sending mail with attribute values");
					
					if (details == null)
					{
						final StringWriter sw = new StringWriter();
						final PrintWriter pw = new PrintWriter(sw);
						
						final StringWriter sw1 = new StringWriter();
						final PrintWriter pw1 = new PrintWriter(sw1);

						//boolean bHtml = Boolean.getBoolean("notification.email.html");
						pw.print("Alert ");
						pw.print(alertRaised ? " raised":"reset");
						pw.print(" by Rule id ");
						pw.print(rule.getRuleId());
						pw.print(" for Node Id ");
						pw.print(rule.getNodeId());
						pw.println('.');

						pw1.print("Alert ");
						pw1.print(alertRaised ? " raised":"reset");
						pw1.print(" by Rule id ");
						pw1.print(rule.getRuleId());
						pw1.print(" for Node Id ");
						pw1.print(rule.getNodeId());
						pw1.println('.');
						
						
						
//						pw.print("<BR />");
						pw.println("Data collected by monitor is as follows: ");
						pw1.println("Data collected by monitor is as follows: ");
//						pw.print("<BR />");
						
						ControllerData cd = MonitorDAO.getControllerData(connection, nodeId, dataTimeStamp);
						List attributes = RemoteManager.getAttributeList(nodeId);
						
						HashMap map = new HashMap();
						AlertManager.fillAlertAttributes(map);
						
						ArrayList alertAttributes = (ArrayList) map.get(rule.getNodeId()); 
						
						if (attributes != null && attributes.size() > 0 && cd != null)
						{
							ControllerAttribute ca;
							Object value;
							for (int i = 0; i < attributes.size(); i++) 
							{
								ca = (ControllerAttribute)attributes.get(i);
								
								if(alertAttributes != null && !alertAttributes.contains(ca.getName()))	continue;
								
								pw.print(ca.getDisplayName() != null ? ca.getDisplayName():ca.getShortName());
								pw.print(" = ");
								pw1.print(ca.getDisplayName() != null ? ca.getDisplayName():ca.getShortName());
								pw1.print(" = ");
								value = cd.getValue(ca.getColumnName());
								if (value != null)
								{
									pw.println(value);
//									pw.print("<BR />");
									pw1.println(value);
								}
								else
								{
									pw.println("Not available!!!");
									pw1.println("Not available!!!");
								}
							}
						}
						else
						{
							pw.println("No attributes present.");
							pw1.println("No attributes present.");
						}
						details = sw.toString();
						details1 = sw1.toString();
					}
					msg = StaticUtilities.searchAndReplace(msg, ALERT_DETAILS, details);
					msg1 = StaticUtilities.searchAndReplace(msg1, ALERT_DETAILS, details1);
				}
				else
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Sending mail without attribute values");
				}
				
				final HashMap hm = new HashMap();
				
				ArrayList users = UserDAO.getUsersDetails(connection);
				User user = null;
				ArrayList userList = new ArrayList();
				for(int i=0; i<users.size(); i++)
				{
					user = (User) users.get(i);
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("User name: " + user.getUserName());
					String roleName = "";
					try
					{
						roleName = UserDAO.getRole(connection, user.getUserName());
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal("Role name could not be obtained for user: " + user.getUserName(), e);
					}
					
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("User role: " + roleName);
					
					if(roleName.equalsIgnoreCase(QueryIOConstants.ROLES_ADMIN))
					{
						userList.add(user);
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Alert Notify User: " + user.getUserName());
					}
				}
				
				if(rule.getNotificationType().equalsIgnoreCase(INotifierConstants.NOTIF_EMAIL))
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Notification Type set for rule is: " + rule.getNotificationType());
					hm.put(INotifierConstants.NOTIF_EMAIL, userList);
				}
				else if(rule.getNotificationType().equalsIgnoreCase(INotifierConstants.NOTIF_SMS))
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Notification Type set for rule is: " + rule.getNotificationType());
					hm.put(INotifierConstants.NOTIF_SMS, userList);
				}
				else if(rule.getNotificationType().equalsIgnoreCase(INotifierConstants.NOTIF_LOG))
				{
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Notification Type set for rule is: " + rule.getNotificationType());
					hm.put(INotifierConstants.NOTIF_LOG, userList);
				}
				
				NotificationHandler.sendNotifications(notify, hm, subject, msg, msg1, implicit, nodeId);
			}
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

	}

}
