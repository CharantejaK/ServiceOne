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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.deloitte.mockservice.util.MockServiceUtil;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class JsonRequestHandler extends AbstractRequestHandler{

	@Override
	public String getMockResponse() throws MockServiceSystemException {		
		try {
			JSONObject requestJson = new JSONObject(request);
			for (MockData mockData : mockDataList) {
				JSONObject mockJsonRequest = new JSONObject(mockData.getRequest());
				JSONObject mockJsonResponse = new JSONObject(mockData.getResponse());
				if ((isSameJsonIgnoringValues(requestJson, mockJsonRequest))) {
					// gets the json response dynamically based on the request
					// key
					// and values
					response = getDynamicResponse(requestJson, mockJsonResponse);
				} else if (isRequestList(requestJson, mockJsonRequest)) {
					// gets the json dynamic response list based on the request
					// arraylist count
					response = getDynamicResponseList(requestJson, mockJsonResponse);
				}
			}
			return response;
		} catch (JSONException e) {			
			throw new MockServiceSystemException(e);
		}
	}
	
	public  String getDynamicResponse(JSONObject requestJson, JSONObject responseJson) throws MockServiceSystemException {
		Map<String, String> map;
		try {			
			map = convertJsonStrToMap(requestJson);
			Set<String> keySet = map.keySet();			
			for (String key: keySet) {
				function(responseJson, key, map.get(key));
			}
			return responseJson.toString();
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}		
	}
	
	public  String getDynamicResponseList(JSONObject requestJson, JSONObject responseJson) throws MockServiceSystemException {
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
	
	  public static JSONObject function(JSONObject obj, String keyMain, String newValue) throws Exception {
		    // We need to know keys of Jsonobject
		    JSONObject json = new JSONObject();
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
		            function(obj.getJSONObject(key), keyMain, newValue);
		        }

		        // if it's jsonarray
		        if (obj.optJSONArray(key) != null) {
		            JSONArray jArray = obj.getJSONArray(key);
		            for (int i=0;i<jArray.length();i++) {
		                    function(jArray.getJSONObject(i), keyMain,  newValue);
		            }
		        }
		    }
		    return obj;
		}
	  
	  public  Map<String, String> convertJsonStrToMap(JSONObject body) throws MockServiceSystemException {
			try {				
				HashMap<String, String> map = new HashMap<String, String>();
				map = getChildNodes(body, null, map);
				return map;
			} catch (JSONException e) {
				throw new MockServiceSystemException(e);
			}
		}

		private  boolean isSameJsonIgnoringValues(Object obj1, Object obj2) throws JSONException {
			if (obj1 instanceof JSONObject) {
				JSONObject jsonObj1 = (JSONObject) obj1;

				JSONObject jsonObj2 = (JSONObject) obj2;

				ArrayList<String> names = MockServiceUtil.getList(jsonObj1.keys());
				ArrayList<String> names2 =MockServiceUtil.getList(jsonObj2.keys());
				if (names.size() != names2.size()) {
					return false;
				}

				for (String fieldName : names) {
					Object obj1FieldValue = jsonObj1.get(fieldName);

					Object obj2FieldValue = jsonObj2.get(fieldName);

					if (!isSameJsonIgnoringValues(obj1FieldValue, obj2FieldValue)) {
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
						if (isSameJsonIgnoringValues(obj1Array.get(i), obj2Array.get(j))) {
							matchFound = true;
							break;
						}
					}

					if (!matchFound) {
						return false;
					}
				}
			}
			return true;
		}

		private  HashMap<String, String> getChildNodes(Object obj1, String keyname, HashMap<String, String> out)
				throws JSONException {
			if (obj1 instanceof JSONObject) {
				JSONObject jsonObj1 = (JSONObject) obj1;
				ArrayList<String> names = MockServiceUtil.getList(jsonObj1.keys());
				for (String fieldName : names) {
					Object obj1FieldValue = jsonObj1.get(fieldName);
					getChildNodes(obj1FieldValue, fieldName, out);
				}
			} else if (obj1 instanceof JSONArray) {
				JSONArray obj1Array = (JSONArray) obj1;

				for (int i = 0; i < obj1Array.length(); i++) {
					getChildNodes(obj1Array.get(i), null, out);

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
		
	public Boolean isRequestList(JSONObject jsonRequestList, JSONObject jsonRequest) throws MockServiceSystemException {
		try {
			if (jsonRequestList.length() == 1) {
				String firstElement = (String) jsonRequestList.keys().next();
				if (jsonRequestList.optJSONArray(firstElement) != null) {
					JSONArray array = jsonRequestList.getJSONArray(firstElement);
					for (int i = 0; i < array.length(); i++) {
						if (isSameJsonIgnoringValues(array.getJSONObject(i), jsonRequest)) {
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
}
