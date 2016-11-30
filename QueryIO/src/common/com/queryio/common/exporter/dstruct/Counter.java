package com.queryio.common.exporter.dstruct;

public class Counter {
	private int value;

	public Counter(final int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}

	public void incrementCounter() {
		this.value++;
	}

	public void incrementBy(int increment) {
		this.value += increment;
	}
}
