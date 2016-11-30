package com.queryio.queryioservices;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.QueryIOServiceDAO;
import com.queryio.core.monitor.beans.SummaryTable;

public class QueryIOServicesManager {
	public static SummaryTable getAllServices(boolean isHiveViewSelected) {
		SummaryTable table = new SummaryTable();
		Connection connection = null;
		ArrayList colName = new ArrayList();
		colName.add("NameNode");
		if (isHiveViewSelected) {
			colName.add("Hive Service");
		} else {
			colName.add("QueryIO Service");
		}
		colName.add("Host");
		colName.add("Status");
		table.setColNames(colName);
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
			Node node = null;
			Host host = null;
			ArrayList row = null;
			for (int i = 0; i < namenodes.size(); i++) {
				node = (Node) namenodes.get(i);
				host = HostDAO.getHostDetail(connection, node.getHostId());

				if (isHiveViewSelected) {
					String analyticsDB = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, node.getId());
					String metastoreDB = NodeDAO.getDBNameForNameNodeMapping(connection, node.getId());
					if (analyticsDB == null || metastoreDB == null) {
						continue;
					}
					row = new ArrayList();
					row.add(node.getId());
					row.add(QueryIOConstants.SERVICE_HIVE);
					row.add(host.getHostIP());
					row.add(QueryIOServiceDAO.get(connection, node.getId(), QueryIOConstants.SERVICE_HIVE).getStatus());
					table.addRow(row);
				} else {
					row = new ArrayList();
					row.add(node.getId());
					row.add(QueryIOConstants.SERVICE_OS3);
					row.add(host.getHostIP());
					row.add(QueryIOServiceDAO.get(connection, node.getId(), QueryIOConstants.SERVICE_OS3).getStatus());
					table.addRow(row);

					row = new ArrayList();
					row.add(node.getId());
					row.add(QueryIOConstants.SERVICE_HDFS_OVER_FTP);
					row.add(host.getHostIP());
					row.add(QueryIOServiceDAO.get(connection, node.getId(), QueryIOConstants.SERVICE_HDFS_OVER_FTP)
							.getStatus());
					table.addRow(row);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return table;
	}
}
