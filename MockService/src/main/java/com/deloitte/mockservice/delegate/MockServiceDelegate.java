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
import com.deloitte.mockservice.dto.ErrorCode;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.dto.ViewMockDataResponse;
import com.deloitte.mockservice.mapper.MockServiceMapper;
import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.deloitte.mockservice.util.MockServiceUtil;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class MockServiceDelegate {
	Logger LOG = Logger.getLogger(MockServiceDelegate.class);

	private static String DUPLICATE_REQUEST = "00001";
	private static String DUPLICATE_REQUEST_MESSAGE = "Duplicate request and response. An entry with the same request and response already exists.";
	private static String INVALID_REQUEST = "00002";
	private static String INVALID_RESPONSE = "Invalid request or response . Please check the request and responses are valid as per the selected content type;";
	private static String FORWARD_SLASH ="/";
	private static String BLANK = "";
	

	@Autowired
	MockDataDao mockDataDao;
	
	@Autowired
	MockServiceMapper mockServiceMapper;
	
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
		List<ErrorCode> errorList = new ArrayList<>();
		response.setErrorList(errorList);
		try {
			String requestStr = "";
			String responseStr = "";
			if (mockDataRequest.getContenttype().equals(MediaType.APPLICATION_JSON_VALUE)) {
				if (!StringUtils.isEmpty(mockDataRequest.getRequest())) {
					requestStr = MockServiceUtil.getFormattedJsonString(mockDataRequest.getRequest());
				}
				responseStr = MockServiceUtil.getFormattedJsonString(mockDataRequest.getResponse());
			} else if (mockDataRequest.getContenttype().equals(MediaType.APPLICATION_XML_VALUE)) {
				if (!StringUtils.isEmpty(mockDataRequest.getRequest())) {
					requestStr = MockServiceUtil.getFormattedXmlString(mockDataRequest.getRequest());
				}
				responseStr = MockServiceUtil.getFormattedXmlString(mockDataRequest.getResponse());
			}			
			List<MockData> mockDataList = mockDataDao.findByRequestAndResponseAndServicename(requestStr, responseStr, mockDataRequest.getServiceName());
			if (mockDataList == null || mockDataList.isEmpty()) {
				mockDataRequest.setRequest(requestStr);
				mockDataRequest.setResponse(responseStr);
			} else {
				ErrorCode errorCode = new ErrorCode();
				errorCode.setErrorCode(DUPLICATE_REQUEST);
				errorCode.setErrorMessage(DUPLICATE_REQUEST_MESSAGE);
				response.getErrorList().add(errorCode);
			}		
		} catch (MockServiceSystemException e) {
			ErrorCode errorCode = new ErrorCode();
			errorCode.setErrorCode(INVALID_REQUEST);
			errorCode.setErrorMessage(INVALID_RESPONSE);
			response.getErrorList().add(errorCode);		
		}
		if (response.getErrorList().isEmpty()) {
		MockData mockData = mockDataDao.save(mockServiceMapper.map(mockDataRequest));
		response.setRequestId(mockData.getId());
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
		String response = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		String service[] = serviceName.split(MOCKSERVICE);
		try {
			// Gets the MockData from the DB based on the Service Name
			List<MockData> mockDataList = mockDataDao.findByServicename(service[1].replace(FORWARD_SLASH, BLANK));
			for (MockData mockData : mockDataList) {
				if (!StringUtils.isEmpty(request)) {
					if ((contentType.equals(MediaType.APPLICATION_JSON_VALUE)) && (MockServiceUtil.isSameJsonIgnoringValues(request, mockData.getRequest()))) {

						// gets the response from the MockData for the request ServiceName
							response = mockData.getResponse();
							MockServiceUtil.convertJsonStrToMap(request);
						// Sets the content type in the response header
						httpHeaders.setContentType(ContentType.findByName(mockData.getContenttype()).getType());
						break;					
					}
					else if ((contentType.equals(MediaType.APPLICATION_XML_VALUE)) && (MockServiceUtil.isSameXmlIgnoringValues(request, mockData.getRequest()))) {
						// gets the response from the MockData for the request ServiceName
							response = mockData.getResponse();
						// Sets the content type in the response header
						httpHeaders.setContentType(ContentType.findByName(mockData.getContenttype()).getType());
						break;
					}
				} else {
					if (StringUtils.isEmpty(mockData.getRequest())) {
						response = mockData.getResponse();
						break;
					}
				}
			}
		} catch (MockServiceSystemException e) {
			LOG.error("Exception inside getMockResponse", e);
		}
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}	
}
