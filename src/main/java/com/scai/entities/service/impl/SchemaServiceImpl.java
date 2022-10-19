package com.scai.entities.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scai.entities.service.SchemaService;

@Service
public class SchemaServiceImpl implements SchemaService {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean createSchema(String tenantId) {
		boolean isSchemaCreated = false;
		try {
			String preparedSql = "CREATE SCHEMA IF NOT EXISTS " + tenantId;
			jdbcTemplate.update(preparedSql);
			isSchemaCreated = true;
		} catch (DataAccessException e) {
			
		} catch(Exception e) {
			
		} 
		return isSchemaCreated;
	}
}
