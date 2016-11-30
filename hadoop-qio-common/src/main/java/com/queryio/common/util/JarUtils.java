package com.queryio.common.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class can be used to search a folder and its children to get all the
 * jars within. Also the excludedResources folders are skipped
 */
public class JarUtils {
	private JarUtils() {
	}

	public static String[] getAllJars(final String serverHomePath, final List excludedResources) {
		File rootFile = null;
		if (serverHomePath != null) {
			rootFile = new File(serverHomePath);
		}
		final Collection results = new ArrayList();
		if ((rootFile != null) && rootFile.exists()) {
			final List excludedResourcesAbs = new ArrayList();
			File tempFile = null;
			if (excludedResources != null) {
				final Iterator excludedResourcesIterator = excludedResources.iterator();
				while (excludedResourcesIterator.hasNext()) {
					final String relativePath = (String) excludedResourcesIterator.next();
					tempFile = new File(rootFile.getAbsolutePath() + File.separatorChar + relativePath);
					if (tempFile.exists()) {
						excludedResourcesAbs.add(tempFile);
					}
				}
			}
			final JarFileFilter filter = new JarFileFilter(excludedResourcesAbs);
			getAllJarsHelper(rootFile, results, filter);
		}
		final String[] jarNames = new String[results.size()];
		results.toArray(jarNames);
		return jarNames;
	}

	private static void getAllJarsHelper(final File rootFile, final Collection results, final FileFilter fileFilter) {
		if (rootFile.isDirectory()) {
			final File[] contents = rootFile.listFiles(fileFilter);
			if (contents == null) {
				return;
			}
			for (int i = 0; i < contents.length; i++) {
				getAllJarsHelper(contents[i], results, fileFilter);
			}
		} else {
			results.add(rootFile.getPath());
		}
	}

	private static class JarFileFilter implements FileFilter {
		final List excludedResourcesAbs;

		JarFileFilter(final List excludedResourcesAbs) {
			this.excludedResourcesAbs = excludedResourcesAbs;
		}

		public boolean accept(final File pathname) {
			final File parentFile = pathname.getParentFile();
			File excludedResource = null;
			final Iterator excludedResourcesAbsIterator = this.excludedResourcesAbs.iterator();
			while (excludedResourcesAbsIterator.hasNext()) {
				excludedResource = (File) excludedResourcesAbsIterator.next();
				if (excludedResource.compareTo(parentFile) == 0) {
					return false;
				}
			}
			return (pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".jar"));
		}

	}

}
