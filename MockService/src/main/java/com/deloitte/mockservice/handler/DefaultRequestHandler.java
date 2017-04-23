package com.deloitte.mockservice.handler;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deloitte.mockservice.dto.GetMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.model.MockData;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class DefaultRequestHandler extends AbstractRequestHandler {

	@Override
	public GetMockDataResponse getMockResponse() throws MockServiceSystemException {
		GetMockDataResponse response = new GetMockDataResponse();
		try {
			List<MockData> mockDataList = getMockData();
			for (MockData mockData : mockDataList) {
				if (StringUtils.isEmpty(request) && StringUtils.isEmpty(mockData.getRequest())) {
					response.setResponse(mockData.getResponse());
					response.setContentType(mockData.getContenttype());					
				}
			}
			return response;
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}
	}

	@Override
	public SaveMockDataResponse saveMockData(SaveMockDataRequest request) throws MockServiceSystemException {
		return null;
	}

}
