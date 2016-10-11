package com.os3.server.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.log4j.Logger;

import com.os3.server.common.StreamUtilities;
import com.os3.server.common.StreamWriteStatus;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.stream.util.QIODFSInputStream;
import com.queryio.stream.util.QIODFSOutputStream;

public class DFSManager 
{
	private static Logger logger = Logger.getLogger(DFSManager.class);
	public static final String ROOT_PATH = "/";
	
	public static boolean isValidBucketName(String bucketName)
	{
		//FIXME : Validate bucket name as per details on https://developers.google.com/storage/docs/bucketnaming
		return true;
	}
	
	public static boolean doesObjectExist(FileSystem dfs, String bucketName, String objectName) throws IOException
	{
		return dfs.exists(new Path(ROOT_PATH + bucketName, objectName));
	}
	
	public static boolean doesPathExist(FileSystem dfs, Path path) throws IOException
	{
		return dfs.exists(path);
	}
	
	public static FileStatus getPathStatus(FileSystem dfs, Path path) throws IOException {
		return dfs.getFileStatus(path);
	}
	
	public static void setOwner(FileSystem dfs, Path path, String owner, String group) throws IOException {
		dfs.setOwner(path, owner, group);
	}
	
	public static void setPermissions(FileSystem dfs, Path path, short permission) throws IOException {
		dfs.setPermission(path, DFSManager.parsePermissions(permission)); 
	}
	
	public static boolean isFile(FileSystem dfs, Path path) throws IOException
	{
		return dfs.isFile(path);
	}
	
	public static FileStatus getObjectStatus(FileSystem dfs, String bucketName, String objectName) throws IOException
	{
		Path objectPath = new Path(ROOT_PATH + bucketName, objectName);
		return dfs.getFileStatus(objectPath);
	}
	
	public static InputStream getObjectDataInputStream(FileSystem dfs, String bucketName, String objectName, String compressionType, String encryptionType) throws Exception
	{
		Path objectPath = new Path(ROOT_PATH + bucketName, objectName);
		
		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		QIODFSInputStream qioInputStream = null;
		DFSInputStream dfsInputStream = (DFSInputStream) fs.getClient().open(objectPath.toUri().toString(), EnvironmentalConstants.getStreamBufferSize(), false);
		try {
			if(compressionType==null && encryptionType==null){
				qioInputStream = new QIODFSInputStream(dfsInputStream);
			} else {
				qioInputStream = new QIODFSInputStream(dfsInputStream, compressionType, encryptionType);
			}
		} catch (Exception e) {
			if (dfsInputStream != null) {
				dfsInputStream.close();
			}
			throw e;
		}
		
		return qioInputStream;
	}
	
	public static boolean doesBucketExists(FileSystem dfs, String bucketName) throws IOException
	{
		return dfs.exists(new Path(ROOT_PATH, bucketName));
	}
	
	public static FileStatus[] getObjectList(FileSystem dfs, String bucketName, BucketFilter filter) throws IOException
	{
		return dfs.listStatus(new Path(ROOT_PATH, bucketName), filter);
	}
	
	public static boolean createBucket(FileSystem dfs, String bucketName, String username, String group, short permission) throws IOException
	{
		if(dfs.mkdirs(new Path(ROOT_PATH, bucketName), DFSManager.parsePermissions(permission))){
			dfs.setOwner(new Path(ROOT_PATH, bucketName), username, group);
			return true;
		}
		return false;
	}
	
	public static boolean createBucket(FileSystem dfs, String bucketName, String username, String group) throws IOException
	{
		if(dfs.mkdirs(new Path(ROOT_PATH, bucketName))){
			dfs.setOwner(new Path(ROOT_PATH, bucketName), username, group);
			return true;
		}
		return false;
	}
	
	public static StreamWriteStatus createObject(String username, String group, short permission, FileSystem dfs, String bucketName, String objectName, long contentLength, InputStream is, List<UserDefinedTag> tags, String compressionType, String encryptionType) throws IOException, NoSuchAlgorithmException {
		StreamWriteStatus status = createObject(username, group, dfs, bucketName, objectName, contentLength, is, tags, compressionType, encryptionType);
		dfs.setPermission(new Path(ROOT_PATH + bucketName, objectName), DFSManager.parsePermissions(permission));
		return status;
	}
	
	public static StreamWriteStatus createObject(String username, String group, FileSystem dfs, String bucketName, String objectName, long contentLength, InputStream is, List<UserDefinedTag> tags, String compressionType, String encryptionType) throws IOException, NoSuchAlgorithmException
	{
		Path objectPath = new Path(ROOT_PATH + bucketName, objectName);
		logger.debug("Creating object: " + objectPath);
		OutputStream cipherOutputStream = null;
		StreamWriteStatus status = null;
		DFSOutputStream dfsOutputStream = null;
		try{
			DistributedFileSystem fs = (DistributedFileSystem) dfs;
			dfsOutputStream = (DFSOutputStream) fs.getClient().create(objectPath.toUri().getPath(), true);
			dfs.setOwnerModified(objectPath, username, group);
			
			if (tags != null && tags.size() > 0){
				dfsOutputStream.addTags(tags);
			}	
			
			try {
				cipherOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, compressionType, encryptionType, null);
			} catch (Exception e) {
				if (dfsOutputStream != null) {
					dfsOutputStream.close();
				}
				throw new Error(e);
			}
			status = StreamUtilities.writeToStream(is, cipherOutputStream,
					contentLength);
			
			logger.debug("Tags for " + objectPath + " are: " + tags);
			
			cipherOutputStream.flush();
		} finally {
			try{
				if(cipherOutputStream!=null)	cipherOutputStream.close();
			} catch(Exception e){
				logger.fatal(e.getMessage(), e);
			}
		}
		
