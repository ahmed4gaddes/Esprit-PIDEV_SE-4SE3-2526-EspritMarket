package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.event.enums.LivePlatform;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveSessionResponse {
    private Long id;
    private String title;
    private String description;
    private LivePlatform platform;
    private LiveSessionStatus status;
    private String link;
    private Date scheduledAt;
    private Date endTime;
    private String thumbnailUrl;
    private Long eventId;
    private String eventTitle;
    private Long storeId;
    private String storeName;
    private Long creatorId;
    private String creatorName;
}
