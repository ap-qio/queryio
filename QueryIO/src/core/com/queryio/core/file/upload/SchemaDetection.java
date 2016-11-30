package com.queryio.core.file.upload;

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
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;

public class SchemaDetection extends HttpServlet {

	static String colFilepath = "FILEPATH";
	static String dtVarchar = "VARCHAR";
	static String dtVarcharDefault[] = { "(128)", "(255)", "(512)", "(1280)" };
	static int maxLength = 1;
	static String dtDatetime = "DATETIME";
	static String dtDecimal = "DECIMAL";
	static String dtInteger = "INTEGER";
	static String dtBoolean = "BOOLEAN";
	static int maxDataRows = 10;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new IllegalArgumentException(
					"Request is not multipart, please use 'multipart/form-data' enctype for your form.");
		}

		PrintWriter writer = res.getWriter();
		res.setContentType("text/plain");
		JSONArray json = new JSONArray();
		JSONObject jsono = new JSONObject();
		try {
			AppLogger.getLogger().debug("File upload request recevied from host: " + req.getRemoteAddr() + ", user: "
					+ req.getRemoteUser());

			long fileSize = -1;
			String fileName = null;
			String delimiter = null;
			String separatorValue = null;
			int noOfRecords = -1;
			boolean isFirstLineHeader = false;
			boolean toBeParsed = false;
			boolean isASQLFile = false;
			boolean hasBeenParsed = false;

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(Integer.MAX_VALUE);

			ServletFileUpload upload = new ServletFileUpload();

			AppLogger.getLogger().debug("Max File Size: " + upload.getFileSizeMax());

			FileItemStream item;
			FileItemIterator iterator;

			iterator = upload.getItemIterator(req);

			while (iterator.hasNext()) {
				item = iterator.next();
				AppLogger.getLogger().debug("Item: " + item.getFieldName());
				if (item.isFormField()) {
					if (item.getFieldName().equals("header")) {
						String data = getValue(item);
						if (headerToBeParsed(data)) {
							isFirstLineHeader = true;
						} else {
							isFirstLineHeader = false;
						}
						AppLogger.getLogger().debug("header: " + isFirstLineHeader);
					} else if (item.getFieldName().equals("deli")) {
						delimiter = getValue(item);
						AppLogger.getLogger().debug("delimiter: " + delimiter);
					} else if (item.getFieldName().equals("separator")) {
						separatorValue = getValue(item);
						AppLogger.getLogger().debug("separator: " + separatorValue);
					} else if (item.getFieldName().equals("records")) {
						noOfRecords = Integer.valueOf(getValue(item));
						AppLogger.getLogger().debug("noOfRecords: " + noOfRecords);
					} else if (item.getFieldName().equals("selectCase")) {
						String data = getValue(item);
						if (data.equalsIgnoreCase("sql")) {
							isASQLFile = true;
							AppLogger.getLogger().debug("isASQLFile: " + isASQLFile);
							jsono.put("isSql", true);
						}
					}
				} else {
					if (!hasBeenParsed) {

						fileName = item.getName();
						AppLogger.getLogger()
								.debug("Got an uploaded file: " + item.getFieldName() + ", name = " + fileName);
						jsono.put("name", fileName);

						AppLogger.getLogger().debug("Processing Upload Request");

						InputStream inStream = null;
						OutputStream outStream = null;
						try {

							String path = EnvironmentalConstants.getAppHome() + File.separator
									+ QueryIOConstants.ADHOC_SAMPLE_FILE_DIR;// +
																				// QueryIOConstants.SAMPLE_FILE_DIR;
							File parentDir = new File(path);

							if (!parentDir.exists())
								parentDir.mkdirs();

							inStream = new BufferedInputStream(item.openStream(), 16 * 1024);
							outStream = new FileOutputStream(
									new File(parentDir.getAbsolutePath() + File.separator + fileName));

							int read = 0;
							final byte[] bytes = new byte[1024];
							while ((read = inStream.read(bytes)) != -1) {
								outStream.write(bytes, 0, read);
							}

							toBeParsed = true;
						} finally {

							try {
								if (inStream != null) {
									inStream.close();
								}
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}
							try {
								if (outStream != null) {
									outStream.close();
								}
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e.getMessage(), e);
							}

						}
						hasBeenParsed = true;
					}
				}
			}
			AppLogger.getLogger().debug("toBeParsed: " + toBeParsed);
			AppLogger.getLogger().debug("isFirstLineHeader: " + isFirstLineHeader);
			AppLogger.getLogger().debug("noOfRecords: " + noOfRecords);
			AppLogger.getLogger().debug("delimiter: " + delimiter);
			AppLogger.getLogger().debug("separatorValue: " + separatorValue);
			AppLogger.getLogger().debug("isASQLFile: " + isASQLFile);

			File sampleDir = new File(EnvironmentalConstants.getAppHome() + QueryIOConstants.ADHOC_SAMPLE_FILE_DIR);
			if (!sampleDir.exists())
				sampleDir.mkdir();
			String path = sampleDir.getAbsolutePath() + File.separator + fileName;
			AppLogger.getLogger().debug("path: " + path);
			File file = new File(sampleDir.getAbsolutePath() + File.separator + fileName);
			if (toBeParsed && !isASQLFile) {

				JSONObject columnDetails = new JSONObject();

				String[] columnTypes = null;
				JSONArray jsonArray = new JSONArray();

				BufferedReader br = null;
				String line = null;
				try {
					// br = new BufferedReader(new FileReader(file));
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

					if (isFirstLineHeader) {
						line = br.readLine();

						AppLogger.getLogger().debug("Reading line: " + line);

						JSONObject header = new JSONObject();
						List<String> headers = parseLine(line, delimiter, separatorValue);

						header.put(0, colFilepath);
						String str = null;

						for (int i = 0; i < headers.size(); i++) {
							str = headers.get(i);
							if (str != null)
								str = str.toUpperCase().trim();

							header.put(i + 1, str);
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
					while (((line = br.readLine()) != null) && (linesParsed < noOfRecords)) {
						records = parseLine(line, delimiter, separatorValue);

						// AppLogger.getLogger().debug("Reading line: " + line);
						// AppLogger.getLogger().debug("Parser line: " +
						// records);

						if (linesParsed == 0) {
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

						for (int i = 0; i < records.size(); i++) {
							klass = getClass(records.get(i));

							if (klass == Boolean.class)
								isBoolean[i] = true;
							else if (klass == Integer.class)
								isInteger[i] = true;
							else if (klass == Double.class)
								isDecimal[i] = true;
							else if (klass == Date.class)
								isDateTime[i] = true;
							else {

								isString[i] = true;
								if (records.get(i).toString().length() < 128 && (tempMax < maxLength)) {
									maxLength = 1;
									tempMax = 1;
								} else if (records.get(i).toString().length() < 255 && (tempMax < maxLength)) {
									maxLength = 2;
									tempMax = 2;
								} else if (records.get(i).toString().length() < 512 && (tempMax < maxLength)) {
									maxLength = 3;
									tempMax = 3;
								} else if (records.get(i).toString().length() < 1280 && (tempMax < maxLength)) {
									maxLength = 4;
									tempMax = 4;
								}

							}
						}

						if (recordsAdded < demoRecords) {
							records.add(0, fileName);
							dataArray.add(records);
							recordsAdded++;
						}
					}
					columnDetails.put("data", dataArray);

					if (columnTypes != null) {
						// All records read. Types are also available.
						resetLowPriorities(isString, isDecimal, isDateTime, isInteger, isBoolean);

						obj = new JSONObject();
						obj.put("index", 0);
						obj.put("type", dtVarchar + dtVarcharDefault[3]);
						jsonArray.add(obj);

						for (int i = 0; i < columnTypes.length; i++) {
							if (isString[i] == true) {
								columnTypes[i] = (dtVarchar + dtVarcharDefault[1]);
							} else if (isDateTime[i] == true) {
								columnTypes[i] = dtDatetime;
							} else if (isDecimal[i] == true) {
								columnTypes[i] = dtDecimal;
							} else if (isInteger[i] == true) {
								columnTypes[i] = dtInteger;
							} else if (isBoolean[i] == true) {
								columnTypes[i] = dtBoolean;
							}

							obj = new JSONObject();
							obj.put("index", i + 1);
							obj.put("type", columnTypes[i]);
							jsonArray.add(obj);

							// AppLogger.getLogger().debug("obj: " + obj);
						}

						columnDetails.put("details", jsonArray);
						jsono.put("meta", columnDetails);
					}
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}

				jsono.put("name", fileName);
				jsono.put("success", "File uploaded successfully.");
			}

		} catch (Exception ex) {
			jsono.put("error", "Error occurred while uploading file.");
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		} finally {
			json.add(jsono);
			writer.write(json.toString());
			writer.close();
			AppLogger.getLogger().debug("Final json = " + json.toJSONString());
		}
	}

	public static void resetLowPriorities(boolean[] isString, boolean[] isDecimal, boolean[] isDateTime,
			boolean[] isInteger, boolean[] isBoolean) {
		for (int i = 0; i < isString.length; i++) {
			if (isString[i] == true) {
				isDecimal[i] = false;
				isDateTime[i] = false;
				isInteger[i] = false;
				isBoolean[i] = false;
			}
			if (isDateTime[i] == true) {
				if (isDecimal[i] == true || isInteger[i] == true || isBoolean[i] == true) {
					isString[i] = true;

					isDecimal[i] = false;
					isDateTime[i] = false;
					isInteger[i] = false;
					isBoolean[i] = false;
				}
			}
			if (isDecimal[i] == true) {
				isInteger[i] = false;
				isBoolean[i] = false;
			}
			if (isInteger[i] == true) {
				isBoolean[i] = false;
			}
		}
	}

	public static List<String> parseLine(String line, String delimiter, String separatorValue) throws Exception {
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
				// System.out.println("Quoted [" + m.group(1) + "]");
				store.add(m.group(1));
			} else {
				// System.out.println("Plain [" + m.group(2) + "]");
				store.add(m.group(2));
			}
		}
		return store;
	}

	public boolean headerToBeParsed(String data) {
		return data.equalsIgnoreCase("on");
	}

	public static Class getClass(String data) {
		try {
			if (data.trim().toLowerCase().equals("true") || data.trim().toLowerCase().equals("false")
					|| data.trim().toLowerCase().equals("0") || data.trim().toLowerCase().equals("1"))
				return Boolean.class;
			throw new Exception();
		} catch (Exception e) {
		}
		try {
			Integer.parseInt(data);
			return Integer.class;
		} catch (Exception e) {
		}
		try {
			Double.parseDouble(data);
			return Double.class;
		} catch (Exception e) {
		}
		try {
			Date.parse(data);
			return Date.class;
		} catch (Exception e) {
		}
		return String.class;
	}

	public String getValue(FileItemStream item) throws IOException {
		String theString = null;
		InputStream is = null;
		try {
			is = item.openStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			theString = writer.toString();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return theString;
	}

}
