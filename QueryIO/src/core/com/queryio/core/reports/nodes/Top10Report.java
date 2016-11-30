package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;

public class Top10Report extends AbstractExportableNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8361634572808314612L;
	private Timestamp currentTimestamp;

	protected Top10Report(String exportedFileName, Timestamp currentTimestamp) {
		super("Top 10 Report", 0, "Top 10 Report", exportedFileName, "Top 10 Report");
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
		return "Top 10 Report  " + sdf.format(currentTimestamp.getTime());
	}
}
