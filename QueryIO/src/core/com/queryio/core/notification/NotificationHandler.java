package com.queryio.core.notification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.User;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.Notifier;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.NotificationManager;


public class NotificationHandler 
{
	private NotificationHandler()
	{
		// do nothing
	}
	
	public static void sendNotifications(final NotifyBean notify, final HashMap hm, final String subject, 
		String htmlMsg, final String nonHTMLMsg, final boolean implicitSMS, String nodeId) throws Exception
	{
		final NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		
		boolean espGenerate = false;
		for (final Iterator iterNotifiers = hm.keySet().iterator(); iterNotifiers.hasNext();)
		{
			final String notifierType = (String) iterNotifiers.next();

			if (notifierType.equals(INotifierConstants.NOTIF_EMAIL))
			{
				// length > 1 MB
				if (htmlMsg.length() > 1024 * 1024) {
					if (nonHTMLMsg.length() < (1024 * 1024)) {
						htmlMsg = nonHTMLMsg;
					} else {
						htmlMsg = htmlMsg.substring(0, 1024 * 1024);
					}
				}
				generateEmailNotification(notifMgr, subject, htmlMsg, notify, (ArrayList) hm.get(notifierType), false);
			}
			else if (notifierType.equals(INotifierConstants.NOTIF_SMS))
			{
//				// length > 1 MB
//				if (htmlMsg.length() > 1024 * 1024) {
//					if (nonHTMLMsg.length() < (1024 * 1024)) {
//						htmlMsg = nonHTMLMsg;
//					} else {
//						htmlMsg = htmlMsg.substring(0, 1024 * 1024);
//					}
//				}
//				generateSmsNotification(notifMgr, subject, htmlMsg, notify, (ArrayList) hm.get(notifierType), false, implicitSMS);
			}
			else if (notifierType.equals(INotifierConstants.NOTIF_LOG))
			{
				generateLogNotification(notifMgr, nonHTMLMsg, notify, false);
			}
		}
	}
	
	/**
	 * This is the method which iterates through all the Users and notifies the
	 * user by sending mail
	 * 
	 * @param notify
	 * @param usersToBeNotified
	 * @throws Exception
	 */
	public static void generateEmailNotification(final NotificationManager notifMgr, final String notificationSubject, final String notificationMessage, 
		final NotifyBean notify, final ArrayList usersToBeNotified, boolean reThrowException) throws Exception
	{
		Notifier emailNotifier = null;
		PropertySet emailPropSet = null;
		NotificationEvent notifEvent = null;

		if ((usersToBeNotified == null) || (usersToBeNotified.size() == 0))
		{
//			AppLogger.getLogger().fatal("Email notification is called without any recepients, mail subject: " + notificationSubject);
			return;
		}

		String email = null;
		Object listObject;
		for (final Iterator iter = usersToBeNotified.iterator(); iter.hasNext();)
		{
			listObject = iter.next();
			if (listObject instanceof User)
			{
				email = ((User)listObject).getEmail();
			}
			else
			{
				email = listObject.toString();
			}
			if (email != null && !email.equals("") && email != null)
			{
				if (emailNotifier == null)
				{
					try
					{
						// initialize the email notifier
						emailNotifier = notifMgr.getEventNotifier(INotifierConstants.ALERT_RAISED_NOTIFICATION,
							INotifierConstants.MC_EMAIL_NOTIFIER);
					}
					catch (final Exception ex)
					{
						// want to supress the exception, bcoz this will not
						// allow the other notifications to
						// be carried out.
						final String msg = "Error getting Email notifier.";
//						AppLogger.getLogger().fatal(msg, ex);
						if (reThrowException)
						{
							throw ex;
						}
						return;
					}

					// creating the propertyset for the email notifier
					emailPropSet = new PropertySet(INotifierConstants.MC_EMAIL_NOTIFIER_PROPERTYSET);

					emailPropSet.addProperty(INotifierConstants.SMTP_PORT, notify.getEmailSMTPPort());
					emailPropSet.addProperty(INotifierConstants.SMTP_SECURE, String.valueOf(notify.isSecuredProtocol()));
					emailPropSet.addProperty(INotifierConstants.SMTP_ADDRESS, notify.getEmailSMTPServer());
					emailPropSet.addProperty(INotifierConstants.SENDER_ADDRESS, notify.getEmailSenderAddress());
					emailPropSet.addProperty(INotifierConstants.SENDER_NAME, notify.getEmailSenderName());
					emailPropSet.addProperty(INotifierConstants.SENDER_USER_NAME, notify.getEmailUsername());
					emailPropSet.addProperty(INotifierConstants.SENDER_PASSWORD, notify.getEmailPassword());
					emailPropSet.addProperty(INotifierConstants.AUTHREQD, String.valueOf(notify.isAuthRequired()));
//					if (controllerState.equals(ControllerManager.CONTROLLER_STATE_POLLING))
//						emailPropSet.addProperty(INotifierConstants.NOTIF_PRIORITY, "1");
//					else
//						emailPropSet.addProperty(INotifierConstants.NOTIF_PRIORITY, "3");
					emailPropSet.addProperty(INotifierConstants.NOTIF_PRIORITY, "1");
					emailNotifier.setPropertySet(emailPropSet);
				}
				emailPropSet.addProperty(INotifierConstants.RECIPIENTS_ADDRESS, email);
			}
		}
		notifEvent = new NotificationEvent(INotifierConstants.ALERT_RAISED_NOTIFICATION);
		if (notificationSubject != null && notificationSubject.trim().length() > 0)
		{
			notifEvent.addProperty(INotifierConstants.ALERT_SUBJECT, notificationSubject);
		}
		notifEvent.addProperty(INotifierConstants.ALERT_MESSAGE, notificationMessage);
		
		if (notify.getAttachments() != null && notify.getAttachments().size() > 0)
		{
			notifEvent.addProperty(INotifierConstants.MESSAGE_ATTACHMENTS, notify.getAttachments());
		}

		try
		{
			notifMgr.fireEventReceived(notifEvent, emailNotifier);
		}
		catch (final Exception ex)
		{
			// want to supress the exception, bcoz this will not allow the other
			// notifications to be carried out.
//			AppLogger.getLogger().fatal(
//				"Error notifying through Email notification.", ex);
			if (reThrowException)
			{
				throw new Exception("Error Generating Notification: ", ex);
			}
		}
	}

