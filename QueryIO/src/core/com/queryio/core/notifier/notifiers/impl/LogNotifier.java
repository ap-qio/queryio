package com.queryio.core.notifier.notifiers.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.notifier.common.INotifierConstants;
import com.queryio.core.notifier.common.NotificationEvent;
import com.queryio.core.notifier.dstruct.PropertySet;
import com.queryio.core.notifier.notifiers.INotifier;

public class LogNotifier implements INotifier {
	private static Object mutex = new Object();

	private String logFileName = null;

	public void initPropertySet(PropertySet propSet) throws Exception {
		List llValues = propSet.getProperty(INotifierConstants.LOGFILE_NAME);
		if (llValues != null) {
			this.logFileName = ((String) llValues.get(0));
			if (!new File(this.logFileName).getParentFile().exists()) {
				throw new RuntimeException("Location where this file needs to be created does not exist"); //$NON-NLS-1$
			}
		} else {
			throw new RuntimeException("Log file path not specified"); //$NON-NLS-1$
		}
	}

	public String notifyEvent(NotificationEvent event) throws Exception {
		final String message = (String) event.getProperty(INotifierConstants.ALERT_MESSAGE);

		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		buffer.append(new Date());
		buffer.append("] ");
		buffer.append(StaticUtilities.replaceAll(message, PlatformHandler.LINE_SEPARATOR, " "));

		synchronized (mutex) {
			// Write to the log file here.
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true));
			writer.write(buffer.toString());
			writer.newLine();
			writer.flush();
			writer.close();
		}

		return null;
	}

}
