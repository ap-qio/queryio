package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.table.TableModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.core.monitor.managers.SummaryManager;

public class StorageForecast extends AbstractExportableNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7972674803530490788L;
	private Timestamp endTimestamp;

	public StorageForecast(String exportedFileName, Timestamp endTimestamp) {
		super("Storage Forecast Report", 0, "Storage Forecast Report", exportedFileName, "Storage Forecast Report");
		this.endTimestamp = endTimestamp;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}

	@Override
	public IExportableItem[] getItems() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");

		TableModel tableModel = SummaryManager.getStorageForecastReportTableModel();
		IExportableItem[] items = new IExportableItem[] {
				new Label(0, 0, 100, 20, " Generated On: " + sdf.format(new java.util.Date())),
				new Label(0, 0, 100, 20, " Report Filter: "),
				new Label(0, 0, 100, 20, " From Host Added to " + sdf.format(endTimestamp)),
				// new Label(0, 0, 100, 20, " "),
				new Table(0, 0, 99, 50, tableModel, new int[] { 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 },
						"Storage Forecast") };
		return items;
	}

	@Override
	public String getHeaderText() {
		return "Storage Forecast Report";
	}
}
