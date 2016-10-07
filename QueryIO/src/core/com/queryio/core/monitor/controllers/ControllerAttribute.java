package com.queryio.core.monitor.controllers;

import java.io.Serializable;

public class ControllerAttribute implements Serializable
{
	private static final long serialVersionUID = -6849794470754667710L;
	/* id of the controller to which this attribute belongs */
	private String controllerId;

	/* short name of the attribute */
	private String shortName;

	/* name of the attribute */
	private String name;

	/* display name of the attribute */
	private String displayName;

	/* column name to be created in the table for this attribute */
	private String columnName;

	/* should this attribute be monitored by default. */
	private boolean monitorByDefault = true;

	/* is this attribute chartable */
	private boolean chartable = true;

	/* attribute's data type */
	private int dataType;

	/* attribute's visibily type */
	private int visibilty;
	
	/*
	 * attribute's data unit used for displaying in the Y-Axis / Title of the
	 * charts
	 */
	private String dataUnit = "";

	/* attribute's mx length */
	private int maxLength;

	/* attribute's operator */
	private int operator;
	/* attribute's constant*/
	private double constant;
	
	/* is this attribute being monitored */
	private boolean toBeMonitored;

	/* All the get/set methods. */

	/**
	 * method isChartable
	 * 
	 * @return
	 */
	public boolean isChartable()
	{
		return this.chartable;
	}

	/**
	 * method getColumnName
	 * 
	 * @return
	 */
	public String getColumnName()
	{
		return this.columnName;
	}

	/**
	 * method getDataType
	 * 
	 * @return
	 */
	public int getDataType()
	{
		return this.dataType;
	}

	/**
	 * It returns if the attrbiute is transient data method isTransient
	 * 
	 * @return
	 */
	public boolean isTransient()
	{
		return false;
	}

	/**
	 * method getDataUnit
	 * 
	 * @return
	 */
	public String getDataUnit()
	{
		return this.dataUnit;
	}

	/**
	 * method isMonitorByDefault
	 * 
	 * @return
	 */
	public boolean isMonitorByDefault()
	{
		return this.monitorByDefault;
	}

	/**
	 * method getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return this.displayName;
	}

	/**
	 * method getShortName
	 * 
	 * @return
	 */
	public String getShortName()
	{
		return this.shortName;
	}

	/**
	 * getMaxLength
	 * 
	 * @return
	 */
	public int getMaxLength()
	{
		return this.maxLength;
	}

	/**
	 * Returns the toBeMonitored.
	 * 
	 * @return boolean
	 */
	public boolean isToBeMonitored()
	{
		return this.toBeMonitored;
	}

	/**
	 * method setChartable
	 * 
	 * @param b
	 */
	public void setChartable(final boolean b)
	{
		this.chartable = b;
	}

	/**
	 * method setColumnName
	 * 
	 * @param string
	 */
	public void setColumnName(final String string)
	{
		this.columnName = string;
	}

	/**
	 * method setDataType
	 * 
	 * @param i
	 */
	public void setDataType(final int i)
	{
		this.dataType = i;
	}

	/**
	 * method setDataUnit
	 * 
	 * @param string
	 */
	public void setDataUnit(final String string)
	{
		this.dataUnit = string;
	}

	/**
	 * method setMonitorByDefault
	 * 
	 * @param b
	 */
	public void setMonitorByDefault(final boolean b)
	{
		this.monitorByDefault = b;
	}

	/**
	 * method setName
	 * 
	 * @param string
	 */
	public void setName(final String string)
	{
		this.name = string;
	}

	/**
	 * method setShortName
	 * 
	 * @param string
	 */
	public void setShortName(final String string)
	{
		this.shortName = string;
	}

	/**
	 * setMaxLength
	 * 
	 * @param i
	 */
	public void setMaxLength(final int i)
	{
		this.maxLength = i;
	}

	/**
	 * Sets the toBeMonitored.
	 * 
	 * @param toBeMonitored
	 *            The toBeMonitored to set
	 */
	public void setToBeMonitored(final boolean toBeMonitored)
	{
		this.toBeMonitored = toBeMonitored;
	}

	/**
	 * setDisplayName
	 * 
	 * @param string
	 */
	public void setDisplayName(final String string)
	{
		this.displayName = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj)
	{
		// We have over-ridden this method as the ControllerAttribute is
		// identified with its name uniquely.
		if (obj instanceof ControllerAttribute)
		{
			final ControllerAttribute ca = (ControllerAttribute) obj;
			return this.getName().equals(ca.getName());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.getName().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * Returns the controllerId.
	 * 
	 * @return String
	 */
	public String getControllerId()
	{
		return this.controllerId;
	}

	/**
	 * Sets the controllerId.
	 * 
	 * @param controllerId
	 *            The controllerId to set
	 */
	public void setControllerId(final String controllerId)
	{
		this.controllerId = controllerId;
	}

	public int getOperator() 
	{
		return operator;
	}

	public void setOperator(int operator) 
	{
		this.operator = operator;
	}

	public double getConstant() 
	{
		return constant;
	}

	public void setConstant(double constant) 
	{
		this.constant = constant;
	}

	public int getVisibilty() {
		return visibilty;
	}
	
	public void setVisibilty(int visibilty) {
		this.visibilty = visibilty;
	}
}
