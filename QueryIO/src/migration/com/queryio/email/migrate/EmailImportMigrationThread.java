package com.queryio.email.migrate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.datasources.DataSourceManager;
import com.queryio.core.datasources.EmailDataSource;
import com.queryio.core.requestprocessor.FSOperationUtil;
import com.queryio.stream.util.QIODFSOutputStream;

public class EmailImportMigrationThread extends Thread
{
	private int unitCount = 10;
	private MigrationInfo migrationInfo;

	private FileSystem dfs;
	
	Store store;
	
	boolean flag;
	
	boolean overwrite;
	
	private String emailAddress;
	private String password;
	private String mailServer;
	private String account;
	private String protocol;
	private String socket;
	private int port;
	private long connectionTimeOut;
	private long readTimeOut;
	private String folder;
	private String suffix = "";
	private String prefix = "";
	private Date startTime = null;
	private Date endTime = null;
	
	private String loginUser;
	private String loginUserGroup;
	
	Configuration conf = null;
	
	Folder inbox;
	
	public EmailImportMigrationThread(String user, String group, MigrationInfo migrationInfo, String dataConnection, String folder, String startDate, String endDate, String prefix, String suffix, Configuration conf, boolean overwrite) throws Exception
	{
		this.loginUser = user;
		this.loginUserGroup = group;
		this.overwrite = overwrite;
		
		EmailDataSource ds = new EmailDataSource();
		ds = DataSourceManager.getEmailDataSource(dataConnection);
		
		this.emailAddress = ds.getEmailAddress();
		this.password = ds.getPassword();
		this.mailServer = ds.getMailServerAddress();
		this.account = ds.getAccountName();
		this.protocol = ds.getProtocol();
		this.socket = ds.getSocketType();
		this.port = ds.getPort();
		this.connectionTimeOut = ds.getConnectionTimeOut();
		this.readTimeOut = ds.getReadTimeOut();
		this.folder = folder;

		if ((suffix != null) && (!suffix.isEmpty()))
			this.suffix = suffix;
		
		if ((prefix != null) && (!prefix.isEmpty()))
			this.prefix = prefix;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if ((startDate != null) && (!startDate.isEmpty()))
			this.startTime = formatter.parse(startDate);
		
		if ((endDate != null) && (!endDate.isEmpty()))
			this.endTime = formatter.parse(endDate);
		
		Thread.currentThread().setName(this.loginUser);
		this.migrationInfo = migrationInfo;
		
		dfs = FileSystem.get(conf);
		dfs.setConf(conf);
		
		this.conf = conf;
		this.overwrite = overwrite;
		
		this.connect();
	}
	
