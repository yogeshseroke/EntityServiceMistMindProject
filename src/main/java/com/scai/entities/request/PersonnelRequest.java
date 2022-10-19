package com.scai.entities.request;

import lombok.Data;

@Data
public class PersonnelRequest {

	private int pageIndex;
	private int pageSize;
	private String sortFieldStringWithASCOrDESC;
	private String searchValue;
	private long personnelId;
	private String tenantId;
}
