package com.queryio.demo.extended.metadata;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.Tag;
import com.queryio.plugin.extended.metadata.TableMetadata;

@SuppressWarnings("PMD.AvoidUsingShortType")
public class MetadataContentParser extends IContentParser {
	final String WHITESPACE = " ";
	
	
	Map<String, Long> wordMatchCount = new HashMap<String, Long>();
	Map<String, Long> patternMatchCount = new HashMap<String, Long>();
	Map<String, Long> totalWordCount = new HashMap<String, Long>();
	Map<String, Long> totalLineCount = new HashMap<String, Long>();
	
	Map<String, String> wordToMatch = new HashMap<String, String>();
	Map<String, String> metaWordToMatch = new HashMap<String, String>();
		
	Map<String, String> patternToMatch = new HashMap<String, String>();
	
	Map<String, String> copyValues = new HashMap<String, String>();
	
	Map<String, Short> tagOperations = new HashMap<String, Short>();
	
	Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	
	final short OPERATION_NO_OPERATION = 0;
	final short OPERATION_WORD_EXISTS = 1;
	final short OPERATION_WORD_MATCH_COUNT = 2;
	final short OPERATION_PATTERN_EXISTS = 3;
	final short OPERATION_PATTERN_MATCH_COUNT = 4;
	final short OPERATION_TOTAL_WORD_COUNT = 5;
	final short OPERATION_TOTAL_LINE_COUNT = 6;
	final short OPERATION_META_OCCURENCE_COUNT = 7;
	final short OPERATION_COPY = 8;
	final short OPERATION_META_EXIST = 9;
	
	public MetadataContentParser(JSONObject tagsJSON, Map<String, String> coreTags) {
		super(tagsJSON, coreTags);
	}
	
	private void initMethod(String tagName, String tagValue) {
		if(tagValue.contains("WordExists")) {
			tagOperations.put(tagName, OPERATION_WORD_EXISTS);
			wordToMatch.put(tagName, tagValue.substring("WordExists('".length(), tagValue.lastIndexOf("')")));
		} else if(tagValue.contains("WordOccurrenceCount")) {
			tagOperations.put(tagName, OPERATION_WORD_MATCH_COUNT);
			wordToMatch.put(tagName, tagValue.substring("WordOccurrenceCount('".length(), tagValue.lastIndexOf("')")));
		} else if(tagValue.contains("OccurrenceCount")) {
			tagOperations.put(tagName, OPERATION_META_OCCURENCE_COUNT);
			metaWordToMatch.put(tagName, tagValue.substring("OccurrenceCount(".length(), tagValue.lastIndexOf(")")));
		} else if(tagValue.contains("isExist")) {
			tagOperations.put(tagName, OPERATION_META_EXIST);
			metaWordToMatch.put(tagName, tagValue.substring("isExist(".length(), tagValue.lastIndexOf(")")));
		} else if(tagValue.contains("PatternExists")) {
			tagOperations.put(tagName, OPERATION_PATTERN_EXISTS);
			String patternToMatch = tagValue.substring("PatternExists('".length(), tagValue.lastIndexOf("')"));
			patterns.put(tagName, Pattern.compile(patternToMatch));
		} else if(tagValue.contains("PatternMatchCount")) {
			tagOperations.put(tagName, OPERATION_PATTERN_MATCH_COUNT);
			String patternToMatch = tagValue.substring("PatternMatchCount('".length(), tagValue.lastIndexOf("')"));
			patterns.put(tagName, Pattern.compile(patternToMatch));
		} else if(tagValue.contains("TotalWordCount")) {
			tagOperations.put(tagName, OPERATION_TOTAL_WORD_COUNT);
		} else if(tagValue.contains("TotalLineCount")) {
			tagOperations.put(tagName, OPERATION_TOTAL_LINE_COUNT);
		} else if(tagValue.contains("Copy")) {
			tagOperations.put(tagName, OPERATION_COPY);
			metaWordToMatch.put(tagName, tagValue.substring("Copy(".length(), tagValue.lastIndexOf(")")));
		} else {
			tagOperations.put(tagName, OPERATION_NO_OPERATION);
		}
	}

	@Override
	public void parse(Reader reader, TableMetadata tableMetadata, Metadata metadata) throws Exception {
		List<Tag> tags = super.getTags();
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			
			initMethod(tag.getName(), tag.getSubstitutionValue());
			
			wordMatchCount.put(tag.getName(), 0l);
			patternMatchCount.put(tag.getName(), 0l);
			totalWordCount.put(tag.getName(), 0l);
			totalLineCount.put(tag.getName(), 0l);
		}
		
		BufferedReader br = new BufferedReader(reader);
		
		String str;
		
		boolean isFirstTime = true;
		
