package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ServiceCalendarDTO {
    private Long id;
    private Date startDate;
    private Date endDate;
    private int capacity;
    private boolean available;

    // Relation
    private Long serviceId;
}
