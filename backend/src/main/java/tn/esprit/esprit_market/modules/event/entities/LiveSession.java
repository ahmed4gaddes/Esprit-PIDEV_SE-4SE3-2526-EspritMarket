package tn.esprit.esprit_market.modules.event.entities;

import jakarta.persistence.*;
import lombok.*;

import tn.esprit.esprit_market.modules.event.enums.LivePlatform;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "live_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private LivePlatform platform;

    @Enumerated(EnumType.STRING)
    private LiveSessionStatus status;

    private String link;

    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    private String thumbnailUrl;

    // Association with Event (ManyToOne, Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // Association with Store (ManyToOne, Optional for seller lives)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    // Association with User (Creator of the live)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    // Chat messages for the live session
    @OneToMany(mappedBy = "liveSession", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages;
}
