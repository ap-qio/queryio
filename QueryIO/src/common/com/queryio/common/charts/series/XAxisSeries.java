package com.queryio.common.charts.series;

public abstract class XAxisSeries extends Series {
	public static final byte TIMEVALUES = 1;
	public static final byte LONGVALUES = 2;
	public static final byte STRINGVALUES = 3;
	public static final byte INTVALUES = 4;

	protected int currentIndex = -1;
	private final byte valuesType;

	public XAxisSeries(byte valuesType) {
		this(valuesType, null);
	}

	public XAxisSeries(byte valuesType, final String text) {
		super(text);
		this.valuesType = valuesType;
	}

	public final int getCurrentCount() {
		return this.currentIndex + 1;
	}

	public byte getValuesType() {
		return valuesType;
	}

	public static String getFormattedValueOf(final long value) {
		return String.valueOf(value);
	}

	public static String getFormattedValueOf(final int value) {
		return String.valueOf(value);
	}

	public abstract String getFormattedMaxValue();

	public abstract String getFormattedMinValue();

	public abstract String getFormattedValue(final int index);

}
