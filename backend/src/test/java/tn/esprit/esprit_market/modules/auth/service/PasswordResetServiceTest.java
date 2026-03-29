package tn.esprit.esprit_market.modules.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.auth.entity.PasswordResetToken;
import tn.esprit.esprit_market.modules.auth.repository.PasswordResetTokenRepository;
import tn.esprit.esprit_market.modules.shared.service.EmailService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User fakeUser;
    private PasswordResetToken validToken;
    private PasswordResetToken expiredToken;

    @BeforeEach
    void setUp() {
        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setName("Test User");
        fakeUser.setEmail("test@gmail.com");
        fakeUser.setPassword("old-password");

        validToken = PasswordResetToken.builder()
                .id(1L)
                .token("valid-uuid-token")
                .user(fakeUser)
                .expiryDate(LocalDateTime.now().plusMinutes(15)) // Future
                .build();

        expiredToken = PasswordResetToken.builder()
                .id(2L)
                .token("expired-uuid-token")
                .user(fakeUser)
                .expiryDate(LocalDateTime.now().minusMinutes(5)) // Past
                .build();
    }

    // ==================== FORGOT PASSWORD ====================
    @Test
    void testForgotPassword_UserExists_TokenGeneratedAndEmailSent() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(fakeUser));
        doNothing().when(passwordResetTokenRepository).deleteByUser(fakeUser);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(validToken);
        doNothing().when(emailService).sendEmail(eq("test@gmail.com"), anyString(), anyString());

        passwordResetService.forgotPassword("test@gmail.com");

        // Verify token deleted and saved
        verify(passwordResetTokenRepository, times(1)).deleteByUser(fakeUser);
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        
        // Verify email sent
        verify(emailService, times(1)).sendEmail(eq("test@gmail.com"), anyString(), anyString());
        
        assertNotNull(tokenCaptor.getValue());
        assertEquals(fakeUser, tokenCaptor.getValue().getUser());
    }

    @Test
    void testForgotPassword_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> passwordResetService.forgotPassword("unknown@gmail.com"));

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // ==================== RESET PASSWORD ====================
    @Test
    void testResetPassword_Success() {
        when(passwordResetTokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashed-new-pwd");

        passwordResetService.resetPassword("valid-uuid-token", "newPassword123");

        assertEquals("hashed-new-pwd", fakeUser.getPassword());
        verify(userRepository, times(1)).save(fakeUser);
        verify(passwordResetTokenRepository, times(1)).delete(validToken); // Token is cleaned up
    }

    @Test
    void testResetPassword_PasswordTooShort_ThrowsException() {
        UserException exception = assertThrows(UserException.class, 
                () -> passwordResetService.resetPassword("valid-uuid-token", "12345"));
        assertTrue(exception.getMessage().contains("at least 6 characters"));
    }

    @Test
    void testResetPassword_InvalidToken_ThrowsException() {
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> passwordResetService.resetPassword("invalid-token", "newPassword123"));
    }

    @Test
    void testResetPassword_ExpiredToken_ThrowsException() {
        when(passwordResetTokenRepository.findByToken("expired-uuid-token")).thenReturn(Optional.of(expiredToken));

        UserException exception = assertThrows(UserException.class, 
                () -> passwordResetService.resetPassword("expired-uuid-token", "newPassword123"));
        
        assertTrue(exception.getMessage().contains("expiré"));
        verify(passwordResetTokenRepository, times(1)).delete(expiredToken); // Verify expired token is removed
        verify(userRepository, never()).save(any());
    }
}
