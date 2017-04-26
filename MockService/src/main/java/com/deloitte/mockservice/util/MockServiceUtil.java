package com.deloitte.mockservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MockServiceUtil {	
	
	public static ArrayList<String> getList(Iterator<String> iterator) {
		ArrayList<String> list = new ArrayList<>();
		for (Iterator<String> it = iterator; it.hasNext();) {
			list.add(it.next());
		}
		return list;
	}

	public static Boolean isSame(ArrayList<String> list1, ArrayList<String> list2) {		
		Collections.sort(list1);
		Collections.sort(list2);
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		return true;
	}	
}
