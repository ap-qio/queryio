package com.queryio.core.bean;


public class HadoopConfig {
	private String type;
	private String key;
	private String value = "";
	private String description = "";
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
		
	@Override 
	public String toString(){
		return "Key: " + this.key + " Value: " + this.value + " Description: " + this.description;
	}
	
	@Override 
	public boolean equals(Object o){
		boolean result = false;
		if(o instanceof HadoopConfig){
			result = true;
			HadoopConfig hc = (HadoopConfig) o;
			if(result && (this.key == null || !hc.key.equals(this.key)))
				result = false;
			if(result && (this.value == null || !hc.value.equals(this.value)))
				result = false;
			if(result && (this.type == null || !hc.type.equals(this.type)))
				result = false;
			if(result && (this.description == null || !hc.description.equals(this.description)))
				result = false;			
		}
		return result;
	}
	
	@Override
	public int hashCode(){
		int result = 1;
		if(this.key != null)
			result += result * 31 + (this.key == null ? 0 : this.key.hashCode());
		if(this.value != null)
			result += result * 31 + (this.value == null ? 0 : this.value.hashCode());
		if(this.type != null)
			result += result * 31 + (this.type == null ? 0 : this.type.hashCode());
		if(this.description != null)
			result += result * 31 + (this.description == null ? 0 : this.description.hashCode());
		return result;
	}	
}
