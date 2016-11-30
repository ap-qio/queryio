/**
 * 
 */
package com.queryio.common.dstruct;

import java.util.ArrayList;

/**
 * @author manoj
 *
 */
public class LogInfo {
	private long lLineNum;
	private boolean bFound;
	private long lLastLineRead;
	private String sLine;
	private String searchString;
	private int matchCount;
	private ArrayList matchedItems = new ArrayList();

	public LogInfo() {
		this(0, false, 1, null, 0);
	}

	public LogInfo(final long lineNum, final boolean found, final long lastLineNum, final String line,
			final int matchCount) {
		this.lLineNum = lineNum;
		this.bFound = found;
		this.lLastLineRead = lastLineNum;
		this.sLine = line;
		this.matchCount = matchCount;
	}

	public long getLineNum() {
		return lLineNum;
	}

	public void setLineNum(long lineNum) {
		lLineNum = lineNum;
	}

	public boolean isFound() {
		return bFound;
	}

	public void setFound(boolean found) {
		bFound = found;
	}

	public long getLastLineRead() {
		return lLastLineRead;
	}

	public void setLastLineRead(long lastLineRead) {
		lLastLineRead = lastLineRead;
	}

	public String getLine() {
		return sLine;
	}

	public void setLine(String line) {
		sLine = line;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public void incrementMatchCount() {
		matchCount++;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("Found: ");
		buffer.append(this.bFound);
		buffer.append(", last line # ");
		buffer.append(this.lLastLineRead);
		if (bFound) {
			buffer.append(", match count ");
			buffer.append(this.matchCount);
			buffer.append(", found ");
			buffer.append(searchString);
			buffer.append(" on line # ");
			buffer.append(this.lLineNum);
			buffer.append(", complete line is: ");
			buffer.append(this.sLine);
		}
		return buffer.toString();
	}

	public ArrayList getMatchedItems() {
		return matchedItems;
	}
}