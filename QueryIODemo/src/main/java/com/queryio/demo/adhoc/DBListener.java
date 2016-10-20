package com.queryio.demo.adhoc;

import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBListener {
	private static final Log LOG = LogFactory.getLog(DBListener.class);
	
	private Connection connection;
	private String tableName;
	private int maxBatchSize;
	private int currentBatchSize = 0;
	private PreparedStatement pst = null;
	public DBListener(Connection connection, String tableName, int maxBatchSize){
		this.connection = connection;
		this.tableName = tableName;
		this.maxBatchSize = maxBatchSize;
	}
	public void close() throws SQLException{
		if (pst != null){
			try{
				if(currentBatchSize > 0)
					pst.executeBatch();
			}finally{
				try {
					pst.close();
				} catch (SQLException e) {
					LOG.fatal("Error Closing PreparedStatement", e);
				}	
			}
		}
		if(connection != null){
			connection.close();
		}
	}
	public void createStatement(Map<Integer, String> columns) throws SQLException{
		
		StringBuffer query = new StringBuffer();
		StringBuffer valueBuf = new StringBuffer();
		
		query.append("INSERT INTO ");
		query.append(tableName).append(" (");
		
		Iterator<String> it = columns.values().iterator();
		int i = 0;
		while (it.hasNext()){
			if(i != 0){
				query.append(",");
				valueBuf.append(",");
			}
			String col = it.next();
			query.append(col);
			valueBuf.append("?");
			i ++;
		}
		
		query.append(") VALUES (").append(valueBuf.toString()).append(")");
		
		LOG.info("query: " + query.toString());
		
		if (pst == null)
			pst = connection.prepareStatement(query.toString());
	}
	
	@SuppressWarnings("unchecked")
	public void insertAdHocEntry(AdHocEntry entry) throws SQLException{
		
		Iterator<Integer> it = entry.getColumns().keySet().iterator();
		int i = 0;
		while(it.hasNext()){
			Integer index = it.next();
			pst.setObject(++i, entry.getValues().get(index), getDataType(entry.getColumnTypes().get(index)));
		}
		
		pst.addBatch();
		currentBatchSize++;
		
		if (currentBatchSize % maxBatchSize == 0)
		{
			try
			{
				pst.executeBatch();
			}
			catch (Exception e)
			{
				LOG.fatal("Exception in executeBatch: ", e);
				if (e instanceof BatchUpdateException)
				{
					throw ((BatchUpdateException) e).getNextException();
				}
			}
			pst.clearBatch();
			currentBatchSize = 0;
		}
	}
	
	private int getDataType(Class dataType)
	{
		try
		{
			if (Integer.class.equals(dataType))
				return Types.INTEGER;
			else if (Short.class.equals(dataType))
				return Types.SMALLINT;
			else if (Long.class.equals(dataType))
				return Types.BIGINT;
			else if (Float.class.equals(dataType))
				return Types.FLOAT;
			else if (Double.class.equals(dataType))
				return Types.DOUBLE;
			else if (Blob.class.equals(dataType))
				return Types.BLOB;
			else if (Boolean.class.equals(dataType))
				return Types.BOOLEAN;
			else if (Timestamp.class.equals(dataType))
				return Types.TIMESTAMP;
			else
				return Types.VARCHAR;
		}
		catch (Exception e)
		{
			return Types.VARCHAR;
		}
	}
}