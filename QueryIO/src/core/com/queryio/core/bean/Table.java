package com.queryio.core.bean;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

public class Table {

	private JSONObject properties;

	private String description;

	private String id;

	private String queryId;

	public JSONObject getProperties() {
		return properties;
	}

	public void setProperties(JSONObject properties) {
		this.properties = properties;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
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

}
