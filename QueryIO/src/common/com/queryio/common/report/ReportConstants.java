package com.queryio.common.report;

import org.eclipse.birt.report.model.api.SharedStyleHandle;

import com.google.common.collect.HashBiMap;

public class ReportConstants
{
	public static final String BIRT_HOME = "BIRT_HOME";
	
	public static final String TYPE_HTML = "html";
	public static final String TYPE_PDF = "pdf";
	public static final String TYPE_XLS = "xls";
	
	public static final HashBiMap CSSSTYLEMAP = HashBiMap.create();
	
	static{
		CSSSTYLEMAP.put("font-size", SharedStyleHandle.FONT_SIZE_PROP);
		CSSSTYLEMAP.put("font-family", SharedStyleHandle.FONT_FAMILY_PROP);
		CSSSTYLEMAP.put("color", SharedStyleHandle.COLOR_PROP);
		CSSSTYLEMAP.put("background-color", SharedStyleHandle.BACKGROUND_COLOR_PROP);
		CSSSTYLEMAP.put("text-align", SharedStyleHandle.TEXT_ALIGN_PROP);
		CSSSTYLEMAP.put("font-weight", SharedStyleHandle.FONT_WEIGHT_PROP);
		CSSSTYLEMAP.put("font-style", SharedStyleHandle.FONT_STYLE_PROP);
	}
	
	public static String getBirtPropertyFromCSS(String key) {
		return (String) CSSSTYLEMAP.get(key);
	}
	
	public static String getCSSPropertyFromBirt(String key) {
		return (String) CSSSTYLEMAP.inverse().get(key);
	}
}