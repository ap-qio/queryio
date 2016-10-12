package com.queryio.plugin.datatags;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.plugin.datatags.common.Expression;
import com.queryio.plugin.datatags.common.Tag;

public abstract class AbstractDataTagParser implements IDataTagParser {
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private final int METHOD_NONE = 0;
	private final int METHOD_COUNT = 1;
	private final int METHOD_SUM = 2;
	private final int METHOD_AVG = 3;
	private final int METHOD_BOOLEAN = 4;
	private final int METHOD_MAX = 5;
	private final int METHOD_MIN = 6;
	private final int METHOD_DISTINCT_COUNT = 7;
	private final int METHOD_WORD_EXIST= 8;
	private final int METHOD_WORD_OCCURRENCE_COUNT = 9;
	private final int METHOD_PATTERN_EXIST = 10;
	private final int METHOD_PATTERN_MATCH_COUNT = 11;
	private final int METHOD_TOTAL_WORD_COUNT = 12;
	private final int METHOD_TOTAL_LINE_COUNT = 13;
	
	Map<String, String> wordToMatch = new HashMap<String, String>();
	Map<String, String> metaWordToMatch = new HashMap<String, String>();
		
	Map<String, String> patternToMatch = new HashMap<String, String>();
	
	Map<String, String> copyValues = new HashMap<String, String>();
	
	Map<String, Integer> tagOperations = new HashMap<String, Integer>();
	
	Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	
	private final int OPERATION_NO_OPERATION = 0;
	private final int OPERATION_WORD_EXISTS = 1;
	private final int OPERATION_WORD_MATCH_COUNT = 2;
	private final int OPERATION_PATTERN_EXISTS = 3;
	private final int OPERATION_PATTERN_MATCH_COUNT = 4;
	private final int OPERATION_TOTAL_WORD_COUNT = 5;
	private final int OPERATION_TOTAL_LINE_COUNT = 6;
	private final int OPERATION_META_OCCURENCE_COUNT = 7;
	private final int OPERATION_COPY = 8;
	private final int OPERATION_META_EXIST = 9;
	
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
	
	private List<Tag> tags = new ArrayList<Tag>();
	protected Map<String, String> coreTags;
	protected JSONObject tagsJSON;

	private HashMap<String, BigDecimal> total = new HashMap<String, BigDecimal>();
	private HashMap<String, Long> count = new HashMap<String, Long>();
	private HashMap<String, Object> distinctCount = new HashMap<String, Object>();
	private HashMap<String, BigDecimal> max = new HashMap<String, BigDecimal>();
	private HashMap<String, BigDecimal> min = new HashMap<String, BigDecimal>();
	
	protected HashMap<String, Long> wordMatchCount = new HashMap<String, Long>();
	protected HashMap<String, Long> patternMatchCount = new HashMap<String, Long>();
	protected HashMap<String, Long> totalWordCount = new HashMap<String, Long>();
	protected HashMap<String, Long> totalLineCount = new HashMap<String, Long>();
	
	private HashMap<String, String> operatorColumnNames = new HashMap<String, String>();
	
//	private HashMap<String, Boolean> doKeepCount = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepDistinctCount = new HashMap<String, Boolean>();
//	HashMap<String, Boolean> doKeepTotal = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepMax = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepMin = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepWordExist = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepWordOccurrenceCount = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepPatternExist = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepPatternMatchCount = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepTotalWordCount = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> doKeepTotalLineCount = new HashMap<String, Boolean>();
	
	private HashMap<String, ArrayList> distinctCountArray = new HashMap<String, ArrayList>();

	protected HashMap<String, Boolean> satisfied = new HashMap<String, Boolean>();
	protected HashMap<String, Boolean> isMethod = new HashMap<String, Boolean>();
	
	public AbstractDataTagParser(JSONObject tagsJSON, Map<String, String> coreTags, boolean isLogical) {
		this.coreTags = coreTags;
		this.tagsJSON = tagsJSON;
		init(isLogical);
	}

	public AbstractDataTagParser() {
	}
	
