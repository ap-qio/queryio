package com.queryio.job.definition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.queryio.common.util.AppLogger;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.dstruct.IDataDefinition;

public class JobMappingExtractor {
	String jarFilePath;
	String tableName;
	ArrayList<ColumnMetadata> columnMetaData;

	public JobMappingExtractor(String path) {
		this.jarFilePath = path;
	}

	public void parse() throws IOException {
		extractTableName();
	}

	private void extractTableName() throws IOException {
		List<String> list = new ArrayList<String>();

		File jarFile = new File(jarFilePath);

		ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
		ZipEntry ze = null;

		while ((ze = zip.getNextEntry()) != null) {
			String entryName = ze.getName();
			if (entryName.endsWith("class")) {
				entryName = entryName.replace('/', '.');
				entryName = entryName.replace('\\', '.');
				entryName = entryName.replace(".class", "");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("List entry: " + entryName);
				list.add(entryName);
			}
		}
		zip.close();

		URL fileURL = jarFile.toURI().toURL();
		String jarURL = "jar:" + fileURL + "!/";
		URL urls[] = { new URL(jarURL) };
		URLClassLoader ucl = new URLClassLoader(urls, this.getClass().getClassLoader());

		Class clazz;
		for (int i = 0; i < list.size(); i++) {
			try {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Loading class: " + list.get(i));
				// clazz = Class.forName(list.get(i), true, ucl);
				clazz = ucl.loadClass(list.get(i));
				if (clazz != null) {
					if (isImplementingInterfce(clazz, IDataDefinition.class.getName())) {
						try {
							Method method = clazz.getMethod("getColumnMetadata");
							this.columnMetaData = (ArrayList<ColumnMetadata>) method.invoke(clazz.newInstance());
							method = clazz.getMethod("getTableName");
							this.tableName = (String) method.invoke(clazz.newInstance());
						} catch (Throwable e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			} catch (Throwable e) {
				// AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private boolean isImplementingInterfce(Class clazz, String interfaceName) throws ClassNotFoundException {
		if (clazz != null) {
			Class[] interfaces = clazz.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i].getName().equals(interfaceName)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getJarFilePath() {
		return jarFilePath;
	}

	public String getTableName() {
		return tableName;
	}

	public List<ColumnMetadata> getColumnMetaData() {
		return columnMetaData;
	}
}
