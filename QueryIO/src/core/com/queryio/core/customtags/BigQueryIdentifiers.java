package com.queryio.core.customtags;

public interface BigQueryIdentifiers {
	
	String SELECTEDCOLUMNLIST = "selectedColumnList";
	String ISCONTAINSMULTIPLEQUERIES = "isContainsMultipleQueries";
	String USERNAME = "username";
	String NAMENODEID = "namenode";
	String DBNAME = "dbName";
	String QUERYID = "queryId";
	
	String SHEETID = "sheetId";
	String TYPE = "type";
	String TYPE_RESULT = "result";
	String TYPE_SAVE_SHEET = "save";
	String TYPE_GET_SHEET = "getContent";
	
	String TYPE_SLICK = "slick";
	String TYPE_REMOTE_MODEL = "remoteModel";
	String TYPE_OFFSET = "offset";
	String TYPE_COUNT = "count";
	
	String PERSISTRESULTS = "persistResults";
	String ISFILTERQUERY = "isFilterQuery";
	String QUERYFILTERDETAIL = "queryFilterDetail";
	String FILTERQUERY = "filterQuery";
	String FILTERQUERY_FILEPATH = "filepath";
	String FILTERQUERY_VALUE = "value";
	
	String QUERYDESC = "queryDesc";
	String SQLQUERY = "sqlQuery";
	String CHARTDETAIL = "chartDetail";
	String COLHEADERDETAIL = "colHeaderDetail";
	String COLDETAIL = "colDetail";
	String QUERYHEADER = "queryHeader";
	String QUERYFOOTER = "queryFooter";
	String GROUPHEADER = "groupHeader";
	String GROUPFOOTER = "groupFooter";
	String AGGREGATEONCOLUMN = "aggregateOnColumn";
	String SELECTEDTABLE = "selectedTable";
	String SELECTEDWHERE = "selectedWhere";
	String RESULTTABLENAME = "resultTableName";
	String OLD_RESULTTABLENAME = "oldResultTableName";
	
	String SETHIGHFIDELITYOUTPUT = "setHighFidelityOutput";
	String SETLIMITRESULTROWS = "setLimitResultRows";
	String LIMITRESULTROWSVALUE = "limitResultRowsValue";
	String ISRUNQUERY = "isRunQuery";
	String X_SERIES = "xseries";
	String X_SERIES_SORT_TYPE = "xseriesSortType";
	String X_SERIES_SORT_COLUMN = "xseriesSortColumn";
	String X_SERIES_SORT_TYPE_NONE = "None";
	String X_SERIES_SORT_TYPE_ASCENDING = "Ascending";
	String X_SERIES_SORT_TYPE_DESCENDING = "Descending";
	
	int REPORT_ROWS_LIMIT = 300;
}