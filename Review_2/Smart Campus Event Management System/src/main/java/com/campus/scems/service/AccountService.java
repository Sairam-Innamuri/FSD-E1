package com.campus.scems.service;

import com.campus.scems.exception.BusinessRuleException;
import com.campus.scems.model.AppUser;
import com.campus.scems.model.UserRole;
import com.campus.scems.repository.AppUserRepository;
import com.campus.scems.util.VeltechLoginIds;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser registerStudent(String fullName, String loginIdRaw, String rawPassword) {
        String loginId = VeltechLoginIds.normalize(loginIdRaw);
        if (!VeltechLoginIds.isValidStudentId(loginId)) {
            throw new BusinessRuleException("Student ID must be in the form VTU plus five digits (example: VTU12345).");
        }
        for (String fac : VeltechLoginIds.FACULTY_SEED_IDS) {
            if (loginId.equalsIgnoreCase(fac)) {
                throw new BusinessRuleException("This ID is reserved for faculty. Use a student VTU number.");
            }
        }
        if (appUserRepository.existsByLoginIdIgnoreCase(loginId)) {
            throw new BusinessRuleException("An account already exists for this university ID.");
        }
        AppUser u = new AppUser();
        u.setFullName(fullName.trim());
        u.setLoginId(loginId);
        u.setEmail(loginId.toLowerCase() + "@veltech.edu");
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(UserRole.STUDENT);
        u.setEnabled(true);
        return appUserRepository.save(u);
    }
}
