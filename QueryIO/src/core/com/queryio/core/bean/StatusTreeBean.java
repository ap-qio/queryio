package com.queryio.core.bean;

import java.util.ArrayList;

public class StatusTreeBean {
	private String name = null;
	private ArrayList dashboardCells = new ArrayList();
	private ArrayList childs = new ArrayList();
	private String nodeType;

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public void addChilds(final StatusTreeBean bean) {
		if (!this.childs.contains(bean)) {
			this.childs.add(bean);
		}
	}

	public ArrayList getChilds() {
		return childs;
	}

	public void setChilds(ArrayList childs) {
		this.childs = childs;
	}

	public void addDashboardCell(final DashboardCell dbCell) {
		this.dashboardCells.add(dbCell);
	}

	public ArrayList getDashboardCells() {
		return this.dashboardCells;
	}

	public void setDashboardCells(final ArrayList dashboardCells) {
		this.dashboardCells = dashboardCells;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
