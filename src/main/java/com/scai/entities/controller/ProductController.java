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
import org.springframework.web.multipart.MultipartFile;

import com.scai.entities.request.ProductRequest;
import com.scai.entities.response.BaseApiResponse;
import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.response.ResponseBuilder;
import com.scai.entities.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Product Controller", description = "Product services are defined here")
@RequestMapping("/entities/products")
public class ProductController {

	public ProductHierarchyResponse productHierarchyResponse;

	@Autowired
	private ProductService productService;

	@Operation(summary = "To Get All Products")
	@GetMapping(path = "v1", produces = { "application/json" })
	public ResponseEntity<BaseApiResponse> getAllProducts(@RequestParam(name = "pageIndex") int pageIndex,
			@RequestParam(name = "pageSize") int pageSize,
			@RequestParam(name = "sortFieldStringWithASCOrDESC") String sortFieldStringWithASCOrDESC,
			@RequestParam(name = "searchValue") String searchValue, @RequestParam(name = "productId") long productId,
			@RequestParam(name = "tenantId") String tenantId) {

		ProductRequest productRequest = productService.getProductRequest(pageIndex, pageSize,
				sortFieldStringWithASCOrDESC, searchValue, productId, tenantId);
		log.info("Entities_Service : : : : > getAllProducts() ");
		BaseApiResponse baseApiResponse = ResponseBuilder
				.getSuccessResponse(productService.getAllProducts(productRequest, productHierarchyResponse, "forGet"));
		return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
	}

	@Operation(summary = "To set dummy ProductHierarchyResponse")
	@PostMapping(path = "v1/setProductHierarchyResponse", produces = { "application/json" })
	public ResponseEntity<BaseApiResponse> setProductHierarchyResponse(@RequestBody Object productHierarchyRequest) {
		log.info("Entities_Service : : : : > setProductHierarchyResponse()");
		productHierarchyResponse = productService.getProductHierarchyResponse(productHierarchyRequest);
		BaseApiResponse baseApiResponse = ResponseBuilder.getSuccessResponse(productHierarchyResponse);
		return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
	}

	/*
	 * API to Download CSV Template
	 */
	@Operation(summary = "To download the template")
	@GetMapping(path = "v1/downloadTemplate", produces = { "application/csv" })
	public ResponseEntity<InputStreamResource> downloadTemplate(@RequestParam(name = "tenantId") String tenantId) {
		log.info("Entities_Service : : : : > downloadTemplate()");
		String filename = "ProductTemplate_" + new Date() + ".csv";
		log.debug("Entities_Service : : : : > File_Name : " + filename + "---");
		InputStreamResource file = productService.getCSVTemplateDownload(productHierarchyResponse, tenantId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

	/*
	 * API to Download CSV File
	 */
	@Operation(summary = "To download the data")
	@GetMapping(path = "v1/downloadCSV", produces = { "application/csv" })
	public ResponseEntity<InputStreamResource> downloadCSV(@RequestParam(name = "tenantId") String tenantId) {

		log.info("Entities_Service : : : : >downloadCSV() Download CSV File Controller");
		String filename = "Product_" + new Date() + ".csv";
		log.debug("Entities_Service : : : : > File_Name : " + filename);
		InputStreamResource file = productService.getCSVDownload(productHierarchyResponse, tenantId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

	@Operation(summary = "To upload the csv data")
	@PostMapping(path = "v1", produces = { "application/json" })
	public ResponseEntity<BaseApiResponse> uploadProductCSVData(@RequestParam("csvFile") MultipartFile csvFile,
			@RequestParam("tenantId") String tenantId) {
		log.info("Entities_Service : : : : > uploadProductCSVData()");
		BaseApiResponse baseApiResponse = ResponseBuilder
				.getSuccessResponse(productService.uploadProductCSVData(csvFile, productHierarchyResponse, tenantId));
		return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
	}

}
