package com.queryio.demo.mr.ncdcgsod;

public class ExpressionRelations {
	private Expression expression1;
	private Expression expression2;
	private String condition;

	public Expression getExpression1() {
		return expression1;
	}

	public void setExpression1(Expression expression1) {
		this.expression1 = expression1;
	}

	public Expression getExpression2() {
		return expression2;
	}

	public void setExpression2(Expression expression2) {
		this.expression2 = expression2;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String toString() {
		return expression1 + " " + condition + " " + expression2;
	}
}
