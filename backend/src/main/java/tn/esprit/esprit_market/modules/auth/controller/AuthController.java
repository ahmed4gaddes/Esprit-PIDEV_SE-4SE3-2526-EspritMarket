package tn.esprit.esprit_market.modules.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import tn.esprit.esprit_market.modules.auth.dto.SocialLoginCompleteRequest;
import tn.esprit.esprit_market.modules.auth.dto.SocialLoginRequest;
import tn.esprit.esprit_market.modules.auth.service.IPasswordResetService;
import tn.esprit.esprit_market.modules.auth.service.SocialLoginService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final SocialLoginService socialLoginService;
    private final IPasswordResetService passwordResetService;

    // ========== HELPER: Set JWT as HttpOnly Cookie ==========
    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);   // JavaScript cannot access this cookie
        cookie.setSecure(false);    // Set to true in production (HTTPS only)
        cookie.setPath("/");        // Cookie sent on all paths
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        // SameSite attribute set via header for broader browser support
        response.addCookie(cookie);
        response.setHeader("Set-Cookie", 
            "jwt=" + token + "; Path=/; HttpOnly; SameSite=Lax; Max-Age=86400");
    }

    // ========== HELPER: Clear JWT Cookie on logout ==========
    private void clearJwtCookie(HttpServletResponse response) {
        response.setHeader("Set-Cookie", 
            "jwt=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user, HttpServletResponse response) {
        User savedUser = userService.createUser(user);
        final String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        
        // Set token in HttpOnly cookie (not in body)
        setJwtCookie(response, token);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userService.getUserByEmail(request.getEmail());
        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        // Set token in HttpOnly cookie (not in body)
        setJwtCookie(response, token);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/social-login")
    public ResponseEntity<AuthResponse> socialLogin(@RequestBody SocialLoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = socialLoginService.socialLogin(request.getProvider(), request.getToken());
        
        // If existing user (has token), set it in cookie and remove from body
        if (authResponse.getToken() != null && !authResponse.isNewUser()) {
            setJwtCookie(response, authResponse.getToken());
            authResponse.setToken(null); // Remove from body
        }
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/social-login/complete")
    public ResponseEntity<AuthResponse> completeSocialLogin(@RequestBody SocialLoginCompleteRequest request, HttpServletResponse response) {
        AuthResponse authResponse = socialLoginService.completeSocialLogin(request);
        
        if (authResponse.getToken() != null) {
            setJwtCookie(response, authResponse.getToken());
            authResponse.setToken(null); // Remove from body
        }
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<java.util.Map<String, String>> logout(HttpServletResponse response) {
        clearJwtCookie(response);
        return ResponseEntity.ok(java.util.Map.of("message", "Déconnexion réussie."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(
            @RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok(java.util.Map.of("message", "Email de réinitialisation envoyé avec succès."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(
            @RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok(java.util.Map.of("message", "Mot de passe réinitialisé avec succès."));
    }
}

