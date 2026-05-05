package com.campus.scems.security;

import com.campus.scems.model.AppUser;
import com.campus.scems.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = appUserRepository.findByLoginIdIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!u.isEnabled()) {
            throw new UsernameNotFoundException("User disabled");
        }
        String role = "ROLE_" + u.getRole().name();
        return User.builder()
                .username(u.getLoginId())
                .password(u.getPasswordHash())
                .authorities(role)
                .build();
    }
}
