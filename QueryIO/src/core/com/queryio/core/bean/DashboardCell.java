package com.queryio.core.bean;

public class DashboardCell {
	public static final int CELL_EMPTY = 0;
	public static final int CELL_SUSPENDED = 1;
	public static final int CELL_OK = 2;
	public static final int CELL_WARNING = 3;
	public static final int CELL_ERROR = 4;
	public static final int CELL_FAILURE = 5;

	private int state = CELL_EMPTY;

	private long startTime = -1;

	/**
	 * method getStartTime
	 * 
	 * @return
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * method setStartTime
	 * 
	 * @param l
	 */
	public void setStartTime(final long l) {
		this.startTime = l;
	}

	/**
	 * method getState
	 * 
	 * @return
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * method setState
	 * 
	 * @param i
	 */
	public void setState(final int i) {
		this.state = i;
	}

}
