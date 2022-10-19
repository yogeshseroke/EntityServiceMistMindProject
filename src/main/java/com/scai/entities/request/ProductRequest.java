package com.scai.entities.request;

import lombok.Data;

@Data
public class ProductRequest {
	private int pageIndex;
	private int pageSize;
	private String sortFieldStringWithASCOrDESC;
	private String searchValue;
	private long productId;
	private String tenantid;
}
