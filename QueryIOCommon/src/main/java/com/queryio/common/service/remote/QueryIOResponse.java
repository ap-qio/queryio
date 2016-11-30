package com.queryio.common.service.remote;

public class QueryIOResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4992717459082878298L;
	private String responseMsg;
	private boolean successfull;

	public QueryIOResponse(boolean successfull, String responseMsg) {
		this.successfull = successfull;
		this.responseMsg = responseMsg;
	}

	public String getResponseMsg() {
		return this.responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public boolean isSuccessful() {
		return this.successfull;
	}

	public void setSuccessful(boolean successfull) {
		this.successfull = successfull;
	}

	public String toString() {
		return getResponseMsg();
	}
}
