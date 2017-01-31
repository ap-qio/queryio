package com.queryio.core.bean;

import org.codehaus.jackson.map.ObjectMapper;

public class Query {

	private String id;

	private String description;

	private String username;

	private String dbname;

	private String namenodeId;

	private String properties;
	
	private String selectedCols;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getNamenodeId() {
		return namenodeId;
	}

	public void setNamenodeId(String namenodeId) {
		this.namenodeId = namenodeId;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSelectedCols() {
		return selectedCols;
	}

	public void setSelectedCols(String selectedCols) {
		this.selectedCols = selectedCols;
	}

}
