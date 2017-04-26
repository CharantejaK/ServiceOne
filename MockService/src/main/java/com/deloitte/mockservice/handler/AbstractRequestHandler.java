package com.deloitte.mockservice.handler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.deloitte.mockservice.dao.MockDataDao;
import com.deloitte.mockservice.dto.GetMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.mapper.MockServiceMapper;
import com.deloitte.mockservice.model.MockData;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public abstract class AbstractRequestHandler {	
	
	protected static String BLANK = "";
	
	protected String request;	
	
	protected String serviceName;	
	
	protected String contentType;
	
	@Autowired
	MockDataDao mockDataDao;
	
	@Autowired
	MockServiceMapper mockServiceMapper;

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setRequest(String request) {
		this.request = request;
	}	

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public abstract GetMockDataResponse getMockResponse() throws MockServiceSystemException;
	
	public abstract SaveMockDataResponse saveMockData(SaveMockDataRequest request) throws MockServiceSystemException;
		
	protected List<MockData> getMockDataListOfStaticMockObjects() {
		List<MockData> mockDataList = mockDataDao.findByServicenameAndIsStaticMockAndContenttype(serviceName, Boolean.TRUE, contentType);
		return mockDataList;
	}
	
	protected List<MockData> getMockDataListOfDynamicMockObjects() {
		List<MockData> mockDataList = mockDataDao.findByServicenameAndIsStaticMockAndContenttype(serviceName, Boolean.FALSE, contentType);
		return mockDataList;
	}
	
	protected List<MockData> getMockData() {
		return mockDataDao.findByServicename(serviceName);
	}

	public  GetMockDataResponse getMockDataForEmptyRequest() throws MockServiceSystemException{
		try {
		GetMockDataResponse response = new GetMockDataResponse();
		MockData mockData = mockDataDao.findByServicenameAndContenttypeAndRequestAndIsStaticMock(serviceName, Boolean.TRUE, contentType, request);			
		response.setResponse(mockData.getResponse());
		response.setContentType(mockData.getContenttype());
		return response;
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}
	}	
	
	public Long saveRequest(SaveMockDataRequest mockDataRequest) {
		MockData mockData = mockDataDao.save(mockServiceMapper.map(mockDataRequest));
		return mockData.getId();
	}
}
