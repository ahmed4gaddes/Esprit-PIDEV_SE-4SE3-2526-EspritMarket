package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.event.enums.TicketStatus;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private double price;
    private String qrCode;
    private boolean checkedIn;
    private TicketStatus status;
    private String seatNumber;
    private Date purchaseDate;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userName;
}
