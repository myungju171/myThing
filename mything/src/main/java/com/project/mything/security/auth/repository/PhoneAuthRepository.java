package com.project.mything.security.auth.repository;

import com.project.mything.security.auth.entity.PhoneAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneAuthRepository extends JpaRepository<PhoneAuth, Long> {
    Optional<PhoneAuth> findPhoneAuthByPhone(String phone);
}
