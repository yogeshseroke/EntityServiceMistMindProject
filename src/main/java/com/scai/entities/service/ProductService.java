package com.scai.entities.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import com.scai.entities.request.ProductRequest;
import com.scai.entities.response.CSVUploadResponse;
import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.response.ProductResponse;

public interface ProductService {

	ProductResponse getAllProducts(ProductRequest productRequest, ProductHierarchyResponse productHierarchyResponse, String actionName);

	InputStreamResource getCSVDownload(ProductHierarchyResponse productHierarchyResponse, String tenantId);

	InputStreamResource getCSVTemplateDownload(ProductHierarchyResponse productHierarchyResponse, String tenantId);

	CSVUploadResponse uploadProductCSVData(MultipartFile csvFile, ProductHierarchyResponse productHierarchyResponse,
			String tenantId);

	ProductHierarchyResponse getProductHierarchyResponse(Object productHierarchyRequest);

	ProductRequest getProductRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue, long productId, String tenantId);
}
