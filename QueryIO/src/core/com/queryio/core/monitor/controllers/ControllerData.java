package com.queryio.core.monitor.controllers;

import java.util.ArrayList;

/**
 * This is the dataStructure responsible for holding one set of Controllers
 * data. This dstruct even has got the timeStamp when the data was retreived and
 * it even keeps the refrence of the columnNames for the Controller.
 * 
 * @author Exceed Consultancy Services
 */
public class ControllerData
{
	private ArrayList columnNames;

	/* time stamp when the data was fetched */
	private long timeStamp;
	
	/*
	 * values fetched from the device. They should match 1-to-1 with the column
	 * names. If the values for a particular column is not retreived then that
	 * would be set to null.
	 */
	private ArrayList values;

	private boolean sampleData;

	private boolean saveAllValues = true;
	/**
	 * It will clear all the column names, values. It will set timeStamp to -1
	 */
	public void clear()
	{
		if (this.columnNames != null)
		{
			this.columnNames.clear();
		}
		if (this.values != null)
		{
			this.values.clear();
		}
	}

	/**
	 * default constructor
	 */
	public ControllerData()
	{
		this.clear();
	}

	public ArrayList getColumnNames()
	{
		return this.columnNames;
	}

	public ArrayList getValues()
	{
		return this.values;
	}

	public void setColumnNames(final ArrayList list)
	{
		this.columnNames = list;
	}

	public void setValues(final ArrayList list)
	{
		this.values = list;
	}

	public void addColumnValue(final String columnName, final Object value)
	{
		if (!this.canAdd(columnName, value))
		{
			return;
		}

		this.init();

		this.columnNames.add(columnName);
		this.values.add(value);
	}

	/**
	 * This method can be used to update the value of an already existing
	 * column. If the column name is not found then both the column name and the
	 * value will be stored in the respective ArrayList's.
	 * 
	 * @param columnName
	 * @param value
	 */
	public void setValue(final String columnName, Object value)
	{
		if (!this.canAdd(columnName, value))
		{
			return;
		}

		this.init();

		// find whether the column name is already present if yes then just set
		// the new value
		// else add the new column name and the value in the respective
		// ArraList's
		final int index = this.columnNames.indexOf(columnName);
		if (index == -1)
		{
			if ((value != null) && value.getClass().equals(String.class))
			{
				value = searchAndReplace((String) value, "\\", "\\\\");
			}
			this.addColumnValue(columnName, value);
		}
		else
		{
			this.values.set(index, value);
		}
	}

	public static String searchAndReplace(String originalStr, final String sWordToReplace, final String sReplaceWith)
	{
		if (originalStr == null)
		{
			return null;
		}
		String sLower = originalStr.toLowerCase();
		final String sWR = sWordToReplace.toLowerCase();
		int i = 0;
		do
		{
			i = sLower.indexOf(sWR, i);
			if (i != -1)
			{
				originalStr = originalStr.substring(0, i) + sReplaceWith
						+ originalStr.substring(i + sWordToReplace.length());
				i += sReplaceWith.length();
				sLower = originalStr.toLowerCase();
			}
		}
		while (i != -1);
		return originalStr;
	}
	
	/**
	 * returns the value of desired column. returns null if there are no columns
	 * or the columName is not present in the controller data.
	 * 
	 * @param columnName
	 * @return
	 */
	public Object getValue(final String columnName)
	{
		if (this.columnNames != null)
		{
			final int index = this.columnNames.indexOf(columnName);
			if (index != -1)
			{
				return this.values.get(index);
			}
		}
		return null;
	}

	/**
	 * initializes the column names and values ArrayLists if the ArrayLists are
	 * null
	 * 
	 */
	private void init()
	{
		if (this.columnNames == null)
		{
			this.columnNames = new ArrayList();
			this.values = new ArrayList();
		}
	}

	/**
	 * Checks for the null values before adding column / value in the
	 * ControllerData
	 * 
	 * @param columnName
	 * @param value
	 * @return
	 */
	private boolean canAdd(final String columnName, final Object value)
	{
		return ((columnName != null) && (value != null));
	}

	/**
	 * The method checks whether values is null or not This is used for checking
	 * the values returned in SNMP Controller
	 * 
	 * @return
	 */
	public boolean isNull()
	{
		if (this.values == null)
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("ControllerData:");
		sbToString.append(' ');

		final int size = this.columnNames.size();
		for (int i = 0; i < size; i++)
		{
			sbToString.append("{");
			sbToString.append(this.columnNames.get(i));
			sbToString.append(",");
			sbToString.append(this.values.get(i));
			sbToString.append("} ");
		}

		return sbToString.toString();
	}

	public boolean isSampleData() 
	{
		return sampleData;
	}

	public void setSampleData(boolean sampleData) 
	{
		this.sampleData = sampleData;
	}

	public void setSaveAllValues(boolean saveAllValues)
	{
		this.saveAllValues = saveAllValues;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}

