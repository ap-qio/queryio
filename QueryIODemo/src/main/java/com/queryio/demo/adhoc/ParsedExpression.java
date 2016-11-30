package com.queryio.demo.adhoc;

import java.util.ArrayList;

import org.apache.commons.collections.bidimap.DualTreeBidiMap;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

public class ParsedExpression {
	ArrayList<Expression> expressions = new ArrayList<Expression>();
	ArrayList<String> booleanExpressions = new ArrayList<String>();
	ArrayList<ExpressionRelations> relations = new ArrayList<ExpressionRelations>();
	String lastOperator = null;

	public ParsedExpression(String expressions) {
		if (expressions != null) {

			expressions += " ";

			expressions = expressions.substring(expressions.indexOf("["), expressions.lastIndexOf("]") + 1);

			this.parseExpressions(null, expressions);

			for (int i = 0; i < this.relations.size(); i++) {
				String operator = this.relations.get(i).getCondition();
				if (operator.contains("&&")) {
					this.booleanExpressions.add("&&");
				} else {
					this.booleanExpressions.add("||");
				}
			}
		}
	}

	private void parseExpressions(Expression lastExpression, String expressionString) {
		Expression currentExpression = null;

		if (expressionString.indexOf("]") == -1) {
			return;
		}

		String exp1 = expressionString.substring(0, expressionString.indexOf("]"));

		String remaining = expressionString.substring(exp1.length());

		exp1 = exp1.replace("[", "");

		currentExpression = new Expression();

		if (exp1.contains(">=")) {
			currentExpression.setOperator(Expression.GTEQ);
			currentExpression.setColumn(exp1.split(">=")[0].trim());
			currentExpression.setValue(exp1.split(">=")[1].trim());
		} else if (exp1.contains("<=")) {
			currentExpression.setOperator(Expression.LTEQ);
			currentExpression.setColumn(exp1.split("<=")[0].trim());
			currentExpression.setValue(exp1.split("<=")[1].trim());
		} else if (exp1.contains(">")) {
			currentExpression.setOperator(Expression.GT);
			currentExpression.setColumn(exp1.split(">")[0].trim());
			currentExpression.setValue(exp1.split(">")[1].trim());
		} else if (exp1.contains("<")) {
			currentExpression.setOperator(Expression.LT);
			currentExpression.setColumn(exp1.split("<")[0].trim());
			currentExpression.setValue(exp1.split("<")[1].trim());
		} else if (exp1.contains("=")) {
			currentExpression.setOperator(Expression.EQ);
			currentExpression.setColumn(exp1.split("=")[0].trim());
			currentExpression.setValue(exp1.split("=")[1].trim());
		} else if (exp1.contains("<>")) {
			currentExpression.setOperator(Expression.NEQ);
			currentExpression.setColumn(exp1.split("<>")[0].trim());
			currentExpression.setValue(exp1.split("<>")[1].trim());
		} else if (exp1.contains("CONTAINS")) {
			currentExpression.setOperator(Expression.CONTAINS);
			currentExpression.setColumn(exp1.split("CONTAINS")[0].trim());
			currentExpression.setValue(exp1.split("CONTAINS")[1].trim());
		} else if (exp1.contains("STARTSWITH")) {
			currentExpression.setOperator(Expression.STARTSWITH);
			currentExpression.setColumn(exp1.split("STARTSWITH")[0].trim());
			currentExpression.setValue(exp1.split("STARTSWITH")[1].trim());
		} else if (exp1.contains("ENDSWITH")) {
			currentExpression.setOperator(Expression.ENDSWITH);
			currentExpression.setColumn(exp1.split("ENDSWITH")[0].trim());
			currentExpression.setValue(exp1.split("ENDSWITH")[1].trim());
		} else if (exp1.contains("MATCHES")) {
			currentExpression.setOperator(Expression.MATCHES);
			currentExpression.setColumn(exp1.split("MATCHES")[0].trim());
			currentExpression.setValue(exp1.split("MATCHES")[1].trim());
		}

		this.expressions.add(currentExpression);

		if (lastExpression != null && lastOperator != null) {
			ExpressionRelations rel = new ExpressionRelations();
			rel.setExpression1(lastExpression);
			rel.setExpression2(currentExpression);
			rel.setCondition(lastOperator);

			this.relations.add(rel);
		}

		int index = remaining.indexOf("[");
		if (index != -1) {
			String condition = remaining.substring(0, index);
			condition = condition.trim();
			String operator = condition.replace("]", "").replace("[", "");
			remaining = remaining.substring(index + 1);

			lastOperator = operator;

			parseExpressions(currentExpression, remaining);
		}
	}

	public boolean evaluateEntry(AdHocEntry entry) {
		boolean valid = true;

		ArrayList<Boolean> expResults = new ArrayList<Boolean>();
		Expression expression;

		for (int i = 0; i < expressions.size(); i++) {
			expression = expressions.get(i);
			String column = expression.getColumn();
			Object o = ((DualTreeBidiMap) entry.getColumns()).inverseSortedBidiMap().get(column);
			if (o != null) {
				boolean result = expression.evaluate(String.valueOf(entry.getValues().get((Integer) o)));
				expResults.add(result);
			}
		}

		if (expResults.size() > 0) {
			valid = evaluateBooleanExpression(expResults);
		}

		return valid;
	}

	public boolean evaluateBooleanExpression(ArrayList<Boolean> expResults) {
		JexlEngine jexl = new JexlEngine();
		jexl.setSilent(true);
		jexl.setLenient(true);

		String exp = "";

		for (int i = 0; i < expResults.size(); i++) {
			if (i != 0) {
				exp += booleanExpressions.get(i - 1);
			}

			exp += " ";
			exp += "a" + i;
			exp += " ";
		}

		org.apache.commons.jexl2.Expression expression = jexl.createExpression(exp);
		JexlContext jexlContext = new MapContext();

		for (int i = 0; i < expResults.size(); i++) {
			jexlContext.set("a" + i, expResults.get(i));
		}

		return (Boolean) expression.evaluate(jexlContext);
	}
}
