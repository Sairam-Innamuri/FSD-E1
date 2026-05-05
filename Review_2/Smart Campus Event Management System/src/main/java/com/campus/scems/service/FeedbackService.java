package com.campus.scems.service;

import com.campus.scems.exception.BusinessRuleException;
import com.campus.scems.model.FeedbackEntity;
import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RegistrationService registrationService;

    public FeedbackService(FeedbackRepository feedbackRepository, RegistrationService registrationService) {
        this.feedbackRepository = feedbackRepository;
        this.registrationService = registrationService;
    }

    @Transactional
    public FeedbackEntity submit(Long registrationId, FeedbackEntity input) {
        RegistrationEntity reg = registrationService.getByIdWithEvent(registrationId);
        if (reg.getFeedback() != null) {
            throw new BusinessRuleException("Feedback was already submitted for this registration.");
        }
        if (reg.getEvent().getStartDateTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Feedback can be submitted after the event starts.");
        }
        input.setRegistration(reg);
        input.setSubmittedAt(LocalDateTime.now());
        FeedbackEntity saved = feedbackRepository.save(input);
        reg.setFeedback(saved);
        return saved;
    }
}
