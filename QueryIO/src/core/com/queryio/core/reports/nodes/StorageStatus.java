package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;

public class StorageStatus extends AbstractExportableNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1214881068732127641L;
	private Timestamp currentTimestamp;

	protected StorageStatus(String exportedFileName, Timestamp currentTimestamp) {
		super("Storage Status Report", 0, "Storage Status Report", exportedFileName, "Storage Status Report");
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
		return "Storage Status Report  " + sdf.format(currentTimestamp.getTime());
	}
}
