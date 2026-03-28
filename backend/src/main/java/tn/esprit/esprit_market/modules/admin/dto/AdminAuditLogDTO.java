package tn.esprit.esprit_market.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLogDTO {
    private Long id;
    private String action;
    private Long actorUserId;
    private String actorEmail;
    private Long targetUserId;
    private String targetUserEmail;
    private String details;
    private LocalDateTime createdAt;
}
