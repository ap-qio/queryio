package com.queryio.core.monitor.beans;

public class Parameter {
	private Object name;
	private Object value;

	public Parameter(Object name, Object value) {
		this.name = (String) name;
		this.value = value;
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Name: " + String.valueOf(name) + " Value: " + String.valueOf(value);
	}
}
