package io.mox.mox_auth.security.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_TIME_MINUTES = 30;
    
    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>(); // TODO: по идее это бы в базу какую-нибудь сохранять...

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        LoginAttempt attempt = attemptsCache.getOrDefault(ipAddress, new LoginAttempt());

        attempt.incrementAttempts();

        HttpSession session = request.getSession();

        if(attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.setLockoutTime(LocalDateTime.now().minusMinutes(LOCK_TIME_MINUTES));

            session.setAttribute("error", "blocked");
            session.setAttribute("message", "Слишком много попыток. Повторите позже.");

            response.sendRedirect("/login");
            return;
        }

        attemptsCache.put(ipAddress, attempt);

        session.setAttribute("error", "invalid_credentials");
        session.setAttribute("message", "Неверные имя пользователя или пароль.");
        response.sendRedirect("/login");
    }

    public boolean isBlocked(String ipAddress) {
        LoginAttempt attempt = attemptsCache.get(ipAddress);

        return attempt != null &&
                attempt.getLockoutTime() != null &&
                attempt.getLockoutTime().isBefore(attempt.getLockoutTime());
    }

    private static class LoginAttempt {
        private int attempts;
        private LocalDateTime lockoutTime;

        public void incrementAttempts() { attempts++; }
        public int getAttempts() { return attempts; }
        public LocalDateTime getLockoutTime() { return lockoutTime; }
        public void setLockoutTime(LocalDateTime lockoutTime) { this.lockoutTime = lockoutTime; }
    }

}
