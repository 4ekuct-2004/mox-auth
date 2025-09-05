package io.mox.mox_auth.controller;

import io.mox.mox_auth.dto.UserRegisterRequest;
import io.mox.mox_auth.model.User;
import io.mox.mox_auth.service.LoginAttemptService;
import io.mox.mox_auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {
    private UserService userService;
    private LoginAttemptService loginAttemptService;

    public AuthController(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegisterRequest request) {
        try{
            User user = new User();
            user.setUsername(request.username());
            user.setPassword(request.password());
            userService.register(user);

            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String ipAddress = request.getRemoteAddr();
        String username = request.getParameter("username");
        HttpSession session = request.getSession(false);

        if(loginAttemptService.isBlocked(ipAddress, username)) {
            model.addAttribute("error", "Слишком много попыток. Попробуйте позже.");
            return "login";
        }

        if(session != null) {
            String error = (String) session.getAttribute("error");

            if (error != null) {
                model.addAttribute("error", error);
                session.removeAttribute("error");
            }
        }

        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }
}
