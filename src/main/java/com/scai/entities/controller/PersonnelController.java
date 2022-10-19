package com.scai.entities.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scai.entities.request.SalesManDemandHierarchyRequest;
import com.scai.entities.response.BaseApiResponse;
import com.scai.entities.response.DemandPlannerHierarchyResponse;
import com.scai.entities.response.ResponseBuilder;
import com.scai.entities.response.SalesmanHierarchyResponse;
import com.scai.entities.service.PersonnelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Personnel Controller", description = "Personnel services are defined here")
@RequestMapping("/entities/personnels")
public class PersonnelController {

	public DemandPlannerHierarchyResponse demandPlannerHierarchyResponse;
	public SalesmanHierarchyResponse salesmanHierarchyResponse;

	@Autowired
	private PersonnelService personnelService;

	/*
	 * API to set Demand Planner Hierarchy Response and to set Salesman Hierarchy
	 * Response
	 */
	@Operation(summary = "To set dummy DemandPlannerHierarchyResponse")
	@PostMapping(path = "v1/setSalesPlannerHierarchyResponse", produces = { "application/json" })
	public ResponseEntity<BaseApiResponse> setDemandPlannerHierarchyResponse(
			@RequestBody SalesManDemandHierarchyRequest salesManDemandHierarchyRequest) {
		log.info("Entities_Service : : : : > setDemandPlannerHierarchyResponse()");
		demandPlannerHierarchyResponse = personnelService
				.getDemandPlannerHierarchyResponse(salesManDemandHierarchyRequest.getDemandPlannerHierarchyRequest());
		salesmanHierarchyResponse = personnelService
				.getSalesmanHierarchyResponse(salesManDemandHierarchyRequest.getSalesmanHierarchyRequest());
		BaseApiResponse baseApiResponse = ResponseBuilder.getSuccessResponse(salesManDemandHierarchyRequest);
		return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
	}

	/*
	 * API to Download CSV Template
	 */
	@Operation(summary = "To download the template")
	@GetMapping(path = "v1/downloadTemplate", produces = { "application/csv" })
	public ResponseEntity<InputStreamResource> downloadTemplate(@RequestParam(name = "tenantId") String tenantId) {
		log.info("Entities_Service : : : : > downloadTemplate() -- Download Template Controller");
		String filename = "PersonnelTemplate_" + new Date() + ".csv"; 
		log.debug("Entities_Service : : : : > File_Name : " + filename);
		InputStreamResource file = personnelService.getPersonnelTemplateDownload(demandPlannerHierarchyResponse,
				salesmanHierarchyResponse, tenantId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aeff222" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

	/*
	 * API to Download CSV File
	 */
	@Operation(summary = "To download the data")
	@GetMapping(path = "v1/downloadCSV", produces = { "application/csv" })
	public ResponseEntity<InputStreamResource> downloadCSV(@RequestParam(name = "tenantId") String tenantId) {
		log.info("Entities_Service : : : : > downloadCSV() Download CSV File Controller");
		String filename = "Product_" + new Date() + ".csv";
		log.debug("Entities_Service : : : : > File_Name : " + filename);
		InputStreamResource file = personnelService.getCSVDownload(salesmanHierarchyResponse,
				demandPlannerHierarchyResponse, tenantId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}
}
