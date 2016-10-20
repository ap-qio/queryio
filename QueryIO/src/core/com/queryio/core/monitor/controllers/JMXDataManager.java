/*
 * @(#)  DataManager.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.DataTypes;


/**
 * 
 * @author Exceed Consultancy Services
 */
public final class JMXDataManager
{
	private static final char COLUMN_ATTR_SEPERATOR = '_';
	
	private ArrayList monitoredHistoricalAttributes = null;
	private ArrayList monitoredSummaryAttributes = null;
	
	private boolean monitoredHistoricalAttributesChanged = false;
	private boolean monitoredSummaryAttributesChanged = false;
	
	
	private ArrayList monitoredHistoricalObjectNames = null;
	private ArrayList monitoredSummaryObjectNames = null;
	
	private MBeanServerConnection server = null;
	
	private ControllerAttribute controllerAttribute = null;
	
	private BaseController base = null;
	
	private transient DefaultMutableTreeNode parentNode = null;
	
	private DefaultTreeModel dynamicAttributes = null;
	
	void init(final BaseController base) throws SecurityException, IllegalArgumentException
	{
		this.base = base;
	}
	
	public void setServer(final MBeanServerConnection server)
	{
		this.server = server;
	}
	
	void setMonitoredHistoricalAttributes(final ArrayList attrList)
	{
		this.monitoredHistoricalAttributes = attrList;
		this.monitoredHistoricalAttributesChanged = true;
	}
	
	void setMonitoredSummaryAttributes(final ArrayList attrList)
	{
		this.monitoredSummaryAttributes = attrList;
		this.monitoredSummaryAttributesChanged = true;
	}
	
	private ArrayList getObjectNames(final ArrayList alAttrbutes) throws Exception
	{
		ArrayList alObjects = null;
		if ((alAttrbutes != null) && (alAttrbutes.size() > 0))
		{
			alObjects = new ArrayList();
			ControllerAttribute ca = null;
			String name = null;
			int index = -1;
			for (final Iterator iter = alAttrbutes.iterator(); iter.hasNext();)
			{
				ca = (ControllerAttribute) iter.next();
				name = ca.getName();
				index = name.lastIndexOf(QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR);
				if (index != -1)
				{
					name = name.substring(0, index);
				}
				if (!alObjects.contains(name))
				{					
					alObjects.add(name);
				}
			}
		}
		return alObjects;
	}

	private Set getAllRegisteredObjectNames() throws IOException
	{
		return this.server.queryMBeans(null, null);
	}
	
	private String getObjectNameType(final ObjectName name)
	{
		// Some MBeans have property j2eeType
		// If j2eeType is not there then check for property type
		String type = name.getKeyProperty("j2eeType");
		if (type == null)
		{
			type = name.getKeyProperty("type");
		}
		if (type == null)
		{
			type = name.getKeyProperty("Type");
		}
		if (type == null)
		{
			int colonIndex = name.getCanonicalName().indexOf(':');
			if (colonIndex != -1)
			{
				type = name.getCanonicalName().substring(0, colonIndex);
			}
		}
		return type;
	}
	
	private String getObjectShortName(final ObjectName name)
	{
		String shortName = name.getKeyProperty("name");
		if (shortName == null)
		{
			shortName = name.getKeyProperty("Name");
		}
		return shortName;
	}
	
	private ControllerAttribute createControllerAttribute(final String name, final String shortName,
			final String columnName, final int dataType)
	{
		ControllerAttribute attribute = null;
		attribute = new ControllerAttribute();
		attribute.setName(name);
		attribute.setShortName(shortName);
		attribute.setColumnName(columnName);
		attribute.setMonitorByDefault(false);
		attribute.setDataType(dataType);
		attribute.setChartable(DataTypes.isNumeric(dataType));
		return attribute;
	}
	
	private void setParentNode(final String name, final String shortName) throws Exception
	{
		final int childCount = this.parentNode.getChildCount();
		DefaultMutableTreeNode node = null;
		ControllerAttribute attribute = null;
		for (int i = 0; i < childCount; i++)
		{
			node = (DefaultMutableTreeNode) this.parentNode.getChildAt(i);
			attribute = (ControllerAttribute) node.getUserObject();
			if (name.equals(attribute.getName()))
			{
				this.parentNode = node;
				return;
			}
		}

		// node not found, hence create it and add it to the tree. column name
		// is null as this will not be leaf node
		node = new DefaultMutableTreeNode(this.createControllerAttribute(name, shortName, null, DataTypes.STRING));
		this.parentNode.add(node);
		this.parentNode = node;
	}
	
