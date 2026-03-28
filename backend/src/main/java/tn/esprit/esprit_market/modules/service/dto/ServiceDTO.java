package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;

import java.util.Date;

@Data
public class ServiceDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    private ServiceType type;
    private boolean active;
    private Date createdAt;
    private String imageUrl;
    
    // Relation
    private Long creatorId;
}
