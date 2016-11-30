package com.queryio.hadoopconfig;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class HadoopConfigManager {
	public static void updateRackConfig(Connection connection) throws Exception {
		List hosts = HostDAO.getAllHostDetails(connection);

		String[] rackNames = new String[hosts.size()];
		String[] hostIps = new String[hosts.size()];

		Host host = null;
		for (int i = 0; i < hosts.size(); i++) {
			host = (Host) hosts.get(i);
			hostIps[i] = host.getHostIP();
			rackNames[i] = host.getRackName();
		}

		QueryIOResponse resp = null;
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
		for (Object obj : namenodes) {
			Node namenode = (Node) obj;
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
			AppLogger.getLogger().debug("namenodeHost : " + namenodeHost.getHostIP() + " " + namenode.getId() + " "
					+ hostIps + " " + rackNames);
			resp = QueryIOAgentManager.updateNetworkTopology(namenodeHost, namenode, hostIps, rackNames);

			if (!resp.isSuccessful()) {
				throw new Exception(resp.getResponseMsg());
			}
			if (namenode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
				resp = QueryIOAgentManager.refreshNodes(namenodeHost, namenode);
				if (!resp.isSuccessful()) {
					throw new Exception(resp.getResponseMsg());
				}
			}
		}
	}

	public static void updateHostsList(Connection connection, boolean refreshNamenodes) throws Exception {
		List datanodes = NodeDAO.getAllDatanodes(connection);
		String[] datanodeAdds = new String[datanodes.size()];
		ArrayList keys = new ArrayList();
		keys.add(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);
		ArrayList values = new ArrayList();
		for (int i = 0; i < datanodes.size(); i++) {
			Node datanode = (Node) datanodes.get(i);
			Host host = HostDAO.getHostDetail(connection, datanode.getHostId());
			values = QueryIOAgentManager.getConfig(host, keys, datanode, "hdfs-site.xml");
			if (values.size() != keys.size()) {
				throw new Exception("Host " + host.getHostIP() + " is not responding.");
			}
			datanodeAdds[i] = (String) values.get(0);
			values.clear();
		}
		QueryIOResponse resp = null;
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
		for (Object obj : namenodes) {
			Node namenode = (Node) obj;
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
			resp = QueryIOAgentManager.updateHostsList(namenodeHost, namenode, datanodeAdds);
			if (!resp.isSuccessful()) {
				throw new Exception(resp.getResponseMsg());
			}
			if (refreshNamenodes && namenode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
				resp = QueryIOAgentManager.refreshNodes(namenodeHost, namenode);
				if (!resp.isSuccessful()) {
					throw new Exception(resp.getResponseMsg());
				}
			}
		}
	}

	public static void updateHostsExcludeList(Connection connection, boolean refreshNamenodes) throws Exception {
		List datanodes = NodeDAO.getAllDecomissioningDatanodes(connection);
		String[] datanodeAdds = new String[datanodes.size()];
		ArrayList keys = new ArrayList();
		keys.add(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);
		ArrayList values = new ArrayList();
		for (int i = 0; i < datanodes.size(); i++) {
			Node datanode = (Node) datanodes.get(i);
			Host host = HostDAO.getHostDetail(connection, datanode.getHostId());
			values = QueryIOAgentManager.getConfig(host, keys, datanode, "hdfs-site.xml");
			if (values.size() != keys.size()) {
				throw new Exception("Host " + host.getHostIP() + " is not responding.");
			}
			datanodeAdds[i] = (String) values.get(0);
			values.clear();
		}
		QueryIOResponse resp = null;
		ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
		for (Object obj : namenodes) {
			Node namenode = (Node) obj;
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
			resp = QueryIOAgentManager.updateHostsExcludeList(namenodeHost, namenode, datanodeAdds);
			if (!resp.isSuccessful()) {
				throw new Exception(resp.getResponseMsg());
			}
			if (refreshNamenodes && namenode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
				resp = QueryIOAgentManager.refreshNodes(namenodeHost, namenode);
				if (!resp.isSuccessful()) {
					throw new Exception(resp.getResponseMsg());
				}
			}
		}
	}

}
