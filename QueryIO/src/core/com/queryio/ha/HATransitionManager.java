package com.queryio.ha;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HAStatusDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class HATransitionManager {
	public static void performFailover(Connection connection, String nodeId, DWRResponse dwrResponse) throws Exception {

		if (!HAStatusDAO.isHANode(connection, nodeId)) {
			dwrResponse.setDwrResponse(false, "Node is not configured as a HA node.", 200);
		}
		;
		String nodeId2 = null;
		Node nn = null, snn = null;
		if ((nodeId2 = HAStatusDAO.getStandbyNodeId(connection, nodeId)) != null) {
			nn = NodeDAO.getNode(connection, nodeId);
			snn = NodeDAO.getNode(connection, nodeId2);
		} else {
			nodeId2 = HAStatusDAO.getActiveNodeId(connection, nodeId);
			nn = NodeDAO.getNode(connection, nodeId2);
			snn = NodeDAO.getNode(connection, nodeId);
		}

		Host snnHost = HostDAO.getHostDetail(connection, snn.getHostId());

		String failoverArgument = "";

		failoverArgument = nn.getId() + " " + snn.getId();
		QueryIOAgentManager.performFailOver(snnHost, snn, failoverArgument, dwrResponse);

		if (dwrResponse.isTaskSuccess()) {
			HAStatusDAO.swapEntry(connection, snn.getId(), nn.getId());
			dwrResponse.setDwrResponse(true, dwrResponse.getResponseMessage(), 200);
		} else {
			dwrResponse.setDwrResponse(false, dwrResponse.getResponseMessage(), 200);
		}

	}

	public static void disableHA(Connection connection, String nodeId, DWRResponse dwrResponse) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Disabling HA mode .. ");
		Node namenode = NodeDAO.getNode(connection, nodeId);
		String snnId = HAStatusDAO.getStandbyNodeId(connection, nodeId);
		if (namenode != null) {
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());

			List datanodeList = NodeDAO.getAllDatanodes(connection);

			QueryIOAgentManager.copySharedDirLogstoEditsLogs(namenodeHost, namenode, dwrResponse);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug(dwrResponse.getResponseMessage());
			if (!dwrResponse.isTaskSuccess()) {
				return;
			}

			ArrayList keys = new ArrayList();

			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + nodeId + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + snnId + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + nodeId + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + snnId + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + nodeId + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + snnId + "." + nodeId);
			ArrayList values = QueryIOAgentManager.getConfig(namenodeHost, keys, namenode, "hdfs-site.xml");

			keys.add(DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + nodeId + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + snnId + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + nodeId + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + snnId + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + nodeId + "." + snnId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + snnId + "." + snnId);

			keys.add(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY);
			keys.add(DFSConfigKeys.DFS_HA_NAMENODE_ID_KEY);

			QueryIOResponse res = QueryIOAgentManager.unsetConfiguration(namenodeHost, namenode, keys, "hdfs-site.xml");

			dwrResponse.setDwrResponse(res.isSuccessful(), res.getResponseMsg(), res.isSuccessful() ? 200 : 500);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug(dwrResponse.isTaskSuccess() + ": " + dwrResponse.getResponseMessage());
			if (!dwrResponse.isTaskSuccess()) {
				return;
			}

			for (int i = 0; i < datanodeList.size(); i++) {
				Node datanode = (Node) datanodeList.get(i);
				Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());

				res = QueryIOAgentManager.unsetConfiguration(datanodeHost, datanode, keys, "hdfs-site.xml");
				dwrResponse.setDwrResponse(res.isSuccessful(), res.getResponseMsg(), res.isSuccessful() ? 200 : 500);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(dwrResponse.isTaskSuccess() + " " + dwrResponse.getResponseMessage());
				if (!dwrResponse.isTaskSuccess()) {
					return;
				}
			}

			keys.clear();

			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + nodeId);

			keys.add(DFSConfigKeys.DFS_NAMESERVICES);
			values.add(nodeId);

			for (int i = 0; i < datanodeList.size(); i++) {
				Node datanode = (Node) datanodeList.get(i);
				Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
				if (!datanode.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
					datanode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
					NodeDAO.updateStatus(connection, datanode);
				}
				QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, keys, values);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(dwrResponse.isTaskSuccess() + " " + dwrResponse.getResponseMessage());
				if (!dwrResponse.isTaskSuccess()) {
					return;
				}
			}
			keys.add(DFSConfigKeys.DFS_NAMESERVICE_ID);
			values.add(nodeId);

			QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, keys, values);
			if (!namenode.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
				namenode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
				NodeDAO.updateStatus(connection, namenode);
			}
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("HA Disabled");
		dwrResponse.setDwrResponse(true, "Cluster is now configured as a non-HA Cluster.", 200);

	}
}
