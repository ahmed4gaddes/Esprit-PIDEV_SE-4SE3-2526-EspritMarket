package tn.esprit.esprit_market.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.user.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean storeActive;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private double totalSales;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private boolean isActive;
}
