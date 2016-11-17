package com.queryio.plugin.datatags.common;

import java.util.List;

public class Tag {
	private String name;
	private int method;
	private String substitutionValue;
	private List<Expression> expressions;
	private List<String> relations;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMethod() {
		return method;
	}
	public void setMethod(int method) {
		this.method = method;
	}
	public String getSubstitutionValue() {
		return substitutionValue;
	}
	public void setSubstitutionValue(String substitutionValue) {
		this.substitutionValue = substitutionValue;
	}
	public List<Expression> getExpressions() {
		return expressions;
	}
	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}
	public List<String> getRelations() {
		return relations;
	}
	public void setRelations(List<String> relations) {
		this.relations = relations;
	}
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("{");
		sBuf.append("\"name\" : \"" + name + "\",");
		sBuf.append("\"expressions\" : ");
		sBuf.append("[");
		for(int i=0; i<expressions.size(); i++) {
			sBuf.append("{");
			Expression expression = expressions.get(i);
			sBuf.append("\"expression: \"" + expression.toString() + "\",");
			sBuf.append("},");
		}
		sBuf.append("],");
		for(int i=0; i<relations.size(); i++) {
			sBuf.append("{");
			sBuf.append("\"realtion: \"" + relations.get(i) + "\",");
			sBuf.append("},");
		}
		sBuf.append("],");
		sBuf.append("}");
		return sBuf.toString();
	}
}
