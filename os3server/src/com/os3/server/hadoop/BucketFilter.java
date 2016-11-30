package com.os3.server.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.log4j.Logger;

import com.os3.server.data.manager.DataManager;

public class BucketFilter implements PathFilter {
	protected final Logger LOGGER = Logger.getLogger(getClass());

	private String prefix;
	private String delimiter;
	private int maxKeys;
	private String marker;

	private FileSystem dfs = null;

	private int count = 0;

	private boolean bMarkerReached = false;

	private boolean bTruncated = false;

	private Map<String, String> commonPrefixes;

	public BucketFilter(FileSystem dfs, String prefix, String delimiter, int maxKeys, String marker) {
		super();
		this.prefix = prefix;
		this.delimiter = delimiter;
		if (delimiter != null) {
			commonPrefixes = new HashMap<String, String>();
		}
		this.maxKeys = maxKeys;
		this.marker = marker;

		this.dfs = dfs;
	}

	public boolean accept(Path path) {

		try {
			if (!DataManager.isFile(dfs, path)) {
				return false;
			}
		} catch (IOException ioe) {
			throw new Error(ioe);
		}

		String objectName = path.getName();

		LOGGER.debug("objectName: " + objectName);

		// If we have already reached maxKeys
		if (count >= maxKeys) {
			bTruncated = true;
			return false;
		}

		boolean accept = false;
		// If marker is defined, then accept only if we have reached marker
		if (marker != null) {
			if (bMarkerReached) {
				accept = true;
			} else if (objectName.startsWith(marker)) {
				bMarkerReached = true;
				accept = false; // skip the marker
			}
		} else {
			accept = true;
			bMarkerReached = true;
		}

		String commonPrefix = null;
		if (delimiter != null && (marker == null || bMarkerReached)) {
			if (prefix != null) {
				if (objectName.startsWith(prefix)) {
					int delimIndex = objectName.indexOf(delimiter, prefix.length());
					if (delimIndex != -1) {
						commonPrefix = objectName.substring(0, delimIndex + 1);
					}
				}
			} else {
				int delimIndex = objectName.indexOf(delimiter);
				if (delimIndex != -1) {
					commonPrefix = objectName.substring(0, delimIndex + 1);
				}
			}
			LOGGER.debug("commonPrefix: " + commonPrefix);
			if (commonPrefix != null) {
				if (commonPrefixes.get(commonPrefix) == null) {
					commonPrefixes.put(commonPrefix, commonPrefix);
				}
				count++;
				return false; // this entry will go to common prefix and not to
								// contents so return false.
			}
		}

		// If prefix is defined then accept only if path starts with prefix
		if (prefix != null) {
			if (delimiter != null) {
				if (commonPrefix != null) {
					if (commonPrefix.startsWith(prefix)) {
						return false;
					}
				}
			} else {
				if (!objectName.startsWith(prefix)) {
					return false;
				}
			}
		}

		if (accept) {
			count++;
			return true;
		}
		return false;
	}

	public boolean isTruncated() {
		return bTruncated;
	}

	public Map<String, String> getCommonPrefixes() {
		return commonPrefixes;
	}
}
