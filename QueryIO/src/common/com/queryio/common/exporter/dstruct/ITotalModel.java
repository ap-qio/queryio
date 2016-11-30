/*
 * @(#) ITotalModel.java
 *
 * Copyright (C) 2002- 2004 Exceed Consultancy Services. All rights reserved.
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
package com.queryio.common.exporter.dstruct;

/**
 * This interface defines the methods which must be implemented by the table
 * where we want to show total at the bottom.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public interface ITotalModel {
	/**
	 * ITotalModel # getColCount
	 * 
	 * @return
	 */
	int getColCount();

	/**
	 * ITotalModel # getValueForCol
	 * 
	 * @param col
	 * @return
	 */
	Object getValueForCol(int col);
}