	/**
	 * This is the method which iterates through all the Users and notifies the
	 * user by sending Sms using the ComPort
	 * 
	 * @param notify
	 * @param usersToBeNotified
	 * @throws Exception
	 */
//	static void generateSmsNotification(final NotificationManager notifMgr, final String subject, 
//		final String notificationMessage, final NotifyBean notify, final ArrayList usersToBeNotified, 
//		boolean reThrowException, boolean forcedSMSThruEmail) throws Exception
//	{
//		Notifier smsNotifier = null;
//		PropertySet smsPropSet = null;
//		NotificationEvent notifEvent = null;
//
//		if ((usersToBeNotified == null) || (usersToBeNotified.size() == 0))
//		{
//			return;
//		}
//		
//		final ArrayList smsThruEmailList = new ArrayList();
//		boolean found = false;
//
//		Object listObject;
//		for (final Iterator iter = usersToBeNotified.iterator(); iter.hasNext();)
//		{
//			listObject = iter.next();
//			String mobileNo = null;
//			if (forcedSMSThruEmail)
//			{
//			if (!listObject.toString().equals("") && listObject.toString() != null)
//				{
//					smsThruEmailList.add(listObject.toString());
//				}
//			}
//			else
//			{
//				if (listObject instanceof User)
//				{
//					mobileNo = ((User)listObject).getMobileno();
//				}
//				else
//				{
//					mobileNo = listObject.toString();
//				}
//			}
//
//			if (mobileNo != null && !mobileNo.equals(""))
//			{
//				found = true;
//				if (smsNotifier == null)
//				{
//					try
//					{
//						// initialize the email notifier
//						smsNotifier = notifMgr.getEventNotifier(INotifierConstants.ALERT_RAISED_NOTIFICATION,
//							INotifierConstants.MC_SMS_NOTIFIER);
//					}
//					catch (final Exception ex)
//					{
//						// want to supress the exception, bcoz this will not
//						// allow the other notifications to
//						// be carried out.
//						AppLogger.getLogger().fatal("Error getting SMS notifier.", ex);
//						if (reThrowException)
//						{
//							throw ex;
//						}
//						return;
//					}
//
//					// creating the propertyset for the email notifier
//					smsPropSet = new PropertySet(INotifierConstants.MC_SMS_NOTIFIER_PROPERTYSET);
//
//					smsPropSet.addProperty(INotifierConstants.SENDERS_MOBILE_NO, notify.getSmsNumber());
//					smsPropSet.addProperty(INotifierConstants.COM_PORT, notify.getSmsSerialPort());
//					smsPropSet.addProperty(INotifierConstants.MOBILE_MFG, notify.getSmsManufacturer());
//					smsPropSet.addProperty(INotifierConstants.MOBILE_MODEL, notify.getSmsModel());
//					smsPropSet.addProperty(INotifierConstants.BAUD_RATE, notify.getSmsBaudRate());
//
//					smsNotifier.setPropertySet(smsPropSet);
//				}
//
//				smsPropSet.addProperty(INotifierConstants.RECIPIENTS_MOBILE_NO, mobileNo);
//			}
//		}
//		if (found)
//		{
//			notifEvent = new NotificationEvent(INotifierConstants.ALERT_RAISED_NOTIFICATION);
//			notifEvent.addProperty(INotifierConstants.ALERT_MESSAGE, notificationMessage);
//	
//			try
//			{
//				notifMgr.fireEventReceived(notifEvent, smsNotifier);
//			}
//			catch (final Exception ex)
//			{
//				// want to supress the exception, bcoz this will not allow the other
//				// notifications to
//				// be carried out.
//				AppLogger.getLogger().fatal(
//					"Error notifying through SMS notification.", ex);
//				if (reThrowException)
//				{
//					throw ex;
//				}
//			}
//		}
//		if (notify.isEmailEnabled() && smsThruEmailList.size() > 0)
//		{
//			generateEmailNotification(notifMgr, subject, notificationMessage, notify, smsThruEmailList, reThrowException);
//		}
//	}

	
	
