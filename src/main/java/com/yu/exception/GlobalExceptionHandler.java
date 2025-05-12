package com.yu.exception;

import com.yu.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result handleException(RuntimeException e){
        return Result.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validHandleException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder msg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            msg.append(fieldError.getDefaultMessage());
        }
        return Result.error(StringUtils.hasLength(e.getMessage())?msg.toString():"操作失败");
    }
}
