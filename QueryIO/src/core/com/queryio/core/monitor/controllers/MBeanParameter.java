/*
 * Created on Jun 3, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.queryio.core.monitor.controllers;

import java.io.Serializable;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MBeanParameter implements Serializable {
	private static final long serialVersionUID = -3237401669649842165L;
	private String name;
	private String dataType;
	private Object value;
	private boolean editable;

	public MBeanParameter(final String name, final String dataType) {
		this.name = name;
		this.dataType = dataType;
		if (this.isSupported(dataType)) {
			this.editable = true;
		} else {
			this.editable = false;
		}
	}

	private boolean isSupported(final String dataType) {
		for (int i = 0; i < MbeanAttribute.SUPPORTEDDATATYPE.length; i++) {
			if (dataType.equals(MbeanAttribute.SUPPORTEDDATATYPE[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getDataType() {
		return this.dataType;
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * @param string
	 */
	public void setDataType(final String string) {
		this.dataType = string;
	}

	/**
	 * @param string
	 */
	public void setName(final String string) {
		this.name = string;
	}

	/**
	 * @param object
	 */
	public void setValue(final Object object) {
		this.value = object;
	}

	public Object getReturnValue() {
		return MbeanAttribute.getReturnObject(this.dataType, this.value);
	}

	/**
	 * @return boolean
	 */
	public boolean isEditable() {
		return this.editable;
	}

	/**
	 * @param b
	 *            void
	 */
	public void setEditable(final boolean b) {
		this.editable = b;
	}

}
