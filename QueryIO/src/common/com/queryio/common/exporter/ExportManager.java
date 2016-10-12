/*
 * @(#)  ExportManager.java May 25, 2005
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.exporter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.exporter.html.HTMLExporter;
import com.queryio.common.exporter.pdf.PDFExporter;
import com.queryio.common.exporter.xls.XLSExporter;
import com.queryio.common.exporter.xml.XMLExporter;
import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.PathFinder;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.ResourceManager;


/**
 * ExportManager
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class ExportManager
{
	public static final transient ResourceManager RM = CommonResourceManager.loadResources("Common_Exporter"); //$NON-NLS-1$

	private static ExportManager thisInstance = null;
	private static int chartUIFactory = IProductConstants.USER_INTERFACE_SWING;

	private ArrayList nodesToExport;

	private String exportLocation;

	private boolean exportIndividualNode = false;

	private int exportType;

	private boolean createNewInstances;

	/**
	 * Constructor
	 */
	private ExportManager()
	{
		// Constructor has been made private to prevent external object
		// instantiation of this class.
		// Use the getInstance() method in order to get an instance of this
		// class.
	}

	public static ExportManager getNewInstance()
	{
		return new ExportManager();
	}

	/**
	 * Method getInstance
	 * 
	 * @param uiFactory
	 * @return
	 */
	public static ExportManager getInstance()
	{
		if (thisInstance == null)
		{
			thisInstance = getNewInstance();
		}
		return thisInstance;
	}

	/**
	 * @param sBrowserPath
	 * @param sOutputFile
	 * @throws Exception
	 */
	public void showExportedReport(final String sBrowserPath, final String sOutputFile) throws Exception
	{
		if (sOutputFile != null)
		{
			final StringBuffer sbURL = new StringBuffer();
			sbURL.append(PathFinder.getSystemSpecificEscapedPath(sBrowserPath));
			if (!PlatformHandler.isMacOS())
			{
				if (sOutputFile.charAt(0) == '/')
				{
					sbURL.append(" file://"); //$NON-NLS-1$
				}
				else
				{
					sbURL.append(" file:///"); //$NON-NLS-1$
				}
			}
			else
			{
				sbURL.append(" ");
			}
			sbURL.append(sOutputFile);
			Runtime.getRuntime().exec(sbURL.toString());
		}
	}

	/**
	 * @param uiFactory
	 */
	public static void setChartUIFactory(final int uiFactory)
	{
		chartUIFactory = uiFactory;
	}

	/**
	 * @return
	 */
	public static UserInterface getUserInterface()
	{
		return UIGraphicsFactory.getUserInterface(chartUIFactory);
	}

	/**
	 * @param summaryNode
	 */
	public void exportNodeAtEnd(final AbstractExportableNode summaryNode)
	{
		if (this.nodesToExport == null)
		{
			this.nodesToExport = new ArrayList(1);
		}
		if (!this.nodesToExport.contains(summaryNode))
		{
			this.nodesToExport.add(summaryNode);
		}
	}

	/**
	 * @return
	 */
	public final String getExportLocation()
	{
		return this.exportLocation;
	}

	/**
	 * @return
	 */
	public final boolean isExportIndividualNode()
	{
		return this.exportIndividualNode;
	}

	/**
	 * @return
	 */
	public final int getExportType()
	{
		return this.exportType;
	}

	/**
	 * Method exportNode
	 * 
	 * @param projectName
	 * @param nodeToExport
	 * @param exportType
	 * @throws ExporterException
	 */
	public void exportNode(final String projectName, final String reportHeaderImagePath, final AbstractExportableNode nodeToExport, final int exportType)
			throws ExporterException
	{
		this.exportIndividualNode = true;
		this.exportType = exportType;
		AbstractExporter exporter = null;
		switch (exportType)
		{
			case ExportConstants.EXPORT_TYPE_HTML:
			{
				exporter = this.createNewInstances ? HTMLExporter.getNewInstance() : HTMLExporter.getInstance();
				exporter.reset();
				exporter.setSize(980, 570);
				break;
			}
			case ExportConstants.EXPORT_TYPE_PDF:
			{
				exporter = this.createNewInstances ? PDFExporter.getNewInstance() : PDFExporter.getInstance();
				exporter.reset();
				//exporter.setSize(595, 842); // Default size for A4 page
				exporter.setSize(842, 595); // Default size for A4 page - Landscape
				break;
			}
			case ExportConstants.EXPORT_TYPE_XLS:
			{
				exporter = this.createNewInstances ? XLSExporter.getNewInstance() : XLSExporter.getInstance();
				exporter.setSize(595, 842);
				break;
			}
			case ExportConstants.EXPORT_TYPE_XML:
			{
				exporter = this.createNewInstances ? XMLExporter.getNewInstance() : XMLExporter.getInstance();
				exporter.setSize(595, 842);
				break;
			}
			default:
			{
				throw new ExporterException(RM.getString(RM.getString("VALUE_INVALID_EXPORTER_TYPE")) + exportType); //$NON-NLS-1$
			}
		}
		exporter.setProjectName(projectName);
		exporter.setHeaderImagePath(reportHeaderImagePath);
		exporter.setExportingResult(false);
		exporter.exportNode(nodeToExport);
		this.exportIndividualNode = false;
	}

	public void exportResult(final String projectName, final String reportHeaderImagePath, 
		final String location, final ArrayList models, final int exportType) throws ExporterException
	{
		this.exportType = exportType;
		switch (exportType)
		{
			case ExportConstants.EXPORT_TYPE_HTML:
			{
				exportHTMLResult(projectName, reportHeaderImagePath, location, models);
				break;
			}
			case ExportConstants.EXPORT_TYPE_PDF:
			{
				exportPDFResult(projectName, reportHeaderImagePath, location, models);
				break;
			}
			case ExportConstants.EXPORT_TYPE_XML:
			{
				exportXMLResult(projectName, reportHeaderImagePath, location, models);
				break;
			}
			default:
			{
				throw new ExporterException("Format not supported for complete result, only HTML, PDF & XML is supported."); //$NON-NLS-1$
			}
		}
	}
	
	public void exportProject(final String projectName, final String projectHeaderImagePath, 
			final String location, final ArrayList models, final int exportType) throws ExporterException
		{
			this.exportType = exportType;
			switch (exportType)
			{
				case ExportConstants.EXPORT_TYPE_HTML:
				{
					exportHTMLProject(projectName, projectHeaderImagePath, location, models);
					break;
				}
				default:
				{
					throw new ExporterException("Format not supported for complete result, only HTML, PDF & XML is supported."); //$NON-NLS-1$
				}
			}
		}
	
	private void exportXMLResult(final String projectName, final String reportHeaderImagePath, 
		final String location, final ArrayList models) throws ExporterException
	{
		this.exportIndividualNode = false;
		this.createLocation(location, false);
		final XMLExporter exporter = this.createNewInstances ? XMLExporter.getNewInstance() : XMLExporter.getInstance();
		exporter.reset();
		exporter.setProjectName(projectName);
		exporter.setHeaderImagePath(reportHeaderImagePath);
		//exporter.setSize(595, 842); // Default size for A4 page
		exporter.setSize(842, 595); // Default size for A4 page - Landscape
		exporter.setExportingResult(true);
		exporter.exportResult(location, models);
		exporter.setExportingResult(false);
	}
	
	private void exportPDFResult(final String projectName, final String reportHeaderImagePath, 
		final String location, final ArrayList models) throws ExporterException
	{
		this.exportIndividualNode = false;
		this.createLocation(location, false);
		final PDFExporter exporter = this.createNewInstances ? PDFExporter.getNewInstance() : PDFExporter.getInstance();
		exporter.reset();
		exporter.setProjectName(projectName);
		exporter.setHeaderImagePath(reportHeaderImagePath);
		//exporter.setSize(595, 842); // Default size for A4 page
		exporter.setSize(842, 595); // Default size for A4 page - Landscape
		exporter.setExportingResult(true);
		exporter.exportResult(location, models);
		exporter.setExportingResult(false);
	}
	
	/**
	 * Method exportResult
	 * 
	 * @param projectName
	 * @param location
	 * @param models
	 * @throws ExporterException
	 */
	private void exportHTMLResult(final String projectName, final String reportHeaderImagePath, 
		final String location, final ArrayList models) throws ExporterException
	{
		this.exportIndividualNode = false;
		this.createLocation(location, true);
		final ArrayList alImages = new ArrayList();
		final HTMLExporter exporter = this.createNewInstances ? HTMLExporter.getNewInstance() : HTMLExporter.getInstance();
		exporter.reset();
		exporter.setProjectName(projectName);
		exporter.setHeaderImagePath(reportHeaderImagePath);
		exporter.setSize(760, 570);
		exporter.setImageList(alImages);
		exporter.setExportingResult(true);
		exporter.exportResult(location, models);
		alImages.add("empty.gif"); //$NON-NLS-1$
		alImages.add("plus_new.gif"); //$NON-NLS-1$
		alImages.add("minus_new.gif"); //$NON-NLS-1$
		alImages.add("menu.png"); //$NON-NLS-1$
		alImages.add("menu-active.png"); //$NON-NLS-1$
		alImages.add("menu-hover.png"); //$NON-NLS-1$
		alImages.add("chart-back.png"); //$NON-NLS-1$
		final ArrayList alJsFiles = new ArrayList(3);
		alJsFiles.add("tree.js"); //$NON-NLS-1$
		alJsFiles.add("encode.js"); //$NON-NLS-1$
		alJsFiles.add("tabs.js"); //$NON-NLS-1$
		final ArrayList alCssFiles = new ArrayList(1);
		alCssFiles.add("tree.css"); //$NON-NLS-1$
		this.copyFiles(alImages, alJsFiles, alCssFiles, location + File.separatorChar + "reports"); //$NON-NLS-1$
		this.exportPendingFile(projectName, reportHeaderImagePath, location);
	}
	
	/**
	 * Method exportResult
	 * 
	 * @param projectName
	 * @param location
	 * @param models
	 * @throws ExporterException
	 */
	private void exportHTMLProject(final String projectName, final String reportHeaderImagePath, 
		final String location, final ArrayList models) throws ExporterException
	{
		try
		{
			this.exportIndividualNode = false;
			this.createLocation(location, true);
			final ArrayList alImages = new ArrayList();
			final HTMLExporter exporter = this.createNewInstances ? HTMLExporter.getNewInstance() : HTMLExporter.getInstance();
			exporter.reset();
			exporter.setProjectName(projectName);
			exporter.setHeaderImagePath(reportHeaderImagePath);
			exporter.setSize(760, 570);
			exporter.setImageList(alImages);
			exporter.setExportingResult(true);
			exporter.exportProject(location, models);
			alImages.add("empty.gif"); //$NON-NLS-1$
			alImages.add("plus_new.gif"); //$NON-NLS-1$
			alImages.add("minus_new.gif"); //$NON-NLS-1$
			alImages.add("menu.png"); //$NON-NLS-1$
			alImages.add("menu-active.png"); //$NON-NLS-1$
			alImages.add("menu-hover.png"); //$NON-NLS-1$
			alImages.add("chart-back.png"); //$NON-NLS-1$
			final ArrayList alJsFiles = new ArrayList(3);
			alJsFiles.add("tree.js"); //$NON-NLS-1$
			alJsFiles.add("encode.js"); //$NON-NLS-1$
			alJsFiles.add("tabs.js"); //$NON-NLS-1$
			final ArrayList alCssFiles = new ArrayList(1);
			alCssFiles.add("tree.css"); //$NON-NLS-1$
			this.copyFiles(alImages, alJsFiles, alCssFiles, location + File.separatorChar + "reports"); //$NON-NLS-1$
			this.exportPendingFile(projectName, reportHeaderImagePath, location);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void exportPendingFile(final String projectName, final String reportHeaderImagePath, final String exportLocation) throws ExporterException
	{
		if ((this.nodesToExport != null) && (this.nodesToExport.size() > 0))
		{
			for (int i = 0; i < this.nodesToExport.size(); ++i)
			{
				final AbstractExportableNode node = (AbstractExportableNode) this.nodesToExport.get(i);
				node.setFilePath(exportLocation);
				this.exportNode(projectName, reportHeaderImagePath, node, ExportConstants.EXPORT_TYPE_HTML);
			}
			this.nodesToExport.clear();
		}
	}

	private void createLocation(final String location, boolean createOthers) throws ExporterException
	{
		File file = new File(location);
		if (file.exists())
		{
			if (!file.isDirectory())
			{
				throw new ExporterException(RM.getString(RM.getString("VALUE_ERR_CREATING_DIR")) + file.getPath()); //$NON-NLS-1$
			}
		}
		else
		{
			file.mkdirs();
		}
		
		if (createOthers)
		{
			file = new File(location + File.separatorChar + "reports"); //$NON-NLS-1$
			if (file.exists())
			{
				if (!file.isDirectory())
				{
					throw new ExporterException(RM.getString("ERR_CREATING_DIRECTORY") + file.getPath()); //$NON-NLS-1$
				}
			}
			else
			{
				file.mkdir();
			}
			
			file = new File(location + File.separatorChar + "reports" + File.separatorChar + "images"); //$NON-NLS-1$ //$NON-NLS-2$
			if (file.exists())
			{
				if (!file.isDirectory())
				{
					throw new ExporterException(RM.getString("ERR_CREATING_DIRECTORY") + file.getPath()); //$NON-NLS-1$
				}
			}
			else
			{
				file.mkdir();
			}
			file = new File(location + File.separatorChar + "reports" + File.separatorChar + "css"); //$NON-NLS-1$ //$NON-NLS-2$
			if (file.exists())
			{
				if (!file.isDirectory())
				{
					throw new ExporterException(RM.getString("ERR_CREATING_DIRECTORY") + file.getPath()); //$NON-NLS-1$
				}
			}
			else
			{
				file.mkdir();
			}
			file = new File(location + File.separatorChar + "reports" + File.separatorChar + "js"); //$NON-NLS-1$ //$NON-NLS-2$
			if (file.exists())
			{
				if (!file.isDirectory())
				{
					throw new ExporterException(RM.getString("ERR_CREATING_DIRECTORY") + file.getPath()); //$NON-NLS-1$
				}
			}
			else
			{
				file.mkdir();
			}
		}
		this.exportLocation = location;
	}

	private void copyFiles(final ArrayList alImages, final ArrayList alJsFiles, final ArrayList alCssFiles,
			final String location) throws ExporterException
	{
		// Copy images
		final StringBuffer sBuffer = new StringBuffer(PathFinder.getDevSuiteHome());
		// sBuffer.append(File.separatorChar);
		sBuffer.append("images"); //$NON-NLS-1$
		sBuffer.append(File.separatorChar);
		String subPath = sBuffer.toString();

		final StringBuffer sBuffer1 = new StringBuffer(location);
		sBuffer1.append(File.separatorChar);
		sBuffer1.append("images"); //$NON-NLS-1$
		sBuffer1.append(File.separatorChar);
		String subPath1 = sBuffer1.toString();
		for (int i = 0; i < alImages.size(); i++)
		{
			sBuffer1.setLength(0);
			sBuffer1.append(subPath1);
			sBuffer1.append(alImages.get(i));
			sBuffer.setLength(0);
			sBuffer.append(subPath);
			sBuffer.append(alImages.get(i));
			try
			{
				final File oldFile = new File(sBuffer.toString());
				if (oldFile.exists())
				{
					this.copyFile(sBuffer.toString(), sBuffer1.toString());
				}
				else
				{
					System.err.println("source file does not exist: " + sBuffer.toString());
				}
			}
			catch (final IOException exception)
			{
				throw new ExporterException(
						RM.getString(RM.getString("VALUE_ERR_CREATING_FILE")) + sBuffer.toString() + " new path: " + sBuffer1.toString(), exception); //$NON-NLS-1$
			}
		}

		// Copy js files
		sBuffer.setLength(0);
		sBuffer.append(PathFinder.getDevSuiteHome());
		// sBuffer.append(File.separatorChar);
		sBuffer.append("reports"); //$NON-NLS-1$
		sBuffer.append(File.separatorChar);
		sBuffer.append("js"); //$NON-NLS-1$
		sBuffer.append(File.separatorChar);
		subPath = sBuffer.toString();

		sBuffer1.setLength(0);
		sBuffer1.append(location);
		sBuffer1.append(File.separatorChar);
		sBuffer1.append("js"); //$NON-NLS-1$
		sBuffer1.append(File.separatorChar);
		subPath1 = sBuffer1.toString();
		for (int i = 0; i < alJsFiles.size(); i++)
		{
			sBuffer1.setLength(0);
			sBuffer1.append(subPath1);
			sBuffer1.append(alJsFiles.get(i));
			sBuffer.setLength(0);
			sBuffer.append(subPath);
			sBuffer.append(alJsFiles.get(i));
			try
			{
				this.copyFile(sBuffer.toString(), sBuffer1.toString());
			}
			catch (final IOException exception)
			{
				throw new ExporterException(RM.getString("ERR_COPYING_FILE") + sBuffer.toString(), exception); //$NON-NLS-1$
			}
		}

		// Copy css files
		sBuffer.setLength(0);
		sBuffer.append(PathFinder.getDevSuiteHome());
		// sBuffer.append(File.separatorChar);
		sBuffer.append("reports"); //$NON-NLS-1$
		sBuffer.append(File.separatorChar);
		sBuffer.append("css"); //$NON-NLS-1$
		sBuffer.append(File.separatorChar);
		subPath = sBuffer.toString();

		sBuffer1.setLength(0);
		sBuffer1.append(location);
		sBuffer1.append(File.separatorChar);
		sBuffer1.append("css"); //$NON-NLS-1$
		sBuffer1.append(File.separatorChar);
		subPath1 = sBuffer1.toString();
		for (int i = 0; i < alCssFiles.size(); i++)
		{
			sBuffer1.setLength(0);
			sBuffer1.append(subPath1);
			sBuffer1.append(alCssFiles.get(i));
			sBuffer.setLength(0);
			sBuffer.append(subPath);
			sBuffer.append(alCssFiles.get(i));
			try
			{
				this.copyFile(sBuffer.toString(), sBuffer1.toString());
			}
			catch (final IOException exception)
			{
				throw new ExporterException(RM.getString("ERR_COPYING_FILE") + sBuffer.toString(), exception); //$NON-NLS-1$
			}
		}
	}

	private void copyFile(final String oldFile, final String newFile) throws IOException
	{
		final File file = new File(newFile);
		if (!file.exists())
		{
			file.createNewFile();
		}
		final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(oldFile)); // $IGN_Close_streams$
		final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)); // $IGN_Close_streams$
		int bytesRead = 0;
		final byte[] b = new byte[512];
		while (bytesRead >= 0)
		{
			bytesRead = bis.read(b);
			if (bytesRead > 0)
			{
				bos.write(b, 0, bytesRead);
			}
		}
		bis.close();
		bos.flush();
		bos.close();
	}

	public void setCreateNewInstances(final boolean createNewInstances)
	{
		this.createNewInstances = createNewInstances;
	}
	
}
