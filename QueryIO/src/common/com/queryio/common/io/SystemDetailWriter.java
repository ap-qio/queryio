/*
 * @(#)  SystemDetailWriter.java	Created on Jan 9, 2004
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.io;

import com.queryio.common.exporter.dstruct.BaseSystemInfo;

/**
 * This class is used to write only the System Details, product related details
 * will not be saved. Whenever the user changes any details in the System
 * Properties in the Options Dialog Box, the System Details are written to the
 * file.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class SystemDetailWriter extends BaseSystemInfoWriter
{
	/**
	 * @param sysInfo
	 * @throws Exception
	 */
	public SystemDetailWriter(final BaseSystemInfo sysInfo) throws Exception
	{
		super(sysInfo);
	}

	/**
	 * @see com.queryio.sysmoncommon.io.BaseSystemInfoWriter#getProductHome()
	 */
	protected final String getProductHome()
	{
		return null;
	}
}