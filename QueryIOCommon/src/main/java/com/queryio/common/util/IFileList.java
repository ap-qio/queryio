/*
 * @(#)  IFileList.java     1.0     16/10/2002 (DD/MM/YYYY)
 *
 * Copyright (C) 2002 - 2003 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.queryio.common.util;

import java.io.File;

/**
 * 
 * @author Exceed Consultancy Services.
 * @version 1.0
 */
public interface IFileList {
	/**
	 * Method isExists.
	 * 
	 * @param f
	 * @return boolean
	 */
	boolean isExists(File f);
}
