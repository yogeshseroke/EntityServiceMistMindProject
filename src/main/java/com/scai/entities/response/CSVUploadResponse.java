package com.scai.entities.response;

import java.util.List;

import lombok.Data;

@Data
public class CSVUploadResponse {
	int processErrorCode;
	String processMessage;
	List<Long> failedValidationRowNumbers;
}
