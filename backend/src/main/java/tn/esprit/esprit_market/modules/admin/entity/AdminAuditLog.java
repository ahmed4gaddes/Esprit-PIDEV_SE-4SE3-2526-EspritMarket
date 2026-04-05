package tn.esprit.esprit_market.modules.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "admin_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false)
    private Long actorUserId;

    @Column(nullable = false, length = 120)
    private String actorEmail;

    @Column
    private Long targetUserId;

    @Column(length = 120)
    private String targetUserEmail;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
