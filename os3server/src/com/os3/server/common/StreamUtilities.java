package com.os3.server.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.queryio.common.EnvironmentalConstants;

public class StreamUtilities {

	private static Logger logger = Logger.getLogger(StreamUtilities.class);
	public static long getContentLength(HttpServletRequest request, HttpServletResponse response, long defaultValue, boolean isRequest) {
		String contentLength = null;
		if(isRequest) {
			contentLength = request.getHeader(OS3Constants.CONTENT_LENGTH);
		}
		else {
			contentLength = response.getHeader(OS3Constants.CONTENT_LENGTH);
		}
		if (contentLength != null) {
			return Long.parseLong(contentLength);
		}
		return defaultValue;
	}

	public static String getRequestInformation(Map<String, Object> helperMap, HttpServletRequest request, int responseCode, String operation) {
		StringBuilder buffer = getStringBuilder(helperMap);
		buffer.append("RequestId: ");
		buffer.append(helperMap != null ? helperMap.get(OS3Constants.X_OS3_REQUESTID) : "");
		buffer.append(" client: ");
		buffer.append(request.getRemoteAddr());
        buffer.append(" operation: ");
        buffer.append(operation);
        buffer.append(" response code: ");
        buffer.append(responseCode);
		return buffer.toString();
	}

	public static void closeInputStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException ioe1) {
				if (logger.isDebugEnabled()) logger.debug("Failed to close input stream");
			}
		}
	}

	public static StringBuilder getStringBuilder(Map<String, Object> helperMap) {
		if (helperMap != null){
			StringBuilder stringBuilder = ((StringBuilder)helperMap.get(OS3Constants.STRING_BUILDER));
			if(stringBuilder == null) {
				stringBuilder = new StringBuilder();
				helperMap.put(OS3Constants.STRING_BUILDER, stringBuilder);
			} else {
				stringBuilder.setLength(0);
			}
			return stringBuilder;
		}
		return new StringBuilder();
	}

	public static StreamWriteStatus writeToStream(InputStream stream, OutputStream baos, long len) throws IOException, NoSuchAlgorithmException {
		logger.debug("Writing to stream");
		StreamWriteStatus status = new StreamWriteStatus();
		
		OS3CheckSum ocs = new OS3CheckSum("MD5");
			
		final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
		int bytesIn = 0;
		long readSoFar = 0;
		while (readSoFar < len && (bytesIn = stream.read(readBuffer, 0, (int) Math.min(readBuffer.length, len - readSoFar))) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			ocs.update(readBuffer, 0, bytesIn);
			
			readSoFar += bytesIn;
		}
		
		status.setCheckSum(ocs.getValue());
		status.setSize(readSoFar);
		
		logger.debug("Writing to stream...complete");
		
		return status;
	}

	public static StreamWriteStatus writeToStream(InputStream stream, OutputStream baos, long len, boolean calcCheckSum) throws IOException, NoSuchAlgorithmException {
		
		StreamWriteStatus status = new StreamWriteStatus();
		
		if(calcCheckSum)
		{
			OS3CheckSum ocs = new OS3CheckSum(EnvironmentalConstants.getEncryptionType());
			
			final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
			int bytesIn = 0;
			long readSoFar = 0;
			while (readSoFar < len && (bytesIn = stream.read(readBuffer, 0, (int) Math.min(readBuffer.length, len - readSoFar))) != -1) {
				baos.write(readBuffer, 0, bytesIn);
				
				ocs.update(readBuffer, 0, bytesIn);
				
				readSoFar += bytesIn;
				baos.flush();
			}
			
			status.setCheckSum(ocs.getValue());
			status.setSize(readSoFar);
		}
		else
		{
			final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
			int bytesIn = 0;
			long readSoFar = 0;
			while (readSoFar < len && (bytesIn = stream.read(readBuffer, 0, (int) Math.min(readBuffer.length, len - readSoFar))) != -1) {
				baos.write(readBuffer, 0, bytesIn);
				
				readSoFar += bytesIn;
				baos.flush();
			}
			
			status.setSize(readSoFar);
		}
		
		return status;
	}
	
	public static long writeToStream(InputStream stream, OutputStream baos, long lowerBound, long higherBound) throws IOException {
		if(lowerBound == 0 && higherBound == 0){
			return writeToStream(stream, baos);
		} else{
			final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
			int bytesIn = 0;
			long readSoFar = 0;
			long len = higherBound - lowerBound + 1;
			stream.skip(lowerBound);
			while (readSoFar < len && (bytesIn = stream.read(readBuffer, 0, (int) Math.min(readBuffer.length, len - readSoFar))) != -1) {
				baos.write(readBuffer, 0, bytesIn);
				readSoFar += bytesIn;
				baos.flush();
			}
			return readSoFar;
		}
	}
	
	public static String getStreamCheckSum(InputStream stream) throws IOException, NoSuchAlgorithmException
	{
		OS3CheckSum ocs = new OS3CheckSum(EnvironmentalConstants.getEncryptionType());
		
		final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
		int bytesIn = 0;
		while ((bytesIn = stream.read(readBuffer)) != -1) {
			if(ocs!=null)	ocs.update(readBuffer, 0, bytesIn);
		}
		
		return ocs.getValue();
	}

	public static long writeToStream(InputStream stream, OutputStream baos) throws IOException {
		final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
		return readSoFar;
	}

	public static long readAndSkipContentsOfStream(InputStream stream) throws IOException {
		final byte[] readBuffer = new byte[EnvironmentalConstants.getStreamBufferSize()];
		int bytesIn = 0;
		long readSoFar = 0;
		while((bytesIn = stream.read(readBuffer, 0, readBuffer.length)) != -1) {
			readSoFar += bytesIn;
		}
		return readSoFar;
	}
	
	public static String[] splitPathInfo(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if(pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1, pathInfo.length());
		}
		if(pathInfo.isEmpty()){
			return null;
		}
		return pathInfo.split("/");
	}
}
