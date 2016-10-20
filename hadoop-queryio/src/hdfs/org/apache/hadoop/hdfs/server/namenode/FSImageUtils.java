/*
 * @QUERYIO@
 */
package org.apache.hadoop.hdfs.server.namenode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.UnresolvedLinkException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.protocol.HdfsConstants.SafeModeAction;
//HDFS-7743. Minor improvement reported by Jing Zhao and fixed by Jing Zhao (namenode)
//Code cleanup of BlockInfo and rename BlockInfo to BlockInfoContiguous
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoContiguous;


public class FSImageUtils {
	private static final Log LOG =  LogFactory.getLog(FSImageUtils.class);
	public static void createFSImage(FileStatusTreeModel treeModel, String storageDir) throws IOException{
		Configuration conf = new Configuration(true);
		conf.set(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY, storageDir);
		FSImage fsImage = new FSImage(conf);
		FSNamesystem sourceNamesystem = new FSNamesystem(conf, fsImage);
		LOG.info("INodeTree before restore : ");
		LOG.info(sourceNamesystem.dir.rootDir.dumpTreeRecursively().toString());
		fsImage.getEditLog().initJournalsForWrite();
		fsImage.format(sourceNamesystem, "queryio");
		
		AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());
		createINodeStructure(sourceNamesystem.dir, sourceNamesystem.dir.rootDir, treeModel.root, atomicLong);
		
		LOG.info("INodeTree after restore : ");
		LOG.info(sourceNamesystem.dir.rootDir.dumpTreeRecursively().toString());
		FSImage.updateCountForQuota(sourceNamesystem.dir.getBlockStoragePolicySuite(),sourceNamesystem.dir.rootDir);
		sourceNamesystem.setSafeMode(SafeModeAction.SAFEMODE_ENTER);
		sourceNamesystem.saveNamespace();
		
//		org.apache.hadoop.hdfs.server.namenode.Saver
//		[6/4/14 2:01:03 PM] Prasoon Mathur: From FSUtils call org.apache.hadoop.hdfs.server.namenode.Saver.save
//		[6/4/14 2:01:35 PM] Prasoon Mathur: instead of FSNamesystem.saveNamespace
	}
	
	private static void createINodeStructure(FSDirectory dir, INodeDirectory directory, FileStatusTreeNode node, AtomicLong atomicLong) throws FileNotFoundException, UnresolvedLinkException{
		String path = node.getFilePath().toUri().getPath();
		LOG.info("path : " + path);
		LOG.info("node.isLeaf() : " + node.isLeaf());
		
		if(!node.isLeaf() || node.getFileStatus() == null) {
			byte[][] pathComponents = INodeFile.getPathComponents(path);
			byte[] name = pathComponents[pathComponents.length - 1];
			
			INodeDirectory newINodeDir = null;
			if(pathComponents[pathComponents.length - 1] == null) {
				newINodeDir = directory;
			} else {
				LOG.info("pathComponent[length - 1] : " + new String(pathComponents[pathComponents.length - 1]));
				newINodeDir = new INodeDirectory(atomicLong.incrementAndGet(), name,
						new PermissionStatus("queryio","queryio",new FsPermission(FsAction.READ_WRITE, FsAction.READ_WRITE, FsAction.READ_WRITE)), 0L);
//				newINodeDir = new INodeDirectorySnapshottable(newINodeDir);
				boolean addChild = directory.addChild(newINodeDir);
				dir.addToInodeMap(newINodeDir);
				LOG.info("addChild for directory: " + addChild);
			}
			for(FileStatusTreeNode n : node.getNodeList()){
				createINodeStructure(dir, newINodeDir, n, atomicLong);
			}
		}else{
			LOG.info("node.getFileStatus().getBlocks() : " + node.getFileStatus().getBlocks());
			LOG.info("node.getFileStatus().getBlocks().length : " + node.getFileStatus().getBlocks().length);
			BlockInfoContiguous[] blockInfo = new BlockInfoContiguous[node.getFileStatus().getBlocks() == null ? 0
					: node.getFileStatus().getBlocks().length];
			
			for (int i = 0; i < blockInfo.length; i++) {
				LOG.info("node.getFileStatus().getBlocks()[i] : " + node.getFileStatus().getBlocks()[i]);
				blockInfo[i] = new BlockInfoContiguous(node.getFileStatus().getBlocks()[i],
						node.getFileStatus().getReplication());
				LOG.info("blockInfo[i] : " + blockInfo[i]);
			}
			
			byte[][] pathComponents = INodeFile.getPathComponents(path);
			toString(pathComponents);
			LOG.info("pathComponent[length - 1] + " + new String(pathComponents[pathComponents.length - 1]));
			INodeFile iNodeFile = new INodeFile(atomicLong.incrementAndGet(), pathComponents[pathComponents.length - 1], new PermissionStatus(
					node.getFileStatus().getOwner(), node.getFileStatus().getGroup(),
					node.getFileStatus().getPermission()), node.getFileStatus().getModificationTime(), node.getFileStatus().getAccessTime(), blockInfo,
					node.getFileStatus().getReplication(), 
					 node.getFileStatus().getBlockSize());
			LOG.info("node.getFileStatus().getOwner() : " + node.getFileStatus().getOwner());
			LOG.info("node.getFileStatus().getGroup()" + node.getFileStatus().getGroup());
			LOG.info("node.getFileStatus().getPermission()" + node.getFileStatus().getPermission());
			LOG.info("node.getFileStatus().getModificationTime()" + node.getFileStatus().getModificationTime());
			LOG.info("node.getFileStatus().getAccessTime()" + node.getFileStatus().getAccessTime());
			LOG.info("node.getFileStatus().getReplication()" + node.getFileStatus().getReplication());
			LOG.info("node.getFileStatus().getBlockSize()" + node.getFileStatus().getBlockSize());
			boolean addChild = directory.addChild(iNodeFile);
			dir.addToInodeMap(iNodeFile);
			LOG.info("addChild for file: " + addChild);
		}
	}

	private static void toString(byte[][] pathComponents) {
		LOG.info("path.toString : ");
		for(byte [] path : pathComponents) {
			LOG.info(new String(path) + ",");
		}
	}

}
