/* 
 * @(#) StringMatcher.java Jun 16, 2006 
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
package com.queryio.common.util;

import java.util.Vector;

/**
 * @author Exceed Consultancy Services
 */
public class StringMatcher
{
	protected String fPattern;
	protected int fLength; // pattern length
	protected boolean fIgnoreWildCards;
	protected boolean fIgnoreCase;
	protected boolean fHasLeadingStar;
	protected boolean fHasTrailingStar;
	protected String fSegments[]; // the given pattern is split into *
	// separated segments

	/* boundary value beyond which we don't need to search in the text */
	protected int fBound = 0;

	protected static final char FSINGLEWILDCARD = '\u0000';

	public static class Position
	{
		int start; // inclusive
		int end; // exclusive

		public Position(final int start, final int end)
		{
			this.start = start;
			this.end = end;
		}

		public int getStart()
		{
			return this.start;
		}

		public int getEnd()
		{
			return this.end;
		}
	}

	/**
	 * StringMatcher constructor takes in a String object that is a simple
	 * pattern. The pattern may contain '*' for 0 and many characters and '?'
	 * for exactly one character. Literal '*' and '?' characters must be escaped
	 * in the pattern e.g., "\*" means literal "*", etc. Escaping any other
	 * character (including the escape character itself), just results in that
	 * character in the pattern. e.g., "\a" means "a" and "\\" means "\" If
	 * invoking the StringMatcher with string literals in Java, don't forget
	 * escape characters are represented by "\\".
	 * 
	 * @param pattern
	 *            the pattern to match text against
	 * @param ignoreCase
	 *            if true, case is ignored
	 * @param ignoreWildCards
	 *            if true, wild cards and their escape sequences are ignored
	 *            (everything is taken literally).
	 */
	public StringMatcher(final String pattern, final boolean ignoreCase, final boolean ignoreWildCards)
	{
		if (pattern == null)
		{
			throw new IllegalArgumentException();
		}
		this.fIgnoreCase = ignoreCase;
		this.fIgnoreWildCards = ignoreWildCards;
		this.fPattern = pattern;
		this.fLength = pattern.length();

		if (this.fIgnoreWildCards)
		{
			this.parseNoWildCards();
		}
		else
		{
			this.parseWildCards();
		}
	}

	/**
	 * @return Returns the fPattern.
	 */
	public final String getPattern()
	{
		return this.fPattern;
	}

	/**
	 * Find the first occurrence of the pattern between
	 * <code>start</code)(inclusive) 
	 * and <code>end</code>(exclusive).  
	 * @param text the String object to search in 
	 * @param start the starting index of the search range, inclusive
	 * @param end the ending index of the search range, exclusive
	 * @return an <code>StringMatcher.Position</code> object that keeps the starting 
	 * (inclusive) and ending positions (exclusive) of the first occurrence of the 
	 * pattern in the specified range of the text; return null if not found or subtext
	 * is empty (start==end). A pair of zeros is returned if pattern is empty string
	 * Note that for pattern like "*abc*" with leading and trailing stars, position of "abc"
	 * is returned. For a pattern like"*??*" in text "abcdf", (1,3) is returned
	 */
	public StringMatcher.Position find(final String text, int start, int end)
	{
		if (text == null)
		{
			throw new IllegalArgumentException();
		}

		final int tlen = text.length();
		if (start < 0)
		{
			start = 0;
		}
		if (end > tlen)
		{
			end = tlen;
		}
		if ((end < 0) || (start >= end))
		{
			return null;
		}
		if (this.fLength == 0)
		{
			return new Position(start, start);
		}
		if (this.fIgnoreWildCards)
		{
			final int x = this.posIn(text, start, end);
			if (x < 0)
			{
				return null;
			}
			return new Position(x, x + this.fLength);
		}

		final int segCount = this.fSegments.length;
		if (segCount == 0)
		{
			return new Position(start, end);
		}

		int curPos = start;
		int matchStart = -1;
		int i;
		for (i = 0; (i < segCount) && (curPos < end); ++i)
		{
			final String current = this.fSegments[i];
			final int nextMatch = this.regExpPosIn(text, curPos, end, current);
			if (nextMatch < 0)
			{
				return null;
			}
			if (i == 0)
			{
				matchStart = nextMatch;
			}
			curPos = nextMatch + current.length();
		}
		if (i < segCount)
		{
			return null;
		}
		return new Position(matchStart, curPos);
	}

