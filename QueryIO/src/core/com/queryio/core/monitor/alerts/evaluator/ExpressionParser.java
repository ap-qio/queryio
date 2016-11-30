/*
 * @(#)  ExpressionParser.java
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
package com.queryio.core.monitor.alerts.evaluator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ks.cm.parser.Expression;
import org.ks.cm.parser.ParameterExpression;
import org.ks.cm.parser.Parser;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.monitor.common.ext.AndOperatorExpression;
import com.queryio.core.monitor.common.ext.DurationFunctionExpression;
import com.queryio.core.monitor.common.ext.EqualsEqualsMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.EqualsEqualsOperatorExpression;
import com.queryio.core.monitor.common.ext.GreaterEqualsMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.GreaterEqualsOperatorExpression;
import com.queryio.core.monitor.common.ext.GreaterMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.GreaterOperatorExpression;
import com.queryio.core.monitor.common.ext.LesserEqualsMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.LesserEqualsOperatorExpression;
import com.queryio.core.monitor.common.ext.LesserMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.LesserOperatorExpression;
import com.queryio.core.monitor.common.ext.LogicalExpression;
import com.queryio.core.monitor.common.ext.ModOperatorExpression;
import com.queryio.core.monitor.common.ext.NotEqualsMinusOperatorExpression;
import com.queryio.core.monitor.common.ext.NotEqualsOperatorExpression;
import com.queryio.core.monitor.common.ext.NotOperatorExpression;
import com.queryio.core.monitor.common.ext.OrOperatorExpression;
import com.queryio.core.monitor.common.ext.PowOperatorExpression;
import com.queryio.core.monitor.common.ext.TimeDurationFunctionExpression;

/**
 * This is the expression Parser class which is used to Parse the expression for
 * evaluation of Rules.
 * 
 * @author Exceed Consultancy Services
 */
public class ExpressionParser {
	private final Parser parser;
	private Map map = null;
	private String errMsg = null;
	private double value;
	private Expression currentExpression = null;

	public static void main(String[] args) {
		ExpressionParser ep = new ExpressionParser();
		ep.addVariableAsObject("attr0", 62.146385);
		ep.parseExpression("duration(Rule1_0,time_stamp,none,3000,attr0!=121)");
	}

	/**
	 * @see java.lang.Object#Object()
	 */
	public ExpressionParser() {
		this.parser = new Parser();

		// setting the appropriate Expression classes for each of the operators
		// and
		// even setting TimeDurationFunctionExpression as the function for the
		// Impression's Parser
		final Map operators = this.parser.getOperators();

		operators.put("%", ModOperatorExpression.class);
		operators.put("^", PowOperatorExpression.class);
		operators.put("!", NotOperatorExpression.class);

		// logical operators
		operators.put(">", GreaterOperatorExpression.class);
		operators.put(">+", GreaterOperatorExpression.class);
		operators.put(">-", GreaterMinusOperatorExpression.class);

		operators.put(">=", GreaterEqualsOperatorExpression.class);
		operators.put(">=+", GreaterEqualsOperatorExpression.class);
		operators.put(">=-", GreaterEqualsMinusOperatorExpression.class);

		operators.put("<", LesserOperatorExpression.class);
		operators.put("<+", LesserOperatorExpression.class);
		operators.put("<-", LesserMinusOperatorExpression.class);

		operators.put("<=", LesserEqualsOperatorExpression.class);
		operators.put("<=+", LesserEqualsOperatorExpression.class);
		operators.put("<=-", LesserEqualsMinusOperatorExpression.class);

		operators.put("!=", NotEqualsOperatorExpression.class);
		operators.put("!=+", NotEqualsOperatorExpression.class);
		operators.put("!=-", NotEqualsMinusOperatorExpression.class);

		operators.put("==", EqualsEqualsOperatorExpression.class);
		operators.put("==+", EqualsEqualsOperatorExpression.class);
		operators.put("==-", EqualsEqualsMinusOperatorExpression.class);

		operators.put("&&", AndOperatorExpression.class);
		operators.put("&&+", AndOperatorExpression.class);
		operators.put("&&-", AndOperatorExpression.class);

		operators.put("||", OrOperatorExpression.class);
		operators.put("||+", OrOperatorExpression.class);
		operators.put("||-", OrOperatorExpression.class);

		final Map functions = this.parser.getFunctions();
		functions.put("timeduration", TimeDurationFunctionExpression.class);
		functions.put("duration", DurationFunctionExpression.class);
	}

