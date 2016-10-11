package com.os3.server.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomizableThreadFactory implements ThreadFactory {

	private boolean daemon;

	private String threadNamePrefix = "Thread-";

	private ThreadGroup threadGroup;

	private final AtomicInteger threadNumber = new AtomicInteger(1);

	public CustomizableThreadFactory(boolean daemon, String threadNamePrefix) {
		this.daemon = daemon;
		setThreadNamePrefix(threadNamePrefix);
	}

	public Thread newThread(Runnable runnable) {
		Thread thread = threadGroup != null ? new Thread(threadGroup, runnable, threadNamePrefix + threadNumber.getAndIncrement()) : new Thread(runnable, threadNamePrefix
				+ threadNumber.getAndIncrement());
		thread.setDaemon(daemon);
		return thread;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public String getThreadNamePrefix() {
		return threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix == null ? "Thread-" : threadNamePrefix;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

}
