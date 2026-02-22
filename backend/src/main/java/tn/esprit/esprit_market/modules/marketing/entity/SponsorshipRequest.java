package tn.esprit.esprit_market.modules.marketing.entity;

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

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String state;

    // SponsorshipRequest *..1 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // SponsorshipRequest 1..0..1 Sponsorship
    @OneToOne
    @JoinColumn(name = "sponsorship_id")
    private Sponsorship sponsorship;
}
