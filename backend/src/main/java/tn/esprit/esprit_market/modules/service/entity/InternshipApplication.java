package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "internship_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(nullable = false, length = 1024)
    private String cvUrl;

    @Column(length = 2000)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InternshipApplicationStatus status = InternshipApplicationStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date appliedAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewedAt;

    @Column(length = 1000)
    private String reviewerComment;
}
