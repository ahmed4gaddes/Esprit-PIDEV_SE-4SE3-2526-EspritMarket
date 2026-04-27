package tn.esprit.esprit_market.modules.event.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PricingRuleRequest {

    @Builder.Default
    private boolean dynamicPricingEnabled = true;

    @DecimalMin(value = "0.0", message = "Early bird discount must be >= 0")
    @DecimalMax(value = "0.99", message = "Early bird discount must be < 1")
    @Builder.Default
    private double earlyBirdDiscount = 0.20;

    @DecimalMin(value = "0.01", message = "Early bird threshold must be > 0")
    @DecimalMax(value = "0.99", message = "Early bird threshold must be < 1")
    @Builder.Default
    private double earlyBirdThreshold = 0.30;

    @DecimalMin(value = "0.0", message = "High demand surcharge must be >= 0")
    @Builder.Default
    private double highDemandSurcharge = 0.20;

    @DecimalMin(value = "0.01", message = "High demand threshold must be > 0")
    @DecimalMax(value = "0.99", message = "High demand threshold must be < 1")
    @Builder.Default
    private double highDemandThreshold = 0.60;

    @DecimalMin(value = "0.0", message = "Last seats surcharge must be >= 0")
    @Builder.Default
    private double lastSeatsSurcharge = 0.50;

    @DecimalMin(value = "0.01", message = "Last seats threshold must be > 0")
    @DecimalMax(value = "0.99", message = "Last seats threshold must be < 1")
    @Builder.Default
    private double lastSeatsThreshold = 0.85;

    @DecimalMin(value = "0.0", message = "Last minute surcharge must be >= 0")
    @Builder.Default
    private double lastMinuteSurcharge = 0.30;

    @Min(value = 1, message = "Last minute hours must be at least 1")
    @Builder.Default
    private int lastMinuteHours = 24;
}
