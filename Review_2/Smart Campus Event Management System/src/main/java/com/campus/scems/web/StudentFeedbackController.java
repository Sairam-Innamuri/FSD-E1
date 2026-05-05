package com.campus.scems.web;

import com.campus.scems.exception.BusinessRuleException;
import com.campus.scems.model.FeedbackEntity;
import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.service.FeedbackService;
import com.campus.scems.service.RegistrationService;
import com.campus.scems.web.dto.FeedbackForm;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/feedback")
public class StudentFeedbackController {

    private final RegistrationService registrationService;
    private final FeedbackService feedbackService;

    public StudentFeedbackController(RegistrationService registrationService, FeedbackService feedbackService) {
        this.registrationService = registrationService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/{registrationId}")
    public String form(@PathVariable Long registrationId, Model model, @AuthenticationPrincipal UserDetails user) {
        RegistrationEntity reg = registrationService.getByIdWithEvent(registrationId);
        assertStudentOwns(user, reg);
        model.addAttribute("registration", reg);
        if (reg.getFeedback() != null) {
            model.addAttribute("existing", reg.getFeedback());
            return "student/feedback-done";
        }
        model.addAttribute("feedbackForm", new FeedbackForm());
        return "student/feedback";
    }

    @PostMapping("/{registrationId}")
    public String submit(
            @PathVariable Long registrationId,
            @Valid @ModelAttribute("feedbackForm") FeedbackForm form,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        RegistrationEntity reg = registrationService.getByIdWithEvent(registrationId);
        assertStudentOwns(user, reg);
        if (reg.getFeedback() != null) {
            model.addAttribute("registration", reg);
            model.addAttribute("existing", reg.getFeedback());
            return "student/feedback-done";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("registration", reg);
            return "student/feedback";
        }
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setRating(form.getRating());
        feedback.setComment(form.getComment() != null ? form.getComment().trim() : null);
        feedbackService.submit(registrationId, feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Thanks! Your feedback was recorded.");
        return "redirect:/student/registrations";
    }

    private static void assertStudentOwns(UserDetails user, RegistrationEntity reg) {
        if (user == null || !reg.getStudentEmail().equalsIgnoreCase(user.getUsername())) {
            throw new BusinessRuleException("You can only access your own registrations.");
        }
    }
}
