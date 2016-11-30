package com.queryio.core.monitor.beans;

import java.util.ArrayList;

public class AttributeData {
	private String name;
	private String description;
	private ArrayList values;

	public AttributeData(String name, String description, ArrayList values) {
		this.name = name;
		this.values = values;
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setValues(ArrayList values) {
		this.values = values;
	}

	public ArrayList getValues() {
		return this.values;
	}
}
