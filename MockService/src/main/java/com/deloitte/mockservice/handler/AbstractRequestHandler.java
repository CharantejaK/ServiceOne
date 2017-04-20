package com.deloitte.mockservice.handler;

import java.util.List;

import com.deloitte.mockservice.model.MockData;
import com.delolitte.mockservice.exception.MockServiceSystemException;

public abstract class AbstractRequestHandler {
	
	protected Boolean dynamicResponse = true;
	
	protected Integer responseRepeatCount = 1;
	
	protected String request;
	
	protected String response;
	
	protected String serviceName;
	
	protected List<MockData> mockDataList;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Boolean getDynamicResponse() {
		return dynamicResponse;
	}

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

	public List<MockData> getMockDataList() {
		return mockDataList;
	}

	public void setMockDataList(List<MockData> mockDataList) {
		this.mockDataList = mockDataList;
	}
	
}
