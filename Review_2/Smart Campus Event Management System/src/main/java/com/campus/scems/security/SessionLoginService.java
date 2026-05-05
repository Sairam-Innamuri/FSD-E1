package com.campus.scems.security;

import com.campus.scems.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionLoginService {

    private final AuthenticationManager authenticationManager;
    private final FacultyAccessService facultyAccessService;

    public SessionLoginService(AuthenticationManager authenticationManager, FacultyAccessService facultyAccessService) {
        this.authenticationManager = authenticationManager;
        this.facultyAccessService = facultyAccessService;
    }

    public void login(HttpServletRequest request, String loginId, String password, UserRole expectedRole) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginId.trim(), password);
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(token);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid university ID or password.", ex);
        }
        String expectedAuthority = "ROLE_" + expectedRole.name();
        boolean roleOk = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(expectedAuthority));
        if (!roleOk) {
            throw new BadCredentialsException(
                    expectedRole == UserRole.STUDENT
                            ? "This ID is not a student account. Use the faculty sign-in page instead."
                            : "This ID is not a faculty account. Use the student sign-in page instead.");
        }
        if (expectedRole == UserRole.FACULTY) {
            facultyAccessService.assertFacultyPortalAllowed(loginId);
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
