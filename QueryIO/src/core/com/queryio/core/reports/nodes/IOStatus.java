package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;

public class IOStatus extends AbstractExportableNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9133956418982545274L;
	private Timestamp currentTimestamp; 
	protected IOStatus(String exportedFileName, Timestamp currentTimestamp) {
		super("I/O Status Report", 0, "I/O Status Report", exportedFileName, "I/O Status Report");
		this.currentTimestamp = currentTimestamp;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}

	@Override
	public IExportableItem[] getItems() {
		IExportableItem[] items = null;
		return items;
	}

	@Override
	public String getHeaderText() {		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		return "I/O Status Report  " + sdf.format(currentTimestamp.getTime());
	}
}
