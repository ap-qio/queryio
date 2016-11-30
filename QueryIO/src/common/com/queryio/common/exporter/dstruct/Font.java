/*
 * @(#)  Font.java Feb 4, 2005
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
public class Font implements java.io.Serializable {
	public static final int STYLE_PLAIN = 0;
	public static final int STYLE_BOLD = 1;
	public static final int STYLE_ITALIC = 2;
	private static final long serialVersionUID = 752600005701000033L;
	public String name;
	public int style;
	public int size;

	public Font(final String name, final int size, final int style) {
		this.name = name;
		this.style = style;
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (obj instanceof Font) {
			final Font fnt = (Font) obj;
			if ((this.name == null) && (fnt.name == null)) {
				return true;
			} else if ((this.name != null) && this.name.equals(fnt.name)) {
				return (this.size == fnt.size) && (this.style == fnt.style);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (this.name != null) ? this.name.hashCode() : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Name: " + this.name + " style: " + this.style + " size: " + this.size; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
