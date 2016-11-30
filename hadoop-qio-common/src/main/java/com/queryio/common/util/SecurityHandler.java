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

/**
 * @author Exceed Consultancy Services
 * 
 */
public class SecurityHandler {
	private static CryptManager handler = null;
	static {
		try {
			handler = CryptManager.createInstance("q12sdfsj3vsdfopj8793009@bk^hjls%^337#");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}

	public static String encryptData(String data) throws Exception {
		return handler.encryptData(data);
	}

	public static String decryptData(String data) throws Exception {
		return handler.decryptData(data);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(decryptData("AloqDmWyK/rX2e/8fgqxAw=="));
	}
}
