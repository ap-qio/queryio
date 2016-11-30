package com.os3.server.response.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.TableModel;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.os3.server.common.IErrorConstants;
import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamUtilities;
import com.os3.server.data.manager.DataManager;

public class ResponseWriter {

	static Logger logger = Logger.getLogger(ResponseWriter.class);

	public static void handleResponseStatus(HttpServletResponse response, int status, int apiType, String requestId,
			String contentType) {
		response.setStatus(status);
		response.setHeader(OS3Constants.CONTENT_TYPE, contentType);
		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}
	}

	public static void sendConnectionClose(HttpServletResponse response) {
		response.setHeader(OS3Constants.CONNECTION, OS3Constants.CLOSE);
	}

	private static String getXmlNSUri(int apiType) {
		if (apiType == OS3Constants.API_TYPE_AMAZON) { // for Amazon
			return "http://s3.amazonaws.com/doc/2006-03-01/";
		} else { // for google
			return "http://doc.commondatastorage.googleapis.com/2010-04-03";
		}
	}

	/*
	 * <ListAllMyBucketsResult xmlns="http://doc.s3.amazonaws.com/2006-03-01">
	 * <Owner> <ID>bcaf1ffd86f461ca5fb16fd081034f</ID>
	 * <DisplayName>webfile</DisplayName> </Owner> <Buckets> <Bucket>
	 * <Name>quotes</Name> <CreationDate>2006-02-03T16:45:09.000Z</CreationDate>
	 * </Bucket> <Bucket> <Name>samples</Name>
	 * <CreationDate>2006-02-03T16:41:58.000Z</CreationDate> </Bucket>
	 * </Buckets> </ListAllMyBucketsResult>
	 */

	public static void writeListAllMyBucketsResponse(final int apiType, final HttpServletResponse response,
			final String ownerID, final String ownerName, final ArrayList dirStats, String requestId)
			throws IOException {
		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeListAllMyBucketsResponseData(apiType, response, ownerID, ownerName, dirStats);
	}

	public static void writeListAllMyBucketsResponseData(final int apiType, final HttpServletResponse response,
			final String ownerID, final String ownerName, final ArrayList dirStats) throws IOException {

		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<ListAllMyBucketsResult xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<Owner>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<ID>");
		writer.write(ownerID);
		writer.write("</ID>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<DisplayName>");
		writer.write(ownerName);
		writer.write("</DisplayName>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("</Owner>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<Buckets>");
		writer.write(IErrorConstants.NEWLINE);
		// Write contents
		if (dirStats != null) {
			for (int i = 0; i < dirStats.size(); i++) {
				writer.write("<Bucket>");
				writer.write(IErrorConstants.NEWLINE);

				writer.write("<Name>");
				writer.write(((FileStatus) dirStats.get(i)).getPath().getName());
				writer.write("</Name>");
				writer.write(IErrorConstants.NEWLINE);

				writer.write("<CreationDate>");
				writer.write(new Date(((FileStatus) dirStats.get(i)).getModificationTime()).toString());
				writer.write("</CreationDate>");
				writer.write(IErrorConstants.NEWLINE);

				writer.write("</Bucket>");
				writer.write(IErrorConstants.NEWLINE);
			}
		}
		writer.write("</Buckets>");
		writer.write("</ListAllMyBucketsResult>");
		writer.flush();
	}

	/*
	 * GET Bucket (List Objects) sample response <?xml version="1.0"
	 * encoding="UTF-8"?> <ListBucketResult
	 * xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
	 * <Name>example-bucket</Name> <Prefix></Prefix> <Marker></Marker>
	 * <MaxKeys>1000</MaxKeys> <Delimiter>/</Delimiter>
	 * <IsTruncated>false</IsTruncated> <Contents> <Key>sample.html</Key>
	 * <LastModified>2011-02-26T01:56:20.000Z</LastModified>
	 * <ETag>&quot;bf1d737a4d46a19f3bced6905cc8b902&quot;</ETag>
	 * <Size>142863</Size> <Owner> <ID>canonical-user-id</ID>
	 * <DisplayName>display-name</DisplayName> </Owner>
	 * <StorageClass>STANDARD</StorageClass> </Contents> <CommonPrefixes>
	 * <Prefix>photos/</Prefix> </CommonPrefixes> </ListBucketResult>
	 */
	public static void writeListObjectsResponse(FileSystem dfs, int apiType, HttpServletResponse response,
			String bucketName, String prefix, String delimiter, int maxKeys, String marker, FileStatus[] fs,
			boolean bTruncated, Map<String, String> commonPrefixes, String requestId) throws Exception {

		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeListObjectsResponseData(dfs, apiType, response, bucketName, prefix, delimiter, maxKeys, marker, fs,
				bTruncated, commonPrefixes);
	}

	private static void writeListObjectsResponseData(FileSystem dfs, int apiType, HttpServletResponse response,
			String bucketName, String prefix, String delimiter, int maxKeys, String marker, FileStatus[] fs,
			boolean bTruncated, Map<String, String> commonPrefixes) throws Exception {

		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<ListBucketResult xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<Name>");
		writer.write(bucketName);
		writer.write("</Name>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<Prefix>");
		if (prefix != null) {
			writer.write(prefix);
		}
		writer.write("</Prefix>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<Marker>");
		if (marker != null) {
			writer.write(marker);
		}
		writer.write("</Marker>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<MaxKeys>");
		writer.write(String.valueOf(maxKeys));
		writer.write("</MaxKeys>");
		writer.write(IErrorConstants.NEWLINE);

		if (delimiter != null) {
			writer.write("<Delimiter>");
			writer.write(delimiter);
			writer.write("</Delimiter>");
			writer.write(IErrorConstants.NEWLINE);
		}
		writer.write("<IsTruncated>");
		writer.write(String.valueOf(bTruncated));
		writer.write("</IsTruncated>");
		writer.write(IErrorConstants.NEWLINE);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		InputStream inputStream = null;
		// Write contents
		for (int i = 0; fs != null && i < fs.length; i++) {
			writer.write("<Contents>");
			writer.write(IErrorConstants.NEWLINE);
			writer.write("<Key>");
			writer.write(fs[i].getPath().getName());
			writer.write("</Key>");
			writer.write(IErrorConstants.NEWLINE);

			writer.write("<LastModified>");
			writer.write(dateFormatter.format(new Date(fs[i].getModificationTime())));
			writer.write("</LastModified>");
			writer.write(IErrorConstants.NEWLINE);

			writer.write("<ETag>");

			try {
				inputStream = DataManager.getObjectDataInputStream(dfs, fs[i].getPath().getParent().getName(),
						fs[i].getPath().getName(), null, null);

				writer.write(StreamUtilities.getStreamCheckSum(inputStream));
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Exception e) {
						logger.fatal(e.getMessage(), e);
					}
				}
			}

			writer.write("</ETag>");
			writer.write(IErrorConstants.NEWLINE);

			writer.write("<Size>");
			writer.write(String.valueOf(fs[i].getLen()));
			writer.write("</Size>");
			writer.write(IErrorConstants.NEWLINE);

			// Always set to STANDARD
			writer.write("<StorageClass>STANDARD</StorageClass>");
			writer.write(IErrorConstants.NEWLINE);

			writer.write("<Owner>");
			writer.write(IErrorConstants.NEWLINE);
			writer.write("<ID>");
			writer.write(fs[i].getOwner());
			writer.write("</ID>");
			writer.write(IErrorConstants.NEWLINE);
			writer.write("<DisplayName>");
			writer.write(fs[i].getOwner());
			writer.write("</DisplayName>");
			writer.write(IErrorConstants.NEWLINE);
			writer.write("</Owner>");
			writer.write(IErrorConstants.NEWLINE);
			writer.write("</Contents>");
			writer.write(IErrorConstants.NEWLINE);
		}

		// Write <CommonPrefixes>
		if (commonPrefixes != null && !commonPrefixes.isEmpty()) {
			for (String commonPrefix : commonPrefixes.keySet()) {
				writer.write("<CommonPrefixes>");
				writer.write(IErrorConstants.NEWLINE);
				writer.write("<Prefix>");
				writer.write(commonPrefix);
				writer.write("</Prefix>");
				writer.write(IErrorConstants.NEWLINE);
				writer.write("</CommonPrefixes>");
				writer.write(IErrorConstants.NEWLINE);
			}
		}

		writer.write("</ListBucketResult>");
		writer.flush();
	}

	public static void writeGetOrHeadObjectResponse(HttpServletRequest request, HttpServletResponse response,
			int apiType, String requestId, long contentLength, long modificationTime, String etag,
			boolean writeMetaData, Map metadata) throws IOException {
		// Write response headers
		String contentType = request.getParameter("response-content-type");
		if (contentType != null) {
			response.addHeader("Content-Type", contentType);
		} else {
			response.addHeader("Content-Type", "application/octet-stream");
		}

		String contentLang = request.getParameter("response-content-language");
		if (contentLang != null) {
			response.addHeader("Content-Language", contentLang);
		}

		String responseExpires = request.getParameter("response-expires");
		if (responseExpires != null) {
			response.addHeader("Expires", responseExpires);
		}

		String cacheControl = request.getParameter("response-cache-control");
		if (cacheControl != null) {
			response.addHeader("Cache-Control", cacheControl);
		}

		String contentDisposition = request.getParameter("response-content-disposition");
		if (contentDisposition != null) {
			response.addHeader("Content-Disposition", contentDisposition);
		}

		String contentEncoding = request.getParameter("response-content-encoding");
		if (contentEncoding != null) {
			response.addHeader("Content-Encoding", contentEncoding);
		}

		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}

		if (writeMetaData) {
			Iterator it = metadata.keySet().iterator();
			Object key;
			while (it.hasNext()) {
				key = it.next();
				response.addHeader((String) key, metadata.get(key) != null ? metadata.get(key).toString() : null);
			}
		}

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		response.setHeader("ETag", etag);
		response.addHeader("Last-Modified", dateFormatter.format(new Date(modificationTime)));
	}

	public static void writeHeadObjectResponse(HttpServletRequest request, HttpServletResponse response, int apiType,
			String requestId, long contentLength, long modificationTime, String etag, boolean writeMetaData,
			Map metadata) throws IOException {
		writeGetOrHeadObjectResponse(request, response, apiType, requestId, contentLength, modificationTime, etag,
				writeMetaData, metadata);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public static void writeGetACLResponse(HttpServletRequest request, HttpServletResponse response, int apiType,
			String requestId, FileStatus status) throws IOException {
		// Write response headers
		String contentType = request.getParameter("response-content-type");
		if (contentType != null) {
			response.addHeader("Content-Type", contentType);
		} else {
			response.addHeader("Content-Type", "application/octet-stream");
		}

		String contentLang = request.getParameter("response-content-language");
		if (contentLang != null) {
			response.addHeader("Content-Language", contentLang);
		}

		String responseExpires = request.getParameter("response-expires");
		if (responseExpires != null) {
			response.addHeader("Expires", responseExpires);
		}

		String cacheControl = request.getParameter("response-cache-control");
		if (cacheControl != null) {
			response.addHeader("Cache-Control", cacheControl);
		}

		String contentDisposition = request.getParameter("response-content-disposition");
		if (contentDisposition != null) {
			response.addHeader("Content-Disposition", contentDisposition);
		}

		String contentEncoding = request.getParameter("response-content-encoding");
		if (contentEncoding != null) {
			response.addHeader("Content-Encoding", contentEncoding);
		}

		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}

		if (status != null) {
			response.setHeader(OS3Constants.OWNER, status.getOwner());
			response.setHeader(OS3Constants.GROUP, status.getGroup());
			response.setHeader(OS3Constants.PERMISSION, String.valueOf(status.getPermission().toShort()));
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}

	public static void writeGetObjectResponse(HttpServletRequest request, HttpServletResponse response, int apiType,
			String requestId, InputStream inputStream, long contentLength, long modificationTime, String etag,
			long lowerBound, long higherBound, String range, boolean writeMetaData, Map metadata) throws IOException {
		// finally set status
		if (range == null) {
			logger.debug("Response for request: " + HttpServletResponse.SC_OK);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			logger.debug("Response for request: " + HttpServletResponse.SC_PARTIAL_CONTENT);
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		}

		writeGetOrHeadObjectResponse(request, response, apiType, requestId, contentLength, modificationTime, etag,
				writeMetaData, metadata);
		// Write Data
		StreamUtilities.writeToStream(inputStream, response.getOutputStream(), lowerBound, higherBound);

		response.flushBuffer();
	}

	public static void writePutObjectResponse(int apiType, HttpServletResponse response, String etag,
			String requestId) {
		response.setHeader("ETag", etag);
		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}
		// finally set status
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public static void writeLoginActionResponse(int apiType, HttpServletResponse response, String requestId,
			String token) {
		response.setHeader(OS3Constants.AUTHORIZATION, token);
		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}
		// finally set status
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public static void writeLogoutActionResponse(int apiType, HttpServletResponse response, String requestId) {
		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}
		// finally set status
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public static void writeDeleteResponse(int apiType, HttpServletResponse response, String requestId) {
		if (apiType == OS3Constants.API_TYPE_AMAZON) {
			response.setHeader(OS3Constants.X_AMZ_REQUEST_ID, requestId);
			response.setHeader(OS3Constants.X_AMZ_ID_2, requestId);
		}
		// finally set status
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	public static void writeAddBigQueryResponse(Object object, int apiType, HttpServletResponse response,
			String requestId) throws IOException {
		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeAddBigQueryResponseData(response, apiType, true);
	}

	public static void writeDeleteBigQueryResponse(Object object, int apiType, HttpServletResponse response,
			String requestId) throws IOException {
		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeDeleteBigQueryResponseData(response, apiType, true);
	}

	public static void writeAddBigQueryResponseData(HttpServletResponse response, int apiType, boolean success)
			throws IOException {
		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<AddBigQuery xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<Status>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write(String.valueOf(success));

		writer.write(IErrorConstants.NEWLINE);
		writer.write("</Status>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("</AddBigQuery>");
	}

	public static void writeDeleteBigQueryResponseData(HttpServletResponse response, int apiType, boolean success)
			throws IOException {
		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<DeleteBigQuery xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<Status>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write(String.valueOf(success));

		writer.write(IErrorConstants.NEWLINE);
		writer.write("</Status>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("</DeleteBigQuery>");
	}

	public static void writeBigQueryResponse(FileSystem dfs, int apiType, HttpServletResponse response,
			String requestId, String bigQuery, TableModel resultTableModel, boolean jobComplete) throws IOException {
		if (resultTableModel == null) {
			throw new IOException("Result table model is Null");
		}
		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeBigQueryResponseData(response, apiType, resultTableModel, jobComplete);
	}

	public static void writeGetAllBigQueriesResponse(FileSystem dfs, int apiType, HttpServletResponse response,
			String requestId, JSONObject queries) throws IOException {
		handleResponseStatus(response, HttpServletResponse.SC_OK, apiType, requestId, OS3Constants.APPLICATION_XML);
		writeGetAllBigQueriesResponseData(response, apiType, queries);
	}

	public static void writeGetAllBigQueriesResponseData(HttpServletResponse response, int apiType, JSONObject queries)
			throws IOException {
		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<BigQueries xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<BigQueryList>");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<![CDATA[" + queries.toJSONString() + "]]>");

		writer.write(IErrorConstants.NEWLINE);
		writer.write("</BigQueryList>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("</BigQueries>");
	}

	public static void writeBigQueryResponseData(HttpServletResponse response, int apiType, TableModel resultTableModel,
			boolean jobComplete) throws IOException {
		PrintWriter writer = response.getWriter();
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

		writer.write(IErrorConstants.XML_RESPONSE_HEADER);
		writer.write(IErrorConstants.NEWLINE);
		writer.write("<ExecuteBigQueryResult xmlns=\"" + getXmlNSUri(apiType) + "\">");
		writer.write(IErrorConstants.NEWLINE);

		writer.write("<BigQueryResult>");
		writer.write(IErrorConstants.NEWLINE);

		JSONObject bigQueryResponse = new JSONObject();
		JSONArray resultSetArray = new JSONArray();
		JSONObject schema = new JSONObject();

		JSONArray fieldsArray = new JSONArray();
		for (int i = 0; i < resultTableModel.getColumnCount(); i++) {
			fieldsArray.add(resultTableModel.getColumnName(i));
		}
		schema.put("fields", fieldsArray);

		for (int i = 0; i < resultTableModel.getRowCount(); i++) {
			JSONArray row = new JSONArray();
			for (int j = 0; j < resultTableModel.getColumnCount(); j++) {
				row.add(resultTableModel.getValueAt(i, j));
			}
			resultSetArray.add(row);
		}

		bigQueryResponse.put("schema", schema);
		bigQueryResponse.put("totalRows", resultTableModel.getRowCount());
		bigQueryResponse.put("rows", resultSetArray);
		bigQueryResponse.put("jobComplete", jobComplete);

		writer.write("<![CDATA[" + bigQueryResponse.toJSONString() + "]]>");
		writer.write("</BigQueryResult>");
		writer.write(IErrorConstants.NEWLINE);
		writer.write("</ExecuteBigQueryResult>");
		writer.flush();
	}
}
