package com.deloitte.mockservice.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deloitte.mockservice.dto.ErrorCode;
import com.deloitte.mockservice.dto.GetMockDataResponse;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.deloitte.mockservice.util.MockServiceUtil;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class JsonRequestHandler extends AbstractRequestHandler{	
	
	private static String DUPLICATE_EMPTY_REQUEST_CODE = "00001";
	private static String DUPLICATE_EMPTY_REQUEST_MESSAGE = "Only one Empty request is allowed for a JSON Mock Request";
	private static String DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_CODE = "00002";
	private static String DYNAMIC_EMPTY_REQUEST_NOT_ALLOWED_MESSAGE = "Empty request is not allowed for a JSON Dynamic Mock Request";
	private static String DUPLICATE_MOCK_REQUEST_CODE = "00003";
	private static String DUPLICATE_MOCK_REQUEST_MESSAGE = "A Json with similar values already exists";
	private static String DUPLICATE_DYNAMIC_MOCK_REQUEST_CODE = "00004";
	private static String DUPLICATE_DYNAMIC_MOCK_REQUEST_MESSAGE = "A Dynamic Mock Json with similar schema already exists";
	private static String INVALID_REQUEST_CODE = "00005";
	private static String INVALID_REQUEST_MESSAGE = "Invalid request or response Json String. Please check the request and responses are in valid d Json Format.";
	
	public JsonRequestHandler() {
		super();
		this.contentType = ContentType.JSON.getName();
	}
	
	@Override
	public GetMockDataResponse getMockResponse() throws MockServiceSystemException {	
		GetMockDataResponse response = new GetMockDataResponse();
		try {
			JSONObject requestJson = new JSONObject(request);
			List<MockData> staticMockList = getMockDataListOfStaticMockObjects();	
			if (staticMockList != null && !staticMockList.isEmpty()) {
				for (MockData mockData : staticMockList) {
					JSONObject mockJsonRequest = new JSONObject(getFormattedJsonString(mockData.getRequest()));
					if ((isSameJson(requestJson, mockJsonRequest, Boolean.FALSE))) {
						// gets the json from the DB for the request which
						// matches exactly with the request json values
						// irrespective of the json order
						response.setResponse(getFormattedJsonString(mockData.getResponse()));
						response.setContentType(mockData.getContenttype());
						return response;
					}
				}
			}
			List<MockData> dynamicMockList = getMockDataListOfDynamicMockObjects();
			if (dynamicMockList != null && !dynamicMockList.isEmpty()) {
				for (MockData mockData : dynamicMockList) {
					JSONObject mockJsonRequest = new JSONObject(mockData.getRequest());
					JSONObject mockJsonResponse = new JSONObject(mockData.getResponse());
					if ((isSameJson(requestJson, mockJsonRequest, Boolean.TRUE))) {
						// gets the json response dynamically based on the
						// request key and values
						response.setResponse(getDynamicResponse(requestJson, mockJsonResponse));
						response.setContentType(mockData.getContenttype());
						return response;
					} else if (isRequestList(requestJson, mockJsonRequest, Boolean.TRUE)) {
						// gets the json dynamic response list based on the
						// request json list
						response.setResponse(getDynamicResponseList(requestJson, mockJsonResponse));
						response.setContentType(mockData.getContenttype());
						return response;
					}
				}
			}
		} catch (JSONException e) {			
			throw new MockServiceSystemException(e);
		}
		return response;
	}
	

	@Override
	public SaveMockDataResponse saveMockData(SaveMockDataRequest mockDataRequest)
			throws MockServiceSystemException {
		SaveMockDataResponse saveMockDataResponse = new SaveMockDataResponse();	
		try {
			if (StringUtils.isEmpty(mockDataRequest.getRequest())) {
				mockDataRequest.setRequest(BLANK);
				mockDataRequest.setResponse(getFormattedJsonString(mockDataRequest.getResponse()));
				validateEmptyRequest(mockDataRequest, saveMockDataResponse);
			} else {
				mockDataRequest.setRequest(getFormattedJsonString(mockDataRequest.getRequest()));
				mockDataRequest.setResponse(getFormattedJsonString(mockDataRequest.getResponse()));
				validateRequest(mockDataRequest, saveMockDataResponse);
			}
			if (saveMockDataResponse.getErrorList().isEmpty()) {
				saveMockDataResponse.setRequestId(saveRequest(mockDataRequest));
			}
		} catch (JSONException e) {
			ErrorCode errorCode = new ErrorCode();
			errorCode.setErrorCode(INVALID_REQUEST_CODE);
			errorCode.setErrorMessage(INVALID_REQUEST_MESSAGE);
			saveMockDataResponse.getErrorList().add(errorCode);
		}
		return saveMockDataResponse;
	}
	
	/**
	 * @param requestJson
	 * @param responseJson
	 * @return
	 * @throws MockServiceSystemException
	 */
	private  String getDynamicResponse(JSONObject requestJson, JSONObject responseJson) throws MockServiceSystemException {
		Map<String, String> map;
		try {			
			map = convertJsonStrToMap(requestJson);
			Set<String> keySet = map.keySet();			
			for (String key: keySet) {
				fetchChildNodes(responseJson, key, map.get(key));
			}
			return responseJson.toString();
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}		
	}
	
	/**
	 * @param requestJson
	 * @param responseJson
	 * @return
	 * @throws MockServiceSystemException
	 */
	private  String getDynamicResponseList(JSONObject requestJson, JSONObject responseJson) throws MockServiceSystemException {
		List<String> responseList = new ArrayList<>();
		try {			
			String firstElement = (String) requestJson.keys().next();
			if (requestJson.optJSONArray(firstElement) != null) {
				JSONArray array = requestJson.getJSONArray(firstElement);
				for (int i=0;i<array.length();i++) {
					String responseStr = getDynamicResponse(array.getJSONObject(i), responseJson);
					responseList.add(responseStr);
				}
            }
			return responseList.toString();
		} catch (JSONException e) {
			throw new MockServiceSystemException(e);
		}
		
	}
	
	  /**
	 * @param obj
	 * @param keyMain
	 * @param newValue
	 * @return
	 * @throws Exception
	 */
	private static JSONObject fetchChildNodes(JSONObject obj, String keyMain, String newValue) throws Exception {		    
		    Iterator iterator = obj.keys();
		    String key = null;
		    while (iterator.hasNext()) {
		        key = (String) iterator.next();
		        // if object is just string we change value in key
		        if ((obj.optJSONArray(key)==null) && (obj.optJSONObject(key)==null)) {
		            if ((key.equals(keyMain))) {
		                // put new value
		                obj.put(key, newValue);
		                return obj;
		            }
		        }

		        // if it's jsonobject
		        if (obj.optJSONObject(key) != null) {
		        	fetchChildNodes(obj.getJSONObject(key), keyMain, newValue);
		        }

		        // if it's jsonarray
		        if (obj.optJSONArray(key) != null) {
		            JSONArray jArray = obj.getJSONArray(key);
		            for (int i=0;i<jArray.length();i++) {
		            	fetchChildNodes(jArray.getJSONObject(i), keyMain,  newValue);
		            }
		        }
		    }
		    return obj;
		}
	  
	  /**
	 * @param body
	 * @return
	 * @throws MockServiceSystemException
	 * converts the json string into a  Map
	 */
	private  Map<String, String> convertJsonStrToMap(JSONObject body) throws MockServiceSystemException {
			try {				
				HashMap<String, String> map = new HashMap<String, String>();
				map = convertJsonToMap(body, null, map);
				return map;
			} catch (JSONException e) {
				throw new MockServiceSystemException(e);
			}
		}

	  /**
	 * @param obj1
	 * @param obj2
	 * @param isSchemaOnlyComparision : depending upon this arguement the methods either compares only schema or schema with values
	 * @return
	 * @throws JSONException
	 * Compares both the Json objects and returns true if they are same
	 */
	private boolean isSameJson(Object obj1, Object obj2, Boolean isSchemaOnlyComparision) throws JSONException {
		try {
			if (obj1 instanceof JSONObject) {
				JSONObject jsonObj1 = (JSONObject) obj1;

				JSONObject jsonObj2 = (JSONObject) obj2;

				ArrayList<String> names = MockServiceUtil.getList(jsonObj1.keys());
				ArrayList<String> names2 = MockServiceUtil.getList(jsonObj2.keys());
				if (names.size() != names2.size()) {
					return false;
				}

				for (String fieldName : names) {
					Object obj1FieldValue = jsonObj1.get(fieldName);

					Object obj2FieldValue = jsonObj2.get(fieldName);

					if (!isSameJson(obj1FieldValue, obj2FieldValue, isSchemaOnlyComparision)) {
						return false;
					}
				}
			} else if (obj1 instanceof JSONArray) {
				JSONArray obj1Array = (JSONArray) obj1;
				JSONArray obj2Array = (JSONArray) obj2;

				if (obj1Array.length() != obj2Array.length()) {
					return false;
				}

				for (int i = 0; i < obj1Array.length(); i++) {
					boolean matchFound = false;

					for (int j = 0; j < obj2Array.length(); j++) {
						if (isSameJson(obj1Array.get(i), obj2Array.get(j), isSchemaOnlyComparision)) {
							matchFound = true;
							break;
						}
					}

					if (!matchFound) {
						return false;
					}
				}
			} else if (!isSchemaOnlyComparision) {
				if (!obj1.equals(obj2)) {
					return false;
				}
			}
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

		/**
		 * @param obj1
		 * @param keyname
		 * @param out
		 * @return
		 * @throws JSONException
		 */
		private  HashMap<String, String> convertJsonToMap(Object obj1, String keyname, HashMap<String, String> out)
				throws JSONException {
			if (obj1 instanceof JSONObject) {
				JSONObject jsonObj1 = (JSONObject) obj1;
				ArrayList<String> names = MockServiceUtil.getList(jsonObj1.keys());
				for (String fieldName : names) {
					Object obj1FieldValue = jsonObj1.get(fieldName);
					convertJsonToMap(obj1FieldValue, fieldName, out);
				}
			} else if (obj1 instanceof JSONArray) {
				JSONArray obj1Array = (JSONArray) obj1;

				for (int i = 0; i < obj1Array.length(); i++) {
					convertJsonToMap(obj1Array.get(i), null, out);

				}
			} else {
				if (obj1 != null) {
					out.put(keyname, obj1.toString());
				} else {
					out.put(keyname, null);
				}
			}
			return out;
		}		
		
	/**
	 * @param jsonRequestList
	 * @param jsonRequest
	 * @param isValueValidationsRequired
	 * @return
	 * @throws MockServiceSystemException
	 * Returns true if jsonRequest matches with any of the element from the jsonRequestList Array 
	 */
	public Boolean isRequestList(JSONObject jsonRequestList, JSONObject jsonRequest, Boolean isValueValidationsRequired) throws MockServiceSystemException {
		try {
			if (jsonRequestList.length() == 1) {
				String firstElement = (String) jsonRequestList.keys().next();
				if (jsonRequestList.optJSONArray(firstElement) != null) {
					JSONArray array = jsonRequestList.getJSONArray(firstElement);
					for (int i = 0; i < array.length(); i++) {
						if (isSameJson(array.getJSONObject(i), jsonRequest, isValueValidationsRequired)) {
							return true;
						}
					}

				}
			}
			return false;
		} catch (JSONException e) {
			return false;
		}
	}

	/**
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	private String getFormattedJsonString(String json) throws JSONException {
		String jsonStr = null;
		JSONObject body =  new JSONObject(json);
		return body.toString();		
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
		try {
			List<MockData> mockDataList = mockDataDao.findByServicenameAndContenttype(serviceName, contentType);
			if (mockDataList != null && !mockDataList.isEmpty()) {
				for (MockData mockData : mockDataList) {
					if (mockData.getId() != mockDataRequest.getId()) {
						JSONObject requestJson = new JSONObject(mockDataRequest.getRequest());
						JSONObject mockJson = new JSONObject(mockData.getRequest());
						if ((isSameJson(requestJson, mockJson, Boolean.FALSE))) {
							ErrorCode errorCode = new ErrorCode();
							errorCode.setErrorCode(DUPLICATE_MOCK_REQUEST_CODE);
							errorCode.setErrorMessage(DUPLICATE_MOCK_REQUEST_MESSAGE);
							response.getErrorList().add(errorCode);
						}
						if (!mockDataRequest.getIsStaticMock() && !mockData.getIsStaticMock()
								&& (isSameJson(requestJson, mockJson, Boolean.TRUE))) {
							ErrorCode errorCode = new ErrorCode();
							errorCode.setErrorCode(DUPLICATE_DYNAMIC_MOCK_REQUEST_CODE);
							errorCode.setErrorMessage(DUPLICATE_DYNAMIC_MOCK_REQUEST_MESSAGE);
							response.getErrorList().add(errorCode);

						}
					}
				}
			}
		} catch (JSONException e) {
			throw new MockServiceSystemException(e);
		}
	}
}
