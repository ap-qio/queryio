/**
 * 
 */
package com.queryio.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author queryio
 *
 */
public class BuildHelperUtil {

	/**
	 * @param args
	 */

	private static Map<String, List<String>> jarMap = new HashMap<>();
	private static String buildDirHome;
	private static long bytesSaved = 0;
	private static String installerLibsDir;
	private static String installerMvFilesScript;
	private static final String USER_INSTALL_DIR = "$USER_INSTALL_DIR";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			throw new RuntimeException("Usage: java " + BuildHelperUtil.class.getName() + " </path/to/build>");
		}
		String buildDirPath = args[0];
		buildDirHome = buildDirPath;
		installerLibsDir = buildDirPath + "/InstallerLibs";
		System.out.println("installerLibsDir: " + installerLibsDir);
		new File(installerLibsDir).mkdir();
		installerMvFilesScript = buildDirPath + "/bin/mvScript.sh";
		new File(new File(installerMvFilesScript).getParent()).mkdirs();
		File buildDir = new File(buildDirPath);
		System.out.println("buildDir: " + buildDir);
		FileUtils.write(new File(installerMvFilesScript), "USER_INSTALL_DIR=$1\n\n", true);
		traverse(buildDir);
		System.out.println("Total space saved (MB): " + (bytesSaved / (1024 * 1024)));
		for (Map.Entry<String, List<String>> entry : jarMap.entrySet()) {
			if (entry.getValue().size() > 1) {
				// System.out.println(entry.getKey() + "(" +
				// entry.getValue().size() + " times)" + " => " +
				// entry.getValue());
			}
		}
	}

	private static void traverse(File dir) throws FileNotFoundException, IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			boolean toConsider = fileName.endsWith(".jar") || fileName.endsWith(".zip");
			if (file.isFile() && toConsider) {
				String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
				String key = fileName + "(" + md5Hex + ")";
				List<String> locations = jarMap.get(key);
				if (locations == null) {
					jarMap.put(key, new ArrayList<String>());
					locations = jarMap.get(key);
				} else {
					bytesSaved += file.length();
				}
				System.out.println("Moving " + file.getAbsolutePath() + " to " + installerLibsDir + "/" + key);
				Files.move(Paths.get(file.getAbsolutePath()), Paths.get(installerLibsDir + "/" + key),
						StandardCopyOption.REPLACE_EXISTING);
				String command = "cp \"" + USER_INSTALL_DIR + "/InstallerLibs/" + key + "\" \"" + USER_INSTALL_DIR + "/"
						+ file.getAbsolutePath().replace(buildDirHome, "") + "\"\n";
				FileUtils.write(new File(installerMvFilesScript), command, true);
				locations.add(file.getParent().replace(buildDirHome, ""));

			} else if (file.isDirectory()) {
				traverse(file);
			}
		}
	}

}
