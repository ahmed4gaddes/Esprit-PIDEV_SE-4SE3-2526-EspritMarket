package tn.esprit.esprit_market.modules.auth.service;

public interface IPasswordResetService {
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
