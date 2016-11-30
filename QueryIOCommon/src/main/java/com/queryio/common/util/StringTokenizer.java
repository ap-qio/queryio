/*
 * @(#)StringTokenizer.java	1.29 13/10/05
 */

package com.queryio.common.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Optimized version of the java.lang.String
 * 
 * @author Exceed Consultancy Services
 */
public class StringTokenizer implements Enumeration {
	private int currentPosition;
	private int newPosition;
	private final int maxPosition;
	private final char[] str;
	private char[] delimiters;
	private boolean retDelims;
	private boolean delimsChanged;

	/**
	 * maxDelimChar stores the value of the delimiter character with the highest
	 * value. It is used to optimize the detection of delimiter characters.
	 */
	private char maxDelimChar;

	/**
	 * Set maxDelimChar to the highest char in the delimiter set.
	 */
	private void setMaxDelimChar() {
		if (this.delimiters == null) {
			this.maxDelimChar = 0;
			return;
		}

		char m = 0;
		for (int i = 0; i < this.delimiters.length; i++) {
			final char c = this.delimiters[i];
			if (m < c) {
				m = c;
			}
		}
		this.maxDelimChar = m;
	}

	/**
	 * Constructs a string tokenizer for the specified string. All characters in
	 * the <code>delim</code> argument are the delimiters for separating tokens.
	 * <p>
	 * If the <code>returnDelims</code> flag is <code>true</code>, then the
	 * delimiter characters are also returned as tokens. Each delimiter is
	 * returned as a string of length one. If the flag is <code>false</code>,
	 * the delimiter characters are skipped and only serve as separators between
	 * tokens.
	 * <p>
	 * Note that if <tt>delim</tt> is <tt>null</tt>, this constructor does not
	 * throw an exception. However, trying to invoke other methods on the
	 * resulting <tt>StringTokenizer</tt> may result in a
	 * <tt>NullPointerException</tt>.
	 * 
	 * @param str
	 *            a string to be parsed.
	 * @param delim
	 *            the delimiters.
	 * @param returnDelims
	 *            flag indicating whether to return the delimiters as tokens.
	 */
	public StringTokenizer(final String str, final String delim, final boolean returnDelims) {
		this.currentPosition = 0;
		this.newPosition = -1;
		this.delimsChanged = false;
		this.str = str.toCharArray();
		this.maxPosition = this.str.length;
		this.delimiters = delim.toCharArray();
		this.retDelims = returnDelims;
		this.setMaxDelimChar();
	}

	/**
	 * Constructs a string tokenizer for the specified string. The characters in
	 * the <code>delim</code> argument are the delimiters for separating tokens.
	 * Delimiter characters themselves will not be treated as tokens.
	 * 
	 * @param str
	 *            a string to be parsed.
	 * @param delim
	 *            the delimiters.
	 */
	public StringTokenizer(final String str, final String delim) {
		this(str, delim, false);
	}

	/**
	 * Constructs a string tokenizer for the specified string. The tokenizer
	 * uses the default delimiter set, which is
	 * <code>"&nbsp;&#92;t&#92;n&#92;r&#92;f"</code>: the space character, the
	 * tab character, the newline character, the carriage-return character, and
	 * the form-feed character. Delimiter characters themselves will not be
	 * treated as tokens.
	 * 
	 * @param str
	 *            a string to be parsed.
	 */
	public StringTokenizer(final String str) {
		this(str, " \t\n\r\f", false); //$NON-NLS-1$
	}

	// private int getIndexOf(char c, char[] arr)
	// {
	// for(int x=0; x<arr.length; ++x)
	// {
	// if(arr[x] == c)
	// {
	// return x;
	// }
	// }
	// return -1;
	// }

