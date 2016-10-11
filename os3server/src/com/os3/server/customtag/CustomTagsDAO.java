package com.os3.server.customtag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.util.StaticUtilities;

public class CustomTagsDAO {
	protected static final Logger logger = Logger
			.getLogger(CustomTagsDAO.class);

	public static TableModel getBigQueryResults(Connection connection,
			String query) {
		logger.debug("Query: " + query);
		
		DefaultTableModel tableModel = null;
		ArrayList colNames = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1; i<=rsmd.getColumnCount(); i++){
				colNames.add(StaticUtilities.toCamelCase(rsmd.getColumnName(i)));
			}
			
			String[] tableModelColumns = (String[]) colNames.toArray(new String[0]);
			
			tableModel = new DefaultTableModel(tableModelColumns, 0);
			
			Object[] object;
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
			while(rs.next())
			{
				int index=0;
				object = new Object[tableModelColumns.length];
				
				for(int i=1; i<=rsmd.getColumnCount(); i++)
				{
					if(rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_ACCESSTIME)||rsmd.getColumnName(i).equals(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME)){
						object[index++]=dateFormatter.format(rs.getObject(i));
						continue;
					}
					object[index++] = rs.getObject(i);
				}
				tableModel.addRow(object);
			}
		} catch(Exception e){
			logger.fatal(e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
			}
		}
		return tableModel;
	}
}