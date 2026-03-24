package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;

import java.util.Date;

@Data
public class WorkshopDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    private ServiceType type;
    private boolean active;
    private Date createdAt;
    private Long creatorId;

    private int durationHours;
    private int capacity;
    private int enrolledCount;
    private String prerequisites;
    private String providedMaterial;
    private String difficultyLevel;
}
