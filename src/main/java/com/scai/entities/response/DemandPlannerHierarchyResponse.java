package com.scai.entities.response;

import java.util.Map;

import lombok.Data;

@Data
public class DemandPlannerHierarchyResponse {
	private int noOfLevels;
	private Map<String, String> demandsHierarchyLevelMap;
}