	DefaultTreeModel constructMonitoredAttributesTree(final ArrayList attributes) throws Exception
	{
		this.parentNode = new DefaultMutableTreeNode("Root Node");
		final DefaultTreeModel dynamicAttributes = new DefaultTreeModel(this.parentNode);
		ControllerAttribute ca = null;
		String oName = null;
		ObjectName on = null;
		int lastIndex = -1;
		boolean bObjectName = false;
		String type = null;
		String name = null;
		for (final Iterator iter = attributes.iterator(); iter.hasNext();)
		{
			ca = (ControllerAttribute) iter.next();
			oName = ca.getName();
			
			lastIndex = oName.lastIndexOf(ca.getShortName());
			if (lastIndex > 0)
			{
				oName = oName.substring(0, lastIndex - 1);
			}
			try
			{
				bObjectName = false;
				on = new ObjectName(oName);
				bObjectName = true;
			}
			catch (final Exception ex)
			{
				// supress the exception, as it may not be possible to create
				// ObjectName, i.e. it is pre-defined
				// attribute
			}
			if (bObjectName)
			{
				type = this.getObjectNameType(on);
				name = this.getObjectShortName(on);
				if (name == null)
				{
					name = oName;
				}
				// set the parent to type of the object name
				this.setParentNode(type, type);

				// set the parent to name of the object name
				this.setParentNode(oName, name);

				this.parentNode.add(new DefaultMutableTreeNode(ca));

				// set the parent back to object type
				this.parentNode = (DefaultMutableTreeNode) this.parentNode.getParent();

				// set the parent back to root
				this.parentNode = (DefaultMutableTreeNode) this.parentNode.getParent();
			}
			else
			{
				if (lastIndex > 0)
				{
					final StringTokenizer stk = new StringTokenizer(oName, 
							String.valueOf('_'));
					String token = null;
					final int tokens = stk.countTokens();
					int var = 0;
					while (stk.hasMoreTokens())
					{
						token = stk.nextToken();
						// set the parent to type of the object name
						if(var > 0)
							this.setParentNode(oName, token);
						else
							this.setParentNode(token, token);
						var++;
					}
					this.parentNode.add(new DefaultMutableTreeNode(ca));
					for (int i = 0; i < tokens; i++)
					{
						// set the parent back to the original position
						this.parentNode = (DefaultMutableTreeNode) this.parentNode.getParent();
					}
				}
				else
				{
					this.parentNode.add(new DefaultMutableTreeNode(ca));
				}
			}
		}
		return dynamicAttributes;
	}
	
	public boolean matches(ArrayList list, String onName)
	{
		String s;
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("onName: " + onName);
		for(int i=0; i<list.size(); i++)
		{
			s = (String)list.get(i);
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("s: " + s);
			if(onName.startsWith(s.split(",")[0]))
				return true;
		}
		
		return false;
	}
	
	public String findMatch(ArrayList list, String onName)
	{
		String s;
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("onName: " + onName);
		for(int i=0; i<list.size(); i++)
		{
			s = (String)list.get(i);
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("s: " + s);
			if(onName.startsWith(s.split(",")[0]))
				return s.split(",")[0];
		}
		
		return "";
	}
	/*
	 * public int findMatchIndex(ArrayList list, String onName)
	{
		String s = null;

		for(int i=0; i<list.size(); i++)
		{
			s = ((ControllerAttribute)list.get(i)).getName();
			
			if(s.contains(",") && onName.contains(","))
			{
				if((onName.split(",")[1]).equals((s.split(",")[1])))
					return i;
			}
			else
			{
				if(onName.equals(s))
					return i;
			}
		}
		
		return -1;
	}
	 * */
	public int findMatchIndex(ArrayList list, String onName)
	{
		String s = null;

		for(int i=0; i<list.size(); i++)
		{
			s = ((ControllerAttribute)list.get(i)).getName();
			
			if(s.contains(",") && onName.contains(","))
			{
				String[] arr1 = onName.split(",");
				String[] arr2 = s.split(",");
				if((arr1[arr1.length -1]).equals(arr2[arr2.length - 1]))
					return i;
			}
			else
			{
				if(onName.equals(s))
					return i;
			}
		}
		
		return -1;
	}
	
