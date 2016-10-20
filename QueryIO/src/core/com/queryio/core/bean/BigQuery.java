package com.queryio.core.bean;

import org.json.simple.JSONObject;

public class BigQuery {
	private JSONObject properties;
	
	public BigQuery(JSONObject properties2){
		this.properties = properties2;
	}
	public JSONObject getProperties() {
		return properties;
	}
	public void setProperties(JSONObject properties) {
		this.properties = properties;
	}
	public String toString(){
		return properties.toJSONString();
	}
}
