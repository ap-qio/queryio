package com.queryio.nodetransition;

// package com.queryio.nodetransition;
//
// import java.sql.Connection;
// import java.util.List;
//
// import com.queryio.common.HiPerConstants;
// import com.queryio.common.util.AppLogger;
// import com.queryio.core.agent.QueryIOAgentManager;
// import com.queryio.core.bean.DWRResponse;
// import com.queryio.core.bean.Host;
// import com.queryio.core.bean.Node;
// import com.queryio.core.dao.HAStatusDAO;
// import com.queryio.core.dao.HostDAO;
// import com.queryio.core.dao.NodeDAO;
//
//
// public class HATransitionManager {
// public static void performFailover(Connection connection,DWRResponse
// dwrResponse) throws Exception{
//
// Node nn = NodeDAO.getNamenode(connection);
// Node snn = NodeDAO.getStandbyNamenode(connection);
//
// if(snn == null){
// dwrResponse.setDwrResponse(false, "No standby namenode is configured.", 500);
// return;
// }
//// Host nnHost = HostDAO.getHostDetail(connection, nn.getHostId());
// Host snnHost = HostDAO.getHostDetail(connection, snn.getHostId());
//
// String failoverArgument = "";
//
// failoverArgument = nn.getId() + " " + snn.getId();
// QueryIOAgentManager.performFailOver(snnHost, snn,
// failoverArgument,dwrResponse);
//
// if(dwrResponse.isTaskSuccess()){
// HAStatusDAO.setActiveNamenodeId(connection, snn.getId());
// HAStatusDAO.setStandbyNamenodeId(connection, nn.getId());
// dwrResponse.setDwrResponse(true, dwrResponse.getResponseMessage(), 200);
// }else{
// dwrResponse.setDwrResponse(false, dwrResponse.getResponseMessage(), 200);
// }
//
// }
// public static void disableHA(Connection connection,DWRResponse dwrResponse)
// throws Exception{
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Disabling HA mode .. ");
// Node namenode = NodeDAO.getNamenode(connection);
// if(namenode != null){
// Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
//
// if(!namenode.getStatus().equals(HiPerConstants.STATUS_STOPPED)){
//
// dwrResponse.setDwrResponse(false,"It is required that the namenode for this
// cluster must be stopped." +
// "The namenode on host "+namenodeHost.getHostIP()+" is still running..." ,
// 500);
// return ;
// }
// List datanodeList = NodeDAO.getAllDatanodes(connection);
// String failureMsg = "It is required that all the datanodes for this cluster
// must be stopped. The datanode(s): ";
// boolean flag = false;
// for(int i = 0; i < datanodeList.size(); i ++){
// Node datanode = (Node) datanodeList.get(i);
// if(!datanode.getStatus().equals(HiPerConstants.STATUS_STOPPED)){
// if(!flag){
// flag = true;
// }else{
// failureMsg += ", ";
// }
// failureMsg += datanode.getId();
// }
// }
// if(flag){
// dwrResponse.setResponseMessage(failureMsg+ " is/are still running.");
// dwrResponse.setResponseCode(500);
// return;
// }
//
// QueryIOAgentManager.copySharedDirLogstoEditsLogs(namenodeHost, namenode,
// dwrResponse);
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.getResponseMessage());
// if(!dwrResponse.isTaskSuccess()){
// return;
// }
//
// String[] response = QueryIOAgentManager.getConfig(namenodeHost, new
// String[]{"dfs.namenode.shared.edits.dir"}, namenode, "hdfs-site.xml");
//
// if(response != null && response.length != 0){
// String[] keys = new String[]{"dfs.ha.namenodes." +
// HiPerConstants.DEFAULT_CLUSTER_NAME, "dfs.namenode.shared.edits.dir",
// "dummy.dfs.namenode.shared.edits.dir"};
// String[] values = new String[]{namenode.getId(), "", response[0]};
//
// QueryIOAgentManager.updateConfiguration(namenodeHost, namenode, keys, values,
// dwrResponse);
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.isTaskSuccess() + ": " +
// dwrResponse.getResponseMessage());
// if(!dwrResponse.isTaskSuccess()){
// return;
// }
// }
// for(int i = 0; i < datanodeList.size(); i ++){
// Node datanode = (Node)datanodeList.get(i);
// Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
// String[] keys = new String[]{"dfs.ha.namenodes." +
// HiPerConstants.DEFAULT_CLUSTER_NAME};
// String[] values = new String[]{namenode.getId()};
//
// QueryIOAgentManager.updateConfiguration(datanodeHost, datanode, keys,
// values,dwrResponse);
//
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.isTaskSuccess()+" "+
// dwrResponse.getResponseMessage());
// if(!dwrResponse.isTaskSuccess()){
// return;
// }
// }
// }
// updateHAEnabled(connection, false);
// if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("HA
// Disabled");
// dwrResponse.setDwrResponse(true,"Cluster is now configured as a non-HA
// Cluster." , 200);
//
// }
//
// public static void enableHA(Connection connection,DWRResponse dwrResponse)
// throws Exception{
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Enabling HA mode .. ");
// Node namenode = NodeDAO.getNamenode(connection);
// Node standbyNode = NodeDAO.getStandbyNamenode(connection);
// if(namenode != null){
// Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
//
// if(!namenode.getStatus().equals(HiPerConstants.STATUS_STOPPED)){
// dwrResponse.setDwrResponse(false, "It is required that the namenode for this
// cluster must be stopped." +
// "The namenode on host "+namenodeHost.getHostIP()+" is still running...",
// 500);
// return;
// }
// List datanodeList = NodeDAO.getAllDatanodes(connection);
// String failureMsg = "It is required that all the datanodes for this cluster
// must be stopped. The datanode(s): ";
// boolean flag = false;
// for(int i = 0; i < datanodeList.size(); i ++){
// Node datanode = (Node) datanodeList.get(i);
// if(!datanode.getStatus().equals(HiPerConstants.STATUS_STOPPED)){
// if(!flag){
// flag = true;
// }else{
// failureMsg += ", ";
// }
// failureMsg += datanode.getId();
// }
// }
// if(flag){
// dwrResponse.setResponseMessage(failureMsg+ " is/are still running.");
// dwrResponse.setResponseCode(500);
// return;
// }
//
// String[] response = QueryIOAgentManager.getConfig(namenodeHost, new
// String[]{"dummy.dfs.namenode.shared.edits.dir"}, namenode, "hdfs-site.xml");
// if(response != null && response.length != 0 && response[0].length() != 0){
// String[] keys = new String[]{"dfs.ha.namenodes." +
// HiPerConstants.DEFAULT_CLUSTER_NAME, "dfs.namenode.shared.edits.dir",
// "dummy.dfs.namenode.shared.edits.dir"};
// String[] values = new String[]{standbyNode == null ? namenode.getId() :
// namenode.getId() + "," + standbyNode.getId(), response[0], ""};
//
// QueryIOAgentManager.updateConfiguration(namenodeHost, namenode, keys,
// values,dwrResponse);
// if(!dwrResponse.isTaskSuccess()){
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.getResponseMessage());
// }
//
// QueryIOAgentManager.copyEditsDirToSharedDir(namenodeHost,
// namenode,dwrResponse);
// if(!dwrResponse.isTaskSuccess()){
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.getResponseMessage());
// }
// }
//
// for(int i = 0; i < datanodeList.size(); i ++){
// Node datanode = (Node)datanodeList.get(i);
// Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
// String[] keys = new String[]{"dfs.ha.namenodes." +
// HiPerConstants.DEFAULT_CLUSTER_NAME};
// String[] values = new String[]{standbyNode == null ? namenode.getId() :
// namenode.getId() + "," + standbyNode.getId()};
//
// QueryIOAgentManager.updateConfiguration(datanodeHost, datanode, keys,
// values,dwrResponse);
// if(!dwrResponse.isTaskSuccess()){
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug(dwrResponse.getResponseMessage());
// }
// }
// }
// updateHAEnabled(connection, true);
// if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("HA
// Enabled");
// dwrResponse.setDwrResponse(true, "Cluster is now configured as a HA
// Cluster.", 200);
//
// }
//
// private static void updateHAEnabled(Connection connection, boolean b) throws
// Exception{
// HAStatusDAO.setHAEnabled(connection, b);
// }
// }
