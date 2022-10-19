package com.scai.entities.request;

import lombok.Data;

@Data
public class LocationRequest {

	private int pageIndex;
	private int pageSize;
	private String sortFieldStringWithASCOrDESC;
	private String searchValue;
	private long locationId;
	private String tenantId;
}
