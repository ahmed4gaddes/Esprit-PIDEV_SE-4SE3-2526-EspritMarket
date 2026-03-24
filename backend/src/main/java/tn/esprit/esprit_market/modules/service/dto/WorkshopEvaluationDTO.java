package tn.esprit.esprit_market.modules.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.util.Date;

@Data
public class WorkshopEvaluationDTO {
    private Long id;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;
    private Date evaluationDate;

    // Relation
    private Long registrationId;
}
