package tn.esprit.esprit_market.modules.administration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDTO {
    private Long id;
    private double revenue;
    private double commissionAmount;
    private double net;
    private Date date;
    private Long commissionId;
    private String storeName;
}
