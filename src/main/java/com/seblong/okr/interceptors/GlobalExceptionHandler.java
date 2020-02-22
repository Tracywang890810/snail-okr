package com.seblong.okr.interceptors;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.resource.StandardRestResource;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> exception(Exception e) {
        LOG.error("", e);
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", 500);
        return result;
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> illegalArgumentException(IllegalArgumentException e) {
        LOG.error("", e);
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", 400);
        result.put("message", e.getMessage());
        return result;
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        LOG.error("", e);
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", 400);
        result.put("message", e.getMessage());
        return result;
    }
    
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> exception(ValidationException e) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", e.getCode());
        result.put("message", e.getReason());
        return result;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", 400);
        result.put("message", e.getMessage());
        return result;
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", 400);
        result.put("message", e.getMessage());
        return result;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> httpClientErrorException(HttpClientErrorException e) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", e.getStatusCode().value());
        result.put("message", e.getStatusText());
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StandardRestResource handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        logger.error("", ex);
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            sb.append(fieldName)
                    .append(":")
                    .append(errorMessage)
                    .append(";");
        });
        return StandardRestResource.fail(sb.toString());
    }
}
