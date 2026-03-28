package tn.esprit.esprit_market.modules.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseCompletionDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private String courseTitle;
    private Date completedAt;
}
