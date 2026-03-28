package tn.esprit.esprit_market.modules.marketing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "sponsorship_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsorshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String offerTitle;

    @Column(columnDefinition = "TEXT")
    private String offerDescription;

    private Double budget;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String state;

    @Column(columnDefinition = "TEXT")
    private String sponsorDesignUrl;

    @Column(columnDefinition = "TEXT")
    private String sponsorNote;

    // Company owner of this offer
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private User company;

    // Sponsor that reviewed this offer
    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    @JsonIgnore
    private User sponsor;

    // SponsorshipRequest 1..0..1 Sponsorship
    @OneToOne
    @JoinColumn(name = "sponsorship_id")
    @JsonIgnore
    private Sponsorship sponsorship;

    @JsonProperty("companyId")
    public Long getCompanyId() {
        return company != null ? company.getId() : null;
    }

    @JsonProperty("companyName")
    public String getCompanyName() {
        return company != null ? company.getName() : null;
    }

    @JsonProperty("sponsorId")
    public Long getSponsorId() {
        return sponsor != null ? sponsor.getId() : null;
    }

    @JsonProperty("sponsorName")
    public String getSponsorName() {
        return sponsor != null ? sponsor.getName() : null;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Date();
        }
        if (state == null || state.isBlank()) {
            state = "PENDING";
        }
    }
}
