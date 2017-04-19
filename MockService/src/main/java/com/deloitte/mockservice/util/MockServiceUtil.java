package com.deloitte.mockservice.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.delolitte.mockservice.exception.MockServiceSystemException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockServiceUtil {

	private static Logger LOG = Logger.getLogger(MockServiceUtil.class);

	public static Boolean isSameJsonIgnoringValues(String json1, String json2) throws MockServiceSystemException {
		try {
			JSONObject jsonObj1 = new JSONObject(json1);
			JSONObject jsonObj2 = new JSONObject(json2);
			return jsonsEqual(jsonObj1, jsonObj2);
		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}
	}

	private static ArrayList<String> getList(Iterator<String> iterator) {
		ArrayList<String> list = new ArrayList<>();
		for (Iterator<String> it = iterator; it.hasNext();) {
			list.add(it.next());
		}
		return list;
	}

	private static Boolean isSame(ArrayList<String> list1, ArrayList<String> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		Collections.sort(list1);
		Collections.sort(list2);
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param json
	 * @return
	 */
	public static String getFormattedJsonString(String json) throws MockServiceSystemException {
		String jsonStr = null;
		try {
			JSONObject body = new JSONObject(json);
			jsonStr = body.toString();

		} catch (Exception e) {
			throw new MockServiceSystemException(e);
		}
		return jsonStr;
	}

	/**
	 * @param xmlStr
	 * @return
	 * @throws validates
	 *             the xml and throws MockServiceSystemException in case of
	 *             invalid xml
	 * 
	 */
	public static String getFormattedXmlString(String xmlStr) throws MockServiceSystemException {
		String formattedXmlStr = null;
		try {
			Document doc = getDocument(xmlStr);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			formattedXmlStr = writer.getBuffer().toString();
		} catch (TransformerException e) {
			LOG.error("exception inside getFormattedXmlString", e);
			throw new MockServiceSystemException(e);
		}
		return formattedXmlStr;
	}

	public static Boolean isSameXmlIgnoringValues(String xmlStr1, String xmlStr2) throws MockServiceSystemException {
		Document document1 = getDocument(xmlStr1);
		Document document2 = getDocument(xmlStr2);
		NodeList nodeList = document1.getElementsByTagName("*");
		NodeList nodeList2 = document2.getElementsByTagName("*");
		ArrayList<String> list1 = new ArrayList<>();
		ArrayList<String> list2 = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			list1.add(nodeList.item(i).getNodeName());
			list2.add(nodeList2.item(i).getNodeName());
		}
		return isSame(list1, list2);
	}

	public static Document getDocument(String xmlStr) throws MockServiceSystemException {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(xmlStr)));
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOG.error("exception inside getFormattedXmlString", e);
			throw new MockServiceSystemException(e);
		}
		return document;
	}

	public static Map<String, String> convertJsonStrToMap(String jsonStr) throws MockServiceSystemException {
		try {
			JSONObject body = new JSONObject(jsonStr);
			HashMap<String, String> map = new HashMap<String, String>();
			map = getChildNodes(body, null, map);
			return map;
		} catch (JSONException e) {
			throw new MockServiceSystemException(e);
		}
	}

	private static boolean jsonsEqual(Object obj1, Object obj2) throws JSONException {
		if (obj1 instanceof JSONObject) {
			JSONObject jsonObj1 = (JSONObject) obj1;

			JSONObject jsonObj2 = (JSONObject) obj2;

			ArrayList<String> names = getList(jsonObj1.keys());
			ArrayList<String> names2 = getList(jsonObj2.keys());
			if (names.size() != names2.size()) {
				return false;
			}

			for (String fieldName : names) {
				Object obj1FieldValue = jsonObj1.get(fieldName);

				Object obj2FieldValue = jsonObj2.get(fieldName);

				if (!jsonsEqual(obj1FieldValue, obj2FieldValue)) {
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
					if (jsonsEqual(obj1Array.get(i), obj2Array.get(j))) {
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

	private static HashMap<String, String> getChildNodes(Object obj1, String keyname, HashMap<String, String> out)
			throws JSONException {
		if (obj1 instanceof JSONObject) {
			JSONObject jsonObj1 = (JSONObject) obj1;
			ArrayList<String> names = getList(jsonObj1.keys());
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
	
	/*public static String getDynamicResponse(HashMap<String, String> map, String response) throws MockServiceSystemException {
		try {
			JSONObject jsonResponse = new JSONObject(response);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node =  mapper.readTree(jsonResponse.toString());node.get(0).f
			
			Set<String> set = map.keySet();
			Jsonpath
			
			for (String key : set) {
			
			updateableNode.va
				
			}
		} catch (JSONException | IOException e) {
			throw new MockServiceSystemException(e);
		}
		
	}*/
	
	public static String getDynamicResponse(String request, String response) throws MockServiceSystemException {
		Map<String, String> map;
		try {
			map = convertJsonStrToMap(request);
			Set<String> keySet = map.keySet();
			JSONObject responseJson = new JSONObject(response);
			for (String key: keySet) {
				function(responseJson, key, map.get(key));
			}
			return responseJson.toString();
		} catch (Exception e) {
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
}
