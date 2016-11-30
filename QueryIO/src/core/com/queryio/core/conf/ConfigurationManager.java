package com.queryio.core.conf;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;

import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class ConfigurationManager {
	public static Configuration getKerberosConfiguration(final Connection connection, String nameNodeId)
			throws Exception {
		Configuration conf = getConfiguration(connection, nameNodeId);

		conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
		conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION, "true");

		// DFS_NAMENODE_USER_NAME_KEY Required Property

		return conf;
	}

	public static Configuration getConfiguration(final Connection connection, String nodeId) throws Exception {

		Configuration conf = new Configuration(true);

		Node node = NodeDAO.getNode(connection, nodeId);
		if (node == null) {
			throw new Exception("No node found by id: " + nodeId);
		}
		Host host = HostDAO.getHostDetail(connection, node.getHostId());

		ArrayList nodeConfigList = QueryIOAgentManager.getAllNodeConfig(host, node);
		HadoopConfig config;
		for (int i = 0; i < nodeConfigList.size(); i++) {
			config = (HadoopConfig) nodeConfigList.get(i);
			conf.set(config.getKey(), config.getValue());
		}

		return conf;
	}
}
