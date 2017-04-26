package com.deloitte.mockservice.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "mockdata")
public class MockData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length=6000000)
	@Lob
	@NotNull
	private String request;

	@Column(length=6000000)
	@Lob
	@NotNull
	private String response;

	@NotNull
	private String contenttype;
	
	@NotNull
	private String client;

	private String description;
	
	@NotNull
	private Calendar createdtime;
	
	@NotNull
	private String createdby;
	
	@NotNull 
	private Boolean isStaticMock;
	
	@NotNull
	private String servicename;
	
	public Calendar getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(Calendar createdtime) {
		this.createdtime = createdtime;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsStaticMock() {
		return isStaticMock;
	}

	public void setIsStaticMock(Boolean isStaticMock) {
		this.isStaticMock = isStaticMock;
	}	
}
