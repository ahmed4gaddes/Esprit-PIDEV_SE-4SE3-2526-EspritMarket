package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tn.esprit.esprit_market.modules.service.enums.CertificationLevel;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;

import java.util.Date;
import java.util.List;

@Data
public class CertificateDTO {
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
    private String imageUrl;

    private String organization;
    private Date validUntil;
    private CertificationLevel level;
    private ValidationStatus status;
    private String documentUrl;
    private Date validationDate;
    private String adminComment;

    /** IDs des cours requis (create/update). */
    private List<Long> courseIds;

    /** Lecture seule : détail des cours liés. */
    private List<CourseDTO> courses;
}
