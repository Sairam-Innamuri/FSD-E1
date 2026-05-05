package com.campus.scems.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

@ControllerAdvice(basePackages = "com.campus.scems.web")
public class WebModelAdvice {

    @ModelAttribute("now")
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