	/**
	 * match the given <code>text</code> with the pattern
	 * 
	 * @return true if matched eitherwise false
	 * @param text
	 *            a String object
	 */
	public boolean match(final String text)
	{
		return this.match(text, 0, text.length());
	}

	/**
	 * Given the starting (inclusive) and the ending (exclusive) positions in
	 * the <code>text</code>, determine if the given substring matches with
	 * aPattern
	 * 
	 * @return true if the specified portion of the text matches the pattern
	 * @param text
	 *            a String object that contains the substring to match
	 * @param start
	 *            marks the starting position (inclusive) of the substring
	 * @param end
	 *            marks the ending index (exclusive) of the substring
	 */
	public boolean match(final String text, int start, int end)
	{
		if (null == text)
		{
			throw new IllegalArgumentException();
		}

		if (start > end)
		{
			return false;
		}

		if (this.fIgnoreWildCards)
		{
			return (end - start == this.fLength)
					&& this.fPattern.regionMatches(this.fIgnoreCase, 0, text, start, this.fLength);
		}
		final int segCount = this.fSegments.length;
		if ((segCount == 0) && (this.fHasLeadingStar || this.fHasTrailingStar))
		{
			// contains
			// only
			// '*'(s)
			return true;
		}
		if (start == end)
		{
			return this.fLength == 0;
		}
		if (this.fLength == 0)
		{
			return start == end;
		}

		final int tlen = text.length();
		if (start < 0)
		{
			start = 0;
		}
		if (end > tlen)
		{
			end = tlen;
		}

		int tCurPos = start;
		final int bound = end - this.fBound;
		if (bound < 0)
		{
			return false;
		}
		int i = 0;
		String current = this.fSegments[i];
		final int segLength = current.length();

		/* process first segment */
		if (!this.fHasLeadingStar)
		{
			if (!this.regExpRegionMatches(text, start, current, 0, segLength))
			{
				return false;
			}
			++i;
			tCurPos = tCurPos + segLength;
		}
		if ((this.fSegments.length == 1) && (!this.fHasLeadingStar) && (!this.fHasTrailingStar))
		{
			// only one segment to match, no wildcards specified
			return tCurPos == end;
		}
		/* process middle segments */
		while (i < segCount)
		{
			current = this.fSegments[i];
			int currentMatch;
			final int k = current.indexOf(FSINGLEWILDCARD);
			if (k < 0)
			{
				currentMatch = this.textPosIn(text, tCurPos, end, current);
				if (currentMatch < 0)
				{
					return false;
				}
			}
			else
			{
				currentMatch = this.regExpPosIn(text, tCurPos, end, current);
				if (currentMatch < 0)
				{
					return false;
				}
			}
			tCurPos = currentMatch + current.length();
			i++;
		}

		/* process final segment */
		if (!this.fHasTrailingStar && (tCurPos != end))
		{
			final int clen = current.length();
			return this.regExpRegionMatches(text, end - clen, current, 0, clen);
		}
		return i == segCount;
	}

	/**
	 * This method parses the given pattern into segments seperated by wildcard
	 * '*' characters. Since wildcards are not being used in this case, the
	 * pattern consists of a single segment.
	 */
	private void parseNoWildCards()
	{
		this.fSegments = new String[1];
		this.fSegments[0] = this.fPattern;
		this.fBound = this.fLength;
	}

	/**
	 * Parses the given pattern into segments seperated by wildcard '*'
	 * characters.
	 */
	private void parseWildCards()
	{
		if (this.fPattern.startsWith("*"))
		{
			this.fHasLeadingStar = true;
		}
		if (this.fPattern.endsWith("*")) {//$NON-NLS-1$
			/* make sure it's not an escaped wildcard */
			if ((this.fLength > 1) && (this.fPattern.charAt(this.fLength - 2) != '\\'))
			{
				this.fHasTrailingStar = true;
			}
		}

		final Vector temp = new Vector();

		int pos = 0;
		final StringBuffer buf = new StringBuffer();
		while (pos < this.fLength)
		{
			final char c = this.fPattern.charAt(pos++);
			switch (c)
			{
				case '\\':
					if (pos >= this.fLength)
					{
						buf.append(c);
					}
					else
					{
						final char next = this.fPattern.charAt(pos++);
						/* if it's an escape sequence */
						if ((next == '*') || (next == '?') || (next == '\\'))
						{
							buf.append(next);
						}
						else
						{
							/* not an escape sequence, just insert literally */
							buf.append(c);
							buf.append(next);
						}
					}
					break;
				case '*':
					if (buf.length() > 0)
					{
						/* new segment */
						temp.addElement(buf.toString());
						this.fBound += buf.length();
						buf.setLength(0);
					}
					break;
				case '?':
					/*
					 * append special character representing single match
					 * wildcard
					 */
					buf.append(FSINGLEWILDCARD);
					break;
				default:
					buf.append(c);
			}
		}

		/* add last buffer to segment list */
		if (buf.length() > 0)
		{
			temp.addElement(buf.toString());
			this.fBound += buf.length();
		}

		this.fSegments = new String[temp.size()];
		temp.copyInto(this.fSegments);
	}

