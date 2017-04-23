package com.deloitte.mockservice.delegate;

import static com.deloitte.mockservice.controller.MockupService.MOCKSERVICE;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deloitte.mockservice.dao.MockDataDao;
import com.deloitte.mockservice.dto.DeleteMockDataRequest;
import com.deloitte.mockservice.dto.DeleteMockDataResponse;
import com.deloitte.mockservice.dto.ErrorCode;
import com.deloitte.mockservice.dto.GetMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.dto.ViewMockDataResponse;
import com.deloitte.mockservice.handler.AbstractRequestHandler;
import com.deloitte.mockservice.handler.AbstractRequestHandlerFactory;
import com.deloitte.mockservice.mapper.MockServiceMapper;
import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.deloitte.mockservice.util.MockServiceUtil;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class MockServiceDelegate {
	Logger LOG = Logger.getLogger(MockServiceDelegate.class);	
	private static String FORWARD_SLASH = "/";
	private static String BLANK = "";

	@Autowired
	MockDataDao mockDataDao;

	@Autowired
	MockServiceMapper mockServiceMapper;

	@Autowired
	AbstractRequestHandlerFactory requestHandlerFactory;

	public ResponseEntity<ViewMockDataResponse> getMockDataByClient(String client) {
		ViewMockDataResponse response = new ViewMockDataResponse();
		List<MockData> mockDataList = mockDataDao.findByClient(client.toLowerCase());
		response.setMockDataList(mockServiceMapper.map(mockDataList));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}

	public ResponseEntity<SaveMockDataResponse> saveMockData(SaveMockDataRequest mockDataRequest) {
		SaveMockDataResponse response = new SaveMockDataResponse();
		mockDataRequest.setIsStaticMock(Boolean.FALSE);		
		List<ErrorCode> errorList = new ArrayList<>();
		response.setErrorList(errorList);
		try {
			AbstractRequestHandler requestHandler = requestHandlerFactory.getRequestHandlerByContentType(mockDataRequest.getContenttype());
			String serviceName = mockDataRequest.getServiceName().replaceAll(FORWARD_SLASH, BLANK);
			requestHandler.setServiceName(serviceName);			
			response = requestHandler.saveMockData(mockDataRequest);
					
		} catch (MockServiceSystemException e) {
			LOG.error("Exception inside save Mock Data request", e);
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}

	/**
	 * @param request
	 * @param contentType
	 * @param id
	 * @return
	 */
	public ResponseEntity<String> getMockResponse(String request, String contentType, String serviceName) {
		LOG.info("Inside get Mock Response method inside the mock helper id=" + serviceName + " request =" + request);
		HttpHeaders httpHeaders = new HttpHeaders();
		GetMockDataResponse mockResponse = new GetMockDataResponse();
		try {
			String service[] = serviceName.split(MOCKSERVICE);
			AbstractRequestHandler requestHandler = requestHandlerFactory.getRequestHandlerByContentType(contentType);
			requestHandler.setServiceName(service[1].replace(FORWARD_SLASH, BLANK));
			requestHandler.setRequest(request);
			if (!StringUtils.isEmpty(contentType) && StringUtils.isEmpty(request)) {
				requestHandler.setRequest(BLANK);
				mockResponse = requestHandler.getMockDataForEmptyRequest();
			} else {
				mockResponse = requestHandler.getMockResponse();
			}
		} catch (MockServiceSystemException e) {
			LOG.error("Exception inside getMockResponse", e);
		}
		if (StringUtils.isEmpty(mockResponse.getResponse())) {
			return new ResponseEntity<>(BLANK, httpHeaders, HttpStatus.OK);
		} else {
			httpHeaders.setContentType(ContentType.findByName(mockResponse.getContentType()).getType());
			return new ResponseEntity<>(mockResponse.getResponse(), httpHeaders, HttpStatus.OK);
		}
	}

	/**
	 * @param id
	 * @return ResponseEntity<DeleteMockDataResponse>
	 */
	public ResponseEntity<DeleteMockDataResponse> deleteMockData(DeleteMockDataRequest request) {
		DeleteMockDataResponse response = new DeleteMockDataResponse();
		HttpHeaders httpHeaders = new HttpHeaders();
		try {
			mockDataDao.deleteById(request.getId());
			response.setResult(Boolean.TRUE);
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		} catch (Exception e) {
			response.setResult(Boolean.FALSE);
		}
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}
	
}
