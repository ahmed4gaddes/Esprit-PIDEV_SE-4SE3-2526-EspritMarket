package tn.esprit.esprit_market.modules.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.auth.entity.PasswordResetToken;
import tn.esprit.esprit_market.modules.auth.repository.PasswordResetTokenRepository;
import tn.esprit.esprit_market.modules.shared.service.EmailService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    private User user;
    private PasswordResetToken token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setEmail("user@mail.com");
        user.setName("John");

        token = new PasswordResetToken();
        token.setId(1L);
        token.setToken("abcdef");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    }

    @Test
    void testForgotPassword() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        doNothing().when(passwordResetTokenRepository).deleteByUser(user);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
        doNothing().when(emailService).sendEmail(eq("user@mail.com"), anyString(), anyString());

        passwordResetService.forgotPassword("user@mail.com");

        verify(passwordResetTokenRepository).deleteByUser(user);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendEmail(eq("user@mail.com"), anyString(), anyString());
    }

    @Test
    void testResetPassword() {
        when(passwordResetTokenRepository.findByToken("abcdef")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).delete(token);

        passwordResetService.resetPassword("abcdef", "newpass");

        assertEquals("encodedpass", user.getPassword());
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void testResetPasswordTooShort() {
        assertThrows(UserException.class, () -> passwordResetService.resetPassword("abcdef", "123"));
    }

    @Test
    void testResetPasswordExpired() {
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5)); // expired

        when(passwordResetTokenRepository.findByToken("abcdef")).thenReturn(Optional.of(token));

        assertThrows(UserException.class, () -> passwordResetService.resetPassword("abcdef", "newpass"));
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void testForgotPassword_UserNotFound() {
        when(userRepository.findByEmail("bad@mail.com")).thenReturn(Optional.empty());
        assertThrows(tn.esprit.esprit_market.exceptions.ResourceNotFoundException.class,
                () -> passwordResetService.forgotPassword("bad@mail.com"));
    }

    @Test
    void testResetPassword_NullPassword() {
        assertThrows(UserException.class, () -> passwordResetService.resetPassword("abc", null));
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(passwordResetTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> passwordResetService.resetPassword("invalid", "newpass123"));
    }
}
