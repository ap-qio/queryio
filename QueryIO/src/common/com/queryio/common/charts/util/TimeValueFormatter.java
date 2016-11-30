/*
 * @(#)  TimeValueFormatter.java Feb 4, 2005
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.queryio.common.IProductConstants;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class TimeValueFormatter {
	private static final DateFormat DAY_TIME = new SimpleDateFormat(
			System.getProperty("chart.datetime", "EEE, h:mm a")); // Wed, 12:03
																	// AM
	private static final int INDEX_HOUR = 0;
	private static final int INDEX_MIN = 1;
	private static final int INDEX_SEC = 2;

	// MADE A PRIVATE CONSTRUCTOR, BECAUSE WE DONOT WANT NE ONE TO MAKE
	// INSTANCES OF THIS CLASS.
	private TimeValueFormatter() {
		// DO NOTHING - PRIVATE CONSTRUCTOR
	}

	public static final String getFormattedValue(final long startTime, final long currTime, final long endTime,
			final boolean bShowRelativeTime) {
		return getFormattedValue(startTime, currTime, endTime, bShowRelativeTime, false);
	}

	public static final String getFormattedValue(final long startTime, final long currTime, final long endTime,
			final boolean bShowRelativeTime, boolean crossedMidNight) {
		final StringBuffer strDiffTime = new StringBuffer();
		if (bShowRelativeTime) {
			if (currTime < 0) {
				strDiffTime.append('-');
			}
			final long arrCurrDiffTime[] = getRelativeHHMMSSValues(startTime, currTime);
			final long arrLastDiffTime[] = getRelativeHHMMSSValues(startTime, endTime);

			if (arrLastDiffTime[INDEX_HOUR] != 0) {
				strDiffTime.append(getStringValue(arrCurrDiffTime[INDEX_HOUR]));
				strDiffTime.append(':');
			}
			strDiffTime.append(getStringValue(arrCurrDiffTime[INDEX_MIN]));
			strDiffTime.append(':');
			strDiffTime.append(getStringValue(arrCurrDiffTime[INDEX_SEC]));
		} else {
			final DateFormat format = crossedMidNight ? DAY_TIME : DateFormat.getTimeInstance(DateFormat.SHORT);
			strDiffTime.append(format.format(new Date(currTime)));
		}
		return strDiffTime.toString();
	}

	private static final String getStringValue(final long timeUnit) {
		String strValue = IProductConstants.EMPTY_STRING;
		if ((timeUnit >= 0) && (timeUnit < 10)) {
			strValue = "0" + timeUnit; //$NON-NLS-1$
		} else {
			strValue += timeUnit;
		}

		return strValue;
	}

	private static final long[] getRelativeHHMMSSValues(final long startTime, final long endTime) {
		final long arrHhMmSs[] = new long[3];
		final long diff = endTime - startTime;
		final long secs = diff / 1000;
		final long mins = secs / 60;
		final long hours = mins / 60;

		arrHhMmSs[INDEX_HOUR] = hours;
		arrHhMmSs[INDEX_MIN] = mins % 60;
		arrHhMmSs[INDEX_SEC] = secs % 60;

		return arrHhMmSs;
	}

	public static String getDummyMaxTimeValue() {
		return "00:00:00"; //$NON-NLS-1$
	}

	public static void main(final String args[]) {
		final long tenMins = 600000;
		final long oneSec = 1000;

		long startTime = System.currentTimeMillis();
		final long currTime = startTime + tenMins + oneSec * 8;
		final long endTime = startTime + tenMins + tenMins;
		final String strFormattedValue = getFormattedValue(startTime, currTime, endTime, false);

		System.out.println("StrFormattedValue : " + strFormattedValue); //$NON-NLS-1$
	}
}
