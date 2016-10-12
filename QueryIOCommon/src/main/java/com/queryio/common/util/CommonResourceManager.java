package com.queryio.common.util;

import java.util.ResourceBundle;

public class CommonResourceManager extends ResourceManager
{
	private CommonResourceManager(final ResourceBundle resourceBundle)
	{
		super(resourceBundle);
	}

	public static ResourceManager loadResources(final String resource)
	{
		return new CommonResourceManager(ResourceManager.getBundle(resource));
	}

}
