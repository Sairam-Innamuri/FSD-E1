package com.campus.scems.web;

import com.campus.scems.exception.BusinessRuleException;
import com.campus.scems.service.AccountService;
import com.campus.scems.util.VeltechLoginIds;
import com.campus.scems.web.dto.StudentSignupForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentSignupController {

    private final AccountService accountService;

    public StudentSignupController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/register/student")
    public String form(Model model) {
        model.addAttribute("form", new StudentSignupForm());
        return "auth/register-student";
    }

    @PostMapping("/register/student")
    public String submit(
            @Valid @ModelAttribute("form") StudentSignupForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register-student";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("passwordMismatch", "Passwords do not match.");
            return "auth/register-student";
        }
        try {
            accountService.registerStudent(form.getFullName(), VeltechLoginIds.normalize(form.getLoginId()), form.getPassword());
        } catch (BusinessRuleException ex) {
            model.addAttribute("signupError", ex.getMessage());
            return "auth/register-student";
        }
        redirectAttributes.addFlashAttribute("infoMessage", "Account created. You can sign in with your VTU ID.");
        return "redirect:/login/student";
    }
}
