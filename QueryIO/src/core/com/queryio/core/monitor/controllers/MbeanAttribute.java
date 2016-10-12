/*
 * @(#)  MbeanAttribute.java
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

import java.io.Serializable;

/**
 * @author Exceed Consultancy Services
 * 
 */
public class MbeanAttribute implements Serializable
{
	private static final long serialVersionUID = -1619548766130922177L;
	public static int BOOLEAN_DATATYPE = 1;
	public static int NONBOOLEAN_PRIMITIVE_DATATYPE = 2;
	public static int NONPRIMITIVE_DATATYPE = 3;
	public static String READ_ONLY = "ReadOnly";
	public static String WRITE_ONLY = "WriteOnly";
	public static String READWRITE = "ReadWrite";

	private String name;
	// private Object previousValue;
	private String dataType;
	private Object value;
	private String accessPermission;
	private boolean editable;
	private int dataTypeSupported;

	static String INTEGER = "java.lang.Integer";
	static String PRIMITIVE_INT = "int";
	static String DOUBLE = "java.lang.Double";
	static String PRIMITIVE_DOUBLE = "double";
	static String PRIMITIVE_FLOAT = "float";
	static String FLOAT = "java.lang.float";
	static String LONG = "java.lang.Long";
	static String PRIMITIVE_LONG = "long";
	static String PRIMITIVE_BOOLEAN = "boolean";
	static String BOOLEAN = "java.lang.Boolean";
	static String STRING = "java.lang.String";

	public static final String[] supportedDataType = { "int", "double", "long", "float", "String", "java.lang.Integer",
			"java.lang.Double", "java.lang.Float", "java.lang.String", "java.lang.Long" };

	/**
	 * 
	 * @param name
	 * @param dataType
	 * @param value
	 */
	public MbeanAttribute(final String name, final String dataType, final Object value, final String accessPermission)
	{
		this.name = name;
		this.dataType = dataType;
		if (this.isSupported(dataType))
		{
			this.editable = true;
		}
		else
		{
			this.editable = false;
		}
		this.value = value;
		this.accessPermission = accessPermission;
	}

	/**
	 * @param dataType
	 * @return
	 */
	private boolean isSupported(final String dataType)
	{
		if (dataType == null)
		{
			return false;
		}
		if (dataType.equals("boolean") || dataType.equals("java.lang.Boolean"))
		{
			this.dataTypeSupported = BOOLEAN_DATATYPE;
			return true;
		}
		this.dataTypeSupported = NONPRIMITIVE_DATATYPE;
		for (int i = 0; i < supportedDataType.length; i++)
		{
			if (dataType.equals(supportedDataType[i]))
			{
				this.dataTypeSupported = NONBOOLEAN_PRIMITIVE_DATATYPE;
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getDataType()
	{
		return this.dataType;
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return
	 */
	public Object getValue()
	{
		if (this.value != null)
		{
			return this.value.toString();
		}
		return null;
	}

	/**
	 * @param string
	 */
	public void setDataType(final String string)
	{
		this.dataType = string;
	}

	/**
	 * @param string
	 */
	public void setName(final String string)
	{
		this.name = string;
	}

	/**
	 * @param string
	 */
	public void setValue(final Object string)
	{
		this.value = string;
	}

	/**
	 * @return
	 */
	public boolean isEditable()
	{
		return this.editable;
	}

	public boolean isWritable()
	{
		return WRITE_ONLY.equals(this.accessPermission) || READWRITE.equals(this.accessPermission);
	}

	/**
	 * @param b
	 */
	public void setEditable(final boolean b)
	{
		this.editable = b;
	}

	public static Object getReturnObject(final String dataType, final Object value)
	{
		if (PRIMITIVE_INT.equalsIgnoreCase(dataType) && !INTEGER.equals(dataType))
		{
			return new Integer((String) value);
		}
		if (PRIMITIVE_BOOLEAN.equalsIgnoreCase(dataType) && !BOOLEAN.equals(dataType))
		{
			return new Boolean((String) value);
		}
		if (PRIMITIVE_LONG.equalsIgnoreCase(dataType) && !LONG.equals(dataType))
		{
			return new Long((String) value);
		}
		if (PRIMITIVE_FLOAT.equalsIgnoreCase(dataType) && !FLOAT.equals(dataType))
		{
			return new Float((String) value);
		}
		if (PRIMITIVE_DOUBLE.equalsIgnoreCase(dataType) && !DOUBLE.equals(dataType))
		{
			return new Double((String) value);
		}
		return value;
	}

	public Object getReturnValue()
	{
		return MbeanAttribute.getReturnObject(this.dataType, this.value);
	}

	/**
	 * @return
	 */
	public int getDataTypeSupported()
	{
		return this.dataTypeSupported;
	}

	/**
	 * @param i
	 */
	public void setDataTypeSupported(final int i)
	{
		this.dataTypeSupported = i;
	}

	/**
	 * @return
	 */
	public String getAccessPermission()
	{
		return this.accessPermission;
	}

	/**
	 * @param string
	 */
	public void setAccessPermission(final String string)
	{
		this.accessPermission = string;
	}

	public static String getPermission(boolean isRead, boolean isWrite)
	{
		if (isRead && isWrite)
		{
			return READWRITE;
		}
		else if (!isRead && isWrite)
		{
			return WRITE_ONLY;
		}
		else if (isRead && !isWrite)
		{
			return READ_ONLY;
		}
		return null;
	}

	public static Class loadPrimitiveClass(final String className)
	{
		if (className.equals(PRIMITIVE_INT))
		{
			return int.class;
		}
		else if (className.equals(PRIMITIVE_BOOLEAN))
		{
			return boolean.class;
		}
		else if (className.equals(PRIMITIVE_DOUBLE))
		{
			return double.class;
		}
		else if (className.equals(PRIMITIVE_FLOAT))
		{
			return float.class;
		}
		else if (className.equals(PRIMITIVE_LONG))
		{
			return long.class;
		}
		else
		{
			return null;
		}
	}
}
