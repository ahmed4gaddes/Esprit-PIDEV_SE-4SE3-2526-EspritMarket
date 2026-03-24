package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;
import java.util.Date;

@Data
public class CertificateValidationDTO {
    private Long id;
    private Date validationDate;
    private ValidationStatus status;
    private String comment;

    // Relations
    private Long certificateId;
    private Long validatorId;
}
