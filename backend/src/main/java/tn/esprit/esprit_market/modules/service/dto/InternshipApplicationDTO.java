package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;

import java.util.Date;

@Data
public class InternshipApplicationDTO {
    private Long id;
    private Long internshipId;
    private String internshipTitle;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String cvUrl;
    private String coverLetter;
    private InternshipApplicationStatus status;
    private Date appliedAt;
    private Date reviewedAt;
    private String reviewerComment;
}
