package com.queryio.common.remote;

import java.util.HashMap;
import java.util.Map;

public class FsImageDaemon {
	private static Map<String, Thread> threadList_ = new HashMap<String, Thread>();
	public static String runFsImageFromDbThread(String installDir, String namenodeId){
		String id = "Rebuild_" + namenodeId + "_" + String.valueOf(System.currentTimeMillis());
		Thread t = new FsImageFromDbThread(id, namenodeId, installDir, null, null);
		t.start();
		threadList_.put(id, t);
		return id;
	}
	
	public static String runFsImageFromDbThread(String installDir, String namenodeId, String backupDbSource, String backupId){
		String id = "Restore_" + namenodeId + "_" + String.valueOf(System.currentTimeMillis());
		Thread t = new FsImageFromDbThread(id, namenodeId, installDir, backupDbSource, backupId);
		t.start();
		threadList_.put(id, t);
		return id;
	}
	public static String runFsImageFromFileThread(String installDir, String namenodeId, String backupFileSourcePath, String backupId){
		String id = "Restore_" + namenodeId + "_" + String.valueOf(System.currentTimeMillis());
		Thread t = new FsImageFromFileThread(id, namenodeId, installDir, backupFileSourcePath, backupId);
		t.start();
		threadList_.put(id, t);
		return id;
	}
	
	public static void interruptDaemon(String id){
		Thread t = threadList_.get(id);
		if(t != null){
			t.interrupt();
			threadList_.remove(id);	
		}
	}
}
