package com.example.cabbookingapplication.security;

import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> optionalUser = userRepo.findByEmail(email);
        User user;

        boolean needsRole = false;

        if (optionalUser.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            userRepo.save(user);
            needsRole = true;
        } else {
            user = optionalUser.get();
            if (user.getRole() == null) {
                needsRole = true;
            }
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole() == null ? "TEMP" : user.getRole().name()
        );

        String redirectUrl =
                "http://localhost:5173/oauth-success" +
                "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
                "&needsRole=" + needsRole;

        response.sendRedirect(redirectUrl);
    }
}
