/*
 * @(#) ResourceManager.java 1.0 02/10/2002 (DD/MM/YYYY)
 *
 * Copyright (C) 2002- 2003 Exceed Consultancy Services. All rights reserved.
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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager
{
	protected static final Locale locale = Locale.getDefault();

	protected static final String sLanguage = locale.getLanguage();

	protected static final String sCountry = locale.getCountry();

	protected static final String sVariant = locale.getVariant();

	private ResourceBundle RESOURCE_BUNDLE = null;

	protected ResourceManager(final ResourceBundle resourceBundle)
	{
		this.RESOURCE_BUNDLE = resourceBundle;
	}

	/*
	 * public static ResourceManager loadResources(String resource) { return new
	 * ResourceManager(ResourceManager.getBundle(resource)); }
	 */

	protected static ResourceBundle getBundle(String resource)
	{
		int n = (sLanguage.length() > 0) ? 2 : 1;
		n += (sCountry.length() > 0) ? 1 : 0;
		n += (sVariant.length() > 0) ? 1 : 0;

		final String[] resourceNames = new String[n];
		final String[] names = { resource, "_" + sLanguage, "_" + sCountry, "__" + sVariant }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		resourceNames[0] = names[0];
		for (int i = 1; i < n; i++)
		{
			resourceNames[i] = resourceNames[i - 1] + names[i];
		}
		boolean bFound = false;
		ResourceBundle temp_bundle = null;
		for (int i = n - 1; i >= 0; i--)
		{
			resource = resourceNames[i];
			try
			{
				// We need to pass emty locale here else it searches by
				// providing the default locale, but in our defautl case we are
				// not providing the files with locales appended.
				if (i == 0)
				{
					temp_bundle = ResourceBundle.getBundle(resource, new Locale("",
							"", ""));
				}
				else
				{
					// This call internally adds Locale.getDefault()
					temp_bundle = ResourceBundle.getBundle(resource);
				}
				if ((temp_bundle != null) && temp_bundle.getKeys().hasMoreElements())
				{
					bFound = true;
					break;
				}
			}
			catch (final MissingResourceException e)
			{
				// Ignore this....we are just trying to find the appropriate
				// resoruce.
			}
		}

		if (bFound)
		{
			return temp_bundle;
		}
		System.out.println("Cannot proceed without loading resources.\nTerminating...\n" + resource); //$NON-NLS-1$
		System.exit(0); // $IGN_Use_System_dot_exit_with_care$
		return null;
	}

	public String getString(final String key)
	{
		try
		{
			return this.RESOURCE_BUNDLE.getString(key);
		}
		catch (final MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
