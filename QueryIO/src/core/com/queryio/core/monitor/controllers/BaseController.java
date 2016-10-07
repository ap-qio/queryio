package com.queryio.core.monitor.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.RuleDAO;
import com.queryio.core.monitor.alerts.evaluator.AlertEvaluator;
import com.queryio.core.monitor.managers.StatusManager;

public abstract class BaseController implements Runnable
{
	private boolean bCollectData = false;
	private int dataFetchInterval = 5;
	
	private boolean notifiedOk = false;
	private boolean notifiedError = false;
	
	long totalVal = 0;
	long saveValue = 0;
	boolean bFirstTimeHistoricalCPUUsage = true;
	
	boolean bFirstTimeSummaryCPUUsage = true;
	
	long lastCPUTimeHistorical = 0;
	
	long lastCPUTimeSummary = 0;
	float perCPUUsage = 0;
	
	long curCPUTime = 0;
	
	long lastGCCountSummary = 0;
	long lastGCTimeSummary = 0;
	
	private boolean bFirstTimeSummaryGCCount;
	private Long curGCCount;
	private boolean bFirstTimeSummaryGCTime;
	private Long curGCTime;
	
	long curValue = 0;
	
	boolean[] bNotFirstTime = new boolean[ColumnConstants.PERSIST_COLUMN_NAMES.length];
	long[] lastValHistorical = new long[ColumnConstants.PERSIST_COLUMN_NAMES.length];
	
	private String state = ControllerManager.CONTROLLER_STATE_SUSPENDED;
	
	protected boolean monitoredAttributesChanged = false;

	protected ArrayList monitoredHistoricalAttributes = null;
	protected ArrayList monitoredSummaryAttributes = null;
	
	private int iRequestId = -1;
	
	private String nodeId = null;
	
	private String nodeType;
	
	private JMXDataManager dataMgr = new JMXDataManager();
	
	ControllerData controllerHistoricalData = null;
	ControllerData controllerSummaryData = null;
	
	protected int servicePort = -1;
	
	protected Host host = null;
	
	protected boolean connect = true;

	public abstract String[] validateConnection(boolean bAdd) throws Exception;
	
	public abstract void collectData() throws Exception;
	
	public abstract void initUpdateStatusProp() throws Exception;
	
	public abstract void updateStatus() throws Exception;
	
	public abstract void setInitProperty(String property, String value) throws Exception;
	
	public abstract DefaultTreeModel constructMonitoredAttributesTree(ArrayList attributes) throws Exception;
	
	public void run()
	{
		this.startCollectingData();
	}
	
	public static boolean isNullOrEmpty(final String strToCheck)
	{
		if ((strToCheck == null) || (strToCheck.trim().length() == 0) || "null".equals(strToCheck))
		{
			return true;
		}

		return false;
	}
	
	public ControllerData getControllerHistoricalData()
	{
		return this.controllerHistoricalData;
	}
	
	public ControllerData getControllerSummaryData()
	{
		return this.controllerSummaryData;
	}
	
	public int getDataFetchInterval() 
	{
		return dataFetchInterval;
	}

	public void setDataFetchInterval(int dataFetchInterval) 
	{
		this.dataFetchInterval = dataFetchInterval;
	}
	
	public int getServicePort()
	{
		return this.servicePort;
	}

	void setServicePort(final int servicePort)
	{
		this.servicePort = servicePort;
	}
	
	public void setHost(Host host)
	{
		this.host = host;
	}
	
	public Host getHost()
	{
		return this.host;
	}
	
	public String getNodeType() 
	{
		return nodeType;
	}

	public void setNodeType(String nodeType) 
	{
		this.nodeType = nodeType;
	}

	public String getNodeId() 
	{
		return nodeId;
	}

	public void setNodeId(String nodeId) 
	{
		this.nodeId = nodeId;
	}
	
	public DefaultTreeModel getWritableMBeans() throws Exception
	{
		return null;
	}
	
