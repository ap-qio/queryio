/*
 * @(#)  XAxisIntegerSeries.java Mar 6, 2009
 *
 * Copyright (C) 2009 Exceed Consultancy Services. All Rights Reserved.
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
package com.queryio.common.charts.series;

/**
 * @author Exceed Consultancy Services
 */
public class XAxisIntegerSeries extends XAxisSeries {
	protected int minValue = Integer.MAX_VALUE;

	protected int maxValue = Integer.MIN_VALUE;

	protected int[] values = null;

	public XAxisIntegerSeries() {
		super(INTVALUES);
	}

	public XAxisIntegerSeries(final String text) {
		super(INTVALUES, text);
	}

	public void initialize(int capacity) {
		if (capacity <= 0) {
			capacity = 1;
		}
		this.values = new int[capacity];
		for (int i = 0; i < capacity; i++) {
			this.values[i] = Integer.MIN_VALUE;
		}
		this.currentIndex = -1;
		this.minValue = Integer.MAX_VALUE;
		this.maxValue = Integer.MIN_VALUE;
	}

	public int getCapacity() {
		return (this.values != null ? this.values.length : 0);
	}

	public void setValue(final int index, final int value) {
		this.values[index] = value;
		if (Integer.MIN_VALUE != value) {
			this.minValue = Math.min(this.minValue, value);
			this.maxValue = Math.max(this.maxValue, value);
		}
		this.currentIndex = Math.max(this.currentIndex, index);
	}

	public void setNextValue(final int value) {
		this.setValue(++this.currentIndex, value);
	}

	public int getValue(final int index) {
		return this.values[index];
	}

	public final int getMaxValue() {
		return this.maxValue;
	}

	public final int getMinValue() {
		return this.minValue;
	}

	public String getFormattedMaxValue() {
		return String.valueOf(this.maxValue);
	}

	public String getFormattedMinValue() {
		return String.valueOf(this.minValue);
	}

	public String getFormattedValue(final int index) {
		return getFormattedValueOf(this.values[index]);
	}

	public void setMaxValue(final int maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(final int minValue) {
		this.minValue = minValue;
	}

}
