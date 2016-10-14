package com.queryio.common.service.windows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;

public class WindowsServiceWrapper {

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		Properties props = System.getProperties();
		props.list(System.out);
		System.out.println("WindowsServiceWrapper called with arguments: "
				+ Arrays.toString(args));
		if (SystemUtils.IS_OS_WINDOWS && args.length > 0) {
			if (args[0].equals("start"))
				start(args);
			else
				stop(args);
		} else {
			start(args);
		}
	}

	private static void stop(String[] args) {
		System.exit(0);
	}

	private static void start(String[] args) throws Throwable {
		try {

			final Class<?> cls = Class.forName(args[1]);
			final Method meth = cls.getMethod("main", String[].class);
			// Skip first and second args that is action, class name.
			final String[] params = Arrays.copyOfRange(args, 3, args.length);
			// static method doesn't have an instance
			meth.invoke(null, (Object) params);
		} catch (final Throwable e) {
			System.err.println("Error in WindowsServiceWrapper "
					+ e.getLocalizedMessage());
			e.printStackTrace(System.err);
			throw e;
		}
	}
}