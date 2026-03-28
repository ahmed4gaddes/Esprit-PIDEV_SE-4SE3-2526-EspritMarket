package tn.esprit.esprit_market.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(
        name = "rates",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rate_rater_rated_user",
                columnNames = {"rater_id", "rated_user_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must be at most 5")
    @Column(nullable = false)
    private int star;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // The user who gives the rating
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    @JsonIgnore
    private User rater;

    // The user being rated (e.g., a seller)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    @JsonIgnore
    private User ratedUser;

    @JsonProperty("raterId")
    public Long getRaterId() {
        return rater != null ? rater.getId() : null;
    }

    @JsonProperty("ratedUserId")
    public Long getRatedUserId() {
        return ratedUser != null ? ratedUser.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
