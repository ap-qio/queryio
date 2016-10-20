/*
 * @(#) BaseSystemInfo.java     1.0     27/11/2002 (DD/MM/YYYY)
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All rights reserved
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.exporter.dstruct;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.queryio.common.IProductConstants;
import com.queryio.common.io.SystemDetailWriter;
import com.queryio.common.util.StaticUtilities;

/**
 * This class is used to store the system settings.
 * 
 * @author Exceed Consultancy Services.
 * @version 1.0
 */
public abstract class BaseSystemInfo
{
	public static final transient String SYSTEMFILE = "System.xml"; //$NON-NLS-1$

	protected boolean bHighlightRowOnMouseOver = false;

	protected HashMap mapJvmList;
	
	protected boolean bUsingSystemFont = true;
	protected String sFontName = "";
	protected int iFontHeight;
	protected int iFontStyle;

	protected int iViewRefreshRate; // Tables and Graph Views' refresh rate in seconds, has default value 1

	private boolean bModified = false;

	/**
	 * 
	 */
	private final void initializeBaseValues()
	{
		this.initValues();
	}

	/**
	 * This API will set the intial values, to the required elements.
	 */
	private void initValues()
	{
		this.mapJvmList = new HashMap();
		this.setViewRefreshRate(1);
	}

	/**
	 * Method saveSystemSettings.
	 * 
	 * @throws Exception
	 */
	public abstract void saveSystemSettings(String fileLocation) throws Exception;

	/**
	 * Method loadSystemSettings.
	 * 
	 * @throws Exception
	 */
	public abstract void loadSystemSettings(String fileLocation) throws Exception;

	/**
	 * method initializeValues
	 */
	public abstract void initializeValues();

	/**
	 * This method will only save the System Details to the file. This will not
	 * write product related information to the file.
	 */
	public final void saveOnlySystemSettings() throws Exception
	{
		final SystemDetailWriter systemDetailWriter = new SystemDetailWriter(this);
		systemDetailWriter.saveSystem();
	}

	/**
	 * 
	 * method initialize
	 * 
	 */
	public void initialize()
	{
		this.initializeBaseValues();
		this.initializeValues();
	}

	/**
	 * Method <code>isModified<code> returns a <code>boolean<code> value showing
	 * whether the project is modified or not.
	 * @return a <tt>boolean</tt> showing if the project is modified.
	 */
	public boolean isModified()
	{
		return this.bModified;
	}

	/**
	 * Method <code>setModified<code> sets the <code>boolean<code> value of the 
	 * field bModified. This is flag is used to indicate whether the project has
	 * been modified or not.
	 *  
	 * @param   modified   indicating whether the project has been modified or not.
	 */
	public final void setModified(final boolean modified)
	{
		this.bModified = modified;
	}

	/**
	 * @return
	 */
	public final boolean isHighlightRowOnMouseOver()
	{
		return this.bHighlightRowOnMouseOver;
	}

	/**
	 * @param highlightRowOnMouseOver
	 */
	public final void setHighlightRowOnMouseOver(final boolean highlightRowOnMouseOver)
	{
		if (this.bHighlightRowOnMouseOver != highlightRowOnMouseOver)
		{
			this.bHighlightRowOnMouseOver = highlightRowOnMouseOver;
			this.setModified(true);
		}
	}

	/**
	 * @param i
	 */
	public final void setViewRefreshRate(final int i)
	{
		if ((i > 0) && (this.iViewRefreshRate != i))
		{
			this.iViewRefreshRate = i;
			this.setModified(true);
		}
	}

	/**
	 * @return
	 */
	public final int getViewRefreshRate()
	{
		return this.iViewRefreshRate;
	}
	/**
	 * @return
	 */
	public final Set getJVMList()
	{
		return this.mapJvmList.keySet();
	}

