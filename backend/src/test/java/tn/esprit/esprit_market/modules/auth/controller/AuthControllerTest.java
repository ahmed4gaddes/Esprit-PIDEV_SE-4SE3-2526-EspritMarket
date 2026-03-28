package tn.esprit.esprit_market.modules.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import tn.esprit.esprit_market.modules.auth.dto.AuthRequest;
import tn.esprit.esprit_market.modules.auth.service.IPasswordResetService;
import tn.esprit.esprit_market.modules.auth.service.SocialLoginService;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IUserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private SocialLoginService socialLoginService;
    @Mock
    private IPasswordResetService passwordResetService;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_WhenBadCredentials_ShouldReturn401() {
        AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("wrong-password")
                .build();
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        ResponseEntity<?> result = authController.login(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertInstanceOf(Map.class, result.getBody());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertEquals("Incorrect email or password.", body.get("error"));
        verify(response, never()).addHeader(any(), any());
    }

    @Test
    void login_WhenAccountDisabled_ShouldReturn403() {
        AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("any")
                .build();
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("disabled"));

        ResponseEntity<?> result = authController.login(request, response);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertInstanceOf(Map.class, result.getBody());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertEquals("Your account is disabled. Please contact support.", body.get("error"));
        verify(response, never()).addHeader(any(), any());
    }
}
