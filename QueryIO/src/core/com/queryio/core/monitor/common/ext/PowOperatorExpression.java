/*
 * @(#)  PowOperatorExpression.java
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
package com.queryio.core.monitor.common.ext;

import org.ks.cm.parser.Expression;
import org.ks.cm.parser.ExpressionException;
import org.ks.cm.parser.OperatorExpression;

/**
 * @author Exceed Consultancy Services
 */
public class PowOperatorExpression extends OperatorExpression
{
	private static final long serialVersionUID = 7526000057010002400L;

	/**
	 * @see java.lang.Object#Object()
	 */
	public PowOperatorExpression()
	{
		super();
	}

	/**
	 * Constructor for PowOperatorExpression.
	 * 
	 * @param left
	 * @param right
	 */
	public PowOperatorExpression(final Expression left, final Expression right)
	{
		super(left, right);
	}

	/**
	 * @see org.ks.cm.parser.Expression#calculate()
	 */
	public double calculate() throws ExpressionException
	{
		return Math.pow(this.left.calculate(), this.right.calculate());
	}

	/**
	 * @see org.ks.cm.parser.OperatorExpression#getPriority()
	 */
	public byte getPriority()
	{
		return 110;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer('[');
		sbToString.append(this.left.toString());
		sbToString.append('^');
		sbToString.append(this.right.toString());
		sbToString.append(']');

		return sbToString.toString();
		// return "[" + left.toString() + '^' + right.toString() + ']';
	}

}
