package com.deloitte.mockservice.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.delolitte.mockservice.exception.MockServiceSystemException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonRequestHandler extends AbstractRequestHandler{

	@Override
	public String getMockResponse() throws MockServiceSystemException{
		/*try {
		if (IsDynamicResponse()) {
			Map<String,String> requestMap = convertJsonStrToMap(getRequest());
			System.out.println(requestMap);
		}		
		return getResponse();
		} catch (IOException e) {
			throw new MockServiceSystemException(e);
		}	*/	
		
		return null;
	}
	
/*	public static void main(String[] args) throws JSONException {		
			convertJsonStrToMap("{\"ustaOrganizations\":[{\"addresses\":[{\"billingAddress\":false,\"country\":{\"isocode\":\"US\",\"name\":\"United States\"},\"defaultAddress\":false,\"formattedAddress\":\"new addresss line test 2, new address line test 1, NY, California, 00210\",\"id\":\"8808414674967\",\"line1\":\"new addresss line test 2\",\"line2\":\"new address line test 1\",\"postalCode\":\"00210\",\"region\":{\"countryIso\":\"US\",\"isocode\":\"US-CA\",\"isocodeShort\":\"CA\",\"name\":\"California\"},\"shippingAddress\":false,\"taDistrict\":{\"code\":\"02\",\"name\":\"New Hampshire\",\"sectionCode\":\"45\"},\"taSection\":{\"code\":\"45\",\"name\":\"New England\"},\"town\":\"NY\",\"visibleInAddressBook\":true}],\"name\":\"timberlake05 organization\",\"orgSubType\":{\"code\":\"COMTNASS\",\"description\":\"Community Tennis Association\",\"name\":\"Community Tennis Association\"},\"socialAddress\":[{\"socialMediaID\":\"85cdb9db-284e-49e4-b746-fe307afc21ca\",\"socialMediaType\":\"Website\"}],\"uaid\":\"3000076014\",\"uid\":\"a31f4e4d-a99f-415b-8f2e-79703edc8efb\",\"ustaPhone\":[{\"phoneNumber\":\"9885178211\",\"phoneType\":\"MOBILE\"}]}]}");
		
	}*/
	

	
	
	public static Map<String, String> convertJsonStrToMap(String jsonStr) throws JSONException {
		JSONObject body = new JSONObject(jsonStr);
		Map<String, String> map = new HashMap<String, String>();
		return parse(body, map);
	}
	
	public static Map<String, String> parse(JSONObject json, Map<String, String> out) throws JSONException {
		Iterator<String> keys = json.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			String val = null;
			if (json.getJSONObject(key) instanceof JSONObject) {
				JSONObject value = json.getJSONObject(key);
				parse(value, out);
			}
			else {
				val = json.getString(key);
			}

			if (val != null) {
				out.put(key, val);
			}
		}
		return out;
	}

}
