package com.book.backend.common.exception;

import com.book.backend.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(VueBookException.class)
    @ResponseStatus(HttpStatus.OK)
    public R businessExceptionHandler(VueBookException e) {
        return R.error(e.getErrMessage(),e.getCode());
    }

//    @ResponseBody
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
//    public R runtimeExceptionHandler(Exception e) {
//        return R.error(CommonError.UNKOWN_ERROR.getErrMessage());
//
//    }
}
