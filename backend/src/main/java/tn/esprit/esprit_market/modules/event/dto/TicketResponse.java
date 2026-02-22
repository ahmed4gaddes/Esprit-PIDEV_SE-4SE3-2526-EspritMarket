package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private double price;
    private String qrCode;
    private boolean checkedIn;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userName;
}
