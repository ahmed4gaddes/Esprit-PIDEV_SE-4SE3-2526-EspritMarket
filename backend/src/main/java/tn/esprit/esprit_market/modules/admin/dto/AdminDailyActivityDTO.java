package tn.esprit.esprit_market.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDailyActivityDTO {
    private String date;
    private long newUsers;
    private long newApplications;
    private long ticketsSold;
}
