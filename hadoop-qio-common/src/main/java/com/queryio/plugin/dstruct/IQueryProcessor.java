package com.queryio.plugin.dstruct;

import java.io.InputStream;

public interface IQueryProcessor {
	void processQuery(InputStream is, String filterExpression);
}
