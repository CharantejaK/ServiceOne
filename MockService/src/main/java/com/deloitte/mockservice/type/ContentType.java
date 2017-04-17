package com.deloitte.mockservice.type;

import org.springframework.http.MediaType;

public enum ContentType {

	XML("application/xml", MediaType.APPLICATION_XML), JSON("application/json", MediaType.APPLICATION_JSON);

	private String name;
	private MediaType type;

	ContentType(String name, MediaType type) {
		this.name = name;
		this.type = type;
	}

	public static ContentType findByName(String name) {
		for (ContentType type : ContentType.values()) {
			if (name.trim().equals(type.getName().trim())) {
				return type;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public MediaType getType() {
		return type;
	}

}
