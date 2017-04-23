package com.deloitte.mockservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deloitte.mockservice.delegate.MockServiceDelegate;
import com.deloitte.mockservice.dto.DeleteMockDataRequest;
import com.deloitte.mockservice.dto.DeleteMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.dto.ViewMockDataResponse;

@RestController
public class MockupService {
	
	Logger LOG = Logger.getLogger(MockupService.class);
	public static String MOCKSERVICE="/mockservice/";

	@Autowired
	MockServiceDelegate mockServiceDelegate;

	@RequestMapping(value = "/mockservice", method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.OPTIONS,
			RequestMethod.HEAD,RequestMethod.TRACE,RequestMethod.PATCH}, produces = {MediaType.ALL_VALUE })
	public @ResponseBody ResponseEntity<String> getMockResponse(
			@RequestHeader(value = "Content-Type", required=false) String contentType, @RequestBody(required=false) String request,HttpServletRequest servletRequest)  {
		LOG.info("Inside the getMockResponse Service");
		return mockServiceDelegate.getMockResponse(request, contentType, servletRequest.getAttribute("serviceName").toString());
	}
	
	
	@RequestMapping(value = "/getmockdatalist", method = RequestMethod.GET, produces = (MediaType.APPLICATION_JSON_VALUE))
	public @ResponseBody ResponseEntity<ViewMockDataResponse> getMockDataByClient(@RequestParam String clientName)  {
		LOG.info("Inside the getMockDataByClient Service");
		return mockServiceDelegate.getMockDataByClient(clientName);
	}
	
	
	@RequestMapping(value = "/savemockdata", method = RequestMethod.POST, produces = (MediaType.APPLICATION_JSON_VALUE))
	public @ResponseBody ResponseEntity<SaveMockDataResponse> saveMockData(@RequestBody SaveMockDataRequest saveMockDataRequest)  {
		LOG.info("Inside the saveMockData Service");
		return mockServiceDelegate.saveMockData(saveMockDataRequest);
	}
	
	@RequestMapping(value = "/deletemockdata", method = RequestMethod.POST, produces = (MediaType.APPLICATION_JSON_VALUE))
	public @ResponseBody ResponseEntity<DeleteMockDataResponse> deleteMockData(@RequestBody DeleteMockDataRequest request)  {
		LOG.info("Inside the delete Mock Data Response Service");
		return mockServiceDelegate.deleteMockData(request);
	}
}