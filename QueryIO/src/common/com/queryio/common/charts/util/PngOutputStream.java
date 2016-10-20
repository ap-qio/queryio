/*
 * @(#)  PngOutputStream.java
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
package com.queryio.common.charts.util;

import java.io.ByteArrayOutputStream;

/**
 * 
 * 
 * @author Exceed Consultancy Services
 */
public class PngOutputStream extends ByteArrayOutputStream
{
	private byte[] byteArrayToUse = null;
	private boolean referenceChanged = false;

	/**
	 * 
	 */
	public PngOutputStream(final byte[] array)
	{
		super();
		this.byteArrayToUse = array;
	}

	/**
	 * @param size
	 */
	public PngOutputStream(final int size, final byte[] array)
	{
		super(size);
		this.byteArrayToUse = array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.ByteArrayOutputStream#toByteArray()
	 */
	public synchronized byte[] toByteArray()
	{
		if ((this.byteArrayToUse == null) || (this.byteArrayToUse.length < this.count))
		{
			this.byteArrayToUse = new byte[this.count];
			this.referenceChanged = true;
		}
		System.arraycopy(this.buf, 0, this.byteArrayToUse, 0, this.count);
		return this.byteArrayToUse;
	}

	public final boolean hasReferenceChanged()
	{
		return this.referenceChanged;
	}

	public final int getLastPosition()
	{
		return this.count;
	}

	/**
	 * @param bs
	 */
	public void setByteArray(final byte[] bs)
	{
		this.byteArrayToUse = bs;
	}

	/**
	 * @param b
	 */
	public void setReferenceChanged(final boolean b)
	{
		this.referenceChanged = b;
	}

	public void setLastPosition(final int count)
	{
		this.count = count;
	}

}
