package com.scai.entities.response;

import java.util.Map;

import lombok.Data;

@Data
public class SalesmanHierarchyResponse {
	private int noOfLevels;
	private Map<String, String> salesmanHierarchyLevelMap;
}
