package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private Date date;
    private int capacity;
    private String location;
    private String imageUrl;
    private EventStatus status;
    private double ticketPrice;
    private int ticketCount;
    private EventType type;
    private String organizerName;
    private Date createdAt;
    // Store link (for seller product launch events)
    private Long storeId;
    private String storeName;
}
