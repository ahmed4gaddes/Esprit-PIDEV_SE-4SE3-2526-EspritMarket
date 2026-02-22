package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course title is required")
    @Column(nullable = false)
    private String title;

    private int durationHours;
    private boolean mandatory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    @Column(name = "course_order")
    private int courseOrder;

    // Course *..1 Workshop
    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;
}
