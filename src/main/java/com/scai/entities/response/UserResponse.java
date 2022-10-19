package com.scai.entities.response;

import lombok.Data;

@Data
public class UserResponse {
	private Integer userId;
	private String userName;
	private String userAddress;
	private String userEducation;
}
