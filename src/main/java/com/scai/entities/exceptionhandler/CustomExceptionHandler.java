package com.scai.entities.exceptionhandler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.scai.entities.constants.Constants;
import com.scai.entities.response.BaseApiResponse;
import com.scai.entities.response.ResponseStatus;






@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseApiResponse> appException(
    		UserException userException, HttpServletRequest request) {
        BaseApiResponse baseApiResponse = new BaseApiResponse();
        baseApiResponse.setResponseStatus(new ResponseStatus(Constants.FAILURE));
        baseApiResponse.setResponseData(userException);
        return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
    }
    

}
