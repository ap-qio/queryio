/**
 * 
 */
package com.queryio.common.exporter.dstruct;

public class LineAttributes
{
	/*
	 * IMPORTANT: Constant defined for CAP and JOIN have same values as SWT class 
	 * So that we can use these constant directly with SWT code.
	 * We can not refer SWT constant in our code for backward compatibility issues
	 *  
	 */
	/**
	 * Line drawing style for solid lines.
	 */
	public static int LINE_STYLE_SOLID = 0;
	
	/**
	 * Line drawing style for dotted lines.
	 */
	public static int LINE_STYLE_DOT = 1;
	
	/**
     * Joins path segments by extending their outside edges until
     * they meet.
     */
    public final static int JOIN_MITER = 1;

    /**
     * Joins path segments by rounding off the corner at a radius
     * of half the line width.
     */
    public final static int JOIN_ROUND = 2;

    /**
     * Joins path segments by connecting the outer corners of their
     * wide outlines with a straight segment.
     */
    public final static int JOIN_BEVEL = 3;
    
    /**
     * Ends unclosed subpaths and dash segments with no added
     * decoration.
     */
    public final static int CAP_FLAT = 1;

    /**
     * Ends unclosed subpaths and dash segments with a round
     * decoration that has a radius equal to half of the width
     * of the pen.
     */
    public final static int CAP_ROUND = 2;

    /**
     * Ends unclosed subpaths and dash segments with a square
     * projection that extends beyond the end of the segment
     * to a distance equal to half of the line width.
     */
    public final static int CAP_SQUARE = 3;
    
    /**
	 * The line width.
	 */
	public float width;
	
	/**
	 * The line style.
	 * 
	 * @see LINE_DOT
	 * @see LINE_SOLID
	 */
	public int style;
	
	/**
	 * The line join style.
	 * 
	 * @see JOIN_BEVEL
	 * @see JOIN_MITER
	 * @see JOIN_ROUND
	 */
	public int join;
    
    /**
	 * The line cap style.
	 * 
	 * @see CAP_FLAT
	 * @see CAP_ROUND
	 * @see CAP_SQUARE
	 */
	public int cap;
    
	public LineAttributes(float width, int cap, int join)
	{
		this (width, cap, join, LINE_STYLE_SOLID);
	}
	
    public LineAttributes(float width, int cap, int join, int style)
	{
    	if (width < 0.0f)
		{
			throw new IllegalArgumentException("negative width");
		}
		if (cap != CAP_FLAT && cap != CAP_ROUND && cap != CAP_SQUARE)
		{
			throw new IllegalArgumentException("illegal end cap value");
		}
		if (join != JOIN_MITER && join != JOIN_ROUND && join != JOIN_BEVEL)
		{
			throw new IllegalArgumentException("illegal line join value");
		}
		if (style != LINE_STYLE_DOT && style != LINE_STYLE_SOLID)
		{
			throw new IllegalArgumentException("illegal line style value");
		}
		this.width	= width;
		this.cap	= cap;
		this.join	= join;
		this.style = style;
	}
}