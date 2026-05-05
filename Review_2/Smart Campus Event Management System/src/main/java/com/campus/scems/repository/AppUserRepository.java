package com.campus.scems.repository;

import com.campus.scems.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByLoginIdIgnoreCase(String loginId);

    boolean existsByLoginIdIgnoreCase(String loginId);
}
