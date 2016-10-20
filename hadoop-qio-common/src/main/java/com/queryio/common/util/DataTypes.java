/*
 * @(#)  DataTypes.java
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
package com.queryio.common.util;

import java.math.BigInteger;
import java.sql.Date;

/**
 * 
 * 
 * @author Exceed Consultancy Services
 */
@SuppressWarnings({"PMD.AvoidUsingShortType" , "PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal"})
public class DataTypes
{
	/* any object having this data type is not of defined type */
	public static final int UNDEFINED = -1;
	/* any object having this data type is an integer */
	public static final int INTEGER = 0;
	/* any object having this data type is a short */
	public static final int SHORT = 1;
	/* any object having this data type is a long */
	public static final int LONG = 2;
	/* any object having this data type is a float */
	public static final int FLOAT = 3;
	/* any object having this data type is a double */
	public static final int DOUBLE = 4;
	/* any object having this data type is a boolean */
	public static final int BOOLEAN = 5;
	/* any object having this data type is a char */
	public static final int CHAR = 6;
	/* any object having this data type is a byte */
	public static final int BYTE = 7;
	/* any object having this data type is a String */
	public static final int STRING = 8;
	/* any object having this data type is an object of a BigInteger */
	public static final int BIGINTEGER = 9;
	/*
	 * any object having this data type is a datetime & is an object of a
	 * TimeStamp
	 */
	public static final int DATETIME = 10;
	/*
	 * any object having this data type is a transient data & is an object of
	 * TransientData
	 */
	public static final int TRANSIENT = 11;

	/* string representation of undefined data type */
	private static final String STR_UNDEFINED = "undefined"; //$NON-NLS-1$
	/* string representation of integer data type */
	private static final String STR_INTEGER = "integer"; //$NON-NLS-1$
	private static final String STR_INT = "int"; //$NON-NLS-1$
	/* string representation of short data type */
	private static final String STR_SHORT = "short"; //$NON-NLS-1$
	/* string representation of long data type */
	private static final String STR_LONG = "long"; //$NON-NLS-1$
	/* string representation of float data type */
	private static final String STR_FLOAT = "float"; //$NON-NLS-1$
	/* string representation of double data type */
	private static final String STR_DOUBLE = "double"; //$NON-NLS-1$
	/* string representation of boolean data type */
	private static final String STR_BOOLEAN = "boolean"; //$NON-NLS-1$
	/* string representation of char data type */
	private static final String STR_CHAR = "char"; //$NON-NLS-1$
	/* string representation of byte data type */
	private static final String STR_BYTE = "byte"; //$NON-NLS-1$
	/* string representation of string data type */
	private static final String STR_STRING = "string"; //$NON-NLS-1$
	/* string representation of biginteger data type */
	private static final String STR_BIGINTEGER = "biginteger"; //$NON-NLS-1$
	/* string representation of datetime data type */
	private static final String STR_DATETIME = "datetime"; //$NON-NLS-1$
	/* string representation of transient data type */
	private static final String STR_TRANSIENT = "transient"; //$NON-NLS-1$

	/*
	 * private default constructor. This class should have only static methods
	 * in it. And there should be no object of DataTypes in the JVM.
	 */
	private DataTypes()
	{
		// DO NOTHING
	}

	/**
	 * This method resloves the string representation of the data type and
	 * returns appropriate data type constant. It also resolves the data type if
	 * the class name of the object is provided. e.g.
	 * resolveDataType("java.lang.Integer") will return DataTypes.INTEGER
	 * 
	 * @param dataType
	 * @return
	 */
	public static int resolveDataType(final String dataType)
	{
		if (dataType != null)
		{
			final int type = resolvePrimitiveDataType(dataType, false);
			if (type != DataTypes.UNDEFINED)
			{
				return type;
			}
			else if (Integer.class.getName().equals(dataType) || dataType.equals(STR_INT))
			{
				return DataTypes.INTEGER;
			}
			else if (Short.class.getName().equals(dataType))
			{
				return DataTypes.SHORT;
			}
			else if (Long.class.getName().equals(dataType))
			{
				return DataTypes.LONG;
			}
			else if (Float.class.getName().equals(dataType))
			{
				return DataTypes.FLOAT;
			}
			else if (Double.class.getName().equals(dataType))
			{
				return DataTypes.DOUBLE;
			}
			else if (Boolean.class.getName().equals(dataType))
			{
				return DataTypes.BOOLEAN;
			}
			else if (Character.class.getName().equals(dataType))
			{
				return DataTypes.CHAR;
			}
			else if (Byte.class.getName().equals(dataType))
			{
				return DataTypes.BYTE;
			}
			else if (String.class.getName().equals(dataType))
			{
				return DataTypes.STRING;
			}
		}
		return DataTypes.UNDEFINED;
	}

