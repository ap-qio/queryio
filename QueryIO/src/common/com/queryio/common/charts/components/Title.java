/*
 * @(#)  Title.java Feb 4, 2005
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
package com.queryio.common.charts.components;

import java.text.MessageFormat;

import com.queryio.common.IProductConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Point;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class Title {
	public static final int ORIENTATION_HORIZONTAL = 0;

	public static final int ORIENTATION_VERTICAL = 1;

	private String text;

	private int orientation = ORIENTATION_HORIZONTAL;

	public Font font;

	private Color textColor;

	private Color backgroundColor = null;

	private boolean showTitle = true;

	private Object[] args = null;

	public Title() {
		this(IProductConstants.EMPTY_STRING);
	}

	public Title(final String text) {
		this.text = text;
	}

	/**
	 * method getBackgroundColor
	 * 
	 * @return
	 */
	public final Color getBackgroundColor() {
		return this.backgroundColor;
	}

	/**
	 * method getFont
	 * 
	 * @return
	 */
	public final Font getFont() {
		return this.font;
	}

	/**
	 * method getOrientation
	 * 
	 * @return
	 */
	public final int getOrientation() {
		return this.orientation;
	}

	/**
	 * method getText
	 * 
	 * @return
	 */
	public final String getText() {
		return this.args == null ? this.text : MessageFormat.format(this.text, this.args);
	}

	/**
	 * method getTextColor
	 * 
	 * @return
	 */
	public final Color getTextColor() {
		return this.textColor;
	}

	/**
	 * method isLegend
	 * 
	 * @return
	 */
	public final boolean isTitle() {
		return this.showTitle;
	}

	/**
	 * method setBackgroundColor
	 * 
	 * @param color
	 */
	public void setBackgroundColor(final Color color) {
		this.backgroundColor = color;
	}

	/**
	 * method setFont
	 * 
	 * @param string
	 */
	public void setFont(final Font fnt) {
		this.font = fnt;
		this.maxHeight = -1;
	}

	/**
	 * method setOrientation
	 * 
	 * @param i
	 */
	public void setOrientation(final int i) {
		this.orientation = i;
	}

	/**
	 * method setText
	 * 
	 * @param string
	 */
	public void setText(final String string) {
		this.text = string;
	}

	/**
	 * method setTextColor
	 * 
	 * @param color
	 */
	public void setTextColor(final Color color) {
		this.textColor = color;
	}

	/**
	 * method showTitle
	 * 
	 * @param title
	 */
	public void showTitle(final boolean title) {
		this.showTitle = title;
	}

	private int maxHeight = -1;

	public int getMaxHeight(final UserInterface userInterface) {
		if (this.maxHeight == -1) {
			final Font oldFont = userInterface.getFont();
			userInterface.setFont(this.font);
			this.maxHeight = userInterface.getFontHeight() + 2;
			userInterface.setFont(oldFont);
		}
		return this.maxHeight;
	}

	public final void draw(final UserInterface graphics, final double angle) {
		final Rectangle r = graphics.getClipping();
		final String title = this.getText();
		graphics.setFont(this.font);
		graphics.setForeground(this.textColor);

		final Point point = graphics.stringExtent(title);
		final int width = point.x;
		final int height = point.y;
		int x;
		int y;
		if (this.orientation == ORIENTATION_VERTICAL) {
			x = r.x + (r.width - height) / 2;
			y = r.y + (r.height - width) / 2;
		} else {
			x = r.x + (r.width - width) / 2;
			y = r.y + (r.height - height) / 2;
		}
		Color bgColor = null;
		if (this.backgroundColor != null) {
			bgColor = graphics.getBackground();
			graphics.setBackground(this.backgroundColor);
			// graphics.fillRectangle(x, x + width, y, y + height);
		}
		graphics.setForeground(this.textColor);
		if (this.orientation == ORIENTATION_VERTICAL) {
			graphics.drawStringVertically(title, x, y);
		} else {
			graphics.drawString(title, x, y, true);
		}
		if (this.backgroundColor != null) {
			graphics.setBackground(bgColor);
		}
	}

	public final void setArguments(final String[] args) {
		this.args = args;
	}
}
