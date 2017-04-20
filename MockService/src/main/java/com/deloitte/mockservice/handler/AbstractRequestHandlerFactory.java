package com.deloitte.mockservice.handler;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.deloitte.mockservice.type.ContentType;

public class AbstractRequestHandlerFactory {

	@Autowired
	private static JsonRequestHandler jsonRequestHandler;

	@Autowired
	private static XmlRequestHandler xmlRequestHandler;

	private static HashMap<String, AbstractRequestHandler> requestHandlerMap = new HashMap<>();

	static {
		requestHandlerMap.put(ContentType.XML.getName(), xmlRequestHandler);
		requestHandlerMap.put(ContentType.JSON.getName(), jsonRequestHandler);
	}

	public static AbstractRequestHandler getRequestHandlerByContentType(String type) {
		return requestHandlerMap.get(type);
	}

}
