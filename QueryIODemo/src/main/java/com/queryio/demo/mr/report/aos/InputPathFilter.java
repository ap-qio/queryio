package com.queryio.demo.mr.report.aos;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class InputPathFilter implements PathFilter {

	@Override
	public boolean accept(Path path) {
		return path.toString().endsWith(".jtl");
	}

}