		while ((str = br.readLine()) != null) {
			try {
				this.parseLine(str, metadata);
				
				if(isFirstTime) {
					for(int i=0; i<metadata.names().length; i++){
						String key = metadata.names()[i];
						String value = metadata.get(key);
						System.out.println("FirstTime: Key: " + key + " , value: " + value);
					}
					
					isFirstTime = false;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		alterMetadata(tableMetadata, metadata);
		
		HashMap<String, String> curValueMap = new HashMap<String, String>();
		
		if (coreTags != null) {
			Iterator<String> it = coreTags.keySet().iterator();
			while (it.hasNext())
			{
				String key = it.next();
				curValueMap.put(key, coreTags.get(key));
			}
		}

		for(int i=0; i<metadata.names().length; i++){
			String key = metadata.names()[i];
			String value = metadata.get(key); 
			
			curValueMap.put(key, value);
		}
		
		this.evaluateCurrentEntry(curValueMap);
		
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			if(isSatisfied(tag.getName())) {
				if ( ! isMethod(tag.getName())) {
					metadata.add(alterSequence(tag.getName()),
							String.valueOf(tag.getSubstitutionValue()));
					continue;
				}

				short operation = tagOperations.get(tag.getName());
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
	
	private String alterSequence(String sequence) {
		sequence = sequence.replaceAll("[^a-zA-Z0-9]+","_");
		sequence = sequence.replace("-", "_");
		sequence = sequence.replace(".", "_");
		sequence = sequence.replace(" ", "_");
		sequence = sequence.toUpperCase();
		return sequence;
	}
	
	private void alterMetadata(TableMetadata tableMetadata, Metadata metadata) {
		for(int i=0; i<metadata.names().length; i++){
			String key = metadata.names()[i];
			String value = metadata.get(key); 
			
			metadata.remove(key);
			
			key = alterSequence(key);
			
			if(tableMetadata.getColumnMetadataByColumnName(key) != null){
				if(value != null && !value.isEmpty()){
					metadata.add(key, value);
				}
			}
		}
	}
	
	private void parseLine(String line, Metadata metadata) {
		System.out.println("line: " + line);
		
		List<Tag> tags = super.getTags();
		for(int i=0; i<tags.size(); i++) {
			Tag tag = tags.get(i);
			
			short operation = tagOperations.get(tag.getName());
		
			switch(operation) {
			case OPERATION_WORD_EXISTS:
			case OPERATION_WORD_MATCH_COUNT:
				String[] words = line.split(WHITESPACE);
				String matchWithWord = wordToMatch.get(tag.getName());
				int count = 0;
				
				for(int j=0; j<words.length; j++) {
					if(words[j].trim().equalsIgnoreCase(matchWithWord)) {
						count++;
					}
				}
				wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + count);
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
				totalWordCount.put(tag.getName(), totalWordCount.get(tag.getName()) + line.split(WHITESPACE).length);
				break;
			case OPERATION_TOTAL_LINE_COUNT:
				totalLineCount.put(tag.getName(), totalLineCount.get(tag.getName()) + 1);
				break;
			case OPERATION_META_EXIST :
			case OPERATION_META_OCCURENCE_COUNT :
				words = line.split(WHITESPACE);
				
				String colNameForMatch = metaWordToMatch.get(tag.getName()); 
				
				System.out.println("match with value for column : " + colNameForMatch);
				
				String valueForColumn = null;
				
				for(int j=0; j<metadata.names().length; j++){
					String key = metadata.names()[j];
					String value = metadata.get(key);
					
					System.out.println("Current metadata key: " +  alterSequence(key) + ", looking for column: " + colNameForMatch);
					
					if(alterSequence(key).equalsIgnoreCase(colNameForMatch)) {
						valueForColumn = value;
						System.out.println("Matching column found");
						break;
					}
				}
				
				count = 0;
				
				for(int j=0; j<words.length; j++) {
					if(words[j].trim().equalsIgnoreCase(valueForColumn)) {
						System.out.println("Current word: " +  words[j].trim() + ", looking for word: " + valueForColumn);
						count++;
					}
				}
				wordMatchCount.put(tag.getName(), wordMatchCount.get(tag.getName()) + count);
				break;
			case OPERATION_COPY :
				words = line.split(WHITESPACE);
				
				colNameForMatch = metaWordToMatch.get(tag.getName()); 
				
				System.out.println("match with value for column : " + colNameForMatch);
				
				valueForColumn = null;
				
				for(int j=0; j<metadata.names().length; j++){
					String key = metadata.names()[j];
					String value = metadata.get(key);
					
					System.out.println("Current metadata key: " +  alterSequence(key) + ", looking for column: " + colNameForMatch);
					
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
