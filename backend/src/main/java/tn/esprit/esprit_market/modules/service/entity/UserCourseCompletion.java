package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "user_course_completions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_course", columnNames = { "user_id", "course_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCourseCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date completedAt;
}
