package io.mox.mox_auth.controller;

import io.mox.mox_auth.dto.UserRegisterRequest;
import io.mox.mox_auth.model.User;
import io.mox.mox_auth.security.auth.AuthFailureHandler;
import io.mox.mox_auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class AuthController {
    private UserService userService;
    private AuthFailureHandler authFailureHandler;

    public AuthController(UserService userService, AuthFailureHandler authFailureHandler) {
        this.userService = userService;
        this.authFailureHandler = authFailureHandler;
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
        HttpSession session = request.getSession(false);

        if(authFailureHandler.isBlocked(ipAddress)) {

            model.addAttribute("error", "blocked");
            model.addAttribute("message", "Слишком много попыток. Попробуйте позже.");
            return "login";
        }

        if(session != null) {
            String error = (String) session.getAttribute("error");
            String message = (String) session.getAttribute("message");

            if (error != null) {
                model.addAttribute("error", error);
                session.removeAttribute("error");
            }
            if (message != null) {
                model.addAttribute("message", message);
                session.removeAttribute("message");
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
