package com.queryio.core.conf;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.queryio.common.ClassPathUtility;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.TagParserConfig;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.TagParserDAO;
import com.queryio.scheduler.service.SchedulerDAO;

public class TagParserConfigManager {
	
	private static final String DEFAULT_JAR = "Plugins/PostIngestJob.jar";
	
	public static void init(){
		ClassPathUtility.recycleClassLoderForUIServer();
	}
	
	private static void deleteNodeConfiguration(Connection connection,
			TagParserConfig config) throws Exception{
		ArrayList keys = new ArrayList();
		ArrayList fileTypeList = new ArrayList();
		
		String[] arr = config.getFileTypes().split(",");
		for(String str : arr){
			str = str.trim();
			if(!fileTypeList.contains(str)){
				fileTypeList.add(str);
				keys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str);			
			}
		}
		keys.add(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES);
		Node namenode = NodeDAO.getNode(connection, config.getNamenodeId());
		Host host = HostDAO.getHostDetail(connection, namenode.getHostId());
		QueryIOAgentManager.unsetConfiguration(host, namenode, keys, "core-site.xml");			
	}
	public static void populateCustomTagParserConfig(Connection connection, String namenodeId, List keys, List values) throws Exception{
		ArrayList fileTypeList = new ArrayList();
		List tagParsers = TagParserDAO.getAllOnIngestForNamenode(connection, namenodeId);
		for(int i = 0; i < tagParsers.size(); i ++){
			TagParserConfig parser = (TagParserConfig) tagParsers.get(i);
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("InGest Parser is : " + parser.isIsActive());
			if (!parser.isIsActive())		// inactive then continue.
				continue;
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("InGest Parser Active : " + parser.isIsActive());
			
			String[] arr = parser.getFileTypes().split(",");
			for(String str : arr){
				str = str.trim();
				if(!fileTypeList.contains(str)){
					fileTypeList.add(str);
					keys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str);
					values.add(parser.getClassName());					
				}
			}
		}
		String fileTypes = "";
		for(int i = 0; i < fileTypeList.size(); i ++){
			if(i != 0){
				fileTypes += ",";				
			}
			fileTypes += (String)fileTypeList.get(i);
		}
		keys.add(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES);
		values.add(fileTypes);
		
	}
	public static void updateNodeConfiguration(Connection connection, String namenodeId) throws Exception{
		ArrayList keys = new ArrayList();
		ArrayList values = new ArrayList();
		populateCustomTagParserConfig(connection, namenodeId, keys, values);
		Node namenode = NodeDAO.getNode(connection, namenodeId);
		Host host = HostDAO.getHostDetail(connection, namenode.getHostId());
		QueryIOAgentManager.setAllNodeConfig(host, namenode, keys, values);		
	}
	
	public static void handleOnIngestParser(Connection connection, String name, String description, String jarName, 
			String tagParserfileTypes, String tagParserClassName, String namenodeId, boolean isActive) throws Exception{
		tagParserfileTypes = tagParserfileTypes.toLowerCase();
		TagParserConfig oldConfig = TagParserDAO.getByName(connection, name, true);
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
		if(oldConfig != null){
			TagParserDAO.delete(connection, oldConfig.getId());	
			
			for(int i = 0; i < namenodes.size(); i ++){
				Node node = (Node) namenodes.get(i);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				QueryIOAgentManager.deleteFolder(host,  QueryIOConstants.TAGPARSER_JAR_DIR + "/" + oldConfig.getTagName());
			}
		}
		TagParserDAO.insert(connection, name, description, jarName, tagParserfileTypes, tagParserClassName, namenodeId, true, isActive);		
		updateNodeConfiguration(connection, namenodeId);
		
		Node node = NodeDAO.getNode(connection, namenodeId);
		Host host = HostDAO.getHostDetail(connection, node.getHostId());
		QueryIOAgentManager.transferFolder(host, QueryIOConstants.TAGPARSER_JAR_DIR, name);		
		RemoteManager.reInitializeHadoopConfigOnServers();
		init();
	}

	public static void updateOnIngestParserExceptJarInfo(Connection connection, String name, String description, 
			String tagParserfileTypes, String tagParserClassName, String namenodeId, boolean isActive) throws Exception{
		tagParserfileTypes = tagParserfileTypes.toLowerCase();
		TagParserDAO.updateExceptJarInfo(connection, name, description, tagParserfileTypes, tagParserClassName, namenodeId, true, isActive);		

		updateNodeConfiguration(connection, namenodeId);
		
		RemoteManager.reInitializeHadoopConfigOnServers();
	}

	public static void handlePostIngestParser(Connection connection, String namenodeId, String rmId, String name, String description, String hdfsURI, String jarName, 
			String tagParserfileTypes, String tagParserClassName) throws Exception{
		tagParserfileTypes = tagParserfileTypes.toLowerCase();
		TagParserDAO.deleteByName(connection, name, false);
		TagParserDAO.insert(connection, name, description, jarName, tagParserfileTypes, tagParserClassName, namenodeId, false, true);
		
		MapRedJobConfigDAO.delete(connection, name, true);
		StringBuffer arguments = new StringBuffer();
		arguments.append("\"" + name + "\"");
		arguments.append(" ");
		arguments.append(tagParserfileTypes);
		arguments.append(" ");
		arguments.append(tagParserClassName);
		arguments.append(" ");
		arguments.append("/");
		// By default Recursive was true, and input path filter was false.
		MapRedJobConfig config = new MapRedJobConfig(namenodeId, rmId, name, DEFAULT_JAR, jarName, "", QueryIOConstants.DEFAULT_POSTINGEST_MAIN_CLASS, arguments.toString(), true, false, null);
		MapRedJobConfigDAO.insert(connection, config);
	}
	
	public static void handleDeleteOnIngestParser(Connection connection, TagParserConfig config) throws Exception{
		
		String defaultName = QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME;
//		String defaultNameWiki = QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI + "_" + config.getNamenodeId();
		
		if (!config.getTagName().startsWith(defaultName))
		{
			FileUtils.deleteDirectory(new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/"+ QueryIOConstants.TAGPARSER_JAR_DIR +"/" + config.getTagName()));
			Node node = NodeDAO.getNode(connection, config.getNamenodeId());
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			QueryIOAgentManager.deleteFolder(host,  QueryIOConstants.TAGPARSER_JAR_DIR + "/" + config.getTagName());
		}
		
		deleteNodeConfiguration(connection, config);
		TagParserDAO.delete(connection, config.getId());
		updateNodeConfiguration(connection, config.getNamenodeId());
		
		RemoteManager.reInitializeHadoopConfigOnServers();
		init();
		
	}
	

	public static void handleDeletePostIngestParser(Connection connection, TagParserConfig config) throws Exception{
		FileUtils.deleteDirectory(new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + config.getTagName()));
		MapRedJobConfigDAO.delete(connection, config.getTagName(), true);
		SchedulerDAO.deleteJob(config.getTagName(), "MAPRED");
		TagParserDAO.delete(connection, config.getId());
	}
}