package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;

@Data
public class InternshipApplicationDecisionRequest {
    @NotNull(message = "Status is required")
    private InternshipApplicationStatus status;

    private String reviewerComment;
}
