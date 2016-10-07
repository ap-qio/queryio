package com.queryio.demo.mr.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;

public class CSVParser {
	private static final Log LOG = LogFactory.getLog(CSVParser.class);
	private static final String COL_TAG_VALUES_FILEPATH = "FILEPATH";
	
	ArrayList<String> columns = null;
	CSVEntry entry = new CSVEntry();
	StringTokenizer token = null; 
	
	Connection connection = null;
	PreparedStatement PST = null;
	String tableName = null;
	int maxBatchSize; 
	FileStatus fileStatus = null;
	
	CSVExpressions csvExpressions;
	HashMap<String, Integer> colMap = new HashMap<String, Integer>();
	int currentBatchSize = 0;
	
	public CSVParser(CSVExpressions csvExpressions, Connection connection, String tableName, FileStatus fileStatus, int maxBatchSize){
		this.csvExpressions = csvExpressions;
		this.connection = connection;
		this.fileStatus = fileStatus;
		this.tableName = tableName;
		this.maxBatchSize = maxBatchSize;
	}
	
	public boolean evaluateExpression(Expression expression, String value){
		return expression.evaluate(value);
	}
	
	public boolean evaluateEntry(CSVEntry entry){
		boolean valid = true;
		
		ArrayList<Boolean> expResults = new ArrayList<Boolean>();
		
		Expression expression;
		for(int i=0; i< csvExpressions.expressions.size(); i++){
			expression = csvExpressions.expressions.get(i);
			boolean result = expression.evaluate(entry.getValues().get(colMap.get(expression.getColumn())));
			expResults.add(result);
		}
		
		if(expResults.size()>0){
			valid = evaluateBooleanExpression(expResults);
		} else {
			valid = true;
		}
		
		return valid;
	}
	
	public boolean evaluateBooleanExpression(ArrayList<Boolean> expResults){
		JexlEngine jexl = new JexlEngine();
	    jexl.setSilent(true);
	    jexl.setLenient(true);

	    String exp = "";
	    
	    for(int i=0; i<expResults.size(); i++){
	    	if(i!=0){
	    		exp += csvExpressions.booleanExpressions.get(i-1);
	    	}
	    	
	    	exp += " ";
	    	exp += "a" + i;
	    	exp += " ";
	    }
	    
	    org.apache.commons.jexl2.Expression expression = jexl.createExpression(exp);
	    JexlContext jexlContext = new MapContext();
	    
	    for(int i=0; i<expResults.size(); i++){
	    	 jexlContext.set("a" + i, expResults.get(i));
	    }

	    return (Boolean)expression.evaluate(jexlContext);
	}
	
	public void parse(InputStream is) throws IOException, InterruptedException, SQLException {
		
		BufferedReader rd = null;
		String str;
		try {
			InputStreamReader in = new InputStreamReader(is);

			rd = new BufferedReader(in);

			while ((str = rd.readLine()) != null) {
				this.parseLine(str);
				entry.clearValues();
			}
		} catch (Exception e) {
			LOG.fatal("Exception: " + e.getLocalizedMessage(), e);
			throw new IOException(e);
		} finally {
			
			if (PST != null){
				try{
					if(currentBatchSize > 0)
						PST.executeBatch();
				}finally{
					try {
						PST.close();
					} catch (SQLException e) {
						LOG.fatal("Error Closing PreparedStatement", e);
					}	
				}
			}
		}
	}
	
	public void parseLine(String line) throws IOException, InterruptedException, SQLException{
		token = new StringTokenizer(line, ","); 
		
		boolean valid = true;
		if(columns!=null){
			this.entry.clearValues();
			String value = null;
			while(token.hasMoreTokens()) { 
				value = token.nextToken();				
				this.entry.addValue(value);
	        }
			
			valid = evaluateEntry(entry);
		} else {
			valid = false;
			this.columns = new ArrayList<String>();
			
			int index = 0;
			String column;
			
			StringBuffer query = new StringBuffer();
			StringBuffer valueBuf = new StringBuffer();
			
			query.append("INSERT INTO ");
			query.append(tableName).append(" (");
			
			while(token.hasMoreTokens()) {
				column = token.nextToken().toUpperCase();
				query.append(column).append(",");
				valueBuf.append("?").append(",");
				this.columns.add(column);
				this.colMap.put(column, index);
				index++;
				
				this.entry.setColumns(this.columns);
	        }
			query.append(COL_TAG_VALUES_FILEPATH).append(") VALUES (").append(valueBuf.toString());
			query.append("?)");			

			if (PST == null)
				PST = connection.prepareStatement(query.toString());
		}
		
		if(valid){
			for(int i=0; i<entry.getColumns().size(); i++){
				PST.setObject(i+1, entry.getValues().get(i));				
			}
			PST.setString(entry.getColumns().size() + 1, this.fileStatus.getPath().toUri().getPath());
			
			PST.addBatch();
			currentBatchSize++;
			
			if (currentBatchSize % maxBatchSize == 0)
			{
				PST.executeBatch();
				PST.clearBatch();
				currentBatchSize = 0;
			}
			
		}
    }
}
