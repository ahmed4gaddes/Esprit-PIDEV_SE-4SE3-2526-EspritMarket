package tn.esprit.esprit_market.modules.event.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
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

    private String location;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private double ticketPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EventType type;

    // Association with User (Organizer) - Cross-module link
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    // Bidirectional list of tickets
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private java.util.List<Ticket> tickets;

    // Bidirectional LiveSession
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private java.util.List<LiveSession> liveSessions;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    @SuppressWarnings("unused")
    protected void onCreate() {
        createdAt = new Date();
    }
}
