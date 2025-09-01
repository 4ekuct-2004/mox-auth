package io.mox.mox_auth.repository;

import io.mox.mox_auth.model.LoginAttempt;
import io.mox.mox_auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginRepo extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByIpAddress(String ipAddress);
    List<LoginAttempt> findByAccount(User user);
}
