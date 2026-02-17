package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import tn.esprit.esprit_market.Module.service.enums.CertificationLevel;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.Module.service.enums.ValidationStatus;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Entity
public class Certificate extends Service {
    private String organization;
    private Date validUntil;

    @Enumerated(EnumType.STRING)
    private CertificationLevel level;

    @Enumerated(EnumType.STRING)
    private ValidationStatus status;

    private String documentUrl;
    private Date validationDate;
    private String adminComment;

    @OneToMany(mappedBy = "certificate")
    private List<SupportingDocument> documents;

    @OneToMany(mappedBy = "certificate")
    private List<CertificateValidation> validations;
}