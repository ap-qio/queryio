package com.queryio.files.tagging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Expression {
	public static final int GT = 0;
	public static final int LT = 1;
	public static final int GTEQ = 2;
	public static final int LTEQ = 3;
	public static final int EQ = 4;
	public static final int NEQ = 5;
	public static final int CONTAINS = 6;
	public static final int STARTSWITH = 7;
	public static final int ENDSWITH = 8;
	public static final int MATCHES = 9;
	public static final int IN = 10;
	public static final int NOTIN = 11;
	public static final int BETWEEN = 12;
	public static final int NOTBETWEEN = 13;
	public static final int LIKE = 14;
	public static final int NOTLIKE = 15;

	private String column;
	private int operator;
	private String value;

	private static final short TYPE_BOOLEAN = 1;
	private static final short TYPE_INTEGER = 2;
	private static final short TYPE_DECIMAL = 3;
	private static final short TYPE_STRING = 4;
	private static final short TYPE_DATE = 5;

	List<String> list = null;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isNumber(String data) {
		try {
			Long.parseLong(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDouble(String data) {
		try {
			Double.parseDouble(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDate(String data) {
		try {
			return parseDate(data) != null;
		} catch (Exception e) {
			return false;
		}
	}

	// 2013-04-12 11:13:09.102
	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public boolean isBoolean(String data) {
		return (data.trim().toLowerCase().equals("true") || data.trim().toLowerCase().equals("false")) ? true : false;
	}

	public boolean evaluate(String checkValue) {
		if (checkValue == null)
			return false;
		switch (this.operator) {
		case GT:
			return gt(checkValue, value);
		case LT:
			return lt(checkValue, value);
		case GTEQ:
			return gteq(checkValue, value);
		case LTEQ:
			return lteq(checkValue, value);
		case EQ:
			return eq(checkValue, value);
		case NEQ:
			return neq(checkValue, value);
		case CONTAINS:
			return checkValue.contains(value) ? true : false;
		case STARTSWITH:
			return checkValue.startsWith(value) ? true : false;
		case ENDSWITH:
			return checkValue.endsWith(value) ? true : false;
		case MATCHES:
			return checkValue.matches(value) ? true : false;
		case LIKE:
			return like(value, checkValue) ? true : false;
		case NOTLIKE:
			return notLike(value, checkValue) ? true : false;
		case IN:
			return in(checkValue) ? true : false;
		case NOTIN:
			return notIn(checkValue) ? true : false;
		case BETWEEN:
			return between(checkValue) ? true : false;
		case NOTBETWEEN:
			return notBetween(checkValue) ? true : false;
		default:
			return true;
		}
	}

	public short getType(String checkValue, String value) {
		if (isNumber(checkValue) && isNumber(value)) {
			return TYPE_INTEGER;
		} else if (isDouble(checkValue) && isDouble(value)) {
			return TYPE_DECIMAL;
		} else if (isBoolean(checkValue) && isBoolean(value)) {
			return TYPE_BOOLEAN;
		} else if (isDate(checkValue) && isDate(value)) {
			return TYPE_DATE;
		} else {
			return TYPE_STRING;
		}
	}

	public boolean gt(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) > Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) > Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return (Boolean.parseBoolean(checkValue) == true) && (Boolean.parseBoolean(value) == false);
		case TYPE_DATE:
			return parseDate(checkValue).getTime() > parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) > 0 ? true : false;
		}
	}

	public boolean lt(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) < Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) < Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return (Boolean.parseBoolean(checkValue) == false) && (Boolean.parseBoolean(value) == true);
		case TYPE_DATE:
			return parseDate(checkValue).getTime() < parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) < 0 ? true : false;
		}
	}

	public boolean gteq(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) >= Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) >= Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return !((Boolean.parseBoolean(checkValue) == false) && (Boolean.parseBoolean(value) == true));
		case TYPE_DATE:
			return parseDate(checkValue).getTime() >= parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) >= 0 ? true : false;
		}
	}

	public boolean lteq(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) <= Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) <= Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return !((Boolean.parseBoolean(checkValue) == true) && (Boolean.parseBoolean(value) == false));
		case TYPE_DATE:
			return parseDate(checkValue).getTime() <= parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) <= 0 ? true : false;
		}
	}

	public boolean eq(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) == Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) == Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return Boolean.parseBoolean(checkValue) == Boolean.parseBoolean(value);
		case TYPE_DATE:
			return parseDate(checkValue).getTime() == parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) == 0 ? true : false;
		}
	}

	public boolean neq(String checkValue, String value) {
		short type = getType(checkValue, value);
		switch (type) {
		case TYPE_INTEGER:
			return Long.parseLong(checkValue) != Long.parseLong(value);
		case TYPE_DECIMAL:
			return Double.parseDouble(checkValue) != Double.parseDouble(value);
		case TYPE_BOOLEAN:
			return Boolean.parseBoolean(checkValue) != Boolean.parseBoolean(value);
		case TYPE_DATE:
			return parseDate(checkValue).getTime() != parseDate(value).getTime();
		default:
			return checkValue.compareTo(value) != 0 ? true : false;
		}
	}

	public boolean like(final String expression, final String value) {
		String regex = quotemeta(expression);
		regex = regex.replace("_", ".").replace("%", ".*?");
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		return p.matcher(value).matches();
	}

	public boolean notLike(final String expression, final String value) {
		return !like(expression, value);
	}

	public void populateList() {
		list = new ArrayList<String>();
		String str = value.substring(value.indexOf("("), value.lastIndexOf(")"));
		boolean quoteStart = false;
		StringBuffer sBuf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\\') {
				if ((i + 1) < str.length() && str.charAt(i + 1) == '"') {
					sBuf.append('"');
					i++;
				} else {
					sBuf.append(str.charAt(i));
				}
			} else if (str.charAt(i) == '"') {
				quoteStart = !quoteStart;
				if (!quoteStart) {
					list.add(sBuf.toString());
				} else {
					sBuf.setLength(0);
				}
				continue;
			} else {
				sBuf.append(str.charAt(i));
			}
		}

	}

	public boolean in(final String checkValue) {
		if (list == null) {
			populateList();
		}

		for (int i = 0; i < list.size(); i++) {
			if (eq(checkValue, list.get(i))) {
				return true;
			}
		}

		return false;
	}

	public boolean notIn(final String checkValue) {
		return !in(checkValue);
	}

	public boolean between(final String checkValue) {
		if (list == null) {
			populateList();
		}

		return gteq(checkValue, list.get(0)) && lt(checkValue, list.get(1));
	}

	public boolean notBetween(final String checkValue) {
		return !between(checkValue);
	}

	public String quotemeta(String s) {
		int len = s.length();
		if (len == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if ("[](){}.*+?$^|#\\".indexOf(c) != -1) {
				sb.append("\\");
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public String toString() {
		String op = null;
		switch (this.operator) {
		case GT:
			op = ">";
			break;
		case LT:
			op = "<";
			break;
		case GTEQ:
			op = ">=";
			break;
		case LTEQ:
			op = "<=";
			break;
		case EQ:
			op = "=";
			break;
		case NEQ:
			op = "!=";
			break;
		case CONTAINS:
			op = "CONTAINS";
			break;
		case STARTSWITH:
			op = "STARTSWITH";
			break;
		case ENDSWITH:
			op = "ENDSWITH";
			break;
		case MATCHES:
			op = "MATCHES";
		case LIKE:
			op = "LIKE";
			break;
		case NOTLIKE:
			op = "NOTLIKE";
			break;
		case IN:
			op = "CONTAINEDIN";
			break;
		case NOTIN:
			op = "NOTCONTAINEDIN";
			break;
		case BETWEEN:
			op = "BETWEEN";
			break;
		case NOTBETWEEN:
			op = "NOTBETWEEN";
			break;
		}

		return this.column + " " + op + " " + this.value;
	}

	public static int getOperator(String operator) {
		if (operator.equalsIgnoreCase(">=")) {
			return Expression.GTEQ;
		} else if (operator.equalsIgnoreCase("<=")) {
			return Expression.LTEQ;
		} else if (operator.equalsIgnoreCase(">")) {
			return Expression.GT;
		} else if (operator.equalsIgnoreCase("<")) {
			return Expression.LT;
		} else if (operator.equalsIgnoreCase("=")) {
			return Expression.EQ;
		} else if (operator.equalsIgnoreCase("!=")) {
			return Expression.NEQ;
		} else if (operator.equalsIgnoreCase("CONTAINS")) {
			return Expression.CONTAINS;
		} else if (operator.equalsIgnoreCase("STARTSWITH")) {
			return Expression.STARTSWITH;
		} else if (operator.equalsIgnoreCase("ENDSWITH")) {
			return Expression.ENDSWITH;
		} else if (operator.equalsIgnoreCase("MATCHES")) {
			return Expression.MATCHES;
		} else if (operator.equalsIgnoreCase("NOTLIKE")) {
			return Expression.NOTLIKE;
		} else if (operator.equalsIgnoreCase("LIKE")) {
			return Expression.LIKE;
		} else if (operator.equalsIgnoreCase("CONTAINEDIN")) {
			return Expression.IN;
		} else if (operator.equalsIgnoreCase("NOTCONTAINEDIN")) {
			return Expression.NOTIN;
		} else if (operator.equalsIgnoreCase("BETWEEN")) {
			return Expression.BETWEEN;
		} else if (operator.equalsIgnoreCase("NOTBETWEEN")) {
			return Expression.NOTBETWEEN;
		}
		return -1;
	}
}
