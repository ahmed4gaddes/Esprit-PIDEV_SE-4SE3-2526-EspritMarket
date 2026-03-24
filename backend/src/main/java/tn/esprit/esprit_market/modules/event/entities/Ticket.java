package tn.esprit.esprit_market.modules.event.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.event.enums.TicketStatus;
import tn.esprit.esprit_market.modules.user.entity.User;
import java.util.Date;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private String qrCode;

    private boolean checkedIn;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private String seatNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDate;

    @PrePersist
    @SuppressWarnings("unused")
    protected void onCreate() {
        purchaseDate = new Date();
        if (status == null) {
            status = TicketStatus.VALID;
        }
    }

    // Association with Event (Many tickets for one event)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // Association with User (Attendee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
