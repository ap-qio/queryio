package com.queryio.plugin.extended.metadata;

import java.io.Serializable;

public class UserDefinedTag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2369422769680227592L;
	/**
	 * 
	 */
	private String key;
	private Object value;
	private Class tagClass;

	public Class getTagClass() {
		return tagClass;
	}

	public void setTagClass(Class tagClass) {
		this.tagClass = tagClass;
	}

	public UserDefinedTag(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UserDefinedTag) {
			UserDefinedTag tag = (UserDefinedTag) o;
			if (this.key != null && this.key.equals(tag.key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result += 31 * result + (this.key != null ? this.key.hashCode() : 0);
		return result;
	}

	public String toString() {
		return String.valueOf(this.key) + " : " + String.valueOf(this.value);
	}
}
