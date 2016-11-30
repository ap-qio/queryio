package com.queryio.core.monitor.beans;

import java.util.ArrayList;
import java.util.List;

public class MonitorData {
	private List attributeDataList;

	public MonitorData() {
		this.attributeDataList = new ArrayList();
	}

	public void addAttributeData(AttributeData attributeData) {
		this.attributeDataList.add(attributeData);
	}

	public AttributeData getAttributeData(int index) {
		return (AttributeData) this.attributeDataList.get(index);
	}

	public int getAttributeCount() {
		return this.attributeDataList.size();
	}

	public List getAttributeDataList() {
		return this.attributeDataList;
	}

	public void merge(MonitorData monitorData) {
		for (int i = 1; i < monitorData.getAttributeCount(); i++) {
			this.attributeDataList.add(monitorData.getAttributeData(i));
		}
	}
}
