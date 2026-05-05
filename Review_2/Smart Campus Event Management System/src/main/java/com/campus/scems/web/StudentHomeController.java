package com.campus.scems.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentHomeController {

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        return "student/home";
    }
}
