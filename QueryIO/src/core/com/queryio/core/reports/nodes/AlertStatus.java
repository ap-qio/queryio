package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.core.monitor.dstruct.Alert;
import com.queryio.core.monitor.managers.AlertManager;

public class AlertStatus extends AbstractExportableNode {
	private long fromTime = 0;
	private long toTime = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2276515062360093725L;
	private Timestamp currentTimestamp;

	public AlertStatus(String exportedFileName, Timestamp currentTimestamp, Timestamp fromTime, Timestamp toTime) {
		super("Alert Status Report", 0, "Alert Status Report", exportedFileName, "Alert Status Report");
		this.currentTimestamp = currentTimestamp;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
		this.fromTime = fromTime.getTime();
		this.toTime = toTime.getTime();
	}

	@Override
	public IExportableItem[] getItems() {
		ArrayList arr = AlertManager.getAlertListInBetween(fromTime, toTime);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		IExportableItem[] items = new IExportableItem[] {
				new Label(0, 0, 100, 20, "Generated On: " + sdf.format(currentTimestamp.getTime())),
				new Label(0, 0, 100, 20, "Report Filter: "),
				new Label(0, 0, 100, 20, "Start Time: " + sdf.format(fromTime) + "  End Time: " + sdf.format(toTime)),
				// new Label(0, 0, 100, 20, " "),
				new Table(0, 0, 98, 50, getAlertTable(arr), getColumnSize(), "Alert Summary") };
		return items;
	}

	private int[] getColumnSize() {
		int result[] = new int[6];
		for (int i = 0; i < result.length; i++) {
			result[i] = 100 / result.length;
		}
		return result;
	}

	private ArrayList getColumnNames() {
		ArrayList columnName = new ArrayList();
		columnName.add("Rule ID");
		columnName.add("HostName");
		columnName.add("Description");
		columnName.add("Start Time");
		columnName.add("End Time");
		columnName.add("Severity");
		return columnName;
	}

	private TableModel getAlertTable(ArrayList arr) {
		ArrayList colNameList = getColumnNames();
		DefaultTableModel tableModel = new DefaultTableModel();
		for (int i = 0; i < colNameList.size(); i++) {
			tableModel.addColumn((String) colNameList.get(i));
		}
		for (int i = 0; i < arr.size(); i++) {
			Alert alert = (Alert) arr.get(i);
			String[] alertInfo = { alert.getRuleId(), alert.getHostname(), alert.getDescription(), alert.getStartTime(),
					alert.getEndTime(), alert.getSeverity() };
			tableModel.addRow(alertInfo);
		}
		return tableModel;
	}

	public String getHeaderText() {
		// SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm
		// a");
		return "Alert Status Report";
	}

}
