package com.queryio.core.datasources;

import java.io.Serializable;

public class S3DataSource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 29670169834587436L;
	private String id;
	private String accessKey;
	private String secretAccessKey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}
}
