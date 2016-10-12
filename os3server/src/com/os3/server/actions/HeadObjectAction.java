package com.os3.server.actions;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.os3.server.common.OS3Constants;
import com.os3.server.common.StreamUtilities;
import com.os3.server.data.manager.DataManager;
import com.os3.server.hadoop.DFSManager;
import com.os3.server.response.writer.ErrorResponseWriter;
import com.os3.server.response.writer.ResponseWriter;
import com.queryio.common.DFSMap;

public class HeadObjectAction extends BaseAction {

	protected final Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void execute(String operation, HttpServletRequest request, HttpServletResponse response, Map<String, Object> helperMap, int apiType) throws Exception {
		String bucketName = (String)helperMap.get(OS3Constants.X_OS3_BUCKET_NAME);
		String objectName = (String)helperMap.get(OS3Constants.X_OS3_OBJECT_NAME);
		String requestId =  (String)helperMap.get(OS3Constants.X_OS3_REQUESTID);
		String token =  (String)request.getHeader(OS3Constants.AUTHORIZATION);
		
		boolean fetchMD = false;
		try{
			fetchMD = Boolean.parseBoolean((String)request.getHeader(OS3Constants.FETCH_METADATA));
		} catch(Exception e){
			// IGNORE
		}
		
		FileSystem dfs = null;
		
		if (token == null) {
			ErrorResponseWriter.missingAuthorizationHeader(helperMap, response, objectName, requestId, apiType);
			return;
		}
		
		String username = DFSMap.getUserForToken(token);
		if(username==null){
			ErrorResponseWriter.invalidToken(helperMap, response, objectName, requestId, apiType);
			return;
		}
		
		dfs = DFSMap.getDFSForUser(username);
		
		if(!DataManager.doesBucketExists(dfs, bucketName)){
			ErrorResponseWriter.buketNotfound(helperMap, response, bucketName, requestId, apiType);
		} else if(!DataManager.doesObjectExist(dfs, bucketName, objectName)){
			ErrorResponseWriter.objectNotfound(helperMap, response, objectName, requestId, apiType);
		} else{
			FileStatus fs = DataManager.getObjectStatus(dfs, bucketName, objectName);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String ifModifiedSince = (String)request.getHeader(OS3Constants.IF_MODIFIED_SINCE);
			if(ifModifiedSince != null){
				long modificationTime = fs.getModificationTime();
				try{
					long modifiedSince = df.parse(ifModifiedSince).getTime();
					if(modificationTime < modifiedSince){
						//Return 304
						response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
						return;
					}
				}catch(ParseException pe){
					logger.error("RequestId" + requestId + ". Error Parsing " + OS3Constants.IF_MODIFIED_SINCE + " request header with value " + ifModifiedSince, pe);
					ErrorResponseWriter.invalidArgument(helperMap, response, objectName, requestId, apiType);
					return;
				}
			}
			
			String ifUnModifiedSince = (String)request.getHeader(OS3Constants.IF_UNMODIFIED_SINCE);
			if(ifUnModifiedSince != null){
				long modificationTime = fs.getModificationTime();
				try{
					long unmodifiedSince = df.parse(ifUnModifiedSince).getTime();
					if(unmodifiedSince < modificationTime){
						//Return 412
						response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
						return;
					}
				}catch(ParseException pe){
					logger.error("RequestId" + requestId + ". Error Parsing " + OS3Constants.IF_UNMODIFIED_SINCE + " request header with value " + ifUnModifiedSince, pe);
					ErrorResponseWriter.invalidArgument(helperMap, response, objectName, requestId, apiType);
					return;
				}
			}
			
			String ifMatch = (String)request.getHeader(OS3Constants.IF_MATCH);
			String ifNoneMatch = (String)request.getHeader(OS3Constants.IF_NONE_MATCH);
			
			String compressionType = request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_COMPRESSION);
			String encryptionType = request.getHeader(OS3Constants.X_OS3_AMZ_SERVER_SIDE_ENCRYPTION);
			
			InputStream inputStream = null;
			String checkSum = null;
			try
			{
				inputStream = DataManager.getObjectDataInputStream(dfs, bucketName, objectName, compressionType, encryptionType);
				
				checkSum = StreamUtilities.getStreamCheckSum(inputStream);
			}
			finally
			{
				if(inputStream != null){
					try{
						inputStream.close();
					}catch(Exception e){
						logger.fatal(e.getMessage(), e);
					}
				}
			}
		
			if(ifMatch != null){
				if(!ifMatch.equals(checkSum)){
					//Return 412
					response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
					return;
				}
			}
			
			if(ifNoneMatch != null){
				if(ifNoneMatch.equals(checkSum)){
					//Return 304
					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}
			}
			
			ResponseWriter.writeHeadObjectResponse(request, response, apiType, requestId, fs.getLen(), fs.getModificationTime(), checkSum, fetchMD, fetchMD ? DataManager.getObjectMetadata(new Path(DFSManager.ROOT_PATH + bucketName, objectName).toString()) : null);				
		}
	}
}
