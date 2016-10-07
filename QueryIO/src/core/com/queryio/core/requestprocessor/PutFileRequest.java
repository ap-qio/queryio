package com.queryio.core.requestprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.User;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.file.upload.FileUploadListener;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.stream.util.QIODFSOutputStream;

public class PutFileRequest extends RequestProcessorCore{
	MigrationInfo migrationInfo = new MigrationInfo();
	private String nameNodeId;
	private String fsDefaultName;
	private long totalBytes;
	private InputStream is;
	private FileSystem dfs;
	String errorMessage = null;
	private Configuration conf;
	boolean deflate = false;
	private String compressionType;
	private String encryptionType;
	private FileUploadListener fileUploadListener;
	List<UserDefinedTag> extraTags = new ArrayList<UserDefinedTag>();
	private JSONObject tagsJSON;
	
	public PutFileRequest(String user, String group, Path path, String nameNodeId, String fsDefaultName, InputStream is, long contentLength, Configuration conf, boolean deflate, String compressionType, String encryptionType,FileUploadListener fileUploadListener, JSONObject tagsJSON) {
		super(user, group, path);
		this.fsDefaultName = fsDefaultName;
		this.nameNodeId = nameNodeId;
		this.is = is;
		this.totalBytes = contentLength;
		this.conf = conf;
		this.deflate = deflate;
		this.compressionType = compressionType;
		this.encryptionType = encryptionType;
		this.fileUploadListener = fileUploadListener;
		this.tagsJSON = tagsJSON;
	}
	
	public PutFileRequest(String user, String group, Path path, String nameNodeId, String fsDefaultName, InputStream is, long contentLength, Configuration conf, boolean deflate, String compressionType, String encryptionType,FileUploadListener fileUploadListener, List<UserDefinedTag> extraTags, JSONObject tagsJSON) {
		this(user, group, path, nameNodeId, fsDefaultName, is, contentLength, conf, deflate, compressionType, encryptionType, fileUploadListener, tagsJSON);
		this.extraTags = extraTags;
	}
	
	public void process() throws Exception{
		
		this.successful = false;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("PutFileRequest, user: " + this.user);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("FsDefaultName: " + this.fsDefaultName);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Path: " + this.path);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("tagsJSON: " + this.tagsJSON);
		
		Connection connection = null;
		
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			if(EnvironmentalConstants.isUseKerberos()){
				User us = UserDAO.getUserDetail(connection, user);
				
				try{
					UserGroupInformation.setConfiguration(conf);
					UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));					
					
					dfs = FileSystem.get(conf);
					dfs.setConf(conf);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(), e);
					throw e;
				}
			} else {
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf);
			}
			
			DFSOutputStream dfsOutputStream = null;
			OutputStream qioOutputStream = null;
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Compression Type: " + this.compressionType);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Encryption Type: " + this.encryptionType);
			
			try{
				DistributedFileSystem fs = (DistributedFileSystem) dfs;
				dfsOutputStream = (DFSOutputStream) fs.getClient().create(this.path.toUri().getPath(), true);
				dfsOutputStream.addTags(extraTags);
				try {
					qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, this.compressionType, this.encryptionType, tagsJSON);
				} catch (Exception e) {
					if (dfsOutputStream != null) {
						dfsOutputStream.close();
					}
					throw e;
				}
				dfs.setOwnerModified(this.path, this.user, this.group);
				writeToStream(connection, is, qioOutputStream);			
			}finally{
				try{
					if(qioOutputStream != null){
						qioOutputStream.close();
					}
				} catch(Exception e){
					AppLogger.getLogger().fatal("Tag parser exception for file : " + this.path + ", reason: " + e.getMessage(), e);
					//throw new TagParserException(e);
				}
			}
			
			this.successful = true;
		} 
		catch (TagParserException tpe) 
		{
			this.successful = true;
			this.errorMessage = "Tag processing for " + this.path + " failed but file uploaded succesfully. Reason: " + tpe.getMessage();
			throw tpe;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal("PutFileRequest failed with exception: " + e.getMessage(), e);
			
			boolean isDeleted = dfs.delete(this.path, true);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Deleting path: " + this.path + "	isDeleted: " + isDeleted);
			
			if (!deflate){
				updateStatusFailed(connection);
			}
			
			this.errorMessage = e.getMessage();
			
			throw e;
		}
		finally
		{
			try {
				if(dfs!= null)
					dfs.close();
			} catch (IOException e1) {
				AppLogger.getLogger().fatal("Error closing FileSystem.", e1);
			}
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void writeToStream(final Connection connection, InputStream inputStream, OutputStream outputStream) throws Exception {
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("is deflate (to be unzipped): " + deflate);
		
		if (!deflate)
			addTask(connection);
		
		int bufferSize = EnvironmentalConstants.getStreamBufferSize();
		if(bufferSize==0){
			bufferSize = 1024000;
		}

		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Writing to file: " + this.path.toString() + ", size: " + getFormattedStorageSize(this.totalBytes));
		
		final byte[] readBuffer = new byte[bufferSize];
		int bytesIn = 0;
		while ((bytesIn = inputStream.read(readBuffer, 0, readBuffer.length)) != -1) {
			outputStream.write(readBuffer, 0, bytesIn);
			
			if (!deflate)
				updateStatus(connection);
		}
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("File upload: " + this.path.toString() + " complete");
		
		if (!deflate)		
			updateStatusCompleted(connection);
	}
	
	public void updateStatusCompleted(final Connection connection){
		try{
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setProgress(100);
			MigrationInfoDAO.update(connection, migrationInfo);
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	public void updateStatusFailed(final Connection connection){
		try{
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
//			migrationInfo.setProgress(0);
			MigrationInfoDAO.update(connection, migrationInfo);
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	public void updateStatus(final Connection connection){
		try{
			if(this.fileUploadListener.getContentLength()!=0){
				long prog = this.fileUploadListener.getBytesRead() * 100 / this.fileUploadListener.getContentLength();
				if(prog > 100)
					return;
				migrationInfo.setProgress(prog);
			}
			MigrationInfoDAO.update(connection, migrationInfo);
		}catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
	
	public void addTask(final Connection connection) throws Exception{
		this.migrationInfo = new MigrationInfo();
		
		migrationInfo.setSourcePath("N/A"); 
		migrationInfo.setDestinationPath(this.path.toString());					
		migrationInfo.setNamenodeId(this.nameNodeId);
		migrationInfo.setImportType(true);
		migrationInfo.setTitle("Upload file " + this.path.toString());
		migrationInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
		migrationInfo.setDataStore("Local");			
		migrationInfo.setProgress(0);
		migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_INPROGRESS);					
		MigrationInfoDAO.insert(connection, migrationInfo);					
		this.migrationInfo = MigrationInfoDAO.get(connection, migrationInfo.getStartTime());	
	}
	
	public static String getFormattedStorageSize(long bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
	public static String getFileExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
