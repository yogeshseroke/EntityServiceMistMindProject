package com.scai.entities.service;

import org.springframework.core.io.InputStreamResource;

import com.scai.entities.response.LocationHierarchyResponse;


public interface LocationService {

	InputStreamResource getLocationTemplateDownload(LocationHierarchyResponse locationHierarchyResponse, String tenantId);

	LocationHierarchyResponse getLocationHierarchyResponse(Object locationHierarchyRequest);

	InputStreamResource getCSVDownload(LocationHierarchyResponse locationHierarchyResponse, String tenantId);

}
