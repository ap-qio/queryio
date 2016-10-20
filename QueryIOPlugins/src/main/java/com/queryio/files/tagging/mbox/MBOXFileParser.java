package com.queryio.files.tagging.mbox;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.files.tagging.IFileParser;

public class MBOXFileParser extends IFileParser{
	
	public MBOXFileParser(JSONObject tagsJSON) {
		super(tagsJSON);
		JSONArray fieldsJSON = (JSONArray) tagsJSON.get(FIELDS_KEY);
		
		columns = new HashMap<Integer, String>();
		
		for (int i = 0; i < fieldsJSON.size(); i++) {
			JSONObject field = (JSONObject) fieldsJSON.get(i);
			columns.put(Integer.parseInt(String.valueOf(field.get(COL_INDEX_KEY))), String.valueOf(field.get(COL_NAME_KEY)).toUpperCase());
		}
		
		JSONObject parsingDetailsJSON = (JSONObject) tagsJSON.get(PARSE_DETAILS_KEY);
		
        encoding = String.valueOf(parsingDetailsJSON.get(ENCODING_KEY));
        skipAll = Boolean.parseBoolean(String.valueOf(parsingDetailsJSON.get(ERROR_ACTION_KEY)));	
	}

	String adhocParserEncoding = "encoding";
	
	private HashMap<String, String> curValueMap = new HashMap<String, String>();
	private Map<Integer, String> columns = new TreeMap<Integer, String>();

	Map<String, String> coreTags;
	
	private String encoding;
	private boolean skipAll;	
	
	private String contentId = "CONTENT_ID";
	private String language = "CONTENT_LANGUAGE";
	private String contentMD5 = "MD5_CONTENT";
	private String contentType = "CONTENT_TYPE";
	private String description = "CONTENT_DESCRIPTION";
	private String disposition = "CONTENT_DISPOSITION";
	private String contentEncoding = "CONTENT_ENCODING";
	private String fileName = "FILENAME";
	private String from = "MAIL_FROM";
	private String lineCount = "LINE_COUNT";
	private String messageId = "MESSAGE_ID";
	private String messageNumber = "MESSAGE_NUMBER";
	private String receivedDate = "RECEIVED_DATE";
	private String replyTo = "REPLY_TO";
	private String sender = "SENDER";
	private String sentDate = "SENT_DATE";
	private String subject = "MAIL_SUBJECT";
	private String size = "MAIL_SIZE";
	
//	header.put(1, "CONTENT_ID");
//	header.put(2, "CONTENT_LANGUAGE");	//Array
//	header.put(3, "MD5_CONTENT");
//	header.put(4, "CONTENT_TYPE");
//	header.put(5, "CONTENT_DESCRIPTION");
//	header.put(6, "CONTENT_DISPOSITION");
//	header.put(7, "CONTENT_ENCODING");
//	header.put(8, "FILENAME");
//	header.put(9, "MAIL_FROM");	//Array
//	header.put(10, "LINE_COUNT");
//	header.put(11, "MESSAGE_ID");
//	header.put(12, "MESSAGE_NUMBER");
//	header.put(13, "RECEIVED_DATE");//Date
//	header.put(14, "REPLY_TO");//Array
//	header.put(15, "SENDER");
//	header.put(16, "SENT_DATE");
//	header.put(17, "MAIL_SUBJECT");
//	header.put(18, "MAIL_SIZE");
	
