package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Course title is required")
    private String title;

    private int durationHours;
    private boolean mandatory;
    private String description;
    private String objectives;
    private int courseOrder;

    // Relation
    private Long workshopId;
}
