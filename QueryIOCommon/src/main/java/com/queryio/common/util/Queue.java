/*
 * @(#)  Queue.java
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
package com.queryio.common.util;

import java.util.LinkedList;

import com.queryio.common.util.AppLogger;


/**
 * It is an implementation of a Queue. If there are items in the queue, then the
 * items are processed else the queue waits for items to join which can be
 * processed.
 * 
 * @author Exceed Consultancy Services
 */
public class Queue extends LinkedList implements Runnable
{
	private static final long serialVersionUID = -8129581485102282990L;

	private String name;
	public boolean debug = false;

	private boolean bRunning;
	private boolean bContinue;
	private boolean bWaiting;

	/**
	 * Method Queue.
	 * 
	 * @param name
	 */
	Queue(final String name)
	{
		this.name = name;
		this.bRunning = false;
		this.bWaiting = false;
		this.bContinue = true;
	}

	/**
	 * Method stop.
	 */
	void stop()
	{
		this.bContinue = false;
		this.wakeup();
	}

	/**
	 * Method addItem.
	 * 
	 * @param item
	 */
	public void addItem(final QueueItem item)
	{
		synchronized (this)
		{
			final boolean added = add(item);
			if (debug && item != null)
			{
				AppLogger.getLogger().info("QueueItem("+ item.hashCode() + ") added: " + added);
			}
			this.notifyAll();
		}
	}

	/**
	 * Method getFirstItem.
	 * 
	 * @return QueueItem
	 */
	private final QueueItem getFirstItem()
	{
		synchronized (this)
		{
			if (size() == 0)
			{
				return null;
			}
			return (QueueItem)removeFirst();
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		this.bRunning = true;
		this.bContinue = true;
		QueueItem item = null;
		while (this.bContinue)
		{
			try
			{
				item = this.getFirstItem();
				while (this.bContinue && (item != null))
				{
					if (debug)
					{
						AppLogger.getLogger().info("QueueItem("+ item.hashCode() + ") served");
					}
					item.serve();
					item = this.getFirstItem();
				}
				// confirm stop has not been called before going into wait
				if (this.bContinue)
				{
					synchronized (this)
					{
						this.bWaiting = true;
						this.wait(15000);
						this.bWaiting = false;
					}
				}
			}
			catch (InterruptedException ie)
			{
				// do not log this exception
				AppLogger.getLogger().info("Queue interrupted name: " + name + ", exception message: " + ie.getMessage());
			}
			catch (final Exception e)
			{
				// do not log this exception
				AppLogger.getLogger().fatal("Error while serving a queue: " + name, e);
			}
		}
		this.bRunning = false;
	}

	/**
	 * Method wakeup.
	 */
	public final void wakeup()
	{
		if (debug)
		{
			AppLogger.getLogger().info("Queue: " + name + " wakeup called explicitly");
		}
		synchronized (this)
		{
			if (this.bWaiting)
			{
				this.notifyAll();
			}
		}
	}

	/**
	 * Method isRunning.
	 * 
	 * @return boolean
	 */
	public final boolean isRunning()
	{
		return this.bRunning;
	}

	/**
	 * current size of the queue
	 * 
	 * @return int current size of the queue
	 */
	public final int getSize()
	{
		return size();
	}

	/**
	 * Returns the name.
	 * 
	 * @return String
	 */
	public final String getName()
	{
		return this.name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            The name to set
	 */
	public final void setName(final String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer(this.name);
		sbToString.append(" run: "); //$NON-NLS-1$
		sbToString.append(this.bRunning);
		sbToString.append(" waiting: "); //$NON-NLS-1$
		sbToString.append(this.bWaiting);

		return sbToString.toString();
	}

}