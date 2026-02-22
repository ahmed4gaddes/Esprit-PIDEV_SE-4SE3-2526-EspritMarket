package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "certificate_validations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    @Enumerated(EnumType.STRING)
    private ValidationStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // CertificateValidation *..1 Certificate
    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    // CertificateValidation *..1 User (validator)
    @ManyToOne
    @JoinColumn(name = "validated_by")
    private User validator;
}
