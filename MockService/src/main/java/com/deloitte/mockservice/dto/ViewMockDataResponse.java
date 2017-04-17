package com.deloitte.mockservice.dto;

import java.util.List;

public class ViewMockDataResponse {
	
	private List<MockDataDto> mockDataList;

	public List<MockDataDto> getMockDataList() {
		return mockDataList;
	}

	public void setMockDataList(List<MockDataDto> mockDataList) {
		this.mockDataList = mockDataList;
	}

}