	public void collectHistoricalData() throws Exception
	{
		if (this.monitoredHistoricalAttributes == null)
		{
//			System.out.println("No attriutes to monitor");
			return;
		}

		if (this.monitoredHistoricalAttributesChanged)
		{
			this.monitoredHistoricalObjectNames = this.getObjectNames(this.monitoredHistoricalAttributes);
			this.monitoredHistoricalAttributesChanged = false;
		}

		final Set objectNameSet = this.getAllRegisteredObjectNames();
		
		if (objectNameSet != null)
		{
			ObjectInstance instance = null;
			MBeanInfo info = null;
			ObjectName on = null;
			String onName = null;
			String onType = null;
			String onShortName = null;
			
			String attrName = null;
			String attrType = null;
			MBeanAttributeInfo[] attributes = null;
			Object value = null;
			ControllerAttribute attribute = null;
			final StringBuffer sbAttributeName = new StringBuffer();

			if (this.controllerAttribute == null)
			{
				this.controllerAttribute = new ControllerAttribute();
			}
			
//			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("MONITORED OB N: " + this.monitoredHistoricalObjectNames);
			
			for (final Iterator iter = objectNameSet.iterator(); iter.hasNext();)
			{
				instance = (ObjectInstance) iter.next();
				
//				System.out.println("instance.getObjectName(): " + instance.getObjectName());
				
				on = instance.getObjectName();

				// get the name of the ObjectName (cannonical name)
				onName = on.getCanonicalName();

				onType = this.getObjectNameType(on);
				// get the short name of the ObjectName
				onShortName = this.getObjectShortName(on);
				if (onShortName == null)
				{
					onShortName = onName;
				}
				if (matches(this.monitoredHistoricalObjectNames,onName))
				{

					// get all the MBeanInfo of the ObjectName.
					info = this.server.getMBeanInfo(on);
					attributes = info.getAttributes();

					if (attributes.length > 0)
					{
						int index = -1;
						for (int j = 0; j < attributes.length; j++)
						{
							attrName = attributes[j].getName();
							attrType = attributes[j].getType();
							
							sbAttributeName.setLength(0);
							sbAttributeName.append(onName);
							sbAttributeName.append(QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR);
							sbAttributeName.append(attrName);
	
							this.controllerAttribute.setName(sbAttributeName.toString());
							
							index = findMatchIndex(this.monitoredHistoricalAttributes, this.controllerAttribute.toString());
							
							if (index == -1)
							{
								continue;
							}
							attribute = (ControllerAttribute) this.monitoredHistoricalAttributes.get(index);
							try
							{
								value = this.server.getAttribute(on, attrName);
							}
							catch (final Exception e)
							{
//								AppLogger.getLogger().fatal(this.base.getNodeType() + ": Error collecting data for attribute : " + attrName, e); //$NON-NLS-1$
								continue;
							}
							if ((value != null) && value.getClass().equals(String.class)
									&& (((String) value).length() > attribute.getMaxLength()))
							{
								value = ((String) value).substring(0, attribute.getMaxLength());
							}
						
							this.base.setHistoricalValue(attribute, value);
						}
					}
				}
			}
		}
	}
	
