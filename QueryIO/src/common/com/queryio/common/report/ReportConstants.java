package com.queryio.common.report;

import org.eclipse.birt.report.model.api.SharedStyleHandle;

import com.google.common.collect.HashBiMap;

public class ReportConstants
{
	public static String BIRT_HOME = "BIRT_HOME";
	
	public static String TYPE_HTML = "html";
	public static String TYPE_PDF = "pdf";
	public static String TYPE_XLS = "xls";
	
	public static final HashBiMap cssStyleMap = HashBiMap.create();
	
	static{
		cssStyleMap.put("font-size", SharedStyleHandle.FONT_SIZE_PROP);
		cssStyleMap.put("font-family", SharedStyleHandle.FONT_FAMILY_PROP);
		cssStyleMap.put("color", SharedStyleHandle.COLOR_PROP);
		cssStyleMap.put("background-color", SharedStyleHandle.BACKGROUND_COLOR_PROP);
		cssStyleMap.put("text-align", SharedStyleHandle.TEXT_ALIGN_PROP);
		cssStyleMap.put("font-weight", SharedStyleHandle.FONT_WEIGHT_PROP);
		cssStyleMap.put("font-style", SharedStyleHandle.FONT_STYLE_PROP);
	}
	
	public static String getBirtPropertyFromCSS(String key) {
		return (String) cssStyleMap.get(key);
	}
	
	public static String getCSSPropertyFromBirt(String key) {
		return (String) cssStyleMap.inverse().get(key);
	}
}