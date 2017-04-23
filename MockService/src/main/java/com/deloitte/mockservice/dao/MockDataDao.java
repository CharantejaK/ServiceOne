package com.deloitte.mockservice.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.deloitte.mockservice.model.MockData;

@Transactional
public interface MockDataDao extends CrudRepository<MockData, Long> {
	
	public MockData findById(Long id);	

	@Query("select m from MockData m where lower(m.client) like %?1%")
	public List<MockData> findByClient(String client);
	
	@SuppressWarnings("unchecked")
	public MockData save(MockData mockData);	
	
	@Query("select m from MockData m where m.contenttype= ?3 and m.isStaticMock = ?2 and replace(m.servicename,'/','') = ?1")
	public List<MockData> findByServicenameAndIsStaticMockAndContenttype(String serviceName, Boolean isStaticMock, String contentType);
	
	public void deleteById(Long id);
	
	public List<MockData> findByServicename(String serviceName);
	
	@Query("select m from MockData m where m.request = ?4 and m.contenttype= ?3 and m.isStaticMock = ?2 and replace(m.servicename,'/','') = ?1")
	public MockData findByServicenameAndContenttypeAndRequestAndIsStaticMock(String serviceName, Boolean isStaticMock, String contentType, String request);
	
	@Query("select m from MockData m where m.contenttype= ?2  and replace(m.servicename,'/','') = ?1")
	public List<MockData> findByServicenameAndContenttype(String serviceName, String contentType);
	
}
