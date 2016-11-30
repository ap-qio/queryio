/*
 * @(#)  Point.java Feb 4, 2005
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
public class Point implements java.io.Serializable {
	private static final long serialVersionUID = 752600005701000034L;
	/**
	 * the x coordinate of the point
	 */
	public int x;

	/**
	 * the y coordinate of the point
	 */
	public int y;

	/**
	 * Constructs a new point with the given x and y coordinates.
	 * 
	 * @param x
	 *            the x coordinate of the new point
	 * @param y
	 *            the y coordinate of the new point
	 */
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Compares the argument to the receiver, and returns true if they represent
	 * the <em>same</em> object using a class specific comparison.
	 * 
	 * @param object
	 *            the object to compare with this object
	 * @return <code>true</code> if the object is the same as this object and
	 *         <code>false</code> otherwise
	 * 
	 * @see #hashCode
	 */
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof Point)) {
			return false;
		}
		final Point p = (Point) object;
		return (p.x == this.x) && (p.y == this.y);
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the point
	 */
	public String toString() {
		return "Point {" + this.x + ", " + this.y + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
