package com.queryio.files.tagging;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public abstract class IFileParser {
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private int METHOD_NONE = 0;
	private int METHOD_COUNT = 1;
	private int METHOD_SUM = 2;
	private int METHOD_AVG = 3;
	private int METHOD_BOOLEAN = 4;
	private int METHOD_MAX = 5;
	private int METHOD_MIN = 6;
	private int METHOD_DISTINCT_COUNT = 7;
	private int METHOD_WORD_EXIST= 8;
	private int METHOD_WORD_OCCURRENCE_COUNT = 9;
	private int METHOD_PATTERN_EXIST = 10;
	private int METHOD_PATTERN_MATCH_COUNT = 11;
	private int METHOD_TOTAL_WORD_COUNT = 12;
	private int METHOD_TOTAL_LINE_COUNT = 13;
	
	protected String ENCODING_KEY = "encoding";
	protected String HAS_HEADER_KEY = "hasHeader";
	protected String DELIMITER_KEY = "delimiter";
	protected String VALUE_SEPERATOR_KEY = "valueSeperator";
	protected String ERROR_ACTION_KEY = "ifErrorOccur";
	
	protected String LOG_PATTERN_KEY = "pattern";
	
	protected String PARSE_DETAILS_KEY = "ParseDetails";
	protected String FIELDS_KEY = "Attributes";
	
	protected String COL_NAME_KEY = "colName";
	protected String COL_INDEX_KEY = "colIndex";
	
	final String WHITESPACE = " ";
	
	private List<Tag> tags;
	
	private HashMap<String, BigDecimal> total = new HashMap<String, BigDecimal>();
	private HashMap<String, Long> count = new HashMap<String, Long>();
	private HashMap<String, Long> distinctCount = new HashMap<String, Long>();
	private HashMap<String, BigDecimal> max = new HashMap<String, BigDecimal>();
	private HashMap<String, BigDecimal> min = new HashMap<String, BigDecimal>();
	
	private HashMap<String, Long> wordMatchCount = new HashMap<String, Long>();
	private HashMap<String, Long> patternMatchCount = new HashMap<String, Long>();
	private HashMap<String, Long> totalWordCount = new HashMap<String, Long>();
	private HashMap<String, Long> totalLineCount = new HashMap<String, Long>();
	
	private HashMap<String, String> operatorColumnNames = new HashMap<String, String>();
	
	private HashMap<String, Boolean> doKeepCount = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepDistinctCount = new HashMap<String, Boolean>();
	HashMap<String, Boolean> doKeepTotal = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepMax = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepMin = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepWordExist = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepWordOccurrenceCount = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepPatternExist = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepPatternMatchCount = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepTotalWordCount = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> doKeepTotalLineCount = new HashMap<String, Boolean>();

	private HashMap<String, ArrayList> distinctCountArray = new HashMap<String, ArrayList>();

	private HashMap<String, Boolean> satisfied = new HashMap<String, Boolean>();
	
	public abstract void parse(InputStream is, Map<String,String> coreTags) throws Exception;
	
	public IFileParser(JSONObject tagsJSON, JSONObject fileInfoJSON) {
		init(tagsJSON);
	}
	
	public IFileParser(JSONObject tagsJSON) {
		init(tagsJSON);
	}
	
	final private void setMethod(Tag tag, String method) {
		method = method.toUpperCase();
		int tagMethod = METHOD_NONE;
		if (method.startsWith("SUM")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("(") + 1, method.indexOf(")")));
			tagMethod = METHOD_SUM;
			doKeepTotal.put(tag.getName(), true);
		} 
		else if (method.startsWith("COUNT")) {
			tagMethod = METHOD_COUNT;
			doKeepCount.put(tag.getName(), true);
		} 
		else if (method.startsWith("AVG")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("(") + 1, method.indexOf(")")));
			tagMethod = METHOD_AVG;
			doKeepTotal.put(tag.getName(), true);
			doKeepCount.put(tag.getName(), true);
		}
		else if (method.startsWith("MAX")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("(") + 1, method.indexOf(")")));
			tagMethod = METHOD_MAX;
			doKeepMax.put(tag.getName(), true);
		}
		else if (method.startsWith("MIN")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("(") + 1, method.indexOf(")")));
			tagMethod = METHOD_MIN;
			doKeepMin.put(tag.getName(), true);
		}
		else if (method.startsWith("DISTINCTCOUNT")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("(") + 1, method.indexOf(")")));
			tagMethod = METHOD_DISTINCT_COUNT;
			doKeepDistinctCount.put(tag.getName(), true);
		}
		else if (method.startsWith("WORDEXISTS")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 2, method.indexOf("')")));
			tagMethod = METHOD_WORD_EXIST;
			doKeepWordOccurrenceCount.put(tag.getName(), true);
		} else if (method.startsWith("WORDOCCURRENCECOUNT")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 2, method.indexOf("')")));
			tagMethod = METHOD_WORD_OCCURRENCE_COUNT;
			doKeepWordOccurrenceCount.put(tag.getName(), true);
		}
		else if (method.startsWith("PATTERNEXISTS")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 2, method.indexOf("')")));
			tagMethod = METHOD_PATTERN_EXIST;
			doKeepPatternMatchCount.put(tag.getName(), true);
		}
		else if (method.startsWith("PATTERNMATCHCOUNT")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 2, method.indexOf("')")));
			tagMethod = METHOD_PATTERN_MATCH_COUNT;
			doKeepPatternMatchCount.put(tag.getName(), true);
		}
		else if (method.startsWith("TOTALWORDCOUNT")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 1, method.indexOf("')")));
			tagMethod = METHOD_TOTAL_WORD_COUNT;
			doKeepTotalWordCount.put(tag.getName(), true);
		}
		else if (method.startsWith("TOTALLINECOUNT")) {
			operatorColumnNames.put(tag.getName(), method.substring(
					method.indexOf("('") + 1, method.indexOf("')")));
			tagMethod = METHOD_TOTAL_LINE_COUNT;
			doKeepTotalLineCount.put(tag.getName(), true);
		}
		else if (method.startsWith("BOOLEAN")) {
			tagMethod = METHOD_BOOLEAN;
		} 
		else {
			tagMethod = METHOD_NONE;
		}
		tag.setMethod(tagMethod);
	}
	
	final private void init(final JSONObject tagInfo) {
		this.tags = new ArrayList<Tag>();
		
		JSONArray tagsArray = (JSONArray) tagInfo.get("Tags");
		for(int i=0; i<tagsArray.size(); i++) {
			JSONObject tagJSON = (JSONObject) tagsArray.get(i);
			
			Tag tag = new Tag();
			tag.setName((String) tagJSON.get("TagName"));
			
			satisfied.put(tag.getName(), false);
			doKeepTotal.put(tag.getName(), false);
			doKeepCount.put(tag.getName(), false);
			doKeepMax.put(tag.getName(), false);
			doKeepMin.put(tag.getName(), false);
			doKeepDistinctCount.put(tag.getName(), false);
			total.put(tag.getName(), new BigDecimal(0));
			count.put(tag.getName(), 0l);
			max.put(tag.getName(), null);
			min.put(tag.getName(), null);
			distinctCountArray.put(tag.getName(), new ArrayList());
			
			distinctCount.put(tag.getName(), 0l);
			wordMatchCount.put(tag.getName(), 0l);
			patternMatchCount.put(tag.getName(), 0l);
			totalLineCount.put(tag.getName(), 0l);
			totalWordCount.put(tag.getName(), 0l);
			
			doKeepWordOccurrenceCount.put(tag.getName(), false);
			doKeepTotalWordCount.put(tag.getName(), false);
			doKeepTotalLineCount.put(tag.getName(), false);
			doKeepPatternMatchCount.put(tag.getName(), false);
			
			boolean isMethod = Boolean.parseBoolean((String) tagJSON.get("isMethod"));
			
			String tagValue = (String) tagJSON.get("TagValue");
			if(isMethod) {
				this.setMethod(tag, tagValue);
			} else {
				tag.setSubstitutionValue(tagValue);
			}
			
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
	
	final protected void evaluateCurrentEntry(HashMap<String, String> curValueMap, String line) {
//		String line = "IP,CPU,RAM,DiskRead,DiskWrite,NetRead,NetWrite";	// Move this to arguments
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			
			List<Expression> expressions = tag.getExpressions();
			List<Boolean> expResults = new ArrayList<Boolean>();
			for(int j=0; j<expressions.size(); j++) {
				Expression expression = expressions.get(j);
				expResults.add(expression.evaluate(curValueMap.get(expression.getColumn())));
			}

			if((expResults.size() == 0) || (expResults.size()>0 && evaluateBooleanExpression(expResults, tag.getRelations()))){
				satisfied.put(tag.getName(), true);
				
				if(doKeepCount.get(tag.getName())){
					count.put(tag.getName(), count.get(tag.getName())+1);
				}
				if(doKeepDistinctCount.get(tag.getName())){
					if(distinctCountArray.get(tag.getName()).contains(curValueMap.get(operatorColumnNames.get(tag.getName())))) {
						distinctCount.put(tag.getName(), distinctCount.get(tag.getName())+1);
					} else {
						distinctCountArray.get(tag.getName()).add(curValueMap.get(operatorColumnNames.get(tag.getName())));
					}
				}
				if(doKeepTotal.get(tag.getName())){
					total.put(tag.getName(), total.get(tag.getName()).add(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))));
				}
				if(doKeepMax.get(tag.getName())) {
					boolean isMax = false;
					if(max.get(tag.getName()) == null) {
						isMax = true;
					} else if(max.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == -1) {
						isMax = true;
					}  
					if(isMax) {
						max.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					}
				}
				if(doKeepMin.get(tag.getName())) {
					boolean isMin = false;
					if(min.get(tag.getName()) == null) {
						isMin = true;
					} else if(min.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == 1) {
						isMin = true;
					}
					if(isMin) {
						min.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					}
				}
				if(doKeepWordOccurrenceCount.get(tag.getName())) {
					String[] words = line.split(WHITESPACE);
					String matchWithWord = operatorColumnNames.get(tag.getName());
					int count = 0;
					
					for(int j=0; j<words.length; j++) {
						if(words[j].trim().equalsIgnoreCase(matchWithWord)) {
							count++;
						}
					}
					wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + count);
				}
				if(doKeepPatternMatchCount.get(tag.getName())) {
					Pattern pattern = Pattern.compile(operatorColumnNames.get(tag.getName()));
					Matcher  matcher2 = pattern.matcher(line);
					int matchCount = 0;
			        while (matcher2.find()) {
			        	matchCount++;
			        }
			        patternMatchCount.put(tag.getName(), patternMatchCount.get(tag.getName()) + matchCount);
				}
				if(doKeepTotalWordCount.get(tag.getName())) {
					totalWordCount.put(tag.getName(), totalWordCount.get(tag.getName()) + line.split(WHITESPACE).length);
				}
				if(doKeepTotalLineCount.get(tag.getName())) {
					totalLineCount.put(tag.getName(), totalLineCount.get(tag.getName()) + 1);
				}
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
	
	final public HashMap<String, Object> getResultTags() {
		HashMap<String, Object> resultTags = new HashMap<String, Object>();
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
		
			if(satisfied.get(tag.getName())) {
				Object value = null;
				if(tag.getMethod()!=METHOD_NONE) {
					int method = tag.getMethod();
					
					if(method==METHOD_SUM) {
						value = total.get(tag.getName()).doubleValue();
					} else if(method==METHOD_COUNT) {
						value = count.get(tag.getName()).longValue();
					} else if(method==METHOD_AVG) {
						value = total.get(tag.getName()).divide(new BigDecimal(count.get(tag.getName())), RoundingMode.HALF_UP).doubleValue();
					} else if(method == METHOD_MAX) {
						value = max.get(tag.getName());
					} else if(method == METHOD_MIN) {
						value = min.get(tag.getName());
					} else if(method==METHOD_DISTINCT_COUNT) {
						value = distinctCount.get(tag.getName()).longValue();
					} else if(method == METHOD_WORD_OCCURRENCE_COUNT) {
						value = wordMatchCount.get(tag.getName()).longValue();
					} else if(method == METHOD_WORD_EXIST) {
						value = wordMatchCount.get(tag.getName()).longValue() > 0;
					} else if(method == METHOD_PATTERN_MATCH_COUNT) {
						value = patternMatchCount.get(tag.getName()).longValue();
					} else if(method == METHOD_PATTERN_EXIST) {
						value = patternMatchCount.get(tag.getName()).longValue() > 0;
					} else if(method == METHOD_TOTAL_WORD_COUNT) {
						value = totalWordCount.get(tag.getName()).longValue();
					} else if(method == METHOD_TOTAL_LINE_COUNT) {
						value = totalLineCount.get(tag.getName()).longValue();
					}
				} else {
					value = tag.getSubstitutionValue();
				}
				
				resultTags.put(tag.getName(), value);
			} else {
				 resultTags.put(tag.getName(), null);
			}
		}
			
		return resultTags;
	}
}
