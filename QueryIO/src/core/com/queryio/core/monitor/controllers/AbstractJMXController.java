/*
 * @(#)  AbstractJMXController.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.core.monitor.controllers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.tree.DefaultTreeModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo.AdminStates;
import org.apache.hadoop.hdfs.protocol.HdfsConstants.DatanodeReportType;
import org.apache.hadoop.net.NetUtils;
//import org.apache.hadoop.yarn.api.ClientRMProtocol;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesRequestPBImpl;
//import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsRequestPBImpl;
//import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesRequestPBImpl;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.ipc.YarnRPC;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HAStatusDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.managers.StatusManager;
import com.queryio.core.utils.QIODFSUtils;

public abstract class AbstractJMXController extends BaseController
{
	protected int iConnectorPort = JMXMonitorConstants.JMX_CONNECTOR_PORT;
	protected String sHostNameOrIPAddress = JMXMonitorConstants.DEFAULT_HOST_IP;
	protected String username = "";
	
	protected String password = "";

	private JMXDataManager dataMgr = null;

	private MBeanServerConnection mbeanServerConnection;
	private JMXConnector jmxConnector;
	Configuration conf = null;
	boolean isHDFSNode = false;
	
	boolean started = false;
	
	public AbstractJMXController()
	{
		this.dataMgr = new JMXDataManager();
	}

	public void setConnectorPort(int port)
	{
		this.iConnectorPort = port;
	}
	
	public int getConnectorPort()
	{
		return this.iConnectorPort;
	}
	
	@Override
	public void setInitProperty(final String property, final String value) throws Exception
	{
		if (JMXMonitorConstants.USERNAME_PROPERTY.equals(property))
		{
			this.username = value;
		}
		else if (JMXMonitorConstants.PASSWORD_PROPERTY.equals(property))
		{
			this.password = value;
		}
		else if (JMXMonitorConstants.JMXCONNECTORPORT_PROPERTY.equals(property))
		{
			try
			{
				this.iConnectorPort = Integer.parseInt(value);
			}
			catch (final Exception ex)
			{
				this.iConnectorPort = 0;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.controllers.BaseController#validateConnection()
	 */
	@Override
	public String[] validateConnection(final boolean bAdd) throws Exception
	{
		final String[] message = null;
		try
		{
			if (this.pingServer())
			{
				return null;
			}
		}
		catch (final SecurityException se)
		{
			se.printStackTrace();
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
		}
		return message;
	}

	/*
	 * 
	 */
	private boolean pingServer() throws Exception
	{
		this.init();
		final Integer count = this.mbeanServerConnection.getMBeanCount();
		if (count != null)
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.controllers.BaseController#collectData(int)
	 */
	@Override
	public void collectData() throws Exception
	{
		this.init();
		if (this.monitoredAttributesChanged)
		{
			this.dataMgr.setMonitoredHistoricalAttributes(this.monitoredHistoricalAttributes);
			this.dataMgr.setMonitoredSummaryAttributes(this.monitoredSummaryAttributes);
			this.monitoredAttributesChanged = false;
		}
		this.dataMgr.collectHistoricalData();
		this.dataMgr.collectSummaryData();
	}

	/**
	 * it initializes data manager, loads necessary classes and connects to the
	 * server
	 */
	private void init() throws Exception
	{
		this.dataMgr.init(this);
		this.connect();
		this.dataMgr.setServer(this.mbeanServerConnection);
	}

	/**
	 * connect
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception
	{
		if(connect){
			
			try{
				if(jmxConnector != null){
					jmxConnector.close();
				}
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connecting to jmx server for node " + this.getNodeId() + " on " + this.getHost().getHostIP() + " : " + this.getConnectorPort());
			
			this.sHostNameOrIPAddress = this.getHost().getHostIP();
//			if (JMXMonitorConstants.DEFAULT_HOST_IP.equals(this.sHostNameOrIPAddress))
//			{
//				// Added a work around, as the tomcat in which MC is running,
//				// has not yet started the JMX service
//				if(!checkJMXService())
//				{
//					AppLogger.getLogger().fatal("JMX Service is not running");
//					this.stopCollectingData();
//				}
//			}
			final JMXServiceURL instanceServiceURL = this.getJMXServiceURL();
			final Map env = new HashMap();
			if ((this.username != null) && (this.username.length() > 0))
			{
				final String[] credentials = new String[] { this.username, this.password };
				env.put("jmx.remote.credentials", credentials); //$NON-NLS-1$
			}
			this.addConnectionEnvironmentVariables(env);

			this.jmxConnector = JMXConnectorFactory.connect(instanceServiceURL, env);
			this.mbeanServerConnection = this.jmxConnector.getMBeanServerConnection();
			
			connect = false;
		}
	}
	
	public void testConnect() throws Exception{
		JMXConnector connector = null;
		try{
			String url = JMXMonitorConstants.DEFAULT_PROTOCOL_START + this.getHost().getHostIP() + ":"
					+ this.iConnectorPort + JMXMonitorConstants.DEFAULT_PROTOCOL_END;
			JMXServiceURL instanceServiceURL = new JMXServiceURL(url);
			Map env = new HashMap();
			if ((this.username != null) && (this.username.length() > 0))
			{
				final String[] credentials = new String[] { this.username, this.password };
				env.put("jmx.remote.credentials", credentials); //$NON-NLS-1$
			}		
			connector = JMXConnectorFactory.connect(instanceServiceURL, env);	
		}finally{
			if(connector != null){
				connector.close();
			}
		}
	}
	
	protected void prePollingState() 
	{
		this.mbeanServerConnection = null;
		monitoredAttributesChanged = true;
	}

	protected abstract JMXServiceURL getJMXServiceURL() throws Exception;

	protected void addConnectionEnvironmentVariables(final Map env)
	{
		// do nothing. Implementor classes can over-ride it to perform
		// additional
		// connection variables
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.controllers.BaseController#constructMonitoredAttributesTree(java.util.ArrayList)
	 */
	@Override
	public DefaultTreeModel constructMonitoredAttributesTree(final ArrayList attributes) throws Exception
	{
		return this.dataMgr.constructMonitoredAttributesTree(attributes);
	}

	/**
	 * This method is for internal used to determine if the JMX service has been
	 * started by the tomcat server in which it is running. There has to be
	 * upper limit for the same in order to keep it from waiting in case the JMX
	 * service has been disabled.
	 * 
	 */
	private static boolean checkJMXService()
	{
		boolean running = false;
		final int limit = 10;
		int tried = 0;
		Socket socket = null;

		while (tried < limit)
		{
			try
			{
				socket = new Socket(JMXMonitorConstants.DEFAULT_HOST_IP, JMXMonitorConstants.JMX_CONNECTOR_PORT);
				socket.close();
				running = true;
				break;
			}
			catch (final Exception exception)
			{
				try
				{
					Thread.sleep(5000);
				}
				catch (final Exception ex)
				{
					// ignoring the exception
				}
			}
			tried++;
		}
		
		return running;
	}

	/*
	 * The method returns the tree model of the mbeans which have attributes
	 * with write access or operations. (non-Javadoc)
	 * 
	 * @see com.appperfect.monitor.controllers.BaseController#getWritableMBeans()
	 */
	@Override
	public DefaultTreeModel getWritableMBeans() throws Exception
	{
		if (mbeanServerConnection == null)
		{
			init();
		}
		return this.dataMgr.getWritableMBeans();
	}
	
	@Override
	public void initUpdateStatusProp(){
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
		
			Node node = NodeDAO.getNode(connection, this.getNodeId());
			ArrayList nodeConfigList = QueryIOAgentManager.getAllNodeConfig(host, node);
			if(nodeConfigList != null && nodeConfigList.size() > 0){
				conf = new Configuration(true);
				HadoopConfig config;
				for(int i=0; i<nodeConfigList.size(); i++){
					config = (HadoopConfig) nodeConfigList.get(i);
					conf.set(config.getKey(), config.getValue());				
				}
			}
			if(node.getNodeType().equals(QueryIOConstants.NAMENODE) || node.getNodeType().equals(QueryIOConstants.DATANODE)){
				Node namenode = node;
				Host namenodeHost = host;
				if(node.getNodeType().equals(QueryIOConstants.DATANODE)){
					namenode = (Node) NodeDAO.getAllNameNodes(connection).get(0);
					namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
					ArrayList keys = new ArrayList();
					keys.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
					List values = QueryIOAgentManager.getConfig(namenodeHost, keys, namenode, "core-site.xml");
					if(values.size() == keys.size()){
						conf.set((String) keys.get(0), (String) values.get(0));
					}
				}
				
				String rootUser = QueryIOAgentManager.getOSUserName(namenodeHost,
						namenode);
				Thread.currentThread().setName(rootUser);
				isHDFSNode = true;
			}else{
				isHDFSNode = false;
			}
		}catch(Exception e){
			conf = null;
			AppLogger.getLogger().fatal(e.getLocalizedMessage(),e);
		}finally{
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(),e);
			}	
		}
	}

	@Override
	public void updateStatus(){
		
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, this.getNodeId());
			if(conf == null){
				initUpdateStatusProp();
			}
			String status = node.getStatus();
			if(conf != null){
				if(!status.equals(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION)){
					if(isHDFSNode){
						if(node.getNodeType().equals(QueryIOConstants.NAMENODE)){
							if(HAStatusDAO.isHANode(connection, node.getId())
									&& HAStatusDAO.getActiveNodeId(connection,
											node.getId()) != null){
								try
								{
									this.testConnect();	
									status = QueryIOConstants.STATUS_STARTED;
								}
								catch(Exception e1)
								{
									status = QueryIOConstants.STATUS_NOT_RESPONDING;						
								}
							} else {
								DFSClient dfsClient = null;
								try{
									dfsClient = new DFSClient(conf);
									dfsClient.exists("/");
									status = QueryIOConstants.STATUS_STARTED;
								}catch(Exception e){
									status = QueryIOConstants.STATUS_NOT_RESPONDING;
//										if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(getNodeId() + ": namenode is not running. " + e.getLocalizedMessage());
								}finally{
									if(dfsClient != null)
										dfsClient.close();
								}	
							}
						}
						if(node.getNodeType().equals(QueryIOConstants.DATANODE)){ 
							DFSClient dfsClient = null;
							try{
								boolean found = false;
//								if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("CurrentThread name is " + Thread.currentThread().getName());
//								dfsClient = new DFSClient(conf);
								dfsClient = QIODFSUtils.getDFSClient(Thread.currentThread().getName(), "QUERYIO", conf);
//								if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("dfsClient : " + dfsClient.getClientName());
								
//								dfsClient = new DFSClient(conf);
								DatanodeInfo[] datanodeInfos = dfsClient.datanodeReport(DatanodeReportType.LIVE);
								String datanodeAdd = conf.get(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);				
								
								for(DatanodeInfo datanodeInfo : datanodeInfos){
									if(datanodeInfo.getName().equals(datanodeAdd) || datanodeInfo.getXferAddrWithHostname().equals(datanodeAdd)){
										found = true;
										AdminStates datanodeStatus = datanodeInfo.getAdminState();
//											if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(datanodeAdd + " status: " + datanodeStatus);
										if(datanodeStatus.equals(AdminStates.NORMAL)){
											status = QueryIOConstants.STATUS_STARTED;
										}else{
											status = datanodeStatus.toString();
										}
										break;
									}
								}
								if(!found){
									status = QueryIOConstants.STATUS_NOT_RESPONDING;
								}
							}catch(Exception e){
								if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(getNodeId() + ": dfs client is not accessibles. " + e.getLocalizedMessage(), e);
								try
								{
									this.testConnect();	
									status = QueryIOConstants.STATUS_STARTED;
								}
								catch(Exception e1)
								{
									status = QueryIOConstants.STATUS_NOT_RESPONDING;						
								}
							}finally{
								if(dfsClient != null)
									dfsClient.close();
							}
						}
					}else{
						
						if(node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)){
							try{
								YarnConfiguration yarnConf = new YarnConfiguration(conf);
								InetSocketAddress rmAddress = NetUtils.createSocketAddr(yarnConf.get(YarnConfiguration.RM_ADDRESS));
								YarnRPC rpc = YarnRPC.create(conf);
//								ClientRMProtocol yarnClient = (ClientRMProtocol)rpc.getProxy(ClientRMProtocol.class, rmAddress, conf);
								ApplicationClientProtocol yarnClient = (ApplicationClientProtocol)rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf);
								yarnClient.getClusterMetrics(new GetClusterMetricsRequestPBImpl());
								status = QueryIOConstants.STATUS_STARTED;
							}catch(Exception e){
								status = QueryIOConstants.STATUS_NOT_RESPONDING;
								e.printStackTrace();
								if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(getNodeId() + ": RM is not running. " + e.getLocalizedMessage());
							}
						}
						if(node.getNodeType().equals(QueryIOConstants.NODEMANAGER)){ 
							try{
								boolean found = false;
								YarnConfiguration yarnConf = new YarnConfiguration(conf);
								
								InetSocketAddress rmAddress = NetUtils.createSocketAddr(yarnConf.get(YarnConfiguration.RM_ADDRESS));
								YarnRPC rpc = YarnRPC.create(conf);
								
//								ClientRMProtocol yarnClient = (ClientRMProtocol)rpc.getProxy(ClientRMProtocol.class, rmAddress, conf);
//								GetClusterNodesResponse response = yarnClient.getClusterNodes(new GetClusterNodesRequestPBImpl());
								ApplicationClientProtocol yarnClient = (ApplicationClientProtocol)rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf);
								GetClusterNodesResponse response = yarnClient.getClusterNodes(new GetClusterNodesRequestPBImpl());
								List<NodeReport> list = response.getNodeReports();
								String nmWebAppAdd = yarnConf.get(YarnConfiguration.NM_WEBAPP_ADDRESS);
//								System.out.println(nmWebAppAdd);
								String[] arr = nmWebAppAdd.split(":");
								if(arr.length > 1){
									String host = arr[0];
									String port = arr[1];
									
									for(NodeReport nodeReport : list){
										String httpAddress = nodeReport.getHttpAddress();
//										System.out.println(httpAddress);
										if(nodeReport.getHttpAddress().equals(nmWebAppAdd)){
											found = true;																						
										}else{
											String[] arr1 = httpAddress.split(":");
											if(arr1.length > 1){
												String host1 = arr1[0];
												String port1 = arr1[1];
												if(port1.equals(port)){
													if(host.equals("127.0.0.1") || host.equals("localhost")){
														found = true;
													}else{
														InetAddress add = InetAddress.getByName(host);
//														System.out.println(host + "\t" + host1 + "\t" + add.getHostAddress() + "\t" + add.getHostName());
														if(add.getHostAddress().equals(host1) || add.getHostName().equals(host1)){
															found = true;
														}	
													}														
												}													
											}
										}
										if(found){
											NodeState nodeState = nodeReport.getNodeState();
//											if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(nmWebAppAdd + " status: " + nodeState);
											if(nodeState.equals(NodeState.RUNNING)){
												status = QueryIOConstants.STATUS_STARTED;
											}else{
												status = nodeState.toString();
											}
											break;
										}
									}
								}								
								if(!found){
									status = QueryIOConstants.STATUS_NOT_RESPONDING;
								}
							}catch(Exception e){
								e.printStackTrace();
//									if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(getNodeId() + ": RM client is not accessibles. " + e.getLocalizedMessage());
								try
								{
									this.testConnect();	
									status = QueryIOConstants.STATUS_STARTED;
								}
								catch(Exception e1)
								{
									status = QueryIOConstants.STATUS_NOT_RESPONDING;						
								}
							}
						}
					}
				}
			}else{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(getNodeId() + ": Conf is null.");
				try
				{
					this.testConnect();	
					status = QueryIOConstants.STATUS_STARTED;
				}
				catch(Exception e1)
				{
					status = QueryIOConstants.STATUS_NOT_RESPONDING;
				}
			}
			
			node = NodeDAO.getNode(connection, this.getNodeId());
			if(!node.getStatus().equals(status) && !node.getStatus().equals(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION)){
				node.setStatus(status);
				NodeDAO.updateStatus(connection, node);
			}
			
			if(node.getStatus().equalsIgnoreCase(QueryIOConstants.STATUS_NOT_RESPONDING))
			{
				if(started) {
					StatusManager.addStatus(node.getId(), System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_FAILURE);
				}
			}
			else if(node.getStatus().equalsIgnoreCase(QueryIOConstants.STATUS_STARTED) || node.getStatus().equalsIgnoreCase(QueryIOConstants.STATUS_NORMAL) || node.getStatus().equals(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION))
			{
				if( ! started)	{
					started = true;
				}
				StatusManager.addStatus(node.getId(), System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_OK);
			}
			else
			{
				StatusManager.addStatus(node.getId(), System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_SUSPENDED);
			}
			
		}catch(Exception e){
			AppLogger.getLogger().fatal("Error getting status", e);
		} finally{
			try{
				CoreDBManager.closeConnection(connection);
			}catch(Exception e){
				AppLogger.getLogger().fatal("Error closing database connection", e);
			}
		}	
	}
	
	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}
}
