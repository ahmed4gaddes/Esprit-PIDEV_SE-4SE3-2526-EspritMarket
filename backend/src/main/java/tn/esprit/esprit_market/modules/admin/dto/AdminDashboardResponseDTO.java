package tn.esprit.esprit_market.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminDashboardResponseDTO {
    private AdminDashboardKpiDTO kpis;
    private List<AdminDailyActivityDTO> activity;
}
