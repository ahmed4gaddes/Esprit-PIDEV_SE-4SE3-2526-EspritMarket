package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.entity.User;

@Entity
@Table(name = "gamifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int points;
    private int level;
    private String badge;

    // Gamification 1..1 User
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
