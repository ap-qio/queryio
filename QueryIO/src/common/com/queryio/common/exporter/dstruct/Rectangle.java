/*
 * @(#)  Rectangle.java Feb 4, 2005
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
package com.queryio.common.exporter.dstruct;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class Rectangle implements java.io.Serializable {
	private static final long serialVersionUID = 752600005701000035L;
	/**
	 * the x coordinate of the rectangle
	 */
	public int x;

	/**
	 * the y coordinate of the rectangle
	 */
	public int y;

	/**
	 * the width of the rectangle
	 */
	public int width;

	/**
	 * the height of the rectangle
	 */
	public int height;

	/**
	 * Construct a new instance of this class given the x, y, width and height
	 * values.
	 * 
	 * @param x
	 *            the x coordinate of the origin of the rectangle
	 * @param y
	 *            the y coordinate of the origin of the rectangle
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 */
	public Rectangle(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the rectangle
	 */
	public String toString() {
		return "Rectangle {" + this.x + ", " + this.y + ", " + this.width + ", " + this.height + "}"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	public boolean contains(final int X, final int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		final int x = this.x;
		final int y = this.y;
		if ((X < x) || (Y < y)) {
			return false;
		}
		w += x;
		h += y;
		// overflow || intersect
		return (((w < x) || (w > X)) && ((h < y) || (h > Y)));
	}

}
