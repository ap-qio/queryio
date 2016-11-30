/*
 * @(#)  CommandNotFoundException.java Dec 8, 2004
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
package com.queryio.sysmoncommon.sysmon;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CommandNotFoundException extends Exception {
	private static final long serialVersionUID = 752600005701000043L;
	private final String command;
	private final String dataFetched;

	public CommandNotFoundException(final String dataFetched, final String command) {
		super(command + " not found in the path.");
		this.command = command;
		this.dataFetched = dataFetched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		if ("iostat".equals(this.command)) {
			return "Error while fetching " + this.dataFetched
					+ ". Please verify that iostat is there in the environmental variable 'PATH'. iostat is not available by default on some Linux systems. Please refer to documentation for further deatils.";
		}
		return "Error while fetching " + this.dataFetched + ". Please verify that " + this.command
				+ " is there in the environmental variable 'PATH'. ";
	}
}