	/**
	 * @return
	 * 
	 */
	private boolean isDelimiter(final char c) {
		for (int x = 0; x < this.delimiters.length; ++x) {
			if (this.delimiters[x] == c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Skips delimiters starting from the specified position. If retDelims is
	 * false, returns the index of the first non-delimiter character at or after
	 * startPos. If retDelims is true, startPos is returned.
	 */
	private int skipDelimiters(final int startPos) {
		if (this.delimiters == null) {
			throw new NullPointerException();
		}

		int position = startPos;
		while (!this.retDelims && (position < this.maxPosition)) {
			final char c = this.str[position];
			if ((c > this.maxDelimChar) || !this.isDelimiter(c)) {
				break;
			}
			position++;
		}
		return position;
	}

	/**
	 * Skips ahead from startPos and returns the index of the next delimiter
	 * character encountered, or maxPosition if no such delimiter is found.
	 */
	private int scanToken(final int startPos) {
		int position = startPos;
		while (position < this.maxPosition) {
			final char c = this.str[position];
			if ((c <= this.maxDelimChar) && this.isDelimiter(c)) {
				break;
			}
			position++;
		}
		if (this.retDelims && (startPos == position)) {
			final char c = this.str[position];
			if ((c <= this.maxDelimChar) && this.isDelimiter(c)) {
				position++;
			}
		}
		return position;
	}

	/**
	 * Tests if there are more tokens available from this tokenizer's string. If
	 * this method returns <tt>true</tt>, then a subsequent call to
	 * <tt>nextToken</tt> with no argument will successfully return a token.
	 * 
	 * @return <code>true</code> if and only if there is at least one token in
	 *         the string after the current position; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasMoreTokens() {
		/*
		 * Temporary store this position and use it in the following nextToken()
		 * method only if the delimiters have'nt been changed in that
		 * nextToken() invocation.
		 */
		this.newPosition = this.skipDelimiters(this.currentPosition);
		return (this.newPosition < this.maxPosition);
	}

	/**
	 * Returns the next token from this string tokenizer.
	 * 
	 * @return the next token from this string tokenizer.
	 * @exception NoSuchElementException
	 *                if there are no more tokens in this tokenizer's string.
	 */
	public String nextToken() {
		/*
		 * If next position already computed in hasMoreElements() and delimiters
		 * have changed between the computation and this invocation, then use
		 * the computed value.
		 */

		this.currentPosition = ((this.newPosition >= 0) && !this.delimsChanged) ? this.newPosition
				: this.skipDelimiters(this.currentPosition);

		/* Reset these anyway */
		this.delimsChanged = false;
		this.newPosition = -1;

		if (this.currentPosition >= this.maxPosition) {
			throw new NoSuchElementException();
		}
		final int start = this.currentPosition;
		this.currentPosition = this.scanToken(this.currentPosition);
		return new String(this.str, start, this.currentPosition - start);
	}

	/**
	 * Returns the next token in this string tokenizer's string. First, the set
	 * of characters considered to be delimiters by this
	 * <tt>StringTokenizer</tt> object is changed to be the characters in the
	 * string <tt>delim</tt>. Then the next token in the string after the
	 * current position is returned. The current position is advanced beyond the
	 * recognized token. The new delimiter set remains the default after this
	 * call.
	 * 
	 * @param delim
	 *            the new delimiters.
	 * @return the next token, after switching to the new delimiter set.
	 * @exception NoSuchElementException
	 *                if there are no more tokens in this tokenizer's string.
	 */
	public String nextToken(final String delim) {
		this.delimiters = delim.toCharArray();

		/* delimiter string specified, so set the appropriate flag. */
		this.delimsChanged = true;

		this.setMaxDelimChar();
		return this.nextToken();
	}

	/**
	 * Returns the same value as the <code>hasMoreTokens</code> method. It
	 * exists so that this class can implement the <code>Enumeration</code>
	 * interface.
	 * 
	 * @return <code>true</code> if there are more tokens; <code>false</code>
	 *         otherwise.
	 * @see java.util.Enumeration
	 * @see java.util.StringTokenizer#hasMoreTokens()
	 */
	public boolean hasMoreElements() {
		return this.hasMoreTokens();
	}

	/**
	 * Returns the same value as the <code>nextToken</code> method, except that
	 * its declared return value is <code>Object</code> rather than
	 * <code>String</code>. It exists so that this class can implement the
	 * <code>Enumeration</code> interface.
	 * 
	 * @return the next token in the string.
	 * @exception NoSuchElementException
	 *                if there are no more tokens in this tokenizer's string.
	 * @see java.util.Enumeration
	 * @see java.util.StringTokenizer#nextToken()
	 */
	public Object nextElement() {
		return this.nextToken();
	}

	/**
	 * Calculates the number of times that this tokenizer's
	 * <code>nextToken</code> method can be called before it generates an
	 * exception. The current position is not advanced.
	 * 
	 * @return the number of tokens remaining in the string using the current
	 *         delimiter set.
	 * @see java.util.StringTokenizer#nextToken()
	 */
	public int countTokens() {
		int count = 0;
		int currpos = this.currentPosition;
		while (currpos < this.maxPosition) {
			currpos = this.skipDelimiters(currpos);
			if (currpos >= this.maxPosition) {
				break;
			}
			currpos = this.scanToken(currpos);
			count++;
		}
		return count;
	}
}
