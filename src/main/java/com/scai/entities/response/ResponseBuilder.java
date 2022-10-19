package com.scai.entities.response;

import com.scai.entities.constants.Constants;
import com.scai.entities.exceptionhandler.UserException;

public class ResponseBuilder {

	public static BaseApiResponse getSuccessResponse(Object responseData) throws UserException {
		
		BaseApiResponse baseApiResponse = new BaseApiResponse();
		baseApiResponse.setResponseStatus(new ResponseStatus(Constants.SUCCESS));
		baseApiResponse.setResponseData(responseData);
		
		return baseApiResponse;
	}
	
	public static BaseApiResponse getSuccessResponse(Object responseData, String message) throws UserException {
		
		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setStatusCode(Constants.SUCCESS);
		return new BaseApiResponse(responseStatus, responseData, message);
	}
	
	public static BaseApiResponse getSuccessResponse() throws UserException {
		
		BaseApiResponse baseApiResponse = new BaseApiResponse();
		baseApiResponse.setResponseStatus(new ResponseStatus(Constants.SUCCESS));
		baseApiResponse.setResponseData(null);
		
		return baseApiResponse;
	}
	private ResponseBuilder()
	{
		
	}
}