	/**
	 * Method addVariable.
	 * 
	 * @param id
	 * @param val
	 */
	public void addVariable(final String id, final double val) {
		this.addVariableAsObject(id, new Double(val));
	}

	/**
	 * Method addVariableAsObject.
	 * 
	 * @param id
	 * @param val
	 */
	public void addVariableAsObject(final String id, final Object val) {
		if (this.map == null) {
			this.map = new HashMap();
		}

		this.map.put(id, val);
	}

	/**
	 * This method parses the expression and replaces the variables within the
	 * expression with the correct values instead.
	 * 
	 * @param expression
	 */
	public void parseExpression(String expression) {
		this.errMsg = null;
		this.value = -1;

		try {
			expression = StaticUtilities.searchAndReplace(expression, " ", "");
			expression = StaticUtilities.searchAndReplace(expression, ":", "_");

			final Expression expr = this.parser.parse(expression);
			if (!this.isLogicalExpression(expr)) {
				this.errMsg = "Error evaluating the expression, its not a logical expression";
				return;
			}
			this.replaceVariableWithValues(expr);
			final String msg = this.isExpressionComplete(expr);
			if (!"".equals(msg)) {
				this.errMsg = msg;
				return;
			}

			this.currentExpression = expr;
			this.value = expr.calculate();

			if (this.value == -1) {
				this.errMsg = "Error evaluating the expression";
			}
		} catch (final Exception ex) {
			this.errMsg = ex.getMessage();
			if (this.errMsg == null) {
				AppLogger.getLogger().fatal("3  this.errMsg == null", ex);

				this.errMsg = "Error evaluating the expression";
			}
		}
	}

	public void calculate() {
		this.errMsg = null;
		this.value = -1;
		try {
			this.replaceVariableWithValues(this.currentExpression);
			this.value = this.currentExpression.calculate();
			if (this.value == -1) {
				this.errMsg = "Error evaluating the expression";
			}
		} catch (final Exception ex) {
			this.errMsg = ex.getMessage();
			if (this.errMsg == null) {
				this.errMsg = "Error evaluating the expression";
			}
		}

	}

	/**
	 * Method replaceVariableWithValues.
	 * 
	 * @param expr
	 * @throws Exception
	 */
	private void replaceVariableWithValues(final Expression expr) throws Exception {
		if (this.map != null) {
			final Iterator itr = this.map.keySet().iterator();
			String key = null;
			Number val = null;

			while (itr.hasNext()) {
				key = (String) itr.next();
				val = (Number) this.map.get(key);
				expr.setParameter(key, (val != null) ? val.doubleValue() : 0.0);
			}
		}
	}

	/**
	 * isLogicalExpression
	 * 
	 * @param expr
	 * @return
	 */
	private boolean isLogicalExpression(final Expression expr) {
		return ((expr instanceof LogicalExpression) || (expr instanceof TimeDurationFunctionExpression)
				|| (expr instanceof DurationFunctionExpression));
	}

	/**
	 * isExpressionComplete
	 * 
	 * @param expr
	 * @return
	 */
	private String isExpressionComplete(final Expression expr) {
		final StringBuffer sBuff = new StringBuffer();
		final boolean isParamExp = expr instanceof ParameterExpression;

		if (isParamExp) {
			final ParameterExpression pe = (ParameterExpression) expr;
			if (!pe.isDefined()) {
				sBuff.append("Unknown token '");
				sBuff.append(pe.getName());
				sBuff.append("' found in expression");
				sBuff.append('\n');
			}
		} else {
			final Expression[] expressions = expr.getSubexpressions();
			final boolean isCustomFunc = expr instanceof TimeDurationFunctionExpression
					|| expr instanceof DurationFunctionExpression;

			if ((expressions != null) && (expressions.length > 0)) {
				for (int i = 0; i < expressions.length; i++) {
					if (isCustomFunc) {
						if (!(expressions[i] instanceof ParameterExpression)) {
							sBuff.append(this.isExpressionComplete(expressions[i]));
						}
					} else {
						sBuff.append(this.isExpressionComplete(expressions[i]));
					}
				}
			}
		}
		return sBuff.toString();
	}

	/**
	 * Method clearAll.
	 */
	public void clearAll() {
		if (this.map != null) {
			this.map.clear();
		}
		currentExpression = null;
	}

	/**
	 * Method hasError.
	 * 
	 * @return boolean
	 */
	public boolean hasError() {
		return (this.errMsg != null);
	}

	/**
	 * Method getValue.
	 * 
	 * @return double
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Method getErrorInfo.
	 * 
	 * @return String
	 */
	public String getErrorInfo() {
		return this.errMsg;
	}
}
