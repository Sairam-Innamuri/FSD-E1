package com.campus.scems.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = {"com.campus.scems.web", "com.campus.scems.web.admin", "com.campus.scems.exception"})
public class SecurityModelAdvice {

    @ModelAttribute("authAuthenticated")
    public boolean authAuthenticated() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.isAuthenticated() && !"anonymousUser".equals(a.getPrincipal());
    }

    @ModelAttribute("authStudent")
    public boolean authStudent() {
        return hasRole("ROLE_STUDENT");
    }

    @ModelAttribute("authFaculty")
    public boolean authFaculty() {
        return hasRole("ROLE_FACULTY");
    }

    private static boolean hasRole(String role) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getPrincipal())) {
            return false;
        }
        for (GrantedAuthority ga : a.getAuthorities()) {
            if (role.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
