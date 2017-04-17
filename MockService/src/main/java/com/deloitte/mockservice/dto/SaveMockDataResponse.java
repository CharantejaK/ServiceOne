package com.deloitte.mockservice.dto;

import java.util.List;

public class SaveMockDataResponse {	
	Long requestId;
	
	List<ErrorCode> errorList;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public List<ErrorCode> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<ErrorCode> errorList) {
		this.errorList = errorList;
	}
}
