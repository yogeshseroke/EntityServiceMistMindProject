package com.scai.entities.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scai.entities.response.LocationHierarchyResponse;
import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.service.LocationService;
import com.scai.entities.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Location Controller", description = "Location services are defined here")
@RequestMapping("/entities/location")
public class LocationController {

	public LocationHierarchyResponse locationHierarchyResponse;

	@Autowired
	private LocationService locationService;
	
	/*
	 * API to Download CSV Template
	 */
	@Operation(summary = "To download the template")
	@GetMapping(path = "v1/downloadTemplate", produces = { "application/csv" })
	public ResponseEntity<InputStreamResource> downloadTemplate(@RequestParam(name = "tenantId") String tenantId) {
		log.info("Entities_Service : : : : > downloadTemplate()");
		String filename = "LocationTemplate_" + new Date() + ".csv";
		log.debug("Entities_Service : : : : > File_Name : " + filename + "---");
		InputStreamResource file = locationService.getLocationTemplateDownload(locationHierarchyResponse, tenantId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

}
