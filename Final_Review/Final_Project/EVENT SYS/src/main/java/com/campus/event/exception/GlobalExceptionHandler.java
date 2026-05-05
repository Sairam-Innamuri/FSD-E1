package com.campus.event.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        log.error("Runtime exception: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorTitle", "Something went wrong");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorTitle", "Server Error");
        return "error";
    }
}
