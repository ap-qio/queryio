package com.queryio.common.database;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class NSMetadataReader {
	private String file;
	FileInputStream fis;
	BufferedInputStream bis;
	ObjectInputStream ois;

	public NSMetadataReader(String nsMetadatFilePath) {
		this.file = nsMetadatFilePath;
	}

	public void start() throws IOException {
		fis = new FileInputStream(file);
		bis = new BufferedInputStream(fis);

		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("ns-metadata", NSMetadata.class);

		ois = xstream.createObjectInputStream(bis);
	}

	public void close() throws IOException {
		if (ois != null) {
			ois.close();
		}
	}

	public NSMetadata next() throws IOException, ClassNotFoundException {
		try {
			return (NSMetadata) ois.readObject();
		} catch (EOFException ex) {
			return null;
		}
	}
}
