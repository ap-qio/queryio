package com.queryio.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible to manage queues which are used for processing
 * events added in the queue. This class can start a queue, stop any running
 * queue, add items to the queue. It has references of all the existing queues.
 * 
 * @author Exceed Consultancy Services
 */
public class QueueManager {
	private static Map queueMap = null;

	static {
		queueMap = new HashMap();
	}

	/**
	 * Method get.
	 * 
	 * @param name
	 * @return Queue
	 */
	private static Queue get(final String name) {
		return (Queue) queueMap.get(name);
	}

	/**
	 * Method getQueue.
	 * 
	 * @param name
	 * @return Queue
	 */
	public static Queue getQueue(final String name) {
		Queue queue = get(name);
		if (queue == null) {
			queue = new Queue(name);
			queueMap.put(name, queue);
		}
		return queue;
	}

	/**
	 * Method startQueue.
	 * 
	 * @param name
	 * @return Queue
	 */
	public static Queue startQueue(final String name) {
		final Queue queue = getQueue(name);
		if (!queue.isRunning()) {
			final Thread t = new Thread(queue, name);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}
		return queue;
	}

	/**
	 * Method stopQueue.
	 * 
	 * @param name
	 */
	public static void stopQueue(final String name) {
		final Queue queue = get(name);
		if (queue != null) {
			queue.stop();
		}
	}

	/**
	 * Method removeQueue.
	 * 
	 * @param name
	 */
	public static void removeQueue(final String name) {
		final Queue queue = (Queue) queueMap.remove(name);
		if ((queue != null) && queue.isRunning()) {
			queue.stop();
		}
	}

	/**
	 * Method shutdown.
	 */
	public static void shutdown() {
		Queue queue = null;
		final Object array[] = queueMap.values().toArray();
		for (int i = 0; i < array.length; i++) {
			queue = (Queue) array[i];
			if ((queue != null) && queue.isRunning()) {
				queue.stop();
			}
		}
	}

	/**
	 * Method debug.
	 */
	public static void debug() {
		final Object array[] = queueMap.values().toArray();
		final StringBuffer sbuff = new StringBuffer("Queues present with manager are "); //$NON-NLS-1$
		sbuff.append(array.length);
		sbuff.append(" & queues are: ["); //$NON-NLS-1$
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sbuff.append(", "); //$NON-NLS-1$
			}
			sbuff.append(array[i].toString());
		}
		sbuff.append(']');
		System.out.println(sbuff);
	}

}
