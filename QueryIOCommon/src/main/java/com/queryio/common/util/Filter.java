/*
 * @(#)  Filter.java  1.0  19/05/2004
 *
 * Copyright (C) 2002 - 2003 Exceed Consultancy Services. All rights reserved.
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
package com.queryio.common.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Exceed Consultancy Services
 * 
 */
public class Filter {
	private ArrayList filterTokens;
	private char[] filterName;
	private boolean includeAllFilter;

	public Filter() {
		// Default empty constructor
	}

	/**
	 * Filter
	 * 
	 * @param filterExpression
	 */
	public Filter(String filterExpression) {
		if (filterExpression == null) {
			filterExpression = "";
		}
		this.includeAllFilter = true;
		this.filterName = filterExpression.toCharArray();
		for (int i = 0; i < this.filterName.length; i++) {
			if ((this.filterName[i] != '*') && (this.filterName[i] != '?')) {
				this.includeAllFilter = false;
				break;
			}
		}
		this.initFilterExpression(filterExpression);
	}

	private void initFilterExpression(final String filterExpression) {
		final StringTokenizer tokenizer = new StringTokenizer(filterExpression, ",");//$NON-NLS-1$

		this.filterTokens = new ArrayList(tokenizer.countTokens());
		final StringBuffer buffer = new StringBuffer();
		ArrayList filterExpressionTokens;

		while (tokenizer.hasMoreTokens()) {
			buffer.setLength(0);
			final String token = tokenizer.nextToken();
			final char[] expChars = (token.trim()).toCharArray();
			if (expChars.length == 0) {
				continue;
			}
			/**
			 * Build the filter pattern by collecting tokens from the
			 * expression. for ex. "*.myclass??" will collect "" and
			 * ".myclass??" tokens.
			 */
			filterExpressionTokens = new ArrayList(10); // $IGN_Avoid_object_instantiation_in_loops$
			for (int i = 0; i < expChars.length; i++) {
				final char ch = expChars[i];
				if (ch == '*') {
					final String value = buffer.toString();
					// System.out.print(value + " ");
					filterExpressionTokens.add(value);
					buffer.setLength(0);
				} else {
					buffer.append(ch);
				}
				if (i + 1 == expChars.length) {
					// The expression characters end, therefore put it to tokens
					final String value = buffer.toString();
					filterExpressionTokens.add(value);
					// System.out.println(value);
				}
			}
			this.filterTokens.add(filterExpressionTokens);
		}
	}

	/**
	 * This method will find out whether parameter string matches the pattern.
	 * Currently supports the wild card characters '?' and '*' only.
	 * 
	 * @param value
	 * @return
	 */
	public boolean matches(final String value) {
		final char valueArray[] = value.toCharArray();
		boolean bMatchFound = this.includeAllFilter;
		for (int i = 0; (i < this.filterTokens.size()) && !bMatchFound; i++) {
			bMatchFound = this.matches(valueArray, (ArrayList) this.filterTokens.get(i));
		}
		return bMatchFound;
	}

	/**
	 * 
	 * method matches
	 * 
	 * @param valueArray
	 * @param filterExpressionTokens
	 * @return
	 */
	private boolean matches(final char[] valueArray, final ArrayList filterExpressionTokens) {
		// System.out.println("Evaluating for value : " +
		// String.valueOf(valueArray));
		int current = 0;
		int i = 0;
		final int ntokens = filterExpressionTokens.size();
		char array[] = new char[0];
		for (; i < ntokens;) {
			array = ((String) filterExpressionTokens.get(i)).toCharArray();
			// System.out.println("token " + i + ". " +
			// filterExpressionTokens.get(i));
			if (array.length > valueArray.length - current) {
				// The token size is greater than to be compared string size.
				return false;
			}
			int j = 0;
			final int prev = current;
			if ((i > 0) && (i + 1 == ntokens) && (array.length != 0)) {
				/*
				 * There are no tokens left and the last token is not an empty
				 * token. This means the match may not be perfect. for ex.
				 * filter expression : *Model string to compare:
				 * TableModel$ThisModel The token "Model" will match first
				 * substring but we need to match it to last substring.
				 */
				current = valueArray.length - array.length;
				// System.out.println("Last token current: " + current);
			}
			// Compare character by character. For '?' just continue the loop,
			// else compare the characters
			for (; j < array.length; j++, current++) {
				if ((array[j] != '?') && (array[j] != valueArray[current])) {
					break;
				}
			}
			if (j < array.length) {
				// means starting from current position, we could not get the
				// match.
				// therefore go to next index only if it is not the first token.
				if (i == 0) {
					return false;
				}
				current = prev + 1; /*
									 * this is done for casese like: filter
									 * expression : java.*Model string to
									 * compare: java.TableModel here we have two
									 * tokens "java." and "Model" while
									 * comparing for the token "Model" we keep
									 * on skipping the characters T,a,b,l,e.
									 * After that "Model" will match "Model".
									 */
			} else {
				// matched with the current token, therefore go to next token
				i++;
			}
		}
		if ((array.length != 0 /* not an empty token */) && (current != valueArray.length)) {
			// means that there was no '*' at the end of the filter expression
			// and
			// the string being compared has some more characters even after
			// matching is over.
			return false;
		}
		return true;
	}

	public char[] getFilterName() {
		return this.filterName;
	}

	public String getFilterNameasString() {
		return new String(this.filterName);
	}
}
