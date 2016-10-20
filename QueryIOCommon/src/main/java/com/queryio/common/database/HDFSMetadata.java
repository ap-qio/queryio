package com.queryio.common.database;

@SuppressWarnings("PMD.AvoidUsingShortType")
public class HDFSMetadata {
	String filePath;
	long accessTime;
	long modificationTime;
	String owner;
	String usergroup;
	String permission;
	long blockSize;
	short replication;
	byte[] blocks;
	long length;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(long accessTime) {
		this.accessTime = accessTime;
	}
	public long getModificationTime() {
		return modificationTime;
	}
	public void setModificationTime(long modificationTime) {
		this.modificationTime = modificationTime;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getUsergroup() {
		return usergroup;
	}
	public void setUsergroup(String usergroup) {
		this.usergroup = usergroup;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}
	public short getReplication() {
		return replication;
	}
	public void setReplication(short replication) {
		this.replication = replication;
	}
	public byte[] getBlocks() {
		return blocks;
	}
	public void setBlocks(byte[] blocks) {
		this.blocks = blocks;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
}