	public void collectSummaryData() throws Exception
	{
		if (this.monitoredSummaryAttributes == null)
		{
//			System.out.println("No attriutes to monitor");
			return;
		}

		if (this.monitoredSummaryAttributesChanged)
		{
			this.monitoredSummaryObjectNames = this.getObjectNames(this.monitoredSummaryAttributes);
			this.monitoredSummaryAttributesChanged = false;
		}

		final Set objectNameSet = this.getAllRegisteredObjectNames();
		
		if (objectNameSet != null)
		{
			ObjectInstance instance = null;
			MBeanInfo info = null;
			ObjectName on = null;
			String onName = null;
			String onType = null;
			String onShortName = null;
			
			String attrName = null;
			String attrType = null;
			MBeanAttributeInfo[] attributes = null;
			Object value = null;
			ControllerAttribute attribute = null;
			final StringBuffer sbAttributeName = new StringBuffer();

			if (this.controllerAttribute == null)
			{
				this.controllerAttribute = new ControllerAttribute();
			}

			for (final Iterator iter = objectNameSet.iterator(); iter.hasNext();)
			{
				instance = (ObjectInstance) iter.next();
				
				on = instance.getObjectName();

				// get the name of the ObjectName (cannonical name)
				onName = on.getCanonicalName();

				onType = this.getObjectNameType(on);
				// get the short name of the ObjectName
				onShortName = this.getObjectShortName(on);
				if (onShortName == null)
				{
					onShortName = onName;
				}
				
				if (matches(this.monitoredSummaryObjectNames,onName))
				{
					info = this.server.getMBeanInfo(on);
					attributes = info.getAttributes();

					if (attributes.length > 0)
					{
						int index = -1;
						for (int j = 0; j < attributes.length; j++)
						{
							attrName = attributes[j].getName();
							attrType = attributes[j].getType();
							
							sbAttributeName.setLength(0);
							sbAttributeName.append(onName);
							sbAttributeName.append(QueryIOConstants.ATTRIBUTE_OBJECT_SEPERATOR);
							sbAttributeName.append(attrName);
	
							this.controllerAttribute.setName(sbAttributeName.toString());

							index = findMatchIndex(this.monitoredSummaryAttributes, this.controllerAttribute.toString());

							if (index == -1)
							{
								continue;
							}
							
							attribute = (ControllerAttribute) this.monitoredSummaryAttributes.get(index);
							try
							{
								value = this.server.getAttribute(on, attrName);
								
							}
							catch (final Exception e)
							{
								//AppLogger.getLogger().fatal("Error collecting data for attribute : " + attrName, e); //$NON-NLS-1$
								continue;
							}
							if ((value != null) && value.getClass().equals(String.class)
									&& (((String) value).length() > attribute.getMaxLength()))
							{
								value = ((String) value).substring(0, attribute.getMaxLength());
							}
						
							this.base.setSummaryValue(attribute, value);
						}
					}
				}
			}
		}
	}
	
	DefaultTreeModel getWritableMBeans() throws Exception
	{
		final Set objectNameSet = this.getAllRegisteredObjectNames();

		this.parentNode = new DefaultMutableTreeNode("Root Node");
		this.dynamicAttributes = new DefaultTreeModel(this.parentNode);

		if (objectNameSet != null)
		{
			String onType = null;
			String onName = null;
			String onShortName = null;
			ObjectInstance on = null;
			ObjectName name = null;

			for (final Iterator iter = objectNameSet.iterator(); iter.hasNext();)
			{
				// get the ObjectInstance
				on = (ObjectInstance) iter.next();
				// get the ObjectName from ObjectInstance
				name = on.getObjectName();
				// get the type of the ObjectName
				onType = this.getObjectNameType(name);
				// If type is null ignore the MBean
				if (onType == null)
				{
					continue;
				}

				// get the name of the ObjectName (cannonical name)
				onName = name.getCanonicalName();

				// get the short name of the ObjectName
				onShortName = this.getObjectShortName(name);
				if (onShortName == null)
				{
					onShortName = onName;
				}
				MBeanInfo info = null;
				try
				{
					info = this.server.getMBeanInfo(name);
				}
				catch (final Exception ex)
				{
//					AppLogger.getLogger().log(
//							AppLogger.getPriority(AppLogger.FATAL), "Canonical name: " + onName + " name: " + name
//							+ " short: " + onShortName + " type: " + onType, ex); //$NON-NLS-1$
//					base.getLogger().fatal("Canonical name: " + onName + " name: " + name
//							+ " short: " + onShortName + " type: " + onType, ex);
				}

				if (info == null)
				{
					continue;
				}
				// set the parent back to type of the object name
				this.setParentNode(onType, onType);
				// set the parent back to name of the object name
				this.setParentNode(onName, onName);
				// set the parent back to object type
				this.parentNode = (DefaultMutableTreeNode) this.parentNode.getParent();
				// set the parent back to root
				this.parentNode = (DefaultMutableTreeNode) this.parentNode.getParent();
			}

		}
		return this.dynamicAttributes;
	}
}
