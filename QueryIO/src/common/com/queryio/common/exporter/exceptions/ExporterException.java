/*
 * @(#)  ExporterException.java May 25, 2005
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
package com.queryio.common.exporter.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * ExporterException
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class ExporterException extends Exception {
	private static final long serialVersionUID = 752600005701000038L;
	/**
	 * The throwable that caused this Exception to get thrown, or null if this
	 * exception was not caused by another throwable, or if the causative
	 * throwable is unknown.
	 */
	private Throwable cause = this;

	/**
	 * Constructor
	 * 
	 * @param msg
	 */
	public ExporterException(final String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * 
	 * @param msg
	 */
	public ExporterException(final String msg, final Throwable cause) {
		super(msg);
		this.cause = cause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(final PrintStream s) {
		s.println(this);
		super.printStackTrace(s);
		if (this.cause != null) {
			s.println("Caused by : " + this.cause); //$NON-NLS-1$
			this.cause.printStackTrace(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(final PrintWriter s) {
		s.println(this);
		super.printStackTrace(s);
		if (this.cause != null) {
			s.println("Caused by : " + this.cause); //$NON-NLS-1$
			this.cause.printStackTrace(s);
		}
	}
}
