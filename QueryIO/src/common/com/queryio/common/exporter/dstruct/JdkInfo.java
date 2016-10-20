package com.queryio.common.exporter.dstruct;

public class JdkInfo
{
	private String sName;
	private String sPath;
	private String sVendor;
	private String sVersion;
	private boolean bDefault;

	/**
	 * 
	 * @param name
	 * @param path
	 * @param defaultJDK
	 */
	public JdkInfo(final String name, final String path, final String vendor, final String version, final boolean defaultJDK)
	{
		this.sName = name;
		this.sPath = path;
		this.sVendor = vendor;
		this.sVersion = version;
		this.bDefault = defaultJDK;
	}

	/**
	 * @return Returns the name.
	 */
	public final String getName()
	{
		return this.sName;
	}

	/**
	 * @param arrName
	 *            The name to set.
	 */
	public void setName(final String arrName)
	{
		this.sName = arrName;
	}

	/**
	 * @return Returns the default.
	 */
	public final boolean isDefault()
	{
		return this.bDefault;
	}

	/**
	 * @param arrDefault
	 *            The default to set.
	 */
	public void setDefault(final boolean arrDefault)
	{
		this.bDefault = arrDefault;
	}

	/**
	 * @return Returns the path.
	 */
	public final String getPath()
	{
		return this.sPath;
	}

	/**
	 * @param arrPath
	 *            The path to set.
	 */
	public void setPath(final String arrPath)
	{
		this.sPath = arrPath;
	}

	public final String getVendor()
	{
		return this.sVendor;
	}

	public void setVendor(final String vendor)
	{
		this.sVendor = vendor;
	}

	public final String getVersion()
	{
		return this.sVersion;
	}

	public void setVersion(final String version)
	{
		this.sVersion = version;
	}
}