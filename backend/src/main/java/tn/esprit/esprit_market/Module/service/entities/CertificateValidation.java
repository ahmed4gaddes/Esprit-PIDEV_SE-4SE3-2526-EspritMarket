package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.Module.service.enums.ValidationStatus;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CertificateValidation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date validationDate;
    private String comment;

    @Enumerated(EnumType.STRING)
    private ValidationStatus status;

    @ManyToOne
    private Certificate certificate;

    @ManyToOne
    private User validator; // Admin/Expert
}

