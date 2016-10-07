/**
 * @QUERYIO@
 */
package org.apache.hadoop.hdfs.server.namenode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.protocol.Block;


public class FileStatusTreeModel {
	FileStatusTreeNode root;
	public FileStatusTreeModel() {
		root = new FileStatusTreeNode(new Path("/"), null);
	}
	public void insert(Path filePath, FileStatus fileStatus){
		root.addChild(new FileStatusTreeNode(filePath, fileStatus));
	}
	public void printTree(){
		root.print();
	}
	
	public static void main(String[] args) throws IOException {
		FileStatusTreeModel treeModel = new FileStatusTreeModel();
		String permStr = "rw-r--r--";
		FsPermission permission = new FsPermission(getFsAction(permStr.substring(0, 3)), getFsAction(permStr.substring(3, 6)), getFsAction(permStr.substring(6, 9)));
		Block[] blocks = null;
		Path path = new Path("/csv2.csv");
		FileStatus fs = new FileStatus(12, false, 1, 1245, 123456789, 123456789, permission, "admin", "queryio", path, blocks);
		treeModel.insert(path, fs);
		path = new Path("/csv/csv1.csv");
		fs = new FileStatus(12, false, 1, 1245, 123456789, 123456789, permission, "admin", "queryio", path, blocks);
		treeModel.insert(path, fs);
//		path = new Path("/Data/pdf/csv2.csv");
//		fs = new FileStatus(12, false, 1, 1245, 123456789, 123456789, permission, "admin", "queryio", path, blocks);
//		treeModel.insert(path, fs);
//		path = new Path("/Data/csv/csv2.csv"); 
//		fs = new FileStatus(12, false, 1, 1245, 123456789, 123456789, permission, "admin", "queryio", path, blocks);
//		treeModel.insert(path, fs);
		treeModel.printTree();
	}
	
	private static FsAction getFsAction(String permssion) {
		if(permssion.equals("---")) {
			return FsAction.NONE;
		} else if(permssion.equals("r--")) {
			return FsAction.READ;
		} else if(permssion.equals("-w-")) {
			return FsAction.WRITE;
		} else if(permssion.equals("--x")) {
			return FsAction.EXECUTE;
		} else if(permssion.equals("rw-")) {
			return FsAction.READ_WRITE;
		} else if(permssion.equals("r-x")) {
			return FsAction.READ_EXECUTE;
		} else if(permssion.equals("-wx")) {
			return FsAction.WRITE_EXECUTE;
		} else if(permssion.equals("rwx")) {
			return FsAction.ALL;
		}
		return null;
	}
}

class FileStatusTreeNode{
	private Path filePath;
	private FileStatus fileStatus;
	private List<FileStatusTreeNode> nodeList = null;

	public Path getFilePath() {
		return filePath;
	}
	public FileStatus getFileStatus() {
		return fileStatus;
	}
	public List<FileStatusTreeNode> getNodeList() {
		return nodeList;
	}

	public FileStatusTreeNode(Path filePath, FileStatus fileStatus) {
		this.filePath = filePath;
		this.fileStatus = fileStatus;
		this.nodeList = new ArrayList<FileStatusTreeNode>();
	}
	public void addChild(FileStatusTreeNode node) {
		createNode(node);
	}
	private FileStatusTreeNode createNode(FileStatusTreeNode node) {
		Path parentPath = node.filePath.getParent();
		if (parentPath == null) {
			node = this;
		} else {
			FileStatusTreeNode parent = createNode(new FileStatusTreeNode(parentPath, null));
			FileStatusTreeNode searchNode = parent.search(node.filePath);
			if (searchNode == null) {
				parent.nodeList.add(node);
			} else {
				node = searchNode;
			}
		}
		return node;
	}
	
	private FileStatusTreeNode search(Path path){
		for(FileStatusTreeNode n : nodeList){
			if(n.filePath.equals(path)){
				return n;
			}else{
				FileStatusTreeNode node = n.search(path);
				if (node != null) {
					return node;
				}
			}
		}
		return null;
	}
	
	public boolean isLeaf(){
		return nodeList.size() == 0;
	}
	public void print(){
		System.out.println(this.filePath.toUri().getPath());
		for(FileStatusTreeNode node : this.nodeList){
			node.print();
		}
	}
}
