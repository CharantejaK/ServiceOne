package com.deloitte.mockservice.handler;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deloitte.mockservice.type.ContentType;

@Component
public class AbstractRequestHandlerFactory {

	@Autowired
	private  JsonRequestHandler jsonRequestHandler;

	@Autowired
	private  XmlRequestHandler xmlRequestHandler;
	
	@Autowired
	private DefaultRequestHandler defaultRequestHandler;

	private  HashMap<String, AbstractRequestHandler> requestHandlerMap = new HashMap<>();

	@PostConstruct
	public void initialize() {
		requestHandlerMap.put(ContentType.XML.getName(), xmlRequestHandler);
		requestHandlerMap.put(ContentType.JSON.getName(), jsonRequestHandler);		
	}

	public  AbstractRequestHandler getRequestHandlerByContentType(String type) {
		if (StringUtils.isEmpty(type)) {
			return defaultRequestHandler;
		} else {
		return requestHandlerMap.get(type);
		}
	}
}
