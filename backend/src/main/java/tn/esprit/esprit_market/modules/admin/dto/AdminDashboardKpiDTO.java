package tn.esprit.esprit_market.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardKpiDTO {
    private long totalUsers;
    private long activeUsers;
    private long totalStores;
    private long totalProducts;
    private long totalEvents;
    private long totalLives;
    private long totalInternships;
    private long pendingInternshipApplications;
    private long ticketsSold;
    private double ticketRevenue;
}