	public static void generateLogNotification(final NotificationManager notifMgr, final String notificationMessage, final NotifyBean notify,
		boolean reThrowException) throws Exception
	{
		Notifier logNotifier = null;
		PropertySet logPropSet = null;
		NotificationEvent notifEvent = null;
		try
		{
			// initialize the AOL Messenger notifier
			logNotifier = notifMgr.getEventNotifier(INotifierConstants.ALERT_RAISED_NOTIFICATION,
					INotifierConstants.MC_LOG_NOTIFIER);
		}
		catch (final Exception ex)
		{
			// want to supress the exception, bcoz this will not
			// allow the other notifications to
			// be carried out.
			AppLogger.getLogger().fatal("Error Generating Notification", ex);
			if (reThrowException)
			{
				throw new Exception("Error Generating Notification");
			}
			return;
		}

		// creating the propertyset for the email notifier
		logPropSet = new PropertySet(INotifierConstants.MC_LOG_NOTIFIER_PROPERTYSET);
		logPropSet.addProperty(INotifierConstants.LOGFILE_NAME, notify.getLogFilePath());
		logNotifier.setPropertySet(logPropSet);

		notifEvent = new NotificationEvent(INotifierConstants.ALERT_RAISED_NOTIFICATION);
		notifEvent.addProperty(INotifierConstants.LOGFILE_NAME, notify.getLogFilePath());
		notifEvent.addProperty(INotifierConstants.ALERT_MESSAGE, notificationMessage);
		
		try
		{
			notifMgr.fireEventReceived(notifEvent, logNotifier);
		}
		catch (final Exception ex)
		{
			AppLogger.getLogger().fatal("Error Generating Notification.", ex);
			if (reThrowException)
			{
				throw new Exception("Error Generating Notification");
			}
		}
	}
	
	public static void validateNotification(final NotifyBean notifyBean, String sNotify) throws Exception
	{
		final NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		
		ArrayList usersToBeNotified = new ArrayList();
		if (INotifierConstants.NOTIF_EMAIL.equals(sNotify))
		{
			usersToBeNotified.add(notifyBean.getEmailSenderAddress());
			
			NotificationHandler.generateEmailNotification(notifMgr, "QueryIO: Validate Notification Settings", 
				"This is a test mail from QueryIO to validate email notification settings.", 
				notifyBean, usersToBeNotified, true);
		}
		else if(INotifierConstants.NOTIF_LOG.equals(sNotify)) {
			String path = notifyBean.getLogFilePath();
			File file = new File(path);
			if(!file.exists()){
				if(file.createNewFile()){
//					file.delete();
				}else{
					throw new FileNotFoundException();
				}
			}
		}
	}
	
	public static boolean isEmailValid(String email)
	{
		email = email.trim();

		if (email.length() == 0)
		{
			return false;
		}
		StringBuffer invalidEmail = new StringBuffer();
		StringTokenizer stk = new StringTokenizer(email, ";");
		while(stk.hasMoreTokens())
		{
			email = stk.nextToken();
			email = email.trim();
			final int length = email.length();
			final int indexOfAtTheRate = email.indexOf('@');
			final int lastIndexOfAtTheRate = email.lastIndexOf('@');
			final int lastIndexOfDot = email.lastIndexOf('.');
	
			if (!((indexOfAtTheRate > 0 /* @ is not the first char */)
					&& (indexOfAtTheRate < length - 1 /* @ is not the last char */)
					&& (indexOfAtTheRate == lastIndexOfAtTheRate /*
								 * only one @
								 * exists
								 */) && (lastIndexOfDot > 0 /* . is not the first char */)
					&& (lastIndexOfDot < length - 1 /* . is not the last char */)
					&& (indexOfAtTheRate < lastIndexOfDot /* @ comes before . */)
					&& (lastIndexOfDot - indexOfAtTheRate != 1 /*
								 * . and @ don't
								 * come together
								 */)/* && Character.isLetter(email.charAt(0))*/ /* first character is a letter */
			))
			{
				invalidEmail.append(email);
				invalidEmail.append("\n");
			}
		}
		if(invalidEmail.toString().length() > 0)
		{
			return false;
		}
		return true;
	}
}