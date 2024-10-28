package com.common.exception;


import com.common.pojo.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleException(ConstraintViolationException e){
        e.printStackTrace();
        return Result.error(500,"传入非法参数");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
       e.printStackTrace();
       return Result.error(500,"出现异常");
    }

}
