package com.queryio.demo.mr.ncdcgsod;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NCDCExpressions {
	private static final Log LOG = LogFactory.getLog(NCDCExpressions.class);
	ArrayList<Expression> expressions = new ArrayList<Expression>();
	ArrayList<String> booleanExpressions = new ArrayList<String>();
	private ArrayList<ExpressionRelations> relations = new ArrayList<ExpressionRelations>();
	private String lastOperator = null;
	
	public NCDCExpressions(String expressions) {
		if(expressions != null){
			
			expressions += " ";
			
			LOG.info("Expression string to parse: " + expressions);
			
			expressions = expressions.substring(expressions.indexOf("["), expressions.lastIndexOf("]")+1);
			
			this.parseExpressions(null, expressions);
			
			for(int i=0; i<this.relations.size(); i++){
				String operator = this.relations.get(i).getCondition();
				if(operator.contains("&&")){
					this.booleanExpressions.add("&&");
				} else {
					this.booleanExpressions.add("||");
				}
			}
			LOG.info("Expressions: " + this.expressions);
			LOG.info("Relations: " + this.relations);
			LOG.info("Bool Exp: " + this.booleanExpressions);
		}
	}
	public void parseExpressions(Expression lastExpression, String expressionString){
		Expression currentExpression = null;
		
		if(expressionString.indexOf("]")==-1){
			return;
		}
		
		String exp1 = expressionString.substring(0, expressionString.indexOf("]"));
		
		String remaining = expressionString.substring(exp1.length());
		
		exp1 = exp1.replace("[", "");
		
		currentExpression = new Expression();
		
		if(exp1.contains(">=")){
			currentExpression.setOperator(Expression.GTEQ);
			currentExpression.setColumn(exp1.split(">=")[0].trim());
			currentExpression.setValue(exp1.split(">=")[1].trim());
		} else if(exp1.contains("<=")){
			currentExpression.setOperator(Expression.LTEQ);
			currentExpression.setColumn(exp1.split("<=")[0].trim());
			currentExpression.setValue(exp1.split("<=")[1].trim());
		} else if(exp1.contains(">")){
			currentExpression.setOperator(Expression.GT);
			currentExpression.setColumn(exp1.split(">")[0].trim());
			currentExpression.setValue(exp1.split(">")[1].trim());
		} else if(exp1.contains("<")){
			currentExpression.setOperator(Expression.LT);
			currentExpression.setColumn(exp1.split("<")[0].trim());
			currentExpression.setValue(exp1.split("<")[1].trim());
		} else if(exp1.contains("=")){
			currentExpression.setOperator(Expression.EQ);
			currentExpression.setColumn(exp1.split("=")[0].trim());
			currentExpression.setValue(exp1.split("=")[1].trim());
		} else if(exp1.contains("<>")){
			currentExpression.setOperator(Expression.NEQ);
			currentExpression.setColumn(exp1.split("<>")[0].trim());
			currentExpression.setValue(exp1.split("<>")[1].trim());
		} else if(exp1.contains("CONTAINS")){
			currentExpression.setOperator(Expression.CONTAINS);
			currentExpression.setColumn(exp1.split("CONTAINS")[0].trim());
			currentExpression.setValue(exp1.split("CONTAINS")[1].trim());
		} else if(exp1.contains("STARTSWITH")){
			currentExpression.setOperator(Expression.STARTSWITH);
			currentExpression.setColumn(exp1.split("STARTSWITH")[0].trim());
			currentExpression.setValue(exp1.split("STARTSWITH")[1].trim());
		} else if(exp1.contains("ENDSWITH")){
			currentExpression.setOperator(Expression.ENDSWITH);
			currentExpression.setColumn(exp1.split("ENDSWITH")[0].trim());
			currentExpression.setValue(exp1.split("ENDSWITH")[1].trim());
		} else if(exp1.contains("MATCHES")){
			currentExpression.setOperator(Expression.MATCHES);
			currentExpression.setColumn(exp1.split("MATCHES")[0].trim());
			currentExpression.setValue(exp1.split("MATCHES")[1].trim());
		}
		
		this.expressions.add(currentExpression);
		
		if(lastExpression!=null && lastOperator!=null){
			ExpressionRelations rel = new ExpressionRelations();
			rel.setExpression1(lastExpression);
			rel.setExpression2(currentExpression);
			rel.setCondition(lastOperator);
			
			this.relations.add(rel);
		}
		
		int index = remaining.indexOf("[");
		if(index != -1){
			String condition = remaining.substring(0, index);
			condition = condition.trim();
			String operator = condition.replace("]", "").replace("[", "");
			remaining = remaining.substring(index+1);
	
			lastOperator = operator;
			
			parseExpressions(currentExpression, remaining);
		}
	}
}
