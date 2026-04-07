package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatisticsDTO {
    private Long eventId;
    private String title;
    private Date date;
    private EventStatus status;
    private String organizerName;
    private Long ticketsSold;
    private Double totalRevenue;
}
