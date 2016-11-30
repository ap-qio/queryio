/*
 * @(#) IProductConstants.java
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common;

/**
 * This interface defines the constants to be used across all the applications.
 * This interface defines the product constants.
 *
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public interface IProductConstants {
	int QUERYIO = 0;
	/**
	 * String constants to signify empty string <code>EMPTY_STRING</code>
	 */
	String EMPTY_STRING = ""; //$NON-NLS-1$
	/**
	 * Comment for <code>SPACE_STRING</code>
	 */
	char SPACE_STRING = ' '; // $NON-NLS-1$
	/**
	 * String constants to signify comma string <code>COMMA_STRING</code>
	 */
	char COMMA_STRING = ','; // $NON-NLS-1$
	/**
	 * String constants to signify tab string <code>TAB</code>
	 */
	char TAB = '\t'; // $NON-NLS-1$
	/**
	 * String constants to signify left string <code>LEFT_BRACE</code>
	 */
	char LEFT_BRACE = '(';// $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string <code>RIGHT_BRACE</code>
	 */
	char RIGHT_BRACE = ')';// $NON-NLS-1$
	/**
	 * String constants to signify separator <code>CODE_SEPARATOR</code>
	 */
	char CODE_SEPARATOR = ';'; // $NON-NLS-1$
	/**
	 * String constants to signify equals <code>EQUALS</code>
	 */
	String EQUALS = " = "; //$NON-NLS-1$
	/**
	 * String constants to signify both brackets string <code>BOTH_BRACES</code>
	 */
	String BOTH_BRACES = "()";//$NON-NLS-1$
	/**
	 * String constants to signify left string <code>LEFT_CURLY</code>
	 */
	char LEFT_CURLY = '{';// $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string <code>RIGHT_CURLY</code>
	 */
	char RIGHT_CURLY = '}';// $NON-NLS-1$
	/**
	 * String constants to signify new string <code>NEW_STRING</code>
	 */
	String NEW_STRING = "new ";//$NON-NLS-1$
	/**
	 * String constants to signify new string <code>NULL_STRING</code>
	 */
	String NULL_STRING = "null"; //$NON-NLS-1$
	/**
	 * String constants to signify new string <code>ZERO_STRING</code>
	 */
	String ZERO_STRING = "0"; //$NON-NLS-1$
	/**
	 * String constants to signify new string <code>COMMA_SPACE_STRING</code>
	 */
	String COMMA_SPACE_STRING = ", "; //$NON-NLS-1$
	/**
	 * String constants to signify left string <code>LEFT_SQUARE</code>
	 */
	char LEFT_SQUARE = '[';// $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string
	 * <code>RIGHT_SQUARE</code>
	 */
	char RIGHT_SQUARE = ']';// $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string <code>DOT</code>
	 */
	char DOT = '.';// $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string <code>COLON</code>
	 */
	char COLON = ':'; // $NON-NLS-1$
	/**
	 * String constants to signify rigth bracket string <code>SEMICOLON</code>
	 */
	char SEMICOLON = ';'; // $NON-NLS-1$

	/**
	 * Integer constant to signify <code>SWT</code> user interface to use during
	 * chart creation
	 */
	int USER_INTERFACE_SWT = 1;
	/**
	 * Integer constant to signify <code>Swing</code> user interface to use
	 * during chart creation
	 */
	int USER_INTERFACE_SWING = 2;

	/**
	 * Integer constant to signify <code>RED</code> color
	 */
	int RED_COLOR = 1;
	/**
	 * Integer constant to signify <code>BLUE</code> color
	 */
	int BLUE_COLOR = 2;
	/**
	 * Integer constant to signify <code>BLUE</code> color
	 */
	int GREEN_COLOR = 3;

	/**
	 * Authentication UserName parameter name
	 */
	String AUTH_USER = "Authentication-Username"; //$NON-NLS-1$
	/**
	 * Authentication Password parameter name
	 */
	String AUTH_PASSWORD = "Authentication-Password"; //$NON-NLS-1$
	/**
	 * Authentication Password parameter name
	 */
	String AUTH_DOMAIN = "Authentication-Domain"; //$NON-NLS-1$

	int NO_AUTHENTICATION = 0;
	int BASIC_AUTHENTICATION = 1;
	int DIGEST_AUTHENTICATION = 2;
	int NTLM_AUTHENTICATION = 3;

	int PROXY_SERVER_JS_BASED = 0;
	int PROXY_SERVER_HTTPCLIENT_BASED = 1;
}