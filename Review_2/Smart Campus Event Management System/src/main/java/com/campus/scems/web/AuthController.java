package com.campus.scems.web;

import com.campus.scems.model.UserRole;
import com.campus.scems.security.SessionLoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class AuthController {

    private final SessionLoginService sessionLoginService;

    public AuthController(SessionLoginService sessionLoginService) {
        this.sessionLoginService = sessionLoginService;
    }

    /** Old bookmark: send users to student login. */
    @GetMapping("/signin")
    public String legacySignin(
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String denied) {
        if (denied != null) {
            return "redirect:/login/student?denied";
        }
        if (logout != null) {
            return "redirect:/login/student?logout";
        }
        return "redirect:/login/student";
    }

    @GetMapping("/login/student")
    public String loginStudent(
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String denied,
            Model model) {
        if (logout != null) {
            model.addAttribute("infoMessage", "You have been signed out.");
        }
        if (denied != null) {
            model.addAttribute("error", "You do not have access to that page. Sign in with the correct account type.");
        }
        return "auth/login-student";
    }

    @GetMapping("/login/faculty")
    public String loginFaculty(Model model) {
        return "auth/login-faculty";
    }

    @PostMapping("/login/student")
    public String loginStudentSubmit(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            sessionLoginService.login(request, loginId, password, UserRole.STUDENT);
            return "redirect:/student/home";
        } catch (BadCredentialsException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login/student";
        }
    }

    @PostMapping("/login/faculty")
    public String loginFacultySubmit(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            sessionLoginService.login(request, loginId, password, UserRole.FACULTY);
            return "redirect:/faculty/dashboard";
        } catch (BadCredentialsException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login/faculty";
        }
    }

    @GetMapping("/admin")
    public String legacyAdmin() {
        return "redirect:/faculty/dashboard";
    }
}
