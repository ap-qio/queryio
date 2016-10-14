package com.queryio.demo.mr.ncdcgsod;

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
import java.util.zip.GZIPInputStream;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;

import com.queryio.plugin.datatags.ColumnMetadata;

public class NCDCParser {
	private static final Log LOG = LogFactory.getLog(NCDCParser.class);
	private static final String COL_TAG_VALUES_FILEPATH = "FILEPATH";
	
	ArrayList<String> columns = null;
	NCDCEntry entry = new NCDCEntry();
	StringTokenizer token = null; 
	
	Connection connection = null;
	PreparedStatement pst = null;
	String tableName = null;
	FileStatus fileStatus = null;
	NCDCExpressions ncdcExpressions;
	
	HashMap<String, Integer> colMap = new HashMap<String, Integer>();
	
	int currentBatchSize = 0;
	int maxBatchSize;
	
	public NCDCParser(NCDCExpressions ncdcExpressions, Connection connection, String tableName, FileStatus fileStatus, int maxBatchSize){
		this.ncdcExpressions = ncdcExpressions;
		this.connection = connection;
		this.fileStatus = fileStatus;
		this.tableName = tableName;
		this.maxBatchSize = maxBatchSize;
		
	}
	
	String lastOperator = null;
	
	
	public boolean evaluateExpression(Expression expression, String value){
		return expression.evaluate(value);
	}
	
	public boolean evaluateEntry(NCDCEntry entry){
		boolean valid = true;
		
		ArrayList<Boolean> expResults = new ArrayList<Boolean>();
		
		Expression expression;
		for(int i=0; i< ncdcExpressions.expressions.size(); i++){
			expression = ncdcExpressions.expressions.get(i);
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
	    		exp += ncdcExpressions.booleanExpressions.get(i-1);
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
		
		is = new GZIPInputStream(is);
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
			try {
				if (rd != null)
					rd.close();
			} catch (Exception e) {
				LOG.fatal(e.getMessage(), e);
			}
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
		}
	}
	
	public void parseLine(String line) throws IOException, InterruptedException, SQLException{
		line = line.replace("  ", " ");
		line = line.replace("\t", " ");
		line = line.replace(" ", ",");
		line = line.replaceAll("I","");
		
		LOG.info("LINE: " + line);
		
		token = new StringTokenizer(line, ",");
		
		boolean valid = true;
		if(columns!=null){
			this.entry.clearValues();
			String value = null;
			int index = 0;
			while(token.hasMoreTokens()) { 
				value = token.nextToken();	
				
				if(index==2){
					LOG.info("DATE: " + value);
					try{ this.entry.addValue(value.substring(0, 4)); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Year
					try{ this.entry.addValue(value.substring(4, 6)); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Month
					try{ this.entry.addValue(value.substring(6, value.length())); } catch(Exception e) {LOG.fatal("DATE: " + value + ", Error: " + e.getMessage(), e);} // Day
				} else if(index==17){
					this.entry.addValue(value.replace("*", ""));
					this.entry.addValue(value.contains("*") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE));
				} else if(index==18){
					this.entry.addValue(value.replace("*", ""));
					this.entry.addValue(value.contains("*") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE));
				} else if(index==21){
					this.entry.addValue(value.substring(0, 1).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // fog
					this.entry.addValue(value.substring(1, 2).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // rain
					this.entry.addValue(value.substring(2, 3).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // snow
					this.entry.addValue(value.substring(3, 4).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // hail
					this.entry.addValue(value.substring(4, 5).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // thunder
					this.entry.addValue(value.substring(5, 6).equals("1") ? String.valueOf(Boolean.TRUE) : String.valueOf(Boolean.FALSE)); // tornado
				} else {
					if(value.contains(".")){
						if(value.endsWith("E"))	value = value + "+0";
						value = String.valueOf(Double.valueOf(value).doubleValue());
					}
					
					this.entry.addValue(value);
				}
				
				index++;
	        }
			
			valid = evaluateEntry(entry);
		} else {
			valid = false;
			this.columns = new ArrayList<String>();
			
			int index = 0;
			String column;
			ArrayList<ColumnMetadata> list = new NCDCDataDefinitionImpl().getColumnMetadata();			
				
			StringBuffer query = new StringBuffer();
			StringBuffer valueBuf = new StringBuffer();
			
			query.append("INSERT INTO ");
			query.append(tableName).append(" (");
			
			for(int i=0; i<list.size(); i++){
				column = list.get(i).getColumnName();
				
				if(column.equalsIgnoreCase(COL_TAG_VALUES_FILEPATH))	continue;
				
				query.append(column).append(",");
				valueBuf.append("?").append(",");
				this.columns.add(column);
				this.colMap.put(column, index);
				index++;
				
				this.entry.setColumns(this.columns);
			}
			
			query.append(COL_TAG_VALUES_FILEPATH).append(") VALUES (").append(valueBuf.toString());
			query.append("?)");			

			try {
				LOG.info(query.toString());
				if (pst == null)
					pst = connection.prepareStatement(query.toString());
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
		
		if(valid){
			
			try {
				for(int i=0; i<entry.getColumns().size(); i++){
					LOG.info("SETTING: " + entry.getColumns().get(i) + " : " + entry.getValues().get(i));
					
					boolean parsed = false;
					
					try{
						boolean value = Boolean.parseBoolean(entry.getValues().get(i));
						pst.setObject(i+1, value);
						parsed = true;
					} catch(Exception e){
						// NOT A BOOLEAN
					}
					
					try{
						int value = Integer.parseInt(entry.getValues().get(i));
						pst.setObject(i+1, value);
						parsed = true;
					} catch(Exception e) {
						// NOT AN INTEGER
					}
					
					try{
						double value = Double.parseDouble(entry.getValues().get(i));
						pst.setObject(i+1, value);
						parsed = true;
					} catch(Exception e) {
						// NOT A DOUBLE
					}
					
					if( ! parsed )	pst.setObject(i+1, entry.getValues().get(i));
				}
				pst.setString(entry.getColumns().size() + 1, this.fileStatus.getPath().toUri().getPath());
				
				pst.addBatch();
				currentBatchSize++;
				
				if (currentBatchSize % maxBatchSize == 0)
				{
					pst.executeBatch();
					pst.clearBatch();
					currentBatchSize = 0;
				}
			} catch (SQLException e) {
				LOG.fatal(e.getMessage(), e);
			}
		}
    }
}