	protected void setMethod(Tag tag, String method, boolean isLogical) {
		if (isLogical)
		{
			method = method.toUpperCase();
			int tagMethod = METHOD_NONE;
			if (method.startsWith("SUM")) {
				operatorColumnNames.put(tag.getName(), method.substring(
						method.indexOf("(") + 1, method.indexOf(")")));
				tagMethod = METHOD_SUM;
				tagOperations.put(tag.getName(), METHOD_SUM);
			} 
			else if (method.startsWith("COUNT")) {
				tagMethod = METHOD_COUNT;
				tagOperations.put(tag.getName(), METHOD_COUNT);
			} 
			else if (method.startsWith("AVG")) {
				operatorColumnNames.put(tag.getName(), method.substring(
						method.indexOf("(") + 1, method.indexOf(")")));
				tagMethod = METHOD_AVG;
				tagOperations.put(tag.getName(), METHOD_AVG);
			}
			else if (method.startsWith("MAX")) {
				operatorColumnNames.put(tag.getName(), method.substring(
						method.indexOf("(") + 1, method.indexOf(")")));
				tagMethod = METHOD_MAX;
				tagOperations.put(tag.getName(), METHOD_MAX);
			}
			else if (method.startsWith("MIN")) {
				operatorColumnNames.put(tag.getName(), method.substring(
						method.indexOf("(") + 1, method.indexOf(")")));
				tagMethod = METHOD_MIN;
				tagOperations.put(tag.getName(), METHOD_MIN);
			}
			else if (method.startsWith("DISTINCTCOUNT")) {
				operatorColumnNames.put(tag.getName(), method.substring(
						method.indexOf("(") + 1, method.indexOf(")")));
				tagMethod = METHOD_DISTINCT_COUNT;
				tagOperations.put(tag.getName(), METHOD_DISTINCT_COUNT);
			}
			else if (method.startsWith("WORDEXISTS")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 2, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 1, method.indexOf(")")));
				}
				tagMethod = METHOD_WORD_EXIST;
				tagOperations.put(tag.getName(), METHOD_WORD_EXIST);
			} else if (method.startsWith("WORDOCCURRENCECOUNT")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 2, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 1, method.indexOf(")")));
				}
				tagMethod = METHOD_WORD_OCCURRENCE_COUNT;
				tagOperations.put(tag.getName(), METHOD_WORD_OCCURRENCE_COUNT);
			}
			else if (method.startsWith("PATTERNEXISTS")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 2, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 2, method.indexOf(")")));
				}
				tagMethod = METHOD_PATTERN_EXIST;
				tagOperations.put(tag.getName(), METHOD_PATTERN_EXIST);
			}
			else if (method.startsWith("PATTERNMATCHCOUNT")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 2, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 2, method.indexOf(")")));
				}
				tagMethod = METHOD_PATTERN_MATCH_COUNT;
				tagOperations.put(tag.getName(), METHOD_PATTERN_MATCH_COUNT);
			}
			else if (method.startsWith("TOTALWORDCOUNT")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 1, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 1, method.indexOf(")")));
				}
				tagMethod = METHOD_TOTAL_WORD_COUNT;
				tagOperations.put(tag.getName(), METHOD_TOTAL_WORD_COUNT);
			}
			else if (method.startsWith("TOTALLINECOUNT")) {
				if (method.contains("'"))
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("('") + 1, method.indexOf("')")));
				}
				else
				{
					operatorColumnNames.put(tag.getName(), method.substring(
							method.indexOf("(") + 1, method.indexOf(")")));
				}
				tagMethod = METHOD_TOTAL_LINE_COUNT;
				tagOperations.put(tag.getName(), METHOD_TOTAL_LINE_COUNT);
			}
			else if (method.startsWith("BOOLEAN")) {
				tagMethod = METHOD_BOOLEAN;
				tagOperations.put(tag.getName(), METHOD_BOOLEAN);
			} 
			else {
				tagMethod = METHOD_NONE;
				tagOperations.put(tag.getName(), METHOD_NONE);
			}
			tag.setMethod(tagMethod);
		}
		else
		{
			String tagName = tag.getName();
			
			if(method.contains("WordExists")) {
				tagOperations.put(tagName, OPERATION_WORD_EXISTS);
				wordToMatch.put(tagName, method.substring("WordExists(".length(), method.lastIndexOf(")")));
			} else if(method.contains("WordOccurrenceCount")) {
				tagOperations.put(tagName, OPERATION_WORD_MATCH_COUNT);
				wordToMatch.put(tagName, method.substring("WordOccurrenceCount(".length(), method.lastIndexOf(")")));
			} else if(method.contains("OccurrenceCount")) {
				tagOperations.put(tagName, OPERATION_META_OCCURENCE_COUNT);
				metaWordToMatch.put(tagName, method.substring("OccurrenceCount(".length(), method.lastIndexOf(")")));
			} else if(method.contains("isExist")) {
				tagOperations.put(tagName, OPERATION_META_EXIST);
				metaWordToMatch.put(tagName, method.substring("isExist(".length(), method.lastIndexOf(")")));
			} else if(method.contains("PatternExists")) {
				tagOperations.put(tagName, OPERATION_PATTERN_EXISTS);
				String patternToMatch = method.substring("PatternExists(".length(), method.lastIndexOf(")"));
				patterns.put(tagName, Pattern.compile(patternToMatch));
			} else if(method.contains("PatternMatchCount")) {
				tagOperations.put(tagName, OPERATION_PATTERN_MATCH_COUNT);
				String patternToMatch = method.substring("PatternMatchCount(".length(), method.lastIndexOf(")"));
				patterns.put(tagName, Pattern.compile(patternToMatch));
			} else if(method.contains("TotalWordCount")) {
				tagOperations.put(tagName, OPERATION_TOTAL_WORD_COUNT);
			} else if(method.contains("TotalLineCount")) {
				tagOperations.put(tagName, OPERATION_TOTAL_LINE_COUNT);
			} else if(method.contains("Copy")) {
				tagOperations.put(tagName, OPERATION_COPY);
				metaWordToMatch.put(tagName, method.substring("Copy(".length(), method.lastIndexOf(")")));
			} else {
				tagOperations.put(tagName, OPERATION_NO_OPERATION);
			}
		}
	}
	
	protected void init(boolean isLogical) {
		
		if (tagsJSON == null)
			return;
		
		JSONArray tagsArray = (JSONArray) tagsJSON.get("Tags");
		
		if (isLogical)
		{
			for(int i=0; i<tagsArray.size(); i++) {
				JSONObject tagJSON = (JSONObject) tagsArray.get(i);
				
				Tag tag = new Tag();
				tag.setName((String) tagJSON.get("TagName"));
				
				satisfied.put(tag.getName(), false);
				
//				doKeepTotal.put(tag.getName(), false);
//				doKeepCount.put(tag.getName(), false);
//				doKeepMax.put(tag.getName(), false);
//				doKeepMin.put(tag.getName(), false);
//				doKeepDistinctCount.put(tag.getName(), false);
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
				
//				doKeepWordOccurrenceCount.put(tag.getName(), false);
//				doKeepTotalWordCount.put(tag.getName(), false);
//				doKeepTotalLineCount.put(tag.getName(), false);
//				doKeepPatternMatchCount.put(tag.getName(), false);
				
				boolean isMethod = Boolean.parseBoolean((String) tagJSON.get("isMethod"));
				
				String tagValue = (String) tagJSON.get("TagValue");
				if (isMethod) {
					this.setMethod(tag, tagValue, isLogical);
				} else {
					tag.setSubstitutionValue(tagValue);
				}
				
				this.isMethod.put(tag.getName(), isMethod);
				
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
		else
		{
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
			
//			System.out.println("Expressions: " + expressions + " for tag: " + tag.getName() + " evaluated to " + expResults + " for values: " + curValueMap);
//			System.out.println("getName : " + tag.getName());
//			System.out.println("isMethod : " + isMethod(tag.getName()));
			if(expResults.size()>0 && evaluateBooleanExpression(expResults, tag.getRelations())){
				satisfied.put(tag.getName(), true);
			} else if(expressions.size() == 0){
				satisfied.put(tag.getName(), true);
			}
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
				if (tagOperations.get(tag.getName()) == null)
					return;
				
				Integer method = tagOperations.get(tag.getName());
				
				switch (method) {
				case METHOD_COUNT:
					count.put(tag.getName(), count.get(tag.getName())+1);
					break;
				case METHOD_SUM:
				case METHOD_AVG:
					total.put(tag.getName(), total.get(tag.getName()).add(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))));
					count.put(tag.getName(), count.get(tag.getName()) + 1);
					break;
				case METHOD_BOOLEAN:
					break;
				case METHOD_MAX:
					boolean isMax = false;
					if(max.get(tag.getName()) == null) {
						isMax = true;
					} else if(max.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == -1) {
						isMax = true;
					}  
					if(isMax) {
						max.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					}
					break;
				case METHOD_MIN:
					boolean isMin = false;
					if(min.get(tag.getName()) == null) {
						isMin = true;
					} else if(min.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == 1) {
						isMin = true;
					}
					if(isMin) {
						min.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					}
					break;
				case METHOD_DISTINCT_COUNT:
					if(!distinctCountArray.get(tag.getName()).contains(curValueMap.get(operatorColumnNames.get(tag.getName())))) {
						distinctCount.put(tag.getName(), ((Long) distinctCount.get(tag.getName())).longValue() + 1L);
						distinctCountArray.get(tag.getName()).add(curValueMap.get(operatorColumnNames.get(tag.getName())));
					}
					break;
				case METHOD_WORD_EXIST:
				case METHOD_WORD_OCCURRENCE_COUNT:
					Pattern pattern = Pattern.compile("\\b" + operatorColumnNames.get(tag.getName())+ "\\b", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(line);
					int matchCount = 0;
					while (matcher.find()) {
			        	matchCount++;
			        }
					wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + matchCount);
					break;
				case METHOD_PATTERN_EXIST:
				case METHOD_PATTERN_MATCH_COUNT:
					Pattern pattern_match = Pattern.compile(operatorColumnNames.get(tag.getName()));
					Matcher  matcherPattern = pattern_match.matcher(line);
					int matchCountPattern = 0;
			        while (matcherPattern.find()) {
			        	matchCountPattern++;
			        }
			        patternMatchCount.put(tag.getName(), patternMatchCount.get(tag.getName()) + matchCountPattern);
					break;
				case METHOD_TOTAL_WORD_COUNT:
					Scanner sc = new Scanner(line);
					int cnt = 0;
					while(sc.hasNext()) {
						cnt++; sc.next();
					}
					totalWordCount.put(tag.getName(), totalWordCount.get(tag.getName()) + cnt);
					sc.close();
					break;
				case METHOD_TOTAL_LINE_COUNT:
					totalLineCount.put(tag.getName(), totalLineCount.get(tag.getName()) + 1);
					break;
				}
				
//				if(doKeepCount.get(tag.getName())){
//					count.put(tag.getName(), count.get(tag.getName())+1);
//				}
//				if(doKeepDistinctCount.get(tag.getName())){
//					if(!distinctCountArray.get(tag.getName()).contains(curValueMap.get(operatorColumnNames.get(tag.getName())))) {
//						distinctCount.put(tag.getName(), ((Long) distinctCount.get(tag.getName())).longValue() + 1L);
//						distinctCountArray.get(tag.getName()).add(curValueMap.get(operatorColumnNames.get(tag.getName())));
//					}
//				}
//				if(doKeepTotal.get(tag.getName())){
//					total.put(tag.getName(), total.get(tag.getName()).add(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))));
//				}
//				if(doKeepMax.get(tag.getName())) {
//					boolean isMax = false;
//					if(max.get(tag.getName()) == null) {
//						isMax = true;
//					} else if(max.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == -1) {
//						isMax = true;
//					}  
//					if(isMax) {
//						max.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
//					}
//				}
//				if(doKeepMin.get(tag.getName())) {
//					boolean isMin = false;
//					if(min.get(tag.getName()) == null) {
//						isMin = true;
//					} else if(min.get(tag.getName()).compareTo(new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))) == 1) {
//						isMin = true;
//					}
//					if(isMin) {
//						min.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
//					}
//				}
//				if(doKeepWordOccurrenceCount.get(tag.getName())) {
//					Pattern pattern = Pattern.compile("\\b" + operatorColumnNames.get(tag.getName())+ "\\b", Pattern.CASE_INSENSITIVE);
//					Matcher matcher = pattern.matcher(line);
//					int matchCount = 0;
//					while (matcher.find()) {
//			        	matchCount++;
//			        }
//					wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + matchCount);
//				}
//				if(doKeepPatternMatchCount.get(tag.getName())) {
//					Pattern pattern = Pattern.compile(operatorColumnNames.get(tag.getName()));
//					Matcher  matcher2 = pattern.matcher(line);
//					int matchCount = 0;
//			        while (matcher2.find()) {
//			        	matchCount++;
//			        }
//			        patternMatchCount.put(tag.getName(), patternMatchCount.get(tag.getName()) + matchCount);
//				}
//				if(doKeepTotalWordCount.get(tag.getName())) {
//					Scanner sc = new Scanner(line);
//					int cnt = 0;
//					while(sc.hasNext()) {
//						cnt++; sc.next();
//					}
//					totalWordCount.put(tag.getName(), totalWordCount.get(tag.getName()) + cnt);
//					sc.close();
//				}
//				if(doKeepTotalLineCount.get(tag.getName())) {
//					totalLineCount.put(tag.getName(), totalLineCount.get(tag.getName()) + 1);
//				}
			}
		}
	}
	
	final protected void evaluateRecordEntry(HashMap<String, String> curValueMap, String line) {
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
				if (tagOperations.get(tag.getName()) == null)
					return;
				int method = tagOperations.get(tag.getName());
				
				switch (method) {
				case METHOD_COUNT:
					count.put(tag.getName(), 1L);
					break;
				case METHOD_SUM:
				case METHOD_AVG:
					total.put(tag.getName(), (new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))));
					count.put(tag.getName(), 1L);
					break;
				case METHOD_BOOLEAN:
					break;
				case METHOD_MAX:
					max.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					break;
				case METHOD_MIN:
					min.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
					break;
				case METHOD_DISTINCT_COUNT:
					distinctCount.put(tag.getName(), curValueMap.get(operatorColumnNames.get(tag.getName())));
					break;
				case METHOD_WORD_EXIST:
				case METHOD_WORD_OCCURRENCE_COUNT:
					Pattern pattern = Pattern.compile("\\b" + operatorColumnNames.get(tag.getName())+ "\\b", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(line);
					Long matchCount = 0L;
					while (matcher.find()) {
						matchCount++;
					}
					
					wordMatchCount.put(tag.getName(), matchCount);
					break;
				case METHOD_PATTERN_EXIST:
				case METHOD_PATTERN_MATCH_COUNT:
					Pattern patternMatch = Pattern.compile(operatorColumnNames.get(tag.getName()));
					Matcher  matcherPattern = patternMatch.matcher(line);
					Long matchCountPattern = 0L;
					while (matcherPattern.find()) {
						matchCountPattern++;
					}
					patternMatchCount.put(tag.getName(), matchCountPattern);
					break;
				case METHOD_TOTAL_WORD_COUNT:
					Scanner sc = new Scanner(line);
					long cnt = 0L;
					while(sc.hasNext()) {
						cnt++; sc.next();
					}
					totalWordCount.put(tag.getName(), cnt);
					sc.close();
					break;
				case METHOD_TOTAL_LINE_COUNT:
					totalLineCount.put(tag.getName(), 1L);
					break;
				}
				
				
