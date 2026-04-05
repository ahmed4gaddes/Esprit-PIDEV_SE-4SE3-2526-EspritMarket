package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.service.enums.CertificationLevel;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificate extends Service {

    private String organization;

    @Temporal(TemporalType.DATE)
    private Date validUntil;

    @Enumerated(EnumType.STRING)
    private CertificationLevel level;

    @Enumerated(EnumType.STRING)
    private ValidationStatus status;

    private String documentUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    private String adminComment;

    // Certificate 1..* SupportingDocument
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL)
    private List<SupportingDocument> supportingDocuments = new ArrayList<>();

    // Certificate 1..* CertificateValidation
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL)
    private List<CertificateValidation> validations = new ArrayList<>();

    /** Cours requis pour débloquer ce certificat (complétion par cours suivie via UserCourseCompletion). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "certificate_courses",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> requiredCourses = new HashSet<>();
}
