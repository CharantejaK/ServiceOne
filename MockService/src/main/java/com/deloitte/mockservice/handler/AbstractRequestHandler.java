package com.deloitte.mockservice.handler;

import com.delolitte.mockservice.exception.MockServiceSystemException;

public abstract class AbstractRequestHandler {
	
	private Boolean dynamicResponse = true;
	
	private Integer responseRepeatCount = 1;
	
	private String request;
	
	private String response;
	
	public abstract String getMockResponse() throws MockServiceSystemException;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
	
	public Boolean IsDynamicResponse() {
		return dynamicResponse;
	}

	public void setDynamicResponse(Boolean dynamicResponse) {
		this.dynamicResponse = dynamicResponse;
	}

	public Integer getResponseRepeatCount() {
		return responseRepeatCount;
	}

	public void setResponseRepeatCount(Integer responseRepeatCount) {
		this.responseRepeatCount = responseRepeatCount;
	}
}
