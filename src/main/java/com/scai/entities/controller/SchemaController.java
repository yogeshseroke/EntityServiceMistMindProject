package com.scai.entities.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scai.entities.response.BaseApiResponse;
import com.scai.entities.response.ResponseBuilder;
import com.scai.entities.service.SchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Schema Controller", description = "Schema services are defined here")
@RequestMapping("/entities")
public class SchemaController {

	@Autowired
	SchemaService schemaService;

	@Operation(summary = "To Get Schema")
	@GetMapping(path = "v1/createSchema/{tenantId}", produces = { "application/json" })
	public ResponseEntity<BaseApiResponse> createSchema(@PathVariable String tenantId) {
		log.info("Entities_Service : : : : > createSchema()");
		BaseApiResponse baseApiResponse = ResponseBuilder.getSuccessResponse(schemaService.createSchema(tenantId));
		return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
	}

}
