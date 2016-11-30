/**
 * 
 */
package com.queryio.common.exporter.dstruct;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;

/**
 * @author manoj
 * 
 */
public class ControlProperties implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8019425593504191656L;

	static final transient ResourceManager RM = CommonResourceManager.loadResources("Common_UICommon"); //$NON-NLS-1$

	public static final int DEFAULT_SIZE = 10;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 15;
	public static final int MIN_HEIGHT = 10;
	public static final String[] HORZ_ALIGNMENTS = new String[] { "Left", //$NON-NLS-1$
			"Center", //$NON-NLS-1$
			"Right", //$NON-NLS-1$
			"Justify" }; //$NON-NLS-1$
	public static final String[] VERT_ALIGNMENTS = new String[] { "Top", //$NON-NLS-1$
			"Middle", //$NON-NLS-1$
			"Bottom" }; //$NON-NLS-1$
	public static final String[] FONT_STYLES = new String[] { "Regular", //$NON-NLS-1$
			"Italic", //$NON-NLS-1$
			"Bold", //$NON-NLS-1$
			"Bold Italic" }; //$NON-NLS-1$
	public static final String[] FONT_SIZES = new String[] { "8", //$NON-NLS-1$
			"9", //$NON-NLS-1$
			"10", //$NON-NLS-1$
			"11", //$NON-NLS-1$
			"12", //$NON-NLS-1$
			"14", //$NON-NLS-1$
			"16", //$NON-NLS-1$
			"18", //$NON-NLS-1$
			"20", //$NON-NLS-1$
			"22" }; //$NON-NLS-1$

	protected boolean bCustomized;
	protected String sFontName;
	protected int iSize;
	protected boolean bBold;
	protected boolean bItalic;
	protected boolean bUnderline;
	protected boolean bStrikeThrough;

	protected byte bHorizontalAlignment;
	protected byte bVerticalAlignment;

	protected boolean bTransparent;
	protected Color foreColor;

	protected Color backColor;

	protected boolean bAlternateColor;
	protected Color alternateForeColor;
	protected Color alternateBackColor;

	protected int xPos = 0;
	protected int yPos = 0;

	private int iWidth;
	protected int iHeight;
	protected int iSection = -1;
	protected int xVal = Integer.MAX_VALUE;
	protected int yVal = Integer.MAX_VALUE;

	public int getSection() {
		return this.iSection;
	}

	public void setSection(final int iSection) {
		this.iSection = iSection;
	}

	public void setProperties(final ControlProperties control) {
		this.sFontName = control.sFontName;
		this.iSize = control.iSize;
		this.bBold = control.bBold;
		this.bItalic = control.bItalic;
		this.bUnderline = control.bUnderline;
		this.bStrikeThrough = control.bStrikeThrough;

		this.bTransparent = control.bTransparent;

		this.foreColor = control.foreColor;

		this.backColor = control.backColor;

		this.bAlternateColor = control.bAlternateColor;
		this.alternateForeColor = control.alternateForeColor;

		this.alternateBackColor = control.alternateBackColor;

		this.bHorizontalAlignment = control.bHorizontalAlignment;
		this.bVerticalAlignment = control.bVerticalAlignment;

		// iWidth = control.iWidth;
		this.setWidth(control.iWidth);
		this.iHeight = control.iHeight;
		this.bCustomized = true;
	}

	/**
	 * Default Constructor of the class CommonProperties(CommonProperties.java)
	 * 
	 */
	public ControlProperties() {
		this.sFontName = "Arial"; //$NON-NLS-1$
		this.iSize = DEFAULT_SIZE;
		this.bBold = false;
		this.bItalic = false;
		this.bUnderline = false;
		this.bStrikeThrough = false;

		this.foreColor = ChartConstants.COLOR_BLACK;
		this.bTransparent = true;
		this.backColor = ChartConstants.COLOR_WHITE;

		this.bAlternateColor = false;
		this.alternateForeColor = ChartConstants.COLOR_BLACK;
		this.alternateBackColor = ChartConstants.COLOR_WHITE;

		this.bHorizontalAlignment = 4; // HORIZONTAL_ALIGN_JUSTIFIED;
		this.bVerticalAlignment = 1; // VERTICAL_ALIGN_TOP;

		this.iWidth = DEFAULT_WIDTH;
		this.iHeight = DEFAULT_HEIGHT;
		this.bCustomized = false;
	}

	/**
	 * Constructor of the class Control(Control.java)
	 * 
	 */
	public ControlProperties(final ControlProperties control) {
		this.setProperties(control);
	}

	/*
	 * public void setSectionProperties(int iSection) { this.iSection =
	 * iSection; DefaultProperties defProps = DEFAULT_PROPERTIES; if (defProps
	 * != null && iSection != -1) { DefaultZoneProperties defZoneProps =
	 * defProps.getDefPropsForZone(IReportDesigner.
	 * REPORT_SECTIONS_DISPLAY_STRINGS[iSection]); sFontName =
	 * defZoneProps.getFontName(); iSize = defZoneProps.getFontSize(); bBold =
	 * defZoneProps.isBold(); bItalic = defZoneProps.isItalic(); bUnderline =
	 * defZoneProps.isUnderline(); bStrikeThrough =
	 * defZoneProps.isStrikeThrough();
	 * 
	 * foreColor = defZoneProps.getForeColor(); bTransparent =
	 * defZoneProps.isTransparent(); backColor = defZoneProps.getBackColor();
	 * 
	 * bAlternateColor = defZoneProps.isAlternateColor(); alternateForeColor =
	 * defZoneProps.getAlternateForeColor(); alternateBackColor =
	 * defZoneProps.getAlternateBackColor();
	 * 
	 * bHorizontalAlignment = defZoneProps.getHorizontalAlignment();
	 * bVerticalAlignment = defZoneProps.getVerticalAlignment();
	 * 
	 * //iWidth = DEFAULT_WIDTH; setWidth(DEFAULT_WIDTH); iHeight =
	 * DEFAULT_HEIGHT; bCustomized = false; } }
	 * 
	 * public ControlProperties(int iSection) { this(); this.iSection =
	 * iSection; DefaultProperties defProps = DEFAULT_PROPERTIES; if (defProps
	 * != null && iSection != -1) { DefaultZoneProperties defZoneProps =
	 * defProps.getDefPropsForZone(IReportDesigner.
	 * REPORT_SECTIONS_DISPLAY_STRINGS[iSection]); sFontName =
	 * defZoneProps.getFontName(); iSize = defZoneProps.getFontSize(); bBold =
	 * defZoneProps.isBold(); bItalic = defZoneProps.isItalic(); bUnderline =
	 * defZoneProps.isUnderline(); bStrikeThrough =
	 * defZoneProps.isStrikeThrough();
	 * 
	 * foreColor = defZoneProps.getForeColor(); bTransparent =
	 * defZoneProps.isTransparent(); backColor = defZoneProps.getBackColor();
	 * 
	 * bAlternateColor = defZoneProps.isAlternateColor(); alternateForeColor =
	 * defZoneProps.getAlternateForeColor(); alternateBackColor =
	 * defZoneProps.getAlternateBackColor();
	 * 
	 * bHorizontalAlignment = defZoneProps.getHorizontalAlignment();
	 * bVerticalAlignment = defZoneProps.getVerticalAlignment();
	 * 
	 * //iWidth = DEFAULT_WIDTH; setWidth(DEFAULT_WIDTH); iHeight =
	 * DEFAULT_HEIGHT; bCustomized = false; } else { Color bgColor = new
	 * Color(0, 0, 100); switch (iSection) { case
	 * IReportConstants.REPORT_HEADER: backColor = ChartConstants.COLOR_WHITE;
	 * foreColor = bgColor; iSize = 18; bBold = true; break; case
	 * IReportConstants.REPORT_FOOTER: backColor = ChartConstants.COLOR_WHITE;
	 * foreColor = bgColor; iSize = 14; bBold = true; break; case
	 * IReportConstants.PAGE_HEADER: bBold = true; backColor = bgColor;
	 * foreColor = ChartConstants.COLOR_WHITE; break; case
	 * IReportConstants.PAGE_FOOTER: bBold = true; backColor = bgColor;
	 * foreColor = ChartConstants.COLOR_WHITE; break; case
	 * IReportConstants.COLUMN_HEADER: bBold = true; backColor = bgColor;
	 * foreColor = ChartConstants.COLOR_WHITE; break; case
	 * IReportConstants.COLUMN_FOOTER: bBold = true; backColor = bgColor;
	 * foreColor = ChartConstants.COLOR_WHITE; break; case
	 * IReportConstants.DETAILS: bBold = false; backColor = new Color(242, 242,
	 * 227); alternateBackColor = ChartConstants.COLOR_WHITE; bAlternateColor =
	 * true; break; } bTransparent = false; bCustomized = false; } }
	 */
	/**
	 * Method setControlProperties
	 * 
	 * @param sProperties
	 */
	public void setControlProperties(final String sProperties) {
		final StringTokenizer strTok = new StringTokenizer(sProperties, "|"); //$NON-NLS-1$
		this.sFontName = strTok.nextToken();
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bBold = true;
		} else {
			this.bBold = false;
		}
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bItalic = true;
		} else {
			this.bItalic = false;
		}
		this.iSize = Integer.parseInt(strTok.nextToken());
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bUnderline = true;
		} else {
			this.bUnderline = false;
		}
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bStrikeThrough = true;
		} else {
			this.bStrikeThrough = false;
		}
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bTransparent = true;
		} else {
			this.bTransparent = false;
		}

		this.foreColor = Color.decode(strTok.nextToken());
		this.backColor = Color.decode(strTok.nextToken());

		// bHorizontalAlignment = Byte.parseByte(strTok.nextToken());
		// bVerticalAlignment = Byte.parseByte(strTok.nextToken());
		final String sHorizAlign = strTok.nextToken();
		if (HORZ_ALIGNMENTS[0].equals(sHorizAlign)) {
			this.bHorizontalAlignment = 1;// HORIZONTAL_ALIGN_LEFT;
		} else if (HORZ_ALIGNMENTS[1].equals(sHorizAlign)) {
			this.bHorizontalAlignment = 2;// HORIZONTAL_ALIGN_CENTER;
		} else if (HORZ_ALIGNMENTS[2].equals(sHorizAlign)) {
			this.bHorizontalAlignment = 3;// HORIZONTAL_ALIGN_RIGHT;
		} else {
			this.bHorizontalAlignment = 4;// HORIZONTAL_ALIGN_JUSTIFIED;
		}

		final String sVerticalAlign = strTok.nextToken();
		if (VERT_ALIGNMENTS[0].equals(sVerticalAlign)) {
			this.bVerticalAlignment = 1;// VERTICAL_ALIGN_TOP;
		} else if (VERT_ALIGNMENTS[1].equals(sVerticalAlign)) {
			this.bVerticalAlignment = 2;// VERTICAL_ALIGN_MIDDLE;
		} else {
			this.bVerticalAlignment = 3;// VERTICAL_ALIGN_BOTTOM;
		}

		// iWidth = Integer.parseInt(strTok.nextToken());
		this.setWidth(Integer.parseInt(strTok.nextToken()));
		this.iHeight = Integer.parseInt(strTok.nextToken());
		this.xPos = Integer.parseInt(strTok.nextToken());
		this.yPos = Integer.parseInt(strTok.nextToken());
		if (strTok.nextToken().equals("true")) //$NON-NLS-1$
		{
			this.bAlternateColor = true;
		} else {
			this.bAlternateColor = false;
		}

		this.alternateForeColor = Color.decode(strTok.nextToken());
		this.alternateBackColor = Color.decode(strTok.nextToken());
	}

	/**
	 * Method getPropertiesAsString
	 * 
	 * @return String - all the properties concatenated into a single string
	 *         separated by '|'
	 */
	public String getPropertiesAsString() {
		final StringBuffer sbProps = new StringBuffer(this.sFontName);
		sbProps.append('|');
		sbProps.append(this.bBold);
		sbProps.append('|');
		sbProps.append(this.bItalic);
		sbProps.append('|');
		sbProps.append(this.iSize);
		sbProps.append('|');
		sbProps.append(this.bUnderline);
		sbProps.append('|');
		sbProps.append(this.bStrikeThrough);
		sbProps.append('|');
		sbProps.append(this.bTransparent);
		sbProps.append('|');
		sbProps.append(Color.toAWTColorString(this.foreColor));
		sbProps.append('|');
		sbProps.append(Color.toAWTColorString(this.backColor));
		sbProps.append('|');
		if (((this.bHorizontalAlignment - 1) >= 0) && ((this.bHorizontalAlignment - 1) < HORZ_ALIGNMENTS.length)) {
			sbProps.append(HORZ_ALIGNMENTS[this.bHorizontalAlignment - 1]);
		} else {
			sbProps.append(this.bHorizontalAlignment); // should never happen
		}
		sbProps.append('|');
		if (((this.bVerticalAlignment - 1) >= 0) && ((this.bVerticalAlignment - 1) < VERT_ALIGNMENTS.length)) {
			sbProps.append(VERT_ALIGNMENTS[this.bVerticalAlignment - 1]);
		} else {
			sbProps.append(this.bVerticalAlignment);
		}
		sbProps.append('|');
		sbProps.append(this.iWidth);
		sbProps.append('|');
		sbProps.append(this.iHeight);
		sbProps.append('|');
		sbProps.append(this.xPos);
		sbProps.append('|');
		sbProps.append(this.yPos);
		sbProps.append('|');
		sbProps.append(this.bAlternateColor);
		sbProps.append('|');
		sbProps.append(Color.toAWTColorString(this.alternateForeColor));
		sbProps.append('|');
		sbProps.append(Color.toAWTColorString(this.alternateBackColor));

		return sbProps.toString();
	}

	/**
	 * getBackColor
	 * 
	 * @return Color
	 */
	public final Color getBackColor() {
		return this.backColor;
	}

	/**
	 * isBold
	 * 
	 * @return boolean
	 */
	public final boolean isBold() {
		return this.bBold;
	}

	/**
	 * isItalic
	 * 
	 * @return boolean
	 */
	public final boolean isItalic() {
		return this.bItalic;
	}

	/**
	 * isStrikeThrough
	 * 
	 * @return boolean
	 */
	public final boolean isStrikeThrough() {
		return this.bStrikeThrough;
	}

	/**
	 * isUnderline
	 * 
	 * @return boolean
	 */
	public final boolean isUnderline() {
		return this.bUnderline;
	}

	/**
	 * getForeColor
	 * 
	 * @return Color
	 */
	public final Color getForeColor() {
		return this.foreColor;
	}

	/**
	 * getHeight
	 * 
	 * @return int
	 */
	public int getHeight() {
		if ((this.iSize + 4) > this.iHeight) {
			this.iHeight = this.iSize + 4;
		}
		return this.iHeight;
	}

	/**
	 * getSize
	 * 
	 * @return int
	 */
	public final int getSize() {
		return this.iSize;
	}

	public final int getFontSize() {
		return this.iSize;
	}

	/**
	 * getWidth
	 * 
	 * @return int
	 */
	public int getWidth() {
		return this.iWidth;
	}

	/**
	 * getFontName
	 * 
	 * @return String
	 */
	public final String getFontName() {
		return this.sFontName;
	}

	/**
	 * getHorizontalAlignment
	 * 
	 * @return byte
	 */
	public final byte getHorizontalAlignment() {
		return this.bHorizontalAlignment;
	}

	/**
	 * getVerticalAlignment
	 * 
	 * @return byte
	 */
	public final byte getVerticalAlignment() {
		return this.bVerticalAlignment;
	}

	/**
	 * setBackColor
	 * 
	 * @param bColor
	 *            void
	 */
	public void setBackColor(final Color bColor) {
		this.backColor = bColor;
		this.bCustomized = true;
	}

	/**
	 * setBold
	 * 
	 * @param bB
	 *            void
	 */
	public void setBold(final boolean bB) {
		this.bBold = bB;
		this.bCustomized = true;
	}

	/**
	 * setItalic
	 * 
	 * @param bB
	 *            void
	 */
	public void setItalic(final boolean bB) {
		this.bItalic = bB;
		this.bCustomized = true;
	}

	/**
	 * setStrikeThrough
	 * 
	 * @param bB
	 *            void
	 */
	public void setStrikeThrough(final boolean bB) {
		this.bStrikeThrough = bB;
		this.bCustomized = true;
	}

	/**
	 * setUnderline
	 * 
	 * @param bB
	 *            void
	 */
	public void setUnderline(final boolean bB) {
		this.bUnderline = bB;
		this.bCustomized = true;
	}

	/**
	 * setForeColor
	 * 
	 * @param bColor
	 *            void
	 */
	public void setForeColor(final Color bColor) {
		this.foreColor = bColor;
		this.bCustomized = true;
	}

	/**
	 * setHeight
	 * 
	 * @param bI
	 *            void
	 */
	public void setHeight(final int bI) {
		this.iHeight = bI;
	}

	/**
	 * setSize
	 * 
	 * @param bI
	 *            void
	 */
	public void setSize(final int bI) {
		this.iSize = bI;
		this.bCustomized = true;
	}

	/**
	 * setWidth
	 * 
	 * @param bI
	 *            void
	 */
	public void setWidth(final int bI) {
		this.iWidth = bI;
	}

	/**
	 * setFontName
	 * 
	 * @param bString
	 *            void
	 */
	public void setFontName(final String bString) {
		this.sFontName = bString;
		this.bCustomized = true;
	}

	/**
	 * setHorizontalAlignment
	 * 
	 * @param bAlign
	 * @return void
	 */
	public void setHorizontalAlignment(final byte bAlign) {
		this.bHorizontalAlignment = bAlign;
		this.bCustomized = true;
	}

	/**
	 * setVerticalAlignment
	 * 
	 * @param bAlign
	 * @return void
	 */
	public void setVerticalAlignment(final byte bAlign) {
		this.bVerticalAlignment = bAlign;
		this.bCustomized = true;
	}

	/**
	 * getXPos
	 * 
	 * @return int
	 */
	public final int getXPos() {
		return this.xPos;
	}

	/**
	 * getYPos
	 * 
	 * @return int
	 */
	public final int getYPos() {
		return this.yPos;
	}

	/**
	 * setXPos
	 * 
	 * @param bI
	 *            void
	 */
	public void setXPos(final int bI) {
		this.xPos = bI;
	}

	/**
	 * setYPos
	 * 
	 * @param bI
	 *            void
	 */
	public void setYPos(final int bI) {
		this.yPos = bI;
	}

	/**
	 * getAlternateBackColor
	 * 
	 * @return Color
	 */
	public final Color getAlternateBackColor() {
		return this.alternateBackColor;
	}

	/**
	 * getAlternateForeColor
	 * 
	 * @return Color
	 */
	public final Color getAlternateForeColor() {
		return this.alternateForeColor;
	}

	/**
	 * isAlternateColor
	 * 
	 * @return boolean
	 */
	public final boolean isAlternateColor() {
		return this.bAlternateColor;
	}

	/**
	 * setAlternateBackColor
	 * 
	 * @param bColor
	 *            void
	 */
	public void setAlternateBackColor(final Color bColor) {
		this.alternateBackColor = bColor;
		this.bCustomized = true;
	}

	/**
	 * setAlternateForeColor
	 * 
	 * @param bColor
	 *            void
	 */
	public void setAlternateForeColor(final Color bColor) {
		this.alternateForeColor = bColor;
		this.bCustomized = true;
	}

	/**
	 * setAlternateColor
	 * 
	 * @param bB
	 *            void
	 */
	public void setAlternateColor(final boolean bB) {
		this.bAlternateColor = bB;
		this.bCustomized = true;
	}

	/**
	 * isTransparent
	 * 
	 * @return boolean
	 */
	public final boolean isTransparent() {
		return this.bTransparent;
	}

	/**
	 * setTransparent
	 * 
	 * @param bB
	 *            void
	 */
	public void setTransparent(final boolean bB) {
		this.bTransparent = bB;
		this.bCustomized = true;
	}

	/**
	 * getMode
	 * 
	 * @return
	 */
	public byte getMode() {
		if (this.isTransparent()) {
			return 2;// MODE_TRANSPARENT;
		}
		return 1;// MODE_OPAQUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbToString = new StringBuffer(RM.getString("VALUE_COMMON_PROPERTIES")); //$NON-NLS-1$
		sbToString.append(RM.getString("VALUE_FONT_NAME")); //$NON-NLS-1$
		sbToString.append(this.sFontName);
		sbToString.append(RM.getString("VALUE_SIZE")); //$NON-NLS-1$
		sbToString.append(this.iSize);
		sbToString.append(RM.getString("VALUE_BOLD")); //$NON-NLS-1$
		sbToString.append(this.bBold);
		sbToString.append(RM.getString("VALUE_ITALIC")); //$NON-NLS-1$
		sbToString.append(this.bItalic);
		sbToString.append(RM.getString("VALUE_UNDERLINE")); //$NON-NLS-1$
		sbToString.append(this.bUnderline);
		sbToString.append(RM.getString("VALUE_STRIKE_THROUGH")); //$NON-NLS-1$
		sbToString.append(this.bStrikeThrough);
		sbToString.append(RM.getString("VALUE_HORIZONTAL")); //$NON-NLS-1$
		sbToString.append(this.bHorizontalAlignment);
		sbToString.append(RM.getString("VALUE_VERTICAL")); //$NON-NLS-1$
		sbToString.append(this.bVerticalAlignment);
		sbToString.append(RM.getString("VALUE_FORE_COLOR")); //$NON-NLS-1$
		sbToString.append(this.foreColor);
		sbToString.append(RM.getString("VALUE_TRANSPARENT")); //$NON-NLS-1$
		sbToString.append(this.bTransparent);
		sbToString.append(RM.getString("VALUE_BACK_COLOR")); //$NON-NLS-1$
		sbToString.append(this.backColor);
		sbToString.append(RM.getString("VALUE_ALTERNATE_COLOR")); //$NON-NLS-1$
		sbToString.append(this.bAlternateColor);
		sbToString.append(RM.getString("VALUE_ALTERNATE_FORE_COLOR")); //$NON-NLS-1$
		sbToString.append(this.alternateForeColor);
		sbToString.append(RM.getString("VALUE_ALTERNATE_BACK_COLOR")); //$NON-NLS-1$
		sbToString.append(this.alternateBackColor);
		sbToString.append(RM.getString("VALUE_XPOS")); //$NON-NLS-1$
		sbToString.append(this.xPos);
		sbToString.append(RM.getString("VALUE_YPOS")); //$NON-NLS-1$
		sbToString.append(this.yPos);
		sbToString.append(RM.getString("VALUE_WIDTH")); //$NON-NLS-1$
		sbToString.append(this.iWidth);
		sbToString.append(RM.getString("VALUE_HEIGHT")); //$NON-NLS-1$
		sbToString.append(this.iHeight);

		return sbToString.toString();
	}

	public String getDisplayString() {
		return RM.getString("VALUE_CONTROL"); //$NON-NLS-1$
	}

	public final boolean isCustomized() {
		return this.bCustomized;
	}

	public final Color getBackground() {
		return this.backColor;
	}

	public final Color getForeground() {
		return this.foreColor;
	}

	public final int getY() {
		return this.yVal;
	}

	public void setY(final int position) {
		this.yVal = position;
	}

	public final int getX() {
		return this.xVal;
	}

	public void setX(final int position) {
		this.xVal = position;
	}

	// public static void setDefaultProperties(DefaultProperties defProps)
	// {
	// DEFAULT_PROPERTIES = defProps;
	// }

	/**
	 * For JS DWR
	 * 
	 * @return
	 */
	public String getTextProperties() {
		return this.getPropertiesAsString();
	}

	/**
	 * For JS DWR
	 * 
	 * @return
	 */
	public void setTextProperties(final String properties) {
		if ((properties != null) && (properties.length() > 0)) {
			this.setControlProperties(properties);
		}
	}
}
