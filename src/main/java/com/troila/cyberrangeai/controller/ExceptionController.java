package com.troila.cyberrangeai.controller;

import com.troila.cyberrangeai.domain.ErrorInfo;
import com.troila.cyberrangeai.exception.AiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {

    @ResponseBody
    @ExceptionHandler(value = AiException.class)    //异常处理器，处理AiException异常
    public ResponseEntity<?> hanlerException(HttpServletRequest request, AiException e){
        ErrorInfo<String> error = new ErrorInfo<>();
        error.setCode(ErrorInfo.ERROR);
        error.setMessage(e.getMessage());
        error.setUrl(request.getRequestURI().toString());
        error.setData("网络靶场人工智能决策计算异常");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }
}

