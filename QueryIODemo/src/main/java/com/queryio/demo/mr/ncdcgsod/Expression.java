package com.queryio.demo.mr.ncdcgsod;


public class Expression {
	public static final int i=0;
	public static final int GT=0;
	public static final int LT=1;
	public static final int GTEQ=2;
	public static final int LTEQ=3;
	public static final int EQ=4;
	public static final int NEQ=5;
	public static final int CONTAINS=6;
	public static final int STARTSWITH=7;
	public static final int ENDSWITH=8;
	public static final int MATCHES=9;
	
	private String column;
	private int operator;
	private String value;
	
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
	public boolean isNumber(String data){
		try{
			Long.parseLong(data);
			return true;
		} catch(Exception e){
			return false;
		}
	}
	public boolean isDouble(String data){
		try{
			Double.parseDouble(data);
			return true;
		} catch(Exception e){
			return false;
		}
	}
	public boolean isBoolean(String data){
		return (data.trim().toLowerCase().equals("true") || data.trim().toLowerCase().equals("false")) ? true : false;
	}
	public boolean evaluate(String checkValue){
		boolean isInt = false;
		boolean isDouble = false;
		boolean isBoolean = false;
		if(isNumber(checkValue) && isNumber(value)){
			isInt = true;
		} else if(isDouble(checkValue) && isDouble(value)){
			isDouble = true;
		} else if(isBoolean(checkValue) && isBoolean(value)){
			isBoolean = true;
		}
		
		boolean result = false;
		switch(this.operator){
			case GT: 
				if(isInt){
					result = Long.parseLong(checkValue) > Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) > Double.parseDouble(value);
				} else if(isBoolean){
					result = (Boolean.parseBoolean(checkValue) == true) && (Boolean.parseBoolean(value) == false);
				} else {
					result = checkValue.compareTo(value)>0 ? true : false;
				}
				break;
			case LT: 
				if(isInt){
					result = Long.parseLong(checkValue) < Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) < Double.parseDouble(value);
				} else if(isBoolean){
					result = (Boolean.parseBoolean(checkValue) == false) && (Boolean.parseBoolean(value) == true);
				} else {
					result = checkValue.compareTo(value)<0 ? true : false;
				}
				break;
			case GTEQ: 
				if(isInt){
					result = Long.parseLong(checkValue) >= Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) >= Double.parseDouble(value);
				} else if(isBoolean){
					result = !((Boolean.parseBoolean(checkValue) == false) && (Boolean.parseBoolean(value) == true));
				} else {
					result = checkValue.compareTo(value)>=0 ? true : false;
				}
				break;
			case LTEQ: 
				if(isInt){
					result = Long.parseLong(checkValue) <= Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) <= Double.parseDouble(value);
				} else if(isBoolean){
					result = !((Boolean.parseBoolean(checkValue) == true) && (Boolean.parseBoolean(value) == false));
				} else {
					result = checkValue.compareTo(value)<=0 ? true : false;
				}
				break;
			case EQ: 
				if(isInt){
					result = Long.parseLong(checkValue) == Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) == Double.parseDouble(value);
				} else if(isBoolean){
					result = Boolean.parseBoolean(checkValue) == Boolean.parseBoolean(value);
				} else {
					result = checkValue.compareTo(value)==0 ? true : false;
				}
				break;
			case NEQ: 
				if(isInt){
					result = Long.parseLong(checkValue) != Long.parseLong(value);
				} else if(isDouble){
					result = Double.parseDouble(checkValue) != Double.parseDouble(value);
				} else if(isBoolean){
					result = Boolean.parseBoolean(checkValue) != Boolean.parseBoolean(value);
				} else {
					result = checkValue.compareTo(value)!=0 ? true : false;
				}
				break;
			case CONTAINS: 
				result = checkValue.contains(value) ? true : false;
				break;
			case STARTSWITH: 
				result = checkValue.startsWith(value) ? true : false;
				break;
			case ENDSWITH: 
				result = checkValue.endsWith(value) ? true : false;
				break;
			case MATCHES: 
				result = checkValue.matches(value) ? true : false;
				break;
			default:
				result = true;
				break;
		}
		return result;
	}
	
	public String toString(){
		String op = null;
		switch(this.operator){
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
			op = "<>";
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
			break;
		}
		
		return this.column + op + this.value;
	}
}
