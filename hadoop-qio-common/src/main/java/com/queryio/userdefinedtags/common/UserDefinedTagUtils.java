package com.queryio.userdefinedtags.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.Block;

import com.queryio.common.database.DBTypeProperties;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;

public class UserDefinedTagUtils {

	public static final int MAX_COL_SIZE = 512;

	public static List<UserDefinedTag> generateDefaultTags(FileSystem fs, String path) throws IOException {
		FileStatus status = fs.getFileStatus(new Path(path));
		return generateDefaultTags(status);
	}

	public static List<UserDefinedTag> generateDefaultTags(FileStatus status) throws IOException {
		List<UserDefinedTag> tags = new ArrayList<UserDefinedTag>();

		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_ACCESSTIME, new Timestamp(status.getAccessTime())));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_MODIFICATIONTIME,
				new Timestamp(status.getModificationTime())));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_OWNER, status.getOwner()));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_GROUP, status.getGroup()));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_PERMISSION, status.getPermission().toString()));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_BLOCKSIZE, status.getBlockSize()));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_REPLICATION, status.getReplication()));
		tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_LENGTH, status.getLen()));
		// tags.add(new UserDefinedTag(TableMetadata.DEFAULT_TAG_BLOCKS,
		// convert(status.getBlocks())));
		return tags;
	}

	public static byte[] convert(Block[] blocks) throws IOException {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			if (blocks == null || blocks.length == 0) {
				oos.writeInt(0);
			} else {
				oos.writeInt(blocks.length);
				for (Block b : blocks) {
					b.write(oos);
				}
			}
			oos.flush();
			return baos.toByteArray();
		} finally {
			if (baos != null)
				baos.close();
		}
	}

	public static Block[] convert(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = null;
		Block[] blocks = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			int count = ois.readInt();
			blocks = new Block[count];
			for (int i = 0; i < count; i++) {
				blocks[i] = new Block();
				blocks[i].readFields(ois);
			}
			return blocks;
		} finally {
			if (bais != null)
				bais.close();
		}
	}

	public static String getDirectory(String filePath) {
		String path = new Path(filePath).toUri().getPath();
		File relative = new File(path);

		return relative.getParent();
	}

	public static String getFileName(String filePath) {
		String path = new Path(filePath).toUri().getPath();
		File relative = new File(path);

		return relative.getName();
	}

	/*
	 * SQL data type Java data type
	 * 
	 * BIT boolean TINYINT byte SMALLINT short INTEGER int BIGINT long REAL
	 * float FLOAT, DOUBLE double DATE java.sql.Date TIME java.sql.Time
	 * TIMESTAMP java.sql.Timestamp VARCHAR String BLOB java.sql.Blob
	 */
	public static String getDataType(Class c, DBTypeProperties props) {
		String result = props.getTypeMap().get(c);

		if (result == null)
			return (props.getTypeMap().get(String.class) + "(" + MAX_COL_SIZE + ")");
		if (c.equals(String.class))
			return (result + "(" + MAX_COL_SIZE + ")");

		return result;
	}

	public static String getFileExtension(String filePath) {
		char extensionSeparator = '.';
		int dot = filePath.lastIndexOf(extensionSeparator);
		return filePath.substring(dot + 1).toLowerCase();
	}
}
