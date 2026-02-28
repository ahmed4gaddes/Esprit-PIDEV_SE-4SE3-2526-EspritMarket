package tn.esprit.esprit_market.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginCompleteRequest {
    private String provider; // "GOOGLE"
    private String token; // Google ID token
    private String role; // Selected role: CUSTOMER, EXPERT, or COMPANY
    private String phoneNumber;
    private java.util.Date dateOfBirth;
}
