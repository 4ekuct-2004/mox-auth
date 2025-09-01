package io.mox.mox_auth.security.auth;

import io.mox.mox_auth.model.LoginAttempt;
import io.mox.mox_auth.repository.LoginRepo;
import io.mox.mox_auth.repository.UserRepo;
import io.mox.mox_auth.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;
    private final UserRepo userRepo;
    private final LoginRepo loginRepo;

    public AuthFailureHandler(final LoginAttemptService loginAttemptService, UserRepo userRepo, LoginRepo loginRepo) {
        this.loginAttemptService = loginAttemptService;
        this.userRepo = userRepo;
        this.loginRepo = loginRepo;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        String username = request.getParameter("username");
        HttpSession session = request.getSession(false);

        if(!userRepo.existsByUsername(username)) {
            session.setAttribute("error", "Неправильное имя пользователя или пароль.");
            response.sendRedirect("/login");
        }
        else {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setIpAddress(ipAddress);

            attempt.setAccount(userRepo.findByUsername(username));

            attempt.setSuccessful(false);
            attempt.setLoginTimestamp(LocalDateTime.now());

            loginAttemptService.recordFailedAttempt(attempt);
            loginRepo.save(attempt);

            session.setAttribute("error", loginAttemptService.isBlocked(username, ipAddress) ? "Слишком много попыток. Повторите позже." :
                    "Неправильное имя пользователя или пароль.");
            response.sendRedirect("/login");
        }
    }

}
