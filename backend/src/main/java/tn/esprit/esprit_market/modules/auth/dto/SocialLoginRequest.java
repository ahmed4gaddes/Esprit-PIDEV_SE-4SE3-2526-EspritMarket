package tn.esprit.esprit_market.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRequest {
    private String provider; // "GOOGLE" or "FACEBOOK"
    private String token; // ID token (Google) or access token (Facebook)
}
