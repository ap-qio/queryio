/*
 * @(#)  Color.java Feb 4, 2005
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

import java.util.StringTokenizer;

import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.util.AppLogger;

/**
 *
 * @author Exceed Consultancy Services
 */
public class Color implements java.io.Serializable
{
	private static final long serialVersionUID = 752600005701000032L;
	public int red;
	public int green;
	public int blue;

	public Color(final String colorStr)
	{
		try
		{
			final StringTokenizer stk = new StringTokenizer(colorStr, ","); //$NON-NLS-1$

			this.red = Integer.parseInt(stk.nextToken());
			this.green = Integer.parseInt(stk.nextToken());
			this.blue = Integer.parseInt(stk.nextToken());
		}
		catch (final NumberFormatException e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
			// do nothing
		}
	}

	public Color(final int red, final int green, final int blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sBuff = new StringBuffer();
		sBuff.append(this.red);
		sBuff.append(',');
		sBuff.append(this.green);
		sBuff.append(',');
		sBuff.append(this.blue);

		return sBuff.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj)
	{
		if (obj instanceof Color)
		{
			final Color clr = (Color) obj;
			return (this.red == clr.red) && (this.green == clr.green) && (this.blue == clr.blue);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return ((this.red & 0xFF) << 16) | ((this.green & 0xFF) << 8) | ((this.blue & 0xFF) << 0);

	}

	/**
	 * Method toAWTColorString This method converts the Color object to an
	 * equivalent String which would be obtained by
	 * Integer.toHexString(myAWTColor.getRGB()).substring(2) Used in AppPerfect
	 * Agentless Monitor
	 *
	 * @param color
	 */
	public static String toAWTColorString(final Color color)
	{
		try
		{
			final StringBuffer sbBuff = new StringBuffer(6);
			sbBuff.append(Integer.toHexString(
					((255 & 0xFF) << 24) | ((color.red & 0xFF) << 16) | ((color.green & 0xFF) << 8)
							| ((color.blue & 0xFF) << 0)).substring(2));
			return sbBuff.toString();
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
			// DO NOTHING
		}
		return "ffffff"; //$NON-NLS-1$
	}

	/**
	 * Method decode This method returns a Color object created using the red,
	 * green, blue values found from the String which was obtained by
	 * Color.toAWTColorString(Color color) Used in AppPerfect Agentless Monitor
	 *
	 * @param str
	 * @return
	 */
	public static Color decode(String str)
	{
		try
		{
			if (str.charAt(0) != '#')
			{
				str = '#' + str;
			}
			final Integer intval = Integer.decode(str);
			final int i = intval.intValue();
			return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
			// DO NOTHING
		}
		return ChartConstants.COLOR_WHITE;
	}
}
