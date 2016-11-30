package com.queryio.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	/**
	 * A constants for buffer size used to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Compresses a collection of files to a destination zip file
	 * 
	 * @param listFiles
	 *            A collection of files and directories
	 * @param destZipFile
	 *            The path of the destination zip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public static void compressFiles(ArrayList listFiles, String destZipFile)
			throws FileNotFoundException, IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));

		File file = null;
		Iterator iterator = listFiles.iterator();
		while (iterator.hasNext()) {
			file = (File) iterator.next();
			if (file.isDirectory()) {
				addFolderToZip(file, file.getName(), zos);
			} else {
				addFileToZip(file, zos);
			}
		}
		zos.flush();
		zos.close();
	}

	/**
	 * Adds a directory to the current zip output stream
	 * 
	 * @param folder
	 *            the directory to be added
	 * @param parentFolder
	 *            the path of parent directory
	 * @param zos
	 *            the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		// if (folder.listFiles().length > 0)
		// {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}

			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			// BufferedInputStreamExt bis = new BufferedInputStreamExt(new
			// FileInputStream(file));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

			// long bytesRead = 0;
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;

			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				// bytesRead += read;
			}

			zos.closeEntry();
			bis.close();
		}
		// }
		// else
		// {
		// if (folder.exists())
		// {
		// zos.putNextEntry(new ZipEntry(parentFolder + "/" +
		// folder.getName()));
		//// BufferedInputStreamExt bis = new BufferedInputStreamExt(new
		// FileInputStream(file));
		// BufferedInputStream bis = new BufferedInputStream(new
		// FileInputStream(folder));
		//
		// //long bytesRead = 0;
		// byte[] bytesIn = new byte[BUFFER_SIZE];
		// int read = 0;
		//
		// while ((read = bis.read(bytesIn)) != -1)
		// {
		// zos.write(bytesIn, 0, read);
		// //bytesRead += read;
		// }
		//
		// zos.closeEntry();
		// bis.close();
		// }
		// }
	}

	/**
	 * Adds a file to the current zip output stream
	 * 
	 * @param file
	 *            the file to be added
	 * @param zos
	 *            the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void addFileToZip(File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		zos.putNextEntry(new ZipEntry(file.getName()));
		// BufferedInputStreamExt bis = new BufferedInputStreamExt(new
		// FileInputStream(file));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

		// long bytesRead = 0;
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;

		while ((read = bis.read(bytesIn)) != -1) {
			zos.write(bytesIn, 0, read);
			// bytesRead += read;
		}

		zos.closeEntry();
		bis.close();
	}

	public static void main(String[] args) throws Exception {
		// test compress files
		File directoryToZip = new File("/Users/indravardhan/Desktop/1");
		String zipFilePath = "/Users/indravardhan/Desktop/1.zip";

		ArrayList listFiles = new ArrayList();
		listFiles.add(directoryToZip);

		compressFiles(listFiles, zipFilePath);
	}
}