	void setMonitoredHistoricalAttributes(final ArrayList monitoredAttributes)
	{
		this.monitoredHistoricalAttributes = monitoredAttributes;
		this.monitoredAttributesChanged = true;
	}
	
	void setMonitoredSummaryAttributes(final ArrayList monitoredAttributes)
	{
		this.monitoredSummaryAttributes = monitoredAttributes;
		this.monitoredAttributesChanged = true;
	}
	
//	private boolean isValidRequestId(final int iReqId)
//	{
//		return iReqId == this.iRequestId;
//	}
	
	private int indexOf(String[] list, String value)
	{
		for(int i=0; i<list.length; i++)
		{
			if(list[i].equals(value))
				return i;
		}
		
		return -1;
	}
	
	boolean modified = false;
	
	public final void setHistoricalValue(final ControllerAttribute ca, final Object value)
	{
		int indexPersistColNames = indexOf(ColumnConstants.PERSIST_COLUMN_NAMES, ca.getColumnName());
		
		if(ca.getColumnName().equals(ColumnConstants.COL_CPUUSAGE))
		{
			if(!bFirstTimeHistoricalCPUUsage)
			{
				curCPUTime = (Long)value;
				if(curCPUTime!=0)
				{
					this.controllerHistoricalData.setValue(ca.getColumnName(), (float)((curCPUTime - lastCPUTimeHistorical) * 100) / (float)curCPUTime);
				}
				else
				{
					this.controllerHistoricalData.setValue(ca.getColumnName(), 0);
				}
				
				lastCPUTimeHistorical = curCPUTime;
			}
			else
			{
				bFirstTimeHistoricalCPUUsage = false;
				lastCPUTimeHistorical = (Long)value;
				
				this.controllerHistoricalData.setValue(ca.getColumnName(), 0);
			}
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_NN_MONITORDATA_REMAINING))
		{
			this.controllerHistoricalData.setValue(ca.getColumnName(), (Long)value/(float)(1024*1024*1024));
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_NN_MONITORDATA_DFSUSED))
		{
			this.controllerHistoricalData.setValue(ca.getColumnName(), (Long)value/(float)(1024*1024*1024));
		}
		else if(indexPersistColNames!=-1)
		{
			if(value instanceof Integer)
			{
				long val = (Integer)value;
				curValue = val;
			}
			else
			{
				curValue = (Long)value;
			}

			if(bNotFirstTime[indexPersistColNames])
			{
				this.controllerHistoricalData.setValue(ca.getColumnName(), curValue - lastValHistorical[indexPersistColNames]);
				
				lastValHistorical[indexPersistColNames] = curValue;
			}
			else
			{
				lastValHistorical[indexPersistColNames] = curValue;
				
				bNotFirstTime[indexPersistColNames] = true;
				this.controllerHistoricalData.setValue(ca.getColumnName(), 0);
			}
		}
		else
		{
			this.controllerHistoricalData.setValue(ca.getColumnName(), value);
		}
	}
	
	public final void setSummaryValue(final ControllerAttribute ca, final Object value)
	{
		if(ca.getColumnName().equals(ColumnConstants.COL_CPUUSAGE))
		{
			if(!bFirstTimeSummaryCPUUsage)
			{
				curCPUTime = (Long)value;
				
				if(curCPUTime!=0)
				{
					this.controllerSummaryData.setValue(ca.getColumnName(), (float)((curCPUTime - lastCPUTimeSummary) * 100) / (float)curCPUTime);
				}
				else
				{
					this.controllerSummaryData.setValue(ca.getColumnName(), 0);
				}
				
				lastCPUTimeSummary = curCPUTime;
			}
			else
			{
				bFirstTimeSummaryCPUUsage = false;
				lastCPUTimeSummary = (Long)value;
				
				this.controllerHistoricalData.setValue(ca.getColumnName(), 0);
			}
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_GC_COUNT))
		{
			if(!bFirstTimeSummaryGCCount)
			{
				curGCCount = (Long)value;
				
				this.controllerSummaryData.setValue(ca.getColumnName(), curGCCount - lastGCCountSummary);
				
				lastGCCountSummary = curGCCount;
			}
			else
			{
				bFirstTimeSummaryGCCount = false;
				lastGCCountSummary = (Long)value;
				
				this.controllerSummaryData.setValue(ca.getColumnName(), 0);
			}
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_GC_TIME_MILLIS))
		{
			if(!bFirstTimeSummaryGCTime)
			{
				curGCTime = (Long)value;
				
				if(curGCTime!=0){
					this.controllerSummaryData.setValue(ca.getColumnName(), (float)((curGCTime - lastGCTimeSummary) * 100) / (float)curGCTime);
				} else {
					this.controllerSummaryData.setValue(ca.getColumnName(), 0);
				}
				
				lastGCTimeSummary = curGCTime;
			}
			else
			{
				bFirstTimeSummaryGCTime = false;
				lastGCTimeSummary = (Long)value;
				
				this.controllerSummaryData.setValue(ca.getColumnName(), 0);
			}
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_NN_MONITORDATA_REMAINING))
		{
			this.controllerSummaryData.setValue(ca.getColumnName(), (Long)value/(float)(1024*1024*1024));
		}
		else if(ca.getColumnName().equals(ColumnConstants.COL_NN_MONITORDATA_DFSUSED))
		{
			this.controllerSummaryData.setValue(ca.getColumnName(), (Long)value/(float)(1024*1024*1024));
		}
		else
		{
			this.controllerSummaryData.setValue(ca.getColumnName(), value);
		}
	}
	
	void stopCollectingData()
	{
		this.bCollectData = false;
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("stopCollectingData called for " + nodeType + " on " + this.host.getHostIP());
		StatusManager.addStatus(this.nodeId, System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_SUSPENDED);
	}
	
	void initControllerData()
	{
		this.controllerHistoricalData = new ControllerData();
		this.controllerSummaryData = new ControllerData();
	}
	
	void startCollectingData()
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("startCollectingData called for " + nodeType + " on " + this.host.getHostIP());
		this.bCollectData = true;
		
		final AlertEvaluator evaluator = new AlertEvaluator();
		ArrayList rules = null;
		
		long curTime = 0;
		int failedAttempts = 0;
		notifiedOk = true;
		notifiedError = false;
		while (this.bCollectData)
		{
			try
			{
				if (this.bCollectData)
				{
					this.controllerHistoricalData = new ControllerData();
					this.controllerSummaryData = new ControllerData();
					
					curTime = System.currentTimeMillis();
					this.controllerHistoricalData.setValue(ColumnConstants.COL_MONITORDATA_MONITOR_TIME, new Timestamp(curTime));
					
					this.controllerHistoricalData.setTimeStamp(curTime);
					this.controllerSummaryData.setTimeStamp(curTime);
					if (this.bCollectData) {
						this.updateStatus();
					}
					this.collectData();
					Connection connection = null;
					try
					{
						connection = CoreDBManager.getQueryIODBConnection();

						// Write monitored data to the database.
						if(nodeType.equals(QueryIOConstants.NAMENODE))
						{
//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Calling: insertNameNodeMonitorData/updateNameNodeSummaryData");
							MonitorDAO.insertNameNodeMonitorData(connection, this.nodeId, this.controllerHistoricalData.getColumnNames(), this.controllerHistoricalData.getValues());
							MonitorDAO.updateNameNodeSummaryData(connection, this.nodeId, this.controllerSummaryData.getColumnNames(), this.controllerSummaryData.getValues());
						}
						else if(nodeType.equals(QueryIOConstants.DATANODE))
						{
//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Calling: insertDataNodeMonitorData/updateDataNodeSummaryData");
							MonitorDAO.insertDataNodeMonitorData(connection, this.nodeId, this.controllerHistoricalData.getColumnNames(), this.controllerHistoricalData.getValues());
							MonitorDAO.updateDataNodeSummaryData(connection, this.nodeId, this.controllerSummaryData.getColumnNames(), this.controllerSummaryData.getValues());
						}
						else if(nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
						{
//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Calling: insertResourceManagerMonitorData/updateResourceManagerSummaryData");
							MonitorDAO.insertResourceManagerMonitorData(connection, this.nodeId, this.controllerHistoricalData.getColumnNames(), this.controllerHistoricalData.getValues());
							MonitorDAO.updateResourceManagerSummaryData(connection, this.nodeId, this.controllerSummaryData.getColumnNames(), this.controllerSummaryData.getValues());
						}
						else if(nodeType.equals(QueryIOConstants.NODEMANAGER))
						{
//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Calling: insertNodeManagerMonitorData/updateNodeManagerSummaryData");
							MonitorDAO.insertNodeManagerMonitorData(connection, this.nodeId, this.controllerHistoricalData.getColumnNames(), this.controllerHistoricalData.getValues());
							MonitorDAO.updateNodeManagerSummaryData(connection, this.nodeId, this.controllerSummaryData.getColumnNames(), this.controllerSummaryData.getValues());
						}
						
						rules = RuleDAO.getRules(connection, this.nodeId);
						evaluator.setNodeId(this.nodeId);
						evaluator.setControllerData(this.controllerHistoricalData);
						evaluator.setTimeStamp(curTime);
						if(rules!=null)	evaluator.serve(rules);		
					}
					catch(Exception e)
					{
						AppLogger.getLogger().fatal("Saving monitored data for nodetype: " + this.nodeType + " on hostname: " + this.host.getHostIP() + " failed with exception: " + e.getMessage(), e);
					}
					finally
					{
						try
						{
							CoreDBManager.closeConnection(connection);
						}
						catch(Exception e)
						{
							AppLogger.getLogger().fatal("Connection could not be closed, Exception: " + e.getMessage(), e);
						}
					}
					
					if( ! notifiedOk)
					{
						failedAttempts = 0;
						
						String subject = "Monitor Success: " + this.nodeType + " on " + this.host.getHostIP();
						String message = "Monitoring " + this.nodeType + " on " + this.host.getHostIP() + " succeeded.";
						
//						AppLogger.getLogger().info("Monitoring " + this.nodeType + " on " + this.host.getHostIP() + " succeeded.");
						
						notifiedError = false;
						notifiedOk = ControllerManager.sendEmailNotification(subject, message);
					}
				} else {
					StatusManager.addStatus(this.nodeId, System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_SUSPENDED);
				}
			}
			catch (final Exception e)
			{
				AppLogger.getLogger().fatal("Monitoring failed for node: " + nodeId + ", " + e.getMessage(), e);	//TODO: Temp Windows handling. uncomment later
				this.connect = true;
				
				failedAttempts++;
				
				if( ( failedAttempts > EnvironmentalConstants.getMonitorRetryCount()) && ( ! notifiedError ) )
				{
					String subject = "Monitor Failure: " + this.nodeType + "[Node Id: "+this.nodeId+"] on " + this.host.getHostIP();
					
					String message = "Monitoring " + this.nodeType + "[Node Id: "+this.nodeId+"] on " + this.host.getHostIP() + " failed with exception: " + e.getMessage();
					message += QueryIOConstants.NEW_LINE;
					
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					
					message += errors.toString();
					
					notifiedOk = false;
					try{
						notifiedError = ControllerManager.sendEmailNotification(subject, message);
					} catch(Exception ex){
						if(failedAttempts <= 1){
							AppLogger.getLogger().fatal("Monitoring " + this.nodeType + " on " + this.host.getHostIP()+ " failed with exception: " + e.getMessage(), e);
							AppLogger.getLogger().fatal("Error sending E-Mail notification: " + ex.getMessage(), ex);
						}
					}
				}
			}
			if(this.bCollectData)
			{
				try 
				{
					Thread.sleep(this.dataFetchInterval * 1000);
				}
				catch (InterruptedException e1) 
				{
					AppLogger.getLogger().fatal(e1.getMessage(), e1);
				}
			}
		}	
	}
}