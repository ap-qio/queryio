/*
 * @(#)  WindowsNetworkInfo.java
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
package com.queryio.common.dstruct;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class WindowsNetworkInfo extends NetworkInfo {
	private static final long serialVersionUID = 752600005701000047L;

	// called from native
	public WindowsNetworkInfo(final String name, final int interfaceId, final float recdPacketsPerSec,
			final float sentPacketsPerSec) {
		super(name, interfaceId, recdPacketsPerSec, sentPacketsPerSec);
	}

}
