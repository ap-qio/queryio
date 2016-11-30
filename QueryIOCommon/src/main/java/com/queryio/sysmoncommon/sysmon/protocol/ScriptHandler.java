/*
 * @(#)  ScriptHandler.java
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
package com.queryio.sysmoncommon.sysmon.protocol;

/**
 * 
 * @author Exceed Consultancy Services
 */
/*
 * A script handler, that tries to match strings and returns true when it found
 * the string it searched for.
 */
public class ScriptHandler {

	private int matchPos; // current position in the match
	private byte[] match; // the current bytes to look for
	private boolean done = true; // nothing to look for!

	/**
	 * Setup the parser using the passed string.
	 * 
	 * @param match
	 *            the string to look for
	 */
	public void setup(final String strMatch) {
		if (strMatch == null) {
			return;
		}
		this.match = strMatch.getBytes();
		this.matchPos = 0;
		this.done = false;
	}

	/**
	 * Try to match the byte array s against the match string.
	 * 
	 * @param s
	 *            the array of bytes to match against
	 * @param length
	 *            the amount of bytes in the array
	 * @return true if the string was found, else false
	 */
	public boolean match(final byte[] s, final int length) {
		if (this.done) {
			return true;
		}
		for (int i = 0; !this.done && (i < length); i++) {
			if (s[i] == this.match[this.matchPos]) {
				// the whole thing matched so, return the match answer
				// and reset to use the next match
				if (++this.matchPos >= this.match.length) {
					this.done = true;
					return true;
				}
			} else {
				this.matchPos = 0; // get back to the beginning
			}
		}
		return false;
	}
}
