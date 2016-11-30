/*
 * @(#)  UIGraphicsFactory.java Feb 7, 2005
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
package com.queryio.common.charts.util;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.util.AppLogger;

/**
 *
 * @author Exceed Consultancy Services
 */
public abstract class UIGraphicsFactory {
	private static final String USER_INTERFACE_SWT_CLASS = "com.queryio.common.ui.charts.swtgraphics.SWTUserInterface"; //$NON-NLS-1$
	private static final String USER_INTERFACE_SWING_CLASS = "com.queryio.common.charts.swinggraphics.SWINGUserInterface"; //$NON-NLS-1$

	private static ClassLoader userInterfaceClassLoader = null;

	public static UserInterface getUserInterface(final int type) {
		UserInterface userInterface = null;
		Class userInterfaceClass = null;
		try {
			switch (type) {
			case IProductConstants.USER_INTERFACE_SWT: {
				userInterfaceClass = userInterfaceClassLoader.loadClass(USER_INTERFACE_SWT_CLASS);
				break;
			}
			case IProductConstants.USER_INTERFACE_SWING: {
				userInterfaceClass = userInterfaceClassLoader.loadClass(USER_INTERFACE_SWING_CLASS);
				break;
			}
			}
		} catch (final ClassNotFoundException cnfe) {
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), cnfe); // $NON-NLS-1$
		}

		if (userInterfaceClass != null) {
			try {
				userInterface = (UserInterface) userInterfaceClass.newInstance();
			} catch (final InstantiationException e) {
				AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
			} catch (final IllegalAccessException e) {
				AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
			}
		}
		return userInterface;
	}

	public static void setUserInterfaceClassLoader(final ClassLoader userInterfaceClassLoader) {
		UIGraphicsFactory.userInterfaceClassLoader = userInterfaceClassLoader;
	}

}
