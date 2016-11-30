package com.queryio.common.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.RemoteManager;

public class AddHiveDefinitionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DWRResponse dwrResponse = new DWRResponse();
		try {
			dwrResponse = doProcess(request, response);
		} catch (Exception e) {
			AppLogger.getLogger().error(e.getMessage(), e);
			response.setStatus(dwrResponse.getResponseCode());
			// response.flushBuffer();
		}
	}

	protected DWRResponse doProcess(HttpServletRequest request, HttpServletResponse response)
			throws SQLIntegrityConstraintViolationException {

		DWRResponse dwrResponse = new DWRResponse();
		// format
		// "{\"fileType\":\"CSV\",\"adHocId\":\"Hive5\",\"nameNodeId\":\"NameNode1\",\"rmId\":\"ResourceManager1\",\"sourcePath\":\"/Data/csv\",\"parseRecursive\":true,\"adHocTableName\":\"HiveCSVTable5\",\"fileName\":\"C:\\fakepath\\MachineLogs_1364454240895.csv\",\"filePathPattern\":\".*\\.csv\",\"fields\":\"[{\"colName\":\"FILEPATH\",\"colType\":\"STRING(1280)\",\"colIndex\":0},{\"colName\":\"IP\",\"colType\":\"STRING(255)\",\"colIndex\":1},{\"colName\":\"CPU\",\"colType\":\"INTEGER\",\"colIndex\":2},{\"colName\":\"RAM\",\"colType\":\"INTEGER\",\"colIndex\":3},{\"colName\":\"DISKREAD\",\"colType\":\"INTEGER\",\"colIndex\":4},{\"colName\":\"DISKWRITE\",\"colType\":\"INTEGER\",\"colIndex\":5},{\"colName\":\"NETREAD\",\"colType\":\"INTEGER\",\"colIndex\":6},{\"colName\":\"NETWRITE\",\"colType\":\"INTEGER\",\"colIndex\":7}]\",\"delimiter\":\",\",\"valueSeparator\":\"\",\"isFirstRowHeader\":true,\"encoding\":\"UTF-8\",\"isSkipAllRecordsString\":null}";
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(inputStreamReader);

			AppLogger.getLogger().debug("in servlet :: " + jsonObject.toString());

			String fileType = (String) jsonObject.get("fileType");
			String adHocId = (String) jsonObject.get("adHocId");
			String nameNodeId = (String) jsonObject.get("nameNodeId");
			String rmId = (String) jsonObject.get("rmId");
			String sourcePath = (String) jsonObject.get("sourcePath");
			boolean parseRecursive = (Boolean) jsonObject.get("parseRecursive");
			String adHocTableName = (String) jsonObject.get("adHocTableName");
			String fileName = (String) jsonObject.get("fileName");
			String filePathPattern = (String) jsonObject.get("filePathPattern");
			String pattern = (String) jsonObject.get("pattern");
			String fields = ((JSONArray) jsonObject.get("fields")).toJSONString();
			String delimiter = (String) jsonObject.get("delimiter");
			String valueSeparator = (String) jsonObject.get("valueSeparator");
			boolean isFirstRowHeader = (Boolean) jsonObject.get("isFirstRowHeader");
			String encoding = (String) jsonObject.get("encoding");
			String nodeName = (String) jsonObject.get("nodeName");
			String isSkipAllRecordsString = (String) jsonObject.get("isSkipAllRecordsString");

			if (AppLogger.getLogger().isDebugEnabled()) {
				AppLogger.getLogger().debug("filetype : " + fileType + "  " + adHocId + "nameNodeId :" + nameNodeId
						+ "rmId " + rmId + "sourcePath " + sourcePath + " parseRecursive " + parseRecursive);
				AppLogger.getLogger()
						.debug("adHocTableName : " + adHocTableName + "  fileName " + fileName + "filePathPattern :"
								+ filePathPattern + " pattern " + pattern + " fields " + fields + "  :: delimiter "
								+ delimiter);
				AppLogger.getLogger()
						.debug("valueSeparator : " + valueSeparator + "  isFirstRowHeader " + isFirstRowHeader
								+ " encoding :" + encoding + " isSkipAllRecordsString " + isSkipAllRecordsString);
			}

			if (QueryIOConstants.ADHOC_TYPE_CSV.equalsIgnoreCase(fileType)) {
				AppLogger.getLogger().debug("in CSV Function call");
				dwrResponse = RemoteManager.addAdHocQueryCSV(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, delimiter, valueSeparator, isFirstRowHeader,
						encoding, isSkipAllRecordsString);
			} else if (QueryIOConstants.ADHOC_TYPE_LOG.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryLOG(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, pattern, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_ACCESSLOG.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryAccessLog(adHocId, nameNodeId, rmId, sourcePath,
						parseRecursive, adHocTableName, fileName, filePathPattern, fields, pattern, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_IISLOG.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryIISLOG(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, delimiter, isFirstRowHeader, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_JSON.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryJSON(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_PAIRS.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryKVPairs(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, delimiter, valueSeparator, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_REGEX.equalsIgnoreCase(fileType))
				dwrResponse = RemoteManager.addAdHocQueryRegex(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, pattern, encoding);
			else if (QueryIOConstants.ADHOC_TYPE_XML.equalsIgnoreCase(fileType))
				/* for type XML */
				dwrResponse = RemoteManager.addAdHocQueryXML(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
						adHocTableName, fileName, filePathPattern, fields, nodeName, encoding);
			inputStreamReader.close();
			AppLogger.getLogger().debug("dwrResponse::" + dwrResponse.getResponseMessage() + "Response code :: "
					+ dwrResponse.getResponseCode());

		} catch (Exception e) {
			AppLogger.getLogger().error(e.getMessage(), e);
			if (AppLogger.getLogger().isDebugEnabled()) {
				AppLogger.getLogger().debug("Exception occured dwrResponse is ::" + dwrResponse.getResponseMessage()
						+ " Response code :: " + dwrResponse.getResponseCode());
			}
		}
		return dwrResponse;

	}

}
