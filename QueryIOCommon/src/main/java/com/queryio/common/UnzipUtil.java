package com.queryio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtil {
	// Expands the zip file passed as argument 1, into the
	// directory provided in argument 2
	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("USAGE: UnzipUtil sourceZipPath OutputDir");
			return;
		}
		byte[] buffer = new byte[2048];
		ZipInputStream zis = null;
		try {
			String zipFilePath = args[0];
			String destinationPath = args[1];
			// String zipFilePath = "/AppPerfect/Testing/bin.zip";
			// String destinationPath = "/AppPerfect/Testing";

			zis = new ZipInputStream(new FileInputStream(zipFilePath));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {

				File entryFile = new File(destinationPath, entry.getName());
				if (entry.isDirectory()) {

					if (!entryFile.exists()) {
						entryFile.mkdirs();
					}

				} else {

					// Make sure all folders exists (they should, but the safer,
					// the better ;-))
					if (entryFile.getParentFile() != null && !entryFile.getParentFile().exists()) {
						entryFile.getParentFile().mkdirs();
					}

					if (!entryFile.exists()) {
						entryFile.createNewFile();
					}
					OutputStream os = null;
					try {
						os = new FileOutputStream(entryFile);
						int len = 0;
						while ((len = zis.read(buffer)) > 0) {
							os.write(buffer, 0, len);
						}
					} finally {
						if (os != null) {
							os.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}