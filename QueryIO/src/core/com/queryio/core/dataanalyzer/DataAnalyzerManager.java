package com.queryio.core.dataanalyzer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataAnalyzerManager extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String rows = request.getParameter("rows");
		String pageno = request.getParameter("page");
		String sidx = request.getParameter("sidx");
		String query = request.getParameter("query");

		JSONObject rowData = new JSONObject();
		JSONArray rowArray = new JSONArray();
		int cpage = pageno != null ? Integer.parseInt(pageno) : 0;
		int crow = rows != null ? Integer.parseInt(rows) : 0;
		rowData.put("total", 0);
		rowData.put("page", 0);
		rowData.put("records", 0);
		rowData.put("records", 30);
		for (int i = 0; i < 30; i++) {

			JSONObject rowObject = new JSONObject();
			rowObject.put("id", i + 1);
			JSONArray tableRow = new JSONArray();

			for (int j = 0; j < 5; j++) {

				tableRow.add("ColVal" + j);
			}
			rowObject.put("cell", tableRow);
			rowArray.add(rowObject);
		}

		rowData.put("rows", rowArray);
		rowData.put("row", rowArray);
		response.getWriter().println(rowData);

	}

}
