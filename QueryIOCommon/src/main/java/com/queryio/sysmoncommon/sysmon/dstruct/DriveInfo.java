/*
 * @(#)  DriveInfo.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.sysmoncommon.sysmon.dstruct;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class DriveInfo {
	private final String sName;
	private final float fTotalSpace;
	private final float fUsedSpace;

	/**
	 * Constructs DriveInfo object
	 * 
	 * @param name
	 *            - name of the drive
	 * @param totalSpace
	 *            - total space on the drive
	 * @param usedSpace
	 *            - used space on the drive
	 */
	public DriveInfo(final String name, final float totalSpace, final float usedSpace) {
		this.sName = name;
		this.fTotalSpace = totalSpace;
		this.fUsedSpace = usedSpace;
	}

	/**
	 * Returns name of the drive
	 * 
	 * @return drive name
	 */
	public String getName() {
		return this.sName;
	}

	/**
	 * Returns total space of the disk drive
	 * 
	 * @return Total space of drive (GB for windows and MB for Linux)
	 */
	public float getTotalSpace() throws Exception {
		if ((this.fTotalSpace < 0) || (this.fTotalSpace > Float.POSITIVE_INFINITY)) {
			throw new Exception("Total space value is incorrect");
		}
		return this.fTotalSpace;
	}

	/**
	 * Returns used space of the drive
	 * 
	 * @return used space on the drive (GB for windows and MB for Linux)
	 */
	public float getUsedSpace() throws Exception {
		if ((this.fUsedSpace < 0) || (this.fUsedSpace > Float.POSITIVE_INFINITY)) {
			throw new Exception("Total used value is incorrect");
		}
		return this.fUsedSpace;
	}
}
