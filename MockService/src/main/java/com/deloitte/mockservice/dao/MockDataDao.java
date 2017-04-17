package com.deloitte.mockservice.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.deloitte.mockservice.model.MockData;

@Transactional
public interface MockDataDao extends CrudRepository<MockData, Long> {
	public List<MockData> findById(Long requestid);	

	@Query("select m from MockData m where lower(m.client) like %?1%")
	public List<MockData> findByClient(String client);
	
	@SuppressWarnings("unchecked")
	public MockData save(MockData mockData);
	
	public List<MockData> findByRequestAndResponseAndServicename(String request, String response, String serviceName);
	
	@Query("select m from MockData m where replace(servicename,'/','') = ?1")
	public List<MockData> findByServicename(String serviceName);
}
