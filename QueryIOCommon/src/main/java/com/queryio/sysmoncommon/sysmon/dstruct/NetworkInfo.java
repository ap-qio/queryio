/*
 * @(#)  NetworkInfo.java
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

import java.io.Serializable;

/**
 * @author Exceed Consultancy Services
 * 
 */
public class NetworkInfo implements Serializable {
	private static final long serialVersionUID = 752600005701000045L;
	private final String sName;

	private int iInterfaceId = 0;

	protected float fReadMBPerSec = 0;
	protected float fSentMBPerSec = 0;

	/**
	 * Method NetworkInfo.
	 * 
	 * @param name
	 * @param interfaceId
	 * @param recdPacketsPerSec
	 * @param sentPacketsPerSec
	 */
	public NetworkInfo(final String name, final int interfaceId, final float recdMBPerSec, final float sentMBPerSec) {
		this.sName = name.trim();
		this.iInterfaceId = interfaceId;
		this.fReadMBPerSec = recdMBPerSec;
		this.fSentMBPerSec = sentMBPerSec;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public final float getRecdPacketsPerSec() throws Exception {
		if ((this.fReadMBPerSec < 0) || (this.fReadMBPerSec > Float.MAX_VALUE)) {
			throw new Exception("MB Read Per second is incorrect : fReadMBPerSec : " + this.fReadMBPerSec);
		}
		return this.fReadMBPerSec;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public final float getSentPacketsPerSec() throws Exception {
		if ((this.fSentMBPerSec < 0) || (this.fSentMBPerSec > Float.MAX_VALUE)) {
			throw new Exception("MB Sent Per second is incorrect");
		}
		return this.fSentMBPerSec;
	}

	/**
	 * Returns the sName.
	 * 
	 * @return String
	 */
	public final String getName() {
		return this.sName;
	}

	/**
	 * Returns the InterfaceId.
	 * 
	 * @return int
	 */
	public final int getInterfaceId() throws Exception {
		if ((this.iInterfaceId < 0) || (this.iInterfaceId > Integer.MAX_VALUE)) {
			throw new Exception("Interface id value is incorrect");
		}
		return this.iInterfaceId;
	}

	public void setRecdPacketsPerSec(float recdPacketsPerSec) {
		this.fReadMBPerSec = recdPacketsPerSec;
	}

	public void setSentPacketsPerSec(float writePacketsPerSec) {
		this.fSentMBPerSec = writePacketsPerSec;
	}

}
