/* 
 * @(#) ControllerStateInformation.java Jan 20, 2010 
 * 
 * Copyright (C) 2002 AppPerfect Corporation. All Rights Reserved. 
 * 
 * This software is proprietary information of AppPerfect Corporation and 
 * constitutes valuable trade secrets of AppPerfect Corporation. You shall 
 * not disclose this information and shall use it only in accordance with the 
 * terms of License. 
 * 
 * APPPERFECT CORPORATION MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE 
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT 
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE 
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. 
 */
package com.queryio.core.monitor.dstruct;

public class ControllerStateInformation {
	private String state;
	private long startTime;
	private long lastDataTime;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getLastDataTime() {
		return lastDataTime;
	}

	public void setLastDataTime(long lastDataTime) {
		this.lastDataTime = lastDataTime;
	}

}
