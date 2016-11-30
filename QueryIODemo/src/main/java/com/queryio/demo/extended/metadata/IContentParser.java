package com.queryio.demo.extended.metadata;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.Expression;
import com.queryio.files.tagging.Tag;
import com.queryio.plugin.extended.metadata.TableMetadata;

public abstract class IContentParser {
	protected Logger logger = Logger.getLogger(this.getClass());
	
	protected final String PARSE_DETAILS_KEY = "ParseDetails";
	protected final String FIELDS_KEY = "Attributes";
	
	protected final String COL_NAME_KEY = "colName";
	protected final String COL_INDEX_KEY = "colIndex";
	
	private List<Tag> tags;
	protected Map<String, String> coreTags;
	
	private HashMap<String, Boolean> satisfied = new HashMap<String, Boolean>();
	
	private HashMap<String, Boolean> isMethod = new HashMap<String, Boolean>();
	
	public abstract void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception;
	
	public IContentParser(JSONObject tagsJSON, JSONObject fileInfoJSON, Map<String, String> coreTags) {
		this.coreTags = coreTags;
		init(tagsJSON);
	}
	
	public IContentParser(JSONObject tagsJSON, Map<String, String> coreTags) {
		this.coreTags = coreTags;
		init(tagsJSON);
	}
	
	final private void init(final JSONObject tagInfo) {
		this.tags = new ArrayList<Tag>();
		
		JSONArray tagsArray = (JSONArray) tagInfo.get("Tags");
		for(int i=0; i<tagsArray.size(); i++) {
			JSONObject tagJSON = (JSONObject) tagsArray.get(i);
			
			Tag tag = new Tag();
			tag.setName((String) tagJSON.get("TagName"));
			
			isMethod.put(tag.getName(), Boolean.parseBoolean((String) tagJSON.get("isMethod")));
			
			tag.setSubstitutionValue((String) tagJSON.get("TagValue"));
			
			satisfied.put(tag.getName(), false);
		
			List<Expression> expressionsForTag = new ArrayList<Expression>();
			
			JSONArray expressionsArray = (JSONArray) tagJSON.get("Expressions");
			
			for(int j=0; j<expressionsArray.size(); j++) {
				JSONObject expressionJSON = (JSONObject) expressionsArray.get(j);
				
				Expression expression = new Expression();
				expression.setColumn((String) expressionJSON.get("Column"));
				expression.setOperator(Expression.getOperator((String) expressionJSON.get("Operator")));
				expression.setValue((String) expressionJSON.get("Value"));
				
				expressionsForTag.add(expression);
			}
			
			List<String> relationsForTag = new ArrayList<String>();
			
			JSONArray relationsArray = (JSONArray) tagJSON.get("Relations");
			
			for(int j=0; j<relationsArray.size(); j++) {
				String relation = (String)relationsArray.get(j);
				if(relation.equalsIgnoreCase("AND")) {
					relationsForTag.add("&&");
				} else if(relation.equalsIgnoreCase("OR")) {
					relationsForTag.add("||");
				}
				
			}
			
			tag.setExpressions(expressionsForTag);
			tag.setRelations(relationsForTag);
			
			this.tags.add(tag);
		}
	}
	
	final protected void evaluateCurrentEntry(HashMap<String, String> curValueMap) {
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			
			List<Expression> expressions = tag.getExpressions();
			List<Boolean> expResults = new ArrayList<Boolean>();
			for(int j=0; j<expressions.size(); j++) {
				Expression expression = expressions.get(j);
				expResults.add(expression.evaluate(curValueMap.get(expression.getColumn())));
			}
			
			if(expResults.size()>0 && evaluateBooleanExpression(expResults, tag.getRelations())){
				satisfied.put(tag.getName(), true);
			} else if(expressions.size() == 0){
				satisfied.put(tag.getName(), true);
			}
		}
	}
	
	final private boolean evaluateBooleanExpression(List<Boolean> expResults, List<String> relations){
		JexlEngine jexl = new JexlEngine();
	    jexl.setSilent(true);
	    jexl.setLenient(true);

	    String exp = "";
	    
	    for(int i=0; i<expResults.size(); i++){
	    	if(i!=0){
	    		exp += relations.get(i-1);
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
	
	final protected List<Tag> getTags() {
		return this.tags;
	}
	
	final protected boolean isSatisfied(String tagName) {
		return satisfied.get(tagName);
	}
	
	final protected boolean isMethod(String tagName) {
		return isMethod.get(tagName);
	}
}
