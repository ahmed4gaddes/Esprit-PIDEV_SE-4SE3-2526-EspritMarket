package tn.esprit.esprit_market.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.modules.auth.entity.PasswordResetToken;
import tn.esprit.esprit_market.modules.auth.repository.PasswordResetTokenRepository;
import tn.esprit.esprit_market.modules.shared.service.EmailService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    // We use UserRepository here because we need to save the password change directly.
    // UserService's update method only updates generic fields and doesn't handle password changes.
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new tn.esprit.esprit_market.exceptions.ResourceNotFoundException(
                        "User not found with email: " + email));

        // Delete existing token if any
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30)) // 30 mins expiry
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Réinitialisation de mot de passe - Esprit Market",
                "Bonjour " + user.getName() + ",\n\n" +
                        "Vous avez demandé la réinitialisation de votre mot de passe.\n" +
                        "Cliquez sur le lien suivant pour le changer :\n" +
                        resetLink + "\n\n" +
                        "Ce lien expire dans 30 minutes.\n" +
                        "Si vous n'avez rien demandé, ignorez cet email.");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // CONTROL: New password must be at least 6 characters
        if (newPassword == null || newPassword.length() < 6) {
            throw new tn.esprit.esprit_market.exceptions.UserException(
                    "Password must be at least 6 characters.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(
                        () -> new tn.esprit.esprit_market.exceptions.UserException("Token invalide ou déjà utilisé."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new tn.esprit.esprit_market.exceptions.UserException(
                    "Le token a expiré. Veuillez redemander un lien.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
