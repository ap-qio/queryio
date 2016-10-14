/* 
 * @(#) UniqueIdGenerator.java May 2, 2006 
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
package com.queryio.common.service.wrapper;

public class UniqueIdGenerator
{
	private static final Object UNIQUE = new Object();
	private static long ctr = 0;

	private UniqueIdGenerator()
	{
		// private constructor
	}

	/**
	 * @return
	 */
	public static String generateUniqueId(final String prefix)
	{
		synchronized (UNIQUE)
		{
			return prefix + (++ctr);
		}
	}
}
