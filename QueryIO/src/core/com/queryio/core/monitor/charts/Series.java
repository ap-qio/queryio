package com.queryio.core.monitor.charts;

import java.io.Serializable;
import java.util.ArrayList;

public class Series implements Serializable {
	private static final long serialVersionUID = 698492612023089877L;
	private String name;
	private ArrayList values;

	public Series(String name, ArrayList values) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getValues() {
		return values;
	}

	public void setValues(ArrayList values) {
		this.values = values;
	}
}
