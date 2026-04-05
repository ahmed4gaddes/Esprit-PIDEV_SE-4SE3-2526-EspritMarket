package tn.esprit.esprit_market.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotBlank(message = "Role is required")
    private String role;
}
