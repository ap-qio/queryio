package com.queryio.core.notifier.notifiers.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * This is the EmailSender class responsible for sending Email Notifications.
 * This is the one which does the necessary handshaking with the Smtp Server and
 * then sends the mail.
 * 
 * @author Exceed Consultancy Services
 */
class EmailSender
{
	/**
	 * @param emailNotif
	 */
	private EmailSender()
	{
		// DO NOTHING
	}
	
	private static boolean isNotAlreadyPresent(ArrayList list, String email)
	{
		boolean bNotPresent = true;
		if(list != null && list.size() > 0)
		{
			String em;
			for(int i = 0; i < list.size(); i++)
			{
				em = (String) list.get(i);
				if(em.equalsIgnoreCase(email))
				{
					bNotPresent = false;
					break;
				}
			}
		}
		return bNotPresent;
	}

	// JavaMail implementation
	static void sendMessage(final EmailNotifier emailNotifier, final boolean debug) throws Exception
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		if (debug)
		{
			mailSender.getJavaMailProperties().setProperty("mail.debug", "false");
		}
		mailSender.setHost(emailNotifier.getSmtpAddress());
		mailSender.setProtocol(emailNotifier.isSecureProtocol() ? "smtps":"smtp");
		mailSender.setPort(emailNotifier.getSmtpPort());
		if (emailNotifier.isAuthRequired())
		{
			mailSender.setUsername(emailNotifier.getSendersUserName());
			mailSender.setPassword(emailNotifier.getSendersPassword());
		}
		final ArrayList attachments = emailNotifier.getAttachmentFiles();
		final boolean hasAttachments = (attachments != null) && (attachments.size() > 0);
		
		final LinkedList llEmailAddresses = emailNotifier.getRecepientsAddresses();
		final ArrayList llRecepientsAddresses = new ArrayList();
		String email;
		String emailAddress;
		StringTokenizer stk;
		for(int j = 0; j < llEmailAddresses.size(); j++)
		{
			email = (String) llEmailAddresses.get(j);
			stk = new StringTokenizer(email, ";");
			while(stk.hasMoreTokens())
			{
				emailAddress = stk.nextToken();
				if(isNotAlreadyPresent(llRecepientsAddresses, emailAddress))
				{
					llRecepientsAddresses.add(emailAddress);
				}
			}		
		}
		String [] to = new String [llRecepientsAddresses.size()];
		llRecepientsAddresses.toArray(to);
		final MimeMessage mm = mailSender.createMimeMessage();
		// use the true flag to indicate you need a multipart message
		final MimeMessageHelper mmh = new MimeMessageHelper(mm, hasAttachments);
		// Put our parameters into the message
		mmh.setFrom(emailNotifier.getSendersName() + " <" + emailNotifier.getSendersEmailAddress() + ">");
		mmh.setSubject(emailNotifier.getEmailSubject());
		mmh.setText(emailNotifier.getMessageBody(), Boolean.getBoolean("notification.email.html"));
		// Specifying to mail id
		mmh.setTo(to);
		
		mmh.setPriority(emailNotifier.getImportanceLevel());
		
		if (hasAttachments)
		{
			// Add all the attachments
			for (int i = 0; i < attachments.size(); i++)
			{
				final File file = new File((String) attachments.get(i));
				if (file.exists())
				{
					mmh.addAttachment(file.getName(), file);
				}
			}
		}
		// Now send the message
		mailSender.send(mm);
	}
	
	/*
	// JGmail implementation
	static void sendMessage(final EmailNotifier emailNotifier) throws Exception
	{
		final SMTP smtp = new SMTP(emailNotifier.getSmtpAddress());
		// smtp.addStatusListener(this);

		// Put our parameters into the message
		smtp.setFrom(emailNotifier.getSendersName(), emailNotifier.getSendersEmailAddress());
		smtp.setSubject(emailNotifier.getEmailSubject());
		smtp.setBody(emailNotifier.getMessageBody());
		final ArrayList attachments = emailNotifier.getAttachmentFiles();
		if ((attachments != null) && (attachments.size() > 0))
		{
			for (int i = 0; i < attachments.size(); i++)
			{
				final File file = new File((String) attachments.get(i));
				if (file.exists())
				{
					smtp.addAttachment(file.getAbsolutePath());
				}
			}
		}

		// Specifying to mail id
		final LinkedList llRecepientsAddresses = emailNotifier.getRecepientsAddresses();
		final int iSize = llRecepientsAddresses.size();
		for (int i = 0; i < iSize; i++)
		{
			smtp.addTo(null, (String) llRecepientsAddresses.get(i));
		}

		if (emailNotifier.isAuthRequired())
		{
			smtp.setAuthType(SMTP.AUTH_LOGIN);
			smtp.setUserName(emailNotifier.getSendersUserName());
			smtp.setPassword(emailNotifier.getSendersPassword().toCharArray());
		}

		//Now send the message
		smtp.send();
	}
	*/
}