	@Override
	public void interrupt()
	{
		flag = false;
		
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_STOPPED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			MigrationInfoDAO.update(connection, migrationInfo);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
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
		super.interrupt();
		try
		{
			if(dfs != null)
				dfs.close();
		}
		catch(IOException e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		try
		{
			if(store != null)
				store.close();
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		try
		{
			inbox.close(true);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	@Override
	public void run()
	{
		flag = true;
		Connection connection = null;		
		try{			
			
			FSOperationUtil.createDirectoryRecursively(conf, conf.get(DFSConfigKeys.DFS_NAMESERVICE_ID), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), migrationInfo.getDestinationPath(), loginUser, loginUserGroup);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			int messageCount = 0;
			
			SearchTerm searchTerm = null;
			
			if(this.protocol.equalsIgnoreCase("pop3"))
				messageCount = inbox.getMessageCount();
			else
			{
				searchTerm = returnRequiredSearchTerm();
				if(searchTerm == null)//inbox.getMessage(0).getReceivedDate() == null)
					messageCount = inbox.getMessageCount();
				else
					messageCount = inbox.search(searchTerm).length;
			}
			
			unitCount = 5/100 * messageCount;
			if(unitCount == 0)
				unitCount = 1;
			int successCount = 0;
			int failCount = 0;
			int connectRetry=0;
			for(int i = 1; i <= messageCount; i ++)
			{
				if(flag)
				{
					if(i % unitCount == 0)
					{
						try	
						{	
							connection = CoreDBManager.getQueryIODBConnection();
							MigrationInfoDAO.update(connection, migrationInfo);
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal(e.getMessage(), e);
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
					Path importedFilePath = null;
					Path attachmentPath = null;
					
					try
					{
						Message msg = null;
						try
						{
							if(searchTerm == null || this.protocol.equalsIgnoreCase("pop3"))
								msg = inbox.getMessage(i);
							else
								msg = inbox.search(searchTerm)[i];
						}
						catch(MessagingException e)
						{
							Thread.sleep(3000);
							if(connectRetry == 3)
							{
								throw e;
							}
							else 
							{
								try
								{
									AppLogger.getLogger().fatal("IOException occurred.. Exception: " + e.getMessage(), e);
									
									if(isConnected())
									{
										connectRetry = 0;
										failCount ++;
										continue;
									}
									else
									{
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reconnecting to Mail server...");
										this.connect();
										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection successful.");
									}
									
									connectRetry = 0;
								}
								catch(Exception ex)
								{
									connectRetry ++;
									AppLogger.getLogger().fatal("Could not connect to Mail server, exception: " + ex.getMessage(), ex);
								}
								i--;
								continue;
							}
						}
						if(msg != null)
						{
							String attach = getFileName(i);
							
							importedFilePath = new Path(migrationInfo.getDestinationPath(), attach + ".eml");
							createObject(importedFilePath, msg, overwrite, migrationInfo.isUnzip());
							
							//Uncomment to save attachments separately
//								List<File> attachments = new ArrayList<File>();
//								attachments = getAttachments(msg);
//								
//								if(attachments != null && attachments.size() != 0)
//								{
//									for(int k=0; k<attachments.size(); k++)
//									{
//										if(migrationInfo.getDestinationPath().endsWith("/"))
//											attachmentPath = new Path(migrationInfo.getDestinationPath() + attach, attachments.get(k).getName());
//										else
//											attachmentPath = new Path(migrationInfo.getDestinationPath() + "/" + attach, attachments.get(k).getName());
//										saveAttachments(attachmentPath, attachments.get(k), overwrite, migrationInfo.isUnzip());
//									}
//								}
							successCount ++;
						}
					}
					catch(Exception e)
					{
						connectRetry = 0;
						AppLogger.getLogger().fatal("Could not save mail. Exception: " + e.getMessage(), e);
						try
						{
							dfs.delete(importedFilePath, true);
						}
						catch(Exception ex)
						{
							AppLogger.getLogger().fatal(ex.getMessage(), ex);
						}
						failCount ++;
					} 
					
					migrationInfo.setProgress((successCount * 100 / messageCount));
					migrationInfo.setStatus("Migrated "+successCount +" of "+ messageCount +" objects." + (failCount > 0 ? " Failed cases: " + failCount + "." : ""));
				}												
			}
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		}	
		finally
		{
			try
			{
				if(store != null)
					store.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try
			{
				if(inbox != null)
					inbox.close(true);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try 
			{
				if(dfs != null)
					dfs.close();
			}
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}				
		}
		if(flag)
		{
			try	
			{	
				connection = CoreDBManager.getQueryIODBConnection();
				MigrationInfoDAO.update(connection, migrationInfo);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
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
	
	private String getFileName(int cnt) throws Exception
	{
		String fileName = "";
		if(!prefix.isEmpty() && !suffix.isEmpty())
			fileName = prefix + "_" + cnt + "_" + suffix;
		else if(!prefix.isEmpty() && suffix.isEmpty())
			fileName = prefix + "_" + cnt;
		else if(prefix.isEmpty() && !suffix.isEmpty())
			fileName = cnt + "_" + suffix;
		else
			fileName = "" + cnt;
		return fileName;
	}
	
	private Path createObject(Path objectPath, Message msg, boolean overwrite, boolean unzip) throws Exception{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating object: " + objectPath.toString());

			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			try{
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
				try {
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null);
				} catch (Exception e) {
					if (dfsOutputStream != null) {
						dfsOutputStream.close();
					}
					throw e;
				}
				
//				writeExceptAttachments(msg, qioOutputStream);
				msg.writeTo(qioOutputStream);
				
				dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
			}finally{
				try{
					if(qioOutputStream != null){
						qioOutputStream.close();
					}
				} catch(Exception e){
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			return objectPath;
	}
	
	private void writeExceptAttachments(Message msg, OutputStream stream) throws Exception
	{
		 Multipart mp = (Multipart) msg.getContent();
		 int count = mp.getCount();
		 for (int i = 0; i < count; i++)
		 {
			 String str = getText(mp.getBodyPart(i));
			 if(str != null)
				 stream.write(str.toString().getBytes());
		 }
	}
	
	 private String getText(Part p) throws MessagingException, IOException 
	 {
		 boolean textIsHtml = false;
		 if (p.isMimeType("text/*")) 
		 {
			 String s = (String)p.getContent();
			 textIsHtml = p.isMimeType("text/html");
			 return s;
		 }

		 if (p.isMimeType("multipart/alternative")) 
		 {
			 Multipart mp = (Multipart)p.getContent();
			 String text = null;
			 for (int i = 0; i < mp.getCount(); i++) 
			 {
				 Part bp = mp.getBodyPart(i);
				 if (bp.isMimeType("text/plain")) 
				 {
					 if (text == null)
						 text = getText(bp);
					 continue;
				 } else if (bp.isMimeType("text/html")) 
				 {
					 String s = getText(bp);
					 if (s != null)
						 return s;
				 } 
				 else 
				 {
					 return getText(bp);
				 }
			 }
			 return text;
		 } 
		 else if (p.isMimeType("multipart/*")) 
		 {
			 Multipart mp = (Multipart)p.getContent();
			 for (int i = 0; i < mp.getCount(); i++) 
			 {
				 String s = getText(mp.getBodyPart(i));
				 if (s != null)
					 return s;
			 }
		 }
		 return null;
	 }
	
	private Path saveAttachments(Path objectPath, File f, boolean overwrite, boolean unzip) throws Exception{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Creating object: " + objectPath.toString());

			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			try{
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true); 
				try {
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, migrationInfo.getCompressionType(), migrationInfo.getEncryptionType(), null);
				} catch (Exception e) {
					if (dfsOutputStream != null) {
						dfsOutputStream.close();
					}
					throw e;
				}
				
				f.setWritable(true);
				
				InputStream is = new FileInputStream(f);
				final byte[] readBuffer = new byte[8192];
				int bytesIn = 0;
				while((bytesIn = is.read(readBuffer, 0, readBuffer.length)) != -1)
				{
					qioOutputStream.write(readBuffer, 0, bytesIn);
				}
				
				dfs.setOwner(objectPath, this.loginUser, this.loginUserGroup);
			}finally{
				try{
					if(qioOutputStream != null){
						qioOutputStream.close();
					}
				} catch(Exception e){
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			return objectPath;
	}
	
	public  long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}
	
	public void connect() throws Exception
	{
		 String connTimeProp = "mail." + this.protocol.toLowerCase() + ".connectiontimeout";
		 String readTimeProp = "mail." + this.protocol.toLowerCase() + ".timeout";
		 String portProp = "mail." + this.protocol.toLowerCase() + ".port";
		 
		 Properties props = System.getProperties();
		 
		 if(this.protocol.equalsIgnoreCase("imap"))
			 props.setProperty("mail.store.protocol", this.protocol.toLowerCase() + "s");
		 else
			 props.setProperty("mail.store.protocol", this.protocol.toLowerCase());
		 
		 props.setProperty(connTimeProp, String.valueOf(this.connectionTimeOut));
		 props.setProperty(readTimeProp, String.valueOf(this.readTimeOut));
		 props.setProperty(portProp, String.valueOf(this.port));

		 Session session = Session.getDefaultInstance(props, null);
		 
		 if(this.protocol.equalsIgnoreCase("imap"))
			 store = session.getStore(this.protocol.toLowerCase() + "s"); //TODO for Socket type
		 else
			 store = session.getStore(this.protocol.toLowerCase()); //TODO for Socket type
		 
		 store.connect(this.mailServer, this.emailAddress, this.password);
		
		 inbox = store.getFolder(this.folder);
		 
		 inbox.open(Folder.READ_ONLY);
//		 messages = inbox.getMessages();
	}
	
	private boolean isConnected()
	{
		return inbox.isOpen();
	}
	
	private SearchTerm returnRequiredSearchTerm() throws Exception
	{
		SearchTerm stStart = null;
		SearchTerm stEnd = null;
		SearchTerm andTerm = null;
		
		if(this.startTime == null && this.endTime != null)
		{
			stEnd = new ReceivedDateTerm(ComparisonTerm.LE, this.endTime);
			return stEnd;
		}
		else if(this.startTime != null && this.endTime == null)
		{
			stStart = new ReceivedDateTerm(ComparisonTerm.GE, this.startTime);
			return stStart;
		}
		else if(this.startTime != null && this.endTime != null)
		{
			stStart = new ReceivedDateTerm(ComparisonTerm.GE, this.startTime);
			stEnd = new ReceivedDateTerm(ComparisonTerm.LE, this.endTime);
			andTerm = new AndTerm(stStart, stEnd);
			
			return andTerm;
		}
		
		return null;
	}
	
	 private List getAttachments(Message message) throws Exception
	 {
		 List<File> attachments = new ArrayList<File>();
		 
		 String contentType = message.getContentType();
		 
		 if (contentType.contains("multipart")) 		     // this message may contain attachment
		 {
			 Multipart multipart = (Multipart) message.getContent();
			 // System.out.println(multipart.getCount());
			 
			 for (int i = 0; i < multipart.getCount(); i++)
			 {
				 BodyPart bodyPart = multipart.getBodyPart(i);
				 if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()))
				 {
					 continue; // dealing with attachments only
				 } 
				 InputStream is = bodyPart.getInputStream();
				 File f = new File("/tmp/" + bodyPart.getFileName());
				 FileOutputStream fos = new FileOutputStream(f);
				 byte[] buf = new byte[4096];
				 int bytesRead;
				 while((bytesRead = is.read(buf))!=-1) 
				 {
					 fos.write(buf, 0, bytesRead);
				 }
				 fos.close();
				 attachments.add(f);
			 }
			 
			 return attachments;
		 }
		 return null;
	 }
	 
	 public void getContent(Message msg) throws Exception
	 {
		 String contentType = msg.getContentType();
		 
		 if(contentType != null)
			 System.out.println("Content Type : " + contentType);
		 Multipart mp = (Multipart) msg.getContent();
		 int count = mp.getCount();
		 for (int i = 0; i < count; i++)
		 {
			 dumpPart(mp.getBodyPart(i));
		 }
	 }

	 public void dumpPart(Part p) throws Exception
	 {
		 InputStream is = p.getInputStream();
		 if (!(is instanceof BufferedInputStream))
		 {
			 is = new BufferedInputStream(is);
		 }
		 int c;
		 System.out.println("Message : ");
		 while ((c = is.read()) != -1)
		 {
			 System.out.write(c);
		 }
	 }

	 public static void main(String args[])
	 {
		 Configuration conf = null;
		 
		 try 
		 {
			new EmailImportMigrationThread("a", "queryio", null, "connEmail", "INBOX", "2013-02-01", "2013-02-05", "mailPrefix", "mailSuffix", conf, true);
		 } 
		 catch (Exception e) 
		 {
			e.printStackTrace();
		 }
	 }
}