	/**
	 * This method converts any object to the specified data type. It may throw
	 * exception if it is unalbe to convert to the required data type.
	 * 
	 * @param value
	 * @param dataType
	 * @return
	 */
	public static Object convertToDataType(final Object value, final int dataType)
	{
		if (value == null)
		{
			return value;
		}
		final String valStr = value.toString();
		switch (dataType)
		{
			case INTEGER:
				return new Integer(valStr);
			case SHORT:
				return new Short(valStr);
			case LONG:
				return new Long(valStr);
			case FLOAT:
				return new Float(valStr);
			case DOUBLE:
				return new Double(valStr);
			case BOOLEAN:
				return new Boolean(valStr);
			case CHAR:
				return new Character(valStr.length() > 0 ? valStr.charAt(0) : '\0');
			case BYTE:
				return new Byte(valStr);
			case STRING:
				return valStr;
			case BIGINTEGER:
				return new BigInteger(valStr);
			case DATETIME:
				return new Date(Long.parseLong(valStr));
			case TRANSIENT:
			case UNDEFINED:
			default:
				return value;
		}
	}

	public static Object getDefaultObject(final int dataType)
	{
		final Object value = null;
		switch (dataType)
		{
			case INTEGER:
				return new Integer(0);
			case SHORT:
				return new Short((short) 0);
			case LONG:
				return new Long(0L);
			case FLOAT:
				return new Float(0.0f);
			case DOUBLE:
				return new Double(0.0);
			case BOOLEAN:
				return new Boolean(false);
			case CHAR:
				return new Character(' '); // a space
			case BYTE:
				return new Byte((byte) 0);
			case STRING:
				return ""; //
			default:
				return value;
		}
	}

	/**
	 * This method returns the string representation of the specified data type
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getStringRepresentation(final int dataType)
	{
		switch (dataType)
		{
			case INTEGER:
				return STR_INTEGER;
			case LONG:
				return STR_LONG;
			case FLOAT:
				return STR_FLOAT;
			case DOUBLE:
				return STR_DOUBLE;
			case STRING:
				return STR_STRING;
			case BIGINTEGER:
				return STR_BIGINTEGER;
			case BOOLEAN:
				return STR_BOOLEAN;
			case DATETIME:
				return STR_DATETIME;
			case TRANSIENT:
				return STR_TRANSIENT;
			case BYTE:
				return STR_BYTE;
			case SHORT:
				return STR_SHORT;
			case CHAR:
				return STR_CHAR;
			case UNDEFINED:
			default:
				return STR_UNDEFINED;
		}
	}

	// It can be used to decide whether an attribute is chartable or
	// non-chartable
	/**
	 * returns true if the data type is numeric else returns false.
	 * 
	 * @param dataType
	 * @return
	 */
	public static boolean isNumeric(final int dataType)
	{
		return isNumeric(dataType, false);
	}

	/**
	 * returns true if the data type is numeric else returns false.
	 * 
	 * @param dataType
	 * @return
	 */
	public static boolean isNumeric(final int dataType, boolean checkForIntegral)
	{
		switch (dataType)
		{
			case INTEGER:
			case LONG:
			case BYTE:
			case SHORT:
			case CHAR:
			{
				return true;
			}
			case FLOAT:
			case DOUBLE:
			case BIGINTEGER:
			{
				return !checkForIntegral;
			}
			case BOOLEAN:
			case TRANSIENT:
			case DATETIME:
			case UNDEFINED:
			default:
			{
				return false;
			}
		}
	}

	/**
	 * This method resloves the string representation of the data type and
	 * returns appropriate data type constant.
	 * 
	 * @param dataType
	 * @return
	 */
	public static int resolvePrimitiveDataType(String dataType, boolean bStrict)
	{
		if (dataType != null)
		{
			dataType = dataType.trim();
			if (STR_INT.equals(dataType))
			{
				return INTEGER;
			}
			else if (STR_SHORT.equals(dataType))
			{
				return SHORT;
			}
			else if (STR_LONG.equals(dataType))
			{
				return LONG;
			}
			else if (STR_FLOAT.equals(dataType))
			{
				return FLOAT;
			}
			if (STR_DOUBLE.equals(dataType))
			{
				return DOUBLE;
			}
			else if (STR_BOOLEAN.equals(dataType))
			{
				return BOOLEAN;
			}
			else if (STR_CHAR.equals(dataType))
			{
				return CHAR;
			}
			else if (STR_BYTE.equals(dataType))
			{
				return BYTE;
			}
			if (!bStrict)
			{
				if (STR_INTEGER.equals(dataType))
				{
					return INTEGER;
				}
				else if (STR_STRING.equals(dataType))
				{
					return STRING;
				}
				else if (STR_BIGINTEGER.equals(dataType))
				{
					return BIGINTEGER;
				}
				else if (STR_DATETIME.equals(dataType))
				{
					return DATETIME;
				}
				else if (STR_TRANSIENT.equals(dataType))
				{
					return TRANSIENT;
				}
			}
		}
		return UNDEFINED;
	}
}
