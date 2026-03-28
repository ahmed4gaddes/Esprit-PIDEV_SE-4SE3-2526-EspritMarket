package tn.esprit.esprit_market.modules.shared.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private boolean read;
    private Date createdAt;
}
