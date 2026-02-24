package tn.esprit.esprit_market.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
    private boolean newUser; // true when user needs to select role
    private String picture; // profile picture from Google
}
