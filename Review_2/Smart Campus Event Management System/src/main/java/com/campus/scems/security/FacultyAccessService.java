package com.campus.scems.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyAccessService {

    private final Set<String> allowedLoginIds;

    public FacultyAccessService(@Value("${app.faculty.allowed-login-ids}") String csv) {
        this.allowedLoginIds = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public void assertFacultyPortalAllowed(String loginId) {
        String id = loginId == null ? "" : loginId.trim().toUpperCase(Locale.ROOT);
        if (!allowedLoginIds.contains(id)) {
            throw new BadCredentialsException("Faculty sign-in is limited to authorised Veltech faculty IDs only.");
        }
    }
}
