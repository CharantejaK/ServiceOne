package com.deloitte.mockservice.handler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.deloitte.mockservice.dto.ErrorCode;
import com.deloitte.mockservice.dto.GetMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.deloitte.mockservice.util.MockServiceUtil;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class XmlRequestHandler extends AbstractRequestHandler {		
	
	private static String DUPLICATE_EMPTY_REQUEST_CODE = "00006";
	private static String DUPLICATE_EMPTY_REQUEST_MESSAGE = "Only one Empty request is allowed for a XML Mock Request";
	private static String DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_CODE = "00007";
	private static String DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_MESSAGE = "Empty request is not allowed for a XML Dynamic Mock Request";
	private static String DUPLICATE_MOCK_REQUEST_CODE = "00008";
	private static String DUPLICATE_MOCK_REQUEST_MESSAGE = "A XML with similar schema already exists";	
	private static String INVALID_REQUEST_CODE = "00009";
	private static String INVALID_REQUEST_MESSAGE = "Invalid request or response XML String. Please check the request and responses are in valid d XML Format.";
	
	public XmlRequestHandler() {
		super();
		this.contentType = ContentType.XML.getName();
	}
	
	@Override
	public GetMockDataResponse getMockResponse() throws MockServiceSystemException {
		GetMockDataResponse response = new GetMockDataResponse();
		try {			
			List<MockData> staticValidationsList = getMockDataListOfStaticMockObjects();
			// Irrespective of isSchema validations true or false it is sending
			// only static response from the db by matching the request schema
			// with the db
			// needs to implemet similar functionality as json
			if (staticValidationsList != null && !staticValidationsList.isEmpty()) {
				for (MockData mockData : staticValidationsList) {
					if (isSameXmlIgnoringValues(getFormattedXmlString(request), mockData.getRequest())) {
						response.setResponse(mockData.getResponse());
						response.setContentType(mockData.getContenttype());
					}
				}
			}
			List<MockData> dynamicMockList = getMockDataListOfDynamicMockObjects();
			if (dynamicMockList != null && !dynamicMockList.isEmpty()) {
				for (MockData mockData : dynamicMockList) {
					if (isSameXmlIgnoringValues(getFormattedXmlString(request), mockData.getRequest())) {
						response.setResponse(mockData.getResponse());
						response.setContentType(mockData.getContenttype());
						return response;
					}
				}
			}
		} catch (MockServiceSystemException e) {
			throw e;
		}
		return response;
	}
	
	@Override
	public SaveMockDataResponse saveMockData(SaveMockDataRequest mockDataRequest) throws MockServiceSystemException {
		SaveMockDataResponse saveMockDataResponse = new SaveMockDataResponse();		
		try {
			if (StringUtils.isEmpty(mockDataRequest.getRequest())) {
				mockDataRequest.setRequest(BLANK);
				mockDataRequest.setResponse(getFormattedXmlString(mockDataRequest.getResponse()));
				validateEmptyRequest(mockDataRequest, saveMockDataResponse);
			} else {
				mockDataRequest.setRequest(getFormattedXmlString(mockDataRequest.getRequest()));
				mockDataRequest.setResponse(getFormattedXmlString(mockDataRequest.getResponse()));
				validateRequest(mockDataRequest, saveMockDataResponse);
			}
			if (saveMockDataResponse.getErrorList().isEmpty()) {
				saveMockDataResponse.setRequestId(saveRequest(mockDataRequest));
			}
		} catch (MockServiceSystemException e) {
			ErrorCode errorCode = new ErrorCode();
			errorCode.setErrorCode(INVALID_REQUEST_CODE);
			errorCode.setErrorMessage(INVALID_REQUEST_MESSAGE);
			saveMockDataResponse.getErrorList().add(errorCode);
		}
		return saveMockDataResponse;
	}

	/**
	 * @param xmlStr1
	 * @param xmlStr2
	 * @return
	 * @throws MockServiceSystemException
	 * Compare both the xml Strings true if both the schema are same
	 */
	private Boolean isSameXmlIgnoringValues(String xmlStr1, String xmlStr2) throws MockServiceSystemException {
		try {
			Document document1 = getDocument(xmlStr1);
			Document document2 = getDocument(xmlStr2);
			NodeList nodeList = document1.getElementsByTagName("*");
			NodeList nodeList2 = document2.getElementsByTagName("*");
			ArrayList<String> list1 = new ArrayList<>();
			ArrayList<String> list2 = new ArrayList<>();
			if (nodeList.getLength() != nodeList2.getLength()) {
				return false;
			}
			for (int i = 0; i < nodeList.getLength(); i++) {
				list1.add(nodeList.item(i).getNodeName());
				list2.add(nodeList2.item(i).getNodeName());
			}
			return MockServiceUtil.isSame(list1, list2);
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}
	}

	/**
	 * @param xmlStr
	 * @return
	 * @throws MockServiceSystemException
	 * Accepts the xml String and send the corrsponding document for the String
	 */
	private Document getDocument(String xmlStr) throws MockServiceSystemException {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(xmlStr)));
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new MockServiceSystemException(e);
		}
		return document;
	}

	/**
	 * @param xmlStr
	 * @return
	 * @throws validates
	 *             the xml and throws MockServiceSystemException in case of
	 *             invalid xml
	 * 
	 */
	private String getFormattedXmlString(String xmlStr) throws MockServiceSystemException {
		String formattedXmlStr = null;
		try {
			Document doc = getDocument(xmlStr);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			formattedXmlStr = writer.getBuffer().toString();
		} catch (TransformerException e) {
			throw new MockServiceSystemException(e);
		}
		return formattedXmlStr;
	}
	
	private void validateEmptyRequest(SaveMockDataRequest mockDataRequest, SaveMockDataResponse response)
			throws MockServiceSystemException {
		if (mockDataRequest.getIsStaticMock()) {
			MockData mockData = mockDataDao.findByServicenameAndContenttypeAndRequestAndIsStaticMock(serviceName, Boolean.TRUE, contentType, mockDataRequest.getRequest());
			if (mockData != null && (mockDataRequest.getId() != mockData.getId())) {
				ErrorCode errorCode = new ErrorCode();
				errorCode.setErrorCode(DUPLICATE_EMPTY_REQUEST_CODE);
				errorCode.setErrorMessage(DUPLICATE_EMPTY_REQUEST_MESSAGE);
				response.getErrorList().add(errorCode);
			}
		} else {
			ErrorCode errorCode = new ErrorCode();
			errorCode.setErrorCode(DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_CODE);
			errorCode.setErrorMessage(DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_MESSAGE);
			response.getErrorList().add(errorCode);
		}
	}
	
	private void validateRequest(SaveMockDataRequest mockDataRequest, SaveMockDataResponse response)
			throws MockServiceSystemException {
		List<MockData> mockDataList = mockDataDao.findByServicenameAndContenttype(serviceName, contentType);
		if (mockDataList != null && !mockDataList.isEmpty()) {
			for (MockData mockData : mockDataList) {
				if (mockDataRequest.getId() != mockData.getId()) {
					if ((isSameXmlIgnoringValues(mockDataRequest.getRequest(), mockData.getRequest()))) {
						ErrorCode errorCode = new ErrorCode();
						errorCode.setErrorCode(DUPLICATE_MOCK_REQUEST_CODE);
						errorCode.setErrorMessage(DUPLICATE_MOCK_REQUEST_MESSAGE);
						response.getErrorList().add(errorCode);
					}
				}
			}
		}
	}
}
