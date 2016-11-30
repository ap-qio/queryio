package com.queryio.demo.adhoc;

import java.io.InputStream;

public interface IAdHocParser {
	void setExpressions(ParsedExpression parsedExpression);

	void setArguments(String arguments) throws Exception;

	void parse(DBListener dbListener, String filePath, InputStream is) throws Exception;
}