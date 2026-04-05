package tn.esprit.esprit_market.modules.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tn.esprit.esprit_market.modules.auth.dto.AuthRequest;
import tn.esprit.esprit_market.modules.auth.dto.AuthResponse;
import tn.esprit.esprit_market.modules.auth.dto.SocialLoginCompleteRequest;
import tn.esprit.esprit_market.modules.auth.dto.SocialLoginRequest;
import tn.esprit.esprit_market.modules.auth.service.IPasswordResetService;
import tn.esprit.esprit_market.modules.auth.service.SocialLoginService;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IUserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private SocialLoginService socialLoginService;
    @Mock
    private IPasswordResetService passwordResetService;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    private User user;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@esprit.tn");
        user.setName("John");
        user.setRole(Role.CUSTOMER);

        authResponse = AuthResponse.builder()
                .token("mock-token")
                .email("test@esprit.tn")
                .name("John")
                .role("CUSTOMER")
                .newUser(false)
                .build();
    }

    @Test
    void testRegister() {
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken("test@esprit.tn", "CUSTOMER")).thenReturn("mock-token");

        ResponseEntity<AuthResponse> res = authController.register(user, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("John", res.getBody().getName());
        verify(response).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@esprit.tn");
        req.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.getUserByEmail("test@esprit.tn")).thenReturn(user);
        when(jwtUtil.generateToken("test@esprit.tn", "CUSTOMER")).thenReturn("mock-token");

        ResponseEntity<?> res = authController.login(req, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(response).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testLoginDisabled() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@esprit.tn");
        req.setPassword("pass");

        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("disabled"));

        ResponseEntity<?> res = authController.login(req, response);

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    void testLoginBadCredentials() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@esprit.tn");
        req.setPassword("wrong");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        ResponseEntity<?> res = authController.login(req, response);

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    void testSocialLogin() {
        SocialLoginRequest req = new SocialLoginRequest();
        req.setProvider("GOOGLE");
        req.setToken("google-token");

        when(socialLoginService.socialLogin("GOOGLE", "google-token")).thenReturn(authResponse);

        ResponseEntity<AuthResponse> res = authController.socialLogin(req, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(response).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testCompleteSocialLogin() {
        SocialLoginCompleteRequest req = new SocialLoginCompleteRequest();
        when(socialLoginService.completeSocialLogin(req)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> res = authController.completeSocialLogin(req, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(response).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testLogout() {
        ResponseEntity<Map<String, String>> res = authController.logout(response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(response).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testForgotPassword() {
        doNothing().when(passwordResetService).forgotPassword("test@esprit.tn");

        ResponseEntity<Map<String, String>> res = authController.forgotPassword(Map.of("email", "test@esprit.tn"));

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(passwordResetService).forgotPassword("test@esprit.tn");
    }

    @Test
    void testResetPassword() {
        doNothing().when(passwordResetService).resetPassword("mock-token", "newPass");

        ResponseEntity<Map<String, String>> res = authController.resetPassword(Map.of("token", "mock-token", "newPassword", "newPass"));

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(passwordResetService).resetPassword("mock-token", "newPass");
    }
}
