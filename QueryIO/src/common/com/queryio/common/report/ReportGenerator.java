package com.queryio.common.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;

import com.queryio.common.util.AppLogger;

public class ReportGenerator {
	public static void main(String[] args) {
		AppLogger.initLoggerProperties("/Users/indravardhan/QueryIO/tomcat/webapps/queryio/logger.properties",
				"/Users/indravardhan/QueryIO/tomcat/webapps/queryio/logs");
		System.out.println(generateViewReport(
				"/Users/indravardhan/QueryIO/tomcat/webapps/queryio/Reports/a/namenode1/new_query_5243.rptdesign",
				ReportConstants.TYPE_HTML));
	}

	public static String generateViewReport(String designFilePath, String format) {
		String outputFilePath = null;
		String outputFileName = null;
		IReportEngine engine = null;
		IReportRunnable design = null;
		IRunAndRenderTask task = null;
		RenderOption options = null;
		BufferedWriter output = null;
		FileWriter writer = null;
		try {
			engine = ReportHandler.getReportEngine();
			design = engine.openReportDesign(designFilePath);

			task = engine.createRunAndRenderTask(design);

			// ReportDesignHandle report = (ReportDesignHandle) design
			// .getDesignHandle();
			//
			// OdaDataSourceHandle dataSource = (OdaDataSourceHandle) report
			// .getAllDataSources().get(0);

			// CusomTagDatabase dbProps =
			// RemoteManager.getCustomTagDBProperties();
			// dataSource.setProperty("odaURL", dbProps.getUrl());
			// dataSource.setProperty("odaDriverClass",
			// dbProps.getDriverName());
			// dataSource.setProperty("odaUser", dbProps.getUserName());
			// dataSource.setProperty("odaPassword", dbProps.getPassword());

			if (format.equalsIgnoreCase(ReportConstants.TYPE_HTML)) {
				outputFilePath = designFilePath.replace(".rptdesign", ".html");
				AppLogger.getLogger().debug("outputFilePath : " + outputFilePath);
				AppLogger.getLogger().debug("File.separator : " + File.separator);
				AppLogger.getLogger()
						.debug("Index of last File.separator : " + outputFilePath.lastIndexOf(File.separator));
				outputFileName = outputFilePath.substring(outputFilePath.lastIndexOf(File.separator));
				AppLogger.getLogger().debug("outputFilePath after substring: " + outputFileName);

				options = new HTMLRenderOption();

				String temp = outputFileName.replace(File.separator, "");

				File resourceDirecory = new File(outputFilePath.substring(0, outputFilePath.lastIndexOf(File.separator))
						+ File.separator + "resources_" + temp.replace(".html", ""));

				resourceDirecory.mkdirs();

				((HTMLRenderOption) options).setImageDirectory("resources_" + temp.replace(".html", ""));

				options.setOutputFileName(outputFilePath);
				options.setOutputFormat(ReportConstants.TYPE_HTML);
			} else if (format.equalsIgnoreCase(ReportConstants.TYPE_PDF)) {
				outputFilePath = designFilePath.replace(".rptdesign", ".pdf");
				outputFileName = outputFilePath.substring(outputFilePath.lastIndexOf(File.separator));

				options = new PDFRenderOption();
				options.setOutputFileName(outputFilePath);
				options.setOption(PDFRenderOption.PAGE_OVERFLOW, PDFRenderOption.FIT_TO_PAGE_SIZE);
				options.setOption(PDFRenderOption.PDF_HYPHENATION, true);
				options.setOption(PDFRenderOption.PDF_TEXT_WRAPPING, true);
				options.setOutputFormat(ReportConstants.TYPE_PDF);
				options.setOption(PDFRenderOption.CHART_DPI, 96);

			} else if (format.equalsIgnoreCase(ReportConstants.TYPE_XLS)) {
				outputFilePath = designFilePath.replace(".rptdesign", ".xls");
				outputFileName = outputFilePath.substring(outputFilePath.lastIndexOf(File.separator));

				options = new EXCELRenderOption();

				options.setOutputFileName(outputFilePath);
				options.setOutputFormat(ReportConstants.TYPE_XLS);
				options.setOption(EXCELRenderOption.CHART_DPI, true);
				options.setSupportedImageFormats("png");
				// ((EXCELRenderOption) options).setBaseURL("&__format=xls");
				((EXCELRenderOption) options).setWrappingText(false);
				options.setOption(EXCELRenderOption.OPTION_MULTIPLE_SHEET, false);
				((EXCELRenderOption) options).setOfficeVersion("office2003");
				options.setOption(EXCELRenderOption.CHART_DPI, 96);

				options.setOption(EXCELRenderOption.EMITTER_ID,
						"org.eclipse.birt.report.engine.emitter.prototype.excel");

			}

			task.setRenderOption(options);

			// Connection connection = null;
			// if (dataSource.getExtensionID().endsWith("oda.hive"))
			// {
			// Connection queryIOConnection = null;
			// try{
			// queryIOConnection = CoreDBManager.getQueryIODBConnection();
			// String nameNodeId =
			// NodeDAO.getNameNodeForAnalyticsDBNameMapping(queryIOConnection,
			// dataSource.getName());
			//
			// if (nameNodeId == null)
			// nameNodeId =
			// NodeDAO.getNameNodeForDBNameMapping(queryIOConnection,
			// dataSource.getName());
			//
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("nameNodeId: "+nameNodeId);
			//
			// connection = AdHocHiveClient.getHiveConnection(queryIOConnection,
			// nameNodeId);
			// } catch(Exception e) {
			// AppLogger.getLogger().fatal(e.getMessage(), e);
			// } finally {
			// try{
			// CoreDBManager.closeConnection(queryIOConnection);
			// } catch(Exception e){
			// AppLogger.getLogger().fatal(e.getMessage(), e);
			// }
			// }
			// }
			// else
			// connection =
			// CoreDBManager.getCustomTagDBConnection(dataSource.getName());
			//
			// task.getAppContext().put("OdaJDBCDriverPassInConnection",
			// connection);

			task.run();
			if (format.equalsIgnoreCase(ReportConstants.TYPE_HTML)) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("File path :"+outputFilePath);
				File file = new File(outputFilePath);
				if (file.exists()) {
					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("File has been found.");
					writer = new FileWriter(file, true);
					output = new BufferedWriter(writer);
					output.append("<script type=\"text/javascript\">"
							+ "var tables = document.getElementsByTagName('table');"
							+ "for(var i= 0;i<tables.length;i++){" + "var firstTableBefore = tables[i];"
							+ "firstTableBefore.style.width=\"100%\";" + "firstTableBefore.style.tableLayout=\"\";"
							+ "firstTableBefore.style.overflow = \"auto\";" + "}"
							+ "var header = document.getElementsByClassName('style_4');"
							+ "for(var i= 0;i<header.length;i++){" + "var firstTableBefore = header[i];"
							+ "firstTableBefore.style.wordWrap = \"break-word\" ;" + "}"
							+ "var images = document.getElementsByTagName('img');"
							+ "for(var i = 0; i < images.length; i++) {" + "var src = images[i].src;"
							+ "if (src.indexOf('file:/') != -1)" + "{"
							+ "var newSrc = src.substring(src.indexOf('resources_'));" + "images[i].src= newSrc;" + "}"
							+ "}" + "var tds = document.getElementsByTagName('img');"
							+ "for (var n=0; n<tds.length;n++)" + "{" + "var divr = tds[n].parentNode;"
							+ "if(divr==undefined || divr == null)" + "continue;"
							+ "divr.style.display = \"inline-block\";" + "divr.parentNode.style.display = \"-web-box\";"
							+

							"}" +

							"</script>");
					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("File has been written.");
				} else {
					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("File not found");
				}
			} else {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("File Format is not html");
			}
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Finished Report Generating");
			// System.out.println("Finished Generating");
		} catch (Exception e) {
			// e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			if (task != null)
				task.close();
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					// e.printStackTrace();
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// e.printStackTrace();
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
		return outputFileName.replace(File.separator, "");
	}
}