package com.queryio.common.report;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.DataPointComponentType;
import org.eclipse.birt.chart.model.attribute.HorizontalAlignment;
import org.eclipse.birt.chart.model.attribute.Insets;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.Stretch;
import org.eclipse.birt.chart.model.attribute.TextAlignment;
import org.eclipse.birt.chart.model.attribute.VerticalAlignment;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointComponentImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.InsetsImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaNumberFormatSpecifierImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.layout.Legend;
import org.json.simple.JSONObject;

public class ChartPreferenceHandler {

	public static void setXAxisProperties(Axis axis, JSONObject properties, String axisTitle) {

		if (properties != null && properties.size() > 0) {

			Boolean visible = (Boolean) properties.get("visible");
			if (visible) {
				String position = (String) properties.get("position");
				if (position.equalsIgnoreCase("above")) {
					axis.setLabelPosition(Position.ABOVE_LITERAL);
				} else if (position.equalsIgnoreCase("below")) {
					axis.setLabelPosition(Position.BELOW_LITERAL);
				}
				String xAxisLblBackground = (String) properties.get("background");
				String xAxisLblShadow = (String) properties.get("shadow");
				Label xAxisLbl = axis.getLabel();
				axis.getLineAttributes().setColor(hexToRGB(xAxisLblBackground));
				xAxisLbl.setVisible(true);
				xAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(xAxisLblBackground));
				xAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(xAxisLblShadow));
				JSONObject xAxisLblInsets = (JSONObject) properties.get("insets");
				if (xAxisLblInsets != null && xAxisLblInsets.size() > 0) {
					int xAxisLblInsetTop = Integer.parseInt((String) xAxisLblInsets.get("top"));
					int xAxisLblInsetBottom = Integer.parseInt((String) xAxisLblInsets.get("bottom"));
					int xAxisLblInsetLeft = Integer.parseInt((String) xAxisLblInsets.get("left"));
					int xAxisLblInsetRight = Integer.parseInt((String) xAxisLblInsets.get("right"));
					xAxisLbl.setInsets(InsetsImpl.create(xAxisLblInsetTop, xAxisLblInsetLeft, xAxisLblInsetBottom,
							xAxisLblInsetRight));
				}
				JSONObject xAxisLblOutLine = (JSONObject) properties.get("outline");
				if (xAxisLblOutLine != null && xAxisLblOutLine.size() > 0) {
					Boolean isxAxisLblOutLineVisible = (Boolean) xAxisLblOutLine.get("visible");
					if (isxAxisLblOutLineVisible) {
						int xAxisLblOutlineWidthInt = Integer.parseInt((String) xAxisLblOutLine.get("width"));
						String xAxisLblOutlineColor = (String) xAxisLblOutLine.get("color");
						xAxisLbl.setOutline(
								LineAttributesImpl.create(ChartPreferenceHandler.hexToRGB(xAxisLblOutlineColor),
										ChartPreferenceHandler.getLineStyleType((String) xAxisLblOutLine.get("style")),
										xAxisLblOutlineWidthInt));
					}
				}

				String xAxisLblFontColor = (String) properties.get("color");
				String sName = (String) properties.get("font-family");
				float fSize = Float.parseFloat((String) properties.get("font-size"));
				String xAxisLblFontStyle = (String) properties.get("font-style");
				String xAxisLblTextAlign = (String) properties.get("text-align");
				String xAxisTitleFontColor = (String) properties.get("titleColor");
				float xAxisTitleFontSize = Float.parseFloat((String) properties.get("titleFontSize"));

				boolean bBold = false;
				boolean bItalic = false;
				boolean bUnderline = false;
				boolean bStrikethrough = false;
				boolean bWordWrap = false;
				if (xAxisLblFontStyle.equalsIgnoreCase("bold"))
					bBold = true;
				xAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisLblFontColor));
				xAxisLbl.getCaption()
						.setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough,
								bWordWrap, 0, ChartPreferenceHandler.getTextAlignment(xAxisLblTextAlign, null)));
				axis.setLabel(xAxisLbl);
				axis.getTitle().getCaption().setValue(axisTitle);
				axis.getTitle().setVisible(true);

				axis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(xAxisTitleFontColor));
				axis.getTitle().getCaption().getFont().setName(sName);
				axis.getTitle().getCaption().getFont().setBold(true);
				axis.getTitle().getCaption().getFont().setSize(xAxisTitleFontSize);

				axis.getTitle().getCaption().getFont().setRotation(0);

			}
			axis.getLabel().setVisible(visible);
			JSONObject xAxisGridProperties = (JSONObject) properties.get("gridline");
			if (xAxisGridProperties != null && xAxisGridProperties.size() > 0) {
				Boolean isYAxisGridLineVisible = (Boolean) xAxisGridProperties.get("visible");
				if (isYAxisGridLineVisible) {
					int yAxisGridLineWidthInt = Integer.parseInt((String) xAxisGridProperties.get("width"));
					String yAxisGridLineColor = (String) xAxisGridProperties.get("color");
					axis.getMajorGrid().getLineAttributes()
							.setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor));
					axis.getMajorGrid().getLineAttributes().setStyle(
							ChartPreferenceHandler.getLineStyleType((String) xAxisGridProperties.get("style")));
					axis.getMajorGrid().getLineAttributes().setThickness(yAxisGridLineWidthInt);
					if (xAxisGridProperties.containsKey("gridstep")) {
						int xAxisGridStep = Integer.parseInt((String) xAxisGridProperties.get("gridstep"));
						axis.getScale().setMajorGridsStepNumber(xAxisGridStep);
					}
				}
				axis.getMajorGrid().getLineAttributes().setVisible(isYAxisGridLineVisible);
			}
		}

	}

	public static void setYAxisProperties(Axis axis, JSONObject properties, String axisTitle) {

		if (properties != null && properties.size() > 0) {
			Boolean visible = (Boolean) properties.get("visible");
			if (visible) {
				String position = (String) properties.get("position");
				if (position.equalsIgnoreCase("right")) {
					axis.setLabelPosition(Position.RIGHT_LITERAL);
				} else if (position.equalsIgnoreCase("left")) {
					axis.setLabelPosition(Position.LEFT_LITERAL);
				}
				String yAxisLblBackground = (String) properties.get("background");
				String yAxisLblShadow = (String) properties.get("shadow");
				Label yAxisLbl = axis.getLabel();
				yAxisLbl.setVisible(true);

				axis.getLineAttributes().setColor(hexToRGB(yAxisLblBackground));
				yAxisLbl.setBackground(ChartPreferenceHandler.hexToRGB(yAxisLblBackground));
				yAxisLbl.setShadowColor(ChartPreferenceHandler.hexToRGB(yAxisLblShadow));
				JSONObject yAxisLblInsets = (JSONObject) properties.get("insets");
				if (yAxisLblInsets != null && yAxisLblInsets.size() > 0) {
					int yAxisLblInsetTop = Integer.parseInt((String) yAxisLblInsets.get("top"));
					int yAxisLblInsetBottom = Integer.parseInt((String) yAxisLblInsets.get("bottom"));
					int yAxisLblInsetLeft = Integer.parseInt((String) yAxisLblInsets.get("left"));
					int yAxisLblInsetRight = Integer.parseInt((String) yAxisLblInsets.get("right"));
					yAxisLbl.setInsets(InsetsImpl.create(yAxisLblInsetTop, yAxisLblInsetLeft, yAxisLblInsetBottom,
							yAxisLblInsetRight));
				}
				JSONObject yAxisLblOutLine = (JSONObject) properties.get("outline");
				if (yAxisLblOutLine != null && yAxisLblOutLine.size() > 0) {
					Boolean isYAxisLblOutLineVisible = (Boolean) yAxisLblOutLine.get("visible");
					if (isYAxisLblOutLineVisible) {
						int yAxisLblOutlineWidthInt = Integer.parseInt((String) yAxisLblOutLine.get("width"));
						String yAxisLblOutlineColor = (String) yAxisLblOutLine.get("color");
						yAxisLbl.setOutline(
								LineAttributesImpl.create(ChartPreferenceHandler.hexToRGB(yAxisLblOutlineColor),
										ChartPreferenceHandler.getLineStyleType((String) yAxisLblOutLine.get("style")),
										yAxisLblOutlineWidthInt));
					}
				}
				String yAxisLblFontColor = (String) properties.get("color");
				String sName = (String) properties.get("font-family");
				float fSize = Float.parseFloat((String) properties.get("font-size"));
				String yAxisLblFontStyle = (String) properties.get("font-style");
				String yAxisLblTextAlign = (String) properties.get("text-align");
				String yAxisTitleFontColor = (String) properties.get("titleColor");
				float yAxisTitleFontSize = Float.parseFloat((String) properties.get("titleFontSize"));

				boolean bBold = false;
				boolean bItalic = false;
				boolean bUnderline = false;
				boolean bStrikethrough = false;
				boolean bWordWrap = false;
				if (yAxisLblFontStyle.equalsIgnoreCase("bold"))
					bBold = true;
				yAxisLbl.getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisLblFontColor));
				yAxisLbl.getCaption()
						.setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline, bStrikethrough,
								bWordWrap, 0, ChartPreferenceHandler.getTextAlignment(yAxisLblTextAlign, null)));
				axis.setLabel(yAxisLbl);
				axis.getTitle().getCaption().setValue(axisTitle);

				axis.getTitle().getCaption().setColor(ChartPreferenceHandler.hexToRGB(yAxisTitleFontColor));
				axis.getTitle().getCaption().getFont().setName(sName);
				axis.getTitle().getCaption().getFont().setBold(true);
				axis.getTitle().getCaption().getFont().setSize(yAxisTitleFontSize);
				axis.getTitle().getCaption().getFont().setRotation(90);
			}
			JSONObject yAxisGridProperties = (JSONObject) properties.get("gridline");
			if (yAxisGridProperties != null && yAxisGridProperties.size() > 0) {
				Boolean isYAxisGridLineVisible = (Boolean) yAxisGridProperties.get("visible");
				if (isYAxisGridLineVisible) {
					int yAxisGridLineWidthInt = Integer.parseInt((String) yAxisGridProperties.get("width"));
					String yAxisGridLineColor = (String) yAxisGridProperties.get("color");
					axis.getMajorGrid().getLineAttributes()
							.setColor(ChartPreferenceHandler.hexToRGB(yAxisGridLineColor));
					axis.getMajorGrid().getLineAttributes().setStyle(
							ChartPreferenceHandler.getLineStyleType((String) yAxisGridProperties.get("style")));
					axis.getMajorGrid().getLineAttributes().setThickness(yAxisGridLineWidthInt);
					if (yAxisGridProperties.containsKey("gridstep")) {
						int yAxisGridStep = Integer.parseInt((String) yAxisGridProperties.get("gridstep"));
						axis.getScale().setMajorGridsStepNumber(yAxisGridStep);
					}
				}
				axis.getMajorGrid().getLineAttributes().setVisible(isYAxisGridLineVisible);
			}
			axis.getLabel().setVisible(visible);

		}

	}

	public static void setTitleProperties(JSONObject titleJSON, Chart chart, String title) {
		if (titleJSON != null && titleJSON.size() > 0) {
			/* Title */
			chart.getTitle().getLabel().getCaption().setValue(title);
			float titleFontSize = Float.parseFloat((String) titleJSON.get("font-size"));
			String titleFontStyle = (String) titleJSON.get("font-style");
			String titleFontFamily = (String) titleJSON.get("font-family");
			String titleAnchor = (String) titleJSON.get("anchor");
			String titleTextAlign = (String) titleJSON.get("text-align");
			String titleColor = (String) titleJSON.get("color");
			String titleBackground = (String) titleJSON.get("background");
			chart.getTitle().setAnchor(getAnchorType(titleAnchor));

			JSONObject titleInsets = (JSONObject) titleJSON.get("insets");
			int titleInsetTop = Integer.parseInt((String) titleInsets.get("top"));
			int titleInsetBottom = Integer.parseInt((String) titleInsets.get("bottom"));
			int titleInsetLeft = Integer.parseInt((String) titleInsets.get("left"));
			int titleInsetRight = Integer.parseInt((String) titleInsets.get("right"));

			boolean bBold = false;
			boolean bItalic = false;
			boolean bUnderline = false;
			boolean bStrikethrough = false;
			boolean bWordWrap = false;
			if (titleFontStyle.equalsIgnoreCase("bold"))
				bBold = true;

			chart.getTitle().getLabel().getCaption().setFont(FontDefinitionImpl.create(titleFontFamily, titleFontSize,
					bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0, getTextAlignment(titleTextAlign, null)));
			chart.getTitle().getLabel().getCaption().setColor(hexToRGB(titleColor));
			chart.getTitle().setBackground(ColorDefinitionImpl.TRANSPARENT());
			chart.getTitle().setStretch(Stretch.HORIZONTAL_LITERAL);
			if ("left".equalsIgnoreCase(titleTextAlign)) {
				chart.getTitle().setAnchor(Anchor.get(Anchor.NORTH_WEST));
			} else if ("right".equalsIgnoreCase(titleTextAlign)) {
				chart.getTitle().setAnchor(Anchor.get(Anchor.NORTH_EAST));
			}

			chart.getTitle().getLabel().setVisible(true);
			chart.getTitle().setVisible(true);

			Insets inset = InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight); // setting
																												// Inset
			chart.getTitle().setInsets(inset);
			// font-style and text-align remains .//TODO
			JSONObject titleOutline = (JSONObject) titleJSON.get("outline");
			Boolean titleOutlineVisible = (Boolean) titleOutline.get("visible");
			// Set Title OutLine .
			if (titleOutlineVisible) {
				int titleOutlineWidthInt = Integer.parseInt((String) titleOutline.get("width"));
				String titleOutlineColor = (String) titleOutline.get("color");
				chart.getTitle().setOutline(LineAttributesImpl.create(hexToRGB(titleOutlineColor),
						getLineStyleType((String) titleOutline.get("style")), titleOutlineWidthInt));
			}

		}
	}

	public static void setLabelProperties(JSONObject labelProperties, Series series, boolean isChartWithAxis,
			int dimension) {
		if (labelProperties != null && labelProperties.size() > 0) {

			Boolean visible = (Boolean) labelProperties.get("visible");
			if (visible) {

				String position = (String) labelProperties.get("position");
				if (isChartWithAxis) {
					if (position.equalsIgnoreCase("inside")) {
						series.setLabelPosition(Position.RIGHT_LITERAL);
					} else if (position.equalsIgnoreCase("outside")) {
						series.setLabelPosition(Position.LEFT_LITERAL);
					}
				} else {
					if ("inside".equalsIgnoreCase(position) && dimension != ChartDimension.THREE_DIMENSIONAL) {
						series.setLabelPosition(Position.INSIDE_LITERAL);
					} else if ("outside".equalsIgnoreCase(position)) {
						series.setLabelPosition(Position.OUTSIDE_LITERAL);
					}
				}
				String lblbackground = (String) labelProperties.get("background");
				String lblshadow = (String) labelProperties.get("shadow");
				Label lbl = series.getLabel();
				lbl.setVisible(true);
				lbl.setBackground(hexToRGB(lblbackground));
				lbl.setShadowColor(hexToRGB(lblshadow));
				JSONObject lblinsets = (JSONObject) labelProperties.get("insets");
				if (lblinsets != null && lblinsets.size() > 0) {
					int lblInsetTop = Integer.parseInt((String) lblinsets.get("top"));
					int lblInsetBottom = Integer.parseInt((String) lblinsets.get("bottom"));
					int lblInsetLeft = Integer.parseInt((String) lblinsets.get("left"));
					int lblInsetRight = Integer.parseInt((String) lblinsets.get("right"));
					lbl.setInsets(InsetsImpl.create(lblInsetTop, lblInsetLeft, lblInsetBottom, lblInsetRight));
				}
				JSONObject lblOutLine = (JSONObject) labelProperties.get("outline");
				if (lblOutLine != null && lblOutLine.size() > 0) {
					Boolean isLblOutLineVisible = (Boolean) lblOutLine.get("visible");
					if (isLblOutLineVisible) {
						int lblOutlineWidthInt = Integer.parseInt((String) lblOutLine.get("width"));
						String lblOutlineColor = (String) lblOutLine.get("color");
						lbl.setOutline(LineAttributesImpl.create(hexToRGB(lblOutlineColor),
								getLineStyleType((String) lblOutLine.get("style")), lblOutlineWidthInt));
					}
				}

				String lblFontColor = (String) labelProperties.get("font-color");
				String sName = (String) labelProperties.get("font-family");
				float fSize = Float.parseFloat((String) labelProperties.get("font-size"));
				String lblFontStyle = (String) labelProperties.get("font-style");
				String lblTextAlign = (String) labelProperties.get("text-align");

				boolean bBold = false;
				boolean bItalic = false;
				boolean bUnderline = false;
				boolean bStrikethrough = false;
				boolean bWordWrap = false;
				if (lblFontStyle.equalsIgnoreCase("bold"))
					bBold = true;
				lbl.getCaption().setColor(hexToRGB(lblFontColor));
				lbl.getCaption().setFont(FontDefinitionImpl.create(sName, fSize, bBold, bItalic, bUnderline,
						bStrikethrough, bWordWrap, 0, getTextAlignment(lblTextAlign, null)));
			}
			series.getLabel().setVisible(visible);
			String values = (String) labelProperties.get("values");
			String suffix = (String) labelProperties.get("suffix");
			String prefix = (String) labelProperties.get("prefix");
			String separator = (String) labelProperties.get("separator");
			if (("percent").equalsIgnoreCase(values)) {
				series.getDataPoint().getComponents().clear();
				series.getDataPoint().getComponents()
						.add(DataPointComponentImpl.create(DataPointComponentType.PERCENTILE_ORTHOGONAL_VALUE_LITERAL,
								JavaNumberFormatSpecifierImpl.create("##.##%")));
			}
			series.getDataPoint().setPrefix(prefix);
			series.getDataPoint().setSuffix(suffix);
			series.getDataPoint().setSeparator(separator);
		}
	}

	public static void setLegendProperties(JSONObject legendProperties, Legend legend, String legendTitleValue) {
		if (legendProperties != null && legendProperties.size() > 0) {
			legend.setMaxPercent(1);
			Boolean visible = (Boolean) legendProperties.get("visible");
			if (visible) {
				legend.setBackground(hexToRGB((String) legendProperties.get("background")));
				String position = (String) legendProperties.get("position");

				if (position.equals("left")) {
					legend.setPosition(Position.LEFT_LITERAL);
					legend.setTitlePosition(Position.LEFT_LITERAL);
				} else if (position.equals("above")) {
					legend.setPosition(Position.ABOVE_LITERAL);
					legend.setTitlePosition(Position.ABOVE_LITERAL);
					legend.setOrientation(Orientation.HORIZONTAL_LITERAL);
				} else if (position.equals("below")) {
					legend.setPosition(Position.BELOW_LITERAL);
					legend.setTitlePosition(Position.BELOW_LITERAL);
					legend.setOrientation(Orientation.HORIZONTAL_LITERAL);
				} else if (position.equals("inside")) {
					legend.setPosition(Position.INSIDE_LITERAL);
					legend.setTitlePosition(Position.INSIDE_LITERAL);
				} else {
					legend.setPosition(Position.RIGHT_LITERAL);
					legend.setTitlePosition(Position.RIGHT_LITERAL);
				}
				String anchor = (String) legendProperties.get("anchor");
				legend.setAnchor(getAnchorType(anchor));
				String stretch = (String) legendProperties.get("stretch");
				if (("horizontal").equals(stretch)) {
					legend.setStretch(Stretch.HORIZONTAL_LITERAL);
				} else if (("vertical").equals(stretch)) {
					legend.setStretch(Stretch.VERTICAL_LITERAL);
				} else {
					legend.setStretch(Stretch.BOTH_LITERAL);
				}

				JSONObject legendinsets = (JSONObject) legendProperties.get("insets");
				if (legendinsets != null && legendinsets.size() > 0) {
					int legendInsetTop = Integer.parseInt((String) legendinsets.get("top"));
					int legendInsetBottom = Integer.parseInt((String) legendinsets.get("bottom"));
					int legendInsetLeft = Integer.parseInt((String) legendinsets.get("left"));
					int legendInsetRight = Integer.parseInt((String) legendinsets.get("right"));
					legend.setInsets(
							InsetsImpl.create(legendInsetTop, legendInsetLeft, legendInsetBottom, legendInsetRight));
				}
				legend.setStretch(Stretch.HORIZONTAL_LITERAL);// Stretch legend
				JSONObject legendOutLine = (JSONObject) legendProperties.get("outline");
				if (legendOutLine != null && legendOutLine.size() > 0) {
					Boolean islegendOutLineVisible = (Boolean) legendOutLine.get("visible");
					if (islegendOutLineVisible) {
						int legendOutlineWidthInt = Integer.parseInt((String) legendOutLine.get("width"));
						String legendOutlineColor = (String) legendOutLine.get("color");
						legend.setOutline(LineAttributesImpl.create(hexToRGB(legendOutlineColor),
								getLineStyleType((String) legendOutLine.get("style")), legendOutlineWidthInt));
					}
				}

				Boolean visibleTitle = (Boolean) legendProperties.get("visibleTitle");
				if (visibleTitle) {
					Label legendTitle = legend.getTitle();
					legend.setTitlePosition(Position.ABOVE_LITERAL);
					legendTitle.getCaption().setColor(hexToRGB((String) legendProperties.get("title-font-color")));
					String titleLblFontStyle = (String) legendProperties.get("title-font-style");
					Float fsize = Float.parseFloat((String) legendProperties.get("title-font-size"));
					boolean bBold = false;
					boolean bItalic = false;
					boolean bUnderline = false;
					boolean bStrikethrough = false;
					boolean bWordWrap = false;
					if (titleLblFontStyle.equalsIgnoreCase("bold"))
						bBold = true;
					legendTitle.getCaption()
							.setFont(FontDefinitionImpl.create((String) legendProperties.get("title-font-family"),
									fsize, bBold, bItalic, bUnderline, bStrikethrough, bWordWrap, 0,
									getTextAlignment("center", null)));
					legendTitle.getCaption().setValue(legendTitleValue);

				}
				legend.getTitle().setVisible(visibleTitle);
			}
			legend.setVisible(visible);
			legend.setShowValue(false);
			legend.getSeparator().setVisible(false);

		}
	}

	public static void main(String[] args) {
		System.out.println(hexToRGB("F0F0F0"));

	}

	public static ColorDefinition hexToRGB(String color) {
		// color = color.substring(1, color.length());
		if (color == null || color.equalsIgnoreCase("")) {
			return ColorDefinitionImpl.TRANSPARENT();
		} else {
			int colors = Integer.parseInt(color, 16);
			int red = (colors >> 16) & 0xFF;
			int green = (colors >> 8) & 0xFF;
			int blue = (colors >> 0) & 0xFF;
			return ColorDefinitionImpl.create(red, green, blue);
		}
	}

	public static ColorDefinition hexToRGB(String color, int opecaity) {
		// color = color.substring(1, color.length());
		if (color == null || color.equalsIgnoreCase("")) {
			return ColorDefinitionImpl.TRANSPARENT();
		} else {
			int colors = Integer.parseInt(color, 16);
			int red = (colors >> 16) & 0xFF;
			int green = (colors >> 8) & 0xFF;
			int blue = (colors >> 0) & 0xFF;
			return ColorDefinitionImpl.create(red, green, blue, opecaity);
		}
	}

	public static Anchor getAnchorType(String position) {
		if (position.equals("bottom")) {
			return Anchor.SOUTH_LITERAL;
		} else if (position.equals("top")) {
			return Anchor.NORTH_LITERAL;
		} else if (position.equals("middle")) {
			return Anchor.WEST_LITERAL;
		} else {
			return Anchor.NORTH_LITERAL;
		}

	}

	public static TextAlignment getTextAlignment(String horizontalAlignment, String verticalAlignment) {

		HorizontalAlignment ha = HorizontalAlignment.LEFT_LITERAL;
		VerticalAlignment va = VerticalAlignment.CENTER_LITERAL;
		if (horizontalAlignment.equalsIgnoreCase("left")) {
			ha = HorizontalAlignment.LEFT_LITERAL;

		} else if (horizontalAlignment.equalsIgnoreCase("center")) {
			ha = HorizontalAlignment.CENTER_LITERAL;
		} else if (horizontalAlignment.equalsIgnoreCase("right")) {
			ha = HorizontalAlignment.RIGHT_LITERAL;
		}
		TextAlignment textAlignment = TextAlignmentImpl.createDefault(ha, va);

		return textAlignment;

	}

	public static LineStyle getLineStyleType(String style) {
		if (style.equalsIgnoreCase("Solid")) {
			return LineStyle.SOLID_LITERAL;
		} else if (style.equalsIgnoreCase("Dashed")) {
			return LineStyle.DASHED_LITERAL;
		} else if (style.equalsIgnoreCase("Dotted")) {
			return LineStyle.DOTTED_LITERAL;
		} else
			return LineStyle.SOLID_LITERAL;
		// switch (style)
		// {
		// case LineStyle.SOLID : return LineStyle.SOLID_LITERAL;
		// case LineStyle.DASHED : return LineStyle.DASHED_LITERAL;
		// case LineStyle.DOTTED : return LineStyle.DOTTED_LITERAL;
		// case LineStyle.DASH_DOTTED : return LineStyle.DASH_DOTTED_LITERAL;
		// default : return LineStyle.SOLID_LITERAL;
		// }
	}

}
