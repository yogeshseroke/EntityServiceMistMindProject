package com.scai.entities.service.impl;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.scai.entities.response.LocationHierarchyResponse;
import com.scai.entities.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

	@Override
	public InputStreamResource getLocationTemplateDownload(LocationHierarchyResponse locationHierarchyResponse,
			String tenantId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationHierarchyResponse getLocationHierarchyResponse(Object locationHierarchyRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStreamResource getCSVDownload(LocationHierarchyResponse locationHierarchyResponse, String tenantId) {
		// TODO Auto-generated method stub
		return null;
	}

}
