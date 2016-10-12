package com.queryio.core.datasources;

import java.io.Serializable;

public class DataConnection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1628430803699504866L;
	private String id;
	private short type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}

}
