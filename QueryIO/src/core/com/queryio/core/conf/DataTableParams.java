/**
 * 
 */
package com.queryio.core.conf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.AppLogger;

/**
 * This class manages server side handling parameters for DataTables.
 * 
 */
public class DataTableParams {
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private Long draw;
	private Long offsetInt;
	private Long countInt;
	private String searchVal = "";
	private int searchColIndex = -1;
	private Long orderByCol;
	private String orderByDir;

	public DataTableParams(String request, int colSize) throws Exception {
		JSONArray paramsDT = null;
		try {
			JSONParser parser = new JSONParser();
			Object parse = parser.parse(request);
			if (parse != null) {
				paramsDT = (JSONArray) parse;
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("DataTable params : " + paramsDT.toString());
				for (Object param : paramsDT) {
					JSONObject data = (JSONObject) param;
					String nameData = (String) data.get(NAME);
					if (nameData.equalsIgnoreCase("draw")) {
						this.draw = (Long) data.get(VALUE);
					} else if (nameData.equalsIgnoreCase("columns")) {
						JSONArray object = (JSONArray) data.get(VALUE);
						parseColumns(object);
					} else if (nameData.equalsIgnoreCase("order")) {
						JSONArray object = (JSONArray) data.get(VALUE);
						parseOrder(object);
					} else if (nameData.equalsIgnoreCase("start")) {
						this.offsetInt = (Long) data.get(VALUE);
					} else if (nameData.equalsIgnoreCase("length")) {
						this.countInt = (Long) data.get(VALUE);
					} else if (nameData.equalsIgnoreCase("search")) {
						JSONObject search = (JSONObject) data.get(VALUE);
						this.searchVal = (String) search.get(VALUE);
					}

				}

			}

			// String offset = request.getParameter("start");
			// String count = request.getParameter("length");
			// this.offsetInt = Integer.parseInt(offset);
			// this.countInt = Integer.parseInt(count);
			//
			// String searchParamPrefix = "columns[";
			// String searchParamSuffix = "][search][value]";
			//
			// this.orderByCol =
			// Integer.parseInt(request.getParameter("order[0][column]"));
			// this.orderByDir = request.getParameter("order[0][dir]");
			// for (int i = 0; i < colSize; i++) {
			// if (StringUtils.isNotEmpty(request.getParameter(searchParamPrefix
			// + i + searchParamSuffix))) {
			// this.searchVal = request.getParameter(searchParamPrefix + i +
			// searchParamSuffix);
			// this.searchColIndex = i;
			// break;
			// }
			// }
			//
			// if (this.searchColIndex == -1) {
			// this.searchVal = request.getParameter("search[value]");
			// }
		} catch (Exception e) {
			AppLogger.getLogger().error("Error in parsing DataTable pagination detials." + e.getMessage(), e);
			throw e;
		}
	}

	private void parseOrder(JSONArray object) {
		for (Object colObj : object) {
			JSONObject columnObj = (JSONObject) colObj;
			this.orderByCol = (Long) columnObj.get("column");
			this.orderByDir = (String) columnObj.get("dir");

		}
	}

	private void parseColumns(JSONArray object) {
		for (Object colObj : object) {
			JSONObject columnObj = (JSONObject) colObj;
			// TODO search on specific column
		}
	}

	public long getOffsetInt() {
		return offsetInt;
	}

	public void setOffsetInt(long offsetInt) {
		this.offsetInt = offsetInt;
	}

	public long getCountInt() {
		return countInt;
	}

	public void setCountInt(long countInt) {
		this.countInt = countInt;
	}

	public String getSearchVal() {
		return searchVal;
	}

	public void setSearchVal(String searchVal) {
		this.searchVal = searchVal;
	}

	public int getSearchColIndex() {
		return searchColIndex;
	}

	public void setSearchColIndex(int searchColIndex) {
		this.searchColIndex = searchColIndex;
	}

	public long getOrderByCol() {
		return orderByCol;
	}

	public void setOrderByCol(long orderByCol) {
		this.orderByCol = orderByCol;
	}

	public String getOrderByDir() {
		return orderByDir;
	}

	public void setOrderByDir(String orderByDir) {
		this.orderByDir = orderByDir;
	}

	public long getDraw() {
		return draw;
	}

	public void setDraw(long draw) {
		this.draw = draw;
	}

	@Override
	public String toString() {
		return "DataTableParams [draw=" + draw + ", offsetInt=" + offsetInt + ", countInt=" + countInt + ", searchVal="
				+ searchVal + ", searchColIndex=" + searchColIndex + ", orderByCol=" + orderByCol + ", orderByDir="
				+ orderByDir + "]";
	}

}