	@Override
	public void parse(InputStream is, Map<String, String> coreTags) throws Exception {
		
		this.coreTags = coreTags;
		try
		{
			Session session =  Session.getDefaultInstance(new Properties());
			MimeMessage message = new MimeMessage(session, is);
			StringBuilder sb = new StringBuilder();
			System.out.println("coreTags: " + this.coreTags);
			if (coreTags != null) {
				Iterator<String> it = coreTags.keySet().iterator();
				while (it.hasNext())
				{
					String key = it.next();
					curValueMap.put(key, coreTags.get(key));
				}
			}
			
			System.out.println("columns: " + columns);
			int asd=0;
			System.out.println("i: " + (asd++) + " message: " + message.getContentID());
	        System.out.println("i: " + (asd++) + " message: " + message.getContentLanguage());
	        System.out.println("i: " + (asd++) + " message: " + message.getContentMD5());
	        System.out.println("i: " + (asd++) + " message: " + message.getContentType());
	        System.out.println("i: " + (asd++) + " message: " + message.getDescription());
	        System.out.println("i: " + (asd++) + " message: " + message.getDisposition());
	        System.out.println("i: " + (asd++) + " message: " + message.getEncoding());
	        System.out.println("i: " + (asd++) + " message: " + message.getFileName());
	        System.out.println("i: " + (asd++) + " message: " + message.getFrom());
	        System.out.println("i: " + (asd++) + " message: " + message.getLineCount());
	        System.out.println("i: " + (asd++) + " message: " + message.getMessageID());
	        System.out.println("i: " + (asd++) + " message: " + message.getMessageNumber());
	        System.out.println("i: " + (asd++) + " message: " + message.getReceivedDate());
	        System.out.println("i: " + (asd++) + " message: " + message.getReplyTo());
	        System.out.println("i: " + (asd++) + " message: " + message.getSender());
	        System.out.println("i: " + (asd++) + " message: " + message.getSentDate());
	        System.out.println("i: " + (asd++) + " message: " + message.getSubject());
	        System.out.println("i: " + (asd++) + " message: " + message.getSize());
			
			if(message.getContentID() != null) {	        	
				if(columns.containsValue(contentId)) {
	        		String value = message.getContentID().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(contentId, value);
	        		}
	        	}
			}
	        if(message.getContentLanguage() != null) {	        	
	        	sb.append("");
	        	String temp[] = message.getContentLanguage();
	        	if (temp != null)
	        	{
	        		for (int i=0; i<temp.length; i++)
	        		{
	        			sb.append(temp[i]);
	        			sb.append(" ");
	        		}
	        	}
	        	if(columns.containsValue(language)) {
	        		String value = sb.toString().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(language, value);
	        		}
	        	}
	        }
	        if(message.getContentMD5() != null) {	        	
	        	if(columns.containsValue(contentMD5)) {
	        		String value = message.getContentMD5().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(contentMD5, value);
	        		}
	        	}
	        }
	        if(message.getContentType() != null) {	        	
	        	if(columns.containsValue(contentType)) {
	        		String value = message.getContentType().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(contentType, value);
	        		}
	        	}
	        }	        
	        if(message.getDescription() != null) {	        	
	        	if(columns.containsValue(description)) {
	        		String value = message.getDescription().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(description, value);
	        		}
	        	}
	        }
	        if(message.getDisposition() != null) {	        	
	        	if(columns.containsValue(disposition)) {
	        		String value = message.getDisposition().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(disposition, value);
	        		}
	        	}
	        }
	        if(message.getEncoding() != null) {	        	
	        	if(columns.containsValue(contentEncoding)) {
	        		String value = message.getEncoding().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(contentEncoding, value);
	        		}
	        	}
	        }
	        if(message.getFileName() != null) {	        	
	        	if(columns.containsValue(fileName)) {
	        		String value = message.getFileName().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(fileName, value);
	        		}
	        	}
	        }
	        Address temp1[] = null;
	        if(message.getFrom() != null) {	        	
	        	
	        	if(columns.containsValue(from)) {
		        	temp1 = message.getFrom();
		        	sb.setLength(0);
		        	if (temp1 != null)
		        	{
		        		for (int i=0; i<temp1.length; i++)
		        		{
		        			sb.append(temp1[i]);
		        			sb.append(" ");
		        		}
		        	}
	        		String value = sb.toString().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(from, value);
	        		}
	        	}
	        }
	        if(String.valueOf(message.getLineCount()) != null) {	        	
	        	if(columns.containsValue(lineCount)) {
	        		String value = String.valueOf(message.getLineCount());
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(lineCount, value);
	        		}
	        	}
	        }
	        if(message.getMessageID() != null) {	        	
	        	if(columns.containsValue(messageId)) {
	        		String value = message.getMessageID().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(messageId, value);
	        		}
	        	}
	        }
	        if(String.valueOf(message.getMessageNumber()) != null) {	        	
	        	if(columns.containsValue(messageNumber)) {
	        		String value = String.valueOf(message.getMessageNumber());
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(messageNumber, value);
	        		}
	        	}
	        }
	        if(message.getReceivedDate() != null) {	        	
	        	Date date = message.getReceivedDate();
	        	if(columns.containsValue(receivedDate)) {
	        		String value = String.valueOf(new Timestamp(date.getTime()));
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(receivedDate, value);
	        		}
	        	}
	        }
	        if(message.getReplyTo() != null) {	 
	        	if(columns.containsValue(replyTo)) {
		        	temp1 = message.getReplyTo();
		        	sb.setLength(0);
		        	if (temp1 != null)
		        	{
		        		for (int i=0; i<temp1.length; i++)
		        		{
		        			sb.append(temp1[i]);
		        			sb.append(" ");
		        		}
		        	}
	        		String value = sb.toString().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(replyTo, value);
	        		}     
	        	}
	        }
	        System.out.println("sender :");
	        System.out.println(message.getSender());
	        if(message.getSender() != null) {
	        	System.out.println(message.getSender());
	        	if(columns.containsValue(sender)) {
	        		String value = String.valueOf(message.getSender());
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(sender, value);
	        		}
	        	}
	        }
	        if(message.getSentDate() != null) {	        		        	
	        	Date date = message.getSentDate();
	        	if(columns.containsValue(sentDate)) {
	        		String value = String.valueOf(new Timestamp(date.getTime()));
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(sentDate, value);
	        		}
	        	}
	        }
	        if(message.getSubject() != null) {	        	
	        	if(columns.containsValue(subject)) {
	        		String value = message.getSubject().trim();
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(subject, value);
	        		}
	        	}
	        }
	        if(String.valueOf(message.getSize()) != null) {	        	
	        	if(columns.containsValue(size)) {
	        		String value = String.valueOf(message.getSize());
	        		if( ! value.isEmpty()) {
	        			curValueMap.put(size, value);
	        		}
	        	}
	        }
			evaluateCurrentEntry(curValueMap, "");	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new IOException(e);
		}
	}
}