package com.queryio.core.notifier.notifiers.impl;

import java.io.File;
import java.util.List;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.common.util.ServiceUtilities;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.INotifier;

public class CustomProgramNotifier implements INotifier 
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$
	private String customProgram = null;

	public void initPropertySet(PropertySet propSet) throws Exception 
	{
		List llValues = propSet.getProperty(INotifierConstants.CUSTOM_PROGRAM);
		if (llValues != null)
		{
			this.customProgram = ((String) llValues.get(0));
			if (!new File(this.customProgram).exists())
			{
				throw new RuntimeException(RM.getString("VALUE_CUSTOM_PROGRAM_EXIST_MSG")); //$NON-NLS-1$
			}
		}
		else
		{
			throw new RuntimeException(RM.getString("VALUE_NO_CUSTOM_PROGRAM_MSG")); //$NON-NLS-1$
		}

	}

	public String notifyEvent(NotificationEvent event) throws Exception 
	{
		String message = (String) event.getProperty(INotifierConstants.ALERT_MESSAGE);
		String [] command;
		if (message != null)
		{
			command = new String [] {customProgram, message};
		}
		else
		{
			command = new String [] {customProgram};
		}
		Process process = Runtime.getRuntime().exec(command);
		
		ServiceUtilities.readDataFromProcess(process, null, null);
		
		return null;
	}

}
