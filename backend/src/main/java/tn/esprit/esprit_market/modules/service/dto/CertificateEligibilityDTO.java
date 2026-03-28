package tn.esprit.esprit_market.modules.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateEligibilityDTO {
    private Long certificateId;
    private Long userId;
    /** true si aucun cours requis, ou si tous les cours requis sont complétés. */
    private boolean eligible;
    private List<Long> requiredCourseIds = new ArrayList<>();
    private List<Long> completedCourseIds = new ArrayList<>();
    private List<Long> missingCourseIds = new ArrayList<>();
}
