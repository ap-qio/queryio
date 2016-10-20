package com.queryio.core.monitor.events;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.Queue;
import com.queryio.common.util.QueueManager;
import com.queryio.core.monitor.alerts.generator.AlertGenerator;

public class AlertListenerImpl implements AlertListener
{
	private static final String QUEUE_ONE = GENERATOR_QUEUE_NAME + "One";

	public AlertListenerImpl()
	{
		
	}

	public void alertReset(final AlertEvent event)
	{
		handleAlertEvent(event, false);
	}

	public void alertRaised(final AlertEvent event)
	{
		handleAlertEvent(event, true);
	}
	
	private void handleAlertEvent(final AlertEvent event, final boolean raised)
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Handling Alert " + (raised? "Raised": "Reset") + " Event");
		
		AlertGenerator generator = null;

		try
		{
			generator = new AlertGenerator(raised, event.getNodeId(), event.getTimeStamp(), event.getRules());

			String queueName = QUEUE_ONE;
			Queue queueOne = QueueManager.getQueue(queueName);
			if (!queueOne.isRunning())
			{
				queueOne = QueueManager.startQueue(queueName);
				/* add it to the queue */
				queueOne.addItem(generator);
			}
			else if (queueOne.getSize() == 0)
			{
				/* add it to the queue */
				queueOne.addItem(generator);
			}
		}
		catch (final Exception ex)
		{
			final String msg = "Error adding AlertGenerator to the queue for controller: " + event.getNodeId();
			AppLogger.getLogger().fatal(msg, ex);
		}
	}
}