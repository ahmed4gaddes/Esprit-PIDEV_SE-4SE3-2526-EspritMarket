package tn.esprit.esprit_market.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.esprit_market.modules.auth.dto.AuthRequest;
import tn.esprit.esprit_market.modules.auth.dto.AuthResponse;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.UserService;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        final String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userService.getUserByEmail(request.getEmail());
        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(
            @RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        userService.forgotPassword(email);
        return ResponseEntity.ok(java.util.Map.of("message", "Email de réinitialisation envoyé avec succès."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(
            @RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(java.util.Map.of("message", "Mot de passe réinitialisé avec succès."));
    }
}
