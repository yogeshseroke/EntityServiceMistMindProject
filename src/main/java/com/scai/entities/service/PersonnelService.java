package com.scai.entities.service;

import org.springframework.core.io.InputStreamResource;

import com.scai.entities.response.DemandPlannerHierarchyResponse;
import com.scai.entities.response.SalesmanHierarchyResponse;

public interface PersonnelService {
	
	InputStreamResource getPersonnelTemplateDownload(DemandPlannerHierarchyResponse demandPlannerHierarchyResponse, SalesmanHierarchyResponse salesmanHierarchyResponse, String tenantId);

	SalesmanHierarchyResponse getSalesmanHierarchyResponse(Object salesmanHierarchyRequest);

	DemandPlannerHierarchyResponse getDemandPlannerHierarchyResponse(Object demandPlannerHierarchyRequest);
	
	InputStreamResource getCSVDownload(SalesmanHierarchyResponse salesmanHierarchyResponse,
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse, String tenantId);
}
