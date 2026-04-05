package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;
import tn.esprit.esprit_market.modules.service.enums.RegistrationStatus;

import java.util.Date;

@Data
public class RegistrationDTO {
    private Long id;
    private Date registrationDate;
    private RegistrationStatus status;
    private boolean attendanceConfirmed;
    private double evaluationScore;
    private String comment;

    // Relations
    private Long workshopId;
    private Long userId;
}
