package tn.esprit.esprit_market.modules.event.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "times")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Times {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    // Duration in minutes
    private int duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Association: each Times slot belongs to one LiveSession
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_session_id")
    @JsonIgnore
    private LiveSession liveSession;
}
