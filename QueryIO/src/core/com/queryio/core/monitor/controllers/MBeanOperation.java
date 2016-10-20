/*
 * @(#)  MBeanOperation.java
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
public class MBeanOperation implements Serializable
{
	private static final long serialVersionUID = 6296199064967717131L;
	/*
	 * private static String DATATYPE_STRING = "java.lang.String"; private
	 * static String DATATYPE_LONG = "long";
	 * 
	 * private static String DATATYPE_INT = "int"; private static String
	 * DATATYPE_FLOAT = "float"; private static String DATATYPE_DOULBE =
	 * "double";
	 */
	private String name;
	private MBeanParameter[] parameters;
	// private String[] parameterDataType;
	// private String[] parameterName;
	// private Object[] parameterValue;
	private String returnType;
	private Object returnValue;

	/**
	 * When the user clicks on any mbean then we have to create objects of all
	 * the operation in that mbean. The details of the operations will be
	 * displayed in the UI for the user and can modify the values of the
	 * parameters. On clicking the invoke action the retun value should be
	 * displayed in the UI.
	 * 
	 * @param name
	 * @param parameterType
	 * @param returnType
	 * @throws Exception
	 */
	public MBeanOperation(final String name, final Object[] parameters, final String returnType) throws Exception
	{
		this.name = name;
		this.parameters = new MBeanParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++)
		{
			this.parameters[i] = (MBeanParameter) parameters[i];
		}
		this.returnType = returnType;
	}

	/**
	 * This method creates the default object for each data type. String -
	 * create empty string int - Integer(0) long - Long(0)
	 * 
	 * @param string
	 * @return
	 */
	/*
	 * private Object getDefaultValue(String parameterDataType) throws Exception {
	 * Object returnValue = null; if(DATATYPE_STRING.equals(parameterDataType)) {
	 * returnValue = new String(); } else
	 * if(DATATYPE_INT.equals(parameterDataType)) { returnValue = new
	 * Integer(0); } else if(DATATYPE_LONG.equals(parameterDataType)) {
	 * returnValue = new Long(0); } else
	 * if(DATATYPE_FLOAT.equals(parameterDataType)) { returnValue = new
	 * Float(0); } else if(DATATYPE_DOULBE.equals(parameterDataType)) {
	 * returnValue = new Double(0); } else { // If the datatype is fully
	 * qualified datatype then create a new instance of the class. //
	 * returnValue =
	 * ClassLoader.getSystemClassLoader().loadClass(parameterDataType).newInstance(); }
	 * return returnValue; }
	 */
	/**
	 * @return
	 */
	public String getReturnType()
	{
		return this.returnType;
	}

	/**
	 * @return
	 */
	public Object getReturnValue()
	{
		return this.returnValue;
	}

	/**
	 * @param string
	 */
	public void setReturnType(final String string)
	{
		this.returnType = string;
	}

	/**
	 * @param object
	 */
	public void setReturnValue(final Object object)
	{
		this.returnValue = object;
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param string
	 */
	public void setName(final String string)
	{
		this.name = string;
	}

	/**
	 * @return
	 */
	public MBeanParameter[] getParameters()
	{
		return this.parameters;
	}

	/**
	 * @param parameters
	 */
	public void setParameters(final MBeanParameter[] parameters)
	{
		this.parameters = parameters;
	}

}
