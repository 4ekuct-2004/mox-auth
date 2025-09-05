package io.mox.mox_auth.security.auth;

import io.mox.mox_auth.model.LoginAttempt;
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

    public AuthSuccessHandler(LoginAttemptService loginAttemptService, UserRepo userRepo) {
        this.loginAttemptService = loginAttemptService;
        this.userRepo = userRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        String username = request.getParameter("username");

        LoginAttempt attempt = new LoginAttempt(
                ipAddress,
                userRepo.findByUsername(username),
                true,
                LocalDateTime.now()
        );

        loginAttemptService.recordSuccessfulAttempt(attempt);

        response.sendRedirect("/welcome");
    }

}
