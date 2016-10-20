package com.queryio.migration.utils;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.util.Count;


public class DFSManager {

	private FileSystem dfs;
	
	public DFSManager(String fsDefaultName) throws IOException {
		Configuration config = new Configuration();
		config.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, fsDefaultName);
		dfs = FileSystem.get(config);
	}
	
	
	public  boolean createBucket(String bucketName) throws IOException{
		return dfs.mkdirs(new Path("/", bucketName));
	}
	
	public  FileStatus createObject(String bucketName, String objectName, InputStream is, boolean overwrite) throws IOException{
		Path objectPath = new Path(new Path("/", bucketName), objectName);
		FSDataOutputStream outputStream = null;
		try{
			outputStream = dfs.create(objectPath, overwrite);
			writeToStream(is, outputStream);
			return dfs.getFileStatus(objectPath);
		}finally{
			if(outputStream != null){
				outputStream.close();
			}
		}
	}
	
	public  long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}

	public  boolean deleteBucket(String bucketName) throws IOException{
		Path bucketPath = new Path(dfs.getWorkingDirectory(), bucketName);
		return dfs.delete(bucketPath, true);
	}
	
	public  boolean deleteObject(String bucketName, String objectName) throws IOException{
		Path objectPath = new Path(new Path(dfs.getWorkingDirectory(), bucketName), objectName);
		return dfs.delete(objectPath, false);
	}
	
	public  boolean isBucketEmpty(String bucketName) throws IOException {
		
		Path bucketPath = new Path(dfs.getWorkingDirectory(), bucketName);
		FileStatus[] fs = dfs.listStatus(bucketPath);
		return (fs == null || fs.length == 0);
	}

	public  String getFileCheckSum(Path path) throws IOException {
		
		FileChecksum checksum = dfs.getFileChecksum(path);
		if(checksum != null){
			//FIXME checksum algo is MD5-of-0MD5-of-512CRC32, we need just MD5
//				System.out.println("Checksum algo " + checksum.getAlgorithmName());
			return checksum.toString();
		} 
		return "";
	}
	
	public void setFileOwner(Path path, String owner, String group) throws IOException {
		dfs.setOwner(path, owner, group);
	}
	
	public void setFilePermissions(Path path, String permissions) throws IOException {
		dfs.setPermission(path, new FsPermission(permissions));
	}
	
	public static List getAllFileCount(FileSystem dfs, String str, Count count) throws Exception
	{
		Path path = new Path(str);
		FileStatus stat = dfs.getFileStatus(path);
		List list = new ArrayList();
		if(!stat.isDirectory())
		{
			count.increment();
		}
		else
		{
			FileStatus[] stats = dfs.listStatus(path);
			for(int i = 0; i < stats.length; i++)
			{
				if(!str.endsWith("/"))
					str += "/";
				getAllFileCount(dfs, str + stats[i].getPath().getName(), count);
			}
		}
		return list;	    
	}
	}


