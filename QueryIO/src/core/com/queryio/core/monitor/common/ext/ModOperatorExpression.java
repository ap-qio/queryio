/*
 * @(#)  ModOperatorExpression.java
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
public class ModOperatorExpression extends OperatorExpression
{
	private static final long serialVersionUID = 7526000057010002395L;

	/**
	 * @see java.lang.Object#Object()
	 */
	public ModOperatorExpression()
	{
		super();
	}

	/**
	 * Constructor for ModOperatorExpression.
	 * 
	 * @param left
	 * @param right
	 */
	public ModOperatorExpression(final Expression left, final Expression right)
	{
		super(left, right);
	}

	/**
	 * @see org.ks.cm.parser.Expression#calculate()
	 */
	public double calculate() throws ExpressionException
	{
		return this.left.calculate() % this.right.calculate();
	}

	/**
	 * @see org.ks.cm.parser.OperatorExpression#getPriority()
	 */
	public byte getPriority()
	{
		return 100;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer('[');
		sbToString.append(this.left.toString());
		sbToString.append('&');
		sbToString.append(this.right.toString());
		sbToString.append(']');

		return sbToString.toString();
		// return "[" + left.toString() + '%' + right.toString() + ']';
	}

}
