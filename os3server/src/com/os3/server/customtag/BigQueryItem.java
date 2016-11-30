package com.os3.server.customtag;

import org.json.simple.JSONObject;

public class BigQueryItem {
	private String id;
	private String description;
	private JSONObject properties;

	public BigQueryItem(String queryId, String queryDesc, JSONObject properties) {
		this.id = queryId;
		this.description = queryDesc;
		this.properties = properties;
	}

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

	public JSONObject getProperties() {
		return properties;
	}

	public void setProperties(JSONObject properties) {
		this.properties = properties;
	}
}
