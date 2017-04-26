package com.deloitte.mockservice.mapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import com.deloitte.mockservice.dto.MockDataDto;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.model.MockData;

@Component
public class MockServiceMapper {

	public List<MockDataDto> map(List<MockData> mockDataList) {
		List<MockDataDto> mockDtoList = new ArrayList<MockDataDto>();
		for (MockData mockData : mockDataList) {
			MockDataDto mockDto = new MockDataDto();			
			mockDto.setRequest(mockData.getRequest());
			mockDto.setContenttype(mockData.getContenttype());			
			mockDto.setResponse(mockData.getResponse());
			mockDto.setDescription(mockData.getDescription());
			mockDto.setRequestId(mockData.getId());
			mockDto.setClient(mockData.getClient());
			mockDto.setServiceName(mockData.getServicename());
			mockDto.setId(mockData.getId());
			mockDto.setIsStaticMock(mockData.getIsStaticMock());
			mockDtoList.add(mockDto);
		}
		return mockDtoList;
	}
	
	public MockData map(SaveMockDataRequest saveMockDataRequest) {
		MockData mockData = new MockData();
		mockData.setContenttype(saveMockDataRequest.getContenttype());
		mockData.setRequest(saveMockDataRequest.getRequest());
		mockData.setResponse(saveMockDataRequest.getResponse());
		mockData.setClient(saveMockDataRequest.getClient());
		mockData.setDescription(saveMockDataRequest.getDescription());
		mockData.setServicename(saveMockDataRequest.getServiceName());
		mockData.setCreatedtime(Calendar.getInstance());
		mockData.setCreatedby(saveMockDataRequest.getClient());
		mockData.setId(saveMockDataRequest.getId());
		mockData.setIsStaticMock(saveMockDataRequest.getIsStaticMock());
		return mockData;		
	}
	
	public MockData map(SaveMockDataRequest saveMockDataRequest, MockData mockData) {		
		mockData.setContenttype(saveMockDataRequest.getContenttype());
		mockData.setRequest(saveMockDataRequest.getRequest());
		mockData.setResponse(saveMockDataRequest.getResponse());
		mockData.setClient(saveMockDataRequest.getClient());
		mockData.setDescription(saveMockDataRequest.getDescription());
		mockData.setServicename(saveMockDataRequest.getServiceName());
		mockData.setCreatedtime(Calendar.getInstance());
		mockData.setCreatedby(saveMockDataRequest.getClient());
		mockData.setId(saveMockDataRequest.getId());
		mockData.setIsStaticMock(saveMockDataRequest.getIsStaticMock());
		return mockData;		
	}
}
