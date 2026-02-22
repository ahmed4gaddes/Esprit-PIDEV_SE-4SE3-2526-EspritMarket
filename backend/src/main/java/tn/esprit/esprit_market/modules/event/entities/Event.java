package tn.esprit.esprit_market.modules.event.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private EventType type;

    // Association with User (Organizer) - Cross-module link
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    // Bidirectional list of tickets
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private java.util.List<Ticket> tickets;

    // Bidirectional LiveSession
    @OneToOne(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LiveSession liveSession;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
