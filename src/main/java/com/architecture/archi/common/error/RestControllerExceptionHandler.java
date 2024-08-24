package com.architecture.archi.common.error;

import com.architecture.archi.common.model.ApiResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler({
        Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody public ApiResponseModel ExceptionHandler(Exception ex) throws Exception {

        log.error(ex.getClass().getSimpleName() + " Handling : {}", ex);

        ApiResponseModel apiResponse = new ApiResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        apiResponse.putError(ex.getMessage());

        return apiResponse;
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody public ApiResponseModel ValidExceptionHandler(MethodArgumentNotValidException ex) throws Exception {

        log.error(ex.getClass().getSimpleName() + " Handling : {}", ex);

        BindingResult bindingResult = ex.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            builder.append(" ");
        }

        ApiResponseModel apiResponse = new ApiResponseModel(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());

        apiResponse.putError(builder.toString());

        return apiResponse;
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            HttpMediaTypeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody public ApiResponseModel badRequestExceptionHandler(Exception ex) throws Exception {

        log.error(ex.getClass().getSimpleName() + " Handling : {}", ex);

        ApiResponseModel apiResponse = new ApiResponseModel(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());

        apiResponse.putError(ex.getMessage());

        return apiResponse;
    }

    @ExceptionHandler({CustomException.class})
    @ResponseBody public ResponseEntity<ApiResponseModel> CustomExceptionHandler(CustomException ex) throws Exception {

        log.error("CustomException Handling : {}", ex);

        ExceptionCode exceptionCode = ex.getExceptionCode();

        ApiResponseModel apiResponse = new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
        apiResponse.putError(ex.getExceptionData());

//        Object exceptionData = ex.getExceptionData();
//
//        if (exceptionData != null) {
//            apiResponse.putError(exceptionData);
//        }

        return new ResponseEntity<>(apiResponse, exceptionCode.getStatusCode());
    }
}
