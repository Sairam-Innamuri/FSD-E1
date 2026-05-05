package com.campus.scems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@ControllerAdvice(basePackages = "com.campus.scems.web")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("message", ex.getMessage());
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ModelAndView handleBusiness(BusinessRuleException ex) {
        ModelAndView mv = new ModelAndView("error/400");
        mv.addObject("message", ex.getMessage());
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ModelAndView mv = new ModelAndView("error/400");
        mv.addObject("message", msg);
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex) {
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("message", "Something went wrong. Please try again later.");
        mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mv;
    }
}
