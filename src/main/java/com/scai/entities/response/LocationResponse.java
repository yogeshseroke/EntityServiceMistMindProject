package com.scai.entities.response;

import java.util.List;

import lombok.Data;

@Data
public class LocationResponse {

	private List<String> listOfHearders;
	private List<Object[]> listOfRowData;
	private int pageIndex;
	private int pageSize;
	private int totalElements;
	private int totalPages;
}