	/**
	 * 
	 */
	public final void clearJVMList()
	{
		this.mapJvmList.clear();
	}
	/**
	 * @param mapList
	 */
	public final void setJVMList(final HashMap mapList)
	{
		if (!this.mapJvmList.equals(mapList))
		{
			this.mapJvmList = mapList;
			this.setModified(true);
		}
	}
	/**
	 * @deprecated BaseSystemInfo # addToJVMList
	 * @param name
	 * @param sHomeFolder
	 */
	public final void addToJVMList(final String name, final String homeFolder)
	{
		this.addToJVMList(name, new File(homeFolder).getAbsolutePath(), IProductConstants.EMPTY_STRING,
				IProductConstants.EMPTY_STRING, false);
	}
	/**
	 * @param name
	 * @param homeFolder
	 * @param vendor
	 * @param version
	 * @param bDef
	 */
	public final void addToJVMList(String name, String homeFolder, final String vendor, final String version,
			final boolean bDef)
	{
		if (name == null) // if name is null then generate new name
		{
			name = StaticUtilities.generateName(this.mapJvmList, "j2sdk"); //$NON-NLS-1$
		}
		homeFolder = new File(homeFolder).getAbsolutePath();
		final JdkInfo jdk = (JdkInfo) this.mapJvmList.get(name);
		if ((jdk == null) || !jdk.getPath().equals(homeFolder) || !jdk.getVendor().equals(vendor)
				|| !jdk.getVersion().equals(version) || (jdk.isDefault() != bDef))
		{
			this.setModified(true);
		}
		this.mapJvmList.put(name, new JdkInfo(name, homeFolder, vendor, version, bDef));
	}
	/**
	 * @param jdkName
	 * @return
	 */
	public final String getJVMPath(final String jdkName)
	{
		// some times jdkInfo is null
		final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(jdkName));
		if (jdkInfo != null)
		{
			return jdkInfo.getPath();
		}
		return null;
	}
	/**
	 * @param jdkPath
	 * @return
	 */
	public final boolean isJdkPathEntryExist(final String jdkPath)
	{
		final Iterator iter = this.mapJvmList.keySet().iterator();
		final File path = new File(jdkPath);
		while (iter.hasNext())
		{
			final JdkInfo jdkInfo = (JdkInfo) this.mapJvmList.get(iter.next());
			if (new File(jdkInfo.getPath()).getAbsolutePath().equals(path.getAbsolutePath()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param jdkName
	 * @return
	 */
	public final boolean isJVMDefault(final String jdkName)
	{
		final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(jdkName));
		if (jdkInfo != null)
		{
			return jdkInfo.isDefault();
		}
		return false;		
	}
	/**
	 * @param jdkName
	 * @return
	 */
	public final String getJVMVendor(final String jdkName)
	{
		final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(jdkName));
		if (jdkInfo != null)
		{
			return jdkInfo.getVendor();
		}
		return IProductConstants.EMPTY_STRING;
	}
	/**
	 * @param jdkName
	 * @return
	 */
	public final String getJVMVersion(final String jdkName)
	{
		final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(jdkName));
		if (jdkInfo != null)
		{
			return jdkInfo.getVersion();
		}
		return IProductConstants.EMPTY_STRING;
	}
	/**
	 * @return
	 */
	public final String getDefaultJVMName()
	{
		final Iterator iterator = this.mapJvmList.keySet().iterator();
		while (iterator.hasNext())
		{
			final String key = (String) iterator.next();
			final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(key));
			if(jdkInfo != null && jdkInfo.isDefault())
			{
				return key;
			}
		}
		return IProductConstants.EMPTY_STRING;
	}

	public final String getJDKPath()
	{
		return this.getJVMPath(this.getDefaultJVMName());
	}

	public final String getJDKVendor()
	{
		return this.getJVMVendor(this.getDefaultJVMName());
	}

	public final String getJDKVersion()
	{
		return this.getJVMVersion(this.getDefaultJVMName());
	}


	/**
	 * @param jvmName
	 */
	public final void setDefaultJVM(final String jvmName)
	{
		if (this.mapJvmList.get(jvmName) == null)
		{
			// JVM does not exists
			return;
		}
		final JdkInfo jdkInfo = (JdkInfo) (this.mapJvmList.get(jvmName));
		if (! jdkInfo.isDefault())
		{
			final Iterator iterator = this.mapJvmList.values().iterator();
			while (iterator.hasNext())
			{
				final JdkInfo rowInfo = (JdkInfo) iterator.next();
				if (rowInfo.isDefault())
				{
					jdkInfo.setDefault(false);
					break;
				}
			}
			jdkInfo.setDefault(true);
			this.setModified(true);
		}
	}

	/**
	 * method isUsingSystemFont
	 * 
	 * @return
	 */
	public boolean isUsingSystemFont()
	{
		return this.bUsingSystemFont;
	}

	/**
	 * method setUsingSystemFont
	 * 
	 * @param b
	 */
	public void setUsingSystemFont(final boolean b)
	{
		if (this.bUsingSystemFont != b)
		{
			this.bUsingSystemFont = b;
			this.setModified(true);
		}
	}

	/**
	 * method getCustomFontData
	 * 
	 * @return
	 */
	public String getCustomFontName()
	{
		return this.sFontName;
	}

	public int getCustomFontHeight()
	{
		return this.iFontHeight;
	}

	public int getCustomFontStyle()
	{
		return this.iFontStyle;
	}

	/**
	 * method setCustomFontData
	 * 
	 * @param datas
	 */
	public void setCustomFontData(final String name, final int height, final int style)
	{
		if (!StaticUtilities.equals(this.sFontName, name, false) || (this.iFontHeight != height)
				|| (this.iFontStyle != style))
		{
			this.sFontName = name;
			this.iFontHeight = height;
			this.iFontStyle = style;
			this.setModified(true);
		}
	}
}