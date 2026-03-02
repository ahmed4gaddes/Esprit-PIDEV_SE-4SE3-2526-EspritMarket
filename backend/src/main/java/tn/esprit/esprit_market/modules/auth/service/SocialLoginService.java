package tn.esprit.esprit_market.modules.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.auth.dto.AuthResponse;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Step 1: Verify Google token and check if user exists
     * - Existing user → return JWT + role
     * - New user → return newUser=true + user info (no account created yet)
     */
    public AuthResponse socialLogin(String provider, String token) {
        if (!"GOOGLE".equalsIgnoreCase(provider)) {
            throw new UserException("Unsupported provider: " + provider);
        }

        Map<String, String> userInfo = verifyGoogleToken(token);

        String email = userInfo.get("email");
        String name = userInfo.get("name");
        String picture = userInfo.get("picture");

        if (email == null || email.isEmpty()) {
            throw new UserException(
                    "Could not retrieve email from Google. Please ensure you allow email access.");
        }

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Block ADMIN from social login
            if (user.getRole() == Role.ADMIN) {
                throw new UserException("Admin accounts cannot use social login.");
            }

            // Block SELLER from social login
            if (user.getRole() == Role.SELLER) {
                throw new UserException("Seller accounts must use their @esprit.tn credentials.");
            }

            // Existing user → generate JWT and return
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return AuthResponse.builder()
                    .token(jwtToken)
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .newUser(false)
                    .build();
        }

        // New user → DON'T create account yet, ask for role selection
        return AuthResponse.builder()
                .newUser(true)
                .name(name)
                .email(email)
                .picture(picture)
                .build();
    }

    /**
     * Step 2: Complete registration with selected role
     * Called after new user selects their role
     */
    public AuthResponse completeSocialLogin(
            tn.esprit.esprit_market.modules.auth.dto.SocialLoginCompleteRequest request) {
        if (!"GOOGLE".equalsIgnoreCase(request.getProvider())) {
            throw new UserException("Unsupported provider: " + request.getProvider());
        }

        // Validate the selected role
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new UserException("Invalid role: " + request.getRole());
        }

        // Only allow CUSTOMER, EXPERT, COMPANY, SPONSOR
        if (role == Role.ADMIN || role == Role.SELLER) {
            throw new UserException("This role is not available for Google login.");
        }

        // Re-verify the Google token
        Map<String, String> userInfo = verifyGoogleToken(request.getToken());
        String email = userInfo.get("email");
        String name = userInfo.get("name");
        String picture = userInfo.get("picture");

        // Check if user already exists (in case of double submission)
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return AuthResponse.builder()
                    .token(jwtToken)
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .newUser(false)
                    .build();
        }

        // Create the new user with selected role and additional info
        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(role)
                .profilePicture(picture)
                .isActive(true)
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : new Date(946684800000L))
                .build();

        User savedUser = userRepository.save(newUser);

        String jwtToken = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return AuthResponse.builder()
                .token(jwtToken)
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .newUser(false)
                .build();
    }

    //Vérifie que le token est valide
    private Map<String, String> verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new UserException("Invalid Google token.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", payload.getEmail());
            userInfo.put("name", (String) payload.get("name"));
            userInfo.put("picture", (String) payload.get("picture"));
            return userInfo;

        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new UserException("Failed to verify Google token: " + e.getMessage());
        }
    }
}