		return status;
	}
	
	private static List<FileStatus> getAllFilePaths(FileSystem dfs, String str, BucketFilter filter) throws Exception{
		Path path = new Path(str);
		FileStatus stat = dfs.getFileStatus(path);
		List list = new ArrayList();
		list.add(stat);
		
		if(stat.isDirectory()){
			FileStatus[] stats = dfs.listStatus(path);
			for(int i = 0; i < stats.length; i ++){
				if(!str.endsWith("/"))
					str += "/";
				list.addAll(getAllFilePaths(dfs, str + stats[i].getPath().getName(), filter));
			}
		}
		return list;	    
	}
	
//	public static void main(String[] args) throws Exception {
//		Configuration conf = new Configuration();
//		conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, "hdfs://192.168.0.12:9000");
//		Thread.currentThread().setName("admin");
//		
//		Thread.dumpStack();
//		
//		FileSystem fs = FileSystem.get(conf);
//		
//		FsPermission permission = parsePermissions(Short.parseShort("777"));
//		
//		fs.setPermission(new Path("deletePutGetObject_29_01_2013_17_29_1359460782285"), permission);
//		
////		List<FileStatus> list = getAllFilePaths(fs, "/data2", null);
////		
////		for(int i=0; i<list.size(); i++) {
////			System.out.println(list.get(i).getPath().toUri().toString());
////		}
//	}
//	
	public static FsPermission parsePermissions(short permissions)
	{
		FsAction u = getAction(permissions / 100);
		int t = permissions % 100;
		FsAction g = getAction(t / 10);
		FsAction o = getAction(t % 10);

		return new FsPermission(u, g, o, false);
	}
	
	public static FsAction getAction(int i)
	{
		switch (i)
		{
			case 0: return FsAction.NONE;
			case 1: return FsAction.EXECUTE;
			case 2: return FsAction.WRITE;
			case 3: return FsAction.WRITE_EXECUTE;
			case 4: return FsAction.READ;
			case 5: return FsAction.READ_EXECUTE;
			case 6: return FsAction.READ_WRITE;
			case 7: return FsAction.ALL;
			default: return FsAction.READ;
		}
	}
	
	public static boolean deleteBucket(FileSystem dfs, String bucketName) throws IOException
	{
		Path bucketPath = new Path(ROOT_PATH, bucketName);
		return dfs.delete(bucketPath, true);
	}
	
	public static boolean deleteObject(FileSystem dfs, String bucketName, String objectName) throws IOException
	{
		Path objectPath = new Path(ROOT_PATH + bucketName, objectName);
		return dfs.delete(objectPath, true);
	}
	
	public static boolean isBucketEmpty(FileSystem dfs, String bucketName) throws IOException 
	{
		Path bucketPath = new Path(ROOT_PATH, bucketName);
		FileStatus[] fs = dfs.listStatus(bucketPath);
		return (fs == null || fs.length == 0);
	}

	public static String getFileCheckSum(FileSystem dfs, Path path) throws IOException 
	{
		FileChecksum checksum = dfs.getFileChecksum(path);
		if(checksum != null){
			//FIXME checksum algo is MD5-of-0MD5-of-512CRC32, we need just MD5
//			System.out.println("Checksum algo " + checksum.getAlgorithmName());
			return checksum.toString();
		} 
		return "";
	}
	
	public static ArrayList getAllDirStats(FileSystem dfs) throws IOException, ConnectException
	{
		try 
		{
			return DFSManager.getAllDirStats(dfs, "/");
		}
		catch (Exception e1) 
		{
			logger.error(e1.getMessage(), e1);
		}
		return null;
	}
	
	public static ArrayList getAllDirStats(FileSystem dfs, String str) throws IOException, ConnectException
	{
		Path path = new Path(str);
		
		/*FileStatus stat = */dfs.getFileStatus(path);
		ArrayList list = new ArrayList();
		FileStatus[] stats = dfs.listStatus(path);
		for(int i = 0; i < stats.length; i++)
		{
			list.add(stats[i]);
		}
		return list;	    
	}
	
	public static List listAllPath(FileSystem dfs, Path p) throws Exception{
		List list = new ArrayList();
		FileStatus[] fileStatus = dfs.listStatus(p);
		for(int i = 0; i < fileStatus.length; i++){
			if(fileStatus[i].isDirectory()){
				list.addAll(listAllPath(dfs, fileStatus[i].getPath()));
			}else{
				list.add(fileStatus[i]);
			}
		}
		return list;
	}
}
