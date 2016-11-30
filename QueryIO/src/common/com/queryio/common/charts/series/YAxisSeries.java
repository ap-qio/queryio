/*
 * @(#)  YAxisSeries.java Feb 4, 2005
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
package com.queryio.common.charts.series;

/**
 * @author Exceed Consultancy Services
 */
public final class YAxisSeries extends Series {
	private int[] values = null;

	private int minValue = Integer.MAX_VALUE;

	private int maxValue = Integer.MIN_VALUE;

	private int currentIndex = -1;

	public YAxisSeries() {
		super();
	}

	public YAxisSeries(final String text) {
		super(text);
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

	public int getCurrentIndex() {
		return currentIndex;
	}

	public final int getMinValue() {
		return this.minValue;
	}

	public final void multiply(float multiplier) {
		if (values != null && currentIndex >= 0) {
			this.minValue = Integer.MAX_VALUE;
			this.maxValue = Integer.MIN_VALUE;
			for (int i = 0; i <= currentIndex; i++) {
				if (values[i] != Integer.MIN_VALUE) {
					values[i] = Math.round(values[i] * multiplier);
					this.minValue = Math.min(this.minValue, values[i]);
					this.maxValue = Math.max(this.maxValue, values[i]);
				}
			}
		}
	}

	public int[] returnAllValues() {
		return this.values;
	}
}
