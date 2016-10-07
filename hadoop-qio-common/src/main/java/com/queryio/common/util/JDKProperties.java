/*
 * @(#)  JDKProperties.java Sep 13, 2004
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
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class JDKProperties
{
	public static final String VENDOR = "java.vendor"; //$NON-NLS-1$
	public static final String VERSION = "java.specification.version"; //$NON-NLS-1$
	public static final String ARCH_DATA_MODEL = "sun.arch.data.model"; //$NON-NLS-1$

	public static final String VALUE_NOT_FOUND = "Error"; //$NON-NLS-1$

	private final String[] names;
	private Properties values;
	private final String jdkPath;
	private String classPath;

	public JDKProperties(final String jdkPath)
	{
		this(jdkPath, new String[] { VENDOR, VERSION, ARCH_DATA_MODEL });
	}

	public JDKProperties(final String jdkPath, final String[] properties)
	{
		this.jdkPath = jdkPath;
		this.names = properties;
	}

	public void setClassPath(final String classPath)
	{
		this.classPath = classPath;
	}

	public void findValues()
	{
		this.values = new Properties();

		if ((this.names != null) && (this.names.length > 0))
		{
			final String classpath = this.classPath != null ? this.classPath : System.getProperty(
					"appperfect.class.path", System.getProperty("java.class.path"));
			final String[] command = new String[this.names.length + 4];

			int position = 0;
			command[position++] = PlatformHandler.getJavaExecutable(this.jdkPath.trim());
			command[position++] = "-classpath"; //$NON-NLS-1$
			command[position++] = classpath;
			command[position++] = "com.appperfect.util.JDKProperties"; //$NON-NLS-1$

			System.arraycopy(this.names, 0, command, position, this.names.length);

			final Runtime runtime = Runtime.getRuntime();
			Process process = null;
			try
			{
				process = runtime.exec(command);
				process.waitFor();
			}
			catch (final Exception ex)
			{
				// do nothing
			}

			if (process != null)
			{
				final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				int ctr = 0;
				try
				{
					line = reader.readLine();
					while (line != null)
					{
						line = line.trim();
						if ((line.length() > 0) && (ctr < this.names.length))
						{
							this.values.setProperty(this.names[ctr], line);
							ctr++;
						}
						line = reader.readLine();
					}
				}
				catch (final Exception ex)
				{
					// do nothing
				}
			}
		}
	}

	public static void main(final String[] args)
	{
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				System.out.println(System.getProperty(args[i]));
			}
		}
	}

	public String getPropertyValue(final String name)
	{
		return this.values.getProperty(name, VALUE_NOT_FOUND);
	}

	public boolean is64Bit()
	{
		return this.getPropertyValue(ARCH_DATA_MODEL).equals("64");//$NON-NLS-1$
	}

}