//				if(doKeepCount.get(tag.getName())){
//					count.put(tag.getName(), 1L);
//				}
//				if(doKeepDistinctCount.get(tag.getName())){
//					distinctCount.put(tag.getName(), curValueMap.get(operatorColumnNames.get(tag.getName())));
//				}
//				if(doKeepTotal.get(tag.getName())){
//					total.put(tag.getName(), (new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName())))));
//				}
//				if(doKeepMax.get(tag.getName())) {
//					max.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
//				}
//				if(doKeepMin.get(tag.getName())) {
//					min.put(tag.getName(), new BigDecimal(curValueMap.get(operatorColumnNames.get(tag.getName()))));
//				}
//				if(doKeepWordOccurrenceCount.get(tag.getName())) {
//					Pattern pattern = Pattern.compile("\\b" + operatorColumnNames.get(tag.getName())+ "\\b", Pattern.CASE_INSENSITIVE);
//					Matcher matcher = pattern.matcher(line);
//					Long matchCount = 0L;
//					while (matcher.find()) {
//						matchCount++;
//					}
//					wordMatchCount.put(tag.getName(), matchCount);
//				}
//				if(doKeepPatternMatchCount.get(tag.getName())) {
//					Pattern pattern = Pattern.compile(operatorColumnNames.get(tag.getName()));
//					Matcher  matcher2 = pattern.matcher(line);
//					Long matchCount = 0L;
//					while (matcher2.find()) {
//						matchCount++;
//					}
//					patternMatchCount.put(tag.getName(), matchCount);
//				}
//				if(doKeepTotalWordCount.get(tag.getName())) {
//					Scanner sc = new Scanner(line);
//					long cnt = 0L;
//					while(sc.hasNext()) {
//						cnt++; sc.next();
//					}
//					totalWordCount.put(tag.getName(), cnt);
//					sc.close();
//				}
//				if(doKeepTotalLineCount.get(tag.getName())) {
//					totalLineCount.put(tag.getName(), 1L);
//				}
			}
		}
	}
	
	public void parseTagData(String key, String value, boolean isReducer) throws Exception {
		satisfied.put(key, true);
		if (tagOperations.get(key) == null)
			return;
		int method = tagOperations.get(key);
		
		switch(method) {
		case METHOD_COUNT:
			if (isReducer) {
				count.put(key, count.get(key) + Long.parseLong(value));
			} else {
				count.put(key, count.get(key) + 1L);
			}
			break;
		case METHOD_SUM:
		case METHOD_AVG:
			total.put(key, total.get(key).add(new BigDecimal(value)));
			count.put(key, count.get(key) + 1L);
			break;
		case METHOD_BOOLEAN:
			break;
		case METHOD_MAX:
			boolean isMax = false;
			if(max.get(key) == null) {
				isMax = true;
			} else if(max.get(key).compareTo(new BigDecimal(value)) == -1) {
				isMax = true;
			}  
			if(isMax) {
				max.put(key, new BigDecimal(value));
			}
			break;
		case METHOD_MIN:
			boolean isMin = false;
			if(min.get(key) == null) {
				isMin = true;
			} else if(min.get(key).compareTo(new BigDecimal(value)) == 1) {
				isMin = true;
			}
			if(isMin) {
				min.put(key, new BigDecimal(value));
			}
			break;
		case METHOD_DISTINCT_COUNT:
			if (isReducer) {
				distinctCount.put(key, ((Long) distinctCount.get(key)).longValue() + Long.parseLong(value));
			} else { 
				if (!distinctCountArray.get(key).contains(value)) {
					distinctCount.put(key, ((Long) distinctCount.get(key)).longValue() + 1L);
					distinctCountArray.get(key).add(value);
				}
			}
			break;
		case METHOD_WORD_EXIST:
			boolean parseBoolean = Boolean.parseBoolean(value);
			wordMatchCount.put(key, wordMatchCount.get(key) + (parseBoolean ? 1 : 0));
			break;
		case METHOD_WORD_OCCURRENCE_COUNT:
			wordMatchCount.put(key, wordMatchCount.get(key) + Integer.parseInt(value));
			break;
		case METHOD_PATTERN_EXIST:
			boolean pattern = Boolean.parseBoolean(value);
			patternMatchCount.put(key, patternMatchCount.get(key) + (pattern ? 1 : 0));
			break;
		case METHOD_PATTERN_MATCH_COUNT:
			patternMatchCount.put(key, patternMatchCount.get(key) + Integer.parseInt(value));
			break;
		case METHOD_TOTAL_WORD_COUNT:
			totalWordCount.put(key, totalWordCount.get(key) + Integer.parseInt(value));
			break;
		case METHOD_TOTAL_LINE_COUNT:
			totalLineCount.put(key, totalLineCount.get(key) + Long.parseLong(value));
			break;
		}
//		if(doKeepCount.get(key)) {
//			count.put(key, count.get(key) + 1L);
//		}
//		if(doKeepDistinctCount.get(key)){
//			if(!distinctCountArray.get(key).contains(value)) {
//				distinctCount.put(key, ((Long) distinctCount.get(key)).longValue() + Long.parseLong(value));
//				distinctCountArray.get(key).add(value);
//			}
//		}
//		if(doKeepTotal.get(key)){
//			total.put(key, total.get(key).add(new BigDecimal(value)));
//		}
//		if(doKeepMax.get(key)) {
//			boolean isMax = false;
//			if(max.get(key) == null) {
//				isMax = true;
//			} else if(max.get(key).compareTo(new BigDecimal(value)) == -1) {
//				isMax = true;
//			}  
//			if(isMax) {
//				max.put(key, new BigDecimal(value));
//			}
//		}
//		if(doKeepMin.get(key)) {
//			boolean isMin = false;
//			if(min.get(key) == null) {
//				isMin = true;
//			} else if(min.get(key).compareTo(new BigDecimal(value)) == 1) {
//				isMin = true;
//			}
//			if(isMin) {
//				min.put(key, new BigDecimal(value));
//			}
//		}
//		if(doKeepWordOccurrenceCount.get(key)) {
//			wordMatchCount.put(key, wordMatchCount.get(key) + Integer.parseInt(value));
//		}
//		if(doKeepPatternMatchCount.get(key)) {
//	        patternMatchCount.put(key, patternMatchCount.get(key) + Integer.parseInt(value));
//		}
//		if(doKeepTotalWordCount.get(key)) {
//			totalWordCount.put(key, totalWordCount.get(key) + Integer.parseInt(value));
//		}
//		if(doKeepTotalLineCount.get(key)) {
//			totalLineCount.put(key, totalLineCount.get(key) + Long.parseLong(value));
//		}
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
	
	public List<UserDefinedTag> getCustomTagList() {
		List<UserDefinedTag> tagList = new ArrayList<UserDefinedTag>();
		
		HashMap<String, Object> tagsMap = getResultTags();
		Iterator<String> iter = tagsMap.keySet().iterator();
		UserDefinedTag tag = null;
		while (iter.hasNext()) {
			String key = iter.next();
			tag = new UserDefinedTag(key, tagsMap.get(key));
			tagList.add(tag);
		}
		
		return tagList;
	};
	
	public final HashMap<String, Object> getResultTags() {
		HashMap<String, Object> resultTags = new HashMap<String, Object>();
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			if(satisfied.get(tag.getName())) {
				Object value = null;
				if(tag.getMethod()!=METHOD_NONE) {
					int method = tag.getMethod();
					switch (method) {
					case METHOD_SUM:
						value = total.get(tag.getName()).doubleValue(); // double
						break;
					case METHOD_COUNT:
						value = count.get(tag.getName()).longValue(); // long
						break;
					case METHOD_AVG:
						value = total.get(tag.getName()).divide(new BigDecimal(count.get(tag.getName())), RoundingMode.HALF_UP).doubleValue(); // double
						break;
					case METHOD_MAX:
						value = max.get(tag.getName()).doubleValue(); // BigDecimal - change to double
						break;
					case METHOD_MIN:
						value = min.get(tag.getName()).doubleValue(); // BigDecimal - change to double
						break;
					case METHOD_DISTINCT_COUNT:
						Object tmp = distinctCount.get(tag.getName()); // long(reducer, combiner) / string(mapper)
						if (tmp instanceof String)
							value = (String) tmp;
						else if (tmp instanceof Long)
							value = ((Long) tmp).longValue();
						break;
					case METHOD_WORD_OCCURRENCE_COUNT:
						value = wordMatchCount.get(tag.getName()).longValue(); // long
						break;
					case METHOD_WORD_EXIST:
						value = wordMatchCount.get(tag.getName()).longValue() > 0; // boolean
						break;
					case METHOD_PATTERN_MATCH_COUNT:
						value = patternMatchCount.get(tag.getName()).longValue(); // long
						break;
					case METHOD_PATTERN_EXIST:
						value = patternMatchCount.get(tag.getName()).longValue() > 0; // boolean
						break;
					case METHOD_TOTAL_WORD_COUNT:
						value = totalWordCount.get(tag.getName()).longValue();  // long
						break;
					case METHOD_TOTAL_LINE_COUNT:
						value = totalLineCount.get(tag.getName()).longValue(); // long
						break;
					}
//					if(method==METHOD_SUM) {
//						value = total.get(tag.getName()).doubleValue(); // double
//					} else if(method==METHOD_COUNT) {
//						value = count.get(tag.getName()).longValue(); // long
//					} else if(method==METHOD_AVG) {
//						value = total.get(tag.getName()).divide(new BigDecimal(count.get(tag.getName())), RoundingMode.HALF_UP).doubleValue(); // double
//					} else if(method == METHOD_MAX) {
//						value = max.get(tag.getName()).doubleValue(); // BigDecimal - change to double
//					} else if(method == METHOD_MIN) {
//						value = min.get(tag.getName()).doubleValue(); // BigDecimal - change to double
//					} else if(method==METHOD_DISTINCT_COUNT) {
//						Object tmp = distinctCount.get(tag.getName()); // long / string
//						if (tmp instanceof String)
//							value = (String) tmp;
//						else if (tmp instanceof Long)
//							value = ((Long) tmp).longValue();
//							
//					} else if(method == METHOD_WORD_OCCURRENCE_COUNT) {
//						value = wordMatchCount.get(tag.getName()).longValue(); // long
//					} else if(method == METHOD_WORD_EXIST) {
//						value = wordMatchCount.get(tag.getName()).longValue() > 0; // boolean
//					} else if(method == METHOD_PATTERN_MATCH_COUNT) {
//						value = patternMatchCount.get(tag.getName()).longValue(); // long
//					} else if(method == METHOD_PATTERN_EXIST) {
//						value = patternMatchCount.get(tag.getName()).longValue() > 0; // boolean
//					} else if(method == METHOD_TOTAL_WORD_COUNT) {
//						value = totalWordCount.get(tag.getName()).longValue();  // long
//					} else if(method == METHOD_TOTAL_LINE_COUNT) {
//						value = totalLineCount.get(tag.getName()).longValue(); // long
//					}
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
	
	final protected boolean isSatisfied(String tagName) {
		return satisfied.get(tagName);
	}
	
	final protected boolean isMethod(String tagName) {
		return isMethod.get(tagName);
	}
	
	final public void addMetaData(Metadata metadata) throws Exception
	{
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			if(isSatisfied(tag.getName())) {
				if ( ! isMethod(tag.getName())) {
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(tag.getSubstitutionValue()));
					continue;
				}

				if (tagOperations.get(tag.getName()) == null)
					return;
				
				int operation = tagOperations.get(tag.getName());
				System.out.println("operation : " + operation);
				switch (operation) {
				case OPERATION_WORD_EXISTS:
					metadata.add(alterSequence(tag.getName()), String
							.valueOf(wordMatchCount.get(tag.getName()) > 0));
					break;
				case OPERATION_WORD_MATCH_COUNT:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(wordMatchCount.get(tag.getName())));
					break;
				case OPERATION_PATTERN_EXISTS:
					metadata.add(alterSequence(tag.getName()), String
							.valueOf(patternMatchCount.get(tag.getName()) > 0));
					break;
				case OPERATION_PATTERN_MATCH_COUNT:
					metadata.add(alterSequence(tag.getName()), String
							.valueOf(patternMatchCount.get(tag.getName())));
					break;
				case OPERATION_TOTAL_WORD_COUNT:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(totalWordCount.get(tag.getName())));
					break;
				case OPERATION_TOTAL_LINE_COUNT:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(totalLineCount.get(tag.getName())));
					break;
				case OPERATION_META_OCCURENCE_COUNT:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(wordMatchCount.get(tag.getName())));
					break;
				case OPERATION_META_EXIST:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(wordMatchCount.get(tag.getName()) > 0));
					break;
				case OPERATION_COPY:
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(copyValues.get(tag.getName())));
					break;
				}
			}
		}
	}
	
	protected String alterSequence(String sequence) {
		sequence = sequence.replaceAll("[^a-zA-Z0-9]+","_");
		sequence = sequence.replace("-", "_");
		sequence = sequence.replace(".", "_");
		sequence = sequence.replace(" ", "_");
		sequence = sequence.toUpperCase();
		return sequence;
	}
	
	protected void alterMetadata(TableMetadata tableMetadata, Metadata metadata) {
		
		String[] names = metadata.names();
		
		for (int i=0; i<names.length; i++){
			String key = names[i];
			String value = metadata.get(key); 
			
			metadata.remove(key);
			
			key = alterSequence(key);

			if (tableMetadata.getColumnMetadataByColumnName(key) != null) {
				if (value != null && !value.isEmpty()) {
					metadata.add(key, value);
				}
			}
		}
	}
	
	protected void parseLine(String line, Metadata metadata) {
		Scanner sc = null;
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			
			if (tagOperations.get(tag.getName()) == null)
				return;
			
			int operation = tagOperations.get(tag.getName());
			switch(operation) {
			case OPERATION_WORD_EXISTS:
			case OPERATION_WORD_MATCH_COUNT:
				sc = new Scanner(line);
				String matchWithWord = wordToMatch.get(tag.getName());
				int count = 0;
				
				while (sc.hasNext()) {
					String word = sc.next();
					if(word.trim().equalsIgnoreCase(matchWithWord)) {
						count++;
					}
				}
				wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + count);
				sc.close();
				break;
			case OPERATION_PATTERN_EXISTS:
			case OPERATION_PATTERN_MATCH_COUNT:
				Matcher  matcher2 = patterns.get(tag.getName()).matcher(line);
				int matchCount = 0;
		        while (matcher2.find()) {
		        	matchCount++;	
		        }
		        patternMatchCount.put(tag.getName(), patternMatchCount.get(tag.getName()) + matchCount);
		        break;
			case OPERATION_TOTAL_WORD_COUNT:
				sc = new Scanner(line);
				int cnt = 0;
				while(sc.hasNext()) {
					cnt++; sc.next();
				}
				totalWordCount.put(tag.getName(), totalWordCount.get(tag.getName()) + cnt);
				break;
			case OPERATION_TOTAL_LINE_COUNT:
				totalLineCount.put(tag.getName(), totalLineCount.get(tag.getName()) + 1);
				break;
			case OPERATION_META_EXIST :
			case OPERATION_META_OCCURENCE_COUNT :
				sc = new Scanner(line);
				
				String colNameForMatch = metaWordToMatch.get(tag.getName()); 
				
//				System.out.println("match with value for column : " + colNameForMatch);
				
				String valueForColumn = null;
				
				for(int j=0; j<metadata.names().length; j++) {
					String key = metadata.names()[j];
					String value = metadata.get(key);
					
//					System.out.println("Current metadata key: " +  alterSequence(key) + ", looking for column: " + colNameForMatch);
					
					if(alterSequence(key).equalsIgnoreCase(colNameForMatch)) {
						valueForColumn = value;
//						System.out.println("Matching column found");
						break;
					}
				}
				
				count = 0;
				
				while (sc.hasNext()) {
					String word = sc.next();
					if(word.trim().equalsIgnoreCase(valueForColumn)) {
//						System.out.println("Current word: " +  words[j].trim() + ", looking for word: " + valueForColumn);
						count++;
					}
				}
				wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + count);
				break;
			case OPERATION_COPY :
				colNameForMatch = metaWordToMatch.get(tag.getName()); 
				valueForColumn = null;
				
				for(int j=0; j<metadata.names().length; j++){
					String key = metadata.names()[j];
					String value = metadata.get(key);
					
//					System.out.println("Current metadata key: " +  alterSequence(key) + ", looking for column: " + colNameForMatch);
					
					if(alterSequence(key).equalsIgnoreCase(colNameForMatch)) {
						copyValues.put(tag.getName(), value);
						break;
					}
				}
				break;	
			}
		}
	}

}