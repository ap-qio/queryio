package com.queryio.core.customtags;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.AppLogger;

public class SpreadSheetServlet extends HttpServlet
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new dB explorer servlet.
	 */
	public SpreadSheetServlet()
	{
		
    }
	
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		processRequest(request, response);
		}
    	catch (Exception e)
    	{
    		AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
    		throw new ServletException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			processRequest(request, response);
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			throw new ServletException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String type = null;
		String nameNodeId = null;
		String queryId = null;
		String sheetId = null;
		String userName = null;
		PrintWriter writer = null;
		InputStream inStream = null;
		OutputStream outStream = null;
		
		try
		{
			type = request.getParameter(BigQueryIdentifiers.TYPE);
			AppLogger.getLogger().debug("Request Recieved for spread sheet. Type "+type);
			
			userName = request.getParameter(BigQueryIdentifiers.USERNAME);
			
			if (type.equalsIgnoreCase(BigQueryIdentifiers.TYPE_REMOTE_MODEL))
			{
				nameNodeId = request.getParameter(BigQueryIdentifiers.NAMENODEID);
				queryId = request.getParameter(BigQueryIdentifiers.QUERYID);
				int offset = Integer.parseInt(request.getParameter(BigQueryIdentifiers.TYPE_OFFSET));
				int count = Integer.parseInt(request.getParameter(BigQueryIdentifiers.TYPE_COUNT));
				
				if ((nameNodeId != null) && (queryId != null))
				{
					writer = response.getWriter();
					response.setContentType("text/plain");
					BigQueryManager.getSpreadSheetSlickResults(queryId, nameNodeId, count, offset, writer);
				}
			}
			else if (type.equalsIgnoreCase(BigQueryIdentifiers.TYPE_RESULT))
			{
				nameNodeId = request.getParameter(BigQueryIdentifiers.NAMENODEID);
				queryId = request.getParameter(BigQueryIdentifiers.QUERYID);
				
				String isRequestContainsMultipleQueries = request.getParameter(BigQueryIdentifiers.ISCONTAINSMULTIPLEQUERIES);
				
				boolean isQueryIdinArray = false;
				if(isRequestContainsMultipleQueries != null)
					isQueryIdinArray =Boolean.parseBoolean(isRequestContainsMultipleQueries);
				
				String isRunAgainFlag = request.getParameter(BigQueryIdentifiers.ISRUNQUERY);
				
				boolean isRunQuery = true;
				
				if(isRunAgainFlag != null)
					isRunQuery =Boolean.parseBoolean(isRunAgainFlag);
			
				writer = response.getWriter();
				response.setContentType("text/plain");
				
				
				
				AppLogger.getLogger().debug("isRunAgainFlag "+isRunAgainFlag);
				AppLogger.getLogger().debug("isRunQuery "+isRunQuery);
				AppLogger.getLogger().debug("isRequestContainsMultipleQueries "+isRequestContainsMultipleQueries);
				AppLogger.getLogger().debug("isQueryIdinArray "+isQueryIdinArray);
				AppLogger.getLogger().debug("queryId "+queryId);
				AppLogger.getLogger().debug("nameNodeId "+nameNodeId);
				if(nameNodeId != null){
					writer.write("[");
					if(isQueryIdinArray){
						
						
						JSONParser parser = new JSONParser();
						JSONArray  queryIdArray = (JSONArray)parser.parse(queryId);
						
						for (int i = 0; i < queryIdArray.size(); i++) {
							
							if ((String) queryIdArray.get(i) != null)
							{
								if(i>0)
									writer.write(",");
								AppLogger.getLogger().debug("Request send for qid from json"+(String) queryIdArray.get(i));
								BigQueryManager.getSpreadSheetResults((String) queryIdArray.get(i), nameNodeId, userName, writer,isRunQuery);
								
							}
						}
						
						
						
					}else{
						
						if (queryId != null)
						{
							AppLogger.getLogger().debug("Request send for qid "+queryId);
							BigQueryManager.getSpreadSheetResults(queryId, nameNodeId, userName, writer,isRunQuery);
							
						}
					}
					writer.write("]");
				}

				writer.flush();
				
				
			}
			else if (type.equalsIgnoreCase(BigQueryIdentifiers.TYPE_SAVE_SHEET))
			{
				nameNodeId = request.getParameter(BigQueryIdentifiers.NAMENODEID);
				sheetId = request.getParameter(BigQueryIdentifiers.SHEETID);
				
				if ((nameNodeId != null) && (sheetId != null))
				{
					inStream = request.getInputStream();
					BigQueryManager.saveSpreadSheet(sheetId, nameNodeId, inStream);
				}
			}
			else if (type.equalsIgnoreCase(BigQueryIdentifiers.TYPE_GET_SHEET))
			{
				sheetId = request.getParameter(BigQueryIdentifiers.SHEETID);
				
				if (sheetId != null)
				{
					outStream = response.getOutputStream();
					response.setContentType("text/plain");
					BigQueryManager.getSpreadSheetContent(sheetId, outStream);
				}
			}
		}
		finally
		{
			if (writer != null)
				writer.close();
			
			try {
				if (inStream != null)
					inStream.close();
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
			
			try {
				if (outStream != null)
					outStream.close();
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
	}
}