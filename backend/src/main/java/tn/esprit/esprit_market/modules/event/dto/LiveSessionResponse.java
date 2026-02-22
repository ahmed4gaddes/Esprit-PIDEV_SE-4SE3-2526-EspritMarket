package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveSessionResponse {
    private Long id;
    private String link;
    private String platform;
    private Date startTime;
    private Long eventId;
    private String eventTitle;
}
