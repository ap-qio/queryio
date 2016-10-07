package com.queryio.core.adhoc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;

public class AdHocQuerySampleFileUpload extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String COL_FILEPATH = "FILEPATH";
	static String DT_VARCHAR = MetadataConstants.GENERIC_DATA_TYPE_STRING;
	static String DT_VARCHAR_DEFAULT [] = {"(128)" , "(255)" , "(512)" , "(1280)"};
	static int MAX_LENGTH = 1;
	static String DT_DATETIME = MetadataConstants.GENERIC_DATA_TYPE_TIMESTAMP;
	static String DT_DECIMAL = MetadataConstants.GENERIC_DATA_TYPE_DECIMAL;
	static String DT_INTEGER = MetadataConstants.GENERIC_DATA_TYPE_INTEGER;
	static String DT_BOOLEAN = MetadataConstants.GENERIC_DATA_TYPE_BOOLEAN;
	static int MAX_DATA_ROWS = 10;
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		if (!ServletFileUpload.isMultipartContent(req)) {
            throw new IllegalArgumentException("Request is not multipart, please use 'multipart/form-data' enctype for your form.");
        }

        PrintWriter writer = res.getWriter();
        res.setContentType("text/plain");
        JSONArray json = new JSONArray();
        JSONObject jsono = new JSONObject();
        
		try
		{
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("File upload request recevied from host: " + req.getRemoteAddr() + ", user: " + req.getRemoteUser());
			
			long fileSize = -1;
			String fileName = null;
			String delimiter = null;
			String separatorValue = null;
			String encodingType = null;
			String adHocType = null;
			int noOfRecords = -1;
			boolean isFirstLineHeader = false;
			boolean toBeParsed = false;
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(Integer.MAX_VALUE);
			
			ServletFileUpload upload = new ServletFileUpload();
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Max File Size: " + upload.getFileSizeMax());

			FileItemStream item;
			FileItemIterator iterator;
			
			iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				item = iterator.next();
            	if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Item: " + item.getFieldName());
            	if (item.isFormField())
            	{
            		if (item.getFieldName().equals("fileSize"))
            		{
						String data = getValue(item);
						if ((data != null) && !data.isEmpty())
							fileSize = Long.valueOf(data);
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fileSize: " + fileSize);
					}
            		else if (item.getFieldName().equals("patternExpr"))
            		{
						delimiter = getValue(item);
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("delimiter: " + delimiter);
					}
            		else if (item.getFieldName().equals("separatorValue"))
            		{
            			separatorValue = getValue(item);
            			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("separatorValue: " + separatorValue);
            		}
            		else if (item.getFieldName().equals("encodingType"))
            		{
            			encodingType = getValue(item);
            			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("encodingType: " + encodingType);
            		}
            		else if (item.getFieldName().equals("adHocType"))
            		{
            			adHocType = getValue(item);
            			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("adHocType: " + adHocType);
            		}
            		else if(item.getFieldName().equals("isFirstLineHeader"))
            		{
 						String data = getValue(item);
 						if (headerToBeParsed(data))
 						{
 							isFirstLineHeader = true;
 						}
 						else
 						{
 							isFirstLineHeader = false;
 						}
 						
 						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("isFirstLineHeader: " + isFirstLineHeader + " data: " + data);
 					}
            		else if (item.getFieldName().equals("records"))
            		{
            			noOfRecords = Integer.valueOf(getValue(item));
						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("noOfRecords: " + noOfRecords);
					}
				}
            	else
				{
            		fileName = item.getName();
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Got an uploaded file: " + item.getFieldName()
							+ ", name = " + fileName);
					jsono.put("name", fileName);
					
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Processing Upload Request");
					
					InputStream inStream = null;
					OutputStream outStream = null;
					try
					{
						String path = EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.ADHOC_SAMPLE_FILE_DIR;//+ QueryIOConstants.SAMPLE_FILE_DIR;
						File parentDir = new File(path);
						
						if (!parentDir.exists())
							parentDir.mkdirs();
						
						inStream = new BufferedInputStream(item.openStream(), 16*1024);
						outStream = new FileOutputStream(new File(parentDir.getAbsolutePath() + File.separator + fileName));
						
						int read = 0;
						final byte[] bytes = new byte[1024];
						while ((read = inStream.read(bytes)) != -1)
						{
							outStream.write(bytes, 0, read);
						}
						
						toBeParsed = true;
					}
					finally
					{
						try {
							if (inStream != null) {
								inStream.close();
							}
						} catch(Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
						try {
							if (outStream != null) {
								outStream.close();
							}
						} catch(Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
            }
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("toBeParsed: " + toBeParsed);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("isFirstLineHeader: " + isFirstLineHeader);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("noOfRecords: " + noOfRecords);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("delimiter: " + delimiter);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("separatorValue: " + separatorValue);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("encodingType: " + encodingType);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("adHocType: " + adHocType);
			
			File sampleDir = new File(EnvironmentalConstants.getAppHome() + QueryIOConstants.ADHOC_SAMPLE_FILE_DIR);
			if (!sampleDir.exists())
				sampleDir.mkdir();
			
			File file = new File(sampleDir.getAbsolutePath() + File.separator + fileName);
			if (toBeParsed )
			{
				if (QueryIOConstants.ADHOC_TYPE_JSON.equalsIgnoreCase(adHocType))
				{
					jsono = jsonParser(file, encodingType, noOfRecords);
				}
				else if (QueryIOConstants.ADHOC_TYPE_MBOX.equalsIgnoreCase(adHocType))
				{
					jsono = emlParser(file);
				}
				else if (QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(adHocType))
				{
					jsono = keyValueFileParser(file, encodingType, delimiter, separatorValue, noOfRecords);
				}
				else if (QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(adHocType))
				{
					jsono = xmlParser(file, delimiter, noOfRecords); //Delimiter should be XML data node type in UI
				}
				else if (QueryIOConstants.ADHOC_TYPE_REGEX.equalsIgnoreCase(adHocType))
				{
					jsono = regexFileParser(file, encodingType, delimiter, noOfRecords); //Delimiter should be REGEX data type in UI
				}
				else if (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(adHocType))
				{
					jsono = apacheLog4JFileParser(file, encodingType, delimiter, noOfRecords); //Delimiter should be LOG Pattern in UI "%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n"
				}
				else if(QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equalsIgnoreCase(adHocType))
				{
					jsono = apacheAccessLog(file, encodingType, delimiter, noOfRecords);
				}
				else		// For CSV and IISLOG
				{
					JSONObject columnDetails = new JSONObject();
					
					String[] columnTypes = null;
					JSONArray jsonArray = new JSONArray();
					
					BufferedReader br = null;
					String line = null;
					try
					{
	//					br = new BufferedReader(new FileReader(file));
						br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodingType));
						
						if (isFirstLineHeader)
						{
							line = br.readLine();
							
							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reading line: " + line);
							
							JSONObject header = new JSONObject();
							List<String> headers =  parseLine(line, delimiter, separatorValue);
							
							header.put(0, COL_FILEPATH);
							String str = null;
							
							for (int i=0; i<headers.size(); i++)
							{
								str = headers.get(i);
								if (str != null)
									str = str.toUpperCase().trim();
								
								header.put(i+1, str);
							}
							
							columnDetails.put("header", header);
						}
						
						columnDetails.put("details", jsonArray);
						
						int linesParsed = 0;
						
						boolean[] isString = null;
						boolean[] isDecimal = null;
						boolean[] isDateTime = null;
						boolean[] isInteger = null;
						boolean[] isBoolean = null;
						
						List<String> records = null;
						Class klass;
						JSONObject obj;
						JSONArray dataArray = new JSONArray();
						int demoRecords = 10;
						int recordsAdded = 0;
						int tempMax = 0;
						int columnCount = -1;
						while (((line = br.readLine()) != null) && (linesParsed < noOfRecords))
						{
							records = parseLine(line, delimiter, separatorValue);
						
	//						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Reading line: " + line);
	//						if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Parser line: " + records);
							
							if(linesParsed==0) {
								isBoolean = new boolean[records.size()];
								isString = new boolean[records.size()];
								isDecimal = new boolean[records.size()];
								isDateTime = new boolean[records.size()];
								isInteger = new boolean[records.size()];
								
								columnTypes = new String[records.size()];
							}
							
							linesParsed++;
							
							if (records == null)
								continue;
							
							for(int i=0; i<records.size(); i++) {
								klass = getClass(records.get(i));
								
								if(klass==Boolean.class) isBoolean[i] = true;
								else if(klass==Integer.class) isInteger[i] = true;
								else if(klass==Double.class) isDecimal[i] = true;
								else if(klass==Date.class) isDateTime[i] = true;
								else{
									
	
									isString[i] = true;
									if(records.get(i).toString().length() < 128 && (tempMax < MAX_LENGTH))
									{
										MAX_LENGTH = 1;
										tempMax = 1;
									}
									else if(records.get(i).toString().length() < 255 && (tempMax < MAX_LENGTH))
									{
										MAX_LENGTH = 2;
										tempMax = 2;
									}
									else if(records.get(i).toString().length() < 512 && (tempMax < MAX_LENGTH))
									{
										MAX_LENGTH = 3;
										tempMax = 3;
									}
									else if(records.get(i).toString().length() < 1280 && (tempMax < MAX_LENGTH))
									{
										MAX_LENGTH = 4;
										tempMax = 4;
									}
									
								}
							}
							
							if (recordsAdded < demoRecords)
							{
								records.add(0, fileName);
								if (columnCount == -1)
									columnCount = records.size();
								dataArray.add(records);
								recordsAdded++;
							}
						}
						
						if (!isFirstLineHeader)
						{
							JSONObject header = new JSONObject();
							for (int i=0; i<columnCount; i++)
							{
								header.put(i, "Column" + i);
							}
							columnDetails.put("header", header);
						}
						
						columnDetails.put("data", dataArray);
						
						if(columnTypes!=null) {
							// All records read. Types are also available.
							resetLowPriorities(isString, isDecimal, isDateTime, isInteger, isBoolean);
							
							obj = new JSONObject();
							obj.put("index", 0);
							obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
							jsonArray.add(obj);
							
							for(int i=0; i<columnTypes.length; i++) {
								if(isString[i]==true) {
									columnTypes[i] = (DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
								} else if(isDateTime[i]==true) {
									if (QueryIOConstants.ADHOC_TYPE_IISLOG.equalsIgnoreCase(adHocType))
										columnTypes[i] = (DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
									else
										columnTypes[i] = DT_DATETIME;
								} else if(isDecimal[i]==true) {
									columnTypes[i] = DT_DECIMAL;
								} else if(isInteger[i]==true) {
									columnTypes[i] = DT_INTEGER;
								} else if(isBoolean[i]==true) {
									columnTypes[i] = DT_BOOLEAN;
								}
								
								obj = new JSONObject();
								obj.put("index", i+1);
								obj.put("type", columnTypes[i]);
								jsonArray.add(obj);
								
	//							if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("obj: " + obj);
							}
							
							columnDetails.put("details", jsonArray);
							jsono.put("meta", columnDetails);
						}
					}
					finally
					{
						try {
							if (br != null) {
								br.close();
							}
						} catch(Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			}
			
			jsono.put("name", fileName);
			jsono.put("success", "File uploaded successfully.");
		}
		catch (Exception ex)
		{
			jsono.put("error", "Error occurred while uploading file.");
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		}
		finally
		{
			json.add(jsono);
		    writer.write(json.toString());
		    writer.close();
		    if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Final json = " + json.toJSONString());
		}
	}
	
	public boolean headerToBeParsed(String data)
	{
		return data.equalsIgnoreCase("on"); 
	}
	
	public String getValue(FileItemStream item) throws IOException{
		String theString = null;
		InputStream is = null;
		try{
			is = item.openStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			theString = writer.toString();
		} finally {
			try{
				if(is!=null)	is.close();
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return theString;
	}
	
	public String getFileExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	
	public String getFileNameWithoutExtension(String fileName){
		int index = fileName.lastIndexOf(".");
		if(index!=-1){
			return fileName.substring(0, index);
		} else {
			return fileName;
		}
	}
	
	private static JSONObject xmlParser(File fXmlFile, String xmlData, int noOfRecords) throws Exception
	{
		JSONObject columnDetails = new JSONObject();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		if (doc.getDocumentElement() != null)
			doc.getDocumentElement().normalize();
	 
		NodeList nList = doc.getElementsByTagName(xmlData);
	 
		JSONObject headerObject = new JSONObject();
		headerObject.put(0, COL_FILEPATH);

		JSONArray dataArray = new JSONArray();
		List<String> records = new ArrayList<String>();
		List<String> recordsTemp = new ArrayList<String>();

		boolean flag = false;
		int count = 1;
		int totalColumns;
		
		for (int temp = 0; temp < nList.getLength() && temp < noOfRecords; temp++)
		{
			Node nNode = nList.item(temp);
			NodeList children = nNode.getChildNodes();
			
			records = new ArrayList<String>();
			
			records.add(fXmlFile.getName());
			//getting #text while running from it 0 index and increment by 1  
			for (int i=1; i<children.getLength(); i+=2)
			{
				records.add(children.item(i).getTextContent());
				recordsTemp.add(children.item(i).getTextContent());
				if (!flag)
				{
					String str = children.item(i).getNodeName();
					if (str != null)
						str = str.toUpperCase().trim();
					
					headerObject.put(count, str);
					count++;
				}
			}
			flag = true;
			dataArray.add(temp , records);
		}
		totalColumns = headerObject.size();
		JSONObject meta = new JSONObject();
		
		// No need to subtract file path column
		JSONArray detail = dataTypeDetector(recordsTemp, totalColumns, fXmlFile.getName());
		
		meta.put("details", detail);
		meta.put("header" , headerObject);
		meta.put("data", dataArray);
		columnDetails.put("meta", meta);
		return columnDetails;
	}
	
	private static String appendLength(int length) {
		
		String size = DT_VARCHAR_DEFAULT[0];
		if (length < 128)
			size = DT_VARCHAR_DEFAULT[0];
		else if (length < 255)
			size = DT_VARCHAR_DEFAULT[1];
		else if (length < 512)
			size = DT_VARCHAR_DEFAULT[2];
		else 
			size = DT_VARCHAR_DEFAULT[3];
		
		return size;
	}
	
	private static JSONArray dataTypeDetector(List<String> records, int totalColumns, String fileName) {
		
		JSONObject object = new JSONObject();
		List<String> existingDataType = new ArrayList<String>();
		JSONArray jsonArray = new JSONArray();
		
		String type = "";
		String value = "";
		String appendLenth = "(128)";

		object.put("index", 0);
		object.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
		
		jsonArray.add(0 , object);
		
		boolean isFirstTime = false;
		
		for (int ii=0; ii<records.size(); ii+=(totalColumns-1))
		{
			if (!isFirstTime)
			{
				for (int i=ii; i<(ii+totalColumns-1); i++)
				{
					type = null;
					value = null;
					appendLenth = "(128)";
				
					value = records.get(i);
					if (value == null)
						type = new String(DT_VARCHAR + DT_VARCHAR_DEFAULT[0]);
					else
					{
						type = parseDataType(value);
						if (DT_VARCHAR.equalsIgnoreCase(type))
						{
							appendLenth = appendLength(value.length());
							type = new String(DT_VARCHAR + appendLenth);
						}
					}
					existingDataType.add(type);
				}
			}
			else if (isFirstTime && totalColumns!=2)
			{
				int count = 0;
				for (int i=ii; i<(ii+totalColumns-1); i++)
				{
					type = null;
					value = null;
					appendLenth = "(128)";
					value = records.get(i);
					if (value == null)
						type = new String(DT_VARCHAR + DT_VARCHAR_DEFAULT[0]);
					else
					{
						type = parseDataType(value);
						if (DT_VARCHAR.equalsIgnoreCase(type))
						{
							appendLenth = appendLength(value.length());
							type = new String(DT_VARCHAR + appendLenth);
						}
					}
					String existingType = existingDataType.get(count);
					if (!existingType.equalsIgnoreCase(type))
					{
						appendLenth = appendLength(value.length());
						type = new String(DT_VARCHAR + appendLenth);
						existingDataType.set(count, type);
					}
					count++;
				}
			}
			isFirstTime = true;
		}
		
		for (int i=0; i<existingDataType.size(); i++)
		{
			object = new JSONObject();
			object.put("index", i+1);
			object.put("type", existingDataType.get(i));
			jsonArray.add(i+1 , object);
		}
		
		return jsonArray;
	}
	
	private static String parseDataType(String data)
	{
		boolean isInt = false;
		boolean isDouble = false;
		boolean isDate = false;
		boolean isBool = false;
		String type;
		try{
			Integer.parseInt(data);
			isInt = true;
		}catch(Exception e)
		{
			isInt = false;
		}
		try{
			Long.parseLong(data);
			isInt = true;
		}catch(Exception e)
		{
			isInt = false;
		}
		try{
			Double.parseDouble(data);
			isDouble = true;
		}catch(Exception e)
		{
			isDouble = false;
		}
		try {
			Date.parse(data);
			isDate = true;
		} catch (Exception e) {
			isDate = false;
		}
		try{
			if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false"))
				isBool = true;
		}catch(Exception e)
		{
			isBool = false;
		}
		
		if(isInt)
			type = DT_INTEGER;
		else if(isDate)
			type = DT_DATETIME;
		else if(isDouble)
			type = DT_DECIMAL;
		else if(isBool)
			type = DT_BOOLEAN;
		else
			type = DT_VARCHAR;
		
		return type;
		
	}
	
	
	private static JSONObject keyValueFileParser(File kvFile, String encodingType, String delimiter, String seperator, int noOfRecords) throws Exception
	{
		JSONObject jsono = new JSONObject();
		
		BufferedReader br = null;
		String line = null;
		
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(kvFile), encodingType));
			
			JSONArray details = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("index", 0);
			obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
			details.add(0 , obj);
			ArrayList <ArrayList> tempRecords = new ArrayList<ArrayList>();
			
			JSONObject headerObj = new JSONObject();
			
			headerObj.put(0, COL_FILEPATH);
			int count = 1;
			String str[] = null;
			
			int counter = 0;
			
			while ((line = br.readLine()) != null)
	        {
				if(counter == noOfRecords) {
					break;
				}
				tempRecords.add(new ArrayList<String>());
				str = line.split(delimiter);
				String[] pairs = null;
				for (int i=0; i < str.length; i++)
				{
					pairs = str[i].split(seperator);
					if ((pairs != null) && (pairs.length == 2))
					{
						String key = pairs[0];
						String value = pairs[1];
						if (key == null)
							continue;
						
						key = key.trim();
						key = key.toUpperCase().trim();
						if (value != null)
							value = value.trim();
						if(!(headerObj.containsValue(key))) {
							headerObj.put(count, key);							
							obj = new JSONObject();
							obj.put("index" , count);
							obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
							
							details.add(count, obj);
							count ++;
						}
						tempRecords.get(counter).add(value);
					}
					else if ((pairs != null) && (pairs.length == 1))
					{
						String value = pairs[0];
						if (value != null)
							value = value.trim();
						tempRecords.get(counter).add(value);
					}
				}
				counter ++;
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Processing " + counter + " data.");
	        }

			for(int i=0; i<tempRecords.size(); i++)
			{
				tempRecords.get(i).add(0 , kvFile.getName());
			}
			
			JSONObject meta = new JSONObject();
			
//			details = dataTypeDetector(tempRecords, tempRecords.size(), kvFile.getName());
			meta.put("details", details);
			meta.put("data", tempRecords);
			meta.put("header", headerObj);
			
			jsono.put("meta", meta);
		}
		finally
		{
			try {
				if (br != null) {
					br.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return jsono;
	}

	private static JSONObject regexFileParser(File regexFile, String encodingType, String regex, int noOfRecords) throws Exception
	{
		JSONObject jsono = new JSONObject();
		
		BufferedReader br = null;
		String line = null;
		
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(regexFile), encodingType));

			JSONObject headerObject = new JSONObject();
			
			JSONArray dataArray = new JSONArray();
			List<String> records = new ArrayList<String>();
			List<String> tempRecords = new ArrayList<String>();
			headerObject.put(0, COL_FILEPATH);
			regex = regex.trim();
			
			int counter = 0;
			
			while ((line = br.readLine()) != null && counter < noOfRecords)
	        {
				Pattern pattern  = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(line);
				records = new ArrayList<String>();
				records.add(0 , regexFile.getName());
				if (matcher.matches() || matcher.lookingAt() || matcher.find() )
				{
					for (int j=0; j<matcher.groupCount(); j++)
					{
						if (matcher.group(j) != null)
						{
							headerObject.put(j+1, "COLUMN"+(j+1));
							records.add(j+1 , matcher.group(j));
							tempRecords.add(matcher.group(j));
						}
					}
					dataArray.add(records);
				}
				counter ++;
	        }
			
			JSONObject meta = new JSONObject();
			meta.put("header" , headerObject);
			meta.put("data" , dataArray);

			JSONArray details = new JSONArray();
			details = dataTypeDetector(tempRecords, headerObject.size() , regexFile.getName());
			
			meta.put("details", details);
			
			jsono.put("meta", meta);
		}
		finally
		{
			try {
				if (br != null) {
					br.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return jsono;
	}
	@SuppressWarnings({ "unchecked" })
	private static JSONObject apacheLog4JFileParser(File logFile, String encodingType, String logPattern, int noOfRecords) throws Exception
	{
		JSONObject jsono = new JSONObject();
		
		InputStream is = null;
		try
		{
			is = new FileInputStream(logFile);
			Log4JParser log4jParser = new Log4JParser(logPattern , logFile.getName() , encodingType, noOfRecords);
			log4jParser.parse(is);
			JSONObject meta = new JSONObject();
			JSONObject obj = log4jParser.getHeader();
			meta.put("header", obj);
			JSONArray jsoArray = new JSONArray();
			jsoArray = log4jParser.getDetails();
			meta.put("details" , jsoArray);
			jsoArray = new JSONArray();
			jsoArray = log4jParser.getData();
			meta.put("data" , jsoArray);
			jsono.put("meta", meta);
		}
		catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try {
				if (is != null) {
					is.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return jsono;
	}
	
	@SuppressWarnings({ "unchecked"})
	private static JSONObject apacheAccessLog(File logFile , String encodingType, String logPattern, int noOfRecords)
	{
		JSONObject jsono = new JSONObject();
		InputStream is = null;
		try
		{
			ApacheAccessLogParser accessLog = new ApacheAccessLogParser(logPattern, logFile.getName(), encodingType, noOfRecords);
			
			accessLog.parse(new FileInputStream(logFile));
			JSONObject header = new JSONObject();
			JSONArray details = new JSONArray();
			JSONArray data = new JSONArray();
			header = accessLog.getHeader();
			details = accessLog.getDetails();
			data = accessLog.getData();
			JSONObject meta = new JSONObject();
			meta.put("header", header);
			meta.put("details", details);
			meta.put("data" , data);
			jsono.put("meta", meta);
		}
		catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally{
			try{
				if(is!=null)
					is.close();
			}
			catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return jsono;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static JSONObject jsonParser(File jsonFile, String encodingType , int noOfRecords) throws Exception
	{
		JSONObject jsono = new JSONObject();
		JSONArray detailsArray = new JSONArray();
		
		BufferedReader br = null;
		String line = null;
		
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), encodingType));
			
	        JSONArray dataArray = new JSONArray();
	        List <String> records = new ArrayList<String>();
	        
	        
	        List <String> tempRecords = new ArrayList<String>();
	        JSONObject headerObj = new JSONObject();
	        List <Class> klass = new ArrayList<Class>();
	        
	        headerObj.put(0, COL_FILEPATH);
	        boolean setHeader = true;
	        int index = 0;
	        int numRecords = 0;
	        while ((line = br.readLine()) != null && (numRecords < noOfRecords))
	        {
				JSONObject jsonObject = (JSONObject)new JSONParser().parse(line);
				Iterator<String> keys = jsonObject.keySet().iterator();
				String key = null;
				records = new ArrayList<String>();
				records.add(jsonFile.getName());
				tempRecords.add(jsonFile.getName());
				
				while (keys.hasNext())
				{
					key = keys.next();
					klass.add(jsonObject.get(key).getClass());
					records.add(jsonObject.get(key).toString());
					tempRecords.add(jsonObject.get(key).toString());
					if (setHeader)
					{
						if (key != null)
							key = key.toUpperCase().trim();
						
						headerObj.put(index + 1, key);
						index ++;
					}
				}
				
				setHeader = false;
				if(numRecords < MAX_DATA_ROWS)
					dataArray.add(numRecords, records);
				numRecords ++;
			}
			
			JSONObject detailsObject = new JSONObject();
			detailsObject.put("index", 0);
			detailsObject.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
			
			detailsArray.add(0, detailsObject);
			
			for (int i=0; i<klass.size(); i++)
			{
				detailsObject = new JSONObject();
				
				String dataType = DT_BOOLEAN;
				Class classObject = klass.get(i);
				
				if (classObject == Boolean.class && (!DT_VARCHAR.equalsIgnoreCase(dataType)))
				{
					dataType = DT_BOOLEAN;
				}
				else if ((classObject == Integer.class || classObject == Long.class) && (!DT_VARCHAR.equalsIgnoreCase(dataType)))
				{
					dataType = DT_INTEGER;
				}
				else if (classObject == Double.class && (!DT_VARCHAR.equalsIgnoreCase(dataType)))
				{
					dataType = DT_DECIMAL;
				}
				else if (classObject == Date.class && (!DT_VARCHAR.equalsIgnoreCase(dataType)))
				{
					dataType = DT_DATETIME;
				}
				else
					dataType = DT_VARCHAR;
				
				detailsObject.put("index", i%index + 1);
				
				if (DT_VARCHAR.equalsIgnoreCase(dataType))
					dataType = dataType.concat(DT_VARCHAR_DEFAULT[1]);
				
				detailsObject.put("type", dataType);
				if (i>=index)
					detailsArray.set((i%index + 1), detailsObject);
				else
					detailsArray.add(i%index + 1, detailsObject);
			}
			
			JSONObject meta = new JSONObject();
			meta.put("details", detailsArray);
			meta.put("data", dataArray);
			meta.put("header", headerObj);
			
			jsono.put("meta", meta);
		}
		finally
		{
			try {
				if (br != null) {
					br.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return jsono;
	}
	
	private static JSONObject emlParser(File emlFile) throws Exception
	{
		JSONObject jsono = new JSONObject();
		JSONObject columnDetails = new JSONObject();
		
		Session	mailSession = null;
		MimeMessage message = null;
		InputStream source = null;
		
		try
		{
//			String host = "localhost";
//	        Properties props = System.getProperties();
//	        props.setProperty("mail.smtp.host", host);
//	        props.put("mail.transport.protocol", "smtp");

	        mailSession = Session.getDefaultInstance(new Properties());
	        source = new FileInputStream(emlFile);
	        message = new MimeMessage(mailSession, source);
	        
	        //Inserting Details
	        JSONArray jsonArray = new JSONArray();
	        JSONObject obj = new JSONObject();
	        obj.put("index",0);
	        obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[3]);
	        jsonArray.add(obj);
	        for (int i=1; i<=18; i++)
	        {
	        	obj = new JSONObject();
	        	obj.put("index", i);
	        	if (i==13 || i==16)
	        	{
	        		obj.put("type", DT_DATETIME);
	        	}
	        	else if (i == 10 || i == 12 || i == 18)
	        	{
	        		obj.put("type", DT_INTEGER);
	        	}
	        	else
	        	{
	        		obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[1]);
	        	}
	        	jsonArray.add(obj);
	        }
	        
	        //Inserting Data
	        JSONArray dataArray = new JSONArray();
	        dataArray.add(0, emlFile.getName());
	        dataArray.add(1, message.getContentID());
	        StringBuilder sb = new StringBuilder();
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
	        dataArray.add(2, sb.toString().trim());
	        dataArray.add(3, message.getContentMD5());
	        dataArray.add(4, message.getContentType());
	        dataArray.add(5, message.getDescription());
	        dataArray.add(6, message.getDisposition());
	        dataArray.add(7, message.getEncoding());
	        dataArray.add(8, message.getFileName());
	        Address temp1[] = message.getFrom();
	        sb.setLength(0);
	        if (temp1 != null)
	        {
	        	for (int i=0; i<temp1.length; i++)
	        	{
	        		sb.append(temp1[i]);
	        		sb.append(" ");
	        	}
	        }
	        dataArray.add(9, sb.toString().trim());
	        dataArray.add(10, message.getLineCount());
	        dataArray.add(11, message.getMessageID());
	        dataArray.add(12, message.getMessageNumber());
	        dataArray.add(13, message.getReceivedDate());
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
	        dataArray.add(14, sb.toString().trim());
	        dataArray.add(15, message.getSender());
	        dataArray.add(16, message.getSentDate().toString());
	        dataArray.add(17, message.getSubject());
	        dataArray.add(18, message.getSize());
	        
	        //Inserting Header
	        JSONObject header = new JSONObject();
			header.put(0, COL_FILEPATH);
			
			header.put(1, "CONTENT_ID");
			header.put(2, "CONTENT_LANGUAGE");	//Array
			header.put(3, "MD5_CONTENT");
			header.put(4, "CONTENT_TYPE");
			header.put(5, "CONTENT_DESCRIPTION");
			header.put(6, "CONTENT_DISPOSITION");
			header.put(7, "CONTENT_ENCODING");
			header.put(8, "FILENAME");
			header.put(9, "MAIL_FROM");	//Array
			header.put(10, "LINE_COUNT");
			header.put(11, "MESSAGE_ID");
			header.put(12, "MESSAGE_NUMBER");
			header.put(13, "RECEIVED_DATE");//Date
			header.put(14, "REPLY_TO");//Array
			header.put(15, "SENDER");
			header.put(16, "SENT_DATE");
			header.put(17, "MAIL_SUBJECT");
			header.put(18, "MAIL_SIZE");
			
			JSONArray daArray = new JSONArray();
			daArray.add(dataArray);
	        columnDetails.put("details", jsonArray);
	        columnDetails.put("data", daArray);
	        columnDetails.put("header", header);
	        
	        jsono.put("meta", columnDetails);
		}
		finally
		{
			try {
				if (source != null) {
					source.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
        return jsono;
	}
	
	
	public static void main(String args[]) throws Exception {

		//jsonParser(new File("/home/hiper/QueryIOUIServer/demo/Data/json/json_10.json"), "UTF-8"  , 100);
//		JSONObject jsono = apacheLog4JFileParser(new File("/home/hiper/QueryIOUIServer/demo/Data/log/Log_1356009249380.log"), "UTF-8", "%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n");
//		JSONObject JSON = apacheAccessLog(new File("/home/hiper/QueryIOUIServer/tomcat/logs/localhost_access_log (copy).2013-02-26.txt"), "UTF-8", "%h %l %u %t \"%r\" %>s %b", 10);
		JSONObject JSon = keyValueFileParser(new File("/AppPerfect/QueryIO/demo/Data/keyvalue/KeyValueMachineLogs_1364898811823.txt"), "UTF-8", ";", ":", 4);
		System.out.println(JSon.toJSONString());
//		System.out.println(JSON.toJSONString());
	//	emlParser();
        //keyValueFileParser(new File("/AppPerfect/HiperCloudStore/HiperCloudStore/demo/1.properties") , "\n" , "=");
		//regexFileParser(new File("/AppPerfect/HiperCloudStore/HiperCloudStore/demo/Data/csv/email.txt") , "UTF-8",  "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
//		String regex = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})"
		//jsonParser();
//		xmlParser(new File("/AppPerfect/HiperCloudStore/HiperCloudStore/demo/test.xml") , "SystemDB");
    }
	
	
	/*public static void main(String[] args)
	{
		JSONObject columnDetails = new JSONObject();
		BufferedReader br = null;
		File file = new File("/AppPerfect/HiperCloudStore/demo/Data/csv/aaa.csv");						// comma(,) as delimiter
//		File file = new File("/AppPerfect/HiperCloudStore/demo/Data/csv/aaa_tab_delimit.csv");			// tab(\t) as delimiter
//		File file = new File("/AppPerfect/HiperCloudStore/demo/Data/csv/aaa_hyphen_delimit.csv");			// hyphen(-) as delimiter
		boolean isFirstLineHeader = true;
		String delimiter = ",";
		String separatorValue = "\"";
		String encodingType = "UTF-8";
		int noOfRecords = 3;
		Timestamp startTime = new Timestamp(System.currentTimeMillis());
		System.out.println(startTime);
		System.out.println(startTime.getTime());
		
		String[] columnTypes = null;
		JSONArray jsonArray = new JSONArray();
		
		String line = null;
		try
		{
//			br = new BufferedReader(new FileReader(file));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodingType));
			if (isFirstLineHeader)
			{
				line = br.readLine();
				
				JSONObject header = new JSONObject();
				List<String> headers =  parseLine(line, delimiter, separatorValue);
				
				header.put(0, COL_FILEPATH);
				for (int i=0; i<headers.size(); i++)
				{
					header.put(i+1, headers.get(i));
				}
				
				columnDetails.put("header", header);
			}
			
			columnDetails.put("details", jsonArray);
			
			int linesParsed = 0;
			
			boolean[] isString = null;
			boolean[] isDecimal = null;
			boolean[] isDateTime = null;
			boolean[] isInteger = null;
			boolean[] isBoolean = null;
			
			List<String> records = null;
			Class klass;
			JSONObject obj;
			JSONArray dataArray = new JSONArray();
			int demoRecords = 10;
			int recordsAdded = 0;
			int tempMax = 0;
			while (((line = br.readLine()) != null) && (linesParsed < noOfRecords))
			{
				records = parseLine(line, delimiter, separatorValue);
				if(linesParsed==0) {
					isBoolean = new boolean[records.size()];
					isString = new boolean[records.size()];
					isDecimal = new boolean[records.size()];
					isDateTime = new boolean[records.size()];
					isInteger = new boolean[records.size()];
					
					columnTypes = new String[records.size()];
				}
				
				linesParsed++;
				
				if (records == null)
					continue;
				
				for(int i=0; i<records.size(); i++) {
					klass = getClass(records.get(i));
					
					if(klass==Boolean.class) isBoolean[i] = true;
					else if(klass==Integer.class) isInteger[i] = true;
					else if(klass==Double.class) isDecimal[i] = true;
					else if(klass==Date.class) isDateTime[i] = true;
					else
					{
						isString[i] = true;
						if(records.get(i).toString().length() < 128 && (tempMax < MAX_LENGTH))
						{
							MAX_LENGTH = 1;
							tempMax = 1;
						}
						else if(records.get(i).toString().length() < 255 && (tempMax < MAX_LENGTH))
						{
							MAX_LENGTH = 2;
							tempMax = 2;
						}
						else if(records.get(i).toString().length() < 512 && (tempMax < MAX_LENGTH))
						{
							MAX_LENGTH = 3;
							tempMax = 3;
						}
						else if(records.get(i).toString().length() < 1280 && (tempMax < MAX_LENGTH))
						{
							MAX_LENGTH = 4;
							tempMax = 4;
						}
					}
				}
				
				if (recordsAdded < demoRecords)
				{
					records.add(0, "fileName");		
					dataArray.add(records);
					recordsAdded++;
				}
			}
			columnDetails.put("data", dataArray);
			if(columnTypes!=null) {
				// All records read. Types are also available.
				resetLowPriorities(isString, isDecimal, isDateTime, isInteger, isBoolean);
				
				obj = new JSONObject();
				obj.put("index", 0);
				obj.put("type", DT_VARCHAR + DT_VARCHAR_DEFAULT[MAX_LENGTH-1]);
				jsonArray.add(obj);
				
				for(int i=0; i<columnTypes.length; i++) {
					if(isString[i]==true) {
						System.out.println("MaxVarChar = "+" type = "+(DT_VARCHAR+DT_VARCHAR_DEFAULT[MAX_LENGTH-1]));
						columnTypes[i] = DT_VARCHAR+DT_VARCHAR_DEFAULT[MAX_LENGTH-1];
					} else if(isDateTime[i]==true) {
						columnTypes[i] = DT_DATETIME;
					} else if(isDecimal[i]==true) {
						columnTypes[i] = DT_DECIMAL;
					} else if(isInteger[i]==true) {
						columnTypes[i] = DT_INTEGER;
					} else if(isBoolean[i]==true) {
						columnTypes[i] = DT_BOOLEAN;
					}
					
					obj = new JSONObject();
					obj.put("index", i+1);
					obj.put("type", columnTypes[i]);
					jsonArray.add(obj);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (br != null) {
					br.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			
			System.out.println("columnDetails: " + columnDetails.toJSONString());
			System.out.println(new Timestamp(System.currentTimeMillis()).getTime() - startTime.getTime());
			System.out.println(new Timestamp(System.currentTimeMillis()));
		}
	}*/
	
	public static void resetLowPriorities(boolean[] isString, boolean[] isDecimal, boolean[] isDateTime, boolean[] isInteger, boolean[] isBoolean) {
		for(int i=0; i<isString.length; i++) {
			if(isString[i]==true) {
				isDecimal[i] = false;
				isDateTime[i] = false;
				isInteger[i] = false;
				isBoolean[i] = false;
			}
			if(isDateTime[i]==true) {
				if(isDecimal[i]==true || isInteger[i]==true || isBoolean[i]==true) {
					isString[i] = true;
					
					isDecimal[i] = false;
					isDateTime[i] = false;
					isInteger[i] = false;
					isBoolean[i] = false;
				}
			}
			if(isDecimal[i]==true) {
				isInteger[i] = false;
				isBoolean[i] = false;
			}
			if(isInteger[i]==true) {
				isBoolean[i] = false;
			}
		}
	}
	
	public static Class getClass(String data) {
		try { if(data.trim().toLowerCase().equals("true") || data.trim().toLowerCase().equals("false") 
				|| data.trim().toLowerCase().equals("0") || data.trim().toLowerCase().equals("1") )
			return Boolean.class; throw new Exception(); } catch(Exception e) { }
		try { Integer.parseInt(data); return Integer.class; } catch(Exception e) { }
		try { Double.parseDouble(data); return Double.class; } catch(Exception e) { }
		try { Date.parse(data); return Date.class; } catch(Exception e) { }
		return String.class;
	}
	
	public static List<String> parseLine(String line, String delimiter, String separatorValue)  
	        throws Exception
    {
        if (line == null) {
            return null;
        }
        
        Vector<String> store = new Vector<String>();
        
        String regex = null;

        if ((separatorValue != null) && (!separatorValue.isEmpty()))
        	regex = "\"([^" + separatorValue + "]*)\"|([^" + delimiter + "]+)";
        else
        	regex = "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";

        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            if (m.group(1) != null) {
//                System.out.println("Quoted [" + m.group(1) + "]");
                store.add(m.group(1));
            } else {
//                System.out.println("Plain [" + m.group(2) + "]");
                store.add(m.group(2));
            }
        }
        
        
//        StringBuffer curVal = new StringBuffer();
//        boolean inquotes = false;
//        for (int i=0; i<line.length(); i++) {
//            char ch = line.charAt(i);
//            if (inquotes) {
//                if (ch=='\"') {
//                    inquotes = false;
//                }
//                else {
//                    curVal.append(ch);
//                }
//            }
//            else {
//                if (ch=='\"') {
//                    inquotes = true;
//                    if (curVal.length()>0) {
//                    	curVal.append('\"');
//                    }
//                }
//                else if (ch==',') {
//                    store.add(curVal.toString());
//                    curVal = new StringBuffer();
//                }
//                else {
//                    curVal.append(ch);
//                }
//            }
//        }
//        store.add(curVal.toString());
        
        return store;
    }
}