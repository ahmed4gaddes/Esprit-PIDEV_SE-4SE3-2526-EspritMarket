package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyInternshipRequest {
    @NotBlank(message = "CV URL is required")
    private String cvUrl;

    private String coverLetter;
}
