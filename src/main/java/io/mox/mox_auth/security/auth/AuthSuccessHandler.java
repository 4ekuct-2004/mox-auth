package io.mox.mox_auth.security.auth;

import io.mox.mox_auth.model.LoginAttempt;
import io.mox.mox_auth.repository.LoginRepo;
import io.mox.mox_auth.repository.UserRepo;
import io.mox.mox_auth.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;
    private final UserRepo userRepo;
    private final LoginRepo loginRepo;

    public AuthSuccessHandler(LoginAttemptService loginAttemptService, UserRepo userRepo, LoginRepo loginRepo) {
        this.loginAttemptService = loginAttemptService;
        this.userRepo = userRepo;
        this.loginRepo = loginRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        String username = request.getParameter("username");

        LoginAttempt attempt = new LoginAttempt();
        attempt.setIpAddress(ipAddress);
        attempt.setAccount(userRepo.findByUsername(username));
        attempt.setSuccessful(true);
        attempt.setLoginTimestamp(LocalDateTime.now());

        loginAttemptService.recordSuccessfulAttempt(attempt);
        loginRepo.save(attempt);

        response.sendRedirect("/welcome");
    }

}
