package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkCourseCompleteRequest {
    @NotNull(message = "courseId is required")
    private Long courseId;
}