	/**
	 * @param text
	 *            a string which contains no wildcard
	 * @param start
	 *            the starting index in the text for search, inclusive
	 * @param end
	 *            the stopping point of search, exclusive
	 * @return the starting index in the text of the pattern , or -1 if not
	 *         found
	 */
	protected int posIn(final String text, final int start, final int end)
	{// no wild card in pattern
		final int max = end - this.fLength;

		if (!this.fIgnoreCase)
		{
			final int i = text.indexOf(this.fPattern, start);
			if ((i == -1) || (i > max))
			{
				return -1;
			}
			return i;
		}

		for (int i = start; i <= max; ++i)
		{
			if (text.regionMatches(true, i, this.fPattern, 0, this.fLength))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * @param text
	 *            a simple regular expression that may only contain '?'(s)
	 * @param start
	 *            the starting index in the text for search, inclusive
	 * @param end
	 *            the stopping point of search, exclusive
	 * @param p
	 *            a simple regular expression that may contains '?'
	 * @return the starting index in the text of the pattern , or -1 if not
	 *         found
	 */
	protected int regExpPosIn(final String text, final int start, final int end, final String p)
	{
		final int plen = p.length();

		final int max = end - plen;
		for (int i = start; i <= max; ++i)
		{
			if (this.regExpRegionMatches(text, i, p, 0, plen))
			{
				return i;
			}
		}
		return -1;
	}

	protected boolean regExpRegionMatches(final String text, int tStart, final String p, int pStart, int plen)
	{
		while (plen-- > 0)
		{
			final char tchar = text.charAt(tStart++);
			final char pchar = p.charAt(pStart++);

			/* process wild cards */
			if (!this.fIgnoreWildCards)
			{
				/* skip single wild cards */
				if (pchar == FSINGLEWILDCARD)
				{
					continue;
				}
			}
			if (pchar == tchar)
			{
				continue;
			}
			if (this.fIgnoreCase)
			{
				if (Character.toUpperCase(tchar) == Character.toUpperCase(pchar))
				{
					continue;
				}
				// comparing after converting to upper case doesn't handle all
				// cases;
				// also compare after converting to lower case
				if (Character.toLowerCase(tchar) == Character.toLowerCase(pchar))
				{
					continue;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * @param text
	 *            the string to match
	 * @param start
	 *            the starting index in the text for search, inclusive
	 * @param end
	 *            the stopping point of search, exclusive
	 * @param p
	 *            a string that has no wildcard
	 * @return the starting index in the text of the pattern , or -1 if not
	 *         found
	 */
	protected int textPosIn(final String text, final int start, final int end, final String p)
	{

		final int plen = p.length();
		final int max = end - plen;

		if (!this.fIgnoreCase)
		{
			final int i = text.indexOf(p, start);
			if ((i == -1) || (i > max))
			{
				return -1;
			}
			return i;
		}

		for (int i = start; i <= max; ++i)
		{
			if (text.regionMatches(true, i, p, 0, plen))
			{
				return i;
			}
		}

		return -1;
	}

	public static void main(final String[] args)
	{
		final String dir = "C:\\\\AppPerfect\\\\AgentlessMonitor\\\\tomcat\\\\bin\\\\*.bat"; // new
		// File("C:/AppPerfect/AgentlessMonitor/tomcat/bin/*.bat").getAbsolutePath();
		System.out.println("pattern = " + dir);
		final StringMatcher matcher = new StringMatcher(dir, false, false);
		final String toMatch = "C:\\AppPerfect\\AgentlessMonitor\\tomcat\\bin\\catalina.bat"; // new
		// File("C:/AppPerfect/AgentlessMonitor/tomcat/bin/catalina.bat").getAbsolutePath();
		System.out.println("toMatch = " + toMatch);
		final boolean b = matcher.match(toMatch);
		System.out.println("matched = " + b);
	}
}
